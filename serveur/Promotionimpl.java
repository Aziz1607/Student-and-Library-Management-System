package serveur;


import java.util.ArrayList;
import java.util.List;

import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import Institue.Etudiant;
import Institue.EtudiantHelper;
import Institue.PromotionPOA;

public class Promotionimpl  extends PromotionPOA {
    List<EtudiantImpl> listEtudiant = new ArrayList<>();
    protected Promotionimpl()  {
        super();
    }


    @Override
    public void AjouterUnEtudiant(int numero, String nom,String prenom) {
    
    

        EtudiantImpl etd =  new EtudiantImpl(numero,nom,prenom);
        System.out.println("numero de etudiant à enrigistrer : "+etd.getNum());
        listEtudiant.add(etd);
       
    }

    @Override

   

public Etudiant RechercherUnEtudiant(int numero) {
    for (EtudiantImpl etudiante : listEtudiant) {
        if (etudiante.getNum() == numero) {
            try {
                org.omg.CORBA.Object ref = _poa().servant_to_reference(etudiante);
                return EtudiantHelper.narrow(ref);
            } catch (ServantNotActive | WrongPolicy e) {
                System.out.println("Erreur lors de la référence du servant : " + e.getMessage());
            }
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