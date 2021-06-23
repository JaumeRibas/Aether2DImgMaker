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

import cellularautomata.evolvinggrid.SymmetricEvolvingLongGrid3D;

/**
 * Simplified implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/SIV-Cellular-Automaton-Definition">Spread Integer Value</a> cellular automaton in 3D, with a single source initial configuration, for review and testing purposes
 * 
 * @author Jaume
 *
 */
public class SpreadIntegerValueSimple3D implements SymmetricEvolvingLongGrid3D {
	
	/** 3D array representing the grid **/
	private long[][][] grid;
	
	private long initialValue;
	private long backgroundValue;
	private long currentStep;
	
	/** The indexes of the origin within the array */
	private int originIndex;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	
	/**
	 * Creates an instance with the given initial value
	 *  
	 * @param initialValue the value at the origin at step 0
	 * @param backgroundValue the value at all other positions of the grid
	 */
	public SpreadIntegerValueSimple3D(long initialValue, long backgroundValue) {
		this.initialValue = initialValue;
		this.backgroundValue = backgroundValue;
		//initial side of the array, will be increased as needed
		int side = 5;
		grid = new long[side][side][side];
		originIndex = (side - 1)/2;
		if (backgroundValue != 0) {
			for (int x = 0; x < grid.length; x++) {
				for (int y = 0; y < grid.length; y++) {
					for (int z = 0; z < grid.length; z++) {
						grid[x][y][z] = backgroundValue;
					}
				}
			}
		}
		grid[originIndex][originIndex][originIndex] = initialValue;
		boundsReached = false;
		//Set the current step to zero
		currentStep = 0;
	}
	
