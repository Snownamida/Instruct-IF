$(document).ready(() => {
  $.ajax({
    url: "../ActionServlet",
    method: "POST",
    data: { todo: "my-info" },
    dataType: "json",
  })
    .done(function (response) {
      if (!response) return;
      const aRemplir = document.getElementById("content-div");
      aRemplir.innerHTML =
        `<p>${response.nom} ${response.prenom}</p>` +
        `<p>Téléphone : ${response.telephone}</p>` +
        `<p>Type : ${response.type}</p>` +
        (response.universite
          ? `<p>Université : ${response.universite}</p>`
          : "") +
        (response.specialite
          ? `<p>Specialité : ${response.specialite}</p>`
          : "") +
        (response.typeEtablissementExercice
          ? `<p>Type d'établissement d'exercice : ${response.typeEtablissementExercice}</p>`
          : "") +
        (response.activite ? `<p>Activité : ${response.activite}</p>` : "");
    })
    .fail(function (error) {
      console.error("Erreur lors de la récupération des données :", error);
      alert("Erreur lors de la récupération des données");
    });
});

function logout() {
  $.ajax({
    url: "../ActionServlet",
    method: "POST",
    data: { todo: "signout" },
    dataType: "json",
  })
    .done((response) => {
      window.location.href = "../index.html";
    })
    .fail((error) => {
      console.error("Erreur lors de la déconnexion:", error);
    });
}
