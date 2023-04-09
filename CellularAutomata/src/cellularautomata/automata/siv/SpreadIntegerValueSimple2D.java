/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2023 Jaume Ribas

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
package cellularautomata.automata.siv;

import java.io.FileNotFoundException;
import java.io.IOException;
import cellularautomata.Utils;
import cellularautomata.model2d.IsotropicSquareModelA;
import cellularautomata.model2d.SymmetricLongModel2D;

/**
 * Simplified implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/SIV-Cellular-Automaton-Definition">Spread Integer Value</a> cellular automaton in 2D, with a single source initial configuration, for review and testing purposes
 * 
 * @author Jaume Ribas
 *
 */
public class SpreadIntegerValueSimple2D implements SymmetricLongModel2D, IsotropicSquareModelA {	

	/** A 2D array representing the grid */
	private long[][] grid;
	
	private final long initialValue;
	private final long backgroundValue;
	private long step;
	
	/** The indexes of the origin within the array */
	private int originIndex;

	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;

	/** Whether or not the state of the model changed between the current and the previous step **/
	private Boolean changed = null;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 * @param backgroundValue the value padding all the grid but the origin at step 0
	 */
	public SpreadIntegerValueSimple2D(long initialValue, long backgroundValue) {
		this.initialValue = initialValue;
		this.backgroundValue = backgroundValue;
		//Create a 2D array to represent the grid. With the initial value at the origin.
		//Make the array of size 5x5 so as to leave a margin of one cell on each side
		int side = 5;
		grid = new long[side][side];
		//The origin will be at the center of the array
		originIndex = (side - 1)/2;
		if (backgroundValue != 0) {
			Utils.fillArray(grid, backgroundValue);
		}
		grid[originIndex][originIndex] = initialValue;
		boundsReached = false;
		//Set the current step to zero
		step = 0;
	}
	
	@Override
	public Boolean nextStep() {
		//Use new array to store the values of the next step
		long[][] newGrid = null;
		//The offset between the indexes of the new and old array
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new long[grid.length + 2][grid.length + 2];
			if (backgroundValue != 0) {
				fillEdges(newGrid, 1, backgroundValue);
			}
			indexOffset = 1;
		} else {
			newGrid = new long[grid.length][grid.length];
		}
		boolean changed = false;
		//For every cell
		for (int i = 0; i < this.grid.length; i++) {
			for (int j = 0; j < this.grid.length; j++) {
				long value = this.grid[i][j];
				if (value != 0) {
					long right;
					long left;
					if (i < grid.length - 1) {
						right = grid[i + 1][j];
						if (i > 0)
							left = grid[i - 1][j];
						else
							left = backgroundValue;
					} else {
						right = backgroundValue;
						left = grid[i - 1][j];
					}
					long up;
					long down;
					if (j < grid[i].length - 1) {
						up = grid[i][j + 1];
						if (j > 0)
							down = grid[i][j - 1];
						else
							down = backgroundValue;
					} else {
						up = backgroundValue;
						down = grid[i][j - 1];
					}
					boolean isUpEqual = value == up, isDownEqual = value == down, 
							isRightEqual = value == right, isLeftEqual = value == left;
					//If the current cell is equal to its neighbors, the algorithm has no effect
					if (!(isUpEqual && isDownEqual && isRightEqual && isLeftEqual)) {
						//Divide its value by 5 (using integer division)
						long share = value/5;
						if (share != 0) {
							//If any share is not zero, the state changes
							changed = true;
							//Add the share and the remainder to the corresponding cell in the new array
							newGrid[i + indexOffset][j + indexOffset] += value%5 + share;
							//Add the share to the neighboring cells
							//If the neighbor's value is equal to the current value, add the share to the current cell instead
							if (isRightEqual)
								newGrid[i + indexOffset][j + indexOffset] += share;
							else
								newGrid[i + indexOffset + 1][j + indexOffset] += share;
							if (isLeftEqual)
								newGrid[i + indexOffset][j + indexOffset] += share;
							else
								newGrid[i + indexOffset - 1][j + indexOffset] += share;
							if (isUpEqual)
								newGrid[i + indexOffset][j + indexOffset] += share;
							else
								newGrid[i + indexOffset][j + indexOffset + 1] += share;
							if (isDownEqual)
								newGrid[i + indexOffset][j + indexOffset] += share;
							else
								newGrid[i + indexOffset][j + indexOffset - 1] += share;
							//Check whether or not we reached the edge of the array
							if (i == 1 || i == this.grid.length - 2 || 
								j == 1 || j == this.grid[0].length - 2) {
								boundsReached = true;
							}
						} else {
							//If the share is zero, just add the value to the corresponding cell in the new array
							newGrid[i + indexOffset][j + indexOffset] += value;
						}
					} else {
						newGrid[i + indexOffset][j + indexOffset] += value;
					}
				}
			}
		}
		//Replace the old array with the new one
		this.grid = newGrid;
		//Update the index of the origin
		originIndex += indexOffset;
		//Increase the current step by one
		step++;
		this.changed = changed;
		//Return whether or not the state of the grid changed
		return changed;
	}

	@Override
	public Boolean isChanged() {
		return changed;
	}
	
	@Override
	public long getFromPosition(int x, int y) {	
		int i = originIndex + x;
		int j = originIndex + y;
		return grid[i][j];
	}
	
	@Override
	public long getFromAsymmetricPosition(int x, int y) {
		return getFromPosition(x, y);
	}
	
	@Override
	public int getAsymmetricMaxX() {
		int arrayMaxX = grid.length - 1 - originIndex;
		int valuesMaxX;
		if (boundsReached) {
			valuesMaxX = arrayMaxX;
		} else {
			valuesMaxX = arrayMaxX - 1;
		}
		return valuesMaxX;
	}
	
	@Override
	public long getStep() {
		return step;
	}
	
	/**
	 * Returns the initial value
	 * 
	 * @return the value at the origin at step 0
	 */
	public long getInitialValue() {
		return initialValue;
	}

	public long getBackgroundValue() {
		return backgroundValue;
	}
	
	private static void fillEdges(long[][] grid, int width, long value) {
		//left
		for (int i = 0; i < width && i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				grid[i][j] = value;
			}
		}
		//right
		for (int i = grid.length - width; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				grid[i][j] = value;
			}
		}
		//down
		for (int i = width; i < grid.length - width; i++) {
			for (int j = 0; j < width && j < grid.length; j++) {
				grid[i][j] = value;
			}
		}
		//up
		for (int i = width; i < grid.length - width; i++) {
			for (int j = grid.length - width; j < grid.length; j++) {
				grid[i][j] = value;
			}
		}
	}

	@Override
	public String getName() {
		return "SpreadIntegerValue";
	}
	
	@Override
	public String getSubfolderPath() {
		return getName() + "/2D/" + initialValue + "/" + backgroundValue;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}
}
