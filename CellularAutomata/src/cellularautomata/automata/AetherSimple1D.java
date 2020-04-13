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
import java.util.ArrayList;
import java.util.List;

import cellularautomata.evolvinggrid.SymmetricEvolvingLongGrid1D;

/**
 * A simplified implementation of the Aether cellular automaton for review and testing purposes
 * 
 * @author Jaume
 *
 */
public class AetherSimple1D implements SymmetricEvolvingLongGrid1D {	
	
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;
	
	/** A 2D array representing the grid */
	private long[] grid;
	
	private long initialValue;
	private long currentStep;
	
	/** The indexes of the origin within the array */
	private int xOriginIndex;

	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public AetherSimple1D(long initialValue) {
		if (initialValue < 0) {
			BigInteger maxNeighboringValuesDifference = Utils.getAetherMaxNeighboringValuesDifferenceFromSingleSource(1, BigInteger.valueOf(initialValue));
			if (maxNeighboringValuesDifference.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
				throw new IllegalArgumentException("Resulting max value difference between neighboring positions (" + maxNeighboringValuesDifference 
						+ ") exceeds implementation's limit (" + Long.MAX_VALUE + "). Use a greater initial value or a different implementation.");
			}
		}
		this.initialValue = initialValue;
		int side = 5;
		grid = new long[side];
		//The origin will be at the center of the array
		xOriginIndex = (side - 1)/2;
		grid[xOriginIndex] = initialValue;
		boundsReached = false;
		//Set the current step to zero
		currentStep = 0;
	}
	
	/**
	 * Computes the next step of the automaton and returns whether
	 * or not the state of the grid changed. 
	 *  
	 * @return true if the state of the grid changed or false otherwise
	 */
	public boolean nextStep(){
		//Use new array to store the values of the next step
		long[] newGrid = null;
		//The offset between the indexes of the new and old array
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new long[grid.length + 2];
			indexOffset = 1;
		} else {
			newGrid = new long[grid.length];
		}
		boolean changed = false;
		//For every position
		for (int x = 0; x < grid.length; x++) {
			//Distribute the positon's value among its neighbors (von Neumann) using the algorithm
			
			//Get the position's value
			long value = grid[x];
			//Get a list of the neighbors whose value is smaller than the one at the current position
			List<LongNeighbor> neighbors = new ArrayList<LongNeighbor>(2);						
			long neighborValue;
			if (x < grid.length - 1)
				neighborValue = grid[x + 1];
			else
				neighborValue = 0;
			if (neighborValue < value)
				neighbors.add(new LongNeighbor(RIGHT, neighborValue));
			if (x > 0)
				neighborValue = grid[x - 1];
			else
				neighborValue = 0;
			if (neighborValue < value)
				neighbors.add(new LongNeighbor(LEFT, neighborValue));

			//If there are any
			if (neighbors.size() > 0) {
				//Sort them by value in ascending order
				boolean sorted = false;
				while (!sorted) {
					sorted = true;
					for (int i = neighbors.size() - 2; i >= 0; i--) {
						LongNeighbor next = neighbors.get(i+1);
						if (neighbors.get(i).getValue() > next.getValue()) {
							sorted = false;
							neighbors.remove(i+1);
							neighbors.add(i, next);
						}
					}
				}
				boolean isFirst = true;
				long previousNeighborValue = 0;
				//Apply the algorithm
				for (int i = neighbors.size() - 1; i >= 0; i--,isFirst = false) {
					neighborValue = neighbors.get(i).getValue();
					if (neighborValue != previousNeighborValue || isFirst) {
						//add one for the center position
						int shareCount = neighbors.size() + 1;
						long toShare = value - neighborValue;
						long share = toShare/shareCount;
						if (share != 0) {
							checkBoundsReached(x + indexOffset, newGrid.length);
							changed = true;
							//the center keeps the remainder and one share
							value = value - toShare + toShare%shareCount + share;
							for (LongNeighbor n : neighbors) {
								int nc = getNeighborCoordinates(x, n.getDirection());
								newGrid[nc + indexOffset] += share;
							}
						}
						previousNeighborValue = neighborValue;
					}
					neighbors.remove(i);
				}	
			}					
			newGrid[x + indexOffset] += value;
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
	
	private void checkBoundsReached(int x, int length) {
		if (x == 1 || x == length - 2) {
			boundsReached = true;
		}
	}
	
	private static int getNeighborCoordinates(int x, byte direction) {
		switch(direction) {
		case RIGHT:
			x++;
			break;
		case LEFT:
			x--;
			break;
		}
		return x;
	}
	
	@Override
	public long getValueAtPosition(int x){	
		int arrayX = xOriginIndex + x;
		if (arrayX < 0 || arrayX > grid.length - 1) {
			//If the entered position is outside the array the value will be 0
			return 0;
		} else {
			//Note that the positions whose value hasn't been defined have value zero by default
			return grid[arrayX];
		}
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

	@Override
	public long getValueAtAsymmetricPosition(int x) {
		return getValueAtPosition(x);
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
}
