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
import java.math.BigInteger;

public class Aether1D implements SymmetricLongCellularAutomaton1D {	

	private static final byte RIGHT = 1;
	private static final byte LEFT = 0;
	
	/** A 1D array representing the grid */
	private long[] grid;
	
	private long initialValue;
	private long currentStep;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;

	private int maxXMinusOne;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public Aether1D(long initialValue) {
		if (initialValue < 0) {
			BigInteger maxNeighboringValuesDifference = Utils.getAetherMaxNeighboringValuesDifferenceFromSingleSource(1, BigInteger.valueOf(initialValue));
			if (maxNeighboringValuesDifference.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
				throw new IllegalArgumentException("Resulting max value difference between neighboring positions (" + maxNeighboringValuesDifference 
						+ ") exceeds implementation's limit (" + Long.MAX_VALUE + "). Use a greater initial value or a different implementation.");
			}
		}
		this.initialValue = initialValue;
		grid = new long[3];
		grid[0] = initialValue;
		boundsReached = false;
		currentStep = 0;
	}
	
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
		byte[] neighborDirections = new byte[2];
		for (int x = 0; x < grid.length; x++) {
			long value = this.grid[x];
			int relevantNeighborCount = 0;
			long neighborValue;
			neighborValue = getValueAtPosition(x + 1);
			if (neighborValue < value) {
				neighborValues[relevantNeighborCount] = neighborValue;
				neighborDirections[relevantNeighborCount] = RIGHT;
				relevantNeighborCount++;
			}
			neighborValue = getValueAtPosition(x - 1);
			if (neighborValue < value) {
				neighborValues[relevantNeighborCount] = neighborValue;
				neighborDirections[relevantNeighborCount] = LEFT;
				relevantNeighborCount++;
			}
			
			if (relevantNeighborCount > 0) {
				//sort
				boolean sorted = false;
				while (!sorted) {
					sorted = true;
					for (int i = relevantNeighborCount - 2; i >= 0; i--) {
						if (neighborValues[i] < neighborValues[i+1]) {
							sorted = false;
							long valSwap = neighborValues[i];
							neighborValues[i] = neighborValues[i+1];
							neighborValues[i+1] = valSwap;
							byte dirSwap = neighborDirections[i];
							neighborDirections[i] = neighborDirections[i+1];
							neighborDirections[i+1] = dirSwap;
						}
					}
				}
				//divide
				boolean isFirstNeighbor = true;
				long previousNeighborValue = 0;
				for (int i = 0; i < relevantNeighborCount; i++,isFirstNeighbor = false) {
					neighborValue = neighborValues[i];
					if (neighborValue != previousNeighborValue || isFirstNeighbor) {
						int shareCount = relevantNeighborCount - i + 1;
						long toShare = value - neighborValue;
						long share = toShare/shareCount;
						if (share != 0) {
							changed = true;
							value = value - toShare + toShare%shareCount + share;
							for (int j = i; j < relevantNeighborCount; j++) {
								addToNeighbor(newGrid, x, neighborDirections[j], share);
							}
						}
						previousNeighborValue = neighborValue;
					}
				}	
			}					
			newGrid[x] += value;
		}
		grid = newGrid;
		currentStep++;
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
	
	public long getValueAtPosition(int x){	
		if (x < 0) x = -x;
		if (x < grid.length) {
			return grid[x];
		} else {
			return 0;
		}
	}
	
	public int getNonsymmetricMinX() {
		return 0;
	}

	public int getNonsymmetricMaxX() {
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
	public int getMinX() {
		return -getNonsymmetricMaxX();
	}

	@Override
	public int getMaxX() {
		return getNonsymmetricMaxX();
	}

	@Override
	public long getStep() {
		return currentStep;
	}

	@Override
	public String getName() {
		return "Aether1D";
	}
	
	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialValue;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getValueAtNonsymmetricPosition(int x) {
		return grid[x];
	}
}
