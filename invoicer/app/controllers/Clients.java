/**
*	ClientController.java
*
*	@author Andreas Rolén
*	@copyright (c) 2013 Andreas Rolén
*	@license MIT
*/

package controllers;

import models.Client;
import play.data.Form;
import play.mvc.Controller;

public class Clients extends Controller {

	public static Form<Client> newForm = Form.form(Client.class);
}
