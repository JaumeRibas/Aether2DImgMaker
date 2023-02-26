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
import java.util.concurrent.ThreadLocalRandom;

import cellularautomata.Utils;
import cellularautomata.model3d.IntModel3D;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 3D with a random initial configuration
 * 
 * @author Jaume
 *
 */
public class IntAether3DRandomConfiguration implements IntModel3D, Serializable {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6499405266821248687L;
	
	private static final byte UP = 0;
	private static final byte DOWN = 1;
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;
	private static final byte FRONT = 4;
	private static final byte BACK = 5;
	
	/** 3D array representing the grid **/
	private int[][][] grid;
	
	private String timeStamp;
	private int initialSide;
	private int minValue;
	private int maxValue;
	private long step;
	
	/** The indexes of the origin within the array */
	private int originIndex;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	
	private Boolean changed = null;

	/**
	 * Creates an instance with an initial configuration consisting of a square area of side {@code initialSide} full 
	 * of random values between {@code minValue} and {@code maxValue}.
	 * 
	 * @param initialSide the side of the square area that will be filled with random values and will be used as initial configuration
	 * @param minValue the minimum value for the random values
	 * @param maxValue the maximum value for the random values
	 */
	public IntAether3DRandomConfiguration(int initialSide, int minValue, int maxValue) {
		if (minValue > maxValue) {
			throw new IllegalArgumentException("Min value cannot be smaller than max value");
		}
		long actualMinValue = minValue;
		long actualMaxValue = maxValue;
		if (minValue > 0) {
			actualMinValue = 0;
		} else if (maxValue < 0) {
			actualMaxValue = 0;
		}
		final int dimension = 3;
		long resultingMaxValue = actualMinValue + (((actualMaxValue-actualMinValue)/2)*(dimension *2 + 1));
		if (resultingMaxValue > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("The range between the actual min and max values ([" + actualMinValue + ", " + actualMaxValue + "]) is too big.");
		}
		if (initialSide < 1) {
			throw new IllegalArgumentException("Initial side cannot be smaller than one");
		}
		this.initialSide = initialSide;
		this.minValue = minValue;
		this.maxValue = maxValue;
		timeStamp = Utils.getTimeStampFolderName();
		int bufferMargin = 2;
		int doubleBufferMargin = 2*bufferMargin;
		grid = new int[initialSide + doubleBufferMargin][initialSide + doubleBufferMargin][initialSide + doubleBufferMargin];
		originIndex = (grid.length - 1)/2;
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int bufferMarginPlusSide = bufferMargin + initialSide;
		for (int i = bufferMargin; i < bufferMarginPlusSide; i++) {
			for (int j = bufferMargin; j < bufferMarginPlusSide; j++) {
				for (int k = bufferMargin; k < bufferMarginPlusSide; k++) {
					grid[i][j][k] = random.nextInt(minValue, maxValue + 1);
				}
			}
		}
		boundsReached = false;
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
	public IntAether3DRandomConfiguration(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		IntAether3DRandomConfiguration data = (IntAether3DRandomConfiguration) Utils.deserializeFromFile(backupPath);
		grid = data.grid;		
		timeStamp = data.timeStamp;
		initialSide = data.initialSide;
		minValue = data.minValue;
		maxValue = data.maxValue;
		step = data.step;
		originIndex = data.originIndex;
		boundsReached = data.boundsReached;
		changed = data.changed;
	}
	
	@Override
	public Boolean nextStep() {
		//Use new array to store the values of the next step
		int[][][] newGrid = null;
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		int newSide;
		if (boundsReached) {
			boundsReached = false;
			newSide = grid.length + 2;
			//The offset between the indexes of the new and old array
			indexOffset = 1;
			newGrid = new int[newSide][][];
			newGrid[0] = new int[newSide][newSide];
			newGrid[1] = new int[newSide][newSide];
		} else {
			newSide = grid.length;
			newGrid = new int[newSide][][];
			newGrid[0] = new int[newSide][newSide];
		}
		boolean changed = false;
		boolean first = true;
		int[] neighborValues = new int[6];
		int[] sortedNeighborsIndexes = new int[6];
		byte[] neighborDirections = new byte[6];
		//For every cell
		for (int i = 0, nextI = i + 1 + indexOffset; i < grid.length; i++, nextI++) {
			if (nextI < newGrid.length) {
				newGrid[nextI] = new int[newSide][newSide];
			}
			for (int j = 0; j < grid.length; j++) {
				for (int k = 0; k < grid.length; k++) {
					int value = grid[i][j][k];
					int relevantNeighborCount = 0;
					int neighborValue;
					neighborValue = getFromIndex(i + 1, j, k);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = RIGHT;
						relevantNeighborCount++;
					}
					neighborValue = getFromIndex(i - 1, j, k);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = LEFT;
						relevantNeighborCount++;
					}
					neighborValue = getFromIndex(i, j + 1, k);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = UP;
						relevantNeighborCount++;
					}
					neighborValue = getFromIndex(i, j - 1, k);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = DOWN;
						relevantNeighborCount++;
					}
					neighborValue = getFromIndex(i, j, k + 1);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = FRONT;
						relevantNeighborCount++;
					}
					neighborValue = getFromIndex(i, j, k - 1);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = BACK;
						relevantNeighborCount++;
					}
					
					if (relevantNeighborCount > 0) {
						//sort
						Utils.sortDescending(relevantNeighborCount, neighborValues, sortedNeighborsIndexes);
						//divide
						boolean isFirstNeighbor = true;
						int previousNeighborValue = 0;
						for (int neighborIndex = 0; neighborIndex < relevantNeighborCount; neighborIndex++,isFirstNeighbor = false) {
							neighborValue = neighborValues[neighborIndex];
							if (neighborValue != previousNeighborValue || isFirstNeighbor) {
								int shareCount = relevantNeighborCount - neighborIndex + 1;
								int toShare = value - neighborValue;
								int share = toShare/shareCount;
								if (share != 0) {
									checkBoundsReached(i + indexOffset, j + indexOffset, k + indexOffset, newGrid.length);
									changed = true;
									value = value - toShare + toShare%shareCount + share;
									for (int remainingNeighborIndex = neighborIndex; remainingNeighborIndex < relevantNeighborCount; remainingNeighborIndex++) {
										int[] nc = getNeighborCoordinates(i, j, k, neighborDirections[sortedNeighborsIndexes[remainingNeighborIndex]]);
										newGrid[nc[0] + indexOffset][nc[1] + indexOffset][nc[2] + indexOffset] += share;
									}
								}
								previousNeighborValue = neighborValue;
							}
						}	
					}					
					newGrid[i + indexOffset][j + indexOffset][k + indexOffset] += value;
				}
			}
			if (!first) {
				grid[i-1] = null;
			} else {
				first = false;
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
	
	private void checkBoundsReached(int i, int j, int k, int length) {
		if (i == 1 || i == length - 2 || 
			j == 1 || j == length - 2 || 
			k == 1 || k == length - 2) {
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
	
	@Override
	public int getFromPosition(int x, int y, int z) {	
		int i = originIndex + x;
		int j = originIndex + y;
		int k = originIndex + z;
		return getFromIndex(i, j, k);
	}
	
	private int getFromIndex(int i, int j, int k) {
		if (i < 0 || i > grid.length - 1 
				|| j < 0 || j > grid.length - 1
				|| k < 0 || k > grid.length - 1) {
			//If the passed coordinates are outside the array, the value will be zero
			return 0;
		} else {
			//Note that the indexes whose value hasn't been defined have value zero by default
			return grid[i][j][k];
		}
	}
	
	@Override
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
	
	@Override
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
	
	@Override
	public int getMinY() {
		return getMinX();
	}
	
	@Override
	public int getMaxY() {
		return getMaxX();
	}
	
	@Override
	public int getMinZ() {
		return getMinX();
	}
	
	@Override
	public int getMaxZ() {
		return getMaxX();
	}
	
	@Override
	public long getStep() {
		return step;
	}

	@Override
	public String getName() {
		return "Aether";
	}

	@Override
	public String getSubfolderPath() {
		return getName() + "/3D/random/" + initialSide + "/min=" + minValue + "_max=" + maxValue + "/" + timeStamp;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		Utils.serializeToFile(this, backupPath, backupName);
	}
	
}