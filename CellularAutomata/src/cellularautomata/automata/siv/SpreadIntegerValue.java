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
package cellularautomata.automata.siv;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import cellularautomata.Coordinates;
import cellularautomata.PartialCoordinates;
import cellularautomata.arrays.AnisotropicIntArray;
import cellularautomata.arrays.HyperrectangularArray;
import cellularautomata.Utils;
import cellularautomata.model.IsotropicHypercubicModel;
import cellularautomata.model.SymmetricIntModel;


public class SpreadIntegerValue implements SymmetricIntModel, IsotropicHypercubicModel {

	private long step;
	private int initialValue;
	private int backgroundValue;
	
	/** An array representing the grid */
	private AnisotropicIntArray grid;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	
	public SpreadIntegerValue(int gridDimension, int initialValue, int backgroundValue) {
		this.initialValue = initialValue;
		this.backgroundValue = backgroundValue;
		int[] indexes = new int[gridDimension];
		Coordinates immutableIndexes = new Coordinates(indexes);
		int side = 3;
		grid = new AnisotropicIntArray(gridDimension, side);
		grid.fill(backgroundValue);
		grid.set(immutableIndexes, initialValue);
		this.initialValue = initialValue;
		boundsReached = false;
		//Set the current step to zero
		step = 0;
	}
	
	@Override
	public int getGridDimension() {
		return grid.getDimension();
	}

	@Override
	public int getFromPosition(Coordinates coordinates) {
		int[] coordsArray = coordinates.getCopyAsArray();
		Utils.abs(coordsArray);
		Utils.sortDescending(coordsArray);
		if (coordsArray.length == 0 || coordsArray[0] < grid.getSide()) {
			return grid.get(new Coordinates(coordsArray));
		} else {
			return backgroundValue;
		}
	}

