
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;

public class Aether3D extends SymmetricLongCellularAutomaton3D {
	
	private static final byte UP = 0;
	private static final byte DOWN = 1;
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;
	private static final byte FRONT = 4;
	private static final byte BACK = 5;

	/** A 3D array representing the grid */
	private long[][][] grid;
	
	private long initialValue;
	private long currentStep;
	
	private int maxY;
	private int maxZ;

	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;

	private int maxXMinusOne;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public Aether3D(long initialValue) {
		if (initialValue < 0) {
			BigInteger maxValue = BigInteger.valueOf(initialValue).add(
					BigInteger.valueOf(initialValue).negate().divide(BigInteger.valueOf(2)).multiply(BigInteger.valueOf(6)));
			if (maxValue.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
				throw new IllegalArgumentException("Resulting max value " + maxValue 
						+ " exceeds implementation's limit (" + Long.MAX_VALUE + ").");
			}
		}
		this.initialValue = initialValue;
		grid = new long[3][][];
		grid[0] = buildGridSlice(0);
		grid[1] = buildGridSlice(1);
		grid[2] = buildGridSlice(2);
		grid[0][0][0] = this.initialValue;
		maxY = 0;
		maxZ = 0;
		boundsReached = false;
		currentStep = 0;
	}
	
	/**
	 * Creates an instance restoring a backup
	 * 
	 * @param backupPath the path to the backup file to restore.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 */
	public Aether3D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		CustomSymmetricLongCA3DData data = (CustomSymmetricLongCA3DData) Utils.deserializeFromFile(backupPath);
		if (data.getBackgroundValue() != 0)
			throw new UnsupportedOperationException(
					"Only background value 0 is suported. Subtract background value from all grid to get same relative values with background value 0");
		initialValue = data.getInitialValue();
		grid = data.getGrid();
		maxY = data.getMaxY();
		maxZ = data.getMaxZ();
		boundsReached = data.isBoundsReached();
		currentStep = data.getStep();
	}
	
	/**
	 * Computes the next step of the algorithm and returns whether
	 * or not the state of the grid changed. 
	 *  
	 * @return true if the state of the grid changed or false otherwise
	 */
	public boolean nextStep(){
		long[][][] newGrid = null;
		if (boundsReached) {
			boundsReached = false;
			newGrid = new long[grid.length + 1][][];
		} else {
			newGrid = new long[grid.length][][];
		}
		maxXMinusOne = newGrid.length - 2;
		boolean changed = false;
		newGrid[0] = buildGridSlice(0);
		boolean first = true;
		long[] neighborValues = new long[6];
		byte[] neighborDirections = new byte[6];
		for (int x = 0, nextX = 1; x < grid.length; x++, nextX++, first = false) {
			if (nextX < newGrid.length) {
				newGrid[nextX] = buildGridSlice(nextX);
			}
			for (int y = 0; y <= x; y++) {
				for (int z = 0; z <= y; z++) {
					long value = grid[x][y][z];
					int relevantNeighborCount = 0;
					long neighborValue;
					neighborValue = getValue(x + 1, y, z);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = RIGHT;
						relevantNeighborCount++;
					}
					neighborValue = getValue(x - 1, y, z);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = LEFT;
						relevantNeighborCount++;
					}
					neighborValue = getValue(x, y + 1, z);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = UP;
						relevantNeighborCount++;
					}
					neighborValue = getValue(x, y - 1, z);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = DOWN;
						relevantNeighborCount++;
					}
					neighborValue = getValue(x, y, z + 1);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = FRONT;
						relevantNeighborCount++;
					}
					neighborValue = getValue(x, y, z - 1);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = BACK;
						relevantNeighborCount++;
					}
					
					if (relevantNeighborCount > 0) {
						//sort
						boolean sorted = false;
						while (!sorted) {
							sorted = true;
							for (int i = relevantNeighborCount - 2; i >= 0; i--) {
								if (neighborValues[i] < neighborValues[i+1]) {
									sorted = false;
									long valSwap = neighborValues[i];
									neighborValues[i] = neighborValues[i+1];
									neighborValues[i+1] = valSwap;
									byte dirSwap = neighborDirections[i];
									neighborDirections[i] = neighborDirections[i+1];
									neighborDirections[i+1] = dirSwap;
								}
							}
						}
						//divide
						boolean isFirstNeighbor = true;
						long previousNeighborValue = 0;
						for (int i = 0; i < relevantNeighborCount; i++,isFirstNeighbor = false) {
							neighborValue = neighborValues[i];
							if (neighborValue != previousNeighborValue || isFirstNeighbor) {
								int shareCount = relevantNeighborCount - i + 1;
								long toShare = value - neighborValue;
								long share = toShare/shareCount;
								if (share != 0) {
									changed = true;
									value = value - toShare + toShare%shareCount + share;
									for (int j = i; j < relevantNeighborCount; j++) {
										addToNeighbor(newGrid, x, y, z, neighborDirections[j], share);
									}
								}
								previousNeighborValue = neighborValue;
							}
						}	
					}					
					newGrid[x][y][z] += value;
				}
			}
			if (!first) {
				grid[x-1] = null;
			}
		}
		grid = newGrid;
		currentStep++;
		return changed;
	}
	
	private void addToNeighbor(long grid[][][], int x, int y, int z, byte direction, long value) {
		switch(direction) {
		case RIGHT:
			addRight(grid, x, y, z, value);
			break;
		case LEFT:
			addLeft(grid, x, y, z, value);
			break;
		case UP:
			addUp(grid, x, y, z, value);
			break;
		case DOWN:
			addDown(grid, x, y, z, value);
			break;
		case FRONT:
			addFront(grid, x, y, z, value);
			break;
		case BACK:
			addBack(grid, x, y, z, value);
			break;
		}
	}
	
	private void addRight(long[][][] grid, int x, int y, int z, long value) {
		grid[x+1][y][z] += value;
		if (x >= maxXMinusOne) {
			boundsReached = true;
		}
	}
	
	private void addLeft(long[][][] grid, int x, int y, int z, long value) {
		if (x > y) {
			long valueToAdd = value;
			if (y == x - 1) {
				valueToAdd += value;
				if (z == y) {
					valueToAdd += value;
					if (x == 1) {
						valueToAdd += 3*value;
					}
				}
			}
			grid[x-1][y][z] += valueToAdd;
		}
		if (x >= maxXMinusOne) {
			boundsReached = true;
		}
	}
	
	private void addUp(long[][][] grid, int x, int y, int z, long value) {
		if (y < x) {
			long valueToAdd = value;
			if (y == x - 1) {
				valueToAdd += value;
			}
			int yy = y+1;
			grid[x][yy][z] += valueToAdd;
			if (yy > maxY)
				maxY = yy;
		}
	}
	
	private void addDown(long[][][] grid, int x, int y, int z, long value) {
		if (y > z) {	
			long valueToAdd = value;
			if (z == y - 1) {
				valueToAdd += value;
				if (y == 1) {
					valueToAdd += 2*value;
				}
			}
			grid[x][y-1][z] += valueToAdd;
		}
	}
	
	private void addFront(long[][][] grid, int x, int y, int z, long value) {
		if (z < y) {
			long valueToAdd = value;
			if (z == y - 1) {
				valueToAdd += value;
				if (x == y) {
					valueToAdd += value;
				}
			}
			int zz = z+1;
			grid[x][y][zz] += valueToAdd;
			if (zz > maxZ)
				maxZ = zz;
		}
	}
	
	private void addBack(long[][][] grid, int x, int y, int z, long value) {
		if (z > 0) {
			long valueToAdd = value;
			if (z == 1) {
				valueToAdd += value;
			}
			grid[x][y][z-1] += valueToAdd;
		}	
	}
	
	private long[][] buildGridSlice(int x) {
		long[][] newGridSlice = new long[x + 1][];
		for (int y = 0; y < newGridSlice.length; y++) {
			newGridSlice[y] = new long[y + 1];
		}
		return newGridSlice;
	}
	
	public long getValue(int x, int y, int z){	
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
		if (x < grid.length) {
			return grid[x][y][z];
		} else {
			return 0;
		}
	}
	
	public long getNonSymmetricValue(int x, int y, int z){	
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
		CustomSymmetricLongCA3DData data = new CustomSymmetricLongCA3DData(grid, initialValue, 0, currentStep, boundsReached, maxY, maxZ);
		Utils.serializeToFile(data, backupPath, backupName);
	}

	@Override
	public long getBackgroundValue() {
		return 0;
	}

	@Override
	public String getName() {
		return "Aether3D";
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