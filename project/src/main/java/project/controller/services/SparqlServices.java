package project.controller.services;

import java.util.HashMap;
import java.util.Map;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
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
  
  // ----------------------------------------------------------- Services for Servlet Initialization
  
  public static Map<String, String> getAllFilmNamesAndUris() {
     Map<String, String> FilmsNamesUris = new HashMap<>();
     for (int i=10; i<=30;i++){
         String condition = "strlen(str(?name))="+i;
         if(i==10){
             condition = "strlen(str(?name))<=10";
         }else if (i==30){
             condition = "strlen(str(?name))>=30";
         }
        QueryExecution qexec = createPrefixedQuery("SELECT distinct ?f ?name WHERE {\n" +
           "  ?f rdf:type dbo:Film ;\n" +
           "     dbo:runtime ?r ;\n" +
           "     rdfs:label ?name .\n" +
           "     FILTER (lang(?name) = 'en').\n" +
           "     Filter ("+condition+"). \n" +
           "}");
           try {
               ResultSet result = qexec.execSelect();

               while( result.hasNext() ){

                   QuerySolution elem = result.next();
                   // System.out.print(elem.getResource("f").getURI().toString()+ " // ");
                   // System.out.println(elem.getLiteral("name").getString());
                   FilmsNamesUris.put(elem.getLiteral("name").getString(),elem.getResource("f").getURI().toString()) ;
               }
           } catch(Exception e) {
               System.out.println(e);
           } finally {
               qexec.close();
           }
     }
        //System.out.println(FilmsNamesUris.size());
        return FilmsNamesUris;
  }
  
  public static Map<String, String> getAllCompanyNamesAndUris() {
    return null;
  }
  
  public static Map<String, String> getAllPersonNamesAndUris() {
    return null;
  }
  
  // ---------------------------------------------------------- Services to get Resource Information
  
  public static boolean isCompany(String uri) {
    return false;
  }
  
  public static boolean isFilm(String uri) {
    return false;
  }
  
  public static boolean isActor(String uri) {
    return false;
  }
  
  public static boolean isFilmDirector(String uri) {
    return false;
  }
  
  public static boolean isFilmProducer(String uri) {
    return false;
  }
  
  public static boolean isFilmMusicComposer(String uri) {
    return false;
  }
  
  public static int numberOfEmployees(String uri) {
    return 0;
  }
  
  public static int numberOfFilmsProducedByThisStudio(String uri) {
    return 0;
  }
  
  public static int numberOfFilmsDistributedByThisStudio(String uri) {
    return 0;
  }
  
  public static Map<String, String> majorFilmsProducedByThisStudio(String uri) {
    return null;
  }
  
  public static Map<String, String> majorFilmsDistributedByThisStudio(String uri) {
    return null;
  }
  
  /*
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
  }*/
  
  // -------------------------------------------------------- Services to get Resource Relationships
    
}
