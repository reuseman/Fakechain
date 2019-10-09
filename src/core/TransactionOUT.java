package core;

import utils.CryptoUtil;

import java.security.PublicKey;

public class TransactionOUT {
    public String id;
    public PublicKey reciepient;
    public float value; // amount of fakecoins
    public String parentTransactionId; // The if of the transaction where this output is created

    public TransactionOUT(PublicKey reciepient, float value, String parentTransactionId) {
        this.reciepient = reciepient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = CryptoUtil.getHash256(CryptoUtil.getStringFromKey(reciepient) + Float.toString(value) + parentTransactionId);
    }

    public boolean isMine(PublicKey publicKey) {
        return (publicKey == reciepient);
    }
}
