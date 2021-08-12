/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2021 Jaume Ribas

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

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.evolvinggrid.SymmetricEvolvingLongGrid3D;
import cellularautomata.grid3d.IsotropicGrid3DA;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/SIV-Cellular-Automaton-Definition">Spread Integer Value</a> cellular automaton in 3D with a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class SpreadIntegerValue3D implements SymmetricEvolvingLongGrid3D, IsotropicGrid3DA {	

	/** A 3D array representing the grid */
	private long[][][] grid;
	
	private long initialValue;
	private long currentStep;
	
	private int maxY;
	private int maxZ;

	/** Whether or not the values reached the bounds of the array */
	private boolean xBoundReached;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public SpreadIntegerValue3D(long initialValue) {
		this.initialValue = initialValue;
		grid = Utils.buildAnisotropic3DLongArray(2);
		grid[0][0][0] = this.initialValue;
		maxY = 0;
		maxZ = 0;
		xBoundReached = false;
		currentStep = 0;
	}
	
	@Override
	public boolean nextStep(){
		long[][][] newGrid = null;
		if (xBoundReached) {
			xBoundReached = false;
			newGrid = new long[grid.length + 1][][];
		} else {
			newGrid = new long[grid.length][][];
		}
		int maxXMinusOne = newGrid.length - 2;
		boolean changed = false;
		newGrid[0] = Utils.buildAnisotropic2DLongArray(1);
		for (int x = 0; x < grid.length; x++) {
			int nextX = x + 1;
			if (nextX < newGrid.length) {
				newGrid[nextX] = Utils.buildAnisotropic2DLongArray(nextX + 1);
			}
			for (int y = 0; y <= x; y++) {
				for (int z = 0; z <= y; z++) {
					long value = grid[x][y][z];
					if (value != 0) {
						//Divide its value by 7 (using integer division)
						long share = value/7;
						if (share != 0) {
							//If any share is not zero the state changes
							changed = true;
							//Add the share to the neighboring positions
							boolean yEqualsXMinusOne = y == x - 1;
							boolean zEqualsYMinusOne = z == y - 1;
							//x+
							newGrid[x+1][y][z] += share;
							if (x == maxXMinusOne) {
								xBoundReached = true;
							}
							//x-
							if (x > y) {
								long valueToAdd = share;
								if (yEqualsXMinusOne) {
									valueToAdd += share;
									if (z == y) {
										valueToAdd += share;
										if (x == 1) {
											valueToAdd += 3*share;
										}
									}
								}
								newGrid[x-1][y][z] += valueToAdd;
							}
							//y+
							if (y < x) {
								long valueToAdd = share;
								if (yEqualsXMinusOne) {
									valueToAdd += share;
								}
								int yy = y+1;
								newGrid[x][yy][z] += valueToAdd;
								if (yy > maxY)
									maxY = yy;
							}
							//y-
							if (y > z) {	
								long valueToAdd = share;
								if (zEqualsYMinusOne) {
									valueToAdd += share;
									if (y == 1) {
										valueToAdd += 2*share;
									}
								}
								newGrid[x][y-1][z] += valueToAdd;
							}
							//z+
							if (z < y) {
								long valueToAdd = share;
								if (zEqualsYMinusOne) {
									valueToAdd += share;
									if (x == y) {
										valueToAdd += share;
									}
								}
								int zz = z+1;
								newGrid[x][y][zz] += valueToAdd;
								if (zz > maxZ)
									maxZ = zz;
							}
							//z-
							if (z > 0) {
								long valueToAdd = share;
								if (z == 1) {
									valueToAdd += share;
								}
								newGrid[x][y][z-1] += valueToAdd;
							}								
						}
						newGrid[x][y][z] += value - 6*share;
					}
				}
				grid[x][y] = null;
			}
			grid[x] = null;
		}
		grid = newGrid;
		currentStep++;
		return changed;
	}
	
	@Override
	public long getFromPosition(int x, int y, int z){	
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		if (z < 0) z = -z;
		//sort coordinates
		boolean sorted;
		do {
			sorted = true;
			if (z > y) {
				sorted = false;
				int swp = z;
				z = y;
				y = swp;
			}
			if (y > x) {
				sorted = false;
				int swp = y;
				y = x;
				x = swp;
			}
		} while (!sorted);
		if (x < grid.length 
				&& y < grid[x].length 
				&& z < grid[x][y].length) {
			return grid[x][y][z];
		} else {
			return 0;
		}
	}
	
	@Override
	public long getFromAsymmetricPosition(int x, int y, int z){	
		return grid[x][y][z];
	}

	@Override
	public int getAsymmetricMaxX() {
		return grid.length - 1;
	}
	
	@Override
	public int getAsymmetricMaxY() {
		return maxY;
	}
	
	@Override
	public int getAsymmetricMaxZ() {
		return maxZ;
	}
	
	@Override
	public long getStep() {
		return currentStep;
	}
	
	/**
	 * Returns the initial value
	 * 
	 * @return the value at the origin at step 0
	 */
	public long getInitialValue() {
		return initialValue;
	}
	
	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}

	public long getBackgroundValue() {
		return 0;
	}

	@Override
	public String getName() {
		return "SpreadIntegerValue3D";
	}
	
	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialValue + "/0";
	}
}
