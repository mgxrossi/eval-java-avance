package fr.fms.app;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import fr.fms.business.CatalogBusiness;
import fr.fms.business.CatalogBusinessImpl;
import fr.fms.entities.Mode;
import fr.fms.entities.Session;

/**
 * Point d'entrée de l'application : menu console du catalogue de formations.
 */
public class App {

    private static final Scanner scanner = new Scanner(System.in);
    private static final CatalogBusiness catalog = new CatalogBusinessImpl();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) { //C'est le point de départ du programme. Quand tu cliques sur Run, Java cherche cette méthode et l'exécute
        System.out.println("=== Bienvenue dans le catalogue de formations ===");
        int choice;
        do {
            displayMenu();
            choice = readInt();
            switch (choice) {
                case 1:
                    showList(catalog.getSessionsSortedByName());
                    break;
                case 2:
                    showList(catalog.getSessionsSortedByDate());
                    break;
                case 3:
                    showList(catalog.getSessionsByMode(Mode.PRESENTIEL));
                    break;
                case 4:
                    showList(catalog.getSessionsByMode(Mode.DISTANCIEL));
                    break;
                case 5:
                    search();
                    break;
                case 0:
                    System.out.println("Au revoir !");
                    break;
                default:
                    System.out.println("Choix invalide, veuillez recommencer.");
            }
        } while (choice != 0);
        scanner.close();
        /* fais ceci, tant que le choix n'est pas 0 ». On affiche le menu, on lit le choix, on l'exécute, 
        puis on recommence. Quand l'utilisateur tape 0, la boucle s'arrête et le programme se termine 
        do ... while plutôt que while parce que le menu doit s'afficher au moins une fois, avant même de connaître le choix
        Le switch : selon la valeur de choice, on va au case correspondant*/
    }

    /** Affiche le menu principal. */
    private static void displayMenu() {
        System.out.println();
        System.out.println("1. Afficher le catalogue (tri par nom)");
        System.out.println("2. Afficher le catalogue (tri par date)");
        System.out.println("3. Afficher seulement le présentiel");
        System.out.println("4. Afficher seulement le distanciel");
        System.out.println("5. Rechercher par mot-clé");
        System.out.println("0. Quitter");
        System.out.print("Votre choix : ");
    }

    /**
     * Affiche une liste numérotée de sessions, puis propose d'en voir le détail.
     * On reste sur la liste jusqu'à ce que l'utilisateur tape 0.
     */
    private static void showList(List<Session> sessions) {
        if (sessions.isEmpty()) {
            System.out.println("Aucune formation à afficher.");
            return;
        }
        int number;
        do {
            System.out.println();
            for (int i = 0; i < sessions.size(); i++) {
                System.out.println((i + 1) + ". " + sessions.get(i));
                //i + 1 car en Java une liste commence à  0 mais pour l utilisateur on préfère compter à partir de 1
                //Chaque ligne s'affiche grâce au toString() de Session
            }
            System.out.print("Numéro d'une formation pour voir son détail (0 pour revenir au menu) : ");
            number = readInt(); // On récupère ce que l'utilisateur a tapé
            if (number >= 1 && number <= sessions.size()) { //on verif que le numero existe, le numéro est au moins 1 ET ne depasse pas le nombre reel de sessions
                Session selected = sessions.get(number - 1);
                showDetail(catalog.getSession(selected.getId())); //on prend le numéro en base de la session choisie, on redemande cette session à la couche business et on affiche son détail
            } else if (number != 0) {
                System.out.println("Numéro invalide.");
            //Java range le premier élément d'une liste à la position 0. Le numéro 2 affiché à l'écran correspond donc à la position 1 pour Java. D'où number - 1
            /*On affiche le détail. On redemande la session à la couche business avec son identifiant, 
            On relit les infos de la session pour avoir des informations à jour, notamment les places restantes */
            }
        } while (number != 0);
        //L'utilisateur tape un numéro pour voir le détail, et revient à la liste ensuite. 0 ramène au menu
    }

    /** Affiche toutes les informations d'une session. */
    private static void showDetail(Session session) {
        if (session == null) { //Tu te souviens que le DAO renvoie null s'il ne trouve rien ? C'est ici qu'on gère ce cas sinon tout planterait qd une session n existe pas
            System.out.println("Cette formation n'est plus disponible.");
            return;
        }
        System.out.println();
        System.out.println("=== " + session.getFormation().getName() + " (" + session.getFormation().getMode() + ") ===");
        System.out.println("Description      : " + session.getFormation().getDescription());
        System.out.println("Durée            : " + session.getFormation().getDurationDays() + " jours");
        System.out.println("Date de début    : " + session.getStartDate().format(DATE_FORMAT));
        System.out.println("Places restantes : " + (session.isFull() ? "COMPLET" : session.getAvailableSeats())); //la session est-elle complète ? Si oui, affiche COMPLET ; sinon, affiche le nombre de places
                                                                                              //Le ? pose la question, et le : sépare les deux réponses possibles.
        System.out.println("Prix             : " + session.getFormation().getPrice() + " € TTC");
        System.out.print("Appuyez sur Entrée pour revenir à la liste...");
        scanner.nextLine(); //Le programme attend que l'utilisateur appuie sur Entrée avant de revenir à la liste
        //on affiche toutes les informations, en allant chercher celles de la formation avec session.getFormation()
    }

    /** Demande un mot-clé et affiche les formations correspondantes. */
    private static void search() {
        System.out.print("Mot-clé : ");
        String keyword = scanner.nextLine();
        List<Session> results = catalog.searchByKeyword(keyword);
        if (results.isEmpty()) {
            System.out.println("Aucune formation ne correspond à votre recherche.");
        } else {
            showList(results);
            //On lit le mot-clé tapé, puis on demande la recherche à la couche business
            //si rien alors on le dit sinon on réutilise showlist
        }
    }

    /** Lit un nombre entier tapé par l'utilisateur, en redemandant tant que ce n'en est pas un et sans planter */
    private static int readInt() {
        while (true) {
            String line = scanner.nextLine().trim(); //nextLine() prend toute la ligne, Entrée compris, Le prochain nextLine() attendra donc vraiment que l'utilisateur tape quelque chose
                               //Mais nextLine() renvoie du texte "5" pas un nombre donc convertion avec Integer.parseInt(...).
            try {
                return Integer.parseInt(line); //Integer.parseInt(line) essaie de la transformer une ligne de texte en nombre
                                                      //Si ça marche, return renvoie le nombre, et la méthode s'arrête
            } catch (NumberFormatException e) { ////Si ça rate, Java lance une erreur NumberFormatException. Le catch l'attrape, affiche un message, et la boucle while (true) recommence : on redemande
                System.out.print("Veuillez entrer un nombre : ");
            }
        }
    }
}