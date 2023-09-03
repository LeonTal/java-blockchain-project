import java.net.ServerSocket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Blockchain {
    private static double reward = 100;

    private static ServerSocket ss;
    int port = 1234;

    // Checks if the nBits value is lower than the required number. If it is, the
    // block is considered solved.
    public static boolean hashSolved(Login login, Blocks block)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
                
        if (ConversionMethods.hexToDec(block).compareTo(Blocks.nBits()) <= 0) {
            System.out.println("Value Lower");
            System.out.println(ConversionMethods.hexToDec(block));
            System.out.println("nBits is " + Blocks.nBits());
            
            cryptoReward(login);
            Blocks.addBlockToChain();
            block.createBlock();
            return true;
        } 
        
        else {
            return false;
        }
    }

    private static void cryptoReward(Login login) {
        login.usersDataStorage.currencyAmount += reward;
        Transactions.blockchainReward(login.getUser(), reward);
        
        if (Blocks.oldList.size() % 2 == 1) {
            reward = reward / 2;
        }
    }
}
