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
package cellularautomata.automata.nearaether;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import cellularautomata.Direction;
import cellularautomata.Utils;
import cellularautomata.model3d.IsotropicCubicIntArrayModelAsymmetricSection;

/**
 * Implementation of a cellular automaton very similar to <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> to showcase its uniqueness.
 * 
 * @author Jaume
 *
 */
public class IntNearAetherTwo3D extends IsotropicCubicIntArrayModelAsymmetricSection implements Serializable {
	
	public static final int MAX_INITIAL_VALUE = Integer.MAX_VALUE;
	public static final int MIN_INITIAL_VALUE = -858993459;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8988793559626431761L;
	
	private final int initialValue;
	private long step;

	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;

	private int maxXMinusOne;

	private Boolean changed = null;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public IntNearAetherTwo3D(int initialValue) {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of int type
			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
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
	public IntNearAetherTwo3D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		IntNearAetherTwo3D data = (IntNearAetherTwo3D) Utils.deserializeFromFile(backupPath);
		initialValue = data.initialValue;
		grid = data.grid;
		maxXMinusOne = data.maxXMinusOne;
		boundsReached = data.boundsReached;
		step = data.step;
		changed = data.changed;
	}
	
	@Override
	public Boolean nextStep() {
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
		Direction[] neighborDirections = new Direction[6];
		for (int x = 0, nextX = 1; x < grid.length; x = nextX, nextX++, first = false) {
			if (nextX < newGrid.length) {
				newGrid[nextX] = Utils.buildAnisotropic2DIntArray(nextX + 1);
			}
			for (int y = 0; y <= x; y++) {
				for (int z = 0; z <= y; z++) {
					int value = grid[x][y][z];
					int relevantNeighborCount = 0;
					int neighborValue;
					int biggestSmallerNeighborValue = Integer.MIN_VALUE;
					neighborValue = getFromPosition(x + 1, y, z);
					if (neighborValue < value) {
						if (neighborValue > biggestSmallerNeighborValue) {
							biggestSmallerNeighborValue = neighborValue;
						}
						neighborDirections[relevantNeighborCount] = Direction.RIGHT;
						relevantNeighborCount++;
					}
					neighborValue = getFromPosition(x - 1, y, z);
					if (neighborValue < value) {
						if (neighborValue > biggestSmallerNeighborValue) {
							biggestSmallerNeighborValue = neighborValue;
						}
						neighborDirections[relevantNeighborCount] = Direction.LEFT;
						relevantNeighborCount++;
					}
					neighborValue = getFromPosition(x, y + 1, z);
					if (neighborValue < value) {
						if (neighborValue > biggestSmallerNeighborValue) {
							biggestSmallerNeighborValue = neighborValue;
						}
						neighborDirections[relevantNeighborCount] = Direction.UP;
						relevantNeighborCount++;
					}
					neighborValue = getFromPosition(x, y - 1, z);
					if (neighborValue < value) {
						if (neighborValue > biggestSmallerNeighborValue) {
							biggestSmallerNeighborValue = neighborValue;
						}
						neighborDirections[relevantNeighborCount] = Direction.DOWN;
						relevantNeighborCount++;
					}
					neighborValue = getFromPosition(x, y, z + 1);
					if (neighborValue < value) {
						if (neighborValue > biggestSmallerNeighborValue) {
							biggestSmallerNeighborValue = neighborValue;
						}
						neighborDirections[relevantNeighborCount] = Direction.FRONT;
						relevantNeighborCount++;
					}
					neighborValue = getFromPosition(x, y, z - 1);
					if (neighborValue < value) {
						if (neighborValue > biggestSmallerNeighborValue) {
							biggestSmallerNeighborValue = neighborValue;
						}
						neighborDirections[relevantNeighborCount] = Direction.BACK;
						relevantNeighborCount++;
					}
					
					if (relevantNeighborCount > 0) {
						//divide
						int shareCount = relevantNeighborCount + 1;
						int toShare = value - biggestSmallerNeighborValue;
						int share = toShare/shareCount;
						if (share != 0) {
							changed = true;
							value = value - toShare + toShare%shareCount + share;
							for (int i = 0; i < relevantNeighborCount; i++) {
								addToNeighbor(newGrid, x, y, z, neighborDirections[i], share);
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
		this.changed = changed;
		return changed;
	}

	@Override
	public Boolean isChanged() {
		return changed;
	}
	
	@SuppressWarnings("incomplete-switch")
	private void addToNeighbor(int grid[][][], int x, int y, int z, Direction direction, int value) {
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
	public int getFromPosition(int x, int y, int z) {
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		if (z < 0) z = -z;
		int xx, yy, zz;
		if (x >= y) {
			if (y >= z) {
				//x >= y >= z
				xx = x; yy = y; zz = z;
			} else if (x >= z) { 
				//x >= z > y
				xx = x; yy = z; zz = y;
			} else {
				//z > x >= y
				xx = z; yy = x; zz = y;
			}
		} else if (y >= z) {
			if (x >= z) {
				//y > x >= z
				xx = y; yy = x; zz = z;
			} else {
				//y >= z > x
				xx = y; yy = z; zz = x;
			}
		} else {
			// z > y > x
			xx = z; yy = y; zz = x;
		}
		if (xx < grid.length) {
			return grid[xx][yy][zz];
		} else {
			return 0; //this implementation relies on being able to get the value of an out of bounds position
		}
	}

	@Override
	public int getSize() {
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
		return "NearAether2";
	}
	
	@Override
	public String getWholeGridSubfolderPath() {
		return getName() + "/3D/" + initialValue;
	}
	
	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		Utils.serializeToFile(this, backupPath, backupName);
	}
	
}