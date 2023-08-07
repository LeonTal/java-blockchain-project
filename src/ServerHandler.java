import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

public class ServerHandler {

    private ServerSocket serverSocket;
    private int port = 1234;

    public void startServer() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started. Please connect with a client.");

        while (true) {
            Socket socket = serverSocket.accept();
            ClientThread clientThread = new ClientThread(socket);
            clientThread.start();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerHandler serverHandler = new ServerHandler();
        serverHandler.startServer();
    }

    class ClientThread extends Thread {
        private Socket socket;
        private PrintStream printStream;
        private Scanner bufferedReader;
        private Login login;
        private Blocks blocks;

        public ClientThread(Socket s) throws IOException {
            printStream = new PrintStream((s.getOutputStream()));
            bufferedReader = new Scanner((s.getInputStream()));
            socket = s;
            login = new Login();
        }

        public void run() {
            String command;
            try {

                while (true) {
                    command = bufferedReader.nextLine();
                    String[] commands = command.split("~");
                    if (command.startsWith("authenticate")) {
                        if (login.authenticate(commands[1], commands[2])) {
                            printStream.println("success~You have logged in successfully");
                            printStream.flush();
                            mineMenu();
                        } else {
                            System.out.println("failure");
                            printStream.println("failed~Your login details are incorrect. Please check and try again");
                            printStream.flush();
                        }
                    } else if (command.startsWith("create")) {
                        if (login.createAccount(commands[1], commands[2])) {
                            printStream.println("success~Account created successfully");
                        } else {
                            printStream.println("failed~Username and password must be 3 or more characters long");
                        }

                        printStream.flush();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                printStream.close();
                bufferedReader.close();
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void mineMenu() throws InvalidKeySpecException, NoSuchAlgorithmException {
            blocks = new Blocks(login);
            blocks.startBlock(printStream, bufferedReader);
        }
    }

}
