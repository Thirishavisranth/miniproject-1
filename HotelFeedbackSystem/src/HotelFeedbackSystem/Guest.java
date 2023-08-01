package HotelFeedbackSystem;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

interface PersonInterface {
    String getName();
    void setName(String name);
    String getAddress();
    void setAddress(String address);
    String getEmail();
    void setEmail(String email);
    String getPhoneNumber();
    void setPhoneNumber(String phoneNumber);
}

abstract class Person implements PersonInterface {
    private String name;
    private String address;
    private String email;
    private String phoneNumber;

    public Person(String name, String address, String email, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

abstract class Guest extends Person {
    private int age;
    private String nationality;

    public Guest(String name, String address, String email, String phoneNumber, int age, String nationality) {
        super(name, address, email, phoneNumber);
        this.age = age;
        this.nationality = nationality;
    }
    
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}

class ConcreteGuest extends Guest {
    public ConcreteGuest(String name, String address, String email, String phoneNumber, int age, String nationality) {
        super(name, address, email, phoneNumber, age, nationality);
    }
}

class HotelGuestSystem 
{
    private ArrayList<Guest> guestList;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/feedback?createDatabaseIfNotExist=true";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "trisha";

    public HotelGuestSystem()
    {
        guestList = new ArrayList<>();
    }

    private boolean isValidEmail(String email)
    {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean isValidPhoneNumber(String phoneNumber) 
    {
        String phoneRegex = "^[0-9]{10}$";
        return Pattern.matches(phoneRegex, phoneNumber);
    }

    private boolean isValidAge(int age)
    {
        return age >= 5 && age <= 150;
    }

    public void addNewGuest()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter guest name: ");
        String name = scanner.nextLine();
        System.out.print("Enter guest address: ");
        String address = scanner.nextLine();
        String email;
        do 
        {
            System.out.print("Enter guest email: ");
            email = scanner.nextLine();
            if (!isValidEmail(email)) 
            {
                System.out.println("Invalid email format. Please try again.");
            }
        } while (!isValidEmail(email));

        String phoneNumber;
        do 
        {
            System.out.print("Enter guest phone number: ");
            phoneNumber = scanner.nextLine();
            if (!isValidPhoneNumber(phoneNumber)) 
            {
                System.out.println("Invalid phone number format. Please enter a 10-digit number.");
            }
        } while (!isValidPhoneNumber(phoneNumber));

        int age;
        do 
        {
            System.out.print("Enter guest age: ");
            while (!scanner.hasNextInt()) 
            {
                System.out.println("Invalid input. Please enter a valid age.");
                scanner.next();
            }
            age = scanner.nextInt();
            scanner.nextLine(); 
            if (!isValidAge(age))
            {
                System.out.println("Invalid age. Please enter a number between 5 and 150.");
            }
        } while (!isValidAge(age));

        System.out.print("Enter guest nationality: ");
        String nationality = scanner.nextLine();

        Guest newGuest = new ConcreteGuest(name, address, email, phoneNumber, age, nationality);
        guestList.add(newGuest);
        System.out.println("Guest added successfully!");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) 
        {
            String sql = "INSERT INTO guests (name, address, email, phone_number, age, nationality) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) 
            {
                stmt.setString(1, name);
                stmt.setString(2, address);
                stmt.setString(3, email);
                stmt.setString(4, phoneNumber);
                stmt.setInt(5, age);
                stmt.setString(6, nationality);

                stmt.executeUpdate();
            }
            System.out.println("Guest details saved to the database!");
        } 
        catch (SQLException e) 
        {
            System.out.println("Error while saving guest details to the database: " + e.getMessage());
        }
        
    }
    public void updateGuestDataByEmail(String email) {
    	guestList = fetchAllGuestsFromDatabase();
        Scanner scanner = new Scanner(System.in);
        for (Guest guest : guestList) {
        	if (guest.getEmail().trim().equalsIgnoreCase(email.trim())) {
                System.out.println("Found guest with the email: " + email);
                System.out.println("Enter new guest details:");

                System.out.print("Enter guest name: ");
                String name = scanner.nextLine();
                System.out.print("Enter guest address: ");
                String address = scanner.nextLine();

                String phoneNumber;
                do {
                    System.out.print("Enter guest phone number: ");
                    phoneNumber = scanner.nextLine();
                    if (!isValidPhoneNumber(phoneNumber)) {
                        System.out.println("Invalid phone number format. Please enter a 10-digit number.");
                    }
                } while (!isValidPhoneNumber(phoneNumber));

                int age;
                do {
                    System.out.print("Enter guest age: ");
                    while (!scanner.hasNextInt()) {
                        System.out.println("Invalid input. Please enter a valid age.");
                        scanner.next();
                    }
                    age = scanner.nextInt();
                    scanner.nextLine(); 
                    if (!isValidAge(age)) {
                        System.out.println("Invalid age. Please enter a number between 5 and 150.");
                    }
                } while (!isValidAge(age));

                System.out.print("Enter guest nationality: ");
                String nationality = scanner.nextLine();

                guest.setName(name);
                guest.setAddress(address);
                guest.setPhoneNumber(phoneNumber);
                guest.setAge(age);
                guest.setNationality(nationality);

                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                    String sql = "UPDATE guests SET name=?, address=?, phone_number=?, age=?, nationality=? WHERE email=?";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, name);
                        stmt.setString(2, address);
                        stmt.setString(3, phoneNumber);
                        stmt.setInt(4, age);
                        stmt.setString(5, nationality);
                        stmt.setString(6, email);

                        stmt.executeUpdate();
                    }
                    System.out.println("Guest details updated successfully!");
                } catch (SQLException e) {
                    System.out.println("Error while updating guest details to the database: " + e.getMessage());
                }

                return; 
            }
        }
        System.out.println("Guest with email " + email + " not found.");
    }
    
    public void setGuestList(ArrayList<Guest> guests) {
        this.guestList = guests;
    }
    public ArrayList<Guest> fetchAllGuestsFromDatabase() {
        ArrayList<Guest> guestsFromDatabase = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) 
        {
            String sql = "SELECT name, address, email, phone_number, age, nationality FROM guests";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) 
            {
                try (ResultSet rs = stmt.executeQuery()) 
                {
                    while (rs.next()) {
                        String name = rs.getString("name");
                        String address = rs.getString("address");
                        String email = rs.getString("email");
                        String phoneNumber = rs.getString("phone_number");
                        int age = rs.getInt("age");
                        String nationality = rs.getString("nationality");

                        Guest guest = new ConcreteGuest(name, address, email, phoneNumber, age, nationality);
                        guestsFromDatabase.add(guest);
                    }
                }
            }
        } 
        catch (SQLException e) 
        {
            System.out.println("Error while fetching guests from the database: " + e.getMessage());
        }

        return guestsFromDatabase;
    }

    public void viewAllGuests() 
    {
        if (guestList.isEmpty()) 
        {
            System.out.println("No guests available.");
        } 
        else 
        {
            System.out.println("==== All Guests ====");
            int index = 1;
            for (Guest guest : guestList) 
            {
                System.out.println(index + ". Name: " + guest.getName());
                System.out.println("   Address: " + guest.getAddress());
                System.out.println("   Email: " + guest.getEmail());
                System.out.println("   Phone Number: " + guest.getPhoneNumber());
                System.out.println("   Age: " + guest.getAge());
                System.out.println("   Nationality: " + guest.getNationality());
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                index++;
            }
        }
    }

    public void run() 
    {
    	addNewGuest();
        viewAllGuests();  
    }

    public static void main(String[] args)
    {
        HotelGuestSystem guestSystem = new HotelGuestSystem();
        ArrayList<Guest> guestsFromDatabase = guestSystem.fetchAllGuestsFromDatabase();
        guestSystem.setGuestList(guestsFromDatabase);
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the email of the guest to update data: ");
        String email = scanner.nextLine();
        guestSystem.updateGuestDataByEmail(email);
        guestSystem.run();
        
    }
}
