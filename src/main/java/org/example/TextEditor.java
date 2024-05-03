package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TextEditor implements Operations{
    private String currentFile;
    private StringBuilder fileContent;
    private ContextFreeGrammar currentGrammar;

    public TextEditor() {
        this.currentFile = null;
        this.fileContent = new StringBuilder();
    }

    @Override
    public void open(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("File " + filePath + " created with empty content.");
            } else {
                BufferedReader reader = new BufferedReader(new FileReader(filePath));
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContent.append(line).append("\n");
                }
                reader.close();
                currentFile = filePath;
                System.out.println("Successfully opened " + filePath);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    @Override
    public void close() {
        fileContent.setLength(0);
        currentFile = null;
        System.out.println("Successfully closed file");
    }

    @Override
    public void list() {

    }

    @Override
    public void print() {

    }

    @Override
    public void save(){
        if (currentFile == null) {
            System.out.println("Error: No file is currently opened");
            return;
        }
            GrammarSerializer.serializeGrammar(currentGrammar.getRules(), currentFile);
            System.out.println("Successfully saved changes to " + currentFile);
    }

    @Override
    public void saveAs(String filePath) {
        GrammarSerializer.serializeGrammar(currentGrammar.getRules(), filePath);
        currentFile = filePath;
        System.out.println("Successfully saved as " + filePath);


    }
    @Override
    public void addRule(String grammarId, String ruleString) {
        if (currentGrammar == null) {
            currentGrammar = new ContextFreeGrammar(grammarId);
        } else if (!currentGrammar.getId().equals(grammarId)) {
            currentGrammar = new ContextFreeGrammar(grammarId);
        }
        String[] parts = ruleString.split("->");
        if (parts.length != 2) {
            System.out.println("Invalid rule format: " + ruleString);
            System.out.println("Please see the usage as example");
            return;
        }
        String variable = parts[0].trim();
        String terminals = parts[1].trim();
        Rule rule = new Rule(variable, terminals);
        List<Rule> ruleList = new ArrayList<>();
        ruleList.add(rule);
        currentGrammar.addRule(grammarId, ruleList);
        System.out.println("Rule added to grammar " + grammarId + ": " + ruleString);
    }

    @Override
    public void removeRule() {

    }

    @Override
    public void union() {

    }

    @Override
    public void concat() {

    }

    @Override
    public void chomsky() {

    }

    @Override
    public void cyk() {

    }

    @Override
    public void iter() {

    }

    @Override
    public void empty() {

    }

    @Override
    public void chomskify() {

    }

    @Override
    public void help() {
        System.out.println("The following commands are supported:");
        System.out.println("open <file>\topens <file>");
        System.out.println("close\t\tcloses currently opened file");
        System.out.println("save\t\tsaves the currently open file");
        System.out.println("saveas <file>\tsaves the currently open file in <file>");
        System.out.println("help\t\tprints this information");
        System.out.println("exit\t\texists the program");
    }


}
