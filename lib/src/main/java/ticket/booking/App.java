package ticket.booking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.services.UserBookingService;
import ticket.booking.util.UserServiceUtil;

public class App {

	public static void main(String[] args) throws IOException {
		System.out.println("Running Train Booking System");
		Scanner scanner = new Scanner(System.in);
		int option = 0;
		UserBookingService userBookingService;
		try {
			userBookingService = new UserBookingService();
		} catch (IOException ex) {
			System.out.println("There is something wrong in loading users.");
			return;
		}

		Train trainSelectedForBooking = new Train();
		String source = null;
		String dest = null;
		while (option != 7) {
			System.out.println("Choose option");
			System.out.println("1. Sign up");
			System.out.println("2. Login");
			System.out.println("3. Fetch Bookings");
			System.out.println("4. Search Trains");
			System.out.println("5. Book a Seat");
			System.out.println("6. Cancel my Booking");
			System.out.println("7. Exit the App");
			option = scanner.nextInt();
			switch (option) {
			case 1:
				System.out.println("Enter the username to signup");
				String nameToSignUp = scanner.next();
				System.out.println("Enter the password to signup");
				String passwordToSignUp = scanner.next();
				User userToSignup = new User(nameToSignUp, passwordToSignUp,
						UserServiceUtil.hashPassword(passwordToSignUp), new ArrayList<>(),
						UUID.randomUUID().toString());
				userBookingService.signUp(userToSignup);
				break;
			case 2:
				System.out.println("Enter the username to Login");
				String nameToLogin = scanner.next();
				System.out.println("Enter the password to signup");
				String passwordToLogin = scanner.next();
				User userToLogin = new User(nameToLogin, passwordToLogin, UserServiceUtil.hashPassword(passwordToLogin),
						new ArrayList<>(), UUID.randomUUID().toString());
				try {
					userBookingService = new UserBookingService(userToLogin);
					Boolean isLoginUser = userBookingService.loginUser();
					if (isLoginUser) {
						System.out.println("Successfully Logged In.");
						System.out.println("Logged in as :  " + userBookingService.fetchLoggedInUser().get().getName());
					} else {
						System.out.println("Incorrect UserName or Password.");
					}
				} catch (IOException ex) {
					return;
				}
				break;
			case 3:
				if (isUserLoggedIn(userBookingService)) {
					System.out.println("Fetching your bookings");
					userBookingService.fetchUserBookings();
				} else {
					System.out.println("Kindly log in to avail this service.");
				}
				break;
			case 4:
				System.out.println("Type your source station");
				source = scanner.next();
				System.out.println("Type your destination station");
				dest = scanner.next();
				List<Train> trains = userBookingService.getTrains(source, dest);
				int index = 1;
				for (Train t : trains) {
					System.out.println(index++ + " Train id : " + t.getTrainId());
					for (Map.Entry<String, String> entry : t.getStationTimes().entrySet()) {
						System.out.println("station " + entry.getKey() + " time: " + entry.getValue());
					}
				}
				if(trains.size() == 0) {
					System.out.println("No available trains from " + source + " to "+ dest);
					break;
				}
				if (isUserLoggedIn(userBookingService)) {
					System.out.println("Select a train by typing 1,2,3...");
					trainSelectedForBooking = trains.get(scanner.nextInt() - 1);
				}
				break;
			case 5:
				if (isUserLoggedIn(userBookingService)) {
					if (!trainSelectedForBooking.getTrainId().isEmpty()) {
						System.out.println("Select a seat out of these seats");
						List<List<Integer>> seats = userBookingService.fetchSeats(trainSelectedForBooking);
						for (List<Integer> row : seats) {
							for (Integer val : row) {
								System.out.print(val + " ");
							}
							System.out.println();
						}
						System.out.println("Select the seat by typing the row and column");
						System.out.println("Enter the row");
						int row = scanner.nextInt();
						System.out.println("Enter the column");
						int col = scanner.nextInt();
						System.out.println("Booking your seat....");
						Boolean booked = userBookingService.bookTrainSeat(trainSelectedForBooking, row, col, source, dest);
						if (booked.equals(Boolean.TRUE)) {
							System.out.println("Booked! Enjoy your journey");
						} else {
							System.out.println("Can't book this seat");
						}
					} else {
						System.out.println("Please select a Train.");
					}
				} else {
					System.out.println("Kindly log in to avail this service.");
				}
				break;
			case 6:
				if(isUserLoggedIn(userBookingService)) {
					System.out.println("Enter ticket Id");
					String ticketId = scanner.next();
					userBookingService.cancelBooking(ticketId);
				}else {
					System.out.println("Kindly log in to avail this service.");
				}
			default:
				break;
			}
		}

	}

	public static Boolean isUserLoggedIn(UserBookingService userBookingService) {
		return !Optional.ofNullable(userBookingService.fetchLoggedInUser()).isEmpty();
	}
}
