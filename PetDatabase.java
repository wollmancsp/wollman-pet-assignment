import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;

public class PetDatabase {
    // setting variables and creating array/scanner object
    private static final int CAPACITY = 100;
    private static Pet[] pets = new Pet[CAPACITY];
    private static int petCount = 0;
    private static final String filename = "pets.txt";
    private static Scanner scnr = new Scanner(System.in);

    public static void main(String[] args) {
        //loads database then displays menu for user interaction
        loadDatabase();
        int choice;
        do {
            choice = getUserChoice();
            switch (choice) {
                case 1:
                    addPets();
                    break;
                case 2:
                    showAllPets();
                    break;
              case 3:
                    System.out.println("Program Closing");
                    break;
                default:
                    System.out.println("Choice not valid. Please try again.");
            }
        } while (choice != 3);
    }

      private static void addPets() {
        System.out.println("Enter pet name and age, type 'exit' to finish:");
        String line;
        while (!(line = scnr.nextLine()).equals("exit")) {
            if (line.isEmpty()){
                continue;
            }
            String[] parts;
            try {
                parts = parseArgument(line);
                addPet(parts[0], Integer.parseInt(parts[1]));
            } catch (InvalidArgumentException | FullDatabaseException e) {
                System.out.println("Error adding pet: " + e.getMessage());
            }
        }
        scnr.nextLine();
        System.out.println("Thanks for adding pets! Back to the menu:");
    }

      private static int getUserChoice() {
        System.out.println("Press 1 to add pets");
        System.out.println("Press 2 to show all pets");
        System.out.println("Press 3 to search by pets age");
        System.out.println("Press 4 to remove a pet");
        System.out.println("Press 5 to save database");
        System.out.println("Press 6 to exit");
        // setting output to green
        System.out.printf("\u001B[32mEnter your choice: \u001B[0m");
        return scnr.nextInt();
    }

      private static void loadDatabase() {
        try (Scanner fileScanner = new Scanner(new File(filename))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = parseArgument(line);
                addPet(parts[0], Integer.parseInt(parts[1]));
            }
        } catch (FileNotFoundException | InvalidArgumentException | FullDatabaseException e) {
            System.out.printf("\u001B[31mError loading database: " + e.getMessage() + "\u001B[0m");
        }
    }

      private static void showAllPets() {
        // printTableHeader();
        for (int i = 0; i < petCount; i++) {
            printTableRow(i, pets[i].getName(), pets[i].getAge());
        }
        printTableFooter(petCount);
    }

      private static void printTableHeader() {
        System.out.println("+----------------------+");
        System.out.printf("| %-3s | %-10s | %-4s |\n", "ID", "NAME", "AGE");
        System.out.println("+----------------------+");
    }

    private static void printTableRow(int id, String name, int age) {
        System.out.printf("| %-3d | %-10s | %-4d |\n", id, name, age);
    }

    private static void printTableFooter(int rowCount) {
        System.out.println("+----------------------+");
        System.out.println(rowCount + " rows in set.");
    }
}
