package com.jamesd.passwordmanager.Models.HierarchyModels;

import com.jamesd.passwordmanager.Models.Passwords.CreditDebitCardEntry;
import com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry;
import com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry;

public class PasswordEntryBuilder {

    public static class WebsitePasswordEntryBuilder {

        private String id;
        private String passwordName;
        private String encryptedPassword;
        private String siteUrl;
        private String masterUsername;
        private String passwordUsername;
        private String dateSet;

        public static WebsitePasswordEntryBuilder newInstance() {
            return new WebsitePasswordEntryBuilder();
        }

        public WebsitePasswordEntryBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public WebsitePasswordEntryBuilder withPasswordName(String passwordName) {
            this.passwordName = passwordName;
            return this;
        }

        public WebsitePasswordEntryBuilder withEncryptedPassword(String encryptedPassword) {
            this.encryptedPassword = encryptedPassword;
            return this;
        }

        public WebsitePasswordEntryBuilder withSiteUrl(String siteUrl) {
            this.siteUrl = siteUrl;
            return this;
        }

        public WebsitePasswordEntryBuilder withMasterUsername(String masterUsername) {
            this.masterUsername = masterUsername;
            return this;
        }

        public WebsitePasswordEntryBuilder withPasswordUsername(String passwordUsername) {
            this.passwordUsername = passwordUsername;
            return this;
        }

        public WebsitePasswordEntryBuilder withDateSet(String dateSet) {
            this.dateSet = dateSet;
            return this;
        }

        public WebsitePasswordEntry build() {
            if(id != null) {
                return new WebsitePasswordEntry(this.id,
                        this.passwordName,
                        this.siteUrl,
                        this.masterUsername,
                        this.passwordUsername,
                        this.dateSet,
                        this.encryptedPassword);
            } else {
                return new WebsitePasswordEntry(this.passwordName,
                        this.siteUrl,
                        this.masterUsername,
                        this.passwordUsername,
                        this.dateSet,
                        this.encryptedPassword);
            }
        }
    }

    public static class DatabasePasswordEntryBuilder {

        private String id;
        private String passwordName;
        private String encryptedPassword;
        private String hostName;
        private String databaseName;
        private String masterUsername;
        private String databaseUsername;
        private String dateSet;

        public static DatabasePasswordEntryBuilder newInstance() {
            return new DatabasePasswordEntryBuilder();
        }

        public DatabasePasswordEntryBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public DatabasePasswordEntryBuilder withPasswordName(String passwordName) {
            this.passwordName = passwordName;
            return this;
        }

        public DatabasePasswordEntryBuilder withEncryptedPassword(String encryptedPassword) {
            this.encryptedPassword = encryptedPassword;
            return this;
        }

        public DatabasePasswordEntryBuilder withHostName(String hostName) {
            this.hostName = hostName;
            return this;
        }

        public DatabasePasswordEntryBuilder withDatabaseName(String databaseName) {
            this.databaseName = databaseName;
            return this;
        }

        public DatabasePasswordEntryBuilder withMasterUsername(String masterUsername) {
            this.masterUsername = masterUsername;
            return this;
        }

        public DatabasePasswordEntryBuilder withDatabaseUsername(String databaseUsername) {
            this.databaseUsername = databaseUsername;
            return this;
        }

        public DatabasePasswordEntryBuilder withDateSet(String dateSet) {
            this.dateSet = dateSet;
            return this;
        }

        public DatabasePasswordEntry build() {
            if(id != null) {
                return new DatabasePasswordEntry(this.id,
                        this.passwordName,
                        this.hostName,
                        this.databaseName,
                        this.masterUsername,
                        this.databaseUsername,
                        this.dateSet,
                        this.encryptedPassword);
            } else {
                return new DatabasePasswordEntry(this.passwordName,
                        this.hostName,
                        this.databaseName,
                        this.masterUsername,
                        this.databaseUsername,
                        this.dateSet,
                        this.encryptedPassword);
            }
        }

    }

    public static class CreditDebitCardEntryBuilder {

        private String id;
        private String passwordName;
        private String creditDebitCardNumber;
        private String expiryDate;
        private String securityCode;
        private String dateSet;
        private String accountNumber;
        private String sortCode;

        public static CreditDebitCardEntryBuilder newInstance() {
            return new CreditDebitCardEntryBuilder();
        }

        public CreditDebitCardEntryBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public CreditDebitCardEntryBuilder withPasswordName(String passwordName) {
            this.passwordName = passwordName;
            return this;
        }

        public CreditDebitCardEntryBuilder withCreditDebitCardNumber(String creditDebitCardNumber) {
            this.creditDebitCardNumber = creditDebitCardNumber;
            return this;
        }

        public CreditDebitCardEntryBuilder withExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public CreditDebitCardEntryBuilder withSecurityCode(String securityCode) {
            this.securityCode = securityCode;
            return this;
        }

        public CreditDebitCardEntryBuilder withDateSet(String dateSet) {
            this.dateSet = dateSet;
            return this;
        }

        public CreditDebitCardEntryBuilder withAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public CreditDebitCardEntryBuilder withSortCode(String sortCode) {
            this.sortCode = sortCode;
            return this;
        }

        public CreditDebitCardEntry build() {
            if(id == null &&
            accountNumber == null && sortCode == null) {
                return new CreditDebitCardEntry(
                        this.passwordName,
                        this.creditDebitCardNumber,
                        this.dateSet,
                        this.expiryDate,
                        this.securityCode);
            } else if(id == null) {
                return new CreditDebitCardEntry(
                        this.passwordName,
                        this.creditDebitCardNumber,
                        this.dateSet,
                        this.expiryDate,
                        this.securityCode,
                        this.accountNumber,
                        this.sortCode);
            } else {
                return new CreditDebitCardEntry(
                        this.id,
                        this.creditDebitCardNumber,
                        this.dateSet,
                        this.expiryDate,
                        this.securityCode,
                        this.accountNumber,
                        this.sortCode);
            }
        }
    }

    public static class PassportEntryBuilder {

    }
    public static class DocumentEntryBuilder {

    }
}
