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
import cellularautomata.model1d.IsotropicBooleanArrayModel1DA;

public class AetherInfinityTopplingAlternationCompliance1D extends IsotropicBooleanArrayModel1DA {

	/** A 1D array representing the grid */
	private BigFraction[] sourceGrid;
	
	private boolean itsEvenPositionsTurnToTopple;

	private final boolean isPositive;
	private long step;
	private int maxX;
	
	public AetherInfinityTopplingAlternationCompliance1D(boolean isPositive) {
		this.isPositive = isPositive;
		final int initialSize = 5;
		sourceGrid = new BigFraction[initialSize];
		Arrays.fill(sourceGrid, BigFraction.ZERO);
		sourceGrid[0] = isPositive? BigFraction.ONE : BigFraction.MINUS_ONE;
		itsEvenPositionsTurnToTopple = isPositive;
		maxX = 2;
		step = 0;
		nextStep();
	}
	
	/**
	 * Creates an instance restoring a backup
	 * 
	 * @param backupPath the path to the backup file to restore.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 */
	public AetherInfinityTopplingAlternationCompliance1D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {		
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
		sourceGrid = (BigFraction[]) data.get(SerializableModelData.GRID);
		maxX = (int) data.get(SerializableModelData.COORDINATE_BOUNDS);
		step = (long) data.get(SerializableModelData.STEP);
		itsEvenPositionsTurnToTopple = isPositive == (step%2 == 0);
		if (SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BOOLEAN_PRIMITIVE_ARRAY_1.equals(data.get(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE_IMPLEMENTATION_TYPE))) {
			grid = (boolean[]) data.get(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE);
		} else {
			nextStep();
		}
	}
	
