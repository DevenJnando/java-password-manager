package com.jamesd.passwordmanager.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {
    private final static String propertiesLocation = System.getProperty("user.dir")+"/src/main/resources/com/jamesd/passwordmanager/properties/database.properties";
    private static Properties props;

    protected static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

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

    public static Properties getProperties() {
        return props;
    }
}
