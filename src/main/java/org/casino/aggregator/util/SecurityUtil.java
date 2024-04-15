package org.casino.aggregator.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class SecurityUtil {

    public static String signMessage(String message, String keyFile) {
        String result;
        try {PrivateKey privateKey = getPrivateKey(keyFile);
            if (privateKey == null) {
                System.out.println("Private key not found");
                return null;
            }
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(privateKey);
            signature.update(message.getBytes());
            byte[] sha1hash = signature.sign();
            result = Base64.getEncoder().encodeToString(sha1hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            System.out.println("Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return result;
    }

    private static PrivateKey getPrivateKey(String fileName) {
        PrivateKey privateKey = null;
        try {

            File file = new File(fileName);
            BufferedInputStream bis;
            try {
                bis = new BufferedInputStream(new FileInputStream(file));
            } catch(FileNotFoundException e) {
                throw new Exception("Could not locate keyfile at '" + fileName + "'", e);
            }
            byte[] encoded = new byte[(int)file.length()];
            bis.read(encoded);
            bis.close();

            KeyFactory kf = KeyFactory.getInstance("RSA");
            EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            privateKey = kf.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Could not reconstruct the private key, the given algorithm could not be found.");
        } catch (InvalidKeySpecException e) {
            System.out.println("Could not reconstruct the private key");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return privateKey;
    }
}
