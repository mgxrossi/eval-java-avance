package fr.fms.app;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import fr.fms.business.CartBusiness;
import fr.fms.business.CartBusinessImpl;
import fr.fms.business.CartException;
import fr.fms.business.CatalogBusiness;
import fr.fms.business.CatalogBusinessImpl;
import fr.fms.entities.Cart;
import fr.fms.entities.Mode;
import fr.fms.entities.Session;

/**
 * Point d'entrée : menu console du catalogue (couche application).
 * <p>Affiche, lit les saisies, affiche les résultats. Aucune règle métier, aucun SQL :
 * tout passe par la business. Seule couche à remplacer pour passer au web.</p>
 * <p>Tout est static car main est lancé sans créer d'objet App.</p>
 * <p>P1 : catalogue trié (nom/date), filtre par mode, recherche, détail.
 * P2 (en cours) : ajout au panier, panier + total, retrait.</p>
 */
public class App {

    /** Lit le clavier (= input() en Python). Créé 1 fois, partagé. */
    private static final Scanner scanner = new Scanner(System.in);

    /** Business du catalogue. Typé par l'interface : le menu sait "quoi", pas "comment". */
    private static final CatalogBusiness catalog = new CatalogBusinessImpl();

    /**
     * Business du panier. Créé 1 seule fois au démarrage → un seul panier
     * pour toute la session. En mémoire : vidé quand on quitte le programme.
     */
    private static final CartBusiness cartBusiness = new CartBusinessImpl();

