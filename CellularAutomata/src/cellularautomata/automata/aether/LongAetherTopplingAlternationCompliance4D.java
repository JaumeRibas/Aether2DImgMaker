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

import cellularautomata.Utils;
import cellularautomata.model.SerializableModelData;
import cellularautomata.model4d.IsotropicHypercubicBooleanArrayModel4DA;

public class LongAetherTopplingAlternationCompliance4D extends IsotropicHypercubicBooleanArrayModel4DA {
	
	public static final long MAX_INITIAL_VALUE = Long.MAX_VALUE;
	public static final long MIN_INITIAL_VALUE = -2635249153387078803L;

	/** A 4D array representing the grid */
	private long[][][][] sourceGrid;
	
	private boolean itsEvenPositionsTurnToTopple;

	private final long initialValue;
	private long step;
	private int maxW;
	private Boolean changed = null;

	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public LongAetherTopplingAlternationCompliance4D(long initialValue) {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of long type
			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
		}
		this.initialValue = initialValue;
		itsEvenPositionsTurnToTopple = initialValue >= 0;
		sourceGrid = Utils.buildAnisotropic4DLongArray(8);
		sourceGrid[0][0][0][0] = this.initialValue;
		maxW = 5;
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
	public LongAetherTopplingAlternationCompliance4D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		SerializableModelData data = (SerializableModelData) Utils.deserializeFromFile(backupPath);
		data = SerializableModelData.updateDataFormat(data);
		if (!SerializableModelData.Models.AETHER.equals(data.get(SerializableModelData.MODEL))) {
			throw new IllegalArgumentException("The backup file contains a different model.");
		}
		if (!SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_TYPE))
				|| !SerializableModelData.InitialConfigurationImplementationTypes.LONG.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.GridTypes.INFINITE_REGULAR.equals(data.get(SerializableModelData.GRID_TYPE))
				|| !Integer.valueOf(4).equals(data.get(SerializableModelData.GRID_DIMENSION))
				|| !SerializableModelData.GridImplementationTypes.ANYSOTROPIC_LONG_PRIMITIVE_ARRAY_1.equals(data.get(SerializableModelData.GRID_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER.equals(data.get(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE))
				|| !data.contains(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP)) {
			throw new IllegalArgumentException("The backup file's configuration is not compatible with this class.");
		}
		initialValue = (long) data.get(SerializableModelData.INITIAL_CONFIGURATION);
		sourceGrid = (long[][][][]) data.get(SerializableModelData.GRID);
		maxW = (int) data.get(SerializableModelData.COORDINATE_BOUNDS);
		step = (long) data.get(SerializableModelData.STEP);
		changed = (Boolean) data.get(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP);
		itsEvenPositionsTurnToTopple = initialValue >= 0 == (step%2 == 0);
		if (SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BOOLEAN_PRIMITIVE_ARRAY_1.equals(data.get(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE_IMPLEMENTATION_TYPE))) {
			grid = (boolean[][][][]) data.get(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE);
		} else {
			nextStep();
		}
	}

	@Override
	public Boolean nextStep() {
		final int newSide = maxW + 3;
		long[][][][] newSourceGrid = new long[newSide][][][];
		grid = null;
		grid = Utils.buildAnisotropic4DBooleanArray(newSide);
		boolean changed = false;
		long[][][] smallerWSlice = null, currentWSlice = sourceGrid[0], greaterWSlice = sourceGrid[1];
		long[][][] newSmallerWSlice = null, 
				newCurrentWSlice = Utils.buildAnisotropic3DLongArray(1), 
				newGreaterWSlice = Utils.buildAnisotropic3DLongArray(2);// build new grid progressively to save memory
		boolean[][][] newCurrentWSliceCompliance = grid[0]; 
		newSourceGrid[0] = newCurrentWSlice;
		newSourceGrid[1] = newGreaterWSlice;
		// w = 0, x = 0, y = 0, z = 0
		boolean itsCurrentPositionsTurnToTopple = itsEvenPositionsTurnToTopple;
		long currentValue = currentWSlice[0][0][0];
		long greaterWNeighborValue = greaterWSlice[0][0][0];
		if (topplePositionOfType1(currentValue, greaterWNeighborValue, newCurrentWSlice, newGreaterWSlice)) {
			changed = true;
			newCurrentWSliceCompliance[0][0][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[0][0][0] = !itsCurrentPositionsTurnToTopple;
		}
		//w slice transition
		// smallerWSlice = currentWSlice; // not needed here
		currentWSlice = greaterWSlice;
		greaterWSlice = sourceGrid[2];
		newSmallerWSlice = newCurrentWSlice;
		newCurrentWSlice = newGreaterWSlice;
		newGreaterWSlice = Utils.buildAnisotropic3DLongArray(3);
		newCurrentWSliceCompliance = grid[1];
		newSourceGrid[2] = newGreaterWSlice;
		long[][][][] newWSlices = new long[][][][] { newSmallerWSlice, newCurrentWSlice, newGreaterWSlice};
		long[] relevantAsymmetricNeighborValues = new long[8];
		int[] sortedNeighborsIndexes = new int[8];
		int[][] relevantAsymmetricNeighborCoords = new int[8][4];
		int[] relevantAsymmetricNeighborShareMultipliers = new int[8];// to compensate for omitted symmetric positions
		int[] relevantAsymmetricNeighborSymmetryCounts = new int[8];// to compensate for omitted symmetric positions
		// w = 1, x = 0, y = 0, z = 0
		itsCurrentPositionsTurnToTopple = !itsEvenPositionsTurnToTopple;
		// reuse values obtained previously
		long smallerWNeighborValue = currentValue;
		currentValue = greaterWNeighborValue;
		greaterWNeighborValue = greaterWSlice[0][0][0];
		long greaterXNeighborValue = currentWSlice[1][0][0];
		if (topplePositionOfType2(currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[0][0][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[0][0][0] = !itsCurrentPositionsTurnToTopple;
		}
		// w = 1, x = 1, y = 0, z = 0
		itsCurrentPositionsTurnToTopple = itsEvenPositionsTurnToTopple;
		// reuse values obtained previously
		long smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterWNeighborValue = greaterWSlice[1][0][0];
		long greaterYNeighborValue = currentWSlice[1][1][0];
		if (topplePositionOfType3(currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[1][0][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[1][0][0] = !itsCurrentPositionsTurnToTopple;
		}
		// w = 1, x = 1, y = 1, z = 0
		itsCurrentPositionsTurnToTopple = !itsEvenPositionsTurnToTopple;
		// reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice[1][1][0];
		long greaterZNeighborValue = currentWSlice[1][1][1];
		if (topplePositionOfType4(currentValue, greaterWNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[1][1][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[1][1][0] = !itsCurrentPositionsTurnToTopple;
		}
		// w = 1, x = 1, y = 1, z = 1
		itsCurrentPositionsTurnToTopple = itsEvenPositionsTurnToTopple;
		// reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[1][1][1];
		if (topplePositionOfType5(currentValue, greaterWNeighborValue, smallerZNeighborValue, newCurrentWSlice, newGreaterWSlice)) {
			changed = true;
			newCurrentWSliceCompliance[1][1][1] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[1][1][1] = !itsCurrentPositionsTurnToTopple;
		}
		//w slice transition
		sourceGrid[0] = null;// free old grid progressively to save memory
		smallerWSlice = currentWSlice;
		currentWSlice = greaterWSlice;
		greaterWSlice = sourceGrid[3];
		long[][][][] wSlices = new long[][][][] { smallerWSlice, currentWSlice, greaterWSlice};
		newSmallerWSlice = newCurrentWSlice;
		newCurrentWSlice = newGreaterWSlice;
		newGreaterWSlice = Utils.buildAnisotropic3DLongArray(4);
		newCurrentWSliceCompliance = grid[2];
		newSourceGrid[3] = newGreaterWSlice;
		newWSlices[0] = newSmallerWSlice;
		newWSlices[1] = newCurrentWSlice;
		newWSlices[2] = newGreaterWSlice;
		// w = 2, x = 0, y = 0, z = 0
		itsCurrentPositionsTurnToTopple = itsEvenPositionsTurnToTopple;
		currentValue = currentWSlice[0][0][0];
		greaterWNeighborValue = greaterWSlice[0][0][0];
		smallerWNeighborValue = smallerWSlice[0][0][0];
		greaterXNeighborValue = currentWSlice[1][0][0];
		if (topplePositionOfType6(currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[0][0][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[0][0][0] = !itsCurrentPositionsTurnToTopple;
		}
		// w = 2, x = 1, y = 0, z = 0
		itsCurrentPositionsTurnToTopple = !itsEvenPositionsTurnToTopple;
		// reuse values obtained previously
		smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterWNeighborValue = greaterWSlice[1][0][0];
		smallerWNeighborValue = smallerWSlice[1][0][0];
		greaterXNeighborValue = currentWSlice[2][0][0];
		greaterYNeighborValue = currentWSlice[1][1][0];
		if (topplePositionOfType7(1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, 
				smallerXNeighborValue, 6, greaterYNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[1][0][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[1][0][0] = !itsCurrentPositionsTurnToTopple;
		}
		// w = 2, x = 1, y = 1, z = 0
		itsCurrentPositionsTurnToTopple = itsEvenPositionsTurnToTopple;
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice[1][1][0];
		smallerWNeighborValue = smallerWSlice[1][1][0];
		greaterXNeighborValue = currentWSlice[2][1][0];
		greaterZNeighborValue = currentWSlice[1][1][1];
		if (topplePositionOfType8(1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 2, smallerYNeighborValue, 4, 
				greaterZNeighborValue, 3, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[1][1][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[1][1][0] = !itsCurrentPositionsTurnToTopple;
		}
		// w = 2, x = 1, y = 1, z = 1
		itsCurrentPositionsTurnToTopple = !itsEvenPositionsTurnToTopple;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[1][1][1];
		smallerWNeighborValue = smallerWSlice[1][1][1];
		greaterXNeighborValue = currentWSlice[2][1][1];
		if (topplePositionOfType9(1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 4, greaterXNeighborValue, 2, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[1][1][1] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[1][1][1] = !itsCurrentPositionsTurnToTopple;
		}
		// w = 2, x = 2, y = 0, z = 0
		itsCurrentPositionsTurnToTopple = itsEvenPositionsTurnToTopple;
		currentValue = currentWSlice[2][0][0];
		greaterWNeighborValue = greaterWSlice[2][0][0];
		smallerXNeighborValue = currentWSlice[1][0][0];
		greaterYNeighborValue = currentWSlice[2][1][0];
		if (topplePositionOfType10(2, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[2][0][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[2][0][0] = !itsCurrentPositionsTurnToTopple;
		}
		// w = 2, x = 2, y = 1, z = 0
		itsCurrentPositionsTurnToTopple = !itsEvenPositionsTurnToTopple;
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice[2][1][0];
		smallerXNeighborValue = currentWSlice[1][1][0];
		greaterYNeighborValue = currentWSlice[2][2][0];
		greaterZNeighborValue = currentWSlice[2][1][1];
		if (topplePositionOfType11(2, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 3, smallerYNeighborValue, 4, 
				greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[2][1][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[2][1][0] = !itsCurrentPositionsTurnToTopple;
		}
		// w = 2, x = 2, y = 1, z = 1
		itsCurrentPositionsTurnToTopple = itsEvenPositionsTurnToTopple;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[2][1][1];
		smallerXNeighborValue = currentWSlice[1][1][1];
		greaterYNeighborValue = currentWSlice[2][2][1];
		if (topplePositionOfType12(2, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 3, greaterYNeighborValue, 3, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[2][1][1] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[2][1][1] = !itsCurrentPositionsTurnToTopple;
		}
		// w = 2, x = 2, y = 2, z = 0
		itsCurrentPositionsTurnToTopple = itsEvenPositionsTurnToTopple;
		// reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;		
		currentValue = currentWSlice[2][2][0];
		greaterWNeighborValue = greaterWSlice[2][2][0];
		if (topplePositionOfType13(2, currentValue, greaterWNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[2][2][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[2][2][0] = !itsCurrentPositionsTurnToTopple;
		}
		// w = 2, x = 2, y = 2, z = 1
		itsCurrentPositionsTurnToTopple = !itsEvenPositionsTurnToTopple;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[2][2][1];
		smallerYNeighborValue = currentWSlice[2][1][1];
		greaterZNeighborValue = currentWSlice[2][2][2];
		if (topplePositionOfType14(2, 1, currentValue, greaterWNeighborValue, smallerYNeighborValue, 2, greaterZNeighborValue, 4, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[2][2][1] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[2][2][1] = !itsCurrentPositionsTurnToTopple;
		}
		// 02 | 02 | 02 | 02 | 15
		itsCurrentPositionsTurnToTopple = itsEvenPositionsTurnToTopple;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[2][2][2];
		if (topplePositionOfType15(2, currentValue, greaterWNeighborValue, smallerZNeighborValue, newCurrentWSlice, newGreaterWSlice)) {
			changed = true;
			newCurrentWSliceCompliance[2][2][2] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[2][2][2] = !itsCurrentPositionsTurnToTopple;
		}
		//w slice transition
		sourceGrid[1] = null;// free old grid progressively to save memory
		smallerWSlice = currentWSlice;
		currentWSlice = greaterWSlice;
		greaterWSlice = sourceGrid[4];
		wSlices[0] = smallerWSlice;
		wSlices[1] = currentWSlice;
		wSlices[2] = greaterWSlice;
		newSmallerWSlice = newCurrentWSlice;
		newCurrentWSlice = newGreaterWSlice;
		newGreaterWSlice = Utils.buildAnisotropic3DLongArray(5);
		newCurrentWSliceCompliance = grid[3];
		newSourceGrid[4] = newGreaterWSlice;
		newWSlices[0] = newSmallerWSlice;
		newWSlices[1] = newCurrentWSlice;
		newWSlices[2] = newGreaterWSlice;
		if (toppleRangeOfType1(wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers,
				newCurrentWSliceCompliance, false, itsEvenPositionsTurnToTopple)) {
			changed = true;
		}
		// 03 | 02 | 00 | 00 | 19
		itsCurrentPositionsTurnToTopple = !itsEvenPositionsTurnToTopple;
		currentValue = currentWSlice[2][0][0];
		greaterWNeighborValue = greaterWSlice[2][0][0];
		smallerWNeighborValue = smallerWSlice[2][0][0];
		greaterXNeighborValue = currentWSlice[3][0][0];
		smallerXNeighborValue = currentWSlice[1][0][0];
		greaterYNeighborValue = currentWSlice[2][1][0];
		if (topplePositionOfType7(2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, 
				smallerXNeighborValue, 1, greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[2][0][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[2][0][0] = !itsCurrentPositionsTurnToTopple;
		}
		// 03 | 02 | 01 | 00 | 20
		itsCurrentPositionsTurnToTopple = itsEvenPositionsTurnToTopple;
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice[2][1][0];
		smallerWNeighborValue = smallerWSlice[2][1][0];
		greaterXNeighborValue = currentWSlice[3][1][0];
		smallerXNeighborValue = currentWSlice[1][1][0];
		greaterYNeighborValue = currentWSlice[2][2][0];
		greaterZNeighborValue = currentWSlice[2][1][1];
		if (topplePositionOfType16(2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[2][1][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[2][1][0] = !itsCurrentPositionsTurnToTopple;
		}
		// 03 | 02 | 01 | 01 | 21
		itsCurrentPositionsTurnToTopple = !itsEvenPositionsTurnToTopple;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[2][1][1];
		smallerWNeighborValue = smallerWSlice[2][1][1];
		greaterXNeighborValue = currentWSlice[3][1][1];
		smallerXNeighborValue = currentWSlice[1][1][1];
		greaterYNeighborValue = currentWSlice[2][2][1];
		if (topplePositionOfType17(2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 3, greaterYNeighborValue, 2, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[2][1][1] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[2][1][1] = !itsCurrentPositionsTurnToTopple;
		}
		// 03 | 02 | 02 | 00 | 22
		itsCurrentPositionsTurnToTopple = !itsEvenPositionsTurnToTopple;
		// reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		currentValue = currentWSlice[2][2][0];
		greaterWNeighborValue = greaterWSlice[2][2][0];
		smallerWNeighborValue = smallerWSlice[2][2][0];
		greaterXNeighborValue = currentWSlice[3][2][0];
		if (topplePositionOfType8(2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[2][2][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[2][2][0] = !itsCurrentPositionsTurnToTopple;
		}
		// 03 | 02 | 02 | 01 | 23
		itsCurrentPositionsTurnToTopple = itsEvenPositionsTurnToTopple;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[2][2][1];
		smallerWNeighborValue = smallerWSlice[2][2][1];
		greaterXNeighborValue = currentWSlice[3][2][1];
		smallerYNeighborValue = currentWSlice[2][1][1];
		greaterZNeighborValue = currentWSlice[2][2][2];
		if (topplePositionOfType18(2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 3, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[2][2][1] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[2][2][1] = !itsCurrentPositionsTurnToTopple;
		}
		// 03 | 02 | 02 | 02 | 24
		itsCurrentPositionsTurnToTopple = !itsEvenPositionsTurnToTopple;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[2][2][2];
		smallerWNeighborValue = smallerWSlice[2][2][2];
		greaterXNeighborValue = currentWSlice[3][2][2];
		if (topplePositionOfType9(2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 4, greaterXNeighborValue, 2, 
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[2][2][2] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[2][2][2] = !itsCurrentPositionsTurnToTopple;
		}
		if (toppleRangeOfType2(3, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers,
				newCurrentWSliceCompliance, false, itsEvenPositionsTurnToTopple)) {
			changed = true;
		}
		// 03 | 03 | 02 | 00 | 27
		itsCurrentPositionsTurnToTopple = itsEvenPositionsTurnToTopple;
		currentValue = currentWSlice[3][2][0];
		greaterWNeighborValue = greaterWSlice[3][2][0];
		smallerXNeighborValue = currentWSlice[2][2][0];
		greaterYNeighborValue = currentWSlice[3][3][0];
		smallerYNeighborValue = currentWSlice[3][1][0];
		greaterZNeighborValue = currentWSlice[3][2][1];
		if (topplePositionOfType11(3, 2, currentValue, greaterWNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 3, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[3][2][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[3][2][0] = !itsCurrentPositionsTurnToTopple;
		}
		// 03 | 03 | 02 | 01 | 28
		itsCurrentPositionsTurnToTopple = !itsEvenPositionsTurnToTopple;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[3][2][1];
		smallerXNeighborValue = currentWSlice[2][2][1];
		greaterYNeighborValue = currentWSlice[3][3][1];
		smallerYNeighborValue = currentWSlice[3][1][1];
		greaterZNeighborValue = currentWSlice[3][2][2];
		if (topplePositionOfType19(3, 2, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 3, smallerYNeighborValue, 2, greaterZNeighborValue, 2, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[3][2][1] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[3][2][1] = !itsCurrentPositionsTurnToTopple;
		}
		// 03 | 03 | 02 | 02 | 29
		itsCurrentPositionsTurnToTopple = itsEvenPositionsTurnToTopple;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[3][2][2];
		smallerXNeighborValue = currentWSlice[2][2][2];
		greaterYNeighborValue = currentWSlice[3][3][2];
		if (topplePositionOfType12(3, 2, currentValue, greaterWNeighborValue, smallerXNeighborValue, 3, greaterYNeighborValue, 3, 
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[3][2][2] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[3][2][2] = !itsCurrentPositionsTurnToTopple;
		}
		if (toppleRangeOfType3(3, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers,
				newCurrentWSliceCompliance, false, itsEvenPositionsTurnToTopple)) {
			changed = true;
		}
		//w slice transition
		sourceGrid[2] = null;// free old grid progressively to save memory
		smallerWSlice = currentWSlice;
		currentWSlice = greaterWSlice;
		greaterWSlice = sourceGrid[5];
		wSlices[0] = smallerWSlice;
		wSlices[1] = currentWSlice;
		wSlices[2] = greaterWSlice;
		newSmallerWSlice = newCurrentWSlice;
		newCurrentWSlice = newGreaterWSlice;
		newGreaterWSlice = Utils.buildAnisotropic3DLongArray(6);
		newCurrentWSliceCompliance = grid[4];
		newSourceGrid[5] = newGreaterWSlice;
		newWSlices[0] = newSmallerWSlice;
		newWSlices[1] = newCurrentWSlice;
		newWSlices[2] = newGreaterWSlice;
		if (toppleRangeOfType4(wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers,
				newCurrentWSliceCompliance, true, itsEvenPositionsTurnToTopple)) {
			changed = true;
		}
		if (toppleRangeOfType5(3, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers,
				newCurrentWSliceCompliance, true, itsEvenPositionsTurnToTopple)) {
			changed = true;
		}
		// 04 | 03 | 02 | 00 | 40
		itsCurrentPositionsTurnToTopple = !itsEvenPositionsTurnToTopple;
		currentValue = currentWSlice[3][2][0];
		greaterWNeighborValue = greaterWSlice[3][2][0];
		smallerWNeighborValue = smallerWSlice[3][2][0];
		greaterXNeighborValue = currentWSlice[4][2][0];
		smallerXNeighborValue = currentWSlice[2][2][0];
		greaterYNeighborValue = currentWSlice[3][3][0];
		smallerYNeighborValue = currentWSlice[3][1][0];
		greaterZNeighborValue = currentWSlice[3][2][1];
		if (topplePositionOfType16(3, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
				smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[3][2][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[3][2][0] = !itsCurrentPositionsTurnToTopple;
		}
		// 04 | 03 | 02 | 01 | 41
		itsCurrentPositionsTurnToTopple = itsEvenPositionsTurnToTopple;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[3][2][1];
		smallerWNeighborValue = smallerWSlice[3][2][1];
		greaterXNeighborValue = currentWSlice[4][2][1];
		smallerXNeighborValue = currentWSlice[2][2][1];
		greaterYNeighborValue = currentWSlice[3][3][1];
		smallerYNeighborValue = currentWSlice[3][1][1];
		greaterZNeighborValue = currentWSlice[3][2][2];
		if (topplePositionOfType23(3, 2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
				smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[3][2][1] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[3][2][1] = !itsCurrentPositionsTurnToTopple;
		}
		// 04 | 03 | 02 | 02 | 42
		itsCurrentPositionsTurnToTopple = !itsEvenPositionsTurnToTopple;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[3][2][2];
		smallerWNeighborValue = smallerWSlice[3][2][2];
		greaterXNeighborValue = currentWSlice[4][2][2];
		smallerXNeighborValue = currentWSlice[2][2][2];
		greaterYNeighborValue = currentWSlice[3][3][2];
		if (topplePositionOfType17(3, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 3, greaterYNeighborValue, 2, 
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[3][2][2] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[3][2][2] = !itsCurrentPositionsTurnToTopple;
		}
		if (toppleRangeOfType6(3, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers,
				newCurrentWSliceCompliance, true, itsEvenPositionsTurnToTopple)) {
			changed = true;
		}
		if (toppleRangeOfType7(4, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers,
				newCurrentWSliceCompliance, true, itsEvenPositionsTurnToTopple)) {
			changed = true;
		}
		// 5 <= w < edge - 2
		int edge = sourceGrid.length - 1;
		int edgeMinusTwo = edge - 2;
		if (toppleRangeBeyondW4(wSlices, newWSlices, newSourceGrid, 5, edgeMinusTwo, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts)) { // is it faster to reuse these arrays?
			changed = true;
		}
		//edge - 2 <= w < edge
		if (toppleRangeBeyondW4(wSlices, newWSlices, newSourceGrid, edgeMinusTwo, edge, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts)) {
			changed = true;
			maxW++;
		}
		registerStaticGridSliceCompliance(edge);
		if (newSourceGrid.length > sourceGrid.length) {
			newSourceGrid[sourceGrid.length] = Utils.buildAnisotropic3DLongArray(newSourceGrid.length);
		}
		sourceGrid = newSourceGrid;
		step++;
		itsEvenPositionsTurnToTopple = !itsEvenPositionsTurnToTopple;
		this.changed = changed;
		return changed;
	}
	
	private void registerStaticGridSliceCompliance(int w) {
		if (w%2 == 0 == itsEvenPositionsTurnToTopple) {
			Utils.fillOddIndexes(grid[w], true);
		} else {
			Utils.fillEvenIndexes(grid[w], true);
		}
	}

	@Override
	public Boolean isChanged() {
		return changed;
	}

	private boolean toppleRangeBeyondW4(long[][][][] wSlices, long[][][][] newWSlices, long[][][][] newSourceGrid, int minW,
			int maxW, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords,
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts) {
		boolean changed = false;
		int w = minW, wMinusOne = w - 1, wMinusTwo = w - 2, wMinusThree = w - 3, wPlusOne = w + 1, wPlusTwo = w + 2;
		long[][][] smallerWSlice = null, currentWSlice = wSlices[1], greaterWSlice = wSlices[2];
		long[][][] newSmallerWSlice = null, newCurrentWSlice = newWSlices[1], newGreaterWSlice = newWSlices[2];
		boolean isWEven = w%2 == 0;
		for (; w != maxW; wMinusThree = wMinusTwo, wMinusTwo = wMinusOne, wMinusOne = w, w = wPlusOne, wPlusOne = wPlusTwo, wPlusTwo++, isWEven = !isWEven) {
			//w slice transition
			sourceGrid[wMinusTwo] = null;// free old grid progressively to save memory
			smallerWSlice = currentWSlice;
			currentWSlice = greaterWSlice;
			greaterWSlice = sourceGrid[wPlusOne];
			wSlices[0] = smallerWSlice;
			wSlices[1] = currentWSlice;
			wSlices[2] = greaterWSlice;
			newSmallerWSlice = newCurrentWSlice;
			newCurrentWSlice = newGreaterWSlice;
			newGreaterWSlice = Utils.buildAnisotropic3DLongArray(wPlusTwo);
			boolean[][][] newCurrentWSliceCompliance = grid[w];
			newSourceGrid[wPlusOne] = newGreaterWSlice;
			newWSlices[0] = newSmallerWSlice;
			newWSlices[1] = newCurrentWSlice;
			newWSlices[2] = newGreaterWSlice;
			if (toppleRangeOfType4(wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers,
					newCurrentWSliceCompliance, isWEven, itsEvenPositionsTurnToTopple)) {
				changed = true;
			}
			if (toppleRangeOfType8(3, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers,
					newCurrentWSliceCompliance, isWEven, itsEvenPositionsTurnToTopple)) {
				changed = true;
			}
			//  w | 03 | 02 | 00 | 53
			boolean itsCurrentPositionsTurnToTopple = isWEven != itsEvenPositionsTurnToTopple;
			long currentValue = currentWSlice[3][2][0];
			long greaterWNeighborValue = greaterWSlice[3][2][0];
			long smallerWNeighborValue = smallerWSlice[3][2][0];
			long greaterXNeighborValue = currentWSlice[4][2][0];
			long smallerXNeighborValue = currentWSlice[2][2][0];
			long greaterYNeighborValue = currentWSlice[3][3][0];
			long smallerYNeighborValue = currentWSlice[3][1][0];
			long greaterZNeighborValue = currentWSlice[3][2][1];
			if (topplePositionOfType16(3, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				changed = true;
				newCurrentWSliceCompliance[3][2][0] = itsCurrentPositionsTurnToTopple;
			} else {
				newCurrentWSliceCompliance[3][2][0] = !itsCurrentPositionsTurnToTopple;
			}
			//  w | 03 | 02 | 01 | 54
			itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
			// reuse values obtained previously
			long smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice[3][2][1];
			smallerWNeighborValue = smallerWSlice[3][2][1];
			greaterXNeighborValue = currentWSlice[4][2][1];
			smallerXNeighborValue = currentWSlice[2][2][1];
			greaterYNeighborValue = currentWSlice[3][3][1];
			smallerYNeighborValue = currentWSlice[3][1][1];
			greaterZNeighborValue = currentWSlice[3][2][2];
			if (topplePositionOfType23(3, 2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
					smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
				changed = true;
				newCurrentWSliceCompliance[3][2][1] = itsCurrentPositionsTurnToTopple;
			} else {
				newCurrentWSliceCompliance[3][2][1] = !itsCurrentPositionsTurnToTopple;
			}
			//  w | 03 | 02 | 02 | 55
			itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice[3][2][2];
			smallerWNeighborValue = smallerWSlice[3][2][2];
			greaterXNeighborValue = currentWSlice[4][2][2];
			smallerXNeighborValue = currentWSlice[2][2][2];
			greaterYNeighborValue = currentWSlice[3][3][2];
			if (topplePositionOfType17(3, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, greaterYNeighborValue, 2, 
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				changed = true;
				newCurrentWSliceCompliance[3][2][2] = itsCurrentPositionsTurnToTopple;
			} else {
				newCurrentWSliceCompliance[3][2][2] = !itsCurrentPositionsTurnToTopple;
			}
			if (toppleRangeOfType9(3, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers,
					newCurrentWSliceCompliance, isWEven, itsEvenPositionsTurnToTopple)) {
				changed = true;
			}
			int x = 4, xPlusOne = 5, xMinusOne = 3;
			boolean itsY2Z0PositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
			for (int xMinusTwo = 2; x != wMinusOne; xMinusTwo = xMinusOne, xMinusOne = x, x = xPlusOne, xPlusOne++, itsY2Z0PositionsTurnToTopple = !itsY2Z0PositionsTurnToTopple) {
				if (toppleRangeOfType8(x, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers,
						newCurrentWSliceCompliance, isWEven, itsEvenPositionsTurnToTopple)) {
					changed = true;
				}
				//  w |  x | 02 | 00 | 67
				itsCurrentPositionsTurnToTopple = itsY2Z0PositionsTurnToTopple;
				currentValue = currentWSlice[x][2][0];
				greaterWNeighborValue = greaterWSlice[x][2][0];
				smallerWNeighborValue = smallerWSlice[x][2][0];
				greaterXNeighborValue = currentWSlice[xPlusOne][2][0];
				smallerXNeighborValue = currentWSlice[xMinusOne][2][0];
				greaterYNeighborValue = currentWSlice[x][3][0];
				smallerYNeighborValue = currentWSlice[x][1][0];
				greaterZNeighborValue = currentWSlice[x][2][1];
				if (topplePositionOfType27(x, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
						smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
					changed = true;
					newCurrentWSliceCompliance[x][2][0] = itsCurrentPositionsTurnToTopple;
				} else {
					newCurrentWSliceCompliance[x][2][0] = !itsCurrentPositionsTurnToTopple;
				}
				//  w |  x | 02 | 01 | 68
				itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = greaterWSlice[x][2][1];
				smallerWNeighborValue = smallerWSlice[x][2][1];
				greaterXNeighborValue = currentWSlice[xPlusOne][2][1];
				smallerXNeighborValue = currentWSlice[xMinusOne][2][1];
				greaterYNeighborValue = currentWSlice[x][3][1];
				smallerYNeighborValue = currentWSlice[x][1][1];
				greaterZNeighborValue = currentWSlice[x][2][2];
				if (topplePositionOfType23(x, 2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
						smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
					changed = true;
					newCurrentWSliceCompliance[x][2][1] = itsCurrentPositionsTurnToTopple;
				} else {
					newCurrentWSliceCompliance[x][2][1] = !itsCurrentPositionsTurnToTopple;
				}
				//  w |  x | 02 | 02 | 69
				itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = greaterWSlice[x][2][2];
				smallerWNeighborValue = smallerWSlice[x][2][2];
				greaterXNeighborValue = currentWSlice[xPlusOne][2][2];
				smallerXNeighborValue = currentWSlice[xMinusOne][2][2];
				greaterYNeighborValue = currentWSlice[x][3][2];
				if (topplePositionOfType28(x, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
						smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
					changed = true;
					newCurrentWSliceCompliance[x][2][2] = itsCurrentPositionsTurnToTopple;
				} else {
					newCurrentWSliceCompliance[x][2][2] = !itsCurrentPositionsTurnToTopple;
				}
				int y = 3, yMinusOne = 2, yPlusOne = 4;
				boolean itsZ0PositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
				for (; y != xMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++, itsZ0PositionsTurnToTopple = !itsZ0PositionsTurnToTopple) {
					//  w |  x |  y | 00 | 67
					itsCurrentPositionsTurnToTopple = itsZ0PositionsTurnToTopple;
					currentValue = currentWSlice[x][y][0];
					greaterWNeighborValue = greaterWSlice[x][y][0];
					smallerWNeighborValue = smallerWSlice[x][y][0];
					greaterXNeighborValue = currentWSlice[xPlusOne][y][0];
					smallerXNeighborValue = currentWSlice[xMinusOne][y][0];
					greaterYNeighborValue = currentWSlice[x][yPlusOne][0];
					smallerYNeighborValue = currentWSlice[x][yMinusOne][0];
					greaterZNeighborValue = currentWSlice[x][y][1];
					if (topplePositionOfType27(x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
							smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
							relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
						changed = true;
						newCurrentWSliceCompliance[x][y][0] = itsCurrentPositionsTurnToTopple;
					} else {
						newCurrentWSliceCompliance[x][y][0] = !itsCurrentPositionsTurnToTopple;
					}
					//  w |  x |  y | 01 | 77
					itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
					// reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterWNeighborValue = greaterWSlice[x][y][1];
					smallerWNeighborValue = smallerWSlice[x][y][1];
					greaterXNeighborValue = currentWSlice[xPlusOne][y][1];
					smallerXNeighborValue = currentWSlice[xMinusOne][y][1];
					greaterYNeighborValue = currentWSlice[x][yPlusOne][1];
					smallerYNeighborValue = currentWSlice[x][yMinusOne][1];
					greaterZNeighborValue = currentWSlice[x][y][2];
					if (topplePositionOfType23(x, y, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
							smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
							relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
						changed = true;
						newCurrentWSliceCompliance[x][y][1] = itsCurrentPositionsTurnToTopple;
					} else {
						newCurrentWSliceCompliance[x][y][1] = !itsCurrentPositionsTurnToTopple;
					}
					int z = 2, zPlusOne = 3;
					for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
						//  w |  x |  y |  z | 81
						itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
						// reuse values obtained previously
						smallerZNeighborValue = currentValue;
						currentValue = greaterZNeighborValue;
						greaterWNeighborValue = greaterWSlice[x][y][z];
						smallerWNeighborValue = smallerWSlice[x][y][z];
						greaterXNeighborValue = currentWSlice[xPlusOne][y][z];
						smallerXNeighborValue = currentWSlice[xMinusOne][y][z];
						greaterYNeighborValue = currentWSlice[x][yPlusOne][z];
						smallerYNeighborValue = currentWSlice[x][yMinusOne][z];
						greaterZNeighborValue = currentWSlice[x][y][zPlusOne];
						if (topplePositionOfType31(x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
								smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, newWSlices)) {
							changed = true;
							newCurrentWSliceCompliance[x][y][z] = itsCurrentPositionsTurnToTopple;
						} else {
							newCurrentWSliceCompliance[x][y][z] = !itsCurrentPositionsTurnToTopple;
						}
					}
					//  w |  x |  y |  z | 78
					itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
					// reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterWNeighborValue = greaterWSlice[x][y][z];
					smallerWNeighborValue = smallerWSlice[x][y][z];
					greaterXNeighborValue = currentWSlice[xPlusOne][y][z];
					smallerXNeighborValue = currentWSlice[xMinusOne][y][z];
					greaterYNeighborValue = currentWSlice[x][yPlusOne][z];
					smallerYNeighborValue = currentWSlice[x][yMinusOne][z];
					greaterZNeighborValue = currentWSlice[x][y][zPlusOne];
					if (topplePositionOfType23(x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
							smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
							relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
						changed = true;
						newCurrentWSliceCompliance[x][y][z] = itsCurrentPositionsTurnToTopple;
					} else {
						newCurrentWSliceCompliance[x][y][z] = !itsCurrentPositionsTurnToTopple;
					}
					//  w |  x |  y |++z | 69
					z = zPlusOne;
					itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
					// reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterWNeighborValue = greaterWSlice[x][y][z];
					smallerWNeighborValue = smallerWSlice[x][y][z];
					greaterXNeighborValue = currentWSlice[xPlusOne][y][z];
					smallerXNeighborValue = currentWSlice[xMinusOne][y][z];
					greaterYNeighborValue = currentWSlice[x][yPlusOne][z];
					if (topplePositionOfType28(x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
							smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
							relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
						changed = true;
						newCurrentWSliceCompliance[x][y][z] = itsCurrentPositionsTurnToTopple;
					} else {
						newCurrentWSliceCompliance[x][y][z] = !itsCurrentPositionsTurnToTopple;
					}
				}
				//  w |  x |  y | 00 | 53
				itsCurrentPositionsTurnToTopple = itsZ0PositionsTurnToTopple;
				currentValue = currentWSlice[x][y][0];
				greaterWNeighborValue = greaterWSlice[x][y][0];
				smallerWNeighborValue = smallerWSlice[x][y][0];
				greaterXNeighborValue = currentWSlice[xPlusOne][y][0];
				smallerXNeighborValue = currentWSlice[xMinusOne][y][0];
				greaterYNeighborValue = currentWSlice[x][yPlusOne][0];
				smallerYNeighborValue = currentWSlice[x][yMinusOne][0];
				greaterZNeighborValue = currentWSlice[x][y][1];
				if (topplePositionOfType16(x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
						smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
					changed = true;
					newCurrentWSliceCompliance[x][y][0] = itsCurrentPositionsTurnToTopple;
				} else {
					newCurrentWSliceCompliance[x][y][0] = !itsCurrentPositionsTurnToTopple;
				}
				//  w |  x |  y | 01 | 70
				itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = greaterWSlice[x][y][1];
				smallerWNeighborValue = smallerWSlice[x][y][1];
				greaterXNeighborValue = currentWSlice[xPlusOne][y][1];
				smallerXNeighborValue = currentWSlice[xMinusOne][y][1];
				greaterYNeighborValue = currentWSlice[x][yPlusOne][1];
				smallerYNeighborValue = currentWSlice[x][yMinusOne][1];
				greaterZNeighborValue = currentWSlice[x][y][2];
				if (topplePositionOfType23(x, y, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
						smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
					changed = true;
					newCurrentWSliceCompliance[x][y][1] = itsCurrentPositionsTurnToTopple;
				} else {
					newCurrentWSliceCompliance[x][y][1] = !itsCurrentPositionsTurnToTopple;
				}
				int z = 2, zPlusOne = 3;
				for (; z != xMinusTwo; z = zPlusOne, zPlusOne++) {
					//  w |  x |  y |  z | 79
					itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
					// reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterWNeighborValue = greaterWSlice[x][y][z];
					smallerWNeighborValue = smallerWSlice[x][y][z];
					greaterXNeighborValue = currentWSlice[xPlusOne][y][z];
					smallerXNeighborValue = currentWSlice[xMinusOne][y][z];
					greaterYNeighborValue = currentWSlice[x][yPlusOne][z];
					smallerYNeighborValue = currentWSlice[x][yMinusOne][z];
					greaterZNeighborValue = currentWSlice[x][y][zPlusOne];
					if (topplePositionOfType23(x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
							smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
							relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
						changed = true;
						newCurrentWSliceCompliance[x][y][z] = itsCurrentPositionsTurnToTopple;
					} else {
						newCurrentWSliceCompliance[x][y][z] = !itsCurrentPositionsTurnToTopple;
					}
				}
				//  w |  x |  y |  z | 71
				itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = greaterWSlice[x][y][z];
				smallerWNeighborValue = smallerWSlice[x][y][z];
				greaterXNeighborValue = currentWSlice[xPlusOne][y][z];
				smallerXNeighborValue = currentWSlice[xMinusOne][y][z];
				greaterYNeighborValue = currentWSlice[x][yPlusOne][z];
				smallerYNeighborValue = currentWSlice[x][yMinusOne][z];
				greaterZNeighborValue = currentWSlice[x][y][zPlusOne];
				if (topplePositionOfType23(x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
						smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
					changed = true;
					newCurrentWSliceCompliance[x][y][z] = itsCurrentPositionsTurnToTopple;
				} else {
					newCurrentWSliceCompliance[x][y][z] = !itsCurrentPositionsTurnToTopple;
				}
				//  w |  x |  y |++z | 55
				z = zPlusOne;
				itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = greaterWSlice[x][y][z];
				smallerWNeighborValue = smallerWSlice[x][y][z];
				greaterXNeighborValue = currentWSlice[xPlusOne][y][z];
				smallerXNeighborValue = currentWSlice[xMinusOne][y][z];
				greaterYNeighborValue = currentWSlice[x][yPlusOne][z];
				if (topplePositionOfType17(x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, greaterYNeighborValue, 2, 
						smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
					changed = true;
					newCurrentWSliceCompliance[x][y][z] = itsCurrentPositionsTurnToTopple;
				} else {
					newCurrentWSliceCompliance[x][y][z] = !itsCurrentPositionsTurnToTopple;
				}
				if (toppleRangeOfType9(x, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers,
						newCurrentWSliceCompliance, isWEven, itsEvenPositionsTurnToTopple)) {
					changed = true;
				}
			}
			if (toppleRangeOfType5(x, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers,
					newCurrentWSliceCompliance, isWEven, itsEvenPositionsTurnToTopple)) {
				changed = true;
			}
			//  w |  x | 02 | 00 | 58
			itsCurrentPositionsTurnToTopple = itsY2Z0PositionsTurnToTopple;
			currentValue = currentWSlice[x][2][0];
			greaterWNeighborValue = greaterWSlice[x][2][0];
			smallerWNeighborValue = smallerWSlice[x][2][0];
			greaterXNeighborValue = currentWSlice[xPlusOne][2][0];
			smallerXNeighborValue = currentWSlice[xMinusOne][2][0];
			greaterYNeighborValue = currentWSlice[x][3][0];
			smallerYNeighborValue = currentWSlice[x][1][0];
			greaterZNeighborValue = currentWSlice[x][2][1];
			if (topplePositionOfType16(x, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				changed = true;
				newCurrentWSliceCompliance[x][2][0] = itsCurrentPositionsTurnToTopple;
			} else {
				newCurrentWSliceCompliance[x][2][0] = !itsCurrentPositionsTurnToTopple;
			}
			//  w |  x | 02 | 01 | 59
			itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice[x][2][1];
			smallerWNeighborValue = smallerWSlice[x][2][1];
			greaterXNeighborValue = currentWSlice[xPlusOne][2][1];
			smallerXNeighborValue = currentWSlice[xMinusOne][2][1];
			greaterYNeighborValue = currentWSlice[x][3][1];
			smallerYNeighborValue = currentWSlice[x][1][1];
			greaterZNeighborValue = currentWSlice[x][2][2];
			if (topplePositionOfType23(x, 2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
					smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
				changed = true;
				newCurrentWSliceCompliance[x][2][1] = itsCurrentPositionsTurnToTopple;
			} else {
				newCurrentWSliceCompliance[x][2][1] = !itsCurrentPositionsTurnToTopple;
			}
			//  w |  x | 02 | 02 | 60
			itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice[x][2][2];
			smallerWNeighborValue = smallerWSlice[x][2][2];
			greaterXNeighborValue = currentWSlice[xPlusOne][2][2];
			smallerXNeighborValue = currentWSlice[xMinusOne][2][2];
			greaterYNeighborValue = currentWSlice[x][3][2];
			if (topplePositionOfType17(x, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				changed = true;
				newCurrentWSliceCompliance[x][2][2] = itsCurrentPositionsTurnToTopple;
			} else {
				newCurrentWSliceCompliance[x][2][2] = !itsCurrentPositionsTurnToTopple;
			}
			int y = 3, yPlusOne = 4, yMinusOne = 2;
			boolean itsZ0PositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
			for (; y != wMinusTwo; yMinusOne = y, y = yPlusOne, yPlusOne++, itsZ0PositionsTurnToTopple = !itsZ0PositionsTurnToTopple) {
				//  w |  x |  y | 00 | 58
				itsCurrentPositionsTurnToTopple = itsZ0PositionsTurnToTopple;
				currentValue = currentWSlice[x][y][0];
				greaterWNeighborValue = greaterWSlice[x][y][0];
				smallerWNeighborValue = smallerWSlice[x][y][0];
				greaterXNeighborValue = currentWSlice[xPlusOne][y][0];
				smallerXNeighborValue = currentWSlice[xMinusOne][y][0];
				greaterYNeighborValue = currentWSlice[x][yPlusOne][0];
				smallerYNeighborValue = currentWSlice[x][yMinusOne][0];
				greaterZNeighborValue = currentWSlice[x][y][1];
				if (topplePositionOfType16(x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
						smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
					changed = true;
					newCurrentWSliceCompliance[x][y][0] = itsCurrentPositionsTurnToTopple;
				} else {
					newCurrentWSliceCompliance[x][y][0] = !itsCurrentPositionsTurnToTopple;
				}
				//  w |  x |  y | 01 | 73
				itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = greaterWSlice[x][y][1];
				smallerWNeighborValue = smallerWSlice[x][y][1];
				greaterXNeighborValue = currentWSlice[xPlusOne][y][1];
				smallerXNeighborValue = currentWSlice[xMinusOne][y][1];
				greaterYNeighborValue = currentWSlice[x][yPlusOne][1];
				smallerYNeighborValue = currentWSlice[x][yMinusOne][1];
				greaterZNeighborValue = currentWSlice[x][y][2];
				if (topplePositionOfType23(x, y, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
						smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
					changed = true;
					newCurrentWSliceCompliance[x][y][1] = itsCurrentPositionsTurnToTopple;
				} else {
					newCurrentWSliceCompliance[x][y][1] = !itsCurrentPositionsTurnToTopple;
				}
				int z = 2, zPlusOne = 3;
				for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
					//  w |  x |  y |  z | 80
					itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
					// reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterWNeighborValue = greaterWSlice[x][y][z];
					smallerWNeighborValue = smallerWSlice[x][y][z];
					greaterXNeighborValue = currentWSlice[xPlusOne][y][z];
					smallerXNeighborValue = currentWSlice[xMinusOne][y][z];
					greaterYNeighborValue = currentWSlice[x][yPlusOne][z];
					smallerYNeighborValue = currentWSlice[x][yMinusOne][z];
					greaterZNeighborValue = currentWSlice[x][y][zPlusOne];
					if (topplePositionOfType23(x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
							smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
							relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
						changed = true;
						newCurrentWSliceCompliance[x][y][z] = itsCurrentPositionsTurnToTopple;
					} else {
						newCurrentWSliceCompliance[x][y][z] = !itsCurrentPositionsTurnToTopple;
					}
				}
				//  w |  x |  y |  z | 74
				itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = greaterWSlice[x][y][z];
				smallerWNeighborValue = smallerWSlice[x][y][z];
				greaterXNeighborValue = currentWSlice[xPlusOne][y][z];
				smallerXNeighborValue = currentWSlice[xMinusOne][y][z];
				greaterYNeighborValue = currentWSlice[x][yPlusOne][z];
				smallerYNeighborValue = currentWSlice[x][yMinusOne][z];
				greaterZNeighborValue = currentWSlice[x][y][zPlusOne];
				if (topplePositionOfType23(x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
						smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
					changed = true;
					newCurrentWSliceCompliance[x][y][z] = itsCurrentPositionsTurnToTopple;
				} else {
					newCurrentWSliceCompliance[x][y][z] = !itsCurrentPositionsTurnToTopple;
				}
				//  w |  x |  y |++z | 60
				z = zPlusOne;
				itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = greaterWSlice[x][y][z];
				smallerWNeighborValue = smallerWSlice[x][y][z];
				greaterXNeighborValue = currentWSlice[xPlusOne][y][z];
				smallerXNeighborValue = currentWSlice[xMinusOne][y][z];
				greaterYNeighborValue = currentWSlice[x][yPlusOne][z];
				if (topplePositionOfType17(x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
						smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
						relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
					changed = true;
					newCurrentWSliceCompliance[x][y][z] = itsCurrentPositionsTurnToTopple;
				} else {
					newCurrentWSliceCompliance[x][y][z] = !itsCurrentPositionsTurnToTopple;
				}
			}
			//  w |  x |  y | 00 | 40
			itsCurrentPositionsTurnToTopple = itsZ0PositionsTurnToTopple;
			currentValue = currentWSlice[x][y][0];
			greaterWNeighborValue = greaterWSlice[x][y][0];
			smallerWNeighborValue = smallerWSlice[x][y][0];
			greaterXNeighborValue = currentWSlice[xPlusOne][y][0];
			smallerXNeighborValue = currentWSlice[xMinusOne][y][0];
			greaterYNeighborValue = currentWSlice[x][yPlusOne][0];
			smallerYNeighborValue = currentWSlice[x][yMinusOne][0];
			greaterZNeighborValue = currentWSlice[x][y][1];
			if (topplePositionOfType16(x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				changed = true;
				newCurrentWSliceCompliance[x][y][0] = itsCurrentPositionsTurnToTopple;
			} else {
				newCurrentWSliceCompliance[x][y][0] = !itsCurrentPositionsTurnToTopple;
			}
			//  w |  x |  y | 01 | 61
			itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice[x][y][1];
			smallerWNeighborValue = smallerWSlice[x][y][1];
			greaterXNeighborValue = currentWSlice[xPlusOne][y][1];
			smallerXNeighborValue = currentWSlice[xMinusOne][y][1];
			greaterYNeighborValue = currentWSlice[x][yPlusOne][1];
			smallerYNeighborValue = currentWSlice[x][yMinusOne][1];
			greaterZNeighborValue = currentWSlice[x][y][2];
			if (topplePositionOfType23(x, y, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
				changed = true;
				newCurrentWSliceCompliance[x][y][1] = itsCurrentPositionsTurnToTopple;
			} else {
				newCurrentWSliceCompliance[x][y][1] = !itsCurrentPositionsTurnToTopple;
			}
			int z = 2, zPlusOne = 3;
			for (; z != wMinusThree; z = zPlusOne, zPlusOne++) {
				//  w |  x |  y |  z | 75
				itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = greaterWSlice[x][y][z];
				smallerWNeighborValue = smallerWSlice[x][y][z];
				greaterXNeighborValue = currentWSlice[xPlusOne][y][z];
				smallerXNeighborValue = currentWSlice[xMinusOne][y][z];
				greaterYNeighborValue = currentWSlice[x][yPlusOne][z];
				smallerYNeighborValue = currentWSlice[x][yMinusOne][z];
				greaterZNeighborValue = currentWSlice[x][y][zPlusOne];
				if (topplePositionOfType23(x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
						smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
					changed = true;
					newCurrentWSliceCompliance[x][y][z] = itsCurrentPositionsTurnToTopple;
				} else {
					newCurrentWSliceCompliance[x][y][z] = !itsCurrentPositionsTurnToTopple;
				}
			}
			//  w |  x |  y |  z | 62
			itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice[x][y][z];
			smallerWNeighborValue = smallerWSlice[x][y][z];
			greaterXNeighborValue = currentWSlice[xPlusOne][y][z];
			smallerXNeighborValue = currentWSlice[xMinusOne][y][z];
			greaterYNeighborValue = currentWSlice[x][yPlusOne][z];
			smallerYNeighborValue = currentWSlice[x][yMinusOne][z];
			greaterZNeighborValue = currentWSlice[x][y][zPlusOne];
			if (topplePositionOfType23(x, y, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
					smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newWSlices)) {
				changed = true;
				newCurrentWSliceCompliance[x][y][z] = itsCurrentPositionsTurnToTopple;
			} else {
				newCurrentWSliceCompliance[x][y][z] = !itsCurrentPositionsTurnToTopple;
			}
			//  w |  x |  y |++z | 42
			z = zPlusOne;
			itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice[x][y][z];
			smallerWNeighborValue = smallerWSlice[x][y][z];
			greaterXNeighborValue = currentWSlice[xPlusOne][y][z];
			smallerXNeighborValue = currentWSlice[xMinusOne][y][z];
			greaterYNeighborValue = currentWSlice[x][yPlusOne][z];
			if (topplePositionOfType17(x, y, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 3, greaterYNeighborValue, 2, 
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				changed = true;
				newCurrentWSliceCompliance[x][y][z] = itsCurrentPositionsTurnToTopple;
			} else {
				newCurrentWSliceCompliance[x][y][z] = !itsCurrentPositionsTurnToTopple;
			}
			if (toppleRangeOfType6(x, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers,
					newCurrentWSliceCompliance, isWEven, itsEvenPositionsTurnToTopple)) {
				changed = true;
			}
			xMinusOne = x;
			x = xPlusOne;
			itsZ0PositionsTurnToTopple = isWEven == (x%2 == 0) != itsEvenPositionsTurnToTopple;
			for (y = 3, yMinusOne = 2, yPlusOne = 4; y != wMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++, itsZ0PositionsTurnToTopple = !itsZ0PositionsTurnToTopple) {
				//  w |  x |  y | 00 | 45
				itsCurrentPositionsTurnToTopple = itsZ0PositionsTurnToTopple;
				currentValue = currentWSlice[x][y][0];
				greaterWNeighborValue = greaterWSlice[x][y][0];
				smallerXNeighborValue = currentWSlice[xMinusOne][y][0];
				greaterYNeighborValue = currentWSlice[x][yPlusOne][0];
				smallerYNeighborValue = currentWSlice[x][yMinusOne][0];
				greaterZNeighborValue = currentWSlice[x][y][1];
				if (topplePositionOfType24(x, y, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerYNeighborValue, 
						greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords,
						relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
					changed = true;
					newCurrentWSliceCompliance[x][y][0] = itsCurrentPositionsTurnToTopple;
				} else {
					newCurrentWSliceCompliance[x][y][0] = !itsCurrentPositionsTurnToTopple;
				}
				//  w |  x |  y | 01 | 64
				itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = greaterWSlice[x][y][1];
				smallerXNeighborValue = currentWSlice[xMinusOne][y][1];
				greaterYNeighborValue = currentWSlice[x][yPlusOne][1];
				smallerYNeighborValue = currentWSlice[x][yMinusOne][1];
				greaterZNeighborValue = currentWSlice[x][y][2];
				if (topplePositionOfType19(x, y, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
						smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
						relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
					changed = true;
					newCurrentWSliceCompliance[x][y][1] = itsCurrentPositionsTurnToTopple;
				} else {
					newCurrentWSliceCompliance[x][y][1] = !itsCurrentPositionsTurnToTopple;
				}
				for (z = 2, zPlusOne = 3; z != yMinusOne; z = zPlusOne, zPlusOne++) {
					//  w |  x |  y |  z | 76
					itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
					// reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterWNeighborValue = greaterWSlice[x][y][z];
					smallerXNeighborValue = currentWSlice[xMinusOne][y][z];
					greaterYNeighborValue = currentWSlice[x][yPlusOne][z];
					smallerYNeighborValue = currentWSlice[x][yMinusOne][z];
					greaterZNeighborValue = currentWSlice[x][y][zPlusOne];
					if (topplePositionOfType30(x, y, z, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
							smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords,
							relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
						changed = true;
						newCurrentWSliceCompliance[x][y][z] = itsCurrentPositionsTurnToTopple;
					} else {
						newCurrentWSliceCompliance[x][y][z] = !itsCurrentPositionsTurnToTopple;
					}
				}
				//  w |  x |  y |  z | 65
				itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = greaterWSlice[x][y][z];
				smallerXNeighborValue = currentWSlice[xMinusOne][y][z];
				greaterYNeighborValue = currentWSlice[x][yPlusOne][z];
				smallerYNeighborValue = currentWSlice[x][yMinusOne][z];
				greaterZNeighborValue = currentWSlice[x][y][zPlusOne];
				if (topplePositionOfType19(x, y, z, currentValue, greaterWNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, 
						smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
						relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
					changed = true;
					newCurrentWSliceCompliance[x][y][z] = itsCurrentPositionsTurnToTopple;
				} else {
					newCurrentWSliceCompliance[x][y][z] = !itsCurrentPositionsTurnToTopple;
				}
				//  w |  x |  y |++z | 47
				z = zPlusOne;
				itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterWNeighborValue = greaterWSlice[x][y][z];
				smallerXNeighborValue = currentWSlice[xMinusOne][y][z];
				greaterYNeighborValue = currentWSlice[x][yPlusOne][z];
				if (topplePositionOfType25(x, y, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerZNeighborValue, 
						relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords,
						relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
					changed = true;
					newCurrentWSliceCompliance[x][y][z] = itsCurrentPositionsTurnToTopple;
				} else {
					newCurrentWSliceCompliance[x][y][z] = !itsCurrentPositionsTurnToTopple;
				}
			}
			if (toppleRangeOfType7(x, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers,
					newCurrentWSliceCompliance, isWEven, itsEvenPositionsTurnToTopple)) {
				changed = true;
			}
		}		
		wSlices[1] = currentWSlice;
		wSlices[2] = greaterWSlice;
		newWSlices[1] = newCurrentWSlice;
		newWSlices[2] = newGreaterWSlice;
		return changed;
	}

	private static boolean toppleRangeOfType1(long[][][][] wSlices, long[][][][] newWSlices, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers, 
			boolean[][][] newCurrentWSliceCompliance, boolean isWEven, boolean itsEvenPositionsTurnToTopple) {
		boolean changed = false;
		long[][][] smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];
		//  w | 00 | 00 | 00 | 06
		boolean itsCurrentPositionsTurnToTopple = isWEven == itsEvenPositionsTurnToTopple;
		long currentValue = currentWSlice[0][0][0];
		long greaterWNeighborValue = greaterWSlice[0][0][0];
		long smallerWNeighborValue = smallerWSlice[0][0][0];
		long greaterXNeighborValue = currentWSlice[1][0][0];
		if (topplePositionOfType6(currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[0][0][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[0][0][0] = !itsCurrentPositionsTurnToTopple;
		}
		//  w | 01 | 00 | 00 | 16
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		long smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterWNeighborValue = greaterWSlice[1][0][0];
		smallerWNeighborValue = smallerWSlice[1][0][0];
		greaterXNeighborValue = currentWSlice[2][0][0];
		long greaterYNeighborValue = currentWSlice[1][1][0];
		if (topplePositionOfType7(1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 6, greaterYNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[1][0][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[1][0][0] = !itsCurrentPositionsTurnToTopple;
		}
		//  w | 01 | 01 | 00 | 17
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice[1][1][0];
		smallerWNeighborValue = smallerWSlice[1][1][0];
		greaterXNeighborValue = currentWSlice[2][1][0];
		long greaterZNeighborValue = currentWSlice[1][1][1];
		if (topplePositionOfType8(1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 3, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[1][1][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[1][1][0] = !itsCurrentPositionsTurnToTopple;
		}
		//  w | 01 | 01 | 01 | 18
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[1][1][1];
		smallerWNeighborValue = smallerWSlice[1][1][1];
		greaterXNeighborValue = currentWSlice[2][1][1];
		if (topplePositionOfType9(1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[1][1][1] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[1][1][1] = !itsCurrentPositionsTurnToTopple;
		}
		return changed;
	}

	private static boolean toppleRangeOfType2(int x, long[][][][] wSlices, long[][][][] newWSlices, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers, 
			boolean[][][] newCurrentWSliceCompliance, boolean isWEven, boolean itsEvenPositionsTurnToTopple) {
		int xMinusOne = x - 1;
		boolean changed = false;
		long[][][] currentWSlice = wSlices[1], greaterWSlice = wSlices[2];		
		//  w |  x | 00 | 00 | 10
		boolean itsCurrentPositionsTurnToTopple = isWEven == (x%2 == 0) == itsEvenPositionsTurnToTopple;
		long currentValue = currentWSlice[x][0][0];
		long greaterWNeighborValue = greaterWSlice[x][0][0];
		long smallerXNeighborValue = currentWSlice[xMinusOne][0][0];
		long greaterYNeighborValue = currentWSlice[x][1][0];
		if (topplePositionOfType10(x, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[x][0][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[x][0][0] = !itsCurrentPositionsTurnToTopple;
		}
		//  w |  x | 01 | 00 | 25
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice[x][1][0];
		smallerXNeighborValue = currentWSlice[xMinusOne][1][0];
		greaterYNeighborValue = currentWSlice[x][2][0];
		long greaterZNeighborValue = currentWSlice[x][1][1];
		if (topplePositionOfType11(x, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[x][1][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[x][1][0] = !itsCurrentPositionsTurnToTopple;
		}
		//  w |  x | 01 | 01 | 26
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[x][1][1];
		smallerXNeighborValue = currentWSlice[xMinusOne][1][1];
		greaterYNeighborValue = currentWSlice[x][2][1];
		if (topplePositionOfType12(x, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[x][1][1] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[x][1][1] = !itsCurrentPositionsTurnToTopple;
		}
		return changed;
	}

	private static boolean toppleRangeOfType3(int coord, long[][][][] wSlices, long[][][][] newWSlices, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers, 
			boolean[][][] newCurrentWSliceCompliance, boolean isWEven, boolean itsEvenPositionsTurnToTopple) {
		int coordMinusOne = coord - 1;
		boolean changed = false;
		long[][][] currentWSlice = wSlices[1], greaterWSlice = wSlices[2];		
		//  w |  x |  y | 00 | 13
		boolean itsCurrentPositionsTurnToTopple = isWEven == itsEvenPositionsTurnToTopple;
		long currentValue = currentWSlice[coord][coord][0];
		long greaterWNeighborValue = greaterWSlice[coord][coord][0];
		long smallerYNeighborValue = currentWSlice[coord][coordMinusOne][0];
		long greaterZNeighborValue = currentWSlice[coord][coord][1];
		if (topplePositionOfType13(coord, currentValue, greaterWNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[coord][coord][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[coord][coord][0] = !itsCurrentPositionsTurnToTopple;
		}
		//  w |  x |  y | 01 | 30
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[coord][coord][1];
		smallerYNeighborValue = currentWSlice[coord][coordMinusOne][1];
		greaterZNeighborValue = currentWSlice[coord][coord][2];
		if (topplePositionOfType14(coord, 1, currentValue, greaterWNeighborValue, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[coord][coord][1] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[coord][coord][1] = !itsCurrentPositionsTurnToTopple;
		}
		int z = 2;
		int zPlusOne = 3;
		for (; z != coordMinusOne; z = zPlusOne, zPlusOne++) {
			//  w |  x |  y |  z | 50
			itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice[coord][coord][z];
			smallerYNeighborValue = currentWSlice[coord][coordMinusOne][z];
			greaterZNeighborValue = currentWSlice[coord][coord][zPlusOne];
			if (topplePositionOfType26(coord, z, currentValue, greaterWNeighborValue, smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				changed = true;
				newCurrentWSliceCompliance[coord][coord][z] = itsCurrentPositionsTurnToTopple;
			} else {
				newCurrentWSliceCompliance[coord][coord][z] = !itsCurrentPositionsTurnToTopple;
			}
		}
		//  w |  x |  y |  z | 31
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[coord][coord][z];
		smallerYNeighborValue = currentWSlice[coord][coordMinusOne][z];
		greaterZNeighborValue = currentWSlice[coord][coord][zPlusOne];
		if (topplePositionOfType14(coord, z, currentValue, greaterWNeighborValue, smallerYNeighborValue, 2, greaterZNeighborValue, 4, smallerZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[coord][coord][z] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[coord][coord][z] = !itsCurrentPositionsTurnToTopple;
		}
		//  w |  x |  y |++z | 15
		z = zPlusOne;
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[coord][coord][z];
		if (topplePositionOfType15(coord, currentValue, greaterWNeighborValue, smallerZNeighborValue, newWSlices[1], newWSlices[2])) {
			changed = true;
			newCurrentWSliceCompliance[coord][coord][z] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[coord][coord][z] = !itsCurrentPositionsTurnToTopple;
		}
		return changed;
	}

	private static boolean toppleRangeOfType4(long[][][][] wSlices, long[][][][] newWSlices, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers, 
			boolean[][][] newCurrentWSliceCompliance, boolean isWEven, boolean itsEvenPositionsTurnToTopple) {
		boolean changed = false;
		if (toppleRangeOfType1(wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers, newCurrentWSliceCompliance, isWEven, itsEvenPositionsTurnToTopple)) {
			changed = true;
		}
		long[][][] smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];
		//  w | 02 | 00 | 00 | 32
		boolean itsCurrentPositionsTurnToTopple = isWEven == itsEvenPositionsTurnToTopple;
		long currentValue = currentWSlice[2][0][0];
		long greaterWNeighborValue = greaterWSlice[2][0][0];
		long smallerWNeighborValue = smallerWSlice[2][0][0];
		long greaterXNeighborValue = currentWSlice[3][0][0];
		long smallerXNeighborValue = currentWSlice[1][0][0];
		long greaterYNeighborValue = currentWSlice[2][1][0];
		if (topplePositionOfType20(2, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[2][0][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[2][0][0] = !itsCurrentPositionsTurnToTopple;
		}
		//  w | 02 | 01 | 00 | 33
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice[2][1][0];
		smallerWNeighborValue = smallerWSlice[2][1][0];
		greaterXNeighborValue = currentWSlice[3][1][0];
		smallerXNeighborValue = currentWSlice[1][1][0];
		greaterYNeighborValue = currentWSlice[2][2][0];
		long greaterZNeighborValue = currentWSlice[2][1][1];
		if (topplePositionOfType16(2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[2][1][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[2][1][0] = !itsCurrentPositionsTurnToTopple;
		}
		//  w | 02 | 01 | 01 | 34
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[2][1][1];
		smallerWNeighborValue = smallerWSlice[2][1][1];
		greaterXNeighborValue = currentWSlice[3][1][1];
		smallerXNeighborValue = currentWSlice[1][1][1];
		greaterYNeighborValue = currentWSlice[2][2][1];
		if (topplePositionOfType17(2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 3, greaterYNeighborValue, 2, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[2][1][1] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[2][1][1] = !itsCurrentPositionsTurnToTopple;
		}
		//  w | 02 | 02 | 00 | 35
		// reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;		
		currentValue = currentWSlice[2][2][0];
		greaterWNeighborValue = greaterWSlice[2][2][0];
		smallerWNeighborValue = smallerWSlice[2][2][0];
		greaterXNeighborValue = currentWSlice[3][2][0];
		if (topplePositionOfType21(2, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[2][2][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[2][2][0] = !itsCurrentPositionsTurnToTopple;
		}
		//  w | 02 | 02 | 01 | 36
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[2][2][1];
		smallerWNeighborValue = smallerWSlice[2][2][1];
		greaterXNeighborValue = currentWSlice[3][2][1];
		smallerYNeighborValue = currentWSlice[2][1][1];
		greaterZNeighborValue = currentWSlice[2][2][2];
		if (topplePositionOfType18(2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 3, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[2][2][1] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[2][2][1] = !itsCurrentPositionsTurnToTopple;
		}
		//  w | 02 | 02 | 02 | 37
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[2][2][2];
		smallerWNeighborValue = smallerWSlice[2][2][2];
		greaterXNeighborValue = currentWSlice[3][2][2];
		if (topplePositionOfType22(2, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[2][2][2] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[2][2][2] = !itsCurrentPositionsTurnToTopple;
		}
		return changed;
	}

	private static boolean toppleRangeOfType5(int x, long[][][][] wSlices, long[][][][] newWSlices, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers, 
			boolean[][][] newCurrentWSliceCompliance, boolean isWEven, boolean itsEvenPositionsTurnToTopple) {
		int xMinusOne = x - 1, xPlusOne = x + 1;
		boolean changed = false;
		long[][][] smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];		
		//  w |  x | 00 | 00 | 19
		boolean itsCurrentPositionsTurnToTopple = isWEven == (x%2 == 0) == itsEvenPositionsTurnToTopple;
		long currentValue = currentWSlice[x][0][0];
		long greaterWNeighborValue = greaterWSlice[x][0][0];
		long smallerWNeighborValue = smallerWSlice[x][0][0];
		long greaterXNeighborValue = currentWSlice[xPlusOne][0][0];
		long smallerXNeighborValue = currentWSlice[xMinusOne][0][0];
		long greaterYNeighborValue = currentWSlice[x][1][0];
		if (topplePositionOfType7(x, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[x][0][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[x][0][0] = !itsCurrentPositionsTurnToTopple;
		}
		//  w |  x | 01 | 00 | 38
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice[x][1][0];
		smallerWNeighborValue = smallerWSlice[x][1][0];
		greaterXNeighborValue = currentWSlice[xPlusOne][1][0];
		smallerXNeighborValue = currentWSlice[xMinusOne][1][0];
		greaterYNeighborValue = currentWSlice[x][2][0];
		long greaterZNeighborValue = currentWSlice[x][1][1];
		if (topplePositionOfType16(x, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[x][1][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[x][1][0] = !itsCurrentPositionsTurnToTopple;
		}
		//  w |  x | 01 | 01 | 39
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[x][1][1];
		smallerWNeighborValue = smallerWSlice[x][1][1];
		greaterXNeighborValue = currentWSlice[xPlusOne][1][1];
		smallerXNeighborValue = currentWSlice[xMinusOne][1][1];
		greaterYNeighborValue = currentWSlice[x][2][1];
		if (topplePositionOfType17(x, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[x][1][1] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[x][1][1] = !itsCurrentPositionsTurnToTopple;
		}
		return changed;
	}

	private static boolean toppleRangeOfType6(int coord, long[][][][] wSlices, long[][][][] newWSlices, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers, 
			boolean[][][] newCurrentWSliceCompliance, boolean isWEven, boolean itsEvenPositionsTurnToTopple) {
		int coordMinusOne = coord - 1, coordPlusOne = coord + 1;
		long[][][] smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];	
		boolean changed = false;		
		//  w |  x |  y | 00 | 22
		boolean itsCurrentPositionsTurnToTopple = isWEven == itsEvenPositionsTurnToTopple;
		long currentValue = currentWSlice[coord][coord][0];
		long greaterWNeighborValue = greaterWSlice[coord][coord][0];
		long smallerWNeighborValue = smallerWSlice[coord][coord][0];
		long greaterXNeighborValue = currentWSlice[coordPlusOne][coord][0];
		long smallerYNeighborValue = currentWSlice[coord][coordMinusOne][0];
		long greaterZNeighborValue = currentWSlice[coord][coord][1];
		if (topplePositionOfType8(coord, currentValue, greaterWNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[coord][coord][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[coord][coord][0] = !itsCurrentPositionsTurnToTopple;
		}
		//  w |  x |  y | 01 | 43
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[coord][coord][1];
		smallerWNeighborValue = smallerWSlice[coord][coord][1];
		greaterXNeighborValue = currentWSlice[coordPlusOne][coord][1];
		smallerYNeighborValue = currentWSlice[coord][coordMinusOne][1];
		greaterZNeighborValue = currentWSlice[coord][coord][2];
		if (topplePositionOfType18(coord, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[coord][coord][1] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[coord][coord][1] = !itsCurrentPositionsTurnToTopple;
		}
		int z = 2;
		int zPlusOne = 3;
		for (; z != coordMinusOne; z = zPlusOne, zPlusOne++) {
			//  w |  x |  y |  z | 63
			itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice[coord][coord][z];
			smallerWNeighborValue = smallerWSlice[coord][coord][z];
			greaterXNeighborValue = currentWSlice[coordPlusOne][coord][z];
			smallerYNeighborValue = currentWSlice[coord][coordMinusOne][z];
			greaterZNeighborValue = currentWSlice[coord][coord][zPlusOne];
			if (topplePositionOfType18(coord, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
					relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				changed = true;
				newCurrentWSliceCompliance[coord][coord][z] = itsCurrentPositionsTurnToTopple;
			} else {
				newCurrentWSliceCompliance[coord][coord][z] = !itsCurrentPositionsTurnToTopple;
			}
		}
		//  w |  x |  y |  z | 44
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[coord][coord][z];
		smallerWNeighborValue = smallerWSlice[coord][coord][z];
		greaterXNeighborValue = currentWSlice[coordPlusOne][coord][z];
		smallerYNeighborValue = currentWSlice[coord][coordMinusOne][z];
		greaterZNeighborValue = currentWSlice[coord][coord][zPlusOne];
		if (topplePositionOfType18(coord, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 3, 
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[coord][coord][z] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[coord][coord][z] = !itsCurrentPositionsTurnToTopple;
		}
		//  w |  x |  y |++z | 24
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		z = zPlusOne;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[coord][coord][z];
		smallerWNeighborValue = smallerWSlice[coord][coord][z];
		greaterXNeighborValue = currentWSlice[coordPlusOne][coord][z];
		if (topplePositionOfType9(coord, currentValue, greaterWNeighborValue, smallerWNeighborValue, 4, greaterXNeighborValue, 2, smallerZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[coord][coord][z] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[coord][coord][z] = !itsCurrentPositionsTurnToTopple;
		}
		coordMinusOne = coord;
		coord++;
		if (toppleRangeOfType2(coord, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers, newCurrentWSliceCompliance, isWEven, itsEvenPositionsTurnToTopple)) {
			changed = true;
		}		
		//  w |  x | 02 | 00 | 45
		itsCurrentPositionsTurnToTopple = isWEven == (coord%2 == 0) == itsEvenPositionsTurnToTopple;
		currentValue = currentWSlice[coord][2][0];
		greaterWNeighborValue = greaterWSlice[coord][2][0];
		long smallerXNeighborValue = currentWSlice[coordMinusOne][2][0];
		long greaterYNeighborValue = currentWSlice[coord][3][0];
		smallerYNeighborValue = currentWSlice[coord][1][0];
		greaterZNeighborValue = currentWSlice[coord][2][1];
		if (topplePositionOfType24(coord, 2, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerYNeighborValue, 
				greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[coord][2][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[coord][2][0] = !itsCurrentPositionsTurnToTopple;
		}
		//  w |  x | 02 | 01 | 46
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[coord][2][1];
		smallerXNeighborValue = currentWSlice[coordMinusOne][2][1];
		greaterYNeighborValue = currentWSlice[coord][3][1];
		smallerYNeighborValue = currentWSlice[coord][1][1];
		greaterZNeighborValue = currentWSlice[coord][2][2];
		if (topplePositionOfType19(coord, 2, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[coord][2][1] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[coord][2][1] = !itsCurrentPositionsTurnToTopple;
		}
		//  w |  x | 02 | 02 | 47
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[coord][2][2];
		smallerXNeighborValue = currentWSlice[coordMinusOne][2][2];
		greaterYNeighborValue = currentWSlice[coord][3][2];
		if (topplePositionOfType25(coord, 2, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[coord][2][2] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[coord][2][2] = !itsCurrentPositionsTurnToTopple;
		}
		return changed;
	}

	//this range could be merged into type 6
	private static boolean toppleRangeOfType7(int x, long[][][][] wSlices, long[][][][] newWSlices, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers, 
			boolean[][][] newCurrentWSliceCompliance, boolean isWEven, boolean itsEvenPositionsTurnToTopple) {		
		int y = x - 1, xMinusOne = x - 1, xMinusTwo = x - 2, yPlusOne = y + 1, yMinusOne = y - 1;
		boolean changed = false;
		long[][][] currentWSlice = wSlices[1], greaterWSlice = wSlices[2];		
		//  w |  x |  y | 00 | 27
		boolean itsCurrentPositionsTurnToTopple = isWEven != itsEvenPositionsTurnToTopple;//if x even -> y odd -> odd, if x odd -> y even -> odd
		long currentValue = currentWSlice[x][y][0];
		long greaterWNeighborValue = greaterWSlice[x][y][0];
		long smallerXNeighborValue = currentWSlice[xMinusOne][y][0];
		long greaterYNeighborValue = currentWSlice[x][yPlusOne][0];
		long smallerYNeighborValue = currentWSlice[x][yMinusOne][0];
		long greaterZNeighborValue = currentWSlice[x][y][1];
		if (topplePositionOfType11(x, y, currentValue, greaterWNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 3, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[x][y][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[x][y][0] = !itsCurrentPositionsTurnToTopple;
		}
		//  w |  x |  y | 01 | 48
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[x][y][1];
		smallerXNeighborValue = currentWSlice[xMinusOne][y][1];
		greaterYNeighborValue = currentWSlice[x][yPlusOne][1];
		smallerYNeighborValue = currentWSlice[x][yMinusOne][1];
		greaterZNeighborValue = currentWSlice[x][y][2];
		if (topplePositionOfType19(x, y, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 3, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[x][y][1] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[x][y][1] = !itsCurrentPositionsTurnToTopple;
		}
		int z = 2;
		int zPlusOne = 3;
		for (; z != xMinusTwo; z = zPlusOne, zPlusOne++) {
			//  w |  x |  y |  z | 66
			itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice[x][y][z];
			smallerXNeighborValue = currentWSlice[xMinusOne][y][z];
			greaterYNeighborValue = currentWSlice[x][yPlusOne][z];
			smallerYNeighborValue = currentWSlice[x][yMinusOne][z];
			greaterZNeighborValue = currentWSlice[x][y][zPlusOne];
			if (topplePositionOfType19(x, y, z, currentValue, greaterWNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 3, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
					smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
					relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				changed = true;
				newCurrentWSliceCompliance[x][y][z] = itsCurrentPositionsTurnToTopple;
			} else {
				newCurrentWSliceCompliance[x][y][z] = !itsCurrentPositionsTurnToTopple;
			}
		}
		//  w |  x |  y |  z | 49
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[x][y][z];
		smallerXNeighborValue = currentWSlice[xMinusOne][y][z];
		greaterYNeighborValue = currentWSlice[x][yPlusOne][z];
		smallerYNeighborValue = currentWSlice[x][yMinusOne][z];
		greaterZNeighborValue = currentWSlice[x][y][zPlusOne];
		if (topplePositionOfType19(x, y, z, currentValue, greaterWNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 3, smallerYNeighborValue, 2, greaterZNeighborValue, 2, 
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[x][y][z] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[x][y][z] = !itsCurrentPositionsTurnToTopple;
		}
		//  w |  x |  y |++z | 29
		z = zPlusOne;
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[x][y][z];
		smallerXNeighborValue = currentWSlice[xMinusOne][y][z];
		greaterYNeighborValue = currentWSlice[x][yPlusOne][z];
		if (topplePositionOfType12(x, y, currentValue, greaterWNeighborValue, smallerXNeighborValue, 3, greaterYNeighborValue, 3, smallerZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[x][y][z] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[x][y][z] = !itsCurrentPositionsTurnToTopple;
		}
		if (toppleRangeOfType3(x, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers, newCurrentWSliceCompliance, isWEven, itsEvenPositionsTurnToTopple)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeOfType8(int x, long[][][][] wSlices, long[][][][] newWSlices, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers, 
			boolean[][][] newCurrentWSliceCompliance, boolean isWEven, boolean itsEvenPositionsTurnToTopple) {
		int xMinusOne = x - 1, xPlusOne = x + 1;
		boolean changed = false;
		long[][][] smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];		
		//  w |  x | 00 | 00 | 32
		boolean itsCurrentPositionsTurnToTopple = isWEven == (x%2 == 0) == itsEvenPositionsTurnToTopple;
		long currentValue = currentWSlice[x][0][0];
		long greaterWNeighborValue = greaterWSlice[x][0][0];
		long smallerWNeighborValue = smallerWSlice[x][0][0];
		long greaterXNeighborValue = currentWSlice[xPlusOne][0][0];
		long smallerXNeighborValue = currentWSlice[xMinusOne][0][0];
		long greaterYNeighborValue = currentWSlice[x][1][0];
		if (topplePositionOfType20(x, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, 
				greaterYNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[x][0][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[x][0][0] = !itsCurrentPositionsTurnToTopple;
		}
		//  w |  x | 01 | 00 | 51
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice[x][1][0];
		smallerWNeighborValue = smallerWSlice[x][1][0];
		greaterXNeighborValue = currentWSlice[xPlusOne][1][0];
		smallerXNeighborValue = currentWSlice[xMinusOne][1][0];
		greaterYNeighborValue = currentWSlice[x][2][0];
		long greaterZNeighborValue = currentWSlice[x][1][1];
		if (topplePositionOfType16(x, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[x][1][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[x][1][0] = !itsCurrentPositionsTurnToTopple;
		}
		//  w |  x | 01 | 01 | 52
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[x][1][1];
		smallerWNeighborValue = smallerWSlice[x][1][1];
		greaterXNeighborValue = currentWSlice[xPlusOne][1][1];
		smallerXNeighborValue = currentWSlice[xMinusOne][1][1];
		greaterYNeighborValue = currentWSlice[x][2][1];
		if (topplePositionOfType17(x, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[x][1][1] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[x][1][1] = !itsCurrentPositionsTurnToTopple;
		}
		return changed;
	}

	private static boolean toppleRangeOfType9(int coord, long[][][][] wSlices, long[][][][] newWSlices, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers, 
			boolean[][][] newCurrentWSliceCompliance, boolean isWEven, boolean itsEvenPositionsTurnToTopple) {
		int coordMinusOne = coord - 1, coordPlusOne = coord + 1;
		boolean changed = false;
		long[][][] smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];
		//  w |  x |  y | 00 | 35
		boolean itsCurrentPositionsTurnToTopple = isWEven == itsEvenPositionsTurnToTopple;
		long currentValue = currentWSlice[coord][coord][0];
		long greaterWNeighborValue = greaterWSlice[coord][coord][0];
		long smallerWNeighborValue = smallerWSlice[coord][coord][0];
		long greaterXNeighborValue = currentWSlice[coordPlusOne][coord][0];
		long smallerYNeighborValue = currentWSlice[coord][coordMinusOne][0];
		long greaterZNeighborValue = currentWSlice[coord][coord][1];
		if (topplePositionOfType21(coord, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerYNeighborValue, 
				greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[coord][coord][0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[coord][coord][0] = !itsCurrentPositionsTurnToTopple;
		}
		//  w |  x |  y | 01 | 56
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[coord][coord][1];
		smallerWNeighborValue = smallerWSlice[coord][coord][1];
		greaterXNeighborValue = currentWSlice[coordPlusOne][coord][1];
		smallerYNeighborValue = currentWSlice[coord][coordMinusOne][1];
		greaterZNeighborValue = currentWSlice[coord][coord][2];
		if (topplePositionOfType18(coord, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[coord][coord][1] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[coord][coord][1] = !itsCurrentPositionsTurnToTopple;
		}
		int z = 2;
		int zPlusOne = 3;
		for (; z != coordMinusOne; z = zPlusOne, zPlusOne++) {
			//  w |  x |  y |  z | 72
			itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice[coord][coord][z];
			smallerWNeighborValue = smallerWSlice[coord][coord][z];
			greaterXNeighborValue = currentWSlice[coordPlusOne][coord][z];
			smallerYNeighborValue = currentWSlice[coord][coordMinusOne][z];
			greaterZNeighborValue = currentWSlice[coord][coord][zPlusOne];
			if (topplePositionOfType29(coord, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
					smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords,
					relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				changed = true;
				newCurrentWSliceCompliance[coord][coord][z] = itsCurrentPositionsTurnToTopple;
			} else {
				newCurrentWSliceCompliance[coord][coord][z] = !itsCurrentPositionsTurnToTopple;
			}
		}
		//  w |  x |  y |  z | 57
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[coord][coord][z];
		smallerWNeighborValue = smallerWSlice[coord][coord][z];
		greaterXNeighborValue = currentWSlice[coordPlusOne][coord][z];
		smallerYNeighborValue = currentWSlice[coord][coordMinusOne][z];
		greaterZNeighborValue = currentWSlice[coord][coord][zPlusOne];
		if (topplePositionOfType18(coord, z, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 3, 
				smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[coord][coord][z] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[coord][coord][z] = !itsCurrentPositionsTurnToTopple;
		}
		//  w |  x |  y |++z | 37
		z = zPlusOne;
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[coord][coord][z];
		smallerWNeighborValue = smallerWSlice[coord][coord][z];
		greaterXNeighborValue = currentWSlice[coordPlusOne][coord][z];
		if (topplePositionOfType22(coord, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
			newCurrentWSliceCompliance[coord][coord][z] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentWSliceCompliance[coord][coord][z] = !itsCurrentPositionsTurnToTopple;
		}
		return changed;
	}

	private static boolean topplePositionOfType1(long currentValue, long gWValue, long[][][] newCurrentWSlice, long[][][] newGreaterWSlice) {
		boolean toppled = false;
		if (gWValue < currentValue) {
			long toShare = currentValue - gWValue;
			long share = toShare/9;
			if (share != 0) {
				toppled = true;
				newCurrentWSlice[0][0][0] += currentValue - toShare + share + toShare%9;
				newGreaterWSlice[0][0][0] += share;
			} else {
				newCurrentWSlice[0][0][0] += currentValue;
			}			
		} else {
			newCurrentWSlice[0][0][0] += currentValue;
		}
		return toppled;
	}

	private static boolean topplePositionOfType2(long currentValue, long gWValue, long sWValue, long gXValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 0;
			nc[2] = 0;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = 0;
			nc[2] = 0;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 8;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 0;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 6;
			relevantNeighborCount += 6;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, 0, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType3(long currentValue, long gWValue, long sXValue, long gYValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 1;
			nc[2] = 0;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 0;
			nc[2] = 0;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 6;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 1;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 3;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, 1, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType4(long currentValue, long gWValue, long sYValue, long gZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 1;
			nc[2] = 1;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 0;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 4;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 1;
			nc[3] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 4;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, 1, 1, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType5(long currentValue, long gWValue, long sZValue, long[][][] newCurrentWSlice, long[][][] newGreaterWSlice) {
		boolean toppled = false;
		if (sZValue < currentValue) {
			if (gWValue < currentValue) {
				if (sZValue == gWValue) {
					// gw = sz < current
					long toShare = currentValue - gWValue; 
					long share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice[1][1][0] += share + share;// one more for the symmetric position at the other side
					newCurrentWSlice[1][1][1] += currentValue - toShare + share + toShare%9;
					newGreaterWSlice[1][1][1] += share;
				} else if (sZValue < gWValue) {
					// sz < gw < current
					long toShare = currentValue - gWValue; 
					long share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice[1][1][0] += share + share;
					newGreaterWSlice[1][1][1] += share;
					long currentRemainingValue = currentValue - 8*share;
					toShare = currentRemainingValue - sZValue; 
					share = toShare/5;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice[1][1][0] += share + share;
					newCurrentWSlice[1][1][1] += currentRemainingValue - toShare + share + toShare%5;
				} else {
					// gw < sz < current
					long toShare = currentValue - sZValue; 
					long share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice[1][1][0] += share + share;
					newGreaterWSlice[1][1][1] += share;
					long currentRemainingValue = currentValue - 8*share;
					toShare = currentRemainingValue - gWValue; 
					share = toShare/5;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice[1][1][1] += currentRemainingValue - toShare + share + toShare%5;
					newGreaterWSlice[1][1][1] += share;
				}
			} else {
				// sz < current <= gw
				long toShare = currentValue - sZValue; 
				long share = toShare/5;
				if (share != 0) {
					toppled = true;
				}
				newCurrentWSlice[1][1][0] += share + share;
				newCurrentWSlice[1][1][1] += currentValue - toShare + share + toShare%5;
			}
		} else if (gWValue < currentValue) {
			// gw < current <= sz
			long toShare = currentValue - gWValue; 
			long share = toShare/5;
			if (share != 0) {
				toppled = true;
			}
			newCurrentWSlice[1][1][1] += currentValue - toShare + share + toShare%5;
			newGreaterWSlice[1][1][1] += share;
		} else {
			// gw >= current <= sz
			newCurrentWSlice[1][1][1] += currentValue;
		}
		return toppled;
	}

	private static boolean topplePositionOfType6(long currentValue, long gWValue, long sWValue, long gXValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 0;
			nc[2] = 0;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = 0;
			nc[2] = 0;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 0;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 6;
			relevantNeighborCount += 6;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, 0, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType7(int x, long currentValue, long gWValue, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = x;
			nc[2] = 0;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = x;
			nc[2] = 0;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x + 1;
			nc[2] = 0;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x - 1;
			nc[2] = 0;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = 1;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, x, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType8(int coord, long currentValue, long gWValue, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord + 1;
			nc[2] = coord;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord - 1;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, coord, coord, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType9(int coord, long currentValue, long gWValue, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord + 1;
			nc[2] = coord;
			nc[3] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, coord, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType10(int x, long currentValue, long gWValue, long sXValue, long gYValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = x;
			nc[2] = 0;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x - 1;
			nc[2] = 0;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = 1;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, x, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType11(int x, int y, long currentValue, long gWValue, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = x;
			nc[2] = y;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x - 1;
			nc[2] = y;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y + 1;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y - 1;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y;
			nc[3] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, x, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType12(int x, int coord, long currentValue, long gWValue, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = x;
			nc[2] = coord;
			nc[3] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x - 1;
			nc[2] = coord;
			nc[3] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = coord + 1;
			nc[3] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = coord;
			nc[3] = coord - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, x, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType13(int coord, long currentValue, long gWValue, long sYValue, long gZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord - 1;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, coord, coord, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType14(int coord, int z, long currentValue, long gWValue, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord - 1;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = z + 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, coord, coord, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType15(int coord, long currentValue, long gWValue, long sZValue, long[][][] newCurrentWSlice, long[][][] newGreaterWSlice) {
		// 4*GW, 4*SZ | 15 | 15
		boolean toppled = false;
		if (sZValue < currentValue) {
			if (gWValue < currentValue) {
				if (sZValue == gWValue) {
					// gw = sz < current
					long toShare = currentValue - gWValue; 
					long share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice[coord][coord][coord - 1] += share;
					newCurrentWSlice[coord][coord][coord] += currentValue - toShare + share + toShare%9;
					newGreaterWSlice[coord][coord][coord] += share;
				} else if (sZValue < gWValue) {
					// sz < gw < current
					int coordMinusOne = coord - 1;
					long toShare = currentValue - gWValue; 
					long share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice[coord][coord][coordMinusOne] += share;
					newGreaterWSlice[coord][coord][coord] += share;
					long currentRemainingValue = currentValue - 8*share;
					toShare = currentRemainingValue - sZValue; 
					share = toShare/5;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice[coord][coord][coordMinusOne] += share;
					newCurrentWSlice[coord][coord][coord] += currentRemainingValue - toShare + share + toShare%5;
				} else {
					// gw < sz < current
					long toShare = currentValue - sZValue; 
					long share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice[coord][coord][coord - 1] += share;
					newGreaterWSlice[coord][coord][coord] += share;
					long currentRemainingValue = currentValue - 8*share;
					toShare = currentRemainingValue - gWValue; 
					share = toShare/5;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice[coord][coord][coord] += currentRemainingValue - toShare + share + toShare%5;
					newGreaterWSlice[coord][coord][coord] += share;
				}
			} else {
				// sz < current <= gw
				long toShare = currentValue - sZValue; 
				long share = toShare/5;
				if (share != 0) {
					toppled = true;
				}
				newCurrentWSlice[coord][coord][coord - 1] += share;
				newCurrentWSlice[coord][coord][coord] += currentValue - toShare + share + toShare%5;
			}
		} else if (gWValue < currentValue) {
			// gw < current <= sz
			long toShare = currentValue - gWValue; 
			long share = toShare/5;
			if (share != 0) {
				toppled = true;
			}
			newCurrentWSlice[coord][coord][coord] += currentValue - toShare + share + toShare%5;
			newGreaterWSlice[coord][coord][coord] += share;
		} else {
			// gw >= current <= sz
			newCurrentWSlice[coord][coord][coord] += currentValue;
		}
		return toppled;
	}

	private static boolean topplePositionOfType16(int x, int y, long currentValue, long gWValue, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = x;
			nc[2] = y;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = x;
			nc[2] = y;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x + 1;
			nc[2] = y;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x - 1;
			nc[2] = y;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y + 1;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y - 1;
			nc[3] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y;
			nc[3] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, x, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType17(int x, int coord, long currentValue, long gWValue, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = x;
			nc[2] = coord;
			nc[3] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = x;
			nc[2] = coord;
			nc[3] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x + 1;
			nc[2] = coord;
			nc[3] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x - 1;
			nc[2] = coord;
			nc[3] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = coord + 1;
			nc[3] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = coord;
			nc[3] = coord - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, x, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType18(int coord, int z, long currentValue, long gWValue, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sWShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord + 1;
			nc[2] = coord;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord - 1;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = z + 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, coord, coord, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType19(int x, int y, int z, long currentValue, long gWValue, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x - 1;
			nc[2] = y;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y + 1;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y - 1;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z + 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, x, y, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType20(int x, long currentValue, long gWValue, long sWValue, long gXValue, long sXValue, long gYValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = x;
			nc[2] = 0;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = x;
			nc[2] = 0;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x + 1;
			nc[2] = 0;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x - 1;
			nc[2] = 0;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = 1;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, x, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType21(int coord, long currentValue, long gWValue, long sWValue, long gXValue, long sYValue, long gZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord + 1;
			nc[2] = coord;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord - 1;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, coord, coord, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType22(int coord, long currentValue, long gWValue, long sWValue, long gXValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord + 1;
			nc[2] = coord;
			nc[3] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = coord - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, coord, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType23(int x, int y, int z, long currentValue, long gWValue, long sWValue, int sWShareMultiplier, long gXValue, int gXShareMultiplier, long sXValue, int sXShareMultiplier, long gYValue, int gYShareMultiplier, long sYValue, int sYShareMultiplier, long gZValue, int gZShareMultiplier, long sZValue, int sZShareMultiplier, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, long[][][][] newWSlices) {
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 2;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = 1;
			relevantNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 0;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = sWShareMultiplier;
			relevantNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = x + 1;
			nc[2] = y;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = gXShareMultiplier;
			relevantNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = x - 1;
			nc[2] = y;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = sXShareMultiplier;
			relevantNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y + 1;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = gYShareMultiplier;
			relevantNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y - 1;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = sYShareMultiplier;
			relevantNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z + 1;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = gZShareMultiplier;
			relevantNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = sZShareMultiplier;
			relevantNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, x, y, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantNeighborCount);
	}

	private static boolean topplePositionOfType24(int x, int y, long currentValue, long gWValue, long sXValue, long gYValue, long sYValue, long gZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = x;
			nc[2] = y;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x - 1;
			nc[2] = y;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y + 1;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y - 1;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y;
			nc[3] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, x, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType25(int x, int coord, long currentValue, long gWValue, long sXValue, long gYValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = x;
			nc[2] = coord;
			nc[3] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x - 1;
			nc[2] = coord;
			nc[3] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = coord + 1;
			nc[3] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = coord;
			nc[3] = coord - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, x, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType26(int coord, int z, long currentValue, long gWValue, long sYValue, long gZValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord - 1;
			nc[3] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 3;
			relevantNeighborCount += 3;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = z + 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = z - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, coord, coord, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType27(int x, int y, long currentValue, long gWValue, long sWValue, long gXValue, long sXValue, long gYValue, long sYValue, long gZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = x;
			nc[2] = y;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = x;
			nc[2] = y;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x + 1;
			nc[2] = y;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x - 1;
			nc[2] = y;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y + 1;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y - 1;
			nc[3] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y;
			nc[3] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, x, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType28(int x, int coord, long currentValue, long gWValue, long sWValue, long gXValue, long sXValue, long gYValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = x;
			nc[2] = coord;
			nc[3] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = x;
			nc[2] = coord;
			nc[3] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x + 1;
			nc[2] = coord;
			nc[3] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x - 1;
			nc[2] = coord;
			nc[3] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = coord + 1;
			nc[3] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = coord;
			nc[3] = coord - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, x, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType29(int coord, int z, long currentValue, long gWValue, long sWValue, long gXValue, long sYValue, long gZValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord + 1;
			nc[2] = coord;
			nc[3] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord - 1;
			nc[3] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = z + 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord;
			nc[3] = z - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, coord, coord, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType30(int x, int y, int z, long currentValue, long gWValue, long sXValue, long gYValue, long sYValue, long gZValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x - 1;
			nc[2] = y;
			nc[3] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y + 1;
			nc[3] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y - 1;
			nc[3] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z + 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount += 1;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, x, y, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType31(int x, int y, int z, long currentValue, long gWValue, long sWValue, long gXValue, long sXValue, long gYValue, long sYValue, long gZValue, long sZValue, long[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, long[][][][] newWSlices) {
		int relevantNeighborCount = 0;
		if (gWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 2;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z;
			relevantNeighborCount++;
		}
		if (sWValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 0;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z;
			relevantNeighborCount++;
		}
		if (gXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = x + 1;
			nc[2] = y;
			nc[3] = z;
			relevantNeighborCount++;
		}
		if (sXValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = x - 1;
			nc[2] = y;
			nc[3] = z;
			relevantNeighborCount++;
		}
		if (gYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y + 1;
			nc[3] = z;
			relevantNeighborCount++;
		}
		if (sYValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y - 1;
			nc[3] = z;
			relevantNeighborCount++;
		}
		if (gZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z + 1;
			relevantNeighborCount++;
		}
		if (sZValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z - 1;
			relevantNeighborCount++;
		}
		return topplePosition(newWSlices, currentValue, x, y, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantNeighborCount);
	}

	private static boolean topplePosition(long[][][][] newWSlices, long value, int x, int y, int z, long[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int neighborCount) {
		boolean toppled = false;
		switch (neighborCount) {
		case 3:
			Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, neighborValues, sortedNeighborsIndexes, 
					neighborCoords, 3);
			break;
		case 2:
			long n0Val = neighborValues[0], n1Val = neighborValues[1];
			int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
			if (n0Val == n1Val) {
				// n0Val = n1Val < value
				long toShare = value - n0Val; 
				long share = toShare/3;
				if (share != 0) {
					toppled = true;
				}
				newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share;
				newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share;
				newWSlices[1][x][y][z] += value - toShare + share + toShare%3;
			} else if (n0Val < n1Val) {
				// n0Val < n1Val < value
				long toShare = value - n1Val; 
				long share = toShare/3;
				if (share != 0) {
					toppled = true;
				}
				newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share;
				newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share;
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n0Val;
				share = toShare/2;
				if (share != 0) {
					toppled = true;
				}
				newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share;
				newWSlices[1][x][y][z] += currentRemainingValue - toShare + share + toShare%2;
			} else {
				// n1Val < n0Val < value
				long toShare = value - n0Val; 
				long share = toShare/3;
				if (share != 0) {
					toppled = true;
				}
				newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share;
				newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share;
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n1Val;
				share = toShare/2;
				if (share != 0) {
					toppled = true;
				}
				newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share;
				newWSlices[1][x][y][z] += currentRemainingValue - toShare + share + toShare%2;
			}				
			break;
		case 1:
			long toShare = value - neighborValues[0];
			long share = toShare/2;
			if (share != 0) {
				toppled = true;
				value = value - toShare + toShare%2 + share;
				int[] nc = neighborCoords[0];
				newWSlices[nc[0]][nc[1]][nc[2]][nc[3]] += share;
			}
			// no break
		case 0:
			newWSlices[1][x][y][z] += value;
			break;
		default: // 8, 7, 6, 5, 4
			Utils.sortDescending(neighborCount, neighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, neighborValues, sortedNeighborsIndexes, neighborCoords, 
					neighborCount);
		}
		return toppled;
	}

	private static boolean topplePositionSortedNeighbors(long[][][][] newWSlices, long value, int x, int y, int z, 
			long[] neighborValues, int[] sortedNeighborsIndexes, int[][] neighborCoords, int neighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		long neighborValue = neighborValues[0];
		long toShare = value - neighborValue;
		long share = toShare/shareCount;
		if (share != 0) {
			toppled = true;
			value = value - toShare + toShare%shareCount + share;
			for (int j = 0; j < neighborCount; j++) {
				int[] nc = neighborCoords[sortedNeighborsIndexes[j]];
				newWSlices[nc[0]][nc[1]][nc[2]][nc[3]] += share;
			}
		}
		long previousNeighborValue = neighborValue;
		shareCount--;
		for (int i = 1; i < neighborCount; i++) {
			neighborValue = neighborValues[i];
			if (neighborValue != previousNeighborValue) {
				toShare = value - neighborValue;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%shareCount + share;
					for (int j = i; j < neighborCount; j++) {
						int[] nc = neighborCoords[sortedNeighborsIndexes[j]];
						newWSlices[nc[0]][nc[1]][nc[2]][nc[3]] += share;
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newWSlices[1][x][y][z] += value;
		return toppled;
	}

	private static boolean topplePosition(long[][][][] newWSlices, long value, int x, int y, int z, long[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
		case 3:
			Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, 
					asymmetricNeighborCoords, asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
			break;
		case 2:
			long n0Val = asymmetricNeighborValues[0], n1Val = asymmetricNeighborValues[1];
			int[] n0Coords = asymmetricNeighborCoords[0], n1Coords = asymmetricNeighborCoords[1];
			int n0Mult = asymmetricNeighborShareMultipliers[0], n1Mult = asymmetricNeighborShareMultipliers[1];
			int shareCount = neighborCount + 1;
			if (n0Val == n1Val) {
				// n0Val = n1Val < value
				long toShare = value - n0Val; 
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share*n0Mult;
				newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share*n1Mult;
				newWSlices[1][x][y][z] += value - toShare + share + toShare%shareCount;
			} else if (n0Val < n1Val) {
				// n0Val < n1Val < value
				long toShare = value - n1Val; 
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share*n0Mult;
				newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share*n1Mult;
				shareCount -= asymmetricNeighborSymmetryCounts[1];
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n0Val;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share*n0Mult;
				newWSlices[1][x][y][z] += currentRemainingValue - toShare + share + toShare%shareCount;
			} else {
				// n1Val < n0Val < value
				long toShare = value - n0Val; 
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share*n0Mult;
				newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share*n1Mult;
				shareCount -= asymmetricNeighborSymmetryCounts[0];
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n1Val;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share*n1Mult;
				newWSlices[1][x][y][z] += currentRemainingValue - toShare + share + toShare%shareCount;
			}				
			break;
		case 1:
			shareCount = neighborCount + 1;
			long toShare = value - asymmetricNeighborValues[0];
			long share = toShare/shareCount;
			if (share != 0) {
				toppled = true;
				value = value - toShare + toShare%shareCount + share;
				int[] nc = asymmetricNeighborCoords[0];
				newWSlices[nc[0]][nc[1]][nc[2]][nc[3]] += share * asymmetricNeighborShareMultipliers[0];
			}
			// no break
		case 0:
			newWSlices[1][x][y][z] += value;
			break;
		default: // 8, 7, 6, 5, 4
			Utils.sortDescending(asymmetricNeighborCount, asymmetricNeighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, asymmetricNeighborCoords, 
					asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}

	private static boolean topplePositionSortedNeighbors(long[][][][] newWSlices, long value, int x, int y, int z, long[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		long neighborValue = asymmetricNeighborValues[0];
		long toShare = value - neighborValue;
		long share = toShare/shareCount;
		if (share != 0) {
			toppled = true;
			value = value - toShare + toShare%shareCount + share;
			for (int j = 0; j < asymmetricNeighborCount; j++) {
				int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];
				newWSlices[nc[0]][nc[1]][nc[2]][nc[3]] += share * asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]];
			}
		}
		long previousNeighborValue = neighborValue;
		shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[0]];
		for (int i = 1; i < asymmetricNeighborCount; i++) {
			neighborValue = asymmetricNeighborValues[i];
			if (neighborValue != previousNeighborValue) {
				toShare = value - neighborValue;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%shareCount + share;
					for (int j = i; j < asymmetricNeighborCount; j++) {
						int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];
						newWSlices[nc[0]][nc[1]][nc[2]][nc[3]] += share * asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]];
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		newWSlices[1][x][y][z] += value;
		return toppled;
	}

	private static boolean topplePosition(long[][][][] newWSlices, long value, int x, int y, int z, long[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		boolean toppled = false;
		switch (neighborCount) {
		case 3:
			Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, neighborValues, sortedNeighborsIndexes, 
					neighborCoords, neighborShareMultipliers, 3);
			break;
		case 2:
			long n0Val = neighborValues[0], n1Val = neighborValues[1];
			int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
			int n0Mult = neighborShareMultipliers[0], n1Mult = neighborShareMultipliers[1];
			if (n0Val == n1Val) {
				// n0Val = n1Val < value
				long toShare = value - n0Val; 
				long share = toShare/3;
				if (share != 0) {
					toppled = true;
				}
				newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share*n0Mult;
				newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share*n1Mult;
				newWSlices[1][x][y][z] += value - toShare + share + toShare%3;
			} else if (n0Val < n1Val) {
				// n0Val < n1Val < value
				long toShare = value - n1Val; 
				long share = toShare/3;
				if (share != 0) {
					toppled = true;
				}
				newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share*n0Mult;
				newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share*n1Mult;
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n0Val;
				share = toShare/2;
				if (share != 0) {
					toppled = true;
				}
				newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share*n0Mult;
				newWSlices[1][x][y][z] += currentRemainingValue - toShare + share + toShare%2;
			} else {
				// n1Val < n0Val < value
				long toShare = value - n0Val; 
				long share = toShare/3;
				if (share != 0) {
					toppled = true;
				}
				newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share*n0Mult;
				newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share*n1Mult;
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n1Val;
				share = toShare/2;
				if (share != 0) {
					toppled = true;
				}
				newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share*n1Mult;
				newWSlices[1][x][y][z] += currentRemainingValue - toShare + share + toShare%2;
			}				
			break;
		case 1:
			long toShare = value - neighborValues[0];
			long share = toShare/2;
			if (share != 0) {
				toppled = true;
				value = value - toShare + toShare%2 + share;
				int[] nc = neighborCoords[0];
				newWSlices[nc[0]][nc[1]][nc[2]][nc[3]] += share * neighborShareMultipliers[0];
			}
			// no break
		case 0:
			newWSlices[1][x][y][z] += value;
			break;
		default: // 8, 7, 6, 5, 4
			Utils.sortDescending(neighborCount, neighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, neighborValues, sortedNeighborsIndexes, neighborCoords, 
					neighborShareMultipliers, neighborCount);
		}
		return toppled;
	}

	private static boolean topplePositionSortedNeighbors(long[][][][] newWSlices, long value, int x, int y, int z, long[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		long neighborValue = neighborValues[0];
		long toShare = value - neighborValue;
		long share = toShare/shareCount;
		if (share != 0) {
			toppled = true;
			value = value - toShare + toShare%shareCount + share;
			for (int j = 0; j < neighborCount; j++) {
				int[] nc = neighborCoords[sortedNeighborsIndexes[j]];
				newWSlices[nc[0]][nc[1]][nc[2]][nc[3]] += share * neighborShareMultipliers[sortedNeighborsIndexes[j]];
			}
		}
		long previousNeighborValue = neighborValue;
		shareCount--;
		for (int i = 1; i < neighborCount; i++) {
			neighborValue = neighborValues[i];
			if (neighborValue != previousNeighborValue) {
				toShare = value - neighborValue;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%shareCount + share;
					for (int j = i; j < neighborCount; j++) {
						int[] nc = neighborCoords[sortedNeighborsIndexes[j]];
						newWSlices[nc[0]][nc[1]][nc[2]][nc[3]] += share * neighborShareMultipliers[sortedNeighborsIndexes[j]];
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newWSlices[1][x][y][z] += value;
		return toppled;
	}

	private static boolean topplePosition(long[][][][] newWSlices, long value, int x, int y, int z, long[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
		case 3:
			Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, 
					asymmetricNeighborCoords, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
			break;
		case 2:
			long n0Val = asymmetricNeighborValues[0], n1Val = asymmetricNeighborValues[1];
			int[] n0Coords = asymmetricNeighborCoords[0], n1Coords = asymmetricNeighborCoords[1];
			int shareCount = neighborCount + 1;
			if (n0Val == n1Val) {
				// n0Val = n1Val < value
				long toShare = value - n0Val; 
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share;
				newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share;
				newWSlices[1][x][y][z] += value - toShare + share + toShare%shareCount;
			} else if (n0Val < n1Val) {
				// n0Val < n1Val < value
				long toShare = value - n1Val; 
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share;
				newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share;
				shareCount -= asymmetricNeighborSymmetryCounts[1];
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n0Val;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share;
				newWSlices[1][x][y][z] += currentRemainingValue - toShare + share + toShare%shareCount;
			} else {
				// n1Val < n0Val < value
				long toShare = value - n0Val; 
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share;
				newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share;
				shareCount -= asymmetricNeighborSymmetryCounts[0];
				long currentRemainingValue = value - neighborCount*share;
				toShare = currentRemainingValue - n1Val;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
				}
				newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share;
				newWSlices[1][x][y][z] += currentRemainingValue - toShare + share + toShare%shareCount;
			}				
			break;
		case 1:
			shareCount = neighborCount + 1;
			long toShare = value - asymmetricNeighborValues[0];
			long share = toShare/shareCount;
			if (share != 0) {
				toppled = true;
				value = value - toShare + toShare%shareCount + share;
				int[] nc = asymmetricNeighborCoords[0];
				newWSlices[nc[0]][nc[1]][nc[2]][nc[3]] += share;
			}
			// no break
		case 0:
			newWSlices[1][x][y][z] += value;
			break;
		default: // 8, 7, 6, 5, 4
			Utils.sortDescending(asymmetricNeighborCount, asymmetricNeighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, asymmetricNeighborCoords, 
					asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}

	private static boolean topplePositionSortedNeighbors(long[][][][] newWSlices, long value, int x, int y, int z, long[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		long neighborValue = asymmetricNeighborValues[0];
		long toShare = value - neighborValue;
		long share = toShare/shareCount;
		if (share != 0) {
			toppled = true;
			value = value - toShare + toShare%shareCount + share;
			for (int j = 0; j < asymmetricNeighborCount; j++) {
				int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];
				newWSlices[nc[0]][nc[1]][nc[2]][nc[3]] += share;
			}
		}
		long previousNeighborValue = neighborValue;
		shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[0]];
		for (int i = 1; i < asymmetricNeighborCount; i++) {
			neighborValue = asymmetricNeighborValues[i];
			if (neighborValue != previousNeighborValue) {
				toShare = value - neighborValue;
				share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%shareCount + share;
					for (int j = i; j < asymmetricNeighborCount; j++) {
						int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];
						newWSlices[nc[0]][nc[1]][nc[2]][nc[3]] += share;
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		newWSlices[1][x][y][z] += value;
		return toppled;
	}

	@Override
	public int getAsymmetricMaxW() {
		return maxW;
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
	public long getIntialValue() {
		return initialValue;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		SerializableModelData data = new SerializableModelData();
		data.put(SerializableModelData.MODEL, SerializableModelData.Models.AETHER);
		data.put(SerializableModelData.INITIAL_CONFIGURATION, initialValue);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_TYPE, SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE, SerializableModelData.InitialConfigurationImplementationTypes.LONG);
		data.put(SerializableModelData.GRID, sourceGrid);
		data.put(SerializableModelData.GRID_TYPE, SerializableModelData.GridTypes.INFINITE_REGULAR);
		data.put(SerializableModelData.GRID_DIMENSION, 4);
		data.put(SerializableModelData.GRID_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_LONG_PRIMITIVE_ARRAY_1);
		data.put(SerializableModelData.COORDINATE_BOUNDS, maxW);
		data.put(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE, SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER);
		data.put(SerializableModelData.STEP, step);
		data.put(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP, changed);
		data.put(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE, grid);
		data.put(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BOOLEAN_PRIMITIVE_ARRAY_1);
		Utils.serializeToFile(data, backupPath, backupName);
	}

	@Override
	public String getName() {
		return "Aether";
	}

	@Override
	public String getSubfolderPath() {
		return getName() + "/4D/" + initialValue + "/toppling_alternation_compliance";
	}

}