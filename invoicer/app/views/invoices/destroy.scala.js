@(invoice : Invoice)

$("#invoice-@invoice.id").addClass("removed").on("webkitTransitionEnd", function(evt) {
	$(this).remove();
});

Util.decrementInvoices();
Util.showNotice("The invoice was removed forever and ever");