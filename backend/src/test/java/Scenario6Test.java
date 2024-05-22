
import java.util.concurrent.TimeUnit;

import instructif.metier.modele.Demande;
import instructif.metier.modele.Eleve;
import instructif.metier.modele.Intervenant;

// ce scénario teste l'allocation des intervenants
public class Scenario6Test extends AbstractScenarioTest {
    @Override
    public void runTest() {
        // inscription de deux éleves
        System.out.println("\n    INSCRIPTION DE L'ELEVE");
        String codeEtab1 = "0692155T";
        Eleve e1 = new Eleve("Hugo", "Victor", "26/02/1802", 4, "vhugo@paris.fr", "1234");

        service.inscrireEleve(e1, codeEtab1);

        // visio
        System.out.println("\n    ENVOI DE LA DEMANDE");
        Demande demande1 = testEnvoiDemande(e1, (long) 4, "Premiere demande");
        System.out.println("\n    RECUPERATION D L'INTERVENANT + LANCEMENT VISIO INTERVENANT");
        Intervenant intervenant1 = service.connecterIntervenant(demande1.getIntervenant().getLogin());
        service.lancerVisioIntervenant(intervenant1);

        System.out.println("\n    DEBUT DE LA VISIO");
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (Exception ex) {
            System.out.println(ex);
        }

        System.out.println("\n    FIN DE LA VISIO");
        service.raccrocherEleve(e1);
        service.evaluerVisio(e1, 2);
        service.envoyerBilan(intervenant1, "Tres bon travail");

        System.out.println("\n    AFFICHAGE DES STATISTIQUES");
        affichageStatistiques();
        System.out.println("\n    AFFICHAGE HISTORIQUE D'UN ELEVE");
        affichageHistoEleve(e1);
        System.out.println("\n    AFFICHAGE HISTORIQUE D'UN INTERVENANT");
        affichageHistoIntervenant(intervenant1);

    }

}
