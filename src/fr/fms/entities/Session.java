package fr.fms.entities;

import java.time.LocalDate;

/**
 * Une session = une formation programmée à une date précise, avec ses places.
 * Exemple : "Git distanciel" qui commence le 12/10/2026, avec 20 places restantes.
 * public Session : Ca prend donc les infos de Formation et rajoute ses propres attributs soit la date et les places
 */
public class Session {
    //le premier déclare les cases, le formulaire vide et ce qu'il faudrait remplir soit les attributs
    private int id;
    private Formation formation; //En base de données, la table session contient un simple numéro, id_formation. En Java, on stocke directement l'objet Formation entier. C'est ce qui permet d'écrire session.getFormation().getName() pour obtenir le nom
    private LocalDate startDate;
    private int availableSeats;

    //le second ce qu'on remplit vraiment remplit; le constructeur avec le meme nom que la classe et pas de type de retour
    public Session(int id, Formation formation, LocalDate startDate, int availableSeats) {
        this.id = id;
        this.formation = formation;
        this.startDate = startDate;
        this.availableSeats = availableSeats;
    }

    //les getters qui rendent une vqleur et c est tout
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
            line += " | COMPLET";
        }
        return line;
        //fabrique le texte affiché pour chaque ligne du catalogue. @Override signale qu'on remplace la version par défaut de Java
    }
}