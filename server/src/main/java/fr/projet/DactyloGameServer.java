package fr.projet;

import fr.projet.type.GameState;
import fr.projet.type.MessageType;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * C'est une classe qui représente le serveur
 * @version 1.0
 */
public final class DactyloGameServer {

    public static final Logger LOG = LogManager.getLogger();

    public static final Client SERVER_CLIENT = new Client(UUID.randomUUID(), "SERVER", "SERVER-PC");

    private static final HelpFormatter formatter = new HelpFormatter();
    private static final Options options = new Options();

    private static final HashMap<ThreadServer, Client> clientList = new HashMap<>();

    private static final String cmdSyntax = "java -jar DactyloGameServer.jar";
    private static GameState gameState = GameState.WAITING_FOR_PLAYERS;
    private static CommandLine cmdLine;
    private static int port = 24165;
    private static int nbPlayer = 2;
    private static int life = 25;
    private static ServerSocket serversocket;
    private static InputStream inputStream;
    private static ArrayList<String> wordsList;
    private static boolean isExternal = false;

    /**
     * Cette fonction retourne le nombre de joueurs dans le jeu
     *
     * @return Le nombre de joueurs
     */
    public static int getNbPlayer() {
        return nbPlayer;
    }

    /**
     * Il prend les options de ligne de commande et les imprime dans un format agréable
     *
     * @return Le message d'aide pour les options de ligne de commande.
     */
    public static String getHelpMessage() {
        StringWriter out = new StringWriter();
        PrintWriter pw = new PrintWriter(out);
        formatter.printHelp(pw, formatter.getWidth(), cmdSyntax, "", options, formatter.getLeftPadding(), formatter.getDescPadding(), "", true);
        pw.flush();
        return out.toString();
    }


    /**
     * Il envoie un message de deconexion à tous les clients, puis ferme leurs sockets et efface la liste des clients
     */
    private synchronized static void disconnectAllClients() throws IOException {
        Message msg = new Message(SERVER_CLIENT, MessageType.GAME_FORCE_DISCONNECT, "Deconnexion forcee du serveur");
        for (var client : clientList.keySet()) {
            client.sendMessage(msg);
            client.getSocket().close();
            LOG.info("Deconnexion du client " + clientList.get(client));
        }
        clientList.clear();
    }

    public static void main(String[] args) {
        formatter.setSyntaxPrefix("Utilisation: ");

        // Création d'une option pour l'argument de ligne de commande.
        Option wordSource = Option.builder("src")
                .hasArg(true)
                .argName("src")
                .desc("Source des mots, chemin vers un fichier texte. Si aucun fichier n'est specifie, les mots seront generes aleatoirement.")
                .numberOfArgs(1)
                .longOpt("wordSource")
                .type(String.class)
                .build();

        Option nbrPlayer = Option.builder("pnb")
                .hasArg(true)
                .argName("nbrPlayer")
                .desc("Nombre max de joueurs. Par default " + nbPlayer + ".")
                .numberOfArgs(1)
                .longOpt("nombrePlayer")
                .type(Number.class)
                .build();

        Option nbrLife = Option.builder("life")
                .hasArg(true)
                .argName("nbrLife")
                .desc("Nombre de vie par joueur. Par default " + life + ".")
                .numberOfArgs(1)
                .longOpt("nombreVie")
                .type(Number.class)
                .build();

        Option serverPort = Option.builder("port")
                .hasArg(true)
                .argName("port")
                .desc("Port du serveur. Par default " + port)
                .numberOfArgs(1)
                .longOpt("port-server")
                .type(Number.class)
                .build();

        options.addOption(wordSource);
        options.addOption(nbrPlayer);
        options.addOption(serverPort);
        options.addOption(nbrLife);


        CommandLineParser parser = new DefaultParser();
        try {
            cmdLine = parser.parse(options, args);

            //Traitement des options de ligne de commande
            if (cmdLine.hasOption(nbrPlayer)) {
                nbPlayer = Integer.parseInt(cmdLine.getOptionValue(nbrPlayer.getOpt()));
            }

            prepareGame(true);


            if (cmdLine.hasOption(nbrLife)) {
                life = Integer.parseInt(cmdLine.getOptionValue(nbrLife));
            }
            if (cmdLine.hasOption(serverPort)) {
                port = Integer.parseInt(cmdLine.getOptionValue(serverPort));
                if (port > 65535 || port < 0) {
                    LOG.fatal("Le port doit etre compris entre 0 et 65535");
                    System.exit(1);
                }
            }

            Thread commandListener = new Thread(new CommandListener());
            commandListener.start();

            try {
                //Création du socket serveur
                serversocket = new ServerSocket(port);
                LOG.info("Serveur a demarrer sur le port " + port);
                //Boucle infinie pour accepter les connexions
                while (true) {
                    Socket socket = serversocket.accept();
                    ThreadServer threadServer = new ThreadServer(socket, clientList);
                    threadServer.start();
                }
            } catch (IOException e) {
                if (!e.getMessage().contains("Socket closed")) {
                    LOG.fatal("Impossible de demarrer le serveur sur le port: " + port);
                    LOG.fatal("Verifiez que le port est disponible ou choisissez un autre port.");
                    LOG.fatal(getHelpMessage());
                    System.exit(1);
                }
            } finally {
                if (serversocket != null && !serversocket.isClosed()) {
                    try {
                        LOG.info("Fermeture du serveur");
                        serversocket.close();
                    } catch (IOException ignored) {
                    }
                    System.exit(0);
                }
            }
        } catch (ParseException e) {
            LOG.fatal(e.getMessage());
            LOG.fatal(getHelpMessage());
        } catch (UnknownHostException e) {
            LOG.fatal("Erreur d'adresse IP: " + e.getMessage());
        } catch (NumberFormatException e) {
            LOG.fatal("Erreur de format: que les nombres sont acceptes pour les arguments. (sauf -src)");
        } catch (IOException e) {
            LOG.fatal(e.getMessage());
        }
    }

