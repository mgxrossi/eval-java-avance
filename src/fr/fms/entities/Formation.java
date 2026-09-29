package fr.fms.entities;

/**
 * Une formation du catalogue, dans un mode donné.
 * Exemple : "Git" en DISTANCIEL, 7 jours, 700 euros TTC.
 */
public class Formation {
    private int id;
    private String name;
    private String description;
    private Mode mode;
    private int durationDays;
    private double price;

    public Formation(int id, String name, String description, Mode mode, int durationDays, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.mode = mode;
        this.durationDays = durationDays;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Mode getMode() {
        return mode;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return name + " | " + mode + " | " + price + " € TTC";
    }
}