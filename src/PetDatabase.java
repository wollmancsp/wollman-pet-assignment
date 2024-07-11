/*
 * I certify, that this computer program submitted by me is all of my own work.
 * Signed: Chris Wollman
 */

import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;


/*
 * Creating exception classes that extend Exception
 * super(message) is used to passed up to Exception constructor which
 * initializes the exception
 */

class InvalidIdException extends Exception {
    InvalidIdException(String message) {
        super(message);
    }
}
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
        if (age < 0 || age > 20) {
            throw new InvalidAgeException("\u001B[31m" + age + " is an invalid entry please enter an age 0-20\u001B[0m\n");
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
        if (age < 0 || age > 20) {
            throw new InvalidAgeException(age + " is an invalid entry\n");
        }
        this.age = age;
    }
}

public class PetDatabase {
    // setting variables and creating array/scanner object
    private static final int CAPACITY = 5;
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
                    showAllPets();
                    break;
                case 2:
                    addPets();
                    break;
                case 3:
                    updatePet();
                    break;
                case 4:
                    removePet();
                    break;
                case 5:
                    searchPetsByName();
                    break;
                case 6:
                    searchPetsByAge();
                    break;
                case 7:
                    System.out.println("7) Exit program");
                    break;
                default:
                    System.out.println("Choice not valid. Please try again.");
            }
        } while (choice != 7);
        System.out.print("Goodbye!");
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
        } catch (FileNotFoundException | InvalidArgumentException | FullDatabaseException | InvalidAgeException e) {
            System.out.printf("\u001B[31mError loading database: " + e.getMessage() + "\u001B[0m");
        }
    }

    /*
     * opens up writer to pets.txt
     * writes pet name and age to file
     * prints success message
     */
    private static void saveDatabase() {
        try (PrintWriter writer = new PrintWriter(filename)) {
            for (int i = 0; i < petCount; i++) {
                writer.println(pets[i].getName() + " " + pets[i].getAge());
            }
            System.out.println("Database saved successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("Error saving database: " + e.getMessage());
        }
    }

    /*
     * this part of the code creates a menu to the screen and is used above
     * in the switch statements
     */
    private static int getUserChoice() {
        System.out.println("What would you like to do?");
        System.out.println("1) View all pets");
        System.out.println("2) Add more pets");
        System.out.println("3) Update an existing pet");
        System.out.println("4) Remove an existing pet");
        System.out.println("5) Search pets by name");
        System.out.println("6) Search pets by age");
        System.out.println("7) Exit program ");
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
        System.out.println("add pet (name, age):");
        String line;
        int petsAddedCounter = 0;
        while (!(line = scnr.nextLine()).equals("done")) {
            if (line.isEmpty()){
                continue;
            }
            String[] parts;
            try {
                parts = parseArgument(line);
                addPet(parts[0], Integer.parseInt(parts[1]));
                petsAddedCounter ++;
            } catch (InvalidArgumentException | FullDatabaseException | InvalidAgeException e) {
                System.out.println("Error adding pet: " + e.getMessage());
            }
        }
        System.out.println(petsAddedCounter + " pets added.");
    }

    private static void addPet(String name, int age) throws FullDatabaseException, InvalidAgeException {
        //exception if at capacity
        if (petCount == CAPACITY) {
            throw new FullDatabaseException("\u001B[31mDatabase is full.\u001B[0m");
        }
        /*
         * creates new pet object
         * increments petCount
         * prints success or throws error
         */
       // try {
        pets[petCount] = new Pet(name, age);
        petCount++;
       // } catch (InvalidAgeException e) {
       //     System.out.printf("\u001B[31mError adding pet: " + e.getMessage() + "\u001B[0m");
       // }
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
     * asks for user input and verifies it's valid
     * if valid, removes pet and shifts everything after it "left"
     * decrements petCount
     * If invalid, throws exception.
     */
    private static void removePet() {
        showAllPets();
        scnr.nextLine(); // Consume newline
        System.out.print("Enter the ID of the pet to remove: ");
        int id = scnr.nextInt();

        try {
            if (id < 0 || id >= petCount) {
                throw new InvalidIdException("\u001B[31mInvalid ID.\u001B[0m");
            }

            String petBeingRemoved = pets[id].getName();
            int removedPetAge = pets[id].getAge();

            for (int i = id; i < petCount - 1; i++) {
                pets[i] = pets[i + 1];
            }

            petCount--;
            System.out.printf("\u001B[32m%s %d is removed.\u001B[0m\n", petBeingRemoved, removedPetAge);

        } catch (InvalidIdException e) {
            System.out.printf("\u001B[31mError removing pet: " + e.getMessage() + "\u001B[0m");
        }
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
    /*
     * function to search pets by name
     * Ignoring case in leiu of some error handling
     * printing header/footer and filling with any rows that match
     */
    private static void searchPetsByName() {
        scnr.nextLine();
        System.out.println("Enter a name to search: ");
        String name = scnr.nextLine();
        printTableHeader();
        int rowCount = 0;
        for (int i = 0; i < petCount; i++) {
            if (pets[i].getName().equalsIgnoreCase(name)) {
                printTableRow(i, pets[i].getName(), pets[i].getAge());
                rowCount++;
            }
        }
        printTableFooter(rowCount);
    }
    /*
     * method to update a pet
     * Stores old and new information so that it can be printed out at
     * the end like the sample run asked
     */
    private static void updatePet(){
        if (petCount == 0){
            System.out.println("There aren't any pets to update!");
            return;
        }

        showAllPets();
        System.out.println("Enter the pet ID you came to update:");
        int id = scnr.nextInt();
        scnr.nextLine();
        try {
            if (id >= petCount || id < 0){
                throw new InvalidIdException("\u001B[31mInvalid ID.\u001B[0m");
            }
            System.out.print("Enter new name: ");
            String newName = scnr.nextLine();
            System.out.print("Enter new age: ");
            int newAge = scnr.nextInt();
            scnr.nextLine();

            String oldName = pets[id].getName();
            int oldAge = pets[id].getAge();

            pets[id].setName(newName);
            pets[id].setAge(newAge);

            System.out.println(oldName + " " + oldAge + " changed to " + newName + " " + newAge + ".");

        } catch (InvalidIdException | InvalidAgeException e) {
        System.out.printf("\u001B[31mError updating pet: " + e.getMessage() + "\u001B[0m");
    }
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
