package chandana;

import java.util.List;

/**
 * Messages that are passed around the actors are usually immutable classes.
 * Think how you go about creating immutable classes:) Make them all static
 * classes inside the Messages class.
 * 
 * This class should have all the immutable messages that you need to pass
 * around actors. You are free to add more classes(Messages) that you think is
 * necessary
 * 
 * @author bala Krishna
 *
 */
public class Messages {
	
	//Messages defined here

	private Double pathLength;
	private List<Integer> path;
	public Double getPathLength() {
		return pathLength;
	}
	public void setPathLength(Double pathLength) {
		this.pathLength = pathLength;
	}
	public List<Integer> getPath() {
		return path;
	}
	public void setPath(List<Integer> path) {
		this.path = path;
	}
}