package org.example;

import java.io.IOException;

public interface Operations {
    void list();
    void print();
    void save();
    void addRule(String grammarId, String ruleString);
    void removeRule();
    void union();
    void concat();
    void chomsky();
    void cyk();
    void iter();
    void empty();
    void chomskify();
    void open(String filePath);
    void close();
    void saveAs(String filePath);
    void help();
}
