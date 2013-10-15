@(client: Client)

var item = "@views.html.clients.row.render(client)";

$(".clients-table").prepend(item).find("tr:first").addClass("added");
