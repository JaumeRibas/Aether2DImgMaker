/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2018 Jaume Ribas

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
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
