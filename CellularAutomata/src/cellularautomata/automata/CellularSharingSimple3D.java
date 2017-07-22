/* CellularSharing2DImgMaker -- console app to generate images from the Cellular Sharing 2D cellular automaton
    Copyright (C) 2017 Jaume Ribas

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

import java.util.ArrayList;

public class CellularSharingSimple3D extends LongCellularAutomaton3D {	
	
	private static final byte UP = 0;
	private static final byte DOWN = 1;
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;
	private static final byte FRONT = 4;
	private static final byte BACK = 5;
	
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
	public CellularSharingSimple3D(long initialValue) {
		this.initialValue = initialValue;
		int side = 3;
		grid = new long[side][side][side];
		originIndex = (side - 1)/2;
		grid[originIndex][originIndex][originIndex] = this.initialValue;
		boundsReached = false;
		//Set the current step to zero
		currentStep = 0;
	}
	
	/**
	 * Computes the next step of the algorithm and returns whether
	 * or not the state of the grid changed. 
	 *  
	 * @return true if the state of the grid changed or false otherwise
	 */
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
					if (value != 0) {
						ArrayList<LongNeighbor> neighbors = new ArrayList<LongNeighbor>(6);						
						long neighborValue;
						if (x < grid.length - 1)
							neighborValue = grid[x + 1][y][z];
						else
							neighborValue = 0;
						if (neighborValue < value)
							neighbors.add(new LongNeighbor(RIGHT, neighborValue));
						if (x > 0)
							neighborValue = grid[x - 1][y][z];
						else
							neighborValue = 0;
						if (neighborValue < value)
							neighbors.add(new LongNeighbor(LEFT, neighborValue));
						if (y < grid[x].length - 1)
							neighborValue = grid[x][y + 1][z];
						else
							neighborValue = 0;
						if (neighborValue < value)
							neighbors.add(new LongNeighbor(UP, neighborValue));
						if (y > 0)
							neighborValue = grid[x][y - 1][z];
						else
							neighborValue = 0;
						if (neighborValue < value)
							neighbors.add(new LongNeighbor(DOWN, neighborValue));
						if (z < grid[x][y].length - 1)
							neighborValue = grid[x][y][z + 1];
						else
							neighborValue = 0;
						if (neighborValue < value)
							neighbors.add(new LongNeighbor(FRONT, neighborValue));
						if (z > 0)
							neighborValue = grid[x][y][z - 1];
						else
							neighborValue = 0;
						if (neighborValue < value)
							neighbors.add(new LongNeighbor(BACK, neighborValue));
						
						if (neighbors.size() > 0) {
							//sort
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
							//divide
							boolean isFirst = true;
							long previousNeighborValue = 0;
							for (int i = neighbors.size() - 1; i >= 0; i--,isFirst = false) {
								neighborValue = neighbors.get(i).getValue();
								if (neighborValue != previousNeighborValue || isFirst) {
									int shareCount = neighbors.size() + 1;
									long toShare = value - neighborValue;
									long share = toShare/shareCount;
									if (share != 0) {
										checkBoundsReached(x, y, z);
										changed = true;
										value = value - toShare + toShare%shareCount + share;
										for (LongNeighbor neighbor : neighbors) {
											int[] nc = getNeighborCoordinates(x, y, z, neighbor.getDirection());
											newGrid[nc[0] + indexOffset][nc[1] + indexOffset][nc[2] + indexOffset] += share;
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
	
	private void checkBoundsReached(int x, int y, int z) {
		if (x == 1 || x == grid.length - 2 || 
			y == 1 || y == grid[0].length - 2 || 
			z == 1 || z == grid[0][0].length - 2) {
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
	
	public long getValueAt(int x, int y, int z){	
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
	
	/**
	 * Returns the smallest x-coordinate of a nonzero value at the current step
	 * 
	 * @return the smallest x of a nonzero value at the current step
	 */
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
	
	/**
	 * Returns the largest x-coordinate of a nonzero value at the current step
	 * 
	 * @return the largest x of a nonzero value at the current step
	 */
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
	
	/**
	 * Returns the current step
	 * 
	 * @return the current step
	 */
	public long getCurrentStep() {
		return currentStep;
	}
	
	/**
	 * Returns the initial value
	 * 
	 * @return the value at the origin at step 0
	 */
	public long getIntialValue() {
		return initialValue;
	}

}