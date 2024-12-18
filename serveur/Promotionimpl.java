package serveur;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Institue.Etudiant;
import Institue.PromotionPOA;

public class Promotionimpl  extends PromotionPOA {
    List<EtudiantImpl> listEtudiant = new ArrayList<>();
    protected Promotionimpl()  {
        super();
    }


    @Override
    public void AjouterUnEtudiant() {
        Scanner input = new Scanner(System.in);
        System.out.println("Donner le numero de l'etudiant: ");
        int numero = input.nextInt();
        System.out.print("Donner le nom de l'etudiant: ");
        String nom =input.nextLine();
        System.out.print("Donner le prenom de l'etudiant: ");
        String prenom = input.nextLine();
    

        EtudiantImpl etd =  new EtudiantImpl(numero,nom,prenom);

        listEtudiant.add(etd);
        input.close();
    }

    @Override
    public Etudiant RechercherUnEtudiant(int numero) {
        for (EtudiantImpl etudiante : listEtudiant) {
            if(etudiante.getNum()==numero){
                return (Etudiant) etudiante;
            }
        }
        System.out.println("etudiant non trouvee");
        return null;
    }

    @Override
    public double CalculerMoyenneDeLaPromotion() {
        double somme=0,moyenne;

        for (EtudiantImpl etudiant : listEtudiant) {
           somme+=etudiant.CalculerLaMoyenne();
    }
    moyenne = somme/ listEtudiant.size();
    return moyenne;
    }
}