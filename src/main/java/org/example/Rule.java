package org.example;

import java.util.ArrayList;
import java.util.List;

public class Rule {
    private String variable;
    private String terminals;
    public Rule(String variable, String terminals) {
        this.variable = variable;
        this.terminals = terminals;
    }

    public String getVariable() {
        return variable;
    }

    public String getTerminals() {
        return terminals;
    }
    @Override
    public String toString() {
        return variable + " -> " + terminals;
    }
}
