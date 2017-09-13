import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Snake extends ArrayList<Dimension>{

	private static final long serialVersionUID = 5427411769867701772L;
	public final Dimension initial;
	public Dir dir;
	public enum Dir{LEFT, RIGHT, UP, DOWN};
	
	/**
	 * Constructs a new snake with initial position (x, y).
	 * @param x
	 * @param y
	 */
	public Snake(int x, int y){
		dir = Dir.UP;
		this.add(new Dimension(x, y));
		initial = new Dimension(x,y);
	}
	
	public int getHeadX(){
		return this.get(0).width;
	}
	
	public int getHeadY(){
		return this.get(0).height;
	}	
	
	/**
	 * Returns the last joint of the snake.
	 * @return
	 */
	public Dimension getLast(){
		return this.get(this.size()-1);
	}
	
	/**
	 * Adds a joint to the end of the snake.
	 */
	public void addJoint(){
		
		Dimension last = getLast();
		
		switch(dir){
		case LEFT:
			this.add(new Dimension(last.width-1, last.height));
			break;
		case RIGHT:
			this.add(new Dimension(last.width+1, last.height));
			break;
		case UP:
			this.add(new Dimension(last.width, last.height+1));
			break;
		case DOWN:
			this.add(new Dimension(last.width, last.height-1));
			break;
		}
	}
	
	/**
	 * Returns the head of the snake.
	 * @return Dimension
	 */
	public Dimension getHeadDim() {
		return this.get(0);
	}
	
	/**
	 * Sets the snake's direction depending on the key event.
	 * @param move
	 */
	public void setDir(int KEY_EVENT){
		switch(KEY_EVENT){
		case KeyEvent.VK_LEFT:
			if(dir != Dir.RIGHT)
				dir = Dir.LEFT;
			break;
		case KeyEvent.VK_RIGHT:
			if(dir != Dir.LEFT)
				dir = Dir.RIGHT;
			break;
		case KeyEvent.VK_UP:
			if(dir != Dir.DOWN)
				dir = Dir.UP;
			break;
		case KeyEvent.VK_DOWN:
			if(dir != Dir.UP)
				dir = Dir.DOWN;
			break;
		}
	}

	/**
	 * Moves the snake by adding to the head and deleting from the back.
	 */
	public void move() {
		
		if(this.size() > 1){
			System.out.println();
		}
		
		Dimension old = (Dimension) this.get(0).clone();
		Dimension head = new Dimension(old.width, old.height);
		
		switch(dir){
		case LEFT:
			head.width--;
			break;
		case RIGHT:
			head.width++;
			break;
		case UP:
			head.height--;
			break;
		case DOWN:
			head.height++;
			break;
		}
		
		this.add(0, head);
		this.remove(this.size()-1);
		
	}
	
	@Override
	public String toString(){
		StringBuilder b = new StringBuilder(2);

		for(int i = 0; i < this.size(); i++){
			Dimension d = this.get(i);
			b.append(String.format("(%d, %d)\t", d.width, d.height));
		}
		
		return b.toString();
	}

	/**
	 * Returns snake to initial state.
	 */
	public void reset(){
		this.clear();
		this.add((Dimension)initial.clone());
	}
}
