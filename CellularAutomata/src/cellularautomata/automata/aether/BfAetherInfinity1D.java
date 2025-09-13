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

import org.apache.commons.math3.fraction.BigFraction;

import cellularautomata.Utils;
import cellularautomata.model.SerializableModelData;
import cellularautomata.model1d.IsotropicNumericArrayModelAsymmetricSection1D;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 1D with a single source initial configuration of infinity
 * 
 * @author Jaume
 *
 */
public class BfAetherInfinity1D extends IsotropicNumericArrayModelAsymmetricSection1D<BigFraction> {

	private final boolean isPositive;
	private long step;
	private int maxX;
	
	public BfAetherInfinity1D(boolean isPositive) {
		this.isPositive = isPositive;
		grid = new BigFraction[5];
		Arrays.fill(grid, BigFraction.ZERO);
		grid[0] = isPositive? BigFraction.ONE : BigFraction.MINUS_ONE;
		maxX = 2;
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
	public BfAetherInfinity1D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		SerializableModelData data = (SerializableModelData) Utils.deserializeFromFile(backupPath);
		data = SerializableModelData.updateDataFormat(data);
		if (!SerializableModelData.Models.AETHER.equals(data.get(SerializableModelData.MODEL))) {
			throw new IllegalArgumentException("The backup file contains a different model.");
		}
		if (!SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_TYPE))
				|| !SerializableModelData.InitialConfigurationImplementationTypes.BOOLEAN.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.GridTypes.INFINITE_REGULAR.equals(data.get(SerializableModelData.GRID_TYPE))
				|| !Integer.valueOf(1).equals(data.get(SerializableModelData.GRID_DIMENSION))
				|| !SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BIG_FRACTION_ARRAY_1.equals(data.get(SerializableModelData.GRID_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER.equals(data.get(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE))) {
			throw new IllegalArgumentException("The backup file's configuration is not compatible with this class.");
		}
		isPositive = (boolean) data.get(SerializableModelData.INITIAL_CONFIGURATION);
		grid = (BigFraction[]) data.get(SerializableModelData.GRID);
		maxX = (int) data.get(SerializableModelData.COORDINATE_BOUNDS);
		step = (long) data.get(SerializableModelData.STEP);
	}
	
