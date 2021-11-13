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
package cellularautomata.automata.nearaether;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import cellularautomata.Utils;
import cellularautomata.grid3d.IsotropicCubicGrid3DA;
import cellularautomata.model3d.SymmetricIntModel3D;

/**
 * Implementation of a cellular automaton very similar to <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> to showcase its uniqueness.
 * 
 * @author Jaume
 *
 */
public class IntNearAether1_3D implements SymmetricIntModel3D, IsotropicCubicGrid3DA, Serializable {
	
	public static final int MAX_INITIAL_VALUE = Integer.MAX_VALUE;
	public static final int MIN_INITIAL_VALUE = -858993459;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1151204213779876612L;
	
	private static final byte UP = 0;
	private static final byte DOWN = 1;
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;
	private static final byte FRONT = 4;
	private static final byte BACK = 5;

	/** A 3D array representing the grid */
	private int[][][] grid;
	
	private int initialValue;
	private long step;

	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;

	private int maxXMinusOne;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public IntNearAether1_3D(int initialValue) {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of int type
			throw new IllegalArgumentException("Initial value cannot be smaller than -858,993,459. Use a greater initial value or a different implementation.");
		}
		this.initialValue = initialValue;
		grid = Utils.buildAnisotropic3DIntArray(3);
		grid[0][0][0] = this.initialValue;
		boundsReached = false;
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
	public IntNearAether1_3D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		IntNearAether1_3D data = (IntNearAether1_3D) Utils.deserializeFromFile(backupPath);
		initialValue = data.initialValue;
		grid = data.grid;
		maxXMinusOne = data.maxXMinusOne;
		boundsReached = data.boundsReached;
		step = data.step;
	}
	
	@Override
	public boolean nextStep(){
		int[][][] newGrid = null;
		if (boundsReached) {
			boundsReached = false;
			newGrid = new int[grid.length + 1][][];
		} else {
			newGrid = new int[grid.length][][];
		}
		maxXMinusOne = newGrid.length - 2;
		boolean changed = false;
		newGrid[0] = Utils.buildAnisotropic2DIntArray(1);
		boolean first = true;
		int[] neighborValues = new int[6];
		byte[] neighborDirections = new byte[6];
		for (int x = 0, nextX = 1; x < grid.length; x = nextX, nextX++, first = false) {
			if (nextX < newGrid.length) {
				newGrid[nextX] = Utils.buildAnisotropic2DIntArray(nextX + 1);
			}
			for (int y = 0; y <= x; y++) {
				for (int z = 0; z <= y; z++) {
					int value = grid[x][y][z];
					int relevantNeighborCount = 0;
					int neighborValue;
					neighborValue = getFromPosition(x + 1, y, z);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = RIGHT;
						relevantNeighborCount++;
					}
					neighborValue = getFromPosition(x - 1, y, z);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = LEFT;
						relevantNeighborCount++;
					}
					neighborValue = getFromPosition(x, y + 1, z);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = UP;
						relevantNeighborCount++;
					}
					neighborValue = getFromPosition(x, y - 1, z);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = DOWN;
						relevantNeighborCount++;
					}
					neighborValue = getFromPosition(x, y, z + 1);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = FRONT;
						relevantNeighborCount++;
					}
					neighborValue = getFromPosition(x, y, z - 1);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = BACK;
						relevantNeighborCount++;
					}
					
					if (relevantNeighborCount > 0) {
						//sort
						boolean sorted = false;
						while (!sorted) {
							sorted = true;
							for (int i = relevantNeighborCount - 2; i >= 0; i--) {
								if (neighborValues[i] < neighborValues[i+1]) {
									sorted = false;
									int valSwap = neighborValues[i];
									neighborValues[i] = neighborValues[i+1];
									neighborValues[i+1] = valSwap;
									byte dirSwap = neighborDirections[i];
									neighborDirections[i] = neighborDirections[i+1];
									neighborDirections[i+1] = dirSwap;
								}
							}
						}
						//divide
						boolean isFirstNeighbor = true;
						int previousNeighborValue = 0;
						for (int i = 0; i < relevantNeighborCount; i++,isFirstNeighbor = false) {
							neighborValue = neighborValues[i];
							if (neighborValue != previousNeighborValue || isFirstNeighbor) {
								int shareCount = relevantNeighborCount - i + 1;
								int toShare = value - neighborValue;
								int share = toShare/shareCount;
								if (share != 0) {
									neighborValue += share;//difference with AE
									changed = true;
									value = value - toShare + toShare%shareCount + share;
									for (int j = i; j < relevantNeighborCount; j++) {
										addToNeighbor(newGrid, x, y, z, neighborDirections[j], share);
										neighborValues[j] += share;//difference with AE
									}
								}
								previousNeighborValue = neighborValue;
							}
						}	
					}					
					newGrid[x][y][z] += value;
				}
			}
			if (!first) {
				grid[x-1] = null;
			}
		}
		grid = newGrid;
		step++;
		return changed;
	}
	
	private void addToNeighbor(int grid[][][], int x, int y, int z, byte direction, int value) {
		switch(direction) {
		case RIGHT:
			addRight(grid, x, y, z, value);
			break;
		case LEFT:
			addLeft(grid, x, y, z, value);
			break;
		case UP:
			addUp(grid, x, y, z, value);
			break;
		case DOWN:
			addDown(grid, x, y, z, value);
			break;
		case FRONT:
			addFront(grid, x, y, z, value);
			break;
		case BACK:
			addBack(grid, x, y, z, value);
			break;
		}
	}
	
	private void addRight(int[][][] grid, int x, int y, int z, int value) {
		grid[x+1][y][z] += value;
		if (x >= maxXMinusOne) {
			boundsReached = true;
		}
	}
	
	private void addLeft(int[][][] grid, int x, int y, int z, int value) {
		if (x > y) {
			long valueToAdd = value;
			if (y == x - 1) {
				valueToAdd += value;
				if (z == y) {
					valueToAdd += value;
					if (x == 1) {
						valueToAdd += 3*value;
					}
				}
			}
			grid[x-1][y][z] += valueToAdd;
		}
		if (x >= maxXMinusOne) {
			boundsReached = true;
		}
	}
	
	private void addUp(int[][][] grid, int x, int y, int z, int value) {
		if (y < x) {
			long valueToAdd = value;
			if (y == x - 1) {
				valueToAdd += value;
			}
			int yy = y+1;
			grid[x][yy][z] += valueToAdd;
		}
	}
	
	private void addDown(int[][][] grid, int x, int y, int z, int value) {
		if (y > z) {	
			long valueToAdd = value;
			if (z == y - 1) {
				valueToAdd += value;
				if (y == 1) {
					valueToAdd += 2*value;
				}
			}
			grid[x][y-1][z] += valueToAdd;
		}
	}
	
	private void addFront(int[][][] grid, int x, int y, int z, int value) {
		if (z < y) {
			long valueToAdd = value;
			if (z == y - 1) {
				valueToAdd += value;
				if (x == y) {
					valueToAdd += value;
				}
			}
			int zz = z+1;
			grid[x][y][zz] += valueToAdd;
		}
	}
	
	private void addBack(int[][][] grid, int x, int y, int z, int value) {
		if (z > 0) {
			long valueToAdd = value;
			if (z == 1) {
				valueToAdd += value;
			}
			grid[x][y][z-1] += valueToAdd;
		}	
	}
	
	@Override
	public int getFromPosition(int x, int y, int z){	
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		if (z < 0) z = -z;
		//sort coordinates
		boolean sorted;
		do {
			sorted = true;
			if (z > y) {
				sorted = false;
				int swp = z;
				z = y;
				y = swp;
			}
			if (y > x) {
				sorted = false;
				int swp = y;
				y = x;
				x = swp;
			}
		} while (!sorted);
		if (x < grid.length 
				&& y < grid[x].length 
				&& z < grid[x][y].length) {
			return grid[x][y][z];
		} else {
			return 0;
		}
	}
	
	@Override
	public int getFromAsymmetricPosition(int x, int y, int z){	
		return grid[x][y][z];
	}

	@Override
	public int getAsymmetricMaxX() {
		return grid.length - 1;
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
	public int getInitialValue() {
		return initialValue;
	}

	@Override
	public String getName() {
		return "NearAether1";
	}
	
	@Override
	public String getSubfolderPath() {
		return getName() + "/3D/" + initialValue;
	}
	
	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		Utils.serializeToFile(this, backupPath, backupName);
	}
	
}