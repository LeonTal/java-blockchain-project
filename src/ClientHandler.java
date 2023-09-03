import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

public class ClientHandler {

    private Socket socket;
    private int port = 1234;
    private String address = "localhost";

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        System.out.println("Welcome to the Blockchain Cryptocurrency Program.");
        new ClientHandler().loginCommands();
    }

    private void loginCommands() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {

        socket = new Socket(address, port);

        PrintStream printStream = new PrintStream(socket.getOutputStream());
        Scanner bufferedReader = new Scanner(socket.getInputStream());
        String response = "";

        Scanner scanner = new Scanner(System.in);
        String username;
        String password;
        boolean loopBoolean = true;

        while (loopBoolean) {
            System.out.println("Enter \"1\" to Login with an existing account, or enter \"2\" to create an account.");
            String choice = scanner.nextLine();
            
            if (choice.equalsIgnoreCase("1")) {
                System.out.println("Enter your username");
                username = scanner.nextLine().replaceAll("~", "-tilde-");
                System.out.println("Enter your password");
                password = scanner.nextLine().replaceAll("~", "-tilde-");

                printStream.println("authenticate~" + username + "~" + password);
                printStream.flush();

                response = bufferedReader.nextLine();
                String[] responses = response.split("~");
                
                if (responses[0].equalsIgnoreCase("success")) {
                    System.out.println(responses[1]);
                    mineBlock(printStream, bufferedReader);

                } 
                
                else {
                    System.out.println(responses[1]);
                }

            } 
            
            else if (choice.equalsIgnoreCase("2")) {
                System.out.println("Enter your desired username: ");
                username = scanner.nextLine().replaceAll("~", "-tilde-");
                System.out.println("Enter your desired password: ");
                password = scanner.nextLine().replaceAll("~", "-tilde-");

                printStream.println("create~" + username + "~" + password);
                printStream.flush();

                response = bufferedReader.nextLine();
                String[] responses = response.split("~");
                
                if (responses[0].equalsIgnoreCase("success")) {
                    System.out.println(responses[1]);
                } 
                
                else {
                    System.out.println(responses[1]);
                }
            }
        }

        printStream.close();
        bufferedReader.close();
        socket.close();
    }

    private void mineBlock(PrintStream printStream, Scanner scanner) {
        boolean loopBoolean = true;
        String receiver;
        Scanner user = new Scanner(System.in);

        String menu = scanner.nextLine().replaceAll("\t", "\n");

        while (loopBoolean) {
            System.out.println(menu);
            receiver = user.nextLine();
            printStream.println(receiver);
            
            switch (receiver) {
                case "1":
                    receiver = scanner.nextLine();
                    System.out.println(receiver);
                    System.out.println(scanner.nextLine());
                    break;

                case "2":
                    System.out.println(scanner.nextLine());
                    String name = user.nextLine();
                    printStream.println(name);
                    System.out.println(scanner.nextLine());
                    printStream.println(user.nextLine());
                    System.out.println(scanner.nextLine());
                    break;

                case "3":
                    System.out.println(scanner.nextLine().replaceAll("\t", "\n"));
                    break;

                case "4":
                    printStream.println("You have been logged out");
                    System.out.println(scanner.nextLine());
                    loopBoolean = false;
                    break;
            }
        }
    }
}
