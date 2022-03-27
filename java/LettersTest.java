/*
  # LettersTest.java
  
  Perform unit testing for `Letters.java`.
  
  Copyright 2022 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc., see LICENSE.
*/

import static org.junit.Assert.*;

import org.junit.Test;

public class LettersTest
{
  @Test
  public void normaliseLetters_isCorrect()
  {
    assertEquals(Letters.normaliseLetters("abc"), "ABC");
    assertEquals(Letters.normaliseLetters("HeRpdERP"), "HERPDERP");
    assertEquals(Letters.normaliseLetters(" whitespace\t"), "WHITESPACE");
  }
  
  @Test
  public void isValid_isCorrect()
  {
    assertTrue(Letters.isValid("A", "A"));
    assertTrue(Letters.isValid("A", "AA"));
    assertTrue(Letters.isValid("ABC", "AABBCCDD"));
    assertTrue(Letters.isValid("ABBCCCDDDD", "QWERTYDDDDCCCBBAA"));
    assertTrue(Letters.isValid("RADAR", "RADAR"));
    assertFalse(Letters.isValid("A", "X"));
    assertFalse(Letters.isValid("AA", "A"));
    assertFalse(Letters.isValid("RADAR", "DARAD"));
  }
}
