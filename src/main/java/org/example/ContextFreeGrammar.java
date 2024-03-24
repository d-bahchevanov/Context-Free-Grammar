package org.example;

import java.util.ArrayList;
import java.util.List;

public class ContextFreeGrammar {
    private String id;
    private List<Rule> rules;

    public ContextFreeGrammar(String id) {
        this.id = id;
        this.rules = new ArrayList<>();
    }

    public String getId() {
        return id;
    }
    public void addRule(Rule rule) {
       // Rule rule = new Rule(variable, terminals);
        rules.add(rule);
    }
}
