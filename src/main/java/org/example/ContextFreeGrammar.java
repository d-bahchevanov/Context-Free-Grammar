package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextFreeGrammar {
    private String id;
    private Map<String, List<Rule>> rules;

    public Map<String, List<Rule>> getRules() {
        return rules;
    }

    public ContextFreeGrammar(String id) {
        this.id = id;
        this.rules = new HashMap<>();
    }

    public String getId() {
        return id;
    }
    public void addRule(String grammarId, List<Rule> rule) {
        rules.put(grammarId, rule);
    }
}
