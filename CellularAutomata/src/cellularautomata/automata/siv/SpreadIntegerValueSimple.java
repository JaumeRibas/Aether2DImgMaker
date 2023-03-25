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
package cellularautomata.automata.siv;

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

/**
 * Simplified implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/SIV-Cellular-Automaton-Definition">Spread Integer Value</a> cellular automaton, with a single source initial configuration, for review and testing purposes
 * 
 * @author Jaume
 *
 */
public class SpreadIntegerValueSimple implements SymmetricIntModel, IsotropicHypercubicModelA {

	private int gridDimension;
	private long step;
	private final int initialValue;
	private final int backgroundValue;
	
	/** An hypercubic array representing the grid */
	private HypercubicIntArray grid;
	
	/** The index of the origin within the array */
	private int originIndex;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	
	private final int shareCount;

	/** Whether or not the state of the model changed between the current and the previous step **/
	private Boolean changed = null;
	
	public SpreadIntegerValueSimple(int gridDimension, int initialValue, int backgroundValue) {
		this.gridDimension = gridDimension;
		//two neighbors
		shareCount = gridDimension * 2 + 1;
		this.initialValue = initialValue;
		this.backgroundValue = backgroundValue;
		int[] indexes = new int[gridDimension];
		//Create an hypercubic array to represent the grid. With the initial value at the origin.
		//Make the array of side 5 so as to leave a margin of two cells around the center.
		int side = 5;
		grid = new HypercubicIntArray(gridDimension, side);
		grid.fill(backgroundValue);
		//The origin will be at the center of the array
		originIndex = side/2;
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
	public Boolean nextStep() throws Exception {
		//Use new array to store the values of the next step
		HypercubicIntArray newGrid = null;
		//The offset between the indexes of the new and old array
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new HypercubicIntArray(gridDimension, grid.getSide() + 2);
			if (backgroundValue != 0) {
				newGrid.fillEdges(1, backgroundValue);
			}
			indexOffset = 1;
		} else {
			newGrid = new HypercubicIntArray(gridDimension, grid.getSide());
		}
		SpreadIntegerValueConsumer sivConsumer = new SpreadIntegerValueConsumer(newGrid, indexOffset);
		//For every cell apply rules
		grid.forEachIndex(sivConsumer);
		//Replace the old array with the new one
		grid = newGrid;
		//Update the index of the origin
		originIndex += indexOffset;
		//Increase the current step by one
		step++;
		changed = sivConsumer.changed;
		//Return whether or not the state of the grid changed
		return changed;
	}

	@Override
	public Boolean isChanged() {
		return changed;
	}
	
	class SpreadIntegerValueConsumer implements Consumer<Coordinates> {
		public boolean changed = false;
		private int gridSideMinusOne;
		private int gridSide;
		private HypercubicIntArray newGrid;
		private int indexOffset;
		
		public SpreadIntegerValueConsumer(HypercubicIntArray newGrid, int indexOffset) {
			this.newGrid = newGrid;
			this.indexOffset = indexOffset;
			gridSide = grid.getSide();
			gridSideMinusOne = gridSide - 1;
		}
		
