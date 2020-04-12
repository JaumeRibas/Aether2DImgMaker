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

import cellularautomata.evolvinggrid.SymmetricEvolvingLongGrid3D;

public class NearAether2Simple3D implements SymmetricEvolvingLongGrid3D {	
	
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
	public NearAether2Simple3D(long initialValue) {
		//safety check to prevent exceeding the data type's max value
		if (initialValue < 0) {
			//for this algorithm the safety check of Aether can be reused
			BigInteger maxNeighboringValuesDifference = Utils.getAetherMaxNeighboringValuesDifferenceFromSingleSource(3, BigInteger.valueOf(initialValue));
			if (maxNeighboringValuesDifference.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
				throw new IllegalArgumentException("Resulting max value difference between neighboring positions (" + maxNeighboringValuesDifference 
						+ ") exceeds implementation's limit (" + Long.MAX_VALUE + "). Use a greater initial value or a different implementation.");
			}
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
					//make list of von Neumann neighbors with value smaller than current position's value
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
							neighborValue += share;
							checkBoundsReached(x, y, z);
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
	
	public long getValueAtPosition(int x, int y, int z){	
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
	
	public int getAsymmetricMinX() {
		return 0;
	}

	public int getAsymmetricMaxX() {
		return getMaxX();
	}

	public int getAsymmetricMinY() {
		return 0;
	}

	public int getAsymmetricMaxY() {
		return getMaxY();
	}

	public int getAsymmetricMinZ() {
		return 0;
	}

	public int getAsymmetricMaxZ() {
		return getMaxZ();
	}

	public long getValueAtAsymmetricPosition(int x, int y, int z) {
		return getValueAtPosition(x, y, z);
	}
	
	/**
	 * Returns the current step
	 * 
	 * @return the current step
	 */
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
		Utils.serializeToFile(this, backupPath, backupName);
	}

	@Override
	public String getName() {
		return "NearAether2_3D";
	}

	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialValue;
	}
	
	@Override
	public int getAsymmetricMinXAtY(int y) {
		return y;
	}

	@Override
	public int getAsymmetricMinXAtZ(int z) {
		return z;
	}

	@Override
	public int getAsymmetricMinX(int y, int z) {
		return Math.max(y, z);
	}

	@Override
	public int getAsymmetricMaxXAtY(int y) {
		return getAsymmetricMaxX();
	}

	@Override
	public int getAsymmetricMaxXAtZ(int z) {
		return getAsymmetricMaxX();
	}

	@Override
	public int getAsymmetricMaxX(int y, int z) {
		return getAsymmetricMaxX();
	}

	@Override
	public int getAsymmetricMinYAtX(int x) {
		return 0;
	}

	@Override
	public int getAsymmetricMinYAtZ(int z) {
		return z;
	}

	@Override
	public int getAsymmetricMinY(int x, int z) {
		return z;
	}

	@Override
	public int getAsymmetricMaxYAtX(int x) {
		return Math.min(getAsymmetricMaxY(), x);
	}

	@Override
	public int getAsymmetricMaxYAtZ(int z) {
		return getAsymmetricMaxY();
	}

	@Override
	public int getAsymmetricMaxY(int x, int z) {
		return Math.min(getAsymmetricMaxY(), x);
	}

	@Override
	public int getAsymmetricMinZAtX(int x) {
		return 0;
	}

	@Override
	public int getAsymmetricMinZAtY(int y) {
		return 0;
	}

	@Override
	public int getAsymmetricMinZ(int x, int y) {
		return 0;
	}

	@Override
	public int getAsymmetricMaxZAtX(int x) {
		return Math.min(getAsymmetricMaxZ(), x);
	}

	@Override
	public int getAsymmetricMaxZAtY(int y) {
		return Math.min(getAsymmetricMaxZ(), y);
	}

	@Override
	public int getAsymmetricMaxZ(int x, int y) {
		return Math.min(getAsymmetricMaxZ(), y);
	}
}