package com.jamesd.passwordmanager.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class which initialises the database properties file
 */
public abstract class PropertiesUtil {

    private final static String propertiesLocation = System.getProperty("user.dir")
            +"/src/main/resources/com/jamesd/passwordmanager/properties/database.properties";
    private final static String twilioPropertiesLocation = System.getProperty("user.dir")
            +"/src/main/resources/com/jamesd/passwordmanager/properties/twilio.properties";
    private static Properties props;
    private static Properties twilioProps;

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
        FileInputStream propsInputStream = new FileInputStream(propertiesLocation);
        FileInputStream twilioInputStream = new FileInputStream(twilioPropertiesLocation);
        props = new Properties();
        twilioProps = new Properties();
        try {
            props.load(propsInputStream);
            twilioProps.load(twilioInputStream);
            logger.info("Successfully initialised properties from path: " + propertiesLocation);
            logger.info("Successfully initialised properties from path: " + twilioPropertiesLocation);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the database properties object
     * @return Properties object
     */
    public static Properties getProperties() {
        return props;
    }

    /**
     * Retrieves the twilio properties object
     * @return Properties object
     */
    public static Properties getTwilioProperties() { return twilioProps; }
}
