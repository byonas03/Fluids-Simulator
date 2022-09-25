import java.awt.Color;
import java.awt.Graphics;

public class Tile {
	public static final int AIR = 0, WATER = 1, BARRIER = 2;
	int type;
	boolean active = true;
	int i, j;
	
	public Tile(int t, int x, int y) {
		type = t;
		i = x;
		j = y;
	}
	
	public void paint(Graphics g) {
		if (type == Tile.AIR)
			g.setColor(Color.WHITE);
		else if (type == Tile.WATER)
			g.setColor(Color.BLUE);
		else if (type == Tile.BARRIER)
			g.setColor(Color.GRAY);
		
		g.fillRect((int)(i * Field.standardYDimension), (int)(j * Field.standardXDimension), (int)Field.standardYDimension, (int)Field.standardXDimension);
	}
}
