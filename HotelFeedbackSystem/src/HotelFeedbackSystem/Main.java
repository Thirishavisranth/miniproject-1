package HotelFeedbackSystem;

import java.util.ArrayList;
import java.util.Scanner;

//... (Other classes and methods remain unchanged)

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HotelGuestSystem guestSystem = new HotelGuestSystem();
        HotelFeedbackSystem feedbackSystem = new HotelFeedbackSystem();
        ResponseInput responseInput = new ResponseInput();
        String email;

        while (true) {
            System.out.println("====  Welcome to Hotel Feedback Management System  ====");
            System.out.println("1. Guest Registration");
            System.out.println("2. Give Feedback");
            System.out.println("3. View current Feedbacks");
            System.out.println("4. Give Response");
            System.out.println("5. View All Responses");
            System.out.println("6. Delete Response");
            System.out.println("7. Update Guest Data");
            System.out.println("8. Get All Guests");
            System.out.println("9. view feedback");
            System.out.println("10. Exit");
            System.out.print("Enter your choice (1/2/3/4/5/6/7/8/9): ");
            System.out.println();
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    guestSystem.addNewGuest();
                    System.out.println();
                    break;
                case "2":
                    feedbackSystem.submitFeedback();
                    System.out.println();
                    break;
                case "3":
                    feedbackSystem.viewAllFeedbacks();
                    System.out.println();
                    break;
                case "4":
                    System.out.println("==== Response Menu ====");
                    System.out.println("1. Give Positive Response");
                    System.out.println("2. Give Negative Response");
                    System.out.println("3. Give Response");
                    System.out.print("Enter your choice (1/2/3): ");
                    System.out.println();
                    String responseChoice = scanner.nextLine();
                    switch (responseChoice) {
                        case "1":
                            responseInput.givePositiveResponse();
                            System.out.println();
                            break;
                        case "2":
                            responseInput.giveNegativeResponse();
                            System.out.println();
                            break;
                        case "3":
                            responseInput.giveResponse();
                            System.out.println();
                            break;
                        default:
                            System.out.println("Invalid choice for Response Menu.");
                            System.out.println();
                            break;
                    }
                    break;
                case "5":
                    responseInput.viewAllResponse();
                    System.out.println();
                    break;
                case "6":
                    responseInput.deleteResponse();
                    System.out.println();
                    break;
                case "7":
                    System.out.print("Enter the email of the guest to update data: ");
                    email = scanner.nextLine();
                    guestSystem.updateGuestDataByEmail(email);
                    System.out.println();
                    break;
                case "8":
                    ArrayList<Guest> allGuests = guestSystem.fetchAllGuestsFromDatabase();
                    System.out.println("==== All Guests ====");
                    for (Guest guest : allGuests) {
                        System.out.println("Name: " + guest.getName());
                        System.out.println("Address: " + guest.getAddress());
                        System.out.println("Email: " + guest.getEmail());
                        System.out.println("Phone Number: " + guest.getPhoneNumber());
                        System.out.println("Age: " + guest.getAge());
                        System.out.println("Nationality: " + guest.getNationality());
                        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    }
                    System.out.println();
                    break;
                case "9":
                    responseInput.viewFeedbackResponses();
                    System.out.println();
                    break;
                case "10":
                    System.out.println("Welcome Again! Thank You");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }
}
