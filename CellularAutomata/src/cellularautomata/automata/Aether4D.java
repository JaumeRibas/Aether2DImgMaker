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
import cellularautomata.evolvinggrid.SymmetricEvolvingLongGrid4D;

public class Aether4D implements SymmetricEvolvingLongGrid4D {
	
	private static final byte W_POSITIVE = 0;
	private static final byte W_NEGATIVE = 1;
	private static final byte X_POSITIVE = 2;
	private static final byte X_NEGATIVE = 3;
	private static final byte Y_POSITIVE = 4;
	private static final byte Y_NEGATIVE = 5;
	private static final byte Z_POSITIVE = 6;
	private static final byte Z_NEGATIVE = 7;

	/** A 4D array representing the grid */
	private long[][][][] grid;
	
	private long initialValue;
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
	public Aether4D(long initialValue) {
		if (initialValue < Long.valueOf("-2635249153387078803")) {//to prevent overflow of long type
			throw new IllegalArgumentException("Initial value cannot be smaller than -2,635,249,153,387,078,803. Use a greater initial value or a different implementation.");
		}
		this.initialValue = initialValue;
		grid = Utils.buildAnisotropic4DLongArray(3);
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
	public Aether4D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		Aether4D data = (Aether4D) Utils.deserializeFromFile(backupPath);
		initialValue = data.initialValue;
		grid = data.grid;
		maxX = data.maxX;
		maxY = data.maxY;
		maxZ = data.maxZ;
		maxWMinusOne = data.maxWMinusOne;
		boundsReached = data.boundsReached;
		currentStep = data.currentStep;
	}

