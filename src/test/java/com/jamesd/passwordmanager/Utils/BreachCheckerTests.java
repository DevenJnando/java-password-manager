package com.jamesd.passwordmanager.Utils;

import org.apache.http.HttpResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BreachCheckerTests {

    @BeforeAll
    public static void initialise() throws FileNotFoundException {
        PropertiesUtil.initialise();
    }

    @Test
    public void testBreachCheckerRequest() throws IOException {
        HttpResponse response = BreachChecker.sendBreachRequest("conormaxwell@ymail.com");
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testCollectBreaches() {
        HashMap<String, String> sitesAndUsernames = new HashMap<>();
        sitesAndUsernames.put("myspace.com", "candjproductions");
        sitesAndUsernames.put("facebook.com", "conormaxwell@ymail.com");
        HashMap<String, String> results = BreachChecker.checkForBreaches(sitesAndUsernames);
        results.forEach((k,v) -> {
            System.out.println("Username: " + k);
            System.out.println("Site breached: " + v);
        });
    }

}
