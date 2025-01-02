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
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import cellularautomata.arrays.HypercubicIntArray;
import cellularautomata.Utils;
import cellularautomata.Coordinates;
import cellularautomata.model.IsotropicHypercubicModelA;
import cellularautomata.model.SymmetricIntModel;
import cellularautomata.numbers.BigInt;

/**
 * Simplified implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton, with a single source initial configuration, for review and testing purposes
 * 
 * @author Jaume
 *
 */
public class SimpleIntAether implements SymmetricIntModel, IsotropicHypercubicModelA {//TODO fix 1D error

	private final int gridDimension;
	private long step;
	private final int initialValue;
	
	/** A hypercubic array representing the grid */
	private HypercubicIntArray grid;
	
	/** The index of the origin within the array */
	private int originIndex;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	
	private final int neighborCount;
	/** Whether or not the state of the model changed between the current and the previous step **/
	private Boolean changed = null;
	
	public SimpleIntAether(int gridDimension, int initialValue) {
		if (gridDimension != 0) {
			int minInitialValue = AetherUtils.getMinAllowedSingleSourceValue(gridDimension, BigInt.valueOf(Integer.MAX_VALUE)).intValue();
			if (initialValue < minInitialValue) {//to prevent overflow of int type
				throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d for dimension %,d. Use a greater initial value or a different implementation.", minInitialValue, gridDimension));
			}
		}
		this.gridDimension = gridDimension;
		//two neighbors per dimension
		neighborCount = gridDimension * 2;
		this.initialValue = initialValue;
		//Create a hypercubic array to represent the grid. With the initial value at the origin.
		//Make the array of side 5 so as to leave a margin of two cells around the center.
		int side = 5;
		grid = new HypercubicIntArray(gridDimension, side);
		//The origin will be at the center of the array
		originIndex = side/2;
		int[] indexes = new int[gridDimension];
		Arrays.fill(indexes, originIndex);
		grid.set(new Coordinates(indexes), initialValue);
		boundsReached = false;
		//Set the current step to zero
		step = 0;
	}
	
	@Override
	public int getGridDimension() {
		return gridDimension;
	}

	@Override
	public int getFromPosition(Coordinates coordinates) {
		int[] indexes = new int[gridDimension];
		coordinates.copyIntoArray(indexes);
		Utils.addToArray(indexes, originIndex);
		return grid.get(new Coordinates(indexes));
	}

	@Override
	public int getFromAsymmetricPosition(Coordinates coordinates) {
		return getFromPosition(coordinates);
	}

