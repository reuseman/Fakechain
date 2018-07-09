package core;

import utils.Commons;
import utils.CryptoUtil;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {
    private static int sequence = 0;
    public String transactionId;
    public PublicKey sender;
    public PublicKey reciepient;
    public float value;
    public byte[] signature;

    public ArrayList<TransactionIN> inputs = new ArrayList<TransactionIN>();
    public ArrayList<TransactionOUT> outputs = new ArrayList<TransactionOUT>();

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionIN> inputs) {
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        this.inputs = inputs;
    }

    public boolean processTransaction(Blockchain blockchain) {
        if (!verifySignature()) {
            System.out.println("ERROR: Transaction signature is not valid");
            return false;
        }

        // Check if transaction inputs are unspent
        for (TransactionIN i : inputs) {
            i.UTXO = blockchain.UTXOs.get(i.transactionOutputId);
        }

        // Check if it is valid
        if (this.getInputsValue() < Commons.MIMIMUM_FAKECOINS_PER_TRANSACTION) {
            System.out.println("Transaction Inputs too small: " + this.getInputsValue());
            System.out.println("Please enter the amount greater than " + Commons.MIMIMUM_FAKECOINS_PER_TRANSACTION);
            return false;
        }

        // Generate the transaction outputs
        float leftOver = getInputsValue() - this.value; //get value of inputs then the left over change:
        this.transactionId = calulateHash();
        this.outputs.add(new TransactionOUT(this.reciepient, this.value, this.transactionId)); //send value to recipient
        this.outputs.add(new TransactionOUT(this.sender, leftOver, this.transactionId)); //send the left over 'change' back to sender

        // Adding outputs to the unspent list
        for (TransactionOUT o : this.outputs) {
            blockchain.UTXOs.put(o.id, o);
        }

        //Remove transaction inputs from UTXO lists as spent:
        for (TransactionIN i : this.inputs) {
            if (i.UTXO == null) continue;
            blockchain.UTXOs.remove(i.UTXO.id);
        }
        // TODO substitute the continue with Java's best practive before the Exam
        return true;
    }

    public float getInputsValue() {
        float total = 0;
        for (TransactionIN i : this.inputs) {
            if (i.UTXO == null) continue;
            total += i.UTXO.value;
        }
        return total;
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = CryptoUtil.getStringFromKey(sender) + CryptoUtil.getStringFromKey(reciepient) + Float.toString(value);
        signature = CryptoUtil.applyECDSASig(privateKey, data);
    }

    public boolean verifySignature() {
        String data = CryptoUtil.getStringFromKey(sender) + CryptoUtil.getStringFromKey(reciepient) + Float.toString(value);
        return CryptoUtil.verifyECDSASig(sender, data, signature);
    }

    public float getOutputsValue() {
        float total = 0;
        for (TransactionOUT o : this.outputs) {
            total += o.value;
        }
        return total;
    }

    private String calulateHash() {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
        return CryptoUtil.getHash256(
                CryptoUtil.getStringFromKey(this.sender) +
                        CryptoUtil.getStringFromKey(this.reciepient) +
                        Float.toString(this.value) +
                        sequence
        );
    }
}
