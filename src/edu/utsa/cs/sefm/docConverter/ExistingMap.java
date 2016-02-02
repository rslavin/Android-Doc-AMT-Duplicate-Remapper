package edu.utsa.cs.sefm.docConverter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rocky on 1/31/2016.
 */
public class ExistingMap {
    public String method;
    public String description;
    public String annotations;

    public ExistingMap(String method, String annotations) {
        this.method = method;
        this.annotations = annotations;
    }

    public static List<ExistingMap> readMaps(String file) {
        List<ExistingMap> maps = new ArrayList<>();

        FileReader inFile = null;
        try {
            inFile = new FileReader(file);

            BufferedReader br = new BufferedReader(inFile);
            String line = null;

            while ((line = br.readLine()) != null) {
                String[] cells = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                maps.add(new ExistingMap(cells[0].replaceAll("^\"|\"$", ""), cells[1].replaceAll("^\"|\"$", "")));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return maps;
    }

    public String getSimpleMethod() {
        Pattern r = Pattern.compile("([^.]*\\))");
        Matcher m = r.matcher(this.method);
        if (m.find())
            return m.group(0);
        return "";
    }

}
