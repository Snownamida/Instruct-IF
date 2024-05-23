/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package instructif.metier.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import instructif.dao.DemandeDAO;
import instructif.dao.EleveDAO;
import instructif.dao.EtablissementDAO;
import instructif.dao.IntervenantDAO;
import instructif.dao.JpaUtil;
import instructif.dao.MatiereDAO;
import instructif.metier.modele.Autre;
import instructif.metier.modele.Demande;
import instructif.metier.modele.Eleve;
import instructif.metier.modele.Enseignant;
import instructif.metier.modele.Etablissement;
import instructif.metier.modele.Etudiant;
import instructif.metier.modele.Intervenant;
import instructif.metier.modele.Matiere;
import instructif.util.ColorUtil;
import instructif.util.EducNetApi;
import instructif.util.Message;

/**
 *
 * @author mbaratova
 */
public class Service { // Tip :le service ne dépend pas de la persistence

    /**
     * Pour désactiver les traces, appelez ces fonctions :
     * desactiverLog() : pour cacher les logs basiques des services
     * desactiverStackTrace() : pour cacher les traces détaillées des erreurs.
     */
    private Boolean afficherLog = true;
    private Boolean afficherTraceErreur = true;

    // ### SERVICES DE PERSISTANCE / INSCRIPTION / INITIALISATION
    // ----------------------------------

    // Peuple toute la base de données avec divers intervenants et matières hard
    // codé(e)s.
    public void initialiserApplication() {
        initialiserIntervenants();
        initialiserMatieres();
    }

    // Persiste un élève (ainsi que son établissement is celui-ci n'est pas déjà
    // connu) dans la base de données suite à son inscription
    public Boolean inscrireEleve(final Eleve eleve, final String codeEtablissement) {
        Boolean reussi = true;

        final EleveDAO eleveDAO = new EleveDAO();
        final EtablissementDAO etablissementDAO = new EtablissementDAO();

        String sujet = "", corps = "";
        final String expediteur = "contact@instruct.if";
        final String pour = eleve.getMail();
        try {
            JpaUtil.creerContextePersistance(); // Tip : Mettre " créer contexte persistance" dans Service car essentiel
                                                // pour manipuler des transactions dans le Service
            JpaUtil.ouvrirTransaction(); // Tip : pas dans les dao, car sinon que des transactions unitaires (un seul
                                         // tuple échangé)

            Etablissement etablissement = etablissementDAO.rechercheParCode(codeEtablissement);
            if (etablissement == null) { // Si l'établissement n'est pas encore dans la BD, on l'ajoute et on le
                                         // persiste :
                if (afficherLog == true) {
                    log("Etablissement rechercheParCode)) Etablissement non trouvé.");
                }
                etablissement = chercherInfosEtablissement(codeEtablissement, eleve.getClasse());
                etablissementDAO.creerEtablissement(etablissement);
            }
            eleve.setEtablissement(etablissement);
            eleveDAO.creerEleve(eleve); // On persiste l'élève

            JpaUtil.validerTransaction();
            if (afficherLog == true) {
                log("inscrireEleve)) Inscription réussie de l'élève : " + eleve + "");
            }

            corps = "Bonjour " + eleve.getPrenom()
                    + ", nous te confirmons ton inscription sur le réseau INSTRUCT'IF. Si tu as besoin d'un soutien pour tes lecons ou tes devoirs, rends toi sur notre site pour une mise en relation avec un intervenant.";
            sujet = "Bienvenue sur le réseau INSTRUCT'IF.";

        } catch (final Exception ex) { // Si le code de l'établissement est invalide, ou s'il y a eu une erreur de
                                       // persistence de l'élève / établissement :
            if (afficherLog == true) {
                log("inscrireEleve)) Inscription échouée pour l'élève : " + eleve + ">");
            }

            JpaUtil.annulerTransaction();
            reussi = false;
            corps = "Bonjour " + eleve.getPrenom()
                    + ", ton inscription sur le réseau INSTRUCT'IF a malencontreusement échoué... Merci de recommencer ultérieurement.";
            sujet = "Echec de l'inscription sur le réseau INSTRUCT'IF.";
            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }
        } finally {
            JpaUtil.fermerContextePersistance();
        }

        Message.envoyerMail(expediteur, pour, sujet, corps);

