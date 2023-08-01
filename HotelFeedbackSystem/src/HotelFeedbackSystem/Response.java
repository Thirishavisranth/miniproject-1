package HotelFeedbackSystem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Response {
	private int responseId; 
    private int feedbackId;
    private String response;
    private Date responseDate;

    public Response(int responseId,int feedbackId, String response, Date responseDate) {
    	this.responseId = responseId;
        this.feedbackId = feedbackId;
        this.response = response;
        this.responseDate = responseDate;
    }
	public int getResponseId() {
        return responseId;
    }

    public void setResponseId(int responseId) {
        this.responseId = responseId;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Date getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(Date responseDate) {
        this.responseDate = responseDate;
    }
}
class PositiveResponse extends Response {
    private boolean isComplimentary;

    public PositiveResponse(int responseId, int feedbackId, String response, Date responseDate, boolean isComplimentary) {
        super(responseId, feedbackId, response, responseDate);
        this.isComplimentary = isComplimentary;
    }

    public boolean isComplimentary() {
        return isComplimentary;
    }

    public void setComplimentary(boolean complimentary) {
        isComplimentary = complimentary;
    }
}
class NegativeResponse extends Response {
    private String complaintReason;

    public NegativeResponse(int responseId, int feedbackId, String response, Date responseDate, String complaintReason) {
        super(responseId, feedbackId, response, responseDate);
        this.complaintReason = complaintReason;
    }

    public String getComplaintReason() {
        return complaintReason;
    }

    public void setComplaintReason(String complaintReason) {
        this.complaintReason = complaintReason;
    }
}
class FeedbackResponse {
    private int feedbackId;
    private String response;
    private String feedback;

    public FeedbackResponse(int feedbackId, String response, String feedback) {
        this.feedbackId = feedbackId;
        this.response = response;
        this.feedback = feedback;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}


class ResponseInput {
    private static Response givenResponse;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/feedback?createDatabaseIfNotExist=true";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "trisha";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static boolean isValidFeedbackId(int feedbackId) {
        return feedbackId > 0; 
    }
    private static boolean isResponseIdUnique(int responseId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String checkResponseSql = "SELECT * FROM responses WHERE response_id = ?";
            try (PreparedStatement checkResponseStmt = conn.prepareStatement(checkResponseSql)) {
                checkResponseStmt.setInt(1, responseId);
                try (ResultSet rs = checkResponseStmt.executeQuery()) {
                    return !rs.next();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while checking response ID: " + e.getMessage());
            return false;
        }
    }
    
    public static void giveResponse() {
        Scanner scanner = new Scanner(System.in);
        
        int responseId;
        do {
            System.out.print("Enter response ID: ");
            responseId = scanner.nextInt();
            scanner.nextLine(); 

            if (!isResponseIdUnique(responseId)) {
                System.out.println("Response ID must be unique. Please choose another value.");
            }
        } while (!isResponseIdUnique(responseId));
        System.out.print("Enter feedback ID: ");
        int feedbackId = scanner.nextInt();

        scanner.nextLine(); 

        if (!isValidFeedbackId(feedbackId)) {
            System.out.println("Invalid feedback ID. Please enter a positive integer.");
            return;
        }
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String checkFeedbackSql = "SELECT * FROM feedback WHERE feedback_id = ?";
            try (PreparedStatement checkFeedbackStmt = conn.prepareStatement(checkFeedbackSql)) {
                checkFeedbackStmt.setInt(1, feedbackId);
                try (ResultSet rs = checkFeedbackStmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("No feedback found with the provided ID: " + feedbackId);
                        return;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while checking feedback ID: " + e.getMessage());
            return;
        }
        System.out.print("Enter response: ");
        String response = scanner.nextLine();
        Date responseDate = new Date();
        givenResponse = new Response(responseId,feedbackId, response, responseDate);
        System.out.println("Response recorded successfully!");
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String saveResponseSql = "INSERT INTO responses (response_id,feedback_id, response, response_date) VALUES (?, ?, ?, ?)";
            try (PreparedStatement saveResponseStmt = conn.prepareStatement(saveResponseSql)) {
            	saveResponseStmt.setInt(1, givenResponse.getResponseId());
                saveResponseStmt.setInt(2, givenResponse.getFeedbackId());
                saveResponseStmt.setString(3, givenResponse.getResponse());
                saveResponseStmt.setString(4, DATE_FORMAT.format(givenResponse.getResponseDate()));
                saveResponseStmt.executeUpdate();
            }
            String updateFeedbackSql = "UPDATE feedback SET response = ? WHERE feedback_id = ?";
            try (PreparedStatement updateFeedbackStmt = conn.prepareStatement(updateFeedbackSql)) {
                updateFeedbackStmt.setString(1, response);
                updateFeedbackStmt.setInt(2, feedbackId);
                updateFeedbackStmt.executeUpdate();
            }

            System.out.println("Response recorded successfully!");
        } catch (SQLException e) {
            System.out.println("Error while saving response details to the database: " + e.getMessage());
        }
    }
    public static void deleteResponse() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the response ID you want to delete: ");
        int responseIdToDelete = scanner.nextInt();
        scanner.nextLine(); 

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String checkResponseSql = "SELECT * FROM responses WHERE response_id = ?";
            try (PreparedStatement checkResponseStmt = conn.prepareStatement(checkResponseSql)) {
                checkResponseStmt.setInt(1, responseIdToDelete);
                try (ResultSet rs = checkResponseStmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("No response found with the provided response ID: " + responseIdToDelete);
                        return;
                    }
                }
            }
            String deleteResponseSql = "DELETE FROM responses WHERE response_id = ?";
            try (PreparedStatement deleteResponseStmt = conn.prepareStatement(deleteResponseSql)) {
                deleteResponseStmt.setInt(1, responseIdToDelete);
                int rowsAffected = deleteResponseStmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Response with ID " + responseIdToDelete + " has been deleted successfully.");
                } else {
                    System.out.println("Deletion failed. No response found with ID " + responseIdToDelete);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error while deleting response from the database: " + e.getMessage());
        }
    }


    public static void viewAllResponse() {
        List<Response> responseList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "SELECT * FROM responses";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                    	int responseId = rs.getInt("response_id");
                        int feedbackId = rs.getInt("feedback_id");
                        String response = rs.getString("response");
                        Date responseDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                .parse(rs.getString("response_date"));

                        Response newResponse = new Response(responseId,feedbackId, response, responseDate);
                        responseList.add(newResponse);
                    }
                }
            }
        } catch (SQLException | ParseException e) {
            System.out.println("Error while fetching responses: " + e.getMessage());
            return;
        }

        if (responseList.isEmpty()) {
            System.out.println("No responses available.");
        } else {
            System.out.println("==== All Responses ====");
            int index = 1;
            for (Response response : responseList) {
                System.out.println(index + ". Feedback ID: " + response.getFeedbackId());
                System.out.println("   Response: " + response.getResponse());
                System.out.println("   Response Date: " + response.getResponseDate());
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                index++;
            }
        }
    }

    public static void viewFeedbackResponses() {
        List<FeedbackResponse> feedbackResponsesList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "SELECT r.feedback_id, r.response, f.feedback FROM responses r " +
                    "LEFT JOIN feedback f ON r.feedback_id = f.feedback_id";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int feedbackId = rs.getInt("feedback_id");
                        String response = rs.getString("response");
                        String feedback = rs.getString("feedback");

                        FeedbackResponse newFeedbackResponse = new FeedbackResponse(feedbackId, response, feedback);
                        feedbackResponsesList.add(newFeedbackResponse);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while fetching feedback responses: " + e.getMessage());
            return;
        }

        if (feedbackResponsesList.isEmpty()) {
            System.out.println("No feedback responses available.");
        } else {
            System.out.println("              ~~~~~~~~~~~~~~~~~~~~  Feedback Responses ~~~~~~~~~~~~~~~~~~~");
            System.out.println();
            System.out.println("+=============+======================================+============================================+");
            System.out.println("| Feedback ID | Response                             | Feedback                                   |");
            System.out.println("+=============+======================================+============================================+");
            for (FeedbackResponse feedbackResponse : feedbackResponsesList) {
                System.out.format("| %-11d | %-36s | %-42s |\n", feedbackResponse.getFeedbackId(), feedbackResponse.getResponse(), feedbackResponse.getFeedback());
            }
            System.out.println("+=============+======================================+============================================+");
        }
    }



