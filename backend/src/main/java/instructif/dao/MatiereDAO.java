/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package instructif.dao;

import java.util.List;
import javax.persistence.Query;
import instructif.metier.modele.Eleve;
import instructif.metier.modele.Matiere;

/**
 *
 * @author mbaratova
 */
public class MatiereDAO {
    public void creerMatiere(Matiere matiere) {
        JpaUtil.obtenirContextePersistance().persist(matiere);
        // Pour ne pas avoir d'erreur, faire "ADD CLASS" dans Persistence !
    }
    
    public List<Matiere> rechercheToutesLesMatieres() {
        Query query = JpaUtil.obtenirContextePersistance().createQuery("SELECT m FROM Matiere m",Matiere.class);
        List <Matiere> liste = query.getResultList();
        return liste;
    }
    
    public Matiere rechercheParID(long id) {
        return JpaUtil.obtenirContextePersistance().find(Matiere.class, id); // Retourne NULl si pas trouvé (pas d'exception)
    }
    
    
    
    
}
