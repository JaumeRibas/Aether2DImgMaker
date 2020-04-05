/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2020 Jaume Ribas

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

public class IntAether4D implements SymmetricIntCellularAutomaton4D {
	
	private static final byte W_POSITIVE = 0;
	private static final byte W_NEGATIVE = 1;
	private static final byte X_POSITIVE = 2;
	private static final byte X_NEGATIVE = 3;
	private static final byte Y_POSITIVE = 4;
	private static final byte Y_NEGATIVE = 5;
	private static final byte Z_POSITIVE = 6;
	private static final byte Z_NEGATIVE = 7;

	/** A 4D array representing the grid */
	private int[][][][] grid;
	
	private int initialValue;
	private long currentStep;
	private int maxX;
	private int maxY;
	private int maxZ;

	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;

	private int maxWMinusOne;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public IntAether4D(int initialValue) {
		if (initialValue < 0) {
			BigInteger maxNeighboringValuesDifference = Utils.getAetherMaxNeighboringValuesDifferenceFromSingleSource(4, BigInteger.valueOf(initialValue));
			if (maxNeighboringValuesDifference.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
				throw new IllegalArgumentException("Resulting max value difference between neighboring positions (" + maxNeighboringValuesDifference 
						+ ") exceeds implementation's limit (" + Integer.MAX_VALUE + "). Use a greater initial value or a different implementation.");
			}
		}
		this.initialValue = initialValue;
		grid = new int[3][][][];
		grid[0] = buildGridSlice(0);
		grid[1] = buildGridSlice(1);
		grid[2] = buildGridSlice(2);
		grid[0][0][0][0] = this.initialValue;
		maxX = 0;
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
	public IntAether4D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		IntAether4D data = (IntAether4D) Utils.deserializeFromFile(backupPath);
		initialValue = data.initialValue;
		grid = data.grid;
		maxX = data.maxX;
		maxY = data.maxY;
		maxZ = data.maxZ;
		maxWMinusOne = data.maxWMinusOne;
		boundsReached = data.boundsReached;
		currentStep = data.currentStep;
	}
	
