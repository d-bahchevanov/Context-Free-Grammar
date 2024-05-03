package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
public class Main {
    public static void main(String[] args) {
        TextEditor editor = new TextEditor();
        MenuManager menuManager = new MenuManager(editor);
        menuManager.start();
    }
}