/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2022 Jaume Ribas

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

import cellularautomata.model1d.IsotropicModel1DA;
import cellularautomata.model1d.SymmetricLongModel1D;

/**
 * A synchronous sandpile model with isotropic rules and a single source initial configuration.
 * 
 * @author Jaume
 *
 */
public class SingleSourceLongSandpile1D implements SymmetricLongModel1D, IsotropicModel1DA {	

	private static final byte RIGHT = 1;
	private static final byte LEFT = 0;
	
	/** A 1D array representing the grid */
	private long[] grid;
	
	private IsotropicLongSandpileRules rules;
	
	private long initialValue;
	private long step;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;

	private int maxXMinusOne;
	
	/**
	 * Creates an instance with the given rules and initial value
	 * 
	 * @param rules
	 * @param initialValue the value at the origin at step 0
	 */
	public SingleSourceLongSandpile1D(IsotropicLongSandpileRules rules, long initialValue) {
		this.rules = rules;
		this.initialValue = initialValue;
		grid = new long[3];
		grid[0] = initialValue;
		boundsReached = false;
		step = 0;
	}
	
	@Override
	public boolean nextStep(){
		long[] newGrid = null;
		if (boundsReached) {
			boundsReached = false;
			newGrid = new long[grid.length + 1];
		} else {
			newGrid = new long[grid.length];
		}
		maxXMinusOne = newGrid.length - 2;
		boolean changed = false;
		long[] neighborValues = new long[2];
		byte[] neighborDirections = new byte[]{RIGHT, LEFT};
		for (int x = 0; x < grid.length; x++) {
			long centerValue = this.grid[x];
			long rightValue = getFromPosition(x + 1);
			long leftValue = getFromPosition(x - 1);
			neighborValues[0] = rightValue;
			neighborValues[1] = leftValue;
			LongTopplingResult topplingResult = rules.topplePosition(centerValue, neighborValues);
			long remainingCenterValue = topplingResult.getRemainingCenterValue();
			long[] valuesTransferedToNeighbors = topplingResult.getValuesSentToNeighbors();
			if (remainingCenterValue != centerValue) {
				changed = true;
			}
			if (remainingCenterValue != 0) {
				newGrid[x] += remainingCenterValue;
			}
			for (int i = 0; i < valuesTransferedToNeighbors.length; i++) {
				long valueTransfered = valuesTransferedToNeighbors[i];
				if (valueTransfered != 0) {
					changed = true;
					addToNeighbor(newGrid, x, neighborDirections[i], valueTransfered);
				}
			}
		}
		grid = newGrid;
		step++;
		return changed;
	}

	private void addToNeighbor(long grid[], int x, byte direction, long value) {
		switch(direction) {
		case RIGHT:
			addRight(grid, x, value);
			break;
		case LEFT:
			addLeft(grid, x, value);
			break;
		}
	}
	
	private void addRight(long[] grid, int x, long value) {
		grid[x+1] += value;
		if (x >= maxXMinusOne) {
			boundsReached = true;
		}
	}
	
	private void addLeft(long[] grid, int x, long value) {
		if (x > 0) {
			long valueToAdd = value;
			if (x == 1) {
				valueToAdd += value;							
			}
			grid[x-1] += valueToAdd;
		}
		if (x >= maxXMinusOne) {
			boundsReached = true;
		}
	}
	
	@Override
	public long getFromPosition(int x){	
		if (x < 0) x = -x;
		if (x < grid.length) {
			return grid[x];
		} else {
			return 0;
		}
	}

	@Override
	public int getAsymmetricMaxX() {
		return grid.length - 1;
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
	public long getStep() {
		return step;
	}
	
	@Override
	public String getSubfolderPath() {
		return getName() + "/1D/single source/" + initialValue;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getFromAsymmetricPosition(int x) {
		return grid[x];
	}

	@Override
	public String getName() {
		return rules.getName();
	}
}
