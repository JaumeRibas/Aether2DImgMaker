package cellularautomata.automata;

import java.io.Serializable;

public class CustomSymmetricLongCA4DData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8484066745850879912L;
	private long initialValue;
	private long backgroundValue;
	private long step;
	private boolean boundsReached;
	private int maxX;
	private int maxY;
	private int maxZ;
	private long[][][][] grid;
	
	public CustomSymmetricLongCA4DData(long[][][][] grid, long initialValue, long backgroundValue, long step, boolean boundsReached, int maxX, int maxY, int maxZ) {
		this.grid = grid;
		this.initialValue = initialValue;
		this.backgroundValue = backgroundValue;
		this.step = step;
		this.boundsReached = boundsReached;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}

	public long getInitialValue() {
		return initialValue;
	}

	public long getStep() {
		return step;
	}

	public long[][][][] getGrid() {
		return grid;
	}
	
	public long getBackgroundValue() {
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
