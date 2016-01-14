package edu.utsa.cs.sefm.docConverter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rocky on 1/12/2016.
 *
 * Creates a list of API filters based on an input file.
 */
public class Filter {
    private List<String> apis;

    public Filter(String file) {
        apis = new ArrayList<>();
        FileReader inFile = null;
        try {
            inFile = new FileReader(file);

            BufferedReader br = new BufferedReader(inFile);
            String line = null;

            while ((line = br.readLine()) != null) {
                apis.add(line.substring(0, line.indexOf(' ')));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean match(String value){
        return apis.contains(value);
    }

}
