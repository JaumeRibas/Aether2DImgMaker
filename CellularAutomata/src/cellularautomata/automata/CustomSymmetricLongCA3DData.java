package cellularautomata.automata;

import java.io.Serializable;

public class CustomSymmetricLongCA3DData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7371404269731885957L;
	private long initialValue;
	private long backgroundValue;
	private long step;
	private boolean boundsReached;
	private int maxY;
	private int maxZ;
	private long[][][] grid;
	
	public CustomSymmetricLongCA3DData(long[][][] grid, long initialValue, long backgroundValue, long step, boolean boundsReached, int maxY, int maxZ) {
		this.grid = grid;
		this.initialValue = initialValue;
		this.backgroundValue = backgroundValue;
		this.step = step;
		this.boundsReached = boundsReached;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}

	public boolean isBoundsReached() {
		return boundsReached;
	}

	public int getMaxY() {
		return maxY;
	}

	public int getMaxZ() {
		return maxZ;
	}

	public long getInitialValue() {
		return initialValue;
	}
	
	public long getBackgroundValue() {
		return backgroundValue;
	}

	public long getStep() {
		return step;
	}

	public long[][][] getGrid() {
		return grid;
	}
}
