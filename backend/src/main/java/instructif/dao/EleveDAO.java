/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package instructif.dao;

import java.util.List;
import javax.persistence.Query;
import instructif.metier.modele.Eleve;

/**
 *
 * @author mbaratova
 */
//  DAO (Data Access Object) est un design pattern qui permet de faire le lien
// entre la couche métier et la couche de persistance
public class EleveDAO { //JpaUtil.obtenirContextePersistence

    public void creerEleve(Eleve eleve) {
        JpaUtil.obtenirContextePersistance().persist(eleve);
        // Pour ne pas avoir d'erreur, faire "ADD CLASS" dans Persistence ! Et si on ne voit pas la classe, l'ajouter manuellement dans le code source de Persistence.
    }

    public Eleve rechercheParMail(String unMail) {
        Eleve eleve = null;
        try{
            Query query = JpaUtil.obtenirContextePersistance().createQuery("SELECT e FROM Eleve e WHERE e.mail = :unMail",Eleve.class);
            query.setParameter("unMail",unMail);
            // pas * dans les query !!
            // "Query" est une classe de JPA. Elle doit donc être exclusivement manipulée par la DAO, et non par les services !
            eleve = (Eleve) query.getSingleResult();
        } catch(Exception ex) {
            
        }
        
        return eleve;
    }
    
    public List<Eleve> rechercheTousLesEleves() {
        // PAS BESOIN ????
        Query query = JpaUtil.obtenirContextePersistance().createQuery("SELECT e FROM Eleve e",Eleve.class);
        List <Eleve> liste = query.getResultList();
        return liste;
    }
    
    public Eleve rechercheParID(long id) {
        return JpaUtil.obtenirContextePersistance().find(Eleve.class, id); // Retourne NULl si pas trouvé (pas d'exception)
    }

}
