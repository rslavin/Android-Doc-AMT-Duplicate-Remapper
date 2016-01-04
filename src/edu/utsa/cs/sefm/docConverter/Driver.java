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
    private static final int MAX_PER_LINE = 1;
    private static final String OUTPUT_NAME = "AndroidDocs";

    private static List<String> files;
    public static Map<String, ArrayList<String>> apis;


    public static void main(String args[]) {
        try {
            CsvWriter csvWriter = new CsvWriter(OUTPUT_NAME);
            apis = null;//new HashMap<>(); // instantiating a HashMap instead of null will get rid of duplicates
            files = new ArrayList();
            walk(DOCS_PATH);

            for (String file : files) {
                ClassDocumentation doc;// = new ClassDocumentation(file);
                if ((doc = ClassDocumentation.getDoc(file,(HashMap) apis, true)) != null)
                    for (String row : doc.toCsvRows(MAX_PER_LINE)) {
                        csvWriter.addRow(row.replaceAll("[^\\x00-\\x7F]", " "));
                    }
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
