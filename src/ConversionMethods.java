import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class ConversionMethods {

    public static byte[] hashGeneration(String createHash) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] digest = messageDigest.digest(createHash.getBytes(StandardCharsets.UTF_8));
        return digest;
    }

    public static String hashToHex(byte[] hashGeneration) {
        BigInteger number = new BigInteger(1, hashGeneration);
        StringBuilder hexString = new StringBuilder(number.toString(16)); // Radix 16 is the base for hexadecimal

        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }
        return /** multiplyZero(difficultyHash()) + */
        hexString.toString();
    }

    public static BigInteger hexToDec(Blocks block) throws InvalidKeySpecException, NoSuchAlgorithmException {
        BigInteger decimal = new BigInteger(block.hashBlock(), 16);
        return decimal;
    }

    public static String hashMerkleRoot(byte[] hashGeneration) {
        BigInteger number = new BigInteger(1, hashGeneration);
        StringBuilder hexString = new StringBuilder(number.toString(16));

        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

}
