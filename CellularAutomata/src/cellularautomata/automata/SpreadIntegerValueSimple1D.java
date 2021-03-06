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

import cellularautomata.evolvinggrid.SymmetricEvolvingLongGrid1D;

/**
 * Simplified implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/SIV-Cellular-Automaton-Definition">Spread Integer Value</a> cellular automaton in 1D, with a single source initial configuration, for review and testing purposes
 * 
 * @author Jaume
 *
 */
public class SpreadIntegerValueSimple1D implements SymmetricEvolvingLongGrid1D {	

	/** A 1D array representing the grid */
	private long[] grid;
	
	private long initialValue;
	private long backgroundValue;
	private long currentStep;
	
	/** The index of the origin within the array */
	private int xOriginIndex;

	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 * @param backgroundValue the value padding all the grid but the origin at step 0
	 */
	public SpreadIntegerValueSimple1D(long initialValue, long backgroundValue) {
		this.initialValue = initialValue;
		this.backgroundValue = backgroundValue;
		//Create a 1D array to represent the grid. With the initial value at the origin.
		//Make the array of size 5 so as to leave a margin of two positions on each side
		int side = 5;
		grid = new long[side];
		//The origin will be at the center of the array
		xOriginIndex = (side - 1)/2;
		if (backgroundValue != 0) {
			for (int x = 0; x < grid.length; x++) {
				grid[x] = backgroundValue;
			}
		}
		grid[xOriginIndex] = initialValue;
		boundsReached = false;
		//Set the current step to zero
		currentStep = 0;
	}
	
	@Override
	public boolean nextStep(){
		//Use new array to store the values of the next step
		long[] newGrid = null;
		//The offset between the indexes of the new and old array
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new long[grid.length + 2];
			if (backgroundValue != 0) {
				//padEdges
				newGrid[0] = backgroundValue;
				newGrid[newGrid.length - 1] = backgroundValue;
			}
			indexOffset = 1;
		} else {
			newGrid = new long[grid.length];
		}
		boolean changed = false;
		//For every position
		for (int x = 0; x < this.grid.length; x++) {
			long value = this.grid[x];
			if (value != 0) {
				long right;
				long left;
				if (x < grid.length - 1) {
					right = grid[x + 1];
					if (x > 0)
						left = grid[x - 1];
					else
						left = backgroundValue;
				} else {
					right = backgroundValue;
					left = grid[x - 1];
				}
				boolean isRightEqual = value == right, isLeftEqual = value == left;
				//if the current position is equal to its neighbors the algorithm has no effect
				if (!(isRightEqual && isLeftEqual)) {
					//Divide its value by 3 (using integer division)
					long share = value/3;
					if (share != 0) {
						//If any share is not zero the state changes
						changed = true;
						//Add the share and the remainder to the corresponding position in the new array
						newGrid[x + indexOffset] += value%3 + share;
						//Add the share to the neighboring positions
						//if the neighbor's value is equal to the current value, add the share to the current position instead
						if (isRightEqual)
							newGrid[x + indexOffset] += share;
						else
							newGrid[x + indexOffset + 1] += share;
						if (isLeftEqual)
							newGrid[x + indexOffset] += share;
						else
							newGrid[x + indexOffset - 1] += share;
						//Check whether or not we reached the edge of the array
						if (x == 1 || x == this.grid.length - 2) {
							boundsReached = true;
						}
					} else {
						//if the share is zero, just add the value to the corresponding position in the new array
						newGrid[x + indexOffset] += value;
					}
				} else {
					newGrid[x + indexOffset] += value;
				}
			}
		}
		//Replace the old array with the new one
		this.grid = newGrid;
		//Update the index of the origin
		xOriginIndex += indexOffset;
		//Increase the current step by one
		currentStep++;
		//Return whether or not the state of the grid changed
		return changed;
	}
	
	@Override
	public long getFromPosition(int x){	
		int arrayX = xOriginIndex + x;
		if (arrayX < 0 || arrayX > grid.length - 1) {
			//If the entered position is outside the array the value will be the backgroundValue
			return backgroundValue;
		} else {
			return grid[arrayX];
		}
	}
	
	@Override
	public long getFromAsymmetricPosition(int x){	
		return getFromPosition(x);
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

	public long getBackgroundValue() {
		return backgroundValue;
	}

	@Override
	public String getName() {
		return "SpreadIntegerValue1D";
	}
	
	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialValue + "/" + backgroundValue;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}
}
