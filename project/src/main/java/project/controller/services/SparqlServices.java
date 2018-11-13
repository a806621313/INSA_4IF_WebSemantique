package project.controller.services;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;

public class SparqlServices {
  
  /** DBpedia SPARQL query endpoint */
  private static final String DBPEDIA_URL = "http://dbpedia.org/sparql";
  
  /** Prefix declarations */
  private static final String QNAMES =
      "PREFIX dbp: <http://dbpedia.org/property/>\n"
    + "PREFIX dbr: <http://dbpedia.org/resource/>\n"
    + "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
    + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
    + "PREFIX dbc: <http://dbpedia.org/resource/Category:>\n"
    + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n";

  private static QueryExecution createPrefixedQuery (String queryString){
    Query query = QueryFactory.create(QNAMES + queryString);
    return QueryExecutionFactory.sparqlService(DBPEDIA_URL, query);
  }
    
  public static void getFilmInformation(String uriFilm){

    String sparqlQuery =
        "SELECT * WHERE {\n"
      + "<" + uriFilm + ">" + " dbo:starring ?a;\n"
      + "dbo:director ?d;\n"
      + "dbo:producer ?p;\n"
      + "dbo:musicComposer ?c;\n"
      + "dbo:budget ?b;\n"
      + "dbo:gross ?g;\n"
      + "dbo:runtime ?r\n"
      + "}";

    QueryExecution query = createPrefixedQuery(sparqlQuery);

    try {
      ResultSet result = query.execSelect();

      //TODO
    } finally {
      query.close();
    }
  }
    
}
