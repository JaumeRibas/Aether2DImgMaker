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
package cellularautomata.automata.sunflower;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import cellularautomata.Utils;
import cellularautomata.model.SerializableModelData;
import cellularautomata.model2d.IsotropicSquareIntArrayModelAsymmetricSection;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Sunflower-Cellular-Automaton-Definition">Sunflower</a> cellular automaton in 2D with a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class IntSunflowerWithBackground2D extends IsotropicSquareIntArrayModelAsymmetricSection {
	
	private final int initialValue;
	private final int backgroundValue;
	private long step;

	/** Whether or not the values reached the bounds of the array */
	private boolean xBoundReached;

	private Boolean changed = null;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 * @param backgroundValue the value padding all the grid but the origin at step 0
	 */
	public IntSunflowerWithBackground2D(int initialValue, int backgroundValue) {
		this.initialValue = initialValue;
		this.backgroundValue = backgroundValue;
		grid = Utils.buildAnisotropic2DIntArray(3);
		Utils.fill(grid, backgroundValue);
		grid[0][0] = this.initialValue;
		xBoundReached = false;
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
	public IntSunflowerWithBackground2D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		SerializableModelData data = (SerializableModelData) Utils.deserializeFromFile(backupPath);
		data = SerializableModelData.updateDataFormat(data);
		if (!SerializableModelData.Models.SUNFLOWER.equals(data.get(SerializableModelData.MODEL))) {
			throw new IllegalArgumentException("The backup file contains a different model.");
		}
		if (!SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_TYPE))
				|| !(SerializableModelData.InitialConfigurationImplementationTypes.ORIGIN_AND_BACKGROUND_VALUES_AS_LENGTH_2_INT_ARRAY.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE)) 
						|| SerializableModelData.InitialConfigurationImplementationTypes.INTEGER.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE)))
				|| !SerializableModelData.GridTypes.INFINITE_REGULAR.equals(data.get(SerializableModelData.GRID_TYPE))
				|| !Integer.valueOf(2).equals(data.get(SerializableModelData.GRID_DIMENSION))
				|| !SerializableModelData.GridImplementationTypes.ANYSOTROPIC_INT_ARRAY_1.equals(data.get(SerializableModelData.GRID_IMPLEMENTATION_TYPE))
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
		grid = (int[][]) data.get(SerializableModelData.GRID);
		xBoundReached = (boolean) data.get(SerializableModelData.COORDINATE_BOUNDS);
		step = (long) data.get(SerializableModelData.STEP);
		changed = (Boolean) data.get(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP);
	}
	
	@Override
	public Boolean nextStep() {
		int[][] newGrid = null;
		if (xBoundReached) {
			xBoundReached = false;
			newGrid = new int[grid.length + 1][];
		} else {
			newGrid = new int[grid.length][];
		}
		int maxXMinusOne = newGrid.length - 2;
		boolean changed = false;
		newGrid[0] = new int[1];
		boolean isFirst = true;
		for (int x = 0, nextX = 1; x < grid.length; x = nextX, nextX++, isFirst = false) {
			if (nextX < newGrid.length) {
				newGrid[nextX] = new int[nextX + 1];
				if (nextX >= grid.length) {
					Arrays.fill(newGrid[nextX], backgroundValue);
				}
			}
			for (int y = 0; y <= x; y++) {
				int value = grid[x][y];
				if (value != 0) {
					int up = getFromPosition(x, y + 1);
					int down = getFromPosition(x, y - 1); 
					int left = getFromPosition(x - 1, y);
					int right = getFromPosition(x + 1, y);
					boolean isUpEqual = value == up, isDownEqual = value == down, 
							isRightEqual = value == right, isLeftEqual = value == left;
					//If the current cell is equal to its neighbors, the algorithm has no effect
					if (!(isUpEqual && isDownEqual && isRightEqual && isLeftEqual)) {
						//Divide its value by 5 (using integer division)
						int share = value/5;
						if (share != 0) {
							//If any share is not zero, the state changes
							changed = true;
							//Add the share to the neighboring cells
							//If the neighbor's value is equal to the current value, add the share to the current cell instead
							//x+
							if (isRightEqual)
								newGrid[x][y] += share;
							else
								newGrid[x+1][y] += share;
							//x-
							if (isLeftEqual)
								newGrid[x][y] += share;
							else if (x > y) {
								int valueToAdd = share;
								if (x == y + 1) {
									valueToAdd += share;
									if (x == 1) {
										valueToAdd += 2*share;							
									}
								}
								newGrid[x-1][y] += valueToAdd;
							}
							//y+
							if (isUpEqual)
								newGrid[x][y] += share;
							else if (y < x) {
								int valueToAdd = share;
								if (y == x - 1) {
									valueToAdd += share;
								}
								int yy = y+1;
								newGrid[x][yy] += valueToAdd;
							}
							//y-
							if (isDownEqual)
								newGrid[x][y] += share;
							else if (y > 0) {
								int valueToAdd = share;
								if (y == 1) {
									valueToAdd += share;
								}
								newGrid[x][y-1] += valueToAdd;
							}
							
							if (x >= maxXMinusOne) {
								xBoundReached = true;
							}								
						}
						newGrid[x][y] += value - 4*share;
					} else {
						newGrid[x][y] += value;
					}
				}
			}
			if (!isFirst) {
				grid[x-1] = null;
			}
		}
		grid = newGrid;
		step++;
		this.changed = changed;
		return changed;
	}

	@Override
	public Boolean isChanged() {
		return changed;
	}
	
	@Override
	public int getFromPosition(int x, int y) {	
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		if (y > x) {
			int swp = y;
			y = x;
			x = swp;
		}
		if (x < grid.length 
				&& y < grid[x].length) {
			return grid[x][y];
		} else {
			return backgroundValue; //this implementation relies on being able to get the value of an out of bounds position
		}
	}

	@Override
	public int getSize() {
		return grid.length - 1;
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
	public int getInitialValue() {
		return initialValue;
	}

	public int getBackgroundValue() {
		return backgroundValue;
	}

	@Override
	public String getName() {
		return "Sunflower";
	}
	
	@Override
	public String getWholeGridSubfolderPath() {
		return getName() + "/2D/" + initialValue + "/" + backgroundValue;
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
		data.put(SerializableModelData.GRID_DIMENSION, 2);
		data.put(SerializableModelData.GRID_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_INT_ARRAY_1);
		data.put(SerializableModelData.COORDINATE_BOUNDS, xBoundReached);
		data.put(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE, SerializableModelData.CoordinateBoundsImplementationTypes.BOUNDS_REACHED_BOOLEAN);
		data.put(SerializableModelData.STEP, step);
		data.put(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP, changed);
		Utils.serializeToFile(data, backupPath, backupName);
	}
}
