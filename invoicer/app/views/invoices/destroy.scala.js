@(invoice : Invoice)

$("#invoice-@invoice.id").addClass("removed").on("webkitTransitionEnd", function(evt) {
	$(this).remove();
});