    /** Format FR des dates : 12/10/2026 au lieu de 2026-10-12. */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Départ du programme (lancé par Run).
     * <p>do...while : menu affiché au moins 1 fois, puis répété tant que choix ≠ 0.
     * switch : oriente chaque choix ; break = sortir du switch ; default = autre chiffre.</p>
     *
     * @param args non utilisés
     */
    public static void main(String[] args) {
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
                    // Le code écrit le mode, pas l'utilisateur : pas de faute possible
                    showList(catalog.getSessionsByMode(Mode.PRESENTIEL));
                    break;
                case 4:
                    showList(catalog.getSessionsByMode(Mode.DISTANCIEL));
                    break;
                case 5:
                    search();
                    break;
                case 6:
                    showCart();
                    break;
                case 0:
                    System.out.println("Au revoir !");
                    break;
                default:
                    System.out.println("Choix invalide, veuillez recommencer.");
            }
        } while (choice != 0);
        scanner.close(); // on ferme le clavier, comme les requêtes dans le DAO
    }

    /** Affiche le menu (méthode à part pour garder main lisible). */
    private static void displayMenu() {
        System.out.println();
        System.out.println("1. Afficher le catalogue (tri par nom)");
        System.out.println("2. Afficher le catalogue (tri par date)");
        System.out.println("3. Afficher seulement le présentiel");
        System.out.println("4. Afficher seulement le distanciel");
        System.out.println("5. Rechercher par mot-clé");
        System.out.println("6. Voir mon panier");
        System.out.println("0. Quitter");
        System.out.print("Votre choix : ");
    }

    /**
     * Affiche une liste numérotée puis propose le détail. Réutilisée par les choix 1 à 4 et la recherche.
     * <ul>
     *   <li>Liste vide → message + return.</li>
     *   <li>Affichage i + 1 : Java compte dès 0, l'humain dès 1. Ligne = toString() de Session.</li>
     *   <li>Numéro valide → session = get(number - 1), relue en BDD par son id pour avoir les places à jour.</li>
     *   <li>Boucle : retour à la liste après chaque détail ; 0 = menu ; sinon "Numéro invalide".</li>
     * </ul>
     *
     * @param sessions sessions déjà triées/filtrées par la business
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
            }
            System.out.print("Numéro d'une formation pour voir son détail (0 pour revenir au menu) : ");
            number = readInt();
            if (number >= 1 && number <= sessions.size()) {
                Session selected = sessions.get(number - 1);
                showDetail(catalog.getSession(selected.getId()));
            } else if (number != 0) {
                System.out.println("Numéro invalide.");
            }
        } while (number != 0);
    }

    /**
     * Affiche le détail d'une session et propose l'ajout au panier (demande du client).
     * <ul>
     *   <li>null (introuvable en BDD) → message, pour ne pas planter.</li>
     *   <li>Infos de la formation via session.getFormation().</li>
     *   <li>(cond ? A : B) = "si... sinon" en 1 ligne : COMPLET ou nb de places.</li>
     *   <li>1 = ajouter ; tout autre choix = retour auto à la liste.</li>
     * </ul>
     *
     * @param session session à afficher (peut être null)
     */
    private static void showDetail(Session session) {
        if (session == null) {
            System.out.println("Cette formation n'est plus disponible.");
            return;
        }
        System.out.println();
        System.out.println("=== " + session.getFormation().getName() + " (" + session.getFormation().getMode() + ") ===");
        System.out.println("Description      : " + session.getFormation().getDescription());
        System.out.println("Durée            : " + session.getFormation().getDurationDays() + " jours");
        System.out.println("Date de début    : " + session.getStartDate().format(DATE_FORMAT));
        System.out.println("Places restantes : " + (session.isFull() ? "COMPLET" : session.getAvailableSeats()));
        System.out.println("Prix             : " + session.getFormation().getPrice() + " euros TTC");
        System.out.println();
        System.out.println("1. Ajouter au panier");
        System.out.println("0. Retour à la liste");
        System.out.print("Votre choix : ");
        if (readInt() == 1) {
    System.out.println("CONTROLE 1 : choix 1 bien lu");
            addToCart(session);
        }
    }

    /**
     * Essaie d'ajouter au panier et informe l'utilisateur.
     * <ul>
     *   <li>OK → confirmation.</li>
     *   <li>Règle bloquée (complète = RG3, doublon = RG4) → la business lance une CartException,
     *       on saute dans le catch et on affiche e.getMessage().</li>
     * </ul>
     * <p>try...catch obligatoire : l'interface déclare "throws CartException".
     * Résultat : jamais de plantage, et la business n'affiche jamais rien.</p>
     *
     * @param session session à ajouter
     */
    private static void addToCart(Session session) {
        try {
            System.out.println("CONTROLE 2 : appel de la business");
            cartBusiness.addToCart(session);
            System.out.println("Formation ajoutée au panier.");
        } catch (CartException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Affiche le panier + total, et permet de retirer une formation.
     * <ul>
     *   <li>Panier relu à chaque tour (il change après un retrait).</li>
     *   <li>Vide → message + retour menu (rien ajouté, ou dernier élément retiré).</li>
     *   <li>Total en bas (demande du client), calculé par Cart.getTotal().</li>
     *   <li>Numéro valide → retrait via get(number - 1) ; 0 = menu ; sinon "Numéro invalide".</li>
     * </ul>
     * <p>Pas de try...catch : retirer ne peut pas échouer (pas de "throws").</p>
     */
    private static void showCart() {
        int number;
        do {
            Cart cart = cartBusiness.getCart();
            System.out.println();
            if (cart.isEmpty()) {
                System.out.println("Votre panier est vide.");
                return;
            }
            System.out.println("=== Votre panier ===");
            List<Session> sessions = cart.getSessions();
            for (int i = 0; i < sessions.size(); i++) {
                System.out.println((i + 1) + ". " + sessions.get(i));
            }
            System.out.println("Total : " + cart.getTotal() + " euros TTC");
            System.out.print("Numéro d'une formation à retirer (0 pour revenir au menu) : ");
            number = readInt();
            if (number >= 1 && number <= sessions.size()) {
                cartBusiness.removeFromCart(sessions.get(number - 1));
                System.out.println("Formation retirée du panier.");
            } else if (number != 0) {
                System.out.println("Numéro invalide.");
            }
        } while (number != 0);
    }

    /**
     * Recherche par mot-clé dans le nom et la description.
     * <p>La business refuse un mot vide et retire les espaces ; la BDD ignore
     * majuscules et accents ("developpeur" trouve "Développeur").
     * Aucun résultat → message exact du client ; sinon réutilise showList.</p>
     */
    private static void search() {
        System.out.print("Mot-clé : ");
        String keyword = scanner.nextLine();
        List<Session> results = catalog.searchByKeyword(keyword);
        if (results.isEmpty()) {
            System.out.println("Aucune formation ne correspond à votre recherche.");
        } else {
            showList(results);
        }
    }

    /**
     * Lit un entier en redemandant tant que la saisie n'en est pas un (ex. "abc").
     * <ul>
     *   <li>Lit la ligne entière (trim = retire les espaces autour).</li>
     *   <li>parseInt réussit → return (seule sortie de la boucle).</li>
     *   <li>Échec → NumberFormatException attrapée, message, while (true) recommence.</li>
     * </ul>
     * <p>nextLine() et pas nextInt() : nextInt() laisse la touche Entrée dans le buffer,
     * et le nextLine() suivant renverrait une ligne vide sans attendre l'utilisateur.</p>
     *
     * @return l'entier saisi
     */
    private static int readInt() {
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Veuillez entrer un nombre : ");
            }
        }
    }
}