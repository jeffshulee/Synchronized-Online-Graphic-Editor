import java.awt.Color;


/**
 * Representation of a message for updating a sketch
 * Dartmouth CS 10, Winter 2015
 */
public class Message {
	// Instance variables
	// TODO: YOUR CODE HERE 
	String[] tokens;
	String messageRep;
	
	/**
	 * Initializes it from a string representation used for communication
	 * @param msg
	 */
	public Message(String msg) {
		// TODO: YOUR CODE HERE
		tokens = msg.split(" ");
		messageRep = msg; 
	}
	
	/**
	 * Updates the sketch according to the message
	 * This may result in a modification of the message to be passed on
	 */
	public void update(Sketch sketch) {
		// TODO: YOUR CODE HERE
		if (tokens[0].equals("add")) {
			if (tokens[1].equals("ellipse")){
				sketch.doAddEnd(new Ellipse(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]), new Color(Integer.parseInt(tokens[6]))));
				// example string: "add ellipse 10 10 20 20 color"  add shape to end of list and return its index
			}
			
			if (tokens[1].equals("rectangle")){
				sketch.doAddEnd(new Rectangle(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]), new Color(Integer.parseInt(tokens[6]))));
				// example string: "add rectangle 10 10 20 20 color"  add shape to end of list and return its index
			}
			if (tokens[1].equals("segment")){
				sketch.doAddEnd(new Segment(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]), new Color(Integer.parseInt(tokens[6]))));
				// example string: "add segment 10 10 20 20 color"  add shape to end of list and return its index
			}
	
		}

		// "delete idx"
		if (tokens[0].equals("delete")){
			sketch.doDelete(Integer.parseInt(tokens[1]));
			
		}
			
		// "recolor idx c"
		if (tokens[0].equals("recolor"))
			sketch.doRecolor(Integer.parseInt(tokens[1]), new Color(Integer.parseInt(tokens[2])));
			
		//  "move idx x1 y1"
		if (tokens[0].equals("move"))
			sketch.doMoveTo(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
					
	}	

	/**
	 * Converts to a string representation for communication
	 */
	public String toString() {			/// WHAT IS THE PURPOSE OF toString FUNCTION???
		// TODO: YOUR CODE HERE
		return messageRep;
		
	}
}
