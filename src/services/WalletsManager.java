package services;

import core.Wallet;

import java.util.ArrayList;

public class WalletsManager {
    public ArrayList<Wallet> wallets;

    public WalletsManager() {
        this.wallets = new ArrayList<>();

        // Hard coding the initial wallet (only the first two are a needed)
        this.wallets.add(new Wallet("coinbase"));
        this.wallets.add(new Wallet("alex"));
        this.wallets.add(new Wallet("john"));

        // Hard coding other wallet, BUT setting them to be even miner
        this.wallets.add(new Wallet("miner1"));
        this.wallets.get(this.wallets.size() - 1).setMiningEnabled(true);
        this.wallets.add(new Wallet("miner2"));
        this.wallets.get(this.wallets.size() - 1).setMiningEnabled(true);
        this.wallets.add(new Wallet("miner3"));
        this.wallets.get(this.wallets.size() - 1).setMiningEnabled(true);
        this.wallets.add(new Wallet("miner4"));
        this.wallets.get(this.wallets.size() - 1).setMiningEnabled(true);
        this.wallets.add(new Wallet("miner5"));
        this.wallets.get(this.wallets.size() - 1).setMiningEnabled(true);
        this.wallets.add(new Wallet("miner6"));
        this.wallets.get(this.wallets.size() - 1).setMiningEnabled(true);
    }

    public void createNewWallet(String username) {
        this.wallets.add(new Wallet(username));
    }

    public Wallet getWallet(int index) {
        return this.wallets.get(index);
    }

    public ArrayList<Wallet> getWallets() {
        return wallets;
    }

    public int getNumberOfWallets() {
        return this.wallets.size();
    }
}
