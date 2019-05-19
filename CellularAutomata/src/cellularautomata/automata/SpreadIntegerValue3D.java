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

import java.io.FileNotFoundException;
import java.io.IOException;

public class SpreadIntegerValue3D implements SymmetricLongCellularAutomaton3D  {	

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
		grid = new long[2][][];
		grid[0] = buildGridSlice(0);
		grid[1] = buildGridSlice(1);
		grid[0][0][0] = this.initialValue;
		maxY = 0;
		maxZ = 0;
		xBoundReached = false;
		currentStep = 0;
	}
	
	/**
	 * Computes the next step of the algorithm and returns whether
	 * or not the state of the grid changed. 
	 *  
	 * @return true if the state of the grid changed or false otherwise
	 */
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
		newGrid[0] = buildGridSlice(0);
		for (int x = 0; x < grid.length; x++) {
			int nextX = x + 1;
			if (nextX < newGrid.length) {
				newGrid[nextX] = buildGridSlice(nextX);
			}
			for (int y = 0; y <= x; y++) {
				for (int z = 0; z <= y; z++) {
					long value = grid[x][y][z];
					if (value != 0) {
						//Divide its value by 7 (using integer division)
						long quotient = value/7;
						if (quotient != 0) {
							//I assume that if any quotient is not zero the state changes
							changed = true;
							//Add the quotient to the neighboring positions
							boolean yEqualsXMinusOne = y == x - 1;
							boolean zEqualsYMinusOne = z == y - 1;
							//x+
							newGrid[x+1][y][z] += quotient;
							if (x == maxXMinusOne) {
								xBoundReached = true;
							}
							//x-
							if (x > y) {
								long valueToAdd = quotient;
								if (yEqualsXMinusOne) {
									valueToAdd += quotient;
									if (z == y) {
										valueToAdd += quotient;
										if (x == 1) {
											valueToAdd += 3*quotient;
										}
									}
								}
								newGrid[x-1][y][z] += valueToAdd;
							}
							//y+
							if (y < x) {
								long valueToAdd = quotient;
								if (yEqualsXMinusOne) {
									valueToAdd += quotient;
								}
								int yy = y+1;
								newGrid[x][yy][z] += valueToAdd;
								if (yy > maxY)
									maxY = yy;
							}
							//y-
							if (y > z) {	
								long valueToAdd = quotient;
								if (zEqualsYMinusOne) {
									valueToAdd += quotient;
									if (y == 1) {
										valueToAdd += 2*quotient;
									}
								}
								newGrid[x][y-1][z] += valueToAdd;
							}
							//z+
							if (z < y) {
								long valueToAdd = quotient;
								if (zEqualsYMinusOne) {
									valueToAdd += quotient;
									if (x == y) {
										valueToAdd += quotient;
									}
								}
								int zz = z+1;
								newGrid[x][y][zz] += valueToAdd;
								if (zz > maxZ)
									maxZ = zz;
							}
							//z-
							if (z > 0) {
								long valueToAdd = quotient;
								if (z == 1) {
									valueToAdd += quotient;
								}
								newGrid[x][y][z-1] += valueToAdd;
							}								
						}
						newGrid[x][y][z] += value - 6*quotient;
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
	
	private long[][] buildGridSlice(int x) {
		long[][] newGridSlice = new long[x + 1][];
		for (int y = 0; y < newGridSlice.length; y++) {
			newGridSlice[y] = new long[y + 1];
		}
		return newGridSlice;
	}
	
	public long getValueAtPosition(int x, int y, int z){	
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
	
	public long getValueAtNonSymmetricPosition(int x, int y, int z){	
		return grid[x][y][z];
	}
	
	@Override
	public int getNonSymmetricMinX() {
		return 0;
	}

	@Override
	public int getNonSymmetricMaxX() {
		return grid.length - 1;
	}
	
	@Override
	public int getNonSymmetricMinY() {
		return 0;
	}
	
	@Override
	public int getNonSymmetricMaxY() {
		return maxY;
	}
	
	@Override
	public int getNonSymmetricMinZ() {
		return 0;
	}
	
	@Override
	public int getNonSymmetricMaxZ() {
		return maxZ;
	}
	
	@Override
	public int getMinX() {
		return -getNonSymmetricMaxX();
	}

	@Override
	public int getMaxX() {
		return getNonSymmetricMaxX();
	}

	@Override
	public int getMinY() {
		return -getNonSymmetricMaxX();
	}

	@Override
	public int getMaxY() {
		return getNonSymmetricMaxX();
	}
	
	@Override
	public int getMinZ() {
		return -getNonSymmetricMaxX();
	}

	@Override
	public int getMaxZ() {
		return getNonSymmetricMaxX();
	}	
	
	/**
	 * Returns the current step
	 * 
	 * @return the current step
	 */
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
		CustomSymmetricLongCA3DData data = new CustomSymmetricLongCA3DData(grid, initialValue, 0, currentStep, xBoundReached, maxY, maxZ);
		Utils.serializeToFile(data, backupPath, backupName);
	}

	@Override
	public long getBackgroundValue() {
		return 0;
	}

	@Override
	public String getName() {
		return "SpreadIntegerValue3D";
	}
	
	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialValue;
	}
	
	@Override
	public int getNonSymmetricMinXAtY(int y) {
		return y;
	}

	@Override
	public int getNonSymmetricMinXAtZ(int z) {
		return z;
	}

	@Override
	public int getNonSymmetricMinX(int y, int z) {
		return Math.max(y, z);
	}

	@Override
	public int getNonSymmetricMaxXAtY(int y) {
		return getNonSymmetricMaxX();
	}

	@Override
	public int getNonSymmetricMaxXAtZ(int z) {
		return getNonSymmetricMaxX();
	}

	@Override
	public int getNonSymmetricMaxX(int y, int z) {
		return getNonSymmetricMaxX();
	}

	@Override
	public int getNonSymmetricMinYAtX(int x) {
		return 0;
	}

	@Override
	public int getNonSymmetricMinYAtZ(int z) {
		return z;
	}

	@Override
	public int getNonSymmetricMinY(int x, int z) {
		return z;
	}

	@Override
	public int getNonSymmetricMaxYAtX(int x) {
		return Math.min(getNonSymmetricMaxY(), x);
	}

	@Override
	public int getNonSymmetricMaxYAtZ(int z) {
		return getNonSymmetricMaxY();
	}

	@Override
	public int getNonSymmetricMaxY(int x, int z) {
		return Math.min(getNonSymmetricMaxY(), x);
	}

	@Override
	public int getNonSymmetricMinZAtX(int x) {
		return 0;
	}

	@Override
	public int getNonSymmetricMinZAtY(int y) {
		return 0;
	}

	@Override
	public int getNonSymmetricMinZ(int x, int y) {
		return 0;
	}

	@Override
	public int getNonSymmetricMaxZAtX(int x) {
		return Math.min(getNonSymmetricMaxZ(), x);
	}

	@Override
	public int getNonSymmetricMaxZAtY(int y) {
		return Math.min(getNonSymmetricMaxZ(), y);
	}

	@Override
	public int getNonSymmetricMaxZ(int x, int y) {
		return Math.min(getNonSymmetricMaxZ(), y);
	}
}
