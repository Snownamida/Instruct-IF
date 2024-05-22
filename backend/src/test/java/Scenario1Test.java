
import java.util.concurrent.TimeUnit;

import instructif.metier.modele.Demande;
import instructif.metier.modele.Eleve;
import instructif.metier.modele.Intervenant;

// ce scénario regroupe beaucoup de testsen tout genre
public class Scenario1Test extends AbstractScenarioTest {
    @Override
    public void runTest() {

        // inscription de trois éleves
        System.out.println("\n    INSCRIPTION DE TROIS ELEVES");
        String codeEtab1 = "0692155T"; // pour e1 et e2, college
        String codeEtab3 = "0690132U"; // pour e3 et e4, lycee
        Eleve e1 = new Eleve("Hugo", "Victor", "26/02/1802", 4, "vhugo@paris.fr", "1234");
        Eleve e2 = new Eleve("Zola", "Emile", "02/04/1840", 4, "ezola@paris.fr", "1234");
        Eleve e3 = new Eleve("Sand", "George", "01/07/1804", 1, "amantine@paris.fr", "1234");
        Eleve e4 = new Eleve("Hugo", "Victor", "26/02/1802", 2, "vhugo@paris.fr", "1234"); // inscription avec la même
                                                                                           // adresse mail

        service.inscrireEleve(e1, codeEtab1);
        service.inscrireEleve(e2, codeEtab1);
        service.inscrireEleve(e3, codeEtab3);
        service.inscrireEleve(e4, codeEtab3); // echec de l'inscription

        // connexion des élèves
        System.out.println("\n    CONNEXION ELEVES ( première est un échec)");
        Eleve e = service.connecterEleve("vhugo@paris.fr", "mauvais"); // mauvais mot de passe
        verificationConnexionEleve(e);
        e1 = service.connecterEleve("vhugo@paris.fr", "1234");
        verificationConnexionEleve(e1);
        e2 = service.connecterEleve("ezola@paris.fr", "1234");
        verificationConnexionEleve(e2);
        e3 = service.connecterEleve("amantine@paris.fr", "1234");
        verificationConnexionEleve(e3);

        // envoi de demandes de cours
        System.out.println("\n    ENVOI DEMANDES DE COURS");
        long id = 4;
        Demande demande1 = testEnvoiDemande(e1, id, "J'ai besoin d'une lecon svp");
        System.out.println("\n  A DEJA UNE VISIO");
        testEnvoiDemande(e1, id, "J'ai besoin d'une lecon svp");
        id = 5;
        Demande demande2 = testEnvoiDemande(e2, id, "Bonjour, j'ai besoin d'une lecon svp, merci.");
        id = 5;
        System.out.println("\n  INTERVENANT NON DISPO");
        testEnvoiDemande(e3, id, "Je suis en première.");

        // connexion des intervenants qui doivent intervenir dans les demandes
        System.out.println("\n    CONNEXION INTERVENANTS");
        Intervenant intervenant1 = service.connecterIntervenant(demande1.getIntervenant().getLogin());
        verificationConnexionIntervenant(intervenant1);
        Intervenant intervenant2 = service.connecterIntervenant(demande2.getIntervenant().getLogin());
        verificationConnexionIntervenant(intervenant2);

        // visio numéro 1 : tout se passe bien
        System.out.println("\n    VISIO 1");
        service.lancerVisioIntervenant(intervenant1);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        service.raccrocherEleve(e1);
        service.evaluerVisio(e1, 2);
        service.envoyerBilan(intervenant1, "Tres bon travail");

        // visio numéro 2 :
        System.out.println("\n    VISIO 2");
        service.lancerVisioIntervenant(intervenant2);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        service.raccrocherEleve(e2);
        service.evaluerVisio(e2, 2);
        System.out.println("\n  ENVOI DEMANDE ALORS QUE LE COURS N'EST PAS ENCORE CONCLU");
        testEnvoiDemande(e2, id, "J'ai encore besoin d'une lecon svp");
        service.envoyerBilan(intervenant2, "Tres bon travail");

        // affichage d'informations
        System.out.println("\n    AFFICHAGE DES STATISTIQUES");
        affichageStatistiques();
        System.out.println("\n    AFFICHAGE HISTORIQUE D'UN ELEVE");
        affichageHistoEleve(e1);
        System.out.println("\n    AFFICHAGE HISTORIQUE D'UN INTERVENANT");
        affichageHistoIntervenant(intervenant1);
        System.out.println("\n    AFFICHAGE ETABLISSEMENTS");
        affichageEtablissements();
        System.out.println("\n    AFFICHAGE MATIERES");
        affichageMatieres();

    }

    public static void verificationConnexionEleve(Eleve e) {
        if (e == null) {
            System.out.println("Connexion échouée : checker le mail ou le mot de passe");
        } else {
            System.out.println("Connexion réussie : " + e + " connecte"); // Pas d'affichage dans les servces et les DAO
                                                                          // !
        }
    }

    public static void verificationConnexionIntervenant(Intervenant intervenant) {
        if (intervenant == null) {
            System.out.println("Connexion intervenant échouée : checker le login");
        } else {
            System.out.println(intervenant);
        }
    }

}
