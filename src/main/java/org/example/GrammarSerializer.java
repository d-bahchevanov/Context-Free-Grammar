package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GrammarSerializer {
    public static void serializeGrammar(Map<String, List<Rule>> grammarMap, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String grammarId : grammarMap.keySet()) {
                writer.write("GrammarID: " + grammarId + "\n");
                writer.write("Rules:\n");
                List<Rule> rules = grammarMap.get(grammarId);
                for (Rule rule : rules) {
                    writer.write(rule.getVariable() + " -> " + rule.getTerminals() + "\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing grammar to file: " + e.getMessage());
        }
    }
}