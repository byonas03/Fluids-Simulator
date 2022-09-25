import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

public class Field {
	JFrame f = new JFrame("Fluids Simulator");
	public final double WIDTH = Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
			HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	static double standardXDimension, standardYDimension;
	Tile[][] tiles;
	boolean drawWater = false, drawBarrier = false;

	public Field(int dimX, int dimY) {
		f.setVisible(true);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		standardXDimension = dimX;
		standardYDimension = dimY;
		tiles = new Tile[(int) (WIDTH / dimX)][(int) (HEIGHT / dimY)];
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				tiles[i][j] = new Tile(0, i, j);
			}
		}

		f.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				if (me.getButton() == MouseEvent.BUTTON1) {
					drawWater = true;
				} else if (me.getButton() == MouseEvent.BUTTON2) {

				} else if (me.getButton() == MouseEvent.BUTTON3) {
					drawBarrier = true;
				}
			}

			public void mouseReleased(MouseEvent me) {

				if (me.getButton() == MouseEvent.BUTTON1) {
					drawWater = false;
				} else if (me.getButton() == MouseEvent.BUTTON2) {

				} else if (me.getButton() == MouseEvent.BUTTON3) {
					drawBarrier = false;
				}
			}
		});

		paint(f.getGraphics());
		Timer dynamics = new Timer();
		dynamics.schedule(new TimerTask() {
			int xx = 0;

			@Override
			public void run() {
				if (drawWater) {
					double x = MouseInfo.getPointerInfo().getLocation().getX(),
							y = MouseInfo.getPointerInfo().getLocation().getY();
					int i = (int) ((double) (x / 1920) * tiles.length),
							j = (int) ((double) (y / 1080) * tiles[0].length);
					for (int h = i; h < i + 5; h++) {
						for (int g = j; g < j + 5; g++) {
							if (h >= 0 && g >= 0 && h < tiles.length && g < tiles[0].length) {
								tiles[h][g].type = Tile.WATER;
								tiles[h][g].paint(f.getGraphics());
							}
						}
					}
					tiles[i][j].type = Tile.WATER;
				}
				if (drawBarrier) {
					double x = MouseInfo.getPointerInfo().getLocation().getX(),
							y = MouseInfo.getPointerInfo().getLocation().getY();
					int i = (int) ((double) (x / 1920) * tiles.length) + 2,
							j = (int) ((double) (y / 1080) * tiles[0].length) + 2;
					if (i >= 0 && j >= 0 && i < tiles.length && j < tiles[0].length) {
						tiles[i][j].type = Tile.BARRIER;
						tiles[i][j].paint(f.getGraphics());
					}
				}

				for (int i = 0; i < tiles.length; i++) {
					for (int j = 0; j < tiles[i].length; j++) {
						Tile t = tiles[i][j];
						if (t.active == true && t.type == Tile.WATER) {
							if (j + 1 < tiles[0].length && tiles[i][j + 1].type == Tile.AIR) {
								swap(i, j, i, j + 1);
							} else if (j + 1 == tiles[0].length || tiles[i][j + 1].type == Tile.WATER
									|| tiles[i][j + 1].type == Tile.BARRIER) {
								double r = Math.random();
								int d = 1;
								if (tiles[i][j - 1].type == Tile.AIR)
									d = (int) (Math.random() * 10 + 1);
								else if (tiles[i][j - 1].type == Tile.WATER)
									d = waterParticlesAbove(i, j);
								if (r < .5 && i + d < tiles.length && tiles[i + d][j].type == Tile.AIR
										&& tiles[i + 1][j].type == Tile.AIR
										&& !containsType(i, j, i + d, j, Tile.BARRIER)) {
									swap(i, j, i + d, j);
								}
								if (r > .5 && i - d >= 0 && tiles[i - d][j].type == Tile.AIR
										&& tiles[i - 1][j].type == Tile.AIR
										&& !containsType(i, j, i - d, j, Tile.BARRIER)) {
									swap(i, j, i - d, j);
								}
							}
						}
					}
				}
				for (int i = 0; i < tiles.length; i++) {
					for (int j = 0; j < tiles[i].length; j++) {
						tiles[i][j].active = true;
					}
				}
			}
		}, 0, 5);
	}

	public boolean containsType(int i, int j, int a, int b, int type) {
		if (i > a) {
			int s = i;
			i = a;
			a = s;
		}
		if (j > b) {
			int s = j;
			i = b;
			b = s;
		}
		for (int x = i; x <= a; x++)
			for (int y = j; y <= b; y++)
				if (tiles[x][y].type == type)
					return true;
		return false;
	}

	public void swap(int a, int b, int c, int d) {
		int store = tiles[a][b].type;
		tiles[a][b].type = tiles[c][d].type;
		tiles[c][d].type = store;
		tiles[a][b].active = false;
		tiles[c][d].active = false;
		tiles[a][b].paint(f.getGraphics());
		tiles[c][d].paint(f.getGraphics());
	}

	public int waterParticlesAbove(int i, int j) {
		int c = 0;
		for (int x = j; x >= 0; x--) {
			if (tiles[i][x].type == Tile.WATER)
				c++;
		}
		return c;
	}

	public void paint(Graphics g) {
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				if (tiles[i][j].type == Tile.AIR)
					g.setColor(Color.WHITE);
				else if (tiles[i][j].type == Tile.WATER) {
					g.setColor(Color.BLUE);
				} else if (tiles[i][j].type == Tile.BARRIER) {
					g.setColor(Color.GRAY);
				}
				g.fillRect((int) (i * standardXDimension), (int) (j * standardYDimension), (int) (standardXDimension),
						(int) (standardYDimension));
			}
		}
	}

	public static void main(String args[]) {
		Field f = new Field(10, 10);
	}
}
