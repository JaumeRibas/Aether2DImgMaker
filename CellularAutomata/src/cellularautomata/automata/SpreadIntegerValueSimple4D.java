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

import cellularautomata.evolvinggrid.SymmetricEvolvingLongGrid4D;

public class SpreadIntegerValueSimple4D implements SymmetricEvolvingLongGrid4D {	
	
	private long[][][][] grid;
	
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
	public SpreadIntegerValueSimple4D(long initialValue, long backgroundValue) {
		this.initialValue = initialValue;
		this.backgroundValue = backgroundValue;
		int side = 5;
		grid = new long[side][side][side][side];
		if (backgroundValue != 0) {
			for (int w = 0; w < grid.length; w++) {
				for (int x = 0; x < grid.length; x++) {
					for (int y = 0; y < grid.length; y++) {
						for (int z = 0; z < grid.length; z++) {
							grid[w][x][y][z] = backgroundValue;
						}
					}
				}
			}
		}
		originIndex = (side - 1)/2;
		grid[originIndex][originIndex][originIndex][originIndex] = this.initialValue;
		boundsReached = false;
		//Set the current step to zero
		currentStep = 0;
	}	
	
	/**
	 * Creates an instance restoring a backup
	 * 
	 * @param backupPath the path to the backup file to restore.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 */
	public SpreadIntegerValueSimple4D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		SpreadIntegerValueSimple4D data = (SpreadIntegerValueSimple4D) Utils.deserializeFromFile(backupPath);
		initialValue = data.initialValue;
		backgroundValue = data.backgroundValue;
		grid = data.grid;
		originIndex = data.originIndex;
		boundsReached = data.boundsReached;
		currentStep = data.currentStep;
	}
	
	@Override
	public boolean nextStep(){
		//Use new array to store the values of the next step
		long[][][][] newGrid = null;
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new long[grid.length + 2][grid.length + 2][grid.length + 2][grid.length + 2];
			if (backgroundValue != 0) {
				padEdges(newGrid, 1, backgroundValue);
			}
			//The offset between the indexes of the new and old array
			indexOffset = 1;
		} else {
			newGrid = new long[grid.length][grid.length][grid.length][grid.length];
		}
		boolean changed = false;
		//For every position
		for (int w = 0; w < grid.length; w++) {
			for (int x = 0; x < grid.length; x++) {
				for (int y = 0; y < grid.length; y++) {
					for (int z = 0; z < grid.length; z++) {
						long value = this.grid[w][x][y][z];
						if (value != 0) {
							long greaterWNeighborValue;
							long lowerWNeighborValue;
							if (w < grid.length - 1) {
								greaterWNeighborValue = grid[w + 1][x][y][z];
								if (w > 0)
									lowerWNeighborValue = grid[w - 1][x][y][z];
								else
									lowerWNeighborValue = backgroundValue;
							} else {
								greaterWNeighborValue = backgroundValue;
								lowerWNeighborValue = grid[w - 1][x][y][z];
							}
							long greaterXNeighborValue;
							long lowerXNeighborValue;
							if (x < grid.length - 1) {
								greaterXNeighborValue = grid[w][x + 1][y][z];
								if (x > 0)
									lowerXNeighborValue = grid[w][x - 1][y][z];
								else
									lowerXNeighborValue = backgroundValue;
							} else {
								greaterXNeighborValue = backgroundValue;
								lowerXNeighborValue = grid[w][x - 1][y][z];
							}
							long greaterYNeighborValue;
							long lowerYNeighborValue;
							if (y < grid.length - 1) {
								greaterYNeighborValue = grid[w][x][y + 1][z];
								if (y > 0)
									lowerYNeighborValue = grid[w][x][y - 1][z];
								else
									lowerYNeighborValue = backgroundValue;
							} else {
								greaterYNeighborValue = backgroundValue;
								lowerYNeighborValue = grid[w][x][y - 1][z];
							}						
							long greaterZNeighborValue;
							long lowerZNeighborValue;
							if (z < grid.length - 1) {
								greaterZNeighborValue = grid[w][x][y][z + 1];
								if (z > 0)
									lowerZNeighborValue = grid[w][x][y][z - 1];
								else
									lowerZNeighborValue = backgroundValue;
							} else {
								greaterZNeighborValue = backgroundValue;
								lowerZNeighborValue = grid[w][x][y][z - 1];
							}						
							boolean isGreaterWNeighborEqual = value == greaterWNeighborValue, isLowerWNeighborEqual = value == lowerWNeighborValue,
									isGreaterXNeighborEqual = value == greaterXNeighborValue, isLowerXNeighborEqual = value == lowerXNeighborValue,
									isGreaterYNeighborEqual = value == greaterYNeighborValue, isLowerYNeighborEqual = value == lowerYNeighborValue, 
									isGreaterZNeighborEqual = value == greaterZNeighborValue, isLowerZNeighborEqual = value == lowerZNeighborValue;
							//if the current position is equal to its neighbors the algorithm has no effect
							if (!(isGreaterWNeighborEqual && isLowerWNeighborEqual 
									&& isGreaterXNeighborEqual && isLowerXNeighborEqual 
									&& isGreaterYNeighborEqual && isLowerYNeighborEqual 
									&& isGreaterZNeighborEqual && isLowerZNeighborEqual)) {
								//Divide its value by 9 (using integer division)
								long share = value/9;
								if (share != 0) {
									//I assume that if any share is not zero the state changes
									changed = true;
									//Add the share and the remainder to the corresponding position in the new array
									newGrid[w + indexOffset][x + indexOffset][y + indexOffset][z + indexOffset] += value%9 + share;
									//Add the share to the neighboring positions
									//if the neighbor's value is equal to the current value, add the share to the current position instead
									if (isGreaterWNeighborEqual)
										newGrid[w + indexOffset][x + indexOffset][y + indexOffset][z + indexOffset] += share;
									else
										newGrid[w + indexOffset + 1][x + indexOffset][y + indexOffset][z + indexOffset] += share;
									if (isLowerWNeighborEqual)
										newGrid[w + indexOffset][x + indexOffset][y + indexOffset][z + indexOffset] += share;
									else
										newGrid[w + indexOffset - 1][x + indexOffset][y + indexOffset][z + indexOffset] += share;
									if (isGreaterXNeighborEqual)
										newGrid[w + indexOffset][x + indexOffset][y + indexOffset][z + indexOffset] += share;
									else
										newGrid[w + indexOffset][x + indexOffset + 1][y + indexOffset][z + indexOffset] += share;
									if (isLowerXNeighborEqual)
										newGrid[w + indexOffset][x + indexOffset][y + indexOffset][z + indexOffset] += share;
									else
										newGrid[w + indexOffset][x + indexOffset - 1][y + indexOffset][z + indexOffset] += share;
									if (isGreaterYNeighborEqual)
										newGrid[w + indexOffset][x + indexOffset][y + indexOffset][z + indexOffset] += share;
									else
										newGrid[w + indexOffset][x + indexOffset][y + indexOffset + 1][z + indexOffset] += share;
									if (isLowerYNeighborEqual)
										newGrid[w + indexOffset][x + indexOffset][y + indexOffset][z + indexOffset] += share;
									else
										newGrid[w + indexOffset][x + indexOffset][y + indexOffset - 1][z + indexOffset] += share;				
									if (isGreaterZNeighborEqual)
										newGrid[w + indexOffset][x + indexOffset][y + indexOffset][z + indexOffset] += share;
									else
										newGrid[w + indexOffset][x + indexOffset][y + indexOffset][z + indexOffset + 1] += share;
									if (isLowerZNeighborEqual)
										newGrid[w + indexOffset][x + indexOffset][y + indexOffset][z + indexOffset] += share;
									else
										newGrid[w + indexOffset][x + indexOffset][y + indexOffset][z + indexOffset - 1] += share;
									//Check whether or not we reached the edge of the array
									if (w == 1 || w == this.grid.length - 2 
											|| x == 1 || x == this.grid.length - 2 
											|| y == 1 || y == this.grid.length - 2
											|| z == 1 || z == this.grid.length - 2) {
										boundsReached = true;
									}
								} else {
									//if the share is zero, just add the value to the corresponding position in the new array
									newGrid[w + indexOffset][x + indexOffset][y + indexOffset][z + indexOffset] += value;
								}
							} else {
								newGrid[w + indexOffset][x + indexOffset][y + indexOffset][z + indexOffset] += value;
							}
						}
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
	
	@Override
	public long getValueAtPosition(int w, int x, int y, int z){
		int arrayW = originIndex + w;
		int arrayX = originIndex + x;
		int arrayY = originIndex + y;
		int arrayZ = originIndex + z;
		if (arrayW < 0 || arrayW > grid.length - 1 
				|| arrayX < 0 || arrayX > grid.length - 1 
				|| arrayY < 0 || arrayY > grid[0].length - 1
				|| arrayZ < 0 || arrayZ > grid[0][0].length - 1) {
			//If the entered position is outside the array the value will be the background value
			return 0;
		} else {
			//Note that the positions whose value hasn't been defined have value zero by default
			return grid[arrayW][arrayX][arrayY][arrayZ];
		}
	}

	@Override
	public int getMinW() {
		int arrayMinW = - originIndex;
		int valuesMinW;
		if (boundsReached) {
			valuesMinW = arrayMinW;
		} else {
			valuesMinW = arrayMinW + 1;
		}
		return valuesMinW;
	}

	@Override
	public int getMaxW() {
		int arrayMaxW = grid.length - 1 - originIndex;
		int valuesMaxW;
		if (boundsReached) {
			valuesMaxW = arrayMaxW;
		} else {
			valuesMaxW = arrayMaxW - 1;
		}
		return valuesMaxW;
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
	public int getAsymmetricMinW(int x, int y, int z) {
		return Math.max(Math.max(x, y), z);
	}

	@Override
	public int getAsymmetricMaxW(int x, int y, int z) {
		return getAsymmetricMaxW();
	}

	@Override
	public int getAsymmetricMinW() {
		return 0;
	}

	@Override
	public int getAsymmetricMaxW() {
		return getMaxW();
	}

	@Override
	public int getAsymmetricMinX() {
		return 0;
	}

	@Override
	public int getAsymmetricMaxX() {
		return getMaxW();
	}

	@Override
	public int getAsymmetricMinY() {
		return 0;
	}

	@Override
	public int getAsymmetricMaxY() {
		return getMaxW();
	}

	@Override
	public int getAsymmetricMinZ() {
		return 0;
	}
	
	@Override
	public int getAsymmetricMaxZ() {
		return getMaxW();
	}

	@Override
	public int getAsymmetricMinWAtZ(int z) {
		return z;
	}

	@Override
	public int getAsymmetricMinWAtXZ(int x, int z) {
		return x;
	}

	@Override
	public int getAsymmetricMinWAtYZ(int y, int z) {
		return y;
	}

	@Override
	public int getAsymmetricMaxWAtZ(int z) {
		return getAsymmetricMaxW(); //TODO: check actual value? store all max values?
	}

	@Override
	public int getAsymmetricMaxWAtXZ(int x, int z) {
		return getAsymmetricMaxW();
	}

	@Override
	public int getAsymmetricMaxWAtYZ(int y, int z) {
		return getAsymmetricMaxW();
	}

	@Override
	public int getAsymmetricMinXAtZ(int z) {
		return z;
	}

	@Override
	public int getAsymmetricMinXAtWZ(int w, int z) {
		return z;
	}

	@Override
	public int getAsymmetricMinXAtYZ(int y, int z) {
		return y;
	}

	@Override
	public int getAsymmetricMinX(int w, int y, int z) {
		return y;
	}

	@Override
	public int getAsymmetricMaxXAtZ(int z) {
		return getAsymmetricMaxX();
	}

	@Override
	public int getAsymmetricMaxXAtWZ(int w, int z) {
		return Math.min(getAsymmetricMaxX(), w);
	}

	@Override
	public int getAsymmetricMaxXAtYZ(int y, int z) {
		return getAsymmetricMaxX();
	}

	@Override
	public int getAsymmetricMaxX(int w, int y, int z) {
		return Math.min(getAsymmetricMaxX(), w);
	}

	@Override
	public int getAsymmetricMinYAtZ(int z) {
		return z;
	}

	@Override
	public int getAsymmetricMaxYAtWZ(int w, int z) {
		return Math.min(getAsymmetricMaxY(), w);
	}

	@Override
	public int getAsymmetricMinYAtXZ(int x, int z) {
		return z;
	}

	@Override
	public int getAsymmetricMinY(int w, int x, int z) {
		return z;
	}

	@Override
	public int getAsymmetricMaxYAtZ(int z) {
		return getAsymmetricMaxY();
	}

	@Override
	public int getAsymmetricMinYAtWZ(int w, int z) {
		return z;
	}

	@Override
	public int getAsymmetricMaxYAtXZ(int x, int z) {
		return Math.min(getAsymmetricMaxY(), x);
	}

	@Override
	public int getAsymmetricMaxY(int w, int x, int z) {
		return Math.min(getAsymmetricMaxY(), x);
	}

	@Override
	public long getValueAtAsymmetricPosition(int w, int x, int y, int z) {
		return getValueAtPosition(w, x, y, z);
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
	public long getIntialValue() {
		return initialValue;
	}
	
	public static void padEdges(long[][][][] grid, int width, long value) {
		int lengthMinusWith = grid.length - width;
		//min W side
		for (int w = 0; w < width && w < grid.length; w++) {
			for (int x = 0; x < grid.length; x++) {
				for (int y = 0; y < grid.length; y++) {
					for (int z = 0; z < grid.length; z++) {
						grid[w][x][y][z] = value;
					}
				}
			}
		}
		//max W side
		for (int w = lengthMinusWith; w < grid.length; w++) {
			for (int x = 0; x < grid.length; x++) {
				for (int y = 0; y < grid.length; y++) {
					for (int z = 0; z < grid.length; z++) {
						grid[w][x][y][z] = value;
					}
				}
			}
		}
		//min X side
		for (int w = 0; w < grid.length; w++) {
			for (int x = 0; x < width && x < grid.length; x++) {
				for (int y = 0; y < grid.length; y++) {
					for (int z = 0; z < grid.length; z++) {
						grid[w][x][y][z] = value;
					}
				}
			}
		}
		//max X side
		for (int w = 0; w < grid.length; w++) {
			for (int x = lengthMinusWith; x < grid.length; x++) {
				for (int y = 0; y < grid.length; y++) {
					for (int z = 0; z < grid.length; z++) {
						grid[w][x][y][z] = value;
					}
				}
			}
		}
		//min Y side
		for (int w = 0; w < grid.length; w++) {
			for (int x = width; x < lengthMinusWith; x++) {
				for (int y = 0; y < width && y < grid.length; y++) {
					for (int z = 0; z < grid.length; z++) {
						grid[w][x][y][z] = value;
					}
				}
			}
		}
		//max Y side
		for (int w = 0; w < grid.length; w++) {
			for (int x = width; x < lengthMinusWith; x++) {
				for (int y = grid.length - width; y < grid.length; y++) {
					for (int z = 0; z < grid.length; z++) {
						grid[w][x][y][z] = value;
					}
				}
			}
		}
		//min Z side
		for (int w = 0; w < grid.length; w++) {
			for (int x = width; x < lengthMinusWith; x++) {
				for (int y = width; y < grid.length - width; y++) {
					for (int z = 0; z < width && z < grid.length; z++) {
						grid[w][x][y][z] = value;
					}
				}
			}
		}
		//max Z side
		for (int w = 0; w < grid.length; w++) {
			for (int x = width; x < lengthMinusWith; x++) {
				for (int y = width; y < grid.length - width; y++) {
					for (int z = grid.length - width; z < grid[x][y].length; z++) {
						grid[w][x][y][z] = value;
					}
				}
			}
		}
	}

	@Override
	public String getName() {
		return "SpreadIntegerValue4D";
	}

	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialValue + "/" + backgroundValue;
	}
	
	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		Utils.serializeToFile(this, backupPath, backupName);
	}
}