public static void givePositiveResponse() {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Enter response ID: ");
    int responseId = scanner.nextInt();
    scanner.nextLine(); 
    int feedbackId;
    do {
        System.out.print("Enter feedback ID: ");
        feedbackId = scanner.nextInt();
        scanner.nextLine(); 

        if (!isValidFeedbackId(feedbackId)) {
            System.out.println("Invalid feedback ID. Please enter a positive integer.");
        }
    } while (!isValidFeedbackId(feedbackId));
    System.out.print("Enter response: ");
    String response = scanner.nextLine();
    Date responseDate = new Date();
    PositiveResponse positiveResponse = new PositiveResponse(responseId, feedbackId, response, responseDate, true);
    System.out.println("Positive Response recorded successfully!");
}


public static void giveNegativeResponse() {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Enter response ID: ");
    int responseId = scanner.nextInt();
    scanner.nextLine(); 

    int feedbackId;
    do {
        System.out.print("Enter feedback ID: ");
        feedbackId = scanner.nextInt();
        scanner.nextLine(); 

        if (!isValidFeedbackId(feedbackId)) {
            System.out.println("Invalid feedback ID. Please enter a positive integer.");
        }
    } while (!isValidFeedbackId(feedbackId));
    System.out.print("Enter response: ");
    String response = scanner.nextLine();
    System.out.print("Enter complaint reason: ");
    String complaintReason = scanner.nextLine();
    Date responseDate = new Date();
    NegativeResponse negativeResponse = new NegativeResponse(responseId, feedbackId, response, responseDate, complaintReason);
    System.out.println("Negative Response recorded successfully!");
}
public static void main(String[] args) {
	viewFeedbackResponses();
	givePositiveResponse();
	giveNegativeResponse();
	viewAllResponse();
	
        
}
}
