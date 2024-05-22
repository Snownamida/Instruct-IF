/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.List;
import java.util.concurrent.TimeUnit;

import instructif.dao.JpaUtil;
import instructif.metier.modele.Demande;
import instructif.metier.modele.Eleve;
import instructif.metier.modele.Etablissement;
import instructif.metier.modele.Intervenant;
import instructif.metier.modele.Matiere;
import instructif.metier.service.Service;

/**
 *
 * @author mbaratova
 */
// Pour refresh la vue de la BD, il faut refresh la BD mais AUSSI réexécuter
// l'instruction SQL qui permet d'afficher les premières lignes !
public class BackendTest {
    static Service service = new Service();

    public static void main(String[] args) {

        JpaUtil.creerFabriquePersistance();
        service.initialiserApplication();

        // scenario1BeaucoupDeTests();
        // scenario2Intervenants();
        // scenario3Connexions();
        // scenario4EnvoiDeDemandes();
        // scenario5Visio();
        scenario6NormalEleve();

        JpaUtil.fermerFabriquePersistance();

    }

    // ce scénario regroupe beaucoup de testsen tout genre
    public static void scenario1BeaucoupDeTests() {

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
        Demande demande1 = testEnvoiDemande(e1, service, id, "J'ai besoin d'une lecon svp");
        System.out.println("\n  A DEJA UNE VISIO");
        testEnvoiDemande(e1, service, id, "J'ai besoin d'une lecon svp");
        id = 5;
        Demande demande2 = testEnvoiDemande(e2, service, id, "Bonjour, j'ai besoin d'une lecon svp, merci.");
        id = 5;
        System.out.println("\n  INTERVENANT NON DISPO");
        testEnvoiDemande(e3, service, id, "Je suis en première.");

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
        testEnvoiDemande(e2, service, id, "J'ai encore besoin d'une lecon svp");
        service.envoyerBilan(intervenant2, "Tres bon travail");

        // affichage d'informations
        System.out.println("\n    AFFICHAGE DES STATISTIQUES");
        affichageStatistiques(service);
        System.out.println("\n    AFFICHAGE HISTORIQUE D'UN ELEVE");
        affichageHistoEleve(e1, service);
        System.out.println("\n    AFFICHAGE HISTORIQUE D'UN INTERVENANT");
        affichageHistoIntervenant(intervenant1, service);
        System.out.println("\n    AFFICHAGE ETABLISSEMENTS");
        affichageEtablissements(service);
        System.out.println("\n    AFFICHAGE MATIERES");
        affichageMatieres(service);

    }

    // ce scénario teste l'allocation des intervenants
    public static void scenario2Intervenants() {

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
        testEnvoiDemande(e1, service, id, "J'ai besoin d'une lecon svp");
        testEnvoiDemande(e2, service, id, "Bonjour, j'ai besoin d'une lecon svp, merci.");
        System.out.println("\n  SEULEMENT DEUX INTERVENANTS EN 4eme " + e4);
        testEnvoiDemande(e4, service, id, "Je suis en quatrieme.");
        System.out.println("\n  PAS D'INTERVENANT EN 1ere " + e3);
        testEnvoiDemande(e3, service, id, "Je suis en première.");

    }

    // ce scnéario teste les connexions à 'application
    public static void scenario3Connexions() {

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

    public static void scenario4EnvoiDeDemandes() {

        // inscription de deux éleves
        System.out.println("\n    INSCRIPTION DE DEUX ELEVES");
        String codeEtab1 = "0692155T";
        Eleve e1 = new Eleve("Hugo", "Victor", "26/02/1802", 4, "vhugo@paris.fr", "1234");
        Eleve e2 = new Eleve("Berger", "Gaston", "01/07/1804", 4, "gberger@paris.fr", "1234");

        service.inscrireEleve(e1, codeEtab1);
        service.inscrireEleve(e2, codeEtab1);

        // envoi de demandes
        System.out.println("\n    ENVOI REUSSI");
        testEnvoiDemande(e1, service, (long) 4, "Première demande");
        System.out.println("\n    ENVOI NON REUSSI");
        testEnvoiDemande(e1, service, (long) 4, "Deuxième demande invalide");
        System.out.println("\n    ENVOI NON REUSSI");
        testEnvoiDemande(e1, service, (long) 10, "Troisieme demande invalide");
    }

    public static void scenario5Visio() {
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
            Demande demande1 = testEnvoiDemande(e1, service, (long) 4, i + " demande");
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
        affichageStatistiques(service);
        System.out.println("\n    AFFICHAGE HISTORIQUE D'UN ELEVE");
        affichageHistoEleve(e1, service);
        System.out.println("\n    AFFICHAGE HISTORIQUE D'UN INTERVENANT");
        affichageHistoIntervenant(intervenant1, service);
        System.out.println("\n    AFFICHAGE ETABLISSEMENTS");
        affichageEtablissements(service);
        System.out.println("\n    AFFICHAGE MATIERES");
        affichageMatieres(service);

    }

    public static void scenario6NormalEleve() {
        // inscription de deux éleves
        System.out.println("\n    INSCRIPTION DE L'ELEVE");
        String codeEtab1 = "0692155T";
        Eleve e1 = new Eleve("Hugo", "Victor", "26/02/1802", 4, "vhugo@paris.fr", "1234");

        service.inscrireEleve(e1, codeEtab1);

        // visio
        System.out.println("\n    ENVOI DE LA DEMANDE");
        Demande demande1 = testEnvoiDemande(e1, service, (long) 4, "Premiere demande");
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
        affichageStatistiques(service);
        System.out.println("\n    AFFICHAGE HISTORIQUE D'UN ELEVE");
        affichageHistoEleve(e1, service);
        System.out.println("\n    AFFICHAGE HISTORIQUE D'UN INTERVENANT");
        affichageHistoIntervenant(intervenant1, service);

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

    public static void affichageStatistiques(Service service) {
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

    public static void affichageHistoEleve(Eleve eleve, Service service) {
        List<Demande> listeDemandes = service.obtenirHistoriqueEleve(eleve);
        for (Demande d : listeDemandes) {
            System.out.println("- " + d);
        }
    }

    public static void affichageHistoIntervenant(Intervenant intervenant, Service service) {
        List<Demande> listeDemandes = service.obtenirHistoriqueIntervenant(intervenant);
        for (Demande d : listeDemandes) {
            System.out.println("- " + d);
        }
    }

    public static void affichageMatieres(Service service) {
        List<Matiere> listeMatieres = service.obtenirListeMatieres();
        for (Matiere m : listeMatieres) {
            System.out.println("- " + m.getDenomination());
        }

    }

    public static void affichageEtablissements(Service service) {
        List<Etablissement> listeEtablissements = service.obtenirListeEtablissements();
        for (Etablissement d : listeEtablissements) {
            System.out.println("- " + d);
        }
    }

    public static Demande testEnvoiDemande(Eleve eleve, Service service, Long id, String description) {
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