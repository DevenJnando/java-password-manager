package com.jamesd.passwordmanager.Models.HierarchyModels;

/**
 * Class to model a country's telephone code
 */
public class CountryCode {

    private String countryName;
    private String countryCode;

    /**
     * Default constructor
     */
    public CountryCode() {
        this.countryName = "";
        this.countryCode = "";
    }

    /**
     * Constructor which assigns the country name and code
     * @param countryName Country name String
     * @param countryCode Country code String
     */
    public CountryCode(String countryName, String countryCode) {
        this.countryName = countryName;
        this.countryCode = countryCode;
    }

    /**
     * Getter for country name
     * @return Country name
     */
    public String getCountryName() {
        return this.countryName;
    }

    /**
     * Setter for country name
     * @param countryName Country name to be set
     */
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    /**
     * Getter for country code
     * @return Country code
     */
    public String getCountryCode() {
        return this.countryCode;
    }

    /**
     * Setter for country code
     * @param countryCode Country code to be set
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
