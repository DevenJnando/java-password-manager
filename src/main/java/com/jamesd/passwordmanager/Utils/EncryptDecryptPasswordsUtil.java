package com.jamesd.passwordmanager.Utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Utility class which encrypts and decrypts PasswordEntry subclass objects
 */
public abstract class EncryptDecryptPasswordsUtil {

    private static SecretKeySpec key;
    private static final byte[] SALT = "gIo9&pb3".getBytes();
    private static final int ITERATION_COUNT = 4000;
    private static final int KEY_LENGTH = 128;

    /**
     * Constructor throws error if called - class is abstract
     */
    public EncryptDecryptPasswordsUtil() {
        throw new UnsupportedOperationException("Cannot instantiate an abstract utility class.");
    }

    /**
     * Creates a SecretKeySpec which will be used to encrypt and decrypt passwords
     * @param encryptionKey The encryption key String to use when building the SecretKeySpec
     * @throws InvalidKeySpecException Throws InvalidKeySpecException if the specifications used to create the SecretKey
     * are incorrect
     * @throws NoSuchAlgorithmException Throws NoSuchAlgorithmException if the specified algorithm does not exist
     */
    public static void initialise(String encryptionKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        key = createSecretKey(encryptionKey.toCharArray(), SALT, ITERATION_COUNT, KEY_LENGTH);
    }

    /**
     * Creates a SecretKeySpec using an encryption key, salt, iteration count and bit-length of the key
     * @param key Encryption key String
     * @param salt Byte array of the salt
     * @param iterationCount Number of iterations for the Key specification to complete as an Integer
     * @param keyLength Bit-length of the encryption key as an Integer
     * @return New generated SecretKeySpec object
     * @throws InvalidKeySpecException Throws InvalidKeySpecException if the specifications used to create the SecretKey
     * are incorrect
     * @throws NoSuchAlgorithmException Throws NoSuchAlgorithmException if the specified algorithm does not exist
     */
    private static SecretKeySpec createSecretKey(char[] key, byte[] salt, int iterationCount, int keyLength)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec keySpec = new PBEKeySpec(key, salt, iterationCount, keyLength);
        SecretKey keyTmp = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(keyTmp.getEncoded(), "AES");
    }

    /**
     * Encrypts a String object using the generated SecretKeySpec object
     * @param property String to be encrypted
     * @param key SecretKeySpec which will be used to encrypt the String object
     * @return Encrypted String object
     * @throws GeneralSecurityException Throws InvalidKeyException if the encryption key is incompatible with the
     * encryption method, throws InvalidParameterSpecException if the specified parameter specification is incorrect,
     * throws IllegalBlockSizeException if the block size does not match the size of the cipher and throws a
     * BadPaddingException if the cipher input data has not been padded correctly
     * @throws UnsupportedEncodingException Throws UnsupportedEncodingException if the cipher cannot be encoded to the
     * specified character set
     */
    private static String encrypt(String property, SecretKeySpec key) throws GeneralSecurityException, UnsupportedEncodingException {
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key);
        AlgorithmParameters parameters = pbeCipher.getParameters();
        IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
        byte[] cryptoText = pbeCipher.doFinal(property.getBytes("UTF-8"));
        byte[] iv = ivParameterSpec.getIV();
        return base64Encode(iv) + ":" + base64Encode(cryptoText);
    }

    /**
     * Encodes a byte array to a String
     * @param bytes Byte array to encode to String
     * @return Encoded String
     */
    private static String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Decrypts a String object using a cipher which leverages the generated SecretKeySpec
     * @param string String object to be decrypted
     * @return Decrypted String object
     * @throws GeneralSecurityException Throws NoSuchAlgorithmException if the specified algorithm does not exist,
     * throws NoSuchPaddingException if the requested padding mechanism does not exist, throws InvalidKeyException if
     * the provided SecretKeySpec is not a valid key spec and throws InvalidAlgorithmParameterException if the specified
     * algorithm parameters are incorrect
     * @throws IOException Throws IOException if the specified character set cannot be read, or if the cipher cannot
     * decode the decrypted String to the specified character set
     */
    private static String decrypt(String string) throws GeneralSecurityException, IOException {
        String iv = string.split(":")[0];
        String property = string.split(":")[1];
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(base64Decode(iv)));
        return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    }

    /**
     * Decodes a String object into a byte array
     * @param property String to decode into byte array
     * @return Decoded byte array
     */
    private static byte[] base64Decode(String property) {
        return Base64.getDecoder().decode(property);
    }

    /**
     * Encrypts a plaintext password using a cipher which uses the generated SecretKeySpec object
     * @param plaintextPassword Plaintext password String to encrypt
     * @return Encrypted password String
     * @throws GeneralSecurityException Throws InvalidKeyException if the encryption key is incompatible with the
     * encryption method, throws InvalidParameterSpecException if the specified parameter specification is incorrect,
     * throws IllegalBlockSizeException if the block size does not match the size of the cipher and throws a
     * BadPaddingException if the cipher input data has not been padded correctly
     * @throws UnsupportedEncodingException Throws UnsupportedEncodingException if the cipher cannot be encoded to the
     * specified character set
     */
    public static String encryptPassword(String plaintextPassword)
            throws GeneralSecurityException, UnsupportedEncodingException {
        String encryptedPassword = encrypt(plaintextPassword, key);
        return encryptedPassword;
    }

    /**
     * Decrypts an encrypted password using a cipher which uses the generated SecretKeySpec object
     * @param encryptedPassword Encrypted password to decrypt
     * @return Decrypted plaintext password String
     * @throws GeneralSecurityException Throws NoSuchAlgorithmException if the specified algorithm does not exist,
     * throws NoSuchPaddingException if the requested padding mechanism does not exist, throws InvalidKeyException if
     * the provided SecretKeySpec is not a valid key spec and throws InvalidAlgorithmParameterException if the specified
     * algorithm parameters are incorrect
     * @throws IOException Throws IOException if the specified character set cannot be read, or if the cipher cannot
     * decode the decrypted String to the specified character set
     */
    public static String decryptPassword(String encryptedPassword)
            throws GeneralSecurityException, IOException {
        return decrypt(encryptedPassword);
    }
}
