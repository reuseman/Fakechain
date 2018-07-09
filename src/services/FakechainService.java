package services;

import core.*;

import java.util.Scanner;

public class FakechainService {
    private WalletsManager walletsManager;
    private Blockchain blockchain;
    private Wallet selectedWallet;

    private Scanner reader;
    private int answer;

    public FakechainService(WalletsManager walletsManager, Blockchain blockchain) {
        this.walletsManager = walletsManager;
        this.blockchain = blockchain;

        Wallet walletA = walletsManager.getWallet(1);
        Wallet walletB = walletsManager.getWallet(2);

        System.out.println("BEFORE BLOCK1");
        System.out.println("\nWalletA's balance is: " + walletA.getBalance(this.blockchain));
        System.out.println("WalletB's balance is: " + walletB.getBalance(this.blockchain));

        //testing
        Block block1 = new Block(this.blockchain.blockchain.get(0).hash);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance(this.blockchain));
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(this.blockchain, walletA.sendFunds(this.blockchain, walletB.publicKey, 40));
        this.blockchain.addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance(this.blockchain));
        System.out.println("WalletB's balance is: " + walletB.getBalance(this.blockchain));

   /*     Block block2 = new Block(block1.hash);
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
        addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());*/

        this.reader = new Scanner(System.in);
        this.answer = -1;
    }

    public void start() {
        System.out.println("LOADING...");
        while (this.answer != 0) {
            if (this.answer != -1) {
                System.out.println("\n-------------MENU-------------");
            } else {
                System.out.println("-------------MENU-------------");
            }
            System.out.println("[1] CREATE NEW WALLET");
            System.out.println("[2] LOGIN INTO WALLET");
            System.out.println("[0] EXIT");
            System.out.print("> ");
            this.answer = reader.nextInt();

            switch (this.answer) {
                case 0:
                    System.out.println("\n~ Goodbye ~");
                    break;
                case 1:
                    walletsManager.createNewWallet(readUsername(reader));
                    System.out.println("# " +
                            walletsManager.getWallet(walletsManager.getNumberOfWallets() - 1).username +
                            "'s wallet has been created!"
                    );
                    break;
                case 2:
                    this.pickWallet(reader);
                    break;
                default:
                    System.out.println("ERROR: The number is not valid!");
                    break;
            }
        }
        this.reader.close();
    }

    public String readUsername(Scanner reader) {
        String username = "EMPTY_USERNAME_INIT";
        while (username.equals("EMPTY_USERNAME_INIT")) {
            System.out.print("Insert wallet's name: ");
            username = reader.next();

            if (username.trim().isEmpty()) {
                username = "EMPTY_USERNAME_INIT";
                System.out.println("ERROR: The name must be not empty!");
            }
        }
        return username;
    }

    public void pickWallet(Scanner reader) {
        int answer = -1;
        while (answer != 0) {
            if (answer != -1) {
                System.out.println("\n-------------WALLETs-------------");
            } else {
                System.out.println("-------------WALLETs-------------");
            }
            for (int i = 0; i < walletsManager.getWallets().size(); i++) {
                System.out.println("[" + (i + 1) + "] " + walletsManager.getWallet(i).username);
            }
            System.out.println("[0] Go back");
            System.out.print("> ");
            answer = reader.nextInt();

            if (answer > walletsManager.getWallets().size()) {
                System.out.println("ERROR: The number is not valid!");
            } else if (answer == 0) {
                this.start();
                break;
            } else {
                selectedWallet = this.walletsManager.getWallet(answer - 1);
                operateOnWallet();
                break;
            }
        }
    }

    public void operateOnWallet() {
        int answer = -1;
        while (answer != 0) {
            if (answer != -1) {
                System.out.println("\n-------------WALLET-------------");
            } else {
                System.out.println("-------------WALLET-------------");
            }

            System.out.println("Selected: " + this.selectedWallet.username + ", " + this.selectedWallet.getBalance(this.blockchain) + " fake coins");
            System.out.println("[1] Make a transaction");
            System.out.println("[0] Go back");
            System.out.print("> ");
            answer = reader.nextInt();

            switch (answer) {
                case 1:
                    System.out.println("TODO");
                    break;
                case 0:
                    this.pickWallet(reader);
                    break;
                default:
                    System.out.println("ERROR: The number is not valid!");
                    break;
            }
        }
    }
}
