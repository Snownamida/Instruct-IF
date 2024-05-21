function login() { // Fonction appelée lors du clic sur le bouton me connecter
    // Récupération de la valeur des champs du formulaire
    const champMail = $('#champ-mail').val();
    const champPassword = $('#champ-password').val();
    // Appel AJAX
    $.ajax({
        url: '../ActionServlet',
        method: 'POST',
        data: {
            todo: 'connecter-e',
            mail: champMail,
            password: champPassword
        },
        dataType: 'json'
    })
        .done(function (response) { // Fonction appelée en cas d'appel AJAX réussi
            console.log('Response', response); // LOG dans Console Javascript
            if (response !== null) { // Si la connexion a réussi
                window.location.href = 'index.html'; // Redirection vers la page d'accueil élève
            } else { // Si la connexion a échoué
                alert("Adresse mail ou mot de passe incorrect"); // Affichage d'une alerte
            }
        })
        .fail(function (error) { // Fonction appelée en cas d'erreur lors de l'appel AJAX
            console.log('Error', error); // LOG dans Console Javascript
            alert("Erreur lors de l'appel AJAX");
        })
        .always(function () { // Fonction toujours appelée
        });
}