/**
*	Events.java
*
*	@author Johan Brook
*	@copyright (c) 2013 Johan Brook
*	@license MIT
*/
package controllers;

import play.*;
import play.mvc.*;
import play.libs.*;
import play.libs.F.*;
import akka.actor.*;
import java.util.*;
import models.Invoice;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Responsible for streaming events.
 */
public class Events extends Controller {

	public final static ActorRef actorInstance = InvoiceActor.instance;
	
	public static Result getPaid() {
		return ok(new EventSource() {
			public void onConnected() {
				Logger.info("Connected to event stream ...");
				// Connect browser
				actorInstance.tell(this, null);
			}
		});
	}

	private static class InvoiceActor extends UntypedActor {
		private final static ActorRef instance = Akka.system().actorOf(Props.create(InvoiceActor.class));

		private List<EventSource> sockets = new ArrayList<EventSource>();

		private void addSocket(final EventSource socket) {
			socket.onDisconnected(new Callback0() {
			    public void invoke() {
			        getContext().self().tell(socket, null);
			        removeSocket(socket);
			    }
			});
			// New browser connected
			sockets.add(socket);
			Logger.info("* New browser connected (" + sockets.size() + " browsers currently connected)");
		}

		private void removeSocket(EventSource socket) {
			// Browser is disconnected
			socket.close();
			sockets.remove(socket);
			Logger.info("* Browser disconnected (" + sockets.size() + " browsers currently connected)");
		}

		private void send(String data) {
			// Send the current time to all EventSource sockets
			List<EventSource> shallowCopy = new ArrayList<EventSource>(sockets); //prevent ConcurrentModificationException

			for(EventSource es: shallowCopy) {
			    es.sendData(data);
			}
		}

		/*
			Listening for messages. Can be connections (EventSources),
			JSON objects or plain objects (will call toString()).
		 */
		public void onReceive(Object message) {
			// Connect or disconnect browser
			if(message instanceof EventSource) {
				final EventSource eventSource = (EventSource) message;

				if(sockets.contains(eventSource)) {                    
				    removeSocket(eventSource);
				} else {                    
						addSocket(eventSource);
				}
			}

			// If JSON, stringify and send
			else if(message instanceof JsonNode) {
				JsonNode json = (JsonNode) message;
				this.send(Json.stringify(json));
			}

			else {
				this.send(message.toString());
			}
		}
	}
}

