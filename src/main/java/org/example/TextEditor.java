package org.example;

import java.io.*;

public class TextEditor {
    private String currentFile;
    private StringBuilder fileContent;
    private ContextFreeGrammar currentGrammar;

    public TextEditor() {
        this.currentFile = null;
        this.fileContent = new StringBuilder();
    }

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

    public void close() {
        fileContent.setLength(0);
        currentFile = null;
        System.out.println("Successfully closed file");
    }

    public void save() {
        if (currentFile == null) {
            System.out.println("Error: No file is currently opened");
            return;
        }
        saveAs(currentFile);
    }

    public void saveAs(String filePath) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(fileContent.toString());
            writer.close();
            currentFile = filePath;
            System.out.println("Successfully saved as " + filePath);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public void addRule(String grammarId, String ruleString) {
        /*if (currentGrammar == null) {
            System.out.println("Error: No grammar loaded. Please open a grammar first.");
            return;
        }
        Rule rule = new Rule(variable, terminals);
        currentGrammar.addRule(rule);
        System.out.println("Rule added: " + rule);
               */
        if (currentGrammar == null) {
            currentGrammar = new ContextFreeGrammar(grammarId);
        } else if (!currentGrammar.getId().equals(grammarId)) {
            // Ако текущият идентификатор на граматиката не съвпада с подадения, създаваме нова граматика
            currentGrammar = new ContextFreeGrammar(grammarId);
        }

        // Парсване на ruleString за създаване на обект от тип Rule
        String[] parts = ruleString.split("->");
        if (parts.length != 2) {
            System.out.println("Invalid rule format: " + ruleString);
            return;
        }
        String variable = parts[0].trim();
        String terminals = parts[1].trim();

        // Създаване на обект от тип Rule
        Rule rule = new Rule(variable, terminals);

        // Добавяне на правилото към текущата граматика
        currentGrammar.addRule(rule);
        System.out.println("Rule added to grammar " + grammarId + ": " + ruleString);
    }

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
