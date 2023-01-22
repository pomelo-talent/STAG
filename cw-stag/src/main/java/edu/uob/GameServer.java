package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;

/** This class implements the STAG server. */
public final class GameServer {

    private static final char END_OF_TRANSMISSION = 4;

    public static void main(String[] args) throws IOException {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        GameServer server = new GameServer(entitiesFile, actionsFile);
        server.blockingListenOn(8888);
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.GameServer(File, File)}) otherwise we won't be able to mark
    * your submission correctly.
    *
    * <p>You MUST use the supplied {@code entitiesFile} and {@code actionsFile}
    *
    * @param entitiesFile The game configuration file containing all game entities to use in your game
    * @param actionsFile The game configuration file containing all game actions to use in your game
    *
    */
    private GameController controller;
    private ArrayList<EntityPlayer> playersList;
    public GameServer(File entitiesFile, File actionsFile) {
        // TODO implement your server logic here
        GameParser parser = new GameParser(entitiesFile, actionsFile);
        controller = new GameController(parser);
        playersList = controller.getPlayersList();
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.GameServer.handleCommand(String)}) otherwise we won't be
    * able to mark your submission correctly.
    *
    * <p>This method handles all incoming game commands and carries out the corresponding actions.
    */
    public String handleCommand(String command) {
        // TODO implement your server logic here
        // e.g. player 1: look
        String[] wholeCommandArray = command.split(":");
        String player = wholeCommandArray[0];
        if (!isPlayerNameValid(player)) {
            return "[ERROR]: check your playername again-> " + player;
        }
        EntityPlayer currentPlayer = getCurrentPlayer(player);

        String[] commandArray = wholeCommandArray[1].trim().split("\\s+");
        if (commandArray.length<1) {
            return "[ERROR]: You need type some actions in your command.";
        }
        //System.out.println("check" + wholeCommandArray[1]);
        if (controller.isCommandValid(commandArray)) {
            currentPlayer.setPlayerLocation(controller.getCurrentLocation());
            return "[VALID]: " + command + "\n" + controller.getBasicText()+ controller.getNarrationText();
        }
        return "[ERROR]: " +command + "\n" + controller.getErrorText() + "\n";
    }

    private boolean isPlayerNameValid(String inputPlayerName) {
        for (int i=0; i<inputPlayerName.length(); i++) {
            if (!isCharValid(inputPlayerName.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean isCharValid(char ch) {
        // uppercase letters
        if (0x41 <= ch && ch <= 0x5A) return true;
        // lowercase letters
        if (0x61 <= ch && ch <= 0x7A) return true;
        // spaces
        if (ch==0x20) return true;
        // apostrophes
        if (ch == 0x27) return true;
        // hyphens
        if (ch == 0x2D) return true;
        return false;
    }

    private EntityPlayer getCurrentPlayer(String playerName) {
        for (EntityPlayer onePlayer:playersList) {
            if (onePlayer.getName().equals(playerName)) {
                controller.setCurrentPlayer(onePlayer);
                controller.setCurrentLocation(onePlayer.getPlayerLocation());
                return onePlayer;
            }
        }
        // if the playerName is not in the playerslist, we can add a new player
        EntityPlayer newPlayer = new EntityPlayer(playerName, "");
        controller.addNewPlayer(newPlayer);
        controller.setCurrentPlayer(newPlayer);
        controller.setCurrentLocation(newPlayer.getPlayerLocation());
        newPlayer.setPlayerLocation(controller.getCurrentLocation());
        return newPlayer;
    }

    //  === Methods below are there to facilitate server related operations. ===

    /**
    * Starts a *blocking* socket server listening for new connections. This method blocks until the
    * current thread is interrupted.
    *
    * <p>This method isn't used for marking. You shouldn't have to modify this method, but you can if
    * you want to.
    *
    * @param portNumber The port to listen on.
    * @throws IOException If any IO related operation fails.
    */
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }
        }
    }

    /**
    * Handles an incoming connection from the socket server.
    *
    * <p>This method isn't used for marking. You shouldn't have to modify this method, but you can if
    * * you want to.
    *
    * @param serverSocket The client socket to read/write from.
    * @throws IOException If any IO related operation fails.
    */
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if(incomingCommand != null) {
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();

            }
        }
    }

}
