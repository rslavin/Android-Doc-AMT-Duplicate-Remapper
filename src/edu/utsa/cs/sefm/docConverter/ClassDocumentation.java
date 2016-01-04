package edu.utsa.cs.sefm.docConverter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rocky on 5/20/2015.
 */
public class ClassDocumentation {
    public String name;
    public String url;
    public Map<String, String> publicMethods; // methodSig -> description
    public Map<String, String> publicFields; // field -> description
    private String overview;
    private Map<String, ArrayList<String>> apis;

    public ClassDocumentation(String url, HashMap apis) {
        this.overview = null;
        this.name = "no name";
        this.url = url;
        this.publicMethods = new HashMap<>();
        this.publicFields = new HashMap<>();
        this.apis = apis;
    }

    /**
     * Creates a ClassDocumentation object based on the doc's url.
     *
     * @param url URL pointing to class documentation.
     * @return
     */
    public static ClassDocumentation getDoc(String url, HashMap apis, boolean isFile) {
        ClassDocumentation doc = new ClassDocumentation(url, apis);
        Document page;
        try {
            // get page
            if (isFile)
                page = Jsoup.parse(new File(url), "UTF-8", "");
            else
                page = Jsoup.connect(url).get();

            if (url.contains("package-summary.html"))
                return null;


            // get Class Inheritance
            Element className = page.select("[class=\"jd-inheritance-class-cell\"").last();
            doc.name = Jsoup.clean(className.toString(), Whitelist.none());
            // get Class Overview
            Element classOverview = page.select("[class=\"jd-descr\"]").first();
            doc.setOverview(Jsoup.clean(classOverview.toString(), Whitelist.none()));

            // get Public Methods
            Elements methodsTable = page.select("[id=\"pubmethods\"] [class=\"jd-linkcol\"]");

            Pattern methodNamePattern = Pattern.compile("\"*>([^>]+)<\\/a");
            Pattern methodParametersPattern = Pattern.compile("span>(\\([^<>]+\\))");
            Pattern methodTypePattern = Pattern.compile("span>(\\([^<>]+\\))");
            Pattern methodDescriptionPattern = Pattern.compile("jd-descrdiv\">\\s*\\n(.+)<\\/div", Pattern.DOTALL);
            for (Element rawMethod : methodsTable) {
                // get method name
                Matcher m = methodNamePattern.matcher(rawMethod.toString());
                String methodName = "no name";
                while (m.find())
                    methodName = m.group(1);

                // get method return type
                m = methodTypePattern.matcher(rawMethod.toString());
                String methodType = "unknown";
                while (m.find())
                    methodType = m.group(1);

                // get method params
                m = methodParametersPattern.matcher(rawMethod.toString());
                String methodParam = "()";
                while (m.find())
                    methodParam = m.group(1);

                // get method description
                m = methodDescriptionPattern.matcher(rawMethod.toString());
                String methodDescription = "no description";
                while (m.find())
                    methodDescription = m.group(1);

                doc.addMethod(methodName + methodParam, Jsoup.clean(methodDescription, Whitelist.none()));
            }

            // get Fields
            Elements fieldsTable = page.select("[id=\"lfields\"] [class*=api]");

            Pattern fieldNamePattern = Pattern.compile("\"*>([^>]+)<\\/a");
            Pattern fieldDescriptionPattern = Pattern.compile("%\">\\s*(.+)<\\/td", Pattern.DOTALL);
            for (Element rawField : fieldsTable) {
                // get method name
                Matcher m = fieldNamePattern.matcher(rawField.toString());
                String fieldName = "no name";
                while (m.find())
                    fieldName = m.group(1);

                // get method description
                m = fieldDescriptionPattern.matcher(rawField.toString());
                String fieldDescription = "no description";
                while (m.find())
                    fieldDescription = m.group(1);

                doc.addField(fieldName, Jsoup.clean(fieldDescription, Whitelist.none()));
            }

        } catch (IOException e) {
            System.err.println("Error retrieving documentation for '" + url + "'");
//            errors.add("Error retrieving documentation for '" + url + "'");
            e.printStackTrace();
//            System.err.println("retrying...");
//            this.getDoc(url, isFile);
        } catch (NullPointerException e) {
            System.err.println("Error retrieving class from " + url);
//            errors.add("Error retrieving class for '" + url + "'");
//            e.printStackTrace();
            return null;
        }

        return doc;
    }

    public String getOverview() {
        return this.overview;
    }

    public void setOverview(String overview) {
        this.overview = overview.replaceFirst("Class Overview", "");
    }

    public void addMethod(String method, String description) {
        publicMethods.put(method, description);
    }

    public void addField(String field, String description) {
        publicFields.put(field, description);
    }

    public void printMethods() {
        for (Map.Entry<String, String> method : publicMethods.entrySet()) {
            System.out.println("Method: " + method.getKey() + "\nDescription: " + method.getValue());
        }
    }

    public List<String> toCsvRows(int maxPerRow) {
        List rows = new ArrayList();
        String row = "";
        int count = 1;
        for (Map.Entry<String, String> method : publicMethods.entrySet()) {
            String addition = methodToRow(method);
            if (!addition.equals("")) {
                row += addition;//methodToRow(method);
                if (count++ >= maxPerRow) {
                    rows.add(row);
                    row = "";
                    count = 1;
                }
            }
        }

        return rows;
    }

    private String methodToRow(Map.Entry<String, String> method) {
//        String exploded[] = method.getKey().split(" ", 2);
//        String returnType = exploded[0];
//        try {
//            String theRest = exploded[1];
//            return returnType + " &mdash; " + theRest.replace("(", " (") + " &mdash; " + method.getValue() + " <br /><br />" ;
//        }catch(Exception e){
//            System.err.println(method.getKey());
//        }
//        return "";
        if(apis == null)
            return name + "." + method.getKey().replace("(", " (") + " &mdash; " + method.getValue() + " <br /><br />";
        if (!apis.containsKey(method.getKey()) || (apis.containsKey(method.getKey()) && !apis.get(method.getKey()).contains(method.getValue()))) {
            if (!apis.containsKey(method.getKey()))
                apis.put(method.getKey(), new ArrayList<String>());
            apis.get(method.getKey()).add(method.getValue());
            return name + "." + method.getKey().replace("(", " (") + " &mdash; " + method.getValue() + " <br /><br />";
        }
        return "";
    }


}