	@Override
	public Boolean nextStep() {
		final int newSize = maxX + 3;
		BigFraction[] newSourceGrid = new BigFraction[newSize];
		grid = null;
		grid = new boolean[newSize];
		Arrays.fill(newSourceGrid, BigFraction.ZERO);
		BigFraction currentValue, greaterXNeighborValue, smallerXNeighborValue;
		//x = 0
		boolean itsCurrentPositionsTurnToTopple = itsEvenPositionsTurnToTopple;
		currentValue = sourceGrid[0];
		greaterXNeighborValue = sourceGrid[1];
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			BigFraction toShare = currentValue.subtract(greaterXNeighborValue);
			BigFraction share = toShare.divide(3);
			newSourceGrid[0] = newSourceGrid[0].add(currentValue.subtract(toShare).add(share));
			newSourceGrid[1] = newSourceGrid[1].add(share);	
			grid[0] = itsCurrentPositionsTurnToTopple;
		} else {
			newSourceGrid[0] = newSourceGrid[0].add(currentValue);
			grid[0] = !itsCurrentPositionsTurnToTopple;
		}
		//x = 1
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		//reuse values obtained previously
		smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterXNeighborValue = sourceGrid[2];
		boolean toppled = false;
		if (smallerXNeighborValue.compareTo(currentValue) < 0) {
			toppled = true;
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				if (smallerXNeighborValue.equals(greaterXNeighborValue)) {
					// gn == sn < current
					BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
					BigFraction share = toShare.divide(3);
					newSourceGrid[0] = newSourceGrid[0].add(share.add(share));//one more for the symmetric position at the other side
					newSourceGrid[1] = newSourceGrid[1].add(currentValue.subtract(toShare).add(share));
					newSourceGrid[2] = newSourceGrid[2].add(share);
				} else if (smallerXNeighborValue.compareTo(greaterXNeighborValue) < 0) {
					// sn < gn < current
					BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
					BigFraction share = toShare.divide(3);
					newSourceGrid[0] = newSourceGrid[0].add(share.add(share));//one more for the symmetric position at the other side
					newSourceGrid[2] = newSourceGrid[2].add(share);
					BigFraction currentRemainingValue = currentValue.subtract(share).subtract(share);
					toShare = currentRemainingValue.subtract(smallerXNeighborValue); 
					share = toShare.divide(2);
					newSourceGrid[0] = newSourceGrid[0].add(share.add(share));//one more for the symmetric position at the other side
					newSourceGrid[1] = newSourceGrid[1].add(currentRemainingValue.subtract(toShare).add(share));
				} else {
					// gn < sn < current
					BigFraction toShare = currentValue.subtract(smallerXNeighborValue); 
					BigFraction share = toShare.divide(3);
					newSourceGrid[0] = newSourceGrid[0].add(share.add(share));//one more for the symmetric position at the other side
					newSourceGrid[2] = newSourceGrid[2].add(share);
					BigFraction currentRemainingValue = currentValue.subtract(share).subtract(share);
					toShare = currentRemainingValue.subtract(greaterXNeighborValue); 
					share = toShare.divide(2);
					newSourceGrid[1] = newSourceGrid[1].add(currentRemainingValue.subtract(toShare).add(share));
					newSourceGrid[2] = newSourceGrid[2].add(share);
				}
			} else {
				// sn < current <= gn
				BigFraction toShare = currentValue.subtract(smallerXNeighborValue); 
				BigFraction share = toShare.divide(2);
				newSourceGrid[0] = newSourceGrid[0].add(share.add(share));//one more for the symmetric position at the other side
				newSourceGrid[1] = newSourceGrid[1].add(currentValue.subtract(toShare).add(share));
			}
		} else {
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				toppled = true;
				// gn < current <= sn
				BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
				BigFraction share = toShare.divide(2);
				newSourceGrid[1] = newSourceGrid[1].add(currentValue.subtract(toShare).add(share));
				newSourceGrid[2] = newSourceGrid[2].add(share);
			} else {
				newSourceGrid[1] = newSourceGrid[1].add(currentValue);
			}
		}
		grid[1] = toppled == itsCurrentPositionsTurnToTopple;
		//2 <= x < edge
		int edge = sourceGrid.length - 1;
		toppleRangeBeyondX1(newSourceGrid, 2, edge);
		grid[edge] = edge%2 == 0 != itsEvenPositionsTurnToTopple;
		sourceGrid = newSourceGrid;
		maxX++;
		step++;
		itsEvenPositionsTurnToTopple = !itsEvenPositionsTurnToTopple;
		return true;
	}

	@Override
	public Boolean isChanged() {
		return step == 0 ? null : true;
	}
	
	private void toppleRangeBeyondX1(BigFraction[] newSourceGrid, int minX, int maxX) {
		int x = minX, xMinusOne = x - 1, xPlusOne = x + 1;
		BigFraction smallerXNeighborValue, currentValue = sourceGrid[xMinusOne], greaterXNeighborValue = sourceGrid[x];
		boolean itsCurrentPositionsTurnToTopple = x%2 == 0 == itsEvenPositionsTurnToTopple; 
		for (; x < maxX; xMinusOne = x, x = xPlusOne, xPlusOne++, itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple) {
			//reuse values obtained previously
			smallerXNeighborValue = currentValue;
			currentValue = greaterXNeighborValue;
			greaterXNeighborValue = sourceGrid[xPlusOne];
			boolean toppled = false;
			if (smallerXNeighborValue.compareTo(currentValue) < 0) {
				toppled = true;
				if (greaterXNeighborValue.compareTo(currentValue) < 0) {
					if (smallerXNeighborValue.equals(greaterXNeighborValue)) {
						// gn == sn < current
						BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
						BigFraction share = toShare.divide(3);
						newSourceGrid[xMinusOne] = newSourceGrid[xMinusOne].add(share);
						newSourceGrid[x] = newSourceGrid[x].add(currentValue.subtract(toShare).add(share));
						newSourceGrid[xPlusOne] = newSourceGrid[xPlusOne].add(share);
					} else if (smallerXNeighborValue.compareTo(greaterXNeighborValue) < 0) {
						// sn < gn < current
						BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
						BigFraction share = toShare.divide(3);
						newSourceGrid[xMinusOne] = newSourceGrid[xMinusOne].add(share);
						newSourceGrid[xPlusOne] = newSourceGrid[xPlusOne].add(share);
						BigFraction currentRemainingValue = currentValue.subtract(share).subtract(share);
						toShare = currentRemainingValue.subtract(smallerXNeighborValue); 
						share = toShare.divide(2);
						newSourceGrid[xMinusOne] = newSourceGrid[xMinusOne].add(share);
						newSourceGrid[x] = newSourceGrid[x].add(currentRemainingValue.subtract(toShare).add(share));
					} else {
						// gn < sn < current
						BigFraction toShare = currentValue.subtract(smallerXNeighborValue); 
						BigFraction share = toShare.divide(3);
						newSourceGrid[xMinusOne] = newSourceGrid[xMinusOne].add(share);
						newSourceGrid[xPlusOne] = newSourceGrid[xPlusOne].add(share);
						BigFraction currentRemainingValue = currentValue.subtract(share).subtract(share);
						toShare = currentRemainingValue.subtract(greaterXNeighborValue); 
						share = toShare.divide(2);
						newSourceGrid[x] = newSourceGrid[x].add(currentRemainingValue.subtract(toShare).add(share));
						newSourceGrid[xPlusOne] = newSourceGrid[xPlusOne].add(share);
					}
				} else {
					// sn < current <= gn
					BigFraction toShare = currentValue.subtract(smallerXNeighborValue); 
					BigFraction share = toShare.divide(2);
					newSourceGrid[xMinusOne] = newSourceGrid[xMinusOne].add(share);
					newSourceGrid[x] = newSourceGrid[x].add(currentValue.subtract(toShare).add(share));
				}
			} else {
				if (greaterXNeighborValue.compareTo(currentValue) < 0) {
					toppled = true;
					// gn < current <= sn
					BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
					BigFraction share = toShare.divide(2);
					newSourceGrid[x] = newSourceGrid[x].add(currentValue.subtract(toShare).add(share));
					newSourceGrid[xPlusOne] = newSourceGrid[xPlusOne].add(share);
				} else {
					newSourceGrid[x] = newSourceGrid[x].add(currentValue);
				}
			}
			grid[x] = toppled == itsCurrentPositionsTurnToTopple;
		}
	}

	@Override
	public int getAsymmetricMaxX() {
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
	public String getSubfolderPath() {
		String path = getName() + "/1D/";
		if (!isPositive) path += "-";
		path += "infinity";
		return path + "/toppling_alternation_compliance";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		SerializableModelData data = new SerializableModelData();
		data.put(SerializableModelData.MODEL, SerializableModelData.Models.AETHER);
		data.put(SerializableModelData.INITIAL_CONFIGURATION, isPositive);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_TYPE, SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE, SerializableModelData.InitialConfigurationImplementationTypes.BOOLEAN);
		data.put(SerializableModelData.GRID, sourceGrid);
		data.put(SerializableModelData.GRID_TYPE, SerializableModelData.GridTypes.INFINITE_REGULAR);
		data.put(SerializableModelData.GRID_DIMENSION, 1);
		data.put(SerializableModelData.GRID_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BIG_FRACTION_ARRAY_1);
		data.put(SerializableModelData.COORDINATE_BOUNDS, maxX);
		data.put(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE, SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER);
		data.put(SerializableModelData.STEP, step);
		data.put(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE, grid);
		data.put(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BOOLEAN_PRIMITIVE_ARRAY_1);
		Utils.serializeToFile(data, backupPath, backupName);
	}
}
