package project.controller.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import project.utils.StringUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResourceServices {
  
  /** A local database of company names and URIs (key = name ; value = URI) */
  private static Map<String, String> COMPANIES;
  /** A local database of film names and URIs (key = name ; value = URI) */
  private static Map<String, String> FILMS;
  /** A local database of person names and URIs (key = name ; value = URI) */
  private static Map<String, String> PERSONS;
  /** A local database of all the resource names and URIs (key = name ; value = URI) */
  private static Map<String, String> ALL_RESOURCES;
  
  /** Maximum distance allowed between query words and resource names */
  private final static int MAX_LEVENSHTEIN_DISTANCE = 3;
  /** Maximum number of results when using the Levenshtein distance matching algorithm */
  private final static int MAX_LEVENSHTEIN_RESULTS = 10;
  /** Maximum number of results when using the standard matching algorithm */
  private final static int MAX_RESULTS = 50;
  /** Number of search suggestions per resource category sent to the user */
  private final static int NUMBER_OF_SUGGESTIONS = 3;
  
  /** Cache path */
  private final static String CACHE_PATH = "C:/Users/alexi/Desktop"; // Machine-specific path
  /** Companies cache filenames */
  private final static String COMPANIES_CACHE_FILENAME = "companies-cache";
  /** Films cache filenames */
  private final static String FILMS_CACHE_FILENAME = "films-cache";
  /** Persons cache filenames */
  private final static String PERSONS_CACHE_FILENAME = "persons-cache";
  
  /**
   * Initializes the application local resource databases.
   */
  public static void init() {
    try {
      // Load the resources from the cache
      Logger.getLogger(ResourceServices.class.getName()).log(Level.SEVERE, "Loading resources from the cache...");
      COMPANIES = new LinkedHashMap<>();
      FILMS = new LinkedHashMap<>();
      PERSONS = new LinkedHashMap<>();
      ALL_RESOURCES = new LinkedHashMap<>();
      COMPANIES.putAll(loadResourceDataFromCache(COMPANIES_CACHE_FILENAME));
      FILMS.putAll(loadResourceDataFromCache(FILMS_CACHE_FILENAME));
      PERSONS.putAll(loadResourceDataFromCache(PERSONS_CACHE_FILENAME));
      ALL_RESOURCES.putAll(COMPANIES);
      ALL_RESOURCES.putAll(FILMS);
      ALL_RESOURCES.putAll(PERSONS);
    } catch (Exception ex) {
      Logger.getLogger(ResourceServices.class.getName()).log(Level.SEVERE, "Cache miss.");
      Logger.getLogger(ResourceServices.class.getName()).log(Level.SEVERE, ex.getMessage());
    }
    
    // Update the resources and the cache in a background process
    new Thread() {
      @Override
      public void run() {
        try {
          // Update each data category
          Logger.getLogger(ResourceServices.class.getName()).log(Level.INFO, "Fetching Companies...");
          Map<String, String> remoteData = SparqlServices.getAllCompanyNamesAndUris();
          synchronized (COMPANIES) {
            Logger.getLogger(ResourceServices.class.getName()).log(Level.INFO, "Synchronizing Companies...");
            COMPANIES.clear();
            COMPANIES.putAll(remoteData);
          }
          Logger.getLogger(ResourceServices.class.getName()).log(Level.INFO, "Fetching Films...");
          remoteData = SparqlServices.getAllFilmNamesAndUris();
          synchronized (FILMS) {
            Logger.getLogger(ResourceServices.class.getName()).log(Level.INFO, "Synchronizing Films...");
            FILMS.clear();
            FILMS.putAll(remoteData);
          }
          Logger.getLogger(ResourceServices.class.getName()).log(Level.INFO, "Fetching Persons...");
          remoteData = SparqlServices.getAllPersonNamesAndUris();
          synchronized (PERSONS) {
            Logger.getLogger(ResourceServices.class.getName()).log(Level.INFO, "Synchronizing Persons...");
            PERSONS.clear();
            PERSONS.putAll(remoteData);
          }
          synchronized (ALL_RESOURCES) {
            Logger.getLogger(ResourceServices.class.getName()).log(Level.INFO, "Synchronizing Resources...");
            ALL_RESOURCES.clear();
            ALL_RESOURCES.putAll(COMPANIES);
            ALL_RESOURCES.putAll(FILMS);
            ALL_RESOURCES.putAll(PERSONS);
          }
          
          // Update the cache
          Logger.getLogger(ResourceServices.class.getName()).log(Level.INFO, "Updating Cached Resources...");
          saveResourceDataToCache(COMPANIES, COMPANIES_CACHE_FILENAME);
          saveResourceDataToCache(FILMS, FILMS_CACHE_FILENAME);
          saveResourceDataToCache(PERSONS, PERSONS_CACHE_FILENAME);

          Logger.getLogger(ResourceServices.class.getName()).log(Level.INFO, "Initialization Complete.");
        } catch (Exception ex) {
          Logger.getLogger(ResourceServices.class.getName()).log(Level.SEVERE, "Synchronization Failed.");
          Logger.getLogger(ResourceServices.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
      }
    }.start();
  }
  
  public static Map<String, String> getRandomCompanies() {
    Map<String, String> results;
    synchronized (COMPANIES) {
      results = getRandomResources(COMPANIES);
    }
    return results;
  }
  
  public static Map<String, String> getRandomFilms() {
    Map<String, String> results;
    synchronized (FILMS) {
      results = getRandomResources(FILMS);
    }
    return results;
  }
  
  public static Map<String, String> getRandomPersons() {
    Map<String, String> results;
    synchronized (PERSONS) {
      results = getRandomResources(PERSONS);
    }
    return results;
  }
  
  public static Map<String, String> getRandomResources() {
    Map<String, String> results;
    synchronized (ALL_RESOURCES) {
      results = getRandomResources(ALL_RESOURCES);
    }
    return results;
  }
  
  private static Map<String, String> getRandomResources(Map<String, String> res) {
    Map<String, String> randomResults = new LinkedHashMap<>();
    List<Map.Entry<String, String>> resourcesList = new ArrayList<>(res.entrySet());
    Random random = new Random();
    
    for (int i = 0; i < NUMBER_OF_SUGGESTIONS && resourcesList.size() > 0; i++) {
      int index = random.nextInt(resourcesList.size());
      Map.Entry<String,String> result = resourcesList.get(index);
      randomResults.put(result.getKey(), result.getValue());
      resourcesList.remove(index);
    }
    
    return randomResults;
  }
  
  public static Map<String, String> matchCompaniesByName(String name) {
    Map<String, String> results;
    synchronized (COMPANIES) {
      results = matchResourcesByName(name, COMPANIES);
    }
    return results;
  }
  
  public static Map<String, String> matchFilmsByName(String name) {
    Map<String, String> results;
    synchronized (FILMS) {
      results = matchResourcesByName(name, FILMS);
    }
    return results;
  }
  
  public static Map<String, String> matchPersonsByName(String name) {
    Map<String, String> results;
    synchronized (PERSONS) {
      results = matchResourcesByName(name, PERSONS);
    }
    return results;
  }
  
  public static Map<String, String> matchResourcesByName(String name) {
    Map<String, String> results;
    synchronized (ALL_RESOURCES) {
      results = matchResourcesByName(name, ALL_RESOURCES);
    }
    return results;
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
        if (numberOfLevenshteinResults >= MAX_LEVENSHTEIN_RESULTS) break;
        relevantResults.put(match.getKey(), res.get(match.getKey()));
        numberOfLevenshteinResults++;
      }
    }
    
    return relevantResults;
  }
  
  public static String getCategory(String uri) {
    String resourceCategory = null;
    synchronized (COMPANIES) {
      if (COMPANIES.containsValue(uri)) {
        resourceCategory = "company";
      }
    }
    synchronized (FILMS) {
      if (FILMS.containsValue(uri)) {
        resourceCategory = "film";
      }
    }
    synchronized (PERSONS) {
      if (PERSONS.containsValue(uri)) {
        resourceCategory = "person";
      }
    }
    return resourceCategory;
  }
  
  public static Map<String, String> loadResourceDataFromCache(String filename) 
    throws FileNotFoundException, IOException {
    Map<String, String> data = new LinkedHashMap<>();
    File file = new File(CACHE_PATH + "/" + filename);
    if (!file.exists()) {
      file.createNewFile();
    }
    Logger.getLogger(ResourceServices.class.getName()).log(Level.INFO, "Reading from " + file.getAbsolutePath());
    BufferedReader reader = new BufferedReader(new FileReader(file));
    reader.readLine();
    while (reader.ready()) {
      String name = reader.readLine();
      String uri = reader.readLine();
      data.put(name, uri);
    }
    reader.close();
    return data;
  }
  
  public static void saveResourceDataToCache(Map<String, String> data, String filename)
    throws FileNotFoundException, IOException {
    File file = new File(CACHE_PATH + "/" + filename);
    if (!file.exists()) {
      file.createNewFile();
    }
    Logger.getLogger(ResourceServices.class.getName()).log(Level.INFO, "Writing to " + file.getAbsolutePath());
    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
    for (Map.Entry<String, String> resource : data.entrySet()) {
      writer.newLine();
      writer.write(resource.getKey());
      writer.newLine();
      writer.write(resource.getValue());
    }
    writer.close();
  }
  
}
