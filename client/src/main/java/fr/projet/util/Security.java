package fr.projet.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.projet.DactyloClient;
import fr.projet.model.Client;
import javafx.scene.control.Alert;

import javax.crypto.*;
import java.io.*;
import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public final class Security {


    /**
     * Recuperation de la cle secrete
     *
     * @return La clé utilisée pour chiffrer/dechiffrer.
     */
    private static SecretKey getKey() {
        try (ObjectInputStream ois = new ObjectInputStream(ClassLoader.getSystemResourceAsStream("I_dont_know_what.is"))) {
            return (SecretKey) ois.readObject();
        } catch (IOException | ClassNotFoundException ignored) {

        }
        return null;
    }

    /**
     * Il ouvre le fichier, le déchiffre, puis désérialise l'objet
     */
    public static void openCrypt() {
        ObjectInputStream ois = null;
        try {
            FileInputStream fichier = new FileInputStream(Constants.PATH + "client.dg");
            ois = new ObjectInputStream(fichier);
            Cipher dcipher = Cipher.getInstance("AES");
            dcipher.init(Cipher.DECRYPT_MODE, getKey());
            Gson gson = new GsonBuilder().create();
            DactyloClient.setClient(gson.fromJson((String) ((SealedObject) ois.readObject()).getObject(dcipher), Client.class));
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                 InvalidKeyException | IOException | ClassNotFoundException ignored) {
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (final IOException ignored) {

            }
        }
    }

    /**
     * Il enregistre les données du client dans un fichier, en les cryptant avec AES
     *
     * @param erase si vrai, le client sera réinitialisé aux valeurs par défaut
     */
    public static void saveCrypt(boolean erase) {
        ObjectOutputStream oos = null;
        try {
            FileOutputStream fichier = new FileOutputStream(Constants.PATH + "client.dg");
            oos = new ObjectOutputStream(fichier);
            SealedObject sealedObject = null;
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, getKey());

            Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().setPrettyPrinting().create();

            if (erase) {
                DactyloClient.setClient(new Client(UUID.randomUUID(), "DefaultName", InetAddress.getLocalHost().getHostName()));
            }
            sealedObject = new SealedObject(gson.toJson(DactyloClient.getClient()), cipher);
            if (sealedObject == null) {
            }
            oos.writeObject(sealedObject);
        } catch (IOException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                 InvalidKeyException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null) {
                    oos.flush();
                    oos.close();
                }
            } catch (IOException ignored) {

            }
        }
    }

    /**
     * Si l'UUID de l'ordinateur n'est pas celui stocké dans le profil, la progression de l'utilisateur est réinitialisée.
     */
    public void checkOk() {
        String pcUUID = System.getProperty("os.name") + "#" + System.getProperty("user.name") + "#" + System.getenv("USERDOMAIN") + "#" + System.getProperty("user.home");
        if (!DactyloClient.getClient().isPcUUID(pcUUID)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Anti-cheat");
            alert.setHeaderText("Une tentative de cheat a été détectée.");
            alert.setContentText("Votre progression a été réinitialisée.");
            alert.showAndWait();
            saveCrypt(true);
        }
    }


}
