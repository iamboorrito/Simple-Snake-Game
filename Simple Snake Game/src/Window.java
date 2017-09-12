import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Stack;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class Window implements KeyListener {

	private JFrame frmSnek;
	private JPanel canvas;
	protected Timer timer;
	protected Tile[][] grid;
	protected Snake snake;
	protected final int COLUMNS = 20, ROWS = 20;
	private final int FRAME_DELAY = 100;
	private Dimension foodTile;
	private boolean foodSetFlag = false;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window window = new Window();
					window.frmSnek.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Window() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSnek = new JFrame();
		frmSnek.setTitle("Snek");
		frmSnek.setBounds(100, 100, 362, 384);
		frmSnek.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		timer = new Timer(FRAME_DELAY, e->{update();});
		frmSnek.getContentPane().setLayout(new BorderLayout(0, 0));
		
		/*
		 * Subclass JPanel to override painting.
		 */
		canvas = new JPanel(){

			private static final long serialVersionUID = 3285469162805856320L;
			
			public void paintComponent(Graphics g){
				super.paintComponent(g);
				
				Graphics2D g2d = (Graphics2D)g;
				
				draw(g2d);
			}
			
		};
		
		// Set double buffered to true for nice graphics updating.
		canvas.setDoubleBuffered(true);
		frmSnek.getContentPane().add(canvas);
		
		// Make sure key inputs are listened for
		frmSnek.addKeyListener(this);
		
		initGame();
	}

	/**
	 * Initializes the game board and snake then starts the timer.
	 */
	protected void initGame() {
		
		grid = new Tile[COLUMNS][ROWS];
		snake = new Snake(grid.length/2, grid[0].length/2);
		//moves = new Stack<Integer>();
		
		// Set all tiles to empty
		for(int i = 0; i < COLUMNS; i++){
			for(int j = 0; j < ROWS; j++){
				grid[i][j] = Tile.EMPTY;
			}
		}
		
		// Set snake tile on board
		grid[snake.getHeadX()][snake.getHeadY()] = Tile.SNAKE;
		
		// Begin game
		timer.start();
	}

	/**
	 * Updates the game board.
	 */
	protected void update() {

		// Clear the snake's tiles
		for(int i = 0; i < snake.size(); i++){
			Dimension d = snake.get(i);
			this.gridSet(Tile.EMPTY, d.width, d.height);
		}
		
		// Process move
		snake.move();
		
		// Check to see if position off grid
		if(snake.getHeadX() < 0 || snake.getHeadY() < 0 || 
		snake.getHeadX() >= COLUMNS || snake.getHeadY() >= ROWS){
			reset();
			return;
		}
		
		HashSet<Dimension> snakeJoints = new HashSet<>(snake.size());

		// Redraw snake
		for(Dimension d : snake){
			this.gridSet(Tile.SNAKE, d.width, d.height);
		}
		
		// Check for food at snake's head and add joint to hash set
		for(int i = 0; i < snake.size(); i++){
			// Check for food tile
			if(snake.get(i).equals(foodTile)){
				snake.addJoint();
				foodSetFlag = false;
				//System.out.println("Got foods");
				break;
			}
			
			Dimension d = snake.get(i);
			
			// If we already found this joint, then self intersection
			// must have occurred.
			if(snakeJoints.contains(d))
				reset();
			else
				snakeJoints.add(d);
		}
		
		// If food found, set new food location
		if(!foodSetFlag)
			setFood();
		
		// Show updated canvas
		canvas.repaint();
	}
	
	/**
	 * Clears snake off the board and resets it to its initial position.
	 */
	private void reset() {
		
		// Clear snake tiles on board
		for(int i = 0; i < snake.size(); i++){
			Dimension d = snake.get(i);
			if(d.width >= 0 && d.height >= 0 && d.width < COLUMNS && d.height < ROWS)
				this.gridSet(Tile.EMPTY, d.width, d.height);
		}
		
		snake.reset();
		grid[snake.getHeadX()][snake.getHeadY()] = Tile.SNAKE;
		canvas.repaint();
	}

	/**
	 * Sets grid[i, j] = tile
	 * @param tile Tile to place
	 * @param i x position
	 * @param j y position
	 */
	protected void gridSet(Tile tile, int i, int j){
		
		if(i >= 0 && j >= 0 && i < COLUMNS && j < ROWS)
			grid[i][j] = tile;
		
	}

	/**
	 * Sets the food on the board.
	 */
	protected void setFood() {
		
		Dimension[] emptyCells = new Dimension[COLUMNS*ROWS];
		
		int k = 0;
		
		for(int i = 0; i < COLUMNS; i++){
			for(int j = 0; j < ROWS; j++){
				if(grid[i][j] == Tile.EMPTY){
					emptyCells[k] = new Dimension(i, j);
					k++;
				}
			}	
		}
		
		Dimension d = emptyCells[(int)(Math.random()*emptyCells.length)];
		
		// If d deleted while trying to set, get new location
		if(d != null){
			grid[d.width][d.height] = Tile.FRUIT;
		}else{
			setFood();
			return;
		}
		
		foodTile = d;
		foodSetFlag = true;
	}

	/**
	 * Draws the state onto the canvas
	 */
	protected void draw(Graphics2D g) {
		
		int width = canvas.getWidth()/COLUMNS;
		int height = canvas.getHeight()/ROWS;
		
		// Maintain aspect ratio
		int size = Math.min(width, height);
		
		for(int i = 0; i < COLUMNS; i++){
			for(int j = 0; j < ROWS; j++){
				switch(grid[i][j]){
				case EMPTY:
					g.setColor(Color.WHITE);
					break;
				case FRUIT:
					g.setColor(Color.RED);
					break;
				case SNAKE:
					g.setColor(Color.BLUE);
					break;
				default:
					break;
				
				}
				
				g.fillRect(i*size, j*size, size, size);
				g.setColor(Color.BLACK);
				g.drawRect(i*size, j*size, size, size);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		
		if(e.getKeyCode() == KeyEvent.VK_SPACE){
			if(timer.isRunning())
				timer.stop();
			else
				timer.start();
		}

		// Set the snake's new direction
		snake.setDir(e.getKeyCode());
		
	}

	@Override
	public void keyReleased(KeyEvent e) {}

}
