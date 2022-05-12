package com.jamesd.passwordmanager.Utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;

/**
 * Utility class which builds a favicon for a subclass of the BaseWrapper class
 */
public abstract class FaviconBuilder {

    private static final Logger logger = LoggerFactory.getLogger(FaviconBuilder.class);

    public FaviconBuilder() {
        throw new UnsupportedOperationException("Cannot instantiate an abstract utility class.");
    }

    /**
     * Leverages clearbit to check if a URL has a valid logo behind it or not
     * @param protocol Protocol to use
     * @param url WebsitePasswordEntry object's URL
     * @return Boolean true if logo exists, else false
     */
    public static Boolean getUrlFavicon(String protocol, String url) {
        try {
            URL faviconUrl = new URL(protocol, "logo.clearbit.com", "/" + url);
            new BufferedInputStream(faviconUrl.openStream());
            return true;
        } catch (IOException e) {
            logger.error("No logo found for provided url: " + url);
            return false;
        }
    }

    /**
     * Checks if a logo can be found for a WebsitePasswordEntry object's URL, and if it can it is downloaded and
     * assigned to the relevant WebsitePasswordEntryWrapper object's favicon field
     * @param faviconUrl WebsitePasswordEntry object's URL
     * @param filename Filename String to save the logo as
     * @return ImageView containing the logo which corresponds to the faviconUrl parameter
     */
    public static ImageView createFavicon(URL faviconUrl, String filename) {
        try {
            BufferedInputStream inputStream = new BufferedInputStream(faviconUrl.openStream());
            File outputFile = new File("src/main/resources/com/jamesd/passwordmanager/icons/favicons/" + filename + ".png");
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            byte[] data = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                outputStream.write(data, 0, byteContent);
            }
            outputStream.flush();
            outputStream.close();
            Image image = new Image(new FileInputStream("src/main/resources/com/jamesd/passwordmanager/icons/favicons/" + filename + ".png"));
            ImageView favicon = new ImageView(image);
            favicon.setFitHeight(32);
            favicon.setFitWidth(32);
            return favicon;
        } catch(IOException e) {
            ImageView favicon = new ImageView();
            e.printStackTrace();
            return favicon;
        }
    }
}
