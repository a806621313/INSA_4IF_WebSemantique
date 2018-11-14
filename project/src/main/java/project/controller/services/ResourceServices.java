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
  private final static Map<String, String> COMPANIES = new LinkedHashMap<String, String>();
  /** A local database of film names and URIs (key = name ; value = URI) */
  private final static Map<String, String> FILMS = new LinkedHashMap<String, String>();
  /** A local database of person names and URIs (key = name ; value = URI) */
  private final static Map<String, String> PERSONS = new LinkedHashMap<String, String>();
  
  private final static int LEVENSHTEIN_LIMIT = 3;
  private final static int NUMBER_OF_RANDOM = 3;
  
  /**
   * Initializes the application local resource databases.
   */
  public static void init() {
    //COMPANIES.putAll(SparqlServices.getAllCompanyNamesAndUris());
    //FILMS.putAll(SparqlServices.getAllFilmNamesAndUris());
    //PERSONS.putAll(SparqlServices.getAllPersonNamesAndUris());
    
    COMPANIES.put("Disney", "d");
    COMPANIES.put("Pixar", "d");
    COMPANIES.put("DreamWorks", "d");
    COMPANIES.put("Twenty Century Fox", "d");
    COMPANIES.put("Marvel", "d");
    COMPANIES.put("Warner Bros", "d");
    COMPANIES.put("Gaumont", "d");
    COMPANIES.put("Pathe", "d");
    COMPANIES.put("Netflix", "d");
    COMPANIES.put("Paramount Pictures", "d");
    
    FILMS.put("FFDisney", "d");
    FILMS.put("FFPixar", "d");
    FILMS.put("FFDreamWorks", "d");
    FILMS.put("FFTwenty Century Fox", "d");
    FILMS.put("FFMarvel", "d");
    FILMS.put("FFWarner Bros", "d");
    FILMS.put("FFGaumont", "d");
    FILMS.put("FFFFNetflix", "d");
    FILMS.put("Paramount Pictures", "d");
    
    PERSONS.put("PPDisney", "d");
    PERSONS.put("PPPixar", "d");
    PERSONS.put("PPreamWorks", "d");
    PERSONS.put("PPTwenty Century Fox", "d");
    PERSONS.put("PPFMarvel", "d");
    PERSONS.put("PP Bros", "d");
    PERSONS.put("PPFPPGaumont", "d");
    PERSONS.put("PPFFFNetflix", "d");
    PERSONS.put("PParamount Pictures", "d");
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
  
  public static Map<String, String> matchCompaniesByName(String name) {
    return matchResourcesByName(name, COMPANIES);
  }
  
  public static Map<String, String> matchFilmsByName(String name) {
    return matchResourcesByName(name, FILMS);
  }
  
  public static Map<String, String> matchPersonsByName(String name) {
    return matchResourcesByName(name, PERSONS);
  }
  
  public static Map<String, String> getRandomResources(Map<String, String> res) {
    Map<String, String> randomResults = new LinkedHashMap<String,String>();
    List<Map.Entry<String, String>> listMatch =new ArrayList<Map.Entry<String, String>>(res.entrySet());
    Random random = new Random();
    
    for(int i = 0; i<NUMBER_OF_RANDOM; i++)
    {
      int index = random.nextInt(listMatch.size());
      Map.Entry<String,String> result = listMatch.get(index);
      randomResults.put(result.getKey(),result.getValue());
      listMatch.remove(index);
    }
    
    return randomResults;
  }

  private static Map<String, String> matchResourcesByName(String name, Map<String, String> res) {
    Map<String, String> relevantResults = new LinkedHashMap<String, String>();
    List<Map.Entry<String, Integer>> listMatch;
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
      listMatch =new ArrayList<Map.Entry<String, Integer>>(resourceMatches.entrySet());
      Collections.sort(listMatch, new Comparator<Map.Entry<String, Integer>>() {
        public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b){
          return b.getValue().compareTo(a.getValue());
        }
      });
      
      for (Map.Entry<String, Integer> match : listMatch) {
        if(match.getValue() == 0)
          break;
        
        relevantResults.put(match.getKey(), res.get(match.getKey()));
      }
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
      listMatch = new ArrayList<Map.Entry<String, Integer>>(resourceMatches.entrySet());
      Collections.sort(listMatch, new Comparator<Map.Entry<String, Integer>>() {
        public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b){
          return a.getValue().compareTo(b.getValue());
        }
      });
      
      for (Map.Entry<String, Integer> match : listMatch) {
        if(match.getValue() > LEVENSHTEIN_LIMIT)
          break;
        
        relevantResults.put(match.getKey(), res.get(match.getKey()));
      }
    }
    
    
    return relevantResults;
  }
  
}
