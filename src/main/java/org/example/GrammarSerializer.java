package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class GrammarSerializer {
    public static void serializeGrammar(String id, List<Rule> rules, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("GrammarID: " + id + "\n");
            writer.write("Rules:\n");
            for (Rule rule : rules) {
                writer.write(rule.getVariable() + " -> " + rule.getTerminals() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing grammar to file: " + e.getMessage());
        }
    }
}
