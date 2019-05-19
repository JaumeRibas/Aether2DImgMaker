/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2019 Jaume Ribas

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
