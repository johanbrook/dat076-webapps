@(account: BankAccount)

var item = "@views.html.bankaccounts.row.render(account)";

$(".accounts-table").prepend(item).find("tr:first").addClass("added");
