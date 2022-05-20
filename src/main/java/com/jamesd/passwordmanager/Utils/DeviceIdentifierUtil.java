package com.jamesd.passwordmanager.Utils;

import com.jamesd.passwordmanager.Models.Users.RecognisedUserDevice;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Utility class which obtains the mac address of the current device.
 */
public abstract class DeviceIdentifierUtil {

    /**
     * Constructor throws error - should not instantiate this class.
     */
    public DeviceIdentifierUtil() {
        throw new UnsupportedOperationException("Cannot instantiate abstract utility class.");
    }

    /**
     * Retrieves the user's MAC address by using the NetworkInterface class
     * @return MAC address of the current device being used.
     * @throws SocketException Throws SocketException if the hardware memory address of the NIC cannot be read.
     */
    public static String getMacAddress() throws SocketException {
        List<NetworkInterface> interfaceList = Collections.list(NetworkInterface.getNetworkInterfaces());
        String macAddress = "";
        for(NetworkInterface networkInterface : interfaceList) {
            if(!networkInterface.isLoopback()) {
                byte[] address = networkInterface.getHardwareAddress();
                if (isValidAddress(address)) {
                    for (int i = 0; i < address.length; i++) {
                        String stringMacByte = Integer.toHexString(address[i] & 0xFF);
                        if (stringMacByte.length() == 1) {
                            stringMacByte = "0" + stringMacByte;
                        }
                        macAddress = i != address.length - 1
                                ? macAddress + stringMacByte.toUpperCase() + ":"
                                : macAddress + stringMacByte.toUpperCase();
                    }
                    break;
                }
            }
        }
        return macAddress;
    }

    /**
     * Private method which checks if a given address is a valid MAC address or not. If the length is incorrect, or the
     * address is null, it immediately returns false. If there are any 0 value bytes to the address, it can be inferred
     * that the address is not a valid MAC address.
     * @param address Byte array of the given hardware address
     * @return True if the hardware address is a valid MAC address, else false
     */
    private static boolean isValidAddress(byte[] address) {
        if (address == null || address.length != 6) {
            return false;
        }
        for (byte b : address) {
            if (b != 0x00) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method which adds a recognised device to the list of RecognisedUserDevice objects. A new RecognisedUserDevice
     * object is created using the newly recognised MAC address along with the current date and the total number of days
     * in the database.
     * @return
     * @throws SocketException
     */
    public static List<HashMap<String, String>> addDeviceToRecognisedList() throws SocketException {
        List<HashMap<String, String>> recognisedDevices = new ArrayList<>();
        HashMap<String, String> recognisedDevice = new HashMap<>();
        RecognisedUserDevice device = new RecognisedUserDevice(getMacAddress(), LocalDate.now().toString());
        recognisedDevice.put("macAddress", device.getMacAddress());
        recognisedDevice.put("dateAdded", device.getDateAdded());
        recognisedDevice.put("daysInDatabase", "0");
        recognisedDevices.add(recognisedDevice);
        return recognisedDevices;
    }
}
