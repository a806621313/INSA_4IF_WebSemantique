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
    + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
    + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n";

  private static QueryExecution createPrefixedQuery (String queryString){
    Query query = QueryFactory.create(QNAMES + queryString);
    return QueryExecutionFactory.sparqlService(DBPEDIA_URL, query);
  }

  // ----------------------------------------------------------- Services for Servlet Initialization
  public static Map<String, String> getAllFilmNamesAndUris() {
    Map<String, String> films = new HashMap<String, String>();
    for (int i = 10; i <= 30; i++) {
      String condition = "strlen(str(?name)) = " + i;
      if (i == 10) {
        condition = "strlen(str(?name)) <= 10";
      } else if (i == 30) {
        condition = "strlen(str(?name)) >= 30";
      }
      
      QueryExecution queryFilms = createPrefixedQuery(
          "SELECT DISTINCT ?f ?name WHERE {\n"
        + "  ?f rdf:type dbo:Film ;\n"
        + "     dbo:runtime ?r ;\n"
        + "     rdfs:label ?name .\n"
        + "     FILTER (lang(?name) = 'en').\n"
        + "     FILTER (" + condition + "). \n"
        + "}"
      );
        
      try {
        ResultSet result = queryFilms.execSelect();
        while (result.hasNext()) {
          QuerySolution elem = result.next();
          films.put(elem.getLiteral("?name").getString(), elem.getResource("?f").getURI().toString()) ;
        }
      } catch (Exception e) {
        System.out.println(e);
      } finally {
        queryFilms.close();
      }
    }
    return films;
  }

  public static Map<String, String> getAllCompanyNamesAndUris() {
    Map<String, String> companies = new HashMap<String, String>();
    
    QueryExecution queryFilms = createPrefixedQuery("SELECT ?uri ?name WHERE {\n"
            + "  ?uri rdf:type dbo:Company ;\n"
            + "     rdf:type ?o ;\n"
            + "     rdfs:label ?name .\n"
            + "  FILTER regex(str(?o), \"WikicatFilmProductionCompaniesOf\").\n"
            + "  FILTER (lang(?name)='en')\n"
            + "}");
    try {
      ResultSet results = queryFilms.execSelect();
      while (results.hasNext()) {
        QuerySolution elem = results.nextSolution();
        companies.put(elem.getLiteral("name").getString(), elem.getResource("uri").getURI().toString());
      }
    } catch (Exception e) {
      System.out.println(e);
    } finally {
      queryFilms.close();
    }
    return companies;
  }

  public static Map<String, String> getAllPersonNamesAndUris() {
    Map<String, String> persons = new HashMap<String, String>();
    for (int i = 0; i < 26; i++) {
      String condition = "strstarts(str(?name), '" + (char)('A' + i) + "')";
      
      // actors
      QueryExecution queryActors = createPrefixedQuery(
          "SELECT DISTINCT ?a ?name WHERE {\n"
        + "  ?f rdf:type dbo:Film ;\n"
        + "     dbo:runtime ?r ;\n"
        + "     dbo:starring ?a .\n"
        + "  ?a foaf:name ?name .\n"
        + "  FILTER(lang(?name)='en') .\n"
        + "  FILTER(" + condition + "). \n"
        + "}"
      );
      
      // directors
      QueryExecution queryDirectors = createPrefixedQuery(
          "SELECT DISTINCT ?a ?name WHERE {\n"
        + "  ?f rdf:type dbo:Film ;\n"
        + "     dbo:runtime ?r ;\n"
        + "     dbo:director ?a .\n"
        + "  ?a foaf:name ?name .\n"
        + "  FILTER(lang(?name)='en') .\n"
        + "  FILTER(" + condition + "). \n"
        + "}"
      );
      
      // music composers
      QueryExecution queryMusicComposers = createPrefixedQuery(
          "SELECT DISTINCT ?a ?name WHERE {\n"
        + "  ?f rdf:type dbo:Film ;\n"
        + "     dbo:runtime ?r ;\n"
        + "     dbo:musicComposer ?a .\n"
        + "  ?a foaf:name ?name .\n"
        + "  FILTER(lang(?name)='en') .\n"
        + "  FILTER(" + condition + "). \n"
        + "}"
      );
      
      try {
        ResultSet actors = queryActors.execSelect();
        while (actors.hasNext()) {
          QuerySolution elem = actors.next();
          persons.put(elem.getLiteral("?name").getString(), elem.getResource("?a").getURI().toString()) ;
        }
        
        ResultSet directors = queryDirectors.execSelect();
        while (directors.hasNext()) {
          QuerySolution elem = directors.next();
          persons.put(elem.getLiteral("?name").getString(), elem.getResource("?a").getURI().toString()) ;
        }
        
        ResultSet musicComposers = queryMusicComposers.execSelect();
        while (musicComposers.hasNext()) {
          QuerySolution elem = musicComposers.next();
          persons.put(elem.getLiteral("?name").getString(), elem.getResource("?a").getURI().toString()) ;
        }
      } catch (Exception e) {
        System.out.println(e);
      } finally {
        queryActors.close();
        queryDirectors.close();
        queryMusicComposers.close();
      }
    }
    return persons;
  }

  // ---------------------------------------------------------- Services to get Resource Information
  public static boolean isCompany(String uri) {
    QueryExecution qexec = createPrefixedQuery("SELECT ?o WHERE {\n"
            + "<" + uri + ">" + " rdf:type dbo:Company ;\n"
            + "     rdf:type ?o .\n"
            + "  FILTER regex(str(?o), \"WikicatFilmProductionCompaniesOf\")\n"
            + "}");

    ResultSet results = qexec.execSelect();

    return results.hasNext();
  }
  
  public static boolean isFilm(String uri) {
    QueryExecution qexec = createPrefixedQuery("SELECT ?r WHERE {\n" +
        "  <"+uri+"> rdf:type dbo:Film ;\n" +
        "  dbo:runtime ?r  .\n" +
        "}");
        
    ResultSet result = qexec.execSelect();
    boolean isfilm=result.hasNext();
  // System.out.println(isfilm);
    return isfilm;
  }
  
  public static boolean isActor(String uri) {
    QueryExecution qexec = createPrefixedQuery("SELECT ?f WHERE {\n"
              + "  ?f rdf:type dbo:Film ;\n"
              + "     dbo:starring <" + uri + "> \n"
              + "}");
    ResultSet results = qexec.execSelect();

    return results.hasNext();
  }

  public static boolean isFilmDirector(String uri) {
    QueryExecution qexec = createPrefixedQuery("SELECT ?f WHERE {\n"
            + "  ?f rdf:type dbo:Film ;\n"
            + "     dbo:director <" + uri + "> .\n"
            + "}");

    ResultSet results = qexec.execSelect();

    return results.hasNext();
  }

  public static boolean isFilmProducer(String uri) {
    QueryExecution qexec = createPrefixedQuery("SELECT ?f WHERE {\n"
            + "  ?f rdf:type dbo:Film ;\n"
            + "     dbo:producer <" + uri + "> .\n"
            + "}");

    ResultSet results = qexec.execSelect();

    return results.hasNext();
  }

  public static boolean isFilmMusicComposer(String uri) {
    QueryExecution qexec = createPrefixedQuery("SELECT ?f WHERE {\n"
            + "  ?f rdf:type dbo:Film ;\n"
            + "     dbo:musicComposer <" + uri + "> .\n"
            + "}");

    ResultSet results = qexec.execSelect();

    return results.hasNext();
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
  
  public static String getFilmBudget(String uri){
      String budget ="";
      QueryExecution qexec = createPrefixedQuery("SELECT ?b WHERE {\n" +
        "    <http://dbpedia.org/resource/Cars_(film)> dbo:budget ?b .\n" +
        "}");
        
    ResultSet result = qexec.execSelect();
    if( result.hasNext() ){

        QuerySolution elem = result.next();
        budget += elem.getLiteral("b").getString() + " ";
        String currencyUri = elem.getLiteral("b").getDatatypeURI();
        budget += currencyUri.substring(currencyUri.lastIndexOf('/') + 1).trim();
    }
    System.out.println(budget);
    return budget;
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
