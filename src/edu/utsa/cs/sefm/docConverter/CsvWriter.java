package edu.utsa.cs.sefm.docConverter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rocky on 12/18/2015.
 */
public class CsvWriter {
    private static final String DELIMITER = ",";
    private static final String NEWLINE = "\n";
    public String name;
    private FileWriter fw;
    private List<String> rows;

    public CsvWriter(String name) throws IOException {
        this.name = name;
        this.rows = new ArrayList<>();
        this.fw = new FileWriter(name + ".csv");
    }

    public void writeFile() throws IOException {
        for (String row : rows)
            fw.append(row);
        fw.flush();
        fw.close();
    }

    public void addRow(ArrayList<String> row) {
        String newRow = "";
        for (String cell : row) {
            cell = "\"" + cell + "\"";
            if (newRow.length() < 1)
                newRow = cell;
            else
                newRow += DELIMITER + cell;
        }
        rows.add(newRow + NEWLINE);
    }

    public void addRow(String row) {
//        rows.add("\"" + row.replace("\"", "'") + "\"" + NEWLINE);
        rows.add("\"" + row.replace("\"", "'").replace("&mdash;", "\",\"") + "\"" + NEWLINE); // split methods and descriptions into cells
    }

    public void addRow(String[] row) {
        String newRow = "";
        for (String cell : row) {
            cell = "\"" + cell.replaceAll("\"", "'") + "\"";
            if (newRow.length() < 1)
                newRow = cell;
            else
                newRow += DELIMITER + cell;
        }
        rows.add(newRow + NEWLINE);
    }

}
