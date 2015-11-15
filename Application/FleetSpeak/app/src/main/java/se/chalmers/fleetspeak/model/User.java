package se.chalmers.fleetspeak.model;

import android.location.Location;

/**
 * A class for representing a user.
 * Created by Patrik on 2014-10-07.
 */
public class User {
    public static final long UPDATE_INTERVAL = 3*60*1000;  //3 minutes

    private String name;
    private Location location;

    private boolean muted;
    private int id;

    public User(String name, int id) {
        this.name = name;
        this.id = id;
        location = new Location(getName()+":"+id);
    }

    public User(int id){
        this("", id);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {this.id = id;}

    /**
     * Updates the location of this User.
     * @param latitude The new latitude.
     * @param longitude The new longitude.
     */
    public void updateLocation(double latitude, double longitude) {
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setTime(System.currentTimeMillis());
        //location.setProvider(String); TODO It might be beneficial in the future to store the server which sent the location.
    }

    /**
     * Checks how long it was since this User's location was updated and returns true
     * if it's up to date.
     * @return true if recently updated, false if not.
     */
    public boolean isLocationUpdated() {
        return (System.currentTimeMillis()-location.getTime()) < UPDATE_INTERVAL;
    }

    /**
     * Finds the distance between the location of this User and the given location.
     * Uses the implementation of android.Location for the calculation.
     * @param targetLocation The location to be compared to this user's location.
     * @return The distance in meters.
     */
    public int getDistanceTo(Location targetLocation) {
        return (int)location.distanceTo(targetLocation);
    }

    public void setMuted(boolean b){
        muted = b;
    }

    public boolean getMuted(){
     return  muted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        //if (name != null ? !name.equals(user.name) : user.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + id;
        return result;
    }

    @Override
    public String toString() {
        return "(name= "+ name + ", id=" + id + ")";
    }
}
