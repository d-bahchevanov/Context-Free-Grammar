package org.example;

import java.io.*;
import java.util.*;

public class TextEditor implements Operations {
    private String currentFile;
    private final StringBuilder fileContent;
    private ContextFreeGrammar currentGrammar;
    private final Map<String, List<Rule>> grammarRules;
    private final List<ContextFreeGrammar> grammarList;

    public TextEditor() {
        this.currentFile = null;
        this.fileContent = new StringBuilder();
        this.grammarRules = new HashMap<>();
        this.grammarList = new ArrayList<>();
    }

    private String generateNewVariable(Set<String> existingVariables) {
        for (char c = 'A'; c <= 'Z'; c++) {
            String candidate = String.valueOf(c);
            if (!existingVariables.contains(candidate)) {
                existingVariables.add(candidate);
                return candidate;
            }
        }
        throw new RuntimeException("No more available single-letter non-terminals!");
    }

    public boolean hasGrammars() {
        return !grammarList.isEmpty();
    }
    private boolean grammarAlreadyExists(String grammarId) {
        if (grammarRules.containsKey(grammarId)) {
            System.out.println("Error: Grammar with ID " + grammarId + " already exists.");
            return true;
        }
        return false;
    }
    private boolean grammarExists(String grammarId) {
        if (!grammarRules.containsKey(grammarId)) {
            System.out.println("Error: Grammar with ID " + grammarId + " does not exist.");
            return false;
        }
        return true;
    }

