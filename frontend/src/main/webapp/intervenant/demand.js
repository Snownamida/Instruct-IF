let nom_eleve = "";
let prenom_eleve = "";

function checkCurrentDemande() {
  $.ajax({
    url: "../ActionServlet",
    method: "POST",
    data: { todo: "current-demande" },
    dataType: "json",
  })
    .done(function (response) {
      const contentdiv = document.getElementById("content-div");
      if (response === null) {
        contentdiv.innerHTML = "<h1>Aucune demande de cours</h1>";
      } else {
        nom_eleve = response.eleveDto.nom;
        prenom_eleve = response.eleveDto.prenom;
        contentdiv.innerHTML = `<h1>Demande de cours:</h1><div class='details'>
            <div class='left-column'><p><strong>Demande de cours :</strong></p>
              <p>Élève : ${" " + nom_eleve + " " + prenom_eleve}</p>
              <p>Classe : ${" " + response.eleveDto.classe}</p>
              <p>Établissement : ${
                " " + response.eleveDto?.etablissement?.nom
              }</p></div>
              <div class='right-column'>
              <p>Cours :</p>
              <p>Matière : ${" " + response.matiere?.denomination}</p>
              <p>Description : ${" " + response.description}</p>
            </div>
            </div><button type='button' class='launch-button' onclick='launchVisio()'>Lancer Visio</button>`;
      }
    })
    .fail(function (error) {
      console.error("Erreur lors de la récupération de la demande:", error);
    });
}

function launchVisio() {
  $.ajax({
    url: "../ActionServlet",
    method: "POST",
    data: { todo: "start-video" },
    dataType: "json",
  })
    .done(function (response) {
      console.log("start video intervenat : ", response);
      const dialog = document.getElementById("visio-dialog");
      dialog.showModal();
    })
    .fail(function (error) {
      console.error("Erreur lors du lancement de la visio intervenant:", error);
    });
}

function endVisio() {
  const endButton = document.getElementById("end-visio-button");
  const sendButton = document.getElementById("send-button");
  const visioImg = document.getElementById("visio-img");
  visioImg.src = "../images/visio_off.png";
  sendButton.disabled = false;
  endButton.disabled = true;
  checkBilan();
}

function sendBilan() {
  const bilan = document.getElementById("bilan").value;
  $.ajax({
    url: "../ActionServlet",
    method: "POST",
    data: { todo: "send-bilan", bilan: bilan },
    dataType: "json",
  })
    .done(function (response) {
      console.log("send bilan : ", response);
      const dialog = document.getElementById("visio-dialog");
      dialog.close();
      checkCurrentDemande();
    })
    .fail(function (error) {
      console.error("Erreur lors de l'envoi du bilan:", error);
    });
}

function checkBilan() {
  const sendButton = document.getElementById("send-button");
  const bilan = document.getElementById("bilan").value;
  const endButton = document.getElementById("end-visio-button");
  sendButton.disabled = bilan.trim() === "" || !endButton.disabled;
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

document.addEventListener("DOMContentLoaded", function () {
  checkCurrentDemande();
});
