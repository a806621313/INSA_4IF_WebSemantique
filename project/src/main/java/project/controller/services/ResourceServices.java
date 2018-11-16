package project.controller.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
  private final static Map<String, String> COMPANIES = new LinkedHashMap<>();
  /** A local database of film names and URIs (key = name ; value = URI) */
  private final static Map<String, String> FILMS = new LinkedHashMap<>();
  /** A local database of person names and URIs (key = name ; value = URI) */
  private final static Map<String, String> PERSONS = new LinkedHashMap<>();
  /** A local database of all the resource names and URIs (key = name ; value = URI) */
  private final static Map<String, String> ALL_RESOURCES = new LinkedHashMap<>();
  
  /** Maximum distance allowed between query words and resource names */
  private final static int MAX_LEVENSHTEIN_DISTANCE = 3;
  /** Maximum number of results when using the standard matching algorithm */
  private final static int MAX_RESULTS = 100;
  /** Number of search suggestions per resource category sent to the user */
  private final static int NUMBER_OF_SUGGESTIONS = 3;
  
  /** Cache path */
  private final static String CACHE_PATH = "C:/Users/alexi/Desktop"; // Machine-specific path
  /** Companies cache filename */
  private final static String COMPANIES_CACHE_FILENAME = "companies-cache";
  /** Films cache filename */
  private final static String FILMS_CACHE_FILENAME = "films-cache";
  /** Persons cache filename */
  private final static String PERSONS_CACHE_FILENAME = "persons-cache";
  
  /**
   * Initializes the application local resource databases.
   */
  public static void init() {
    try {
      // Load the resources from the cache
      Logger.getLogger(ResourceServices.class.getName()).log(Level.SEVERE, "Loading resources from the cache...");
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
    String[] queryWords = name.toUpperCase().trim().split("\\W+");
    
    // Edge case
    if (queryWords.length == 0) {
      return relevantResults;
    }
    
    // Optimization: sort query words by length
    Arrays.sort(queryWords, new Comparator<String>() {
      @Override
      public int compare(String s1, String s2) {
        return s1.length() - s2.length();
      }
    });
    
    // Temporary structure to save the distance between the query and the resources
    Map<String, Integer> resourceMatches = new LinkedHashMap<>();
    for (Map.Entry<String, String> entry : res.entrySet()) {
      resourceMatches.put(entry.getKey(), 0);
    }
      
    // Match resource names with query words
    for (Map.Entry<String, Integer> match : resourceMatches.entrySet()) {
      for (String queryWord : queryWords) {
        int queryWordScore = 0;
        int minDistance = MAX_LEVENSHTEIN_DISTANCE + queryWord.length();
        boolean useLevenshtein = true;
        for (String resourceWord : match.getKey().split("\\W+")) {
          resourceWord = resourceWord.toUpperCase();
          if (resourceWord.equals(queryWord)) {
            queryWordScore += (queryWord.length() * queryWord.length() * queryWord.length());
            useLevenshtein = false; // Perfect match, no need to compute the Levenshtein distance
          } else if (resourceWord.contains(queryWord)) {
            queryWordScore += (queryWord.length() * queryWord.length());
            minDistance = resourceWord.length() - queryWord.length();
          } else if (useLevenshtein) {
            minDistance = Math.min(minDistance, StringUtils.getLevenshteinDistance(queryWord, resourceWord));
          }
        }
        if (useLevenshtein && minDistance <= MAX_LEVENSHTEIN_DISTANCE && queryWord.length() >= 3*minDistance) {
          queryWordScore += ((queryWord.length() - minDistance) * (queryWord.length() - minDistance));
        } else if (useLevenshtein) {
          match.setValue(0);
          break;
        }
        match.setValue(match.getValue() + queryWordScore);
      }
      // Tie-breaking: shortest results first
      match.setValue(match.getValue() - match.getKey().split("\\W+").length);
    }
    
    // Keep only resources that matched something
    List<Map.Entry<String, Integer>> sortedMatches = new ArrayList<>();
    for (Map.Entry<String, Integer> match : resourceMatches.entrySet()) {
      if (match.getValue() > 0) {
        sortedMatches.add(match);
      }
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
      if(numberOfResults >= MAX_RESULTS) break;
      relevantResults.put(match.getKey(), res.get(match.getKey()));
      numberOfResults++;
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
