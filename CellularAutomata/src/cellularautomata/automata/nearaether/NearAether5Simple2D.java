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
package cellularautomata.automata.nearaether;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cellularautomata.automata.Neighbor;
import cellularautomata.model2d.IsotropicSquareModelA;
import cellularautomata.model2d.SymmetricLongModel2D;

/**
 * Implementation of a cellular automaton very similar to <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> which produces patterns very similar to those produced by Aether.
 * 
 * @author Jaume
 *
 */
public class NearAether5Simple2D implements SymmetricLongModel2D, IsotropicSquareModelA {	
	
	public static final long MAX_INITIAL_VALUE = Long.MAX_VALUE;
	public static final long MIN_INITIAL_VALUE = -6148914691236517205L;
	
	private static final byte UP = 0;
	private static final byte DOWN = 1;
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;
	
	/** A 2D array representing the grid */
	private long[][] grid;
	
	private final long initialValue;
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
	 */
	public NearAether5Simple2D(long initialValue) {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of long type
			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
	    }
		this.initialValue = initialValue;
		int side = 5;
		grid = new long[side][side];
		//The origin will be at the center of the array
		originIndex = (side - 1)/2;
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
			indexOffset = 1;
		} else {
			newGrid = new long[grid.length][grid.length];
		}
		boolean changed = false;
		//For every cell
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				//Distribute the cell's value among its neighbors (von Neumann) using the algorithm
				
				//Get the cell's value
				long value = grid[i][j];
				//Get a list of the neighbors whose value is smaller than the one at the current cell
				List<Neighbor<Long>> neighbors = new ArrayList<Neighbor<Long>>(4);						
				long neighborValue;
				if (i < grid.length - 1)
					neighborValue = grid[i + 1][j];
				else
					neighborValue = 0;
				if (neighborValue < value)
					neighbors.add(new Neighbor<Long>(RIGHT, neighborValue));
				if (i > 0)
					neighborValue = grid[i - 1][j];
				else
					neighborValue = 0;
				if (neighborValue < value)
					neighbors.add(new Neighbor<Long>(LEFT, neighborValue));
				if (j < grid.length - 1)
					neighborValue = grid[i][j + 1];
				else
					neighborValue = 0;
				if (neighborValue < value)
					neighbors.add(new Neighbor<Long>(UP, neighborValue));
				if (j > 0)
					neighborValue = grid[i][j - 1];
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
						for (int neighborIndex = neighbors.size() - 2; neighborIndex >= 0; neighborIndex--) {
							Neighbor<Long> next = neighbors.get(neighborIndex+1);
							if (neighbors.get(neighborIndex).getValue() > next.getValue()) {
								sorted = false;
								neighbors.remove(neighborIndex+1);
								neighbors.add(neighborIndex, next);
							}
						}
					}
					boolean isFirst = true;
					long previousNeighborValue = 0;
					//Apply the algorithm
					long share = 1;
					for (int neighborIndex = neighbors.size() - 1; neighborIndex >= 0 && share != 0/*difference with Aether*/; neighborIndex--,isFirst = false) {
						neighborValue = neighbors.get(neighborIndex).getValue();
						if (neighborValue != previousNeighborValue || isFirst) {
							//Add one for the current cell
							int shareCount = neighbors.size() + 1;
							long toShare = value - neighborValue;
							share = toShare/shareCount;
							if (share != 0) {
								checkBoundsReached(i + indexOffset, j + indexOffset, newGrid.length);
								changed = true;
								//The current cell keeps the remainder and one share
								value = value - toShare + toShare%shareCount + share;
								for (Neighbor<Long> n : neighbors) {
									int[] nc = getNeighborCoordinates(i, j, n.getDirection());
									newGrid[nc[0] + indexOffset][nc[1] + indexOffset] += share;
								}
							}
							previousNeighborValue = neighborValue;
						}
						neighbors.remove(neighborIndex);
					}	
				}					
				newGrid[i + indexOffset][j + indexOffset] += value;
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
	
	private void checkBoundsReached(int i, int j, int length) {
		if (i == 1 || i == length - 2 || 
			j == 1 || j == length - 2) {
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
	public long getFromPosition(int x, int y) {	
		int i = originIndex + x;
		int j = originIndex + y;
		if (i < 0 || i > grid.length - 1 
				|| j < 0 || j > grid.length - 1) {
			//If the coordinates are outside the array, the value will be 0
			return 0;
		} else {
			//Note that the indexes whose value hasn't been defined have value zero by default
			return grid[i][j];
		}
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

	@Override
	public String getName() {
		return "NearAether5";
	}
	
	@Override
	public String getSubfolderPath() {
		return getName() + "/2D/" + initialValue;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}
}
