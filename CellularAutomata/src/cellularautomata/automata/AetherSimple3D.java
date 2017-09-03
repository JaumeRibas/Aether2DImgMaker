/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017 Jaume Ribas

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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * A simplified implementation of the Aether cellular automaton for review and testing purposes
 * 
 * @author Jaume
 *
 */
public class AetherSimple3D extends SymmetricLongCellularAutomaton3D {	
	
	private static final byte UP = 0;
	private static final byte DOWN = 1;
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;
	private static final byte FRONT = 4;
	private static final byte BACK = 5;
	
	private long[][][] grid;
	
	private long initialValue;
	private long backgroundValue;
	private long currentStep;
	
	/** The indexes of the origin within the array */
	private int originIndex;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 * @param backgroundValue the value padding all the grid but the origin at step 0
	 */
	public AetherSimple3D(long initialValue, long backgroundValue) {
		if (backgroundValue > initialValue) {
			BigInteger maxValue = BigInteger.valueOf(initialValue).add(BigInteger.valueOf(backgroundValue)
					.subtract(BigInteger.valueOf(initialValue)).divide(BigInteger.valueOf(2)).multiply(BigInteger.valueOf(6)));
			if (maxValue.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
				throw new IllegalArgumentException("Resulting max value " + maxValue 
						+ " exceeds implementation's limit (" + Long.MAX_VALUE 
						+ "). Consider using a different implementation or a smaller backgroundValue/initialValue ratio.");
			}
		}
		this.initialValue = initialValue;
		this.backgroundValue = backgroundValue;
		int side = 5;
		grid = new long[side][side][side];
		originIndex = (side - 1)/2;
		if (backgroundValue != 0) {
			for (int x = 0; x < grid.length; x++) {
				for (int y = 0; y < grid[x].length; y++) {
					for (int z = 0; z < grid[x][y].length; z++) {
						grid[x][y][z] = backgroundValue;
					}
				}
			}
		}
		grid[originIndex][originIndex][originIndex] = initialValue;
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
		long[][][] newGrid = null;
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new long[grid.length + 2][grid[0].length + 2][grid[0][0].length + 2];
			if (backgroundValue != 0) {
				padEdges(newGrid, 1, backgroundValue);
			}
			//The offset between the indexes of the new and old array
			indexOffset = 1;
		} else {
			newGrid = new long[grid.length][grid[0].length][grid[0][0].length];
		}
		boolean changed = false;
		//For every position
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[0].length; y++) {
				for (int z = 0; z < grid[0][0].length; z++) {
					long value = grid[x][y][z];
					if (value != 0) {
						List<LongNeighbor> neighbors = new ArrayList<LongNeighbor>(6);						
						long neighborValue;
						if (x < grid.length - 1)
							neighborValue = grid[x + 1][y][z];
						else
							neighborValue = backgroundValue;
						if (neighborValue < value)
							neighbors.add(new LongNeighbor(RIGHT, neighborValue));
						if (x > 0)
							neighborValue = grid[x - 1][y][z];
						else
							neighborValue = backgroundValue;
						if (neighborValue < value)
							neighbors.add(new LongNeighbor(LEFT, neighborValue));
						if (y < grid[x].length - 1)
							neighborValue = grid[x][y + 1][z];
						else
							neighborValue = backgroundValue;
						if (neighborValue < value)
							neighbors.add(new LongNeighbor(UP, neighborValue));
						if (y > 0)
							neighborValue = grid[x][y - 1][z];
						else
							neighborValue = backgroundValue;
						if (neighborValue < value)
							neighbors.add(new LongNeighbor(DOWN, neighborValue));
						if (z < grid[x][y].length - 1)
							neighborValue = grid[x][y][z + 1];
						else
							neighborValue = backgroundValue;
						if (neighborValue < value)
							neighbors.add(new LongNeighbor(FRONT, neighborValue));
						if (z > 0)
							neighborValue = grid[x][y][z - 1];
						else
							neighborValue = backgroundValue;
						if (neighborValue < value)
							neighbors.add(new LongNeighbor(BACK, neighborValue));
						
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
										checkBoundsReached(x, y, z);
										changed = true;
										value = value - toShare + toShare%shareCount + share;
										for (LongNeighbor neighbor : neighbors) {
											int[] nc = getNeighborCoordinates(x, y, z, neighbor.getDirection());
											newGrid[nc[0] + indexOffset][nc[1] + indexOffset][nc[2] + indexOffset] += share;
										}
									}
									previousNeighborValue = neighborValue;
								}
								neighbors.remove(i);
							}	
						}					
						newGrid[x + indexOffset][y + indexOffset][z + indexOffset] += value;
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
	
	private void checkBoundsReached(int x, int y, int z) {
		if (x == 1 || x == grid.length - 2 || 
			y == 1 || y == grid[0].length - 2 || 
			z == 1 || z == grid[0][0].length - 2) {
			boundsReached = true;
		}
	}
	
	private static int[] getNeighborCoordinates(int x, int y, int z, byte direction) {
		switch(direction) {
		case UP:
			y++;
			break;
		case DOWN:
			y--;
			break;
		case RIGHT:
			x++;
			break;
		case LEFT:
			x--;
			break;
		case FRONT:
			z++;
			break;
		case BACK:
			z--;
			break;
		}
		return new int[]{
			x, y, z
		};
	}
	
	public long getValueAt(int x, int y, int z){	
		int arrayX = originIndex + x;
		int arrayY = originIndex + y;
		int arrayZ = originIndex + z;
		if (arrayX < 0 || arrayX > grid.length - 1 
				|| arrayY < 0 || arrayY > grid[0].length - 1
				|| arrayZ < 0 || arrayZ > grid[0][0].length - 1) {
			//If the entered position is outside the array the value will be zero
			return 0;
		} else {
			//Note that the positions whose value hasn't been defined have value zero by default
			return grid[arrayX][arrayY][arrayZ];
		}
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

	public long getNonSymmetricValueAt(int x, int y, int z) {
		return getValueAt(x, y, z);
	}
	
	/**
	 * Returns the current step
	 * 
	 * @return the current step
	 */
	public long getCurrentStep() {
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
		return backgroundValue;
	}
	
	public static void padEdges(long[][][] grid, int width, long value) {
		//left
		for (int x = 0; x < grid.length && x < width; x++) {
			for (int y = 0; y < grid[x].length; y++) {
				for (int z = 0; z < grid[x][y].length; z ++) {
					grid[x][y][z] = value;				
				}
			}
		}
		//right
		for (int x = grid.length - width; x < grid.length; x++) {
			for (int y = 0; y < grid[x].length; y++) {
				for (int z = 0; z < grid[x][y].length; z++) {
					grid[x][y][z] = value;
				}
			}
		}
		//up
		for (int x = width; x < grid.length - width; x++) {
			for (int y = 0; y < grid[x].length && y < width; y++) {
				for (int z = 0; z < grid[x][y].length; z++) {
					grid[x][y][z] = value;
				}
			}
		}
		//down
		for (int x = width; x < grid.length - width; x++) {
			for (int y = grid[x].length - width; y < grid[x].length; y++) {
				for (int z = 0; z < grid[x][y].length; z++) {
					grid[x][y][z] = value;
				}
			}
		}
		//front
		for (int x = width; x < grid.length - width; x++) {
			for (int y = width; y < grid[x].length - width; y++) {
				for (int z = 0; z < grid[x][y].length && z < width; z++) {
					grid[x][y][z] = value;
				}
			}
		}
		//back
		for (int x = width; x < grid.length - width; x++) {
			for (int y = width; y < grid[x].length - width; y++) {
				for (int z = grid[x][y].length - width; z < grid[x][y].length; z++) {
					grid[x][y][z] = value;
				}
			}
		}
	}

	@Override
	public CustomSymmetricLongCA3DData getData() {
		return new CustomSymmetricLongCA3DData(grid, initialValue, backgroundValue, currentStep, boundsReached, getMaxY(), getMaxZ());
	}

	@Override
	public String getName() {
		return "Aether3D";
	}

	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialValue + "/" + backgroundValue;
	}
}