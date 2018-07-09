package services;

import core.Block;
import core.Blockchain;
import core.Transaction;
import core.Wallet;

import java.util.ArrayList;

public class MiningService {
    private ArrayList<Thread> threadsMiners;
    private ArrayList<Wallet> miners;
    private boolean isRunning;
    private Blockchain possibleBlockchain;

    public MiningService(WalletsManager walletsManager) {
        for (Wallet wallet : walletsManager.wallets) {
            if (wallet.miningEnabled) {
                miners.add(wallet);
            }
        }
        this.isRunning = false;
    }

    public void startMiningSession(Blockchain blockchain, ArrayList<Transaction> transactionsToAdd, String previousHash) {
        if (!isRunning) {
            //TODO check if not running yet, otherwise start it
            Block possibleNewBlock = new Block(previousHash);
            this.possibleBlockchain = new Blockchain(blockchain);

            Transaction minerTransaction = new Transaction();
            possibleNewBlock.addTransaction(this.possibleBlockch);
            isRunning = true;
        }
    }

    public class MiningThread implements Runnable {
        @Override
        public void run() {

        }
    }
}
