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

import cellularautomata.model3d.IsotropicCubicModelA;
import cellularautomata.model3d.SymmetricLongModel3D;

/**
 * Implementation of a cellular automaton very similar to <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> to showcase its uniqueness.
 * 
 * @author Jaume
 *
 */
public class NearAether2Simple3D implements SymmetricLongModel3D, IsotropicCubicModelA {	
	
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
	private long step;
	
	/** The indexes of the origin within the array */
	private int originIndex;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	
	/**
	 * Creates an instance with the given initial value
	 *  
	 * @param initialValue the value at the origin at step 0
	 */
	public NearAether2Simple3D(long initialValue) {
		//safety check to prevent exceeding the data type's max value
		if (initialValue < MIN_INITIAL_VALUE) {
			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
		}
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
	public boolean nextStep() {
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
		//For every cell
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[0].length; y++) {
				for (int z = 0; z < grid[0][0].length; z++) {
					long value = grid[x][y][z];
					//make list of von Neumann neighbors with value smaller than current cell's value
					List<Byte> relevantNeighborDirections = new ArrayList<Byte>(6);						
					long neighborValue;
					long biggestSmallerNeighborValue = Long.MIN_VALUE;
					if (x < grid.length - 1) {
						neighborValue = grid[x + 1][y][z];
					} else {
						neighborValue = 0;
					}
					if (neighborValue < value) {
						if (neighborValue > biggestSmallerNeighborValue) {
							biggestSmallerNeighborValue = neighborValue;
						}
						relevantNeighborDirections.add(RIGHT);
					}
					if (x > 0) {
						neighborValue = grid[x - 1][y][z];
					} else {
						neighborValue = 0;
					}
					if (neighborValue < value) {
						if (neighborValue > biggestSmallerNeighborValue) {
							biggestSmallerNeighborValue = neighborValue;
						}
						relevantNeighborDirections.add(LEFT);
					}
					if (y < grid[x].length - 1) {
						neighborValue = grid[x][y + 1][z];
					} else {
						neighborValue = 0;
					}
					if (neighborValue < value) {
						if (neighborValue > biggestSmallerNeighborValue) {
							biggestSmallerNeighborValue = neighborValue;
						}
						relevantNeighborDirections.add(UP);
					}
					if (y > 0) {
						neighborValue = grid[x][y - 1][z];
					} else {
						neighborValue = 0;
					}
					if (neighborValue < value) {
						if (neighborValue > biggestSmallerNeighborValue) {
							biggestSmallerNeighborValue = neighborValue;
						}
						relevantNeighborDirections.add(DOWN);
					}
					if (z < grid[x][y].length - 1) {
						neighborValue = grid[x][y][z + 1];
					} else {
						neighborValue = 0;
					}
					if (neighborValue < value) {
						if (neighborValue > biggestSmallerNeighborValue) {
							biggestSmallerNeighborValue = neighborValue;
						}
						relevantNeighborDirections.add(FRONT);
					}
					if (z > 0) {
						neighborValue = grid[x][y][z - 1];
					} else {
						neighborValue = 0;
					}
					if (neighborValue < value) {
						if (neighborValue > biggestSmallerNeighborValue) {
							biggestSmallerNeighborValue = neighborValue;
						}
						relevantNeighborDirections.add(BACK);
					}
					
					if (relevantNeighborDirections.size() > 0) {
						//apply algorithm rules to redistribute value
						int shareCount = relevantNeighborDirections.size() + 1;
						long toShare = value - biggestSmallerNeighborValue;
						long share = toShare/shareCount;
						if (share != 0) {
							checkBoundsReached(x + indexOffset, y + indexOffset, z + indexOffset, newGrid.length);
							changed = true;
							value = value - toShare + toShare%shareCount + share;
							for (Byte neighborDirection : relevantNeighborDirections) {
								int[] nc = getNeighborCoordinates(x, y, z, neighborDirection);
								newGrid[nc[0] + indexOffset][nc[1] + indexOffset][nc[2] + indexOffset] += share;
							}
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
		step++;
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
	public long getFromPosition(int x, int y, int z) {	
		int arrayX = originIndex + x;
		int arrayY = originIndex + y;
		int arrayZ = originIndex + z;
		if (arrayX < 0 || arrayX > grid.length - 1 
				|| arrayY < 0 || arrayY > grid[0].length - 1
				|| arrayZ < 0 || arrayZ > grid[0][0].length - 1) {
			//If the passed coordinates are outside the array, the value will be zero
			return 0;
		} else {
			//Note that the indexes whose value hasn't been defined have value zero by default
			return grid[arrayX][arrayY][arrayZ];
		}
	}
	
	@Override
	public long getFromAsymmetricPosition(int x, int y, int z) {
		return getFromPosition(x, y, z);
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
	public int getAsymmetricMaxX() {
		return getMaxX();
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
		return "NearAether2";
	}

	@Override
	public String getSubfolderPath() {
		return getName() + "/3D/" + initialValue;
	}
}