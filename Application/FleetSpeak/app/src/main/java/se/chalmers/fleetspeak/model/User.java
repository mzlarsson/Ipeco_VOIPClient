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
        double latitude = 57.716697, longitude = 11.920601;
        switch (id) {
            case 10000: //Karl johan väst   Malmö   55*36'36''N, 13*1'12''E
                latitude = 55.3636;
                longitude = 13.112;
                break;
            case 10001: //John Matrix   Malmö
                latitude = 55.3636;
                longitude = 13.112;
                break;
            case 10003: //Anders Andersson  Borås   57*43'33'' N,  12*52'22.1''E
                latitude = 55.4333;
                longitude = 13.52221;
                break;
            case 10008: //Pac Man   Mölndal 57*36'1''N, 12*5'35.5''E
                latitude = 55.361;
                longitude = 13.5355;
                break;
        }
        location.setLatitude(latitude);
        location.setLongitude(longitude);
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
        if (false) {    //FIXME Temp for demo 24/11
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            location.setTime(System.currentTimeMillis());
            //location.setProvider(String); TODO It might be beneficial in the future to store the server which sent the location.
        }
    }

    /**
     * Checks how long it was since this User's location was updated and returns true
     * if it's up to date.
     * @return true if recently updated, false if not.
     */
    public boolean isLocationUpdated() {
        return true;    //FIXME Temp for demo 24/11
        //return (System.currentTimeMillis()-location.getTime()) < UPDATE_INTERVAL;
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
