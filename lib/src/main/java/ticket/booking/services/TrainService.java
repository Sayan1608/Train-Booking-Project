package ticket.booking.services;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ticket.booking.entities.Train;

public class TrainService {
	
	private Train train;
	
	private List<Train> trainList;
	
	private static final String TRAIN_PATH = "C:/DEMO-WS/Ticket-Booking-App/lib/src/main/resources/localDb/trains.json";
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	public TrainService() throws IOException {
		loadTainList();
	}
	
	public TrainService(Train train) throws IOException {
		this.train = train;
		loadTainList();
	}
		
	public void loadTainList() throws IOException {
		File trains = new File(TRAIN_PATH);
		trainList = objectMapper.readValue(trains, new TypeReference<List<Train>>() {
		});
	}
	
	public void saveTainListToFile() throws IOException {
		File trains = new File(TRAIN_PATH);
		objectMapper.writeValue(trains, trainList);
	}
	
	public List<Train> searchTains(String source, String destination){
		return trainList.stream().filter(train -> isValidTrain(train, source, destination)).collect(Collectors.toList());
	}
	
	public Boolean isValidTrain(Train train, String source, String destination) {
		List<String> stationOrder = train.getStations();
		
		int sourceIndex = stationOrder.indexOf(source.toLowerCase());
		int destinationIndex = stationOrder.indexOf(destination);
		
		return sourceIndex != -1 && destinationIndex != -1 && sourceIndex < destinationIndex;
	}

	public void addTrain(Train newTrain) throws IOException {
		 // Check if a train with the same trainId already exists
		Optional<Train> existingTrain = trainList.stream()
				.filter(t -> t.getTrainId().equalsIgnoreCase(newTrain.getTrainId())).findFirst();
		if(existingTrain.isPresent()) {
			// If a train with the same trainId exists, update it instead of adding a new one
			updateTrain(newTrain);
		}else {
			// Otherwise, add the new train to the list
			trainList.add(newTrain);
			saveTainListToFile();
		}
		
	}

	private void updateTrain(Train updatedTrain) throws IOException {
		// Find the index of the train with the same trainId
		OptionalInt index = IntStream.range(0, trainList.size())
		.filter(i -> trainList.get(i).getTrainId().equalsIgnoreCase(updatedTrain.getTrainId())).findFirst();
		
		if(index.isPresent()) {
			// If found, replace the existing train with the updated one
			trainList.set(index.getAsInt(), updatedTrain);
			saveTainListToFile();
		}else {
			// If not found, treat it as adding a new train
			addTrain(updatedTrain);
		}
	}

}
