package services;

import core.Wallet;

public class MiningSessionThread implements Runnable {
    private boolean blockSolved;
    private Wallet miner;

    public MiningSessionThread(Wallet miner) {
        this.miner = miner;
    }

    @Override
    public void run() {

    }
}
