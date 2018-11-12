/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.OutputStream;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
/**
 *
 * @author 杨振宇
 */
public class ServiceWebSeman {
    //protected JsonObject container;
    
    public ServiceWebSeman(){};
    
    public void ExecuteSelectJson(String motCle, String objet, OutputStream out){
        String queryString = "";
        queryString = FormerQueryParObjet(motCle,objet,queryString);
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
        try {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;

            // Execute.
            ResultSet results = qexec.execSelect();
            System.out.println(results);
            ResultSetFormatter.outputAsJSON(out,results);
        } catch(Exception e){
            ServiceWebSemanServlet.errorInternal = 1;
        }finally {
           qexec.close();
        }
    }
    
    public String FormerQueryParObjet(String motCle, String objet, String query){
        String queryString = null;
        queryString = AjouterPrefixStandard(query);
        if("All Film Production Companies".equals(objet)){
            queryString += " select distinct ?company " +
                            "where" +
                            "{" +
                            " ?company rdf:type dbo:Company ;" +
                            " rdf:type ?o ." +
                            " FILTER regex(str(?o), \"WikicatFilmProductionCompaniesOf\")" +
                            " }";
            System.out.println(queryString);
        }else if("Testunitaire".equals(objet)){
            queryString += " select ?p ?o " +
                            "where" +
                            "{" +
                            " dbr:Pixar ?p ?o ." +
                            " }";
            System.out.println(queryString);
        } else if("Actors of a Film".equals(objet)){
            queryString += " select distinct ?film ?actor " +
                            "where" +
                            "{" +
                            "?film rdf:type dbo:Film . " +
                            "?film dbo:starring ?actor . " +
                            "FILTER (?film = <http://dbpedia.org/resource/" +
                            motCle +
                            ">) " +
                            "}";
            System.out.println(queryString);
        } else if("Films and the Company that distributed each Film".equals(objet)){
             queryString += " SELECT ?film ?company WHERE { " +
                            "?company rdf:type dbo:Company ; " +
                            "rdf:type ?o . " +
                            "?film rdf:type dbo:Film ; " +
                            "dbo:distributor ?company . " +
                            "FILTER regex(str(?o), \"WikicatFilmProductionCompaniesOf\") " +
                            "}";
            System.out.println(queryString);
           
        } else if("Film Director".equals(objet)){
             queryString += " SELECT ?film ?director WHERE { " +
                            "?film rdf:type dbo:Film ; " +
                            "dbo:director ?director . " +
                            "FILTER (?film = <http://dbpedia.org/resource/" +
                            motCle +
                            ">) " +
                            "}";
            System.out.println(queryString);
        } else if("Film Music Composer".equals(objet)){
             queryString += " SELECT ?film ?music_composer WHERE { " +
                            "?film rdf:type dbo:Film ; " +
                            "dbo:musicComposer ?music_composer . " +
                            "FILTER (?film = <http://dbpedia.org/resource/" +
                            motCle +
                            ">) " +
                            "}";
            System.out.println(queryString);
        } else if("Parent Company".equals(objet)){
             queryString += " SELECT ?company ?parent_company WHERE { " +
                            "?company rdf:type dbo:Company ; " +
                            "dbo:parentCompany ?parent_company . " +
                            "FILTER (?company = <http://dbpedia.org/resource/" +
                            motCle +
                            ">) " +
                            "}";
            System.out.println(queryString);
        }else if("Find Resource".equals(objet)){
             queryString += " SELECT DISTINCT ?s ?sort ?name WHERE{ " +
                            "?s foaf:name ?name . " +
                            "{ " +
                            "?s rdf:type ?sort . " +
                            "FILTER(?sort = dbo:Film) " +
                            "} " +
                            "UNION" +
                            "{" +
                            "?s rdf:type ?sort . " + 
                            "FILTER(?sort = dbo:Actor)  " +
                            "}" +
                            "UNION" +
                            "{" +
                            "?s rdf:type ?sort ; " +
                            "rdf:type ?sortUn . " +
                            "FILTER(regex(str(?sortUn),\"WikicatFilmProductionCompaniesOf\") && ?sort = dbo:Company) " +
                            "} " +
                            "FILTER(regex(lcase(str(?name)),lcase(\""+motCle+"\"))) " +
                            "}";
            System.out.println(queryString);
        }
        return queryString;
    }
    
    public String AjouterPrefixStandard(String query){
        String prefix = query + "PREFIX dbp: <http://dbpedia.org/property/>"+
                        "PREFIX dbr: <http://dbpedia.org/resource/>"+
                        "PREFIX dbo: <http://dbpedia.org/ontology/>"+
                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
                        "PREFIX dbc: <http://dbpedia.org/resource/Category:>"+
                        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
                        "PREFIX foaf: <http://xmlns.com/foaf/0.1/>";
                        
        return prefix;
    }

}
