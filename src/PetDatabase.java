/*
 * I certify, that this computer program submitted by me is all of my own work.
 * Signed: Chris Wollman
 */

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;


/*
 * Creating exception classes that extend Exception
 * super(message) is used to passed up to Exception constructor which
 * initializes the exception
 */

class InvalidAgeException extends Exception {
    InvalidAgeException(String message) {
        super(message);
    }
}

class InvalidArgumentException extends Exception {
    InvalidArgumentException(String message) {
        super(message);
    }
}
class FullDatabaseException extends Exception {
    FullDatabaseException(String message) {
        super(message);
    }
}

/*
 * Constructor method for Pet
 * Checks age range, throws exception if outside of range
 */
class Pet {
    private String name;
    private int age;

    Pet(String name, int age) throws InvalidAgeException {
        if (age < 1 || age > 50) {
            throw new InvalidAgeException("\u001B[31m" + age + " is an invalid entry\u001B[0m");
        }
        this.name = name;
        this.age = age;
    }

    // getters and setters
    String getName() {
        return name;
    }

    int getAge() {
        return age;
    }

    void setName(String name) {
        this.name = name;
    }

    // also throws exception here for invalid age
    void setAge(int age) throws InvalidAgeException {
        if (age < 1 || age > 50) {
            throw new InvalidAgeException(age + " is an invalid entry");
        }
        this.age = age;
    }
}

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
                    searchPetsByAge();
                    break;
                case 4:
                    System.out.println("Program Closing");
                    break;
                default:
                    System.out.println("Choice not valid. Please try again.");
            }
        } while (choice != 4);
    }

    /*
     * uses try-with-resources, this closes scanner properly
     * loops as long as there are more lines in file
     * parseArgument is used to separate the line of input into strings
     * uses addPet method based on the parsing of arguments
     * Catch block can catch any 3 different exceptions using OR
     */
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

    /*
     * this part of the code creates a menu to the screen and is used above
     * in the switch statements
     */
    private static int getUserChoice() {
        System.out.println("Press 1 to add pets");
        System.out.println("Press 2 to show all pets");
        System.out.println("Press 3 to search by pets age");
        System.out.println("Press 4 to exit");
        // setting output to green
        System.out.printf("\u001B[32mEnter your choice: \u001B[0m");
        return scnr.nextInt();
    }

    /*
     * user prompted to enter pet name and age
     * response stored in String variable line
     * opens up an array to parse "line" into two array entries [0] and [1]
     * uses addPet to add into database
     */
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

    private static void addPet(String name, int age) throws FullDatabaseException {
        //exception if at capacity
        if (petCount == CAPACITY) {
            throw new FullDatabaseException("\u001B[31mDatabase is full.\u001B[0m");
        }
        /*
         * creates new pet object
         * increments petCount
         * prints success or throws error
         */
        try {
            pets[petCount] = new Pet(name, age);
            petCount++;
            System.out.println("Woohoo! Pet added successfully!");
        } catch (InvalidAgeException e) {
            System.out.printf("\u001B[31mError adding pet: " + e.getMessage() + "\u001B[0m");
        }
    }

    /*
     * splits line into array of strings based on where the space is
     * checks that there is two parts, if not, error.
     */
    private static String[] parseArgument(String line) throws InvalidArgumentException {
        String[] parts = line.split("\\s+");
        if (parts.length != 2) {
            throw new InvalidArgumentException("\u001B[31mInvalid input format, please try again.\u001B[0m");
        }
        return parts;
    }

    /*
     * prints header, then rows, then footer
     */
    private static void showAllPets() {
        printTableHeader();
        for (int i = 0; i < petCount; i++) {
            printTableRow(i, pets[i].getName(), pets[i].getAge());
        }
        printTableFooter(petCount);
    }

    /*
     * asks for user input (age)
     * prints header, then loops through for matching age
     * prints row if age matches and increments row count
     * prints footer
     */
    private static void searchPetsByAge() {
        System.out.print("Enter the age to search: ");
        int age = scnr.nextInt();
        scnr.nextLine();
        printTableHeader();
        int rowCount = 0;
        for (int i = 0; i < petCount; i++) {
            if (pets[i].getAge() == age) {
                printTableRow(i, pets[i].getName(), pets[i].getAge());
                rowCount++;
            }
        }
        printTableFooter(rowCount);
    }

    // methods to print header, row and footer
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