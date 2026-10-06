package client;

import java.util.Scanner;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import Institue.Etudiant;
import Institue.Livre;
import Institue.Promotion;
import Institue.PromotionHelper;

public class Client {
    private static final String DEFAULT_PORT = "900";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            String port = DEFAULT_PORT;
            for (int i = 0; i < args.length - 1; i++) {
                if (args[i].equalsIgnoreCase("-ORBInitialPort")) {
                    port = args[i + 1];
                }
            }

            System.out.println("Connexion au service de nommage CORBA sur le port " + port + "...");

            // Configuration et initialisation de l'ORB
            String[] orbArgs = (args != null && args.length > 0)
                    ? args
                    : new String[]{"-ORBInitialPort", port, "-ORBInitialHost", "localhost"};
            ORB orb = ORB.init(orbArgs, null);

            // Récupération du service de nommage
            org.omg.CORBA.Object nameService = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(nameService);

            // Résolution de l'objet distant "Promotion"
            org.omg.CORBA.Object objRef = ncRef.resolve_str("Promotion");
            Promotion promotion = PromotionHelper.narrow(objRef);

            if (promotion == null) {
                System.err.println("Impossible d'obtenir la référence vers l'objet Promotion.");
                return;
            }

            System.out.println("Connecté avec succès au serveur Promotion !\n");

