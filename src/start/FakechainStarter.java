package start;

import core.Blockchain;
import core.Wallet;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import services.FakechainService;
import services.WalletsManager;
import utils.Commons;

import java.security.Security;

public class FakechainStarter {
    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());

        WalletsManager walletsManager = new WalletsManager();
        Wallet coinBase = walletsManager.getWallet(Commons.COIN_BASE_INDEX);
        Wallet receiverWallet = walletsManager.getWallet(1);

        Blockchain blockchain = new Blockchain(coinBase, receiverWallet);

        FakechainService fakechainService = new FakechainService(walletsManager, blockchain);
        fakechainService.start();
    }
}
