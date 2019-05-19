/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2018 Jaume Ribas

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
import java.util.ArrayList;
import java.util.List;

public class AetherSimple4D implements SymmetricLongCellularAutomaton4D {	
	
	private static final byte W_POSITIVE = 0;
	private static final byte W_NEGATIVE = 1;
	private static final byte X_POSITIVE = 2;
	private static final byte X_NEGATIVE = 3;
	private static final byte Y_POSITIVE = 4;
	private static final byte Y_NEGATIVE = 5;
	private static final byte Z_POSITIVE = 6;
	private static final byte Z_NEGATIVE = 7;
	
	private long[][][][] grid;
	
	private long initialValue;
	private long currentStep;
	
	/** The indexes of the origin within the array */
	private int originIndex;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public AetherSimple4D(long initialValue) {
		if (initialValue < 0) {
			BigInteger maxValue = BigInteger.valueOf(initialValue).add(
					BigInteger.valueOf(initialValue).negate().divide(BigInteger.valueOf(2)).multiply(BigInteger.valueOf(8)));
			if (maxValue.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
				throw new IllegalArgumentException("Resulting max value " + maxValue 
						+ " exceeds implementation's limit (" + Long.MAX_VALUE + ").");
			}
		}
		this.initialValue = initialValue;
		int side = 5;
		grid = new long[side][side][side][side];
		originIndex = (side - 1)/2;
		grid[originIndex][originIndex][originIndex][originIndex] = this.initialValue;
		boundsReached = false;
		//Set the current step to zero
		currentStep = 0;
	}
	
