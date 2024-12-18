package serveur;


import java.util.ArrayList;
import java.util.List;

import Institue.Etudiant;
import Institue.PromotionPOA;

public class Promotionimpl  extends PromotionPOA {
    List<EtudiantImpl> listEtudiant = new ArrayList<>();
    protected Promotionimpl()  {
        super();
    }


    @Override
    public void AjouterUnEtudiant(int numero, String nom,String prenom) {
    
    

        EtudiantImpl etd =  new EtudiantImpl(numero,nom,prenom);

        listEtudiant.add(etd);
       
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