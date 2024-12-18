package serveur;
import java.text.DecimalFormat;
import Institue.EtudiantPOA;
import Institue.Epreuve;
import Institue.Livre;
import java.util.ArrayList;
import java.util.List;

public class EtudiantImpl extends EtudiantPOA {
    private int num ;
    private String nom;
    private String prenom;
    private List<Epreuve> epreuves = new ArrayList<>();
    private List<Livre> livres = new ArrayList<>();
    int nbLivreEmprunte =0;

    Livre[] biblio ={new Livre(1, "Les Misérables", "Victor Hugo", "Littérature Française", "1862"),
    new Livre(2, "1984", "George Orwell", "Science-Fiction", "1949"),
    new Livre(3, "Le Petit Prince", "Antoine de Saint-Exupéry", "Jeunesse", "1943"),
    new Livre(4, "La Peste", "Albert Camus", "Philosophie", "1947"),
    new Livre(5, "Don Quichotte", "Miguel de Cervantes", "Classique", "1605"),
    new Livre(6, "Crime et Châtiment", "Fiodor Dostoïevski", "Roman Russe", "1866"),
    new Livre(7, "L'Étranger", "Albert Camus", "Philosophie", "1942")
    

};

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
    public void AjouterUneEpreuve(String nom,double note, double coefficient) {


    Epreuve epreuve = new Epreuve(nom,note,coefficient);
    epreuves.add(epreuve);
    ;
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
    double moy = 0, som = 0, coef = 0;

    // Calcul de la somme pondérée et du total des coefficients
    for (Epreuve ep : epreuves) {
        som += ep.note * ep.coefficient;
        coef += ep.coefficient;
    }

    // Calcul de la moyenne
    moy = som / coef;

    // Création de l'objet DecimalFormat pour limiter à 2 décimales
    DecimalFormat df = new DecimalFormat("#.00");

    // Retourner la moyenne formatée à 2 décimales
    return Double.parseDouble(df.format(moy));
}


    public Livre EmprunterUnLivre(int bookNumber) {

     
        if(nbLivreEmprunte>=2){
            System.out.println("vous avez emprunter deja 2 livre");
            return null;
        }
        else {
            for (Livre livre : biblio) {
                
                if(bookNumber==livre.numero){

                    
                    this.livres.add(livre);
                    nbLivreEmprunte+=1;
                    return livre;
                }
             }
        }
                return  this.livres.get(nbLivreEmprunte-1);
    }
 

  public String toStringIDL(){
    return "num= "+num+" /nom= "+nom+" /prenom= "+prenom;
  }


  
}




