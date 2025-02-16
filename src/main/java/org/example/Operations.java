package org.example;
import java.util.List;
import java.util.Map;

public interface Operations {
    void list();
    void print(String id);
    void save();
    void save(String id, String filename);
    void addRule(String grammarId, String ruleString);
    void removeRule(String id, int n);
    void union(String id1, String id2);
    void concat(String id1, String id2);
    boolean chomsky(String id);
    //boolean cyk(String id, String word);
    void iter(String id);
    void empty(String id);
    void chomskify(String id);
    void open(String filePath);
    void close();
    void saveAs(String filePath);
    void help();
}
