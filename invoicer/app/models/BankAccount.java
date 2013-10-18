package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class BankAccount extends AbstractModel {

	@Column(nullable=false)
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JsonIgnore
	public User owner;
	
	@Required
	@Column(nullable=false)
	public AccountType accountType;
	
	@Required
	@Pattern(value = "([0-9]{4}-[0-9]{4})|([0-9]{1,6}(-[0-9]){1})|([0-9]{4}(-[0-9]{1})[0-9]{0,9}(-[0-9]){0,1})")
	@Column(nullable=false)
	public String accountNumber;

	public String bank;

	@Pattern(value = "[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{4}[0-9]{7}([a-zA-Z0-9]?){0,16}")
	public String iban;

	@Pattern(value = "([a-zA-Z]{4}[a-zA-Z]{2}[a-zA-Z0-9]{2}([a-zA-Z0-9]{3})?)")
	public String bic;

	public enum AccountType{
		PG("PG"),
		BG("BG"),
		BUSINESSACCOUNT("Business Account");
		
		private String name;
		
		private AccountType(String name){
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
		
	}

	// Finder object
	public static Model.Finder<Long, BankAccount> find = new Model.Finder<Long, BankAccount>(Long.class, BankAccount.class);

	public BankAccount(User owner, String accountNumber, AccountType accountType) {
		this.owner = owner;
		this.accountNumber = accountNumber;
		this.accountType = accountType;
	}
	public BankAccount(User owner, String accountNumber, AccountType accountType, String bank){
		this(owner, accountNumber, accountType);
		this.bank = bank;
	}

	public BankAccount(User owner, String accountNumber, AccountType accountType, String bank, String iban, String bic){
		this(owner, accountNumber, accountType, bank);
		this.iban = iban;
		this.bic = bic;
	}
	
	public static com.avaje.ebean.Query<BankAccount> bankAccountsOfUser(Long userId) {
		return find.where().like("owner", String.valueOf(userId)).orderBy("accountType");
	}
	
	public static List<BankAccount> getBankAccountsOfUser(Long userId) {
		return bankAccountsOfUser(userId).findList();
	}

	public String toString() {
		return String.valueOf(this.accountType.getName()) + " " + this.accountNumber;
	}

}
