@(invoice : Invoice)

$("#invoice-@invoice.id").find(".star").toggleClass("starred");