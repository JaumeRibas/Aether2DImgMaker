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
package cellularautomata.automata.siv;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.Utils;
import cellularautomata.model4d.IsotropicHypercubicModel4DA;
import cellularautomata.model4d.SymmetricLongModel4D;

/**
 * Simplified implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/SIV-Cellular-Automaton-Definition">Spread Integer Value</a> cellular automaton in 4D, with a single source initial configuration, for review and testing purposes
 * 
 * @author Jaume
 *
 */
public class SpreadIntegerValueSimple4D implements SymmetricLongModel4D, IsotropicHypercubicModel4DA {	
	
	private long[][][][] grid;
	
	private final long initialValue;
	private final long backgroundValue;
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
	 * @param backgroundValue the value at all other positions of the grid
	 */
	public SpreadIntegerValueSimple4D(long initialValue, long backgroundValue) {
		this.initialValue = initialValue;
		this.backgroundValue = backgroundValue;
		int side = 5;
		grid = new long[side][side][side][side];
		if (backgroundValue != 0) {
			Utils.fillArray(grid, backgroundValue);
		}
		originIndex = (side - 1)/2;
		grid[originIndex][originIndex][originIndex][originIndex] = this.initialValue;
		boundsReached = false;
		//Set the current step to zero
		step = 0;
	}
	
