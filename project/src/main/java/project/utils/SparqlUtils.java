/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.utils;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;


/**
 *
 * @author thomasmalvoisin
 */
public class SparqlUtils {
    
    static final String dbpedia = "http://dbpedia.org/sparql";
    static final String PREFIX = "PREFIX dbp: <http://dbpedia.org/property/>"+
                        "PREFIX dbr: <http://dbpedia.org/resource/>"+
                        "PREFIX dbo: <http://dbpedia.org/ontology/>"+
                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
                        "PREFIX dbc: <http://dbpedia.org/resource/Category:>"+
                        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>";

    
    public static QueryExecution createQuery (String queryString){
        Query query = QueryFactory.create(queryString + PREFIX);
        
        QueryExecution qexec = QueryExecutionFactory.sparqlService(dbpedia, query);
        
        return qexec;
    }
    
}
