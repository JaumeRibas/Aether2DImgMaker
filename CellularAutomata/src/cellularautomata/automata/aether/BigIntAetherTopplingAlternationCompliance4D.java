/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2024 Jaume Ribas

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
import cellularautomata.Constants;
import cellularautomata.Utils;
import cellularautomata.model.SerializableModelData;
import cellularautomata.model4d.IsotropicHypercubicBooleanArrayModel4DA;
import cellularautomata.numbers.BigInt;

public class BigIntAetherTopplingAlternationCompliance4D extends IsotropicHypercubicBooleanArrayModel4DA {
	
	private static final BigInt two = BigInt.valueOf(2);
	private static final BigInt three = BigInt.valueOf(3);
	private static final BigInt five = BigInt.valueOf(5);
	private static final BigInt eight = BigInt.valueOf(8);
	private static final BigInt nine = BigInt.valueOf(9);

	/** A 4D array representing the grid */
	private BigInt[][][][] sourceGrid;
	
	private boolean itsEvenPositionsTurnToTopple;

	private final BigInt initialValue;
	private long step;
	private int maxW;
	private Boolean changed = null;
	/**
	 * Used in {@link #getSubfolderPath()}.
	 */
	private final String folderName;

	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public BigIntAetherTopplingAlternationCompliance4D(BigInt initialValue) {
		this.initialValue = initialValue;
		itsEvenPositionsTurnToTopple = initialValue.compareTo(BigInt.ZERO) >= 0;
		sourceGrid = Utils.buildAnisotropic4DBigIntArray(8);
		sourceGrid[0][0][0][0] = this.initialValue;
		maxW = 5;
		step = 0;
		String strInitialValue = Utils.numberToPlainTextMaxLength(initialValue, Constants.MAX_INITIAL_VALUE_LENGTH_IN_PATH);
		if (strInitialValue == null) {
			folderName = Utils.getFileNameSafeTimeStamp();
		} else {
			folderName = strInitialValue;
		}
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
	public BigIntAetherTopplingAlternationCompliance4D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		SerializableModelData data = (SerializableModelData) Utils.deserializeFromFile(backupPath);
		data = SerializableModelData.updateDataFormat(data);
		if (!SerializableModelData.Models.AETHER.equals(data.get(SerializableModelData.MODEL))) {
			throw new IllegalArgumentException("The backup file contains a different model.");
		}
		if (!SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_TYPE))
				|| !SerializableModelData.InitialConfigurationImplementationTypes.BIG_INT.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.GridTypes.INFINITE_REGULAR.equals(data.get(SerializableModelData.GRID_TYPE))
				|| !Integer.valueOf(4).equals(data.get(SerializableModelData.GRID_DIMENSION))
				|| !SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BIG_INT_ARRAY_1.equals(data.get(SerializableModelData.GRID_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER.equals(data.get(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE))
				|| !data.contains(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP)
				|| !data.contains(SerializableModelData.INITIAL_CONFIGURATION_FOLDER_NAME)) {
			throw new IllegalArgumentException("The backup file's configuration is not compatible with this class.");
		}
		initialValue = (BigInt) data.get(SerializableModelData.INITIAL_CONFIGURATION);
		sourceGrid = (BigInt[][][][]) data.get(SerializableModelData.GRID);
		maxW = (int) data.get(SerializableModelData.COORDINATE_BOUNDS);
		step = (long) data.get(SerializableModelData.STEP);
		changed = (Boolean) data.get(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP);
		folderName = (String) data.get(SerializableModelData.INITIAL_CONFIGURATION_FOLDER_NAME);
		itsEvenPositionsTurnToTopple = initialValue.compareTo(BigInt.ZERO) >= 0 == (step%2 == 0);
		if (SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BOOLEAN_PRIMITIVE_ARRAY_1.equals(data.get(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE_IMPLEMENTATION_TYPE))) {
			grid = (boolean[][][][]) data.get(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE);
		} else {
			nextStep();
		}
	}

