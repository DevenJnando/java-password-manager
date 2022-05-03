package com.jamesd.passwordmanager.Models.HierarchyModels;

import com.jamesd.passwordmanager.Models.Passwords.CreditDebitCardEntry;
import com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry;
import com.jamesd.passwordmanager.Models.Passwords.DocumentEntry;
import com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry;

/**
 * Builder class which is used to build all subclasses of PasswordEntry. Used in conjunction with the EntryFactory when
 * retrieving PasswordEntry objects from the password database
 */
public class PasswordEntryBuilder {

    /**
     * Static class which builds a new WebsitePasswordEntry
     */
    public static class WebsitePasswordEntryBuilder {

        private String id;
        private String passwordName;
        private String encryptedPassword;
        private String siteUrl;
        private String masterUsername;
        private String passwordUsername;
        private String dateSet;

        /**
         * Creates a new instance of a WebsitePasswordEntryBuilder object
         * @return WebsitePasswordEntryBuilder object
         */
        public static WebsitePasswordEntryBuilder newInstance() {
            return new WebsitePasswordEntryBuilder();
        }

        /**
         * Sets the ID for the WebsitePasswordEntry object to have
         * @param id ID of the WebsitePasswordEntry
         * @return This WebsitePasswordEntryBuilder object
         */
        public WebsitePasswordEntryBuilder withId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the password name for the WebsitePasswordEntry object to have
         * @param passwordName Password name of the WebsitePasswordEntry
         * @return This WebsitePasswordEntryBuilder object
         */
        public WebsitePasswordEntryBuilder withPasswordName(String passwordName) {
            this.passwordName = passwordName;
            return this;
        }

        /**
         * Sets the encrypted password for the WebsitePasswordEntry object to have
         * @param encryptedPassword Encrypted password of the WebsitePasswordEntry
         * @return This WebsitePasswordEntryBuilder object
         */
        public WebsitePasswordEntryBuilder withEncryptedPassword(String encryptedPassword) {
            this.encryptedPassword = encryptedPassword;
            return this;
        }

        /**
         * Sets the URL for the WebsitePasswordEntry object to have
         * @param siteUrl Website URL of the WebsitePasswordEntry
         * @return This WebsitePasswordEntryBuilder object
         * */
        public WebsitePasswordEntryBuilder withSiteUrl(String siteUrl) {
            this.siteUrl = siteUrl;
            return this;
        }

        /**
         * Sets the username of the logged-in user in this application for the WebsitePasswordEntry object to have
         * @param masterUsername Master username of the WebsitePasswordEntry
         * @return This WebsitePasswordEntryBuilder object
         * */
        public WebsitePasswordEntryBuilder withMasterUsername(String masterUsername) {
            this.masterUsername = masterUsername;
            return this;
        }

        /**
         * Sets the username of the logged-in user in their website password for the WebsitePasswordEntry object to have
         * @param passwordUsername Password username of the WebsitePasswordEntry
         * @return This WebsitePasswordEntryBuilder object
         */
        public WebsitePasswordEntryBuilder withPasswordUsername(String passwordUsername) {
            this.passwordUsername = passwordUsername;
            return this;
        }

        /**
         * Sets the last updated date for the WebsitePasswordEntry to have
         * @param dateSet Date last updated of the WebsitePasswordEntry
         * @return This WebsitePasswordEntryBuilder object
         */
        public WebsitePasswordEntryBuilder withDateSet(String dateSet) {
            this.dateSet = dateSet;
            return this;
        }

        /**
         * Builds the WebsitePasswordEntry using the specified fields
         * @return New WebsitePasswordEntry object
         */
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

    /**
     * Static class which builds a new DatabasePasswordEntry object
     */
    public static class DatabasePasswordEntryBuilder {

        private String id;
        private String passwordName;
        private String encryptedPassword;
        private String hostName;
        private String databaseName;
        private String masterUsername;
        private String databaseUsername;
        private String dateSet;

        /**
         * Creates a new instance of a DatabasePasswordEntryBuilder object
         * @return DatabasePasswordEntryBuilder object
         */
        public static DatabasePasswordEntryBuilder newInstance() {
            return new DatabasePasswordEntryBuilder();
        }

        /**
         * Sets the ID for the DatabasePasswordEntry object to have
         * @param id ID of the DatabasePasswordEntry
         * @return This DatabasePasswordEntryBuilder object
         */
        public DatabasePasswordEntryBuilder withId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the password name for the DatabasePasswordEntry object to have
         * @param passwordName Password name of the DatabasePasswordEntry
         * @return This DatabasePasswordEntryBuilder object
         */
        public DatabasePasswordEntryBuilder withPasswordName(String passwordName) {
            this.passwordName = passwordName;
            return this;
        }

