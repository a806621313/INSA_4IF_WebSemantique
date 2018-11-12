package project.controller.services;

import java.util.HashMap;
import java.util.Map;

public class ResourceServices {
  private static Map<String, String> companies = new HashMap<String, String>();
  private static Map<String, String> films = new HashMap<String, String>();
  private static Map<String, String> persons = new HashMap<String, String>();
  
  /**
   *
   */
  public static void init() {
    // query all companies
    
    // query all films
    
    // query all persons
    
  }
  
  public static String[] getBestCompanyUri(String query) {
    return new String[]{};
  }
  
  public static String[] getBestFilmUri(String query) {
    return new String[]{};
  }
  
   public static String[] getBestPersonUri(String query) {
    return new String[]{};
  }
}
