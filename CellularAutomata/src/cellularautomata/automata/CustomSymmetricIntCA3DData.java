package cellularautomata.automata;

import java.io.Serializable;

public class CustomSymmetricIntCA3DData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5481938899010602744L;
	private int initialValue;
	private int backgroundValue;
	private long step;
	private boolean boundsReached;
	private int maxY;
	private int maxZ;
	private int[][][] grid;
	
	public CustomSymmetricIntCA3DData(int[][][] grid, int initialValue, int backgroundValue, long step, boolean boundsReached, int maxY, int maxZ) {
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

	public int getInitialValue() {
		return initialValue;
	}
	
	public int getBackgroundValue() {
		return backgroundValue;
	}

	public long getStep() {
		return step;
	}

	public int[][][] getGrid() {
		return grid;
	}
}
