@(invoice: Invoice)

var item = "@views.html.invoices.item.render(invoice)";

// TODO: put the rendered item into the correct spot in 
// the list.
$(".invoice-list").prepend(item);

incrementTotal($("#invoices-total"));

function incrementTotal(element) {
	element.text(parseInt(element.text()) + 1);
}