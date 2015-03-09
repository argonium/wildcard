import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *  This class can be used to check for wildcard matches.
 *
 *  Notes: There are two ways to use this class.  The approach
 *  you use depends on the input data.  If the pattern changes
 *  frequently, use the following code:
 *  
 *    boolean match = Wildcard.matchFound("a*b", "abc");
 *  
 *  If you need to check a pattern against many strings, use
 *  the optimized version: parse the pattern once, and then call
 *  another method to check if the pattern matches each string.
 *  The advantage over the above call (matchFound) is that the
 *  same pattern is not parsed repeatedly.  Here's an example of
 *  the optimized call:
 *  
 *    List pattern = Wildcard.parsePattern("*a*b*");
 *    boolean match = Wildcard.matchPattern(pattern, "abc");
 *    
 * @author Mike Wallace, 03 June 2004
 */
public final class Wildcard
{
  /**
   * Set up debug mode.
   */
  private static final boolean DEBUG_MODE = false;
  
  /**
   * Default constructor.  Private so the class cannot be instantiated.
   */
  private Wildcard()
  {
    super();
  }
  
  
  /**
   * Returns whether target fits the pattern in pat.
   * This is the unoptimized version, since pat is parsed
   * into a list every time the method is called.  A
   * sample call is:
   *
   *    boolean rc = matchFound("a*b", "ab", false);
   *
   * @param pat The input string, with wildcards
   * @param target The string to test pat against
   * @param bIgnoreCase Whether to ignore case.
   * @return Whether target matches pat
   */
  public static boolean matchFound(final String pat,
                                   final String target,
                                   final boolean bIgnoreCase)
  {
    if (bIgnoreCase)
    {
      // Check the inputs.  If no pattern, assume it's a match.
      if ((pat == null) || (pat.length() == 0))
      {
        return true;
      }
      else
      {
        // If we reach here, pat is non-null and non-empty.  If target
        // is null or empty, consider it a non-match.
        if ((target == null) || (target.length() == 0))
        {
          return false;
        }
        else
        {
          // Ignore case by converting both strings to uppercase
          return matchFound(pat.toUpperCase(), target.toUpperCase());
        }
      }
    }
    else
    {
      return matchFound(pat, target);
    }
  }
  
  
  /**
   * Returns whether target fits the pattern in pat.
   * This is the unoptimized version, since pat is parsed
   * into a list every time the method is called.  A
   * sample call is:
   *
   *    boolean rc = matchFound("a*b", "ab");
   *
   * Assumes the match should be case-sensitive.
   *
   * @param pat The input string, with wildcards
   * @param target The string to test pat against
   * @return Whether target matches pat
   */
  public static boolean matchFound(final String pat,
                                   final String target)
  {
    // Store whether a match was found.  The initial value
    // doesn't matter since we handle all cases below.
    boolean match = false;
    
    // Check the inputs.  If no pattern, assume it's a match.
    if ((pat == null) || (pat.length() == 0))
    {
      match = true;
    }
    else
    {
      // If we reach here, pat is non-null and non-empty.  If target
      // is null or empty, consider it a non-match.
      if ((target == null) || (target.length() == 0))
      {
        match = false;
      }
      else
      {
        // Parse the input into pieces between *
        List list = parsePattern(pat);
        
        // Check for just a *, or no * at all
        if (list.size() == 1)
        {
          // Just one element in the list, meaning pat was either '*' or
          // had no wildcards.
          if (list.get(0) == null)
          {
            // Just a * in the input, so it matches
            match = true;
          }
          else
          {
            // No *, so return whether they're equal
            match = equalsWild(target, pat);
          }
        }
        else
        {
          // OK, call the pattern finder
          match = findMatch(list, 0, target, 0);
        }
        
        // Empty the list
        list.clear();
        list = null;
      }
    }
    
    // Show a message if in debug mode
    if (DEBUG_MODE)
    {
      System.out.println("The match on " + pat + " for " + target +
                         " is " + match);
    }
    
    // Return whether a match was found
    return match;
  }
  
  
  /**
   * Returns whether target fits the pattern in pat.
   *
   * @param pattern The pattern to check
   * @param target The target string to compare against the pattern
   * @param bIgnoreCase Whether to ignore the case of target
   * @return whether target fits the pattern in pat
   */
  public static boolean matchPattern(final List pattern,
                                     final String target,
                                     final boolean bIgnoreCase)
  {
    if (bIgnoreCase)
    {
      // Check the inputs.  If no pattern, assume it's a match.
      if ((pattern == null) || (pattern.size() == 0))
      {
        return true;
      }
      else
      {
        // If we reach here, pattern is non-null and non-empty.  If target
        // is null or empty, consider it a non-match.
        if ((target == null) || (target.length() == 0))
        {
          return false;
        }
        else
        {
          return matchPattern(pattern, target.toUpperCase());
        }
      }
    }
    else
    {
      return matchPattern(pattern, target);
    }
  }
  
  
  /**
   * Returns whether target fits the pattern in pat.  This
   * method assumes a match should be case-sensitive.
   *
   * @param pattern The pattern to check
   * @param target The target string to compare against the pattern
   * @return whether target fits the pattern in pat
   */
  public static boolean matchPattern(final List pattern,
                                     final String target)
  {
    // Store whether a match was found.  The initial value
    // doesn't matter since we handle all cases below.
    boolean match = false;
    
    // Check the inputs.  If no pattern, assume it's a match.
    if ((pattern == null) || (pattern.size() == 0))
    {
      match = true;
    }
    else
    {
      // If we reach here, pattern is non-null and non-empty.  If target
      // is null or empty, consider it a non-match.
      if ((target == null) || (target.length() == 0))
      {
        match = false;
      }
      else
      {
        // Check for just a *, or no * at all
        if (pattern.size() == 1)
        {
          // Just one element in the list, meaning pat was either '*' or had
          // no wildcards.
          final String pat = (String) (pattern.get(0));
          if (null == pat)
          {
            // Just a * in the input, so it matches
            match = true;
          }
          else
          {
            // No *, so return whether they're equal
            match = equalsWild(target, pat);
          }
        }
        else
        {
          // OK, call the pattern finder
          match = findMatch(pattern, 0, target, 0);
        }
      }
    }
    
    // Return whether a match was found
    return match;
  }
  
  
  /**
   * This is a recursive method that does the work
   * of checking for a match.  In the unoptimized
   * version, this should only be called by the method
   * matchFound.
   *
   * @param list The pattern to check
   * @param nCurrPart The current part of the list we're checking
   * @param target The string to compare against
   * @param nCurrIndex The current index of target
   * @return whether the pattern matches the target string
   */
  private static boolean findMatch(final List list,
                                   final int nCurrPart,
                                   final String target,
                                   final int nCurrIndex)
  {
    // Default return value
    boolean found = false;
    
    // Check if we're looking past the end of the list
    if (nCurrPart >= list.size())
    {
      // We hit the end, so return.  I don't think this code
      // is ever reached, but it's here just in case.
      return true;
    }
    
    // Save the current string in its own variable
    String part = (String) (list.get(nCurrPart));
    
    // Check if we're trying to find a match past the end of target
    if (nCurrIndex >= target.length())
    {
      // If 'part' is * (null), return true; else, no match.
      return (part == null);
    }
    else
    {
      // Check if we're on a *
      if (part == null)
      {
        // We hit a *, so we're either at the start of the string, or at the end
        if (nCurrPart == 0)
        {
          // It starts with *, so start looking with the next element of list
          return (findMatch(list, 1, target, 0)); 
        }
        else
        {
          // We're at the end of the list, so assume it matches
          // (pattern ends with '*')
          return true;
        }
      }
      else
      {
        // See if we're looking at the last field
        if (nCurrPart == (list.size() - 1))
        {
          // We are, so return whether the target string ends with this string
          return (endsWithWild(target, part));
        }
        
        // Save the length
        final int nLen = part.length();
        
        // Find the next occurrence of s[nCurrPart], starting after
        // the current index
        int foundIndex = indexOfWild(target, part, nCurrIndex);
        
        // Keep looking until the subsequent s partitions are all found
        while ((foundIndex >= 0) && (!found))
        {
          // If there was no wildcard before 'part' (nCurrPart = 0), and
          // the string was found after the point we started looking, then
          // return false.
          if ((foundIndex > nCurrIndex) && (nCurrPart == 0))
          {
            return false;
          }
          
          // Find the next occurrence of the next s elements, starting after
          // the end of the current match
          found = findMatch(list, (nCurrPart + 1), target, (foundIndex + nLen));
          
          // If no match found, find the next occurrence of part in target
          if (!found)
          {
            // Store where it was found (if at all)
            foundIndex = indexOfWild(target, part, ++foundIndex);
          }
        }
      }
    }
    
    // Return whether a match was found (in target) for the current element
    // of list and all subsequent elements of list.
    return found;
  }
  
  
  /**
   * This method determines whether two strings are equal.
   * The 'part' argument is allowed to have a '?', which
   * is interpreted to mean any single character.
   *
   * @param target The target string to compare with
   * @param part The string with zero or more '?' characters
   * @return whether the two strings match
   */
  private static boolean equalsWild(final String target,
                                    final String part)
  {
    boolean bEquals = true;
    
    // Check the input strings
    if ((target == null) || (part == null))
    {
      return false;
    }
    
    // Check if part has a wildcard
    if (part.indexOf("?") < 0)
    {
      // No wildcard, so call the String::indexOf() function
      return target.equals(part);
    }
    
    // part has a wildcard
    // Check the length
    if (target.length() != part.length())
    {
      // Lengths are different
      return false;
    }
    
    // The lengths are the same, so check each character
    final int nLen = target.length();
    for (int nIndex = 0; (nIndex < nLen) && (bEquals); ++nIndex)
    {
      // Save the current character in each string
      char targetChar = target.charAt(nIndex);
      char partChar = part.charAt(nIndex);
      
      // Check for a mismatch
      if ((partChar != '?') && (partChar != targetChar))
      {
        // The characters don't match, and the current part character
        // is not a question mark, so the strings don't match
        bEquals = false;
      }
    }
    
    // Return whether the strings are equal
    return bEquals;
  }
  
  
  /**
   * This method determines whether 'target' ends with 'part'.
   * The 'part' argument is allowed to have a '?', which is
   * interpreted to mean any single character.
   *
   * @param target The target string to compare with
   * @param part The string with zero or more '?' characters
   * @return whether target ends with part
   */
  private static boolean endsWithWild(final String target,
                                      final String part)
  {
    // Check the input strings
    if ((target == null) || (part == null))
    {
      return false;
    }
    
    // Check if part has a wildcard
    if (part.indexOf("?") < 0)
    {
      // No wildcard, so call the String::indexOf() function
      return target.endsWith(part);
    }
    
    // part has a wildcard
    // Check the length
    if (target.length() < part.length())
    {
      // The string isn't long enough
      return false;
    }
    
    // Get the end of the target string
    String targetEnd = target.substring(target.length() - part.length());
    
    // Return whether targetEnd equals part
    return equalsWild(targetEnd, part);
  }
  
  
  /**
   * This method checks for the existence of the string 'part'
   * within the string 'target', starting at target[fromIndex].
   * The 'part' argument is allowed to have a '?', which is
   * interpreted to mean any single character.
   *
   * @param target The target string to compare with
   * @param part The string with zero or more '?' characters
   * @param fromIndex the starting index of target
   * @return the index at which part exists within target
   */
  private static int indexOfWild(final String target,
                                 final String part,
                                 final int fromIndex)
  {
    // Declare needed variables
    boolean bFound = false;
    int nFoundIndex = fromIndex;
    
    // Check the input strings
    if ((target == null) || (part == null) || (fromIndex < 0))
    {
      return -1;
    }
    
    // Check if part has a wildcard
    if (part.indexOf("?") < 0)
    {
      // No wildcard, so call the String::indexOf() function
      return target.indexOf(part, fromIndex);
    }
    
    // part has a wildcard
    // Check the starting index
    final int nTargetLen = target.length();
    final int nPartLen = part.length();
    if ((nPartLen + fromIndex) > nTargetLen)
    {
      // The starting index is too high
      return -1;
    }
    
    // Check for the existence of part as a substring in target
    for (int nIndex = fromIndex; (!bFound) &&
         ((nPartLen + nIndex) <= nTargetLen); ++nIndex)
    {
      String targetSub = target.substring(nIndex, (nIndex + nPartLen));
      if (equalsWild(targetSub, part))
      {
        nFoundIndex = nIndex;
        bFound = true;
      }
    }
    
    // Check if not found
    if (!bFound)
    {
      return -1;
    }
    
    // Return the index that part was found at in target
    return nFoundIndex;
  }
  
  
  /**
   * This method separates a String containing one or more
   * wildcards ('*') into a List of substrings.  Each
   * element of the List is the substring of 'pat' between
   * wildcards (e.g., "ab*cd" is two elements: "ab" and "cd").
   * Also, if pat starts with a wildcard, the first element
   * of the List will be null.  If pat ends with a wildcard,
   * the last element of the list will be null.
   *
   * @param pat the string to parse into a list
   * @return the list of substrings in pat
   */
  public static List parsePattern(final String pat)
  {
    return parsePattern(pat, false);
  }
  
  
  /**
   * This method separates a String containing one or more
   * wildcards ('*') into a List of substrings.  Each
   * element of the List is the substring of 'pat' between
   * wildcards (e.g., "ab*cd" is two elements: "ab" and "cd").
   * Also, if pat starts with a wildcard, the first element
   * of the list will be null.  If pat ends with a wildcard,
   * the last element of the list will be null.
   *
   * @param pat the string to parse into a list
   * @param bIgnoreCase whether to ignore the case
   * @return the list of substrings in pat
   */
  public static List parsePattern(final String pat, final boolean bIgnoreCase)
  {
    // Check the input.  If no pattern, return null.
    if ((pat == null) || (pat.length() == 0))
    {
      return null;
    }
    
    // Declare the list to hold the output
    List fields = new ArrayList();
    
    // If pat begins with *, add null to the list.
    if (pat.startsWith("*"))
    {
      fields.add(null);
    }
    
    // Parse the input string (skip over consecutive adjacent *)
    StringTokenizer tokenizer = new StringTokenizer(pat, "*");
    while (tokenizer.hasMoreTokens())
    {
      // Get the token.  If we're ignoring case, convert to uppercase.
      if (bIgnoreCase)
      {
        fields.add(tokenizer.nextToken().toUpperCase());
      }
      else
      {
        fields.add(tokenizer.nextToken());
      }
    }
    
    // If the string ends with a *, and there's at least one non-* before it,
    // add null to the end of the list.
    if ((fields.size() > 1) ||
        ((fields.size() == 1) && (fields.get(0) != null)))
    {
      if (pat.endsWith("*"))
      {
        fields.add(null);
      }
    }
    
    // Return the list of substrings
    return fields;
  }
  
  
  /**
   * Main method.  Used for testing.
   *
   * @param args arguments to the application
   */
  public static void main(final String[] args)
  {
    // Demonstrate calling matchFound for a variety of
    // combinations (unoptimized code)
    boolean rc = matchFound("a", "abcd");
    rc = matchFound("a*", "abcd");
    rc = matchFound("*a", "abcd");
    rc = matchFound("*a*", "abcd");
    rc = matchFound("a*b", "abcd");
    rc = matchFound("a*b*", "abcd");
    rc = matchFound("*a*b", "abcd");
    rc = matchFound("*a*b*", "abcd");
    rc = matchFound("*d", "abcd");
    rc = matchFound("a*d", "abcd");
    rc = matchFound("a*d*", "abcd");
    rc = matchFound("*d*", "abcd");
    rc = matchFound("*bc*", "abcd");
    rc = matchFound("*", "abcd");
    rc = matchFound("*******", "abcd");
    rc = matchFound("a*", "diva");
    
    // Demonstrate calling matchPattern (optimized code)
    String pat = "*a*b*";
    List pattern = parsePattern(pat);
    System.out.println("Matching " + pat + " on abcd is " +
                       matchPattern(pattern, "abcd"));
    System.out.println("Matching " + pat + " on abbcd is " +
                       matchPattern(pattern, "abbcd"));
  }
}
