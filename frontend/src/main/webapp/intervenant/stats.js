function getStatsTable() {
  $.ajax({
    url: "../ActionServlet",
    method: "POST",
    data: { todo: "etabs" },
    dataType: "json",
  })
    .done(function (response) {
      if (!response) return;
      const etabs = $("#etabs");
      response.forEach(function (etab) {
        const row =
          "<tr>" +
          "<td>" +
          etab.nom +
          "</td>" +
          "<td>" +
          etab.codeDepartement +
          "</td>" +
          "<td>" +
          etab.nomCommune +
          "</td>" +
          "<td>" +
          etab.ips +
          "</td>" +
          "<td>" +
          etab.nbDemandes +
          "</td>" +
          "<td>" +
          etab.dureeMoyenne +
          "</td>" +
          "</tr>";
        etabs.append(row);
      });
    })
    .fail(function (error) {
      console.error(
        "Erreur lors de la récupération du tableau de stats :",
        error
      );
      alert("Erreur lors de la récupération du tableau de stats");
    });
}
function getStats() {
  $.ajax({
    url: "../ActionServlet",
    method: "POST",
    data: { todo: "stats" },
    dataType: "json",
  })
    .done(function (response) {
      if (!response) return;
      const aRemplir = document.getElementById("content-div");
      aRemplir.innerHTML = `<p>Matière la plus demandée : ${response.populate_matiere?.denomination}</p>
          <p>Au total : ${response.nb_soutien} soutiens pour une durée totale de ${response.time_total_soutien} minutes</p>
          <p>Par eleve, en moyenne : ${response.nb_average_soutien_per_eleve} soutiens pour une durée moyenne de ${response.time_average_soutien_per_eleve} minutes</p>
          <p>Par établissement, en moyenne : ${response.nb_average_soutien_per_etab} soutiens pour une durée moyenne de ${response.time_average_soutien_per_etab} minutes</p>`;
    })
    .fail(function (error) {
      console.error("Erreur lors de la récupération des stats :", error);
      alert("Erreur lors de la récupération des stats");
    });
}

// Fonction pour formater la durée en heures, minutes et secondes
function formatDuration(duration) {
  const seconds = parseInt(duration, 10) % 60;
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  return (
    hours +
    "h" +
    (minutes < 10 ? "0" : "") +
    minutes +
    "min" +
    (seconds < 10 ? "0" : "") +
    seconds +
    "s"
  );
}

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
$(document).ready(() => {
  getStats();
  getStatsTable();
});
