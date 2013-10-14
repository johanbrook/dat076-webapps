@(client: Client) 

$("#client-@client.id").remove();
Util.showNotice("The client was removed");