package org.example;

import java.io.*;
import java.util.*;

public class TextEditor implements Operations {
    private String currentFile;
    private StringBuilder fileContent;
    private ContextFreeGrammar currentGrammar;
    private Map<String, List<Rule>> grammarRules;
    private List<ContextFreeGrammar> grammarList;
    public TextEditor() {
        this.currentFile = null;
        this.fileContent = new StringBuilder();
        this.grammarRules = new HashMap<>();
        this.grammarList = new ArrayList<>();
    }
    public boolean hasGrammars() {
        return !grammarList.isEmpty();
    }

    @Override
    public void open(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("File " + filePath + " created with empty content.");
                return;
            }
            this.currentFile = filePath;
            grammarList.clear();
            grammarRules.clear();

            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            String grammarId = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("GrammarID: ")) {
                    grammarId = line.substring(11);
                    ContextFreeGrammar grammar = new ContextFreeGrammar(grammarId);
                    grammarList.add(grammar);
                    grammarRules.put(grammarId, new ArrayList<>());

                    // Автоматично задаваме първата граматика като currentGrammar
                    if (currentGrammar == null) {
                        currentGrammar = grammar;
                    }
                } else if (!line.isEmpty() && grammarId != null && line.contains("->")) {
                    String[] ruleParts = line.split("->");
                    if (ruleParts.length == 2) {
                        String variable = ruleParts[0].trim();
                        String terminals = ruleParts[1].trim();
                        Rule rule = new Rule(variable, terminals);
                        grammarRules.get(grammarId).add(rule);
                    }
                }
            }
            reader.close();
            System.out.println("Successfully opened " + filePath);
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
        Set<String> idList = new HashSet<>();
        if (grammarList.isEmpty()) {
            System.out.println("No grammars found.");
            return;
        }
        for (ContextFreeGrammar grammar : grammarList) {
            idList.add(grammar.getId());
        }
        System.out.println("All the grammar IDs: ");
        for (String id:idList) {
            System.out.println(id);
        }
    }

    @Override
    public void print(String id) {
        if (!grammarRules.containsKey(id)) {
            System.out.println("Error: Grammar ID not found.");
            return;
        }
        List<Rule> rules = grammarRules.get(id);
        System.out.println("GrammarID: " + id);
        System.out.println("Rules:");
        if (rules == null || rules.isEmpty()) {
            System.out.println("No rules found for this grammar.");
            return;
        }
        for (Rule rule : rules) {
            System.out.println(rule);
        }
    }
    @Override
    public void save() {
        if (currentFile == null) {
            System.out.println("Error: No file is currently opened. Use 'saveAs' to specify a file.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile, false))) {
            for (String grammarId : grammarRules.keySet()) {
                writer.write("GrammarID: " + grammarId);
                writer.newLine();
                writer.write("Rules:");
                writer.newLine();
                for (Rule rule : grammarRules.get(grammarId)) {
                    writer.write(rule.toString());
                    writer.newLine();
                }
                writer.newLine();
            }
            System.out.println("File saved successfully: " + currentFile);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    @Override
    public void save(String id, String filename) {
        if (currentGrammar == null) {
            System.out.println("Error: No grammar is currently opened");
            return;
        }

        if (!currentGrammar.getRules().containsKey(id)) {
            System.out.println("Error: Grammar with ID " + id + " not found");
            return;
        }

                grammarRules.put(id, currentGrammar.getRules().get(id));
                GrammarSerializer.serializeGrammar(grammarRules, filename);
                System.out.println("Successfully saved grammar with ID " + id + " to " + filename);
            }

    @Override
    public void saveAs(String fileName) {
        this.currentFile = fileName;  // Запазваме новото име на файла
        save();
    }

    @Override
    public void addRule(String grammarId, String ruleString) {
        if (currentGrammar == null || !currentGrammar.getId().equals(grammarId)) {
            currentGrammar = new ContextFreeGrammar(grammarId);
        }

        String[] parts = ruleString.split("->");
        if (parts.length != 2) {
            System.out.println("Invalid rule format: " + ruleString);
            System.out.println("Please see the usage as example");
            return;
        }

        String variable = parts[0].trim();
        if (!variable.matches("[A-Z]+")) {
            System.out.println("Please use upper case for variables!");
            return;
        }
        String terminals = parts[1].trim();
        if (terminals.matches("[A-Z]+")) {
            System.out.println("Please use lowercase or numbers for terminals!");
            return;
        }
        Rule rule = new Rule(variable, terminals);
        if (currentGrammar.getRules().containsKey(grammarId)) {
            // Ако граматиката вече съществува, добавяме новото правило към вече съществуващия списък с правила
            List<Rule> ruleList = currentGrammar.getRules().get(grammarId);
            ruleList.add(rule);
            System.out.println("Rule added to existing grammar " + grammarId + ": " + ruleString);
        } else {
            // Ако граматиката не съществува, създаваме нова граматика и добавяме новото правило към нея
            List<Rule> ruleList = new ArrayList<>();
            ruleList.add(rule);
            currentGrammar.getRules().put(grammarId, ruleList);
            System.out.println("New grammar created with rule " + grammarId + ": " + ruleString);
        }
    }
    @Override
    public void removeRule(String grammarId, int ruleIndex) {
        if (!grammarRules.containsKey(grammarId)) {
            System.out.println("Error: Grammar with ID " + grammarId + " does not exist.");
            return;
        }
        List<Rule> rules = grammarRules.get(grammarId);
        if (rules == null || rules.isEmpty()) {
            System.out.println("Error: No rules found for GrammarID " + grammarId);
            return;
        }
        if (ruleIndex < 0 || ruleIndex >= rules.size()) {
            System.out.println("Error: Rule index is out of bounds.");
            return;
        }
        Rule removedRule = rules.remove(ruleIndex);
        System.out.println("Removed rule: " + removedRule + " from GrammarID: " + grammarId);
        if (rules.isEmpty()) {
            grammarRules.remove(grammarId);
        } else {
            grammarRules.put(grammarId, rules);
        }
        save();
    }


    @Override
    public void union(String id1, String id2) {
        if (!grammarRules.containsKey(id1) || !grammarRules.containsKey(id2)) {
            System.out.println("Error: One or both grammars do not exist.");
            return;
        }
        String newGrammarId = id1 + "_" + id2;
        if (grammarRules.containsKey(newGrammarId)) {
            System.out.println("Error: Grammar with ID " + newGrammarId + " already exists.");
            return;
        }
        List<Rule> rules1 = grammarRules.get(id1);
        List<Rule> rules2 = grammarRules.get(id2);

        List<Rule> mergedRules = new ArrayList<>();
        mergedRules.addAll(rules1);
        mergedRules.addAll(rules2);

        ContextFreeGrammar newGrammar = new ContextFreeGrammar(newGrammarId);
        grammarList.add(newGrammar);
        grammarRules.put(newGrammarId, mergedRules);
        System.out.println("New grammar created: " + newGrammarId);
    }


    @Override
    public void concat(String id1, String id2) {

    }

    @Override
    public void chomsky(String id) {

    }

    @Override
    public void cyk(String id) {

    }

    @Override
    public void iter(String id) {

    }

    @Override
    public void empty(String id) {

    }

    @Override
    public void chomskify(String id) {

    }

    @Override
    public void help() {
        System.out.println("The following commands are supported:");
        System.out.println("open <file>\topens <file>");
        System.out.println("close\t\tcloses currently opened file");
        System.out.println("save\t\tsaves the currently open file");
        System.out.println("saveas <file>\tsaves the currently open file in <file>");
        System.out.println("addRule <id> <rule>\tadd rules");
        System.out.println("removeRule <id> <n>\tremove rule per index");
        System.out.println("list\tmakes a list with every grammar ID");
        System.out.println("help\t\tprints this information");
        System.out.println("exit\t\texists the program");
    }
}
