package project.controller.services;

public class StringServices {
  
  /**
   * Strip all non alphanumeric symbols from a URI.
   * 
   * @param uri the URI
   * @return a valid alphanumeric identifier: the URI without symbols
   */
  public static String stripUriSymbols(String uri) {
    return uri.replaceAll("^[a-zA-Z0-9]+", "");
  }
  
  /**
   * Computes the Levensthein distance between two Strings.
   * 
   * @param query the user's query
   * @param resource a resource name
   * @return the distance between query and resource
   */
  public static int levenshtein(String query, String resource) {
    int[][] table = new int[query.length() + 1][resource.length() + 1];
    for (int i=0; i<table.length; i++) {
      table[i][0] = i;
    }
    for (int i=0; i<table[0].length; i++) {
      table[0][i] = i;
    }
    
    for(int i=1; i<=query.length(); i++) {
      for(int j=1; j<=resource.length(); j++) {
        int del = table[i-1][j] + 1;
        int ins = table[i][j-1] + 1;
        int rep = table[i-1][j-1] + (query.charAt(i-1) == resource.charAt(j-1) ? 0 : 1);
        table[i][j] = Math.min(Math.min(ins, rep), del);
      }
    }

    return table[query.length()][resource.length()];
  }
  
}
