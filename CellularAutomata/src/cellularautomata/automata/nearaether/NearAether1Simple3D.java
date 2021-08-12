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
package cellularautomata.automata.nearaether;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cellularautomata.automata.Neighbor;
import cellularautomata.evolvinggrid.EvolvingLongGrid3D;

/**
 * Implementation of a cellular automaton very similar to <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> to showcase its uniqueness.
 * 
 * @author Jaume
 *
 */
public class NearAether1Simple3D implements EvolvingLongGrid3D {	
	
	public static final long MAX_INITIAL_VALUE = Long.MAX_VALUE;
	public static final long MIN_INITIAL_VALUE = -3689348814741910323L;

	private static final byte UP = 0;
	private static final byte DOWN = 1;
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;
	private static final byte FRONT = 4;
	private static final byte BACK = 5;
	
	/** 3D array representing the grid **/
	private long[][][] grid;
	
	private long initialValue;
	private long currentStep;
	
	/** The indexes of the origin within the array */
	private int originIndex;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	
	/**
	 * Creates an instance with the given initial value
	 *  
	 * @param initialValue the value at the origin at step 0
	 */
	public NearAether1Simple3D(long initialValue) {
		//safety check to prevent exceeding the data type's max value
		if (initialValue < MIN_INITIAL_VALUE) {
			throw new IllegalArgumentException("Initial value cannot be smaller than -3,689,348,814,741,910,323. Use a greater initial value or a different implementation.");
		}
		this.initialValue = initialValue;
		//initial side of the array, will be increased as needed
		int side = 5;
		grid = new long[side][side][side];
		originIndex = (side - 1)/2;
		grid[originIndex][originIndex][originIndex] = initialValue;
		boundsReached = false;
		//Set the current step to zero
		currentStep = 0;
	}
	
	@Override
	public boolean nextStep(){
		//Use new array to store the values of the next step
		long[][][] newGrid = null;
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new long[grid.length + 2][grid[0].length + 2][grid[0][0].length + 2];
			//The offset between the indexes of the new and old array
			indexOffset = 1;
		} else {
			newGrid = new long[grid.length][grid[0].length][grid[0][0].length];
		}
		boolean changed = false;
		//For every position
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[0].length; y++) {
				for (int z = 0; z < grid[0][0].length; z++) {
					long value = grid[x][y][z];
					//make list of von Neumann neighbors with value smaller than current position's value
					List<Neighbor<Long>> neighbors = new ArrayList<Neighbor<Long>>(6);						
					long neighborValue;
					if (x < grid.length - 1)
						neighborValue = grid[x + 1][y][z];
					else
						neighborValue = 0;
					if (neighborValue < value)
						neighbors.add(new Neighbor<Long>(RIGHT, neighborValue));
					if (x > 0)
						neighborValue = grid[x - 1][y][z];
					else
						neighborValue = 0;
					if (neighborValue < value)
						neighbors.add(new Neighbor<Long>(LEFT, neighborValue));
					if (y < grid[x].length - 1)
						neighborValue = grid[x][y + 1][z];
					else
						neighborValue = 0;
					if (neighborValue < value)
						neighbors.add(new Neighbor<Long>(UP, neighborValue));
					if (y > 0)
						neighborValue = grid[x][y - 1][z];
					else
						neighborValue = 0;
					if (neighborValue < value)
						neighbors.add(new Neighbor<Long>(DOWN, neighborValue));
					if (z < grid[x][y].length - 1)
						neighborValue = grid[x][y][z + 1];
					else
						neighborValue = 0;
					if (neighborValue < value)
						neighbors.add(new Neighbor<Long>(FRONT, neighborValue));
					if (z > 0)
						neighborValue = grid[x][y][z - 1];
					else
						neighborValue = 0;
					if (neighborValue < value)
						neighbors.add(new Neighbor<Long>(BACK, neighborValue));
					
					if (neighbors.size() > 0) {
						//sort neighbors by value
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
						//apply algorithm rules to redistribute value
						boolean isFirst = true;
						long previousNeighborValue = 0;
						for (int i = neighbors.size() - 1; i >= 0; i--,isFirst = false) {
							neighborValue = neighbors.get(i).getValue();
							if (neighborValue != previousNeighborValue || isFirst) {
								int shareCount = neighbors.size() + 1;
								long toShare = value - neighborValue;
								long share = toShare/shareCount;
								if (share != 0) {
									neighborValue += share;
									checkBoundsReached(x + indexOffset, y + indexOffset, z + indexOffset, newGrid.length);
									changed = true;
									value = value - toShare + toShare%shareCount + share;
									for (Neighbor<Long> neighbor : neighbors) {
										int[] nc = getNeighborCoordinates(x, y, z, neighbor.getDirection());
										newGrid[nc[0] + indexOffset][nc[1] + indexOffset][nc[2] + indexOffset] += share;
										//difference with AE
										neighbor.setValue(neighbor.getValue() + share);
									}
								}
								previousNeighborValue = neighborValue;
							}
							neighbors.remove(i);
						}	
					}					
					newGrid[x + indexOffset][y + indexOffset][z + indexOffset] += value;
				}
			}
		}
		//Replace the old array with the new one
		grid = newGrid;
		//Update the index of the origin
		originIndex += indexOffset;
		//Increase the current step by one
		currentStep++;
		//Return whether or not the state of the grid changed
		return changed;
	}
	
	private void checkBoundsReached(int x, int y, int z, int length) {
		if (x == 1 || x == length - 2 || 
			y == 1 || y == length - 2 || 
			z == 1 || z == length - 2) {
			boundsReached = true;
		}
	}
	
	private static int[] getNeighborCoordinates(int x, int y, int z, byte direction) {
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
	public long getFromPosition(int x, int y, int z){	
		int arrayX = originIndex + x;
		int arrayY = originIndex + y;
		int arrayZ = originIndex + z;
		if (arrayX < 0 || arrayX > grid.length - 1 
				|| arrayY < 0 || arrayY > grid[0].length - 1
				|| arrayZ < 0 || arrayZ > grid[0][0].length - 1) {
			//If the entered position is outside the array the value will be zero
			return 0;
		} else {
			//Note that the positions whose value hasn't been defined have value zero by default
			return grid[arrayX][arrayY][arrayZ];
		}
	}
	
	@Override
	public int getMinX() {
		int arrayMinX = - originIndex;
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
	public int getMinY() {
		int arrayMinY = - originIndex;
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
		int arrayMaxY = grid[0].length - 1 - originIndex;
		int valuesMaxY;
		if (boundsReached) {
			valuesMaxY = arrayMaxY;
		} else {
			valuesMaxY = arrayMaxY - 1;
		}
		return valuesMaxY;
	}
	
	@Override
	public int getMinZ() {
		int arrayMinZ = - originIndex;
		int valuesMinZ;
		if (boundsReached) {
			valuesMinZ = arrayMinZ;
		} else {
			valuesMinZ = arrayMinZ + 1;
		}
		return valuesMinZ;
	}
	
	@Override
	public int getMaxZ() {
		int arrayMaxZ = grid[0][0].length - 1 - originIndex;
		int valuesMaxZ;
		if (boundsReached) {
			valuesMaxZ = arrayMaxZ;
		} else {
			valuesMaxZ = arrayMaxZ - 1;
		}
		return valuesMaxZ;
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
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		return "NearAether1_3D";
	}

	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialValue;
	}
}