	@Override
	public Boolean nextStep() {
		final int newSide = maxW + 3;
		BigInt[][][][] newSourceGrid = new BigInt[newSide][][][];
		grid = null;
		grid = Utils.buildAnisotropic4DBooleanArray(newSide);
		boolean changed = false;
		BigInt[][][] smallerWSlice = null, currentWSlice = sourceGrid[0], greaterWSlice = sourceGrid[1];
		BigInt[][][] newSmallerWSlice = null, 
				newCurrentWSlice = Utils.buildAnisotropic3DBigIntArray(1), 
				newGreaterWSlice = Utils.buildAnisotropic3DBigIntArray(2);// build new grid progressively to save memory
		boolean[][][] newCurrentWSliceCompliance = grid[0]; 
		newSourceGrid[0] = newCurrentWSlice;
		newSourceGrid[1] = newGreaterWSlice;
		// w = 0, x = 0, y = 0, z = 0
		boolean itsCurrentPositionsTurnToTopple = itsEvenPositionsTurnToTopple;
		BigInt currentValue = currentWSlice[0][0][0];
		BigInt greaterWNeighborValue = greaterWSlice[0][0][0];
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
		newGreaterWSlice = Utils.buildAnisotropic3DBigIntArray(3);
		newCurrentWSliceCompliance = grid[1];
		newSourceGrid[2] = newGreaterWSlice;
		BigInt[][][][] newWSlices = new BigInt[][][][] { newSmallerWSlice, newCurrentWSlice, newGreaterWSlice};
		BigInt[] relevantAsymmetricNeighborValues = new BigInt[8];
		int[] sortedNeighborsIndexes = new int[8];
		int[][] relevantAsymmetricNeighborCoords = new int[8][4];
		int[] relevantAsymmetricNeighborShareMultipliers = new int[8];// to compensate for omitted symmetric positions
		int[] relevantAsymmetricNeighborSymmetryCounts = new int[8];// to compensate for omitted symmetric positions
		// w = 1, x = 0, y = 0, z = 0
		itsCurrentPositionsTurnToTopple = !itsEvenPositionsTurnToTopple;
		// reuse values obtained previously
		BigInt smallerWNeighborValue = currentValue;
		currentValue = greaterWNeighborValue;
		greaterWNeighborValue = greaterWSlice[0][0][0];
		BigInt greaterXNeighborValue = currentWSlice[1][0][0];
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
		BigInt smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterWNeighborValue = greaterWSlice[1][0][0];
		BigInt greaterYNeighborValue = currentWSlice[1][1][0];
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
		BigInt smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice[1][1][0];
		BigInt greaterZNeighborValue = currentWSlice[1][1][1];
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
		BigInt smallerZNeighborValue = currentValue;
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
		BigInt[][][][] wSlices = new BigInt[][][][] { smallerWSlice, currentWSlice, greaterWSlice};
		newSmallerWSlice = newCurrentWSlice;
		newCurrentWSlice = newGreaterWSlice;
		newGreaterWSlice = Utils.buildAnisotropic3DBigIntArray(4);
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
		newGreaterWSlice = Utils.buildAnisotropic3DBigIntArray(5);
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
		// reuse values obtained previously
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
		newGreaterWSlice = Utils.buildAnisotropic3DBigIntArray(6);
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
			newSourceGrid[sourceGrid.length] = Utils.buildAnisotropic3DBigIntArray(newSourceGrid.length);
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

	private boolean toppleRangeBeyondW4(BigInt[][][][] wSlices, BigInt[][][][] newWSlices, BigInt[][][][] newSourceGrid, int minW,
			int maxW, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords,
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts) {
		boolean changed = false;
		int w = minW, wMinusOne = w - 1, wMinusTwo = w - 2, wMinusThree = w - 3, wPlusOne = w + 1, wPlusTwo = w + 2;
		BigInt[][][] smallerWSlice = null, currentWSlice = wSlices[1], greaterWSlice = wSlices[2];
		BigInt[][][] newSmallerWSlice = null, newCurrentWSlice = newWSlices[1], newGreaterWSlice = newWSlices[2];
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
			newGreaterWSlice = Utils.buildAnisotropic3DBigIntArray(wPlusTwo);
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
			BigInt currentValue = currentWSlice[3][2][0];
			BigInt greaterWNeighborValue = greaterWSlice[3][2][0];
			BigInt smallerWNeighborValue = smallerWSlice[3][2][0];
			BigInt greaterXNeighborValue = currentWSlice[4][2][0];
			BigInt smallerXNeighborValue = currentWSlice[2][2][0];
			BigInt greaterYNeighborValue = currentWSlice[3][3][0];
			BigInt smallerYNeighborValue = currentWSlice[3][1][0];
			BigInt greaterZNeighborValue = currentWSlice[3][2][1];
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
			BigInt smallerZNeighborValue = currentValue;
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

	private static boolean toppleRangeOfType1(BigInt[][][][] wSlices, BigInt[][][][] newWSlices, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers, 
			boolean[][][] newCurrentWSliceCompliance, boolean isWEven, boolean itsEvenPositionsTurnToTopple) {
		boolean changed = false;
		BigInt[][][] smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];
		//  w | 00 | 00 | 00 | 06
		boolean itsCurrentPositionsTurnToTopple = isWEven == itsEvenPositionsTurnToTopple;
		BigInt currentValue = currentWSlice[0][0][0];
		BigInt greaterWNeighborValue = greaterWSlice[0][0][0];
		BigInt smallerWNeighborValue = smallerWSlice[0][0][0];
		BigInt greaterXNeighborValue = currentWSlice[1][0][0];
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
		BigInt smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterWNeighborValue = greaterWSlice[1][0][0];
		smallerWNeighborValue = smallerWSlice[1][0][0];
		greaterXNeighborValue = currentWSlice[2][0][0];
		BigInt greaterYNeighborValue = currentWSlice[1][1][0];
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
		BigInt smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice[1][1][0];
		smallerWNeighborValue = smallerWSlice[1][1][0];
		greaterXNeighborValue = currentWSlice[2][1][0];
		BigInt greaterZNeighborValue = currentWSlice[1][1][1];
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
		BigInt smallerZNeighborValue = currentValue;
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

	private static boolean toppleRangeOfType2(int x, BigInt[][][][] wSlices, BigInt[][][][] newWSlices, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers, 
			boolean[][][] newCurrentWSliceCompliance, boolean isWEven, boolean itsEvenPositionsTurnToTopple) {
		int xMinusOne = x - 1;
		boolean changed = false;
		BigInt[][][] currentWSlice = wSlices[1], greaterWSlice = wSlices[2];		
		//  w |  x | 00 | 00 | 10
		boolean itsCurrentPositionsTurnToTopple = isWEven == (x%2 == 0) == itsEvenPositionsTurnToTopple;
		BigInt currentValue = currentWSlice[x][0][0];
		BigInt greaterWNeighborValue = greaterWSlice[x][0][0];
		BigInt smallerXNeighborValue = currentWSlice[xMinusOne][0][0];
		BigInt greaterYNeighborValue = currentWSlice[x][1][0];
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
		BigInt smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice[x][1][0];
		smallerXNeighborValue = currentWSlice[xMinusOne][1][0];
		greaterYNeighborValue = currentWSlice[x][2][0];
		BigInt greaterZNeighborValue = currentWSlice[x][1][1];
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
		BigInt smallerZNeighborValue = currentValue;
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

	private static boolean toppleRangeOfType3(int coord, BigInt[][][][] wSlices, BigInt[][][][] newWSlices, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers, 
			boolean[][][] newCurrentWSliceCompliance, boolean isWEven, boolean itsEvenPositionsTurnToTopple) {
		int coordMinusOne = coord - 1;
		boolean changed = false;
		BigInt[][][] currentWSlice = wSlices[1], greaterWSlice = wSlices[2];		
		//  w |  x |  y | 00 | 13
		boolean itsCurrentPositionsTurnToTopple = isWEven == itsEvenPositionsTurnToTopple;
		BigInt currentValue = currentWSlice[coord][coord][0];
		BigInt greaterWNeighborValue = greaterWSlice[coord][coord][0];
		BigInt smallerYNeighborValue = currentWSlice[coord][coordMinusOne][0];
		BigInt greaterZNeighborValue = currentWSlice[coord][coord][1];
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
		BigInt smallerZNeighborValue = currentValue;
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

	private static boolean toppleRangeOfType4(BigInt[][][][] wSlices, BigInt[][][][] newWSlices, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers, 
			boolean[][][] newCurrentWSliceCompliance, boolean isWEven, boolean itsEvenPositionsTurnToTopple) {
		boolean changed = false;
		if (toppleRangeOfType1(wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers, newCurrentWSliceCompliance, isWEven, itsEvenPositionsTurnToTopple)) {
			changed = true;
		}
		BigInt[][][] smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];
		//  w | 02 | 00 | 00 | 32
		boolean itsCurrentPositionsTurnToTopple = isWEven == itsEvenPositionsTurnToTopple;
		BigInt currentValue = currentWSlice[2][0][0];
		BigInt greaterWNeighborValue = greaterWSlice[2][0][0];
		BigInt smallerWNeighborValue = smallerWSlice[2][0][0];
		BigInt greaterXNeighborValue = currentWSlice[3][0][0];
		BigInt smallerXNeighborValue = currentWSlice[1][0][0];
		BigInt greaterYNeighborValue = currentWSlice[2][1][0];
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
		BigInt smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice[2][1][0];
		smallerWNeighborValue = smallerWSlice[2][1][0];
		greaterXNeighborValue = currentWSlice[3][1][0];
		smallerXNeighborValue = currentWSlice[1][1][0];
		greaterYNeighborValue = currentWSlice[2][2][0];
		BigInt greaterZNeighborValue = currentWSlice[2][1][1];
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
		BigInt smallerZNeighborValue = currentValue;
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

	private static boolean toppleRangeOfType5(int x, BigInt[][][][] wSlices, BigInt[][][][] newWSlices, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers, 
			boolean[][][] newCurrentWSliceCompliance, boolean isWEven, boolean itsEvenPositionsTurnToTopple) {
		int xMinusOne = x - 1, xPlusOne = x + 1;
		boolean changed = false;
		BigInt[][][] smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];		
		//  w |  x | 00 | 00 | 19
		boolean itsCurrentPositionsTurnToTopple = isWEven == (x%2 == 0) == itsEvenPositionsTurnToTopple;
		BigInt currentValue = currentWSlice[x][0][0];
		BigInt greaterWNeighborValue = greaterWSlice[x][0][0];
		BigInt smallerWNeighborValue = smallerWSlice[x][0][0];
		BigInt greaterXNeighborValue = currentWSlice[xPlusOne][0][0];
		BigInt smallerXNeighborValue = currentWSlice[xMinusOne][0][0];
		BigInt greaterYNeighborValue = currentWSlice[x][1][0];
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
		BigInt smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice[x][1][0];
		smallerWNeighborValue = smallerWSlice[x][1][0];
		greaterXNeighborValue = currentWSlice[xPlusOne][1][0];
		smallerXNeighborValue = currentWSlice[xMinusOne][1][0];
		greaterYNeighborValue = currentWSlice[x][2][0];
		BigInt greaterZNeighborValue = currentWSlice[x][1][1];
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
		BigInt smallerZNeighborValue = currentValue;
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

	private static boolean toppleRangeOfType6(int coord, BigInt[][][][] wSlices, BigInt[][][][] newWSlices, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers, 
			boolean[][][] newCurrentWSliceCompliance, boolean isWEven, boolean itsEvenPositionsTurnToTopple) {
		int coordMinusOne = coord - 1, coordPlusOne = coord + 1;
		BigInt[][][] smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];	
		boolean changed = false;		
		//  w |  x |  y | 00 | 22
		boolean itsCurrentPositionsTurnToTopple = isWEven == itsEvenPositionsTurnToTopple;
		BigInt currentValue = currentWSlice[coord][coord][0];
		BigInt greaterWNeighborValue = greaterWSlice[coord][coord][0];
		BigInt smallerWNeighborValue = smallerWSlice[coord][coord][0];
		BigInt greaterXNeighborValue = currentWSlice[coordPlusOne][coord][0];
		BigInt smallerYNeighborValue = currentWSlice[coord][coordMinusOne][0];
		BigInt greaterZNeighborValue = currentWSlice[coord][coord][1];
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
		BigInt smallerZNeighborValue = currentValue;
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
		z = zPlusOne;
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
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
		BigInt smallerXNeighborValue = currentWSlice[coordMinusOne][2][0];
		BigInt greaterYNeighborValue = currentWSlice[coord][3][0];
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

	private static boolean toppleRangeOfType7(int x, BigInt[][][][] wSlices, BigInt[][][][] newWSlices, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers, 
			boolean[][][] newCurrentWSliceCompliance, boolean isWEven, boolean itsEvenPositionsTurnToTopple) {		
		int y = x - 1, xMinusOne = x - 1, xMinusTwo = x - 2, yPlusOne = y + 1, yMinusOne = y - 1;
		boolean changed = false;
		BigInt[][][] currentWSlice = wSlices[1], greaterWSlice = wSlices[2];		
		//  w |  x |  y | 00 | 27
		boolean itsCurrentPositionsTurnToTopple = isWEven != itsEvenPositionsTurnToTopple;//if x even -> y odd -> odd, if x odd -> y even -> odd
		BigInt currentValue = currentWSlice[x][y][0];
		BigInt greaterWNeighborValue = greaterWSlice[x][y][0];
		BigInt smallerXNeighborValue = currentWSlice[xMinusOne][y][0];
		BigInt greaterYNeighborValue = currentWSlice[x][yPlusOne][0];
		BigInt smallerYNeighborValue = currentWSlice[x][yMinusOne][0];
		BigInt greaterZNeighborValue = currentWSlice[x][y][1];
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
		BigInt smallerZNeighborValue = currentValue;
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

	private static boolean toppleRangeOfType8(int x, BigInt[][][][] wSlices, BigInt[][][][] newWSlices, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers, 
			boolean[][][] newCurrentWSliceCompliance, boolean isWEven, boolean itsEvenPositionsTurnToTopple) {
		int xMinusOne = x - 1, xPlusOne = x + 1;
		boolean changed = false;
		BigInt[][][] smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];		
		//  w |  x | 00 | 00 | 32
		boolean itsCurrentPositionsTurnToTopple = isWEven == (x%2 == 0) == itsEvenPositionsTurnToTopple;
		BigInt currentValue = currentWSlice[x][0][0];
		BigInt greaterWNeighborValue = greaterWSlice[x][0][0];
		BigInt smallerWNeighborValue = smallerWSlice[x][0][0];
		BigInt greaterXNeighborValue = currentWSlice[xPlusOne][0][0];
		BigInt smallerXNeighborValue = currentWSlice[xMinusOne][0][0];
		BigInt greaterYNeighborValue = currentWSlice[x][1][0];
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
		BigInt smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice[x][1][0];
		smallerWNeighborValue = smallerWSlice[x][1][0];
		greaterXNeighborValue = currentWSlice[xPlusOne][1][0];
		smallerXNeighborValue = currentWSlice[xMinusOne][1][0];
		greaterYNeighborValue = currentWSlice[x][2][0];
		BigInt greaterZNeighborValue = currentWSlice[x][1][1];
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
		BigInt smallerZNeighborValue = currentValue;
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

	private static boolean toppleRangeOfType9(int coord, BigInt[][][][] wSlices, BigInt[][][][] newWSlices, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers, 
			boolean[][][] newCurrentWSliceCompliance, boolean isWEven, boolean itsEvenPositionsTurnToTopple) {
		int coordMinusOne = coord - 1, coordPlusOne = coord + 1;
		boolean changed = false;
		BigInt[][][] smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];
		//  w |  x |  y | 00 | 35
		boolean itsCurrentPositionsTurnToTopple = isWEven == itsEvenPositionsTurnToTopple;
		BigInt currentValue = currentWSlice[coord][coord][0];
		BigInt greaterWNeighborValue = greaterWSlice[coord][coord][0];
		BigInt smallerWNeighborValue = smallerWSlice[coord][coord][0];
		BigInt greaterXNeighborValue = currentWSlice[coordPlusOne][coord][0];
		BigInt smallerYNeighborValue = currentWSlice[coord][coordMinusOne][0];
		BigInt greaterZNeighborValue = currentWSlice[coord][coord][1];
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
		BigInt smallerZNeighborValue = currentValue;
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

	private static boolean topplePositionOfType1(BigInt currentValue, BigInt gWValue, BigInt[][][] newCurrentWSlice, BigInt[][][] newGreaterWSlice) {
		boolean toppled = false;
		if (gWValue.compareTo(currentValue) < 0) {
			BigInt toShare = currentValue.subtract(gWValue);
			BigInt[] shareAndRemainder = toShare.divideAndRemainder(nine);BigInt share = shareAndRemainder[0];
			if (!share.equals(BigInt.ZERO)) {
				toppled = true;
				newCurrentWSlice[0][0][0] = newCurrentWSlice[0][0][0].add(currentValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
				newGreaterWSlice[0][0][0] = newGreaterWSlice[0][0][0].add(share);
			} else {
				newCurrentWSlice[0][0][0] = newCurrentWSlice[0][0][0].add(currentValue);
			}			
		} else {
			newCurrentWSlice[0][0][0] = newCurrentWSlice[0][0][0].add(currentValue);
		}
		return toppled;
	}

	private static boolean topplePositionOfType2(BigInt currentValue, BigInt gWValue, BigInt sWValue, BigInt gXValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sWValue.compareTo(currentValue) < 0) {
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
		if (gXValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType3(BigInt currentValue, BigInt gWValue, BigInt sXValue, BigInt gYValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sXValue.compareTo(currentValue) < 0) {
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
		if (gYValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType4(BigInt currentValue, BigInt gWValue, BigInt sYValue, BigInt gZValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sYValue.compareTo(currentValue) < 0) {
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
		if (gZValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType5(BigInt currentValue, BigInt gWValue, BigInt sZValue, BigInt[][][] newCurrentWSlice, BigInt[][][] newGreaterWSlice) {
		boolean toppled = false;
		if (sZValue.compareTo(currentValue) < 0) {
			if (gWValue.compareTo(currentValue) < 0) {
				if (sZValue.equals(gWValue)) {
					// gw = sz < current
					BigInt toShare = currentValue.subtract(gWValue); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(nine);BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newCurrentWSlice[1][1][0] = newCurrentWSlice[1][1][0].add(share.add(share));// one more for the symmetric position at the other side
					newCurrentWSlice[1][1][1] = newCurrentWSlice[1][1][1].add(currentValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
					newGreaterWSlice[1][1][1] = newGreaterWSlice[1][1][1].add(share);
				} else if (sZValue.compareTo(gWValue) < 0) {
					// sz < gw < current
					BigInt toShare = currentValue.subtract(gWValue); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(nine);BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newCurrentWSlice[1][1][0] = newCurrentWSlice[1][1][0].add(share.add(share));
					newGreaterWSlice[1][1][1] = newGreaterWSlice[1][1][1].add(share);
					BigInt currentRemainingValue = currentValue.subtract(share.multiply(eight));
					toShare = currentRemainingValue.subtract(sZValue); 
					shareAndRemainder = toShare.divideAndRemainder(five);share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newCurrentWSlice[1][1][0] = newCurrentWSlice[1][1][0].add(share.add(share));
					newCurrentWSlice[1][1][1] = newCurrentWSlice[1][1][1].add(currentRemainingValue .subtract(toShare).add(share).add(shareAndRemainder[1]));
				} else {
					// gw < sz < current
					BigInt toShare = currentValue.subtract(sZValue); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(nine);BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newCurrentWSlice[1][1][0] = newCurrentWSlice[1][1][0].add(share.add(share));
					newGreaterWSlice[1][1][1] = newGreaterWSlice[1][1][1].add(share);
					BigInt currentRemainingValue = currentValue.subtract(share.multiply(eight));
					toShare = currentRemainingValue.subtract(gWValue); 
					shareAndRemainder = toShare.divideAndRemainder(five);share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newCurrentWSlice[1][1][1] = newCurrentWSlice[1][1][1].add(currentRemainingValue .subtract(toShare).add(share).add(shareAndRemainder[1]));
					newGreaterWSlice[1][1][1] = newGreaterWSlice[1][1][1].add(share);
				}
			} else {
				// sz < current <= gw
				BigInt toShare = currentValue.subtract(sZValue); 
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(five);BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newCurrentWSlice[1][1][0] = newCurrentWSlice[1][1][0].add(share.add(share));
				newCurrentWSlice[1][1][1] = newCurrentWSlice[1][1][1].add(currentValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
			}
		} else if (gWValue.compareTo(currentValue) < 0) {
			// gw < current <= sz
			BigInt toShare = currentValue.subtract(gWValue); 
			BigInt[] shareAndRemainder = toShare.divideAndRemainder(five);BigInt share = shareAndRemainder[0];
			if (!share.equals(BigInt.ZERO)) {
				toppled = true;
			}
			newCurrentWSlice[1][1][1] = newCurrentWSlice[1][1][1].add(currentValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
			newGreaterWSlice[1][1][1] = newGreaterWSlice[1][1][1].add(share);
		} else {
			// gw >= current <= sz
			newCurrentWSlice[1][1][1] = newCurrentWSlice[1][1][1].add(currentValue);
		}
		return toppled;
	}

	private static boolean topplePositionOfType6(BigInt currentValue, BigInt gWValue, BigInt sWValue, BigInt gXValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sWValue.compareTo(currentValue) < 0) {
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
		if (gXValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType7(int x, BigInt currentValue, BigInt gWValue, BigInt sWValue, int sWShareMultiplier, BigInt gXValue, int gXShareMultiplier, BigInt sXValue, int sXShareMultiplier, BigInt gYValue, int gYShareMultiplier, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sWValue.compareTo(currentValue) < 0) {
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
		if (gXValue.compareTo(currentValue) < 0) {
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
		if (sXValue.compareTo(currentValue) < 0) {
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
		if (gYValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType8(int coord, BigInt currentValue, BigInt gWValue, BigInt sWValue, int sWShareMultiplier, BigInt gXValue, int gXShareMultiplier, BigInt sYValue, int sYShareMultiplier, BigInt gZValue, int gZShareMultiplier, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sWValue.compareTo(currentValue) < 0) {
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
		if (gXValue.compareTo(currentValue) < 0) {
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
		if (sYValue.compareTo(currentValue) < 0) {
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
		if (gZValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType9(int coord, BigInt currentValue, BigInt gWValue, BigInt sWValue, int sWShareMultiplier, BigInt gXValue, int gXShareMultiplier, BigInt sZValue, int sZShareMultiplier, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sWValue.compareTo(currentValue) < 0) {
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
		if (gXValue.compareTo(currentValue) < 0) {
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
		if (sZValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType10(int x, BigInt currentValue, BigInt gWValue, BigInt sXValue, BigInt gYValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sXValue.compareTo(currentValue) < 0) {
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
		if (gYValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType11(int x, int y, BigInt currentValue, BigInt gWValue, BigInt sXValue, int sXShareMultiplier, BigInt gYValue, int gYShareMultiplier, BigInt sYValue, int sYShareMultiplier, BigInt gZValue, int gZShareMultiplier, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sXValue.compareTo(currentValue) < 0) {
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
		if (gYValue.compareTo(currentValue) < 0) {
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
		if (sYValue.compareTo(currentValue) < 0) {
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
		if (gZValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType12(int x, int coord, BigInt currentValue, BigInt gWValue, BigInt sXValue, int sXShareMultiplier, BigInt gYValue, int gYShareMultiplier, BigInt sZValue, int sZShareMultiplier, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sXValue.compareTo(currentValue) < 0) {
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
		if (gYValue.compareTo(currentValue) < 0) {
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
		if (sZValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType13(int coord, BigInt currentValue, BigInt gWValue, BigInt sYValue, BigInt gZValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sYValue.compareTo(currentValue) < 0) {
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
		if (gZValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType14(int coord, int z, BigInt currentValue, BigInt gWValue, BigInt sYValue, int sYShareMultiplier, BigInt gZValue, int gZShareMultiplier, BigInt sZValue, int sZShareMultiplier, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sYValue.compareTo(currentValue) < 0) {
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
		if (gZValue.compareTo(currentValue) < 0) {
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
		if (sZValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType15(int coord, BigInt currentValue, BigInt gWValue, BigInt sZValue, BigInt[][][] newCurrentWSlice, BigInt[][][] newGreaterWSlice) {
		// 4*GW, 4*SZ | 15 | 15
		boolean toppled = false;
		if (sZValue.compareTo(currentValue) < 0) {
			if (gWValue.compareTo(currentValue) < 0) {
				if (sZValue.equals(gWValue)) {
					// gw = sz < current
					int coordMinusOne = coord - 1;
					BigInt toShare = currentValue.subtract(gWValue); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(nine);BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newCurrentWSlice[coord][coord][coordMinusOne] = newCurrentWSlice[coord][coord][coordMinusOne].add(share);
					newCurrentWSlice[coord][coord][coord] = newCurrentWSlice[coord][coord][coord].add(currentValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
					newGreaterWSlice[coord][coord][coord] = newGreaterWSlice[coord][coord][coord].add(share);
				} else if (sZValue.compareTo(gWValue) < 0) {
					// sz < gw < current
					int coordMinusOne = coord - 1;
					BigInt toShare = currentValue.subtract(gWValue); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(nine);BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newCurrentWSlice[coord][coord][coordMinusOne] = newCurrentWSlice[coord][coord][coordMinusOne].add(share);
					newGreaterWSlice[coord][coord][coord] = newGreaterWSlice[coord][coord][coord].add(share);
					BigInt currentRemainingValue = currentValue.subtract(share.multiply(eight));
					toShare = currentRemainingValue.subtract(sZValue); 
					shareAndRemainder = toShare.divideAndRemainder(five);share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newCurrentWSlice[coord][coord][coordMinusOne] = newCurrentWSlice[coord][coord][coordMinusOne].add(share);
					newCurrentWSlice[coord][coord][coord] = newCurrentWSlice[coord][coord][coord].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
				} else {
					// gw < sz < current
					int coordMinusOne = coord - 1;
					BigInt toShare = currentValue.subtract(sZValue); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(nine);BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newCurrentWSlice[coord][coord][coordMinusOne] = newCurrentWSlice[coord][coord][coordMinusOne].add(share);
					newGreaterWSlice[coord][coord][coord] = newGreaterWSlice[coord][coord][coord].add(share);
					BigInt currentRemainingValue = currentValue.subtract(share.multiply(eight));
					toShare = currentRemainingValue.subtract(gWValue); 
					shareAndRemainder = toShare.divideAndRemainder(five);share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newCurrentWSlice[coord][coord][coord] = newCurrentWSlice[coord][coord][coord].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
					newGreaterWSlice[coord][coord][coord] = newGreaterWSlice[coord][coord][coord].add(share);
				}
			} else {
				// sz < current <= gw
				int coordMinusOne = coord - 1;
				BigInt toShare = currentValue.subtract(sZValue); 
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(five);BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newCurrentWSlice[coord][coord][coordMinusOne] = newCurrentWSlice[coord][coord][coordMinusOne].add(share);
				newCurrentWSlice[coord][coord][coord] = newCurrentWSlice[coord][coord][coord].add(currentValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
			}
		} else if (gWValue.compareTo(currentValue) < 0) {
			// gw < current <= sz
			BigInt toShare = currentValue.subtract(gWValue); 
			BigInt[] shareAndRemainder = toShare.divideAndRemainder(five);BigInt share = shareAndRemainder[0];
			if (!share.equals(BigInt.ZERO)) {
				toppled = true;
			}
			newCurrentWSlice[coord][coord][coord] = newCurrentWSlice[coord][coord][coord].add(currentValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
			newGreaterWSlice[coord][coord][coord] = newGreaterWSlice[coord][coord][coord].add(share);
		} else {
			// gw >= current <= sz
			newCurrentWSlice[coord][coord][coord] = newCurrentWSlice[coord][coord][coord].add(currentValue);
		}
		return toppled;
	}

	private static boolean topplePositionOfType16(int x, int y, BigInt currentValue, BigInt gWValue, BigInt sWValue, int sWShareMultiplier, BigInt gXValue, int gXShareMultiplier, BigInt sXValue, int sXShareMultiplier, BigInt gYValue, int gYShareMultiplier, BigInt sYValue, int sYShareMultiplier, BigInt gZValue, int gZShareMultiplier, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sWValue.compareTo(currentValue) < 0) {
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
		if (gXValue.compareTo(currentValue) < 0) {
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
		if (sXValue.compareTo(currentValue) < 0) {
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
		if (gYValue.compareTo(currentValue) < 0) {
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
		if (sYValue.compareTo(currentValue) < 0) {
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
		if (gZValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType17(int x, int coord, BigInt currentValue, BigInt gWValue, BigInt sWValue, int sWShareMultiplier, BigInt gXValue, int gXShareMultiplier, BigInt sXValue, int sXShareMultiplier, BigInt gYValue, int gYShareMultiplier, BigInt sZValue, int sZShareMultiplier, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sWValue.compareTo(currentValue) < 0) {
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
		if (gXValue.compareTo(currentValue) < 0) {
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
		if (sXValue.compareTo(currentValue) < 0) {
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
		if (gYValue.compareTo(currentValue) < 0) {
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
		if (sZValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType18(int coord, int z, BigInt currentValue, BigInt gWValue, BigInt sWValue, int sWShareMultiplier, BigInt gXValue, int gXShareMultiplier, BigInt sYValue, int sYShareMultiplier, BigInt gZValue, int gZShareMultiplier, BigInt sZValue, int sZShareMultiplier, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sWValue.compareTo(currentValue) < 0) {
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
		if (gXValue.compareTo(currentValue) < 0) {
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
		if (sYValue.compareTo(currentValue) < 0) {
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
		if (gZValue.compareTo(currentValue) < 0) {
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
		if (sZValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType19(int x, int y, int z, BigInt currentValue, BigInt gWValue, BigInt sXValue, int sXShareMultiplier, BigInt gYValue, int gYShareMultiplier, BigInt sYValue, int sYShareMultiplier, BigInt gZValue, int gZShareMultiplier, BigInt sZValue, int sZShareMultiplier, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sXValue.compareTo(currentValue) < 0) {
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
		if (gYValue.compareTo(currentValue) < 0) {
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
		if (sYValue.compareTo(currentValue) < 0) {
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
		if (gZValue.compareTo(currentValue) < 0) {
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
		if (sZValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType20(int x, BigInt currentValue, BigInt gWValue, BigInt sWValue, BigInt gXValue, BigInt sXValue, BigInt gYValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sWValue.compareTo(currentValue) < 0) {
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
		if (gXValue.compareTo(currentValue) < 0) {
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
		if (sXValue.compareTo(currentValue) < 0) {
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
		if (gYValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType21(int coord, BigInt currentValue, BigInt gWValue, BigInt sWValue, BigInt gXValue, BigInt sYValue, BigInt gZValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sWValue.compareTo(currentValue) < 0) {
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
		if (gXValue.compareTo(currentValue) < 0) {
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
		if (sYValue.compareTo(currentValue) < 0) {
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
		if (gZValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType22(int coord, BigInt currentValue, BigInt gWValue, BigInt sWValue, BigInt gXValue, BigInt sZValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sWValue.compareTo(currentValue) < 0) {
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
		if (gXValue.compareTo(currentValue) < 0) {
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
		if (sZValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType23(int x, int y, int z, BigInt currentValue, BigInt gWValue, BigInt sWValue, int sWShareMultiplier, BigInt gXValue, int gXShareMultiplier, BigInt sXValue, int sXShareMultiplier, BigInt gYValue, int gYShareMultiplier, BigInt sYValue, int sYShareMultiplier, BigInt gZValue, int gZShareMultiplier, BigInt sZValue, int sZShareMultiplier, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, BigInt[][][][] newWSlices) {
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 2;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = 1;
			relevantNeighborCount++;
		}
		if (sWValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 0;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = sWShareMultiplier;
			relevantNeighborCount++;
		}
		if (gXValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = x + 1;
			nc[2] = y;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = gXShareMultiplier;
			relevantNeighborCount++;
		}
		if (sXValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = x - 1;
			nc[2] = y;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = sXShareMultiplier;
			relevantNeighborCount++;
		}
		if (gYValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y + 1;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = gYShareMultiplier;
			relevantNeighborCount++;
		}
		if (sYValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y - 1;
			nc[3] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = sYShareMultiplier;
			relevantNeighborCount++;
		}
		if (gZValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z + 1;
			relevantAsymmetricNeighborShareMultipliers[relevantNeighborCount] = gZShareMultiplier;
			relevantNeighborCount++;
		}
		if (sZValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType24(int x, int y, BigInt currentValue, BigInt gWValue, BigInt sXValue, BigInt gYValue, BigInt sYValue, BigInt gZValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sXValue.compareTo(currentValue) < 0) {
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
		if (gYValue.compareTo(currentValue) < 0) {
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
		if (sYValue.compareTo(currentValue) < 0) {
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
		if (gZValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType25(int x, int coord, BigInt currentValue, BigInt gWValue, BigInt sXValue, BigInt gYValue, BigInt sZValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sXValue.compareTo(currentValue) < 0) {
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
		if (gYValue.compareTo(currentValue) < 0) {
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
		if (sZValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType26(int coord, int z, BigInt currentValue, BigInt gWValue, BigInt sYValue, BigInt gZValue, BigInt sZValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sYValue.compareTo(currentValue) < 0) {
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
		if (gZValue.compareTo(currentValue) < 0) {
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
		if (sZValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType27(int x, int y, BigInt currentValue, BigInt gWValue, BigInt sWValue, BigInt gXValue, BigInt sXValue, BigInt gYValue, BigInt sYValue, BigInt gZValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sWValue.compareTo(currentValue) < 0) {
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
		if (gXValue.compareTo(currentValue) < 0) {
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
		if (sXValue.compareTo(currentValue) < 0) {
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
		if (gYValue.compareTo(currentValue) < 0) {
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
		if (sYValue.compareTo(currentValue) < 0) {
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
		if (gZValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType28(int x, int coord, BigInt currentValue, BigInt gWValue, BigInt sWValue, BigInt gXValue, BigInt sXValue, BigInt gYValue, BigInt sZValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sWValue.compareTo(currentValue) < 0) {
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
		if (gXValue.compareTo(currentValue) < 0) {
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
		if (sXValue.compareTo(currentValue) < 0) {
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
		if (gYValue.compareTo(currentValue) < 0) {
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
		if (sZValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType29(int coord, int z, BigInt currentValue, BigInt gWValue, BigInt sWValue, BigInt gXValue, BigInt sYValue, BigInt gZValue, BigInt sZValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sWValue.compareTo(currentValue) < 0) {
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
		if (gXValue.compareTo(currentValue) < 0) {
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
		if (sYValue.compareTo(currentValue) < 0) {
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
		if (gZValue.compareTo(currentValue) < 0) {
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
		if (sZValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType30(int x, int y, int z, BigInt currentValue, BigInt gWValue, BigInt sXValue, BigInt gYValue, BigInt sYValue, BigInt gZValue, BigInt sZValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][][] newWSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
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
		if (sXValue.compareTo(currentValue) < 0) {
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
		if (gYValue.compareTo(currentValue) < 0) {
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
		if (sYValue.compareTo(currentValue) < 0) {
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
		if (gZValue.compareTo(currentValue) < 0) {
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
		if (sZValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePositionOfType31(int x, int y, int z, BigInt currentValue, BigInt gWValue, BigInt sWValue, BigInt gXValue, BigInt sXValue, BigInt gYValue, BigInt sYValue, BigInt gZValue, BigInt sZValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, BigInt[][][][] newWSlices) {
		int relevantNeighborCount = 0;
		if (gWValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 2;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z;
			relevantNeighborCount++;
		}
		if (sWValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sWValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 0;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z;
			relevantNeighborCount++;
		}
		if (gXValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = x + 1;
			nc[2] = y;
			nc[3] = z;
			relevantNeighborCount++;
		}
		if (sXValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = x - 1;
			nc[2] = y;
			nc[3] = z;
			relevantNeighborCount++;
		}
		if (gYValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y + 1;
			nc[3] = z;
			relevantNeighborCount++;
		}
		if (sYValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y - 1;
			nc[3] = z;
			relevantNeighborCount++;
		}
		if (gZValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = x;
			nc[2] = y;
			nc[3] = z + 1;
			relevantNeighborCount++;
		}
		if (sZValue.compareTo(currentValue) < 0) {
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

	private static boolean topplePosition(BigInt[][][][] newWSlices, BigInt value, int x, int y, int z, BigInt[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int neighborCount) {
		boolean toppled = false;
		switch (neighborCount) {
		case 3:
			Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, neighborValues, sortedNeighborsIndexes, 
					neighborCoords, 3);
			break;
		case 2:
			BigInt n0Val = neighborValues[0], n1Val = neighborValues[1];
			int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
			int n0W = n0Coords[0], n0X = n0Coords[1], n0Y = n0Coords[2], n0Z = n0Coords[3];
			int n1W = n1Coords[0], n1X = n1Coords[1], n1Y = n1Coords[2], n1Z = n1Coords[3];
			if (n0Val.equals(n1Val)) {
				// n0Val = n1Val < value
				BigInt toShare = value.subtract(n0Val); 
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(three);BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newWSlices[n0W][n0X][n0Y][n0Z] = newWSlices[n0W][n0X][n0Y][n0Z].add(share);
				newWSlices[n1W][n1X][n1Y][n1Z] = newWSlices[n1W][n1X][n1Y][n1Z].add(share);
				newWSlices[1][x][y][z] = newWSlices[1][x][y][z].add(value.subtract(toShare).add(share).add(shareAndRemainder[1]));
			} else if (n0Val.compareTo(n1Val) < 0) {
				// n0Val < n1Val < value
				BigInt toShare = value.subtract(n1Val); 
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(three);BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newWSlices[n0W][n0X][n0Y][n0Z] = newWSlices[n0W][n0X][n0Y][n0Z].add(share);
				newWSlices[n1W][n1X][n1Y][n1Z] = newWSlices[n1W][n1X][n1Y][n1Z].add(share);
				BigInt currentRemainingValue = value.subtract(share.multiply(BigInt.valueOf(neighborCount)));
				toShare = currentRemainingValue.subtract(n0Val);
				shareAndRemainder = toShare.divideAndRemainder(two);share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newWSlices[n0W][n0X][n0Y][n0Z] = newWSlices[n0W][n0X][n0Y][n0Z].add(share);
				newWSlices[1][x][y][z] = newWSlices[1][x][y][z].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
			} else {
				// n1Val < n0Val < value
				BigInt toShare = value.subtract(n0Val); 
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(three);BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newWSlices[n0W][n0X][n0Y][n0Z] = newWSlices[n0W][n0X][n0Y][n0Z].add(share);
				newWSlices[n1W][n1X][n1Y][n1Z] = newWSlices[n1W][n1X][n1Y][n1Z].add(share);
				BigInt currentRemainingValue = value.subtract(share.multiply(BigInt.valueOf(neighborCount)));
				toShare = currentRemainingValue.subtract(n1Val);
				shareAndRemainder = toShare.divideAndRemainder(two);share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newWSlices[n1W][n1X][n1Y][n1Z] = newWSlices[n1W][n1X][n1Y][n1Z].add(share);
				newWSlices[1][x][y][z] = newWSlices[1][x][y][z].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
			}				
			break;
		case 1:
			BigInt toShare = value.subtract(neighborValues[0]);
			BigInt[] shareAndRemainder = toShare.divideAndRemainder(two);BigInt share = shareAndRemainder[0];
			if (!share.equals(BigInt.ZERO)) {
				toppled = true;
				value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
				int[] nc = neighborCoords[0];int nW = nc[0], nX = nc[1], nY = nc[2], nZ = nc[3];
				newWSlices[nW][nX][nY][nZ] = newWSlices[nW][nX][nY][nZ].add(share);
			}
			// no break
		case 0:
			newWSlices[1][x][y][z] = newWSlices[1][x][y][z].add(value);
			break;
		default: // 8, 7, 6, 5, 4
			Utils.sortDescending(neighborCount, neighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, neighborValues, sortedNeighborsIndexes, neighborCoords, 
					neighborCount);
		}
		return toppled;
	}

	private static boolean topplePositionSortedNeighbors(BigInt[][][][] newWSlices, BigInt value, int x, int y, int z, 
			BigInt[] neighborValues, int[] sortedNeighborsIndexes, int[][] neighborCoords, int neighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		BigInt neighborValue = neighborValues[0];
		BigInt toShare = value.subtract(neighborValue);
		BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));BigInt share = shareAndRemainder[0];
		if (!share.equals(BigInt.ZERO)) {
			toppled = true;
			value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
			for (int j = 0; j < neighborCount; j++) {
				int[] nc = neighborCoords[sortedNeighborsIndexes[j]];int nW = nc[0], nX = nc[1], nY = nc[2], nZ = nc[3];
				newWSlices[nW][nX][nY][nZ] = newWSlices[nW][nX][nY][nZ].add(share);
			}
		}
		BigInt previousNeighborValue = neighborValue;
		shareCount--;
		for (int i = 1; i < neighborCount; i++) {
			neighborValue = neighborValues[i];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
					value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
					for (int j = i; j < neighborCount; j++) {
						int[] nc = neighborCoords[sortedNeighborsIndexes[j]];int nW = nc[0], nX = nc[1], nY = nc[2], nZ = nc[3];
						newWSlices[nW][nX][nY][nZ] = newWSlices[nW][nX][nY][nZ].add(share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newWSlices[1][x][y][z] = newWSlices[1][x][y][z].add(value);
		return toppled;
	}

	private static boolean topplePosition(BigInt[][][][] newWSlices, BigInt value, int x, int y, int z, BigInt[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
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
			BigInt n0Val = asymmetricNeighborValues[0], n1Val = asymmetricNeighborValues[1];
			int[] n0Coords = asymmetricNeighborCoords[0], n1Coords = asymmetricNeighborCoords[1];
			int n0W = n0Coords[0], n0X = n0Coords[1], n0Y = n0Coords[2], n0Z = n0Coords[3];
			int n1W = n1Coords[0], n1X = n1Coords[1], n1Y = n1Coords[2], n1Z = n1Coords[3];
			int n0Mult = asymmetricNeighborShareMultipliers[0], n1Mult = asymmetricNeighborShareMultipliers[1];
			int shareCount = neighborCount + 1;
			if (n0Val.equals(n1Val)) {
				// n0Val = n1Val < value
				BigInt toShare = value.subtract(n0Val); 
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newWSlices[n0W][n0X][n0Y][n0Z] = newWSlices[n0W][n0X][n0Y][n0Z].add(share.multiply(BigInt.valueOf(n0Mult)));
				newWSlices[n1W][n1X][n1Y][n1Z] = newWSlices[n1W][n1X][n1Y][n1Z].add(share.multiply(BigInt.valueOf(n1Mult)));
				newWSlices[1][x][y][z] = newWSlices[1][x][y][z].add(value.subtract(toShare).add(share).add(shareAndRemainder[1]));
			} else if (n0Val.compareTo(n1Val) < 0) {
				// n0Val < n1Val < value
				BigInt toShare = value.subtract(n1Val); 
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newWSlices[n0W][n0X][n0Y][n0Z] = newWSlices[n0W][n0X][n0Y][n0Z].add(share.multiply(BigInt.valueOf(n0Mult)));
				newWSlices[n1W][n1X][n1Y][n1Z] = newWSlices[n1W][n1X][n1Y][n1Z].add(share.multiply(BigInt.valueOf(n1Mult)));
				shareCount -= asymmetricNeighborSymmetryCounts[1];
				BigInt currentRemainingValue = value.subtract(share.multiply(BigInt.valueOf(neighborCount)));
				toShare = currentRemainingValue.subtract(n0Val);
				shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newWSlices[n0W][n0X][n0Y][n0Z] = newWSlices[n0W][n0X][n0Y][n0Z].add(share.multiply(BigInt.valueOf(n0Mult)));
				newWSlices[1][x][y][z] = newWSlices[1][x][y][z].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
			} else {
				// n1Val < n0Val < value
				BigInt toShare = value.subtract(n0Val); 
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newWSlices[n0W][n0X][n0Y][n0Z] = newWSlices[n0W][n0X][n0Y][n0Z].add(share.multiply(BigInt.valueOf(n0Mult)));
				newWSlices[n1W][n1X][n1Y][n1Z] = newWSlices[n1W][n1X][n1Y][n1Z].add(share.multiply(BigInt.valueOf(n1Mult)));
				shareCount -= asymmetricNeighborSymmetryCounts[0];
				BigInt currentRemainingValue = value.subtract(share.multiply(BigInt.valueOf(neighborCount)));
				toShare = currentRemainingValue.subtract(n1Val);
				shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newWSlices[n1W][n1X][n1Y][n1Z] = newWSlices[n1W][n1X][n1Y][n1Z].add(share.multiply(BigInt.valueOf(n1Mult)));
				newWSlices[1][x][y][z] = newWSlices[1][x][y][z].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
			}				
			break;
		case 1:
			shareCount = neighborCount + 1;
			BigInt toShare = value.subtract(asymmetricNeighborValues[0]);
			BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));BigInt share = shareAndRemainder[0];
			if (!share.equals(BigInt.ZERO)) {
				toppled = true;
				value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
				int[] nc = asymmetricNeighborCoords[0];int nW = nc[0], nX = nc[1], nY = nc[2], nZ = nc[3];
				newWSlices[nW][nX][nY][nZ] = newWSlices[nW][nX][nY][nZ].add(share.multiply(BigInt.valueOf(asymmetricNeighborShareMultipliers[0])));
			}
			// no break
		case 0:
			newWSlices[1][x][y][z] = newWSlices[1][x][y][z].add(value);
			break;
		default: // 8, 7, 6, 5, 4
			Utils.sortDescending(asymmetricNeighborCount, asymmetricNeighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, asymmetricNeighborCoords, 
					asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}

	private static boolean topplePositionSortedNeighbors(BigInt[][][][] newWSlices, BigInt value, int x, int y, int z, BigInt[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		BigInt neighborValue = asymmetricNeighborValues[0];
		BigInt toShare = value.subtract(neighborValue);
		BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));BigInt share = shareAndRemainder[0];
		if (!share.equals(BigInt.ZERO)) {
			toppled = true;
			value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
			for (int j = 0; j < asymmetricNeighborCount; j++) {
				int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];int nW = nc[0], nX = nc[1], nY = nc[2], nZ = nc[3];
				newWSlices[nW][nX][nY][nZ] = newWSlices[nW][nX][nY][nZ].add(share.multiply(BigInt.valueOf(asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]])));
			}
		}
		BigInt previousNeighborValue = neighborValue;
		shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[0]];
		for (int i = 1; i < asymmetricNeighborCount; i++) {
			neighborValue = asymmetricNeighborValues[i];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
					value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
					for (int j = i; j < asymmetricNeighborCount; j++) {
						int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];int nW = nc[0], nX = nc[1], nY = nc[2], nZ = nc[3];
						newWSlices[nW][nX][nY][nZ] = newWSlices[nW][nX][nY][nZ].add(share.multiply(BigInt.valueOf(asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]])));
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		newWSlices[1][x][y][z] = newWSlices[1][x][y][z].add(value);
		return toppled;
	}

	private static boolean topplePosition(BigInt[][][][] newWSlices, BigInt value, int x, int y, int z, BigInt[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		boolean toppled = false;
		switch (neighborCount) {
		case 3:
			Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, neighborValues, sortedNeighborsIndexes, 
					neighborCoords, neighborShareMultipliers, 3);
			break;
		case 2:
			BigInt n0Val = neighborValues[0], n1Val = neighborValues[1];
			int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
			int n0W = n0Coords[0], n0X = n0Coords[1], n0Y = n0Coords[2], n0Z = n0Coords[3];
			int n1W = n1Coords[0], n1X = n1Coords[1], n1Y = n1Coords[2], n1Z = n1Coords[3];
			int n0Mult = neighborShareMultipliers[0], n1Mult = neighborShareMultipliers[1];
			if (n0Val.equals(n1Val)) {
				// n0Val = n1Val < value
				BigInt toShare = value.subtract(n0Val); 
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(three);BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newWSlices[n0W][n0X][n0Y][n0Z] = newWSlices[n0W][n0X][n0Y][n0Z].add(share.multiply(BigInt.valueOf(n0Mult)));
				newWSlices[n1W][n1X][n1Y][n1Z] = newWSlices[n1W][n1X][n1Y][n1Z].add(share.multiply(BigInt.valueOf(n1Mult)));
				newWSlices[1][x][y][z] = newWSlices[1][x][y][z].add(value.subtract(toShare).add(share).add(shareAndRemainder[1]));
			} else if (n0Val.compareTo(n1Val) < 0) {
				// n0Val < n1Val < value
				BigInt toShare = value.subtract(n1Val); 
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(three);BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newWSlices[n0W][n0X][n0Y][n0Z] = newWSlices[n0W][n0X][n0Y][n0Z].add(share.multiply(BigInt.valueOf(n0Mult)));
				newWSlices[n1W][n1X][n1Y][n1Z] = newWSlices[n1W][n1X][n1Y][n1Z].add(share.multiply(BigInt.valueOf(n1Mult)));
				BigInt currentRemainingValue = value.subtract(share.multiply(BigInt.valueOf(neighborCount)));
				toShare = currentRemainingValue.subtract(n0Val);
				shareAndRemainder = toShare.divideAndRemainder(two);share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newWSlices[n0W][n0X][n0Y][n0Z] = newWSlices[n0W][n0X][n0Y][n0Z].add(share.multiply(BigInt.valueOf(n0Mult)));
				newWSlices[1][x][y][z] = newWSlices[1][x][y][z].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
			} else {
				// n1Val < n0Val < value
				BigInt toShare = value.subtract(n0Val); 
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(three);BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newWSlices[n0W][n0X][n0Y][n0Z] = newWSlices[n0W][n0X][n0Y][n0Z].add(share.multiply(BigInt.valueOf(n0Mult)));
				newWSlices[n1W][n1X][n1Y][n1Z] = newWSlices[n1W][n1X][n1Y][n1Z].add(share.multiply(BigInt.valueOf(n1Mult)));
				BigInt currentRemainingValue = value.subtract(share.multiply(BigInt.valueOf(neighborCount)));
				toShare = currentRemainingValue.subtract(n1Val);
				shareAndRemainder = toShare.divideAndRemainder(two);share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newWSlices[n1W][n1X][n1Y][n1Z] = newWSlices[n1W][n1X][n1Y][n1Z].add(share.multiply(BigInt.valueOf(n1Mult)));
				newWSlices[1][x][y][z] = newWSlices[1][x][y][z].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
			}				
			break;
		case 1:
			BigInt toShare = value.subtract(neighborValues[0]);
			BigInt[] shareAndRemainder = toShare.divideAndRemainder(two);BigInt share = shareAndRemainder[0];
			if (!share.equals(BigInt.ZERO)) {
				toppled = true;
				value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
				int[] nc = neighborCoords[0];int nW = nc[0], nX = nc[1], nY = nc[2], nZ = nc[3];
				newWSlices[nW][nX][nY][nZ] = newWSlices[nW][nX][nY][nZ].add(share.multiply(BigInt.valueOf(neighborShareMultipliers[0])));
			}
			// no break
		case 0:
			newWSlices[1][x][y][z] = newWSlices[1][x][y][z].add(value);
			break;
		default: // 8, 7, 6, 5, 4
			Utils.sortDescending(neighborCount, neighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, neighborValues, sortedNeighborsIndexes, neighborCoords, 
					neighborShareMultipliers, neighborCount);
		}
		return toppled;
	}

	private static boolean topplePositionSortedNeighbors(BigInt[][][][] newWSlices, BigInt value, int x, int y, int z, BigInt[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		BigInt neighborValue = neighborValues[0];
		BigInt toShare = value.subtract(neighborValue);
		BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));BigInt share = shareAndRemainder[0];
		if (!share.equals(BigInt.ZERO)) {
			toppled = true;
			value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
			for (int j = 0; j < neighborCount; j++) {
				int[] nc = neighborCoords[sortedNeighborsIndexes[j]];int nW = nc[0], nX = nc[1], nY = nc[2], nZ = nc[3];
				newWSlices[nW][nX][nY][nZ] = newWSlices[nW][nX][nY][nZ].add(share.multiply(BigInt.valueOf(neighborShareMultipliers[sortedNeighborsIndexes[j]])));
			}
		}
		BigInt previousNeighborValue = neighborValue;
		shareCount--;
		for (int i = 1; i < neighborCount; i++) {
			neighborValue = neighborValues[i];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
					value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
					for (int j = i; j < neighborCount; j++) {
						int[] nc = neighborCoords[sortedNeighborsIndexes[j]];int nW = nc[0], nX = nc[1], nY = nc[2], nZ = nc[3];
						newWSlices[nW][nX][nY][nZ] = newWSlices[nW][nX][nY][nZ].add(share.multiply(BigInt.valueOf(neighborShareMultipliers[sortedNeighborsIndexes[j]])));
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newWSlices[1][x][y][z] = newWSlices[1][x][y][z].add(value);
		return toppled;
	}

	private static boolean topplePosition(BigInt[][][][] newWSlices, BigInt value, int x, int y, int z, BigInt[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
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
			BigInt n0Val = asymmetricNeighborValues[0], n1Val = asymmetricNeighborValues[1];
			int[] n0Coords = asymmetricNeighborCoords[0], n1Coords = asymmetricNeighborCoords[1];
			int n0W = n0Coords[0], n0X = n0Coords[1], n0Y = n0Coords[2], n0Z = n0Coords[3];
			int n1W = n1Coords[0], n1X = n1Coords[1], n1Y = n1Coords[2], n1Z = n1Coords[3];
			int shareCount = neighborCount + 1;
			if (n0Val.equals(n1Val)) {
				// n0Val = n1Val < value
				BigInt toShare = value.subtract(n0Val); 
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newWSlices[n0W][n0X][n0Y][n0Z] = newWSlices[n0W][n0X][n0Y][n0Z].add(share);
				newWSlices[n1W][n1X][n1Y][n1Z] = newWSlices[n1W][n1X][n1Y][n1Z].add(share);
				newWSlices[1][x][y][z] = newWSlices[1][x][y][z].add(value.subtract(toShare).add(share).add(shareAndRemainder[1]));
			} else if (n0Val.compareTo(n1Val) < 0) {
				// n0Val < n1Val < value
				BigInt toShare = value.subtract(n1Val); 
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newWSlices[n0W][n0X][n0Y][n0Z] = newWSlices[n0W][n0X][n0Y][n0Z].add(share);
				newWSlices[n1W][n1X][n1Y][n1Z] = newWSlices[n1W][n1X][n1Y][n1Z].add(share);
				shareCount -= asymmetricNeighborSymmetryCounts[1];
				BigInt currentRemainingValue = value.subtract(share.multiply(BigInt.valueOf(neighborCount)));
				toShare = currentRemainingValue.subtract(n0Val);
				shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newWSlices[n0W][n0X][n0Y][n0Z] = newWSlices[n0W][n0X][n0Y][n0Z].add(share);
				newWSlices[1][x][y][z] = newWSlices[1][x][y][z].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
			} else {
				// n1Val < n0Val < value
				BigInt toShare = value.subtract(n0Val); 
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newWSlices[n0W][n0X][n0Y][n0Z] = newWSlices[n0W][n0X][n0Y][n0Z].add(share);
				newWSlices[n1W][n1X][n1Y][n1Z] = newWSlices[n1W][n1X][n1Y][n1Z].add(share);
				shareCount -= asymmetricNeighborSymmetryCounts[0];
				BigInt currentRemainingValue = value.subtract(share.multiply(BigInt.valueOf(neighborCount)));
				toShare = currentRemainingValue.subtract(n1Val);
				shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newWSlices[n1W][n1X][n1Y][n1Z] = newWSlices[n1W][n1X][n1Y][n1Z].add(share);
				newWSlices[1][x][y][z] = newWSlices[1][x][y][z].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
			}				
			break;
		case 1:
			shareCount = neighborCount + 1;
			BigInt toShare = value.subtract(asymmetricNeighborValues[0]);
			BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));BigInt share = shareAndRemainder[0];
			if (!share.equals(BigInt.ZERO)) {
				toppled = true;
				value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
				int[] nc = asymmetricNeighborCoords[0];int nW = nc[0], nX = nc[1], nY = nc[2], nZ = nc[3];
				newWSlices[nW][nX][nY][nZ] = newWSlices[nW][nX][nY][nZ].add(share);
			}
			// no break
		case 0:
			newWSlices[1][x][y][z] = newWSlices[1][x][y][z].add(value);
			break;
		default: // 8, 7, 6, 5, 4
			Utils.sortDescending(asymmetricNeighborCount, asymmetricNeighborValues, sortedNeighborsIndexes);
			toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, asymmetricNeighborCoords, 
					asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}

	private static boolean topplePositionSortedNeighbors(BigInt[][][][] newWSlices, BigInt value, int x, int y, int z, BigInt[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		BigInt neighborValue = asymmetricNeighborValues[0];
		BigInt toShare = value.subtract(neighborValue);
		BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));BigInt share = shareAndRemainder[0];
		if (!share.equals(BigInt.ZERO)) {
			toppled = true;
			value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
			for (int j = 0; j < asymmetricNeighborCount; j++) {
				int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];int nW = nc[0], nX = nc[1], nY = nc[2], nZ = nc[3];
				newWSlices[nW][nX][nY][nZ] = newWSlices[nW][nX][nY][nZ].add(share);
			}
		}
		BigInt previousNeighborValue = neighborValue;
		shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[0]];
		for (int i = 1; i < asymmetricNeighborCount; i++) {
			neighborValue = asymmetricNeighborValues[i];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
					value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
					for (int j = i; j < asymmetricNeighborCount; j++) {
						int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];int nW = nc[0], nX = nc[1], nY = nc[2], nZ = nc[3];
						newWSlices[nW][nX][nY][nZ] = newWSlices[nW][nX][nY][nZ].add(share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		newWSlices[1][x][y][z] = newWSlices[1][x][y][z].add(value);
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
	public BigInt getIntialValue() {
		return initialValue;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		SerializableModelData data = new SerializableModelData();
		data.put(SerializableModelData.MODEL, SerializableModelData.Models.AETHER);
		data.put(SerializableModelData.INITIAL_CONFIGURATION, initialValue);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_TYPE, SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE, SerializableModelData.InitialConfigurationImplementationTypes.BIG_INT);
		data.put(SerializableModelData.GRID, sourceGrid);
		data.put(SerializableModelData.GRID_TYPE, SerializableModelData.GridTypes.INFINITE_REGULAR);
		data.put(SerializableModelData.GRID_DIMENSION, 4);
		data.put(SerializableModelData.GRID_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BIG_INT_ARRAY_1);
		data.put(SerializableModelData.COORDINATE_BOUNDS, maxW);
		data.put(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE, SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER);
		data.put(SerializableModelData.STEP, step);
		data.put(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP, changed);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_FOLDER_NAME, folderName);
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
		return getName() + "/4D/" + folderName + "/toppling_alternation_compliance";
	}

}