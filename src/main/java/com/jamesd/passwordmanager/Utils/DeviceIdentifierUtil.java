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
            if(networkInterface.getName().equalsIgnoreCase("wlon0"));
            {
                for(int i = 0 ;i <networkInterface.getHardwareAddress().length; i++){
                    String stringMacByte = Integer.toHexString(networkInterface.getHardwareAddress()[i]& 0xFF);
                    if(stringMacByte.length() == 1)
                    {
                        stringMacByte = "0" +stringMacByte;
                    }
                    macAddress = i!=networkInterface.getHardwareAddress().length - 1
                            ? macAddress + stringMacByte.toUpperCase() + ":"
                            : macAddress + stringMacByte.toUpperCase();
                }
                break;
            }
        }
        return macAddress;
    }

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
