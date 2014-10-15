package se.chalmers.fleetspeak;

/**
 * A class for representing a user.
 * Created by Patrik on 2014-10-07.
 */
public class User {
    private String name;



    private int id;

    public User(String name, int id) {
        this.name = name;
        this.id = id;
    }
    public User(int id){
        this.name = "";
        this.id = id;
    }

    private static int tmpID;   //TODO This is a temporary solution for test purposes.
    public User(String name) {  //TODO This is a temporary solution for test purposes.
        this(name, tmpID++);    //TODO This is a temporary solution for test purposes.
    }                           //TODO This is a temporary solution for test purposes.

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;

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
        return name + ", " + id;
    }
}
