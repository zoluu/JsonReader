package JsonReader;

import org.junit.Test;
import static org.junit.Assert.*;

import org.hsqldb.server.Server;

public class JsonReaderTest {
	private static final String dbPath = "file:creditsuisse";
	private static Server server;

	@Test
	public void testSomeLibraryMethod() {
		server = new Server();

		server.setDatabaseName(0, "test");
		server.setDatabasePath(0, dbPath);
		server.setLogWriter(null);
		server.setErrWriter(null);
		server.start();
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
		} catch (Exception e) {
			e.printStackTrace();
		}
		JsonReader.begin("src/test/resources/test.json");
		server.stop();
	}
}
