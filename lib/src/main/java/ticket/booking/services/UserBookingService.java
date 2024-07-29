package ticket.booking.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtil;

public class UserBookingService {

	private User user;

	private List<User> userList;

	private static final String USERS_PATH = "C:/DEMO-WS/Ticket-Booking-App/lib/src/main/resources/localDb/users.json";

	private static ObjectMapper objectMapper = new ObjectMapper();

	public UserBookingService() throws IOException {
		loadUserList();
	}

	public UserBookingService(User user) throws IOException {
		this.user = user;
		loadUserList();
	}

	public void loadUserList() throws IOException {
		File users = new File(USERS_PATH);
		userList = objectMapper.readValue(users, new TypeReference<List<User>>() {
		});
	}

	public Optional<User> fetchLoggedInUser() {
		if(user != null) {
			return Optional.ofNullable(userList.stream().filter(user1 -> 
			user1.getName().equals(user.getName())
			&& UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword())
					).findFirst().orElse(null));
		}else {
			return null;
		}
	}

	public Boolean loginUser() {
//		Optional<Optional<User>> foundUser = Optional.ofNullable(fetchLoggedInUser());
//		if(foundUser.isPresent()) {
//			return foundUser.isPresent();
//		}
//		return false;
		return !Optional.ofNullable(fetchLoggedInUser()).isEmpty();
		
	}

	public Boolean signUp(User user1) {
		try {
			userList.add(user1);
			saveUserListToFile();
			return Boolean.TRUE;
		} catch (IOException ex) {
			return Boolean.FALSE;
		}
	}

	public void saveUserListToFile() throws IOException {
		File users = new File(USERS_PATH);
		objectMapper.writeValue(users, userList);
	}

	public void fetchUserBookings() {
		Optional<Optional<User>> fetchedUser = Optional.ofNullable(fetchLoggedInUser());
		if (!fetchedUser.isEmpty()) {
			fetchedUser.get().get().printTickets();
		}
	}

	public Boolean cancelBooking(String ticketId) throws IOException {
//		Scanner sc = new Scanner(System.in);
//		System.out.println("Enter the ticket Id to cancel : ");
//		ticketId = sc.next();

		if (ticketId == null || ticketId.isEmpty()) {
			System.out.println("Ticket Id cannot be null or empty");
			return Boolean.FALSE;
		}

		final String finalTicketId = ticketId;

		boolean removed = user.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(finalTicketId));

		if (removed) {
			System.out.println("Ticket Id : " + ticketId + " is cancelled successfully.");
			saveUserListToFile();
			return Boolean.TRUE;
		}

		System.out.println("Ticket Id : " + ticketId + " is not found.");
		return Boolean.FALSE;

	}

	public List<Train> getTrains(String source, String destination) {
		try {
			TrainService trainService = new TrainService();
			return trainService.searchTains(source, destination);
		} catch (Exception e) {
			return new ArrayList<Train>();
		}
	}

	public List<List<Integer>> fetchSeats(Train train) {
		return train.getSeats();
	}
	
	public Boolean bookTrainSeat(Train train, int row, int seat, String source, String dest) {
		try {
			TrainService trainService = new TrainService();
			List<List<Integer>> seats = train.getSeats();
			if(row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
				if(seats.get(row).get(seat) == 0) {
					seats.get(row).set(seat, 1);
					train.setSeats(seats);
					trainService.addTrain(train);
					Optional<User> loggedInUser = fetchLoggedInUser();
					if(loggedInUser.isPresent()) {
						Ticket ticket = new Ticket();
						ticket.setTicketId(generateTicketId());
						ticket.setSource(source);
						ticket.setDestination(dest);
						ticket.setTrain(train);
						loggedInUser.get().getTicketsBooked().add(ticket);
						saveUserListToFile();
						return Boolean.TRUE;
					}
					return Boolean.FALSE;
				}else {
					return false;
				}
			}else {
				return false;
			}
		} catch (IOException e) {
			return Boolean.FALSE;
		}
	}

	private String generateTicketId() {
		return UUID.randomUUID().toString();
	}
	
//	public static void main(String[] args) {
//		try {
//			File file = new File(USERS_PATH);
//			file.exists();
//			List<User> userws = objectMapper.readValue(file, new TypeReference<List<User>>() {
//			});
//			System.out.println("success");
//		} catch (Exception e) {
//			System.out.println("wrong");
//		}
//	}

}
