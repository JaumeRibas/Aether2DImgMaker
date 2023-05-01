/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2023 Jaume Ribas

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
package cellularautomata.automata.siv;

import java.io.FileNotFoundException;
import java.io.IOException;
import cellularautomata.Utils;
import cellularautomata.model3d.IsotropicCubicModelA;
import cellularautomata.model3d.SymmetricLongModel3D;

/**
 * Simplified implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/SIV-Cellular-Automaton-Definition">Spread Integer Value</a> cellular automaton in 3D, with a single source initial configuration, for review and testing purposes
 * 
 * @author Jaume
 *
 */
public class SimpleSpreadIntegerValue3D implements SymmetricLongModel3D, IsotropicCubicModelA {
	
	/** 3D array representing the grid **/
	private long[][][] grid;
	
	private final long initialValue;
	private final long backgroundValue;
	private long step;
	
	/** The indexes of the origin within the array */
	private int originIndex;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	
	/** Whether or not the state of the model changed between the current and the previous step **/
	private Boolean changed = null;
	
	/**
	 * Creates an instance with the given initial value
	 *  
	 * @param initialValue the value at the origin at step 0
	 * @param backgroundValue the value at all other positions of the grid
	 */
	public SimpleSpreadIntegerValue3D(long initialValue, long backgroundValue) {
		this.initialValue = initialValue;
		this.backgroundValue = backgroundValue;
		//initial side of the array, will be increased as needed
		int side = 5;
		grid = new long[side][side][side];
		originIndex = (side - 1)/2;
		if (backgroundValue != 0) {
			Utils.fillArray(grid, backgroundValue);
		}
		grid[originIndex][originIndex][originIndex] = initialValue;
		boundsReached = false;
		//Set the current step to zero
		step = 0;
	}
	
