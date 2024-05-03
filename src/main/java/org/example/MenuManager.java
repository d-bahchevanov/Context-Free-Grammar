package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MenuManager {
    private TextEditor editor;
    private BufferedReader inputReader;


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
                editor.save();
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

            case "list":

                break;
            case "print":
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