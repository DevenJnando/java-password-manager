package com.jamesd.passwordmanager.Tables;

import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.Models.Passwords.CreditDebitCardEntry;
import com.jamesd.passwordmanager.Models.Passwords.PasswordEntry;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Wrappers.BaseWrapper;
import javafx.scene.control.TableView;

import javax.security.auth.login.LoginException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Abstract class which takes a subclass of BaseWrapper and a subclass of PasswordEntry. Contains common methods and
 * abstract methods for building a table of PasswordEntry tables.
 * @param <T> subclass of BaseWrapper
 * @param <K> subclass of PasswordEntry
 */
public abstract class BasePasswordTable<T extends BaseWrapper, K extends PasswordEntry> {

    /**
     * Creates the relevant columns for the table being built
     */
    protected abstract void loadColumns();

    /**
     * Wraps the PasswordEntry subclass objects in a BaseWrapper subclass
     * @param passwordEntries List of PasswordEntry subclass objects to be wrapped
     * @return List of wrapped BaseWrapper subclass objects
     * @throws MalformedURLException Throws MalformedURLException if a password logo cannot be retrieved from the given
     * URL
     */
    protected abstract List<T> wrapPasswords(List<K> passwordEntries) throws MalformedURLException;

    /**
     * Creates the TableView object by wrapping the PasswordEntry subclass objects in a BaseWrapper subclass object
     * and populating the TableView with these wrapped passwords
     * @param folder PasswordEntryFolder object which the PasswordEntry subclass objects belong to
     * @return TableView containing the BaseWrapper subclass objects
     * @throws MalformedURLException Throws MalformedURLException if a password logo cannot be retrieved from the given
     * URL
     * @throws LoginException Throws LoginException if this method is called whilst the user is not logged in
     * @throws ClassNotFoundException Throws ClassNotFoundException if any subclass of PasswordEntry or BaseWrapper
     * cannot be found
     */
    public abstract TableView<T> createTableView(PasswordEntryFolder folder) throws MalformedURLException,
            LoginException, ClassNotFoundException;

    /**
     * Examines a List of PasswordEntry subclass objects and checks if they need updated. If they do, a reminder message
     * is set for that PasswordEntry subclass object
     * @param passwordEntries List of PasswordEntry subclass objects
     */
    protected void checkIfPasswordNeedsUpdated(List<K> passwordEntries) throws ClassNotFoundException {
        for(K entry : passwordEntries) {
            Class<?> creditDebitCardClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.CreditDebitCardEntry");
            if(creditDebitCardClass.isInstance(entry)) {
                CreditDebitCardEntry creditDebitCardEntry = (CreditDebitCardEntry) entry;
                if(!creditDebitCardEntry.getExpiryDate().isEmpty()) {
                    if (isCreditDebitCardExpired(creditDebitCardEntry.getExpiryDate())) {
                        String expiredCardMessage = "Credit/Debit card has expired!";
                        creditDebitCardEntry.setNeedsUpdatedMessage(expiredCardMessage);
                    }
                }
            } else {
                if (passwordNeedsUpdated(entry.getDateSet(), PasswordManagerApp.getLoggedInUser().getReminderTimePeriod())) {
                    long daysOutOfDate = daysSinceLastUpdate(entry.getDateSet());
                    String outOfDateMessage = daysOutOfDate > 1
                            ? "Password is " + daysOutOfDate + " days out of date!"
                            : "Password is " + daysOutOfDate + " day out of date!";
                    entry.setNeedsUpdatedMessage(outOfDateMessage);
                }
            }
        }
    }

    /**
     * Gets how long it has been since a PasswordEntry subclass object has been updated
     * @param passwordEntryDateSet Last updated date of a PasswordEntry subclass object
     * @return Number of days since last update as a long
     */
    protected long daysSinceLastUpdate(String passwordEntryDateSet) {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateSet = LocalDate.parse(passwordEntryDateSet, formatter);
        return dateSet.until(currentDate, ChronoUnit.DAYS);
    }

    protected Boolean isCreditDebitCardExpired(String expiryDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yy");
        YearMonth expiryYearMonth = YearMonth.parse(expiryDate, formatter);
        LocalDate expiryLocalDate = expiryYearMonth.atDay(1);
        LocalDate currentDate = LocalDate.now();
        return expiryLocalDate.isBefore(currentDate);
    }

    /**
     * Determines whether a password reminder message needs to be set or not
     * @param passwordEntryDateSet Last updated date of a PasswordEntry subclass object
     * @return Boolean true if needs a reminder set, else false
     */
    protected Boolean passwordNeedsUpdated(String passwordEntryDateSet, String reminderTimePeriod) {
        Boolean needsUpdated = false;
        long daysSinceLastUpdate = daysSinceLastUpdate(passwordEntryDateSet);
        switch(reminderTimePeriod) {
            case "1 month":
                if(daysSinceLastUpdate > 30) {
                    needsUpdated = true;
                }
                break;
            case "6 months":
                if(daysSinceLastUpdate > 182) {
                    needsUpdated = true;
                }
                break;
            case "1 year":
                if(daysSinceLastUpdate > 365) {
                    needsUpdated = true;
                }
                break;
        }
        return needsUpdated;
    }
}