        /**
         * Sets the encrypted password for the DatabasePasswordEntry object to have
         * @param encryptedPassword Encrypted password of the DatabasePasswordEntry
         * @return This DatabasePasswordEntryBuilder object
         */
        public DatabasePasswordEntryBuilder withEncryptedPassword(String encryptedPassword) {
            this.encryptedPassword = encryptedPassword;
            return this;
        }

        /**
         * Sets the hostname for the DatabasePasswordEntry object to have
         * @param hostName Hostname of the DatabasePasswordEntry
         * @return This DatabasePasswordEntryBuilder object
         */
        public DatabasePasswordEntryBuilder withHostName(String hostName) {
            this.hostName = hostName;
            return this;
        }

        /**
         * Sets the database name for the DatabasePasswordEntry object to have
         * @param databaseName Database name of the DatabasePasswordEntry
         * @return This DatabasePasswordEntryBuilder object
         */
        public DatabasePasswordEntryBuilder withDatabaseName(String databaseName) {
            this.databaseName = databaseName;
            return this;
        }

        /**
         * Sets the username of the logged-in user in this application for the DatabasePasswordEntry object to have
         * @param masterUsername Master username of the DatabasePasswordEntry
         * @return This DatabasePasswordEntryBuilder object
         * */
        public DatabasePasswordEntryBuilder withMasterUsername(String masterUsername) {
            this.masterUsername = masterUsername;
            return this;
        }

        /**
         * Sets the username of the logged-in user in their database password for the DatabasePasswordEntry object to have
         * @param databaseUsername Password username of the DatabasePasswordEntry
         * @return This DatabasePasswordEntryBuilder object
         */
        public DatabasePasswordEntryBuilder withDatabaseUsername(String databaseUsername) {
            this.databaseUsername = databaseUsername;
            return this;
        }

        /**
         * Sets the last updated date for the DatabasePasswordEntry to have
         * @param dateSet Date last updated of the DatabasePasswordEntry
         * @return This DatabasePasswordEntryBuilder object
         */
        public DatabasePasswordEntryBuilder withDateSet(String dateSet) {
            this.dateSet = dateSet;
            return this;
        }

        /**
         * Builds the new DatabasePasswordEntry object with the specified fields
         * @return New DatabasePasswordEntry object
         */
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

    /**
     * Static class which builds a new CreditDebitCardEntry object
     */
    public static class CreditDebitCardEntryBuilder {

        private String id;
        private String passwordName;
        private String creditDebitCardNumber;
        private String creditDebitCardType;
        private String masterUsername;
        private String expiryDate;
        private String securityCode;
        private String dateSet;
        private String accountNumber;
        private String sortCode;

        /**
         * Creates a new instance of a CreditDebitCardEntryBuilder object
         * @return CreditDebitCardEntryBuilder object
         */
        public static CreditDebitCardEntryBuilder newInstance() {
            return new CreditDebitCardEntryBuilder();
        }

        /**
         * Sets the ID for the CreditDebitCardEntry object to have
         * @param id ID of the CreditDebitCardEntry
         * @return This CreditDebitCardEntryBuilder object
         */
        public CreditDebitCardEntryBuilder withId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the password name for the CreditDebitCardEntry object to have
         * @param passwordName Password name of the CreditDebitCardEntry
         * @return This CreditDebitCardEntryBuilder object
         */
        public CreditDebitCardEntryBuilder withPasswordName(String passwordName) {
            this.passwordName = passwordName;
            return this;
        }

        /**
         * Sets the credit/debit card number for the CreditDebitCardEntry object to have
         * @param creditDebitCardNumber Credit/debit card number of the CreditDebitCardEntry
         * @return This CreditDebitCardEntryBuilder object
         */
        public CreditDebitCardEntryBuilder withCreditDebitCardNumber(String creditDebitCardNumber) {
            this.creditDebitCardNumber = creditDebitCardNumber;
            return this;
        }

        /**
         * Sets the credit/debit card type for the CreditDebitCardEntry object to have
         * @param creditDebitCardType Credit/debit card type of the CreditDebitCardEntry
         * @return This CreditDebitCardEntryBuilder object
         */
        public CreditDebitCardEntryBuilder withCreditDebitCardType(String creditDebitCardType) {
            this.creditDebitCardType = creditDebitCardType;
            return this;
        }

        /**
         * Sets the username in this application for the CreditDebitCardEntry object to have
         * @param masterUsername Master username of the CreditDebitCardEntry
         * @return This CreditDebitCardEntryBuilder object
         */
        public CreditDebitCardEntryBuilder withMasterUsername(String masterUsername) {
            this.masterUsername = masterUsername;
            return this;
        }

