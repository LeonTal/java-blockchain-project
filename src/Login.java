import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.security.SecureRandom;

public class Login {
    public UsersDataStorage usersDataStorage = new UsersDataStorage();
    public static HashMap<String, UsersDataStorage> dataStorage = new HashMap<>();

    private static String generateSalt() throws NoSuchAlgorithmException {
        SecureRandom getRandom = SecureRandom.getInstance("SHA1PRNG"); // Uses the SHA1 function to create a random
                                                                       // number generator
        byte[] saltBytes = new byte[4];
        getRandom.nextBytes(saltBytes);
        String encodeSalt = Base64.getEncoder().encodeToString(saltBytes);
        return encodeSalt;
    }

    // Creates a new account
    public boolean createAccount(String username, String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        usersDataStorage = new UsersDataStorage();
        usersDataStorage.username = username;
        usersDataStorage.currencyAmount = 0.0;
        usersDataStorage.salt = generateSalt(); // Stores the users personal salt
        usersDataStorage.hashedPassword = hashPassword(password, usersDataStorage.salt); // Makes it so the password is
                                                                                         // never stored in plaintext in
                                                                                         // the database
        if (username.length() < 3 || password.length() < 3) {
            return false;
        } else {
            insertUserToDatabase(usersDataStorage); // Saves the user into the database
            return true;
        }
    }

    public boolean authenticate(String username, String password)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        usersDataStorage = dataStorage.get(username);
                
        if (usersDataStorage != null) {
            String currentUsersSalt = usersDataStorage.salt;
            String getUsersHash = hashPassword(password, currentUsersSalt);
            
            if (getUsersHash.equals(usersDataStorage.hashedPassword)) {
                return true;
            } else
                return false;
        } else
            return false;
    }

    private static String hashPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 5000;
        int keyLength = 112;
        byte[] saltByte = Base64.getDecoder().decode(salt);

        PBEKeySpec PBEKeySpec = new PBEKeySpec(password.toCharArray(), saltByte, iterations, keyLength);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] hashBytes = secretKeyFactory.generateSecret(PBEKeySpec).getEncoded();
        String finalHash = Base64.getEncoder().encodeToString(hashBytes);
        return finalHash;
    }

    // Creates a new version of the UserDataStorage class so that the values don't
    // overwrite everytime a new account is created
    private void insertUserToDatabase(UsersDataStorage user) {
        if (dataStorage.containsKey(user.username)) {
            System.out.println("There is already a user with that username. Please try another name.");
        } else {
            dataStorage.put(user.username, user);
            newUserDataStorage();
        }
    }

    public void newUserDataStorage() {
        usersDataStorage = new UsersDataStorage();
    }

    private void printAccounts() {
        System.out.println(dataStorage);
    }

    public String getUser() {
        return usersDataStorage.username;
    }

    public String userAmount() {
        return "User: " + usersDataStorage.username + "\t" + "Crypto in Wallet: " + usersDataStorage.currencyAmount;
    }
}

class UsersDataStorage { // Stores information regarding a users account
    String username;
    String salt;
    String hashedPassword;
    double currencyAmount = 0;

    @Override
    public String toString() {
        return username + salt + hashedPassword + currencyAmount;
    }
}
