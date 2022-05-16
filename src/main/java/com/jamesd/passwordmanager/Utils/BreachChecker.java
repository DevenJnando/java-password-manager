package com.jamesd.passwordmanager.Utils;

import com.jamesd.passwordmanager.DAO.MasterSQLQueries;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class which sends a http request to the breachdirectory API. The response is then sanitised and returned to
 * the user as a hashmap.
 */
public abstract class BreachChecker {

    public static Logger logger = LoggerFactory.getLogger(BreachChecker.class);

    /**
     * Default constructor (should never be called)
     */
    public BreachChecker() {
        throw new UnsupportedOperationException("Cannot instantiate abstract utility class.");
    }

    /**
     * Method which takes a HashMap with a website as the key and the email/username as a value. This hashmap is then
     * used to call the breachdirectory API. If breaches are found for a username, but not for the associated website then
     * it can be inferred that the breach is for a different person who happens to have an identical username. The result
     * is then discarded. Any results which match both username and website are added to a HashMap of results which is
     * returned to the user. All email results are added regardless if the source matches or not.
     * @param sitesAndUsernames HashMap websites and usernames
     * @return HashMap of all breaches found with the username as the key and the source of the breach as the result.
     */
    public static HashMap<String, String> checkForBreaches(HashMap<String, String> sitesAndUsernames){
        for(Map.Entry<String, String> entry : sitesAndUsernames.entrySet()) {
            logger.info("Entry website: " + entry.getKey() + "\n" + "Entry username/email: " + entry.getValue());
        }
        HashMap<String, String> breaches = new HashMap<>();
        sitesAndUsernames.forEach((k,v)-> {
            HttpResponse response;
            try {
                int attempts = 0;
                response = sendBreachRequest(v);
                while(response.getStatusLine().getStatusCode() != 200 && attempts < 5) {
                    logger.error("Bad request for site: " + k + " username: " + v + "\n" +
                            ", error code " + response.getStatusLine().getStatusCode() + ", "
                            + response.getStatusLine().getReasonPhrase());
                    logger.error("Attempt number " + attempts + " (will give up after 5)");
                    attempts++;
                    response = sendBreachRequest(v);
                }
                if(attempts != 5) {
                    String result = MasterSQLQueries.readResponse(response).trim();
                    logger.info(result);
                    JSONObject resultAsJson = new JSONObject(result);
                    if (resultAsJson.get("success").equals(true)) {
                        JSONArray resultsArray = resultAsJson.getJSONArray("result");
                        for (int i = 0; i < resultsArray.length(); i++) {
                            JSONObject resultObject = resultsArray.getJSONObject(i);
                            JSONArray sourcesArray = resultObject.getJSONArray("sources");
                            String source = sourcesArray.getString(0);
                            if(!isValidEmail(v)) {
                                if (source.contains(k)) {
                                    breaches.put(v, source);
                                }
                            } else {
                                breaches.put(v, source);
                            }
                        }
                    }
                } else {
                    breaches.put("Error code " + response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
                }
            } catch (IOException e) {
                logger.error("Bad request to breachdirectory API.");
                e.printStackTrace();
            }
        });
        return breaches;
    }

    /**
     * Sends a request to the breachdirectory API and takes a String as an argument. This string can either be an email
     * or a username. The response is then returned to the user.
     * @param toBeChecked String defining which username/email should be checked for breaches
     * @return Returns the API Response object to the user
     * @throws IOException Throws IOException if a bad request occurs.
     */
    public static HttpResponse sendBreachRequest(String toBeChecked) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet("https://breachdirectory.p.rapidapi.com/?func=auto&term=" + toBeChecked);
        getRequest.setHeader("X-RapidAPI-Host", PropertiesUtil.getBreachDirectoryProps().getProperty("api-host"));
        getRequest.setHeader("X-RapidAPI-Key", PropertiesUtil.getBreachDirectoryProps().getProperty("api-key"));
        return client.execute(getRequest);
    }

    /**
     * Checks if an entry is an email address. Returns true if it is
     * @param email Potential email string
     * @return True if the string is an email address, else false
     */
    public static boolean isValidEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }
}
