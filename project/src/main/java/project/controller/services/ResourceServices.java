package project.controller.services;

import project.utils.StringUtils;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResourceServices {
  
  /** A local database of company names and URIs (key = name ; value = URI) */
  private final static Map<String, String> COMPANIES = new LinkedHashMap<String, String>();
  /** A local database of film names and URIs (key = name ; value = URI) */
  private final static Map<String, String> FILMS = new LinkedHashMap<String, String>();
  /** A local database of person names and URIs (key = name ; value = URI) */
  private final static Map<String, String> PERSONS = new LinkedHashMap<String, String>();
  
  private final static int LEVENSHTEIN_LIMIT = 3;
  
  /**
   * Initializes the application local resource databases.
   */
  public static void init() {
    COMPANIES.putAll(SparqlServices.getAllCompanyNamesAndUris());
    FILMS.putAll(SparqlServices.getAllFilmNamesAndUris());
    PERSONS.putAll(SparqlServices.getAllPersonNamesAndUris());
  }
  
  public static Map<String, String> getRandomResources() {
    return null;
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

  private static Map<String, String> matchResourcesByName(String name, Map<String, String> res) {
    Map<String, String> relevantResults = new LinkedHashMap<String, String>();
    String[] queryWords = name.trim().split("\\s+");  // Each word composing the name query
    
    // Temporary Map to save the distance between the query and the resources
    Map<String, Integer> resourceMatches = new LinkedHashMap<String, Integer>();
    for (Map.Entry<String, String> entry : res.entrySet()) {
      resourceMatches.put(entry.getKey(), 0);
    }
      
     // Find resource names that contain query words
    boolean queryWordsMatchResources = false;
    for (Map.Entry<String, Integer> match : resourceMatches.entrySet()){
      for (String word : queryWords) {
        if (match.getKey().toUpperCase().contains(word.toUpperCase())) {
          queryWordsMatchResources = true;
          match.setValue(match.getValue() + 1);
        }
      }
    }
    
    if (queryWordsMatchResources) {
      // Sort the resources by relevance (number of matches)
      int maxMatchValue;
      String maxMatchResource;
      do {
        maxMatchValue = 0;
        maxMatchResource = null;

        for (Map.Entry<String, Integer> match : resourceMatches.entrySet()){
          if (match.getValue() > maxMatchValue){
            maxMatchValue = match.getValue();
            maxMatchResource = match.getKey();
          }
        }

        if (maxMatchResource != null){
          relevantResults.put(maxMatchResource, res.get(maxMatchResource));
          resourceMatches.remove(maxMatchResource);
        }
      } while(maxMatchValue != 0);
    }
    // If no resource name matches the query, compute Levenshtein distances
    else {
      for (Map.Entry<String, Integer> match : resourceMatches.entrySet()){
        int minDistance = LEVENSHTEIN_LIMIT+1;
        for(String resourceWord : match.getKey().split("\\s+")){
          for (String queryWord : queryWords) {
            int distance = StringUtils.getLevenshteinDistance(queryWord.toUpperCase(), resourceWord.toUpperCase());
            minDistance = Math.min(minDistance, distance);
          }
        }
        match.setValue(minDistance);
      }

      // Sort the resources by relevance (lowest Levenshtein distance)
      int maxMatchValue;
      String maxMatchResource;
      do {
        maxMatchValue = LEVENSHTEIN_LIMIT+1;
        maxMatchResource = null;
        for (Map.Entry<String, Integer> match : resourceMatches.entrySet()){
          if (match.getValue() < maxMatchValue){
            maxMatchValue = match.getValue();
            maxMatchResource = match.getKey();
          }
        }

        if (maxMatchResource != null){
          relevantResults.put(maxMatchResource, res.get(maxMatchResource));
          resourceMatches.remove(maxMatchResource);
        }
      } while (maxMatchValue <= LEVENSHTEIN_LIMIT);
    }
    
    return relevantResults;
  }
  
}