	@Override
	public boolean nextStep(){
		long[][][][] newGrid = null;
		if (boundsReached) {
			boundsReached = false;
			newGrid = new long[grid.length + 1][][][];
		} else {
			newGrid = new long[grid.length][][][];
		}
		maxWMinusOne = newGrid.length - 2;
		boolean changed = false;
		newGrid[0] = Utils.buildAnisotropic3DLongArray(1);
		boolean first = true;
		long[] neighborValues = new long[8];
		byte[] neighborDirections = new byte[8];
		for (int w = 0, nextW = 1; w < grid.length; w++, nextW++, first = false) {
			if (nextW < newGrid.length) {
				newGrid[nextW] = Utils.buildAnisotropic3DLongArray(nextW + 1);
			}
			for (int x = 0; x <= w; x++) {
				for (int y = 0; y <= x; y++) {
					for (int z = 0; z <= y; z++) {
						long value = grid[w][x][y][z];
						int relevantNeighborCount = 0;
						long neighborValue;
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
	
	private void addToNeighbor(long grid[][][][], int w, int x, int y, int z, byte direction, long value) {
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
	
	private void addToWPositive(long[][][][] grid, int w, int x, int y, int z, long value) {
		grid[w+1][x][y][z] += value;
		if (w >= maxWMinusOne) {
			boundsReached = true;
		}	
	}
				
	private void addToWNegative(long[][][][] grid, int w, int x, int y, int z, long value) {
		if (w > x) {
			long valueToAdd = value;
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

	private void addToXPositive(long[][][][] grid, int w, int x, int y, int z, long value) {
		if (x < w) {
			long valueToAdd = value;
			if (x == w - 1) {
				valueToAdd += value;
			}
			int xx = x+1;
			grid[w][xx][y][z] += valueToAdd;
			if (xx > maxX)
				maxX = xx;
		}
	}

	private void addToXNegative(long[][][][] grid, int w, int x, int y, int z, long value) {
		if (x > y) {
			long valueToAdd = value;									
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

	private void addToYPositive(long[][][][] grid, int w, int x, int y, int z, long value) {
		if (y < x) {
			long valueToAdd = value;									
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

	private void addToYNegative(long[][][][] grid, int w, int x, int y, int z, long value) {
		if (y > z) {	
			long valueToAdd = value;
			if (z == y - 1) {
				valueToAdd += value;
				if (y == 1) {
					valueToAdd += 2*value;
				}
			}
			grid[w][x][y-1][z] += valueToAdd;
		}
	}

	private void addToZPositive(long[][][][] grid, int w, int x, int y, int z, long value) {
		if (z < y) {
			long valueToAdd = value;
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

	private void addToZNegative(long[][][][] grid, int w, int x, int y, int z, long value) {
		if (z > 0) {
			long valueToAdd = value;
			if (z == 1) {
				valueToAdd += value;
			}
			grid[w][x][y][z-1] += valueToAdd;
		}
	}
	
	@Override
	public long getValueAtPosition(int w, int x, int y, int z){	
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
	public long getValueAtAsymmetricPosition(int w, int x, int y, int z){	
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
		return -getAsymmetricMaxW();
	}

	@Override
	public int getMaxW() {
		return getAsymmetricMaxW();
	}

	@Override
	public int getMinX() {
		return -getAsymmetricMaxW();
	}

	@Override
	public int getMaxX() {
		return getAsymmetricMaxW();
	}

	@Override
	public int getMinY() {
		return -getAsymmetricMaxW();
	}

	@Override
	public int getMaxY() {
		return getAsymmetricMaxW();
	}

	@Override
	public int getMinZ() {
		return -getAsymmetricMaxW();
	}

	@Override
	public int getMaxZ() {
		return getAsymmetricMaxW();
	}
	
	@Override
	public int getAsymmetricMinW() {
		return 0;
	}
	
	@Override
	public int getAsymmetricMinW(int x, int y, int z) {
		return Math.max(Math.max(x, y), z);
	}

	@Override
	public int getAsymmetricMaxW(int x, int y, int z) {
		return getAsymmetricMaxW();
	}
	
	@Override
	public int getAsymmetricMaxW() {
		return grid.length - 1;
	}
	
	@Override
	public int getAsymmetricMinX() {
		return 0;
	}

	@Override
	public int getAsymmetricMaxX() {
		return maxX;
	}
	
	@Override
	public int getAsymmetricMinY() {
		return 0;
	}
	
	@Override
	public int getAsymmetricMaxY() {
		return maxY;
	}
	
	@Override
	public int getAsymmetricMinZ() {
		return 0;
	}
	
	@Override
	public int getAsymmetricMaxZ() {
		return maxZ;
	}

	@Override
	public int getAsymmetricMinWAtZ(int z) {
		return z;
	}

	@Override
	public int getAsymmetricMinWAtXZ(int x, int z) {
		return x;
	}

	@Override
	public int getAsymmetricMinWAtYZ(int y, int z) {
		return y;
	}

	@Override
	public int getAsymmetricMaxWAtZ(int z) {
		return getAsymmetricMaxW(); //TODO: check actual value? store all max values?
	}

	@Override
	public int getAsymmetricMaxWAtXZ(int x, int z) {
		return getAsymmetricMaxW();
	}

	@Override
	public int getAsymmetricMaxWAtYZ(int y, int z) {
		return getAsymmetricMaxW();
	}

	@Override
	public int getAsymmetricMinXAtZ(int z) {
		return z;
	}

	@Override
	public int getAsymmetricMinXAtWZ(int w, int z) {
		return z;
	}

	@Override
	public int getAsymmetricMinXAtYZ(int y, int z) {
		return y;
	}

	@Override
	public int getAsymmetricMinX(int w, int y, int z) {
		return y;
	}

	@Override
	public int getAsymmetricMaxXAtZ(int z) {
		return getAsymmetricMaxX();
	}

	@Override
	public int getAsymmetricMaxXAtWZ(int w, int z) {
		return Math.min(getAsymmetricMaxX(), w);
	}

	@Override
	public int getAsymmetricMaxXAtYZ(int y, int z) {
		return getAsymmetricMaxX();
	}

	@Override
	public int getAsymmetricMaxX(int w, int y, int z) {
		return Math.min(getAsymmetricMaxX(), w);
	}

	@Override
	public int getAsymmetricMinYAtZ(int z) {
		return z;
	}

	@Override
	public int getAsymmetricMaxYAtWZ(int w, int z) {
		return Math.min(getAsymmetricMaxY(), w);
	}

	@Override
	public int getAsymmetricMinYAtXZ(int x, int z) {
		return z;
	}

	@Override
	public int getAsymmetricMinY(int w, int x, int z) {
		return z;
	}

	@Override
	public int getAsymmetricMaxYAtZ(int z) {
		return getAsymmetricMaxY();
	}

	@Override
	public int getAsymmetricMinYAtWZ(int w, int z) {
		return z;
	}

	@Override
	public int getAsymmetricMaxYAtXZ(int x, int z) {
		return Math.min(getAsymmetricMaxY(), x);
	}

	@Override
	public int getAsymmetricMaxY(int w, int x, int z) {
		return Math.min(getAsymmetricMaxY(), x);
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
	public long getIntialValue() {
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