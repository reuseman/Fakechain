package services;

import core.Transaction;
import utils.Commons;

import java.util.ArrayList;

public class TransactionsBroadcast {
    ArrayList<Transaction> transactionsToAdd;
    MiningService miningService;

    public TransactionsBroadcast(MiningService miningService) {
        this.miningService = miningService;
        this.transactionsToAdd = new ArrayList<>(Commons.MAX_TRANSACTIONS_NUMBER_PER_BLOCK + 1);
    }

    public boolean addTransaction(Transaction transaction) {
        if (transaction == null) {
            return false;
        }

        // The maximum number of transactions per block does not include the reward for the miner
        if (transactionsToAdd.size() == Commons.MAX_TRANSACTIONS_NUMBER_PER_BLOCK) {
            System.out.println("ERROR: The block is full, wait the next one!");
            return false;
        } else if (transactionsToAdd.size() <= Commons.MAX_TRANSACTIONS_NUMBER_PER_BLOCK - 1){
            transactionsToAdd.add(transaction);
            this.miningService.startMiningSession();
            return true;
        }

        return false;
    }
}
