@(account: BankAccount) 

$("#account-@account.id").remove();
Util.showNotice("The account was removed");