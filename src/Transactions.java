import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

// This code allows users to transfer currency between each other

public class Transactions {

    public static ArrayList<TransactionVariables> transactionList = new ArrayList<>();
    private static TransactionVariables transactionVariables = new TransactionVariables();
    private static UsersDataStorage usersDataStorage;
    private static HashMap<String, UsersDataStorage> dataStorage = Login.dataStorage;
    private Login login;

    public Transactions(Login login) {
        this.login = login;
        usersDataStorage = login.usersDataStorage;
    }

    private void addToTransactionList(String receiver, double amount) {
        transactionVariables.sender = login.getUser();
        transactionVariables.receiver = receiver;
        transactionVariables.amount = amount;
        System.out.println(
                "User " + transactionVariables.sender + " has sent user " + receiver + " " + amount + " currency");
        transactionList.add(transactionVariables);
    }

    public static void blockchainReward(String receiver, double amount) {
        transactionVariables.sender = "Blockchain";
        transactionVariables.receiver = receiver;
        transactionVariables.amount = amount;
        System.out.println("The blockchain has awarded user " + receiver + " " + amount + " for solving the block");
        transactionList.add(transactionVariables);
    }

    // Whenever a user sends currency to another user, this transaction is added to
    // a list so all transactions are viewable
    public String transferCurrency(String receiver, double amount) {
        if (amount < 0) {
            return ("Please enter an amount greater than 0");
        }

        // Checks if you have enough currency to transfer
        if (usersDataStorage.currencyAmount < amount) {
            return ("You do not have enough currency to send.");

        }

        // Checks if the user you're trying to send currency to exists within the current blockchain
        if (!dataStorage.containsKey(receiver)) {
            return ("This user does not exist.");
        }
        
        if (usersDataStorage.currencyAmount >= amount) {
            UsersDataStorage getReceiver = dataStorage.get(receiver);
            usersDataStorage.currencyAmount -= amount;
            getReceiver.currencyAmount += amount;
            addToTransactionList(receiver, amount);
            newTransactionVariables();
        }
        System.out.println(transactionList);
        return "Transaction successful";

    }

    public static byte[] transactionHash(TransactionVariables transactionList) throws NoSuchAlgorithmException {
        String transactionString = transactionList.toString();
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] digest = messageDigest.digest(transactionString.getBytes(StandardCharsets.UTF_8));
        return digest;
    }

    // This is necessary so everyones Merkle Root is different, thus giving everyone
    // different hashes
    public String uniqueTransactionMessage() {
        return "User " + login.usersDataStorage.username + " currently has " + login.usersDataStorage.currencyAmount;
    }

    private void newTransactionVariables() {
        transactionVariables = new TransactionVariables();
    }

    static class TransactionVariables {
        String receiver;
        String sender;
        double amount;

        @Override
        public String toString() {
            return sender + " transferred " + amount + " currency to " + receiver + "\n";
        }
    }
}
