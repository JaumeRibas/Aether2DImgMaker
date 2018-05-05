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

public class CustomSymmetricShortCA4DData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4116366437727762248L;
	private short initialValue;
	private short backgroundValue;
	private long step;
	private boolean boundsReached;
	private int maxX;
	private int maxY;
	private int maxZ;
	private short[][][][] grid;
	
	public CustomSymmetricShortCA4DData(short[][][][] grid, short initialValue, short backgroundValue, long step, boolean boundsReached, int maxX, int maxY, int maxZ) {
		this.grid = grid;
		this.initialValue = initialValue;
		this.backgroundValue = backgroundValue;
		this.step = step;
		this.boundsReached = boundsReached;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}
	
	public CustomSymmetricShortCA4DData(CustomSymmetricIntCA4DData data) {
		initialValue = (short) data.getInitialValue();
		backgroundValue = (short) data.getBackgroundValue();
		maxX = data.getMaxX();
		maxY = data.getMaxY();
		maxZ = data.getMaxZ();
		boundsReached = data.isBoundsReached();
		step = data.getStep();
		//convert grid
		int[][][][] intGrid = data.getGrid();
		grid = new short[intGrid.length][][][];
		for (int w = 0; w < intGrid.length; w++) {
			grid[w] = new short[intGrid[w].length][][];
			for (int x = 0; x < intGrid[w].length; x++) {
				grid[w][x] = new short[intGrid[w][x].length][];
				for (int y = 0; y < intGrid[w][x].length; y++) {
					grid[w][x][y] = new short[intGrid[w][x][y].length];
					for (int z = 0; z < intGrid[w][x][y].length; z++) {
						grid[w][x][y][z] = (short)intGrid[w][x][y][z];
					}
					intGrid[w][x][y] = null;
				}
				intGrid[w][x] = null;
			}
			intGrid[w] = null;
		}
	}

	public short getInitialValue() {
		return initialValue;
	}

	public long getStep() {
		return step;
	}

	public short[][][][] getGrid() {
		return grid;
	}
	
	public short getBackgroundValue() {
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
