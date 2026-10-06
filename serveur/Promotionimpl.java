package serveur;

import java.util.ArrayList;
import java.util.List;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import Institue.Etudiant;
import Institue.EtudiantHelper;
import Institue.PromotionPOA;

public class PromotionImpl extends PromotionPOA {
    private final List<EtudiantImpl> listEtudiant = new ArrayList<>();

    public PromotionImpl() {
        super();
    }

    @Override
    public void AjouterUnEtudiant(int numero, String nom, String prenom) {
        // Vérification de doublon d'étudiant
        for (EtudiantImpl e : listEtudiant) {
            if (e.getNum() == numero) {
                System.out.println("Avertissement : L'étudiant avec le numéro " + numero + " existe déjà !");
                return;
            }
        }

        EtudiantImpl etd = new EtudiantImpl(numero, nom, prenom);
        listEtudiant.add(etd);
        System.out.println("Étudiant enregistré avec succès : [" + numero + "] " + nom + " " + prenom);
    }

    @Override
    public Etudiant RechercherUnEtudiant(int numero) {
        for (EtudiantImpl etudiante : listEtudiant) {
            if (etudiante.getNum() == numero) {
                try {
                    org.omg.CORBA.Object ref = _poa().servant_to_reference(etudiante);
                    return EtudiantHelper.narrow(ref);
                } catch (ServantNotActive | WrongPolicy e) {
                    System.err.println("Erreur lors de la référence du servant : " + e.getMessage());
                    return null;
                }
            }
        }
        System.out.println("Étudiant avec le numéro " + numero + " introuvable.");
        return null;
    }

    @Override
    public double CalculerMoyenneDeLaPromotion() {
        if (listEtudiant.isEmpty()) {
            return 0.0;
        }

        double somme = 0.0;
        for (EtudiantImpl etudiant : listEtudiant) {
            somme += etudiant.CalculerLaMoyenne();
        }

        double moyenne = somme / listEtudiant.size();
        return Math.round(moyenne * 100.0) / 100.0;
    }
}