package core;

public class TransactionIN {
    public String transactionOutputId; //Reference to TransactionOutputs -> transactionId
    public TransactionOUT UTXO; //Contains the Unspent transaction output

    public TransactionIN(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }

}
