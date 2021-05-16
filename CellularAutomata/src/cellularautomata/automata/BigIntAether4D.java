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
import java.io.Serializable;

import cellularautomata.evolvinggrid.SymmetricEvolvingNumberGrid4D;
import cellularautomata.numbers.BigInt;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 4D with a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class BigIntAether4D implements SymmetricEvolvingNumberGrid4D<BigInt>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5853970703675608009L;
	private static final byte W_POSITIVE = 0;
	private static final byte W_NEGATIVE = 1;
	private static final byte X_POSITIVE = 2;
	private static final byte X_NEGATIVE = 3;
	private static final byte Y_POSITIVE = 4;
	private static final byte Y_NEGATIVE = 5;
	private static final byte Z_POSITIVE = 6;
	private static final byte Z_NEGATIVE = 7;

	/** A 4D array representing the grid */
	private BigInt[][][][] grid;
	
	private BigInt initialValue;
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
	public BigIntAether4D(BigInt initialValue) {
		this.initialValue = initialValue;
		grid = Utils.buildAnisotropic4DBigIntArray(3);
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
	public BigIntAether4D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		BigIntAether4D data = (BigIntAether4D) Utils.deserializeFromFile(backupPath);
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
		BigInt[][][][] newGrid = null;
		if (boundsReached) {
			boundsReached = false;
			newGrid = new BigInt[grid.length + 1][][][];
		} else {
			newGrid = new BigInt[grid.length][][][];
		}
		maxWMinusOne = newGrid.length - 2;
		boolean changed = false;
		newGrid[0] = Utils.buildAnisotropic3DBigIntArray(1);
		boolean first = true;
		BigInt[] neighborValues = new BigInt[8];
		byte[] neighborDirections = new byte[8];
		for (int w = 0, nextW = 1; w < grid.length; w++, nextW++, first = false) {
			if (nextW < newGrid.length) {
				newGrid[nextW] = Utils.buildAnisotropic3DBigIntArray(nextW + 1);
			}
			for (int x = 0; x <= w; x++) {
				for (int y = 0; y <= x; y++) {
					for (int z = 0; z <= y; z++) {
						BigInt value = grid[w][x][y][z];
						int relevantNeighborCount = 0;
						BigInt neighborValue;
						neighborValue = getFromPosition(w + 1, x, y, z);
						if (neighborValue.compareTo(value) < 0) {
							neighborValues[relevantNeighborCount] = neighborValue;
							neighborDirections[relevantNeighborCount] = W_POSITIVE;
							relevantNeighborCount++;
						}
						neighborValue = getFromPosition(w - 1, x, y, z);
						if (neighborValue.compareTo(value) < 0) {
							neighborValues[relevantNeighborCount] = neighborValue;
							neighborDirections[relevantNeighborCount] = W_NEGATIVE;
							relevantNeighborCount++;
						}
						neighborValue = getFromPosition(w, x + 1, y, z);
						if (neighborValue.compareTo(value) < 0) {
							neighborValues[relevantNeighborCount] = neighborValue;
							neighborDirections[relevantNeighborCount] = X_POSITIVE;
							relevantNeighborCount++;
						}
						neighborValue = getFromPosition(w, x - 1, y, z);
						if (neighborValue.compareTo(value) < 0) {
							neighborValues[relevantNeighborCount] = neighborValue;
							neighborDirections[relevantNeighborCount] = X_NEGATIVE;
							relevantNeighborCount++;
						}
						neighborValue = getFromPosition(w, x, y + 1, z);
						if (neighborValue.compareTo(value) < 0) {
							neighborValues[relevantNeighborCount] = neighborValue;
							neighborDirections[relevantNeighborCount] = Y_POSITIVE;
							relevantNeighborCount++;
						}
						neighborValue = getFromPosition(w, x, y - 1, z);
						if (neighborValue.compareTo(value) < 0) {
							neighborValues[relevantNeighborCount] = neighborValue;
							neighborDirections[relevantNeighborCount] = Y_NEGATIVE;
							relevantNeighborCount++;
						}
						neighborValue = getFromPosition(w, x, y, z + 1);
						if (neighborValue.compareTo(value) < 0) {
							neighborValues[relevantNeighborCount] = neighborValue;
							neighborDirections[relevantNeighborCount] = Z_POSITIVE;
							relevantNeighborCount++;
						}
						neighborValue = getFromPosition(w, x, y, z - 1);
						if (neighborValue.compareTo(value) < 0) {
							neighborValues[relevantNeighborCount] = neighborValue;
							neighborDirections[relevantNeighborCount] = Z_NEGATIVE;
							relevantNeighborCount++;
						}
						
						if (relevantNeighborCount > 0) {
							//sort							
							Utils.sortNeighborsByValueDesc(relevantNeighborCount, neighborValues, neighborDirections);
							//divide
							boolean isFirstNeighbor = true;
							BigInt previousNeighborValue = null;
							for (int i = 0; i < relevantNeighborCount; i++,isFirstNeighbor = false) {
								neighborValue = neighborValues[i];
								if (!neighborValue.equals(previousNeighborValue) || isFirstNeighbor) {
									int shareCount = relevantNeighborCount - i + 1;
									BigInt toShare = value.subtract(neighborValue);
									BigInt[] shareAndReminder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
									BigInt share = shareAndReminder[0];
									if (!share.equals(BigInt.ZERO)) {
										changed = true;
										value = value.subtract(toShare).add(shareAndReminder[1]).add(share);
										for (int j = i; j < relevantNeighborCount; j++) {
											addToNeighbor(newGrid, w, x, y, z, neighborDirections[j], share);
										}
									}
									previousNeighborValue = neighborValue;
								}
							}	
						}					
						newGrid[w][x][y][z] = newGrid[w][x][y][z].add(value);
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
	
	private void addToNeighbor(BigInt grid[][][][], int w, int x, int y, int z, byte direction, BigInt value) {
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
	
	private void addToWPositive(BigInt[][][][] grid, int w, int x, int y, int z, BigInt value) {
		int ww = w+1;
		grid[ww][x][y][z] = grid[ww][x][y][z].add(value);
		if (w >= maxWMinusOne) {
			boundsReached = true;
		}	
	}
				
	private void addToWNegative(BigInt[][][][] grid, int w, int x, int y, int z, BigInt value) {
		if (w > x) {
			BigInt valueToAdd = value;
			if (w == x + 1) {
				valueToAdd = valueToAdd.add(value);
				if (x == y) {
					valueToAdd = valueToAdd.add(value);
					if (y == z) {
						valueToAdd = valueToAdd.add(value);
						if (w == 1) {
							valueToAdd = valueToAdd.add(value.multiply(4));
						}
					}
				}
			}
			int ww = w-1;
			grid[ww][x][y][z] = grid[ww][x][y][z].add(valueToAdd);
		}
		if (w >= maxWMinusOne) {
			boundsReached = true;
		}
	}

	private void addToXPositive(BigInt[][][][] grid, int w, int x, int y, int z, BigInt value) {
		if (x < w) {
			BigInt valueToAdd = value;
			if (x == w - 1) {
				valueToAdd = valueToAdd.add(value);
			}
			int xx = x+1;
			grid[w][xx][y][z] = grid[w][xx][y][z].add(valueToAdd);
			if (xx > maxX)
				maxX = xx;
		}
	}

	private void addToXNegative(BigInt[][][][] grid, int w, int x, int y, int z, BigInt value) {
		if (x > y) {
			BigInt valueToAdd = value;									
			if (y == x - 1) {
				valueToAdd = valueToAdd.add(value);
				if (y == z) {
					valueToAdd = valueToAdd.add(value);
					if (y == 0) {
						valueToAdd = valueToAdd.add(value.multiply(3));
					}
				}
			}
			int xx = x-1;
			grid[w][xx][y][z] = grid[w][xx][y][z].add(valueToAdd);
		}
	}

	private void addToYPositive(BigInt[][][][] grid, int w, int x, int y, int z, BigInt value) {
		if (y < x) {
			BigInt valueToAdd = value;									
			if (y == x - 1) {
				valueToAdd = valueToAdd.add(value);
				if (w == x) {
					valueToAdd = valueToAdd.add(value);
				}
			}
			int yy = y+1;
			grid[w][x][yy][z] = grid[w][x][yy][z].add(valueToAdd);
			if (yy > maxY)
				maxY = yy;
		}
	}

	private void addToYNegative(BigInt[][][][] grid, int w, int x, int y, int z, BigInt value) {
		if (y > z) {	
			BigInt valueToAdd = value;
			if (z == y - 1) {
				valueToAdd = valueToAdd.add(value);
				if (y == 1) {
					valueToAdd = valueToAdd.add(value.multiply(2));
				}
			}
			int yy = y-1;
			grid[w][x][yy][z] = grid[w][x][yy][z].add(valueToAdd);
		}
	}

	private void addToZPositive(BigInt[][][][] grid, int w, int x, int y, int z, BigInt value) {
		if (z < y) {
			BigInt valueToAdd = value;
			if (z == y - 1) {
				valueToAdd = valueToAdd.add(value);
				if (x == y) {
					valueToAdd = valueToAdd.add(value);
					if (w == x) {
						valueToAdd = valueToAdd.add(value);
					}
				}
			}
			int zz = z+1;
			grid[w][x][y][zz] = grid[w][x][y][zz].add(valueToAdd);
			if (zz > maxZ)
				maxZ = zz;
		}
	}

	private void addToZNegative(BigInt[][][][] grid, int w, int x, int y, int z, BigInt value) {
		if (z > 0) {
			BigInt valueToAdd = value;
			if (z == 1) {
				valueToAdd = valueToAdd.add(value);
			}
			int zz = z-1;
			grid[w][x][y][zz] = grid[w][x][y][zz].add(valueToAdd);
		}
	}
	
	@Override
	public BigInt getFromPosition(int w, int x, int y, int z){	
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
			return BigInt.ZERO;
		}
	}
	
	@Override
	public BigInt getFromAsymmetricPosition(int w, int x, int y, int z){	
		if (w < grid.length 
				&& x < grid[w].length 
				&& y < grid[w][x].length 
				&& z < grid[w][x][y].length) {
			return grid[w][x][y][z];
		} else {
			return BigInt.ZERO;
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
	public int getAsymmetricMinXAtW(int w) {
		return 0;
	}

	@Override
	public int getAsymmetricMaxXAtW(int w) {
		return Math.min(getAsymmetricMaxX(), w);
	}

	@Override
	public int getAsymmetricMinYAtWX(int w, int x) {
		return 0;
	}

	@Override
	public int getAsymmetricMaxYAtWX(int w, int x) {
		return Math.min(getAsymmetricMaxY(), x);
	}

	@Override
	public int getAsymmetricMinZ(int w, int x, int y) {
		return 0;
	}

	@Override
	public int getAsymmetricMaxZ(int w, int x, int y) {
		return Math.min(getAsymmetricMaxZ(), y);
	}
	
	@Override
	public int getAsymmetricMinYAtW(int w) {
		return 0;
	}

	@Override
	public int getAsymmetricMaxYAtW(int w) {
		return Math.min(getAsymmetricMaxY(), w);
	}

	@Override
	public int getAsymmetricMinZAtW(int w) {
		return 0;
	}

	@Override
	public int getAsymmetricMaxZAtW(int w) {
		return Math.min(getAsymmetricMaxZ(), w);
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
	public BigInt getIntialValue() {
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