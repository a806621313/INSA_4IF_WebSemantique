package project.controller.services;

import java.util.ArrayList;
import project.utils.StringUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ResourceServices {
  
  /** A local database of company names and URIs (key = name ; value = URI) */
  private final static Map<String, String> COMPANIES = new LinkedHashMap<>();
  /** A local database of film names and URIs (key = name ; value = URI) */
  private final static Map<String, String> FILMS = new LinkedHashMap<>();
  /** A local database of person names and URIs (key = name ; value = URI) */
  private final static Map<String, String> PERSONS = new LinkedHashMap<>();
  /** A local database of all the resource names and URIs (key = name ; value = URI) */
  private final static Map<String, String> ALL_RESOURCES = new LinkedHashMap<>();
  
  /** Maximum distance allowed between query words and resource names */
  private final static int MAX_LEVENSHTEIN_DISTANCE = 3;
  /** Maximum number of results when using the Levenshtein distance matching algorithm */
  private final static int MAX_LEVENSHTEIN_RESULTS = 10;
  /** Maximum number of results when using the standard matching algorithm */
  private final static int MAX_RESULTS = 50;
  /** Number of search suggestions per resource category sent to the user */
  private final static int NUMBER_OF_SUGGESTIONS = 3;
  
  /**
   * Initializes the application local resource databases.
   */
  public static void init() {
    COMPANIES.putAll(SparqlServices.getAllCompanyNamesAndUris());
    FILMS.putAll(SparqlServices.getAllFilmNamesAndUris());
    PERSONS.putAll(SparqlServices.getAllPersonNamesAndUris());
    ALL_RESOURCES.putAll(COMPANIES);
    ALL_RESOURCES.putAll(FILMS);
    ALL_RESOURCES.putAll(PERSONS);
  }
  
  public static Map<String, String> getRandomCompanies() {
    return getRandomResources(COMPANIES);
  }
  
  public static Map<String, String> getRandomFilms() {
    return getRandomResources(FILMS);
  }
  
  public static Map<String, String> getRandomPersons() {
    return getRandomResources(PERSONS);
  }
  
  public static Map<String, String> getRandomResources() {
    return getRandomResources(ALL_RESOURCES);
  }
  
  public static Map<String, String> getRandomResources(Map<String, String> res) {
    Map<String, String> randomResults = new LinkedHashMap<>();
    List<Map.Entry<String, String>> listMatch = new ArrayList<>(res.entrySet());
    Random random = new Random();
    
    for (int i = 0; i < NUMBER_OF_SUGGESTIONS; i++) {
      int index = random.nextInt(listMatch.size());
      Map.Entry<String,String> result = listMatch.get(index);
      randomResults.put(result.getKey(), result.getValue());
      listMatch.remove(index);
    }
    
    return randomResults;
  }
  
  public static Map<String, String> matchCompaniesByName(String name) {
    return matchResourcesByName(name, COMPANIES);
  }
  
  public static Map<String, String> matchFilmsByName(String name) {
    return matchResourcesByName(name, FILMS);
  }
  
  public static Map<String, String> matchPersonsByName(String name) {
    return matchResourcesByName(name, PERSONS);
  }
  
  public static Map<String, String> matchResourcesByName(String name) {
    return matchResourcesByName(name, ALL_RESOURCES);
  }

  private static Map<String, String> matchResourcesByName(String name, Map<String, String> res) {
    Map<String, String> relevantResults = new LinkedHashMap<>();
    String[] queryWords = name.trim().split("\\s+");  // Each word composing the name query
    
    // Temporary structure to save the distance between the query and the resources
    Map<String, Integer> resourceMatches = new LinkedHashMap<>();
    for (Map.Entry<String, String> entry : res.entrySet()) {
      resourceMatches.put(entry.getKey(), 0);
    }
      
     // Match resource names with query words
    boolean queryWordsMatchResources = false;
    for (Map.Entry<String, Integer> match : resourceMatches.entrySet()) {
      for (String word : queryWords) {
        if (match.getKey().toUpperCase().contains(word.toUpperCase())) {
          queryWordsMatchResources = true;
          match.setValue(match.getValue() + word.length());
        }
      }
    }
    
    if (queryWordsMatchResources) {
      // Keep only resources that matched with the name query
      List<Map.Entry<String, Integer>> sortedMatches = new ArrayList<>();
      for (Map.Entry<String, Integer> match : resourceMatches.entrySet()) {
        if (match.getValue() > 0) {
          sortedMatches.add(match);
        }
      }
      
      // Refine resources relevance: relevance is (Match Length)^2 - Levenshtein Distance
      for (Map.Entry<String, Integer> match : sortedMatches) {
        int relevance = match.getValue() * match.getValue() - StringUtils.getLevenshteinDistance(match.getKey(), name);
        match.setValue(relevance);
      }
      
      // Sort the resources by relevance
      Collections.sort(sortedMatches, new Comparator<Map.Entry<String, Integer>>() {
        @Override
        public int compare(Map.Entry<String, Integer> r1, Map.Entry<String, Integer> r2) {
          return - r1.getValue().compareTo(r2.getValue());
        }
      });
      
      int numberOfResults = 0;
      for (Map.Entry<String, Integer> match : sortedMatches) {
        if(numberOfResults > MAX_RESULTS) break;
        relevantResults.put(match.getKey(), res.get(match.getKey()));
        numberOfResults++;
      }
    }
    // If no resource name matches the query, compute Levenshtein distances
    else {
      for (Map.Entry<String, Integer> match : resourceMatches.entrySet()){
        int minDistance = MAX_LEVENSHTEIN_DISTANCE + 1;
        for(String resourceWord : match.getKey().split("\\s+")){
          for (String queryWord : queryWords) {
            int distance = StringUtils.getLevenshteinDistance(queryWord.toUpperCase(), resourceWord.toUpperCase());
            minDistance = Math.min(minDistance, distance);
          }
        }
        match.setValue(minDistance);
      }

      // Sort the resources by relevance (lowest Levenshtein distance)
      List<Map.Entry<String, Integer>> sortedMatches = new ArrayList<>(resourceMatches.entrySet());
      Collections.sort(sortedMatches, new Comparator<Map.Entry<String, Integer>>() {
        @Override
        public int compare(Map.Entry<String, Integer> r1, Map.Entry<String, Integer> r2){
          return r1.getValue().compareTo(r2.getValue());
        }
      });
      
      int numberOfLevenshteinResults = 0;
      for (Map.Entry<String, Integer> match : sortedMatches) {
        if (match.getValue() > MAX_LEVENSHTEIN_DISTANCE) break;
        if (numberOfLevenshteinResults > MAX_LEVENSHTEIN_RESULTS) break;
        relevantResults.put(match.getKey(), res.get(match.getKey()));
        numberOfLevenshteinResults++;
      }
    }
    
    return relevantResults;
  }
  
  public static String getCategory(String uri) {
    if (COMPANIES.containsValue(uri)) {
      return "company";
    } else if(FILMS.containsValue(uri)) {
      return "film";
    } else if(PERSONS.containsValue(uri)) {
      return "person";
    }
    return null;
  }
  
}
