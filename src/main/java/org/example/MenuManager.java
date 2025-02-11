package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import static javax.script.ScriptEngine.FILENAME;

public class MenuManager {
    private TextEditor editor;
    private BufferedReader inputReader;
    private ContextFreeGrammar currentGrammar;

    public MenuManager(TextEditor editor) {
        this.editor = editor;
        this.inputReader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void start() {
        while (true) {
            try {
                System.out.print("> ");
                String[] command = inputReader.readLine().split(" ");
                processCommand(command);
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void processCommand(String[] command) throws IOException {
        switch (command[0]) {
            case "open":
                if (command.length != 2) {
                    System.out.println("Usage: open <file>");
                    break;
                }
                editor.open(command[1]);
                break;
            case "close":
                editor.close();
                break;
            case "save":
                if (command.length != 3) {
                    System.out.println("Usage: save <id> <filename>");
                    break;
                }
                String idToSave = command[1];
                String filename = command[2];
                editor.save(idToSave, filename);
                break;
            case "addRule":
                if (command.length != 3) {
                    System.out.println("Usage: addRule <grammarId> <variable>-><terminals>");
                    break;
                }
                String variable = command[1];
                String terminals = command[2];
                editor.addRule(variable, terminals);
                break;
            case "removeRule":
                if (command.length != 3) {
                    System.out.println("Usage: removeRule <grammarId> <ruleIndex>");
                    break;
                }
                String grammarId = command[1];
                int ruleIndex = Integer.parseInt(command[2]);
                editor.removeRule(grammarId, ruleIndex);
                break;
            case "list":
                if (command.length > 1) {
                    System.out.println("Usage: list");
                    break;
                }
                try {
                    if (editor.hasGrammars()) {
                        System.out.println("Listed successfully!");
                        editor.list();
                    } else {
                        System.out.println("No grammars found!");
                    }
                } catch (NullPointerException e) {
                    System.out.println("Error: " + e.getMessage());
                }
                break;
            case "print":
                if (command.length != 2) {
                    System.out.println("Usage: print <id>");
                    break;
                }
                String id = command[1];
                editor.print(id);
                    break;
            case "saveas":
                if (command.length != 2) {
                    System.out.println("Usage: saveas <file>");
                    break;
                }
                editor.saveAs(command[1]);
                break;
            case "help":
                editor.help();
                break;
            case "exit":
                System.out.println("Exiting the program.....");
                System.exit(0);
            default:
                System.out.println("Unknown command. Type 'help' for available commands.");
        }
    }
}