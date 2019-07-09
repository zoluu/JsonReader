package JsonReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;


/**
 * Takes a JSON file and adds all of the data to a database.
 * @author Muhammad Rahim
 *
 */
public class JsonReader {
	private static final String CREATE_EVENTS_TABLE = "Create table if not exists Events (id varchar(255) not null, type varchar(255), host varchar(255), duration integer, alert boolean)";
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String INSERT_TO_EVENTS = "INSERT INTO Events VALUES (?, ?, ?, ?, ?)";

	public static void main(String[] args) {
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
		} catch (Exception e) {
			e.printStackTrace();
		}
		JsonReader.begin("/Users/muhammadrahim/Desktop/test.json");
	}
	
	/**
	 * Begins the task
	 * @param jsonPath (non-null)
	 * @throws IllegalArgumentException Thrown if no JSON file provided
	 */
	public static void begin(String jsonPath) {
		if (Objects.isNull(jsonPath)) {
			throw new IllegalArgumentException("No JSON file provided");
		}
		InputStream input = null;
		try {
			File filePath = new File(jsonPath);
			input = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			LOGGER.debug("No JSON file found");
			return;
		}
		try (Scanner fileScanner = new Scanner(input)) {
			scanJsonFile(fileScanner);
		}
	}

	/**
	 * Converts each line from JSON file into an object
	 * @param fileScanner Scanner for the input file (non-null)
	 * @throws IllegalArgumentException Thrown if pre-conditions not met
	 */
	private static void scanJsonFile(Scanner fileScanner) {
		if (Objects.isNull(fileScanner)) {
			throw new IllegalArgumentException("Scanner not found.");
		}
		LOGGER.info("Starting to scan JSON file");
		JSONObject obj;
		List<EventData> list = new ArrayList<>();
		while (fileScanner.hasNextLine()) {
			String line = fileScanner.nextLine();
			obj = new JSONObject(new JSONTokener(line.trim()));
			String id = obj.getString("id");
			String state = obj.getString("state");
			String type = obj.getString("type");
			String host = obj.getString("host");
			Long timestamp = obj.getLong("timestamp");
			LOGGER.debug("Current line: " + line.toString());
			Optional<EventData> op = list.stream().filter(e -> Objects.equals(e.getId(), id)).findFirst();
			if (op.isPresent()) {
				EventData existing = op.get();
				long timeTaken = Math.abs(existing.getTimestamp() - timestamp);
				existing.setTimeTaken(timeTaken);
				if (timeTaken > 4) {
					existing.setAlert(true);
				}
			} else {
				EventData ed = new EventData(id, state, type, host, timestamp);
				list.add(ed);
			}
		}
		LOGGER.info("Finished searching JSON file. Now to insert to DB...");
		uploadToDatabase(list);
	}

	/**
	 * Uploads Events data to database
	 * @param list List of Events from JSON file (non-null & non-empty)
	 * @throws IllegalArgumentException Thrown if pre-conditions not met
	 */
	private static void uploadToDatabase(List<EventData> list) {
		if (Objects.isNull(list) || list.size() == 0) {
			throw new IllegalArgumentException("List is empty or null.");
		}
		String user = "sa";
		String password = "";
		String url = "jdbc:hsqldb:file:test";
		try (Connection c = DriverManager.getConnection(url, user, password)) {
			Statement stmt = c.createStatement();
			stmt.execute(
					CREATE_EVENTS_TABLE);
			LOGGER.info("Events table created successfully. Now inserting data...");

			PreparedStatement ps = c.prepareStatement(INSERT_TO_EVENTS);
			for (EventData event : list) {
				LOGGER.debug(event.toString());
				ps.setString(1, event.getId());
				ps.setString(2, event.getType());
				ps.setString(3, event.getHost());
				ps.setLong(4, event.getTimeTaken());
				ps.setBoolean(5, event.isAlert());
				ps.addBatch();
			}
			ps.executeBatch();
			c.commit();
			LOGGER.info("All data successfully committed.");
		} catch (SQLException e) {
			LOGGER.error("Error saving to database", e);
		}
	}
}
