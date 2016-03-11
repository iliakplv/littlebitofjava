package com.iliakplv.java;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Dummy Android missing translations finder.
 *
 * Do not forget to change RES_DIR constant to your actual value before running!
 */
public final class AndroidMissingTranslationsFinder {

    private static final String RES_DIR = "/Users/USER/workspace/PROJECT/app/src/main/res/";

    private static final String VALUES_DIR = "values";
    private static final String STRINGS_FILE = "strings.xml";

    private static final String[] LOCALES = {
            "da", "de", "el", "en-rGB", "es", "fi", "fr", "it", "nl", "no", "pl", "pt", "ru", "sv", "tr"
    };


    public static void main(String[] args) throws Exception {

        // set of strings in default locale
        final Set<String> defaultStringsSet = getTranslations(null);

        // map of missing string sets for each app locale (locale is a key)
        final Map<String, Set<String>> missingStrings = new HashMap<>();

        // set of string existing only in default locale but missing in all others
        final Set<String> commonMissingStrings = new HashSet<>();


        // find missing strings
        for (String locale : LOCALES) {
            final Set<String> localizedStringsSet = getTranslations(locale);
            final Set<String> missingStringsSet = new HashSet<>();

            for (String stringName : defaultStringsSet) {
                if (!localizedStringsSet.contains(stringName)) {
                    missingStringsSet.add(stringName);
                }
            }

            missingStrings.put(locale, missingStringsSet);
        }

        // find common missing strings
        for (String locale : LOCALES) {
            final Set<String> localeMissingStrings = missingStrings.get(locale);
            for (String string : localeMissingStrings) {
                if (isStringMissingInAllLocales(missingStrings, string)) {
                    commonMissingStrings.add(string);
                }
            }
        }

        // remove common missing string from locale specific sets
        for (String locale : LOCALES) {
            final Set<String> localeMissingStrings = missingStrings.get(locale);
            for (String commonMissingString : commonMissingStrings) {
                localeMissingStrings.remove(commonMissingString);
            }
        }

        // Print results
        System.out.println("\nStrings existing in default locale only (missing in all others):\n");
        for (String commonMissingString : commonMissingStrings) {
            System.out.println(commonMissingString);
        }
        System.out.println("\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n");

        for (String locale : LOCALES) {
            System.out.println(String.format("Strings missing in locale [%s]:\n", locale));
            final Set<String> localeMissingStrings = missingStrings.get(locale);
            for (String string : localeMissingStrings) {
                System.out.println(string);
            }
            System.out.println("\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n");
        }

    }

    private static boolean isStringMissingInAllLocales(Map<String, Set<String>> localeMissingStringsMap, String stringToSearch) {
        for (String locale : localeMissingStringsMap.keySet()) {
            final Set<String> localeMissingStrings = localeMissingStringsMap.get(locale);
            if (!localeMissingStrings.contains(stringToSearch)) {
                return false;
            }
        }
        return true;
    }


    private static Set<String> getTranslations(String locale) throws Exception {
        final File stringsFile = isEmpty(locale) ?
                new File(RES_DIR + VALUES_DIR + "/" + STRINGS_FILE) :
                new File(RES_DIR + VALUES_DIR + "-" + locale + "/" + STRINGS_FILE);


        final NodeList stringList = getXmlNodeList(stringsFile, "string");
        final HashSet<String> stringSet = new HashSet<>();

        for (int i = 0; i < stringList.getLength(); i++) {
            String stringName = stringList.item(i).getAttributes().getNamedItem("name").getNodeValue();
            stringSet.add(stringName);
        }

        return stringSet;
    }

    private static NodeList getXmlNodeList(File xmlFile, String nodeTagName) throws Exception {
        DocumentBuilderFactory documentBuilderFactory
                = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(xmlFile);
        document.getDocumentElement().normalize();
        return document.getElementsByTagName(nodeTagName);
    }

    private static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

}
