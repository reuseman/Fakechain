package core;

import utils.Commons;
import utils.CryptoUtil;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {
    private static int sequence = 0; //A rough count of how many transactions have been generated
    public String transactionId; //Contains a hash of transaction*
    public PublicKey sender; //Senders address/public key.
    public PublicKey reciepient; //Recipients address/public key.
    public float value; //Contains the amount we wish to send to the recipient.
    public byte[] signature; //This is to prevent anybody else from spending funds in our wallet.
    public ArrayList<TransactionIN> inputs = new ArrayList<TransactionIN>();
    public ArrayList<TransactionOUT> outputs = new ArrayList<TransactionOUT>();

    // Constructor:
    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionIN> inputs) {
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        this.inputs = inputs;
    }

    public boolean processTransaction(Blockchain blockchain) {
        if (!verifySignature()) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        //Gathers transaction inputs (Making sure they are unspent):
        for (TransactionIN i : inputs) {
            i.UTXO = blockchain.UTXOs.get(i.transactionOutputId);
        }

        //Checks if transaction is valid:
        if (this.getInputsValue() < Commons.MIMIMUM_FAKECOINS_PER_TRANSACTION) {
            System.out.println("Transaction Inputs too small: " + this.getInputsValue());
            System.out.println("Please enter the amount greater than " + Commons.MIMIMUM_FAKECOINS_PER_TRANSACTION);
            return false;
        }

        //Generate transaction outputs:
        float leftOver = getInputsValue() - this.value; //get value of inputs then the left over change:
        this.transactionId = calulateHash();
        this.outputs.add(new TransactionOUT(this.reciepient, this.value, this.transactionId)); //send value to recipient
        this.outputs.add(new TransactionOUT(this.sender, leftOver, this.transactionId)); //send the left over 'change' back to sender

        //Add outputs to Unspent list
        for (TransactionOUT o : this.outputs) {
            blockchain.UTXOs.put(o.id, o);
        }

        //Remove transaction inputs from UTXO lists as spent:
        for (TransactionIN i : this.inputs) {
            if (i.UTXO == null) continue; //if Transaction can't be found skip it
            blockchain.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    public float getInputsValue() {
        float total = 0;
        for (TransactionIN i : this.inputs) {
            if (i.UTXO == null) continue; //if Transaction can't be found skip it, This behavior may not be optimal.
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
