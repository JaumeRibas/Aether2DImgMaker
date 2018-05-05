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

public class Aether2DConfigurationTesting extends LongCellularAutomaton2D {	
	
	private static final byte UP = 0;
	private static final byte DOWN = 1;
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;
	
	/** A 2D array representing the grid */
	private long[][] grid;
	
	private String initialConfigurationName;
	private long currentStep = 0;
	
	/** The indexes of the origin within the array */
	private int xOriginIndex;
	private int yOriginIndex;

	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached = true;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialConfiguration
	 * @param initialConfigurationName a String with the name of the initial configuration, safe for using in file names
	 */
	public Aether2DConfigurationTesting(long[][] initialConfiguration, String initialConfigurationName) {
		grid = initialConfiguration;
		this.initialConfigurationName = initialConfigurationName;
		//The origin will be at the center of the array
		xOriginIndex = (grid.length - 1)/2;
		yOriginIndex = (grid[0].length - 1)/2;
	}
	
	/**
	 * Computes the next step of the automaton and returns whether
	 * or not the state of the grid changed. 
	 *  
	 * @return true if the state of the grid changed or false otherwise
	 */
	public boolean nextStep(){
		//Use new array to store the values of the next step
		long[][] newGrid = null;
		//The offset between the indexes of the new and old array
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new long[grid.length + 2][grid[0].length + 2];
			indexOffset = 1;
		} else {
			newGrid = new long[grid.length][grid[0].length];
		}
		boolean changed = false;
		long[] neighborValues = new long[4];
		byte[] neighborDirections = new byte[4];
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[0].length; y++) {				
				long value = grid[x][y];
				int relevantNeighborCount = 0;
				long neighborValue;				
				if (x < grid.length - 1)
					neighborValue = grid[x + 1][y];
				else
					neighborValue = 0;
				if (neighborValue < value) {
					neighborValues[relevantNeighborCount] = neighborValue;
					neighborDirections[relevantNeighborCount] = RIGHT;
					relevantNeighborCount++;
				}
				if (x > 0)
					neighborValue = grid[x - 1][y];
				else
					neighborValue = 0;
				if (neighborValue < value) {
					neighborValues[relevantNeighborCount] = neighborValue;
					neighborDirections[relevantNeighborCount] = LEFT;
					relevantNeighborCount++;
				}
				if (y < grid[x].length - 1)
					neighborValue = grid[x][y + 1];
				else
					neighborValue = 0;
				if (neighborValue < value) {
					neighborValues[relevantNeighborCount] = neighborValue;
					neighborDirections[relevantNeighborCount] = UP;
					relevantNeighborCount++;
				}
				if (y > 0)
					neighborValue = grid[x][y - 1];
				else
					neighborValue = 0;
				if (neighborValue < value) {
					neighborValues[relevantNeighborCount] = neighborValue;
					neighborDirections[relevantNeighborCount] = DOWN;
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
						neighborValue = neighborValues[i];
						if (neighborValue != previousNeighborValue || isFirstNeighbor) {
							int shareCount = relevantNeighborCount - i + 1;
							long toShare = value - neighborValue;
							long share = toShare/shareCount;
							if (share != 0) {
								changed = true;
								checkBoundsReached(x, y);
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
		//Update the index of the origin
		xOriginIndex += indexOffset;
		yOriginIndex += indexOffset;
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
	public long getValue(int x, int y){	
		int arrayX = xOriginIndex + x;
		int arrayY = yOriginIndex + y;
		if (arrayX < 0 || arrayX > grid.length - 1 
				|| arrayY < 0 || arrayY > grid[0].length - 1) {
			//If the entered position is outside the array the value will be the backgroundValue
			return 0;
		} else {
			//Note that the positions whose value hasn't been defined have value zero by default
			return grid[arrayX][arrayY];
		}
	}
	
	/**
	 * Returns the smallest x-coordinate of a nonzero value at the current step
	 * 
	 * @return the smallest x of a nonzero value at the current step
	 */
	public int getMinX() {
		int arrayMinX = - xOriginIndex;
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
		int arrayMaxX = grid.length - 1 - xOriginIndex;
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
		int arrayMinY = - yOriginIndex;
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
		int arrayMaxY = grid[0].length - 1 - yOriginIndex;
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
	
	/**
	 * Returns the initial value
	 * 
	 * @return the name of the initial configuration
	 */
	public String getInitialConfigurationName() {
		return initialConfigurationName;
	}

	@Override
	public long getBackgroundValue() {
		return 0;
	}

	@Override
	public String getName() {
		return "Aether2D";
	}
	
	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialConfigurationName;
	}

	@Override
	public LongCellularAutomaton2D caSubGrid(int minX, int maxX, int minY, int maxY) {
		return new LongCASubGrid2D(this, minX, maxX, minY, maxY);
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}
}
