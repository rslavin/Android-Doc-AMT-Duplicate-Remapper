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
    private static final String DOCS_PATH = "C:\\Users\\Rocky\\AppData\\Local\\Android\\sdk\\docs\\reference\\android";
    private static final String FILTER_PATH = "AllAPI-Rank-50.list";
    private static final int MAX_PER_LINE = 20;
    private static final String OUTPUT_NAME = "AndroidDocs";
    public static final String MATCH_TYPE = "d"; // 'd' => description, 'b' => description and method (both)

    private static List<String> files;
    public static Filter filter;
    public static Map<String, ArrayList<String>> apis;
    public static List<String> descriptions;
    public static List<String> csvRows;
    public static int csvRowsCount;
    public static String csvRow;

    public static void main(String args[]) {
        try {
            filter = new Filter(FILTER_PATH); // instantiating a filter will filter the results through it
            CsvWriter csvWriter = new CsvWriter(OUTPUT_NAME);
            apis = new HashMap<>(); // instantiating a HashMap instead of null will get rid of duplicates
            descriptions = new ArrayList<>();
            files = new ArrayList();
            csvRows = new ArrayList<>();
            csvRowsCount = 1;
            csvRow = "";
            walk(DOCS_PATH);

            for (String file : files) {
                ClassDocumentation doc;// = new ClassDocumentation(file);
                if ((doc = ClassDocumentation.getDoc(file, (HashMap) apis, true)) != null)
                    doc.toCsvRows(MAX_PER_LINE);
            }
            // add whatever didn't make it to MAX_ROWS
            if(!csvRow.equals(""))
                csvRows.add(csvRow);
            for (String row : Driver.csvRows) {
                csvWriter.addRow(row.replaceAll("[^\\x00-\\x7F]", " "));
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
