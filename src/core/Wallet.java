package core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.Commons;

import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
    public String username;
    public PrivateKey privateKey;
    public PublicKey publicKey;

    public HashMap<String, TransactionOUT> UTXOs = new HashMap<String, TransactionOUT>();

    public Wallet(String username) {
        this.username = username;
        generateKeyPair();
    }

    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random); //256
            KeyPair keyPair = keyGen.generateKeyPair();
            // Set the public and private keys from the keyPair
            this.privateKey = keyPair.getPrivate();
            this. publicKey = keyPair.getPublic();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public float getBalance(Blockchain blockchain) {
        float total = 0;
        for (Map.Entry<String, TransactionOUT> item : blockchain.UTXOs.entrySet()) {
            TransactionOUT UTXO = item.getValue();
            if (UTXO.isMine(publicKey)) { //if output belongs to me ( if coins belong to me )
                UTXOs.put(UTXO.id, UTXO); //add it to our list of unspent transactions.
                total += UTXO.value;
            }
        }
        return total;
    }

    public Transaction sendFunds(Blockchain blockchain, PublicKey _recipient, float value) {
        if (this.getBalance(blockchain) < value) {
            System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }
        ArrayList<TransactionIN> inputs = new ArrayList<TransactionIN>();

        float total = 0;
        for (Map.Entry<String, TransactionOUT> item : UTXOs.entrySet()) {
            TransactionOUT UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionIN(UTXO.id));
            if (total > value) break;
        }

        Transaction newTransaction = new Transaction(publicKey, _recipient, value, inputs);
        newTransaction.generateSignature(privateKey);

        for (TransactionIN input : inputs) {
            UTXOs.remove(input.transactionOutputId);
        }

        return newTransaction;
    }

    public boolean saveToFile() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(Commons.WALLET_FILE_PATH), this);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String convertToString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public HashMap<String, TransactionOUT> getUTXOs() {
        return UTXOs;
    }

    public void setUTXOs(HashMap<String, TransactionOUT> UTXOs) {
        this.UTXOs = UTXOs;
    }
}
