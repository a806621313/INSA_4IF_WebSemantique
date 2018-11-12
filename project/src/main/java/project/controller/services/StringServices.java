package project.controller.services;

public class StringServices {
  
  /**
   * Strip all non alphanumeric symbols from a URI.
   * 
   * @param uri the URI
   * @return a valid alphanumeric identifier: the URI without symbols
   */
  public String stripUriSymbols(String uri) {
    return uri.replaceAll("^[a-zA-Z0-9]+", "");
  }
  
  /**
   * Computes the Levensthein distance between two Strings
   * 
   * @param query the user's query
   * @param resource a resource name
   * @return the distance between query and resource
   */
  public Integer levenshtein(String query, String resource) {
    // TODO
    return 0;
  }
  
}
