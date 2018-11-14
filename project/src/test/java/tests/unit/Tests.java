package tests.unit;

import project.utils.StringUtils;
import project.controller.services.ResourceServices;
import java.util.LinkedHashMap;
import java.util.Map;

public class Tests {
  public static void main(String[] args) {
    System.out.println(StringUtils.getLevenshteinDistance("abcdefg", "abcdefg"));
    System.out.println(StringUtils.getLevenshteinDistance("abcdefg", "abcdfg"));
    System.out.println(StringUtils.getLevenshteinDistance("abcdefg", "bcdefg"));
    System.out.println(StringUtils.getLevenshteinDistance("abcdefg", "abxdeeefg"));
    System.out.println(StringUtils.getLevenshteinDistance("abcdefg", "abcdefghijklm"));
    
    ResourceServices.init();
    Map<String,String> result = ResourceServices.matchCompaniesByName("disne para pict");
    
    
    for (Map.Entry<String, String> entry : result.entrySet()) {
        System.out.println(entry.getKey()+" : "+entry.getValue());
    }
  }
}
