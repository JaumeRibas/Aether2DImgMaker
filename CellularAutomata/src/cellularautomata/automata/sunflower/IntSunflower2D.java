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
import cellularautomata.Utils;
import cellularautomata.model.SerializableModelData;
import cellularautomata.model2d.IsotropicSquareIntArrayModelA;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Sunflower-Cellular-Automaton-Definition">Sunflower</a> cellular automaton in 2D with a single source initial configuration.
 * 
 * @author Jaume
 *
 */
public class IntSunflower2D extends IsotropicSquareIntArrayModelA {
	
	private final int initialValue;
	private long step;
	private int maxX;
	private Boolean changed = null;

	/**
	 * Creates an instance with the given initial value.
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public IntSunflower2D(int initialValue) {
		this.initialValue = initialValue;
		grid = Utils.buildAnisotropic2DIntArray(6);
		grid[0][0] = initialValue;
		maxX = 4;
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
	public IntSunflower2D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		SerializableModelData data = (SerializableModelData) Utils.deserializeFromFile(backupPath);
		data = SerializableModelData.updateDataFormat(data);
		if (!SerializableModelData.Models.SUNFLOWER.equals(data.get(SerializableModelData.MODEL))) {
			throw new IllegalArgumentException("The backup file contains a different model.");
		}
		String incompatibleBackupConfigErrorMessage = "The backup file's configuration is not compatible with this class.";
		if (!SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_TYPE))
				|| !(SerializableModelData.InitialConfigurationImplementationTypes.ORIGIN_AND_BACKGROUND_VALUES_AS_LENGTH_2_INT_ARRAY.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE)) 
						|| SerializableModelData.InitialConfigurationImplementationTypes.INTEGER.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE)))
				|| !SerializableModelData.GridTypes.INFINITE_REGULAR.equals(data.get(SerializableModelData.GRID_TYPE))
				|| !Integer.valueOf(2).equals(data.get(SerializableModelData.GRID_DIMENSION))
				|| !SerializableModelData.GridImplementationTypes.ANYSOTROPIC_INT_ARRAY_1.equals(data.get(SerializableModelData.GRID_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER.equals(data.get(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE))
				|| !data.contains(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP)) {
			throw new IllegalArgumentException(incompatibleBackupConfigErrorMessage);
		}
		if (data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE).equals(SerializableModelData.InitialConfigurationImplementationTypes.INTEGER)) {
			initialValue = (int) data.get(SerializableModelData.INITIAL_CONFIGURATION);
		} else {
			int[] initialConfiguration = (int[]) data.get(SerializableModelData.INITIAL_CONFIGURATION);
			if (initialConfiguration[1] != 0) {
				throw new IllegalArgumentException(incompatibleBackupConfigErrorMessage);
			}
			initialValue = initialConfiguration[0];
		}
		grid = (int[][]) data.get(SerializableModelData.GRID);
		maxX = (int) data.get(SerializableModelData.COORDINATE_BOUNDS);
		step = (long) data.get(SerializableModelData.STEP);
		changed = (Boolean) data.get(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP);
	}
	
	@Override
	public Boolean nextStep() {
		int[][] newGrid = new int[maxX + 2][];
		boolean changed = false;
		int currentValue;
		int[] currentXSlice = grid[0];
		int[] newSmallerXSlice = null, newCurrentXSlice = new int[1], newGreaterXSlice = new int[2];// build new grid progressively to save memory 
		newGrid[0] = newCurrentXSlice;
		newGrid[1] = newGreaterXSlice;
		// x = 0, y = 0
		currentValue = currentXSlice[0];
		int share = currentValue/5;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[0] += currentValue - 4*share;
			newGreaterXSlice[0] += share;
		} else {
			newCurrentXSlice[0] += currentValue;
		}
		// x slice transition
		grid[0] = null;// free old grid progressively to save memory
		currentXSlice = grid[1];
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = new int[3];
		newGrid[2] = newGreaterXSlice;		
		// x = 1, y = 0
		currentValue = currentXSlice[0];
		share = currentValue/5;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[0] += currentValue - 4*share;
			newGreaterXSlice[0] += share;
			newSmallerXSlice[0] += 4*share;
			newCurrentXSlice[1] += 2*share;
		} else {
			newCurrentXSlice[0] += currentValue;
		}
		// x = 1, y = 1
		currentValue = currentXSlice[1];
		share = currentValue/5;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[1] += currentValue - 4*share;
			newGreaterXSlice[1] += share;
			newCurrentXSlice[0] += 2*share;
		} else {
			newCurrentXSlice[1] += currentValue;
		}
		// x slice transition
		grid[1] = null;
		currentXSlice = grid[2];
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = new int[4];
		newGrid[3] = newGreaterXSlice;		
		// x = 2, y = 0
		currentValue = currentXSlice[0];
		share = currentValue/5;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[0] += currentValue - 4*share;
			newGreaterXSlice[0] += share;
			newSmallerXSlice[0] += share;
			newCurrentXSlice[1] += share;
		} else {
			newCurrentXSlice[0] += currentValue;
		}
		// x = 2, y = 1
		currentValue = currentXSlice[1];
		share = currentValue/5;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[1] += currentValue - 4*share;
			newGreaterXSlice[1] += share;
			newSmallerXSlice[1] += 2*share;
			newCurrentXSlice[2] += 2*share;
			newCurrentXSlice[0] += 2*share;
		} else {
			newCurrentXSlice[1] += currentValue;
		}
		// x = 2, y = 2
		currentValue = currentXSlice[2];
		share = currentValue/5;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[2] += currentValue - 4*share;
			newGreaterXSlice[2] += share;
			newCurrentXSlice[1] += share;
		} else {
			newCurrentXSlice[2] += currentValue;
		}
		// 3 <= x < edge - 2
		int edge = grid.length - 1;
		int edgeMinusTwo = edge - 2;
		if (toppleRangeBeyondX2(newGrid, 3, edgeMinusTwo)) {
			changed = true;
		}
		//edge - 2 <= x < edge
		if (toppleRangeBeyondX2(newGrid, edgeMinusTwo, edge)) {
			changed = true;
			maxX++;
		}
		if (newGrid.length > grid.length) {
			newGrid[grid.length] = new int[newGrid.length];
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
	
	private boolean toppleRangeBeyondX2(int[][] newGrid, int minX, int maxX) {
		boolean anyToppled = false;
		int x = minX, xMinusOne = x - 1, xPlusOne = x + 1, xPlusTwo = xPlusOne + 1;
		int[] newSmallerXSlice = null, newCurrentXSlice = newGrid[xMinusOne], newGreaterXSlice = newGrid[x];
		for (; x != maxX; xMinusOne = x, x = xPlusOne, xPlusOne = xPlusTwo, xPlusTwo++) {
			//x slice transition
			grid[xMinusOne] = null;
			int[] currentXSlice = grid[x];
			newSmallerXSlice = newCurrentXSlice;
			newCurrentXSlice = newGreaterXSlice;
			newGreaterXSlice = new int[xPlusTwo];
			newGrid[xPlusOne] = newGreaterXSlice;
			// y = 0;
			int currentValue = currentXSlice[0];
			int share = currentValue/5;
			if (share != 0) {
				anyToppled = true;
				newCurrentXSlice[0] += currentValue - 4*share;
				newGreaterXSlice[0] += share;
				newSmallerXSlice[0] += share;
				newCurrentXSlice[1] += share;
			} else {
				newCurrentXSlice[0] += currentValue;
			}
			// y = 1
			currentValue = currentXSlice[1];
			share = currentValue/5;
			if (share != 0) {
				anyToppled = true;
				newCurrentXSlice[1] += currentValue - 4*share;
				newGreaterXSlice[1] += share;
				newSmallerXSlice[1] += share;
				newCurrentXSlice[2] += share;
				newCurrentXSlice[0] += 2*share;
			} else {
				newCurrentXSlice[1] += currentValue;
			}
			// 2 >= y < x - 1
			int y = 2, yMinusOne = 1, yPlusOne = 3;
			for (; y != xMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
				currentValue = currentXSlice[y];
				share = currentValue/5;
				if (share != 0) {
					anyToppled = true;
					newCurrentXSlice[y] += currentValue - 4*share;
					newGreaterXSlice[y] += share;
					newSmallerXSlice[y] += share;
					newCurrentXSlice[yPlusOne] += share;
					newCurrentXSlice[yMinusOne] += share;
				} else {
					newCurrentXSlice[y] += currentValue;
				}
			}
			// y = x - 1
			currentValue = currentXSlice[y];
			share = currentValue/5;
			if (share != 0) {
				anyToppled = true;
				newCurrentXSlice[y] += currentValue - 4*share;
				newGreaterXSlice[y] += share;
				newSmallerXSlice[y] += 2*share;
				newCurrentXSlice[yPlusOne] += 2*share;
				newCurrentXSlice[yMinusOne] += share;
			} else {
				newCurrentXSlice[y] += currentValue;
			}
			// y = x
			yMinusOne = y;
			y = x;
			currentValue = currentXSlice[y];
			share = currentValue/5;
			if (share != 0) {
				anyToppled = true;
				newCurrentXSlice[y] += currentValue - 4*share;
				newGreaterXSlice[y] += share;
				newCurrentXSlice[yMinusOne] += share;
			} else {
				newCurrentXSlice[y] += currentValue;
			}			
		}
		return anyToppled;
	}

	@Override
	public int getAsymmetricMaxX() {
		return maxX;
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
		return 0;
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
		return getName() + "/2D/" + initialValue + "/0";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		SerializableModelData data = new SerializableModelData();
		data.put(SerializableModelData.MODEL, SerializableModelData.Models.SUNFLOWER);
		data.put(SerializableModelData.INITIAL_CONFIGURATION, initialValue);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_TYPE, SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE, SerializableModelData.InitialConfigurationImplementationTypes.INTEGER);
		data.put(SerializableModelData.GRID, grid);
		data.put(SerializableModelData.GRID_TYPE, SerializableModelData.GridTypes.INFINITE_REGULAR);
		data.put(SerializableModelData.GRID_DIMENSION, 2);
		data.put(SerializableModelData.GRID_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_INT_ARRAY_1);
		data.put(SerializableModelData.COORDINATE_BOUNDS, maxX);
		data.put(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE, SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER);
		data.put(SerializableModelData.STEP, step);
		data.put(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP, changed);
		Utils.serializeToFile(data, backupPath, backupName);
	}
}
