package com.stp.ssm.Util;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import static java.lang.Integer.toHexString;
import static java.security.MessageDigest.getInstance;
import static javax.crypto.Cipher.ENCRYPT_MODE;


public class Encriptacion {
    public static String MD5 = "MD5";


    public static String getStringMessageDigest(String message) {
        byte[] digest = null;
        byte[] buffer = message.getBytes();
        try {
            MessageDigest messageDigest = getInstance(MD5);
            messageDigest.reset();
            messageDigest.update(buffer);
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException ex) {
        }
        return toHexadecimal(digest);
    }


    private static String toHexadecimal(byte[] digest) {
        String hash = "";
        for (byte aux : digest) {
            int b = aux & 0xff;
            if (toHexString(b).length() == 1) hash += "0";
            hash += toHexString(b);
        }
        return hash;
    }


    public static String cifrarAes(String cadena) {
        String llaveSimetrica = "PONETUCLAVE";
        SecretKeySpec key = new SecretKeySpec(llaveSimetrica.getBytes(), "AES");
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(ENCRYPT_MODE, key);
            byte[] campoCifrado = cipher.doFinal(cadena.getBytes());
            return new String(campoCifrado);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }
}
