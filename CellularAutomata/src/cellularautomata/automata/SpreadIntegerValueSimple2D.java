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

import cellularautomata.evolvinggrid.SymmetricEvolvingLongGrid2D;

/**
 * Simplified implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/SIV-Cellular-Automaton-Definition">Spread Integer Value</a> cellular automaton in 2D, with a single source initial configuration, for review and testing purposes
 * 
 * @author Jaume Ribas
 *
 */
public class SpreadIntegerValueSimple2D implements SymmetricEvolvingLongGrid2D {	

	/** A 2D array representing the grid */
	private long[][] grid;
	
	private long initialValue;
	private long backgroundValue;
	private long currentStep;
	
	/** The indexes of the origin within the array */
	private int xOriginIndex;
	private int yOriginIndex;

	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	
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
		//Make the array of size 5x5 so as to leave a margin of one position on each side
		int side = 5;
		grid = new long[side][side];
		//The origin will be at the center of the array
		xOriginIndex = (side - 1)/2;
		yOriginIndex = xOriginIndex;
		if (backgroundValue != 0) {
			for (int x = 0; x < grid.length; x++) {
				for (int y = 0; y < grid[x].length; y++) {
					grid[x][y] = backgroundValue;
				}
			}
		}
		grid[xOriginIndex][yOriginIndex] = initialValue;
		boundsReached = false;
		//Set the current step to zero
		currentStep = 0;
	}
	
	@Override
	public boolean nextStep(){
		//Use new array to store the values of the next step
		long[][] newGrid = null;
		//The offset between the indexes of the new and old array
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new long[grid.length + 2][grid[0].length + 2];
			if (backgroundValue != 0) {
				padEdges(newGrid, 1, backgroundValue);
			}
			indexOffset = 1;
		} else {
			newGrid = new long[grid.length][grid[0].length];
		}
		boolean changed = false;
		//For every position
		for (int x = 0; x < this.grid.length; x++) {
			for (int y = 0; y < this.grid[0].length; y++) {
				long value = this.grid[x][y];
				if (value != 0) {
					long right;
					long left;
					if (x < grid.length - 1) {
						right = grid[x + 1][y];
						if (x > 0)
							left = grid[x - 1][y];
						else
							left = backgroundValue;
					} else {
						right = backgroundValue;
						left = grid[x - 1][y];
					}
					long up;
					long down;
					if (y < grid[x].length - 1) {
						up = grid[x][y + 1];
						if (y > 0)
							down = grid[x][y - 1];
						else
							down = backgroundValue;
					} else {
						up = backgroundValue;
						down = grid[x][y - 1];
					}
					boolean isUpEqual = value == up, isDownEqual = value == down, 
							isRightEqual = value == right, isLeftEqual = value == left;
					//if the current position is equal to its neighbors the algorithm has no effect
					if (!(isUpEqual && isDownEqual && isRightEqual && isLeftEqual)) {
						//Divide its value by 5 (using integer division)
						long share = value/5;
						if (share != 0) {
							//If any share is not zero the state changes
							changed = true;
							//Add the share and the remainder to the corresponding position in the new array
							newGrid[x + indexOffset][y + indexOffset] += value%5 + share;
							//Add the share to the neighboring positions
							//if the neighbor's value is equal to the current value, add the share to the current position instead
							if (isRightEqual)
								newGrid[x + indexOffset][y + indexOffset] += share;
							else
								newGrid[x + indexOffset + 1][y + indexOffset] += share;
							if (isLeftEqual)
								newGrid[x + indexOffset][y + indexOffset] += share;
							else
								newGrid[x + indexOffset - 1][y + indexOffset] += share;
							if (isUpEqual)
								newGrid[x + indexOffset][y + indexOffset] += share;
							else
								newGrid[x + indexOffset][y + indexOffset + 1] += share;
							if (isDownEqual)
								newGrid[x + indexOffset][y + indexOffset] += share;
							else
								newGrid[x + indexOffset][y + indexOffset - 1] += share;
							//Check whether or not we reached the edge of the array
							if (x == 1 || x == this.grid.length - 2 || 
								y == 1 || y == this.grid[0].length - 2) {
								boundsReached = true;
							}
						} else {
							//if the share is zero, just add the value to the corresponding position in the new array
							newGrid[x + indexOffset][y + indexOffset] += value;
						}
					} else {
						newGrid[x + indexOffset][y + indexOffset] += value;
					}
				}
			}
		}
		//Replace the old array with the new one
		this.grid = newGrid;
		//Update the index of the origin
		xOriginIndex += indexOffset;
		yOriginIndex += indexOffset;
		//Increase the current step by one
		currentStep++;
		//Return whether or not the state of the grid changed
		return changed;
	}
	
	@Override
	public long getFromPosition(int x, int y){	
		int arrayX = xOriginIndex + x;
		int arrayY = yOriginIndex + y;
		if (arrayX < 0 || arrayX > grid.length - 1 
				|| arrayY < 0 || arrayY > grid[0].length - 1) {
			//If the entered position is outside the array the value will be the backgroundValue
			return backgroundValue;
		} else {
			return grid[arrayX][arrayY];
		}
	}
	
	@Override
	public long getFromAsymmetricPosition(int x, int y){	
		return getFromPosition(x, y);
	}
	
	@Override
	public int getMinX() {
		int arrayMinX = - xOriginIndex;
		int valuesMinX;
		if (boundsReached) {
			valuesMinX = arrayMinX;
		} else {
			valuesMinX = arrayMinX + 1;
		}
		return valuesMinX;
	}
	
	@Override
	public int getMaxX() {
		int arrayMaxX = grid.length - 1 - xOriginIndex;
		int valuesMaxX;
		if (boundsReached) {
			valuesMaxX = arrayMaxX;
		} else {
			valuesMaxX = arrayMaxX - 1;
		}
		return valuesMaxX;
	}
	
	@Override
	public int getMinY() {
		int arrayMinY = - yOriginIndex;
		int valuesMinY;
		if (boundsReached) {
			valuesMinY = arrayMinY;
		} else {
			valuesMinY = arrayMinY + 1;
		}
		return valuesMinY;
	}
	
	@Override
	public int getMaxY() {
		int arrayMaxY = grid[0].length - 1 - yOriginIndex;
		int valuesMaxY;
		if (boundsReached) {
			valuesMaxY = arrayMaxY;
		} else {
			valuesMaxY = arrayMaxY - 1;
		}
		return valuesMaxY;
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
	public int getAsymmetricMinX() {
		return 0;
	}

	@Override
	public int getAsymmetricMaxX() {
		return getMaxX();
	}

	@Override
	public int getAsymmetricMinY() {
		return 0;
	}

	@Override
	public int getAsymmetricMaxY() {
		return getMaxY();
	}

	public long getBackgroundValue() {
		return backgroundValue;
	}
	
	public static void padEdges(long[][] grid, int width, long value) {
		//left
		for (int x = 0; x < width && x < grid.length; x++) {
			for (int y = 0; y < grid[x].length; y ++) {
				grid[x][y] = value;
			}
		}
		//right
		for (int x = grid.length - width; x < grid.length; x++) {
			for (int y = 0; y < grid[x].length; y ++) {
				grid[x][y] = value;
			}
		}
		//down
		for (int x = width; x < grid.length - width; x++) {
			for (int y = 0; y < width && y < grid[x].length; y++) {
				grid[x][y] = value;
			}
		}
		//up
		for (int x = width; x < grid.length - width; x++) {
			for (int y = grid[x].length - width; y < grid[x].length; y++) {
				grid[x][y] = value;
			}
		}
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
		return getMaxY();
	}

	@Override
	public int getAsymmetricMinY(int x) {
		return 0;
	}

	@Override
	public int getAsymmetricMaxY(int x) {
		return x;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}
}
