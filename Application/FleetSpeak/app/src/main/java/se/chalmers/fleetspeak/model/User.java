package se.chalmers.fleetspeak.model;

/**
 * A class for representing a user.
 * Created by Patrik on 2014-10-07.
 */
public class User {
    private String name;


    private boolean muted;
    private int id;

    public User(String name, int id) {
        this.name = name;
        this.id = id;
    }
    public User(int id){
        this.name = "";
        this.id = id;
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
