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

import cellularautomata.grid3d.NonsymmetricLongGrid3DSlice;

public class Aether3D2 implements SymmetricLongCellularAutomaton3D {
	
	private static final byte UP = 0;
	private static final byte DOWN = 1;
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;
	private static final byte FRONT = 4;
	private static final byte BACK = 5;

	/** A 3D array representing the grid */
	private NonsymmetricLongGrid3DSlice[] grid;
	
	private long initialValue;
	private long currentStep;
	
	private int maxY;
	private int maxZ;

	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;

	private int maxXMinusOne;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public Aether3D2(long initialValue) {
		if (initialValue < 0) {
			BigInteger maxNeighboringValuesDifference = Utils.getAetherMaxNeighboringValuesDifferenceFromSingleSource(3, BigInteger.valueOf(initialValue));
			if (maxNeighboringValuesDifference.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
				throw new IllegalArgumentException("Resulting max value difference between neighboring positions (" + maxNeighboringValuesDifference 
						+ ") exceeds implementation's limit (" + Long.MAX_VALUE + "). Use a greater initial value or a different implementation.");
			}
		}
		this.initialValue = initialValue;
		grid = new NonsymmetricLongGrid3DSlice[3];
		grid[0] = new NonsymmetricLongGrid3DSlice(0);
		grid[1] = new NonsymmetricLongGrid3DSlice(1);
		grid[2] = new NonsymmetricLongGrid3DSlice(2);
		grid[0].setValueAtPosition(0, 0, this.initialValue);
		maxY = 0;
		maxZ = 0;
		boundsReached = false;
		currentStep = 0;
	}
	
	/**
	 * Computes the next step of the algorithm and returns whether
	 * or not the state of the grid changed. 
	 *  
	 * @return true if the state of the grid changed or false otherwise
	 */
	public boolean nextStep(){
		NonsymmetricLongGrid3DSlice[] newGrid = null;
		if (boundsReached) {
			boundsReached = false;
			newGrid = new NonsymmetricLongGrid3DSlice[grid.length + 1];
		} else {
			newGrid = new NonsymmetricLongGrid3DSlice[grid.length];
		}
		maxXMinusOne = newGrid.length - 2;
		boolean changed = false;
		newGrid[0] = new NonsymmetricLongGrid3DSlice(0);
		boolean first = true;
		for (int x = 0, nextX = 1; x < grid.length; x++, nextX++, first = false) {
			if (nextX < newGrid.length) {
				newGrid[nextX] = new NonsymmetricLongGrid3DSlice(nextX);
			}
			for (int y = 0; y <= x; y++) {
				for (int z = 0; z <= y; z++) {
					long value = grid[x].getValueAtPosition(y, z);
					long rightValue = getValueAtPosition(x + 1, y, z);
					long leftValue = getValueAtPosition(x - 1, y, z);
					long upValue = getValueAtPosition(x, y + 1, z);
					long downValue = getValueAtPosition(x, y - 1, z);
					long frontValue = getValueAtPosition(x, y, z + 1);
					long backValue = getValueAtPosition(x, y, z - 1);
					boolean positionChanged = computePosition(value,  rightValue, leftValue, upValue, downValue, frontValue, backValue, x, y, z, newGrid);
					changed = changed || positionChanged;
				}
			}
			if (!first) {
				grid[x-1] = null;
			}
		}
		grid = newGrid;
		currentStep++;
		return changed;
	}
	