	@Override
	public Boolean nextStep() {
		//Use new array to store the values of the next step
		long[][][] newGrid = null;
		//The offset between the indexes of the new and old array
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new long[grid.length + 2][grid.length + 2][grid.length + 2];
			if (backgroundValue != 0) {
				fillEdges(newGrid, 1, backgroundValue);
			}
			indexOffset = 1;
		} else {
			newGrid = new long[grid.length][grid.length][grid.length];
		}
		boolean changed = false;
		//For every cell
		for (int i = 0; i < this.grid.length; i++) {
			for (int j = 0; j < this.grid.length; j++) {
				for (int k = 0; k < this.grid.length; k++) {
					long value = this.grid[i][j][k];
					if (value != 0) {
						long greaterXNeighborValue;
						long lowerXNeighborValue;
						if (i < grid.length - 1) {
							greaterXNeighborValue = grid[i + 1][j][k];
							if (i > 0)
								lowerXNeighborValue = grid[i - 1][j][k];
							else
								lowerXNeighborValue = backgroundValue;
						} else {
							greaterXNeighborValue = backgroundValue;
							lowerXNeighborValue = grid[i - 1][j][k];
						}
						long greaterYNeighborValue;
						long lowerYNeighborValue;
						if (j < grid.length - 1) {
							greaterYNeighborValue = grid[i][j + 1][k];
							if (j > 0)
								lowerYNeighborValue = grid[i][j - 1][k];
							else
								lowerYNeighborValue = backgroundValue;
						} else {
							greaterYNeighborValue = backgroundValue;
							lowerYNeighborValue = grid[i][j - 1][k];
						}						
						long greaterZNeighborValue;
						long lowerZNeighborValue;
						if (k < grid.length - 1) {
							greaterZNeighborValue = grid[i][j][k + 1];
							if (k > 0)
								lowerZNeighborValue = grid[i][j][k - 1];
							else
								lowerZNeighborValue = backgroundValue;
						} else {
							greaterZNeighborValue = backgroundValue;
							lowerZNeighborValue = grid[i][j][k - 1];
						}						
						boolean isGreaterYNeighborEqual = value == greaterYNeighborValue, isLowerYNeighborEqual = value == lowerYNeighborValue, 
								isGreaterXNeighborEqual = value == greaterXNeighborValue, isLowerXNeighborEqual = value == lowerXNeighborValue,
								isGreaterZNeighborEqual = value == greaterZNeighborValue, isLowerZNeighborEqual = value == lowerZNeighborValue;
						//If the current cell is equal to its neighbors, the algorithm has no effect
						if (!(isGreaterYNeighborEqual && isLowerYNeighborEqual 
								&& isGreaterXNeighborEqual && isLowerXNeighborEqual 
								&& isGreaterZNeighborEqual && isLowerZNeighborEqual)) {
							//Divide its value by 7 (using integer division)
							long share = value/7;
							if (share != 0) {
								//If any share is not zero, the state changes
								changed = true;
								//Add the share and the remainder to the corresponding cell in the new array
								newGrid[i + indexOffset][j + indexOffset][k + indexOffset] += value%7 + share;
								//Add the share to the neighboring cells
								//If the neighbor's value is equal to the current value, add the share to the current cell instead
								if (isGreaterXNeighborEqual)
									newGrid[i + indexOffset][j + indexOffset][k + indexOffset] += share;
								else
									newGrid[i + indexOffset + 1][j + indexOffset][k + indexOffset] += share;
								if (isLowerXNeighborEqual)
									newGrid[i + indexOffset][j + indexOffset][k + indexOffset] += share;
								else
									newGrid[i + indexOffset - 1][j + indexOffset][k + indexOffset] += share;
								if (isGreaterYNeighborEqual)
									newGrid[i + indexOffset][j + indexOffset][k + indexOffset] += share;
								else
									newGrid[i + indexOffset][j + indexOffset + 1][k + indexOffset] += share;
								if (isLowerYNeighborEqual)
									newGrid[i + indexOffset][j + indexOffset][k + indexOffset] += share;
								else
									newGrid[i + indexOffset][j + indexOffset - 1][k + indexOffset] += share;				
								if (isGreaterZNeighborEqual)
									newGrid[i + indexOffset][j + indexOffset][k + indexOffset] += share;
								else
									newGrid[i + indexOffset][j + indexOffset][k + indexOffset + 1] += share;
								if (isLowerZNeighborEqual)
									newGrid[i + indexOffset][j + indexOffset][k + indexOffset] += share;
								else
									newGrid[i + indexOffset][j + indexOffset][k + indexOffset - 1] += share;
								//Check whether or not we reached the edge of the array
								if (i == 1 || i == this.grid.length - 2 
										|| j == 1 || j == this.grid.length - 2
										|| k == 1 || k == this.grid.length - 2) {
									boundsReached = true;
								}
							} else {
								//If the share is zero, just add the value to the corresponding cell in the new array
								newGrid[i + indexOffset][j + indexOffset][k + indexOffset] += value;
							}
						} else {
							newGrid[i + indexOffset][j + indexOffset][k + indexOffset] += value;
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
		step++;
		this.changed = changed;
		//Return whether or not the state of the grid changed
		return changed;
	}

	@Override
	public Boolean isChanged() {
		return changed;
	}
	
	@Override
	public long getFromPosition(int x, int y, int z) {	
		int i = originIndex + x;
		int j = originIndex + y;
		int k = originIndex + z;
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
	
	private static void fillEdges(long[][][] grid, int width, long value) {
		int lengthMinusWith = grid.length - width;
		//min X side
		for (int i = 0; i < width && i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				for (int k = 0; k < grid.length; k++) {
					grid[i][j][k] = value;
				}
			}
		}
		//max X side
		for (int i = lengthMinusWith; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				for (int k = 0; k < grid.length; k++) {
					grid[i][j][k] = value;
				}
			}
		}
		//min Y side
		for (int i = width; i < lengthMinusWith; i++) {
			for (int j = 0; j < width && j < grid.length; j++) {
				for (int k = 0; k < grid.length; k++) {
					grid[i][j][k] = value;
				}
			}
		}
		//max Y side
		for (int i = width; i < lengthMinusWith; i++) {
			for (int j = grid.length - width; j < grid.length; j++) {
				for (int k = 0; k < grid.length; k++) {
					grid[i][j][k] = value;
				}
			}
		}
		//min Z side
		for (int i = width; i < lengthMinusWith; i++) {
			for (int j = width; j < grid.length - width; j++) {
				for (int k = 0; k < width && k < grid.length; k++) {
					grid[i][j][k] = value;
				}
			}
		}
		//max Z side
		for (int i = width; i < lengthMinusWith; i++) {
			for (int j = width; j < grid.length - width; j++) {
				for (int k = grid.length - width; k < grid.length; k++) {
					grid[i][j][k] = value;
				}
			}
		}
	}

	@Override
	public String getName() {
		return "SpreadIntegerValue";
	}

	@Override
	public String getSubfolderPath() {
		return getName() + "/3D/" + initialValue + "/" + backgroundValue;
	}
}