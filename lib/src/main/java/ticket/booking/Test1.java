package ticket.booking;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Test1 {

	 public static void main(String[] args) {
	        // Print the current working directory
	        String currentDirectory = System.getProperty("user.dir");
	        System.out.println("Current working directory: " + currentDirectory);

	        // Define the relative path to the JSON file
	        String relativePath = "../src/main/java/ticket/booking/localDb/users.json";
	        File file = new File(relativePath);

	        try {
	            // Print the canonical path for debugging
	            System.out.println("Canonical path: " + file.getCanonicalPath());

	            // Check if the file exists
	            if (file.exists()) {
	                System.out.println("File exists at: " + file.getAbsolutePath());
	                try {
	                    // Read the file content
	                    String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
	                    System.out.println("File content: " + content);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            } else {
	                System.out.println("File does not exist at the specified path: " + file.getAbsolutePath());
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

}
