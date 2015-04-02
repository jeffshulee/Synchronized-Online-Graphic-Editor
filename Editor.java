import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Client-server graphical editor
 * Dartmouth CS 10, Winter 2015
 */

public class Editor extends JFrame {	
	private static String serverIP = "localhost";			// IP address of sketch server
															// "localhost" for your own machine;
															// or ask a friend for their IP address

	private static final int width = 800, height = 800;		// canvas size

	// GUI components
	private JComponent canvas, gui;
	JDialog colorDialog;
	JColorChooser colorChooser;
	JLabel colorL;

	// Current settings on GUI
	private boolean drawing = true;				// adding objects vs. moving/deleting/recoloring them
	private String shapeType = "ellipse"; 			// type of object to add
	private Color color = Color.black;			// current drawing color

	// Drawing state
	private Point point = null;					// initial mouse press for drawing; current position for moving
	private Shape current = null;				// the object currently being drawn (if one is)
	private int selected = -1;					// index of object (if any; -1=none) has been selected for deleting/recoloring
	private boolean dragged = false;			// keep track of whether object was actually moved
	
	// The sketch and communication
	private Sketch sketch;						// holds and handles all the drawn objects
	private EditorCommunicator comm;			// communication with the sketch server

	public Editor() {
        super("Graphical Editor");

        sketch = new Sketch();
        
        // Connect to server
        comm = new EditorCommunicator(serverIP, this);
        comm.start();

        // Helpers to create the canvas and GUI (buttons, etc.)
        setupCanvas();
        setupGUI();

        // Put the buttons and canvas together into the window
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(canvas, BorderLayout.CENTER);
        cp.add(gui, BorderLayout.NORTH);

        // Usual initialization
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

	/**
	 * Creates a panel with all the buttons, etc.
	 */
	private void setupGUI() {
		// Toggle whether drawing or editing
		JToggleButton drawingB = new JToggleButton("drawing", drawing);
		drawingB.addActionListener(new AbstractAction("drawing") {
			public void actionPerformed(ActionEvent e) {
				drawing = !drawing;
				current = null;
			}
		});

		// Select type of shape
		String[] shapes = {"ellipse", "rectangle", "segment"};
		JComboBox shapeB = new JComboBox(shapes);
		shapeB.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				shapeType = (String)((JComboBox)e.getSource()).getSelectedItem();
			}
		});

		// Select drawing/recoloring color
		// Following Oracle example
		JButton chooseColorB = new JButton("choose color");
		colorChooser = new JColorChooser();
		colorDialog = JColorChooser.createDialog(chooseColorB,
				"Pick a Color",
				true,  //modal
				colorChooser,
				new AbstractAction() { 
			public void actionPerformed(ActionEvent e) {
				color = colorChooser.getColor();
				colorL.setBackground(color); 
			} 
		}, //OK button
		null); //no CANCEL button handler
		chooseColorB.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				colorDialog.setVisible(true);
			}
		});
		colorL = new JLabel();
		colorL.setBackground(Color.black);
		colorL.setOpaque(true);
		colorL.setBorder(BorderFactory.createLineBorder(Color.black));
		colorL.setPreferredSize(new Dimension(25, 25));

		// Delete object if it is selected
		JButton deleteB = new JButton("delete");
		deleteB.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				// TODO: YOUR CODE HERE
				if (selected != -1) {
					comm.delete(selected);
					
				}	
				repaint();
			}
		});

		// Recolor object if it is selected
		JButton recolorB = new JButton("recolor");
		recolorB.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				// TODO: YOUR CODE HERE
				
				if (selected != -1) {
					current.color = color;
					comm.recolor(selected, color.getRGB());  	// comm.recolor(int idx, int c)
					
				repaint();
				}
			}
		});
		

		// Put all the stuff into a panel
		gui = new JPanel();
		gui.setLayout(new FlowLayout());
		gui.add(shapeB);
		gui.add(chooseColorB);
		gui.add(colorL);
		gui.add(new JSeparator(SwingConstants.VERTICAL));
		gui.add(drawingB);
		gui.add(deleteB);
		gui.add(recolorB);
	}

	

	private void setupCanvas() {
		canvas = new JComponent() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				// Display the sketch
				// Also display the object currently being drawn in this editor (not yet part of the sketch) 
				// TODO: YOUR CODE HERE
				sketch.draw(g, selected);
				
				if (current!= null){		// only show what is currently being drawn if it's not null
					current.draw(g);
				}

		repaint();		
				
			}
		};
		
		canvas.setPreferredSize(new Dimension(width, height));

		canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				point = event.getPoint();
				// In drawing mode, start a new object;
				// in editing mode, set selected according to which object contains the point
				// TODO: YOUR CODE HERE
				if (drawing == true) {	// in drawing mode
					
					if (shapeType.equals("ellipse"))  {			// note string comparison uses .equals()
						current = new Ellipse(point.x, point.y, point.x, point.y, color);
					}
					if (shapeType.equals("rectangle")) {
						current = new Rectangle(point.x, point.y, point.x, point.y, color);
					}
					if (shapeType.equals("segment")) {
						current = new Segment(point.x, point.y, point.x, point.y, color);
					}
				}
				
				else { // not in drawing mode
					
					selected = sketch.container(point.x, point.y);  // check to see if the point is within the shape and return the shape index
				}

				repaint();
			}

			public void mouseReleased(MouseEvent event) {
				// Pass the update (added object or moved object) on to the server
				// TODO: YOUR CODE HERE
				
				Point newPoint = event.getPoint();
				if (drawing) {
					comm.add(current);
					selected = -1;			// when drawing, you're no longer selecting anything
					current = null;
				}	
				
				else{
					if (selected != -1){
						comm.move(selected, sketch.get(selected).x1, sketch.get(selected).y1);			
					}
				}
				repaint();
			}
		});		

		canvas.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent event) {
				// In drawing mode, update the other corner of the object;
				// in editing mode, move the object by the difference between the current point and the previous one
				// TODO: YOUR CODE HERE
				Point newPoint = event.getPoint();
				if (drawing) {
					current.setCorners(  point.x, point.y, newPoint.x, newPoint.y  );
				}	
				
				else{
					if (selected != -1) {
						sketch.get(selected).moveBy((newPoint.x - point.x), (newPoint.y-point.y));
						point = newPoint; 	// update the previous point to the current point
					}

				}
				repaint();
			}				
		});
	}

	/**
	 * Getter for the sketch instance variable
	 * @return
	 */
	public Sketch getSketch() {
		return sketch;
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Editor();
			}
		});	
	}
}
