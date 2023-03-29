/**
 * C'est une classe qui représente un client
 * <p>
 * C'est une classe qui représente un client
 */
/**
 * C'est une classe qui représente un client
 */
package fr.projet;

import com.google.gson.annotations.Expose;

import java.util.UUID;

/**
 * C'est une classe qui représente un client
 */
public final class Client {

    /**
     * C'est l'identifiant du client
     * @see Client#getUUID()
     * @see Client#getShortName()
     */
    @Expose
    private UUID id;
    /**
     * C'est le nom du client
     * @see Client#getName()
     */
    @Expose
    private String name;

    /**
     * C'est le nom du ordinateur du client
     * @see Client#getPcName()
     */
    @Expose
    private String pcName;

    /**
     *  C'est un constructeur de la classe Client
     * @param id C'est l'identifiant du client
     * @param name C'est le nom du client
     * @param pcName C'est le nom de l'ordinateur du client
     */
    public Client(UUID id, String name, String pcName) {
        this.id = id;
        this.name = name;
        this.pcName = pcName;
    }

    /**
     *  C'est une méthode qui permet de récupérer l'id du client
     * @return l'id du client
     */
    public UUID getUUID() {
        return id;
    }

    /**
     * C'est une méthode qui permet de récupérer le nom du client
     * @return le nom du client
     */
    public String getName() {
        return name;
    }

    /**
     * C'est une méthode qui permet de récupérer le nom de l'ordinateur du client
     * @return le nom de l'ordinateur du client
     */
    public String getPcName() {
        return pcName;
    }

    /**
     * C'est une méthode qui permet de recuperer 4 lettres du id du client
     * @return id du client en format racourci
     */
    public String getShortName() {
        return getUUID().toString().substring(1, 5);
    }

    /**
     * Si l'UUID, le nom et le pcName du client transmis sont égaux au client courrant, alors renvoie true
     *
     * @param client Le client avec lequel vous voulez vous comparer.
     * @return Une valeur booléenne.
     */
    public boolean equals(Client client) {
        if (client.getUUID().equals(this.getUUID())) {
            if (client.getName().equals(this.getName())) {
                return client.getPcName().equals(this.getPcName());
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "UUID: " + id + " Name: " + name + " PCName: " + pcName;
    }
}
