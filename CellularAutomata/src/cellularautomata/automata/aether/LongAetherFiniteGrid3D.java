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
package cellularautomata.automata.aether;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import cellularautomata.Utils;
import cellularautomata.model3d.LongModel3D;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 3D with a finite cuboid-shaped grid and a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class LongAetherFiniteGrid3D implements LongModel3D, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 357391523086633368L;
	
	public static final long MAX_INITIAL_VALUE = Long.MAX_VALUE;
	public static final long MIN_INITIAL_VALUE = -3689348814741910323L;
	
	/** 3D array representing the grid **/
	private long[][][] grid;
	
	private final long singleSourceValue;
	private long step;
	private final int xSide;
	private final int ySide;
	private final int zSide;
	private final int xSideMinusOne;
	private final int ySideMinusOne;
	private final int zSideMinusOne;
	private Boolean changed = null;
	
	public LongAetherFiniteGrid3D(int xSide, int ySide, int zSide, long singleSourceValue) {
		if (xSide < 1 || ySide < 1 || zSide < 1) {
			throw new IllegalArgumentException("Sides cannot be smaller than one.");
		}
		//safety check to prevent exceeding the data type's max value
		if (singleSourceValue < MIN_INITIAL_VALUE) {
			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
		}
		grid = new long[xSide][ySide][zSide];
		grid[0][0][0] = singleSourceValue;
		this.xSide = xSide;
		this.ySide = ySide;
		this.zSide = zSide;
		this.xSideMinusOne = xSide - 1;
		this.ySideMinusOne = ySide - 1;
		this.zSideMinusOne = zSide - 1;
		this.singleSourceValue = singleSourceValue;
		//Set the current step to zero
		step = 0;
	}
	
	/**
	 * Creates an instance restoring a backup
	 * 
	 * @param backupPath the path to the backup file to restore.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 */
	public LongAetherFiniteGrid3D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		LongAetherFiniteGrid3D data = (LongAetherFiniteGrid3D) Utils.deserializeFromFile(backupPath);
		grid = data.grid;
		singleSourceValue = data.singleSourceValue;
		step = data.step;
		xSide = data.xSide;
		ySide = data.ySide;
		zSide = data.zSide;
		xSideMinusOne = data.xSideMinusOne;
		ySideMinusOne = data.ySideMinusOne;
		zSideMinusOne = data.zSideMinusOne;
		changed = data.changed;
	}
	
	@Override
	public Boolean nextStep() {
		//Use new array to store the values of the next step
		long[][][] newGrid = new long[xSide][ySide][zSide];
		boolean changed = false;
		//For every cell
		long[] neighborValues = new long[6];
		int[] sortedNeighborsIndexes = new int[6];
		int[][] neighborCoordinates = new int[6][3];
		for (int x = 0; x < xSide; x++) {
			for (int y = 0; y < ySide; y++) {
				for (int z = 0; z < zSide; z++) {
					long value = grid[x][y][z];
					int relevantNeighborCount = 0;
					long positiveNeighborValue, negativeNeighborValue;
					int positiveNeighborCoord, negativeNeighborCoord;
					//x
					if (x == xSideMinusOne) {
						positiveNeighborCoord = 0;
					} else {
						positiveNeighborCoord = x + 1;
					}
					if (x == 0) {
						negativeNeighborCoord = xSideMinusOne;
					} else {
						negativeNeighborCoord = x - 1;
					}
					positiveNeighborValue = grid[positiveNeighborCoord][y][z];
					if (positiveNeighborValue < value) {
						neighborValues[relevantNeighborCount] = positiveNeighborValue;
						int[] nc = neighborCoordinates[relevantNeighborCount];
						nc[0] = positiveNeighborCoord;
						nc[1] = y;
						nc[2] = z;
						relevantNeighborCount++;
					}	
					negativeNeighborValue = grid[negativeNeighborCoord][y][z];
					if (negativeNeighborValue < value) {
						neighborValues[relevantNeighborCount] = negativeNeighborValue;
						int[] nc = neighborCoordinates[relevantNeighborCount];
						nc[0] = negativeNeighborCoord;
						nc[1] = y;
						nc[2] = z;
						relevantNeighborCount++;
					}
					//y
					if (y == ySideMinusOne) {
						positiveNeighborCoord = 0;
					} else {
						positiveNeighborCoord = y + 1;
					}
					if (y == 0) {
						negativeNeighborCoord = ySideMinusOne;
					} else {
						negativeNeighborCoord = y - 1;
					}
					positiveNeighborValue = grid[x][positiveNeighborCoord][z];
					if (positiveNeighborValue < value) {
						neighborValues[relevantNeighborCount] = positiveNeighborValue;
						int[] nc = neighborCoordinates[relevantNeighborCount];
						nc[0] = x;
						nc[1] = positiveNeighborCoord;
						nc[2] = z;
						relevantNeighborCount++;
					}	
					negativeNeighborValue = grid[x][negativeNeighborCoord][z];
					if (negativeNeighborValue < value) {
						neighborValues[relevantNeighborCount] = negativeNeighborValue;
						int[] nc = neighborCoordinates[relevantNeighborCount];
						nc[0] = x;
						nc[1] = negativeNeighborCoord;
						nc[2] = z;
						relevantNeighborCount++;
					}
					//z
					if (z == zSideMinusOne) {
						positiveNeighborCoord = 0;
					} else {
						positiveNeighborCoord = z + 1;
					}
					if (z == 0) {
						negativeNeighborCoord = zSideMinusOne;
					} else {
						negativeNeighborCoord = z - 1;
					}
					positiveNeighborValue = grid[x][y][positiveNeighborCoord];
					if (positiveNeighborValue < value) {
						neighborValues[relevantNeighborCount] = positiveNeighborValue;
						int[] nc = neighborCoordinates[relevantNeighborCount];
						nc[0] = x;
						nc[1] = y;
						nc[2] = positiveNeighborCoord;
						relevantNeighborCount++;
					}	
					negativeNeighborValue = grid[x][y][negativeNeighborCoord];
					if (negativeNeighborValue < value) {
						neighborValues[relevantNeighborCount] = negativeNeighborValue;
						int[] nc = neighborCoordinates[relevantNeighborCount];
						nc[0] = x;
						nc[1] = y;
						nc[2] = negativeNeighborCoord;
						relevantNeighborCount++;
					}					
					if (relevantNeighborCount > 0) {
						//sort
						Utils.sortDescending(relevantNeighborCount, neighborValues, sortedNeighborsIndexes);
						//divide
						long previousNeighborValue = value;//all relevant neighbors' values are different from the current value
						for (int i = 0; i < relevantNeighborCount; i++) {
							long neighborValue = neighborValues[i];
							if (neighborValue != previousNeighborValue) {
								int shareCount = relevantNeighborCount - i + 1;
								long toShare = value - neighborValue;
								long share = toShare/shareCount;
								if (share != 0) {
									changed = true;
									value = value - toShare + toShare%shareCount + share;
									for (int j = i; j < relevantNeighborCount; j++) {
										int[] nc = neighborCoordinates[sortedNeighborsIndexes[j]];
										newGrid[nc[0]][nc[1]][nc[2]] += share;
									}
								}
								previousNeighborValue = neighborValue;
							}
						}	
					}					
					newGrid[x][y][z] += value;
				}
			}
		}
		//Replace the old array with the new one
		grid = newGrid;
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
		//Transform passed coordinates to grid coordinates
		x = x < 0 ? xSideMinusOne + x%xSide : x%xSide;
		y = y < 0 ? ySideMinusOne + y%ySide : y%ySide;
		z = z < 0 ? zSideMinusOne + z%zSide : z%zSide;
		return grid[x][y][z];
	}
	
	@Override
	public int getMinX() {
		return 0;
	}
	
	@Override
	public int getMaxX() {
		return xSideMinusOne;
	}
	
	@Override
	public int getMinY() {
		return 0;
	}
	
	@Override
	public int getMaxY() {
		return ySideMinusOne;
	}
	
	@Override
	public int getMinZ() {
		return 0;
	}
	
	@Override
	public int getMaxZ() {
		return zSideMinusOne;
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
		return singleSourceValue;
	}

	@Override
	public String getName() {
		return "Aether";
	}

	@Override
	public String getSubfolderPath() {
		return getName() + "/3D/finite_grid/" + xSide + "x" + ySide + "x" + zSide 
				+ "/" + singleSourceValue;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		Utils.serializeToFile(this, backupPath, backupName);
	}

}