	/**
	 * Computes the next step of the algorithm and returns whether
	 * or not the state of the grid changed. 
	 *  
	 * @return true if the state of the grid changed or false otherwise
	 */
	@Override
	public boolean nextStep(){
		int[][][][] newGrid = null;
		if (boundsReached) {
			boundsReached = false;
			newGrid = new int[grid.length + 1][][][];
		} else {
			newGrid = new int[grid.length][][][];
		}
		maxWMinusOne = newGrid.length - 2;
		boolean changed = false;
		newGrid[0] = buildGridSlice(0);
		boolean first = true;
		int[] neighborValues = new int[8];
		byte[] neighborDirections = new byte[8];
		for (int w = 0, nextW = 1; w < grid.length; w++, nextW++, first = false) {
			if (nextW < newGrid.length) {
				newGrid[nextW] = buildGridSlice(nextW);
			}
			for (int x = 0; x <= w; x++) {
				for (int y = 0; y <= x; y++) {
					for (int z = 0; z <= y; z++) {
						int value = grid[w][x][y][z];
						int relevantNeighborCount = 0;
						int neighborValue;
						neighborValue = getValueAtPosition(w + 1, x, y, z);
						if (neighborValue < value) {
							neighborValues[relevantNeighborCount] = neighborValue;
							neighborDirections[relevantNeighborCount] = W_POSITIVE;
							relevantNeighborCount++;
						}
						neighborValue = getValueAtPosition(w - 1, x, y, z);
						if (neighborValue < value) {
							neighborValues[relevantNeighborCount] = neighborValue;
							neighborDirections[relevantNeighborCount] = W_NEGATIVE;
							relevantNeighborCount++;
						}
						neighborValue = getValueAtPosition(w, x + 1, y, z);
						if (neighborValue < value) {
							neighborValues[relevantNeighborCount] = neighborValue;
							neighborDirections[relevantNeighborCount] = X_POSITIVE;
							relevantNeighborCount++;
						}
						neighborValue = getValueAtPosition(w, x - 1, y, z);
						if (neighborValue < value) {
							neighborValues[relevantNeighborCount] = neighborValue;
							neighborDirections[relevantNeighborCount] = X_NEGATIVE;
							relevantNeighborCount++;
						}
						neighborValue = getValueAtPosition(w, x, y + 1, z);
						if (neighborValue < value) {
							neighborValues[relevantNeighborCount] = neighborValue;
							neighborDirections[relevantNeighborCount] = Y_POSITIVE;
							relevantNeighborCount++;
						}
						neighborValue = getValueAtPosition(w, x, y - 1, z);
						if (neighborValue < value) {
							neighborValues[relevantNeighborCount] = neighborValue;
							neighborDirections[relevantNeighborCount] = Y_NEGATIVE;
							relevantNeighborCount++;
						}
						neighborValue = getValueAtPosition(w, x, y, z + 1);
						if (neighborValue < value) {
							neighborValues[relevantNeighborCount] = neighborValue;
							neighborDirections[relevantNeighborCount] = Z_POSITIVE;
							relevantNeighborCount++;
						}
						neighborValue = getValueAtPosition(w, x, y, z - 1);
						if (neighborValue < value) {
							neighborValues[relevantNeighborCount] = neighborValue;
							neighborDirections[relevantNeighborCount] = Z_NEGATIVE;
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
										int valSwap = neighborValues[i];
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
							int previousNeighborValue = 0;
							for (int i = 0; i < relevantNeighborCount; i++,isFirstNeighbor = false) {
								neighborValue = neighborValues[i];
								if (neighborValue != previousNeighborValue || isFirstNeighbor) {
									int shareCount = relevantNeighborCount - i + 1;
									int toShare = value - neighborValue;
									int share = toShare/shareCount;
									if (share != 0) {
										changed = true;
										value = value - toShare + toShare%shareCount + share;
										for (int j = i; j < relevantNeighborCount; j++) {
											addToNeighbor(newGrid, w, x, y, z, neighborDirections[j], share);
										}
									}
									previousNeighborValue = neighborValue;
								}
							}	
						}					
						newGrid[w][x][y][z] += value;
					}
				}
			}
			if (!first) {
				grid[w-1] = null;
			}
		}
		grid = newGrid;
		currentStep++;
		return changed;
	}
	
	private void addToNeighbor(int grid[][][][], int w, int x, int y, int z, byte direction, int value) {
		switch(direction) {
		case W_POSITIVE:
			addToWPositive(grid, w, x, y, z, value);
			break;
		case W_NEGATIVE:
			addToWNegative(grid, w, x, y, z, value);
			break;
		case X_POSITIVE:
			addToXPositive(grid, w, x, y, z, value);
			break;
		case X_NEGATIVE:
			addToXNegative(grid, w, x, y, z, value);
			break;
		case Y_POSITIVE:
			addToYPositive(grid, w, x, y, z, value);
			break;
		case Y_NEGATIVE:
			addToYNegative(grid, w, x, y, z, value);
			break;
		case Z_POSITIVE:
			addToZPositive(grid, w, x, y, z, value);
			break;
		case Z_NEGATIVE:
			addToZNegative(grid, w, x, y, z, value);
			break;
		}
	}
	
	private void addToWPositive(int[][][][] grid, int w, int x, int y, int z, int value) {
		grid[w+1][x][y][z] += value;
		if (w >= maxWMinusOne) {
			boundsReached = true;
		}	
	}
				
	private void addToWNegative(int[][][][] grid, int w, int x, int y, int z, int value) {
		if (w > x) {
			int valueToAdd = value;
			if (w == x + 1) {
				valueToAdd += value;
				if (x == y) {
					valueToAdd += value;
					if (y == z) {
						valueToAdd += value;
						if (w == 1) {
							valueToAdd += 4*value;
						}
					}
				}
			}
			grid[w-1][x][y][z] += valueToAdd;
		}
		if (w >= maxWMinusOne) {
			boundsReached = true;
		}
	}

