import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;


/**
 * A rectangle-shaped Shape
 * Dartmouth CS 10, Winter 2015
 */
public class Rectangle extends Shape {
	// TODO: YOUR CODE HERE
	public Rectangle(int x1, int y1, int x2, int y2, Color c) {
		// Infer upper left and lower right
		super(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1,  y2), c);
	}

	public boolean contains(int x, int y) {

		if ( x >= x1 && x<=x2 && y>=y1 && y<=y2) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void setCorners(int x1, int y1, int x2, int y2) {
		// Infer upper left and lower right
		this.x1 = Math.min(x1, x2); this.y1 = Math.min(y1, y2);
		this.x2 = Math.max(x1, x2); this.y2 = Math.max(y1,  y2);
	}
	
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillRect(x1, y1, x2-x1, y2-y1);		// similar to g.drawRect(x,y,w,h) but fills the inside of rectangle too
	}
	
	public void border(Graphics g) {
		((Graphics2D)g).setStroke(dottedStroke);
		g.setColor(Color.blue);					// set color of rectangle to blue for now
		g.drawOval(x1, y1, x2-x1, y2-y1);
	}
	
	public String toString() {
		return "rectangle "+super.toString();
	}
	
}
