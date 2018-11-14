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

    /**
     * DBpedia SPARQL query endpoint
     */
    private static final String DBPEDIA_URL = "http://dbpedia.org/sparql";

    /**
     * Prefix declarations
     */
    private static final String QNAMES
            = "PREFIX dbp: <http://dbpedia.org/property/>\n"
            + "PREFIX dbr: <http://dbpedia.org/resource/>\n"
            + "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
            + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
            + "PREFIX dbc: <http://dbpedia.org/resource/Category:>\n"
            + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n";

    private static QueryExecution createPrefixedQuery(String queryString) {
        Query query = QueryFactory.create(QNAMES + queryString);
        return QueryExecutionFactory.sparqlService(DBPEDIA_URL, query);
    }

    // ----------------------------------------------------------- Services for Servlet Initialization
    public static Map<String, String> getAllFilmNamesAndUris() {
        return null;
    }

    public static Map<String, String> getAllCompanyNamesAndUris() {
        QueryExecution qexec = createPrefixedQuery("SELECT ?uri ?name WHERE {\n"
                + "  ?uri rdf:type dbo:Company ;\n"
                + "     rdf:type ?o ;\n"
                + "     rdfs:label ?name .\n"
                + "  FILTER regex(str(?o), \"WikicatFilmProductionCompaniesOf\").\n"
                + "  FILTER (lang(?name)='en')\n"
                + "}");

        ResultSet results = qexec.execSelect();

        Map<String, String> films = new HashMap<String, String>();

        String uri = "";
        String name = "";

        for (; results.hasNext();) {
            QuerySolution elem = results.nextSolution();
            uri = elem.getResource("uri").getURI().toString();
            name = elem.getLiteral("name").getString();
            films.put(name, uri);
        }
        return films;
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
