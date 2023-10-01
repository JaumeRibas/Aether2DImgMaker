/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2023 Jaume Ribas

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
import cellularautomata.model1d.IsotropicModel1DA;
import cellularautomata.model1d.SymmetricBooleanModel1D;

public class Aether1DInfinityTopplingAlternationCompliance implements SymmetricBooleanModel1D, IsotropicModel1DA {

	/** A 1D array representing the grid */
	private BigFraction[] grid;
	
	private boolean[] topplingAlternationCompliance;
	private boolean isItEvenPositionsTurnToTopple;

	private final boolean isPositive;
	private long step;
	private int maxX;
	
	public Aether1DInfinityTopplingAlternationCompliance(boolean isPositive) {
		this.isPositive = isPositive;
		final int initialSize = 5;
		grid = new BigFraction[initialSize];
		Arrays.fill(grid, BigFraction.ZERO);
		grid[0] = isPositive? BigFraction.ONE : BigFraction.MINUS_ONE;
		isItEvenPositionsTurnToTopple = isPositive;
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
	public Aether1DInfinityTopplingAlternationCompliance(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {		
		SerializableModelData data = (SerializableModelData) Utils.deserializeFromFile(backupPath);
		if (!SerializableModelData.Models.AETHER.equals(data.get(SerializableModelData.MODEL))) {
			throw new IllegalArgumentException("The backup file contains a different model.");
		}
		if (!SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_TYPE))
				|| !SerializableModelData.InitialConfigurationImplementationTypes.BOOLEAN.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.GridTypes.INFINITE_1D.equals(data.get(SerializableModelData.GRID_TYPE))
				|| !SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BIG_FRACTION_ARRAY_1.equals(data.get(SerializableModelData.GRID_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER.equals(data.get(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE))) {
			throw new IllegalArgumentException("The backup file's configuration is not compatible with the " + Aether1DInfinityTopplingAlternationCompliance.class + " class.");
		}
		isPositive = (boolean) data.get(SerializableModelData.INITIAL_CONFIGURATION);
		grid = (BigFraction[]) data.get(SerializableModelData.GRID);
		maxX = (int) data.get(SerializableModelData.COORDINATE_BOUNDS);
		step = (long) data.get(SerializableModelData.STEP);
		isItEvenPositionsTurnToTopple = isPositive == (step%2 == 0);
		if (SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BOOLEAN_PRIMITIVE_ARRAY_1.equals(data.get(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE_IMPLEMENTATION_TYPE))) {
			topplingAlternationCompliance = (boolean[]) data.get(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE);
		} else {
			nextStep();
		}
	}
	
	@Override
	public Boolean nextStep() {
		final int newSize = maxX + 3;
		BigFraction[] newGrid = new BigFraction[newSize];
		topplingAlternationCompliance = null;
		topplingAlternationCompliance = new boolean[newSize];
		Arrays.fill(newGrid, BigFraction.ZERO);
		BigFraction currentValue, greaterXNeighborValue, smallerXNeighborValue;
		//x = 0
		boolean isItCurrentPositionsTurnToTopple = isItEvenPositionsTurnToTopple;
		currentValue = grid[0];
		greaterXNeighborValue = grid[1];
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			BigFraction toShare = currentValue.subtract(greaterXNeighborValue);
			BigFraction share = toShare.divide(3);
			newGrid[0] = newGrid[0].add(currentValue.subtract(toShare).add(share));
			newGrid[1] = newGrid[1].add(share);	
			topplingAlternationCompliance[0] = isItCurrentPositionsTurnToTopple;
		} else {
			newGrid[0] = newGrid[0].add(currentValue);
			topplingAlternationCompliance[0] = !isItCurrentPositionsTurnToTopple;
		}
		//x = 1
		isItCurrentPositionsTurnToTopple = !isItCurrentPositionsTurnToTopple;
		//reuse values obtained previously
		smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterXNeighborValue = grid[2];
		boolean toppled = false;
		if (smallerXNeighborValue.compareTo(currentValue) < 0) {
			toppled = true;
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
				toppled = true;
				// gn < current <= sn
				BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
				BigFraction share = toShare.divide(2);
				newGrid[1] = newGrid[1].add(currentValue.subtract(toShare).add(share));
				newGrid[2] = newGrid[2].add(share);
			} else {
				newGrid[1] = newGrid[1].add(currentValue);
			}
		}
		topplingAlternationCompliance[1] = toppled == isItCurrentPositionsTurnToTopple;
		//2 <= x < edge
		int edge = grid.length - 1;
		toppleRangeBeyondX1(newGrid, 2, edge);
		topplingAlternationCompliance[edge] = edge%2 == 0 != isItEvenPositionsTurnToTopple;
		grid = newGrid;
		maxX++;
		step++;
		isItEvenPositionsTurnToTopple = !isItEvenPositionsTurnToTopple;
		return true;
	}

	@Override
	public Boolean isChanged() {
		return step == 0 ? null : true;
	}
	
	private void toppleRangeBeyondX1(BigFraction[] newGrid, int minX, int maxX) {
		int x = minX, xMinusOne = x - 1, xPlusOne = x + 1;
		BigFraction smallerXNeighborValue, currentValue = grid[xMinusOne], greaterXNeighborValue = grid[x];
		boolean isItCurrentPositionsTurnToTopple = x%2 == 0 == isItEvenPositionsTurnToTopple; 
		for (; x < maxX; xMinusOne = x, x = xPlusOne, xPlusOne++, isItCurrentPositionsTurnToTopple = !isItCurrentPositionsTurnToTopple) {
			//reuse values obtained previously
			smallerXNeighborValue = currentValue;
			currentValue = greaterXNeighborValue;
			greaterXNeighborValue = grid[xPlusOne];
			boolean toppled = false;
			if (smallerXNeighborValue.compareTo(currentValue) < 0) {
				toppled = true;
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
					toppled = true;
					// gn < current <= sn
					BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
					BigFraction share = toShare.divide(2);
					newGrid[x] = newGrid[x].add(currentValue.subtract(toShare).add(share));
					newGrid[xPlusOne] = newGrid[xPlusOne].add(share);
				} else {
					newGrid[x] = newGrid[x].add(currentValue);
				}
			}
			topplingAlternationCompliance[x] = toppled == isItCurrentPositionsTurnToTopple;
		}
	}
	
	@Override
	public boolean getFromPosition(int x) {	
		if (x < 0) x = -x;
		return topplingAlternationCompliance[x];
	}

	@Override
	public boolean getFromAsymmetricPosition(int x) {
		return topplingAlternationCompliance[x];
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
		data.put(SerializableModelData.GRID, grid);
		data.put(SerializableModelData.GRID_TYPE, SerializableModelData.GridTypes.INFINITE_1D);
		data.put(SerializableModelData.GRID_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BIG_FRACTION_ARRAY_1);
		data.put(SerializableModelData.COORDINATE_BOUNDS, maxX);
		data.put(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE, SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER);
		data.put(SerializableModelData.STEP, step);
		data.put(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE, topplingAlternationCompliance);
		data.put(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BOOLEAN_PRIMITIVE_ARRAY_1);
		Utils.serializeToFile(data, backupPath, backupName);
	}
}
