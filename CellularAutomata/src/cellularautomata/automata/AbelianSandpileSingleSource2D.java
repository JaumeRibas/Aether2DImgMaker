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

import cellularautomata.evolvinggrid.SymmetricEvolvingIntGrid2D;

public class AbelianSandpileSingleSource2D implements SymmetricEvolvingIntGrid2D {

	private int[][] grid;
	
	private int initialValue;
	private long currentStep;
	
	private int maxY;

	/** Whether or not the values reached the bounds of the array */
	private boolean xBoundReached;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public AbelianSandpileSingleSource2D(int initialValue) {
		this.initialValue = initialValue;
		grid = new int[3][];
		grid[0] = buildGridSlice(0);
		grid[1] = buildGridSlice(1);
		grid[2] = buildGridSlice(2);
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
		int[][] newGrid = null;
		if (xBoundReached) {
			xBoundReached = false;
			newGrid = new int[grid.length + 1][];
		} else {
			newGrid = new int[grid.length][];
		}
		int maxXMinusOne = newGrid.length - 2;
		boolean changed = false;
		newGrid[0] = buildGridSlice(0);
		boolean isFirst = true;
		for (int x = 0, nextX = 1; x < grid.length; x++, nextX++, isFirst = false) {
			if (nextX < newGrid.length) {
				newGrid[nextX] = buildGridSlice(nextX);
			}
			for (int y = 0; y <= x; y++) {
				int value = grid[x][y];
				if (value >= 4) {
					//I assume that if any position topples the state changes
					changed = true;
					//Add one to the neighboring positions
					//x+
					newGrid[x+1][y] += 1;
					//x-
					if (x > y) {
						int valueToAdd = 1;
						if (x == y + 1) {
							valueToAdd += 1;
							if (x == 1) {
								valueToAdd += 2;							
							}
						}
						newGrid[x-1][y] += valueToAdd;
					}
					//y+
					if (y < x) {
						int valueToAdd = 1;
						if (y == x - 1) {
							valueToAdd += 1;
						}
						int yy = y+1;
						newGrid[x][yy] += valueToAdd;
						if (yy > maxY)
							maxY = yy;
					}
					//y-
					if (y > 0) {
						int valueToAdd = 1;
						if (y == 1) {
							valueToAdd += 1;
						}
						newGrid[x][y-1] += valueToAdd;
					}
					
					if (x >= maxXMinusOne) {
						xBoundReached = true;
					}
					newGrid[x][y] += value - 4;
				} else {
					newGrid[x][y] += value;
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
	
	private int[] buildGridSlice(int x) {
		int[] newGridSlice = new int[x + 1];
		return newGridSlice;
	}
	
	public int getValueAtPosition(int x, int y){	
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
	
	public int getValueAtAsymmetricPosition(int x, int y){	
		return grid[x][y];
	}
	
	public int getAsymmetricMinX() {
		return 0;
	}

	public int getAsymmetricMaxX() {
		return grid.length - 1;
	}
	
	public int getAsymmetricMinY() {
		return 0;
	}
	
	public int getAsymmetricMaxY() {
		return maxY;
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
	public int getInitialValue() {
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
	public String getName() {
		return "AbelianSandpile2D";
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
