package com.jamesd.passwordmanager.Models.Users;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class RecognisedUserDevice {

    private String macAddress;
    private String dateAdded;
    private String daysInDatabase;

    public RecognisedUserDevice() {
        macAddress = "0";
        dateAdded = LocalDate.now().toString();
        daysInDatabase = "0";
    }

    public RecognisedUserDevice(String macAddress) {
        this.macAddress = macAddress;
        this.dateAdded = LocalDate.now().toString();
        this.daysInDatabase = "0";
    }

    public RecognisedUserDevice(String macAddress, String dateAdded) {
        this.macAddress = macAddress;
        this.dateAdded = dateAdded;
        this.daysInDatabase = String.valueOf(daysSinceAdded(this.dateAdded));
    }

    private long daysSinceAdded(String dateAddedString) {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateAdded = LocalDate.parse(dateAddedString, formatter);
        return dateAdded.until(currentDate, ChronoUnit.DAYS);
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getDaysInDatabase() {
        return daysInDatabase;
    }

    public void setDaysInDatabase(String daysInDatabase) {
        this.daysInDatabase = daysInDatabase;
    }
}
