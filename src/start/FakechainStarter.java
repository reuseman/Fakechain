package start;

import core.Blockchain;
import core.Transaction;
import core.Wallet;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import services.FakechainService;
import services.MiningService;
import services.TransactionsBroadcast;
import services.WalletsManager;
import utils.Commons;

import java.security.Security;

public class FakechainStarter {
    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());

        WalletsManager walletsManager = new WalletsManager();
        Wallet coinBase = walletsManager.getWallet(Commons.COIN_BASE_INDEX);
        Wallet receiverWallet = walletsManager.getWallet(1);

        MiningService miningService = new MiningService(walletsManager);
        TransactionsBroadcast transactionsBroadcast = new TransactionsBroadcast(miningService);

        Blockchain blockchain = new Blockchain(coinBase, receiverWallet);

        FakechainService fakechainService = new FakechainService(walletsManager, blockchain);
        fakechainService.start();
    }
}
