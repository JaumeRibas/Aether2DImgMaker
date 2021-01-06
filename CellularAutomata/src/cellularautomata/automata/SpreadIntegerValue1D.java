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
 * Optimized implementation of the SpreadIntegerValue cellular automaton in 1D.
 * 
 * @author Jaume
 *
 */
public class SpreadIntegerValue1D implements SymmetricEvolvingLongGrid1D {

	private long[] grid;
	
	private long initialValue;
	private long backgroundValue;
	private long currentStep;

	/** Whether or not the values reached the bounds of the array */
	private boolean boundReached;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 * @param backgroundValue the value padding all the grid but the origin at step 0
	 */
	public SpreadIntegerValue1D(long initialValue, long backgroundValue) {
		this.initialValue = initialValue;
		this.backgroundValue = backgroundValue;
		grid = new long[3];
		grid[0] = this.initialValue;
		grid[1] = backgroundValue;
		grid[2] = backgroundValue;
		boundReached = false;
		currentStep = 0;
	}
	
	@Override
	public boolean nextStep(){
		long[] newGrid = null;
		if (boundReached) {
			boundReached = false;
			newGrid = new long[grid.length + 1];
			newGrid[grid.length] = backgroundValue;
		} else {
			newGrid = new long[grid.length];
		}
		int maxXMinusOne = newGrid.length - 2;
		boolean changed = false;
		for (int x = 0; x < grid.length; x++) {
			long value = grid[x];
			if (value != 0) {
				long left = getValueAtPosition(x - 1);
				long right = getValueAtPosition(x + 1);
				boolean isRightEqual = value == right, isLeftEqual = value == left;
				//if the current position is equal to its neighbors the algorithm has no effect
				if (!(isRightEqual && isLeftEqual)) {
					//Divide its value by 3 (using integer division)
					long share = value/3;
					if (share != 0) {
						//I assume that if any share is not zero the state changes
						changed = true;
						//Add the share to the neighboring positions
						//if the neighbor's value is equal to the current value, add the share to the current position instead
						//x+
						if (isRightEqual)
							newGrid[x] += share;
						else
							newGrid[x+1] += share;
						//x-
						if (isLeftEqual)
							newGrid[x] += share;
						else if (x > 0) {
							long valueToAdd = share;
							if (x == 1) {
								valueToAdd += share;							
							}
							newGrid[x-1] += valueToAdd;
						}
						
						if (x >= maxXMinusOne) {
							boundReached = true;
						}								
					}
					newGrid[x] += value - 2*share;
				} else {
					newGrid[x] += value;
				}
			}
		}
		grid = newGrid;
		currentStep++;
		return changed;
	}
	
	@Override
	public long getValueAtPosition(int x){	
		if (x < 0) x = -x;
		if (x < grid.length) {
			return grid[x];
		} else {
			return backgroundValue;
		}
	}
	
	@Override
	public long getValueAtAsymmetricPosition(int x){	
		return grid[x];
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