	/**
	 * Computes the next step of the algorithm and returns whether
	 * or not the state of the grid changed. 
	 *  
	 * @return true if the state of the grid changed or false otherwise
	 */
	public boolean nextStep(){
		//Use new array to store the values of the next step
		long[][][][] newGrid = null;
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new long[grid.length + 2][grid[0].length + 2][grid[0][0].length + 2][grid[0][0][0].length + 2];
			//The offset between the indexes of the new and old array
			indexOffset = 1;
		} else {
			newGrid = new long[grid.length][grid[0].length][grid[0][0].length][grid[0][0][0].length];
		}
		boolean changed = false;
		//For every position
		for (int w = 0; w < grid.length; w++) {
			for (int x = 0; x < grid[0].length; x++) {
				for (int y = 0; y < grid[0][0].length; y++) {
					for (int z = 0; z < grid[0][0][0].length; z++) {
						long value = grid[w][x][y][z];
						List<LongNeighbor> neighbors = new ArrayList<LongNeighbor>(8);						
						long neighborValue;
						if (w < grid.length - 1)
							neighborValue = grid[w + 1][x][y][z];
						else
							neighborValue = 0;
						if (neighborValue < value)
							neighbors.add(new LongNeighbor(W_POSITIVE, neighborValue));
						if (w > 0)
							neighborValue = grid[w - 1][x][y][z];
						else
							neighborValue = 0;
						if (neighborValue < value)
							neighbors.add(new LongNeighbor(W_NEGATIVE, neighborValue));
						if (x < grid[w].length - 1)
							neighborValue = grid[w][x + 1][y][z];
						else
							neighborValue = 0;
						if (neighborValue < value)
							neighbors.add(new LongNeighbor(X_POSITIVE, neighborValue));
						if (x > 0)
							neighborValue = grid[w][x - 1][y][z];
						else
							neighborValue = 0;
						if (neighborValue < value)
							neighbors.add(new LongNeighbor(X_NEGATIVE, neighborValue));
						if (y < grid[w][x].length - 1)
							neighborValue = grid[w][x][y + 1][z];
						else
							neighborValue = 0;
						if (neighborValue < value)
							neighbors.add(new LongNeighbor(Y_POSITIVE, neighborValue));
						if (y > 0)
							neighborValue = grid[w][x][y - 1][z];
						else
							neighborValue = 0;
						if (neighborValue < value)
							neighbors.add(new LongNeighbor(Y_NEGATIVE, neighborValue));
						if (z < grid[w][x][y].length - 1)
							neighborValue = grid[w][x][y][z + 1];
						else
							neighborValue = 0;
						if (neighborValue < value)
							neighbors.add(new LongNeighbor(Z_POSITIVE, neighborValue));
						if (z > 0)
							neighborValue = grid[w][x][y][z - 1];
						else
							neighborValue = 0;
						if (neighborValue < value)
							neighbors.add(new LongNeighbor(Z_NEGATIVE, neighborValue));
						
						if (neighbors.size() > 0) {
							//sort
							boolean sorted = false;
							while (!sorted) {
								sorted = true;
								for (int i = neighbors.size() - 2; i >= 0; i--) {
									LongNeighbor next = neighbors.get(i+1);
									if (neighbors.get(i).getValue() > next.getValue()) {
										sorted = false;
										neighbors.remove(i+1);
										neighbors.add(i, next);
									}
								}
							}
							//divide
							boolean isFirst = true;
							long previousNeighborValue = 0;
							for (int i = neighbors.size() - 1; i >= 0; i--,isFirst = false) {
								neighborValue = neighbors.get(i).getValue();
								if (neighborValue != previousNeighborValue || isFirst) {
									int shareCount = neighbors.size() + 1;
									long toShare = value - neighborValue;
									long share = toShare/shareCount;
									if (share != 0) {
										checkBoundsReached(w, x, y, z);
										changed = true;
										value = value - toShare + toShare%shareCount + share;
										for (LongNeighbor neighbor : neighbors) {
											int[] nc = getNeighborCoordinates(w, x, y, z, neighbor.getDirection());
											newGrid[nc[0] + indexOffset][nc[1] + indexOffset][nc[2] + indexOffset][nc[3] + indexOffset] += share;
										}
									}
									previousNeighborValue = neighborValue;
								}
								neighbors.remove(i);
							}	
						}					
						newGrid[w + indexOffset][x + indexOffset][y + indexOffset][z + indexOffset] += value;
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
	
	private void checkBoundsReached(int w, int x, int y, int z) {
		if (w == 1 || w == grid.length - 2 || 
			x == 1 || x == grid[0].length - 2 || 
			y == 1 || y == grid[0][0].length - 2 || 
			z == 1 || z == grid[0][0][0].length - 2) {
			boundsReached = true;
		}
	}
	
	private static int[] getNeighborCoordinates(int w, int x, int y, int z, byte direction) {
		switch(direction) {
		case W_POSITIVE:
			w++;
			break;
		case W_NEGATIVE:
			w--;
			break;
		case X_POSITIVE:
			x++;
			break;
		case X_NEGATIVE:
			x--;
			break;
		case Y_POSITIVE:
			y++;
			break;
		case Y_NEGATIVE:
			y--;
			break;
		case Z_POSITIVE:
			z++;
			break;
		case Z_NEGATIVE:
			z--;
			break;
		}
		return new int[]{
			w, x, y, z
		};
	}
	
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
	
	/**
	 * Returns the smallest w-coordinate of a nonzero value at the current step
	 * 
	 * @return the smallest w of a nonzero value at the current step
	 */
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
	
	/**
	 * Returns the largest w-coordinate of a nonzero value at the current step
	 * 
	 * @return the largest w of a nonzero value at the current step
	 */
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
	
	/**
	 * Returns the smallest x-coordinate of a nonzero value at the current step
	 * 
	 * @return the smallest x of a nonzero value at the current step
	 */
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
	
	/**
	 * Returns the largest x-coordinate of a nonzero value at the current step
	 * 
	 * @return the largest x of a nonzero value at the current step
	 */
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
	public long getIntialValue() {
		return initialValue;
	}

	public int getNonSymmetricMinW() {
		return 0;
	}

	public int getNonSymmetricMaxW() {
		return getMaxW();
	}

	public int getNonSymmetricMinX() {
		return 0;
	}

	public int getNonSymmetricMaxX() {
		return getMaxX();
	}

	public int getNonSymmetricMinY() {
		return 0;
	}

	public int getNonSymmetricMaxY() {
		return getMaxY();
	}

	public int getNonSymmetricMinZ() {
		return 0;
	}

	public int getNonSymmetricMaxZ() {
		return getMaxZ();
	}

	public long getValueAtNonSymmetricPosition(int w, int x, int y, int z) {
		return getValueAtPosition(w, x, y, z);
	}

	@Override
	public String getName() {
		return "Aether4D";
	}

	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialValue;
	}

	@Override
	public long getBackgroundValue() {
		return 0;
	}
	
	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		CustomSymmetricLongCA4DData data = new CustomSymmetricLongCA4DData(grid, initialValue, 0, currentStep, 
				boundsReached, getMaxX(), getMaxY(), getMaxZ());
		Utils.serializeToFile(data, backupPath, backupName);
	}
}