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
package cellularautomata.automata.aether;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import cellularautomata.Utils;
import cellularautomata.model3d.LongModel3D;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 3D with a bounded cuboid-shaped grid and a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class Aether3DBoundedGrid implements LongModel3D, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7567346581581646585L;
	
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
	private final int singleSourceX;
	private final int singleSourceY;
	private final int singleSourceZ;
	private Boolean changed = null;
	
	/**
	 * <p>Creates an instance with the given grid sides and single source value.</p>
	 * <p>The minimum coordinate in all axes is 0, therefore, each coordinate of the single source must be in the [0, side) range.</p> 
	 * 
	 * @param xSide
	 * @param ySide
	 * @param zSide
	 * @param singleSourceValue
	 * @param singleSourceX
	 * @param singleSourceY
	 * @param singleSourceZ
	 */
	public Aether3DBoundedGrid(int xSide, int ySide, int zSide, 
			long singleSourceValue, int singleSourceX, int singleSourceY, int singleSourceZ) {
		if (xSide < 1 || ySide < 1 || zSide < 1) {
			throw new IllegalArgumentException("Sides cannot be smaller than one.");
		}
		//safety check to prevent exceeding the data type's max value
		if (singleSourceValue < MIN_INITIAL_VALUE) {
			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
		}
		this.xSide = xSide;
		this.ySide = ySide;
		this.zSide = zSide;
		this.xSideMinusOne = xSide - 1;
		this.ySideMinusOne = ySide - 1;
		this.zSideMinusOne = zSide - 1;
		if (singleSourceX < 0 || singleSourceX > xSideMinusOne) {
			throw new IllegalArgumentException("Single source x-coordinate out of bounds.");
		}
		if (singleSourceY < 0 || singleSourceY > ySideMinusOne) {
			throw new IllegalArgumentException("Single source y-coordinate out of bounds.");
		}
		if (singleSourceZ < 0 || singleSourceZ > zSideMinusOne) {
			throw new IllegalArgumentException("Single source z-coordinate out of bounds.");
		}
		grid = new long[xSide][ySide][zSide];
		grid[singleSourceX][singleSourceY][singleSourceZ] = singleSourceValue;
		this.singleSourceValue = singleSourceValue;
		this.singleSourceX = singleSourceX;
		this.singleSourceY = singleSourceY;
		this.singleSourceZ = singleSourceZ;
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
	public Aether3DBoundedGrid(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		Aether3DBoundedGrid data = (Aether3DBoundedGrid) Utils.deserializeFromFile(backupPath);
		grid = data.grid;
		singleSourceValue = data.singleSourceValue;
		step = data.step;
		xSide = data.xSide;
		ySide = data.ySide;
		zSide = data.zSide;
		xSideMinusOne = data.xSideMinusOne;
		ySideMinusOne = data.ySideMinusOne;
		zSideMinusOne = data.zSideMinusOne;
		singleSourceX = data.singleSourceX;
		singleSourceY = data.singleSourceY;
		singleSourceZ = data.singleSourceZ;
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
					long neighborValue;
					int neighborCoord;
					//x
					if (x != xSideMinusOne) {
						neighborCoord = x + 1;
						neighborValue = grid[neighborCoord][y][z];
						if (neighborValue < value) {
							neighborValues[relevantNeighborCount] = neighborValue;
							int[] nc = neighborCoordinates[relevantNeighborCount];
							nc[0] = neighborCoord;
							nc[1] = y;
							nc[2] = z;
							relevantNeighborCount++;
						}
					}
					if (x != 0) {
						neighborCoord = x - 1;
						neighborValue = grid[neighborCoord][y][z];
						if (neighborValue < value) {
							neighborValues[relevantNeighborCount] = neighborValue;
							int[] nc = neighborCoordinates[relevantNeighborCount];
							nc[0] = neighborCoord;
							nc[1] = y;
							nc[2] = z;
							relevantNeighborCount++;
						}
					}
					//y
					if (y != ySideMinusOne) {
						neighborCoord = y + 1;
						neighborValue = grid[x][neighborCoord][z];
						if (neighborValue < value) {
							neighborValues[relevantNeighborCount] = neighborValue;
							int[] nc = neighborCoordinates[relevantNeighborCount];
							nc[0] = x;
							nc[1] = neighborCoord;
							nc[2] = z;
							relevantNeighborCount++;
						}
					}
					if (y != 0) {
						neighborCoord = y - 1;
						neighborValue = grid[x][neighborCoord][z];
						if (neighborValue < value) {
							neighborValues[relevantNeighborCount] = neighborValue;
							int[] nc = neighborCoordinates[relevantNeighborCount];
							nc[0] = x;
							nc[1] = neighborCoord;
							nc[2] = z;
							relevantNeighborCount++;
						}
					}
					//z
					if (z != zSideMinusOne) {
						neighborCoord = z + 1;
						neighborValue = grid[x][y][neighborCoord];
						if (neighborValue < value) {
							neighborValues[relevantNeighborCount] = neighborValue;
							int[] nc = neighborCoordinates[relevantNeighborCount];
							nc[0] = x;
							nc[1] = y;
							nc[2] = neighborCoord;
							relevantNeighborCount++;
						}
					}
					if (z != 0) {
						neighborCoord = z - 1;
						neighborValue = grid[x][y][neighborCoord];
						if (neighborValue < value) {
							neighborValues[relevantNeighborCount] = neighborValue;
							int[] nc = neighborCoordinates[relevantNeighborCount];
							nc[0] = x;
							nc[1] = y;
							nc[2] = neighborCoord;
							relevantNeighborCount++;
						}
					}					
					if (relevantNeighborCount > 0) {
						//sort
						Utils.sortDescending(relevantNeighborCount, neighborValues, sortedNeighborsIndexes);
						//divide
						boolean isFirstNeighbor = true;
						long previousNeighborValue = 0;
						for (int i = 0; i < relevantNeighborCount; i++,isFirstNeighbor = false) {
							neighborValue = neighborValues[i];
							if (neighborValue != previousNeighborValue || isFirstNeighbor) {
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
	 * @return the single source value
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
		return getName() + "/3D/bounded_grid/" + xSide + "x" + ySide + "x" + zSide 
				+ "/(" + singleSourceX + "," + singleSourceY + "," + singleSourceZ + ")=" + singleSourceValue;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		Utils.serializeToFile(this, backupPath, backupName);
	}

}