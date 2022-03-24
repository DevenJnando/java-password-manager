package com.jamesd.passwordmanager.Tables;

import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.Models.Passwords.PasswordEntry;
import com.jamesd.passwordmanager.Wrappers.BaseWrapper;
import com.jamesd.passwordmanager.Wrappers.WebsitePasswordEntryWrapper;
import javafx.scene.control.TableView;

import javax.security.auth.login.LoginException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public abstract class BasePasswordTable<T extends BaseWrapper, K extends PasswordEntry> {

    protected abstract void loadColumns();

    protected abstract List<T> wrapPasswords(List<K> passwordEntries) throws MalformedURLException;

    public abstract TableView<T> createTableView(PasswordEntryFolder folder) throws MalformedURLException,
            LoginException, ClassNotFoundException;

    protected void checkIfPasswordNeedsUpdated(List<K> passwordEntries) {
        for(K entry : passwordEntries) {
            if(passwordNeedsUpdated(entry.getDateSet())) {
                long daysOutOfDate = daysSinceLastUpdate(entry.getDateSet());
                String outOfDateMessage = daysOutOfDate > 1
                        ? "Password is " + daysOutOfDate + " days out of date!"
                        : "Password is " + daysOutOfDate + " day out of date!";
                entry.setNeedsUpdatedMessage(outOfDateMessage);
            }
        }
    }
    protected long daysSinceLastUpdate(String passwordEntryDateSet) {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateSet = LocalDate.parse(passwordEntryDateSet, formatter);
        long daysSinceLastUpdate = dateSet.until(currentDate, ChronoUnit.DAYS);
        return daysSinceLastUpdate;
    }

    protected Boolean passwordNeedsUpdated(String passwordEntryDateSet) {
        Boolean needsUpdated = false;
        long daysSinceLastUpdate = daysSinceLastUpdate(passwordEntryDateSet);
        if(daysSinceLastUpdate > 182) {
            needsUpdated = true;
        }
        return needsUpdated;
    }
}
