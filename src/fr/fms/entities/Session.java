package fr.fms.entities;

import java.time.LocalDate;

/**
 * Une session = une formation programmée à une date précise, avec ses places.
 * Exemple : "Git distanciel" qui commence le 12/10/2026, avec 20 places restantes.
 */
public class Session {
    private int id;
    private Formation formation; //En base de données, la table session contient un simple numéro, id_formation. En Java, on stocke directement l'objet Formation entier. C'est ce qui permet d'écrire session.getFormation().getName() pour obtenir le nom
    private LocalDate startDate;
    private int availableSeats;

    public Session(int id, Formation formation, LocalDate startDate, int availableSeats) {
        this.id = id;
        this.formation = formation;
        this.startDate = startDate;
        this.availableSeats = availableSeats;
    }

    public int getId() {
        return id;
    }

    public Formation getFormation() {
        return formation;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    /**
     * Indique si la session est complète.
     * @return true s'il ne reste aucune place, false sinon
     */
    public boolean isFull() {
        return availableSeats == 0;
    }

    @Override
    public String toString() {
        String line = formation.getName() + " | " + formation.getMode() + " | " + formation.getPrice() + " € TTC";
        if (isFull()) {
            line += " | COMPLET"; //signifie « ajoute ce texte à la fin de line ».
        }
        return line;
    }
}