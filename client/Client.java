package client;

import serveur.EtudiantImpl;
import Institue.Etudiant;
import Institue.Promotion;
import Institue.PromotionHelper;
import org.omg.CORBA.*;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
           // ORB orb = ORB.init(args, null);
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
                System.out.println("\n=== Menu ===");
                System.out.println("1. Ajouter une epreuve a un etudiant");
                System.out.println("2. Liste des epreuves d'un etudiant");
                System.out.println("3. Calculer la moyenne d'un etudiant");
                System.out.println("4. Emprunter un livre pour un etudiant");
                System.out.println("5. Ajouter un etudiant");
                System.out.println("6. Rechercher un etudiant");
                System.out.println("7. Calculer la moyenne de la promotion");
                System.out.println("0. Quitter");
                System.out.print("Choisissez une option : ");

                int choix = scanner.nextInt();
                scanner.nextLine();

                switch (choix) {
                    case 1: // Ajouter une épreuve à un étudiant
                        //Etudiant etudiant = rechercherEtudiant(promotion, scanner);
                        System.out.print("Tapez le numero d etudiant ");
                        int nume = scanner.nextInt();
                        Etudiant etudiant = promotion.RechercherUnEtudiant(nume);

                        if (etudiant != null) {
                         
                            etudiant.AjouterUneEpreuve();
                            System.out.println("Epreuve ajoutée avec succès !");
                        }
                        break;

                    case 2: // Liste des épreuves d'un étudiant
                        etudiant = rechercherEtudiant(promotion, scanner);
                        if (etudiant != null) {
                            String[] epreuves = etudiant.Liste_des_epreuves();
                            System.out.println(epreuves.length);
                            System.out.println("Liste des épreuves :");
                            for (String e : epreuves) {
                                System.out.println("- " + e);
                            }
                        }
                        break;

                    case 3: // Calculer la moyenne d'un étudiant
                        etudiant = rechercherEtudiant(promotion, scanner);
                        if (etudiant != null) {
                            float moyenne = etudiant.Calculer_la_moyenne();
                            System.out.println("Moyenne generale : " + moyenne);
                        }
                        break;

                    case 4: // Emprunter un livre pour un étudiant
                        System.out.print("Entrez le numéro de l'étudiant : ");
                        int numeroRecherche = scanner.nextInt();
                        scanner.nextLine(); // Consommer la ligne
                        Etudiant etudiantRech = promotion.Rechercher_un_etudiant( numeroRecherche);

                        if (etudiantRech != null) {
                            System.out.print("Num du livre a emprunte: ");

                            int num = scanner.nextInt();
                            System.out.println(etudiantRech.Emprunter_un_livre(num));
                            //System.out.println("Livre emprunté avec succès !");
                        }
                        break;

                    case 5: // Ajouter un étudiant
                        System.out.print("Nom : ");
                        String nomEtudiant = scanner.nextLine();
                        System.out.print("Prénom : ");
                        String prenom = scanner.nextLine();
                        System.out.print("Numéro : ");
                        long numero = scanner.nextLong();
                        promotion.Ajouter_un_etudiant(nomEtudiant, prenom, (int) numero);
                        System.out.println("Étudiant ajouté avec succès !");
                        break;

                    case 6:
                        System.out.print("Entrez le numéro de l'étudiant à rechercher : ");
                        numeroRecherche = scanner.nextInt();
                        scanner.nextLine(); // Consommer la ligne
                        Etudiant etudiantRecherche = promotion.Rechercher_un_etudiant( numeroRecherche);
                        if (etudiantRecherche != null) {

                            System.out.println("Étudiant trouvé : " + etudiantRecherche.toStringIDL());
                        } else {
                            System.out.println("Étudiant non trouvé.");
                        }
                        break;

                    case 7: // Calculer la moyenne de la promotion
                        float moyennePromo = promotion.Calculer_moyenne_de_la_promotion();
                        System.out.println("Moyenne de la promotion : " + moyennePromo);
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
        long numero = scanner.nextLong();
        scanner.nextLine(); // Consommer la ligne
        Etudiant etudiant = promotion.Rechercher_un_etudiant((int) numero);
        if (etudiant == null) {
            System.out.println("Étudiant non trouvé.");
        }
        return etudiant;
    }
}