	@Override
	public Boolean nextStep() {
		//Use new array to store the values of the next step
		HypercubicIntArray newGrid = null;
		//The offset between the indexes of the new and old array
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new HypercubicIntArray(gridDimension, grid.getSide() + 2);
			indexOffset = 1;
		} else {
			newGrid = new HypercubicIntArray(gridDimension, grid.getSide());
		}
		AetherConsumer aetherConsumer = new AetherConsumer(newGrid, indexOffset);
		//For every cell apply rules
		grid.forEachIndex(aetherConsumer);
		//Replace the old array with the new one
		grid = newGrid;
		//Update the index of the origin
		originIndex += indexOffset;
		//Increase the current step by one
		step++;
		changed = aetherConsumer.changed;
		//Return whether or not the state of the grid changed
		return changed;
	}

	@Override
	public Boolean isChanged() {
		return changed;
	}
	
	class AetherConsumer implements Consumer<Coordinates> {
		
		public boolean changed = false;
		private int gridSide;
		private HypercubicIntArray newGrid;
		private int indexOffset;
		
		public AetherConsumer(HypercubicIntArray newGrid, int indexOffset) {
			this.newGrid = newGrid;
			this.indexOffset = indexOffset;
			gridSide = grid.getSide();
		}
		
		@Override
		public void accept(Coordinates currentIndexes) {
			//Distribute the cell's value among its neighbors (von Neumann) using the algorithm
			
			//Get the cell's value
			int value = grid.get(currentIndexes);
			int relevantNeighborCount = 0;
			int[] relevantNeighborValues = new int[neighborCount];
			int[][] relevantNeighborIndexes = new int[neighborCount][gridDimension];
			
			int[] indexes = new int[gridDimension];
			currentIndexes.copyIntoArray(indexes);
			boolean isPositionCloseToEdge = false;
			//Get a list of the neighbors whose value is smaller than the one at the current cell
			for (int axis = 0; axis < gridDimension; axis++) {
				int indexOnAxis = indexes[axis];
				//Check whether or not we reached the edge of the array
				if (indexOnAxis <= 1 || indexOnAxis >= gridSide - 2) {
					isPositionCloseToEdge = true;
				}
				if (indexOnAxis < gridSide - 1) {
					indexes[axis] = indexOnAxis + 1;
					int neighborValue = grid.get(new Coordinates(indexes));
					if (neighborValue < value) {
						relevantNeighborValues[relevantNeighborCount] = neighborValue;
						int[] neighborIndexes = indexes.clone();
						Utils.addToArray(neighborIndexes, indexOffset);
						relevantNeighborIndexes[relevantNeighborCount] = neighborIndexes;
						relevantNeighborCount++;
					}
					indexes[axis] = indexOnAxis - 1;
					if (indexOnAxis > 0) {
						neighborValue = grid.get(new Coordinates(indexes));
					} else {
						neighborValue = 0; 
					}
					if (neighborValue < value) {
						relevantNeighborValues[relevantNeighborCount] = neighborValue;
						int[] neighborIndexes = indexes.clone();
						Utils.addToArray(neighborIndexes, indexOffset);
						relevantNeighborIndexes[relevantNeighborCount] = neighborIndexes;
						relevantNeighborCount++;
					}
				} else {
					indexes[axis] = indexOnAxis + 1;
					int neighborValue = 0; 
					if (neighborValue < value) {
						relevantNeighborValues[relevantNeighborCount] = neighborValue;
						int[] neighborIndexes = indexes.clone();
						Utils.addToArray(neighborIndexes, indexOffset);
						relevantNeighborIndexes[relevantNeighborCount] = neighborIndexes;
						relevantNeighborCount++;
					}
					indexes[axis] = indexOnAxis - 1;
					//if the grid side were one, this would be out of bounds.
					//but since it starts at 5 and only gets bigger it's fine
					neighborValue = grid.get(new Coordinates(indexes));
					if (neighborValue < value) {
						relevantNeighborValues[relevantNeighborCount] = neighborValue;
						int[] neighborIndexes = indexes.clone();
						Utils.addToArray(neighborIndexes, indexOffset);
						relevantNeighborIndexes[relevantNeighborCount] = neighborIndexes;
						relevantNeighborCount++;
					}
				}
				indexes[axis] = indexOnAxis;//reset index
			}
			if (relevantNeighborCount > 0) {
				//sort neighbors by value
				int[] sortedPositions = new int[relevantNeighborCount];
				Utils.sortDescending(relevantNeighborCount, relevantNeighborValues, sortedPositions);
				//apply algorithm rules to redistribute value
				boolean isFirst = true;
				int previousNeighborValue = 0;
				int shareCount = relevantNeighborCount + 1;
				for (int neighborIndex = 0; neighborIndex < relevantNeighborCount; neighborIndex++, shareCount--, isFirst = false) {
					int neighborValue = relevantNeighborValues[neighborIndex];
					if (neighborValue != previousNeighborValue || isFirst) {
						int toShare = value - neighborValue;
						int share = toShare/shareCount;
						if (share != 0) {
							if (isPositionCloseToEdge)
								boundsReached = true;
							changed = true;
							value = value - toShare + toShare%shareCount + share;//TODO change to -= everywhere?
							for (int i = neighborIndex; i < relevantNeighborCount; i++) {
								int[] neighborIndexes = relevantNeighborIndexes[sortedPositions[i]];
								newGrid.addAndGet(new Coordinates(neighborIndexes), share);
							}
						}
						previousNeighborValue = neighborValue;
					}
				}	
			}
			Utils.addToArray(indexes, indexOffset);
			newGrid.addAndGet(new Coordinates(indexes), value);
		}
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
		return getName() + "/" + gridDimension + "D/" + initialValue;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void forEach(IntConsumer consumer) {
		grid.forEach(consumer);
	}

	@Override
	public int getAsymmetricMaxCoordinate(int axis) {
		return grid.getSide() - 1 - originIndex;
	}
	
}
