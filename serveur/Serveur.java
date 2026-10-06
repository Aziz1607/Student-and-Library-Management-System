package serveur;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

public class Serveur {
    private static final String DEFAULT_PORT = "900";
    private static Process namingServiceProcess = null;

    public static void main(String[] args) {
        try {
            String port = DEFAULT_PORT;
            for (int i = 0; i < args.length - 1; i++) {
                if (args[i].equalsIgnoreCase("-ORBInitialPort")) {
                    port = args[i + 1];
                }
            }

            // Tentative de démarrage automatique du service de nommage tnameserv
            try {
                System.out.println("Tentative de démarrage de tnameserv sur le port " + port + "...");
                namingServiceProcess = Runtime.getRuntime().exec("tnameserv -ORBInitialPort " + port);

                // Laisser un court délai à tnameserv pour initialiser le socket
                Thread.sleep(1500);

                // Hook pour arrêter le processus lors de l'arrêt du serveur
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    if (namingServiceProcess != null && namingServiceProcess.isAlive()) {
                        namingServiceProcess.destroy();
                        System.out.println("Service de nommage tnameserv arrêté.");
                    }
                }));
            } catch (Exception e) {
                System.out.println("Info : tnameserv n'a pas pu être lancé automatiquement (" + e.getMessage() + ").");
                System.out.println("Connexion au service de nommage existant...");
            }

            // Configuration et initialisation de l'ORB
            String[] orbArgs = (args != null && args.length > 0)
                    ? args
                    : new String[]{"-ORBInitialPort", port, "-ORBInitialHost", "localhost"};

            ORB orb = ORB.init(orbArgs, null);

            // Activation du RootPOA et du POAManager
            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();

            // Création du servant Promotion et obtention de sa référence CORBA
            PromotionImpl promotionImpl = new PromotionImpl();
            org.omg.CORBA.Object ref = rootPOA.servant_to_reference(promotionImpl);

            // Enregistrement de l'objet dans le service de nommage
            org.omg.CORBA.Object nameService = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(nameService);
            NameComponent[] path = ncRef.to_name("Promotion");
            ncRef.rebind(path, ref);

            System.out.println("==================================================");
            System.out.println("  Serveur CORBA démarré avec succès !");
            System.out.println("  Port du service de nommage : " + port);
            System.out.println("  Objet 'Promotion' enregistré dans le NameService.");
            System.out.println("  En attente des requêtes des clients...");
            System.out.println("==================================================");

            // Maintien du serveur en écoute
            orb.run();

        } catch (Exception e) {
            System.err.println("Erreur dans le serveur CORBA : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
