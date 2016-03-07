package com.iliakplv.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


/**
 * Dummy Android missing translations finder.
 * Displays list of string names existing in default locale but missing in any other supported locale.
 *
 * Do not forget to change constants to your actual values before running!
 */
public final class AndroidMissingTranslationsFinder {

    private static final String RES_DIR = "/Users/USER/workspace/PROJECT/app/src/main/res/";
    private static final String VALUES_DIR = "values";
    private static final String STRINGS_FILE = "strings.xml";

    private static final String[] LOCALES = {
            "da", "de", "el", "en-rGB", "es", "fi", "fr", "it", "nl", "no", "pl", "pt", "ru", "sv", "tr"};


    public static void main(String[] args) {

        final Set<String> defaultStrings = getTranslations(null);

        for (String locale : LOCALES) {
            System.out.println("Locale [" + locale + "] missing strings:");
            final Set<String> localizedStrings = getTranslations(locale);
            defaultStrings.stream().filter(string -> !localizedStrings.contains(string)).forEach(System.out::println);
            System.out.println();
        }

    }


    private static Set<String> getTranslations(String locale) {
        final File stringsFile = isEmpty(locale) ?
                new File(RES_DIR + VALUES_DIR + "/" + STRINGS_FILE) :
                new File(RES_DIR + VALUES_DIR + "-" + locale + "/" + STRINGS_FILE);

        final HashSet<String> stringsSet = new HashSet<>();

        try {
            try (BufferedReader br = new BufferedReader(new FileReader(stringsFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    final String namePrefix = "name=\"";
                    final String nameSuffix = "\">";
                    final int nameIndex = line.indexOf(namePrefix);
                    final int nameEndIndex = line.indexOf(nameSuffix);

                    if (nameIndex > 0 && nameEndIndex > 0) {
                        final int nameStartIndex = nameIndex + namePrefix.length();
                        stringsSet.add(line.substring(nameStartIndex, nameEndIndex));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringsSet;
    }


    private static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

}
