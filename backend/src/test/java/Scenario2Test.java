
import instructif.metier.modele.Eleve;

// ce scénario teste l'allocation des intervenants
public class Scenario2Test extends AbstractScenarioTest {
    @Override
    public void runTest() {

        /**
         * On a trois intervenants, avec leurs niveaux enseignés respectifs:
         * FSAM de la 6eme à la 3eme DONALD de la 4eme à la 2nde ew en terminale
         */
        // inscription de trois éleves
        System.out.println("\n    INSCRIPTION DE QUATRES ELEVES");
        String codeEtab1 = "0692155T";
        String codeEtab3 = "0690132U"; // lycee
        Eleve e1 = new Eleve("Hugo", "Victor", "26/02/1802", 4, "vhugo@paris.fr", "1234");
        Eleve e2 = new Eleve("Zola", "Emile", "02/04/1840", 4, "ezola@paris.fr", "1234");
        Eleve e3 = new Eleve("Sand", "George", "01/07/1804", 1, "amantine@paris.fr", "1234");
        Eleve e4 = new Eleve("Berger", "Gaston", "01/07/1804", 4, "gberger@lyon.fr", "1234");

        service.inscrireEleve(e1, codeEtab1);
        service.inscrireEleve(e2, codeEtab1);
        service.inscrireEleve(e3, codeEtab3);
        service.inscrireEleve(e4, codeEtab1);

        // envoi de demandes de cours
        System.out.println("\n    ENVOI DEMANDES DE COURS");
        long id = 4;
        testEnvoiDemande(e1, id, "J'ai besoin d'une lecon svp");
        testEnvoiDemande(e2, id, "Bonjour, j'ai besoin d'une lecon svp, merci.");
        System.out.println("\n  SEULEMENT DEUX INTERVENANTS EN 4eme " + e4);
        testEnvoiDemande(e4, id, "Je suis en quatrieme.");
        System.out.println("\n  PAS D'INTERVENANT EN 1ere " + e3);
        testEnvoiDemande(e3, id, "Je suis en première.");

    }

}