    private boolean invalidID(String id1, String id2) {
        if (!grammarRules.containsKey(id1) || !grammarRules.containsKey(id2)) {
            System.out.println("Error: One or both grammars do not exist.");
            return true;
        }
        return false;
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
        if (!grammarExists(id)) {
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
        public void saveAs(String filename) {
        if (currentFile != null && !grammarRules.isEmpty()) {
            save();
        }
        currentFile = filename;
        System.out.println("Now saving to: " + filename);
        save();
    }


    @Override
    public void addRule(String grammarId, String ruleString) {
        if (!grammarExists(grammarId)) {
            return;
        }
        String[] parts = ruleString.split("->");
        if (parts.length != 2) {
            System.out.println("Invalid rule format: " + ruleString);
            return;
        }
        String left = parts[0].trim();
        String right = parts[1].trim();
        if (!left.matches("[A-Z]")) {
            System.out.println("Error: Left side must be a single uppercase letter (non-terminal).");
            return;
        }
        if (right.matches("[a-z0-9]")) {
        } else if (right.matches("[A-Z]{1,2}")) {
            for (char c : right.toCharArray()) {
                if (!grammarRules.get(grammarId).stream().anyMatch(r -> r.getVariable().equals(String.valueOf(c)))) {
                    System.out.println("Error: Both non-terminals must be defined in the grammar.");
                    return;
                }
            }
        } else {
            System.out.println("Error: Right side must be a single terminal or a combination of one or two non-terminals.");
            return;
        }
        Rule newRule = new Rule(left, right);
        grammarRules.get(grammarId).add(newRule);
        System.out.println("Rule added successfully to grammar " + grammarId + ": " + ruleString);
        save();
    }

    @Override
    public void removeRule(String grammarId, int ruleIndex) {
        if (!grammarExists(grammarId)) {
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
        if (invalidID(id1,id2)) {
            return;
        }
        String newGrammarId = id1 + "_" + id2;
        if (grammarAlreadyExists(newGrammarId)) {
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
        save();
    }

    public void concat(String id1, String id2) {
        String newGrammarId = id1 + id2;
        if (grammarAlreadyExists(newGrammarId)) {
            return;
        }
        if (invalidID(id1,id2)) {
            return;
        }
        List<Rule> rules1 = grammarRules.get(id1);
        List<Rule> rules2 = grammarRules.get(id2);

        if (rules1.isEmpty() || rules2.isEmpty()) {
            System.out.println("Error: One of the grammars has no rules.");
            return;
        }

        String newProduction = rules1.get(0).getTerminals() + rules2.get(0).getTerminals();
        Rule newRule = new Rule(newGrammarId, newProduction);

        List<Rule> newRules = new ArrayList<>();
        newRules.add(newRule);
        grammarRules.put(newGrammarId, newRules);
        grammarList.add(new ContextFreeGrammar(newGrammarId));
        System.out.println("Concatenation successful! New GrammarID: " + newGrammarId);
        save();
    }

    @Override
    public boolean chomsky(String grammarId) {
        if (!grammarExists(grammarId)) {
            return false;
        }
        List<Rule> rules = grammarRules.get(grammarId);
        Set<String> definedNonTerminals = new HashSet<>();
        for (Rule r : rules) {
            definedNonTerminals.add(r.getVariable());
        }
        String startSymbol = rules.isEmpty() ? "S" : rules.get(0).getVariable();
        for (Rule rule : rules) {
            String left = rule.getVariable();
            String right = rule.getTerminals();
            if (!left.matches("[A-Z]")) {
                System.out.println("Grammar " + grammarId + " is NOT in CNF. Left side must be single uppercase letter: " + left);
                return false;
            }
            if (right.equals("ε")) {
                if (!left.equals(startSymbol)) {
                    System.out.println("Grammar " + grammarId + " is NOT in CNF. ε-production not on start symbol: " + rule);
                    return false;
                }
            }
            else if (right.matches("[a-z0-9]")) {
            }
            else if (right.matches("[A-Z]{2}")) {
                String B = right.substring(0, 1);
                String C = right.substring(1, 2);
                if (!definedNonTerminals.contains(B) || !definedNonTerminals.contains(C)) {
                    System.out.println("Grammar " + grammarId + " is NOT in CNF. Non-terminal(s) not defined: " + right);
                    return false;
                }
            }
            else {
                System.out.println("Grammar " + grammarId + " is NOT in CNF. Invalid right side: " + right);
                return false;
            }
        }
        System.out.println("Grammar " + grammarId + " IS in Chomsky Normal Form.");
        return true;
    }


    @Override
    public void iter(String grammarId) {
        if (!grammarExists(grammarId)) {
            return;
        }
        int count = 1;
        String newGrammarId;
        do {
            newGrammarId = grammarId + "_ITER" + (count == 1 ? "" : count);
            count++;
        } while (grammarRules.containsKey(newGrammarId));

        Set<String> existingVars = new HashSet<>();
        for (String gId : grammarRules.keySet()) {
            if (gId.startsWith(grammarId)) {
                for (Rule r : grammarRules.get(gId)) {
                    existingVars.add(r.getVariable());
                }
            }
        }
        String newStart = "S'";
        while (existingVars.contains(newStart)) {
            newStart += "'";
        }
        existingVars.add(newStart);
        List<Rule> originalRules = grammarRules.get(grammarId);
        List<Rule> newRules = new ArrayList<>(originalRules);
        newRules.add(new Rule(newStart, "ε"));
        newRules.add(new Rule(newStart, grammarId + newStart));
        grammarRules.put(newGrammarId, newRules);
        System.out.println("New grammar created: " + newGrammarId);
        save();
    }


    @Override
    public boolean empty(String grammarId) {
        if (!grammarExists(grammarId)) {
            return false;
        }
        Set<String> generating = new HashSet<>();
        boolean changed;
        do {
            changed = false;
            for (Rule rule : grammarRules.get(grammarId)) {
                if (!generating.contains(rule.getVariable()) &&
                        rule.getTerminals().chars().allMatch(c -> Character.isLowerCase(c) || generating.contains(String.valueOf((char) c)))) {
                    generating.add(rule.getVariable());
                    changed = true;
                }
            }
        } while (changed);
        boolean isEmpty = !generating.contains(grammarRules.get(grammarId).get(0).getVariable());
        if (isEmpty) {
            System.out.println("The grammar " + grammarId + " generates an empty language.");
        } else {
            System.out.println("The grammar " + grammarId + " does NOT generate an empty language.");
        }
        return isEmpty;
    }


    @Override
    public void chomskify(String grammarId) {
        if (!grammarExists(grammarId)) {
            return;
        }
        List<Rule> originalRules = new ArrayList<>(grammarRules.get(grammarId));
        List<Rule> newRules = new ArrayList<>();
        Set<String> existingVariables = new HashSet<>();
        Map<String, String> terminalToVariable = new HashMap<>();
        for (Rule rule : originalRules) {
            existingVariables.add(rule.getVariable());
        }
        for (Rule rule : originalRules) {
            String left = rule.getVariable();
            String right = rule.getTerminals();

            if (right.length() == 2 && right.matches("[a-z0-9][A-Z]")) {
                String terminal = right.substring(0, 1);
                String nonTerminal = right.substring(1);

                String newVar = terminalToVariable.computeIfAbsent(terminal, t -> {
                    String var = generateNewVariable(existingVariables);
                    newRules.add(new Rule(var, t));
                    return var;
                });

                newRules.add(new Rule(left, newVar + nonTerminal));
            } else {
                newRules.add(rule);
            }
        }
        List<Rule> cnfRules = new ArrayList<>();
        for (Rule rule : newRules) {
            String left = rule.getVariable();
            String right = rule.getTerminals();

            if (right.length() > 2 && right.matches("[A-Z]+")) {
                List<String> symbols = new ArrayList<>();
                for (char c : right.toCharArray()) {
                    symbols.add(String.valueOf(c));
                }

                String first = symbols.remove(0);
                String second = symbols.remove(0);
                String newVar = generateNewVariable(existingVariables);

                cnfRules.add(new Rule(left, first + newVar));

                while (symbols.size() > 1) {
                    String nextVar = generateNewVariable(existingVariables);
                    cnfRules.add(new Rule(newVar, second + nextVar));
                    newVar = nextVar;
                    second = symbols.remove(0);
                }

                cnfRules.add(new Rule(newVar, second + symbols.get(0)));
            } else {
                cnfRules.add(rule);
            }
        }
        String newGrammarId = grammarId + "_CNF";
        grammarRules.put(newGrammarId, cnfRules);
        System.out.println("New CNF grammar created: " + newGrammarId);
        save();
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
        System.out.println("print <id>\tprint by id");
        System.out.println("union <id1> <id2>\tunite <id1> <id2>");
        System.out.println("concat <id1> <id2>\tconcat <id1> <id2>");
        System.out.println("chomsky <id>\tcheck if id is in proper chomsky form <file>");
        System.out.println("chomskify <id>\tmake the grammar in proper chomsky form <file>");
        System.out.println("iter <id>\titer by id (Kleene star)");
        System.out.println("empty <id>\tcheck if empty by id");
        System.out.println("help\t\tprints this information");
        System.out.println("exit\t\texists the program");
    }
}
