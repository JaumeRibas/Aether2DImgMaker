/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2019 Jaume Ribas

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

import cellularautomata.grid3d.NonSymmetricLongGrid3DSlice;

public class Aether3D3 implements SymmetricLongCellularAutomaton3D {
	
	private static final byte UP = 0;
	private static final byte DOWN = 1;
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;
	private static final byte FRONT = 4;
	private static final byte BACK = 5;

	/** A 3D array representing the grid */
	private NonSymmetricLongGrid3DSlice[] grid;
	
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
	public Aether3D3(long initialValue) {
		if (initialValue < 0) {
			BigInteger maxValue = BigInteger.valueOf(initialValue).add(
					BigInteger.valueOf(initialValue).negate().divide(BigInteger.valueOf(2)).multiply(BigInteger.valueOf(6)));
			if (maxValue.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
				throw new IllegalArgumentException("Resulting max value " + maxValue 
						+ " exceeds implementation's limit (" + Long.MAX_VALUE + ").");
			}
		}
		this.initialValue = initialValue;
		grid = new NonSymmetricLongGrid3DSlice[3];
		grid[0] = new NonSymmetricLongGrid3DSlice(0);
		grid[1] = new NonSymmetricLongGrid3DSlice(1);
		grid[2] = new NonSymmetricLongGrid3DSlice(2);
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
		NonSymmetricLongGrid3DSlice[] newGrid = null;
		if (boundsReached) {
			boundsReached = false;
			newGrid = new NonSymmetricLongGrid3DSlice[grid.length + 1];
		} else {
			newGrid = new NonSymmetricLongGrid3DSlice[grid.length];
		}
		maxXMinusOne = newGrid.length - 2;
		boolean changed = false;
		newGrid[0] = new NonSymmetricLongGrid3DSlice(0);
		boolean first = true;
		long[] neighborValues = new long[6];
		byte[] neighborDirections = new byte[6];
		for (int x = 0, nextX = 1; x < grid.length; x++, nextX++, first = false) {
			if (nextX < newGrid.length) {
				newGrid[nextX] = new NonSymmetricLongGrid3DSlice(nextX);
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
										switch(neighborDirections[j]) {
										case RIGHT:
											newGrid[x+1].addValueAtPosition(y, z, share);
											if (x >= maxXMinusOne) {
												boundsReached = true;
											}
											break;
										case LEFT:
											if (x > y) {
												long valueToAdd = share;
												if (y == x - 1) {
													valueToAdd += share;
													if (z == y) {
														valueToAdd += share;
														if (x == 1) {
															valueToAdd += 3*share;
														}
													}
												}
												newGrid[x-1].addValueAtPosition(y, z, valueToAdd);
											}
											if (x >= maxXMinusOne) {
												boundsReached = true;
											}
											break;
										case UP:
											if (y < x) {
												long valueToAdd = share;
												if (y == x - 1) {
													valueToAdd += share;
												}
												int yy = y+1;
												newGrid[x].addValueAtPosition(yy, z, valueToAdd);
												if (yy > maxY)
													maxY = yy;
											}
											break;
										case DOWN:
											if (y > z) {	
												long valueToAdd = share;
												if (z == y - 1) {
													valueToAdd += share;
													if (y == 1) {
														valueToAdd += 2*share;
													}
												}
												newGrid[x].addValueAtPosition(y-1, z, valueToAdd);
											}
											break;
										case FRONT:
											if (z < y) {
												long valueToAdd = share;
												if (z == y - 1) {
													valueToAdd += share;
													if (x == y) {
														valueToAdd += share;
													}
												}
												int zz = z+1;
												newGrid[x].addValueAtPosition(y, zz, valueToAdd);
												if (zz > maxZ)
													maxZ = zz;
											}
											break;
										case BACK:
											if (z > 0) {
												long valueToAdd = share;
												if (z == 1) {
													valueToAdd += share;
												}
												newGrid[x].addValueAtPosition(y, z-1, valueToAdd);
											}
											break;
										}
									}
								}
								previousNeighborValue = neighborValue;
							}
						}	
					}					
					newGrid[x].addValueAtPosition(y, z, value);
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
		if (x < grid.length) {
			return grid[x].getValueAtPosition(y, z);
		} else {
			return 0;
		}
	}
	
	public long getValueAtNonSymmetricPosition(int x, int y, int z){	
		return grid[x].getValueAtPosition(y, z);
	}
	
	@Override
	public int getNonSymmetricMinX() {
		return 0;
	}

	@Override
	public int getNonSymmetricMaxX() {
		return grid.length - 1;
	}
	
	@Override
	public int getNonSymmetricMinY() {
		return 0;
	}
	
	@Override
	public int getNonSymmetricMaxY() {
		return maxY;
	}
	
	@Override
	public int getNonSymmetricMinZ() {
		return 0;
	}
	
	@Override
	public int getNonSymmetricMaxZ() {
		return maxZ;
	}
	
	@Override
	public int getMinX() {
		return -getNonSymmetricMaxX();
	}

	@Override
	public int getMaxX() {
		return getNonSymmetricMaxX();
	}

	@Override
	public int getMinY() {
		return -getNonSymmetricMaxX();
	}

	@Override
	public int getMaxY() {
		return getNonSymmetricMaxX();
	}
	
	@Override
	public int getMinZ() {
		return -getNonSymmetricMaxX();
	}

	@Override
	public int getMaxZ() {
		return getNonSymmetricMaxX();
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
	public long getBackgroundValue() {
		return 0;
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
	public int getNonSymmetricMinXAtY(int y) {
		return y;
	}

	@Override
	public int getNonSymmetricMinXAtZ(int z) {
		return z;
	}

	@Override
	public int getNonSymmetricMinX(int y, int z) {
		return Math.max(y, z);
	}

	@Override
	public int getNonSymmetricMaxXAtY(int y) {
		return getNonSymmetricMaxX();
	}

	@Override
	public int getNonSymmetricMaxXAtZ(int z) {
		return getNonSymmetricMaxX();
	}

	@Override
	public int getNonSymmetricMaxX(int y, int z) {
		return getNonSymmetricMaxX();
	}

	@Override
	public int getNonSymmetricMinYAtX(int x) {
		return 0;
	}

	@Override
	public int getNonSymmetricMinYAtZ(int z) {
		return z;
	}

	@Override
	public int getNonSymmetricMinY(int x, int z) {
		return z;
	}

	@Override
	public int getNonSymmetricMaxYAtX(int x) {
		return Math.min(getNonSymmetricMaxY(), x);
	}

	@Override
	public int getNonSymmetricMaxYAtZ(int z) {
		return getNonSymmetricMaxY();
	}

	@Override
	public int getNonSymmetricMaxY(int x, int z) {
		return Math.min(getNonSymmetricMaxY(), x);
	}

	@Override
	public int getNonSymmetricMinZAtX(int x) {
		return 0;
	}

	@Override
	public int getNonSymmetricMinZAtY(int y) {
		return 0;
	}

	@Override
	public int getNonSymmetricMinZ(int x, int y) {
		return 0;
	}

	@Override
	public int getNonSymmetricMaxZAtX(int x) {
		return Math.min(getNonSymmetricMaxZ(), x);
	}

	@Override
	public int getNonSymmetricMaxZAtY(int y) {
		return Math.min(getNonSymmetricMaxZ(), y);
	}

	@Override
	public int getNonSymmetricMaxZ(int x, int y) {
		return Math.min(getNonSymmetricMaxZ(), y);
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}
}