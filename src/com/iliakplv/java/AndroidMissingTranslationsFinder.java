package com.iliakplv.java;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Missing translations finder
 *
 * Usage in terminal from project root directory:
 *
 * javac MissingStringsFinder.java && java MissingStringsFinder
 */
public final class AndroidMissingTranslationsFinder {

    private static final String RES_DIR = "./app/src/main/res/";

    private static final String VALUES_DIR = "values";
    private static final String STRINGS_FILE = "strings.xml";

    private static final String REGEX_SUFFIX_LANGUAGE = "[a-z]{2}"; // "da", "de", "fr", etc.
    private static final String REGEX_SUFFIX_LANGUAGE_COUNTRY = "[a-z]{2}-r[A-Z]{2}"; // "en-rGB", "pt-rBR", etc.


    public static void main(String[] args) throws Exception {

        // app locales
        final List<String> locales = new LinkedList<>();

        // set of strings in default locale
        final Set<String> defaultStrings = getTranslations(null);

        // map of missing string sets for each app locale (locale is a key)
        final Map<String, Set<String>> missingStrings = new HashMap<>();

        // set of strings not existing in default locale
        final Set<String> defaultMissingStrings = new HashSet<>();

        // set of string existing only in default locale but missing in all others
        final Set<String> commonMissingStrings = new HashSet<>();


        // find locales
        final File resDirectory = new File(RES_DIR);
        final String localisedValuesPrefix = VALUES_DIR + "-";
        for (File file : resDirectory.listFiles()) {
            if (file.getName().startsWith(localisedValuesPrefix)) {
                final String suffix =
                        file.getName().substring(localisedValuesPrefix.length(), file.getName().length());
                if (suffix.matches(REGEX_SUFFIX_LANGUAGE) || suffix.matches(REGEX_SUFFIX_LANGUAGE_COUNTRY)) {
                    locales.add(suffix);
                }
            }
        }
        System.out.println(String.format("Locales found: %s + default", locales.size()));
        for (int i = 0; i < locales.size(); i++) {
            System.out.print(locales.get(i));
            if (i < locales.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("\n");

        // find missing strings
        for (String locale : locales) {
            final Set<String> localizedStringsSet = getTranslations(locale);
            final Set<String> missingStringsSet = new HashSet<>();

            // missing in locale
            missingStringsSet.addAll(defaultStrings
                    .stream()
                    .filter(stringName -> !localizedStringsSet.contains(stringName))
                    .collect(Collectors.toSet()));

            missingStrings.put(locale, missingStringsSet);

            // missing in default
            defaultMissingStrings.addAll(localizedStringsSet
                    .stream()
                    .filter(localisedString -> !defaultStrings.contains(localisedString))
                    .collect(Collectors.toSet()));
        }

        // find common missing strings
        for (String locale : locales) {
            final Set<String> localeMissingStrings = missingStrings.get(locale);
            commonMissingStrings.addAll(localeMissingStrings
                    .stream()
                    .filter(string -> isStringMissingInAllLocales(missingStrings, string))
                    .collect(Collectors.toSet()));
        }

        // remove common missing string from locale specific sets
        for (String locale : locales) {
            final Set<String> localeMissingStrings = missingStrings.get(locale);
            commonMissingStrings.forEach(localeMissingStrings::remove);
        }


        // Print results
        int totalMissingStrings = 0;

        if (defaultMissingStrings.size() > 0) {
            System.out.println(String.format("Strings missing in default locale (exist in some other) (%s strings)", defaultMissingStrings.size()));
            for (String defaultMissingString : defaultMissingStrings) {
                System.out.println("\t" + defaultMissingString);
            }
            System.out.println();
            totalMissingStrings += defaultMissingStrings.size();
        }

        if (commonMissingStrings.size() > 0) {
            System.out.println(String.format("Default locale only strings (missing in ALL others) (%s strings)", commonMissingStrings.size()));
            for (String commonMissingString : commonMissingStrings) {
                System.out.println("\t" + commonMissingString);
            }
            System.out.println();
            totalMissingStrings += commonMissingStrings.size();
        }

        for (String locale : locales) {
            final Set<String> localeMissingStrings = missingStrings.get(locale);
            if (localeMissingStrings.size() > 0) {
                System.out.println(String.format("[%s] locale missing strings (exist in default) (%s strings)", locale, localeMissingStrings.size()));
                for (String string : localeMissingStrings) {
                    System.out.println("\t" + string);
                }
                System.out.println();
                totalMissingStrings += localeMissingStrings.size();
            }
        }

        System.out.println(totalMissingStrings > 0 ?
                String.format("Total missing strings: %s", totalMissingStrings) :
                "Yay! All strings are existing in all locales!");

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