		@Override
		public void accept(Coordinates currentIndexes) {
			int value = grid.get(currentIndexes);
			if (value != 0) {
				int[] indexes = new int[gridDimension];
				currentIndexes.copyIntoArray(indexes);
				if (Math.abs(value) >= shareCount) {
					int[] upperNeighborValues = new int[gridDimension];
					int[] lowerNeighborValues = new int[gridDimension];
					boolean[] isUpperNeighborValueEqual = new boolean[gridDimension];
					boolean[] isLowerNeighborValueEqual = new boolean[gridDimension];
					boolean areAllNeighborValuesEqual = true;
					boolean isPositionCloseToEdge = false;
					for (int axis = 0; axis < gridDimension; axis++) {
						int indexOnAxis = indexes[axis];
						//Check whether or not we reached the edge of the array
						if (indexOnAxis == 1 || indexOnAxis == gridSide - 2) {
							isPositionCloseToEdge = true;
						}
						if (indexOnAxis < gridSideMinusOne) {
							indexes[axis] = indexOnAxis + 1;
							upperNeighborValues[axis] = grid.get(new Coordinates(indexes));
							if (indexOnAxis > 0) {
								indexes[axis] = indexOnAxis - 1;
								lowerNeighborValues[axis] = grid.get(new Coordinates(indexes));
							} else {
								lowerNeighborValues[axis] = backgroundValue; 
							}
						} else {
							upperNeighborValues[axis] = backgroundValue;
							indexes[axis] = indexOnAxis - 1;
							//if the grid side where one, this would be out of bounds.
							//but since it starts at 5 and only gets bigger it's fine
							lowerNeighborValues[axis] = grid.get(new Coordinates(indexes));
						}
						indexes[axis] = indexOnAxis;//reset index
						boolean isCurrentUpperNeighborValueEqual = value == upperNeighborValues[axis];
						boolean isCurrentLowerNeighborValueEqual = value == lowerNeighborValues[axis];
						isUpperNeighborValueEqual[axis] = isCurrentUpperNeighborValueEqual;
						isLowerNeighborValueEqual[axis] = isCurrentLowerNeighborValueEqual;
						areAllNeighborValuesEqual = areAllNeighborValuesEqual 
								&& isCurrentUpperNeighborValueEqual 
								&& isCurrentLowerNeighborValueEqual;
					}
					int[] newIndexes = indexes.clone();
					Utils.addToArray(newIndexes, indexOffset);
					//If the current cell is equal to its neighbors, the algorithm has no effect
					if (!areAllNeighborValuesEqual) {
						//Divide its value between the neighbors and itself (using integer division)
						int share = value/shareCount;
						if (share != 0) {
							//I assume that if any share is not zero the state changes (doesn't work for background value != 0 :( )
							changed = true;
							if (isPositionCloseToEdge)
								boundsReached = true;
							//Add the share and the remainder to the corresponding cell in the new array
							newGrid.addAndGet(new Coordinates(newIndexes), value%shareCount + share);
							//Add the share to the neighboring cells
							//If the neighbor's value is equal to the current value, add the share to the current cell instead
							for (int axis = 0; axis < gridDimension; axis++) {
								int newIndexOnAxis = newIndexes[axis];
								if (isUpperNeighborValueEqual[axis]) {
									newGrid.addAndGet(new Coordinates(newIndexes), share);
								} else {
									newIndexes[axis] = newIndexOnAxis + 1;
									newGrid.addAndGet(new Coordinates(newIndexes), share);
									newIndexes[axis] = newIndexOnAxis;//reset index
								}
								if (isLowerNeighborValueEqual[axis]) {
									newGrid.addAndGet(new Coordinates(newIndexes), share);
								} else {
									newIndexes[axis] = newIndexOnAxis - 1;
									newGrid.addAndGet(new Coordinates(newIndexes), share);
									newIndexes[axis] = newIndexOnAxis;//reset index
								}
							}
						} else {
							//If the share is zero, just add the value to the corresponding cell in the new array
							newGrid.addAndGet(new Coordinates(newIndexes), value);
						}
					} else {
						//If all neighbor values are equal the current cell won't change (it will get from them the same value it gives them)
						newGrid.addAndGet(new Coordinates(newIndexes), value);
					}
				} else {
					//If the abs value is smaller than the divisor just copy the value to the new grid
					Utils.addToArray(indexes, indexOffset);
					newGrid.addAndGet(new Coordinates(indexes), value);
				}					
			}
		}
	}

	@Override
	public long getStep() {
		return step;
	}

	@Override
	public String getName() {
		return "SpreadIntegerValue";
	}

	@Override
	public String getSubfolderPath() {
		return getName() + "/" + gridDimension + "D/" + initialValue + "/" + backgroundValue;
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
