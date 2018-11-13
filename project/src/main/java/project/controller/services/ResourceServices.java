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
    
    Map<String, String> releventResults = new LinkedHashMap<String, String>();
    
    String[] queryWords = name.trim().split("\\s+");  //Each word composing the name
    
    
    //Temporary Map to save distance between query and resources
    Map<String, Integer> match = new LinkedHashMap<String, Integer>();
    for (Map.Entry<String, String> entry : res.entrySet()) {
        match.put(entry.getKey(),0);
    }
      
     //Find resource names that contain query words
    boolean nameMatchQuery = false;
    for(Map.Entry<String, Integer> keyRes : match.entrySet()){
        for (String queryWord : queryWords) {
            if (keyRes.getKey().toUpperCase().contains(queryWord.toUpperCase())) {
                nameMatchQuery = true;
                keyRes.setValue(keyRes.getValue()+1);
            }
        }
    }
    
    if(nameMatchQuery){
         // Sort the resources by relevance (number of matches)
        int maxMatch;
        String keyMatch;
        do{
            maxMatch = 0;
            keyMatch = null;
            
            for(Map.Entry<String, Integer> keyRes : match.entrySet()){
                if(keyRes.getValue()>maxMatch){
                    maxMatch = keyRes.getValue();
                    keyMatch = keyRes.getKey();
                }
            }
            
            if(keyMatch!=null){
                releventResults.put(keyMatch, res.get(keyMatch));
                match.remove(keyMatch);
            }
        }while(maxMatch!=0);
    }
    // If no resource name matches the query, compute Levenshtein distances
    else{
        for(Map.Entry<String, Integer> keyRes : match.entrySet()){
            int minDistance = LEVENSHTEIN_LIMIT+1;
            for(String resourceWord : keyRes.getKey().split("\\s+")){
                for (String queryWord : queryWords) {
                    int distance = StringUtils.getLevenshteinDistance(queryWord.toUpperCase(), resourceWord.toUpperCase());
                    if(distance<minDistance){
                        minDistance = distance;
                    }
                }
            }
            keyRes.setValue(minDistance);
        }
        
        // Sort the resources by relevance (lowest Levenshtein distance)
        int minMatch;
        String keyMatch;
        do{
            minMatch = LEVENSHTEIN_LIMIT+1;
            keyMatch = null;
            for(Map.Entry<String, Integer> keyRes : match.entrySet()){
                if(keyRes.getValue()<minMatch){
                    minMatch = keyRes.getValue();
                    keyMatch = keyRes.getKey();
                }
            }
            
            if(keyMatch!=null){
                releventResults.put(keyMatch, res.get(keyMatch));
                match.remove(keyMatch);
            }
        }while(minMatch<=LEVENSHTEIN_LIMIT);
    }
        
    
    
    return releventResults;
  }
  
}
