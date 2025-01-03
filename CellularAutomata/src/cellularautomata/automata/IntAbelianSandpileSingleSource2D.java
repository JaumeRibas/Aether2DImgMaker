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
package cellularautomata.automata;

import java.io.FileNotFoundException;
import java.io.IOException;
import cellularautomata.Utils;
import cellularautomata.model.SerializableModelData;
import cellularautomata.model2d.IsotropicSquareIntArrayModelA;

/**
 * Implementation of the <a href="https://en.wikipedia.org/wiki/Abelian_sandpile_model">Abelian sandpile</a> cellular automaton in 2D with synchronous toppling and a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class IntAbelianSandpileSingleSource2D extends IsotropicSquareIntArrayModelA {
	
	private final int initialValue;
	private long step;
	private Boolean changed = null;

	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public IntAbelianSandpileSingleSource2D(int initialValue) {
		if (initialValue < 0) {
			throw new IllegalArgumentException("Initial value cannot be less than zero.");
		}
		this.initialValue = initialValue;
		grid = Utils.buildAnisotropic2DIntArray(3);
		grid[0][0] = this.initialValue;
		boundsReached = false;
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
	public IntAbelianSandpileSingleSource2D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		SerializableModelData data = (SerializableModelData) Utils.deserializeFromFile(backupPath);
		data = SerializableModelData.updateDataFormat(data);
		if (!SerializableModelData.Models.ABELIAN_SANDPILE.equals(data.get(SerializableModelData.MODEL))) {
			throw new IllegalArgumentException("The backup file contains a different model.");
		}
		if (!SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_TYPE))
				|| !SerializableModelData.InitialConfigurationImplementationTypes.INTEGER.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.GridTypes.INFINITE_REGULAR.equals(data.get(SerializableModelData.GRID_TYPE))
				|| !Integer.valueOf(2).equals(data.get(SerializableModelData.GRID_DIMENSION))
				|| !SerializableModelData.GridImplementationTypes.ANYSOTROPIC_INT_ARRAY_1.equals(data.get(SerializableModelData.GRID_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.CoordinateBoundsImplementationTypes.BOUNDS_REACHED_BOOLEAN.equals(data.get(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE))) {
			throw new IllegalArgumentException("The backup file's configuration is not compatible with this class.");
		}
		initialValue = (int) data.get(SerializableModelData.INITIAL_CONFIGURATION);
		grid = (int[][]) data.get(SerializableModelData.GRID);
		boundsReached = (boolean) data.get(SerializableModelData.COORDINATE_BOUNDS);
		step = (long) data.get(SerializableModelData.STEP);
		changed = (Boolean) data.get(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP);
	}
	
	@Override
	public Boolean nextStep() {
		int[][] newGrid = null;
		if (boundsReached) {
			boundsReached = false;
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
			}
			for (int y = 0; y <= x; y++) {
				int value = grid[x][y];
				if (value >= 4) {
					//If any position topples the state changes
					changed = true;
					//Add one to the neighboring positions
					//x+
					newGrid[x+1][y] += 1;
					//x-
					if (x > y) {
						int valueToAdd = 1;
						if (x == y + 1) {
							valueToAdd += 1;
							if (x == 1) {
								valueToAdd += 2;							
							}
						}
						newGrid[x-1][y] += valueToAdd;
					}
					//y+
					if (y < x) {
						int valueToAdd = 1;
						if (y == x - 1) {
							valueToAdd += 1;
						}
						int yy = y+1;
						newGrid[x][yy] += valueToAdd;
					}
					//y-
					if (y > 0) {
						int valueToAdd = 1;
						if (y == 1) {
							valueToAdd += 1;
						}
						newGrid[x][y-1] += valueToAdd;
					}
					
					if (x >= maxXMinusOne) {
						boundsReached = true;
					}
					newGrid[x][y] += value - 4;
				} else {
					newGrid[x][y] += value;
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
	public int getAsymmetricMaxX() {
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

	@Override
	public String getName() {
		return "AbelianSandpile";
	}
	
	@Override
	public String getSubfolderPath() {
		return getName() + "/2D/" + initialValue;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		SerializableModelData data = new SerializableModelData();
		data.put(SerializableModelData.MODEL, SerializableModelData.Models.ABELIAN_SANDPILE);
		data.put(SerializableModelData.INITIAL_CONFIGURATION, initialValue);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_TYPE, SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE, SerializableModelData.InitialConfigurationImplementationTypes.INTEGER);
		data.put(SerializableModelData.GRID, grid);
		data.put(SerializableModelData.GRID_TYPE, SerializableModelData.GridTypes.INFINITE_REGULAR);
		data.put(SerializableModelData.GRID_DIMENSION, 2);
		data.put(SerializableModelData.GRID_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_INT_ARRAY_1);
		data.put(SerializableModelData.COORDINATE_BOUNDS, boundsReached);
		data.put(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE, SerializableModelData.CoordinateBoundsImplementationTypes.BOUNDS_REACHED_BOOLEAN);
		data.put(SerializableModelData.STEP, step);
		data.put(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP, changed);
		Utils.serializeToFile(data, backupPath, backupName);
	}
	
}
