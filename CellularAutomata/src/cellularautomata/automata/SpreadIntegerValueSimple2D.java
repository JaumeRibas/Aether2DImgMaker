/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2019 Jaume Ribas

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

/**
 * <h1>Cellular automaton that spreads an integer value over a grid.</h1>
 * 
 * <p>
 * Every step, the value of each position is divided between itself and its von Neumann neighbors.
 * This division is an integer division, and its remainder is left at that given position.
 * The values that collide are added up.
 * The algorithm effectively ends once all positions have a value too small to divide.
 * </p>
 * 
 * <h2>2D Example:</h2>
 * 
 * <h3>step 0</h3>
 * <br/>
 * <table>
 * 	<tr><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td></tr>
 * 	<tr><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td></tr>
 * 	<tr><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;32</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td></tr>
 * 	<tr><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td></tr>
 * 	<tr><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td></tr>
 * </table>
 * 
 * <p>We start off with a grid padded with 0 and a single position with value of 32.</p>
 * <br/>
 *
 * <h3>step 1</h3>
 * <br/>
 * <table>
 * 	<tr><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td></tr>
 * 	<tr><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;6</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td></tr>
 * 	<tr><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;6</td><td>&nbsp;&nbsp;&nbsp;8</td><td>&nbsp;&nbsp;&nbsp;6</td><td>&nbsp;&nbsp;&nbsp;0</td></tr>
 * 	<tr><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;6</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td></tr>
 * 	<tr><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td></tr>
 * </table>
 * 
 * <p>
 * We divide the 32 between his position and his neighboring positions (up, down, left, right and center).<br/>
 * &nbsp;32/5 = 6<br/>
 * We place the result at each position, and we add the remaining 2 to the center value.<br/>
 * &nbsp;6+2 = 8</p>
 * <br/>
 * 
 * 
 * <h3>step 2</h3> 
 * <br/>
 * <table>
 * 	<tr><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;1</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td></tr>
 * 	<tr><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;2</td><td>&nbsp;&nbsp;&nbsp;3</td><td>&nbsp;&nbsp;&nbsp;2</td><td>&nbsp;&nbsp;&nbsp;0</td></tr>
 * 	<tr><td>&nbsp;&nbsp;&nbsp;1</td><td>&nbsp;&nbsp;&nbsp;3</td><td>&nbsp;&nbsp;&nbsp;8</td><td>&nbsp;&nbsp;&nbsp;3</td><td>&nbsp;&nbsp;&nbsp;1</td></tr>
 * 	<tr><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;2</td><td>&nbsp;&nbsp;&nbsp;3</td><td>&nbsp;&nbsp;&nbsp;2</td><td>&nbsp;&nbsp;&nbsp;0</td></tr>
 * 	<tr><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;1</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td></tr>
 * </table>
 * 
 * <p>
 * We repeat the process for every position, adding up the values that collide.<br/>
 * The new value at any given position results from adding up the quotient of the division<br/>
 * of its four neighbors, the quotient of its own division and the remainder of its own division.<br/>
 * For example, for the center position:<br/> 
 * &nbsp;4*6/5 + 8/5 + 8%5 = 4 + 1 + 3 = 8<br/><br/>
 * At this step all values, but the center 8, are smaller than 5.
 * </p>
 * <br/>
 * 
 * 
 * <h3>step 3</h3>
 * <br/>
 * <table>
 * 	<tr><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;1</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td></tr>
 * 	<tr><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;2</td><td>&nbsp;&nbsp;&nbsp;4</td><td>&nbsp;&nbsp;&nbsp;2</td><td>&nbsp;&nbsp;&nbsp;0</td></tr>
 * 	<tr><td>&nbsp;&nbsp;&nbsp;1</td><td>&nbsp;&nbsp;&nbsp;4</td><td>&nbsp;&nbsp;&nbsp;4</td><td>&nbsp;&nbsp;&nbsp;4</td><td>&nbsp;&nbsp;&nbsp;1</td></tr>
 * 	<tr><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;2</td><td>&nbsp;&nbsp;&nbsp;4</td><td>&nbsp;&nbsp;&nbsp;2</td><td>&nbsp;&nbsp;&nbsp;0</td></tr>
 * 	<tr><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;1</td><td>&nbsp;&nbsp;&nbsp;0</td><td>&nbsp;&nbsp;&nbsp;0</td></tr>
 * </table>
 * 
 * <p>
 * Finally, we divide the center 8. 
 * We add the resulting 1 to its neighbors and leave the remaining 3 plus a 1 at the center.<br/>
 * Since all positions now have a value smaller than 5, we consider this the last step.
 * </p>
 * 
 * @author Jaume Ribas
 *
 */
public class SpreadIntegerValueSimple2D implements SymmetricLongCellularAutomaton2D {	

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
	