	private void addToXPositive(int[][][][] grid, int w, int x, int y, int z, int value) {
		if (x < w) {
			int valueToAdd = value;
			if (x == w - 1) {
				valueToAdd += value;
			}
			int xx = x+1;
			grid[w][xx][y][z] += valueToAdd;
			if (xx > maxX)
				maxX = xx;
		}
	}

	private void addToXNegative(int[][][][] grid, int w, int x, int y, int z, int value) {
		if (x > y) {
			int valueToAdd = value;									
			if (y == x - 1) {
				valueToAdd += value;
				if (y == z) {
					valueToAdd += value;
					if (y == 0) {
						valueToAdd += 3*value;
					}
				}
			}
			grid[w][x-1][y][z] += valueToAdd;
		}
	}

	private void addToYPositive(int[][][][] grid, int w, int x, int y, int z, int value) {
		if (y < x) {
			int valueToAdd = value;									
			if (y == x - 1) {
				valueToAdd += value;
				if (w == x) {
					valueToAdd += value;
				}
			}
			int yy = y+1;
			grid[w][x][yy][z] += valueToAdd;
			if (yy > maxY)
				maxY = yy;
		}
	}

	private void addToYNegative(int[][][][] grid, int w, int x, int y, int z, int value) {
		if (y > z) {	
			int valueToAdd = value;
			if (z == y - 1) {
				valueToAdd += value;
				if (y == 1) {
					valueToAdd += 2*value;
				}
			}
			grid[w][x][y-1][z] += valueToAdd;
		}
	}

	private void addToZPositive(int[][][][] grid, int w, int x, int y, int z, int value) {
		if (z < y) {
			int valueToAdd = value;
			if (z == y - 1) {
				valueToAdd += value;
				if (x == y) {
					valueToAdd += value;
					if (w == x) {
						valueToAdd += value;
					}
				}
			}
			int zz = z+1;
			grid[w][x][y][zz] += valueToAdd;
			if (zz > maxZ)
				maxZ = zz;
		}
	}

	private void addToZNegative(int[][][][] grid, int w, int x, int y, int z, int value) {
		if (z > 0) {
			int valueToAdd = value;
			if (z == 1) {
				valueToAdd += value;
			}
			grid[w][x][y][z-1] += valueToAdd;
		}
	}
	
	private int[][][] buildGridSlice(int w) {
		int[][][] newGridSlice = new int[w + 1][][];
		for (int x = 0; x < newGridSlice.length; x++) {
			newGridSlice[x] = new int[x + 1][];
			for (int y = 0; y < newGridSlice[x].length; y++) {
				newGridSlice[x][y] = new int[y + 1];
			}
		}
		return newGridSlice;
	}

