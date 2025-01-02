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
import cellularautomata.model3d.IsotropicCubicLongArrayModelA;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Sunflower-Cellular-Automaton-Definition">Sunflower</a> cellular automaton in 3D with a single source initial configuration.
 * 
 * @author Jaume
 *
 */
public class LongSunflower3D extends IsotropicCubicLongArrayModelA {
	
	private final long initialValue;
	private long step;
	private int maxX;
	private Boolean changed = null;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public LongSunflower3D(long initialValue) {
		this.initialValue = initialValue;
		grid = Utils.buildAnisotropic3DLongArray(7);
		grid[0][0][0] = initialValue;
		maxX = 5;
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
	public LongSunflower3D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
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
				|| !Integer.valueOf(3).equals(data.get(SerializableModelData.GRID_DIMENSION))
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
		grid = (long[][][]) data.get(SerializableModelData.GRID);
		maxX = (int) data.get(SerializableModelData.COORDINATE_BOUNDS);
		step = (long) data.get(SerializableModelData.STEP);
		changed = (Boolean) data.get(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP);
	}
	
	@Override
	public Boolean nextStep() {
		long[][][] newGrid = new long[maxX + 2][][];
		boolean changed = false;
		long[][] currentXSlice = grid[0];
		long[][] newSmallerXSlice = null, newCurrentXSlice = Utils.buildAnisotropic2DLongArray(1), newGreaterXSlice = Utils.buildAnisotropic2DLongArray(2);// build new grid progressively to save memory 
		newGrid[0] = newCurrentXSlice;
		newGrid[1] = newGreaterXSlice;
		// 00 | 00 | 00 | 01
		long currentValue = currentXSlice[0][0];
		long share = currentValue/7;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[0][0] += currentValue - 6*share;
			newGreaterXSlice[0][0] += share;
		} else {
			newCurrentXSlice[0][0] += currentValue;
		}
		// x slice transition
		grid[0] = null;// free old grid progressively to save memory
		currentXSlice = grid[1];
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = Utils.buildAnisotropic2DLongArray(3);
		newGrid[2] = newGreaterXSlice;		
		// 01 | 00 | 00 | 02
		currentValue = currentXSlice[0][0];
		share = currentValue/7;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[0][0] += currentValue - 6*share;
			newGreaterXSlice[0][0] += share;
			newSmallerXSlice[0][0] += 6*share;
			newCurrentXSlice[1][0] += 2*share;
		} else {
			newCurrentXSlice[0][0] += currentValue;
		}
		// 01 | 01 | 00 | 03
		currentValue = currentXSlice[1][0];
		share = currentValue/7;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[1][0] += currentValue - 6*share;
			newGreaterXSlice[1][0] += share;
			newCurrentXSlice[0][0] += 4*share;
			newCurrentXSlice[1][1] += 3*share;
		} else {
			newCurrentXSlice[1][0] += currentValue;
		}
		// 01 | 01 | 01 | 04
		currentValue = currentXSlice[1][1];
		share = currentValue/7;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[1][1] += currentValue - 6*share;
			newGreaterXSlice[1][1] += share;
			newCurrentXSlice[1][0] += 2*share;
		} else {
			newCurrentXSlice[1][1] += currentValue;
		}
		// x slice transition
		grid[1] = null;
		currentXSlice = grid[2];
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = Utils.buildAnisotropic2DLongArray(4);
		newGrid[3] = newGreaterXSlice;		
		// 02 | 00 | 00 | 05
		currentValue = currentXSlice[0][0];
		share = currentValue/7;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[0][0] += currentValue - 6*share;
			newGreaterXSlice[0][0] += share;
			newSmallerXSlice[0][0] += share;
			newCurrentXSlice[1][0] += share;
		} else {
			newCurrentXSlice[0][0] += currentValue;
		}
		// 02 | 01 | 00 | 06
		currentValue = currentXSlice[1][0];
		share = currentValue/7;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[1][0] += currentValue - 6*share;
			newGreaterXSlice[1][0] += share;
			newSmallerXSlice[1][0] += 2*share;
			newCurrentXSlice[2][0] += 2*share;
			newCurrentXSlice[0][0] += 4*share;
			newCurrentXSlice[1][1] += 2*share;
		} else {
			newCurrentXSlice[1][0] += currentValue;
		}
		// 02 | 01 | 01 | 07
		currentValue = currentXSlice[1][1];
		share = currentValue/7;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[1][1] += currentValue - 6*share;
			newGreaterXSlice[1][1] += share;
			newSmallerXSlice[1][1] += 3*share;
			newCurrentXSlice[2][1] += 2*share;
			newCurrentXSlice[1][0] += 2*share;
		} else {
			newCurrentXSlice[1][1] += currentValue;
		}
		// 02 | 02 | 00 | 08
		currentValue = currentXSlice[2][0];
		share = currentValue/7;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[2][0] += currentValue - 6*share;
			newGreaterXSlice[2][0] += share;
			newCurrentXSlice[1][0] += share;
			newCurrentXSlice[2][1] += share;
		} else {
			newCurrentXSlice[2][0] += currentValue;
		}
		// 02 | 02 | 01 | 09
		currentValue = currentXSlice[2][1];
		share = currentValue/7;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[2][1] += currentValue - 6*share;
			newGreaterXSlice[2][1] += share;
			newCurrentXSlice[1][1] += 2*share;
			newCurrentXSlice[2][2] += 3*share;
			newCurrentXSlice[2][0] += 2*share;
		} else {
			newCurrentXSlice[2][1] += currentValue;
		}
		// 02 | 02 | 02 | 10
		currentValue = currentXSlice[2][2];
		share = currentValue/7;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[2][2] += currentValue - 6*share;
			newGreaterXSlice[2][2] += share;
			newCurrentXSlice[2][1] += share;
		} else {
			newCurrentXSlice[2][2] += currentValue;
		}
		// x slice transition
		grid[2] = null;
		currentXSlice = grid[3];
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = Utils.buildAnisotropic2DLongArray(5);
		newGrid[4] = newGreaterXSlice;		
		// 03 | 00 | 00 | 05
		currentValue = currentXSlice[0][0];
		share = currentValue/7;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[0][0] += currentValue - 6*share;
			newGreaterXSlice[0][0] += share;
			newSmallerXSlice[0][0] += share;
			newCurrentXSlice[1][0] += share;
		} else {
			newCurrentXSlice[0][0] += currentValue;
		}
		// 03 | 01 | 00 | 11
		currentValue = currentXSlice[1][0];
		share = currentValue/7;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[1][0] += currentValue - 6*share;
			newGreaterXSlice[1][0] += share;
			newSmallerXSlice[1][0] += share;
			newCurrentXSlice[2][0] += share;
			newCurrentXSlice[0][0] += 4*share;
			newCurrentXSlice[1][1] += 2*share;
		} else {
			newCurrentXSlice[1][0] += currentValue;
		}
		// 03 | 01 | 01 | 12
		currentValue = currentXSlice[1][1];
		share = currentValue/7;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[1][1] += currentValue - 6*share;
			newGreaterXSlice[1][1] += share;
			newSmallerXSlice[1][1] += share;
			newCurrentXSlice[2][1] += share;
			newCurrentXSlice[1][0] += 2*share;
		} else {
			newCurrentXSlice[1][1] += currentValue;
		}
		// 03 | 02 | 00 | 13
		currentValue = currentXSlice[2][0];
		share = currentValue/7;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[2][0] += currentValue - 6*share;
			newGreaterXSlice[2][0] += share;
			newSmallerXSlice[2][0] += 2*share;
			newCurrentXSlice[3][0] += 2*share;
			newCurrentXSlice[1][0] += share;
			newCurrentXSlice[2][1] += share;
		} else {
			newCurrentXSlice[2][0] += currentValue;
		}
		// 03 | 02 | 01 | 14
		currentValue = currentXSlice[2][1];
		share = currentValue/7;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[2][1] += currentValue - 6*share;
			newGreaterXSlice[2][1] += share;
			newSmallerXSlice[2][1] += 2*share;
			newCurrentXSlice[3][1] += 2*share;
			newCurrentXSlice[1][1] += 2*share;
			newCurrentXSlice[2][2] += 2*share;
			newCurrentXSlice[2][0] += 2*share;
		} else {
			newCurrentXSlice[2][1] += currentValue;
		}
		// 03 | 02 | 02 | 15
		currentValue = currentXSlice[2][2];
		share = currentValue/7;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[2][2] += currentValue - 6*share;
			newGreaterXSlice[2][2] += share;
			newSmallerXSlice[2][2] += 3*share;
			newCurrentXSlice[3][2] += 2*share;
			newCurrentXSlice[2][1] += share;
		} else {
			newCurrentXSlice[2][2] += currentValue;
		}
		// 03 | 03 | 00 | 08
		currentValue = currentXSlice[3][0];
		share = currentValue/7;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[3][0] += currentValue - 6*share;
			newGreaterXSlice[3][0] += share;
			newCurrentXSlice[2][0] += share;
			newCurrentXSlice[3][1] += share;
		} else {
			newCurrentXSlice[3][0] += currentValue;
		}
		// 03 | 03 | 01 | 16
		currentValue = currentXSlice[3][1];
		share = currentValue/7;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[3][1] += currentValue - 6*share;
			newGreaterXSlice[3][1] += share;
			newCurrentXSlice[2][1] += share;
			newCurrentXSlice[3][2] += share;
			newCurrentXSlice[3][0] += 2*share;
		} else {
			newCurrentXSlice[3][1] += currentValue;
		}
		// 03 | 03 | 02 | 17
		currentValue = currentXSlice[3][2];
		share = currentValue/7;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[3][2] += currentValue - 6*share;
			newGreaterXSlice[3][2] += share;
			newCurrentXSlice[2][2] += 2*share;
			newCurrentXSlice[3][3] += 3*share;
			newCurrentXSlice[3][1] += share;
		} else {
			newCurrentXSlice[3][2] += currentValue;
		}
		// 03 | 03 | 03 | 10
		currentValue = currentXSlice[3][3];
		share = currentValue/7;
		if (share != 0) {
			changed = true;
			newCurrentXSlice[3][3] += currentValue - 6*share;
			newGreaterXSlice[3][3] += share;
			newCurrentXSlice[3][2] += share;
		} else {
			newCurrentXSlice[3][3] += currentValue;
		}
		// 4 <= x < edge - 2
		int edge = grid.length - 1;
		int edgeMinusTwo = edge - 2;
		if (toppleRangeBeyondX3(newGrid, 4, edgeMinusTwo)) {
			changed = true;
		}
		//edge - 2 <= x < edge
		if (toppleRangeBeyondX3(newGrid, edgeMinusTwo, edge)) {
			changed = true;
			maxX++;
		}
		if (newGrid.length != grid.length) {
			newGrid[grid.length] = Utils.buildAnisotropic2DLongArray(newGrid.length);
		}	
		grid = newGrid;
		step++;
		this.changed = changed;
		return changed;
	}
	
	private boolean toppleRangeBeyondX3(long[][][] newGrid, int minX, int maxX) {
		boolean anyToppled = false;
		int x = minX, xMinusOne = x - 1, xPlusOne = x + 1, xPlusTwo = xPlusOne + 1;
		long[][] newSmallerXSlice = null, newCurrentXSlice = newGrid[xMinusOne], newGreaterXSlice = newGrid[x];
		for (; x != maxX; xMinusOne = x, x = xPlusOne, xPlusOne = xPlusTwo, xPlusTwo++) {
			// x slice transition
			grid[xMinusOne] = null;
			long[][] currentXSlice = grid[x];
			newSmallerXSlice = newCurrentXSlice;
			newCurrentXSlice = newGreaterXSlice;
			newGreaterXSlice = Utils.buildAnisotropic2DLongArray(xPlusTwo);
			newGrid[xPlusOne] = newGreaterXSlice;
			//  x | 00 | 00 | 05
			long currentValue = currentXSlice[0][0];
			long share = currentValue/7;
			if (share != 0) {
				anyToppled = true;
				newCurrentXSlice[0][0] += currentValue - 6*share;
				newGreaterXSlice[0][0] += share;
				newSmallerXSlice[0][0] += share;
				newCurrentXSlice[1][0] += share;
			} else {
				newCurrentXSlice[0][0] += currentValue;
			}
			//  x | 01 | 00 | 11
			currentValue = currentXSlice[1][0];
			share = currentValue/7;
			if (share != 0) {
				anyToppled = true;
				newCurrentXSlice[1][0] += currentValue - 6*share;
				newGreaterXSlice[1][0] += share;
				newSmallerXSlice[1][0] += share;
				newCurrentXSlice[2][0] += share;
				newCurrentXSlice[0][0] += 4*share;
				newCurrentXSlice[1][1] += 2*share;
			} else {
				newCurrentXSlice[1][0] += currentValue;
			}
			//  x | 01 | 01 | 12
			currentValue = currentXSlice[1][1];
			share = currentValue/7;
			if (share != 0) {
				anyToppled = true;
				newCurrentXSlice[1][1] += currentValue - 6*share;
				newGreaterXSlice[1][1] += share;
				newSmallerXSlice[1][1] += share;
				newCurrentXSlice[2][1] += share;
				newCurrentXSlice[1][0] += 2*share;
			} else {
				newCurrentXSlice[1][1] += currentValue;
			}
			//  x | 02 | 00 | 18
			currentValue = currentXSlice[2][0];
			share = currentValue/7;
			if (share != 0) {
				anyToppled = true;
				newCurrentXSlice[2][0] += currentValue - 6*share;
				newGreaterXSlice[2][0] += share;
				newSmallerXSlice[2][0] += share;
				newCurrentXSlice[3][0] += share;
				newCurrentXSlice[1][0] += share;
				newCurrentXSlice[2][1] += share;
			} else {
				newCurrentXSlice[2][0] += currentValue;
			}
			//  x | 02 | 01 | 19
			currentValue = currentXSlice[2][1];
			share = currentValue/7;
			if (share != 0) {
				anyToppled = true;
				newCurrentXSlice[2][1] += currentValue - 6*share;
				newGreaterXSlice[2][1] += share;
				newSmallerXSlice[2][1] += share;
				newCurrentXSlice[3][1] += share;
				newCurrentXSlice[1][1] += 2*share;
				newCurrentXSlice[2][2] += 2*share;
				newCurrentXSlice[2][0] += 2*share;
			} else {
				newCurrentXSlice[2][1] += currentValue;
			}
			//  x | 02 | 02 | 20
			currentValue = currentXSlice[2][2];
			share = currentValue/7;
			if (share != 0) {
				anyToppled = true;
				newCurrentXSlice[2][2] += currentValue - 6*share;
				newGreaterXSlice[2][2] += share;
				newSmallerXSlice[2][2] += share;
				newCurrentXSlice[3][2] += share;
				newCurrentXSlice[2][1] += share;
			} else {
				newCurrentXSlice[2][2] += currentValue;
			}
			int y = 3, yMinusOne = 2, yPlusOne = 4;
			for (; y != xMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
				//  x |  y | 00 | 18
				currentValue = currentXSlice[y][0];
				share = currentValue/7;
				if (share != 0) {
					anyToppled = true;
					newCurrentXSlice[y][0] += currentValue - 6*share;
					newGreaterXSlice[y][0] += share;
					newSmallerXSlice[y][0] += share;
					newCurrentXSlice[yPlusOne][0] += share;
					newCurrentXSlice[yMinusOne][0] += share;
					newCurrentXSlice[y][1] += share;
				} else {
					newCurrentXSlice[y][0] += currentValue;
				}
				//  x |  y | 01 | 24
				currentValue = currentXSlice[y][1];
				share = currentValue/7;
				if (share != 0) {
					anyToppled = true;
					newCurrentXSlice[y][1] += currentValue - 6*share;
					newGreaterXSlice[y][1] += share;
					newSmallerXSlice[y][1] += share;
					newCurrentXSlice[yPlusOne][1] += share;
					newCurrentXSlice[yMinusOne][1] += share;
					newCurrentXSlice[y][2] += share;
					newCurrentXSlice[y][0] += 2*share;
				} else {
					newCurrentXSlice[y][1] += currentValue;
				}
				int z = 2, zMinusOne = 1, zPlusOne = 3;
				for (; z != yMinusOne; zMinusOne = z, z = zPlusOne, zPlusOne++) {
					//  x |  y |  z | 27
					currentValue = currentXSlice[y][z];
					share = currentValue/7;
					if (share != 0) {
						anyToppled = true;
						newCurrentXSlice[y][z] += currentValue - 6*share;
						newGreaterXSlice[y][z] += share;
						newSmallerXSlice[y][z] += share;
						newCurrentXSlice[yPlusOne][z] += share;
						newCurrentXSlice[yMinusOne][z] += share;
						newCurrentXSlice[y][zPlusOne] += share;
						newCurrentXSlice[y][zMinusOne] += share;
					} else {
						newCurrentXSlice[y][z] += currentValue;
					}
				}
				//  x |  y |  z | 25
				currentValue = currentXSlice[y][z];
				share = currentValue/7;
				if (share != 0) {
					anyToppled = true;
					newCurrentXSlice[y][z] += currentValue - 6*share;
					newGreaterXSlice[y][z] += share;
					newSmallerXSlice[y][z] += share;
					newCurrentXSlice[yPlusOne][z] += share;
					newCurrentXSlice[yMinusOne][z] += 2*share;
					newCurrentXSlice[y][zPlusOne] += 2*share;
					newCurrentXSlice[y][zMinusOne] += share;
				} else {
					newCurrentXSlice[y][z] += currentValue;
				}
				zMinusOne = z;
				z = y;
				//  x |  y |  z | 20
				currentValue = currentXSlice[y][z];
				share = currentValue/7;
				if (share != 0) {
					anyToppled = true;
					newCurrentXSlice[y][z] += currentValue - 6*share;
					newGreaterXSlice[y][z] += share;
					newSmallerXSlice[y][z] += share;
					newCurrentXSlice[yPlusOne][z] += share;
					newCurrentXSlice[y][zMinusOne] += share;
				} else {
					newCurrentXSlice[y][z] += currentValue;
				}
			}
			//  x |  y | 00 | 13
			currentValue = currentXSlice[y][0];
			share = currentValue/7;
			if (share != 0) {
				anyToppled = true;
				newCurrentXSlice[y][0] += currentValue - 6*share;
				newGreaterXSlice[y][0] += share;
				newSmallerXSlice[y][0] += 2*share;
				newCurrentXSlice[yPlusOne][0] += 2*share;
				newCurrentXSlice[yMinusOne][0] += share;
				newCurrentXSlice[y][1] += share;
			} else {
				newCurrentXSlice[y][0] += currentValue;
			}
			//  x |  y | 01 | 21
			currentValue = currentXSlice[y][1];
			share = currentValue/7;
			if (share != 0) {
				anyToppled = true;
				newCurrentXSlice[y][1] += currentValue - 6*share;
				newGreaterXSlice[y][1] += share;
				newSmallerXSlice[y][1] += 2*share;
				newCurrentXSlice[yPlusOne][1] += 2*share;
				newCurrentXSlice[yMinusOne][1] += share;
				newCurrentXSlice[y][2] += share;
				newCurrentXSlice[y][0] += 2*share;
			} else {
				newCurrentXSlice[y][1] += currentValue;
			}
			int z = 2, zMinusOne = 1, zPlusOne = 3;
			for (; z != yMinusOne; zMinusOne = z, z = zPlusOne, zPlusOne++) {
				//  x |  y |  z | 26
				currentValue = currentXSlice[y][z];
				share = currentValue/7;
				if (share != 0) {
					anyToppled = true;
					newCurrentXSlice[y][z] += currentValue - 6*share;
					newGreaterXSlice[y][z] += share;
					newSmallerXSlice[y][z] += 2*share;
					newCurrentXSlice[yPlusOne][z] += 2*share;
					newCurrentXSlice[yMinusOne][z] += share;
					newCurrentXSlice[y][zPlusOne] += share;
					newCurrentXSlice[y][zMinusOne] += share;
				} else {
					newCurrentXSlice[y][z] += currentValue;
				}
			}
			//  x |  y |  z | 22
			currentValue = currentXSlice[y][z];
			share = currentValue/7;
			if (share != 0) {
				anyToppled = true;
				newCurrentXSlice[y][z] += currentValue - 6*share;
				newGreaterXSlice[y][z] += share;
				newSmallerXSlice[y][z] += 2*share;
				newCurrentXSlice[yPlusOne][z] += 2*share;
				newCurrentXSlice[yMinusOne][z] += 2*share;
				newCurrentXSlice[y][zPlusOne] += 2*share;
				newCurrentXSlice[y][zMinusOne] += share;
			} else {
				newCurrentXSlice[y][z] += currentValue;
			}
			zMinusOne = z;
			z = y;
			//  x |  y |  z | 15
			currentValue = currentXSlice[y][z];
			share = currentValue/7;
			if (share != 0) {
				anyToppled = true;
				newCurrentXSlice[y][z] += currentValue - 6*share;
				newGreaterXSlice[y][z] += share;
				newSmallerXSlice[y][z] += 3*share;
				newCurrentXSlice[yPlusOne][z] += 2*share;
				newCurrentXSlice[y][zMinusOne] += share;
			} else {
				newCurrentXSlice[y][z] += currentValue;
			}
			yMinusOne = y;
			y = x;
			//  x |  y | 00 | 08
			currentValue = currentXSlice[y][0];
			share = currentValue/7;
			if (share != 0) {
				anyToppled = true;
				newCurrentXSlice[y][0] += currentValue - 6*share;
				newGreaterXSlice[y][0] += share;
				newCurrentXSlice[yMinusOne][0] += share;
				newCurrentXSlice[y][1] += share;
			} else {
				newCurrentXSlice[y][0] += currentValue;
			}
			//  x |  y | 01 | 16
			currentValue = currentXSlice[y][1];
			share = currentValue/7;
			if (share != 0) {
				anyToppled = true;
				newCurrentXSlice[y][1] += currentValue - 6*share;
				newGreaterXSlice[y][1] += share;
				newCurrentXSlice[yMinusOne][1] += share;
				newCurrentXSlice[y][2] += share;
				newCurrentXSlice[y][0] += 2*share;
			} else {
				newCurrentXSlice[y][1] += currentValue;
			}
			z = 2;
			zMinusOne = 1;
			zPlusOne = 3;
			for (; z != yMinusOne; zMinusOne = z, z = zPlusOne, zPlusOne++) {
				//  x |  y |  z | 23
				currentValue = currentXSlice[y][z];
				share = currentValue/7;
				if (share != 0) {
					anyToppled = true;
					newCurrentXSlice[y][z] += currentValue - 6*share;
					newGreaterXSlice[y][z] += share;
					newCurrentXSlice[yMinusOne][z] += share;
					newCurrentXSlice[y][zPlusOne] += share;
					newCurrentXSlice[y][zMinusOne] += share;
				} else {
					newCurrentXSlice[y][z] += currentValue;
				}
			}
			//  x |  y |  z | 17
			currentValue = currentXSlice[y][z];
			share = currentValue/7;
			if (share != 0) {
				anyToppled = true;
				newCurrentXSlice[y][z] += currentValue - 6*share;
				newGreaterXSlice[y][z] += share;
				newCurrentXSlice[yMinusOne][z] += 2*share;
				newCurrentXSlice[y][zPlusOne] += 3*share;
				newCurrentXSlice[y][zMinusOne] += share;
			} else {
				newCurrentXSlice[y][z] += currentValue;
			}
			zMinusOne = z;
			z = y;
			//  x |  y |  z | 10
			currentValue = currentXSlice[y][z];
			share = currentValue/7;
			if (share != 0) {
				anyToppled = true;
				newCurrentXSlice[y][z] += currentValue - 6*share;
				newGreaterXSlice[y][z] += share;
				newCurrentXSlice[y][zMinusOne] += share;
			} else {
				newCurrentXSlice[y][z] += currentValue;
			}
		}
		return anyToppled;
	}

	@Override
	public Boolean isChanged() {
		return changed;
	}

	@Override
	public int getAsymmetricMaxX() {
		return maxX;
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
		return 0;
	}

	@Override
	public String getName() {
		return "Sunflower";
	}
	
	@Override
	public String getSubfolderPath() {
		return getName() + "/3D/" + initialValue + "/0";
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
		data.put(SerializableModelData.GRID_DIMENSION, 3);
		data.put(SerializableModelData.GRID_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_LONG_PRIMITIVE_ARRAY_1);
		data.put(SerializableModelData.COORDINATE_BOUNDS, maxX);
		data.put(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE, SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER);
		data.put(SerializableModelData.STEP, step);
		data.put(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP, changed);
		Utils.serializeToFile(data, backupPath, backupName);
	}
}
