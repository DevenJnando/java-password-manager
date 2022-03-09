package com.jamesd.passwordmanager.Models.HierarchyModels;

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

    }

    public static class CreditCardEntryBuilder {

    }

    public static class PassportEntryBuilder {

    }
    public static class DocumentEntryBuilder {

    }
}
