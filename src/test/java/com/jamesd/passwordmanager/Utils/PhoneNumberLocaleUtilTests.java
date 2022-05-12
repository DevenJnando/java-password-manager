package com.jamesd.passwordmanager.Utils;

import com.jamesd.passwordmanager.Models.HierarchyModels.CountryCode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for PhoneNumberLocaleUtil
 */
public class PhoneNumberLocaleUtilTests {

    @Test
    public void getPhoneNumberListTest() {
        List<CountryCode> countriesAndCodes= PhoneNumberLocaleUtil.getPhoneNumberList();
        countriesAndCodes.forEach(o -> {
            System.out.println("key: " + o.getCountryName() + " value: " + o.getCountryCode() + "\n");
        });
    }

    @Test
    public void checkValidityTest() {
        String goodNumber = "1234567890";
        String badNumber = "12345";
        String countryCode = "44";
        assertFalse(PhoneNumberLocaleUtil.checkValidity(countryCode, badNumber));
        assertTrue(PhoneNumberLocaleUtil.checkValidity(countryCode, goodNumber));
    }
}
