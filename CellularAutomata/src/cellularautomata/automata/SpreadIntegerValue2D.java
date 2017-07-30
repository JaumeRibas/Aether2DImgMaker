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

/**
 * Optimized implementation of the SpreadIntegerValue2D cellular automaton.
 * 
 * @author Jaume
 *
 */
public class SpreadIntegerValue2D extends SymmetricLongCellularAutomaton2D {

	private long[][] grid;
	
	private long initialValue;
	private long currentStep;
	
	private int maxY;

	/** Whether or not the values reached the bounds of the array */
	private boolean xBoundReached;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public SpreadIntegerValue2D(long initialValue) {
		this.initialValue = initialValue;
		grid = new long[2][];
		grid[0] = buildGridBlock(0);
		grid[1] = buildGridBlock(1);
		grid[0][0] = this.initialValue;
		maxY = 0;
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
		long[][] newGrid = null;
		if (xBoundReached) {
			xBoundReached = false;
			newGrid = new long[grid.length + 1][];
		} else {
			newGrid = new long[grid.length][];
		}
		int maxXMinusOne = newGrid.length - 2;
		boolean changed = false;
		newGrid[0] = buildGridBlock(0);
		for (int x = 0; x < grid.length; x++) {
			int nextX = x + 1;
			if (nextX < newGrid.length) {
				newGrid[nextX] = buildGridBlock(nextX);
			}
			for (int y = 0; y <= x; y++) {
				long value = grid[x][y];
				if (value != 0) {
					//Divide its value by 5 (using integer division)
					long quotient = value/5;
					if (quotient != 0) {
						//I assume that if any quotient is not zero the state changes
						changed = true;
						//Add the quotient to the neighboring positions
						//x+
						newGrid[x+1][y] += quotient;
						//x-
						if (x > y) {
							long valueToAdd = quotient;
							if (x == y + 1) {
								valueToAdd += quotient;
								if (x == 1) {
									valueToAdd += 2*quotient;							
								}
							}
							newGrid[x-1][y] += valueToAdd;
						}
						//y+
						if (y < x) {
							long valueToAdd = quotient;
							if (y == x - 1) {
								valueToAdd += quotient;
							}
							int yy = y+1;
							newGrid[x][yy] += valueToAdd;
							if (yy > maxY)
								maxY = yy;
						}
						//y-
						if (y > 0) {
							long valueToAdd = quotient;
							if (y == 1) {
								valueToAdd += quotient;
							}
							newGrid[x][y-1] += valueToAdd;
						}
						
						if (x == maxXMinusOne) {
							xBoundReached = true;
						}								
					}
					newGrid[x][y] += value - 4*quotient;
				}
			}
			grid[x] = null;
		}
		grid = newGrid;
		currentStep++;
		return changed;
	}
	
	private long[] buildGridBlock(int x) {
		long[] newGridBlock = new long[x + 1];
		return newGridBlock;
	}
	
	public long getValueAt(int x, int y){	
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
	
	public long getNonSymmetricValueAt(int x, int y){	
		if (x < grid.length 
				&& y < grid[x].length) {
			return grid[x][y];
		} else {
			return 0;
		}
	}
	
	public int getNonSymmetricMinX() {
		return 0;
	}

	public int getNonSymmetricMaxX() {
		return grid.length - 1;
	}
	
	public int getNonSymmetricMinY() {
		return 0;
	}
	
	public int getNonSymmetricMaxY() {
		return maxY;
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
	public long getIntialValue() {
		return initialValue;
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
}
