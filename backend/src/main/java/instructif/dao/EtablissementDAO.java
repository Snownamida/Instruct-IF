/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package instructif.dao;

import java.util.List;
import javax.persistence.Query;
import instructif.metier.modele.Etablissement;

/**
 *
 * @author mbaratova
 */
public class EtablissementDAO {
    public void creerEtablissement(Etablissement etablissement) {
        JpaUtil.obtenirContextePersistance().persist(etablissement);
        // Pour ne pas avoir d'erreur, faire "ADD CLASS" dans Persistence ! Et si on ne voit pas la classe, l'ajouter manuellement dans le code source de Persistence.
    }
    
    public void majEtablissement(Etablissement etablissement) {
        JpaUtil.obtenirContextePersistance().merge(etablissement);
    }
    
    public Etablissement rechercheParCode(String unCode) {
       Etablissement etablissement;
        try{
            Query query = JpaUtil.obtenirContextePersistance().createQuery("SELECT e FROM Etablissement e WHERE e.code = :unCode",Etablissement.class);
            query.setParameter("unCode",unCode);
            // pas * dans les query !!
            // "Query" est une classe de JPA. Elle doit donc être exclusivement manipulée par la DAO, et non par les services !
        
            etablissement = (Etablissement) query.getSingleResult();
        }
        catch(Exception ex){
            etablissement = null;
            
        }
        return etablissement;
    }
    
    public Etablissement rechercheParID(long id) {
        return JpaUtil.obtenirContextePersistance().find(Etablissement.class, id); // Retourne NULL si pas trouvé (pas d'exception)
    }
    
    public List<Etablissement> rechercheToutesLesEtablissements() {
        Query query = JpaUtil.obtenirContextePersistance().createQuery("SELECT e FROM Etablissement e",Etablissement.class);
        List <Etablissement> liste = query.getResultList();
        return liste;
    }
}
