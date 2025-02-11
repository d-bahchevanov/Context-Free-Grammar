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
            idList.addAll(grammar.getRules().keySet());
        }
    }

    @Override
    public void print(String id) {
            for (ContextFreeGrammar grammar : grammarList) {
                if (grammar.getId().equals(id)) {
                    System.out.println("GrammarID: " + grammar.getId());
                    System.out.println("Rules:");
                    Map<String, List<Rule>> rules = grammar.getRules();
                    for (Map.Entry<String, List<Rule>> entry : rules.entrySet()) {
                        List<Rule> ruleList = entry.getValue();
                        for (Rule rule : ruleList) {
                            System.out.println(rule.getVariable() + " -> " + rule.getTerminals());
                        }
                    }
                    return;
                }
            }
            System.out.println("Grammar with ID " + id + " not found.");
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
    public void saveAs(String filePath) {
        GrammarSerializer.serializeGrammar(currentGrammar.getRules(), filePath);
        currentFile = filePath;
        System.out.println("Successfully saved as " + filePath);
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
   /* @Override
    public void removeRule(String grammarId, int ruleIndex) {
        try {
            if (!currentGrammar.getRules().containsKey(grammarId)) {
                System.out.println("Error: Grammar with ID " + grammarId + " does not exist.");
                return;
            }
            List<Rule> rules = currentGrammar.getRules().get(grammarId);
            if (ruleIndex < 0 || ruleIndex >= rules.size()) {
                throw new IndexOutOfBoundsException("Rule index is out of bounds.");
            }
            rules.remove(ruleIndex);
            currentGrammar.getRules().put(grammarId, rules);
            System.out.println("Rule at index " + ruleIndex + " removed for GrammarID " + grammarId);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
        System.out.println();
    }
    */
   @Override
   public void removeRule(String grammarId, int ruleIndex) {
       try {
           for (ContextFreeGrammar grammar : grammarList) {
               if (grammar.getId().equals(grammarId)) {
                   List<Rule> rules = grammar.getRules().get(grammarId);
                   if (ruleIndex < 0 || ruleIndex >= rules.size()) {
                       throw new IndexOutOfBoundsException("Rule index is out of bounds.");
                   }
                   rules.remove(ruleIndex);
                   grammarRules.put(grammarId, rules);
                   System.out.println("Rule at index " + ruleIndex + " removed for GrammarID " + grammarId);
               }
           }
           System.out.println("Error: Grammar with ID " + grammarId + " does not exist.");
       } catch (IndexOutOfBoundsException e) {
           System.out.println("Error: " + e.getMessage());
       } catch (Exception e) {
           System.out.println("An error occurred: " + e.getMessage());
       }
       System.out.println();
   }

    @Override
    public void union(String id1, String id2) {

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
