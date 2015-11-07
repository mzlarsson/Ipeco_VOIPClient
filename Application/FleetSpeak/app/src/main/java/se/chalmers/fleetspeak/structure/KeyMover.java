package se.chalmers.fleetspeak.structure;

/**
 * Created by David Gustafsson on 2015-08-11.
 */
public interface KeyMover {
    public void moveLeft();
    public void moveRight();
    public void moveDown();
    public void moveUp();
    public void onEnter();
}
