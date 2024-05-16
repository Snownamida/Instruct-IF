/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package instructif.metier.service;

import instructif.dao.DemandeDAO;
import instructif.dao.EleveDAO;
import instructif.dao.EtablissementDAO;
import instructif.dao.IntervenantDAO;
import instructif.dao.JpaUtil;
import instructif.dao.MatiereDAO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import instructif.metier.modele.Autre;
import instructif.metier.modele.Demande;
import instructif.metier.modele.Eleve;
import instructif.metier.modele.Enseignant;
import instructif.metier.modele.Etablissement;
import instructif.metier.modele.Etudiant;
import instructif.metier.modele.Intervenant;
import instructif.metier.modele.Matiere;
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
    
    // ### SERVICES DE PERSISTANCE / INSCRIPTION / INITIALISATION ----------------------------------
    
    // Peuple toute la base de données avec divers intervenants et matières hard codé(e)s.
    public void initialiserApplication() { 
        initialiserIntervenants();
        initialiserMatieres();
    }

    // Persiste un élève (ainsi que son établissement is celui-ci n'est pas déjà connu) dans la base de données suite à son inscription
    public Boolean inscrireEleve(Eleve eleve, String codeEtablissement) { 
        Boolean reussi = true;

        EleveDAO eleveDAO = new EleveDAO();
        EtablissementDAO etablissementDAO = new EtablissementDAO();

        String sujet = "", corps = "";
        String expediteur = "contact@instruct.if";
        String pour = eleve.getMail();
        try {
            JpaUtil.creerContextePersistance(); // Tip : Mettre " créer contexte persistance" dans Service car essentiel pour manipuler des transactions dans le Service
            JpaUtil.ouvrirTransaction(); // Tip : pas dans les dao, car sinon que des transactions unitaires (un seul tuple échangé)

            Etablissement etablissement = etablissementDAO.rechercheParCode(codeEtablissement);
            if (etablissement == null) { // Si l'établissement n'est pas encore dans la BD, on l'ajoute et on le persiste :
                if(afficherLog == true){
                    System.out.println("Etablissement rechercheParCode)) Etablissement non trouvé.");
                }
                etablissement = chercherInfosEtablissement(codeEtablissement,eleve.getClasse());
                etablissementDAO.creerEtablissement(etablissement);
            }
            eleve.setEtablissement(etablissement);
            eleveDAO.creerEleve(eleve); // On persiste l'élève

            JpaUtil.validerTransaction();
            if(afficherLog == true){
                System.out.print("Service inscrireEleve)) Inscription réussie de l'élève : " + eleve + "\n");
            }
            

            corps = "Bonjour " + eleve.getPrenom() + ", nous te confirmons ton inscription sur le réseau INSTRUCT'IF. Si tu as besoin d'un soutien pour tes lecons ou tes devoirs, rends toi sur notre site pour une mise en relation avec un intervenant.";
            sujet = "Bienvenue sur le réseau INSTRUCT'IF.";

        } catch (Exception ex) { // Si le code de l'établissement est invalide, ou s'il y a eu une erreur de persistence de l'élève / établissement :
            if(afficherLog == true){
                System.out.print("Service inscrireEleve)) Inscription échouée pour l'élève : " + eleve + ">\n");
            }
            
            
            JpaUtil.annulerTransaction();
            reussi = false;
            corps = "Bonjour " + eleve.getPrenom() + ", ton inscription sur le réseau INSTRUCT'IF a malencontreusement échoué... Merci de recommencer ultérieurement.";
            sujet = "Echec de l'inscription sur le réseau INSTRUCT'IF.";
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }
        } finally {
            JpaUtil.fermerContextePersistance();
        }

        Message.envoyerMail(expediteur, pour, sujet, corps);

        return reussi;
    }

    // Créée et persiste une demande de soutien à partir de la matière et de la description fournis
    public Demande envoyerDemande(Matiere matiere, String description, Eleve eleve) {

        DemandeDAO demandeDAO = new DemandeDAO();
        Demande demande = new Demande(description, matiere, eleve);
        IntervenantDAO intervenantDAO = new IntervenantDAO();
        
        Demande demandeAncienne; 

        try {

            JpaUtil.creerContextePersistance(); 
            JpaUtil.ouvrirTransaction();

            Intervenant intervenant = intervenantDAO.trouverIntervenant(eleve.getClasse()); // On cherche un intervenant
            if (intervenant == null){
                if(afficherLog == true){
                     System.out.println("Intervenant trouverIntervenant)) Intervenant non trouvé.");
                }
            }

            demandeAncienne = demandeDAO.rechercheParEleve(eleve); // On regarde s'il existe déjà une demande non finie faite par cet élève
            if (intervenant != null && demandeAncienne == null) { // On ne peut continuer avec la demande que si on a trouvé un intervenant et que toutes les demandes de soutien de l'élèves sont bien terminées.
                if(afficherLog == true){
                    System.out.print("Service envoyerDemande)) Succès de la recherche d'un intervenant : " + intervenant + "\n");
                }
                
                demande.setIntervenant(intervenant);
                
                intervenant.setDisponible(false); // On marque l'intervenant comme "occupé" jusqu'à la fin du soutien (c'est-à-dire l'envoi du bilan)
                demandeDAO.creerDemande(demande); // Persister la demande
                lancerVisioEleve(eleve);
                JpaUtil.validerTransaction();
                
                Message.envoyerNotification(intervenant.getTelephone(), "Bonjour " + intervenant.getPrenom() + ". Merci de prendre en charge la demande de soutien en " + demande.getMatiere().getDenomination() + " demandée à " + demande.obtenirDateFormatee(demande.getDateDebut()) + " par " + demande.getEleve().getPrenom() + " en classe de " + demande.getEleve().obtenirDenominationClasse());

            } else { // Si pas trouvé d'intervenant, ou demande inachevée déjà en cours
                demande = null;
                JpaUtil.annulerTransaction();
                if(afficherLog == true){
                    System.out.print("Service envoyerDemande)) Echec de la prise en compte de la demande : pas d'intervenant disponible, ou demande de soutien inachevée déjà en cours.\n");
                }
                
            }

        } catch (Exception ex) { // Erreur de persistance / mise à jour de la demande
            if(afficherLog == true){
                System.out.print("Service envoyerDemande)) Echec de la prise en compte de la demande (échec d'enregistrement de la demande)\n");
            }
            
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }
            JpaUtil.annulerTransaction();
            demande = null;

        } finally {
            JpaUtil.fermerContextePersistance();
        }

        return demande; 
    }

    
    // ### SERVICES DE CONNEXION -------------------------------------------------------------------
    
    // Renvoie un objet Eleve présent dans la BD à partir d'un login et d'un mot de passe valides.
    public Eleve connecterEleve(String mail, String motDePasse) {
        Eleve eleve = null; // 'eleve" sera NULL en cas d'erreur d'authentification
        EleveDAO eleveDAO = new EleveDAO();
        try {

            JpaUtil.creerContextePersistance();
            eleve = eleveDAO.rechercheParMail(mail); // Chercher si un élève correspondant au mail existe dans la BD
            if (eleve == null){
                if(afficherLog == true){
                    System.out.println("Eleve rechercheParMail)) Eleve non trouvé.");
                }
            }
            if (!(eleve.getMotDePasse().equals(motDePasse))) { // Puis, vérifier que le mot de passe correspond bien
                eleve = null;
                if(afficherLog == true){
                    System.out.print("Service connecterEleve)) Echec de l'authentification de l'élève.\n");
                }
            }
            else{
                if(afficherLog == true){
                    System.out.print("Service connecterEleve)) Succès de l'authentification de l'élève : " + eleve + "\n");
                }
                
            }
        } catch (Exception ex) {
            if(afficherLog == true){
                System.out.print("Service connecterEleve)) Echec de l'authentification de l'élève : mail et/ou mot de passe invalide(s).\n");
            }
            
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }
        } finally {
            JpaUtil.fermerContextePersistance();
        }

        return eleve;

    }
    
    // Renvoie un objet Intervenant présent dans la BD à partir d'un login valide.
    public Intervenant connecterIntervenant(String login) {
        IntervenantDAO intervenantDAO = new IntervenantDAO();
        Intervenant intervenant = null; // "intervenant" sera NULL en cas de login invalide
        try {

            JpaUtil.creerContextePersistance();
            intervenant = intervenantDAO.rechercheParLogin(login); // Chercher s'il existe un intervenant avec ce login dans la BD
            if (intervenant == null){
                if(afficherLog == true){
                     System.out.println("Intervenant rechercheParLogin)) Intervenant non trouvé.");
                }
            }
            
            else{
                if(afficherLog == true){
                    System.out.print("Service connecterIntervenant)) Succès de l'authentification de l'intervenant : " + intervenant + "\n");
                }
            }
            

        } catch (Exception ex) {
            if(afficherLog == true){
                System.out.print("Service connecterIntervenant)) Echec de l'authentification de l'intervenant : erreur système.\n");
            }
            
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }

        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return intervenant;
    }
    
    

    // ### SERVICES D'ACTIONS CONCRETES DANS L'APPLICATION -----------------------------------------
    
    // Met à jour la date de début de la demande de soutien en cours de l'élève.
    public Boolean lancerVisioEleve(Eleve eleve) {
        Boolean reussi = true;
        DemandeDAO demandeDAO = new DemandeDAO();

        try {

            Demande demande = demandeDAO.rechercheParEleve(eleve); // On cherche la dernière (et unique) demande de soutien non terminée de l'élève.
            
            Date dateDebut = new Date();
            demande.setDateDebut(dateDebut);
            demandeDAO.majDemande(demande); // On met à jour la date de début de la demande, de NULL à l'instant actuel. 
            if(afficherLog == true){
                System.out.print("Service lancerVisioEleve)) Succès du lancement de la visio par l'éleve " + eleve + "\n");
            }
            

        } catch (Exception ex) {
            if(afficherLog == true){
                System.out.print("Service lancerVisioEleve)) Echec du lancement de la visio par l'éleve " + eleve + "\n");
            }
            
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }
            reussi = false;

        }
        return reussi;
    }

    // Incrémente le nombre d'interventions de l'intervenant
    public Boolean lancerVisioIntervenant(Intervenant intervenant) {
        Boolean reussi = true;
        IntervenantDAO intervenantDAO = new IntervenantDAO();

        try {

            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction(); 

            int nombre = intervenant.getNombreInterventions();
            intervenant.setNombreInterventions(nombre + 1);
            intervenantDAO.majIntervenant(intervenant); // Incrémenter puis mettre à jour le nombre d'interventions

            if(afficherLog == true){
                System.out.print("Service lancerVisioIntervenant)) Succès  : l'intervenant " +  intervenant + " a pu rejoindre la visio.\n");
            }
            

        } catch (Exception ex) { // En cas d'échec de la mise à jour de l'intervenant
            if(afficherLog == true){
                System.out.print("Service lancerVisioIntervenant)) Succès  : l'intervenant " + intervenant + " a pu rejoindre la visio.\n");
            }
            
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }
          
            reussi = false;

        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return reussi;
    }

    
    // Met fin à au cours de soutien visio en donnant une date de fin et une durée à la demande.
    public Boolean raccrocherEleve(Eleve eleve) {
        Boolean reussi = true;
        DemandeDAO demandeDAO = new DemandeDAO();
        EtablissementDAO etablissementDAO = new EtablissementDAO();

        try {

            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();            
            
            Demande demande = demandeDAO.rechercheParEleve(eleve); // On cherche toujours la denrière (et unique) demande de soutien inachevée de l'élève.
            
            Date dateFin = new Date();
            Date dateDebut = demande.getDateDebut();
            long dureeMS = dateFin.getTime() - dateDebut.getTime();
            long duree = TimeUnit.SECONDS.convert(dureeMS, TimeUnit.MILLISECONDS); // changer seconds en minutes si nécessaire

            demande.setDateFin(dateFin); // On met à jour la date de fin et la durée (en minutes) de la visio. On part du principe qu'une visio dure moins d'un jour.
            demande.setDuree(duree);
            demandeDAO.majDemande(demande);
            
            // modification des attributs nbDemandes et duréeMoyenne de l'établissement
            Etablissement etablissement = etablissementDAO.rechercheParID(eleve.getEtablissement().getId()); // eleve.getEtablissement a ses variables encore à 0,0
            Long nbDemandes = etablissement.getNbDemandes();
            Long dureeMoyenne = etablissement.getDureeMoyenne();
            
            etablissement.setNbDemandes((long) (nbDemandes+1));
            etablissement.setDureeMoyenne((long)((dureeMoyenne*nbDemandes+duree)/(nbDemandes+1)));
            etablissementDAO.majEtablissement(etablissement);
            eleve.setEtablissement(etablissement);  // obligatoire car on a une ManyToOne dans Eleve, donc les changements dans Etablissements ne sont pas refléter dans la variable établissement de Eleve
            
            JpaUtil.validerTransaction();
            if(afficherLog == true){
                System.out.print("Service raccrocherEleve)) Succès de l'arrêt de la visio par l'éleve : " + eleve + ". Durée : " + duree + " |  Terminée le " + dateFin + "\n");
            }
            

        } catch (Exception ex) { // Si erreur dans la mise à jour de l'objet Demande
            if(afficherLog == true){
                System.out.print("Service raccrocherEleve)) Echec de l'arrêt de la visio par l'élève : " + eleve + "\n");
            }
            
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }
            JpaUtil.annulerTransaction();
            reussi = false;

        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return reussi;

    }


    // Attribue une note à une demande de soutien. Une des deux conditions requises pour qu'une demande de soutien soit "terminée".
    public Boolean evaluerVisio(Eleve eleve, int eval) {
        Boolean reussi = true;
        DemandeDAO demandeDAO = new DemandeDAO();

        try {

            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();
            
            Demande demande = demandeDAO.rechercheParEleve(eleve); // Chercher la dernière (et unique) demande non terminée de l'élève.
            demande.setEvaluation(eval); // Remplir le champ "evaluation" de la demande
            
            if (demande.getBilan() != null) { // Si le bilan a été rédigé par l'intervenant (autre condition requise pour terminer), alors la demande est terminée.
                demande.setFini(true);
                Intervenant intervenant = demande.getIntervenant();
                remettreIntervenantDispo(intervenant); // On remet aussi l'intervenant comme "disponible"
            }

            demandeDAO.majDemande(demande); // Mettre à jour les modifications
            JpaUtil.validerTransaction();
            if(afficherLog == true){
                System.out.print("Service evaluerVisio)) Succès de l'évaluation : l'élève " + eleve + " a choisi la note '" + eval + "'.\n");
            }
            

        } catch (Exception ex) { // En cas d'erreur de mise à jour de la demande
            if(afficherLog == true){
                System.out.print("Service evaluerVisio)) Echec de l'évaluation par l'élève " + eleve + ".\n");
            }
            
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }
            JpaUtil.annulerTransaction();
            reussi = false;

        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return reussi;

    }

    // Attribue un bilan à une demande de soutien et envoie le bilan à l'élève. Une des deux conditions requises pour qu'une demande de soutien soit "terminée".
    public Boolean envoyerBilan(Intervenant intervenant, String bilan) {
        Boolean reussi = true;
        DemandeDAO demandeDAO = new DemandeDAO();
        Demande demande = null;

        try {

            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();
            
            demande = demandeDAO.rechercheParIntervenant(intervenant); // Cherche al dernière (et unique) demande inachevée de l'élève.
            
            String sujet = "", corps = "";
            String expediteur = "contact@instruct.if";
            Eleve eleve = demande.getEleve();
            String pour = eleve.getMail();

            demande.setBilan(bilan); // Remplir le chanp "bilan" de la demande
            if (demande.getEvaluation() != 0) { // Si la demande a aussi été évaluée par l'élève (deuxième condition requise), la demande est "terminée".
                demande.setFini(true);
                remettreIntervenantDispo(intervenant); // On remet aussi l'intervenant en "disponible"
            }

            demandeDAO.majDemande(demande); // Mettre à jour les modifications.
            JpaUtil.validerTransaction();

            sujet = "Bilan après ta visio sur Instruct'IF";
            corps = "Voici le message de la part de " + demande.getIntervenant().getPrenom() + "\n" + bilan;
            Message.envoyerMail(expediteur, pour, sujet, corps);
            
            if(afficherLog == true){
                System.out.print("Service envoyerBilan)) Succès de l'envoi du bilan par l'intervenant : " + intervenant + ".\n");
            }
            

        } catch (Exception ex) { // EN cas d'erreur de mise à jour de la demande
            if(afficherLog == true){
                System.out.print("Service envoyerBilan)) Echec de l'envoi du bilan par l'intervenant : " + intervenant + ".\n");
            }
            
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }
            JpaUtil.annulerTransaction();
            reussi = false;

        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return reussi;

    }


    
    // ### SERVICES D'OBTENTION D'UN OBJET A PARTIR D'UN AUTRE ------------------------------------

    // Permet d'obtenir la dernière (et unique) demande non terminée d'un intervenant donné.
    public Demande obtenirDemandeIntervenant(Intervenant intervenant) {
        Demande demande = null;
        DemandeDAO demandeDAO = new DemandeDAO();
        try {

            JpaUtil.creerContextePersistance();
            demande = demandeDAO.rechercheParIntervenant(intervenant);
            if(afficherLog == true){
                System.out.print("Service obtenirDemandeIntervenant)) Succès de l'obtention détails de la demande en cours : " + demande + "\n");
            }
            

        } catch (Exception ex) { // Erreur interne dans la recherche de la demande
            if(afficherLog == true){
                System.out.print("Service obtenirDemandeIntervenant)) Echec de l'obtention détails de la demande en cours");
            }
            
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }

        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return demande;
    }

    
    
    // ### SERVICES D'OBTENTION DE LISTES ---------------------------------------------------------
    
    // Permet d'obtenir la liste de tous les établissements dans la BD
    public List<Etablissement> obtenirListeEtablissements() {
        List<Etablissement> listeEtablissements = null; // La liste sera NULL en cas d'échec de la recherche
        EtablissementDAO etablissementDAO = new EtablissementDAO();

        try {
            JpaUtil.creerContextePersistance();
            listeEtablissements = etablissementDAO.rechercheToutesLesEtablissements();
            if(afficherLog == true){
                System.out.print("Service obtenirListeEtablissements)) Succès de l'obtention de la liste des établissements.\n");
            }
            

        } catch (Exception ex) {
            if(afficherLog == true){
                System.out.print("Service obtenirListeEtablissements)) Echec de l'obtention de la liste des établissements.\n");
            }
            
            if(afficherTraceErreur == true){
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
        MatiereDAO matiereDAO = new MatiereDAO();
        Matiere matiere;

        try {
            JpaUtil.creerContextePersistance();
            listeMatieres = matiereDAO.rechercheToutesLesMatieres();
            if(afficherLog == true){
                System.out.print("Service obtenirListeMatieres)) Succès de l'obtention de la liste des matières.\n");
            }
            

        } catch (Exception ex) {
            if(afficherLog == true){
                System.out.print("Service obtenirListeMatieres)) Echec de l'obtention de la liste des matières.\n");
            }
            
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }
        } finally {
            JpaUtil.fermerContextePersistance();
        }

        return listeMatieres;
    }

    // Permet d'obtenir la liste de toutes les demandes de soutien prises en compte par un certain intervenant
    public List<Demande> obtenirHistoriqueIntervenant(Intervenant intervenant) {
        List<Demande> listeDemandes = null; // La liste renvoyée sera NULL en cas d'échec
        DemandeDAO demandeDAO = new DemandeDAO();
        Demande demande;

        try {
            JpaUtil.creerContextePersistance();
            listeDemandes = demandeDAO.rechercheToutesLesDemandes(intervenant);
            if(afficherLog == true){
                System.out.print("Service obtenirHistoriqueIntervenant)) Succès de l'obtention de l'historique de l'intervenant " + intervenant + ".\n");
            }
            
    

        } catch (Exception ex) {
            if(afficherLog == true){
                System.out.print("Service obtenirHistoriqueIntervenant)) Echec de l'obtention de l'historique de l'intervenant " + intervenant + ".\n");
            }
            
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }
        } finally {
            JpaUtil.fermerContextePersistance();
        }

        return listeDemandes;
    }
    
  

    // Permet d'obtenir la liste de toutes les demandes de soutien initiées par un certain élève
    public List<Demande> obtenirHistoriqueEleve(Eleve eleve) {
        List<Demande> listeDemandes = null; // La liste renvoyée sera NULL en cas d'échec
        DemandeDAO demandeDAO = new DemandeDAO();
        Demande demande;

        try {
            JpaUtil.creerContextePersistance();
            listeDemandes = demandeDAO.rechercheToutesLesDemandes(eleve);
            if(afficherLog == true){
                System.out.print("Service obtenirHistoriqueEleve)) Succès de l'obtention de l'historique de l'élève " + eleve + ".\n");
            }
            

        } catch (Exception ex) {
            if(afficherLog == true){
                System.out.print("Service obtenirHistoriqueEleve)) Echec de l'obtention de l'historique de l'élève " + eleve + ".\n");
            }
            
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }
        } finally {
            JpaUtil.fermerContextePersistance();
        }

        return listeDemandes;
    }
    
    
    
    // ### SERVICES D'OBTENTION DE STATISTIQUES ---------------------------------------------------
    
    // Permet d'obtenir la matière la plus demandée en soutien par les élèves du réseau.
    public Matiere obtenirMatierePopulaire(){
        Matiere matiere = null; // La matière sera NULL en cas d'échec
        DemandeDAO demandeDAO = new DemandeDAO();
        try {

            JpaUtil.creerContextePersistance();
            matiere = demandeDAO.rechercheMatierePopulaire();
            if (matiere==null){
                if(afficherLog == true){
                    System.out.println("Matiere rechercheMatierePopulaire)) Matiere non trouvée");
                }
            }
            else{
                if(afficherLog == true){
                    System.out.print("Service obtenirMatierePopulaire)) Succès de la recherche : Matiere la plus populaire : " + matiere + ".\n");
                }
            }
            
                
            
        } catch (Exception ex) { // Erreur interne dans la recherche
            if(afficherLog == true){
                System.out.print("Service obtenirMatierePopulaire)) Echec de la recherche.\n");
            }
            
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }
           
            
        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return matiere;
    }
    
    // Permet d'obtenir un tuple du type : (nombre total de demandes de soutien, durée totale additionnée de toutes ces demandes)
    public Long[] obtenirStatsSoutienTotal(){
        Long[] tuple = new Long[2];
        DemandeDAO demandeDAO = new DemandeDAO();
        try {

            JpaUtil.creerContextePersistance();
            tuple = demandeDAO.rechercheStatsSoutienTotal();
            if(afficherLog == true){
                System.out.print("Service obtenirStatsSoutienTotal))  Succès de la recherche. Nombre total de demandes : " + tuple[0] + ", durée totale des soutiens : " + tuple[1] + "min. \n");
            }
            
                
        } catch (Exception ex) { // Erreur interne dans la recherche
            if(afficherLog == true){
                System.out.print("Service obtenirStatsSoutienTotal)) Echec de la recherche.\n");
            }
            
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }
            
        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return tuple;
    }

    // Permet d'obtenir un tuple du type : (nombre moyen de demandes de soutien par élève, durée moyenne totale de tous les soutiens par élève)
    public Long[] obtenirStatsSoutienEleve(){
        Long[] tuple = new Long[2];
        DemandeDAO demandeDAO = new DemandeDAO();
        try {

            JpaUtil.creerContextePersistance();
            tuple = demandeDAO.rechercheStatsSoutienEleve();
            if(afficherLog == true){
                System.out.print("Service obtenirStatsSoutienEleve)) Succès de la recherche. Nombre moyen de demandes par élève : " + tuple[0] + ", durée moyenne de tous les soutien par élève : " + tuple[1] + "min.\n");
            }
            
                
        } catch (Exception ex) { // Erreur interne dans la recherche
            if(afficherLog == true){
                System.out.print("Service obtenirStatsSoutienEleve)) Echec de la recherche.\n");
            }
            
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }
            
        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return tuple;
    }
    
    // Permet d'obtenir un tuple du type : (nombre moyen de demandes de soutien par établissement, durée moyenne totale de tous les soutiens par établissement) 
    public Long[] obtenirStatsSoutienEtablissement(){
        Long[] tuple = new Long[2];
        DemandeDAO demandeDAO = new DemandeDAO();
        try {

            JpaUtil.creerContextePersistance();
            tuple = demandeDAO.rechercheStatsSoutienEtablissement();
            if(afficherLog == true){
                System.out.print("Service obtenirStatsSoutienEtablissement)) Succès de la recherche. Nombre moyen de demandes par établissement : " + tuple[0] + ", durée moyenne de tous les soutien par établissement : " + tuple[1] + "min.\n");
            }
            
                
        } catch (Exception ex) { // Erreur interne dans la recherche
            if(afficherLog == true){
                System.out.print("Service obtenirStatsSoutienEtablissement)) Echec de la recherche.\n");
            }
            
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }
            
        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return tuple;
    }
    
    
    
    // RECHERCHES PAR ID -----------------------------------------------------------------------
    
    public Eleve recupererEleveParID(long id) {
        Eleve eleve = null;
        EleveDAO eleveDAO = new EleveDAO();
        try {

            JpaUtil.creerContextePersistance();
            eleve = eleveDAO.rechercheParID(id);
            if(afficherLog == true){
                System.out.print("Service recupererEleveParID) Succès de la recherche par ID : " + eleve + ".\n");
            }
            
        } catch (Exception ex) {
            if(afficherLog == true){
                System.out.print("Service recupererEleveParID) Echec de la recherche par ID.\n");
            }
            
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }
        } finally {
            JpaUtil.fermerContextePersistance();
        }

        return eleve;
    }

    public Intervenant recupererIntervenantParID(long id) {
        IntervenantDAO intervenantDAO = new IntervenantDAO();
        Intervenant intervenant = null;
        try {
            JpaUtil.creerContextePersistance();
            intervenant = intervenantDAO.rechercheParID(id);
            if(afficherLog == true){
                System.out.print("Service recupererIntervenantParID) Succès de la recherche par ID : " + intervenant + ".\n");
            }
            

        } catch (Exception ex) {

            if(afficherLog == true){
                System.out.print("Service recupererIntervenantParID) Echec de la recherche par ID.\n");
            }
            
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }

        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return intervenant;
    }
    
    public Demande recupererDemandeParID(long id) {
        DemandeDAO demandeDAO = new DemandeDAO();
        Demande demande = null;
        try {
            JpaUtil.creerContextePersistance();
            demande = demandeDAO.rechercheParID(id);
            if(afficherLog == true){
                System.out.print("Service recupererDemandeParID) Succès de la recherche par ID : " + demande + ".\n");
            }
            

        } catch (Exception ex) {

            if(afficherLog == true){
                System.out.print("Service recupererDemandeParID) Echec de la recherche par ID.\n");if(afficherTraceErreur == true){
            }
            
                ex.printStackTrace();
            }
        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return demande;
    }

    public Matiere recupererMatiereParID(long id) {
        MatiereDAO matiereDAO = new MatiereDAO();
        Matiere matiere = null;
        try {
            JpaUtil.creerContextePersistance();
            matiere = matiereDAO.rechercheParID(id);
            if(afficherLog == true){
                System.out.print("Service recupererMatiereParID) Succès de la recherche par ID : " + matiere + ".\n");
            }
            

        } catch (Exception ex) {

            if(afficherLog == true){
                System.out.print("Service recupererMatiereParID) Echec de la recherche par ID.\n");
            }
            
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }

        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return matiere;

    }

    public Etablissement recupererEtablissementParID(long id) {
        EtablissementDAO etablissementDAO = new EtablissementDAO();
        Etablissement etablissement = null;
        try {
            JpaUtil.creerContextePersistance();
            etablissement = etablissementDAO.rechercheParID(id);
            if(afficherLog == true){
                System.out.print("Service recupererEtablissementParID) Succès de la recherche par ID : " + etablissement + ".\n");
            }
            

        } catch (Exception ex) {

            if(afficherLog == true){
                System.out.print("Service recupererEtablissementParID) Echec de la recherche par ID.\n");
            }
            
            if(afficherTraceErreur == true){
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
    
    // METHODES INTERNES ----------------------------------------------------------------------
    
    // Peuple la table INTERVENANT de la BD
    private void initialiserIntervenants() {
        IntervenantDAO intervenantDAO = new IntervenantDAO();

        Etudiant et = new Etudiant("FSAM", "FAVRO", "Samuel", "0642049305", 6, 3, "INSA", "Maths");

        Autre a = new Autre("DONALD", "DONODIO GALVIS", "Florine", "0671150503", 4, 2, "Ingénieur");

        Enseignant en = new Enseignant("ew", "DEKEW", "Simon", "0713200950", 0, 0, "Lycée");

        try {
            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction();
            intervenantDAO.creerIntervenant(en);
            intervenantDAO.creerIntervenant(a);
            intervenantDAO.creerIntervenant(et);
            JpaUtil.validerTransaction();
            
            if(afficherLog == true){
                System.out.print("Service Interne initialiserIntervenants> Succès de l'initialisation des intervenants.\n");
            }
            

        } catch (Exception ex) { // Erreur interne de persistance
            if(afficherLog == true){
                System.out.print("Service Interne initialiserIntervenants> Echec de l'initialisation des intervenants.\n");
            }
            
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }
            
            JpaUtil.annulerTransaction();
        } finally {
            JpaUtil.fermerContextePersistance();
        }

    }

    // Peuple la table MATIERES de la BD
    private void initialiserMatieres() {
        List<String> matieres = new ArrayList();
        matieres.add("Maths");
        matieres.add("Histoire Géographie");
        matieres.add("Francais");

        MatiereDAO matiereDAO = new MatiereDAO();
        Matiere matiere;

        try {
            JpaUtil.creerContextePersistance();

            JpaUtil.ouvrirTransaction(); // Tip : pas dans les dao, car sinon que des transactions unitaires (un seul tuple échangé)

            for (String nom : matieres) {
                matiere = new Matiere(nom);
                matiereDAO.creerMatiere(matiere); // Persistance de la matière
            }
            if(afficherLog == true){
                System.out.print("Service Interne initialiserMatieres> Succès de l'initialisation des matières.\n");
            }
            
            JpaUtil.validerTransaction();

        } catch (Exception ex) {
            if(afficherLog == true){
                System.out.print("Service Interne initialiserMatieres> Echec de l'initialisation des matières.\n");
            }
            
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }
            JpaUtil.annulerTransaction();
        } finally {
            JpaUtil.fermerContextePersistance();
        }

    }

    // Renvoie un objet Etablissement (sans le persister) correspondant aux données fournies par EducNetAPI à partir d'un code d'établissement.
    private Etablissement chercherInfosEtablissement(String code,int classe) {
        
        EducNetApi api = new EducNetApi();
        Etablissement etablissement = null; // L'établissement renvoyé sera NULL en cas d'échec

        try {
            List<String> result;
            if (classe>=3){
                result = api.getInformationCollege(code);
            }
            else{
                result = api.getInformationLycee(code);
            }
            
            if (result != null) { // Si le code établissement est bon, on crée l'objet Etablissement.
                etablissement = new Etablissement(code, result.get(1), result.get(2), result.get(4), result.get(5), result.get(6), result.get(8));
                if(afficherLog == true){
                    System.out.print("Service Interne chercherInfosEtablissement> Succès de la création d'un nouvel établissement : " + etablissement + ".\n");
                }
                
            }

        } catch (Exception ex) {
            if(afficherLog == true){
                System.out.print("Service Interne chercherInfosEtablissement> Echec de la création d'un nouvel établissement.\n");
            }
            
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }
        }

        return etablissement;
    }

    
    // Remet l'intervenant correspondant comme à nouveau disponible.
    private Boolean remettreIntervenantDispo(Intervenant intervenant) {
        Boolean reussi = true;
        IntervenantDAO intervenantDAO = new IntervenantDAO();

        try {

            intervenant.setDisponible(true); // On remet l'intervenant à "disponible" et on met à jour
            intervenantDAO.majIntervenant(intervenant);

            if(afficherLog == true){
                System.out.print("Service Interne remettreIntervenantDispo> Succès : l'intervenant " + intervenant + " est à nouveau disponible.\n");
            }
            

        } catch (Exception ex) { // Si erreur dans la mise à jour de l'objet Intervenant
            if(afficherLog == true){
                System.out.print("Service Interne remettreIntervenantDispo> Echec : l'intervenant " + intervenant + " n'a pas être remis comme disponible.\n");
            }
            
            if(afficherTraceErreur == true){
                ex.printStackTrace();
            }

            JpaUtil.annulerTransaction();
            reussi = false;

        }
        return reussi;

    }
    
    /*public Intervenant connecterIntervenant(String login){
        try {

            JpaUtil.creerContextePersistance();
            JpaUtil.ouvrirTransaction(); 
            
            JpaUtil.validerTransaction();
            
            System.out.print("Succès connexion intervenant." );
                
            
        } catch (Exception ex) {
            System.out.print("Echec connexion intervenant");

            JpaUtil.annulerTransaction();
            
            
        } finally {
            JpaUtil.fermerContextePersistance();
        }
        return intervenant;
    }*/
}
