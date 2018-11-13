package project.controller.services;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResourceServices {
  
  /** A local database of company names and URIs (key = name ; value = URI) */
  private final static Map<String, String> COMPANIES = new LinkedHashMap<String, String>();
  /** A local database of film names and URIs (key = name ; value = URI) */
  private final static Map<String, String> FILMS = new LinkedHashMap<String, String>();
  /** A local database of person names and URIs (key = name ; value = URI) */
  private final static Map<String, String> PERSONS = new LinkedHashMap<String, String>();
  
  /**
   * Initializes the application local resource databases.
   */
  public static void init() {
    // Fill the local resource databases
    
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
    // Find resource names that contain query words
    
    // If no resource name matches the query, compute Levenshtein distances
    
    // Sort the resources by relevance (number of matches or lowest Levenshtein distance)
    
    return null;
  }
  
}
