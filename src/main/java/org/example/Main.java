package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
public class Main {
    public static void main(String[] args) {
        TextEditor editor = new TextEditor();
        /*
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        */
        List<Rule> rules = List.of(

                new Rule("A", "aB"),
                new Rule("B", "bC"),
                new Rule("C", "c")
        );

        GrammarSerializer.serializeGrammar("A", rules, "grammarRules.txt");
        System.out.println("Grammar serialized successfully.");
        /*while (true) {
            try {
                System.out.print("> ");
                String[] command = inputReader.readLine().split(" ");
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
                        editor.save();
                        break;
                    case "addRule":

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
                        System.out.println("Exiting the program...");
                        System.exit(0);
                    default:
                        System.out.println("Unknown command. Type 'help' for available commands.");
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
         */
        MenuManager menuManager = new MenuManager(editor);
        menuManager.start();
    }
}