    /**
     * Il prépare le jeu pour un nouveau tour
     *
     * @param disconnect booléen, si vrai, déconnecte tous les clients
     * @throws IOException Si une erreur d'entrée / sortie se produit
     */
    public static void prepareGame(boolean disconnect) throws IOException {
        if (disconnect) {
            disconnectAllClients();
        } else if (GameState.IN_PROGRESS != gameState) {
            Message msg = new Message(SERVER_CLIENT, MessageType.GAME_PLAYER_DISCONNECT, "Un joueur c'est deconnecter, le jeu est annule.");
            LOG.info("Un joueur s'est deconnecter, le jeu est annule.");
            for (var client : clientList.keySet()) {
                client.sendMessage(msg);
            }
        }
        gameState = GameState.WAITING_FOR_PLAYERS;
        LOG.info("En attente des joueurs et du serveur pour commencer la partie");
        if (cmdLine.hasOption("src")) {
            LOG.info("Source des mots: fichier");
            LOG.info("Chemin vers le fichier: " + cmdLine.getOptionValue("src"));
            isExternal = true;
        } else {
            LOG.info("Source des mots: aleatoire");
            inputStream = ClassLoader.getSystemResourceAsStream("wordsList.txt");
        }
        wordsList = getWords();
    }

    /**
     * Il retourne la liste des mots
     * Si l'utilisateur a spécifié un fichier externe, utilisez ce fichier. Sinon, utilisez le fichier par défaut
     *
     * @return Une ArrayList de chaînes
     * @throws FileNotFoundException Si le fichier spécifié n'existe pas
     */
    public static ArrayList<String> getWords() throws FileNotFoundException {
        if (isExternal) {
            inputStream = new FileInputStream(cmdLine.getOptionValue("src"));
        } else {
            inputStream = ClassLoader.getSystemResourceAsStream("wordsList.txt");
        }
        return WordController.getWordsList(inputStream, isExternal);
    }

    /**
     * "Démarrez un nouveau fil qui diffusera un message à tous les clients toutes les secondes jusqu'au démarrage du jeu."
     */
    public static void preStartGame() {
        gameState = GameState.WAITING_FOR_START;
        int nbrSec = 15;
        LOG.info("Debut de la partie dans " + nbrSec + " secondes");
        Thread thread = new Thread(() -> {
            int i = 0;
            Message message;
            while (gameState == GameState.WAITING_FOR_START) {
                message = new Message(SERVER_CLIENT, MessageType.GAME_INFO, "Debut de la partie dans " + (nbrSec - i) + " secondes");
                broadcast(message);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
                if (i == nbrSec) {
                    startGame();
                }
            }
        });
        thread.start();
    }