	@Override
	public boolean nextStep() throws Exception {
		int gridDimension = grid.getDimension();
		int side = grid.getSide();
		//Use new array to store the values of the next step
		AnisotropicIntArray newGrid = null;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new AnisotropicIntArray(gridDimension, side + 1);
			if (backgroundValue != 0) {
				newGrid.fillEdges(1, backgroundValue);
			}
		} else {
			newGrid = new AnisotropicIntArray(gridDimension, side);
		}
		int[] indexes = new int[gridDimension];
		Coordinates immutableIndexes = new Coordinates(indexes);
		Arrays.fill(indexes, -1);
		int sideMinusOne = side - 1;
		int dimensionMinusOne = gridDimension - 1;
		int currentAxis = dimensionMinusOne;
		boolean changed = false;
		while (currentAxis > -1) {
			if (currentAxis == dimensionMinusOne) {
				boolean positionToppled = topplePosition(indexes, immutableIndexes, newGrid);
				changed = changed || positionToppled;
			}
			int currentCoordinate = indexes[currentAxis];
			int max;
			if (currentAxis == 0) {
				max = sideMinusOne;
			} else {
				max = indexes[currentAxis - 1] + 1;
			}
			if (currentCoordinate < max) {
				currentCoordinate++;
				indexes[currentAxis] = currentCoordinate;
				currentAxis = dimensionMinusOne;
			} else {
				indexes[currentAxis] = -1;
				currentAxis--;
			}
		}
		//Replace the old array with the new one
		grid = newGrid;
		//Increase the current step by one
		step++;
		//Return whether or not the state of the grid changed
		return changed;
	}
	
	private boolean topplePosition(int[] indexes, Coordinates immutableIndexes, AnisotropicIntArray newGrid) {
		int gridDimension = grid.getDimension();
		int gridSide = grid.getSide();
		int gridSideMinusOne = gridSide - 1;
		int shareCount = gridDimension * 2 + 1;
		boolean isCurrentPositionInsideGrid = Utils.areAllPositive(indexes) && Utils.isSortedDescending(indexes);
		int value = getFromPosition(immutableIndexes);
		boolean changed = false;
		if (value != 0) {
			if (Math.abs(value) >= shareCount) {
				int[] upperNeighborValues = new int[gridDimension];
				int[] lowerNeighborValues = new int[gridDimension];
				boolean[] isUpperNeighborValueEqual = new boolean[gridDimension];
				boolean[] isLowerNeighborValueEqual = new boolean[gridDimension];
				boolean areAllNeighborValuesEqual = true;
				for (int axis = 0; axis < gridDimension; axis++) {
					int indexOnAxis = indexes[axis];
					if (indexOnAxis < gridSideMinusOne) {
						indexes[axis] = indexOnAxis + 1;
						upperNeighborValues[axis] = getFromPosition(immutableIndexes);
						if (indexOnAxis > -gridSideMinusOne) {
							indexes[axis] = indexOnAxis - 1;
							lowerNeighborValues[axis] = getFromPosition(immutableIndexes);
						} else {
							lowerNeighborValues[axis] = backgroundValue; 
						}
					} else {
						upperNeighborValues[axis] = backgroundValue;
						indexes[axis] = indexOnAxis - 1;
						//if the grid side where one, this would be out of bounds.
						//but since it starts at 5 and only gets bigger it's fine
						lowerNeighborValues[axis] = getFromPosition(immutableIndexes);
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
				//if the current position is equal to its neighbors the algorithm has no effect
				if (!areAllNeighborValuesEqual) {
					//Divide its value between the neighbors and center (using integer division)
					int share = value/shareCount;
					if (share != 0) {
						//I assume that if any share is not zero the state changes (doesn't work for background value != 0 :( )
						changed = true;
						if (indexes[0] == gridSide - 2)
							boundsReached = true;
						//Add the share and the remainder to the corresponding position in the new array
						if (isCurrentPositionInsideGrid) {
							newGrid.addAndGet(immutableIndexes, value%shareCount + share);
						}
						//Add the share to the neighboring positions
						//if the neighbor's value is equal to the current value, add the share to the current position instead
						for (int axis = 0; axis < gridDimension; axis++) {
							int indexOnAxis = indexes[axis];
							if (isUpperNeighborValueEqual[axis]) {
								if (isCurrentPositionInsideGrid) {
									newGrid.addAndGet(immutableIndexes, share);
								}
							} else {
								indexes[axis] = indexOnAxis + 1;
								if (Utils.areAllPositive(indexes) && Utils.isSortedDescending(indexes)) {
									newGrid.addAndGet(immutableIndexes, share);
								}
								indexes[axis] = indexOnAxis;//reset index
							}
							if (isLowerNeighborValueEqual[axis]) {
								if (isCurrentPositionInsideGrid) {
									newGrid.addAndGet(immutableIndexes, share);
								}
							} else {
								indexes[axis] = indexOnAxis - 1;
								if (Utils.areAllPositive(indexes) && Utils.isSortedDescending(indexes)) {
									newGrid.addAndGet(immutableIndexes, share);
								}
								indexes[axis] = indexOnAxis;//reset index
							}
						}
					} else {
						//if the share is zero, just add the value to the corresponding position in the new array
						if (isCurrentPositionInsideGrid) {
							newGrid.addAndGet(immutableIndexes, value);
						}
					}
				} else {
					//if all neighbor values are equal the current value won't change (it will get from them the same value it gives them)
					if (isCurrentPositionInsideGrid) {
						newGrid.addAndGet(immutableIndexes, value);
					}
				}
			} else {
				if (isCurrentPositionInsideGrid) {
					newGrid.addAndGet(immutableIndexes, value);
				}
			}					
		}
		return changed;
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
		return getName() + "/" + grid.getDimension() + "/" + initialValue + "/" + backgroundValue;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void forEach(IntConsumer consumer) {
		int gridDimension = grid.getDimension();
		int sideMinusOne = grid.getSide() - 1;
		int[] upperBounds = new int[gridDimension];
		Arrays.fill(upperBounds, sideMinusOne);
		int[] lowerBounds = new int[gridDimension];
		Arrays.fill(lowerBounds, -sideMinusOne);
		HyperrectangularArray.forEachIndexWithinBounds(upperBounds, lowerBounds, new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates coordinates) {
				consumer.accept(getFromPosition(coordinates));
			}
		});
	}
	
	@Override
	public int getAsymmetricMaxCoordinate(int axis) {
		//side >= c1 >= c2... >= cN >= 0
		return grid.getSide() - 1;
	}

	@Override
	public int getAsymmetricMinCoordinate(int axis) {
		return 0;
	}

	@Override
	public int getAsymmetricMaxCoordinate(int axis, PartialCoordinates coordinates) {
		//side >= c1 >= c2... >= cN >= 0
		if (axis > 0) {
			for (int i = axis - 1; i >= 0; i--) {
				Integer coord = coordinates.get(i);
				if (coord != null) {
					return coord;
				}
			}
		}
		return grid.getSide();
	}

	@Override
	public int getAsymmetricMinCoordinate(int axis, PartialCoordinates coordinates) {
		//side >= c1 >= c2... >= cN >= 0
		int coordCount = coordinates.getCount();
		if (axis < coordCount - 1) {
			for (int i = axis + 1; i < coordCount; i++) {
				Integer coord = coordinates.get(i);
				if (coord != null) {
					return coord;
				}
			}
		}
		return 0;
	}

	@Override
	public int getFromAsymmetricPosition(Coordinates coordinates) {
		return grid.get(coordinates);
	}

	@Override
	public void forEachInAsymmetricSection(IntConsumer consumer) {
		grid.forEach(consumer);
	}
	
}