	@Override
	public Boolean nextStep() {
		BigFraction[] newGrid = new BigFraction[maxX + 3];
		Arrays.fill(newGrid, BigFraction.ZERO);
		BigFraction currentValue, greaterXNeighborValue, smallerXNeighborValue;
		//x = 0
		currentValue = grid[0];
		greaterXNeighborValue = grid[1];
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			BigFraction toShare = currentValue.subtract(greaterXNeighborValue);
			BigFraction share = toShare.divide(3);
			newGrid[0] = newGrid[0].add(currentValue.subtract(toShare).add(share));
			newGrid[1] = newGrid[1].add(share);	
		} else {
			newGrid[0] = newGrid[0].add(currentValue);
		}
		//x = 1
		//reuse values obtained previously
		smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterXNeighborValue = grid[2];
		if (smallerXNeighborValue.compareTo(currentValue) < 0) {
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				if (smallerXNeighborValue.equals(greaterXNeighborValue)) {
					// gn == sn < current
					BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
					BigFraction share = toShare.divide(3);
					newGrid[0] = newGrid[0].add(share.add(share));//one more for the symmetric position at the other side
					newGrid[1] = newGrid[1].add(currentValue.subtract(toShare).add(share));
					newGrid[2] = newGrid[2].add(share);
				} else if (smallerXNeighborValue.compareTo(greaterXNeighborValue) < 0) {
					// sn < gn < current
					BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
					BigFraction share = toShare.divide(3);
					newGrid[0] = newGrid[0].add(share.add(share));//one more for the symmetric position at the other side
					newGrid[2] = newGrid[2].add(share);
					BigFraction currentRemainingValue = currentValue.subtract(share).subtract(share);
					toShare = currentRemainingValue.subtract(smallerXNeighborValue); 
					share = toShare.divide(2);
					newGrid[0] = newGrid[0].add(share.add(share));//one more for the symmetric position at the other side
					newGrid[1] = newGrid[1].add(currentRemainingValue.subtract(toShare).add(share));
				} else {
					// gn < sn < current
					BigFraction toShare = currentValue.subtract(smallerXNeighborValue); 
					BigFraction share = toShare.divide(3);
					newGrid[0] = newGrid[0].add(share.add(share));//one more for the symmetric position at the other side
					newGrid[2] = newGrid[2].add(share);
					BigFraction currentRemainingValue = currentValue.subtract(share).subtract(share);
					toShare = currentRemainingValue.subtract(greaterXNeighborValue); 
					share = toShare.divide(2);
					newGrid[1] = newGrid[1].add(currentRemainingValue.subtract(toShare).add(share));
					newGrid[2] = newGrid[2].add(share);
				}
			} else {
				// sn < current <= gn
				BigFraction toShare = currentValue.subtract(smallerXNeighborValue); 
				BigFraction share = toShare.divide(2);
				newGrid[0] = newGrid[0].add(share.add(share));//one more for the symmetric position at the other side
				newGrid[1] = newGrid[1].add(currentValue.subtract(toShare).add(share));
			}
		} else {
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				// gn < current <= sn
				BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
				BigFraction share = toShare.divide(2);
				newGrid[1] = newGrid[1].add(currentValue.subtract(toShare).add(share));
				newGrid[2] = newGrid[2].add(share);
			} else {
				newGrid[1] = newGrid[1].add(currentValue);
			}
		}
		//2 <= x < edge
		int edge = grid.length - 1;
		toppleRangeBeyondX1(newGrid, 2, edge);
		grid = newGrid;
		maxX++;
		step++;
		return true;
	}

	@Override
	public Boolean isChanged() {
		return step == 0 ? null : true;
	}
	
	private void toppleRangeBeyondX1(BigFraction[] newGrid, int minX, int maxX) {
		int x = minX, xMinusOne = x - 1, xPlusOne = x + 1;
		BigFraction smallerXNeighborValue, currentValue = grid[xMinusOne], greaterXNeighborValue = grid[x];
		for (; x < maxX; xMinusOne = x, x = xPlusOne, xPlusOne++) {
			//reuse values obtained previously
			smallerXNeighborValue = currentValue;
			currentValue = greaterXNeighborValue;
			greaterXNeighborValue = grid[xPlusOne];
			if (smallerXNeighborValue.compareTo(currentValue) < 0) {
				if (greaterXNeighborValue.compareTo(currentValue) < 0) {
					if (smallerXNeighborValue.equals(greaterXNeighborValue)) {
						// gn == sn < current
						BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
						BigFraction share = toShare.divide(3);
						newGrid[xMinusOne] = newGrid[xMinusOne].add(share);
						newGrid[x] = newGrid[x].add(currentValue.subtract(toShare).add(share));
						newGrid[xPlusOne] = newGrid[xPlusOne].add(share);
					} else if (smallerXNeighborValue.compareTo(greaterXNeighborValue) < 0) {
						// sn < gn < current
						BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
						BigFraction share = toShare.divide(3);
						newGrid[xMinusOne] = newGrid[xMinusOne].add(share);
						newGrid[xPlusOne] = newGrid[xPlusOne].add(share);
						BigFraction currentRemainingValue = currentValue.subtract(share).subtract(share);
						toShare = currentRemainingValue.subtract(smallerXNeighborValue); 
						share = toShare.divide(2);
						newGrid[xMinusOne] = newGrid[xMinusOne].add(share);
						newGrid[x] = newGrid[x].add(currentRemainingValue.subtract(toShare).add(share));
					} else {
						// gn < sn < current
						BigFraction toShare = currentValue.subtract(smallerXNeighborValue); 
						BigFraction share = toShare.divide(3);
						newGrid[xMinusOne] = newGrid[xMinusOne].add(share);
						newGrid[xPlusOne] = newGrid[xPlusOne].add(share);
						BigFraction currentRemainingValue = currentValue.subtract(share).subtract(share);
						toShare = currentRemainingValue.subtract(greaterXNeighborValue); 
						share = toShare.divide(2);
						newGrid[x] = newGrid[x].add(currentRemainingValue.subtract(toShare).add(share));
						newGrid[xPlusOne] = newGrid[xPlusOne].add(share);
					}
				} else {
					// sn < current <= gn
					BigFraction toShare = currentValue.subtract(smallerXNeighborValue); 
					BigFraction share = toShare.divide(2);
					newGrid[xMinusOne] = newGrid[xMinusOne].add(share);
					newGrid[x] = newGrid[x].add(currentValue.subtract(toShare).add(share));
				}
			} else {
				if (greaterXNeighborValue.compareTo(currentValue) < 0) {
					// gn < current <= sn
					BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
					BigFraction share = toShare.divide(2);
					newGrid[x] = newGrid[x].add(currentValue.subtract(toShare).add(share));
					newGrid[xPlusOne] = newGrid[xPlusOne].add(share);
				} else {
					newGrid[x] = newGrid[x].add(currentValue);
				}
			}
		}
	}

	@Override
	public int getSize() {
		return maxX;
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
	public String getWholeGridSubfolderPath() {
		String path = getName() + "/1D/";
		if (!isPositive) path += "-";
		path += "infinity";
		return path;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		SerializableModelData data = new SerializableModelData();
		data.put(SerializableModelData.MODEL, SerializableModelData.Models.AETHER);
		data.put(SerializableModelData.INITIAL_CONFIGURATION, isPositive);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_TYPE, SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE, SerializableModelData.InitialConfigurationImplementationTypes.BOOLEAN);
		data.put(SerializableModelData.GRID, grid);
		data.put(SerializableModelData.GRID_TYPE, SerializableModelData.GridTypes.INFINITE_REGULAR);
		data.put(SerializableModelData.GRID_DIMENSION, 1);
		data.put(SerializableModelData.GRID_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BIG_FRACTION_ARRAY_1);
		data.put(SerializableModelData.COORDINATE_BOUNDS, maxX);
		data.put(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE, SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER);
		data.put(SerializableModelData.STEP, step);
		Utils.serializeToFile(data, backupPath, backupName);
	}
}
