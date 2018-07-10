package core;

public class TransactionIN {
    public String transactionOutputId; // TransactionOutputs.transactionId
    public TransactionOUT UTXO; // Contains the Unspent transaction output

    public TransactionIN(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }

}
