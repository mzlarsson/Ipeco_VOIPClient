package se.chalmers.fleetspeak;

/**
 * A class for representing a room.
 * Created by Patrik on 2014-10-07.
 */
public class Room {
    private String name;
    private int id;

    public Room(String name, int id) {
        this.name = name;
        this.id = id;
    }

    private static int tmpID;   //TODO This is a temporary solution for test purposes.
    public Room(String name) {  //TODO This is a temporary solution for test purposes.
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

    public void setId(int id){this.id = id;}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Room room = (Room) o;

        if (id != room.id) return false;
        if (name != null ? !name.equals(room.name) : room.name != null) return false;

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
