@(invoice: Invoice)

var item = "@views.html.invoices.item.render(invoice)";

$(".invoice-list").prepend(item);