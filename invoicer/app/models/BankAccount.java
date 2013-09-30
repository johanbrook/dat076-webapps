package models;

import javax.persistence.Column;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

public class BankAccount extends Model {

	@Id
	public Long id;

	@Required
	@Column(nullable=false)
	public String accountNumber;

	public String bank;

	public String iban;

	public String bic;

	@Required
	@Column(nullable=false)
	public AccountType accountType;

	public enum AccountType{
		PG, BG, BUSINESSACCOUNT
	}

	// Finder object
	public static Model.Finder<Long, BankAccount> find = new Model.Finder<Long, BankAccount>(Long.class, BankAccount.class);

	public BankAccount(String accountNumber, AccountType accountType) {
		this.accountNumber = accountNumber;
		this.accountType = accountType;
	}
	public BankAccount(String accountNumber, AccountType accountType, String bank){
		this(accountNumber, accountType);
		this.bank = bank;
	}

	public BankAccount(String accountNumber, AccountType accountType, String bank, String iban, String bic){
		this(accountNumber, accountType, bank);
		this.iban = iban;
		this.bic = bic;
	}

}
