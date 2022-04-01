package com.jamesd.passwordmanager.Models.Passwords;

/**
 * Class which models credit/debit card entries within the database
 */
public class CreditDebitCardEntry extends PasswordEntry {
    private String cardNumber;
    private String masterUsername;
    private String expiryDate;
    private String securityCode;
    private String accountNumber;
    private String sortCode;

    /**
     * Default constructor
     */
    public CreditDebitCardEntry() {
        super();
        this.cardNumber = "";
        this.masterUsername = "";
        this.expiryDate = "";
        this.securityCode = "";
        this.accountNumber = "";
        this.sortCode = "";
    }

    /**
     * Constructor which takes the password name, card number, last updated date in the database, card expiry date and
     * security code as parameters
     * @param passwordName String of the password name
     * @param cardNumber String of the card number
     * @param dateSet String of the last updated date in the database
     * @param expiryDate String of the card's expiry date
     * @param securityCode String of the card's security code
     */
    public CreditDebitCardEntry(String passwordName, String cardNumber, String masterUsername, String dateSet, String expiryDate,
                                String securityCode) {
        super(passwordName, cardNumber, dateSet);
        this.cardNumber = cardNumber;
        this.masterUsername = masterUsername;
        this.expiryDate = expiryDate;
        this.securityCode = securityCode;
        this.accountNumber = "";
        this.sortCode = "";
    }

    /**
     * Constructor which takes the password name, card number, last updated date in the database, card expiry date,
     * security code, account number and sort code as parameters
     * @param passwordName String of the password name
     * @param cardNumber String of the card number
     * @param dateSet String of the last updated date in the database
     * @param expiryDate String of the card's expiry date
     * @param securityCode String of the card's security code
     * @param accountNumber String of the account number this card belongs to
     * @param sortCode String of the sort code this card belongs to
     */
    public CreditDebitCardEntry(String passwordName, String cardNumber, String masterUsername, String dateSet,
                                String expiryDate, String securityCode, String accountNumber, String sortCode) {
        super(passwordName, cardNumber, dateSet);
        this.cardNumber = cardNumber;
        this.masterUsername = masterUsername;
        this.expiryDate = expiryDate;
        this.securityCode = securityCode;
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
    }

    /**
     * Constructor which takes the credit/debit card entry ID in the database, password name, card number,
     * last updated date in the database, card expiry date, security code, account number and sort code as parameters
     * @param id String of the credit/debit card ID in the database
     * @param passwordName String of the password name
     * @param cardNumber String of the card number
     * @param dateSet String of the last updated date in the database
     * @param expiryDate String of the card's expiry date
     * @param securityCode String of the card's security code
     * @param accountNumber String of the account number this card belongs to
     * @param sortCode String of the sort code this card belongs to
     */
    public CreditDebitCardEntry(String id, String passwordName, String cardNumber, String masterUsername,  String dateSet,
                                String expiryDate, String securityCode, String accountNumber, String sortCode) {
        super(id, passwordName, cardNumber, dateSet);
        this.cardNumber = cardNumber;
        this.masterUsername = masterUsername;
        this.expiryDate = expiryDate;
        this.securityCode = securityCode;
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
    }

    /**
     * Retrieves the card number
     * @return Card number String
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Retrieves the username in this application
     * @return Master username String
     */
    public String getMasterUsername() {
        return masterUsername;
    }

    /**
     * Sets the username in this application
     * @param masterUsername Master username String
     */
    public void setMasterUsername(String masterUsername) {
        this.masterUsername = masterUsername;
    }
    /**
     * Sets the card number
     * @param cardNumber Card number String
     */
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * Retrieves the card's expiry date
     * @return Card's expiry date String
     */
    public String getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the card's expiry date
     * @param expiryDate Card's expiry date String
     */
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * Retrieves the card's security code
     * @return Card's security code String
     */
    public String getSecurityCode() {
        return securityCode;
    }

    /**
     * Sets the card's security code
     * @param securityCode Card's security code String
     */
    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    /**
     * Gets the card's account number
     * @return Card's account number String
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the card's account number
     * @param accountNumber Card's account number String
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Retrieves the card's sort code
     * @return Card's sort code String
     */
    public String getSortCode() {
        return sortCode;
    }

    /**
     * Sets the card's sort code
     * @param sortCode Card's sort code String
     */
    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }
}
