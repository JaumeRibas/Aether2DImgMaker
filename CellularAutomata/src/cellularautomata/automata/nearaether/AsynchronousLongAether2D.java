/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2024 Jaume Ribas

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
public class AsynchronousLongAether2D implements LongModel2D, Serializable {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -531595590796258827L;
	
	private static final byte UP = 0;
	private static final byte DOWN = 1;
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;
	
	/** A 2D array representing the grid */
	private long[][] grid;
	
	private final long initialValue;
	private long step;
	
	/** The index of the origin within the array */
	private int originIndex;

	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	
	/** Whether or not the state of the model changed between the current and the previous step **/
	private Boolean changed = null;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public AsynchronousLongAether2D(long initialValue) {
		this.initialValue = initialValue;
		int side = 5;
		grid = new long[side][side];
		//The origin will be at the center of the array
		originIndex = (side - 1)/2;
		grid[originIndex][originIndex] = initialValue;
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
	public AsynchronousLongAether2D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		AsynchronousLongAether2D data = (AsynchronousLongAether2D) Utils.deserializeFromFile(backupPath);
		grid = data.grid;
		initialValue = data.initialValue;
		step = data.step;
		originIndex = data.originIndex;
		changed = data.changed;
		boundsReached = data.boundsReached;
		changed = data.changed;
	}
	
	@Override
	public Boolean nextStep() {
		//Use new array to store the values of the next step
		long[][] newGrid = null;
		//The offset between the indexes of the new and old array
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			//For simplicity expand the region in all directions
			boundsReached = false;
			newGrid = new long[grid.length + 2][grid.length + 2];
			indexOffset = 1;
		} else {
			newGrid = new long[grid.length][grid.length];
		}
		boolean changed = false;
		//For every cell
		int i = 0;
		for (; i < grid.length && !changed; i++) {
			int j = 0;
			for (; j < grid.length && !changed; j++) {
				//Distribute the cell's value among its neighbors (von Neumann) using the algorithm
				
				//Get the cell's value
				long value = grid[i][j];
				//Get a list of the neighbors whose value is smaller than the one at the current cell
				List<Neighbor<Long>> neighbors = new ArrayList<Neighbor<Long>>(4);						
				long neighborValue;
				if (i < grid.length - 1)
					neighborValue = grid[i + 1][j];
				else
					neighborValue = 0;
				if (neighborValue < value)
					neighbors.add(new Neighbor<Long>(RIGHT, neighborValue));
				if (i > 0)
					neighborValue = grid[i - 1][j];
				else
					neighborValue = 0;
				if (neighborValue < value)
					neighbors.add(new Neighbor<Long>(LEFT, neighborValue));
				if (j < grid.length - 1)
					neighborValue = grid[i][j + 1];
				else
					neighborValue = 0;
				if (neighborValue < value)
					neighbors.add(new Neighbor<Long>(UP, neighborValue));
				if (j > 0)
					neighborValue = grid[i][j - 1];
				else
					neighborValue = 0;
				if (neighborValue < value)
					neighbors.add(new Neighbor<Long>(DOWN, neighborValue));

				//If there are any
				if (neighbors.size() > 0) {
					//Sort them by value in ascending order
					boolean sorted = false;
					while (!sorted) {
						sorted = true;
						for (int neighborIndex = neighbors.size() - 2; neighborIndex >= 0; neighborIndex--) {
							Neighbor<Long> next = neighbors.get(neighborIndex+1);
							if (neighbors.get(neighborIndex).getValue() > next.getValue()) {
								sorted = false;
								neighbors.remove(neighborIndex+1);
								neighbors.add(neighborIndex, next);
							}
						}
					}
					long previousNeighborValue = value;//all relevant neighbors' values are different from the current value
					//Apply the algorithm
					for (int neighborIndex = neighbors.size() - 1; neighborIndex >= 0; neighborIndex--) {
						neighborValue = neighbors.get(neighborIndex).getValue();
						if (neighborValue != previousNeighborValue) {
							//Add one for the current cell
							int shareCount = neighbors.size() + 1;
							long toShare = value - neighborValue;
							long share = toShare/shareCount;
							if (share != 0) {
								checkBoundsReached(i + indexOffset, j + indexOffset, newGrid.length);
								changed = true;
								//The current cell keeps the remainder and one share
								value = value - toShare + toShare%shareCount + share;
								for (Neighbor<Long> n : neighbors) {
									int[] nc = getNeighborCoordinates(i, j, n.getDirection());
									newGrid[nc[0] + indexOffset][nc[1] + indexOffset] += share;
								}
							}
							previousNeighborValue = neighborValue;
						}
						neighbors.remove(neighborIndex);
					}	
				}					
				newGrid[i + indexOffset][j + indexOffset] += value;
			}
			//if a cell already toppled just move the value from the old array into the new one for the remaining cells
			for (; j < grid.length; j++) {
				newGrid[i + indexOffset][j + indexOffset] += grid[i][j];
			}
		}
		//if a cell already toppled just move the value from the old array into the new one for the remaining cells
		for (; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				newGrid[i + indexOffset][j + indexOffset] += grid[i][j];
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
		//Note that the indexes whose value hasn't been defined have value zero by default
		return grid[i][j];
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
	public int getMinX() {
		return -getMaxX();
	}
	
	@Override
	public int getMaxY() {
		return getMaxX();
	}
	
	@Override
	public int getMinY() {
		return -getMaxY();
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
		throw new UnsupportedOperationException();
	}
	
}
