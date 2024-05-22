
import instructif.metier.modele.Eleve;

// ce scnéario teste les connexions à 'application
public class Scenario3Test extends AbstractScenarioTest {

    @Override
    public void runTest() {

        // inscription de trois éleves
        System.out.println("\n    INSCRIPTION D'ELEVES");
        String codeEtab1 = "0692155T";
        Eleve e1 = new Eleve("Hugo", "Victor", "26/02/1802", 4, "vhugo@paris.fr", "1234");
        Eleve e2 = new Eleve("Berger", "Gaston", "01/07/1804", 4, "vhugo@paris.fr", "1234"); // inscription échouée

        service.inscrireEleve(e1, codeEtab1);
        service.inscrireEleve(e2, codeEtab1);

        // connexions d'un élève
        System.out.println("\n    CONNEXION REUSSIE");
        service.connecterEleve("vhugo@paris.fr", "1234");
        System.out.println("\n    CONNEXION ECHOUEE");
        service.connecterEleve("vhugo@paris.fr", "mauvais");
        System.out.println("\n    CONNEXION ECHOUEE");
        service.connecterEleve("vhugo", "1234");
        System.out.println("\n    CONNEXION ECHOUEE");
        service.connecterEleve("vhugo", "mauvais");

        // connexion d'intervenants
        System.out.println("\n    CONNEXION REUSSIE");
        service.connecterIntervenant("FSAM");
        System.out.println("\n    CONNEXION ECHOUEE");
        service.connecterIntervenant("Malika");

    }

}