        return reussi;
    }

    // Créée et persiste une demande de soutien à partir de la matière et de la
    // description fournis
    public Demande envoyerDemande(final Matiere matiere, final String description, final Eleve eleve) {

        final DemandeDAO demandeDAO = new DemandeDAO();
        Demande demande = new Demande(description, matiere, eleve);
        final IntervenantDAO intervenantDAO = new IntervenantDAO();

        Demande demandeAncienne;

        try {

            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();

            final Intervenant intervenant = intervenantDAO.trouverIntervenant(eleve.getClasse()); // On cherche un
                                                                                                  // intervenant
            if (intervenant == null) {
                if (afficherLog == true) {
                    log("Intervenant trouverIntervenant)) Intervenant non trouvé.");
                }
            }

            demandeAncienne = demandeDAO.rechercheParEleve(eleve); // On regarde s'il existe déjà une demande non finie
                                                                   // faite par cet élève
            if (intervenant != null && demandeAncienne == null) { // On ne peut continuer avec la demande que si on a
                                                                  // trouvé un intervenant et que toutes les demandes de
                                                                  // soutien de l'élèves sont bien terminées.
                if (afficherLog == true) {
                    log(
                            "envoyerDemande)) Succès de la recherche d'un intervenant : " + intervenant + "");
                }

                demande.setIntervenant(intervenant);

                intervenant.setDisponible(false); // On marque l'intervenant comme "occupé" jusqu'à la fin du soutien
                                                  // (c'est-à-dire l'envoi du bilan)
                demandeDAO.creerDemande(demande); // Persister la demande
                lancerVisioEleve(eleve);
                JpaUtil.validerTransaction();

                Message.envoyerNotification(intervenant.getTelephone(),
                        "Bonjour " + intervenant.getPrenom() + ". Merci de prendre en charge la demande de soutien en "
                                + demande.getMatiere().getDenomination() + " demandée à "
                                + demande.obtenirDateFormatee(demande.getDateDebut()) + " par "
                                + demande.getEleve().getPrenom() + " en classe de "
                                + demande.getEleve().obtenirDenominationClasse());

            } else { // Si pas trouvé d'intervenant, ou demande inachevée déjà en cours
                demande = null;
                JpaUtil.annulerTransaction();
                if (afficherLog == true) {
                    log(
                            "envoyerDemande)) Echec de la prise en compte de la demande : pas d'intervenant disponible, ou demande de soutien inachevée déjà en cours.");
                }

            }

        } catch (final Exception ex) { // Erreur de persistance / mise à jour de la demande
            if (afficherLog == true) {
                log(
                        "envoyerDemande)) Echec de la prise en compte de la demande (échec d'enregistrement de la demande)");
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }
            JpaUtil.annulerTransaction();
            demande = null;

        } finally {
            JpaUtil.fermerContextePersistance();
        }

        return demande;
    }

    // ### SERVICES DE CONNEXION
    // -------------------------------------------------------------------

    // Renvoie un objet Eleve présent dans la BD à partir d'un login et d'un mot de
    // passe valides.
    public Eleve connecterEleve(final String mail, final String motDePasse) {
        Eleve eleve = null; // 'eleve" sera NULL en cas d'erreur d'authentification
        final EleveDAO eleveDAO = new EleveDAO();
        try {

            JpaUtil.creerContextePersistance();
            eleve = eleveDAO.rechercheParMail(mail); // Chercher si un élève correspondant au mail existe dans la BD
            if (eleve == null) {
                if (afficherLog == true) {
                    log("Eleve rechercheParMail)) Eleve non trouvé.");
                }
            }
            if (!(eleve.getMotDePasse().equals(motDePasse))) { // Puis, vérifier que le mot de passe correspond bien
                eleve = null;
                if (afficherLog == true) {
                    log("connecterEleve)) Echec de l'authentification de l'élève.");
                }
            } else {
                if (afficherLog == true) {
                    log(
                            "connecterEleve)) Succès de l'authentification de l'élève : " + eleve + "");
                }

            }
        } catch (final Exception ex) {
            if (afficherLog == true) {
                log(
                        "connecterEleve)) Echec de l'authentification de l'élève : mail et/ou mot de passe invalide(s).");
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }
        } finally {
            JpaUtil.fermerContextePersistance();
        }

        return eleve;

    }

    // Renvoie un objet Intervenant présent dans la BD à partir d'un login valide.
    public Intervenant connecterIntervenant(final String login) {
        final IntervenantDAO intervenantDAO = new IntervenantDAO();
        Intervenant intervenant = null; // "intervenant" sera NULL en cas de login invalide
        try {

            JpaUtil.creerContextePersistance();
            intervenant = intervenantDAO.rechercheParLogin(login); // Chercher s'il existe un intervenant avec ce login
                                                                   // dans la BD
            if (intervenant == null) {
                if (afficherLog == true) {
                    log("Intervenant rechercheParLogin)) Intervenant non trouvé.");
                }
            }

            else {
                if (afficherLog == true) {
                    log("connecterIntervenant)) Succès de l'authentification de l'intervenant : "
                            + intervenant + "");
                }
            }

        } catch (final Exception ex) {
            if (afficherLog == true) {
                log(
                        "connecterIntervenant)) Echec de l'authentification de l'intervenant : erreur système.");
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }

        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return intervenant;
    }

    // ### SERVICES D'ACTIONS CONCRETES DANS L'APPLICATION
    // -----------------------------------------

    // Met à jour la date de début de la demande de soutien en cours de l'élève.
    public Boolean lancerVisioEleve(final Eleve eleve) {
        Boolean reussi = true;
        final DemandeDAO demandeDAO = new DemandeDAO();

        try {

            final Demande demande = demandeDAO.rechercheParEleve(eleve); // On cherche la dernière (et unique) demande
                                                                         // de soutien non terminée de l'élève.

            final Date dateDebut = new Date();
            demande.setDateDebut(dateDebut);
            demandeDAO.majDemande(demande); // On met à jour la date de début de la demande, de NULL à l'instant actuel.
            if (afficherLog == true) {
                log(
                        "lancerVisioEleve)) Succès du lancement de la visio par l'éleve " + eleve + "");
            }

        } catch (final Exception ex) {
            if (afficherLog == true) {
                System.out
                        .print("lancerVisioEleve)) Echec du lancement de la visio par l'éleve " + eleve + "");
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }
            reussi = false;

        }
        return reussi;
    }

    // Incrémente le nombre d'interventions de l'intervenant
    public Boolean lancerVisioIntervenant(final Intervenant intervenant) {
        Boolean reussi = true;
        final IntervenantDAO intervenantDAO = new IntervenantDAO();

        try {

            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();

            final int nombre = intervenant.getNombreInterventions();
            intervenant.setNombreInterventions(nombre + 1);
            intervenantDAO.majIntervenant(intervenant); // Incrémenter puis mettre à jour le nombre d'interventions

            if (afficherLog == true) {
                log("lancerVisioIntervenant)) Succès  : l'intervenant " + intervenant
                        + " a pu rejoindre la visio.");
            }

        } catch (final Exception ex) { // En cas d'échec de la mise à jour de l'intervenant
            if (afficherLog == true) {
                log("lancerVisioIntervenant)) Succès  : l'intervenant " + intervenant
                        + " a pu rejoindre la visio.");
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }

            reussi = false;

        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return reussi;
    }

    // Met fin à au cours de soutien visio en donnant une date de fin et une durée à
    // la demande.
    public Boolean raccrocherEleve(final Eleve eleve) {
        Boolean reussi = true;
        final DemandeDAO demandeDAO = new DemandeDAO();
        final EtablissementDAO etablissementDAO = new EtablissementDAO();

        try {

            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();

            final Demande demande = demandeDAO.rechercheParEleve(eleve); // On cherche toujours la denrière (et unique)
                                                                         // demande de soutien inachevée de l'élève.

            final Date dateFin = new Date();
            final Date dateDebut = demande.getDateDebut();
            final long dureeMS = dateFin.getTime() - dateDebut.getTime();
            final long duree = TimeUnit.SECONDS.convert(dureeMS, TimeUnit.MILLISECONDS); // changer seconds en minutes
                                                                                         // si nécessaire

            demande.setDateFin(dateFin); // On met à jour la date de fin et la durée (en minutes) de la visio. On part
                                         // du principe qu'une visio dure moins d'un jour.
            demande.setDuree(duree);
            demandeDAO.majDemande(demande);

            // modification des attributs nbDemandes et duréeMoyenne de l'établissement
            final Etablissement etablissement = etablissementDAO.rechercheParID(eleve.getEtablissement().getId()); // eleve.getEtablissement
                                                                                                                   // a
                                                                                                                   // ses
                                                                                                                   // variables
                                                                                                                   // encore
                                                                                                                   // à
                                                                                                                   // 0,0
            final Long nbDemandes = etablissement.getNbDemandes();
            final Long dureeMoyenne = etablissement.getDureeMoyenne();

            etablissement.setNbDemandes((long) (nbDemandes + 1));
            etablissement.setDureeMoyenne((long) ((dureeMoyenne * nbDemandes + duree) / (nbDemandes + 1)));
            etablissementDAO.majEtablissement(etablissement);
            eleve.setEtablissement(etablissement); // obligatoire car on a une ManyToOne dans Eleve, donc les
                                                   // changements dans Etablissements ne sont pas refléter dans la
                                                   // variable établissement de Eleve

            JpaUtil.validerTransaction();
            if (afficherLog == true) {
                log("raccrocherEleve)) Succès de l'arrêt de la visio par l'éleve : " + eleve
                        + ". Durée : " + duree + " |  Terminée le " + dateFin + "");
            }

        } catch (final Exception ex) { // Si erreur dans la mise à jour de l'objet Demande
            if (afficherLog == true) {
                System.out
                        .print("raccrocherEleve)) Echec de l'arrêt de la visio par l'élève : " + eleve + "");
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }
            JpaUtil.annulerTransaction();
            reussi = false;

        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return reussi;

    }

    // Attribue une note à une demande de soutien. Une des deux conditions requises
    // pour qu'une demande de soutien soit "terminée".
    public Boolean evaluerVisio(final Eleve eleve, final int eval) {
        Boolean reussi = true;
        final DemandeDAO demandeDAO = new DemandeDAO();

        try {

            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();

            final Demande demande = demandeDAO.rechercheParEleve(eleve); // Chercher la dernière (et unique) demande non
                                                                         // terminée de l'élève.
            demande.setEvaluation(eval); // Remplir le champ "evaluation" de la demande

            if (demande.getBilan() != null) { // Si le bilan a été rédigé par l'intervenant (autre condition requise
                                              // pour terminer), alors la demande est terminée.
                demande.setFini(true);
                final Intervenant intervenant = demande.getIntervenant();
                remettreIntervenantDispo(intervenant); // On remet aussi l'intervenant comme "disponible"
            }

            demandeDAO.majDemande(demande); // Mettre à jour les modifications
            JpaUtil.validerTransaction();
            if (afficherLog == true) {
                log("evaluerVisio)) Succès de l'évaluation : l'élève " + eleve
                        + " a choisi la note '" + eval + "'.");
            }

        } catch (final Exception ex) { // En cas d'erreur de mise à jour de la demande
            if (afficherLog == true) {
                log("evaluerVisio)) Echec de l'évaluation par l'élève " + eleve);
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }
            JpaUtil.annulerTransaction();
            reussi = false;

        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return reussi;

    }

    // Attribue un bilan à une demande de soutien et envoie le bilan à l'élève. Une
    // des deux conditions requises pour qu'une demande de soutien soit "terminée".
    public Boolean envoyerBilan(final Intervenant intervenant, final String bilan) {
        Boolean reussi = true;
        final DemandeDAO demandeDAO = new DemandeDAO();
        Demande demande = null;

        try {

            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();

            demande = demandeDAO.rechercheParIntervenant(intervenant); // Cherche al dernière (et unique) demande
                                                                       // inachevée de l'élève.

            String sujet = "", corps = "";
            final String expediteur = "contact@instruct.if";
            final Eleve eleve = demande.getEleve();
            final String pour = eleve.getMail();

            demande.setBilan(bilan); // Remplir le chanp "bilan" de la demande
            if (demande.getEvaluation() != 0) { // Si la demande a aussi été évaluée par l'élève (deuxième condition
                                                // requise), la demande est "terminée".
                demande.setFini(true);
                remettreIntervenantDispo(intervenant); // On remet aussi l'intervenant en "disponible"
            }

            demandeDAO.majDemande(demande); // Mettre à jour les modifications.
            JpaUtil.validerTransaction();

            sujet = "Bilan après ta visio sur Instruct'IF";
            corps = "Voici le message de la part de " + demande.getIntervenant().getPrenom() + "" + bilan;
            Message.envoyerMail(expediteur, pour, sujet, corps);

            if (afficherLog == true) {
                log(
                        "envoyerBilan)) Succès de l'envoi du bilan par l'intervenant : " + intervenant);
            }

        } catch (final Exception ex) { // EN cas d'erreur de mise à jour de la demande
            if (afficherLog == true) {
                log(
                        "envoyerBilan)) Echec de l'envoi du bilan par l'intervenant : " + intervenant);
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }
            JpaUtil.annulerTransaction();
            reussi = false;

        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return reussi;

    }

    // ### SERVICES D'OBTENTION D'UN OBJET A PARTIR D'UN AUTRE
    // ------------------------------------

    // Permet d'obtenir la dernière (et unique) demande non terminée d'un
    // intervenant donné.
    public Demande obtenirDemandeIntervenant(final Intervenant intervenant) {
        Demande demande = null;
        final DemandeDAO demandeDAO = new DemandeDAO();
        try {

            JpaUtil.creerContextePersistance();
            demande = demandeDAO.rechercheParIntervenant(intervenant);
            if (afficherLog == true) {
                log(
                        "obtenirDemandeIntervenant)) Succès de l'obtention détails de la demande en cours : "
                                + demande + "");
            }

        } catch (final Exception ex) { // Erreur interne dans la recherche de la demande
            if (afficherLog == true) {
                log(
                        "obtenirDemandeIntervenant)) Echec de l'obtention détails de la demande en cours");
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }

        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return demande;
    }

    // ### SERVICES D'OBTENTION DE LISTES
    // ---------------------------------------------------------

    // Permet d'obtenir la liste de tous les établissements dans la BD
    public List<Etablissement> obtenirListeEtablissements() {
        List<Etablissement> listeEtablissements = null; // La liste sera NULL en cas d'échec de la recherche
        final EtablissementDAO etablissementDAO = new EtablissementDAO();

        try {
            JpaUtil.creerContextePersistance();
            listeEtablissements = etablissementDAO.rechercheToutesLesEtablissements();
            if (afficherLog == true) {
                log(
                        "obtenirListeEtablissements)) Succès de l'obtention de la liste des établissements.");
            }

        } catch (final Exception ex) {
            if (afficherLog == true) {
                log(
                        "obtenirListeEtablissements)) Echec de l'obtention de la liste des établissements.");
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }
        } finally {
            JpaUtil.fermerContextePersistance();
        }

        return listeEtablissements;
    }

    // Permet d'obtenir la liste de toutes les matières dans la BD
    public List<Matiere> obtenirListeMatieres() {
        List<Matiere> listeMatieres = null; // La liste renvoyée sera NULL en cas d'échec
        final MatiereDAO matiereDAO = new MatiereDAO();

        try {
            JpaUtil.creerContextePersistance();
            listeMatieres = matiereDAO.rechercheToutesLesMatieres();
            if (afficherLog == true) {
                log("obtenirListeMatieres)) Succès de l'obtention de la liste des matières.");
            }

        } catch (final Exception ex) {
            if (afficherLog == true) {
                log("obtenirListeMatieres)) Echec de l'obtention de la liste des matières.");
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }
        } finally {
            JpaUtil.fermerContextePersistance();
        }

        return listeMatieres;
    }

    // Permet d'obtenir la liste de toutes les demandes de soutien prises en compte
    // par un certain intervenant
    public List<Demande> obtenirHistoriqueIntervenant(final Intervenant intervenant) {
        List<Demande> listeDemandes = null; // La liste renvoyée sera NULL en cas d'échec
        final DemandeDAO demandeDAO = new DemandeDAO();

        try {
            JpaUtil.creerContextePersistance();
            listeDemandes = demandeDAO.rechercheToutesLesDemandes(intervenant);
            if (afficherLog == true) {
                log(
                        "obtenirHistoriqueIntervenant)) Succès de l'obtention de l'historique de l'intervenant "
                                + intervenant);
            }

        } catch (final Exception ex) {
            if (afficherLog == true) {
                log(
                        "obtenirHistoriqueIntervenant)) Echec de l'obtention de l'historique de l'intervenant "
                                + intervenant);
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }
        } finally {
            JpaUtil.fermerContextePersistance();
        }

        return listeDemandes;
    }

    // Permet d'obtenir la liste de toutes les demandes de soutien initiées par un
    // certain élève
    public List<Demande> obtenirHistoriqueEleve(final Eleve eleve) {
        List<Demande> listeDemandes = null; // La liste renvoyée sera NULL en cas d'échec
        final DemandeDAO demandeDAO = new DemandeDAO();

        try {
            JpaUtil.creerContextePersistance();
            listeDemandes = demandeDAO.rechercheToutesLesDemandes(eleve);
            if (afficherLog == true) {
                log("obtenirHistoriqueEleve)) Succès de l'obtention de l'historique de l'élève "
                        + eleve);
            }

        } catch (final Exception ex) {
            if (afficherLog == true) {
                log("obtenirHistoriqueEleve)) Echec de l'obtention de l'historique de l'élève "
                        + eleve);
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }
        } finally {
            JpaUtil.fermerContextePersistance();
        }

        return listeDemandes;
    }

    // ### SERVICES D'OBTENTION DE STATISTIQUES
    // ---------------------------------------------------

    // Permet d'obtenir la matière la plus demandée en soutien par les élèves du
    // réseau.
    public Matiere obtenirMatierePopulaire() {
        Matiere matiere = null; // La matière sera NULL en cas d'échec
        final DemandeDAO demandeDAO = new DemandeDAO();
        try {

            JpaUtil.creerContextePersistance();
            matiere = demandeDAO.rechercheMatierePopulaire();
            if (matiere == null) {
                if (afficherLog == true) {
                    log("Matiere rechercheMatierePopulaire)) Matiere non trouvée");
                }
            } else {
                if (afficherLog == true) {
                    log(
                            "obtenirMatierePopulaire)) Succès de la recherche : Matiere la plus populaire : "
                                    + matiere);
                }
            }

        } catch (final Exception ex) { // Erreur interne dans la recherche
            if (afficherLog == true) {
                log("obtenirMatierePopulaire)) Echec de la recherche.");
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }

        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return matiere;
    }

    // Permet d'obtenir un tuple du type : (nombre total de demandes de soutien,
    // durée totale additionnée de toutes ces demandes)
    public Long[] obtenirStatsSoutienTotal() {
        Long[] tuple = new Long[2];
        final DemandeDAO demandeDAO = new DemandeDAO();
        try {

            JpaUtil.creerContextePersistance();
            tuple = demandeDAO.rechercheStatsSoutienTotal();
            if (afficherLog == true) {
                System.out
                        .print("obtenirStatsSoutienTotal))  Succès de la recherche. Nombre total de demandes : "
                                + tuple[0] + ", durée totale des soutiens : " + tuple[1] + "min. ");
            }

        } catch (final Exception ex) { // Erreur interne dans la recherche
            if (afficherLog == true) {
                log("obtenirStatsSoutienTotal)) Echec de la recherche.");
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }

        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return tuple;
    }

    // Permet d'obtenir un tuple du type : (nombre moyen de demandes de soutien par
    // élève, durée moyenne totale de tous les soutiens par élève)
    public Long[] obtenirStatsSoutienEleve() {
        Long[] tuple = new Long[2];
        final DemandeDAO demandeDAO = new DemandeDAO();
        try {

            JpaUtil.creerContextePersistance();
            tuple = demandeDAO.rechercheStatsSoutienEleve();
            if (afficherLog == true) {
                log(
                        "obtenirStatsSoutienEleve)) Succès de la recherche. Nombre moyen de demandes par élève : "
                                + tuple[0] + ", durée moyenne de tous les soutien par élève : " + tuple[1] + "min.");
            }

        } catch (final Exception ex) { // Erreur interne dans la recherche
            if (afficherLog == true) {
                log("obtenirStatsSoutienEleve)) Echec de la recherche.");
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }

        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return tuple;
    }

    // Permet d'obtenir un tuple du type : (nombre moyen de demandes de soutien par
    // établissement, durée moyenne totale de tous les soutiens par établissement)
    public Long[] obtenirStatsSoutienEtablissement() {
        Long[] tuple = new Long[2];
        final DemandeDAO demandeDAO = new DemandeDAO();
        try {

            JpaUtil.creerContextePersistance();
            tuple = demandeDAO.rechercheStatsSoutienEtablissement();
            if (afficherLog == true) {
                log(
                        "obtenirStatsSoutienEtablissement)) Succès de la recherche. Nombre moyen de demandes par établissement : "
                                + tuple[0] + ", durée moyenne de tous les soutien par établissement : " + tuple[1]
                                + "min.");
            }

        } catch (final Exception ex) { // Erreur interne dans la recherche
            if (afficherLog == true) {
                log("obtenirStatsSoutienEtablissement)) Echec de la recherche.");
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }

        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return tuple;
    }

    // RECHERCHES PAR ID
    // -----------------------------------------------------------------------

    public Eleve recupererEleveParID(final long id) {
        Eleve eleve = null;
        final EleveDAO eleveDAO = new EleveDAO();
        try {

            JpaUtil.creerContextePersistance();
            eleve = eleveDAO.rechercheParID(id);
            if (afficherLog == true) {
                log("recupererEleveParID) Succès de la recherche par ID : " + eleve);
            }

        } catch (final Exception ex) {
            if (afficherLog == true) {
                log("recupererEleveParID) Echec de la recherche par ID.");
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }
        } finally {
            JpaUtil.fermerContextePersistance();
        }

        return eleve;
    }

    public Intervenant recupererIntervenantParID(final long id) {
        final IntervenantDAO intervenantDAO = new IntervenantDAO();
        Intervenant intervenant = null;
        try {
            JpaUtil.creerContextePersistance();
            intervenant = intervenantDAO.rechercheParID(id);
            if (afficherLog == true) {
                log(
                        "recupererIntervenantParID) Succès de la recherche par ID : " + intervenant);
            }

        } catch (final Exception ex) {

            if (afficherLog == true) {
                log("recupererIntervenantParID) Echec de la recherche par ID.");
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }

        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return intervenant;
    }

    public Demande recupererDemandeParID(final long id) {
        final DemandeDAO demandeDAO = new DemandeDAO();
        Demande demande = null;
        try {
            JpaUtil.creerContextePersistance();
            demande = demandeDAO.rechercheParID(id);
            if (afficherLog == true) {
                log("recupererDemandeParID) Succès de la recherche par ID : " + demande);
            }

        } catch (final Exception ex) {

            if (afficherLog == true) {
                log("recupererDemandeParID) Echec de la recherche par ID.");
                if (afficherTraceErreur == true) {
                }

                ex.printStackTrace();
            }
        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return demande;
    }

    public Matiere recupererMatiereParID(final long id) {
        final MatiereDAO matiereDAO = new MatiereDAO();
        Matiere matiere = null;
        try {
            JpaUtil.creerContextePersistance();
            matiere = matiereDAO.rechercheParID(id);
            if (afficherLog == true) {
                log("recupererMatiereParID) Succès de la recherche par ID : " + matiere);
            }

        } catch (final Exception ex) {

            if (afficherLog == true) {
                log("recupererMatiereParID) Echec de la recherche par ID.");
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }

        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return matiere;

    }

    public Etablissement recupererEtablissementParID(final long id) {
        final EtablissementDAO etablissementDAO = new EtablissementDAO();
        Etablissement etablissement = null;
        try {
            JpaUtil.creerContextePersistance();
            etablissement = etablissementDAO.rechercheParID(id);
            if (afficherLog == true) {
                log("recupererEtablissementParID) Succès de la recherche par ID : " + etablissement);
            }

        } catch (final Exception ex) {

            if (afficherLog == true) {
                log("recupererEtablissementParID) Echec de la recherche par ID.");
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }

        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return etablissement;
    }

    // METHODES D'ACTIVTION DES LOGS
    public void desactiverLog() {
        afficherLog = false;
    }

    public void desactiverStackTrace() {
        afficherTraceErreur = false;
    }

    // METHODES INTERNES
    // ----------------------------------------------------------------------

    // Peuple la table INTERVENANT de la BD
    private void initialiserIntervenants() {
        final IntervenantDAO intervenantDAO = new IntervenantDAO();

        final Etudiant et = new Etudiant("FSAM", "FAVRO", "Samuel", "0642049305", 6, 3, "INSA", "Maths");

        final Autre a = new Autre("DONALD", "DONODIO GALVIS", "Florine", "0671150503", 4, 2, "Ingénieur");

        final Enseignant en = new Enseignant("ew", "DEKEW", "Simon", "0713200950", 0, 0, "Lycée");

        final Autre samy = new Autre("SAMSAM", "Saa", "Myy", "0123321456", 4, 2, "INSAAAAMYYY");

        try {
            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();
            intervenantDAO.creerIntervenant(en);
            intervenantDAO.creerIntervenant(a);
            intervenantDAO.creerIntervenant(et);
            intervenantDAO.creerIntervenant(samy);
            JpaUtil.validerTransaction();

            if (afficherLog == true) {
                log(
                        "Interne initialiserIntervenants> Succès de l'initialisation des intervenants.");
            }

        } catch (final Exception ex) { // Erreur interne de persistance
            if (afficherLog == true) {
                log(
                        "Interne initialiserIntervenants> Echec de l'initialisation des intervenants.");
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }

            JpaUtil.annulerTransaction();
        } finally {
            JpaUtil.fermerContextePersistance();
        }

    }

    // Peuple la table MATIERES de la BD
    private void initialiserMatieres() {
        final List<String> matieres = new ArrayList<String>();
        matieres.add("Maths");
        matieres.add("Histoire Géographie");
        matieres.add("Francais");
        matieres.add("Physique");

        final MatiereDAO matiereDAO = new MatiereDAO();
        Matiere matiere;

        try {
            JpaUtil.creerContextePersistance();

            JpaUtil.ouvrirTransaction(); // Tip : pas dans les dao, car sinon que des transactions unitaires (un seul
                                         // tuple échangé)

            for (final String nom : matieres) {
                matiere = new Matiere(nom);
                matiereDAO.creerMatiere(matiere); // Persistance de la matière
            }
            if (afficherLog == true) {
                log("Interne initialiserMatieres> Succès de l'initialisation des matières.");
            }

            JpaUtil.validerTransaction();

        } catch (final Exception ex) {
            if (afficherLog == true) {
                log("Interne initialiserMatieres> Echec de l'initialisation des matières.");
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }
            JpaUtil.annulerTransaction();
        } finally {
            JpaUtil.fermerContextePersistance();
        }

    }

    // Renvoie un objet Etablissement (sans le persister) correspondant aux données
    // fournies par EducNetAPI à partir d'un code d'établissement.
    private Etablissement chercherInfosEtablissement(final String code, final int classe) {

        final EducNetApi api = new EducNetApi();
        Etablissement etablissement = null; // L'établissement renvoyé sera NULL en cas d'échec

        try {
            List<String> result;
            if (classe >= 3) {
                result = api.getInformationCollege(code);
            } else {
                result = api.getInformationLycee(code);
            }

            if (result != null) { // Si le code établissement est bon, on crée l'objet Etablissement.
                etablissement = new Etablissement(code, result.get(1), result.get(2), result.get(4), result.get(5),
                        result.get(6), result.get(8));
                if (afficherLog == true) {
                    log(
                            "Interne chercherInfosEtablissement> Succès de la création d'un nouvel établissement : "
                                    + etablissement);
                }

            }

        } catch (final Exception ex) {
            if (afficherLog == true) {
                log(
                        "Interne chercherInfosEtablissement> Echec de la création d'un nouvel établissement.");
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }
        }

        return etablissement;
    }

    // Remet l'intervenant correspondant comme à nouveau disponible.
    private Boolean remettreIntervenantDispo(final Intervenant intervenant) {
        Boolean reussi = true;
        final IntervenantDAO intervenantDAO = new IntervenantDAO();

        try {

            intervenant.setDisponible(true); // On remet l'intervenant à "disponible" et on met à jour
            intervenantDAO.majIntervenant(intervenant);

            if (afficherLog == true) {
                log("Interne remettreIntervenantDispo> Succès : l'intervenant " + intervenant
                        + " est à nouveau disponible.");
            }

        } catch (final Exception ex) { // Si erreur dans la mise à jour de l'objet Intervenant
            if (afficherLog == true) {
                log("Interne remettreIntervenantDispo> Echec : l'intervenant " + intervenant
                        + " n'a pas être remis comme disponible.");
            }

            if (afficherTraceErreur == true) {
                ex.printStackTrace();
            }

            JpaUtil.annulerTransaction();
            reussi = false;

        }
        return reussi;

    }

    private static void log(final String message) {
        System.out.println(ColorUtil.ANSI_YELLOW + "[Service:Log] " + message + ColorUtil.ANSI_RESET);
    }

}
