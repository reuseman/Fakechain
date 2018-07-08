package services;

import core.Wallet;

import java.util.ArrayList;

public class WalletsManager {
    public ArrayList<Wallet> wallets;

    public WalletsManager() {
        this.wallets = new ArrayList<>();
        this.wallets.add(new Wallet("coinbase"));
        this.wallets.add(new Wallet("alex"));
        this.wallets.add(new Wallet("john"));
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
