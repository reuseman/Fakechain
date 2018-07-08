package core;

import utils.Commons;
import utils.CryptoUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class Blockchain {
    public ArrayList<Block> blockchain;
    public HashMap<String, TransactionOUT> UTXOs;
    public Transaction genesisTransaction;

    public Blockchain(Wallet coinbaseWallet, Wallet receiverWallet) {
        this.blockchain = new ArrayList<>();
        this.UTXOs = new HashMap<>();

        // Hard code the first transaction to get a proper working Blockchain
        this.genesisTransaction = new Transaction(coinbaseWallet.publicKey, receiverWallet.publicKey, 100f, null);
        this.genesisTransaction.generateSignature(coinbaseWallet.privateKey);     //manually sign the genesis transaction
        this.genesisTransaction.transactionId = "0"; //manually set the transaction id
        this.genesisTransaction.outputs.add(new TransactionOUT(this.genesisTransaction.reciepient,
                this.genesisTransaction.value, this.genesisTransaction.transactionId)); //manually add the Transactions Output
        UTXOs.put(this.genesisTransaction.outputs.get(0).id, this.genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.

        System.out.println("Creating and Mining Genesis block... ");
        Block genesis = new Block("0");
        genesis.addTransaction(this.genesisTransaction);
        this.addBlock(genesis);
    }

    public Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = CryptoUtil.getTarget(Commons.MINING_BLOCK_DIFFICULTY);
        HashMap<String, TransactionOUT> tempUTXOs = new HashMap<>(); //a temporary working list of unspent transactions at a given block state.
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        // Loop check on the blocks of the chain
        for (int i = 1; i < this.blockchain.size(); i++) {
            currentBlock = this.blockchain.get(i);
            previousBlock = this.blockchain.get(i - 1);
            //compare registered hash and calculated hash:
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("ERROR: Current Hashes not equal!");
                return false;
            }
            //compare previous hash and registered previous hash
            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("ERROR: Previous Hashes not equal!");
                return false;
            }

            // Is puzzle properly solved?
            if (!currentBlock.hash.substring(0, Commons.MINING_BLOCK_DIFFICULTY).equals(hashTarget)) {
                System.out.println("ERROR: This block hasn't been mined!");
                return false;
            }

            // Loop check over the transactions of a single block
            TransactionOUT tmpOut;
            for (int t = 0; t < currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if (!currentTransaction.verifySignature()) {
                    System.out.println("ERROR: Signature on Transaction(" + t + ") is Invalid!");
                    return false;
                }
                if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("ERROR: Inputs are note equal to outputs on Transaction(" + t + ")!");
                    return false;
                }

                for (TransactionIN input : currentTransaction.inputs) {
                    tmpOut = tempUTXOs.get(input.transactionOutputId);

                    if (tmpOut == null) {
                        System.out.println("ERROR: Referenced input on Transaction(" + t + ") is Missing!");
                        return false;
                    }

                    if (input.UTXO.value != tmpOut.value) {
                        System.out.println("ERROR: Referenced input Transaction(" + t + ") value is Invalid!");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for (TransactionOUT output : currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if (currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
                    System.out.println("ERROR: Transaction(" + t + ") output reciepient is not who it should be!");
                    return false;
                }
                if (currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
                    System.out.println("ERROR: Transaction(" + t + ") output 'change' is not sender!");
                    return false;
                }

            }

        }
        System.out.println("# Fakechain is valid");
        return true;
    }

    public void addBlock(Block newBlock) {
        newBlock.mineBlock(Commons.MINING_BLOCK_DIFFICULTY);
        this.blockchain.add(newBlock);
    }
}
