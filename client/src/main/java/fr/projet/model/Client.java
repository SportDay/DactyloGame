package fr.projet.model;

import com.google.gson.annotations.Expose;
import fr.projet.util.Security;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

/**
 * La classe Client représente un client.
 */
public final class Client {


    // Une variable temporaire qui n'est pas envoyé vers le serveur
    @Expose(serialize = false, deserialize = false)
    private final String pcUUID = System.getProperty("os.name") + "#" + System.getProperty("user.name") + "#" + System.getenv("USERDOMAIN") + "#" + System.getProperty("user.home");
    @Expose
    private UUID id;
    @Expose
    private String name;
    @Expose
    private String pcName;
    // Une variable temporaire qui n'est pas envoyé vers le serveur
    @Expose(serialize = false, deserialize = false)
    private int level = 0;
    // Une variable temporaire qui n'est pas envoyé vers le serveur
    @Expose(serialize = false, deserialize = false)
    private int life = 50;
    // Une variable temporaire qui n'est pas envoyé vers le serveur
    @Expose(serialize = false, deserialize = false)
    private boolean firstTime = true;
    // Une variable temporaire qui n'est pas envoyé vers le serveur
    @Expose(serialize = false, deserialize = false)
    private int nbrMots;
    // Une variable temporaire qui n'est pas envoyé vers le serveur
    @Expose(serialize = false, deserialize = false)
    private int multiLife;
    // Une variable temporaire qui n'est pas envoyé vers le serveur
    @Expose(serialize = false, deserialize = false)
    private SettingsModel settingsModel = new SettingsModel();
    // Une variable temporaire qui n'est pas envoyé vers le serveur
    @Expose(serialize = false, deserialize = false)
    private HashMap<Integer, Double> precision = new HashMap<>();
    // Une variable temporaire qui n'est pas envoyé vers le serveur
    @Expose(serialize = false, deserialize = false)
    private HashMap<Integer, Double> speed = new HashMap<>();
    // Une variable temporaire qui n'est pas envoyé vers le serveur
    @Expose(serialize = false, deserialize = false)
    private HashMap<Integer, Double> regularity = new HashMap<>();
    // Une variable temporaire qui n'est pas envoyé vers le serveur
    @Expose(serialize = false, deserialize = false)
    private HashMap<Integer, Double> time = new HashMap<>();
    // Une variable temporaire qui n'est pas envoyé vers le serveur
    @Expose(serialize = false, deserialize = false)
    private TmpStats tmpStats = new TmpStats();

    public Client(UUID id, String name, String pcName) {
        this.id = id;
        this.name = name;
        this.pcName = pcName;
    }

    public int getMultiLife() {
        return multiLife;
    }

    public void setMultiLife(int multiLife) {
        this.multiLife = multiLife;
    }

    public TmpStats getTmpStats() {
        return tmpStats;
    }

    public boolean isPcUUID(String pcUUID) {
        return this.pcUUID.equalsIgnoreCase(pcUUID);
    }

    public UUID getUUID() {
        return id;
    }

    public void setUUID(UUID id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        Security.saveCrypt(false);
        DecimalFormat df = new DecimalFormat("#.##");
        tmpStats.updateTotalTime();
        double time = tmpStats.getTotalTime() / 1000d; // pour avoir le temps en secondes
        addTime(level, Double.parseDouble(df.format(time).replace(",", ".")));
        tmpStats.clear();
    }

    public int getNbrMots() {
        return nbrMots;
    }

    public void setNbrMots(int nbrMots) {
        this.nbrMots = nbrMots;
        Security.saveCrypt(false);
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        firstTime = false;
        this.name = name;
        Security.saveCrypt(false);
    }

    public HashMap<Integer, Double> getTime() {
        return time;
    }

    public HashMap<Integer, Double> getPrecision() {
        return precision;
    }

    public HashMap<Integer, Double> getSpeed() {
        return speed;
    }

    public HashMap<Integer, Double> getRegularity() {
        return regularity;
    }

    public void addPrecision(int level, double precision) {
        this.precision.put(level, precision);
        Security.saveCrypt(false);
    }

    public void addSpeed(int level, double speed) {
        this.speed.put(level, speed);
        Security.saveCrypt(false);
    }

    public void addRegularity(int level, double regularity) {
        this.regularity.put(level, regularity);
        Security.saveCrypt(false);
    }

    public void addTime(int level, double time) {
        this.time.put(level, time);
        Security.saveCrypt(false);
    }

    public boolean isFirstTime() {
        return firstTime;
    }

    public SettingsModel getSettingsModel() {
        return settingsModel;
    }

    @Override
    public String toString() {
        return "UUID: " + id + " Name: " + name + " PCName: " + pcName;
    }
}
