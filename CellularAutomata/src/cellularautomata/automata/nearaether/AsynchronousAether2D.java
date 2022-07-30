/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2022 Jaume Ribas

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
import java.util.ArrayList;
import java.util.List;

import cellularautomata.Utils;
import cellularautomata.automata.Neighbor;
import cellularautomata.model2d.LongModel2D;

/**
 * Asynchronous variation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton to test its abelianness
 * 
 * @author Jaume
 *
 */
public class AsynchronousAether2D implements LongModel2D, Serializable {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -531595590796258827L;
	
	public static final long MAX_INITIAL_VALUE = Long.MAX_VALUE;
	public static final long MIN_INITIAL_VALUE = -6148914691236517205L;
	
	private static final byte UP = 0;
	private static final byte DOWN = 1;
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;
	
	/** A 2D array representing the grid */
	private long[][] grid;
	
	private long initialValue;
	private long step;
	
	/** The index of the origin within the array */
	private int originIndex;
	
	/** The indexes of the cell to topple */
	private int topplingPositionIndex1;
	private int topplingPositionIndex2;
	
	/** Whether or not the state of the grid changed after toppling all cells*/
	private boolean changed = false;

	/** Whether or not the values reached the bounds of the array*/
	private boolean boundsReached;
	
	private boolean resizeGrid = false;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public AsynchronousAether2D(long initialValue) {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of long type
			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
	    }
		this.initialValue = initialValue;
		int side = 5;
		grid = new long[side][side];
		//The origin will be at the center of the array
		originIndex = (side - 1)/2;
		grid[originIndex][originIndex] = initialValue;
		boundsReached = false;
		//the indexes of the toppling cell
		topplingPositionIndex1 = 1;
		topplingPositionIndex2 = 1;
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
	public AsynchronousAether2D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		AsynchronousAether2D data = (AsynchronousAether2D) Utils.deserializeFromFile(backupPath);
		grid = data.grid;
		initialValue = data.initialValue;
		step = data.step;
		originIndex = data.originIndex;
		topplingPositionIndex1 = data.topplingPositionIndex1;
		topplingPositionIndex2 = data.topplingPositionIndex2;
		changed = data.changed;
		boundsReached = data.boundsReached;
		resizeGrid = data.resizeGrid;
	}
	
	@Override
	public boolean nextStep() {
		//Use new array to store the values of the next step
		long[][] newGrid = null;
		//The offset between the indexes of the new and old array
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (resizeGrid) {
			resizeGrid = false;
			newGrid = new long[grid.length + 2][grid.length + 2];
			indexOffset = 1;
		} else {
			newGrid = new long[grid.length][grid.length];
		}
		//Distribute the cell's value among its neighbors (von Neumann) using the algorithm
		
		//Get the cell's value
		long topplingValue = grid[topplingPositionIndex1][topplingPositionIndex2];
		//Get a list of the neighbors whose value is smaller than the one at the current cell
		List<Neighbor<Long>> neighbors = new ArrayList<Neighbor<Long>>(4);						
		long neighborValue;
		if (topplingPositionIndex1 < grid.length - 1)
			neighborValue = grid[topplingPositionIndex1 + 1][topplingPositionIndex2];
		else
			neighborValue = 0;
		if (neighborValue < topplingValue)
			neighbors.add(new Neighbor<Long>(RIGHT, neighborValue));
		if (topplingPositionIndex1 > 0)
			neighborValue = grid[topplingPositionIndex1 - 1][topplingPositionIndex2];
		else
			neighborValue = 0;
		if (neighborValue < topplingValue)
			neighbors.add(new Neighbor<Long>(LEFT, neighborValue));
		if (topplingPositionIndex2 < grid[topplingPositionIndex1].length - 1)
			neighborValue = grid[topplingPositionIndex1][topplingPositionIndex2 + 1];
		else
			neighborValue = 0;
		if (neighborValue < topplingValue)
			neighbors.add(new Neighbor<Long>(UP, neighborValue));
		if (topplingPositionIndex2 > 0)
			neighborValue = grid[topplingPositionIndex1][topplingPositionIndex2 - 1];
		else
			neighborValue = 0;
		if (neighborValue < topplingValue)
			neighbors.add(new Neighbor<Long>(DOWN, neighborValue));

		//If there are any
		if (neighbors.size() > 0) {
			//Sort them by value in ascending order
			boolean sorted = false;
			while (!sorted) {
				sorted = true;
				for (int i = neighbors.size() - 2; i >= 0; i--) {
					Neighbor<Long> next = neighbors.get(i+1);
					if (neighbors.get(i).getValue() > next.getValue()) {
						sorted = false;
						neighbors.remove(i+1);
						neighbors.add(i, next);
					}
				}
			}
			boolean isFirst = true;
			long previousNeighborValue = 0;
			//Apply the algorithm
			for (int i = neighbors.size() - 1; i >= 0; i--,isFirst = false) {
				neighborValue = neighbors.get(i).getValue();
				if (neighborValue != previousNeighborValue || isFirst) {
					//add one for the current cell
					int shareCount = neighbors.size() + 1;
					long toShare = topplingValue - neighborValue;
					long share = toShare/shareCount;
					if (share != 0) {
						checkBoundsReached(topplingPositionIndex1 + indexOffset, topplingPositionIndex2 + indexOffset, newGrid.length);
						changed = true;
						//the current cell keeps the remainder and one share
						topplingValue = topplingValue - toShare + toShare%shareCount + share;
						for (Neighbor<Long> n : neighbors) {
							int[] nc = getNeighborCoordinates(topplingPositionIndex1, topplingPositionIndex2, n.getDirection());
							newGrid[nc[0] + indexOffset][nc[1] + indexOffset] += share;
						}
					}
					previousNeighborValue = neighborValue;
				}
				neighbors.remove(i);
			}	
		}
		newGrid[topplingPositionIndex1 + indexOffset][topplingPositionIndex2 + indexOffset] += topplingValue;
		//For every cell
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				if (i != topplingPositionIndex1 || j != topplingPositionIndex2) {
					long value = grid[i][j];
					newGrid[i + indexOffset][j + indexOffset] += value;
				}
			}
		}
		boolean previousChanged = true;
		//next position to topple		
		if (topplingPositionIndex1 < grid.length - 2) {
			topplingPositionIndex1++;
		} else {
			topplingPositionIndex1 = 1;
			if (topplingPositionIndex2 < grid.length - 2) {
				topplingPositionIndex2++;
			} else {
				topplingPositionIndex2 = 1;
				previousChanged = changed;
				changed = false;
				resizeGrid = boundsReached;
				boundsReached = false;
			}
		}
		//Replace the old array with the new one
		this.grid = newGrid;
		//Update the index of the origin
		originIndex += indexOffset;
		//Increase the current step by one
		step++;
		//Return whether or not the state of the grid changed
		return previousChanged;
	}
	
	private void checkBoundsReached(int i, int j, int length) {
		if (i == 1 || i == length - 2 || 
			j == 1 || j == length - 2) {
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
	
	@Override
	public long getFromPosition(int x, int y) {	
		int i = originIndex + x;
		int j = originIndex + y;
		if (i < 0 || i > grid.length - 1 
				|| j < 0 || j > grid.length - 1) {
			//If the entered position is outside the array the value will be the backgroundValue
			return 0;
		} else {
			//Note that the positions whose value hasn't been defined have value zero by default
			return grid[i][j];
		}
	}
	
	@Override
	public int getMinX() {
		int arrayMinX = - originIndex;
		int valuesMinX = arrayMinX;
		return valuesMinX;
	}
	
	@Override
	public int getMaxX() {
		int arrayMaxX = grid.length - 1 - originIndex;
		int valuesMaxX = arrayMaxX;
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
		return "AsynchronousAether";
	}
	
	@Override
	public String getSubfolderPath() {
		return getName() + "/2D/" + initialValue;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		Utils.serializeToFile(this, backupPath, backupName);
	}
	
}