	@Override
	public Boolean nextStep() {
		//Use new array to store the values of the next step
		long[][][][] newGrid = null;
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new long[grid.length + 2][grid.length + 2][grid.length + 2][grid.length + 2];
			if (backgroundValue != 0) {
				fillEdges(newGrid, 1, backgroundValue);
			}
			//The offset between the indexes of the new and old array
			indexOffset = 1;
		} else {
			newGrid = new long[grid.length][grid.length][grid.length][grid.length];
		}
		boolean changed = false;
		//For every cell
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				for (int k = 0; k < grid.length; k++) {
					for (int l = 0; l < grid.length; l++) {
						long value = this.grid[i][j][k][l];
						if (value != 0) {
							long greaterWNeighborValue;
							long lowerWNeighborValue;
							if (i < grid.length - 1) {
								greaterWNeighborValue = grid[i + 1][j][k][l];
								if (i > 0)
									lowerWNeighborValue = grid[i - 1][j][k][l];
								else
									lowerWNeighborValue = backgroundValue;
							} else {
								greaterWNeighborValue = backgroundValue;
								lowerWNeighborValue = grid[i - 1][j][k][l];
							}
							long greaterXNeighborValue;
							long lowerXNeighborValue;
							if (j < grid.length - 1) {
								greaterXNeighborValue = grid[i][j + 1][k][l];
								if (j > 0)
									lowerXNeighborValue = grid[i][j - 1][k][l];
								else
									lowerXNeighborValue = backgroundValue;
							} else {
								greaterXNeighborValue = backgroundValue;
								lowerXNeighborValue = grid[i][j - 1][k][l];
							}
							long greaterYNeighborValue;
							long lowerYNeighborValue;
							if (k < grid.length - 1) {
								greaterYNeighborValue = grid[i][j][k + 1][l];
								if (k > 0)
									lowerYNeighborValue = grid[i][j][k - 1][l];
								else
									lowerYNeighborValue = backgroundValue;
							} else {
								greaterYNeighborValue = backgroundValue;
								lowerYNeighborValue = grid[i][j][k - 1][l];
							}						
							long greaterZNeighborValue;
							long lowerZNeighborValue;
							if (l < grid.length - 1) {
								greaterZNeighborValue = grid[i][j][k][l + 1];
								if (l > 0)
									lowerZNeighborValue = grid[i][j][k][l - 1];
								else
									lowerZNeighborValue = backgroundValue;
							} else {
								greaterZNeighborValue = backgroundValue;
								lowerZNeighborValue = grid[i][j][k][l - 1];
							}						
							boolean isGreaterWNeighborEqual = value == greaterWNeighborValue, isLowerWNeighborEqual = value == lowerWNeighborValue,
									isGreaterXNeighborEqual = value == greaterXNeighborValue, isLowerXNeighborEqual = value == lowerXNeighborValue,
									isGreaterYNeighborEqual = value == greaterYNeighborValue, isLowerYNeighborEqual = value == lowerYNeighborValue, 
									isGreaterZNeighborEqual = value == greaterZNeighborValue, isLowerZNeighborEqual = value == lowerZNeighborValue;
							//If the current cell is equal to its neighbors, the algorithm has no effect
							if (!(isGreaterWNeighborEqual && isLowerWNeighborEqual 
									&& isGreaterXNeighborEqual && isLowerXNeighborEqual 
									&& isGreaterYNeighborEqual && isLowerYNeighborEqual 
									&& isGreaterZNeighborEqual && isLowerZNeighborEqual)) {
								//Divide its value by 9 (using integer division)
								long share = value/9;
								if (share != 0) {
									//If any share is not zero, the state changes
									changed = true;
									//Add the share and the remainder to the corresponding cell in the new array
									newGrid[i + indexOffset][j + indexOffset][k + indexOffset][l + indexOffset] += value%9 + share;
									//Add the share to the neighboring cells
									//If the neighbor's value is equal to the current value, add the share to the current cell instead
									if (isGreaterWNeighborEqual)
										newGrid[i + indexOffset][j + indexOffset][k + indexOffset][l + indexOffset] += share;
									else
										newGrid[i + indexOffset + 1][j + indexOffset][k + indexOffset][l + indexOffset] += share;
									if (isLowerWNeighborEqual)
										newGrid[i + indexOffset][j + indexOffset][k + indexOffset][l + indexOffset] += share;
									else
										newGrid[i + indexOffset - 1][j + indexOffset][k + indexOffset][l + indexOffset] += share;
									if (isGreaterXNeighborEqual)
										newGrid[i + indexOffset][j + indexOffset][k + indexOffset][l + indexOffset] += share;
									else
										newGrid[i + indexOffset][j + indexOffset + 1][k + indexOffset][l + indexOffset] += share;
									if (isLowerXNeighborEqual)
										newGrid[i + indexOffset][j + indexOffset][k + indexOffset][l + indexOffset] += share;
									else
										newGrid[i + indexOffset][j + indexOffset - 1][k + indexOffset][l + indexOffset] += share;
									if (isGreaterYNeighborEqual)
										newGrid[i + indexOffset][j + indexOffset][k + indexOffset][l + indexOffset] += share;
									else
										newGrid[i + indexOffset][j + indexOffset][k + indexOffset + 1][l + indexOffset] += share;
									if (isLowerYNeighborEqual)
										newGrid[i + indexOffset][j + indexOffset][k + indexOffset][l + indexOffset] += share;
									else
										newGrid[i + indexOffset][j + indexOffset][k + indexOffset - 1][l + indexOffset] += share;				
									if (isGreaterZNeighborEqual)
										newGrid[i + indexOffset][j + indexOffset][k + indexOffset][l + indexOffset] += share;
									else
										newGrid[i + indexOffset][j + indexOffset][k + indexOffset][l + indexOffset + 1] += share;
									if (isLowerZNeighborEqual)
										newGrid[i + indexOffset][j + indexOffset][k + indexOffset][l + indexOffset] += share;
									else
										newGrid[i + indexOffset][j + indexOffset][k + indexOffset][l + indexOffset - 1] += share;
									//Check whether or not we reached the edge of the array
									if (i == 1 || i == this.grid.length - 2 
											|| j == 1 || j == this.grid.length - 2 
											|| k == 1 || k == this.grid.length - 2
											|| l == 1 || l == this.grid.length - 2) {
										boundsReached = true;
									}
								} else {
									//If the share is zero, just add the value to the corresponding cell in the new array
									newGrid[i + indexOffset][j + indexOffset][k + indexOffset][l + indexOffset] += value;
								}
							} else {
								newGrid[i + indexOffset][j + indexOffset][k + indexOffset][l + indexOffset] += value;
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
	public long getFromPosition(int w, int x, int y, int z) {
		int i = originIndex + w;
		int j = originIndex + x;
		int k = originIndex + y;
		int l = originIndex + z;
		if (i < 0 || i > grid.length - 1 
				|| j < 0 || j > grid.length - 1 
				|| k < 0 || k > grid.length - 1
				|| l < 0 || l > grid.length - 1) {
			//If the entered position is outside the array the value will be the background value
			return 0;
		} else {
			//Note that the indexes whose value hasn't been defined have value zero by default
			return grid[i][j][k][l];
		}
	}
	
	@Override
	public long getFromAsymmetricPosition(int w, int x, int y, int z) {
		return getFromPosition(w, x, y, z);
	}

	@Override
	public int getAsymmetricMaxW() {
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
	public long getStep() {
		return step;
	}
	
	/**
	 * Returns the initial value
	 * 
	 * @return the value at the origin at step 0
	 */
	public long getIntialValue() {
		return initialValue;
	}
	
	private static void fillEdges(long[][][][] grid, int width, long value) {
		int lengthMinusWith = grid.length - width;
		//min W side
		for (int i = 0; i < width && i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				for (int k = 0; k < grid.length; k++) {
					for (int l = 0; l < grid.length; l++) {
						grid[i][j][k][l] = value;
					}
				}
			}
		}
		//max W side
		for (int i = lengthMinusWith; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				for (int k = 0; k < grid.length; k++) {
					for (int l = 0; l < grid.length; l++) {
						grid[i][j][k][l] = value;
					}
				}
			}
		}
		//min X side
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < width && j < grid.length; j++) {
				for (int k = 0; k < grid.length; k++) {
					for (int l = 0; l < grid.length; l++) {
						grid[i][j][k][l] = value;
					}
				}
			}
		}
		//max X side
		for (int i = 0; i < grid.length; i++) {
			for (int j = lengthMinusWith; j < grid.length; j++) {
				for (int k = 0; k < grid.length; k++) {
					for (int l = 0; l < grid.length; l++) {
						grid[i][j][k][l] = value;
					}
				}
			}
		}
		//min Y side
		for (int i = 0; i < grid.length; i++) {
			for (int j = width; j < lengthMinusWith; j++) {
				for (int k = 0; k < width && k < grid.length; k++) {
					for (int l = 0; l < grid.length; l++) {
						grid[i][j][k][l] = value;
					}
				}
			}
		}
		//max Y side
		for (int i = 0; i < grid.length; i++) {
			for (int j = width; j < lengthMinusWith; j++) {
				for (int k = grid.length - width; k < grid.length; k++) {
					for (int l = 0; l < grid.length; l++) {
						grid[i][j][k][l] = value;
					}
				}
			}
		}
		//min Z side
		for (int i = 0; i < grid.length; i++) {
			for (int j = width; j < lengthMinusWith; j++) {
				for (int k = width; k < grid.length - width; k++) {
					for (int l = 0; l < width && l < grid.length; l++) {
						grid[i][j][k][l] = value;
					}
				}
			}
		}
		//max Z side
		for (int i = 0; i < grid.length; i++) {
			for (int j = width; j < lengthMinusWith; j++) {
				for (int k = width; k < grid.length - width; k++) {
					for (int l = grid.length - width; l < grid.length; l++) {
						grid[i][j][k][l] = value;
					}
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
		return getName() + "/4D/" + initialValue + "/" + backgroundValue;
	}
	
	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}
}