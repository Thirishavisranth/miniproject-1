package HotelFeedbackSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

class Feedback {
    private String name;
    private String feedback;
    private int rating;
    private Date date;
    private int roomNumber;
    private String email;
    private String phoneNumber;
    private String favoriteService;
    private String improvementSuggestions;
    private String wouldVisitAgain;
    private String response; 

    public Feedback(String name, String feedback, int rating, Date date, int roomNumber, String email, String phoneNumber,
                    String favoriteService, String improvementSuggestions, String wouldVisitAgain) {
        this.name = name;
        this.feedback = feedback;
        this.rating = rating;
        this.date = date;
        this.roomNumber = roomNumber;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.favoriteService = favoriteService;
        this.improvementSuggestions = improvementSuggestions;
        this.wouldVisitAgain = wouldVisitAgain;
        this.response = "No response"; 
    }

    public String getName() {
        return name;
    }

    public String getFeedback() {
        return feedback;
    }

    public int getRating() {
        return rating;
    }

    public Date getDate() {
        return date;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFavoriteService() {
        return favoriteService;
    }

    public String getImprovementSuggestions() {
        return improvementSuggestions;
    }

    public String getWouldVisitAgain() {
        return wouldVisitAgain;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}

class HotelFeedbackSystem {
    private ArrayList<Feedback> feedbackList;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/feedback?createDatabaseIfNotExist=true";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "trisha";

    public HotelFeedbackSystem() {
        feedbackList = new ArrayList<>();
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^[0-9]{10}$";
        return Pattern.matches(phoneRegex, phoneNumber);
    }

    private boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 10;
    }

    private boolean isValidYNChoice(String choice) {
        return choice.equalsIgnoreCase("Y") || choice.equalsIgnoreCase("N");
    }

    public void submitFeedback() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your feedback: ");
        String feedback = scanner.nextLine();
        int rating;
        do {
            System.out.print("Enter your rating (1-10): ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a valid rating between 1 and 10.");
                scanner.next();
            }
            rating = scanner.nextInt();
            scanner.nextLine(); 
            if (!isValidRating(rating)) {
                System.out.println("Invalid rating. Please enter a number between 1 and 10.");
            }
        } while (!isValidRating(rating));

        System.out.print("Enter the room number: ");
        int roomNumber;
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter digits only for the room number.");
            scanner.next();
        }
        roomNumber = scanner.nextInt();
        scanner.nextLine(); 
        String emailChoice;
        do {
            System.out.print("Would you like to share your email address for any follow-up queries? (Y/N): ");
            emailChoice = scanner.nextLine();
            if (!isValidYNChoice(emailChoice)) {
                System.out.println("Invalid choice. Please enter 'Y' or 'N'.");
            }
        } while (!isValidYNChoice(emailChoice));

        String email = null;
        if (emailChoice.equalsIgnoreCase("Y")) {
            do {
                System.out.print("Enter your email address: ");
                email = scanner.nextLine();
                if (!isValidEmail(email)) {
                    System.out.println("Invalid email format. Please try again.");
                    email = null;
                }
            } while (email == null);
        }

        String phoneChoice;
        do {
            System.out.print("Could you provide your phone number in case we need to contact you? (Y/N): ");
            phoneChoice = scanner.nextLine();
            if (!isValidYNChoice(phoneChoice)) {
                System.out.println("Invalid choice. Please enter 'Y' or 'N'.");
            }
        } while (!isValidYNChoice(phoneChoice));

        String phoneNumber = null;
        if (phoneChoice.equalsIgnoreCase("Y")) {
            do {
                System.out.print("Enter your phone number: ");
                phoneNumber = scanner.nextLine();
                if (!isValidPhoneNumber(phoneNumber)) {
                    System.out.println("Invalid phone number format. Please enter digits only (up to 10 digits).");
                    phoneNumber = null;
                }
            } while (phoneNumber == null);
        }

        String wouldVisitAgainChoice;
        do {
            System.out.print("Would you consider visiting us again? (Y/N): ");
            wouldVisitAgainChoice = scanner.nextLine();
            if (!isValidYNChoice(wouldVisitAgainChoice)) {
                System.out.println("Invalid choice. Please enter 'Y' or 'N'.");
            }
        } while (!isValidYNChoice(wouldVisitAgainChoice));

        String wouldVisitAgainMsg;
        if (wouldVisitAgainChoice.equalsIgnoreCase("Y")) {
            wouldVisitAgainMsg = "Thank you for considering visiting us again!";
        } else {
            wouldVisitAgainMsg = "We're sorry to hear that you won't be visiting us again. We hope to improve your experience next time.";
        }

        System.out.print("What service did you like the most during your stay? ");
        String favoriteService = scanner.nextLine();
        System.out.print("Do you have any suggestions for improvement? ");
        String improvementSuggestions = scanner.nextLine();

        Feedback newFeedback = new Feedback(name, feedback, rating, new Date(), roomNumber, email, phoneNumber,
                favoriteService, improvementSuggestions, wouldVisitAgainChoice);
        feedbackList.add(newFeedback);
        System.out.println("Feedback submitted successfully!");
        System.out.println(wouldVisitAgainMsg);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "INSERT INTO feedback (name, feedback, rating, date, room_number, email, phone_number, favorite_service, improvement_suggestions, would_visit_again, response) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, name);
                stmt.setString(2, feedback);
                stmt.setInt(3, rating);
                stmt.setTimestamp(4, new java.sql.Timestamp(new Date().getTime()));
                stmt.setInt(5, roomNumber);
                stmt.setString(6, email);
                stmt.setString(7, phoneNumber);
                stmt.setString(8, favoriteService);
                stmt.setString(9, improvementSuggestions);
                stmt.setString(10, wouldVisitAgainChoice);
                stmt.setString(11, newFeedback.getResponse()); 

                stmt.executeUpdate();
            }
            System.out.println("Feedback submitted successfully and saved to the database!");
            System.out.println(wouldVisitAgainMsg);
        } catch (SQLException e) {
            System.out.println("Error while saving feedback to the database: " + e.getMessage());
            return;
        }
        
    }

    public void viewAllFeedbacks() {
        if (feedbackList.isEmpty()) {
            System.out.println("No feedbacks available.");
        } else {
            System.out.println("==== All Feedbacks ====");
            int index = 1;
            for (Feedback feedback : feedbackList) {
                System.out.println(index + ". Name: " + feedback.getName());
                System.out.println("   Feedback: " + feedback.getFeedback());
                System.out.println("   Rating: " + feedback.getRating());
                System.out.println("   Date: " + feedback.getDate());
                System.out.println("   Room Number: " + feedback.getRoomNumber());
                System.out.println("   Email: " + feedback.getEmail());
                System.out.println("   Phone Number: " + feedback.getPhoneNumber());
                System.out.println("   Favorite Service: " + feedback.getFavoriteService());
                System.out.println("   Improvement Suggestions: " + feedback.getImprovementSuggestions());
                System.out.println("   Would Visit Again: " + feedback.getWouldVisitAgain());
                System.out.println("   Response: " + feedback.getResponse());
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                index++;
            }
        }
    }

    public void run() {
    	submitFeedback();
    	viewAllFeedbacks();
    }

    public static void main(String[] args) {
        HotelFeedbackSystem feedbackSystem = new HotelFeedbackSystem();
        feedbackSystem.run();
    }
}
