package client;

import Institue.Etudiant;
import Institue.Livre;
import Institue.Promotion;
import Institue.PromotionHelper;
import org.omg.CORBA.*;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            System.out.println("Service de nommage démarré sur le port 900...");

            // Configuration ORB
            String[] orbArgs = {"-ORBInitialPort", "900"};
            ORB orb = ORB.init(orbArgs, null);

            org.omg.CORBA.Object nameService = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(nameService);

            org.omg.CORBA.Object objRef = ncRef.resolve_str("Promotion");
            Promotion promotion = PromotionHelper.narrow(objRef);

            // Scanner pour les entrées utilisateur
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\n------------------- Menu -------------------");
                System.out.println("1. Ajouter un etudiant");
                System.out.println("2. Ajouter une epreuve a un etudiant");
                System.out.println("3. Liste des epreuves d'un etudiant");
                System.out.println("4. Calculer la moyenne d'un etudiant");
                System.out.println("5. Calculer la moyenne de la promotion");
                System.out.println("6. Rechercher un etudiant");
                System.out.println("7. Emprunter un livre pour un etudiant");
                System.out.println("0. Quitter");
                System.out.print("Choisissez une option : ");

                int choix = scanner.nextInt();
                scanner.nextLine();

                switch (choix) {
                    case 1: // Ajouter un étudiant
                        System.out.println("Donner le numero de l'etudiant: ");
                        int numero = scanner.nextInt();
                        scanner.nextLine();

                        System.out.println("Donner le nom de l'etudiant: ");
                        String nom = scanner.nextLine();

                        System.out.println("Donner le prenom de l'etudiant: ");
                        String prenom = scanner.nextLine();

                        promotion.AjouterUnEtudiant(numero, nom, prenom);
                        System.out.println("Étudiant ajouté avec succès !");
                        break;

                    case 2: // Ajouter une épreuve à un étudiant
                        Etudiant etudiant = rechercherEtudiant(promotion, scanner);
                        if (etudiant != null) {
                            System.out.print("Donner le nom de l'epreuve: ");
                            String epreuveNom = scanner.nextLine();

                            System.out.print("Donner la note de l'epreuve: ");
                            double note = scanner.nextDouble();

                            System.out.print("Donner le coefficient de l'epreuve: ");
                            double coefficient = scanner.nextDouble();

                            etudiant.AjouterUneEpreuve(epreuveNom, note, coefficient);
                            System.out.println("Epreuve ajoutée avec succès !");
                        }
                        break;

                    case 3: // Liste des épreuves d'un étudiant
                        etudiant = rechercherEtudiant(promotion, scanner);
                        if (etudiant != null) {
                            String[] epreuves = etudiant.ListeDesEpreuves();
                            System.out.println("Liste des épreuves :");
                            for (String e : epreuves) {
                                System.out.println("- " + e);
                            }
                        }
                        break;

                    case 4: // Calculer la moyenne d'un étudiant
                        etudiant = rechercherEtudiant(promotion, scanner);
                        if (etudiant != null) {
                            double moyenne = etudiant.CalculerLaMoyenne();
                            System.out.println("Moyenne générale : " + moyenne);
                        }
                        break;

                    case 5: // Calculer la moyenne de la promotion
                        double moyennePromo = promotion.CalculerMoyenneDeLaPromotion();
                        System.out.println("Moyenne de la promotion : " + moyennePromo);
                        break;

                    case 6: // Rechercher un étudiant
                        System.out.print("Entrez le numéro de l'étudiant à rechercher : ");
                        int numeroRecherche = scanner.nextInt();
                        scanner.nextLine();

                        Etudiant etudiantRecherche = promotion.RechercherUnEtudiant(numeroRecherche);
                        if (etudiantRecherche != null) {
                            System.out.println("Étudiant trouvé : " + etudiantRecherche.toStringIDL());
                        } else {
                            System.out.println("Étudiant non trouvé.");
                        }
                        break;

                    case 7: // Emprunter un livre pour un étudiant
                        System.out.print("Entrez le numéro de l'étudiant : ");
                        int numEtudiant = scanner.nextInt();
                        scanner.nextLine();

                        Etudiant etudiantRech = promotion.RechercherUnEtudiant(numEtudiant);
                        if (etudiantRech != null) {
                            System.out.print("Numéro du livre à emprunter : ");
                            int numLivre = scanner.nextInt();

                            Livre livre = etudiantRech.EmprunterUnLivre(numLivre);
                            System.out.println("Livre emprunté : nom = " + livre.nom + ", auteur = " + livre.auteur +
                                    ", date_publication = " + livre.date_publication + ", collection = " + livre.collection);
                        }
                        break;

                    case 0: // Quitter
                        System.out.println("Au revoir !");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Option invalide. Veuillez réessayer.");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour rechercher un étudiant
    private static Etudiant rechercherEtudiant(Promotion promotion, Scanner scanner) {
        System.out.print("Entrez le numéro de l'étudiant : ");
        int numero = scanner.nextInt();
        scanner.nextLine(); // Consommer la ligne restante

        Etudiant etudiant = promotion.RechercherUnEtudiant(numero);
        if (etudiant == null) {
            System.out.println("Étudiant non trouvé.");
        }
        return etudiant;
    }
}
