package org.example;

import java.io.*;
import java.util.*;

public class GrammarSerializer {
    public static Map<String, List<Rule>> deserializeGrammar(String filePath) {
        Map<String, List<Rule>> grammarRules = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String grammarId = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.matches("^GrammarID: \\w+$")) {
                    grammarId = line.substring(11);
                    grammarRules.putIfAbsent(grammarId, new ArrayList<>());
                } else if (!line.isEmpty() && grammarId != null && line.contains("->")) {
                    String[] parts = line.split("->");
                    if (parts.length == 2) {
                        Rule rule = new Rule(parts[0].trim(), parts[1].trim());
                        grammarRules.get(grammarId).add(rule);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading grammar file: " + e.getMessage());
        }
        return grammarRules;
    }

    public static void serializeGrammar(Map<String, List<Rule>> grammarRules, String filePath) {
        Map<String, List<Rule>> existingGrammars = deserializeGrammar(filePath);

        for (Map.Entry<String, List<Rule>> entry : grammarRules.entrySet()) {
            existingGrammars.merge(entry.getKey(), entry.getValue(), (oldRules, newRules) -> {
                Set<String> uniqueRules = new HashSet<>();
                oldRules.stream().map(Rule::toString).forEach(uniqueRules::add);
                newRules.stream().map(Rule::toString).forEach(uniqueRules::add);

                return uniqueRules.stream().map(ruleStr -> {
                    String[] parts = ruleStr.split(" -> ");
                    return new Rule(parts[0], parts[1]);
                }).toList();
            });
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            if (existingGrammars.isEmpty()) {
                System.out.println("No grammars found. Creating new grammar file...");
            }

            for (Map.Entry<String, List<Rule>> entry : existingGrammars.entrySet()) {
                writer.write("GrammarID: " + entry.getKey() + "\nRules:\n");
                for (Rule rule : entry.getValue()) {
                    writer.write(rule.getVariable() + "->" + rule.getTerminals() + "\n");
                }
                writer.write("\n");
            }
            System.out.println("Successfully updated grammar file: " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing grammar file: " + e.getMessage());
        }
    }
}