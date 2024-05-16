/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package instructif.dao;

import javax.persistence.Query;
import instructif.metier.modele.Intervenant;

/**
 *
 * @author mbaratova
 */
public class IntervenantDAO {
    public void creerIntervenant(Intervenant intervenant) {
        JpaUtil.obtenirContextePersistance().persist(intervenant);
        
    }
    public void majIntervenant(Intervenant intervenant) {
        JpaUtil.obtenirContextePersistance().merge(intervenant);
        
    }
    
    public Intervenant trouverIntervenant(int classe){
        
        Intervenant resultat = null;
        
        try{
            Query query = JpaUtil.obtenirContextePersistance().createQuery("SELECT i FROM Intervenant i WHERE i.disponible = true AND :classe <= i.niveauMin AND :classe >= i.niveauMax ORDER BY i.nombreInterventions ",Intervenant.class);
            query.setParameter("classe",classe);
            query.setMaxResults(1);
            // pas * dans les query !!
            // "Query" est une classe de JPA. Elle doit donc être exclusivement manipulée par la DAO, et non par les services !
            resultat = (Intervenant)(query.getSingleResult());
        }
        catch(Exception ex){
        }
        
        return resultat;
        
    }
    
    public Intervenant rechercheParID(long id) {
        return JpaUtil.obtenirContextePersistance().find(Intervenant.class, id); // Retourne NULl si pas trouvé (pas d'exception)
    }
    
    public Intervenant rechercheParLogin(String login) {
        Intervenant intervenant = null;
        try{
            Query query = JpaUtil.obtenirContextePersistance().createQuery("SELECT i FROM Intervenant i WHERE i.login = :unLogin",Intervenant.class);
            query.setParameter("unLogin",login);
            intervenant = (Intervenant) query.getSingleResult();
        } catch (Exception ex) {
        }
        return intervenant;
    }
    
    
    
}
