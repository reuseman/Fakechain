package core;

import start.Fakechain;
import utils.CryptoUtil;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {
    public String transactionId; //Contains a hash of transaction*
    public PublicKey sender; //Senders address/public key.
    public PublicKey reciepient; //Recipients address/public key.
    public float value; //Contains the amount we wish to send to the recipient.
    public byte[] signature; //This is to prevent anybody else from spending funds in our wallet.

    public ArrayList<TransactionIN> inputs = new ArrayList<TransactionIN>();
    public ArrayList<TransactionOUT> outputs = new ArrayList<TransactionOUT>();

    private static int sequence = 0; //A rough count of how many transactions have been generated

    // Constructor:
    public Transaction(PublicKey from, PublicKey to, float value,  ArrayList<TransactionIN> inputs) {
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        this.inputs = inputs;
    }

    public boolean processTransaction() {

        if(verifySignature() == false) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        //Gathers transaction inputs (Making sure they are unspent):
        for(TransactionIN i : inputs) {
            i.UTXO = Fakechain.UTXOs.get(i.transactionOutputId);
        }

        //Checks if transaction is valid:
        if(getInputsValue() < Fakechain.minimumTransaction) {
            System.out.println("Transaction Inputs too small: " + getInputsValue());
            System.out.println("Please enter the amount greater than " + Fakechain.minimumTransaction);
            return false;
        }

        //Generate transaction outputs:
        float leftOver = getInputsValue() - value; //get value of inputs then the left over change:
        transactionId = calulateHash();
        outputs.add(new TransactionOUT( this.reciepient, value,transactionId)); //send value to recipient
        outputs.add(new TransactionOUT( this.sender, leftOver,transactionId)); //send the left over 'change' back to sender

        //Add outputs to Unspent list
        for(TransactionOUT o : outputs) {
            Fakechain.UTXOs.put(o.id , o);
        }

        //Remove transaction inputs from UTXO lists as spent:
        for(TransactionIN i : inputs) {
            if(i.UTXO == null) continue; //if Transaction can't be found skip it
            Fakechain.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    public float getInputsValue() {
        float total = 0;
        for(TransactionIN i : inputs) {
            if(i.UTXO == null) continue; //if Transaction can't be found skip it, This behavior may not be optimal.
            total += i.UTXO.value;
        }
        return total;
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = CryptoUtil.getStringFromKey(sender) + CryptoUtil.getStringFromKey(reciepient) + Float.toString(value)	;
        signature = CryptoUtil.applyECDSASig(privateKey,data);
    }

    public boolean verifySignature() {
        String data = CryptoUtil.getStringFromKey(sender) + CryptoUtil.getStringFromKey(reciepient) + Float.toString(value)	;
        return CryptoUtil.verifyECDSASig(sender, data, signature);
    }

    public float getOutputsValue() {
        float total = 0;
        for(TransactionOUT o : outputs) {
            total += o.value;
        }
        return total;
    }

    private String calulateHash() {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
        return CryptoUtil.applySha256(
                CryptoUtil.getStringFromKey(sender) +
                        CryptoUtil.getStringFromKey(reciepient) +
                        Float.toString(value) + sequence
        );
    }
}
