package JsonReader;

import java.util.Objects;

/** 
 * Event Data object for each row in .json file
 * @author muhammadrahim
 *
 */
public class EventData {
	private String id;
	private String state;
	private String type;
	private String host;
	private long timestamp;
	private long timeTaken;
	private boolean alert;
	

	public EventData(String id, String state, String type, String host, long timestamp) {
		super();
		this.id = id;
		this.state = state;
		this.type = type;
		this.host = host;
		this.timestamp = timestamp;
		this.alert = false;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public boolean isAlert() {
		return alert;
	}
	
	public void setAlert(boolean alert) {
		this.alert = alert;
	}
	
	public long getTimeTaken() {
		return timeTaken;
	}

	public void setTimeTaken(long timeTaken) {
		this.timeTaken = timeTaken;
	}

	@Override
	public String toString() {
		return "ID: " + this.getId() + ", State: " + this.getState() + ", Type: " + this.getType() + ", Host: " + this.getHost()
		+ ", Timestamp: " + this.getTimestamp() + ", Alert Status: " + (Objects.isNull(this.isAlert()) ? "no data" : this.isAlert())+ ", Duration: " + (Objects.isNull(this.getTimeTaken()) ? "no data" : this.getTimeTaken());
	}
}
