package project.controller.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

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
    + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
    + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";

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
        + "     dbo:director ?d ;\n"
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
    
    QueryExecution queryCompanies = createPrefixedQuery("SELECT ?uri ?name WHERE {\n"
            + "  ?uri rdf:type dbo:Company ;\n"
            + "     rdf:type ?o ;\n"
            + "     rdfs:label ?name .\n"
            + "  FILTER regex(str(?o), \"WikicatFilmProductionCompaniesOf\").\n"
            + "  FILTER (lang(?name)='en')\n"
            + "}");
    try {
      ResultSet results = queryCompanies.execSelect();
      while (results.hasNext()) {
        QuerySolution elem = results.nextSolution();
        companies.put(elem.getLiteral("name").getString(), elem.getResource("uri").getURI().toString());
      }
    } catch (Exception e) {
      System.out.println(e);
    } finally {
      queryCompanies.close();
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
        + "  ?a foaf:name ?name ;\n"
        + "     rdf:type dbo:Person .\n"
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
        + "  ?a foaf:name ?name ;\n"
        + "     rdf:type dbo:Person .\n"
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
        + "  ?a foaf:name ?name ;\n"
        + "     rdf:type dbo:Person .\n"
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
  
  public static Map<String, String> majorFilmsProducedByThisStudio(String uri) {
    return null;
  }
  
  public static Map<String, String> majorFilmsDistributedByThisStudio(String uri) {
    return null;
  }
  
  public static String getFilmBudget(String uri){
      String budget ="";
      QueryExecution qexec = createPrefixedQuery("SELECT ?b WHERE {\n" +
        "   <"+uri+"> dbo:budget ?b .\n" +
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
  
  public static String getFilmBoxOffice(String uri){
      String gross ="";
      QueryExecution qexec = createPrefixedQuery("SELECT ?b WHERE {\n" +
        "   <"+uri+"> dbo:gross ?b .\n" +
        "}"); 
    ResultSet result = qexec.execSelect();
    if( result.hasNext() ){
        QuerySolution elem = result.next();
        gross += elem.getLiteral("b").getString() + " ";
        String currencyUri = elem.getLiteral("b").getDatatypeURI();
        gross += currencyUri.substring(currencyUri.lastIndexOf('/') + 1).trim();
    }
    System.out.println(gross);
    return gross;
  }
  
  public static int getFilmRuntime(String uri){
      int runtime =0;
      QueryExecution qexec = createPrefixedQuery("SELECT ?r WHERE {\n" +
        "   <"+uri+"> dbo:runtime ?r .\n" +
        "}"); 
    ResultSet result = qexec.execSelect();
    if( result.hasNext() ){
        QuerySolution elem = result.next();
        runtime = elem.getLiteral("r").getInt();
    }
    System.out.println(runtime);
    return runtime;
  }
  
  public static String getFilmDirector(String uri){
      String director ="";
      QueryExecution qexec = createPrefixedQuery("SELECT ?d WHERE {\n" +
        "   <"+uri+"> dbo:director ?d .\n" +
        "}"); 
    ResultSet result = qexec.execSelect();
    if( result.hasNext() ){
        QuerySolution elem = result.next();
        director = elem.getResource("d").getURI().toString();
    }
    System.out.println(director);
    return director;
  }
  
  public static String getFilmProducer(String uri){
      String producer ="";
      QueryExecution qexec = createPrefixedQuery("SELECT ?p WHERE {\n" +
        "   <"+uri+"> dbo:producer ?p .\n" +
        "}"); 
    ResultSet result = qexec.execSelect();
    if( result.hasNext() ){
        QuerySolution elem = result.next();
        producer = elem.getResource("p").getURI().toString();
    }
    System.out.println(producer);
    return producer;
  }
  
  public static String getFilmMusicComposer(String uri){
      String musicComposer ="";
      QueryExecution qexec = createPrefixedQuery("SELECT ?m WHERE {\n" +
        "   <"+uri+"> dbo:musicComposer ?m .\n" +
        "}"); 
    ResultSet result = qexec.execSelect();
    if( result.hasNext() ){
        QuerySolution elem = result.next();
        musicComposer = elem.getResource("m").getURI().toString();
    }
    System.out.println(musicComposer);
    return musicComposer;
  }
  
  public static List<String> getFilmActors(String uri){
      List<String> actors = new ArrayList<>();
      QueryExecution qexec = createPrefixedQuery("SELECT ?a WHERE {\n" +
        "   <"+uri+"> dbo:starring  ?a .\n" +
        "}"); 
    ResultSet result = qexec.execSelect();
    while( result.hasNext() ){
        QuerySolution elem = result.next();
        actors.add(elem.getResource("a").getURI().toString());
    }
    return actors;
  }
  
  public static String getObjectName(String uri){
         String name = null;
         String query = "SELECT ?name WHERE { <" + uri + "> rdfs:label ?name. FILTER(lang(?name) = \"en\") }" ;
         System.out.println(query);
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            //Traitement
            if(rs.hasNext()){
                QuerySolution qs = rs.nextSolution();
                name = qs.get("name").toString();
            }
        } catch(Exception e){
            
        }
        return name;
  }
  
  public static int addDefaultNode(String uri,  JsonObject nodes, JsonObject edgeSubject, int index){
      if(isFilm(uri)){
          JsonObject tempNode = new JsonObject();
          JsonObject tempEdge = new JsonObject();
          tempNode.addProperty("name","Film");
          tempNode.addProperty("uri","None");
          tempNode.addProperty("color", "#4444ff");
           nodes.add("node"+String.valueOf(index),tempNode); 
           tempEdge.addProperty("type", "Type");
           edgeSubject.add("node"+String.valueOf(index), tempEdge);
           index++;
      }else if(isCompany(uri)){
          JsonObject tempNode = new JsonObject();
          JsonObject tempEdge = new JsonObject();
          tempNode.addProperty("name","Company");
          tempNode.addProperty("uri","None");
          tempNode.addProperty("color", "#4444ff");
           nodes.add("node"+String.valueOf(index),tempNode); 
           tempEdge.addProperty("type", "Type");
           edgeSubject.add("node"+String.valueOf(index), tempEdge);
           index++;
      }else{
         JsonObject tempNode = new JsonObject();
          JsonObject tempEdge = new JsonObject();
          tempNode.addProperty("name","Person");
          tempNode.addProperty("uri","None");
          tempNode.addProperty("color", "#4444ff");
           nodes.add("node"+String.valueOf(index),tempNode); 
           tempEdge.addProperty("type", "Type");
           edgeSubject.add("node"+String.valueOf(index), tempEdge); 
           index++;
      }
      return index;
  }
  
  public static int filmRunTime(String uri,JsonArray infos, int index){
         try (QueryExecution qexec = createPrefixedQuery(" SELECT ?r WHERE{ <" + uri +  "> dbo:runtime ?r . }")) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
            while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
               QuerySolution qs = rs.nextSolution();
               String value = qs.get("r").toString();
               int ocurrence = value.indexOf("^");
               value = value.substring(0,ocurrence);
               infoTempObject.addProperty("uri","None");
               infoTempObject.addProperty("value", value);
               infoTempObject.addProperty("type", "Litteral");
               
               liste.add(infoTempObject);
            }
            if(liste.size() > 0){
                tempObject.add("Budget",liste);
                infos.add(tempObject);
            }
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int filmBudget(String uri,JsonArray infos, int index){
         try (QueryExecution qexec = createPrefixedQuery(" SELECT ?b WHERE{ <" + uri +  "> dbo:budget ?b . }")) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
                String value = qs.get("b").toString();
                int ocurrence = value.indexOf("^");
               value = value.substring(0,ocurrence);
               infoTempObject.addProperty("uri","None");
               infoTempObject.addProperty("value", value);
               infoTempObject.addProperty("type", "Litteral");
               liste.add(infoTempObject);
            }  
             if(liste.size() > 0){
                tempObject.add("Run time",liste);
                infos.add(tempObject);
            }
        } catch(Exception e){
            
        }
         return index;
  }

  public static int filmBoxOffice(String uri,JsonArray infos, int index){
         try (QueryExecution qexec = createPrefixedQuery(" SELECT ?g WHERE{ <" + uri +  "> dbo:gross ?g . }")) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
                String value = qs.get("g").toString();
                int ocurrence = value.indexOf("^");
               value = value.substring(0,ocurrence);
               infoTempObject.addProperty("uri","None");
               infoTempObject.addProperty("value", value);
               infoTempObject.addProperty("type", "Litteral");
               liste.add(infoTempObject);
            } 
            if(liste.size() > 0){
                tempObject.add("Box-Office Gross",liste);
                infos.add(tempObject);
            }
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int filmStarring(String uri, JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
      String query = "SELECT ?actor WHERE {\n" +
                        "<" + uri + "> rdf:type dbo:Film ;\n" +
                        "dbo:starring ?actor .\n" +
                        "}";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonObject edgesSup = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                JsonObject nodeTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
                try{
                    Resource Uri = qs.getResource("actor");
                    String uriS = Uri.getURI();              
                    String name = getObjectName(uriS);
                    /*if(name == null){
                       name = "Producer without documents.";
                    }*/
                    if(name != null){
                        name = adjustName(name);
                        infoTempObject.addProperty("uri",uriS);
                        infoTempObject.addProperty("value", name);
                        infoTempObject.addProperty("type", "Actor");
                        nodeTempObject.addProperty("uri", uriS);
                        nodeTempObject.addProperty("color", "#4444ff");
                        nodeTempObject.addProperty("name",name);
                        liste.add(infoTempObject);
                        JsonObject vide = new JsonObject();
                        vide.addProperty("type","");
                        nodes.add("node"+String.valueOf(index),nodeTempObject); 
                        edgesSup.add("node"+String.valueOf(index),vide); 
                        index++;
                    }
                    
                }catch(Exception e){
                    System.out.println("Not a resource");
                }
            }
            if(liste.size() > 0){
                tempObject.add("Actors of this film",liste);
                infos.add(tempObject);
                JsonObject nodeAnonym = new JsonObject();
                nodeAnonym.addProperty("uri", "None");
                nodeAnonym.addProperty("color", "#4444ff");
                nodeAnonym.addProperty("name","");
                nodes.add("node"+String.valueOf(index),nodeAnonym); 
                edges.add("node"+String.valueOf(index), edgesSup);
                JsonObject edgeAnonym = new JsonObject();
                edgeAnonym.addProperty("type", "Actors of this film");
                edgeSubject.add("node"+String.valueOf(index), edgeAnonym);
                index++;
            } 
        } catch(Exception e){
            
        }
         return index;
  }

  private static String getNameFromUri(String uri){
     int index = uri.lastIndexOf("/");
     String subs = uri.substring(index+1);
     String replaceAll = subs.replaceAll("_"," ");
     return replaceAll;
  }
  
  private static String adjustName(String name){
     int index = name.lastIndexOf("@");
     String subs = name.substring(0,index);
     return subs;
  }
  
  public static int objectName(String uri,JsonArray infos, JsonObject nodes, int index){
         try (QueryExecution qexec = createPrefixedQuery(" SELECT ?name WHERE{ <" + uri +  "> foaf:name ?name . } LIMIT 1")) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            //Traitement
            JsonObject infoObject = new JsonObject();
            JsonObject nodeObject = new JsonObject();
            if(rs.hasNext()){
                QuerySolution qs = rs.nextSolution();
                String value = adjustName(qs.get("name").toString());
                infoObject.addProperty("value", value);
                nodeObject.addProperty("name",value);
            }else{
                String tempName = getNameFromUri(uri);
                infoObject.addProperty("value", tempName); 
                nodeObject.addProperty("name",tempName);
            }
            infoObject.addProperty("uri",uri);
            if(isFilm(uri)){
                infoObject.addProperty("type", "Film");
            }else{
                infoObject.addProperty("type", "Company");
            }
            nodeObject.addProperty("uri", uri);
            nodeObject.addProperty("color", "#44ff44");
            nodes.add("node"+String.valueOf(index),nodeObject); 
            index++; 
            tempObject.add("Subject",infoObject);
            infos.add(tempObject);
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int personName(String uri,JsonArray infos, JsonObject nodes, int index, boolean isActor, boolean isDirector, boolean isMusicComposer, boolean isProducer){
         try (QueryExecution qexec = createPrefixedQuery(" SELECT ?name WHERE{ <" + uri +  "> foaf:name ?name . } LIMIT 1")) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            //Traitement
            JsonObject infoObject = new JsonObject();
            JsonObject nodeObject = new JsonObject();
            if(rs.hasNext()){
                QuerySolution qs = rs.nextSolution();
                String value = adjustName(qs.get("name").toString());
                infoObject.addProperty("value", value);
                nodeObject.addProperty("name",value);
            }else{
                infoObject.addProperty("value", "None"); 
                nodeObject.addProperty("name","None");
            }
            infoObject.addProperty("uri",uri);
            infoObject.addProperty("type", "Person");
            JsonArray roles = new JsonArray();
            if(isActor){
                JsonObject temp = new JsonObject();
                temp.addProperty("role","Actor" );
                roles.add(temp);
            }
            if(isMusicComposer){
                JsonObject temp = new JsonObject();
                temp.addProperty("role","MusicComposer" );
                roles.add(temp);
            }
            if(isProducer){
                JsonObject temp = new JsonObject();
                temp.addProperty("role","Producer" );
                roles.add(temp);
            }
            if(isDirector){
                JsonObject temp = new JsonObject();
                temp.addProperty("role","Director" );
                roles.add(temp);
            }
            infoObject.add("roles", roles);
            nodeObject.addProperty("uri", uri);
            nodeObject.addProperty("color", "#44ff44");
            nodes.add("node"+String.valueOf(index),nodeObject); 
            index++;
            tempObject.add("Subject",infoObject);
            infos.add(tempObject);
        } catch(Exception e){
            
        }
         return index;
  }

  public static int producerFrequentlyWorkWithActor(String uri,JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
         String query = "SELECT ?p (COUNT(?f) AS ?n) WHERE {\n" +
                        "?f rdf:type dbo:Film ;\n" +
                        "dbo:starring <" + uri + "> ;\n" +
                        "dbo:producer ?p .\n" +
                        "FILTER(?p != <" + uri + ">).\n" +
                        "}\n" +
                        "GROUP BY ?p\n" +
                        "ORDER BY DESC(?n)\n" +
                        "LIMIT 5";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonObject edgesSup = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                JsonObject nodeTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
                try{
                    Resource Uri = qs.getResource("p");
                    String uriS = Uri.getURI();              
                    String name = getObjectName(uriS);
                    /*if(name == null){
                       name = "Producer without documents.";
                    }*/
                    if(name != null){
                        name = adjustName(name);
                        infoTempObject.addProperty("uri",uriS);
                        infoTempObject.addProperty("value", name);
                        infoTempObject.addProperty("type", "Producer");
                        nodeTempObject.addProperty("uri", uriS);
                        nodeTempObject.addProperty("color", "#4444ff");
                        nodeTempObject.addProperty("name",name);
                        liste.add(infoTempObject);
                        JsonObject vide = new JsonObject();
                        vide.addProperty("type","");
                        nodes.add("node"+String.valueOf(index),nodeTempObject); 
                        edgesSup.add("node"+String.valueOf(index),vide); 
                        index++;
                    }
                }catch(Exception e){
                    System.out.println("Not a resource");
                }
            }  
               if(liste.size() > 0){
                tempObject.add("Producers this person frequently worked with as Actor",liste);
                infos.add(tempObject);
                JsonObject nodeAnonym = new JsonObject();
                nodeAnonym.addProperty("uri", "None");
                nodeAnonym.addProperty("color", "#4444ff");
                nodeAnonym.addProperty("name","");
                nodes.add("node"+String.valueOf(index),nodeAnonym); 
                edges.add("node"+String.valueOf(index), edgesSup);
                JsonObject edgeAnonym = new JsonObject();
                edgeAnonym.addProperty("type", "Producers frequently worked with as Actor");
                edgeSubject.add("node"+String.valueOf(index), edgeAnonym);
                index++;
            }
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int producerFrequentlyWorkWithMusicComposer(String uri,JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
         String query = "SELECT ?p (COUNT(?f) AS ?n) WHERE {\n" +
                        "?f rdf:type dbo:Film ;\n" +
                        "dbo:musicComposer <" + uri + "> ;\n" +
                        "dbo:producer ?p .\n" +
                        "FILTER(?p != <" + uri + ">).\n" +
                        "}\n" +
                        "GROUP BY ?p\n" +
                        "ORDER BY DESC(?n)\n" +
                        "LIMIT 5";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonObject edgesSup = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                JsonObject nodeTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
                try{
                    Resource Uri = qs.getResource("p");
                    String uriS = Uri.getURI();              
                    String name = getObjectName(uriS);
                    /*if(name == null){
                       name = "Producer without documents.";
                    }*/
                    if(name != null){
                        name = adjustName(name);
                        infoTempObject.addProperty("uri",uriS);
                        infoTempObject.addProperty("value", name);
                        infoTempObject.addProperty("type", "Producer");
                        nodeTempObject.addProperty("uri", uriS);
                        nodeTempObject.addProperty("color", "#4444ff");
                        nodeTempObject.addProperty("name",name);
                        liste.add(infoTempObject);
                        JsonObject vide = new JsonObject();
                        vide.addProperty("type","");
                        nodes.add("node"+String.valueOf(index),nodeTempObject); 
                        edgesSup.add("node"+String.valueOf(index),vide); 
                        index++;
                    }
                }catch(Exception e){
                    System.out.println("Not a resource");
                }
            }  
              if(liste.size() > 0){
                tempObject.add("Producers this person frequently worked with as music composer",liste);
                infos.add(tempObject);
                JsonObject nodeAnonym = new JsonObject();
                nodeAnonym.addProperty("uri", "None");
                nodeAnonym.addProperty("color", "#4444ff");
                nodeAnonym.addProperty("name","");
                nodes.add("node"+String.valueOf(index),nodeAnonym); 
                edges.add("node"+String.valueOf(index), edgesSup);
                JsonObject edgeAnonym = new JsonObject();
                edgeAnonym.addProperty("type", "Producers frequently worked with as music composer");
                edgeSubject.add("node"+String.valueOf(index), edgeAnonym);
                index++;
            }
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int producerFrequentlyWorkWithDirector(String uri,JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
         String query = "SELECT ?p (COUNT(?f) AS ?n) WHERE {\n" +
                        "?f rdf:type dbo:Film ;\n" +
                        "dbo:director <" + uri + "> ;\n" +
                        "dbo:producer ?p .\n" +
                        "FILTER(?p != <" + uri + ">).\n" +
                        "}\n" +
                        "GROUP BY ?p\n" +
                        "ORDER BY DESC(?n)\n" +
                        "LIMIT 5";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonObject edgesSup = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                JsonObject nodeTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
                try{
                    Resource Uri = qs.getResource("p");
                    String uriS = Uri.getURI();              
                    String name = getObjectName(uriS);
                    /*if(name == null){
                       name = "Producer without documents.";
                    }*/
                    if(name != null){
                        name = adjustName(name);
                        infoTempObject.addProperty("uri",uriS);
                        infoTempObject.addProperty("value", name);
                        infoTempObject.addProperty("type", "Producer");
                        nodeTempObject.addProperty("uri", uriS);
                        nodeTempObject.addProperty("color", "#4444ff");
                        nodeTempObject.addProperty("name",name);
                        liste.add(infoTempObject);
                        JsonObject vide = new JsonObject();
                        vide.addProperty("type","");
                        nodes.add("node"+String.valueOf(index),nodeTempObject); 
                        edgesSup.add("node"+String.valueOf(index),vide); 
                        index++;
                    }
                }catch(Exception e){
                    System.out.println("Not a resource");
                }
            }  
              if(liste.size() > 0){
                tempObject.add("Producers this person frequently worked with as director",liste);
                infos.add(tempObject);
                JsonObject nodeAnonym = new JsonObject();
                nodeAnonym.addProperty("uri", "None");
                nodeAnonym.addProperty("color", "#4444ff");
                nodeAnonym.addProperty("name","");
                nodes.add("node"+String.valueOf(index),nodeAnonym); 
                edges.add("node"+String.valueOf(index), edgesSup);
                JsonObject edgeAnonym = new JsonObject();
                edgeAnonym.addProperty("type", "Producers frequently worked with as director");
                edgeSubject.add("node"+String.valueOf(index), edgeAnonym);
                index++;
            }
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int directorFrequentlyWorkWithProducer(String uri,JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
         String query = "SELECT ?d (COUNT(?f) AS ?n) WHERE {\n" +
                        "?f rdf:type dbo:Film ;\n" +
                        "dbo:producer <" + uri + "> ;\n" +
                        "dbo:director ?d .\n" +
                        "FILTER(?d != <" + uri + ">).\n" +
                        "}\n" +
                        "GROUP BY ?d\n" +
                        "ORDER BY DESC(?n)\n" +
                        "LIMIT 5";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonObject edgesSup = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                JsonObject nodeTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
                try{
                    Resource Uri = qs.getResource("d");
                    String uriS = Uri.getURI();              
                    String name = getObjectName(uriS);
                    /*if(name == null){
                       name = "Director without documents.";
                    }*/
                    if(name != null){
                        name = adjustName(name);
                        infoTempObject.addProperty("uri",uriS);
                        infoTempObject.addProperty("value", name);
                        infoTempObject.addProperty("type", "Director");
                        nodeTempObject.addProperty("uri", uriS);
                        nodeTempObject.addProperty("color", "#4444ff");
                        nodeTempObject.addProperty("name",name);
                        liste.add(infoTempObject);
                        JsonObject vide = new JsonObject();
                        vide.addProperty("type","");
                        nodes.add("node"+String.valueOf(index),nodeTempObject); 
                        edgesSup.add("node"+String.valueOf(index),vide); 
                        index++;
                    }
                }catch(Exception e){
                    System.out.println("Not a resource");
                }
            }  
              if(liste.size() > 0){
                tempObject.add("Directors this person frequently worked with as Producer",liste);
                infos.add(tempObject);
                JsonObject nodeAnonym = new JsonObject();
                nodeAnonym.addProperty("uri", "None");
                nodeAnonym.addProperty("color", "#4444ff");
                nodeAnonym.addProperty("name","");
                nodes.add("node"+String.valueOf(index),nodeAnonym); 
                edges.add("node"+String.valueOf(index), edgesSup);
                JsonObject edgeAnonym = new JsonObject();
                edgeAnonym.addProperty("type", "Directors frequently worked with as Producer");
                edgeSubject.add("node"+String.valueOf(index), edgeAnonym);
                index++;
            }
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int directorFrequentlyWorkWithMusicComposer(String uri,JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
         String query = "SELECT ?d (COUNT(?f) AS ?n) WHERE {\n" +
                        "?f rdf:type dbo:Film ;\n" +
                        "dbo:musicComposer <" + uri + "> ;\n" +
                        "dbo:director ?d .\n" +
                        "FILTER(?d != <" + uri + ">).\n" +
                        "}\n" +
                        "GROUP BY ?d\n" +
                        "ORDER BY DESC(?n)\n" +
                        "LIMIT 5";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonObject edgesSup = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                JsonObject nodeTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
                try{
                    Resource Uri = qs.getResource("d");
                    String uriS = Uri.getURI();              
                    String name = getObjectName(uriS);
                    /*if(name == null){
                       name = "Director without documents.";
                    }*/
                    if(name != null){
                        name = adjustName(name);
                        infoTempObject.addProperty("uri",uriS);
                        infoTempObject.addProperty("value", name);
                        infoTempObject.addProperty("type", "Director");
                        nodeTempObject.addProperty("uri", uriS);
                        nodeTempObject.addProperty("color", "#4444ff");
                        nodeTempObject.addProperty("name",name);
                        liste.add(infoTempObject);
                        JsonObject vide = new JsonObject();
                        vide.addProperty("type","");
                        nodes.add("node"+String.valueOf(index),nodeTempObject); 
                        edgesSup.add("node"+String.valueOf(index),vide); 
                        index++;
                    }
                }catch(Exception e){
                    System.out.println("Not a resource");
                }
            }  
             if(liste.size() > 0){
                tempObject.add("Directors this person frequently worked with as Music Composer",liste);
                infos.add(tempObject);
                JsonObject nodeAnonym = new JsonObject();
                nodeAnonym.addProperty("uri", "None");
                nodeAnonym.addProperty("color", "#4444ff");
                nodeAnonym.addProperty("name","");
                nodes.add("node"+String.valueOf(index),nodeAnonym); 
                edges.add("node"+String.valueOf(index), edgesSup);
                JsonObject edgeAnonym = new JsonObject();
                edgeAnonym.addProperty("type", "Directors frequently worked with as Music Composer");
                edgeSubject.add("node"+String.valueOf(index), edgeAnonym);
                index++;
            }
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int musicComposerFrequentlyWorkWithProducer(String uri,JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
         String query = "SELECT ?m (COUNT(?f) AS ?n) WHERE {\n" +
                        "?f rdf:type dbo:Film ;\n" +
                        "dbo:producer <" + uri + "> ;\n" +
                        "dbo:musicComposer ?m .\n" +
                        "FILTER(?m != <" + uri + ">).\n" +
                        "}\n" +
                        "GROUP BY ?m\n" +
                        "ORDER BY DESC(?n)\n" +
                        "LIMIT 5";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonObject edgesSup = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                JsonObject nodeTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
                try{
                    Resource Uri = qs.getResource("m");
                    String uriS = Uri.getURI();              
                    String name = getObjectName(uriS);
                    /*if(name == null){
                       name = "Music Composer without documents.";
                    }*/
                    if(name != null){
                        name = adjustName(name);
                        infoTempObject.addProperty("uri",uriS);
                        infoTempObject.addProperty("value", name);
                        infoTempObject.addProperty("type", "Music Composer");
                        nodeTempObject.addProperty("uri", uriS);
                        nodeTempObject.addProperty("color", "#4444ff");
                        nodeTempObject.addProperty("name",name);
                        liste.add(infoTempObject);
                        JsonObject vide = new JsonObject();
                        vide.addProperty("type","");
                        nodes.add("node"+String.valueOf(index),nodeTempObject); 
                        edgesSup.add("node"+String.valueOf(index),vide); 
                        index++;
                    }
                }catch(Exception e){
                    System.out.println("Not a resource");
                }
            }  
             if(liste.size() > 0){
                tempObject.add("Music Composers this person frequently worked with as Producer",liste);
                infos.add(tempObject);
                JsonObject nodeAnonym = new JsonObject();
                nodeAnonym.addProperty("uri", "None");
                nodeAnonym.addProperty("color", "#4444ff");
                nodeAnonym.addProperty("name","");
                nodes.add("node"+String.valueOf(index),nodeAnonym); 
                edges.add("node"+String.valueOf(index), edgesSup);
                JsonObject edgeAnonym = new JsonObject();
                edgeAnonym.addProperty("type", "Music Composers frequently worked with as Producer");
                edgeSubject.add("node"+String.valueOf(index), edgeAnonym);
                index++;
            }
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int musicComposerFrequentlyWorkWithDirector(String uri,JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
         String query = "SELECT ?m (COUNT(?f) AS ?n) WHERE {\n" +
                        "?f rdf:type dbo:Film ;\n" +
                        "dbo:director <" + uri + "> ;\n" +
                        "dbo:musicComposer ?m .\n" +
                        "FILTER(?m != <" + uri + ">).\n" +
                        "}\n" +
                        "GROUP BY ?m\n" +
                        "ORDER BY DESC(?n)\n" +
                        "LIMIT 5";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonObject edgesSup = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                JsonObject nodeTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
                try{
                    Resource Uri = qs.getResource("m");
                    String uriS = Uri.getURI();              
                    String name = getObjectName(uriS);
                    /*if(name == null){
                       name = "Music Composer without documents.";
                    }*/
                    if(name != null){
                        name = adjustName(name);
                        infoTempObject.addProperty("uri",uriS);
                        infoTempObject.addProperty("value", name);
                        infoTempObject.addProperty("type", "Music Composer");
                        nodeTempObject.addProperty("uri", uriS);
                        nodeTempObject.addProperty("color", "#4444ff");
                        nodeTempObject.addProperty("name",name);
                        liste.add(infoTempObject);
                        JsonObject vide = new JsonObject();
                        vide.addProperty("type","");
                        nodes.add("node"+String.valueOf(index),nodeTempObject); 
                        edgesSup.add("node"+String.valueOf(index),vide); 
                        index++;
                    }
                }catch(Exception e){
                    System.out.println("Not a resource");
                }
            }  
              if(liste.size() > 0){
                tempObject.add("Music Composers this person frequently worked with as Director",liste);
                infos.add(tempObject);
                JsonObject nodeAnonym = new JsonObject();
                nodeAnonym.addProperty("uri", "None");
                nodeAnonym.addProperty("color", "#4444ff");
                nodeAnonym.addProperty("name","");
                nodes.add("node"+String.valueOf(index),nodeAnonym); 
                edges.add("node"+String.valueOf(index), edgesSup);
                JsonObject edgeAnonym = new JsonObject();
                edgeAnonym.addProperty("type", "Music Composers frequently worked with as Director");
                edgeSubject.add("node"+String.valueOf(index), edgeAnonym);
                index++;
            }
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int studioFrequentlyWorkWithActor(String uri,JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
         String query = "SELECT ?s (COUNT(?f) AS ?n) WHERE {\n" +
                        "?f rdf:type dbo:Film ;\n" +
                        "dbo:starring <" + uri + "> ;\n" +
                        "dbp:studio ?s .\n" +
                        "}\n" +
                        "GROUP BY ?s\n" +
                        "ORDER BY DESC(?n)\n" +
                        "LIMIT 5";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonObject edgesSup = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                JsonObject nodeTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
                try{
                    Resource Uri = qs.getResource("s");
                    String uriS = Uri.getURI();              
                    String name = getObjectName(uriS);
                    /*if(name == null){
                       name = "Studio without documents.";
                    }*/
                    if(name != null){
                        name = adjustName(name);
                        infoTempObject.addProperty("uri",uriS);
                        infoTempObject.addProperty("value", name);
                        infoTempObject.addProperty("type", "Company");
                        nodeTempObject.addProperty("uri", uriS);
                        nodeTempObject.addProperty("color", "#4444ff");
                        nodeTempObject.addProperty("name",name);
                        liste.add(infoTempObject);
                        JsonObject vide = new JsonObject();
                        vide.addProperty("type","");
                        nodes.add("node"+String.valueOf(index),nodeTempObject); 
                        edgesSup.add("node"+String.valueOf(index),vide); 
                        index++;
                    }
                }catch(Exception e){
                    System.out.println("Not a resource");
                }
            }  
             if(liste.size() > 0){
                tempObject.add("Studios this person frequently worked with as Actor",liste);
                infos.add(tempObject);
                JsonObject nodeAnonym = new JsonObject();
                nodeAnonym.addProperty("uri", "None");
                nodeAnonym.addProperty("color", "#4444ff");
                nodeAnonym.addProperty("name","");
                nodes.add("node"+String.valueOf(index),nodeAnonym); 
                edges.add("node"+String.valueOf(index), edgesSup);
                JsonObject edgeAnonym = new JsonObject();
                edgeAnonym.addProperty("type", "Studios frequently worked with as Actor");
                edgeSubject.add("node"+String.valueOf(index), edgeAnonym);
                index++;
            }
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int studioFrequentlyWorkWithProducer(String uri,JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
         String query = "SELECT ?s (COUNT(?f) AS ?n) WHERE {\n" +
                        "?f rdf:type dbo:Film ;\n" +
                        "dbo:producer <" + uri + "> ;\n" +
                        "dbp:studio ?s .\n" +
                        "}\n" +
                        "GROUP BY ?s\n" +
                        "ORDER BY DESC(?n)\n" +
                        "LIMIT 5";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonObject edgesSup = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                JsonObject nodeTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
                try{
                    Resource Uri = qs.getResource("s");
                    String uriS = Uri.getURI();              
                    String name = getObjectName(uriS);
                    /*if(name == null){
                       name = "Studio without documents.";
                    }*/
                    if(name != null){
                        name = adjustName(name);
                        infoTempObject.addProperty("uri",uriS);
                        infoTempObject.addProperty("value", name);
                        infoTempObject.addProperty("type", "Company");
                        nodeTempObject.addProperty("uri", uriS);
                        nodeTempObject.addProperty("color", "#4444ff");
                        nodeTempObject.addProperty("name",name);
                        liste.add(infoTempObject);
                        JsonObject vide = new JsonObject();
                        vide.addProperty("type","");
                        nodes.add("node"+String.valueOf(index),nodeTempObject); 
                        edgesSup.add("node"+String.valueOf(index),vide); 
                        index++;
                    }
                }catch(Exception e){
                    System.out.println("Not a resource");
                }
            }  
             if(liste.size() > 0){
                tempObject.add("Studios this person frequently worked with as Producer",liste);
                infos.add(tempObject);
                JsonObject nodeAnonym = new JsonObject();
                nodeAnonym.addProperty("uri", "None");
                nodeAnonym.addProperty("color", "#4444ff");
                nodeAnonym.addProperty("name","");
                nodes.add("node"+String.valueOf(index),nodeAnonym); 
                edges.add("node"+String.valueOf(index), edgesSup);
                JsonObject edgeAnonym = new JsonObject();
                edgeAnonym.addProperty("type", "Studios frequently worked with as Producer");
                edgeSubject.add("node"+String.valueOf(index), edgeAnonym);
                index++;
            } 
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int studioFrequentlyWorkWithDirector(String uri,JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
         String query = "SELECT ?s (COUNT(?f) AS ?n) WHERE {\n" +
                        "?f rdf:type dbo:Film ;\n" +
                        "dbo:director <" + uri + "> ;\n" +
                        "dbp:studio ?s .\n" +
                        "}\n" +
                        "GROUP BY ?s\n" +
                        "ORDER BY DESC(?n)\n" +
                        "LIMIT 5";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonObject edgesSup = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                JsonObject nodeTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
                try{
                    Resource Uri = qs.getResource("s");
                    String uriS = Uri.getURI();              
                    String name = getObjectName(uriS);
                    /*if(name == null){
                       name = "Studio without documents.";
                    }*/
                    if(name != null){
                        name = adjustName(name);
                        infoTempObject.addProperty("uri",uriS);
                        infoTempObject.addProperty("value", name);
                        infoTempObject.addProperty("type", "Company");
                        nodeTempObject.addProperty("uri", uriS);
                        nodeTempObject.addProperty("color", "#4444ff");
                        nodeTempObject.addProperty("name",name);
                        liste.add(infoTempObject);
                        JsonObject vide = new JsonObject();
                        vide.addProperty("type","");
                        nodes.add("node"+String.valueOf(index),nodeTempObject); 
                        edgesSup.add("node"+String.valueOf(index),vide); 
                        index++;
                    }
                }catch(Exception e){
                    System.out.println("Not a resource");
                }
            }  
             if(liste.size() > 0){
                tempObject.add("Studios this person frequently worked with as Director",liste);
                infos.add(tempObject);
                JsonObject nodeAnonym = new JsonObject();
                nodeAnonym.addProperty("uri", "None");
                nodeAnonym.addProperty("color", "#4444ff");
                nodeAnonym.addProperty("name","");
                nodes.add("node"+String.valueOf(index),nodeAnonym); 
                edges.add("node"+String.valueOf(index), edgesSup);
                JsonObject edgeAnonym = new JsonObject();
                edgeAnonym.addProperty("type", "Studios frequently worked with as Director");
                edgeSubject.add("node"+String.valueOf(index), edgeAnonym);
                index++;
            } 
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int studioFrequentlyWorkWithMusicComposer(String uri,JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
         String query = "SELECT ?s (COUNT(?f) AS ?n) WHERE {\n" +
                        "?f rdf:type dbo:Film ;\n" +
                        "dbo:musicComposer <" + uri + "> ;\n" +
                        "dbp:studio ?s .\n" +
                        "}\n" +
                        "GROUP BY ?s\n" +
                        "ORDER BY DESC(?n)\n" +
                        "LIMIT 5";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonObject edgesSup = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                JsonObject nodeTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
                try{
                    Resource Uri = qs.getResource("s");
                    String uriS = Uri.getURI();              
                    String name = getObjectName(uriS);
                    /*if(name == null){
                       name = "Studio without documents.";
                    }*/
                    if(name != null){
                        name = adjustName(name);
                        infoTempObject.addProperty("uri",uriS);
                        infoTempObject.addProperty("value", name);
                        infoTempObject.addProperty("type", "Company");
                        nodeTempObject.addProperty("uri", uriS);
                        nodeTempObject.addProperty("color", "#4444ff");
                        nodeTempObject.addProperty("name",name);
                        liste.add(infoTempObject);
                        JsonObject vide = new JsonObject();
                        vide.addProperty("type","");
                        nodes.add("node"+String.valueOf(index),nodeTempObject); 
                        edgesSup.add("node"+String.valueOf(index),vide); 
                        index++;
                    }
                }catch(Exception e){
                    System.out.println("Not a resource");
                }
            }  
             if(liste.size() > 0){
                tempObject.add("Studios this person frequently worked with as Music Composer",liste);
                infos.add(tempObject);
                JsonObject nodeAnonym = new JsonObject();
                nodeAnonym.addProperty("uri", "None");
                nodeAnonym.addProperty("color", "#4444ff");
                nodeAnonym.addProperty("name","");
                nodes.add("node"+String.valueOf(index),nodeAnonym); 
                edges.add("node"+String.valueOf(index), edgesSup);
                JsonObject edgeAnonym = new JsonObject();
                edgeAnonym.addProperty("type", "Studios frequently worked with as Music Composer");
                edgeSubject.add("node"+String.valueOf(index), edgeAnonym);
                index++;
            } 
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int actorFrequentlyWorkWithActor(String uri,JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
         String query = "SELECT ?a (COUNT(?f) AS ?n) WHERE {\n" +
                        "?f rdf:type dbo:Film ;\n" +
                        "dbo:starring <" + uri + "> ;\n" +
                        "dbo:starring ?a .\n" +
                        "FILTER(?a != <" + uri + ">).\n" +
                        "}\n" +
                        "GROUP BY ?a\n" +
                        "ORDER BY DESC(?n)\n" +
                        "LIMIT 5";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonObject edgesSup = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                JsonObject nodeTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
                try{
                    Resource Uri = qs.getResource("a");
                    String uriS = Uri.getURI();              
                    String name = getObjectName(uriS);
                    /*if(name == null){
                       name = "Actor without documents.";
                    }*/
                    if(name != null){
                        name = adjustName(name);
                        infoTempObject.addProperty("uri",uriS);
                        infoTempObject.addProperty("value", name);
                        infoTempObject.addProperty("type", "Actor");
                        nodeTempObject.addProperty("uri", uriS);
                        nodeTempObject.addProperty("color", "#4444ff");
                        nodeTempObject.addProperty("name",name);
                        liste.add(infoTempObject);
                        JsonObject vide = new JsonObject();
                        vide.addProperty("type","");
                        nodes.add("node"+String.valueOf(index),nodeTempObject); 
                        edgesSup.add("node"+String.valueOf(index),vide); 
                        index++;
                    }
                }catch(Exception e){
                    System.out.println("Not a resource");
                }
            } 
             if(liste.size() > 0){
                tempObject.add("Actors this person frequently worked with as Actor",liste);
                infos.add(tempObject);
                JsonObject nodeAnonym = new JsonObject();
                nodeAnonym.addProperty("uri", "None");
                nodeAnonym.addProperty("color", "#4444ff");
                nodeAnonym.addProperty("name","");
                nodes.add("node"+String.valueOf(index),nodeAnonym); 
                edges.add("node"+String.valueOf(index), edgesSup);
                JsonObject edgeAnonym = new JsonObject();
                edgeAnonym.addProperty("type", "Actors frequently worked with as Actor");
                edgeSubject.add("node"+String.valueOf(index), edgeAnonym);
                index++;
            } 
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int actorFrequentlyWorkWithProducer(String uri,JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
         String query = "SELECT ?a (COUNT(?f) AS ?n) WHERE {\n" +
                        "?f rdf:type dbo:Film ;\n" +
                        "dbo:producer <" + uri + "> ;\n" +
                        "dbo:starring ?a .\n" +
                        "FILTER(?a != <" + uri + ">).\n" +
                        "}\n" +
                        "GROUP BY ?a\n" +
                        "ORDER BY DESC(?n)\n" +
                        "LIMIT 5";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonObject edgesSup = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                JsonObject nodeTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
                try{
                    Resource Uri = qs.getResource("a");
                    String uriS = Uri.getURI();              
                    String name = getObjectName(uriS);
                    /*if(name == null){
                       name = "Actor without documents.";
                    }*/
                    if(name != null){
                        name = adjustName(name);
                        infoTempObject.addProperty("uri",uriS);
                        infoTempObject.addProperty("value", name);
                        infoTempObject.addProperty("type", "Actor");
                        nodeTempObject.addProperty("uri", uriS);
                        nodeTempObject.addProperty("color", "#4444ff");
                        nodeTempObject.addProperty("name",name);
                        liste.add(infoTempObject);
                        JsonObject vide = new JsonObject();
                        vide.addProperty("type","");
                        nodes.add("node"+String.valueOf(index),nodeTempObject); 
                        edgesSup.add("node"+String.valueOf(index),vide); 
                        index++;
                    }
                }catch(Exception e){
                    System.out.println("Not a resource");
                }
            }  
             if(liste.size() > 0){
                tempObject.add("Actors this person frequently worked with as Producer",liste);
                infos.add(tempObject);
                JsonObject nodeAnonym = new JsonObject();
                nodeAnonym.addProperty("uri", "None");
                nodeAnonym.addProperty("color", "#4444ff");
                nodeAnonym.addProperty("name","");
                nodes.add("node"+String.valueOf(index),nodeAnonym); 
                edges.add("node"+String.valueOf(index), edgesSup);
                JsonObject edgeAnonym = new JsonObject();
                edgeAnonym.addProperty("type", "Actors frequently worked with as Producer");
                edgeSubject.add("node"+String.valueOf(index), edgeAnonym);
                index++;
            } 
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int actorFrequentlyWorkWithDirector(String uri,JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
         String query = "SELECT ?a (COUNT(?f) AS ?n) WHERE {\n" +
                        "?f rdf:type dbo:Film ;\n" +
                        "dbo:director <" + uri + "> ;\n" +
                        "dbo:starring ?a .\n" +
                        "FILTER(?a != <" + uri + ">).\n" +
                        "}\n" +
                        "GROUP BY ?a\n" +
                        "ORDER BY DESC(?n)\n" +
                        "LIMIT 5";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonObject edgesSup = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                JsonObject nodeTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
                try{
                    Resource Uri = qs.getResource("a");
                    String uriS = Uri.getURI();              
                    String name = getObjectName(uriS);
                    /*if(name == null){
                       name = "Actor without documents.";
                    }*/
                    if(name != null){
                        name = adjustName(name);
                        infoTempObject.addProperty("uri",uriS);
                        infoTempObject.addProperty("value", name);
                        infoTempObject.addProperty("type", "Actor");
                        nodeTempObject.addProperty("uri", uriS);
                        nodeTempObject.addProperty("color", "#4444ff");
                        nodeTempObject.addProperty("name",name);
                        liste.add(infoTempObject);
                        JsonObject vide = new JsonObject();
                        vide.addProperty("type","");
                        nodes.add("node"+String.valueOf(index),nodeTempObject); 
                        edgesSup.add("node"+String.valueOf(index),vide); 
                        index++;
                    }
                }catch(Exception e){
                    System.out.println("Not a resource");
                }
            }
             if(liste.size() > 0){
                tempObject.add("Actors this person frequently worked with as Director",liste);
                infos.add(tempObject);
                JsonObject nodeAnonym = new JsonObject();
                nodeAnonym.addProperty("uri", "None");
                nodeAnonym.addProperty("color", "#4444ff");
                nodeAnonym.addProperty("name","");
                nodes.add("node"+String.valueOf(index),nodeAnonym); 
                edges.add("node"+String.valueOf(index), edgesSup);
                JsonObject edgeAnonym = new JsonObject();
                edgeAnonym.addProperty("type", "Actors frequently worked with as Director");
                edgeSubject.add("node"+String.valueOf(index), edgeAnonym);
                index++;
            } 
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int famousFilmActor(String uri,JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
         String query = "SELECT ?f ?g WHERE {\n" +
                        "?f rdf:type dbo:Film ;\n" +
                        "dbo:starring <"+ uri +"> ;\n" +
                        "dbo:gross ?g .\n" +
                        "}\n" +
                        "ORDER BY DESC(xsd:integer(?g))\n" +
                        "LIMIT 5";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonObject edgesSup = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                JsonObject nodeTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
                try{
                    Resource Uri = qs.getResource("f");
                    String uriS = Uri.getURI();              
                    String name = getObjectName(uriS);
                    /*if(name == null){
                        name = "film without documents";
                    }*/
                    if(name != null){
                        name = adjustName(name);
                        infoTempObject.addProperty("uri",uriS);
                        infoTempObject.addProperty("value", name);
                        infoTempObject.addProperty("type", "Film");
                        nodeTempObject.addProperty("uri", uriS);
                        nodeTempObject.addProperty("color", "#4444ff");
                        nodeTempObject.addProperty("name",name);
                        liste.add(infoTempObject);
                        JsonObject vide = new JsonObject();
                        vide.addProperty("type","");
                        nodes.add("node"+String.valueOf(index),nodeTempObject); 
                        edgesSup.add("node"+String.valueOf(index),vide); 
                        index++;
                    }
                }catch(Exception e){
                    System.out.println("Not a resource");
                }
            }  
             if(liste.size() > 0){
                tempObject.add("Famous Films as Actor",liste);
                infos.add(tempObject);
                JsonObject nodeAnonym = new JsonObject();
                nodeAnonym.addProperty("uri", "None");
                nodeAnonym.addProperty("color", "#4444ff");
                nodeAnonym.addProperty("name","");
                nodes.add("node"+String.valueOf(index),nodeAnonym); 
                edges.add("node"+String.valueOf(index), edgesSup);
                JsonObject edgeAnonym = new JsonObject();
                edgeAnonym.addProperty("type", "Famous Films as Actor");
                edgeSubject.add("node"+String.valueOf(index), edgeAnonym);
                index++;
            } 
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int majorFilmProducer(String uri,JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
         String query = "SELECT ?f ?g WHERE {\n" +
                        "?f rdf:type dbo:Film ;\n" +
                        "dbo:producer <"+ uri +"> ;\n" +
                        "dbo:gross ?g .\n" +
                        "}\n" +
                        "ORDER BY DESC(xsd:integer(?g))\n" +
                        "LIMIT 5";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonObject edgesSup = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                JsonObject nodeTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
                try{
                    Resource Uri = qs.getResource("f");
                    String uriS = Uri.getURI();              
                    String name = getObjectName(uriS);
                    /*if(name == null){
                        name = "film without documents";
                    }*/
                    if(name != null){
                        name = adjustName(name);
                        infoTempObject.addProperty("uri",uriS);
                        infoTempObject.addProperty("value", name);
                        infoTempObject.addProperty("type", "Film");
                        nodeTempObject.addProperty("uri", uriS);
                        nodeTempObject.addProperty("color", "#4444ff");
                        nodeTempObject.addProperty("name",name);
                        liste.add(infoTempObject);
                        JsonObject vide = new JsonObject();
                        vide.addProperty("type","");
                        nodes.add("node"+String.valueOf(index),nodeTempObject); 
                        edgesSup.add("node"+String.valueOf(index),vide); 
                        index++;
                    }
                }catch(Exception e){
                    System.out.println("Not a resource");
                }
            }  
             if(liste.size() > 0){
                tempObject.add("Major Films produced by this person",liste);
                infos.add(tempObject);
                JsonObject nodeAnonym = new JsonObject();
                nodeAnonym.addProperty("uri", "None");
                nodeAnonym.addProperty("color", "#4444ff");
                nodeAnonym.addProperty("name","");
                nodes.add("node"+String.valueOf(index),nodeAnonym); 
                edges.add("node"+String.valueOf(index), edgesSup);
                JsonObject edgeAnonym = new JsonObject();
                edgeAnonym.addProperty("type", "Major Films produced by this person");
                edgeSubject.add("node"+String.valueOf(index), edgeAnonym);
                index++;
            } 
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int majorFilmMusicComposer(String uri,JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
         String query = "SELECT ?f ?g WHERE {\n" +
                        "?f rdf:type dbo:Film ;\n" +
                        "dbo:musicComposer <"+ uri +"> ;\n" +
                        "dbo:gross ?g .\n" +
                        "}\n" +
                        "ORDER BY DESC(xsd:integer(?g))\n" +
                        "LIMIT 5";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonObject edgesSup = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                JsonObject nodeTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
                try{
                    Resource Uri = qs.getResource("f");
                    String uriS = Uri.getURI();              
                    String name = getObjectName(uriS);
                    /*if(name == null){
                        name = "film without documents";
                    }*/
                    if(name != null){
                        name = adjustName(name);
                        infoTempObject.addProperty("uri",uriS);
                        infoTempObject.addProperty("value", name);
                        infoTempObject.addProperty("type", "Film");
                        nodeTempObject.addProperty("uri", uriS);
                        nodeTempObject.addProperty("color", "#4444ff");
                        nodeTempObject.addProperty("name",name);
                        liste.add(infoTempObject);
                        JsonObject vide = new JsonObject();
                        vide.addProperty("type","");
                        nodes.add("node"+String.valueOf(index),nodeTempObject); 
                        edgesSup.add("node"+String.valueOf(index),vide); 
                        index++;
                    }
                }catch(Exception e){
                    System.out.println("Not a resource");
                }
            } 
             if(liste.size() > 0){
                tempObject.add("Major Films composed by this person",liste);
                infos.add(tempObject);
                JsonObject nodeAnonym = new JsonObject();
                nodeAnonym.addProperty("uri", "None");
                nodeAnonym.addProperty("color", "#4444ff");
                nodeAnonym.addProperty("name","");
                nodes.add("node"+String.valueOf(index),nodeAnonym); 
                edges.add("node"+String.valueOf(index), edgesSup);
                JsonObject edgeAnonym = new JsonObject();
                edgeAnonym.addProperty("type", "Major Films composed by this person");
                edgeSubject.add("node"+String.valueOf(index), edgeAnonym);
                index++;
            } 
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int majorFilmDirector(String uri,JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
         String query = "SELECT ?f ?g WHERE {\n" +
                        "?f rdf:type dbo:Film ;\n" +
                        "dbo:director <"+ uri +"> ;\n" +
                        "dbo:gross ?g .\n" +
                        "}\n" +
                        "ORDER BY DESC(xsd:integer(?g))\n" +
                        "LIMIT 5";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonObject edgesSup = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                JsonObject nodeTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
                try{
                    Resource Uri = qs.getResource("f");
                    String uriS = Uri.getURI();              
                    String name = getObjectName(uriS);
                    /*if(name == null){
                        name = "film without documents";
                    }*/
                    if(name != null){
                        name = adjustName(name);
                        infoTempObject.addProperty("uri",uriS);
                        infoTempObject.addProperty("value", name);
                        infoTempObject.addProperty("type", "Film");
                        nodeTempObject.addProperty("uri", uriS);
                        nodeTempObject.addProperty("color", "#4444ff");
                        nodeTempObject.addProperty("name",name);
                        liste.add(infoTempObject);
                        JsonObject vide = new JsonObject();
                        vide.addProperty("type","");
                        nodes.add("node"+String.valueOf(index),nodeTempObject); 
                        edgesSup.add("node"+String.valueOf(index),vide); 
                        index++;
                    }
                }catch(Exception e){
                    System.out.println("Not a resource");
                }
            } 
             if(liste.size() > 0){
                tempObject.add("Major Films directed by this person",liste);
                infos.add(tempObject);
                JsonObject nodeAnonym = new JsonObject();
                nodeAnonym.addProperty("uri", "None");
                nodeAnonym.addProperty("color", "#4444ff");
                nodeAnonym.addProperty("name","");
                nodes.add("node"+String.valueOf(index),nodeAnonym); 
                edges.add("node"+String.valueOf(index), edgesSup);
                JsonObject edgeAnonym = new JsonObject();
                edgeAnonym.addProperty("type", "Major Films directed by this person");
                edgeSubject.add("node"+String.valueOf(index), edgeAnonym);
                index++;
            } 
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int numberOfStarredFilmActor(String uri,JsonArray infos, int index){
         String query = "SELECT (COUNT(?f) AS ?n) WHERE {\n" +
                        "?f rdf:type dbo:Film ;\n" +
                        "dbo:starring <"+ uri +"> .\n" +
                        "}\n";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
               String value = qs.get("n").toString();
               int ocurrence = value.indexOf("^");
               value = value.substring(0,ocurrence);
               infoTempObject.addProperty("uri","None");
               infoTempObject.addProperty("value", value);
               infoTempObject.addProperty("type", "Litteral");
               liste.add(infoTempObject);
            }  
             if(liste.size() > 0){
                tempObject.add("Number of films this person starred in",liste);
                infos.add(tempObject);
            }
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int numberOfProducedFilmProducer(String uri,JsonArray infos, int index){
         String query = "SELECT (COUNT(?f) AS ?n) WHERE {\n" +
                        "?f rdf:type dbo:Film ;\n" +
                        "dbo:producer <"+ uri +"> .\n" +
                        "}\n";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
               String value = qs.get("n").toString();
               int ocurrence = value.indexOf("^");
               value = value.substring(0,ocurrence);
               infoTempObject.addProperty("uri","None");
               infoTempObject.addProperty("value", value);
               infoTempObject.addProperty("type", "Litteral");
               liste.add(infoTempObject);
            }  
             if(liste.size() > 0){
                tempObject.add("Number of films this person produced",liste);
                infos.add(tempObject);
            }
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int numberOfDirectedFilmDirector(String uri,JsonArray infos, int index){
         String query = "SELECT (COUNT(?f) AS ?n) WHERE {\n" +
                        "?f rdf:type dbo:Film ;\n" +
                        "dbo:director <"+ uri +"> .\n" +
                        "}\n";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
               String value = qs.get("n").toString();
               int ocurrence = value.indexOf("^");
               value = value.substring(0,ocurrence);
               infoTempObject.addProperty("uri","None");
               infoTempObject.addProperty("value", value);
               infoTempObject.addProperty("type", "Litteral");
               liste.add(infoTempObject);
            } 
             if(liste.size() > 0){
                tempObject.add("Number of Films this person directed",liste);
                infos.add(tempObject);
            }
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int numberOfComposedFilmComposer(String uri,JsonArray infos, int index){
         String query = "SELECT (COUNT(?f) AS ?n) WHERE {\n" +
                        "?f rdf:type dbo:Film ;\n" +
                        "dbo:musicComposer <"+ uri +"> .\n" +
                        "}\n";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
               String value = qs.get("n").toString();
               int ocurrence = value.indexOf("^");
               value = value.substring(0,ocurrence);
               infoTempObject.addProperty("uri","None");
               infoTempObject.addProperty("value", value);
               infoTempObject.addProperty("type", "Litteral");
               liste.add(infoTempObject);
            }  
             if(liste.size() > 0){
                tempObject.add("Number of Films this person composed",liste);
                infos.add(tempObject);
            }
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int numberOfEmployeesCompany(String uri,JsonArray infos, int index){
         String query = "SELECT ?noe WHERE {\n" +
                        "  <" + uri +"> rdf:type dbo:Company ;\n" +
                        "     dbo:numberOfEmployees ?noe.\n" +
                        "}";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
               String value = qs.get("noe").toString();
               int ocurrence = value.indexOf("^");
               value = value.substring(0,ocurrence);
               infoTempObject.addProperty("uri","None");
               infoTempObject.addProperty("value", value);
               infoTempObject.addProperty("type", "Litteral");
               liste.add(infoTempObject);
            }  
             if(liste.size() > 0){
                tempObject.add("Number of Employees",liste);
                infos.add(tempObject);
            }
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int numberOfFilmDistributedOrProducedCompany(String uri,JsonArray infos, int index){
         String query = "SELECT (COUNT(?f) AS ?numberOfMovies) WHERE {\n" +
                        "  {?f rdf:type dbo:Film ;\n" +
                        "     dbp:studio <" + uri + "> .}\n" +
                        "  UNION\n" +
                         "  {?f rdf:type dbo:Film ;\n" +
                        "     dbo:distributor <" + uri + "> .}\n" +
                        "}";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
               String value = qs.get("numberOfMovies").toString();
               int ocurrence = value.indexOf("^");
               value = value.substring(0,ocurrence);
               infoTempObject.addProperty("uri","None");
               infoTempObject.addProperty("value", value);
               infoTempObject.addProperty("type", "Litteral");
               liste.add(infoTempObject);
            }  
             if(liste.size() > 0){
                tempObject.add("Number of Films distributed or produced by this Company",liste);
                infos.add(tempObject);
            }
        } catch(Exception e){
            
        }
         return index;
  }
  
  public static int majorFilmsDistributedOrProducedCompany(String uri,JsonArray infos, JsonObject nodes, JsonObject edges, JsonObject edgeSubject, int index){
         String query = "SELECT ?f ?g WHERE {\n" +
                        "{?f rdf:type dbo:Film ;\n" +
                        "dbp:studio <"+ uri +"> ;\n" +
                        "dbo:gross ?g .}\n" +
                        "UNION\n" +
                        "{?f rdf:type dbo:Film ;\n" +
                        "dbo:distributor <"+ uri +"> ;\n" +
                        "dbo:gross ?g .}\n" +
                        "}\n" +
                        "ORDER BY DESC(xsd:integer(?g))\n" +
                        "LIMIT 5";
         try (QueryExecution qexec = createPrefixedQuery(query)) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
            // Execute.
            ResultSet rs = qexec.execSelect();
            JsonObject tempObject = new JsonObject();
            JsonObject edgesSup = new JsonObject();
            JsonArray liste = new JsonArray();
            //Traitement
             while (rs.hasNext()) {
                JsonObject infoTempObject = new JsonObject();
                JsonObject nodeTempObject = new JsonObject();
                QuerySolution qs = rs.nextSolution();
                try{
                    Resource Uri = qs.getResource("f");
                    String uriS = Uri.getURI();              
                    String name = getObjectName(uriS);
                    /*if(name == null){
                        name = "film without documents";
                    }*/
                    if(name != null){
                        name = adjustName(name);
                        infoTempObject.addProperty("uri",uriS);
                        infoTempObject.addProperty("value", name);
                        infoTempObject.addProperty("type", "Film");
                        nodeTempObject.addProperty("uri", uriS);
                        nodeTempObject.addProperty("color", "#4444ff");
                        nodeTempObject.addProperty("name",name);
                        liste.add(infoTempObject);
                        JsonObject vide = new JsonObject();
                        vide.addProperty("type","");
                        nodes.add("node"+String.valueOf(index),nodeTempObject); 
                        edgesSup.add("node"+String.valueOf(index),vide); 
                        index++;
                    }
                }catch(Exception e){
                    System.out.println("Not a resource");
                }
             }
             if(liste.size() > 0){
                tempObject.add("Major Films of this Company",liste);
                infos.add(tempObject);
                JsonObject nodeAnonym = new JsonObject();
                nodeAnonym.addProperty("uri", "None");
                nodeAnonym.addProperty("color", "#4444ff");
                nodeAnonym.addProperty("name","");
                nodes.add("node"+String.valueOf(index),nodeAnonym); 
                edges.add("node"+String.valueOf(index), edgesSup);
                JsonObject edgeAnonym = new JsonObject();
                edgeAnonym.addProperty("type", "Major Films of this Company");
                edgeSubject.add("node"+String.valueOf(index), edgeAnonym);
                index++;
            } 
        } catch(Exception e){
            
        }
         return index;
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
