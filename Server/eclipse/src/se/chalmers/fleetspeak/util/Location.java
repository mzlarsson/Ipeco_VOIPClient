package se.chalmers.fleetspeak.util;

/**
 * A class for holding a GPS-coordinate.
 *
 * @author Patrik Haar
 */
public class Location {
	
	private double latitude, longitude;
	
	public Location(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	@Override
	public String toString() {
		return "[lat:"+latitude+",long:"+longitude+"]";
	}
}
