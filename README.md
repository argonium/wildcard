# Wildcard
The Wildcard class allows a developer to compare two strings for equality, with support for the '\*' and '%' characters treated the same way that SQL uses the characters. A '\*' will match zero or more characters, and a '%' will match any one character. This class is useful in those cases where you want to compare two strings that do not reside in a database.

There are two ways to use this class. The first is useful when you only expect to compare a string with wildcard characters ('\*' and '%') to another string once. Let sWild be a wildcard string, and sTame be a non-wildcard string. The method to use is:

```
  boolean match = Wildcard.matchFound(String sWild, String sTame);
```

The return value indicates if the two strings match. By default, this method is case-sensitive. To make the comparison case-insensitive, use this method:

```
  boolean match = Wildcard.matchFound(String sWild, String sTame, boolean ignoreCase);
```

Pass true for the third argument to make the comparison case-insensitive.

If you need to check a pattern against many strings, use the optimized version: parse the pattern once, and then call another method to check if the pattern matches each string. The advantage over the above call (matchFound) is that the same pattern is not parsed repeatedly. The methods to use are:

```
  List pattern = Wildcard.parsePattern(sWild);
  boolean match = Wildcard.matchPattern(pattern, sTame);
```

This comparison is case-sensitive. To make the comparison case-insensitive, instead use these methods:

```
  List pattern = Wildcard.parsePattern(String sWild, boolean ignoreCase);
  boolean match = Wildcard.matchPattern(List pattern, String sTame, boolean ignoreCase);
```

Below are some examples of using this class:

```
  boolean rc = Wildcard.matchFound("po*l", "portal"); /* true */
  boolean rc = Wildcard.matchFound("po?l", "portal"); /* false */
  boolean rc = Wildcard.matchFound("p?li*", "pelican"); /* true */
```

The source code is released under the MIT license.
