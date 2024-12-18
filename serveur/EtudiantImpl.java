package serveur;

import Institue.EtudiantPOA;
import Institue.Epreuve;
import Institue.Livre;
import Institue.LivreListHolder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class EtudiantImpl extends EtudiantPOA {
    private int num ;
    private String nom;
    private String prenom;
    private List<Epreuve> epreuves = new ArrayList<>();
    private List<Livre> livres = new ArrayList<>();
    int nbLivreEmprunte =0;

    public EtudiantImpl(int num,String nom, String prenom){
        this.num=num;
        this.nom=nom;
        this.prenom=prenom;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
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
        this.prenom = prenom;
    }

    @Override
    public void AjouterUneEpreuve() {
    Scanner input = new Scanner(System.in);
    System.out.print("Donner le nom de l'epreuve: ");
    String nom =input.nextLine();
    System.out.print("Donner le note de l'epreuve: ");
    double note = input.nextDouble();
    System.out.println("Donner le coefficient de l'epreuve: ");
    double coefficient = input.nextDouble();

    Epreuve epreuve = new Epreuve(nom,note,coefficient);
    epreuves.add(epreuve);
    input.close();
    }

    @Override
    public String[] ListeDesEpreuves() {
        String[] listeEp =new String[epreuves.size()];
        int cpt =0;
        for (Epreuve ep : epreuves) {
            listeEp[cpt]= ep.afficher();
            cpt+=1;
        }

        return listeEp;
    }

    @Override
    public double CalculerLaMoyenne() {
        double moy=0,som=0;
        for (Epreuve ep : epreuves) {
           som+= ep.note*ep.coefficient;
        }
        moy = som / epreuves.size();
        return moy;
    }

    public Livre EmprunterUnLivre(Livre book,LivreListHolder livresHolder) {

        List<Livre> livresList = new ArrayList<>(Arrays.asList(livresHolder.value));
        if(nbLivreEmprunte>=2){
            System.out.println("vous avez emprunter deja 2 livre");
            return null;
        }
        else {
            for (Livre livre : livresList) {
                if(book.nom==livre.nom){
                    this.livres.add(livre);
                    nbLivreEmprunte+=1;
                    return livre;
                }
             }
        }
                return book;
    }

  


  
}