            while (true) {
                afficherMenu();
                int choix = lireEntier(scanner, "Choisissez une option : ");

                switch (choix) {
                    case 1: // Ajouter un étudiant
                        System.out.println("\n--- Ajouter un étudiant ---");
                        int numero = lireEntier(scanner, "Donner le numéro de l'étudiant : ");
                        String nom = lireChaineNonVide(scanner, "Donner le nom de l'étudiant : ");
                        String prenom = lireChaineNonVide(scanner, "Donner le prénom de l'étudiant : ");

                        promotion.AjouterUnEtudiant(numero, nom, prenom);
                        System.out.println("Étudiant ajouté avec succès !");
                        break;

                    case 2: // Ajouter une épreuve à un étudiant
                        System.out.println("\n--- Ajouter une épreuve ---");
                        Etudiant etudiant = rechercherEtudiant(promotion, scanner);
                        if (etudiant != null) {
                            String epreuveNom = lireChaineNonVide(scanner, "Donner le nom de l'épreuve : ");
                            double note = lireDoubleBorne(scanner, "Donner la note de l'épreuve (0 à 20) : ", 0.0, 20.0);
                            double coefficient = lireDoublePositif(scanner, "Donner le coefficient de l'épreuve (> 0) : ");

                            etudiant.AjouterUneEpreuve(epreuveNom, note, coefficient);
                            System.out.println("Épreuve ajoutée avec succès !");
                        }
                        break;

                    case 3: // Liste des épreuves d'un étudiant
                        System.out.println("\n--- Liste des épreuves d'un étudiant ---");
                        etudiant = rechercherEtudiant(promotion, scanner);
                        if (etudiant != null) {
                            String[] epreuves = etudiant.ListeDesEpreuves();
                            if (epreuves == null || epreuves.length == 0) {
                                System.out.println("Aucune épreuve enregistrée pour cet étudiant.");
                            } else {
                                System.out.println("Liste des épreuves (" + epreuves.length + ") :");
                                for (String e : epreuves) {
                                    System.out.println("  - " + e);
                                }
                            }
                        }
                        break;

                    case 4: // Calculer la moyenne d'un étudiant
                        System.out.println("\n--- Moyenne d'un étudiant ---");
                        etudiant = rechercherEtudiant(promotion, scanner);
                        if (etudiant != null) {
                            double moyenne = etudiant.CalculerLaMoyenne();
                            System.out.println("Moyenne générale de l'étudiant : " + moyenne + "/20");
                        }
                        break;

                    case 5: // Calculer la moyenne de la promotion
                        System.out.println("\n--- Moyenne générale de la promotion ---");
                        double moyennePromo = promotion.CalculerMoyenneDeLaPromotion();
                        System.out.println("Moyenne générale de la promotion : " + moyennePromo + "/20");
                        break;

                    case 6: // Rechercher un étudiant
                        System.out.println("\n--- Recherche d'un étudiant ---");
                        int numRech = lireEntier(scanner, "Entrez le numéro de l'étudiant à rechercher : ");
                        Etudiant etudiantRecherche = promotion.RechercherUnEtudiant(numRech);
                        if (etudiantRecherche != null) {
                            System.out.println("Étudiant trouvé : " + etudiantRecherche.toStringIDL());
                        } else {
                            System.out.println("Aucun étudiant ne correspond au numéro " + numRech);
                        }
                        break;

                    case 7: // Emprunter un livre pour un étudiant
                        System.out.println("\n--- Emprunt d'un livre ---");
                        Etudiant etudiantEmprunt = rechercherEtudiant(promotion, scanner);
                        if (etudiantEmprunt != null) {
                            afficherCatalogueLivres();
                            int numLivre = lireEntier(scanner, "Numéro du livre à emprunter : ");

                            Livre livre = etudiantEmprunt.EmprunterUnLivre(numLivre);
                            if (livre == null || livre.numero <= 0) {
                                System.out.println("Échec de l'emprunt : " + (livre != null ? livre.nom : "Erreur inconnue"));
                            } else {
                                System.out.println("Livre emprunté avec succès !");
                                System.out.println("  Numéro      : " + livre.numero);
                                System.out.println("  Titre       : " + livre.nom);
                                System.out.println("  Auteur      : " + livre.auteur);
                                System.out.println("  Collection  : " + livre.collection);
                                System.out.println("  Publication : " + livre.date_publication);
                            }
                        }
                        break;

                    case 0: // Quitter
                        System.out.println("Au revoir !");
                        return;

                    default:
                        System.out.println("Option invalide. Veuillez réessayer.");
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur client CORBA : " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void afficherMenu() {
        System.out.println("\n====================== MENU ======================");
        System.out.println("  1. Ajouter un étudiant");
        System.out.println("  2. Ajouter une épreuve à un étudiant");
        System.out.println("  3. Liste des épreuves d'un étudiant");
        System.out.println("  4. Calculer la moyenne d'un étudiant");
        System.out.println("  5. Calculer la moyenne de la promotion");
        System.out.println("  6. Rechercher un étudiant");
        System.out.println("  7. Emprunter un livre pour un étudiant");
        System.out.println("  0. Quitter");
        System.out.println("==================================================");
    }

    private static void afficherCatalogueLivres() {
        System.out.println("\nLivres disponibles dans la bibliothèque :");
        System.out.println("  [1] Les Misérables (Victor Hugo, 1862) - Littérature Française");
        System.out.println("  [2] 1984 (George Orwell, 1949) - Science-Fiction");
        System.out.println("  [3] Le Petit Prince (Antoine de Saint-Exupéry, 1943) - Jeunesse");
        System.out.println("  [4] La Peste (Albert Camus, 1947) - Philosophie");
        System.out.println("  [5] Don Quichotte (Miguel de Cervantes, 1605) - Classique");
        System.out.println("  [6] Crime et Châtiment (Fiodor Dostoïevski, 1866) - Roman Russe");
        System.out.println("  [7] L'Étranger (Albert Camus, 1942) - Philosophie");
    }

    private static Etudiant rechercherEtudiant(Promotion promotion, Scanner scanner) {
        int numero = lireEntier(scanner, "Entrez le numéro de l'étudiant : ");
        Etudiant etudiant = promotion.RechercherUnEtudiant(numero);
        if (etudiant == null) {
            System.out.println("Étudiant non trouvé avec le numéro " + numero + ".");
        }
        return etudiant;
    }

    private static int lireEntier(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String ligne = scanner.nextLine().trim();
            try {
                return Integer.parseInt(ligne);
            } catch (NumberFormatException e) {
                System.out.println("Entrée invalide. Veuillez saisir un nombre entier.");
            }
        }
    }

    private static double lireDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String ligne = scanner.nextLine().trim().replace(',', '.');
            try {
                return Double.parseDouble(ligne);
            } catch (NumberFormatException e) {
                System.out.println("Entrée invalide. Veuillez saisir un nombre décimal (ex: 14.5).");
            }
        }
    }

    private static double lireDoubleBorne(Scanner scanner, String prompt, double min, double max) {
        while (true) {
            double val = lireDouble(scanner, prompt);
            if (val >= min && val <= max) {
                return val;
            }
            System.out.println("La valeur doit être comprise entre " + min + " et " + max + ".");
        }
    }

    private static double lireDoublePositif(Scanner scanner, String prompt) {
        while (true) {
            double val = lireDouble(scanner, prompt);
            if (val > 0.0) {
                return val;
            }
            System.out.println("La valeur doit être strictement positive (> 0).");
        }
    }

    private static String lireChaineNonVide(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String ligne = scanner.nextLine().trim();
            if (!ligne.isEmpty()) {
                return ligne;
            }
            System.out.println("Ce champ ne peut pas être vide. Veuillez saisir une valeur.");
        }
    }
}
