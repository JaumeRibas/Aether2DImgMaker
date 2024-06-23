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
package cellularautomata.automata.sunflower;

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
import cellularautomata.model.IsotropicHypercubicModelA;
import cellularautomata.model.SerializableModelData;
import cellularautomata.model.SymmetricIntModel;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Sunflower-Cellular-Automaton-Definition">Sunflower</a> cellular automaton with a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class IntSunflower implements SymmetricIntModel, IsotropicHypercubicModelA {

	private long step;
	private final int initialValue;
	private final int backgroundValue;
	
	/** An array representing the grid */
	private AnisotropicIntArray grid;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;

	private Boolean changed = null;
	
	public IntSunflower(int gridDimension, int initialValue, int backgroundValue) {
		this.initialValue = initialValue;
		this.backgroundValue = backgroundValue;
		int side = 3;
		grid = new AnisotropicIntArray(gridDimension, side);
		grid.fill(backgroundValue);
		Coordinates originCoordinates = new Coordinates(new int[gridDimension]);
		grid.set(originCoordinates, initialValue);
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
	public IntSunflower(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		SerializableModelData data = (SerializableModelData) Utils.deserializeFromFile(backupPath);
		if (!SerializableModelData.Models.SUNFLOWER.equals(data.get(SerializableModelData.MODEL))) {
			throw new IllegalArgumentException("The backup file contains a different model.");
		}
		if (!SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_TYPE))
				|| !(SerializableModelData.InitialConfigurationImplementationTypes.ORIGIN_AND_BACKGROUND_VALUES_AS_LENGTH_2_INT_ARRAY.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE)) 
						|| SerializableModelData.InitialConfigurationImplementationTypes.INTEGER.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE)))
				|| !SerializableModelData.GridTypes.INFINITE_REGULAR.equals(data.get(SerializableModelData.GRID_TYPE))
				|| !SerializableModelData.GridImplementationTypes.ANYSOTROPIC_INT_ARRAY_CLASS_INSTANCE.equals(data.get(SerializableModelData.GRID_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.CoordinateBoundsImplementationTypes.BOUNDS_REACHED_BOOLEAN.equals(data.get(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE))
				|| !data.contains(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP)) {
			throw new IllegalArgumentException("The backup file's configuration is not compatible with this class.");
		}
		if (data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE).equals(SerializableModelData.InitialConfigurationImplementationTypes.INTEGER)) {
			initialValue = (int) data.get(SerializableModelData.INITIAL_CONFIGURATION);
			backgroundValue = 0;
		} else {
			int[] initialConfiguration = (int[]) data.get(SerializableModelData.INITIAL_CONFIGURATION);
			initialValue = initialConfiguration[0];
			backgroundValue = initialConfiguration[1];
		}
		grid = (AnisotropicIntArray) data.get(SerializableModelData.GRID);
		boundsReached = (boolean) data.get(SerializableModelData.COORDINATE_BOUNDS);
		step = (long) data.get(SerializableModelData.STEP);
		changed = (Boolean) data.get(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP);
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
	public Boolean nextStep() throws Exception {
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
		Arrays.fill(indexes, -1);
		int sideMinusOne = side - 1;
		int dimensionMinusOne = gridDimension - 1;
		int currentAxis = dimensionMinusOne;
		boolean changed = false;
		while (currentAxis > -1) {
			if (currentAxis == dimensionMinusOne) {
				boolean positionToppled = topplePosition(indexes, newGrid);
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
		this.changed = changed;
		//Return whether or not the state of the grid changed
		return changed;
	}

	@Override
	public Boolean isChanged() {
		return changed;
	}
	
	private boolean topplePosition(int[] indexes, AnisotropicIntArray newGrid) {
		int gridDimension = grid.getDimension();
		int gridSide = grid.getSide();
		int gridSideMinusOne = gridSide - 1;
		int shareCount = gridDimension * 2 + 1;
		boolean isCurrentPositionInsideGrid = Utils.areAllPositive(indexes) && Utils.isSortedDescending(indexes);
		int value = getFromPosition(new Coordinates(indexes));
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
						upperNeighborValues[axis] = getFromPosition(new Coordinates(indexes));
						if (indexOnAxis > -gridSideMinusOne) {
							indexes[axis] = indexOnAxis - 1;
							lowerNeighborValues[axis] = getFromPosition(new Coordinates(indexes));
						} else {
							lowerNeighborValues[axis] = backgroundValue; 
						}
					} else {
						upperNeighborValues[axis] = backgroundValue;
						indexes[axis] = indexOnAxis - 1;
						//if the grid side were one, this would be out of bounds.
						//but since it starts at 5 and only gets bigger it's fine
						lowerNeighborValues[axis] = getFromPosition(new Coordinates(indexes));
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
				//If the current cell is equal to its neighbors, the algorithm has no effect
				if (!areAllNeighborValuesEqual) {
					//Divide its value between the neighbors and itself (using integer division)
					int share = value/shareCount;
					if (share != 0) {
						//I assume that if any share is not zero the state changes (doesn't work for background value != 0 :( )
						changed = true;
						if (indexes[0] == gridSide - 2)
							boundsReached = true;
						//Add the share and the remainder to the corresponding cell in the new array
						if (isCurrentPositionInsideGrid) {
							newGrid.addAndGet(new Coordinates(indexes), value%shareCount + share);
						}
						//Add the share to the neighboring cells
						//If the neighbor's value is equal to the current value, add the share to the current cell instead
						for (int axis = 0; axis < gridDimension; axis++) {
							int indexOnAxis = indexes[axis];
							if (isUpperNeighborValueEqual[axis]) {
								if (isCurrentPositionInsideGrid) {
									newGrid.addAndGet(new Coordinates(indexes), share);
								}
							} else {
								indexes[axis] = indexOnAxis + 1;
								if (Utils.areAllPositive(indexes) && Utils.isSortedDescending(indexes)) {
									newGrid.addAndGet(new Coordinates(indexes), share);
								}
								indexes[axis] = indexOnAxis;//reset index
							}
							if (isLowerNeighborValueEqual[axis]) {
								if (isCurrentPositionInsideGrid) {
									newGrid.addAndGet(new Coordinates(indexes), share);
								}
							} else {
								indexes[axis] = indexOnAxis - 1;
								if (Utils.areAllPositive(indexes) && Utils.isSortedDescending(indexes)) {
									newGrid.addAndGet(new Coordinates(indexes), share);
								}
								indexes[axis] = indexOnAxis;//reset index
							}
						}
					} else {
						//If the share is zero, just add the value to the corresponding cell in the new array
						if (isCurrentPositionInsideGrid) {
							newGrid.addAndGet(new Coordinates(indexes), value);
						}
					}
				} else {
					//If all neighbor values are equal the current cell won't change (it will get from them the same value it gives them)
					if (isCurrentPositionInsideGrid) {
						newGrid.addAndGet(new Coordinates(indexes), value);
					}
				}
			} else {
				if (isCurrentPositionInsideGrid) {
					newGrid.addAndGet(new Coordinates(indexes), value);
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
		return "Sunflower";
	}

	@Override
	public String getSubfolderPath() {
		return getName() + "/" + grid.getDimension() + "D/" + initialValue + "/" + backgroundValue;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		SerializableModelData data = new SerializableModelData();
		int[] initialConfiguration = new int[] { initialValue, backgroundValue };
		data.put(SerializableModelData.MODEL, SerializableModelData.Models.SUNFLOWER);
		data.put(SerializableModelData.INITIAL_CONFIGURATION, initialConfiguration);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_TYPE, SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE, SerializableModelData.InitialConfigurationImplementationTypes.ORIGIN_AND_BACKGROUND_VALUES_AS_LENGTH_2_INT_ARRAY);
		data.put(SerializableModelData.GRID, grid);
		data.put(SerializableModelData.GRID_TYPE, SerializableModelData.GridTypes.INFINITE_REGULAR);
		data.put(SerializableModelData.GRID_DIMENSION, grid.getDimension());
		data.put(SerializableModelData.GRID_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_INT_ARRAY_CLASS_INSTANCE);
		data.put(SerializableModelData.COORDINATE_BOUNDS, boundsReached);
		data.put(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE, SerializableModelData.CoordinateBoundsImplementationTypes.BOUNDS_REACHED_BOOLEAN);
		data.put(SerializableModelData.STEP, step);
		data.put(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP, changed);
		Utils.serializeToFile(data, backupPath, backupName);
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
		return grid.getSide() - 1;
	}

	@Override
	public int getAsymmetricMaxCoordinate(int axis, PartialCoordinates coordinates) {
		if (axis == 0) {
			return grid.getSide() - 1;
		} else {
			return IsotropicHypercubicModelA.super.getAsymmetricMaxCoordinate(axis, coordinates);
		}
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
