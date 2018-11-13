package project.utils;

public class StringUtils {
  
  /**
   * Removes all non-alphanumeric characters from a String.
   * 
   * @param str the String to clean
   * @return a valid alphanumeric identifier from the String str
   */
  public static String getAlphanumericIdentifier(String str) {
    return str.replaceAll("^[a-zA-Z0-9]+", "");
  }
  
  /**
   * Computes the Levenshtein distance between two Strings.
   * 
   * @param s1 the first String
   * @param s2 the second String
   * @return the distance between s1 and s2
   */
  public static int getLevenshteinDistance(String s1, String s2) {
    // edge case: empty string(s)
    if (s1.length() == 0 || s2.length() == 0) {
      return Math.max(s1.length(), s2.length());
    }
    
    // dp table initialization
    int[][] table = new int[s1.length() + 1][s2.length() + 1];
    for (int i = 0; i < table.length; i++) {
      table[i][0] = i;
    }
    for (int i = 0; i < table[0].length; i++) {
      table[0][i] = i;
    }
    
    // dp algorithm
    for(int i = 1; i <= s1.length(); i++) {
      for(int j = 1; j <= s2.length(); j++) {
        int del = table[i-1][j] + 1;
        int ins = table[i][j-1] + 1;
        int rep = table[i-1][j-1] + (s1.charAt(i-1) == s2.charAt(j-1) ? 0 : 1);
        table[i][j] = Math.min(Math.min(ins, rep), del);
      }
    }
    
    return table[s1.length()][s2.length()];
  }
  
}
