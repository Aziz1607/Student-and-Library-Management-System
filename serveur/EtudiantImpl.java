package serveur;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import Institue.Epreuve;
import Institue.EtudiantPOA;
import Institue.Livre;

public class EtudiantImpl extends EtudiantPOA {
    private int num;
    private String nom;
    private String prenom;
    private final List<Epreuve> epreuves = new ArrayList<>();
    private final List<Livre> livres = new ArrayList<>();

    // Catalogue partagé des livres disponibles dans la bibliothèque
    public static final Livre[] BIBLIOTHEQUE = {
        new Livre(1, "Les Misérables", "Victor Hugo", "Littérature Française", "1862"),
        new Livre(2, "1984", "George Orwell", "Science-Fiction", "1949"),
        new Livre(3, "Le Petit Prince", "Antoine de Saint-Exupéry", "Jeunesse", "1943"),
        new Livre(4, "La Peste", "Albert Camus", "Philosophie", "1947"),
        new Livre(5, "Don Quichotte", "Miguel de Cervantes", "Classique", "1605"),
        new Livre(6, "Crime et Châtiment", "Fiodor Dostoïevski", "Roman Russe", "1866"),
        new Livre(7, "L'Étranger", "Albert Camus", "Philosophie", "1942")
    };

    public EtudiantImpl(int num, String nom, String prenom) {
        this.num = num;
        this.nom = (nom != null) ? nom.trim() : "";
        this.prenom = (prenom != null) ? prenom.trim() : "";
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = (nom != null) ? nom.trim() : "";
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = (prenom != null) ? prenom.trim() : "";
    }

    public int getNombreEpreuves() {
        return epreuves.size();
    }

    public List<Livre> getLivresEmpruntes() {
        return Collections.unmodifiableList(livres);
    }

    @Override
    public void AjouterUneEpreuve(String nom, double note, double coefficient) {
        String nomEpreuve = (nom != null && !nom.trim().isEmpty()) ? nom.trim() : "Épreuve sans nom";
        double noteValidee = Math.max(0.0, Math.min(20.0, note));
        double coeffValide = (coefficient > 0.0) ? coefficient : 1.0;

        Epreuve epreuve = new Epreuve(nomEpreuve, noteValidee, coeffValide);
        epreuves.add(epreuve);
        System.out.println("Épreuve ajoutée pour l'étudiant [" + num + " " + this.nom + "] : "
                + nomEpreuve + " (Note: " + noteValidee + "/20, Coeff: " + coeffValide + ")");
    }

    @Override
    public String[] ListeDesEpreuves() {
        String[] listeEp = new String[epreuves.size()];
        for (int i = 0; i < epreuves.size(); i++) {
            Epreuve ep = epreuves.get(i);
            listeEp[i] = "Matière : " + ep.nom + " | Note : " + ep.note + "/20 | Coeff : " + ep.coefficient;
        }
        return listeEp;
    }

    @Override
    public double CalculerLaMoyenne() {
        if (epreuves.isEmpty()) {
            return 0.0;
        }

        double som = 0.0;
        double coefTotal = 0.0;

        for (Epreuve ep : epreuves) {
            som += ep.note * ep.coefficient;
            coefTotal += ep.coefficient;
        }

        if (coefTotal <= 0.0) {
            return 0.0;
        }

        double moy = som / coefTotal;
        // Arrondi sécurisé à 2 décimales indépendant de la Locale
        return Math.round(moy * 100.0) / 100.0;
    }

    @Override
    public Livre EmprunterUnLivre(int bookNumber) {
        // Règle 1 : Limite de 2 livres par étudiant
        if (livres.size() >= 2) {
            System.out.println("Refus d'emprunt pour l'étudiant [" + num + "] : quota de 2 livres déjà atteint.");
            return new Livre(0, "Quota de 2 livres déjà atteint", "", "", "");
        }

        // Règle 2 : Vérifier si l'étudiant n'a pas déjà emprunté ce livre
        for (Livre dejaEmprunte : livres) {
            if (dejaEmprunte.numero == bookNumber) {
                System.out.println("Refus d'emprunt pour l'étudiant [" + num + "] : livre déjà emprunté.");
                return new Livre(-1, "Livre déjà emprunté par cet étudiant", "", "", "");
            }
        }

        // Règle 3 : Chercher le livre dans le catalogue de la bibliothèque
        for (Livre livre : BIBLIOTHEQUE) {
            if (livre.numero == bookNumber) {
                this.livres.add(livre);
                System.out.println("Livre emprunté avec succès par l'étudiant [" + num + "] : " + livre.nom);
                return livre;
            }
        }

        // Livre introuvable
        System.out.println("Refus d'emprunt pour l'étudiant [" + num + "] : livre " + bookNumber + " introuvable.");
        return new Livre(-2, "Livre introuvable dans la bibliothèque", "", "", "");
    }

    @Override
    public String toStringIDL() {
        return "Numéro: " + num + " | Nom: " + nom + " | Prénom: " + prenom;
    }
}
