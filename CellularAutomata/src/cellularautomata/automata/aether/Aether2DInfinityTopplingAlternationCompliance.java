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
import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.math3.fraction.BigFraction;

import cellularautomata.Utils;
import cellularautomata.model.SerializableModelData;
import cellularautomata.model2d.IsotropicSquareModelA;
import cellularautomata.model2d.SymmetricBooleanModel2D;

public class Aether2DInfinityTopplingAlternationCompliance implements SymmetricBooleanModel2D, IsotropicSquareModelA, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 712939515015572944L;

	/** A 2D array representing the grid */
	private BigFraction[][] grid;
	
	private boolean[][] topplingAlternationCompliance;
	private boolean isItEvenPositionsTurnToTopple;

	private final boolean isPositive;
	private long step;	
	private int maxX;
	
	public Aether2DInfinityTopplingAlternationCompliance(boolean isPositive) {
		this.isPositive = isPositive;
		final int side = 6;
		grid = Utils.buildAnisotropic2DBigFractionArray(side);
		grid[0][0] = isPositive? BigFraction.ONE : BigFraction.MINUS_ONE;
		isItEvenPositionsTurnToTopple = isPositive;
		maxX = 3;
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
	public Aether2DInfinityTopplingAlternationCompliance(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		SerializableModelData data = (SerializableModelData) Utils.deserializeFromFile(backupPath);
		if (!SerializableModelData.Models.AETHER.equals(data.get(SerializableModelData.MODEL))) {
			throw new IllegalArgumentException("The backup file contains a different model.");
		}
		if (!SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_TYPE))
				|| !SerializableModelData.InitialConfigurationImplementationTypes.BOOLEAN.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.GridTypes.INFINITE_SQUARE.equals(data.get(SerializableModelData.GRID_TYPE))
				|| !SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BIG_FRACTION_ARRAY_1.equals(data.get(SerializableModelData.GRID_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER.equals(data.get(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE))) {
			throw new IllegalArgumentException("The backup file's configuration is not compatible with the " + Aether2DInfinityTopplingAlternationCompliance.class + " class.");
		}
		isPositive = (boolean) data.get(SerializableModelData.INITIAL_CONFIGURATION);
		grid = (BigFraction[][]) data.get(SerializableModelData.GRID);
		maxX = (int) data.get(SerializableModelData.COORDINATE_BOUNDS);
		step = (long) data.get(SerializableModelData.STEP);
		isItEvenPositionsTurnToTopple = isPositive == (step%2 == 0);
		if (SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BOOLEAN_PRIMITIVE_ARRAY_1.equals(data.get(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE_IMPLEMENTATION_TYPE))) {
			topplingAlternationCompliance = (boolean[][]) data.get(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE);
		} else {
			nextStep();
		}
	}
	
	@Override
	public Boolean nextStep() {
		final int newSide = maxX + 3;
		BigFraction[][] newGrid = new BigFraction[newSide][];
		topplingAlternationCompliance = null;
		topplingAlternationCompliance = Utils.buildAnisotropic2DBooleanArray(newSide);
		BigFraction currentValue, greaterXNeighborValue;
		BigFraction[] smallerXSlice = null, currentXSlice = grid[0], greaterXSlice = grid[1];
		BigFraction[] newSmallerXSlice = null, newCurrentXSlice = new BigFraction[1], newGreaterXSlice = new BigFraction[2];// build new grid progressively to save memory
		boolean[] newCurrentXSliceCompliance = topplingAlternationCompliance[0];
		Arrays.fill(newCurrentXSlice, BigFraction.ZERO);
		Arrays.fill(newGreaterXSlice, BigFraction.ZERO);
		newGrid[0] = newCurrentXSlice;
		newGrid[1] = newGreaterXSlice;
		// x = 0, y = 0
		boolean isItCurrentPositionsTurnToTopple = isItEvenPositionsTurnToTopple;
		currentValue = currentXSlice[0];
		greaterXNeighborValue = greaterXSlice[0];
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			BigFraction toShare = currentValue.subtract(greaterXNeighborValue);
			BigFraction share = toShare.divide(5);
			newCurrentXSlice[0] = newCurrentXSlice[0].add(currentValue.subtract(toShare).add(share));
			newGreaterXSlice[0] = newGreaterXSlice[0].add(share);
			newCurrentXSliceCompliance[0] = isItCurrentPositionsTurnToTopple;
		} else {
			newCurrentXSlice[0] = newCurrentXSlice[0].add(currentValue);
			newCurrentXSliceCompliance[0] = !isItCurrentPositionsTurnToTopple;
		}		
		// x = 1, y = 0
		isItCurrentPositionsTurnToTopple = !isItCurrentPositionsTurnToTopple;
		// smallerXSlice = currentXSlice; // not needed here
		currentXSlice = greaterXSlice;
		greaterXSlice = grid[2];
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = new BigFraction[3];
		newCurrentXSliceCompliance = topplingAlternationCompliance[1];
		Arrays.fill(newGreaterXSlice, BigFraction.ZERO);
		newGrid[2] = newGreaterXSlice;
		BigFraction[][] newXSlices = new BigFraction[][] { newSmallerXSlice, newCurrentXSlice, newGreaterXSlice};
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		BigFraction[] relevantAsymmetricNeighborValues = new BigFraction[4];
		int[] sortedNeighborsIndexes = new int[4];
		int[][] relevantAsymmetricNeighborCoords = new int[4][2];
		int[] relevantAsymmetricNeighborShareMultipliers = new int[4];// to compensate for omitted symmetric positions
		int[] relevantAsymmetricNeighborSymmetryCounts = new int[4];// to compensate for omitted symmetric positions
		// reuse values obtained previously
		BigFraction smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterXNeighborValue = greaterXSlice[0];
		BigFraction greaterYNeighborValue = currentXSlice[1];
		boolean toppled = false;
		if (smallerXNeighborValue.compareTo(currentValue) < 0) {
			toppled = true;
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;// this is the index of the new slice: 0->newSmallerXSlice, 1->newCurrentXSlice, 2->newGreaterXSlice
			nc[1] = 0;// this is the actual y coordinate
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 4;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			toppled = true;
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue.compareTo(currentValue) < 0) {
			toppled = true;
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		topplePosition(newXSlices, currentValue, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, 
				relevantNeighborCount, relevantAsymmetricNeighborCount);
		newCurrentXSliceCompliance[0] = toppled == isItCurrentPositionsTurnToTopple;
		// x = 1, y = 1
		isItCurrentPositionsTurnToTopple = !isItCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		BigFraction smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterXNeighborValue = greaterXSlice[1];
		toppled = false;
		if (smallerYNeighborValue.compareTo(currentValue) < 0) {
			toppled = true;
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				if (smallerYNeighborValue.equals(greaterXNeighborValue)) {
					// gx = sy < current
					BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
					BigFraction share = toShare.divide(5);
					newCurrentXSlice[0] = newCurrentXSlice[0].add(share.add(share));// one more for the symmetric position at the other side
					newCurrentXSlice[1] = newCurrentXSlice[1].add(currentValue.subtract(toShare).add(share));
					newGreaterXSlice[1] = newGreaterXSlice[1].add(share);
				} else if (smallerYNeighborValue.compareTo(greaterXNeighborValue) < 0) {
					// sy < gx < current
					BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
					BigFraction share = toShare.divide(5);
					newCurrentXSlice[0] = newCurrentXSlice[0].add(share.add(share));
					newGreaterXSlice[1] = newGreaterXSlice[1].add(share);
					BigFraction currentRemainingValue = currentValue.subtract(share.multiply(4));
					toShare = currentRemainingValue.subtract(smallerYNeighborValue); 
					share = toShare.divide(3);
					newCurrentXSlice[0] = newCurrentXSlice[0].add(share.add(share));
					newCurrentXSlice[1] = newCurrentXSlice[1].add(currentRemainingValue.subtract(toShare).add(share));
				} else {
					// gx < sy < current
					BigFraction toShare = currentValue.subtract(smallerYNeighborValue); 
					BigFraction share = toShare.divide(5);
					newCurrentXSlice[0] = newCurrentXSlice[0].add(share.add(share));
					newGreaterXSlice[1] = newGreaterXSlice[1].add(share);
					BigFraction currentRemainingValue = currentValue.subtract(share.multiply(4));
					toShare = currentRemainingValue.subtract(greaterXNeighborValue); 
					share = toShare.divide(3);
					newCurrentXSlice[1] = newCurrentXSlice[1].add(currentRemainingValue.subtract(toShare).add(share));
					newGreaterXSlice[1] = newGreaterXSlice[1].add(share);
				}
			} else {
				// sy < current <= gx
				BigFraction toShare = currentValue.subtract(smallerYNeighborValue); 
				BigFraction share = toShare.divide(3);
				newCurrentXSlice[0] = newCurrentXSlice[0].add(share.add(share));
				newCurrentXSlice[1] = newCurrentXSlice[1].add(currentValue.subtract(toShare).add(share));
			}
		} else if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			toppled = true;
			// gx < current <= sy
			BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
			BigFraction share = toShare.divide(3);
			newCurrentXSlice[1] = newCurrentXSlice[1].add(currentValue.subtract(toShare).add(share));
			newGreaterXSlice[1] = newGreaterXSlice[1].add(share);
		} else {
			// gx >= current <= sy
			newCurrentXSlice[1] = newCurrentXSlice[1].add(currentValue);
		}
		newCurrentXSliceCompliance[1] = toppled == isItCurrentPositionsTurnToTopple;
		grid[0] = null;// free old grid progressively to save memory
		// x = 2, y = 0
		smallerXSlice = currentXSlice;
		currentXSlice = greaterXSlice;
		greaterXSlice = grid[3];
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = new BigFraction[4];
		newCurrentXSliceCompliance = topplingAlternationCompliance[2];
		Arrays.fill(newGreaterXSlice, BigFraction.ZERO);
		newGrid[3] = newGreaterXSlice;
		newXSlices[0] = newSmallerXSlice;
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		relevantAsymmetricNeighborCount = 0;
		relevantNeighborCount = 0;
		// reuse values obtained previously
		greaterYNeighborValue = greaterXNeighborValue;
		currentValue = currentXSlice[0];
		greaterXNeighborValue = greaterXSlice[0];
		smallerXNeighborValue = smallerXSlice[0];
		toppled = false;
		if (smallerXNeighborValue.compareTo(currentValue) < 0) {
			toppled = true;
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			toppled = true;
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue.compareTo(currentValue) < 0) {
			toppled = true;
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		topplePosition(newXSlices, currentValue, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, 
				relevantNeighborCount, relevantAsymmetricNeighborCount);
		newCurrentXSliceCompliance[0] = toppled == isItCurrentPositionsTurnToTopple;
		// x = 2, y = 1
		isItCurrentPositionsTurnToTopple = !isItCurrentPositionsTurnToTopple;
		relevantAsymmetricNeighborCount = 0;
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice[2];
		smallerXNeighborValue = smallerXSlice[1];
		greaterXNeighborValue = greaterXSlice[1];
		toppled = false;
		if (smallerXNeighborValue.compareTo(currentValue) < 0) {
			toppled = true;
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			toppled = true;
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue.compareTo(currentValue) < 0) {
			toppled = true;
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue.compareTo(currentValue) < 0) {
			toppled = true;
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborCount++;
		}
		topplePosition(newXSlices, currentValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborCount);
		newCurrentXSliceCompliance[1] = toppled == isItCurrentPositionsTurnToTopple;
		// x = 2, y = 2
		isItCurrentPositionsTurnToTopple = !isItCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterXNeighborValue = greaterXSlice[2];
		toppled = false;
		if (smallerYNeighborValue.compareTo(currentValue) < 0) {
			toppled = true;
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				if (smallerYNeighborValue.equals(greaterXNeighborValue)) {
					// gx = sy < current
					BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
					BigFraction share = toShare.divide(5);
					newCurrentXSlice[1] = newCurrentXSlice[1].add(share);
					newCurrentXSlice[2] = newCurrentXSlice[2].add(currentValue.subtract(toShare).add(share));
					newGreaterXSlice[2] = newGreaterXSlice[2].add(share);
				} else if (smallerYNeighborValue.compareTo(greaterXNeighborValue) < 0) {
					// sy < gx < current
					BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
					BigFraction share = toShare.divide(5);
					newCurrentXSlice[1] = newCurrentXSlice[1].add(share);
					newGreaterXSlice[2] = newGreaterXSlice[2].add(share);
					BigFraction currentRemainingValue = currentValue.subtract(share.multiply(4));
					toShare = currentRemainingValue.subtract(smallerYNeighborValue); 
					share = toShare.divide(3);
					newCurrentXSlice[1] = newCurrentXSlice[1].add(share);
					newCurrentXSlice[2] = newCurrentXSlice[2].add(currentRemainingValue.subtract(toShare).add(share));
				} else {
					// gx < sy < current
					BigFraction toShare = currentValue.subtract(smallerYNeighborValue); 
					BigFraction share = toShare.divide(5);
					newCurrentXSlice[1] = newCurrentXSlice[1].add(share);
					newGreaterXSlice[2] = newGreaterXSlice[2].add(share);
					BigFraction currentRemainingValue = currentValue.subtract(share.multiply(4));
					toShare = currentRemainingValue.subtract(greaterXNeighborValue); 
					share = toShare.divide(3);
					newCurrentXSlice[2] = newCurrentXSlice[2].add(currentRemainingValue.subtract(toShare).add(share));
					newGreaterXSlice[2] = newGreaterXSlice[2].add(share);
				}
			} else {
				// sy < current <= gx
				BigFraction toShare = currentValue.subtract(smallerYNeighborValue); 
				BigFraction share = toShare.divide(3);
				newCurrentXSlice[1] = newCurrentXSlice[1].add(share);
				newCurrentXSlice[2] = newCurrentXSlice[2].add(currentValue.subtract(toShare).add(share));
			}
		} else if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			toppled = true;
			// gx < current <= sy
			BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
			BigFraction share = toShare.divide(3);
			newCurrentXSlice[2] = newCurrentXSlice[2].add(currentValue.subtract(toShare).add(share));
			newGreaterXSlice[2] = newGreaterXSlice[2].add(share);
		} else {
			// gx >= current <= sy
			newCurrentXSlice[2] = newCurrentXSlice[2].add(currentValue);
		}
		newCurrentXSliceCompliance[2] = toppled == isItCurrentPositionsTurnToTopple;
		grid[1] = null;
		// 3 <= x < edge
		int edge = grid.length - 1;
		BigFraction[][] xSlices = new BigFraction[][] {null, currentXSlice, greaterXSlice};
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		toppleRangeBeyondX2(xSlices, newXSlices, newGrid, 3, edge, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, sortedNeighborsIndexes); // is it faster to reuse these arrays?
		registerStaticGridSliceCompliance(edge);
		if (newGrid.length > grid.length) {
			newGreaterXSlice = new BigFraction[newGrid.length];
			Arrays.fill(newGreaterXSlice, BigFraction.ZERO);
			newGrid[grid.length] = newGreaterXSlice;
		}
		grid = newGrid;
		maxX++;
		step++;
		isItEvenPositionsTurnToTopple = !isItEvenPositionsTurnToTopple;
		return true;
	}
	
	private void registerStaticGridSliceCompliance(int x) {
		if (x%2 == 0 == isItEvenPositionsTurnToTopple) {
			Utils.fillOddIndexes(topplingAlternationCompliance[x], true);
		} else {
			Utils.fillEvenIndexes(topplingAlternationCompliance[x], true);
		}
	}

	@Override
	public Boolean isChanged() {
		return step == 0 ? null : true;
	}
	
	private void toppleRangeBeyondX2(BigFraction[][] xSlices, BigFraction[][] newXSlices, BigFraction[][] newGrid, int minX, int maxX, 
			BigFraction[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts, int[] sortedNeighborsIndexes) {
		int x = minX, xMinusOne = x - 1, xPlusOne = x + 1, xPlusTwo = xPlusOne + 1;
		BigFraction[] smallerXSlice = null, currentXSlice = xSlices[1], greaterXSlice = xSlices[2];
		BigFraction[] newSmallerXSlice = null, newCurrentXSlice = newXSlices[1], newGreaterXSlice = newXSlices[2];
		boolean isItY0PositionsTurnToTopple = x%2 == 0 == isItEvenPositionsTurnToTopple;
		for (; x < maxX; xMinusOne = x, x = xPlusOne, xPlusOne = xPlusTwo, xPlusTwo++, isItY0PositionsTurnToTopple = !isItY0PositionsTurnToTopple) {
			// y = 0;
			boolean isItCurrentPositionsTurnToTopple = isItY0PositionsTurnToTopple;
			smallerXSlice = currentXSlice;
			currentXSlice = greaterXSlice;
			greaterXSlice = grid[xPlusOne];
			newSmallerXSlice = newCurrentXSlice;
			newCurrentXSlice = newGreaterXSlice;
			newGreaterXSlice = new BigFraction[xPlusTwo];
			boolean[] newCurrentXSliceCompliance = topplingAlternationCompliance[x];
			Arrays.fill(newGreaterXSlice, BigFraction.ZERO);
			newGrid[xPlusOne] = newGreaterXSlice;
			newXSlices[0] = newSmallerXSlice;
			newXSlices[1] = newCurrentXSlice;
			newXSlices[2] = newGreaterXSlice;
			int relevantAsymmetricNeighborCount = 0;
			int relevantNeighborCount = 0;
			BigFraction currentValue = currentXSlice[0];
			BigFraction greaterYNeighborValue = currentXSlice[1];
			BigFraction smallerXNeighborValue = smallerXSlice[0];
			BigFraction greaterXNeighborValue = greaterXSlice[0];
			boolean toppled = false;
			if (smallerXNeighborValue.compareTo(currentValue) < 0) {
				toppled = true;
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 0;
				nc[1] = 0;
				relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
				relevantNeighborCount++;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				toppled = true;
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 2;
				nc[1] = 0;
				relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
				relevantNeighborCount++;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterYNeighborValue.compareTo(currentValue) < 0) {
				toppled = true;
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 1;
				nc[1] = 1;
				relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
				relevantNeighborCount += 2;
				relevantAsymmetricNeighborCount++;
			}
			topplePosition(newXSlices, currentValue, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
			newCurrentXSliceCompliance[0] = toppled == isItCurrentPositionsTurnToTopple;
			// y = 1
			isItCurrentPositionsTurnToTopple = !isItCurrentPositionsTurnToTopple;
			relevantAsymmetricNeighborCount = 0;
			// reuse values obtained previously
			BigFraction smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterYNeighborValue = currentXSlice[2];
			smallerXNeighborValue = smallerXSlice[1];
			greaterXNeighborValue = greaterXSlice[1];
			toppled = false;
			if (smallerXNeighborValue.compareTo(currentValue) < 0) {
				toppled = true;
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 0;
				nc[1] = 1;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				toppled = true;
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 2;
				nc[1] = 1;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
				relevantAsymmetricNeighborCount++;
			}
			if (smallerYNeighborValue.compareTo(currentValue) < 0) {
				toppled = true;
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 1;
				nc[1] = 0;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterYNeighborValue.compareTo(currentValue) < 0) {
				toppled = true;
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 1;
				nc[1] = 2;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
				relevantAsymmetricNeighborCount++;
			}
			topplePosition(newXSlices, currentValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborCount);
			newCurrentXSliceCompliance[1] = toppled == isItCurrentPositionsTurnToTopple;
			// 2 >= y < x - 1
			int y = 2, yMinusOne = 1, yPlusOne = 3;
			for (; y < xMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
				isItCurrentPositionsTurnToTopple = !isItCurrentPositionsTurnToTopple;
				relevantAsymmetricNeighborCount = 0;
				// reuse values obtained previously
				smallerYNeighborValue = currentValue;
				currentValue = greaterYNeighborValue;
				greaterYNeighborValue = currentXSlice[yPlusOne];
				smallerXNeighborValue = smallerXSlice[y];
				greaterXNeighborValue = greaterXSlice[y];
				toppled = false;
				if (smallerXNeighborValue.compareTo(currentValue) < 0) {
					toppled = true;
					relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
					int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
					nc[0] = 0;
					nc[1] = y;
					relevantAsymmetricNeighborCount++;
				}
				if (greaterXNeighborValue.compareTo(currentValue) < 0) {
					toppled = true;
					relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
					int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
					nc[0] = 2;
					nc[1] = y;
					relevantAsymmetricNeighborCount++;
				}
				if (smallerYNeighborValue.compareTo(currentValue) < 0) {
					toppled = true;
					relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
					int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
					nc[0] = 1;
					nc[1] = yMinusOne;
					relevantAsymmetricNeighborCount++;
				}
				if (greaterYNeighborValue.compareTo(currentValue) < 0) {
					toppled = true;
					relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
					int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
					nc[0] = 1;
					nc[1] = yPlusOne;
					relevantAsymmetricNeighborCount++;
				}
				topplePosition(newXSlices, currentValue, y, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborCount);
				newCurrentXSliceCompliance[y] = toppled == isItCurrentPositionsTurnToTopple;
			}
			// y = x - 1
			isItCurrentPositionsTurnToTopple = !isItCurrentPositionsTurnToTopple;
			relevantAsymmetricNeighborCount = 0;
			// reuse values obtained previously
			smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterYNeighborValue = currentXSlice[yPlusOne];
			smallerXNeighborValue = smallerXSlice[y];
			greaterXNeighborValue = greaterXSlice[y];
			toppled = false;
			if (smallerXNeighborValue.compareTo(currentValue) < 0) {
				toppled = true;
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 0;
				nc[1] = y;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				toppled = true;
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 2;
				nc[1] = y;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
				relevantAsymmetricNeighborCount++;
			}
			if (smallerYNeighborValue.compareTo(currentValue) < 0) {
				toppled = true;
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 1;
				nc[1] = yMinusOne;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterYNeighborValue.compareTo(currentValue) < 0) {
				toppled = true;
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 1;
				nc[1] = yPlusOne;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
				relevantAsymmetricNeighborCount++;
			}
			topplePosition(newXSlices, currentValue, y, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborCount);
			newCurrentXSliceCompliance[y] = toppled == isItCurrentPositionsTurnToTopple;
			// y = x
			yMinusOne = y;
			y = x;
			isItCurrentPositionsTurnToTopple = !isItCurrentPositionsTurnToTopple;
			// reuse values obtained previously
			smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterXNeighborValue = greaterXSlice[y];
			toppled = false;
			if (smallerYNeighborValue.compareTo(currentValue) < 0) {
				toppled = true;
				if (greaterXNeighborValue.compareTo(currentValue) < 0) {
					if (smallerYNeighborValue.equals(greaterXNeighborValue)) {
						// gx = sy < current
						BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
						BigFraction share = toShare.divide(5);
						newCurrentXSlice[yMinusOne] = newCurrentXSlice[yMinusOne].add(share);
						newCurrentXSlice[y] = newCurrentXSlice[y].add(currentValue.subtract(toShare).add(share));
						newGreaterXSlice[y] = newGreaterXSlice[y].add(share);
					} else if (smallerYNeighborValue.compareTo(greaterXNeighborValue) < 0) {
						// sy < gx < current
						BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
						BigFraction share = toShare.divide(5);
						newCurrentXSlice[yMinusOne] = newCurrentXSlice[yMinusOne].add(share);
						newGreaterXSlice[y] = newGreaterXSlice[y].add(share);
						BigFraction currentRemainingValue = currentValue.subtract(share.multiply(4));
						toShare = currentRemainingValue.subtract(smallerYNeighborValue); 
						share = toShare.divide(3);
						newCurrentXSlice[yMinusOne] = newCurrentXSlice[yMinusOne].add(share);
						newCurrentXSlice[y] = newCurrentXSlice[y].add(currentRemainingValue.subtract(toShare).add(share));
					} else {
						// gx < sy < current
						BigFraction toShare = currentValue.subtract(smallerYNeighborValue); 
						BigFraction share = toShare.divide(5);
						newCurrentXSlice[yMinusOne] = newCurrentXSlice[yMinusOne].add(share);
						newGreaterXSlice[y] = newGreaterXSlice[y].add(share);
						BigFraction currentRemainingValue = currentValue.subtract(share.multiply(4));
						toShare = currentRemainingValue.subtract(greaterXNeighborValue); 
						share = toShare.divide(3);
						newCurrentXSlice[y] = newCurrentXSlice[y].add(currentRemainingValue.subtract(toShare).add(share));
						newGreaterXSlice[y] = newGreaterXSlice[y].add(share);
					}
				} else {
					// sy < current <= gx
					BigFraction toShare = currentValue.subtract(smallerYNeighborValue); 
					BigFraction share = toShare.divide(3);
					newCurrentXSlice[yMinusOne] = newCurrentXSlice[yMinusOne].add(share);
					newCurrentXSlice[y] = newCurrentXSlice[y].add(currentValue.subtract(toShare).add(share));
				}
			} else if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				toppled = true;
				// gx < current <= sy
				BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
				BigFraction share = toShare.divide(3);
				newCurrentXSlice[y] = newCurrentXSlice[y].add(currentValue.subtract(toShare).add(share));
				newGreaterXSlice[y] = newGreaterXSlice[y].add(share);
			} else {
				// gx >= current <= sy
				newCurrentXSlice[y] = newCurrentXSlice[y].add(currentValue);
			}
			newCurrentXSliceCompliance[y] = toppled == isItCurrentPositionsTurnToTopple;
			grid[xMinusOne] = null;
		}
		xSlices[1] = currentXSlice;
		xSlices[2] = greaterXSlice;
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
	}

	private static void topplePosition(BigFraction[][] newXSlices, BigFraction value, int y, BigFraction[] neighborValues, 
			int[] sortedNeighborsIndexes, int[][] neighborCoords, int neighborCount) {
		switch (neighborCount) {
			case 4:
				Utils.sortDescendingLength4(neighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, y, neighborValues, 
						sortedNeighborsIndexes, neighborCoords, neighborCount);
				break;
			case 3:
				Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, y, neighborValues, 
						sortedNeighborsIndexes, neighborCoords, neighborCount);
				break;
			case 2:
				BigFraction n0Val = neighborValues[0], n1Val = neighborValues[1];
				int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
				int shareCount = 3;
				if (n0Val.equals(n1Val)) {
					// n0Val = n1Val < value
					BigFraction toShare = value.subtract(n0Val); 
					BigFraction share = toShare.divide(shareCount);
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share);
					newXSlices[1][y] = newXSlices[1][y].add(value.subtract(toShare).add(share));
				} else if (n0Val.compareTo(n1Val) < 0) {
					// n0Val < n1Val < value
					BigFraction toShare = value.subtract(n1Val); 
					BigFraction share = toShare.divide(shareCount);
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share);
					shareCount = 2;
					BigFraction currentRemainingValue = value.subtract(share.multiply(2));
					toShare = currentRemainingValue.subtract(n0Val);
					share = toShare.divide(shareCount);
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share);
					newXSlices[1][y] = newXSlices[1][y].add(currentRemainingValue.subtract(toShare).add(share));
				} else {
					// n1Val < n0Val < value
					BigFraction toShare = value.subtract(n0Val); 
					BigFraction share = toShare.divide(shareCount);
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share);
					shareCount = 2;
					BigFraction currentRemainingValue = value.subtract(share.multiply(2));
					toShare = currentRemainingValue.subtract(n1Val);
					share = toShare.divide(shareCount);
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share);
					newXSlices[1][y] = newXSlices[1][y].add(currentRemainingValue.subtract(toShare).add(share));
				}
				break;
			case 1:
				BigFraction toShare = value.subtract(neighborValues[0]);
				BigFraction share = toShare.divide(2);
				value = value.subtract(toShare).add(share);
				int[] nc = neighborCoords[0];
				newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share);
				// no break
			default: // 0
				newXSlices[1][y] = newXSlices[1][y].add(value);
		}
	}
	
	
	private static void topplePositionSortedNeighbors(BigFraction[][] newXSlices, BigFraction value, int y, 
			BigFraction[] neighborValues, int[] sortedNeighborsIndexes, int[][] neighborCoords, int neighborCount) {
		int shareCount = neighborCount + 1;
		BigFraction neighborValue = neighborValues[0];
		BigFraction toShare = value.subtract(neighborValue);
		BigFraction share = toShare.divide(shareCount);
		value = value.subtract(toShare).add(share);
		for (int j = 0; j < neighborCount; j++) {
			int[] nc = neighborCoords[sortedNeighborsIndexes[j]];
			newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share);
		}
		BigFraction previousNeighborValue = neighborValue;
		shareCount--;
		for (int i = 1; i < neighborCount; i++) {
			neighborValue = neighborValues[i];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				share = toShare.divide(shareCount);
				value = value.subtract(toShare).add(share);
				for (int j = i; j < neighborCount; j++) {
					int[] nc = neighborCoords[sortedNeighborsIndexes[j]];
					newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share);
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newXSlices[1][y] = newXSlices[1][y].add(value);
	}
	
	private static void topplePosition(BigFraction[][] newXSlices, BigFraction value, int y, BigFraction[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		switch (asymmetricNeighborCount) {
			case 4:
				Utils.sortDescendingLength4(asymmetricNeighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, y, asymmetricNeighborValues, sortedNeighborsIndexes, 
						asymmetricNeighborCoords, asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
				break;
			case 3:
				Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, y, asymmetricNeighborValues, sortedNeighborsIndexes, 
						asymmetricNeighborCoords, asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
				break;
			case 2:
				BigFraction n0Val = asymmetricNeighborValues[0], n1Val = asymmetricNeighborValues[1];
				int[] n0Coords = asymmetricNeighborCoords[0], n1Coords = asymmetricNeighborCoords[1];
				int n0Mult = asymmetricNeighborShareMultipliers[0], n1Mult = asymmetricNeighborShareMultipliers[1];
				int shareCount = neighborCount + 1;
				if (n0Val.equals(n1Val)) {
					// n0Val = n1Val < value
					BigFraction toShare = value.subtract(n0Val); 
					BigFraction share = toShare.divide(shareCount);
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share.multiply(n0Mult));
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share.multiply(n1Mult));
					newXSlices[1][y] = newXSlices[1][y].add(value.subtract(toShare).add(share));
				} else if (n0Val.compareTo(n1Val) < 0) {
					// n0Val < n1Val < value
					BigFraction toShare = value.subtract(n1Val); 
					BigFraction share = toShare.divide(shareCount);
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share.multiply(n0Mult));
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share.multiply(n1Mult));
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					BigFraction currentRemainingValue = value.subtract(share.multiply(neighborCount));
					toShare = currentRemainingValue.subtract(n0Val);
					share = toShare.divide(shareCount);
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share.multiply(n0Mult));
					newXSlices[1][y] = newXSlices[1][y].add(currentRemainingValue.subtract(toShare).add(share));
				} else {
					// n1Val < n0Val < value
					BigFraction toShare = value.subtract(n0Val); 
					BigFraction share = toShare.divide(shareCount);
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share.multiply(n0Mult));
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share.multiply(n1Mult));
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					BigFraction currentRemainingValue = value.subtract(share.multiply(neighborCount));
					toShare = currentRemainingValue.subtract(n1Val);
					share = toShare.divide(shareCount);
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share.multiply(n1Mult));
					newXSlices[1][y] = newXSlices[1][y].add(currentRemainingValue.subtract(toShare).add(share));
				}				
				break;
			case 1:
				shareCount = neighborCount + 1;
				BigFraction toShare = value.subtract(asymmetricNeighborValues[0]);
				BigFraction share = toShare.divide(shareCount);
				value = value.subtract(toShare).add(share);
				int[] nc = asymmetricNeighborCoords[0];
				newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share.multiply(asymmetricNeighborShareMultipliers[0]));
				// no break
			default: // 0
				newXSlices[1][y] = newXSlices[1][y].add(value);
		}
	}
	
	
	private static void topplePositionSortedNeighbors(BigFraction[][] newXSlices, BigFraction value, int y, BigFraction[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		int shareCount = neighborCount + 1;
		BigFraction neighborValue = asymmetricNeighborValues[0];
		BigFraction toShare = value.subtract(neighborValue);
		BigFraction share = toShare.divide(shareCount);
		value = value.subtract(toShare).add(share);
		for (int j = 0; j < asymmetricNeighborCount; j++) {
			int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];
			newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share.multiply(asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]]));
		}
		BigFraction previousNeighborValue = neighborValue;
		shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[0]];
		for (int i = 1; i < asymmetricNeighborCount; i++) {
			neighborValue = asymmetricNeighborValues[i];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				share = toShare.divide(shareCount);
				value = value.subtract(toShare).add(share);
				for (int j = i; j < asymmetricNeighborCount; j++) {
					int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];
					newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share.multiply(asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]]));
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		newXSlices[1][y] = newXSlices[1][y].add(value);
	}
	
	private static void topplePosition(BigFraction[][] newXSlices, BigFraction value, int y, BigFraction[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		switch (neighborCount) {
			case 4:
				Utils.sortDescendingLength4(neighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, y, neighborValues, sortedNeighborsIndexes, 
						neighborCoords, neighborShareMultipliers, neighborCount);
				break;
			case 3:
				Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, y, neighborValues, sortedNeighborsIndexes, 
						neighborCoords, neighborShareMultipliers, neighborCount);
				break;
			case 2:
				BigFraction n0Val = neighborValues[0], n1Val = neighborValues[1];
				int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
				int n0Mult = neighborShareMultipliers[0], n1Mult = neighborShareMultipliers[1];
				int shareCount = 3;
				if (n0Val.equals(n1Val)) {
					// n0Val = n1Val < value
					BigFraction toShare = value.subtract(n0Val); 
					BigFraction share = toShare.divide(shareCount);
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share.multiply(n0Mult));
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share.multiply(n1Mult));
					newXSlices[1][y] = newXSlices[1][y].add(value.subtract(toShare).add(share));
				} else if (n0Val.compareTo(n1Val) < 0) {
					// n0Val < n1Val < value
					BigFraction toShare = value.subtract(n1Val); 
					BigFraction share = toShare.divide(shareCount);
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share.multiply(n0Mult));
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share.multiply(n1Mult));
					shareCount = 2;
					BigFraction currentRemainingValue = value.subtract(share.multiply(2));
					toShare = currentRemainingValue.subtract(n0Val);
					share = toShare.divide(shareCount);
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share.multiply(n0Mult));
					newXSlices[1][y] = newXSlices[1][y].add(currentRemainingValue.subtract(toShare).add(share));
				} else {
					// n1Val < n0Val < value
					BigFraction toShare = value.subtract(n0Val); 
					BigFraction share = toShare.divide(shareCount);
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share.multiply(n0Mult));
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share.multiply(n1Mult));
					shareCount = 2;
					BigFraction currentRemainingValue = value.subtract(share.multiply(2));
					toShare = currentRemainingValue.subtract(n1Val);
					share = toShare.divide(shareCount);
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share.multiply(n1Mult));
					newXSlices[1][y] = newXSlices[1][y].add(currentRemainingValue.subtract(toShare).add(share));
				}
				break;
			case 1:
				BigFraction toShare = value.subtract(neighborValues[0]);
				BigFraction share = toShare.divide(2);
				value = value.subtract(toShare).add(share);
				int[] nc = neighborCoords[0];
				newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share.multiply(neighborShareMultipliers[0]));
				// no break
			default: // 0
				newXSlices[1][y] = newXSlices[1][y].add(value);
		}
	}
	
	
	private static void topplePositionSortedNeighbors(BigFraction[][] newXSlices, BigFraction value, int y, BigFraction[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		int shareCount = neighborCount + 1;
		BigFraction neighborValue = neighborValues[0];
		BigFraction toShare = value.subtract(neighborValue);
		BigFraction share = toShare.divide(shareCount);
		value = value.subtract(toShare).add(share);
		for (int j = 0; j < neighborCount; j++) {
			int[] nc = neighborCoords[sortedNeighborsIndexes[j]];
			newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share.multiply(neighborShareMultipliers[sortedNeighborsIndexes[j]]));
		}
		BigFraction previousNeighborValue = neighborValue;
		shareCount--;
		for (int i = 1; i < neighborCount; i++) {
			neighborValue = neighborValues[i];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				share = toShare.divide(shareCount);
				value = value.subtract(toShare).add(share);
				for (int j = i; j < neighborCount; j++) {
					int[] nc = neighborCoords[sortedNeighborsIndexes[j]];
					newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share.multiply(neighborShareMultipliers[sortedNeighborsIndexes[j]]));
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newXSlices[1][y] = newXSlices[1][y].add(value);
	}
	
	private static void topplePosition(BigFraction[][] newXSlices, BigFraction value, int y, BigFraction[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, int neighborCount, int asymmetricNeighborCount) {
		switch (asymmetricNeighborCount) {
			case 4:
				Utils.sortDescendingLength4(asymmetricNeighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, y, asymmetricNeighborValues, sortedNeighborsIndexes, 
						asymmetricNeighborCoords, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
				break;
			case 3:
				Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, y, asymmetricNeighborValues, sortedNeighborsIndexes, 
						asymmetricNeighborCoords, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
				break;
			case 2:
				BigFraction n0Val = asymmetricNeighborValues[0], n1Val = asymmetricNeighborValues[1];
				int[] n0Coords = asymmetricNeighborCoords[0], n1Coords = asymmetricNeighborCoords[1];
				int shareCount = neighborCount + 1;
				if (n0Val.equals(n1Val)) {
					// n0Val = n1Val < value
					BigFraction toShare = value.subtract(n0Val); 
					BigFraction share = toShare.divide(shareCount);
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share);
					newXSlices[1][y] = newXSlices[1][y].add(value.subtract(toShare).add(share));
				} else if (n0Val.compareTo(n1Val) < 0) {
					// n0Val < n1Val < value
					BigFraction toShare = value.subtract(n1Val); 
					BigFraction share = toShare.divide(shareCount);
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share);
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					BigFraction currentRemainingValue = value.subtract(share.multiply(neighborCount));
					toShare = currentRemainingValue.subtract(n0Val);
					share = toShare.divide(shareCount);
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share);
					newXSlices[1][y] = newXSlices[1][y].add(currentRemainingValue.subtract(toShare).add(share));
				} else {
					// n1Val < n0Val < value
					BigFraction toShare = value.subtract(n0Val); 
					BigFraction share = toShare.divide(shareCount);
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share);
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					BigFraction currentRemainingValue = value.subtract(share.multiply(neighborCount));
					toShare = currentRemainingValue.subtract(n1Val);
					share = toShare.divide(shareCount);
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share);
					newXSlices[1][y] = newXSlices[1][y].add(currentRemainingValue.subtract(toShare).add(share));
				}
				break;
			case 1:
				shareCount = neighborCount + 1;
				BigFraction toShare = value.subtract(asymmetricNeighborValues[0]);
				BigFraction share = toShare.divide(shareCount);
				value = value.subtract(toShare).add(share);
				int[] nc = asymmetricNeighborCoords[0];
				newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share);
				// no break
			default: // 0
				newXSlices[1][y] = newXSlices[1][y].add(value);
		}
	}
	
	private static void topplePositionSortedNeighbors(BigFraction[][] newXSlices, BigFraction value, int y, BigFraction[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, int neighborCount, int asymmetricNeighborCount) {
		int shareCount = neighborCount + 1;
		BigFraction neighborValue = asymmetricNeighborValues[0];
		BigFraction toShare = value.subtract(neighborValue);
		BigFraction share = toShare.divide(shareCount);
		value = value.subtract(toShare).add(share);
		for (int j = 0; j < asymmetricNeighborCount; j++) {
			int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];
			newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share);
		}
		BigFraction previousNeighborValue = neighborValue;
		shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[0]];
		for (int i = 1; i < asymmetricNeighborCount; i++) {
			neighborValue = asymmetricNeighborValues[i];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				share = toShare.divide(shareCount);
				value = value.subtract(toShare).add(share);
				for (int j = i; j < asymmetricNeighborCount; j++) {
					int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];
					newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share);
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		newXSlices[1][y] = newXSlices[1][y].add(value);
	}
	
	@Override
	public boolean getFromPosition(int x, int y) {	
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		boolean value;
		if (y > x) {
			value = topplingAlternationCompliance[y][x];
		} else {
			value = topplingAlternationCompliance[x][y];
		}
		return value;
	}
	
	@Override
	public boolean getFromAsymmetricPosition(int x, int y) {	
		return topplingAlternationCompliance[x][y];
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
		String path = getName() + "/2D/";
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
		data.put(SerializableModelData.GRID_TYPE, SerializableModelData.GridTypes.INFINITE_SQUARE);
		data.put(SerializableModelData.GRID_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BIG_FRACTION_ARRAY_1);
		data.put(SerializableModelData.COORDINATE_BOUNDS, maxX);
		data.put(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE, SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER);
		data.put(SerializableModelData.STEP, step);
		data.put(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE, topplingAlternationCompliance);
		data.put(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BOOLEAN_PRIMITIVE_ARRAY_1);
		Utils.serializeToFile(data, backupPath, backupName);
	}
	
}