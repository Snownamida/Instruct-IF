
import java.util.concurrent.TimeUnit;

import instructif.metier.modele.Demande;
import instructif.metier.modele.Eleve;
import instructif.metier.modele.Intervenant;

// ce scénario teste l'allocation des intervenants
public class Scenario5Test extends AbstractScenarioTest {
    @Override
    public void runTest() {
        // inscription de deux éleves
        System.out.println("\n    INSCRIPTION DE DEUX ELEVES");
        String codeEtab1 = "0692155T";
        Eleve e1 = new Eleve("Hugo", "Victor", "26/02/1802", 4, "vhugo@paris.fr", "1234");
        Eleve e2 = new Eleve("Berger", "Gaston", "01/07/1804", 4, "gberger@paris.fr", "1234");

        service.inscrireEleve(e1, codeEtab1);
        service.inscrireEleve(e2, codeEtab1);

        // visio
        Intervenant intervenant1 = null;

        System.out.println("\n    CINQ VISIO SUCCESSIVES");
        for (int i = 0; i < 5; i++) {
            Demande demande1 = testEnvoiDemande(e1, (long) 4, i + " demande");
            intervenant1 = service.connecterIntervenant(demande1.getIntervenant().getLogin());
            service.lancerVisioIntervenant(intervenant1);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (Exception ex) {
                System.out.println(ex);
            }
            service.raccrocherEleve(e1);
            service.evaluerVisio(e1, 2);
            service.envoyerBilan(intervenant1, "Tres bon travail");
        }

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

}
