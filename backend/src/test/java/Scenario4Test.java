
import instructif.metier.modele.Eleve;

// ce scénario teste l'allocation des intervenants
public class Scenario4Test extends AbstractScenarioTest {
    @Override
    public void runTest() {

        // inscription de deux éleves
        System.out.println("\n    INSCRIPTION DE DEUX ELEVES");
        String codeEtab1 = "0692155T";
        Eleve e1 = new Eleve("Hugo", "Victor", "26/02/1802", 4, "vhugo@paris.fr", "1234");
        Eleve e2 = new Eleve("Berger", "Gaston", "01/07/1804", 4, "gberger@paris.fr", "1234");

        service.inscrireEleve(e1, codeEtab1);
        service.inscrireEleve(e2, codeEtab1);

        // envoi de demandes
        System.out.println("\n    ENVOI REUSSI");
        testEnvoiDemande(e1, (long) 4, "Première demande");
        System.out.println("\n    ENVOI NON REUSSI");
        testEnvoiDemande(e1, (long) 4, "Deuxième demande invalide");
        System.out.println("\n    ENVOI NON REUSSI");
        testEnvoiDemande(e1, (long) 10, "Troisieme demande invalide");
    }

}
