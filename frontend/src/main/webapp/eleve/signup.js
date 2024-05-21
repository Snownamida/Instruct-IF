function signup() { // Fonction appelée lors du clic sur le bouton m'inscrire
    // Récupération de la valeur des champs du formulaire
    const champNom = $('#champ-nom').val();
    const champPrenom = $('#champ-prenom').val();
    const champJour = $('#champ-jour').val();
    const champMois = $('#champ-mois').val();
    const champAnnee = $('#champ-annee').val();
    const champCode = $('#champ-code').val();
    const champClasse = $('#champ-classe').val();
    const champMail = $('#champ-mail').val();
    const champPassword = $('#champ-password').val();
    // Appel AJAX
    $.ajax({
        url: '../ActionServlet',
        method: 'POST',
        data: {
            todo: 'inscrire',
            lastName: champNom,
            firstName: champPrenom,
            birthday: champJour + '/' + champMois + '/' + champAnnee,
            codeEtablissement: champCode,
            class: champClasse,
            mail: champMail,
            password: champPassword
        },
        dataType: 'json'
    })
        .done(function (response) { // Fonction appelée en cas d'appel AJAX réussi
            console.log('Response', response); // LOG dans Console Javascript
        })
        .fail(function (error) { // Fonction appelée en cas d'erreur lors de l'appel AJAX
            console.log('Error', error); // LOG dans Console Javascript
            alert("Erreur lors de l'appel AJAX");
        })
        .always(function () { // Fonction toujours appelée
        });
}