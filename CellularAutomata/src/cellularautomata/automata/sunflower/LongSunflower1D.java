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
import cellularautomata.model1d.IsotropicLongArrayModelAsymmetricSection1D;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Sunflower-Cellular-Automaton-Definition">Sunflower</a> cellular automaton in 1D with a single source initial configuration.
 * 
 * @author Jaume
 *
 */
public class LongSunflower1D extends IsotropicLongArrayModelAsymmetricSection1D {
	
	private final long initialValue;
	private long step;
	private int maxX;
	private Boolean changed = null;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public LongSunflower1D(long initialValue) {
		this.initialValue = initialValue;
		grid = new long[5];
		grid[0] = initialValue;
		maxX = 3;
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
	public LongSunflower1D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		SerializableModelData data = (SerializableModelData) Utils.deserializeFromFile(backupPath);
		data = SerializableModelData.updateDataFormat(data);
		if (!SerializableModelData.Models.SUNFLOWER.equals(data.get(SerializableModelData.MODEL))) {
			throw new IllegalArgumentException("The backup file contains a different model.");
		}
		String incompatibleBackupConfigErrorMessage = "The backup file's configuration is not compatible with this class.";
		if (!SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_TYPE))
				|| !(SerializableModelData.InitialConfigurationImplementationTypes.ORIGIN_AND_BACKGROUND_VALUES_AS_LENGTH_2_LONG_PRIMITIVE_ARRAY.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE)) 
						|| SerializableModelData.InitialConfigurationImplementationTypes.LONG.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE)))
				|| !SerializableModelData.GridTypes.INFINITE_REGULAR.equals(data.get(SerializableModelData.GRID_TYPE))
				|| !Integer.valueOf(1).equals(data.get(SerializableModelData.GRID_DIMENSION))
				|| !SerializableModelData.GridImplementationTypes.ANYSOTROPIC_LONG_PRIMITIVE_ARRAY_1.equals(data.get(SerializableModelData.GRID_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER.equals(data.get(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE))
				|| !data.contains(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP)) {
			throw new IllegalArgumentException(incompatibleBackupConfigErrorMessage);
		}
		if (data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE).equals(SerializableModelData.InitialConfigurationImplementationTypes.LONG)) {
			initialValue = (long) data.get(SerializableModelData.INITIAL_CONFIGURATION);
		} else {
			long[] initialConfiguration = (long[]) data.get(SerializableModelData.INITIAL_CONFIGURATION);
			if (initialConfiguration[1] != 0) {
				throw new IllegalArgumentException(incompatibleBackupConfigErrorMessage);
			}
			initialValue = initialConfiguration[0];
		}
		grid = (long[]) data.get(SerializableModelData.GRID);
		maxX = (int) data.get(SerializableModelData.COORDINATE_BOUNDS);
		step = (long) data.get(SerializableModelData.STEP);
		changed = (Boolean) data.get(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP);
	}
	
	@Override
	public Boolean nextStep() {
		long[] newGrid = new long[maxX + 2];
		boolean changed = false;
		//x = 0
		long currentValue = grid[0];
		long share = currentValue / 3;
		if (share != 0) {
			changed = true;
			newGrid[0] += currentValue - 2*share;
			newGrid[1] += share;
		} else {
			newGrid[0] += currentValue;
		}
		//x = 1
		currentValue = grid[1];
		share = currentValue / 3;
		if (share != 0) {
			changed = true;
			newGrid[0] += 2*share;
			newGrid[1] += currentValue - 2*share;
			newGrid[2] += share;
		} else {
			newGrid[1] += currentValue;
		}
		//2 <= x < edge - 2
		int edge = grid.length - 1;
		int edgeMinusTwo = edge - 2;
		if (toppleRangeBeyondX1(newGrid, 2, edgeMinusTwo)) {
			changed = true;
		}
		//edge - 2 <= x < edge
		if (toppleRangeBeyondX1(newGrid, edgeMinusTwo, edge)) {
			changed = true;
			maxX++;
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
	
	private boolean toppleRangeBeyondX1(long[] newGrid, int minX, int maxX) {
		boolean anyToppled = false;
		int x = minX, xMinusOne = x - 1, xPlusOne = x + 1;
		for (; x != maxX; xMinusOne = x, x = xPlusOne, xPlusOne++) {
			long currentValue = grid[x];
			long share = currentValue / 3;
			if (share != 0) {
				anyToppled = true;
				newGrid[xMinusOne] += share;
				newGrid[x] += currentValue - 2*share;
				newGrid[xPlusOne] += share;
			} else {
				newGrid[x] += currentValue;
			}
		}
		return anyToppled;
	}

	@Override
	public int getSize() {
		return maxX;
	}
	
	/**
	 * Returns the initial value
	 * 
	 * @return the value at the origin at step 0
	 */
	public long getInitialValue() {
		return initialValue;
	}

	public long getBackgroundValue() {
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
	public String getWholeGridSubfolderPath() {
		return getName() + "/1D/" + initialValue + "/0";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		SerializableModelData data = new SerializableModelData();
		data.put(SerializableModelData.MODEL, SerializableModelData.Models.SUNFLOWER);
		data.put(SerializableModelData.INITIAL_CONFIGURATION, initialValue);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_TYPE, SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE, SerializableModelData.InitialConfigurationImplementationTypes.LONG);
		data.put(SerializableModelData.GRID, grid);
		data.put(SerializableModelData.GRID_TYPE, SerializableModelData.GridTypes.INFINITE_REGULAR);
		data.put(SerializableModelData.GRID_DIMENSION, 1);
		data.put(SerializableModelData.GRID_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_LONG_PRIMITIVE_ARRAY_1);
		data.put(SerializableModelData.COORDINATE_BOUNDS, maxX);
		data.put(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE, SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER);
		data.put(SerializableModelData.STEP, step);
		data.put(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP, changed);
		Utils.serializeToFile(data, backupPath, backupName);
	}
}
