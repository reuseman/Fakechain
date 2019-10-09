package utils;

import core.Transaction;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class CryptoUtil {
    // https://stackoverflow.com/questions/5531455/how-to-hash-some-string-with-sha256-in-java#5531479

    /**
     * This method take a string as input and convert it with the SHA-256 algorithm
     * @param base the string to convert
     * @return a string of 64 characters converted through SHA-256
     */
    public static String getHash256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * This function returns a string that is formed by a number of zeros equals to the difficulty parameter
     * @param difficulty the number of zeros that the string will contain
     * @return the string formed by zeros
     */
    public static String getTarget(int difficulty) {
        StringBuilder target = new StringBuilder();
        for (int i = 0; i < difficulty; i++) {
            target.append('0');
        }
        return target.toString();
    }

    /**
     * Applies the ECDSA signiture
     * @param privateKey
     * @param input
     * @return
     */
    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output = new byte[0];
        Security.addProvider(new BouncyCastleProvider());

        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }

    /**
     *
     * @param publicKey
     * @param data
     * @param signature
     * @return
     */
    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        Security.addProvider(new BouncyCastleProvider());

        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a Key (public or private) in hexadecimal
     * @param key to convert
     * @return a string that represents the key in hexadecimal
     */
    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String getMerkleRoot(ArrayList<Transaction> transactions) {
        int count = transactions.size();

        List<String> previousTreeLayer = new ArrayList<>();
        for (Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.transactionId);
        }
        List<String> treeLayer = previousTreeLayer;

        while (count > 1) {
            treeLayer = new ArrayList<>();
            for (int i = 1; i < previousTreeLayer.size(); i += 2) {
                treeLayer.add(CryptoUtil.getHash256(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }

        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
        return merkleRoot;
    }
}