    /**
     * Cette fonction renvoie l'état actuel du jeu.
     *
     * @return La variable gameState.
     */
    public static GameState getGameState() {
        return gameState;
    }

    /**
     * Cette fonction démarre le jeu.
     * Et envoie un message à tous les clients pour qu'ils commencent à jouer, avec les mots.
     */
    private static void startGame() {
        gameState = GameState.IN_PROGRESS;
        LOG.info("Debut de la partie");
        Message wordMessage = new Message(SERVER_CLIENT, MessageType.GAME_START, "La partie commence!");
        wordMessage.setWords(wordsList);
        wordMessage.setLife(life);
        broadcast(wordMessage);
    }

    /**
     * Il envoie un message à tous les clients pour les informer que le jeu est terminé, puis il envoie un message à tous
     * les clients sauf le gagnant pour les informer qu'ils ont perdu, et aux gagnat qu'il a gagné, ensuite il réinitialise le jeu.
     *
     * @param winner Le gagnant du jeu
     * @throws IOException Si une erreur d'entrée / sortie se produit
     */
    public static void EndGame(ThreadServer winner) throws IOException {
        gameState = GameState.FINISHED;
        LOG.info("Fin de la partie");
        Message message = new Message(SERVER_CLIENT, MessageType.GAME_END, "La partie est terminee!");
        broadcast(message);
        message = new Message(clientList.get(winner), MessageType.GAME_LOSE, "Le gagnant est " + clientList.get(winner).getName() + "!");
        broadcastNotAll(winner, message);
        LOG.info("Le gagnant est " + winner.getId());
        LOG.info("reinitialisation de la partie");
        prepareGame(true);
    }

    /**
     * The broadcast function sends a message to all the clients connected to the server.
     *
     * @param message Send a message to the client
     * @return Nothing
     * @docauthor Trelent
     */
    private static void broadcast(Message message) {
        if (clientList.size() > 0) {
            for (var client : clientList.keySet()) {
                client.sendMessage(message);
            }
        } else {
            LOG.info("Aucun client connecte");
        }
    }

    /**
     * Pour chaque client de la liste des clients, si le client n'est pas le client actuel, envoyez le message au client.
     *
     * @param threadServer Le client qui a envoyé le message
     * @param message      Le message a envoyé à tous les clients.
     */
    private static void broadcastNotAll(ThreadServer threadServer, Message message) {
        for (var client : clientList.keySet()) {
            if (client != threadServer) {
                client.sendMessage(message);
            }
        }
    }

    /**
     * Il prend une liste de clients et renvoie une chaîne avec le nom de chaque client et son adresse IP
     *
     * @return Une chaîne de tous les joueurs du jeu.
     */
    private static String prettyPlayersList() {
        StringBuilder sb = new StringBuilder();
        for (var client : clientList.keySet()) {
            sb.append(clientList.get(client)).append(" IP: ").append(client.getSocket().getInetAddress().getHostAddress()).append("\n");
        }
        return sb.toString();
    }


    /**
     * C'est un thread qui écoute la console et exécute des commandes
     */
    private final static class CommandListener implements Runnable {
        @Override
        public void run() {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String input;
            try {
                while ((input = br.readLine()) != null) {
                    input = input.toLowerCase();
                    if (input.equalsIgnoreCase("stop")) {
                        LOG.info("Fermeture du serveur");
                        disconnectAllClients();
                        serversocket.close();
                        System.exit(0);
                    } else if (input.equalsIgnoreCase("reload")) {
                        prepareGame(true);
                    } else if (input.equalsIgnoreCase("help")) {
                        LOG.info("stop: arrete le serveur");
                        LOG.info("reload: reinitialise la partie");
                        LOG.info("info: affiche les informations du serveur");
                        LOG.info("help: affiche l'aide");
                    } else if (input.equalsIgnoreCase("info")) {
                        LOG.info("Serveur a demarrer sur le port " + port);
                        LOG.info("Etat de la partie: " + gameState);
                        LOG.info("Nombre de joueurs connectes: " + clientList.size());
                        LOG.info("Liste des joueurs connectes:\n" + prettyPlayersList());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

