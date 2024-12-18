package serveur;

import org.omg.CORBA.*;
import org.omg.CosNaming.*;

import serveur.EtudiantImpl;
import serveur.Promotionimpl;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

public class Serveur {
    public static void main(String[] args) {
        try {
            // Lancement du service de nommage (tnameserv) sur le port 900
            Process namingService = Runtime.getRuntime().exec("tnameserv -ORBInitialPort 900");
            System.out.println("Service de nommage démarré sur le port 900...");

            // Configuration ORB
            String[] orbArgs = {"-ORBInitialPort", "900"};
            ORB orb = ORB.init(orbArgs, null);

            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();

            Promotionimpl promotionImpl = new Promotionimpl();
            org.omg.CORBA.Object ref = rootPOA.servant_to_reference(promotionImpl);

            // Log pour vérifier que le serveur est bien lancé
            System.out.println("Serveur CORBA prêt...");

            org.omg.CORBA.Object nameService = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(nameService);
            NameComponent[] path = ncRef.to_name("Promotion");
            ncRef.rebind(path, ref);

            orb.run();


        } catch (Exception e) {
            System.err.println("Erreur dans le serveur CORBA : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
