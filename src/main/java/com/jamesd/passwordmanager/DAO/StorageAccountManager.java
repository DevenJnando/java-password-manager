package com.jamesd.passwordmanager.DAO;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobStorageException;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.PropertiesUtil;
import org.controlsfx.tools.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.azure.storage.common.StorageSharedKeyCredential;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Class containing static methods which connect to the storage account, creates a container for the logged in user,
 * and performs blob upload, modification, download and deletion operations.
 */
public class StorageAccountManager {

    private final static Logger logger = LoggerFactory.getLogger(StorageAccountManager.class);
    private final static String accountName = PropertiesUtil.getDatabaseProperties().getProperty("storageAccountName");
    private final static String SCRIPTS_DIR = System.getProperty("user.dir") + "/src/main/resources/com/jamesd/passwordmanager/scripts/";
    private static BlobContainerClient containerClient;

    /**
     * Retrieves the password manager storage account key, and uses it to create the BlobContainerClient object.
     * This is then assigned to the static containerClient field.
     */
    public static void connect(String accountKey) {
        StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accountKey);
        String endpoint = String.format(Locale.ROOT, "https://%s.blob.core.windows.net", accountName);
        BlobServiceClient storageClient = new BlobServiceClientBuilder().endpoint(endpoint).credential(credential).buildClient();
        try {
            containerClient = storageClient.createBlobContainer(String.format("%s-container", PasswordManagerApp.getLoggedInUser().getUsername()));
            logger.info("New container client created for user " + PasswordManagerApp.getLoggedInUser().getUsername());
        } catch(BlobStorageException e) {
            if(e.getErrorCode().equals(BlobErrorCode.CONTAINER_ALREADY_EXISTS)) {
                logger.info("Existing container client retrieved for user " + PasswordManagerApp.getLoggedInUser().getUsername());
                containerClient = storageClient.getBlobContainerClient(String.format("%s-container", PasswordManagerApp.getLoggedInUser().getUsername()));
            } else {
                e.printStackTrace();
                logger.error("Could not create/retrieve container client.");
            }
        }
    }

    /**
     * Method for uploading a blob to the user's storage container
     */
    public static void uploadBlob(File file, String directory, String fileName) {
        BlobClient blobClient = containerClient.getBlobClient(directory+"/"+fileName);
        try {
            blobClient.uploadFromFile(file.getAbsolutePath(), true);
            logger.info("Upload succeeded.");
        } catch(Exception e) {
            System.err.println(e.getMessage());
            logger.error("Upload failed...");
        }
    }

    /**
     * Deletes a blob from the user's container
     * @param blobName Name of blob to be deleted
     */
    public static void deleteBlob(String blobName) {
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        blobClient.delete();
        logger.info("Blob deleted.");
    }

    /**
     * Method which downloads a blob from the user's storage container to the user's download directory.
     * The full name of the blob is given as an argument.
     * @param blobName Name of blob to download
     * @throws IOException Throws an IOException if the default download directory cannot be located.
     */
    public static void downloadBlob(String blobName) throws IOException {
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        String downloadedBlobName = blobName.split("/")[1];
        String filePath = getDownloadLocation() + "/" + downloadedBlobName;
        executeDownload(blobClient, filePath, 0);
        openDownloadLocation(getDownloadLocation());
    }

    /**
     * Method which executes the downloading on a blob once the download directory and the blob in the user's storage
     * container have been found.
     * @param blobClient BlobClient object containing the blob to be downloaded
     * @param filePath The filepath which the blob should be downloaded into
     * @param attempt The download attempt - increases if a file already exists
     */
    private static void executeDownload(BlobClient blobClient, String filePath, int attempt) {
        try {
            if(attempt > 0) {
                String[] splitFilePath = filePath.split("\\.");
                String incrementedFilePath = splitFilePath[0] + "("+attempt+")." + splitFilePath[1];
                blobClient.downloadToFile(incrementedFilePath);
            } else {
                blobClient.downloadToFile(filePath);
            }
            logger.info("File downloaded successfully.");
        } catch(UncheckedIOException e) {
            if(e.getCause().getClass().equals(FileAlreadyExistsException.class)) {
                attempt++;
                executeDownload(blobClient, filePath, attempt);
            } else {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * Private method which retrieves the default downloads directory, depending on the OS the user is running the application on.
     * @return Full download directory String
     * @throws IOException Throws IOException if a shell command cannot be executed by the Process object
     */
    public static String getDownloadLocation() throws IOException {
        if(Platform.getCurrent() == Platform.WINDOWS) {
            return System.getProperty("user.home")+"/Downloads";
        } else if(Platform.getCurrent() == Platform.UNIX) {
            String[] command = {SCRIPTS_DIR + "get_download_location.sh"};
            Runtime.getRuntime().exec("chmod +x " + SCRIPTS_DIR + "get_download_location.sh");
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s;
            if ((s = reader.readLine()) != null) {
                return s;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static void openDownloadLocation(String downloadLocation) throws IOException {
        if(Platform.getCurrent() == Platform.WINDOWS) {
            Desktop.getDesktop().open(new File(downloadLocation));
        } else if(Platform.getCurrent() == Platform.UNIX
        || Platform.getCurrent() == Platform.OSX) {
            Runtime.getRuntime().exec("chmod +x " + SCRIPTS_DIR + "open_download_location.sh");
            new ProcessBuilder(SCRIPTS_DIR + "open_download_location.sh", downloadLocation).start();
        }
    }

    /**
     * Returns a list of String objects which contain the file name of each blob within the user's container
     * @return List of file names
     */
    public static List<String> getBlobsInContainer() {
        List<String> listOfBlobs = new ArrayList<>();
        for(BlobItem blob : containerClient.listBlobs()) {
            listOfBlobs.add(blob.getName());
        }
        return listOfBlobs;
    }
}
