package se.chalmers.fleetspeak.sound;

public interface BufferedStream {

	/**
	 * Retrieves array of data with max length nbrOfBytes. May not be null. If there are unsufficient data,
	 * instead the length of the array should be changed. An empty array can be interpreted as total lack of data.
	 * A call to getData() forwards the internal "cursor" of the stream. 
	 * @param nbrOfBytes The number of bytes to retrieve
	 * @return An array of data from this stream
	 */
	public byte[] getData(int nbrOfBytes);
	
}