	@Override
	public boolean nextStep(){
		//Use new array to store the values of the next step
		long[][][] newGrid = null;
		//The offset between the indexes of the new and old array
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new long[grid.length + 2][grid.length + 2][grid.length + 2];
			if (backgroundValue != 0) {
				padEdges(newGrid, 1, backgroundValue);
			}
			indexOffset = 1;
		} else {
			newGrid = new long[grid.length][grid.length][grid.length];
		}
		boolean changed = false;
		//For every position
		for (int x = 0; x < this.grid.length; x++) {
			for (int y = 0; y < this.grid.length; y++) {
				for (int z = 0; z < this.grid.length; z++) {
					long value = this.grid[x][y][z];
					if (value != 0) {
						long greaterXNeighborValue;
						long lowerXNeighborValue;
						if (x < grid.length - 1) {
							greaterXNeighborValue = grid[x + 1][y][z];
							if (x > 0)
								lowerXNeighborValue = grid[x - 1][y][z];
							else
								lowerXNeighborValue = backgroundValue;
						} else {
							greaterXNeighborValue = backgroundValue;
							lowerXNeighborValue = grid[x - 1][y][z];
						}
						long greaterYNeighborValue;
						long lowerYNeighborValue;
						if (y < grid.length - 1) {
							greaterYNeighborValue = grid[x][y + 1][z];
							if (y > 0)
								lowerYNeighborValue = grid[x][y - 1][z];
							else
								lowerYNeighborValue = backgroundValue;
						} else {
							greaterYNeighborValue = backgroundValue;
							lowerYNeighborValue = grid[x][y - 1][z];
						}						
						long greaterZNeighborValue;
						long lowerZNeighborValue;
						if (z < grid.length - 1) {
							greaterZNeighborValue = grid[x][y][z + 1];
							if (z > 0)
								lowerZNeighborValue = grid[x][y][z - 1];
							else
								lowerZNeighborValue = backgroundValue;
						} else {
							greaterZNeighborValue = backgroundValue;
							lowerZNeighborValue = grid[x][y][z - 1];
						}						
						boolean isGreaterYNeighborEqual = value == greaterYNeighborValue, isLowerYNeighborEqual = value == lowerYNeighborValue, 
								isGreaterXNeighborEqual = value == greaterXNeighborValue, isLowerXNeighborEqual = value == lowerXNeighborValue,
								isGreaterZNeighborEqual = value == greaterZNeighborValue, isLowerZNeighborEqual = value == lowerZNeighborValue;
						//if the current position is equal to its neighbors the algorithm has no effect
						if (!(isGreaterYNeighborEqual && isLowerYNeighborEqual 
								&& isGreaterXNeighborEqual && isLowerXNeighborEqual 
								&& isGreaterZNeighborEqual && isLowerZNeighborEqual)) {
							//Divide its value by 7 (using integer division)
							long share = value/7;
							if (share != 0) {
								//If any share is not zero the state changes
								changed = true;
								//Add the share and the remainder to the corresponding position in the new array
								newGrid[x + indexOffset][y + indexOffset][z + indexOffset] += value%7 + share;
								//Add the share to the neighboring positions
								//if the neighbor's value is equal to the current value, add the share to the current position instead
								if (isGreaterXNeighborEqual)
									newGrid[x + indexOffset][y + indexOffset][z + indexOffset] += share;
								else
									newGrid[x + indexOffset + 1][y + indexOffset][z + indexOffset] += share;
								if (isLowerXNeighborEqual)
									newGrid[x + indexOffset][y + indexOffset][z + indexOffset] += share;
								else
									newGrid[x + indexOffset - 1][y + indexOffset][z + indexOffset] += share;
								if (isGreaterYNeighborEqual)
									newGrid[x + indexOffset][y + indexOffset][z + indexOffset] += share;
								else
									newGrid[x + indexOffset][y + indexOffset + 1][z + indexOffset] += share;
								if (isLowerYNeighborEqual)
									newGrid[x + indexOffset][y + indexOffset][z + indexOffset] += share;
								else
									newGrid[x + indexOffset][y + indexOffset - 1][z + indexOffset] += share;				
								if (isGreaterZNeighborEqual)
									newGrid[x + indexOffset][y + indexOffset][z + indexOffset] += share;
								else
									newGrid[x + indexOffset][y + indexOffset][z + indexOffset + 1] += share;
								if (isLowerZNeighborEqual)
									newGrid[x + indexOffset][y + indexOffset][z + indexOffset] += share;
								else
									newGrid[x + indexOffset][y + indexOffset][z + indexOffset - 1] += share;
								//Check whether or not we reached the edge of the array
								if (x == 1 || x == this.grid.length - 2 
										|| y == 1 || y == this.grid.length - 2
										|| z == 1 || z == this.grid.length - 2) {
									boundsReached = true;
								}
							} else {
								//if the share is zero, just add the value to the corresponding position in the new array
								newGrid[x + indexOffset][y + indexOffset][z + indexOffset] += value;
							}
						} else {
							newGrid[x + indexOffset][y + indexOffset][z + indexOffset] += value;
						}
					}
				}
			}
		}
		//Replace the old array with the new one
		this.grid = newGrid;
		//Update the index of the origin
		originIndex += indexOffset;
		//Increase the current step by one
		currentStep++;
		//Return whether or not the state of the grid changed
		return changed;
	}
	
	@Override
	public long getFromPosition(int x, int y, int z){	
		int arrayX = originIndex + x;
		int arrayY = originIndex + y;
		int arrayZ = originIndex + z;
		if (arrayX < 0 || arrayX > grid.length - 1 
				|| arrayY < 0 || arrayY > grid[0].length - 1
				|| arrayZ < 0 || arrayZ > grid[0][0].length - 1) {
			//If the entered position is outside the array the value will be the backgroundValue
			return backgroundValue;
		} else {
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
	public int getAsymmetricMinX() {
		return 0;
	}

	@Override
	public int getAsymmetricMaxX() {
		return getMaxX();
	}

	@Override
	public int getAsymmetricMinY() {
		return 0;
	}

	@Override
	public int getAsymmetricMaxY() {
		return getMaxY();
	}

	@Override
	public int getAsymmetricMinZ() {
		return 0;
	}

	@Override
	public int getAsymmetricMaxZ() {
		return getMaxZ();
	}

	@Override
	public long getFromAsymmetricPosition(int x, int y, int z) {
		return getFromPosition(x, y, z);
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
	
	public static void padEdges(long[][][] grid, int width, long value) {
		int lengthMinusWith = grid.length - width;
		//min X side
		for (int x = 0; x < width && x < grid.length; x++) {
			for (int y = 0; y < grid[x].length; y++) {
				for (int z = 0; z < grid[x][y].length; z++) {
					grid[x][y][z] = value;
				}
			}
		}
		//max X side
		for (int x = lengthMinusWith; x < grid.length; x++) {
			for (int y = 0; y < grid[x].length; y++) {
				for (int z = 0; z < grid[x][y].length; z++) {
					grid[x][y][z] = value;
				}
			}
		}
		//min Y side
		for (int x = width; x < lengthMinusWith; x++) {
			for (int y = 0; y < width && y < grid[x].length; y++) {
				for (int z = 0; z < grid[x][y].length; z++) {
					grid[x][y][z] = value;
				}
			}
		}
		//max Y side
		for (int x = width; x < lengthMinusWith; x++) {
			for (int y = grid[x].length - width; y < grid[x].length; y++) {
				for (int z = 0; z < grid[x][y].length; z++) {
					grid[x][y][z] = value;
				}
			}
		}
		//min Z side
		for (int x = width; x < lengthMinusWith; x++) {
			for (int y = width; y < grid[x].length - width; y++) {
				for (int z = 0; z < width && z < grid[x][y].length; z++) {
					grid[x][y][z] = value;
				}
			}
		}
		//max Z side
		for (int x = width; x < lengthMinusWith; x++) {
			for (int y = width; y < grid[x].length - width; y++) {
				for (int z = grid[x][y].length - width; z < grid[x][y].length; z++) {
					grid[x][y][z] = value;
				}
			}
		}
	}

	@Override
	public String getName() {
		return "SpreadIntegerValue3D";
	}

	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialValue + "/" + backgroundValue;
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