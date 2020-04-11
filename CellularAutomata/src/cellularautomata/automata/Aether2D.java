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

public class Aether2D implements SymmetricLongCellularAutomaton2D {
	
	private static final byte UP = 0;
	private static final byte DOWN = 1;
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;

	/** A 2D array representing the grid */
	private long[][] grid;
	
	private long initialValue;
	private long currentStep;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;

	private int maxY;

	private int maxXMinusOne;
	
	/**
	 * Creates an instance with the given initial value.
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public Aether2D(long initialValue) {
		if (initialValue < 0) {
			BigInteger maxNeighboringValuesDifference = Utils.getAetherMaxNeighboringValuesDifferenceFromSingleSource(2, BigInteger.valueOf(initialValue));
			if (maxNeighboringValuesDifference.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
				throw new IllegalArgumentException("Resulting max value difference between neighboring positions (" + maxNeighboringValuesDifference 
						+ ") exceeds implementation's limit (" + Long.MAX_VALUE + "). Use a greater initial value or a different implementation.");
			}
		}
		this.initialValue = initialValue;
		grid = new long[3][];
		grid[0] = buildGridSlice(0);
		grid[1] = buildGridSlice(1);
		grid[2] = buildGridSlice(2);
		grid[0][0] = initialValue;
		maxY = 0;
		boundsReached = false;
		currentStep = 0;
	}
	
	public boolean nextStep(){
		long[][] newGrid = null;
		if (boundsReached) {
			boundsReached = false;
			newGrid = new long[grid.length + 1][];
		} else {
			newGrid = new long[grid.length][];
		}
		maxXMinusOne = newGrid.length - 2;
		boolean changed = false;
		newGrid[0] = buildGridSlice(0);
		boolean isFirst = true;
		long[] neighborValues = new long[4];
		byte[] neighborDirections = new byte[4];
		for (int x = 0, nextX = 1; x < grid.length; x++, nextX++, isFirst = false) {
			if (nextX < newGrid.length) {
				newGrid[nextX] = buildGridSlice(nextX);
			}
			for (int y = 0; y <= x; y++) {
				long value = this.grid[x][y];
				int relevantNeighborCount = 0;
				long neighborValue;
				neighborValue = getValueAtPosition(x + 1, y);
				if (neighborValue < value) {
					neighborValues[relevantNeighborCount] = neighborValue;
					neighborDirections[relevantNeighborCount] = RIGHT;
					relevantNeighborCount++;
				}
				neighborValue = getValueAtPosition(x - 1, y);
				if (neighborValue < value) {
					neighborValues[relevantNeighborCount] = neighborValue;
					neighborDirections[relevantNeighborCount] = LEFT;
					relevantNeighborCount++;
				}
				neighborValue = getValueAtPosition(x, y + 1);
				if (neighborValue < value) {
					neighborValues[relevantNeighborCount] = neighborValue;
					neighborDirections[relevantNeighborCount] = UP;
					relevantNeighborCount++;
				}
				neighborValue = getValueAtPosition(x, y - 1);
				if (neighborValue < value) {
					neighborValues[relevantNeighborCount] = neighborValue;
					neighborDirections[relevantNeighborCount] = DOWN;
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
									addToNeighbor(newGrid, x, y, neighborDirections[j], share);
								}
							}
							previousNeighborValue = neighborValue;
						}
					}	
				}
				newGrid[x][y] += value;
			}
			if (!isFirst) {
				grid[x-1] = null;
			}
		}
		grid = newGrid;
		currentStep++;
		return changed;
	}
	
	private void addToNeighbor(long grid[][], int x, int y, byte direction, long value) {
		switch(direction) {
		case RIGHT:
			addRight(grid, x, y, value);
			break;
		case LEFT:
			addLeft(grid, x, y, value);
			break;
		case UP:
			addUp(grid, x, y, value);
			break;
		case DOWN:
			addDown(grid, x, y, value);
			break;
		}
	}
	
	private long[] buildGridSlice(int x) {
		long[] newGridSlice = new long[x + 1];
		return newGridSlice;
	}
	
	private void addRight(long[][] grid, int x, int y, long value) {
		grid[x+1][y] += value;
		if (x >= maxXMinusOne) {
			boundsReached = true;
		}
	}
	
	private void addLeft(long[][] grid, int x, int y, long value) {
		if (x > y) {
			long valueToAdd = value;
			if (x == y + 1) {
				valueToAdd += value;
				if (x == 1) {
					valueToAdd += 2*value;							
				}
			}
			grid[x-1][y] += valueToAdd;
		}
		if (x >= maxXMinusOne) {
			boundsReached = true;
		}
	}
	
	private void addUp(long[][] grid, int x, int y, long value) {
		if (y < x) {
			long valueToAdd = value;
			if (y == x - 1) {
				valueToAdd += value;
			}
			int yy = y+1;
			grid[x][yy] += valueToAdd;
			if (yy > maxY)
				maxY = yy;
		}
	}
	
	private void addDown(long[][] grid, int x, int y, long value) {
		if (y > 0) {
			long valueToAdd = value;
			if (y == 1) {
				valueToAdd += value;
			}
			grid[x][y-1] += valueToAdd;
		}
	}
	
	public long getValueAtPosition(int x, int y){	
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		if (y > x) {
			int swp = y;
			y = x;
			x = swp;
		}
		if (x < grid.length 
				&& y < grid[x].length) {
			return grid[x][y];
		} else {
			return 0;
		}
	}
	
	@Override
	public long getValueAtAsymmetricPosition(int x, int y){	
		return grid[x][y];
	}
	
	@Override
	public int getAsymmetricMinX() {
		return 0;
	}

	@Override
	public int getAsymmetricMaxX() {
		return grid.length - 1;
	}
	
	@Override
	public int getAsymmetricMinY() {
		return 0;
	}
	
	@Override
	public int getAsymmetricMaxY() {
		return maxY;
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
	public int getMinX() {
		return -getAsymmetricMaxX();
	}

	@Override
	public int getMaxX() {
		return getAsymmetricMaxX();
	}

	@Override
	public int getMinY() {
		return -getAsymmetricMaxX();
	}

	@Override
	public int getMaxY() {
		return getAsymmetricMaxX();
	}

	@Override
	public long getStep() {
		return currentStep;
	}

	@Override
	public String getName() {
		return "Aether2D";
	}

	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialValue;
	}

	@Override
	public int getAsymmetricMinX(int y) {
		return y;
	}

	@Override
	public int getAsymmetricMaxX(int y) {
		int maxX = getAsymmetricMaxX();
		while (getValueAtAsymmetricPosition(maxX, y) == 0 && maxX >= 0) {
			maxX--;
		}
		return maxX;
	}

	@Override
	public int getAsymmetricMinY(int x) {
		return 0;
	}

	@Override
	public int getAsymmetricMaxY(int x) {
		return Math.min(getAsymmetricMaxY(), x);
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}
}
