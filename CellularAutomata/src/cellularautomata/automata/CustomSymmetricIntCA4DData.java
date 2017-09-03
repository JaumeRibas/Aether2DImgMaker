package cellularautomata.automata;

import java.io.Serializable;

public class CustomSymmetricIntCA4DData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8490202215552559121L;
	private int initialValue;
	private int backgroundValue;
	private long step;
	private boolean boundsReached;
	private int maxX;
	private int maxY;
	private int maxZ;
	private int[][][][] grid;
	
	public CustomSymmetricIntCA4DData(int[][][][] grid, int initialValue, int backgroundValue, long step, boolean boundsReached, int maxX, int maxY, int maxZ) {
		this.grid = grid;
		this.initialValue = initialValue;
		this.backgroundValue = backgroundValue;
		this.step = step;
		this.boundsReached = boundsReached;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}

	public int getInitialValue() {
		return initialValue;
	}

	public long getStep() {
		return step;
	}

	public int[][][][] getGrid() {
		return grid;
	}
	
	public int getBackgroundValue() {
		return backgroundValue;
	}

	public boolean isBoundsReached() {
		return boundsReached;
	}

	public int getMaxX() {
		return maxX;
	}

	public int getMaxY() {
		return maxY;
	}

	public int getMaxZ() {
		return maxZ;
	}
	
}
