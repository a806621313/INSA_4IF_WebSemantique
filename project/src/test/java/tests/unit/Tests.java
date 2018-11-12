package tests.unit;

import project.controller.services.StringServices;

public class Tests {
  public static void main(String[] args) {
    System.out.println(StringServices.levenshtein("abcdefg", "abcdefg"));
    System.out.println(StringServices.levenshtein("abcdefg", "abcdfg"));
    System.out.println(StringServices.levenshtein("abcdefg", "bcdefg"));
    System.out.println(StringServices.levenshtein("abcdefg", "abxdeeefg"));
    System.out.println(StringServices.levenshtein("abcdefg", "abcdefghijklm"));
  }
}
