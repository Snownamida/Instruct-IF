// Fonction pour récupérer les informations de l'élève depuis le serveur
function getUserDetails() {
    $.ajax({
        url: '../ActionServlet',
        method: 'POST',
        data: { todo: 'my-info' },
        dataType: 'json'
    })

        .done(function (response) {
            $('#userFullName').text(response.nom + ' ' + response.prenom);
        })
        .fail(function (error) {
            console.error('Erreur lors de la récupération des informations de l\'élève:', error);
        });
}

// Fonction pour récupérer les matières depuis le serveur
function getMatieres() {
    $.ajax({
        url: '../ActionServlet',
        method: 'POST',
        data: { todo: 'get-matieres' },
        dataType: 'json'
    })
        .done(function (response) {
            const matiereSelect = $('#matiereSelect');
            response.forEach(function (matiere) {
                matiereSelect.append('<option value="' + matiere.id + '">' + matiere.denomination + '</option>');
            });
        })
        .fail(function (error) {
            console.error('Erreur lors de la récupération des matières:', error);
        });
}

// Fonction pour récupérer l'historique des demandes depuis le serveur
function getRequestsHistory() {
    $.ajax({
        url: '../ActionServlet',
        method: 'POST',
        data: { todo: 'my-history' },
        dataType: 'json'
    })
        .done(function (response) {
            const requestsTableBody = $('#requestsTableBody');
            response.forEach(function (demande) {
                const row = '<tr>' +
                    '<td>' + new Date(demande.dateDebut).toLocaleDateString() + '</td>' +
                    '<td>' + demande.matiere.denomination + '</td>' +
                    '<td>' + demande.intervenantDto.nom + ' ' + demande.intervenantDto.prenom + '</td>' +
                    '<td>' + formatDuration(demande.duree) + '</td>' +
                    '<td>' + demande.description + '</td>' +
                    '</tr>';
                requestsTableBody.append(row);

            });
        })
        .fail(function (error) {
            console.error('Erreur lors de la récupération de l\'historique des demandes:', error);
        });
}
// Fonction pour envoyer la demande de soutien au serveur
function sendRequest() {
    const matiereId = $('#matiereSelect').val();
    const description = $('#description').val();
    $.ajax({
        url: '../ActionServlet',
        method: 'POST',
        data: { todo: 'send-demande', matiereId: matiereId, description: description },
    })
        .done(function (response) {
            if (response !== null) {
                const dialog = document.getElementById('visio-dialog');
                dialog.open = true;
                $('#TeacherFullName').text(response.intervenantDto.prenom + ' ' + response.intervenantDto.nom);
            } else {
                alert('Demande de soutien refusée');
            }
        })
        .fail(function (error) {
            alert('Erreur lors de l\'envoi de la demande');
        });
}

// Fonction pour envoyer l'évaluation au serveur
function evaluate() {
    const evaluation = $('input[name="evaluation"]:checked').val();
    $.ajax({
        url: '../ActionServlet',
        method: 'POST',
        data: { todo: 'evaluate', evaluation: evaluation },
    })
        .done(function (response) {
            console.log('Evaluation envoyée');
            const dialog = document.getElementById('evaluate-dialog');
            dialog.open = false;
        })
        .fail(function (error) {
            alert('Erreur lors de l\'envoi de l\'évaluation');
        });
}

// Fonction pour terminer la visio
function EndVisio() {
    $.ajax({
        url: '../ActionServlet',
        method: 'POST',
        data: { todo: 'end-video' },
        dataType: 'json'
    })
        .done(function (response) {
            console.log('Response', response);
            alert("Visio terminée");
            const dialog_visio = document.querySelector('#visio-dialog');
            const dialog_evaluate = document.getElementById('evaluate-dialog');
            dialog_visio.open = false;
            dialog_evaluate.open = true;
        })
        .fail(function (error) {
            console.error('Erreur lors de la fin de la visio:', error);
            alert("Erreur lors de la fin de la visio");
        });
}
// Fonction pour formater la durée en heures, minutes et secondes
function formatDuration(duration) {
    const seconds = parseInt(duration, 10) % 60;
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    return hours + 'h' + (minutes < 10 ? '0' : '') + minutes + 'min' + (seconds < 10 ? '0' : '') + seconds + 's';
}

// Fonction pour vérifier l'état des champs et activer/désactiver le bouton
function checkFormFields() {
    const matiereSelected = $('#matiereSelect').val() !== '';
    const descriptionFilled = $('#description').val().trim() !== '';
    $('#submitRequestButton').prop('disabled', !(matiereSelected && descriptionFilled));
}

// Appeler les fonctions pour récupérer les informations de l'élève, les matières et l'historique des demandes lorsque la page est chargée
$(document).ready(function () {
    getUserDetails();
    getMatieres();
    getRequestsHistory();

    // Ajouter les gestionnaires d'événements pour vérifier les champs de formulaire
    $('#matiereSelect, #description').on('input change', checkFormFields);
});

document.addEventListener("DOMContentLoaded", function () {
    const evaluationOptions = document.querySelectorAll(
        'input[name="evaluation"]'
    );
    const submitButton = document.getElementById("submitButton");

    evaluationOptions.forEach((option) => {
        option.addEventListener("change", function () {
            submitButton.disabled = false;
            evaluationOptions.forEach((opt) => {
                if (opt.checked) {
                    opt.parentNode.classList.add("selected");
                } else {
                    opt.parentNode.classList.remove("selected");
                }
            });
        });
    });
});