package edu.utsa.cs.sefm.docConverter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rocky on 12/18/2015.
 */
public class Driver {
    public static final String MATCH_TYPE = "d"; // 'd' => description, 'b' => description and method (both)
    private static final String DOCS_PATH = "C:\\Users\\Rocky\\AppData\\Local\\Android\\sdk\\docs\\reference\\android";
    private static final String ANNOTATED_METHODS_PATH = "annotations.csv";
    private static final int MAX_PER_LINE = 20;
    private static final String OUTPUT_NAME = "remappedDuplicates.csv";
    public static Filter filter;
    public static Map<String, ArrayList<String>> apis;
    public static List<String> descriptions;
    public static List<String> csvRows;
    public static int csvRowsCount;
    public static String csvRow;
    public static List<ExistingMap> maps;
    private static List<String> files;

    public static void main(String args[]) {
        try {
//            filter = new Filter(FILTER_PATH); // instantiating a filter will filter the results through it
            CsvWriter csvWriter = new CsvWriter(OUTPUT_NAME);
            maps = ExistingMap.readMaps(ANNOTATED_METHODS_PATH);
            files = new ArrayList<>();
            walk(DOCS_PATH);
            int count = 1;
            for (ExistingMap map : maps) {
                boolean found = false;
                System.out.println("("+ count++ + "/" + maps.size() + ") Retrieving description: " + map.method + "...");
                // get description
                for (String file : files) {
                    if(found)
                        break;
                    ClassDocumentation doc;// = new ClassDocumentation(file);
                    if ((doc = ClassDocumentation.getDoc(file, (HashMap) apis, true)) != null) {
                        // look for the method
                        for (Map.Entry<String, String> method : doc.publicMethods.entrySet()) {
                            String key = method.getKey().replace("\"", "'").replace("&lt;", "<").replace("&gt;", ">").replace("&nbsp;", " ").replace("&amp;", "&");
                            key = key.substring(0, key.indexOf(")") + 1) + "";
                            if ((doc.name + "." + key).replaceAll("< ", "<").equals(map.method)) {
                                map.description = method.getValue();
                                System.out.println("Description found: " + map.description + "\n");
                                found = true;
                                break;
                            }
                        }
                    }
                }

                // go back and find matches
                System.out.println("Searching for duplicates...");
                for (String file : files) {
                    ClassDocumentation doc;// = new ClassDocumentation(file);
                    if ((doc = ClassDocumentation.getDoc(file, (HashMap) apis, true)) != null) {
                        // look for the method
                        for (Map.Entry<String, String> method : doc.publicMethods.entrySet()) {
                            String key = method.getKey().replace("\"", "'").replace("&lt;", "<").replace("&gt;", ">").replace("&nbsp;", " ").replace("&amp;", "&");
                            key = key.substring(0, key.indexOf(")") + 1) + "";
//                            System.err.println("Matching:\n" + doc.name + "." + key + "\n" + map.method + '\n' + method.getValue() + "\n" + map.description);
                            if (key.replaceAll("< ", "<").equals(map.getSimpleMethod()) && method.getValue().equals(map.description)) {
                                System.out.println("Match found: " + doc.name + "." + key);
                                // write it
                                ArrayList<String> row = new ArrayList<>();
//                                row.add(map.method.replace("\"", "'").replace("&lt;", "<").replace("&gt;", ">").replace("&nbsp;", " ").replace("&amp;", "&"));
//                                row.add(map.annotations);
//                                csvWriter.addRow(row);
                                row.add(doc.name + "." + key);
                                row.add(map.description);
                                row.add(map.annotations);
                                csvWriter.addRow(row);

                            }
                        }
                    }
                }
                System.out.println("-----------------------------------------------------\n");
            }

            csvWriter.writeFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void walk(String path) {
        File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) return;

        for (File f : list) {
            if (f.isDirectory()) {
                walk(f.getAbsolutePath());
            } else {
                files.add(f.getAbsolutePath());
            }
        }
    }
}
