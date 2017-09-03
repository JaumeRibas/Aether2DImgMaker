/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017 Jaume Ribas

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

import java.math.BigInteger;

public class IntAether3D extends SymmetricIntCellularAutomaton3D {
	
	private static final byte UP = 0;
	private static final byte DOWN = 1;
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;
	private static final byte FRONT = 4;
	private static final byte BACK = 5;

	/** A 3D array representing the grid */
	private int[][][] grid;
	
	private int initialValue;
	private int backgroundValue;
	private long currentStep;
	
	private int maxY;
	private int maxZ;

	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;

	private int maxXMinusOne;
	private boolean changed;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 * @param backgroundValue the value padding all the grid but the origin at step 0
	 */
	public IntAether3D(int initialValue, int backgroundValue) {
		if (backgroundValue > initialValue) {
			BigInteger maxValue = BigInteger.valueOf(initialValue).add(BigInteger.valueOf(backgroundValue)
					.subtract(BigInteger.valueOf(initialValue)).divide(BigInteger.valueOf(2)).multiply(BigInteger.valueOf(6)));
			if (maxValue.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
				throw new IllegalArgumentException("Resulting max value " + maxValue 
						+ " exceeds implementation's limit (" + Integer.MAX_VALUE 
						+ "). Consider using a different implementation or a smaller backgroundValue/initialValue ratio.");
			}
		}
		this.initialValue = initialValue;
		this.backgroundValue = backgroundValue;
		grid = new int[3][][];
		grid[0] = buildGridBlock(0, backgroundValue);
		grid[1] = buildGridBlock(1, backgroundValue);
		grid[2] = buildGridBlock(2, backgroundValue);
		grid[0][0][0] = this.initialValue;
		maxY = 0;
		maxZ = 0;
		boundsReached = false;
		currentStep = 0;
	}
	
	/**
	 * Creates an instance using the passed data
	 * 
	 * @param data an instance of {@link CustomSymmetricIntCA3DData}
	 */
	public IntAether3D(CustomSymmetricIntCA3DData data) {
		initialValue = data.getInitialValue();
		backgroundValue = data.getBackgroundValue();
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
		int[][][] newGrid = null;
		if (boundsReached) {
			boundsReached = false;
			newGrid = new int[grid.length + 1][][];
		} else {
			newGrid = new int[grid.length][][];
		}
		maxXMinusOne = newGrid.length - 2;
		changed = false;
		newGrid[0] = buildGridBlock(0, 0);
		boolean first = true;
		int[] neighborValues = new int[6];
		byte[] neighborDirections = new byte[6];
		for (int x = 0, nextX = 1; x < grid.length; x++, nextX++, first = false) {
			if (nextX < grid.length) {
				newGrid[nextX] = buildGridBlock(nextX, 0);
			} else if (nextX < newGrid.length) {
				newGrid[nextX] = buildGridBlock(nextX, backgroundValue);
			}
			for (int y = 0; y <= x; y++) {
				for (int z = 0; z <= y; z++) {
					int value = grid[x][y][z];
					if (value != 0) {
						int relevantNeighborCount = 0;
						int neighborValue;
						neighborValue = getValueAt(x + 1, y, z);
						if (neighborValue < value) {
							neighborValues[relevantNeighborCount] = neighborValue;
							neighborDirections[relevantNeighborCount] = RIGHT;
							relevantNeighborCount++;
						}
						neighborValue = getValueAt(x - 1, y, z);
						if (neighborValue < value) {
							neighborValues[relevantNeighborCount] = neighborValue;
							neighborDirections[relevantNeighborCount] = LEFT;
							relevantNeighborCount++;
						}
						neighborValue = getValueAt(x, y + 1, z);
						if (neighborValue < value) {
							neighborValues[relevantNeighborCount] = neighborValue;
							neighborDirections[relevantNeighborCount] = UP;
							relevantNeighborCount++;
						}
						neighborValue = getValueAt(x, y - 1, z);
						if (neighborValue < value) {
							neighborValues[relevantNeighborCount] = neighborValue;
							neighborDirections[relevantNeighborCount] = DOWN;
							relevantNeighborCount++;
						}
						neighborValue = getValueAt(x, y, z + 1);
						if (neighborValue < value) {
							neighborValues[relevantNeighborCount] = neighborValue;
							neighborDirections[relevantNeighborCount] = FRONT;
							relevantNeighborCount++;
						}
						neighborValue = getValueAt(x, y, z - 1);
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
			}
			if (!first) {
				grid[x-1] = null;
			}
		}
		grid = newGrid;
		currentStep++;
		return changed;
	}
	
	private void addToNeighbor(int grid[][][], int x, int y, int z, byte direction, int value) {
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
	
	private void addRight(int[][][] grid, int x, int y, int z, int value) {
		grid[x+1][y][z] += value;
		if (x >= maxXMinusOne) {
			boundsReached = true;
		}
	}
	
	private void addLeft(int[][][] grid, int x, int y, int z, int value) {
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
	
	private void addUp(int[][][] grid, int x, int y, int z, int value) {
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
	
	private void addDown(int[][][] grid, int x, int y, int z, int value) {
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
	
	private void addFront(int[][][] grid, int x, int y, int z, int value) {
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
	
	private void addBack(int[][][] grid, int x, int y, int z, int value) {
		if (z > 0) {
			long valueToAdd = value;
			if (z == 1) {
				valueToAdd += value;
			}
			grid[x][y][z-1] += valueToAdd;
		}	
	}
	
	private int[][] buildGridBlock(int x, int value) {
		int[][] newGridBlock = new int[x + 1][];
		for (int y = 0; y < newGridBlock.length; y++) {
			newGridBlock[y] = new int[y + 1];
		}
		if (value != 0) {
			for (int y = 0; y < newGridBlock.length; y++) {
				for (int z = 0; z < newGridBlock[y].length; z++) {
					newGridBlock[y][z] = value;
				}
			}
		}
		return newGridBlock;
	}
	
	public int getValueAt(int x, int y, int z){	
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
			return backgroundValue;
		}
	}
	
	public int getNonSymmetricValueAt(int x, int y, int z){	
		if (x < grid.length 
				&& y < grid[x].length 
				&& z < grid[x][y].length) {
			return grid[x][y][z];
		} else {
			return backgroundValue;
		}
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
	public long getCurrentStep() {
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
	public CustomSymmetricIntCA3DData getData() {
		return new CustomSymmetricIntCA3DData(grid, initialValue, backgroundValue, currentStep, boundsReached, maxY, maxZ);
	}

	@Override
	public int getBackgroundValue() {
		return backgroundValue;
	}

	@Override
	public String getName() {
		return "Aether3D";
	}
	
	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialValue + "/" + backgroundValue;
	}
}