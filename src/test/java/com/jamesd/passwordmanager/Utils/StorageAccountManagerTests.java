package com.jamesd.passwordmanager.Utils;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import com.jamesd.passwordmanager.DAO.StorageAccountManager;
import com.jamesd.passwordmanager.Models.Users.User;
import com.jamesd.passwordmanager.PasswordManagerApp;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class StorageAccountManagerTests {

    @BeforeAll
    public static void connectToStorageAccount() throws FileNotFoundException {
        PasswordManagerApp.setLoggedInUser(new User());
        PasswordManagerApp.getLoggedInUser().setUsername("test-user");
        PropertiesUtil.initialise();
        StorageAccountManager.connect("0hMQzrtrhGkLfbJB+nSMi0sfwXhij/OJJBfZn7GQDaOHpFVsrWcfK+SkUUE53qcZEDlUhxlGyXt7Oi269LiSAw==");
    }

    @Test
    public void testDownload() throws IOException {
        StorageAccountManager.downloadBlob("testfolder/HelloWorld.txt");
    }

    @Test
    @Order(1)
    public void uploadTest() throws IOException {
        StorageAccountManager.uploadBlob(new File(System.getProperty("user.dir") + "/src/main/resources/com/jamesd/passwordmanager/scripts/get_download_location.sh"), "testfolder", "rename_test.sh");
    }

    @Test
    @Order(2)
    public void listBlobs() {
        List<String> blobs = StorageAccountManager.getBlobsInContainer();
        for(String blobName : blobs) {
            System.out.println(blobName);
        }
    }
}
