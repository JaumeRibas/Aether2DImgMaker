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
import java.util.Arrays;

import cellularautomata.model1d.IsotropicModel1DA;
import cellularautomata.model1d.SymmetricLongModel1D;

/**
 * Simplified implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/SIV-Cellular-Automaton-Definition">Spread Integer Value</a> cellular automaton in 1D, with a single source initial configuration, for review and testing purposes
 * 
 * @author Jaume
 *
 */
public class SimpleSpreadIntegerValue1D implements SymmetricLongModel1D, IsotropicModel1DA {	

	/** A 1D array representing the grid */
	private long[] grid;
	
	private final long initialValue;
	private final long backgroundValue;
	private long step;
	
	/** The index of the origin within the array */
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
	public SimpleSpreadIntegerValue1D(long initialValue, long backgroundValue) {
		this.initialValue = initialValue;
		this.backgroundValue = backgroundValue;
		//Create a 1D array to represent the grid. With the initial value at the origin.
		//Make the array of size 5 so as to leave a margin of two cells on each side
		int side = 5;
		grid = new long[side];
		//The origin will be at the center of the array
		originIndex = (side - 1)/2;
		if (backgroundValue != 0) {
			Arrays.fill(grid, backgroundValue);
		}
		grid[originIndex] = initialValue;
		boundsReached = false;
		//Set the current step to zero
		step = 0;
	}
	
	@Override
	public Boolean nextStep() {
		//Use new array to store the values of the next step
		long[] newGrid = null;
		//The offset between the indexes of the new and old array
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new long[grid.length + 2];
			if (backgroundValue != 0) {
				//fill edges
				newGrid[0] = backgroundValue;
				newGrid[newGrid.length - 1] = backgroundValue;
			}
			indexOffset = 1;
		} else {
			newGrid = new long[grid.length];
		}
		boolean changed = false;
		//For every cell
		for (int i = 0; i < this.grid.length; i++) {
			long value = this.grid[i];
			if (value != 0) {
				long right;
				long left;
				if (i < grid.length - 1) {
					right = grid[i + 1];
					if (i > 0)
						left = grid[i - 1];
					else
						left = backgroundValue;
				} else {
					right = backgroundValue;
					left = grid[i - 1];
				}
				boolean isRightEqual = value == right, isLeftEqual = value == left;
				//If the current cell is equal to its neighbors, the algorithm has no effect
				if (!(isRightEqual && isLeftEqual)) {
					//Divide its value by 3 (using integer division)
					long share = value/3;
					if (share != 0) {
						//If any share is not zero, the state changes
						changed = true;
						//Add the share and the remainder to the corresponding cell in the new array
						newGrid[i + indexOffset] += value%3 + share;
						//Add the share to the neighboring cells
						//If the neighbor's value is equal to the current value, add the share to the current cell instead
						if (isRightEqual)
							newGrid[i + indexOffset] += share;
						else
							newGrid[i + indexOffset + 1] += share;
						if (isLeftEqual)
							newGrid[i + indexOffset] += share;
						else
							newGrid[i + indexOffset - 1] += share;
						//Check whether or not we reached the edge of the array
						if (i == 1 || i == this.grid.length - 2) {
							boundsReached = true;
						}
					} else {
						//If the share is zero, just add the value to the corresponding cell in the new array
						newGrid[i + indexOffset] += value;
					}
				} else {
					newGrid[i + indexOffset] += value;
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
	public long getFromPosition(int x) {	
		int index = originIndex + x;
		return grid[index];
	}
	
	@Override
	public long getFromAsymmetricPosition(int x) {	
		return getFromPosition(x);
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

	@Override
	public String getName() {
		return "SpreadIntegerValue";
	}
	
	@Override
	public String getSubfolderPath() {
		return getName() + "/1D/" + initialValue + "/" + backgroundValue;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}
}
