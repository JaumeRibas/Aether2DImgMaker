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
import java.util.ArrayList;
import java.util.List;

import cellularautomata.evolvinggrid.EvolvingLongGrid2D;

/**
 * Simplified implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 2D, with a single source initial configuration, for review and testing purposes
 * 
 * @author Jaume
 *
 */
public class AetherSimple2D implements EvolvingLongGrid2D {	
	
	public static final long MAX_INITIAL_VALUE = Long.MAX_VALUE;
	public static final long MIN_INITIAL_VALUE = -6148914691236517205L;
	
	private static final byte UP = 0;
	private static final byte DOWN = 1;
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;
	
	/** A 2D array representing the grid */
	private long[][] grid;
	
	private long initialValue;
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
	 */
	public AetherSimple2D(long initialValue) {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of long type
			throw new IllegalArgumentException("Initial value cannot be smaller than -6,148,914,691,236,517,205. Use a greater initial value or a different implementation.");
	    }
		this.initialValue = initialValue;
		int side = 5;
		grid = new long[side][side];
		//The origin will be at the center of the array
		xOriginIndex = (side - 1)/2;
		yOriginIndex = xOriginIndex;
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
			indexOffset = 1;
		} else {
			newGrid = new long[grid.length][grid[0].length];
		}
		boolean changed = false;
		//For every position
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[0].length; y++) {
				//Distribute the positon's value among its neighbors (von Neumann) using the algorithm
				
				//Get the position's value
				long value = grid[x][y];
				//Get a list of the neighbors whose value is smaller than the one at the current position
				List<Neighbor<Long>> neighbors = new ArrayList<Neighbor<Long>>(4);						
				long neighborValue;
				if (x < grid.length - 1)
					neighborValue = grid[x + 1][y];
				else
					neighborValue = 0;
				if (neighborValue < value)
					neighbors.add(new Neighbor<Long>(RIGHT, neighborValue));
				if (x > 0)
					neighborValue = grid[x - 1][y];
				else
					neighborValue = 0;
				if (neighborValue < value)
					neighbors.add(new Neighbor<Long>(LEFT, neighborValue));
				if (y < grid[x].length - 1)
					neighborValue = grid[x][y + 1];
				else
					neighborValue = 0;
				if (neighborValue < value)
					neighbors.add(new Neighbor<Long>(UP, neighborValue));
				if (y > 0)
					neighborValue = grid[x][y - 1];
				else
					neighborValue = 0;
				if (neighborValue < value)
					neighbors.add(new Neighbor<Long>(DOWN, neighborValue));

				//If there are any
				if (neighbors.size() > 0) {
					//Sort them by value in ascending order
					boolean sorted = false;
					while (!sorted) {
						sorted = true;
						for (int i = neighbors.size() - 2; i >= 0; i--) {
							Neighbor<Long> next = neighbors.get(i+1);
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
								checkBoundsReached(x + indexOffset, y + indexOffset, newGrid.length);
								changed = true;
								//the center keeps the remainder and one share
								value = value - toShare + toShare%shareCount + share;
								for (Neighbor<Long> n : neighbors) {
									int[] nc = getNeighborCoordinates(x, y, n.getDirection());
									newGrid[nc[0] + indexOffset][nc[1] + indexOffset] += share;
								}
							}
							previousNeighborValue = neighborValue;
						}
						neighbors.remove(i);
					}	
				}					
				newGrid[x + indexOffset][y + indexOffset] += value;
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
	
	private void checkBoundsReached(int x, int y, int length) {
		if (x == 1 || x == length - 2 || 
			y == 1 || y == length - 2) {
			boundsReached = true;
		}
	}
	
	private static int[] getNeighborCoordinates(int x, int y, byte direction) {
		switch(direction) {
		case UP:
			y++;
			break;
		case DOWN:
			y--;
			break;
		case RIGHT:
			x++;
			break;
		case LEFT:
			x--;
			break;
		}
		return new int[]{
			x, y
		};
	}
	
	@Override
	public long getFromPosition(int x, int y){	
		int arrayX = xOriginIndex + x;
		int arrayY = yOriginIndex + y;
		if (arrayX < 0 || arrayX > grid.length - 1 
				|| arrayY < 0 || arrayY > grid[0].length - 1) {
			//If the entered position is outside the array the value will be 0
			return 0;
		} else {
			//Note that the positions whose value hasn't been defined have value zero by default
			return grid[arrayX][arrayY];
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
	public String getName() {
		return "Aether2D";
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