	/**
	 * Computes the next step of the algorithm and returns whether
	 * or not the state of the grid changed. 
	 *  
	 * @return true if the state of the grid changed or false otherwise
	 */
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
					if (x < grid.length - 1)
						right = grid[x + 1][y];
					else
						right = backgroundValue;
					long left;
					if (x > 0)
						left = grid[x - 1][y];
					else
						left = backgroundValue;
					long up;
					if (y < grid[x].length - 1)
						up = grid[x][y + 1];
					else
						up = backgroundValue;
					long down;
					if (y > 0)
						down = grid[x][y - 1];
					else
						down = backgroundValue;
					boolean isUpEqual = value == up, isDownEqual = value == down, 
							isRightEqual = value == right, isLeftEqual = value == left;
					//if the current position is equal to its neighbors the algorithm has no effect
					if (!(isUpEqual && isDownEqual && isRightEqual && isLeftEqual)) {
						//Divide its value by 5 (using integer division)
						long quotient = value/5;
						if (quotient != 0) {
							//I assume that if any quotient is not zero the state changes
							changed = true;
							//Add the quotient and the remainder to the corresponding position in the new array
							newGrid[x + indexOffset][y + indexOffset] += value%5 + quotient;
							//Add the quotient to the neighboring positions
							//if the neighbor's value is equal to the current value, add the quotient to the current position instead
							if (isRightEqual)
								newGrid[x + indexOffset][y + indexOffset] += quotient;
							else
								newGrid[x + indexOffset + 1][y + indexOffset] += quotient;
							if (isLeftEqual)
								newGrid[x + indexOffset][y + indexOffset] += quotient;
							else
								newGrid[x + indexOffset - 1][y + indexOffset] += quotient;
							if (isUpEqual)
								newGrid[x + indexOffset][y + indexOffset] += quotient;
							else
								newGrid[x + indexOffset][y + indexOffset + 1] += quotient;
							if (isDownEqual)
								newGrid[x + indexOffset][y + indexOffset] += quotient;
							else
								newGrid[x + indexOffset][y + indexOffset - 1] += quotient;
							//Check whether or not we reached the edge of the array
							if (x == 1 || x == this.grid.length - 2 || 
								y == 1 || y == this.grid[0].length - 2) {
								boundsReached = true;
							}
						} else {
							//if the quotient is zero, just add the value to the corresponding position in the new array
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
	
	/**
	 * Returns the value at a given position for the current step
	 * 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @return the value at (x,y)
	 */
	public long getValueAtPosition(int x, int y){	
		int arrayX = xOriginIndex + x;
		int arrayY = yOriginIndex + y;
		if (arrayX < 0 || arrayX > grid.length - 1 
				|| arrayY < 0 || arrayY > grid[0].length - 1) {
			//If the entered position is outside the array the value will be zero
			return backgroundValue;
		} else {
			//Note that the positions whose value hasn't been defined have value zero by default
			return grid[arrayX][arrayY];
		}
	}
	
	public long getValueAtNonSymmetricPosition(int x, int y){	
		return getValueAtPosition(x, y);
	}
	
	/**
	 * Returns the smallest x-coordinate of a nonzero value at the current step
	 * 
	 * @return the smallest x of a nonzero value at the current step
	 */
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
	
	/**
	 * Returns the largest x-coordinate of a nonzero value at the current step
	 * 
	 * @return the largest x of a nonzero value at the current step
	 */
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
	
	/**
	 * Returns the smallest y-coordinate of a nonzero value at the current step
	 * 
	 * @return the smallest y of a nonzero value at the current step
	 */
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
	
	/**
	 * Returns the largest y-coordinate of a nonzero value at the current step
	 * 
	 * @return the largest y of a nonzero value at the current step
	 */
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
	public long getInitialValue() {
		return initialValue;
	}

	@Override
	public int getNonSymmetricMinX() {
		return 0;
	}

	@Override
	public int getNonSymmetricMaxX() {
		return getMaxX();
	}

	@Override
	public int getNonSymmetricMinY() {
		return 0;
	}

	@Override
	public int getNonSymmetricMaxY() {
		return getMaxY();
	}

	@Override
	public long getBackgroundValue() {
		return backgroundValue;
	}
	
	public static void padEdges(long[][] grid, int width, long value) {
		//left
		for (int x = 0; x < grid.length && x < width; x++) {
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
		//up
		for (int x = width; x < grid.length - width; x++) {
			for (int y = 0; y < grid[x].length && y < width; y++) {
				grid[x][y] = value;
			}
		}
		//down
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
	public int getNonSymmetricMinX(int y) {
		return y;
	}

	@Override
	public int getNonSymmetricMaxX(int y) {
		return getMaxY();
	}

	@Override
	public int getNonSymmetricMinY(int x) {
		return 0;
	}

	@Override
	public int getNonSymmetricMaxY(int x) {
		return x;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}
}
