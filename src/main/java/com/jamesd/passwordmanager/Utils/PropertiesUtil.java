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
public class PropertiesUtil {

    private final static String propertiesLocation = System.getProperty("user.dir")+"/src/main/resources/com/jamesd/passwordmanager/properties/database.properties";
    private static Properties props;

    protected static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    /**
     * Initialises the properties by loading the properties file from its location and adding the contents to the props
     * field
     * @throws FileNotFoundException Throws FileNotFoundException if the specified properties location cannot be found
     */
    public static void initialise() throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream(propertiesLocation);
        props = new Properties();
        try {
            props.load(inputStream);
            logger.info("Successfully initialised properties from path: " + propertiesLocation);
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
}
