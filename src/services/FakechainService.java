package services;

import core.*;

import java.util.Scanner;

public class FakechainService {
    private WalletsManager walletsManager;
    private Blockchain blockchain;
    private Wallet selectedWallet;
    private Wallet receiverWallet;

    private Scanner reader;
    private int answer;

    public FakechainService(WalletsManager walletsManager, Blockchain blockchain) {
        this.walletsManager = walletsManager;
        this.blockchain = blockchain;
        this.reader = new Scanner(System.in);
        this.answer = -1;
    }

    public void start() {
        System.out.println("\nLOADING...");
        while (this.answer != 0) {
            if (this.answer != -1) {
                System.out.println("\n-------------MENU-------------");
            } else {
                System.out.println("-------------MENU-------------");
            }
            System.out.println("[1] CREATE NEW WALLET");
            System.out.println("[2] LOGIN INTO WALLET");
            System.out.println("[3] CHECK CHAIN'S VALIDITY");
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
                case 3:
                    boolean tmp = this.blockchain.isChainValid();
                    System.out.println("Chain is valid: " + tmp);
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

            System.out.println("Selected: " + this.selectedWallet.username + ", " +
                    this.selectedWallet.getBalance(this.blockchain) + " fake coins");
            System.out.println("[1] Make a transaction");
            System.out.println("[0] Go back");
            System.out.print("> ");
            answer = reader.nextInt();

            switch (answer) {
                case 1:
                    this.selectReceiverWallet();
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

    public void selectReceiverWallet() {
        int answer = -1;
        while (answer != 0) {
            if (answer != -1) {
                System.out.println("\n-------------RECEIVER-------------");
            } else {
                System.out.println("-------------RECEIVER-------------");
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
                receiverWallet = this.walletsManager.getWallet(answer - 1);
                if (receiverWallet.equals(selectedWallet)) {
                    System.out.println("ERROR: You cannot send Fakecoins to yourself!");
                } else {
                    int fakeCoins = -1;
                    float tmpSelectedWalletBalance = this.selectedWallet.getBalance(blockchain);

                    while (fakeCoins == -1) {
                        if (tmpSelectedWalletBalance == 0) {
                            System.out.println("You got no Fakecoins..");
                        } else {
                            System.out.println("Fakecoins to transfer [0.1, " + tmpSelectedWalletBalance + "]");
                        }
                        System.out.print("> ");
                        fakeCoins = reader.nextInt();

                        if (fakeCoins <= 0) {
                            System.out.println("ERROR: You cannot send less than 0 Fakecoins!");
                            fakeCoins = -1;
                        } else if (fakeCoins > tmpSelectedWalletBalance) {
                            System.out.println("ERROR: You cannot more than the coins in your wallet!");
                        } else {
                            Block blockToAdd =
                                    new Block(this.blockchain.blockchain.get(this.blockchain.blockchain.size() - 1).hash);
                            System.out.println("\n" + selectedWallet.username + " is trying to send " + fakeCoins +
                                    " Fakecoins to " + receiverWallet.username);
                            boolean transactionDone = blockToAdd.addTransaction(
                                    this.blockchain,
                                    selectedWallet.sendFunds(this.blockchain, receiverWallet.publicKey, fakeCoins));
                            if (transactionDone) {
                                this.blockchain.addBlock(blockToAdd);
                            } else {
                                System.out.println("ERROR: Transaction went wrong!");
                            }
                            break;
                        }
                    }

                    break;
                }
            }
        }
    }
}