	@Override
	public int getValueAtPosition(int w, int x, int y, int z){	
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		if (z < 0) z = -z;
		if (w < 0) w = -w;
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
			if (x > w) {
				sorted = false;
				int swp = x;
				x = w;
				w = swp;
			}
		} while (!sorted);
		if (w < grid.length 
				&& x < grid[w].length 
				&& y < grid[w][x].length 
				&& z < grid[w][x][y].length) {
			return grid[w][x][y][z];
		} else {
			return 0;
		}
	}

	@Override
	public int getValueAtNonsymmetricPosition(int w, int x, int y, int z){	
		if (w < grid.length 
				&& x < grid[w].length 
				&& y < grid[w][x].length 
				&& z < grid[w][x][y].length) {
			return grid[w][x][y][z];
		} else {
			return 0;
		}
	}
	
	@Override
	public int getMinW() {
		return -getNonsymmetricMaxW();
	}

	@Override
	public int getMaxW() {
		return getNonsymmetricMaxW();
	}

	@Override
	public int getMinX() {
		return -getNonsymmetricMaxW();
	}

	@Override
	public int getMaxX() {
		return getNonsymmetricMaxW();
	}

	@Override
	public int getMinY() {
		return -getNonsymmetricMaxW();
	}

	@Override
	public int getMaxY() {
		return getNonsymmetricMaxW();
	}

	@Override
	public int getMinZ() {
		return -getNonsymmetricMaxW();
	}

	@Override
	public int getMaxZ() {
		return getNonsymmetricMaxW();
	}
	
	@Override
	public int getNonsymmetricMinW(int x, int y, int z) {
		return Math.max(Math.max(x, y), z);
	}

	@Override
	public int getNonsymmetricMaxW(int x, int y, int z) {
		return getNonsymmetricMaxW();
	}

	@Override
	public int getNonsymmetricMinW() {
		return 0;
	}

	@Override
	public int getNonsymmetricMaxW() {
		return grid.length - 1;
	}

	@Override
	public int getNonsymmetricMinX() {
		return 0;
	}

	@Override
	public int getNonsymmetricMaxX() {
		return maxX;
	}

	@Override
	public int getNonsymmetricMinY() {
		return 0;
	}

	@Override
	public int getNonsymmetricMaxY() {
		return maxY;
	}

	@Override
	public int getNonsymmetricMinZ() {
		return 0;
	}
	
	@Override
	public int getNonsymmetricMaxZ() {
		return maxZ;
	}

	@Override
	public int getNonsymmetricMinWAtZ(int z) {
		return z;
	}

	@Override
	public int getNonsymmetricMinWAtXZ(int x, int z) {
		return x;
	}

	@Override
	public int getNonsymmetricMinWAtYZ(int y, int z) {
		return y;
	}

	@Override
	public int getNonsymmetricMaxWAtZ(int z) {
		return getNonsymmetricMaxW(); //TODO: check actual value? store all max values?
	}

	@Override
	public int getNonsymmetricMaxWAtXZ(int x, int z) {
		return getNonsymmetricMaxW();
	}

	@Override
	public int getNonsymmetricMaxWAtYZ(int y, int z) {
		return getNonsymmetricMaxW();
	}

	@Override
	public int getNonsymmetricMinXAtZ(int z) {
		return z;
	}

	@Override
	public int getNonsymmetricMinXAtWZ(int w, int z) {
		return z;
	}

	@Override
	public int getNonsymmetricMinXAtYZ(int y, int z) {
		return y;
	}

	@Override
	public int getNonsymmetricMinX(int w, int y, int z) {
		return y;
	}

	@Override
	public int getNonsymmetricMaxXAtZ(int z) {
		return getNonsymmetricMaxX();
	}

	@Override
	public int getNonsymmetricMaxXAtWZ(int w, int z) {
		return Math.min(getNonsymmetricMaxX(), w);
	}

	@Override
	public int getNonsymmetricMaxXAtYZ(int y, int z) {
		return getNonsymmetricMaxX();
	}

	@Override
	public int getNonsymmetricMaxX(int w, int y, int z) {
		return Math.min(getNonsymmetricMaxX(), w);
	}

	@Override
	public int getNonsymmetricMinYAtZ(int z) {
		return z;
	}

	@Override
	public int getNonsymmetricMaxYAtWZ(int w, int z) {
		return Math.min(getNonsymmetricMaxY(), w);
	}

	@Override
	public int getNonsymmetricMinYAtXZ(int x, int z) {
		return z;
	}

	@Override
	public int getNonsymmetricMinY(int w, int x, int z) {
		return z;
	}

	@Override
	public int getNonsymmetricMaxYAtZ(int z) {
		return getNonsymmetricMaxY();
	}

	@Override
	public int getNonsymmetricMinYAtWZ(int w, int z) {
		return z;
	}

	@Override
	public int getNonsymmetricMaxYAtXZ(int x, int z) {
		return Math.min(getNonsymmetricMaxY(), x);
	}

	@Override
	public int getNonsymmetricMaxY(int w, int x, int z) {
		return Math.min(getNonsymmetricMaxY(), x);
	}
	
	/**
	 * Returns the current step
	 * 
	 * @return the current step
	 */
	@Override
	public long getStep() {
		return currentStep;
	}
	
	/**
	 * Returns the initial value
	 * 
	 * @return the value at the origin at step 0
	 */
	public int getIntialValue() {
		return initialValue;
	}
	
	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		Utils.serializeToFile(this, backupPath, backupName);
	}

	@Override
	public String getName() {
		return "Aether4D";
	}
	
	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialValue;
	}
}