        /**
         * Sets the expiry date for the CreditDebitCardEntry object
         * @param expiryDate Expiry date of the CreditDebitCardEntry
         * @return This CreditDebitCardEntryBuilder object
         */
        public CreditDebitCardEntryBuilder withExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        /**
         * Sets the security code for the CreditDebitCardEntry object
         * @param securityCode Security code of the CreditDebitCardEntry
         * @return This CreditDebitCardEntryBuilder object
         */
        public CreditDebitCardEntryBuilder withSecurityCode(String securityCode) {
            this.securityCode = securityCode;
            return this;
        }

        /**
         * Sets the last updated date for the CreditDebitCardEntry object
         * @param dateSet Last updated date of the CreditDebitCardEntry
         * @return This CreditDebitCardEntryBuilder object
         */
        public CreditDebitCardEntryBuilder withDateSet(String dateSet) {
            this.dateSet = dateSet;
            return this;
        }

        /**
         * Sets the account number for the CreditDebitCardEntry object
         * @param accountNumber Account number of the CreditDebitCardEntry
         * @return This CreditDebitCardEntryBuilder object
         */
        public CreditDebitCardEntryBuilder withAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        /**
         * Sets the sort code for the CreditDebitCardEntry object
         * @param sortCode Sort code of the CreditDebitCardEntry
         * @return This CreditDebitCardEntryBuilder object
         */
        public CreditDebitCardEntryBuilder withSortCode(String sortCode) {
            this.sortCode = sortCode;
            return this;
        }

        /**
         * Builds a new CreditDebitCardEntry object using the specified fields
         * @return New CreditDebitCardEntry object
         */
        public CreditDebitCardEntry build() {
            if(id == null) {
                return new CreditDebitCardEntry(
                        this.passwordName,
                        this.creditDebitCardNumber,
                        this.creditDebitCardType,
                        this.masterUsername,
                        this.dateSet,
                        this.expiryDate,
                        this.securityCode,
                        this.accountNumber,
                        this.sortCode);
            } else {
                return new CreditDebitCardEntry(
                        this.id,
                        this.passwordName,
                        this.creditDebitCardNumber,
                        this.creditDebitCardType,
                        this.masterUsername,
                        this.dateSet,
                        this.expiryDate,
                        this.securityCode,
                        this.accountNumber,
                        this.sortCode);
            }
        }
    }

    /**
     * Static class which builds a new DocumentEntry object
     */
    public static class DocumentEntryBuilder {
        private String id;
        private String documentName;
        private String documentDescription;
        private String masterUsername;
        private String dateSet;
        private String documentStorageReference;

        /**
         * Creates a new instance of a DocumentEntryBuilder object
         * @return DocumentEntryBuilder object to be populated
         */
        public static DocumentEntryBuilder newInstance() {
            return new DocumentEntryBuilder();
        }

        /**
         * Sets the ID to use when building the DocumentEntry object
         * @param id Assigned ID
         * @return DocumentEntryBuilder object
         */
        public DocumentEntryBuilder withId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the name to use when building the DocumentEntry object
         * @param documentName Assigned name
         * @return DocumentEntryBuilder object
         */
        public DocumentEntryBuilder withName(String documentName) {
            this.documentName = documentName;
            return this;
        }

        /**
         * Sets the description to use when building the DocumentEntry object
         * @param documentDescription Assigned description
         * @return DocumentEntryBuilder object
         */
        public DocumentEntryBuilder withDescription(String documentDescription) {
            this.documentDescription = documentDescription;
            return this;
        }

        /**
         * Sets the master username to use when building the DocumentEntry object
         * @param masterUsername Assigned master username
         * @return DocumentEntryBuilder object
         */
        public DocumentEntryBuilder withMasterUsername(String masterUsername) {
            this.masterUsername = masterUsername;
            return this;
        }

        /**
         * Sets the creation/last modification date to use when building the DocumentEntry object
         * @param dateSet Assigned creation/last modified date
         * @return DocumentEntryBuilder object
         */
        public DocumentEntryBuilder withDateSet(String dateSet) {
            this.dateSet = dateSet;
            return this;
        }

        /**
         * Sets the storage reference for the document in the user's storage container to use when building the
         * DocumentEntry object
         * @param documentStorageReference Assigned storage reference
         * @return DocumentEntryBuilder object
         */
        public DocumentEntryBuilder withStorageReference(String documentStorageReference) {
            this.documentStorageReference = documentStorageReference;
            return this;
        }

        /**
         * Builds the DocumentEntry object using the fields assigned to the DocumentEntryBuilder object
         * @return The fully built DocumentEntry object
         */
        public DocumentEntry build() {
            if(id == null) {
                return new DocumentEntry(
                        this.documentName,
                        this.documentDescription,
                        this.masterUsername,
                        this.dateSet,
                        this.documentStorageReference
                );
            } else {
                return new DocumentEntry(
                        this.id,
                        this.documentName,
                        this.documentDescription,
                        this.masterUsername,
                        this.dateSet,
                        this.documentStorageReference
                );
            }
        }
    }
}
