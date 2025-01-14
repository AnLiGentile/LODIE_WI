package uk.ac.shef.dcs.oak.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Author: Ziqi Zhang (ziqi.zhang@sheffield.ac.uk)
 * Date: 30/04/13
 * Time: 18:30
 */
public class StringUtils {
    private static Pattern ORDINAL_NUMBER_PATTERN = Pattern.compile("(?<=[0-9])(?:st|nd|rd|th)");

    //convert a string to only alphenumeric and white chars. any other chars are removed
    public static String toAlphaNumericWhitechar(String value) {
        //keep only alpha-numeric characters
        return value.replaceAll("[^\\p{L}\\s\\d]", " ");

    }

    public static List<String> toBagOfWords(String text, boolean lowercase, boolean alphanumeric) {
        List<String> rs = new ArrayList<String>();
        if (lowercase)
            text=text.toLowerCase();
        if(alphanumeric)
            text = toAlphaNumericWhitechar(text);
        for (String vp : text.split("\\s+")) {
            if (vp.length() > 0)
                rs.add(vp);
        }
        return rs;
    }

    public static List<String> toAlphaNumericTokens(String value, boolean lowercase) {
        List<String> query_tokens = new ArrayList<String>();
        for (String t : toAlphaNumericWhitechar(value).split("\\s+")) {
            if(t.length()==0)
                continue;
            if (lowercase)
                query_tokens.add(t.toLowerCase());
            else
                query_tokens.add(t);
        }
        return query_tokens;
    }

    public static String findPossibleNameSpace(String urlString) {
        try {
            URL u = new URL(urlString);
            if (u.getFile().equals(""))
                return urlString;
        } catch (MalformedURLException m) {
        }

        int hash = urlString.lastIndexOf("#");
        int slash = urlString.lastIndexOf("/");
        int end = hash > slash ? hash : slash;

        if (end != -1)
            return urlString.substring(0, end);
        return urlString;

    }

    //permitted tokens must be numeric, or ordinal
    public static boolean isNumericArray(String[] alphanums) {
        int countNumerics = 0, firstNumeric = -1;
        for (int index = 0; index < alphanums.length; index++) {
            if (isNumericToken(alphanums[index]) || isOrdinalNumber(alphanums[index])) {
                countNumerics++;
                if (index == 0)
                    firstNumeric = 1;
            }

        }
        if (countNumerics >= (alphanums.length - countNumerics) && firstNumeric == 1) //if there are more numeric tokens than non-numeric, then yes
            return true;

        //otherwise, we have less or equal numeric tokens than other tokens, check special cases as:
        //if multi tokens, the first token must be a number, then it must be followed by 0-2 tokens,
        //each must not exceed a length of 10 characters. these wors are likely to be "units", e.g., "kilometers"
        if (firstNumeric == 1) {
            if (alphanums.length > 3)
                return false; //because it is likely to be an address. However this cannot perfect distinguish true/false positives
            for (int i = 1; i < alphanums.length; i++) {
                if (alphanums[i].length() > 10) //last token must be a word, possibly indicating unit
                    return false;
            }
            return true;
        }

        return false;
    }

    public static boolean isNumericToken(String alphanum) {
        int legal_letters = 0, illegal_letters = 0;
        for (int i = 0; i < alphanum.length(); i++) {  //allowing scientific numbers
            if (!Character.isDigit(alphanum.charAt(i))) {
                if (alphanum.charAt(i) == 'e' || alphanum.charAt(i) == 'E' || alphanum.charAt(i) == 'x' || alphanum.charAt(i) == 'X')
                    legal_letters++;
                else
                    illegal_letters++;
            }
        }

        if (illegal_letters == 0 && legal_letters < 2) //conforms to a number or scientific writing
            return true;

        //in this case there are letters in the token.
        String alphabeticRemoved = alphanum.replaceAll("[^\\d]", " ").trim();
        if (alphabeticRemoved.indexOf(" ") == -1  //it means either the alphabetics are suffix or prefix and are trimmed, or there are no alhpabetics
                && alphabeticRemoved.length() > (alphanum.length() - alphabeticRemoved.length())) //it means token must have more digits than letters
            return true;

        return false;
    }


    public static boolean isOrdinalNumber(String singleToken) {
        return ORDINAL_NUMBER_PATTERN.matcher(singleToken).find();
    }

    public static boolean isCapitalized(String string) {
        return (Character.isUpperCase(string.charAt(0)));
    }

    public static boolean isPath(String value) {
        if(value.startsWith("http://")||value.startsWith("www.")||
                value.startsWith("ftp://")||value.startsWith("https://"))
            return true;
        value = value.replaceAll("\\\\","/");
        int slashes = value.split("/").length;
        if(slashes-1>2 && value.indexOf(" ")==-1)
            return true;
        return false;
    }
}
