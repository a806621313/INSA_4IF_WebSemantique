package tests.unit;

import project.utils.StringUtils;

public class Tests {
  public static void main(String[] args) {
    System.out.println(StringUtils.getLevenshteinDistance("abcdefg", "abcdefg"));
    System.out.println(StringUtils.getLevenshteinDistance("abcdefg", "abcdfg"));
    System.out.println(StringUtils.getLevenshteinDistance("abcdefg", "bcdefg"));
    System.out.println(StringUtils.getLevenshteinDistance("abcdefg", "abxdeeefg"));
    System.out.println(StringUtils.getLevenshteinDistance("abcdefg", "abcdefghijklm"));
  }
}
