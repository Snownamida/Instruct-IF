/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import instructif.dao.JpaUtil;

/**
 *
 * @author mbaratova
 */
// Pour refresh la vue de la BD, il faut refresh la BD mais AUSSI réexécuter
// l'instruction SQL qui permet d'afficher les premières lignes !
public class BackendTest {

    public static void main(String[] args) {

        JpaUtil.creerFabriquePersistance();

        // new Scenario1Test().runTest();
        // new Scenario2Test().runTest();
        // new Scenario3Test().runTest();
        // new Scenario4Test().runTest();
        // new Scenario5Test().runTest();
        new Scenario6Test().runTest();

        JpaUtil.fermerFabriquePersistance();

    }

}