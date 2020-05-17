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

import cellularautomata.evolvinggrid.SymmetricEvolvingLongGrid2D;

/**
 * Optimized implementation of the SpreadIntegerValue cellular automaton in 2D.
 * 
 * @author Jaume
 *
 */
public class SpreadIntegerValue2D implements SymmetricEvolvingLongGrid2D {

	private long[][] grid;
	
	private long initialValue;
	private long backgroundValue;
	private long currentStep;
	
	private int maxY;

	/** Whether or not the values reached the bounds of the array */
	private boolean xBoundReached;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 * @param backgroundValue the value padding all the grid but the origin at step 0
	 */
	public SpreadIntegerValue2D(long initialValue, long backgroundValue) {
		this.initialValue = initialValue;
		this.backgroundValue = backgroundValue;
		grid = Utils.buildAnisotropic2DLongArray(3);
		Utils.setAllArrayIndexes(grid, backgroundValue);
		grid[0][0] = this.initialValue;
		maxY = 0;
		xBoundReached = false;
		currentStep = 0;
	}
	
	@Override
	public boolean nextStep(){
		long[][] newGrid = null;
		if (xBoundReached) {
			xBoundReached = false;
			newGrid = new long[grid.length + 1][];
		} else {
			newGrid = new long[grid.length][];
		}
		int maxXMinusOne = newGrid.length - 2;
		boolean changed = false;
		newGrid[0] = new long[1];
		boolean isFirst = true;
		for (int x = 0, nextX = 1; x < grid.length; x++, nextX++, isFirst = false) {
			if (nextX < newGrid.length) {
				newGrid[nextX] = new long[nextX + 1];
				if (nextX >= grid.length) {
					Utils.setAllArrayIndexes(newGrid[nextX], backgroundValue);
				}
			}
			for (int y = 0; y <= x; y++) {
				long value = grid[x][y];
				if (value != 0) {
					long up = getValueAtPosition(x, y + 1);
					long down = getValueAtPosition(x, y - 1); 
					long left = getValueAtPosition(x - 1, y);
					long right = getValueAtPosition(x + 1, y);
					boolean isUpEqual = value == up, isDownEqual = value == down, 
							isRightEqual = value == right, isLeftEqual = value == left;
					//if the current position is equal to its neighbors the algorithm has no effect
					if (!(isUpEqual && isDownEqual && isRightEqual && isLeftEqual)) {
						//Divide its value by 5 (using integer division)
						long share = value/5;
						if (share != 0) {
							//I assume that if any share is not zero the state changes
							changed = true;
							//Add the share to the neighboring positions
							//if the neighbor's value is equal to the current value, add the share to the current position instead
							//x+
							if (isRightEqual)
								newGrid[x][y] += share;
							else
								newGrid[x+1][y] += share;
							//x-
							if (isLeftEqual)
								newGrid[x][y] += share;
							else if (x > y) {
								long valueToAdd = share;
								if (x == y + 1) {
									valueToAdd += share;
									if (x == 1) {
										valueToAdd += 2*share;							
									}
								}
								newGrid[x-1][y] += valueToAdd;
							}
							//y+
							if (isUpEqual)
								newGrid[x][y] += share;
							else if (y < x) {
								long valueToAdd = share;
								if (y == x - 1) {
									valueToAdd += share;
								}
								int yy = y+1;
								newGrid[x][yy] += valueToAdd;
								if (yy > maxY)
									maxY = yy;
							}
							//y-
							if (isDownEqual)
								newGrid[x][y] += share;
							else if (y > 0) {
								long valueToAdd = share;
								if (y == 1) {
									valueToAdd += share;
								}
								newGrid[x][y-1] += valueToAdd;
							}
							
							if (x >= maxXMinusOne) {
								xBoundReached = true;
							}								
						}
						newGrid[x][y] += value - 4*share;
					} else {
						newGrid[x][y] += value;
					}
				}
			}
			if (!isFirst) {
				grid[x-1] = null;
			}
		}
		grid = newGrid;
		currentStep++;
		return changed;
	}
	
	@Override
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
			return backgroundValue;
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

	public long getBackgroundValue() {
		return backgroundValue;
	}

	@Override
	public String getName() {
		return "SpreadIntegerValue2D";
	}
	
	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialValue + "/" + backgroundValue;
	}

	@Override
	public int getAsymmetricMinX(int y) {
		return y;
	}

	@Override
	public int getAsymmetricMaxX(int y) {
		return getAsymmetricMaxX();
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
