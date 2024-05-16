/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package instructif.dao;

import java.util.List;
import javax.persistence.Query;
import instructif.metier.modele.Demande;
import instructif.metier.modele.Eleve;
import instructif.metier.modele.Intervenant;
import instructif.metier.modele.Matiere;
import instructif.metier.modele.Etablissement;

/**
 *
 * @author mbaratova
 */
public class DemandeDAO {
    public void creerDemande(Demande demande) {
        JpaUtil.obtenirContextePersistance().persist(demande);
        // Pour ne pas avoir d'erreur, faire "ADD CLASS" dans Persistence ! Et si on ne voit pas la classe, l'ajouter manuellement dans le code source de Persistence.
    }
    
    public void majDemande(Demande demande) {
        JpaUtil.obtenirContextePersistance().merge(demande);
        // Pour ne pas avoir d'erreur, faire "ADD CLASS" dans Persistence ! Et si on ne voit pas la classe, l'ajouter manuellement dans le code source de Persistence.
    }
    
    public Demande rechercheParEleve(Eleve eleve){
        Demande demande = null;
        try{
            String jpql = "SELECT d FROM Demande d WHERE d.eleve = :unEleve AND d.fini = FALSE";
            Query query = JpaUtil.obtenirContextePersistance().createQuery(jpql,Demande.class);
            query.setParameter("unEleve",eleve);
            demande = (Demande ) query.getSingleResult();
        } catch(Exception ex) {
            System.out.println("Demande rechercheParEleve)) l'eleve n'a pas de demande." + eleve );
        }
        return demande;
    }
    
    public Demande rechercheParIntervenant(Intervenant intervenant){
        Demande demande = null;
        try{
            String jpql = "SELECT d FROM Demande d WHERE d.intervenant = :unIntervenant AND d.fini = FALSE";
            Query query = JpaUtil.obtenirContextePersistance().createQuery(jpql,Demande.class);
            query.setParameter("unIntervenant",intervenant);
            demande = (Demande ) query.getSingleResult();
        } catch(Exception ex) {
            System.out.println("Demande rechercheParIntervenant)) l'intervenant n'a pas de demande." + intervenant);
        }
        return demande;
    }
    
    public Demande rechercheParID(long id) {
        return JpaUtil.obtenirContextePersistance().find(Demande.class, id); // Retourne NULl si pas trouvé (pas d'exception)
    }
    
    public List<Demande> rechercheToutesLesDemandes(Eleve eleve){
        Query query = JpaUtil.obtenirContextePersistance().createQuery("SELECT d FROM Demande d WHERE d.eleve = :eleve",Demande.class);
        query.setParameter("eleve", eleve);
        List <Demande> liste = query.getResultList();
        return liste;
    }
    
    public List<Demande> rechercheToutesLesDemandes(Intervenant intervenant){
        Query query = JpaUtil.obtenirContextePersistance().createQuery("SELECT d FROM Demande d WHERE d.intervenant= :intervenant",Demande.class);
        query.setParameter("intervenant", intervenant);
        List <Demande> liste = query.getResultList();
        return liste;
    }
    
    public Matiere rechercheMatierePopulaire(){
        Matiere matiere = null;
        try{            
            String jpql = "SELECT count(d.matiere) somme FROM Demande d GROUP BY d.matiere ORDER BY somme DESC";
            Query query = JpaUtil.obtenirContextePersistance().createQuery(jpql,Demande.class);
            query.setMaxResults(1);
            Long sommeMax = (Long) (query.getSingleResult());
            
            jpql = "SELECT d.matiere FROM Demande d GROUP BY d.matiere HAVING count(d.matiere) = :unNombre";
            query = JpaUtil.obtenirContextePersistance().createQuery(jpql,Demande.class);
            query.setParameter("unNombre", sommeMax);
            query.setMaxResults(1);
            matiere = (Matiere) (query.getSingleResult());            
            
        } catch(Exception ex) {
            
        }
        return matiere;
    }
    
    public Long[] rechercheStatsSoutienTotal(){
        Long[] tuple = new Long[2];
        
        String jpql = "SELECT count(d) FROM Demande d";
        Query query = JpaUtil.obtenirContextePersistance().createQuery(jpql,Demande.class);
        tuple[0] = (Long) query.getSingleResult(); // nombre
        
        jpql = "SELECT sum(d.duree) FROM Demande d";
        query = JpaUtil.obtenirContextePersistance().createQuery(jpql,Demande.class);
        tuple[1] = (Long) query.getSingleResult(); // durée
        
        if (tuple[0] == null) tuple[0] = (long) 0;
        if (tuple[1] == null) tuple[1] = (long) 0;
        return tuple;
        
    }
    
    public Long[] rechercheStatsSoutienEleve(){
        Long[] total;
        Long[] tuple = new Long[2];
        
        total = rechercheStatsSoutienTotal();
        
        String jpql = "SELECT count(e) FROM Eleve e";
        Query query = JpaUtil.obtenirContextePersistance().createQuery(jpql,Eleve.class);
        Long nbEleve = (Long) query.getSingleResult();
        
        if (nbEleve == null){
            tuple[0] = (long) 0;
            tuple[1] = (long) 0;
        }
        else{
            tuple[0] = (long) total[0]/nbEleve;
            tuple[1] = (long) total[1]/nbEleve;
        };
        
        return tuple;
        
    }
    
    public Long[] rechercheStatsSoutienEtablissement(){
        Long[] total;
        Long[] tuple = new Long[2];
        
        total = rechercheStatsSoutienTotal();
        
        String jpql = "SELECT count(e) FROM Etablissement e";
        Query query = JpaUtil.obtenirContextePersistance().createQuery(jpql,Etablissement.class);
        Long nbEtab = (Long) query.getSingleResult();
        
        if (nbEtab == null){
            tuple[0] = (long) 0;
            tuple[1] = (long) 0;
        }
        else{
            tuple[0] = (long) total[0]/nbEtab;
            tuple[1] = (long) total[1]/nbEtab;
        };
        
        return tuple;
        
    }

    
}
