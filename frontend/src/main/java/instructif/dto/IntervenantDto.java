package instructif.dto;

import instructif.metier.modele.Intervenant;

public class IntervenantDto {
    private Long id;
    private String login;
    private String nom;
    private String prenom;
    private String telephone;
    private int niveauMin;
    private int niveauMax;
    private int nombreInterventions;
    private Boolean disponible;

    public IntervenantDto(Intervenant intervenant) {
        this.id = intervenant.getId();
        this.login = intervenant.getLogin();
        this.nom = intervenant.getNom();
        this.prenom = intervenant.getPrenom();
        this.telephone = intervenant.getTelephone();
        this.niveauMin = intervenant.getNiveauMin();
        this.niveauMax = intervenant.getNiveauMax();
        this.nombreInterventions = intervenant.getNombreInterventions();
        this.disponible = intervenant.getDisponible();
    }

    public IntervenantDto(Long id, String login, String nom, String prenom, String telephone, int niveauMin,
            int niveauMax, int nombreInterventions, Boolean disponible) {
        this.id = id;
        this.login = login;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.niveauMin = niveauMin;
        this.niveauMax = niveauMax;
        this.nombreInterventions = nombreInterventions;
        this.disponible = disponible;
    }

    @Override
    public String toString() {
        return "IntervenantDto [id=" + id + ", login=" + login + ", nom=" + nom + ", prenom=" + prenom + ", telephone="
                + telephone + ", niveauMin=" + niveauMin + ", niveauMax=" + niveauMax + ", nombreInterventions="
                + nombreInterventions + ", disponible=" + disponible + "]";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public int getNiveauMin() {
        return niveauMin;
    }

    public void setNiveauMin(int niveauMin) {
        this.niveauMin = niveauMin;
    }

    public int getNiveauMax() {
        return niveauMax;
    }

    public void setNiveauMax(int niveauMax) {
        this.niveauMax = niveauMax;
    }

    public int getNombreInterventions() {
        return nombreInterventions;
    }

    public void setNombreInterventions(int nombreInterventions) {
        this.nombreInterventions = nombreInterventions;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

}
