/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2025 Jaume Ribas

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

import cellularautomata.Direction;
import cellularautomata.automata.Neighbor;
import cellularautomata.model3d.IsotropicCubicModelA;
import cellularautomata.model3d.SymmetricLongModel3D;

/**
 * Implementation of a cellular automaton very similar to <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> to showcase its uniqueness.
 * 
 * @author Jaume
 *
 */
public class SimpleLongNearAetherThree3D implements SymmetricLongModel3D, IsotropicCubicModelA {
	
	/** 3D array representing the grid **/
	private long[][][] grid;
	
	private final long initialValue;
	private long step;
	
	/** The indexes of the origin within the array */
	private int originIndex;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;

	private Boolean changed = null;
	
	/**
	 * Creates an instance with the given initial value
	 *  
	 * @param initialValue the value at the origin at step 0
	 */
	public SimpleLongNearAetherThree3D(long initialValue) {
		//TODO figure out safe range for initial value
		//safety check to prevent exceeding the data type's max value
//		if (initialValue < MIN_INITIAL_VALUE) {
//			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
//		}
		this.initialValue = initialValue;
		//initial side of the array, will be increased as needed
		int side = 5;
		grid = new long[side][side][side];
		originIndex = (side - 1)/2;
		grid[originIndex][originIndex][originIndex] = initialValue;
		boundsReached = false;
		//Set the current step to zero
		step = 0;
	}
	
	@Override
	public Boolean nextStep() {
		//Use new array to store the values of the next step
		long[][][] newGrid = null;
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new long[grid.length + 2][grid.length + 2][grid.length + 2];
			//The offset between the indexes of the new and old array
			indexOffset = 1;
		} else {
			newGrid = new long[grid.length][grid.length][grid.length];
		}
		boolean changed = false;
		//For every cell
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				for (int k = 0; k < grid.length; k++) {
					long value = grid[i][j][k];
					if (value > Long.MIN_VALUE) {
						//make list of von Neumann neighbors with the smallest value
						List<Neighbor<Long>> neighbors = new ArrayList<Neighbor<Long>>(6);			
						long neighborValue;
						long smallestNeighborValue = value - 1;
						if (i < grid.length - 1) {
							neighborValue = grid[i + 1][j][k];
						} else {
							neighborValue = 0;
						}
						if (neighborValue < smallestNeighborValue) {
							smallestNeighborValue = neighborValue;
							neighbors.clear();
							neighbors.add(new Neighbor<Long>(Direction.RIGHT, neighborValue));
						} else if (neighborValue == smallestNeighborValue) {
							neighbors.add(new Neighbor<Long>(Direction.RIGHT, neighborValue));
						}
						if (i > 0) {
							neighborValue = grid[i - 1][j][k];
						} else {
							neighborValue = 0;
						}
						if (neighborValue < smallestNeighborValue) {
							smallestNeighborValue = neighborValue;
							neighbors.clear();
							neighbors.add(new Neighbor<Long>(Direction.LEFT, neighborValue));
						} else if (neighborValue == smallestNeighborValue) {
							neighbors.add(new Neighbor<Long>(Direction.LEFT, neighborValue));
						}
						if (j < grid[i].length - 1) {
							neighborValue = grid[i][j + 1][k];
						} else {
							neighborValue = 0;
						}
						if (neighborValue < smallestNeighborValue) {
							smallestNeighborValue = neighborValue;
							neighbors.clear();
							neighbors.add(new Neighbor<Long>(Direction.UP, neighborValue));
						} else if (neighborValue == smallestNeighborValue) {
							neighbors.add(new Neighbor<Long>(Direction.UP, neighborValue));
						}
						if (j > 0) {
							neighborValue = grid[i][j - 1][k];
						} else {
							neighborValue = 0;
						}
						if (neighborValue < smallestNeighborValue) {
							smallestNeighborValue = neighborValue;
							neighbors.clear();
							neighbors.add(new Neighbor<Long>(Direction.DOWN, neighborValue));
						} else if (neighborValue == smallestNeighborValue) {
							neighbors.add(new Neighbor<Long>(Direction.DOWN, neighborValue));
						}
						if (k < grid[i][j].length - 1) {
							neighborValue = grid[i][j][k + 1];
						} else {
							neighborValue = 0;
						}
						if (neighborValue < smallestNeighborValue) {
							smallestNeighborValue = neighborValue;
							neighbors.clear();
							neighbors.add(new Neighbor<Long>(Direction.FRONT, neighborValue));
						} else if (neighborValue == smallestNeighborValue) {
							neighbors.add(new Neighbor<Long>(Direction.FRONT, neighborValue));
						}
						if (k > 0) {
							neighborValue = grid[i][j][k - 1];
						} else {
							neighborValue = 0;
						}
						if (neighborValue < smallestNeighborValue) {
							smallestNeighborValue = neighborValue;
							neighbors.clear();
							neighbors.add(new Neighbor<Long>(Direction.BACK, neighborValue));
						} else if (neighborValue == smallestNeighborValue) {
							neighbors.add(new Neighbor<Long>(Direction.BACK, neighborValue));
						}
						
						if (neighbors.size() > 0) {
							//apply algorithm rules to redistribute value
							int shareCount = neighbors.size() + 1;
							long toShare = value - smallestNeighborValue;
							long share = toShare/shareCount;
							if (share != 0) {
								checkBoundsReached(i + indexOffset, j + indexOffset, k + indexOffset, newGrid.length);
								changed = true;
								value = value - toShare + toShare%shareCount + share;
								for (Neighbor<Long> neighbor : neighbors) {
									int[] nc = getNeighborCoordinates(i, j, k, neighbor.getDirection());
									newGrid[nc[0] + indexOffset][nc[1] + indexOffset][nc[2] + indexOffset] += share;
								}
							}	
						}
					}
					newGrid[i + indexOffset][j + indexOffset][k + indexOffset] += value;
				}
			}
		}
		//Replace the old array with the new one
		grid = newGrid;
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
	
	private void checkBoundsReached(int i, int j, int k, int length) {
		if (i == 1 || i == length - 2 || 
			j == 1 || j == length - 2 || 
			k == 1 || k == length - 2) {
			boundsReached = true;
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	private static int[] getNeighborCoordinates(int x, int y, int z, Direction direction) {
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
		case FRONT:
			z++;
			break;
		case BACK:
			z--;
			break;
		}
		return new int[]{
			x, y, z
		};
	}
	
	@Override
	public long getFromPosition(int x, int y, int z) {	
		int i = originIndex + x;
		int j = originIndex + y;
		int k = originIndex + z;
		//Note that the indexes whose value hasn't been defined have value zero by default
		return grid[i][j][k];
	}
	
	@Override
	public long getFromAsymmetricPosition(int x, int y, int z) {
		return getFromPosition(x, y, z);
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
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		return "NearAether3";
	}

	@Override
	public String getSubfolderPath() {
		return getName() + "/3D/" + initialValue;
	}
}