import java.io.PrintStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Blocks {
    private Login login;
    private static BlockValues blockValues = new BlockValues();

    public static ArrayList<BlockValues> blockContents = new ArrayList<>();
    public static ArrayList<BlockValues> firstTimeStampBlock = new ArrayList<>();
    public static ArrayList<ArrayList<BlockValues>> oldList = new ArrayList<>();

    private static double difficulty = 1;
    private static int blockCompletionCounter = 0;
    public static Instant firstBlockTime;
    private static Timer timer;
    private static int nBits = 505000000;
    public static Long nonce = 0L;
    public Transactions transactions;

    public Blocks(Login login) {
        this.login = login;
        transactions = new Transactions(login);
    }

    public static String previousBlockHash() throws NoSuchAlgorithmException {
        String previousBlockHash;

        if (oldListCount() == 0) {
            return "00000000000000000000";
        } else {
            ArrayList<BlockValues> previousBlock = oldList.get(oldList.size() - 1);
            previousBlockHash = ConversionMethods.hashToHex(ConversionMethods.hashGeneration(previousBlock.toString()));
            return previousBlockHash;
        }
    }

    private static String currentDateTime() { // Time is important as it changes every second which means when combined
                                              // with the nonce, gives a unique hash every second.
        String formatDateTime = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z").format(new Date());
        return formatDateTime;
    }

    private static double version() {
        return 1.0;
    }

    public static double difficultyTarget() {
        if (oldListCount() > blockCompletionCounter) {
            blockCompletionCounter++;
            if (blockCompletionCounter == 3) {
                Instant lastBlockTime = Instant.now();
                long minutesElapsed = Duration.between(firstBlockTime, lastBlockTime).toMinutes();
                // System.out.println(" Minute = " + minutesElapsed + " FBT = " + firstBlockTime
                // + " SBT = " + lastBlockTime);

                if (minutesElapsed > 1) {
                    difficulty -= (difficulty * (10.0 / 100.0));
                }

                else if (minutesElapsed < 1) {
                    difficulty += (difficulty * (10.0 / 100));
                }

                blockCompletionCounter = 0;
                firstBlockTime = Instant.now();
            }
        }
        System.out.println(difficulty);
        return difficulty;
    }

    // HOW TO CALCULATE Target nBits (Target Hash):
    // Translate nBits to Big-Endian Hex e.g. 404472624 > 0x181bc330
    // Remove the first 2 digits after the 0x. e.g. 0x181bc330 > 0x1bc330
    // Multiply the above by 256 ^ the removed digits - 3 e.g. 0x1bc330 * 256 ^
    // (0x18 - 3)
    // the above translate to decimal as 1819440 * (256 ^ (24 - 3)) =
    // 680,733,321,990,486,529,407,107,157,001,552,378,184,394,215,934,016,880,640
    // Reconvert the answer above to hexadecimal to get the nBit value that the
    // final hash needs to be lower than
    // The above conversion to hex being
    // 0x1BC330000000000000000000000000000000000000000000
    public static BigInteger nBits() {
        String nBitHex = Integer.toHexString(nBits);
        String storeFirstTwoHexDigits = nBitHex.substring(0, 2);
        String getRemainingHex = nBitHex.substring(2);
        BigInteger baseValue = new BigInteger(String.valueOf(Integer.parseInt(getRemainingHex, 16)));
        int powerOf = Integer.parseInt(storeFirstTwoHexDigits, 16) - 3;
        BigInteger mutliplyBy = new BigInteger("256");
        BigInteger finalResult = baseValue.multiply(mutliplyBy.pow(powerOf));
        return finalResult;
    }

    public static Long nonce() {
        return nonce;
    }

    private String merkleRoot() throws NoSuchAlgorithmException {
        ArrayList<String> i = new ArrayList<>();
        i.add(transactions.uniqueTransactionMessage());

        for (Transactions.TransactionVariables iterateTransaction : blockValues.transactionList) {
            String transactions = iterateTransaction.toString();
            transactions = ConversionMethods.hashToHex(Transactions.transactionHash(iterateTransaction));
            i.add(transactions);
        }

        String result = ConversionMethods.hashMerkleRoot(ConversionMethods.hashGeneration(i.toString()));
        return result;
    }

    private static int oldListCount() {
        int listCount = 0;

        for (ArrayList count : oldList) {
            if (count.iterator().hasNext()) {
                listCount++;
                count.iterator().next();
                if (listCount % 100 == 0) {
                    firstTimeStampBlock = blockContents;
                }
            }
        }
        return listCount;
    }

    private void newBlock() throws NoSuchAlgorithmException, InvalidKeySpecException {
        blockValues.previousBlockHash = previousBlockHash();
        blockValues.currentDateTime = currentDateTime();
        blockValues.version = version();
        blockValues.nonce = nonce();
        blockValues.difficultyTarget = difficultyTarget();
        blockValues.merkleRoot = merkleRoot();
        blockValues.transactionList = Transactions.transactionList;
        blockContents.add(blockValues);
        updateBlock();
    }

    private static void updateBlock() { // Updates the block every second (changes time every second)
        timer = new Timer();
        blockValues.currentDateTime = currentDateTime();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                updateBlock();
            }
        };

        timer.schedule(timerTask, 1000);
    }

    // The currentDateTime, nonce, and merkleRoot are constantly updated here as
    // they constantly change while the block
    // is mining, thus they need to be constantly hashed. The other 3 values do not
    // change while the block is mining.
    public String hashBlock() throws NoSuchAlgorithmException, InvalidKeySpecException {
        blockValues.currentDateTime = currentDateTime();
        blockValues.nonce = nonce();
        blockValues.merkleRoot = merkleRoot(); // This isn't how the Merkle Root works in the Bitcoin implementation

        String hashString = blockValues.previousBlockHash + blockValues.currentDateTime + blockValues.version +
                blockValues.nonce + blockValues.difficultyTarget + blockValues.merkleRoot;
        String result = ConversionMethods.hashToHex(ConversionMethods.hashGeneration(hashString));

        return result;
    }

    // This creates a new Block. Should only be called once the previous Block has
    // been successfully mined and a new Block is required.
    public void createBlock() throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (blockContents.isEmpty()) {
            if (blockCompletionCounter == 0) {
                firstBlockTime = Instant.now();
                newBlock();
                nonce = 0L;
            } else {
                newBlock();
            }
        } else {
            System.out.println("Already contains block");
        }
    }

    public static void addBlockToChain() {
        oldList.add(blockContents);
        blockContents = new ArrayList<>();
        blockValues = new Blocks.BlockValues();
        Transactions.transactionList = new ArrayList<>();
    }

    public void startBlock(PrintStream printStream, Scanner scanner)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        boolean loopBoolean = true;
        String receiver;
        double amount;
        createBlock();

        printStream.println(
                "Enter \"1\" to start mining.\tEnter \"2\" to transfer currency\tEnter \"3\" to check your currency cryptocurrency balance.\tEnter \"4\" to logout");

        while (loopBoolean) {
            switch (scanner.nextLine()) {
                case "1":
                    printStream.println("Mining Commencing...");
                    while (!Blockchain.hashSolved(login, this)) {
                        nonce++;
                    }
                    printStream.println("Mining Complete");
                    break;

                case "2":
                    printStream.println("Which user do you want to transfer to? ");
                    receiver = scanner.nextLine();
                    printStream.println("How much do you want to transfer? ");
                    amount = Double.parseDouble(scanner.nextLine());
                    Transactions t = new Transactions(login);
                    printStream.println(t.transferCurrency(receiver, amount));
                    break;

                case "3":
                    printStream.println(login.userAmount());
                    break;

                case "4":
                    printStream.println("You have been logged out");
                    loopBoolean = false;
                    login.newUserDataStorage();
                    break;
            }
        }
    }

    static class BlockValues {
        String previousBlockHash;
        String currentDateTime;
        double version;
        Long nonce;
        double difficultyTarget;
        String merkleRoot;
        ArrayList<Transactions.TransactionVariables> transactionList = Transactions.transactionList;

        @Override
        public String toString() { // Makes it so it the block values can be printed in text and not as a memory
                                   // location. Probably a better way to do it but it works for now.
            String result = "Previous Block Hash: " + previousBlockHash + "\n" + "Timestamp: " + currentDateTime + "\n"
                    +
                    "Version: " + version + "\n" + "Nonce: " + nonce + "\n" + "Difficulty Target: " + difficultyTarget +
                    "\n" + "Merkle Root: " + merkleRoot + "\n" + "Transaction List: " + transactionList + "\n";
            return result;
        }
    }

}