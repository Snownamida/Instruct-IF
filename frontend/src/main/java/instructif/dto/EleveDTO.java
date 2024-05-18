package instructif.dto;

import instructif.metier.modele.Eleve;
import instructif.metier.modele.Etablissement;

public class EleveDto {
    private Long id;
    private String nom;
    private String prenom;
    private String mail;
    private String dateNaissance;
    private int classe;
    private Etablissement etablissement;

    public EleveDto(Long id, String nom, String prenom, String mail, String dateNaissance, int classe,
            Etablissement etablissement) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.dateNaissance = dateNaissance;
        this.classe = classe;
        this.etablissement = etablissement;
    }

    public EleveDto(Eleve eleve) {
        this.id = eleve.getId();
        this.nom = eleve.getNom();
        this.prenom = eleve.getPrenom();
        this.mail = eleve.getMail();
        this.dateNaissance = eleve.getDateNaissance();
        this.classe = eleve.getClasse();
        this.etablissement = eleve.getEtablissement();
    }

    @Override
    public String toString() {
        return "EleveDto [id=" + id + ", nom=" + nom + ", prenom=" + prenom + ", mail=" + mail + ", dateNaissance="
                + dateNaissance + ", classe=" + classe + ", etablissement=" + etablissement + "]";
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public int getClasse() {
        return classe;
    }

    public void setClasse(int classe) {
        this.classe = classe;
    }

    public Etablissement getEtablissement() {
        return etablissement;
    }

    public void setEtablissement(Etablissement etablissement) {
        this.etablissement = etablissement;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
