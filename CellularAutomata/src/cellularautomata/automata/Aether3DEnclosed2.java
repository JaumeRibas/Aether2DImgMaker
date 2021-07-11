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
import cellularautomata.evolvinggrid.EvolvingLongGrid3D;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 3D with a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class Aether3DEnclosed2 implements EvolvingLongGrid3D {
	
	public static final long MAX_INITIAL_VALUE = Long.MAX_VALUE;
	public static final long MIN_INITIAL_VALUE = Long.valueOf("-3689348814741910323");
	
	/** 3D array representing the grid **/
	private long[][][] grid;
	
	private long singleSourceValue;
	private long currentStep;
	private int xSide;
	private int ySide;
	private int zSide;
	private int xSideMinusOne;
	private int ySideMinusOne;
	private int zSideMinusOne;
	private int singleSourceX;
	private int singleSourceY;
	private int singleSourceZ;
	
	public Aether3DEnclosed2(int xSide, int ySide, int zSide, 
			long singleSourceValue, int singleSourceX, int singleSourceY, int singleSourceZ) {
		if (xSide < 2 || ySide < 2 || zSide < 2) {
			throw new IllegalArgumentException("Sides cannot be smaller than 2.");
		}
		//safety check to prevent exceeding the data type's max value
		if (singleSourceValue < MIN_INITIAL_VALUE) {
			throw new IllegalArgumentException("Initial value cannot be smaller than -3,689,348,814,741,910,323. Use a greater initial value or a different implementation.");
		}
		grid = new long[xSide][ySide][zSide];
		grid[singleSourceX][singleSourceY][singleSourceZ] = singleSourceValue;
		this.xSide = xSide;
		this.ySide = ySide;
		this.zSide = zSide;
		this.xSideMinusOne = xSide - 1;
		this.ySideMinusOne = ySide - 1;
		this.zSideMinusOne = zSide - 1;
		this.singleSourceValue = singleSourceValue;
		this.singleSourceX = singleSourceX;
		this.singleSourceY = singleSourceY;
		this.singleSourceZ = singleSourceZ;
		//Set the current step to zero
		currentStep = 0;
	}
	
	@Override
	public boolean nextStep(){
		//Use new array to store the values of the next step
		long[][][] newGrid = new long[xSide][ySide][zSide];
		boolean changed = false;
		//For every position
		long[] neighborValues = new long[6];
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
						negativeNeighborCoord = x - 1;
					} else {
						positiveNeighborCoord = x + 1;
						if (x == 0) {
							negativeNeighborCoord = xSideMinusOne;
						} else {
							negativeNeighborCoord = x - 1;
						}
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
						negativeNeighborCoord = y - 1;
					} else {
						positiveNeighborCoord = y + 1;
						if (y == 0) {
							negativeNeighborCoord = ySideMinusOne;
						} else {
							negativeNeighborCoord = y - 1;
						}
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
						negativeNeighborCoord = z - 1;
					} else {
						positiveNeighborCoord = z + 1;
						if (z == 0) {
							negativeNeighborCoord = zSideMinusOne;
						} else {
							negativeNeighborCoord = z - 1;
						}
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
						Utils.sortNeighborsByValueDesc(relevantNeighborCount, neighborValues, neighborCoordinates);
						//divide
						boolean isFirstNeighbor = true;
						long previousNeighborValue = 0;
						for (int i = 0; i < relevantNeighborCount; i++,isFirstNeighbor = false) {
							long neighborValue = neighborValues[i];
							if (neighborValue != previousNeighborValue || isFirstNeighbor) {
								int shareCount = relevantNeighborCount - i + 1;
								long toShare = value - neighborValue;
								long share = toShare/shareCount;
								if (share != 0) {
									changed = true;
									value = value - toShare + toShare%shareCount + share;
									for (int j = i; j < relevantNeighborCount; j++) {
										int[] nc = neighborCoordinates[j];
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
		currentStep++;
		//Return whether or not the state of the grid changed
		return changed;
	}
	
	@Override
	public long getFromPosition(int x, int y, int z){
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
		return currentStep;
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
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		return "Aether3DEnclosed";
	}

	@Override
	public String getSubFolderPath() {
		return getName() + "/" + xSide + "x" + ySide + "x" + zSide 
				+ "/(" + singleSourceX + "," + singleSourceY + "," + singleSourceZ + ")=" + singleSourceValue;
	}

}