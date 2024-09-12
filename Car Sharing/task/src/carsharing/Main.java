package carsharing;

import java.sql.*;

public class Main {

    public static void main(String[] args) {
        CommandLineParser cp = new CommandLineParser();
        cp.addParameter("-databaseFileName", "test");
        cp.add(args);
        String databaseFileName = cp.getValue("-databaseFileName");
        MenuHierarchy menu = new MenuHierarchy(databaseFileName);
        menu.showMainMenu();
        System.out.println("Goodbye!");
    }
}