/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.controller.services;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import project.utils.SparqlUtils;

/**
 *
 * @author thomasmalvoisin
 */
public class SparqlServices {
    
    public static void getFilmInformation(String uriFilm){
        
        String sparqlQuery = "SELECT * WHERE { "
                + "<" + uriFilm + ">" + " dbo:starring ?a;"
                + "dbo:director ?d;"
                + "dbo:producer ?p;"
                + "dbo:musicComposer ?c;"
                + "dbo:budget ?b;"
                + "dbo:gross ?g;"
                + "dbo:runtime ?r"
                + "}";
        
        QueryExecution query = SparqlUtils.createQuery(sparqlQuery);
        
        try {
            ResultSet result = query.execSelect();
            
            //TODO
        } finally {
            query.close();
        }
    }
    
}
