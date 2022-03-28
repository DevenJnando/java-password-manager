package com.jamesd.passwordmanager.Models.Passwords;

public class CreditDebitCardEntry extends PasswordEntry {
    private String cardNumber;
    private String expiryDate;
    private String securityCode;
    private String accountNumber;
    private String sortCode;

    public CreditDebitCardEntry() {
        super();
        this.cardNumber = "";
        this.expiryDate = "";
        this.securityCode = "";
        this.accountNumber = "";
        this.sortCode = "";
    }

    public CreditDebitCardEntry(String passwordName, String encryptedPassword, String dateSet, String expiryDate,
                                String securityCode) {
        super(passwordName, encryptedPassword, dateSet);
        this.cardNumber = encryptedPassword;
        this.expiryDate = expiryDate;
        this.securityCode = securityCode;
        this.accountNumber = "";
        this.sortCode = "";
    }

    public CreditDebitCardEntry(String passwordName, String encryptedPassword, String dateSet,
                                String expiryDate, String securityCode, String accountNumber, String sortCode) {
        super(passwordName, encryptedPassword, dateSet);
        this.cardNumber = encryptedPassword;
        this.expiryDate = expiryDate;
        this.securityCode = securityCode;
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
    }

    public CreditDebitCardEntry(String id, String passwordName, String encryptedPassword, String dateSet,
                                String expiryDate, String securityCode, String accountNumber, String sortCode) {
        super(id, passwordName, encryptedPassword, dateSet);
        this.cardNumber = encryptedPassword;
        this.expiryDate = expiryDate;
        this.securityCode = securityCode;
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }
}