	private boolean computePosition(long value, long rightValue, long leftValue, long upValue, long downValue, long frontValue, long backValue, 
			int x, int y, int z, NonsymmetricLongGrid3DSlice[] newGrid) {
		boolean changed = false;
		long[] neighborValues = new long[6];
		byte[] neighborDirections = new byte[6];
		int relevantNeighborCount = 0;
		if (rightValue < value) {
			neighborValues[relevantNeighborCount] = rightValue;
			neighborDirections[relevantNeighborCount] = RIGHT;
			relevantNeighborCount++;
		}
		if (leftValue < value) {
			neighborValues[relevantNeighborCount] = leftValue;
			neighborDirections[relevantNeighborCount] = LEFT;
			relevantNeighborCount++;
		}
		if (upValue < value) {
			neighborValues[relevantNeighborCount] = upValue;
			neighborDirections[relevantNeighborCount] = UP;
			relevantNeighborCount++;
		}
		if (downValue < value) {
			neighborValues[relevantNeighborCount] = downValue;
			neighborDirections[relevantNeighborCount] = DOWN;
			relevantNeighborCount++;
		}
		if (frontValue < value) {
			neighborValues[relevantNeighborCount] = frontValue;
			neighborDirections[relevantNeighborCount] = FRONT;
			relevantNeighborCount++;
		}
		if (backValue < value) {
			neighborValues[relevantNeighborCount] = backValue;
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
						long valSwap = neighborValues[i];
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
							addToNeighbor(newGrid, x, y, z, neighborDirections[j], share);
						}
					}
					previousNeighborValue = neighborValue;
				}
			}	
		}					
		newGrid[x].addValueAtPosition(y, z, value);
		return changed;
	}

	private void addToNeighbor(NonsymmetricLongGrid3DSlice[] newGrid, int x, int y, int z, byte direction, long value) {
		switch(direction) {
		case RIGHT:
			addRight(newGrid, x, y, z, value);
			break;
		case LEFT:
			addLeft(newGrid, x, y, z, value);
			break;
		case UP:
			addUp(newGrid, x, y, z, value);
			break;
		case DOWN:
			addDown(newGrid, x, y, z, value);
			break;
		case FRONT:
			addFront(newGrid, x, y, z, value);
			break;
		case BACK:
			addBack(newGrid, x, y, z, value);
			break;
		}
	}
	
	private void addRight(NonsymmetricLongGrid3DSlice[] newGrid, int x, int y, int z, long value) {
		newGrid[x+1].addValueAtPosition(y, z, value);
		if (x >= maxXMinusOne) {
			boundsReached = true;
		}
	}
	
	private void addLeft(NonsymmetricLongGrid3DSlice[] newGrid, int x, int y, int z, long value) {
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
			newGrid[x-1].addValueAtPosition(y, z, valueToAdd);
		}
		if (x >= maxXMinusOne) {
			boundsReached = true;
		}
	}
	
	private void addUp(NonsymmetricLongGrid3DSlice[] newGrid, int x, int y, int z, long value) {
		if (y < x) {
			long valueToAdd = value;
			if (y == x - 1) {
				valueToAdd += value;
			}
			int yy = y+1;
			newGrid[x].addValueAtPosition(yy, z, valueToAdd);
			if (yy > maxY)
				maxY = yy;
		}
	}
	
	private void addDown(NonsymmetricLongGrid3DSlice[] newGrid, int x, int y, int z, long value) {
		if (y > z) {	
			long valueToAdd = value;
			if (z == y - 1) {
				valueToAdd += value;
				if (y == 1) {
					valueToAdd += 2*value;
				}
			}
			newGrid[x].addValueAtPosition(y-1, z, valueToAdd);
		}
	}
	
	private void addFront(NonsymmetricLongGrid3DSlice[] newGrid, int x, int y, int z, long value) {
		if (z < y) {
			long valueToAdd = value;
			if (z == y - 1) {
				valueToAdd += value;
				if (x == y) {
					valueToAdd += value;
				}
			}
			int zz = z+1;
			newGrid[x].addValueAtPosition(y, zz, valueToAdd);
			if (zz > maxZ)
				maxZ = zz;
		}
	}
	
	private void addBack(NonsymmetricLongGrid3DSlice[] newGrid, int x, int y, int z, long value) {
		if (z > 0) {
			long valueToAdd = value;
			if (z == 1) {
				valueToAdd += value;
			}
			newGrid[x].addValueAtPosition(y, z-1, valueToAdd);
		}	
	}
	
	public long getValueAtPosition(int x, int y, int z){	
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
		if (x < grid.length && y <= x && z <= y) {
			return grid[x].getValueAtPosition(y, z);
		} else {
			return 0;
		}
	}
	
	public long getValueAtNonsymmetricPosition(int x, int y, int z){	
		return grid[x].getValueAtPosition(y, z);
	}
	
	@Override
	public int getNonsymmetricMinX() {
		return 0;
	}

	@Override
	public int getNonsymmetricMaxX() {
		return grid.length - 1;
	}
	
	@Override
	public int getNonsymmetricMinY() {
		return 0;
	}
	
	@Override
	public int getNonsymmetricMaxY() {
		return maxY;
	}
	
	@Override
	public int getNonsymmetricMinZ() {
		return 0;
	}
	
	@Override
	public int getNonsymmetricMaxZ() {
		return maxZ;
	}
	
	@Override
	public int getMinX() {
		return -getNonsymmetricMaxX();
	}

	@Override
	public int getMaxX() {
		return getNonsymmetricMaxX();
	}

	@Override
	public int getMinY() {
		return -getNonsymmetricMaxX();
	}

	@Override
	public int getMaxY() {
		return getNonsymmetricMaxX();
	}
	
	@Override
	public int getMinZ() {
		return -getNonsymmetricMaxX();
	}

	@Override
	public int getMaxZ() {
		return getNonsymmetricMaxX();
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
	public String getName() {
		return "Aether3D2";
	}
	
	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialValue;
	}
	
	@Override
	public int getNonsymmetricMinXAtY(int y) {
		return y;
	}

	@Override
	public int getNonsymmetricMinXAtZ(int z) {
		return z;
	}

	@Override
	public int getNonsymmetricMinX(int y, int z) {
		return Math.max(y, z);
	}

	@Override
	public int getNonsymmetricMaxXAtY(int y) {
		return getNonsymmetricMaxX();
	}

	@Override
	public int getNonsymmetricMaxXAtZ(int z) {
		return getNonsymmetricMaxX();
	}

	@Override
	public int getNonsymmetricMaxX(int y, int z) {
		return getNonsymmetricMaxX();
	}

	@Override
	public int getNonsymmetricMinYAtX(int x) {
		return 0;
	}

	@Override
	public int getNonsymmetricMinYAtZ(int z) {
		return z;
	}

	@Override
	public int getNonsymmetricMinY(int x, int z) {
		return z;
	}

	@Override
	public int getNonsymmetricMaxYAtX(int x) {
		return Math.min(getNonsymmetricMaxY(), x);
	}

	@Override
	public int getNonsymmetricMaxYAtZ(int z) {
		return getNonsymmetricMaxY();
	}

	@Override
	public int getNonsymmetricMaxY(int x, int z) {
		return Math.min(getNonsymmetricMaxY(), x);
	}

	@Override
	public int getNonsymmetricMinZAtX(int x) {
		return 0;
	}

	@Override
	public int getNonsymmetricMinZAtY(int y) {
		return 0;
	}

	@Override
	public int getNonsymmetricMinZ(int x, int y) {
		return 0;
	}

	@Override
	public int getNonsymmetricMaxZAtX(int x) {
		return Math.min(getNonsymmetricMaxZ(), x);
	}

	@Override
	public int getNonsymmetricMaxZAtY(int y) {
		return Math.min(getNonsymmetricMaxZ(), y);
	}

	@Override
	public int getNonsymmetricMaxZ(int x, int y) {
		return Math.min(getNonsymmetricMaxZ(), y);
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}
}