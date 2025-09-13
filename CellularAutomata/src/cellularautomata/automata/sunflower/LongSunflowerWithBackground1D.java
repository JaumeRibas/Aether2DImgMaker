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
public class LongSunflowerWithBackground1D extends IsotropicLongArrayModelAsymmetricSection1D {
	
	private final long initialValue;
	private final long backgroundValue;
	private long step;

	/** Whether or not the values reached the bounds of the array */
	private boolean boundReached;

	private Boolean changed = null;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 * @param backgroundValue the value padding all the grid but the origin at step 0
	 */
	public LongSunflowerWithBackground1D(long initialValue, long backgroundValue) {
		this.initialValue = initialValue;
		this.backgroundValue = backgroundValue;
		grid = new long[3];
		grid[0] = this.initialValue;
		grid[1] = backgroundValue;
		grid[2] = backgroundValue;
		boundReached = false;
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
	public LongSunflowerWithBackground1D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		SerializableModelData data = (SerializableModelData) Utils.deserializeFromFile(backupPath);
		data = SerializableModelData.updateDataFormat(data);
		if (!SerializableModelData.Models.SUNFLOWER.equals(data.get(SerializableModelData.MODEL))) {
			throw new IllegalArgumentException("The backup file contains a different model.");
		}
		if (!SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_TYPE))
				|| !(SerializableModelData.InitialConfigurationImplementationTypes.ORIGIN_AND_BACKGROUND_VALUES_AS_LENGTH_2_LONG_PRIMITIVE_ARRAY.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE)) 
						|| SerializableModelData.InitialConfigurationImplementationTypes.LONG.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE)))
				|| !SerializableModelData.GridTypes.INFINITE_REGULAR.equals(data.get(SerializableModelData.GRID_TYPE))
				|| !Integer.valueOf(1).equals(data.get(SerializableModelData.GRID_DIMENSION))
				|| !SerializableModelData.GridImplementationTypes.ANYSOTROPIC_LONG_PRIMITIVE_ARRAY_1.equals(data.get(SerializableModelData.GRID_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.CoordinateBoundsImplementationTypes.BOUNDS_REACHED_BOOLEAN.equals(data.get(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE))
				|| !data.contains(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP)) {
			throw new IllegalArgumentException("The backup file's configuration is not compatible with this class.");
		}
		if (data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE).equals(SerializableModelData.InitialConfigurationImplementationTypes.LONG)) {
			initialValue = (long) data.get(SerializableModelData.INITIAL_CONFIGURATION);
			backgroundValue = 0;
		} else {
			long[] initialConfiguration = (long[]) data.get(SerializableModelData.INITIAL_CONFIGURATION);
			initialValue = initialConfiguration[0];
			backgroundValue = initialConfiguration[1];
		}
		grid = (long[]) data.get(SerializableModelData.GRID);
		boundReached = (boolean) data.get(SerializableModelData.COORDINATE_BOUNDS);
		step = (long) data.get(SerializableModelData.STEP);
		changed = (Boolean) data.get(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP);
	}
	
	@Override
	public Boolean nextStep() {
		long[] newGrid = null;
		if (boundReached) {
			boundReached = false;
			newGrid = new long[grid.length + 1];
			newGrid[grid.length] = backgroundValue;
		} else {
			newGrid = new long[grid.length];
		}
		int maxXMinusOne = newGrid.length - 2;
		boolean changed = false;
		for (int x = 0; x < grid.length; x++) {
			long value = grid[x];
			if (value != 0) {
				long left = getFromPosition(x - 1);
				long right = getFromPosition(x + 1);
				boolean isRightEqual = value == right, isLeftEqual = value == left;
				//If the current cell is equal to its neighbors, the algorithm has no effect
				if (!(isRightEqual && isLeftEqual)) {
					//Divide its value by 3 (using integer division)
					long share = value/3;
					if (share != 0) {
						//If any share is not zero, the state changes
						changed = true;
						//Add the share to the neighboring cells
						//If the neighbor's value is equal to the current value, add the share to the current cell instead
						//x+
						if (isRightEqual)
							newGrid[x] += share;
						else
							newGrid[x+1] += share;
						//x-
						if (isLeftEqual)
							newGrid[x] += share;
						else if (x > 0) {
							long valueToAdd = share;
							if (x == 1) {
								valueToAdd += share;							
							}
							newGrid[x-1] += valueToAdd;
						}
						
						if (x >= maxXMinusOne) {
							boundReached = true;
						}								
					}
					newGrid[x] += value - 2*share;
				} else {
					newGrid[x] += value;
				}
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
	public long getFromPosition(int x) {	
		if (x < 0) x = -x;
		if (x < grid.length) {
			return grid[x];
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
	public long getInitialValue() {
		return initialValue;
	}
	
	public long getBackgroundValue() {
		return backgroundValue;
	}

	@Override
	public String getName() {
		return "Sunflower";
	}
	
	@Override
	public String getWholeGridSubfolderPath() {
		return getName() + "/1D/" + initialValue + "/" + backgroundValue;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		SerializableModelData data = new SerializableModelData();
		long[] initialConfiguration = new long[] { initialValue, backgroundValue };
		data.put(SerializableModelData.MODEL, SerializableModelData.Models.SUNFLOWER);
		data.put(SerializableModelData.INITIAL_CONFIGURATION, initialConfiguration);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_TYPE, SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE, SerializableModelData.InitialConfigurationImplementationTypes.ORIGIN_AND_BACKGROUND_VALUES_AS_LENGTH_2_LONG_PRIMITIVE_ARRAY);
		data.put(SerializableModelData.GRID, grid);
		data.put(SerializableModelData.GRID_TYPE, SerializableModelData.GridTypes.INFINITE_REGULAR);
		data.put(SerializableModelData.GRID_DIMENSION, 1);
		data.put(SerializableModelData.GRID_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_LONG_PRIMITIVE_ARRAY_1);
		data.put(SerializableModelData.COORDINATE_BOUNDS, boundReached);
		data.put(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE, SerializableModelData.CoordinateBoundsImplementationTypes.BOUNDS_REACHED_BOOLEAN);
		data.put(SerializableModelData.STEP, step);
		data.put(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP, changed);
		Utils.serializeToFile(data, backupPath, backupName);
	}
}
