import java.util.List;

import instructif.metier.modele.Demande;
import instructif.metier.modele.Eleve;
import instructif.metier.modele.Etablissement;
import instructif.metier.modele.Intervenant;
import instructif.metier.modele.Matiere;
import instructif.metier.service.Service;

public abstract class AbstractScenarioTest {
    Service service = new Service();

    {
        service.initialiserApplication();
    }

    public abstract void runTest();

    public void affichageStatistiques() {
        Matiere matiere = service.obtenirMatierePopulaire();
        System.out.println("Matiere la plus demandée : " + matiere);

        Long[] tuple = service.obtenirStatsSoutienTotal();
        System.out.println("Nombre total de soutien : " + tuple[0]);
        System.out.println("Durée total des soutiens : " + tuple[1] + " s");

        tuple = service.obtenirStatsSoutienEleve();
        System.out.println("Nombre moyen de soutien par élève : " + tuple[0]);
        System.out.println("Durée moyenne des soutiens par élève : " + tuple[1] + " s");

        tuple = service.obtenirStatsSoutienEtablissement();
        System.out.println("Nombre moyen de soutien par établissement : " + tuple[0]);
        System.out.println("Durée moyenne des soutiens par établissement : " + tuple[1] + " s");

    }

    public void affichageHistoEleve(Eleve eleve) {
        List<Demande> listeDemandes = service.obtenirHistoriqueEleve(eleve);
        for (Demande d : listeDemandes) {
            System.out.println("- " + d);
        }
    }

    public void affichageHistoIntervenant(Intervenant intervenant) {
        List<Demande> listeDemandes = service.obtenirHistoriqueIntervenant(intervenant);
        for (Demande d : listeDemandes) {
            System.out.println("- " + d);
        }
    }

    public void affichageMatieres() {
        List<Matiere> listeMatieres = service.obtenirListeMatieres();
        for (Matiere m : listeMatieres) {
            System.out.println("- " + m.getDenomination());
        }

    }

    public void affichageEtablissements() {
        List<Etablissement> listeEtablissements = service.obtenirListeEtablissements();
        for (Etablissement d : listeEtablissements) {
            System.out.println("- " + d);
        }
    }

    public Demande testEnvoiDemande(Eleve eleve, Long id, String description) {
        Matiere matiere = service.recupererMatiereParID(id);
        Demande demande = null;
        if (matiere != null) {
            demande = service.envoyerDemande(matiere, description, eleve);
            if (demande == null) {
                System.out.println("La demande n'a pas été envoyée.");
            } else {
                System.out.println("La demande a été envoyée.");
            }
        } else {
            System.out.println("Id matiere non valide");
        }
        return demande;
    }

}
