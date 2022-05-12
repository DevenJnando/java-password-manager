package com.jamesd.passwordmanager.Utils;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.jamesd.passwordmanager.Models.HierarchyModels.CountryCode;
import javafx.fxml.Initializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Comparator;


/**
 * Utility class which obtains the list of country codes.
 */
public abstract class PhoneNumberLocaleUtil {

    /**
     * Obtains the full list of telephone country codes
     */
    public static List<CountryCode> getPhoneNumberList() {
        Set<String> set = PhoneNumberUtil.getInstance().getSupportedRegions();

        String[] arr = set.toArray(new String[0]);
        List<CountryCode> countryCodeList = new ArrayList<>();
        for (String s : arr) {
            Locale locale = new Locale("en", s);
            CountryCode countryCode = new CountryCode(locale.getDisplayCountry(), String.valueOf(PhoneNumberUtil.getInstance().getCountryCodeForRegion(s)));
            countryCodeList.add(countryCode);
        }
        countryCodeList.sort(Comparator.comparing(CountryCode::getCountryName));
        return countryCodeList;
    }

    public static boolean checkValidity(String countryCode, String phoneNumber) {
        PhoneNumber number = new PhoneNumber();
        number.setCountryCode(Integer.parseInt(countryCode));
        number.setNationalNumber(Long.parseLong(phoneNumber));
        return PhoneNumberUtil.getInstance().isPossibleNumberForType(number, PhoneNumberUtil.PhoneNumberType.MOBILE);
    }
}

