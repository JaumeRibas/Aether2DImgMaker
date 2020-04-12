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
import java.util.Calendar;
import java.util.concurrent.ThreadLocalRandom;

import cellularautomata.evolvinggrid.EvolvingIntGrid2D;

public class Aether2DRandomConfiguration implements EvolvingIntGrid2D {	
	
	private static final byte UP = 0;
	private static final byte DOWN = 1;
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;
	
	/** A 2D array representing the grid */
	private int[][] grid;

	private String timeStamp;
	private int initialSide;
	private int minValue;
	private int maxValue;
	private long currentStep;

	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	
	/**
	 * Creates an instance with an initial configuration consisting of a square area of side {@code initialSide} full 
	 * of random values between {@code minValue} and {@code maxValue}.
	 * 
	 * @param initialSide the side of the square area that will be filled with random values and will be used as initial configuration
	 * @param minValue the minimum value for the random values
	 * @param maxValue the maximum value for the random values
	 */
	public Aether2DRandomConfiguration(int initialSide, int minValue, int maxValue) {
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
			throw new IllegalArgumentException("The range between min and max value is too big.");
		}
		if (initialSide < 1) {
			throw new IllegalArgumentException("Initial side cannot be smaller than one");
		}
		this.initialSide = initialSide;
		this.minValue = minValue;
		this.maxValue = maxValue;
		Calendar currentDate = Calendar.getInstance();
		timeStamp = currentDate.get(Calendar.YEAR) 
				+ "-" + (currentDate.get(Calendar.MONTH) + 1)
				+ "-" + currentDate.get(Calendar.DATE)
				+ "_" + currentDate.get(Calendar.HOUR_OF_DAY)
				+ "" + currentDate.get(Calendar.MINUTE)
				+ "" + currentDate.get(Calendar.SECOND)
				+ "." + currentDate.get(Calendar.MILLISECOND);
		int bufferMargin = 2;
		int doubleBufferMargin = 2*bufferMargin;
		grid = new int[initialSide + doubleBufferMargin][initialSide + doubleBufferMargin];
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int bufferMarginPlusSide = bufferMargin + initialSide;
		for (int x = bufferMargin; x < bufferMarginPlusSide; x++) {
			for (int y = bufferMargin; y < bufferMarginPlusSide; y++) {
				grid[x][y] = random.nextInt(minValue, maxValue + 1);
			}
		}
		boundsReached = false;
		//Set the current step to zero
		currentStep = 0;
	}
	
	/**
	 * Computes the next step of the automaton and returns whether
	 * or not the state of the grid changed. 
	 *  
	 * @return true if the state of the grid changed or false otherwise
	 */
	public boolean nextStep(){
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
		byte[] neighborDirections = new byte[4];
		//For every position
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid.length; y++) {
				//Distribute the positon's value among its neighbors (von Neumann) using the algorithm
				
				//Get the position's value
				int value = grid[x][y];
				//Get a list of the neighbors whose value is smaller than the one at the current position
				int relevantNeighborCount = 0;
				int neighborValue;
				neighborValue = getValueAtPosition(x + 1, y);
				if (neighborValue < value) {
					neighborValues[relevantNeighborCount] = neighborValue;
					neighborDirections[relevantNeighborCount] = RIGHT;
					relevantNeighborCount++;
				}
				neighborValue = getValueAtPosition(x - 1, y);
				if (neighborValue < value) {
					neighborValues[relevantNeighborCount] = neighborValue;
					neighborDirections[relevantNeighborCount] = LEFT;
					relevantNeighborCount++;
				}
				neighborValue = getValueAtPosition(x, y + 1);
				if (neighborValue < value) {
					neighborValues[relevantNeighborCount] = neighborValue;
					neighborDirections[relevantNeighborCount] = UP;
					relevantNeighborCount++;
				}
				neighborValue = getValueAtPosition(x, y - 1);
				if (neighborValue < value) {
					neighborValues[relevantNeighborCount] = neighborValue;
					neighborDirections[relevantNeighborCount] = DOWN;
					relevantNeighborCount++;
				}

				//If there are any
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
								checkBoundsReached(x, y);
								changed = true;
								value = value - toShare + toShare%shareCount + share;
								for (int j = i; j < relevantNeighborCount; j++) {
									int[] nc = getNeighborCoordinates(x, y, neighborDirections[j]);
									newGrid[nc[0] + indexOffset][nc[1] + indexOffset] += share;
								}
							}
							previousNeighborValue = neighborValue;
						}
					}	
				}
				newGrid[x + indexOffset][y + indexOffset] += value;
			}
		}
		//Replace the old array with the new one
		this.grid = newGrid;
		//Increase the current step by one
		currentStep++;
		//Return whether or not the state of the grid changed
		return changed;
	}
	
	private void checkBoundsReached(int x, int y) {
		if (x == 1 || x == grid.length - 2 || 
			y == 1 || y == grid[0].length - 2) {
			boundsReached = true;
		}
	}
	
	private static int[] getNeighborCoordinates(int x, int y, byte direction) {
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
	
	/**
	 * Returns the value at a given position for the current step
	 * 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @return the value at (x,y)
	 */
	public int getValueAtPosition(int x, int y){
		if (x < 0 || x > grid.length - 1 
				|| y < 0 || y > grid[0].length - 1) {
			//If the entered position is outside the array the value will be 0
			return 0;
		} else {
			//Note that the positions whose value hasn't been defined have value zero by default
			return grid[x][y];
		}
	}
	
	/**
	 * Returns the smallest x-coordinate of a nonzero value at the current step
	 * 
	 * @return the smallest x of a nonzero value at the current step
	 */
	public int getMinX() {
		int arrayMinX = 0;
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
		int arrayMaxX = grid.length - 1;
		int valuesMaxX;
		if (boundsReached) {
			valuesMaxX = arrayMaxX;
		} else {
			valuesMaxX = arrayMaxX - 1;
		}
		return valuesMaxX;
	}
	
	/**
	 * Returns the smallest y-coordinate of a nonzero value at the current step
	 * 
	 * @return the smallest y of a nonzero value at the current step
	 */
	public int getMinY() {
		int arrayMinY = 0;
		int valuesMinY;
		if (boundsReached) {
			valuesMinY = arrayMinY;
		} else {
			valuesMinY = arrayMinY + 1;
		}
		return valuesMinY;
	}
	
	/**
	 * Returns the largest y-coordinate of a nonzero value at the current step
	 * 
	 * @return the largest y of a nonzero value at the current step
	 */
	public int getMaxY() {
		int arrayMaxY = grid[0].length - 1;
		int valuesMaxY;
		if (boundsReached) {
			valuesMaxY = arrayMaxY;
		} else {
			valuesMaxY = arrayMaxY - 1;
		}
		return valuesMaxY;
	}
	
	/**
	 * Returns the current step
	 * 
	 * @return the current step
	 */
	public long getStep() {
		return currentStep;
	}

	@Override
	public String getName() {
		return "Aether2D";
	}
	
	@Override
	public String getSubFolderPath() {
		return getName() + "/random/" + initialSide + "/min=" + minValue + "_max=" + maxValue + "/" + timeStamp;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}
}
