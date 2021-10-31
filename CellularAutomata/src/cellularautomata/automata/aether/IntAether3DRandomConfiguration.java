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
package cellularautomata.automata.aether;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
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
		grid = new int[initialSide + doubleBufferMargin][initialSide + doubleBufferMargin][initialSide + doubleBufferMargin];
		originIndex = (grid.length - 1)/2;
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int bufferMarginPlusSide = bufferMargin + initialSide;
		for (int x = bufferMargin; x < bufferMarginPlusSide; x++) {
			for (int y = bufferMargin; y < bufferMarginPlusSide; y++) {
				for (int z = bufferMargin; z < bufferMarginPlusSide; z++) {
					grid[x][y][z] = random.nextInt(minValue, maxValue + 1);
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
	}
	
	@Override
	public boolean nextStep(){
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
		byte[] neighborDirections = new byte[6];
		//For every position
		for (int x = 0, nextX = x + 1 + indexOffset; x < grid.length; x++, nextX++) {
			if (nextX < newGrid.length) {
				newGrid[nextX] = new int[newSide][newSide];
			}
			for (int y = 0; y < grid.length; y++) {
				for (int z = 0; z < grid.length; z++) {
					int value = grid[x][y][z];
					int relevantNeighborCount = 0;
					int neighborValue;
					neighborValue = getFromIndex(x + 1, y, z);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = RIGHT;
						relevantNeighborCount++;
					}
					neighborValue = getFromIndex(x - 1, y, z);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = LEFT;
						relevantNeighborCount++;
					}
					neighborValue = getFromIndex(x, y + 1, z);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = UP;
						relevantNeighborCount++;
					}
					neighborValue = getFromIndex(x, y - 1, z);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = DOWN;
						relevantNeighborCount++;
					}
					neighborValue = getFromIndex(x, y, z + 1);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = FRONT;
						relevantNeighborCount++;
					}
					neighborValue = getFromIndex(x, y, z - 1);
					if (neighborValue < value) {
						neighborValues[relevantNeighborCount] = neighborValue;
						neighborDirections[relevantNeighborCount] = BACK;
						relevantNeighborCount++;
					}
					
					if (relevantNeighborCount > 0) {
						//sort
						Utils.sortNeighborsByValueDesc(relevantNeighborCount, neighborValues, neighborDirections);
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
									checkBoundsReached(x + indexOffset, y + indexOffset, z + indexOffset, newGrid.length);
									changed = true;
									value = value - toShare + toShare%shareCount + share;
									for (int j = i; j < relevantNeighborCount; j++) {
										int[] nc = getNeighborCoordinates(x, y, z, neighborDirections[j]);
										newGrid[nc[0] + indexOffset][nc[1] + indexOffset][nc[2] + indexOffset] += share;
									}
								}
								previousNeighborValue = neighborValue;
							}
						}	
					}					
					newGrid[x + indexOffset][y + indexOffset][z + indexOffset] += value;
				}
			}
			if (!first) {
				grid[x-1] = null;
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
		//Return whether or not the state of the grid changed
		return changed;
	}
	
	private void checkBoundsReached(int x, int y, int z, int length) {
		if (x == 1 || x == length - 2 || 
			y == 1 || y == length - 2 || 
			z == 1 || z == length - 2) {
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
	public int getFromPosition(int x, int y, int z){	
		int arrayX = originIndex + x;
		int arrayY = originIndex + y;
		int arrayZ = originIndex + z;
		return getFromIndex(arrayX, arrayY, arrayZ);
	}
	
	private int getFromIndex(int arrayX, int arrayY, int arrayZ){
		if (arrayX < 0 || arrayX > grid.length - 1 
				|| arrayY < 0 || arrayY > grid.length - 1
				|| arrayZ < 0 || arrayZ > grid.length - 1) {
			//If the entered position is outside the array the value will be zero
			return 0;
		} else {
			//Note that the positions whose value hasn't been defined have value zero by default
			return grid[arrayX][arrayY][arrayZ];
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
		int arrayMinY = - originIndex;
		int valuesMinY;
		if (boundsReached) {
			valuesMinY = arrayMinY;
		} else {
			valuesMinY = arrayMinY + 1;
		}
		return valuesMinY;
	}
	
	@Override
	public int getMaxY() {
		int arrayMaxY = grid.length - 1 - originIndex;
		int valuesMaxY;
		if (boundsReached) {
			valuesMaxY = arrayMaxY;
		} else {
			valuesMaxY = arrayMaxY - 1;
		}
		return valuesMaxY;
	}
	
	@Override
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
	
	@Override
	public int getMaxZ() {
		int arrayMaxZ = grid.length - 1 - originIndex;
		int valuesMaxZ;
		if (boundsReached) {
			valuesMaxZ = arrayMaxZ;
		} else {
			valuesMaxZ = arrayMaxZ - 1;
		}
		return valuesMaxZ;
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