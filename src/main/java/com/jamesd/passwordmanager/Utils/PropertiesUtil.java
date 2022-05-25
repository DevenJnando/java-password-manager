package com.jamesd.passwordmanager.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class which initialises the database properties file
 */
public abstract class PropertiesUtil {

    private final static String databasePropertiesLocation = System.getProperty("user.dir")
            +"/src/main/resources/com/jamesd/passwordmanager/properties/database.properties";
    private final static String themePropertiesLocation = System.getProperty("user.dir")
            +"/src/main/resources/com/jamesd/passwordmanager/properties/theme.properties";
    private final static String twilioPropertiesLocation = System.getProperty("user.dir")
            +"/src/main/resources/com/jamesd/passwordmanager/properties/twilio.properties";
    private final static String breachDirectoryPropertiesLocation = System.getProperty("user.dir")
            +"/src/main/resources/com/jamesd/passwordmanager/properties/breachdirectory.properties";
    private static Properties databaseProps;
    private static Properties themeProps;
    private static Properties twilioProps;
    private static Properties breachDirectoryProps;

    protected static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    /**
     * Throws UnsupportedOperationException if this class is attempted to be instantiated
     */
    public PropertiesUtil() {
        throw new UnsupportedOperationException("Cannot instantiate abstract utility class.");
    }

    /**
     * Initialises the properties by loading the properties file from its location and adding the contents to the props
     * field
     * @throws FileNotFoundException Throws FileNotFoundException if the specified properties' location cannot be found
     */
    public static void initialise() throws FileNotFoundException {
        FileInputStream databasePropsInputStream = new FileInputStream(databasePropertiesLocation);
        FileInputStream themeInputStream = new FileInputStream(themePropertiesLocation);
        FileInputStream twilioInputStream = new FileInputStream(twilioPropertiesLocation);
        FileInputStream breachDirectoryInputStream = new FileInputStream(breachDirectoryPropertiesLocation);
        databaseProps = new Properties();
        themeProps = new Properties();
        twilioProps = new Properties();
        breachDirectoryProps = new Properties();
        try {
            databaseProps.load(databasePropsInputStream);
            themeProps.load(themeInputStream);
            twilioProps.load(twilioInputStream);
            breachDirectoryProps.load(breachDirectoryInputStream);
            logger.info("Successfully initialised properties from path: " + databasePropertiesLocation);
            logger.info("Successfully initialised properties from path: " + themePropertiesLocation);
            logger.info("Successfully initialised properties from path: " + twilioPropertiesLocation);
            logger.info("Successfully initialised properties from path: " + breachDirectoryPropertiesLocation);
        } catch(IOException e) {
            e.printStackTrace();
            logger.error("Properties could not be initialised. Make sure you have all .properties files" +
                    " in your properties directory.");
        }
    }

    public static void updateThemeProperties() throws IOException {
        themeProps.store(new FileOutputStream(themePropertiesLocation), "theme properties");
    }

    /**
     * Retrieves the database properties object
     * @return Properties object
     */
    public static Properties getDatabaseProperties() {
        return databaseProps;
    }

    /**
     * Retrieves the theme properties object
     * @return Properties object
     */
    public static Properties getThemeProperties() { return themeProps; }

    /**
     * Retrieves the twilio properties object
     * @return Properties object
     */
    public static Properties getTwilioProperties() { return twilioProps; }

    /**
     * Retrieves the breach directory properties object
     * @return Properties object
     */
    public static Properties getBreachDirectoryProps() {
        return breachDirectoryProps;
    }
}
