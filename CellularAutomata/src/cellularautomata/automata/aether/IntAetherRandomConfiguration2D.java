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
import java.util.concurrent.ThreadLocalRandom;

import cellularautomata.Direction;
import cellularautomata.Utils;
import cellularautomata.model2d.IntModel2D;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 2D with a random initial configuration
 * 
 * @author Jaume
 *
 */
public class IntAetherRandomConfiguration2D implements IntModel2D, Serializable {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8460494265821920620L;
	
	/** A 2D array representing the grid */
	private int[][] grid;

	private String timeStamp;
	private int initialSide;
	private int minValue;
	private int maxValue;
	private long step;
	
	/** The index of the origin within the array */
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
	public IntAetherRandomConfiguration2D(int initialSide, int minValue, int maxValue) {
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
		final int dimension = 2;
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
		timeStamp = Utils.getFileNameSafeTimeStamp();
		int bufferMargin = 2;
		int doubleBufferMargin = 2*bufferMargin;
		grid = new int[initialSide + doubleBufferMargin][initialSide + doubleBufferMargin];
		//The origin will be at the center of the array
		originIndex = (grid.length - 1)/2;
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int bufferMarginPlusSide = bufferMargin + initialSide;
		for (int i = bufferMargin; i < bufferMarginPlusSide; i++) {
			for (int j = bufferMargin; j < bufferMarginPlusSide; j++) {
				grid[i][j] = random.nextInt(minValue, maxValue + 1);
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
	public IntAetherRandomConfiguration2D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		IntAetherRandomConfiguration2D data = (IntAetherRandomConfiguration2D) Utils.deserializeFromFile(backupPath);
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
		int[][] newGrid = null;
		//The offset between the indexes of the new and old array
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new int[grid.length + 2][grid.length + 2];
			indexOffset = 1;
		} else {
			newGrid = new int[grid.length][grid.length];
		}
		boolean changed = false;
		int[] neighborValues = new int[4];
		int[] sortedNeighborsIndexes = new int[4];
		Direction[] neighborDirections = new Direction[4];
		//For every cell
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				//Distribute the cell's value among its neighbors (von Neumann) using the algorithm
				
				//Get the cell's value
				int value = grid[i][j];
				//Get a list of the neighbors whose value is smaller than the one at the current cell
				int relevantNeighborCount = 0;
				int neighborValue;
				if (i < grid.length - 1)
					neighborValue = grid[i + 1][j];
				else
					neighborValue = 0;
				if (neighborValue < value) {
					neighborValues[relevantNeighborCount] = neighborValue;
					neighborDirections[relevantNeighborCount] = Direction.RIGHT;
					relevantNeighborCount++;
				}
				if (i > 0)
					neighborValue = grid[i - 1][j];
				else
					neighborValue = 0;
				if (neighborValue < value) {
					neighborValues[relevantNeighborCount] = neighborValue;
					neighborDirections[relevantNeighborCount] = Direction.LEFT;
					relevantNeighborCount++;
				}
				if (j < grid.length - 1)
					neighborValue = grid[i][j + 1];
				else
					neighborValue = 0;
				if (neighborValue < value) {
					neighborValues[relevantNeighborCount] = neighborValue;
					neighborDirections[relevantNeighborCount] = Direction.UP;
					relevantNeighborCount++;
				}
				if (j > 0)
					neighborValue = grid[i][j - 1];
				else
					neighborValue = 0;
				if (neighborValue < value) {
					neighborValues[relevantNeighborCount] = neighborValue;
					neighborDirections[relevantNeighborCount] = Direction.DOWN;
					relevantNeighborCount++;
				}

				//If there are any
				if (relevantNeighborCount > 0) {
					//sort
					Utils.sortDescending(relevantNeighborCount, neighborValues, sortedNeighborsIndexes);
					//divide
					int previousNeighborValue = value;//all relevant neighbors' values are different from the current value
					for (int neighborIndex = 0; neighborIndex < relevantNeighborCount; neighborIndex++) {
						neighborValue = neighborValues[neighborIndex];
						if (neighborValue != previousNeighborValue) {
							int shareCount = relevantNeighborCount - neighborIndex + 1;
							int toShare = value - neighborValue;
							int share = toShare/shareCount;
							if (share != 0) {
								checkBoundsReached(i + indexOffset, j + indexOffset, newGrid.length);
								changed = true;
								value = value - toShare + toShare%shareCount + share;
								for (int remainingNeighborIndex = neighborIndex; remainingNeighborIndex < relevantNeighborCount; remainingNeighborIndex++) {
									int[] nc = getNeighborCoordinates(i, j, neighborDirections[sortedNeighborsIndexes[remainingNeighborIndex]]);
									newGrid[nc[0] + indexOffset][nc[1] + indexOffset] += share;
								}
							}
							previousNeighborValue = neighborValue;
						}
					}	
				}
				newGrid[i + indexOffset][j + indexOffset] += value;
			}
		}
		//Replace the old array with the new one
		this.grid = newGrid;
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
	
	private void checkBoundsReached(int i, int j, int length) {
		if (i == 1 || i == length - 2 || 
			j == 1 || j == length - 2) {
			boundsReached = true;
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	private static int[] getNeighborCoordinates(int x, int y, Direction direction) {
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
		}
		return new int[]{
			x, y
		};
	}
	
	@Override
	public int getFromPosition(int x, int y) {
		int i = originIndex + x;
		int j = originIndex + y;
		//Note that the indexes whose value hasn't been defined have value zero by default
		return grid[i][j];
	}
	
	@Override
	public int getMinX() {
		int arrayMinX = -originIndex;
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
	public long getStep() {
		return step;
	}

	@Override
	public String getName() {
		return "Aether";
	}
	
	@Override
	public String getSubfolderPath() {
		return getName() + "/2D/random/" + initialSide + "/min=" + minValue + "_max=" + maxValue + "/" + timeStamp;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		Utils.serializeToFile(this, backupPath, backupName);
	}
}
