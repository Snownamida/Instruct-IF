$(document).ready(() => {
  $.ajax({
    url: "../ActionServlet",
    method: "POST",
    data: { todo: "my-history" },
    dataType: "json",
  })
    .done(function (response) {
      const historyTableBody = $("#historyTableBody");
      response?.forEach(function (demande) {
        const row =
          "<tr>" +
          "<td>" +
          new Date(demande.dateDebut).toLocaleDateString() +
          "</td>" +
          "<td>" +
          demande.matiere.denomination +
          "</td>" +
          "<td>" +
          demande.eleveDto.nom +
          " " +
          demande.eleveDto.prenom +
          "</td>" +
          "<td>" +
          formatDuration(demande.duree) +
          "</td>" +
          '<td><button type="button" class="btn secondary" onclick="showBilan(\'' +
          demande.bilan +
          "')\">Voir bilan</button></td>" +
          "</tr>";
        historyTableBody.append(row);
      });
    })
    .fail(function (error) {
      console.error(
        "Erreur lors de la récupération de l'historique des demandes:",
        error
      );
    });
});

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

function showBilan(bilan) {
  const dialog = document.getElementById("bilan-dialog");
  const bilanText = document.getElementById("bilan-text");
  bilanText.textContent = bilan;
  dialog.showModal();
}

function closeBilan() {
  const dialog = document.getElementById("bilan-dialog");
  dialog.close();
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
