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
import cellularautomata.model4d.IsotropicHypercubicIntArrayModel4DA;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 4D with a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class IntAether4D extends IsotropicHypercubicIntArrayModel4DA {
	
	public static final int MAX_INITIAL_VALUE = Integer.MAX_VALUE;
	public static final int MIN_INITIAL_VALUE = -613566757;
	
	private final int initialValue;
	private long step;
	private int maxW;
	private Boolean changed = null;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public IntAether4D(int initialValue) {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of int type
			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
		}
		this.initialValue = initialValue;
		grid = Utils.buildAnisotropic4DIntArray(8);
		grid[0][0][0][0] = this.initialValue;
		maxW = 5;
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
	public IntAether4D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		SerializableModelData data = (SerializableModelData) Utils.deserializeFromFile(backupPath);
		data = SerializableModelData.updateDataFormat(data);
		if (!SerializableModelData.Models.AETHER.equals(data.get(SerializableModelData.MODEL))) {
			throw new IllegalArgumentException("The backup file contains a different model.");
		}
		if (!SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_TYPE))
				|| !SerializableModelData.InitialConfigurationImplementationTypes.INTEGER.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.GridTypes.INFINITE_REGULAR.equals(data.get(SerializableModelData.GRID_TYPE))
				|| !Integer.valueOf(4).equals(data.get(SerializableModelData.GRID_DIMENSION))
				|| !SerializableModelData.GridImplementationTypes.ANYSOTROPIC_INT_ARRAY_1.equals(data.get(SerializableModelData.GRID_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER.equals(data.get(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE))
				|| !data.contains(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP)) {
			throw new IllegalArgumentException("The backup file's configuration is not compatible with this class.");
		}
		initialValue = (int) data.get(SerializableModelData.INITIAL_CONFIGURATION);
		grid = (int[][][][]) data.get(SerializableModelData.GRID);
		maxW = (int) data.get(SerializableModelData.COORDINATE_BOUNDS);
		step = (long) data.get(SerializableModelData.STEP);
		changed = (Boolean) data.get(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP);
	}

	@Override
	public Boolean nextStep() {
		int[][][][] newGrid = new int[maxW + 3][][][];
		boolean changed = false;
		int[][][] smallerWSlice = null, currentWSlice = grid[0], greaterWSlice = grid[1];
		int[][][] newSmallerWSlice = null, 
				newCurrentWSlice = Utils.buildAnisotropic3DIntArray(1), 
				newGreaterWSlice = Utils.buildAnisotropic3DIntArray(2);// build new grid progressively to save memory
		newGrid[0] = newCurrentWSlice;
		newGrid[1] = newGreaterWSlice;
		// w = 0, x = 0, y = 0, z = 0
		int currentValue = currentWSlice[0][0][0];
		int greaterWNeighborValue = greaterWSlice[0][0][0];
		if (topplePositionOfType1(currentValue, greaterWNeighborValue, newCurrentWSlice, newGreaterWSlice)) {
			changed = true;
		}
		//w slice transition
		// smallerWSlice = currentWSlice; // not needed here
		currentWSlice = greaterWSlice;
		greaterWSlice = grid[2];
		newSmallerWSlice = newCurrentWSlice;
		newCurrentWSlice = newGreaterWSlice;
		newGreaterWSlice = Utils.buildAnisotropic3DIntArray(3);
		newGrid[2] = newGreaterWSlice;
		int[][][][] newWSlices = new int[][][][] { newSmallerWSlice, newCurrentWSlice, newGreaterWSlice};
		int[] relevantAsymmetricNeighborValues = new int[8];
		int[] sortedNeighborsIndexes = new int[8];
		int[][] relevantAsymmetricNeighborCoords = new int[8][4];
		int[] relevantAsymmetricNeighborShareMultipliers = new int[8];// to compensate for omitted symmetric positions
		int[] relevantAsymmetricNeighborSymmetryCounts = new int[8];// to compensate for omitted symmetric positions
		// w = 1, x = 0, y = 0, z = 0
		// reuse values obtained previously
		int smallerWNeighborValue = currentValue;
		currentValue = greaterWNeighborValue;
		greaterWNeighborValue = greaterWSlice[0][0][0];
		int greaterXNeighborValue = currentWSlice[1][0][0];
		if (topplePositionOfType2(currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		// w = 1, x = 1, y = 0, z = 0
		// reuse values obtained previously
		int smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterWNeighborValue = greaterWSlice[1][0][0];
		int greaterYNeighborValue = currentWSlice[1][1][0];
		if (topplePositionOfType3(currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		// w = 1, x = 1, y = 1, z = 0
		// reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice[1][1][0];
		int greaterZNeighborValue = currentWSlice[1][1][1];
		if (topplePositionOfType4(currentValue, greaterWNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		// w = 1, x = 1, y = 1, z = 1
		// reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[1][1][1];
		if (topplePositionOfType5(currentValue, greaterWNeighborValue, smallerZNeighborValue, newCurrentWSlice, newGreaterWSlice)) {
			changed = true;
		}
		//w slice transition
		grid[0] = null;// free old grid progressively to save memory
		smallerWSlice = currentWSlice;
		currentWSlice = greaterWSlice;
		greaterWSlice = grid[3];
		int[][][][] wSlices = new int[][][][] { smallerWSlice, currentWSlice, greaterWSlice};
		newSmallerWSlice = newCurrentWSlice;
		newCurrentWSlice = newGreaterWSlice;
		newGreaterWSlice = Utils.buildAnisotropic3DIntArray(4);
		newGrid[3] = newGreaterWSlice;
		newWSlices[0] = newSmallerWSlice;
		newWSlices[1] = newCurrentWSlice;
		newWSlices[2] = newGreaterWSlice;
		// w = 2, x = 0, y = 0, z = 0
		currentValue = currentWSlice[0][0][0];
		greaterWNeighborValue = greaterWSlice[0][0][0];
		smallerWNeighborValue = smallerWSlice[0][0][0];
		greaterXNeighborValue = currentWSlice[1][0][0];
		if (topplePositionOfType6(currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		// w = 2, x = 1, y = 0, z = 0
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
		}
		// w = 2, x = 1, y = 1, z = 0
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
		}
		// w = 2, x = 1, y = 1, z = 1
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
		}
		// w = 2, x = 2, y = 0, z = 0
		currentValue = currentWSlice[2][0][0];
		greaterWNeighborValue = greaterWSlice[2][0][0];
		smallerXNeighborValue = currentWSlice[1][0][0];
		greaterYNeighborValue = currentWSlice[2][1][0];
		if (topplePositionOfType10(2, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		// w = 2, x = 2, y = 1, z = 0
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
		}
		// w = 2, x = 2, y = 1, z = 1
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
		}
		// w = 2, x = 2, y = 2, z = 0
		// reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;		
		currentValue = currentWSlice[2][2][0];
		greaterWNeighborValue = greaterWSlice[2][2][0];
		if (topplePositionOfType13(2, currentValue, greaterWNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		// w = 2, x = 2, y = 2, z = 1
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
		}
		// 02 | 02 | 02 | 02 | 15
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[2][2][2];
		if (topplePositionOfType15(2, currentValue, greaterWNeighborValue, smallerZNeighborValue, newCurrentWSlice, newGreaterWSlice)) {
			changed = true;
		}		
		//w slice transition
		grid[1] = null;// free old grid progressively to save memory
		smallerWSlice = currentWSlice;
		currentWSlice = greaterWSlice;
		greaterWSlice = grid[4];
		wSlices[0] = smallerWSlice;
		wSlices[1] = currentWSlice;
		wSlices[2] = greaterWSlice;
		newSmallerWSlice = newCurrentWSlice;
		newCurrentWSlice = newGreaterWSlice;
		newGreaterWSlice = Utils.buildAnisotropic3DIntArray(5);
		newGrid[4] = newGreaterWSlice;
		newWSlices[0] = newSmallerWSlice;
		newWSlices[1] = newCurrentWSlice;
		newWSlices[2] = newGreaterWSlice;
		if (toppleRangeOfType1(wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
	// 03 | 02 | 00 | 00 | 19
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
		}
	// 03 | 02 | 01 | 00 | 20
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
		}
	// 03 | 02 | 01 | 01 | 21
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
		}
	// 03 | 02 | 02 | 00 | 22
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
		}
	// 03 | 02 | 02 | 01 | 23
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
		}
	// 03 | 02 | 02 | 02 | 24
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
		}
		if (toppleRangeOfType2(3, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
	// 03 | 03 | 02 | 00 | 27
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
		}
	// 03 | 03 | 02 | 01 | 28
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
		}
	// 03 | 03 | 02 | 02 | 29
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
		}
		if (toppleRangeOfType3(3, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}

		//w slice transition
		grid[2] = null;// free old grid progressively to save memory
		smallerWSlice = currentWSlice;
		currentWSlice = greaterWSlice;
		greaterWSlice = grid[5];
		wSlices[0] = smallerWSlice;
		wSlices[1] = currentWSlice;
		wSlices[2] = greaterWSlice;
		newSmallerWSlice = newCurrentWSlice;
		newCurrentWSlice = newGreaterWSlice;
		newGreaterWSlice = Utils.buildAnisotropic3DIntArray(6);
		newGrid[5] = newGreaterWSlice;
		newWSlices[0] = newSmallerWSlice;
		newWSlices[1] = newCurrentWSlice;
		newWSlices[2] = newGreaterWSlice;
		
		if (toppleRangeOfType4(wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		if (toppleRangeOfType5(3, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		
	// 04 | 03 | 02 | 00 | 40
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
		}
	// 04 | 03 | 02 | 01 | 41
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
		}
	// 04 | 03 | 02 | 02 | 42
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
		}
		if (toppleRangeOfType6(3, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		if (toppleRangeOfType7(4, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}	
		// 5 <= w < edge - 2
		int edge = grid.length - 1;
		int edgeMinusTwo = edge - 2;
		if (toppleRangeBeyondW4(wSlices, newWSlices, newGrid, 5, edgeMinusTwo, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts)) { // is it faster to reuse these arrays?
			changed = true;
		}
		//edge - 2 <= w < edge
		if (toppleRangeBeyondW4(wSlices, newWSlices, newGrid, edgeMinusTwo, edge, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts)) {
			changed = true;
			maxW++;
		}
		if (newGrid.length > grid.length) {
			newGrid[grid.length] = Utils.buildAnisotropic3DIntArray(newGrid.length);
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

	private boolean toppleRangeBeyondW4(int[][][][] wSlices, int[][][][] newWSlices, int[][][][] newGrid, int minW,
			int maxW, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords,
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts) {
		boolean changed = false;
		int w = minW, wMinusOne = w - 1, wMinusTwo = w - 2, wMinusThree = w - 3, wPlusOne = w + 1, wPlusTwo = w + 2;
		int[][][] smallerWSlice = null, currentWSlice = wSlices[1], greaterWSlice = wSlices[2];
		int[][][] newSmallerWSlice = null, newCurrentWSlice = newWSlices[1], newGreaterWSlice = newWSlices[2];
		for (; w != maxW; wMinusThree = wMinusTwo, wMinusTwo = wMinusOne, wMinusOne = w, w = wPlusOne, wPlusOne = wPlusTwo, wPlusTwo++) {
			//w slice transition
			grid[wMinusTwo] = null;// free old grid progressively to save memory
			smallerWSlice = currentWSlice;
			currentWSlice = greaterWSlice;
			greaterWSlice = grid[wPlusOne];
			wSlices[0] = smallerWSlice;
			wSlices[1] = currentWSlice;
			wSlices[2] = greaterWSlice;
			newSmallerWSlice = newCurrentWSlice;
			newCurrentWSlice = newGreaterWSlice;
			newGreaterWSlice = Utils.buildAnisotropic3DIntArray(wPlusTwo);
			newGrid[wPlusOne] = newGreaterWSlice;
			newWSlices[0] = newSmallerWSlice;
			newWSlices[1] = newCurrentWSlice;
			newWSlices[2] = newGreaterWSlice;
			if (toppleRangeOfType4(wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			if (toppleRangeOfType8(3, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
	//  w | 03 | 02 | 00 | 53
			int currentValue = currentWSlice[3][2][0];
			int greaterWNeighborValue = greaterWSlice[3][2][0];
			int smallerWNeighborValue = smallerWSlice[3][2][0];
			int greaterXNeighborValue = currentWSlice[4][2][0];
			int smallerXNeighborValue = currentWSlice[2][2][0];
			int greaterYNeighborValue = currentWSlice[3][3][0];
			int smallerYNeighborValue = currentWSlice[3][1][0];
			int greaterZNeighborValue = currentWSlice[3][2][1];
			if (topplePositionOfType16(3, 2, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
					smallerYNeighborValue, 1, greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				changed = true;
			}
	//  w | 03 | 02 | 01 | 54
			// reuse values obtained previously
			int smallerZNeighborValue = currentValue;
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
			}
	//  w | 03 | 02 | 02 | 55
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
			}
			if (toppleRangeOfType9(3, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			int x = 4, xPlusOne = 5, xMinusOne = 3;
			for (int xMinusTwo = 2; x != wMinusOne; xMinusTwo = xMinusOne, xMinusOne = x, x = xPlusOne, xPlusOne++) {
				if (toppleRangeOfType8(x, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
					changed = true;
				}
	//  w |  x | 02 | 00 | 67
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
				}
	//  w |  x | 02 | 01 | 68
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
				}
	//  w |  x | 02 | 02 | 69
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
				}
				int y = 3, yMinusOne = 2, yPlusOne = 4;
				for (; y != xMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
	//  w |  x |  y | 00 | 67
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
					}
	//  w |  x |  y | 01 | 77
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
					}
					int z = 2, zPlusOne = 3;
					for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
	//  w |  x |  y |  z | 81
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
						}
					}
	//  w |  x |  y |  z | 78
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
					}
	//  w |  x |  y |++z | 69
					z = zPlusOne;
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
					}
				}
	//  w |  x |  y | 00 | 53
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
				}
	//  w |  x |  y | 01 | 70
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
				}
				int z = 2, zPlusOne = 3;
				for (; z != xMinusTwo; z = zPlusOne, zPlusOne++) {
	//  w |  x |  y |  z | 79
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
					}
				}
	//  w |  x |  y |  z | 71
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
				}
	//  w |  x |  y |++z | 55
				z = zPlusOne;
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
				}
				if (toppleRangeOfType9(x, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
					changed = true;
				}
			}
			if (toppleRangeOfType5(x, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
	//  w |  x | 02 | 00 | 58
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
			}
	//  w |  x | 02 | 01 | 59
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
			}
	//  w |  x | 02 | 02 | 60
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
			}
			int y = 3, yPlusOne = 4, yMinusOne = 2;
			for (; y != wMinusTwo; yMinusOne = y, y = yPlusOne, yPlusOne++) {
	//  w |  x |  y | 00 | 58
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
				}
	//  w |  x |  y | 01 | 73
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
				}
				int z = 2, zPlusOne = 3;
				for (; z != yMinusOne; z = zPlusOne, zPlusOne++) {
	//  w |  x |  y |  z | 80
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
					}
				}
	//  w |  x |  y |  z | 74
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
				}
	//  w |  x |  y |++z | 60
				z = zPlusOne;
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
				}
			}
	//  w |  x |  y | 00 | 40
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
			}
	//  w |  x |  y | 01 | 61
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
			}
			int z = 2, zPlusOne = 3;
			for (; z != wMinusThree; z = zPlusOne, zPlusOne++) {
	//  w |  x |  y |  z | 75
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
				}
			}
	//  w |  x |  y |  z | 62
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
			}
	//  w |  x |  y |++z | 42
			z = zPlusOne;
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
			}			
			if (toppleRangeOfType6(x, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
			xMinusOne = x;
			x = xPlusOne;
			for (y = 3, yMinusOne = 2, yPlusOne = 4; y != wMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
	//  w |  x |  y | 00 | 45
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
				}
	//  w |  x |  y | 01 | 64
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
				}
				for (z = 2, zPlusOne = 3; z != yMinusOne; z = zPlusOne, zPlusOne++) {
	//  w |  x |  y |  z | 76
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
					}
				}
	//  w |  x |  y |  z | 65
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
				}
	//  w |  x |  y |++z | 47
				z = zPlusOne;
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
				}
			}
			if (toppleRangeOfType7(x, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes,
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
				changed = true;
			}
		}		
		wSlices[1] = currentWSlice;
		wSlices[2] = greaterWSlice;
		newWSlices[1] = newCurrentWSlice;
		newWSlices[2] = newGreaterWSlice;
		return changed;
	}

	private static boolean toppleRangeOfType1(int[][][][] wSlices, int[][][][] newWSlices, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		boolean changed = false;
		int[][][] smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];
	//  w | 00 | 00 | 00 | 06
		int currentValue = currentWSlice[0][0][0];
		int greaterWNeighborValue = greaterWSlice[0][0][0];
		int smallerWNeighborValue = smallerWSlice[0][0][0];
		int greaterXNeighborValue = currentWSlice[1][0][0];
		if (topplePositionOfType6(currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}		
	//  w | 01 | 00 | 00 | 16
		// reuse values obtained previously
		int smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterWNeighborValue = greaterWSlice[1][0][0];
		smallerWNeighborValue = smallerWSlice[1][0][0];
		greaterXNeighborValue = currentWSlice[2][0][0];
		int greaterYNeighborValue = currentWSlice[1][1][0];
		if (topplePositionOfType7(1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 6, greaterYNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w | 01 | 01 | 00 | 17
		// reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice[1][1][0];
		smallerWNeighborValue = smallerWSlice[1][1][0];
		greaterXNeighborValue = currentWSlice[2][1][0];
		int greaterZNeighborValue = currentWSlice[1][1][1];
		if (topplePositionOfType8(1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 3, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w | 01 | 01 | 01 | 18
		// reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[1][1][1];
		smallerWNeighborValue = smallerWSlice[1][1][1];
		greaterXNeighborValue = currentWSlice[2][1][1];
		if (topplePositionOfType9(1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeOfType2(int x, int[][][][] wSlices, int[][][][] newWSlices, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int xMinusOne = x - 1;
		boolean changed = false;
		int[][][] currentWSlice = wSlices[1], greaterWSlice = wSlices[2];		
	//  w |  x | 00 | 00 | 10
		int currentValue = currentWSlice[x][0][0];
		int greaterWNeighborValue = greaterWSlice[x][0][0];
		int smallerXNeighborValue = currentWSlice[xMinusOne][0][0];
		int greaterYNeighborValue = currentWSlice[x][1][0];
		if (topplePositionOfType10(x, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x | 01 | 00 | 25
		// reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice[x][1][0];
		smallerXNeighborValue = currentWSlice[xMinusOne][1][0];
		greaterYNeighborValue = currentWSlice[x][2][0];
		int greaterZNeighborValue = currentWSlice[x][1][1];
		if (topplePositionOfType11(x, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x | 01 | 01 | 26
		// reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[x][1][1];
		smallerXNeighborValue = currentWSlice[xMinusOne][1][1];
		greaterYNeighborValue = currentWSlice[x][2][1];
		if (topplePositionOfType12(x, 1, currentValue, greaterWNeighborValue, smallerXNeighborValue, 1, greaterYNeighborValue, 1, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeOfType3(int coord, int[][][][] wSlices, int[][][][] newWSlices, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int coordMinusOne = coord - 1;
		boolean changed = false;
		int[][][] currentWSlice = wSlices[1], greaterWSlice = wSlices[2];		
	//  w |  x |  y | 00 | 13
		int currentValue = currentWSlice[coord][coord][0];
		int greaterWNeighborValue = greaterWSlice[coord][coord][0];
		int smallerYNeighborValue = currentWSlice[coord][coordMinusOne][0];
		int greaterZNeighborValue = currentWSlice[coord][coord][1];
		if (topplePositionOfType13(coord, currentValue, greaterWNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x |  y | 01 | 30
		// reuse values obtained previously
		int smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[coord][coord][1];
		smallerYNeighborValue = currentWSlice[coord][coordMinusOne][1];
		greaterZNeighborValue = currentWSlice[coord][coord][2];
		if (topplePositionOfType14(coord, 1, currentValue, greaterWNeighborValue, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
		int z = 2;
		int zPlusOne = 3;
		for (; z != coordMinusOne; z = zPlusOne, zPlusOne++) {
	//  w |  x |  y |  z | 50
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterWNeighborValue = greaterWSlice[coord][coord][z];
			smallerYNeighborValue = currentWSlice[coord][coordMinusOne][z];
			greaterZNeighborValue = currentWSlice[coord][coord][zPlusOne];
			if (topplePositionOfType26(coord, z, currentValue, greaterWNeighborValue, smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
				changed = true;
			}
		}
	//  w |  x |  y |  z | 31
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
		}
	//  w |  x |  y |++z | 15
		z = zPlusOne;
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterWNeighborValue = greaterWSlice[coord][coord][z];
		if (topplePositionOfType15(coord, currentValue, greaterWNeighborValue, smallerZNeighborValue, newWSlices[1], newWSlices[2])) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeOfType4(int[][][][] wSlices, int[][][][] newWSlices, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		boolean changed = false;
		if (toppleRangeOfType1(wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		int[][][] smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];
	//  w | 02 | 00 | 00 | 32
		int currentValue = currentWSlice[2][0][0];
		int greaterWNeighborValue = greaterWSlice[2][0][0];
		int smallerWNeighborValue = smallerWSlice[2][0][0];
		int greaterXNeighborValue = currentWSlice[3][0][0];
		int smallerXNeighborValue = currentWSlice[1][0][0];
		int greaterYNeighborValue = currentWSlice[2][1][0];
		if (topplePositionOfType20(2, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w | 02 | 01 | 00 | 33
		// reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice[2][1][0];
		smallerWNeighborValue = smallerWSlice[2][1][0];
		greaterXNeighborValue = currentWSlice[3][1][0];
		smallerXNeighborValue = currentWSlice[1][1][0];
		greaterYNeighborValue = currentWSlice[2][2][0];
		int greaterZNeighborValue = currentWSlice[2][1][1];
		if (topplePositionOfType16(2, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 2, greaterYNeighborValue, 2, 
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w | 02 | 01 | 01 | 34
		// reuse values obtained previously
		int smallerZNeighborValue = currentValue;
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
		}
	//  w | 02 | 02 | 01 | 36
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
		}
	//  w | 02 | 02 | 02 | 37
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
		}
		return changed;
	}

	private static boolean toppleRangeOfType5(int x, int[][][][] wSlices, int[][][][] newWSlices, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int xMinusOne = x - 1, xPlusOne = x + 1;
		boolean changed = false;
		int[][][] smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];		
	//  w |  x | 00 | 00 | 19
		int currentValue = currentWSlice[x][0][0];
		int greaterWNeighborValue = greaterWSlice[x][0][0];
		int smallerWNeighborValue = smallerWSlice[x][0][0];
		int greaterXNeighborValue = currentWSlice[xPlusOne][0][0];
		int smallerXNeighborValue = currentWSlice[xMinusOne][0][0];
		int greaterYNeighborValue = currentWSlice[x][1][0];
		if (topplePositionOfType7(x, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x | 01 | 00 | 38
		// reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice[x][1][0];
		smallerWNeighborValue = smallerWSlice[x][1][0];
		greaterXNeighborValue = currentWSlice[xPlusOne][1][0];
		smallerXNeighborValue = currentWSlice[xMinusOne][1][0];
		greaterYNeighborValue = currentWSlice[x][2][0];
		int greaterZNeighborValue = currentWSlice[x][1][1];
		if (topplePositionOfType16(x, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 2, greaterXNeighborValue, 2, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x | 01 | 01 | 39
		// reuse values obtained previously
		int smallerZNeighborValue = currentValue;
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
		}
		return changed;
	}

	private static boolean toppleRangeOfType6(int coord, int[][][][] wSlices, int[][][][] newWSlices, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int coordMinusOne = coord - 1, coordPlusOne = coord + 1;
		int[][][] smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];	
		boolean changed = false;		
	//  w |  x |  y | 00 | 22
		int currentValue = currentWSlice[coord][coord][0];
		int greaterWNeighborValue = greaterWSlice[coord][coord][0];
		int smallerWNeighborValue = smallerWSlice[coord][coord][0];
		int greaterXNeighborValue = currentWSlice[coordPlusOne][coord][0];
		int smallerYNeighborValue = currentWSlice[coord][coordMinusOne][0];
		int greaterZNeighborValue = currentWSlice[coord][coord][1];
		if (topplePositionOfType8(coord, currentValue, greaterWNeighborValue, smallerWNeighborValue, 3, greaterXNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x |  y | 01 | 43
		// reuse values obtained previously
		int smallerZNeighborValue = currentValue;
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
		}
		int z = 2;
		int zPlusOne = 3;
		for (; z != coordMinusOne; z = zPlusOne, zPlusOne++) {
	//  w |  x |  y |  z | 63
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
			}
		}
	//  w |  x |  y |  z | 44
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
		}
	//  w |  x |  y |++z | 24
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
		}
		coordMinusOne = coord;
		coord++;
		if (toppleRangeOfType2(coord, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}		
	//  w |  x | 02 | 00 | 45
		currentValue = currentWSlice[coord][2][0];
		greaterWNeighborValue = greaterWSlice[coord][2][0];
		int smallerXNeighborValue = currentWSlice[coordMinusOne][2][0];
		int greaterYNeighborValue = currentWSlice[coord][3][0];
		smallerYNeighborValue = currentWSlice[coord][1][0];
		greaterZNeighborValue = currentWSlice[coord][2][1];
		if (topplePositionOfType24(coord, 2, currentValue, greaterWNeighborValue, smallerXNeighborValue, greaterYNeighborValue, smallerYNeighborValue, 
				greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x | 02 | 01 | 46
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
		}
	//  w |  x | 02 | 02 | 47
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
		}
		return changed;
	}

	private static boolean toppleRangeOfType7(int x, int[][][][] wSlices, int[][][][] newWSlices, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {		
		int y = x - 1, xMinusOne = x - 1, xMinusTwo = x - 2, yPlusOne = y + 1, yMinusOne = y - 1;
		boolean changed = false;
		int[][][] currentWSlice = wSlices[1], greaterWSlice = wSlices[2];		
	//  w |  x |  y | 00 | 27
		int currentValue = currentWSlice[x][y][0];
		int greaterWNeighborValue = greaterWSlice[x][y][0];
		int smallerXNeighborValue = currentWSlice[xMinusOne][y][0];
		int greaterYNeighborValue = currentWSlice[x][yPlusOne][0];
		int smallerYNeighborValue = currentWSlice[x][yMinusOne][0];
		int greaterZNeighborValue = currentWSlice[x][y][1];
		if (topplePositionOfType11(x, y, currentValue, greaterWNeighborValue, smallerXNeighborValue, 2, greaterYNeighborValue, 3, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x |  y | 01 | 48
		// reuse values obtained previously
		int smallerZNeighborValue = currentValue;
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
		}
		int z = 2;
		int zPlusOne = 3;
		for (; z != xMinusTwo; z = zPlusOne, zPlusOne++) {
	//  w |  x |  y |  z | 66
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
			}
		}
	//  w |  x |  y |  z | 49
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
		}
	//  w |  x |  y |++z | 29
		z = zPlusOne;
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
		}		
		if (toppleRangeOfType3(x, wSlices, newWSlices, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantAsymmetricNeighborShareMultipliers)) {
			changed = true;
		}
		return changed;
	}

	private static boolean toppleRangeOfType8(int x, int[][][][] wSlices, int[][][][] newWSlices, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int xMinusOne = x - 1, xPlusOne = x + 1;
		boolean changed = false;
		int[][][] smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];		
	//  w |  x | 00 | 00 | 32
		int currentValue = currentWSlice[x][0][0];
		int greaterWNeighborValue = greaterWSlice[x][0][0];
		int smallerWNeighborValue = smallerWSlice[x][0][0];
		int greaterXNeighborValue = currentWSlice[xPlusOne][0][0];
		int smallerXNeighborValue = currentWSlice[xMinusOne][0][0];
		int greaterYNeighborValue = currentWSlice[x][1][0];
		if (topplePositionOfType20(x, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerXNeighborValue, 
				greaterYNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x | 01 | 00 | 51
		// reuse values obtained previously
		int smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterWNeighborValue = greaterWSlice[x][1][0];
		smallerWNeighborValue = smallerWSlice[x][1][0];
		greaterXNeighborValue = currentWSlice[xPlusOne][1][0];
		smallerXNeighborValue = currentWSlice[xMinusOne][1][0];
		greaterYNeighborValue = currentWSlice[x][2][0];
		int greaterZNeighborValue = currentWSlice[x][1][1];
		if (topplePositionOfType16(x, 1, currentValue, greaterWNeighborValue, smallerWNeighborValue, 1, greaterXNeighborValue, 1, smallerXNeighborValue, 1, greaterYNeighborValue, 1, 
				smallerYNeighborValue, 4, greaterZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x | 01 | 01 | 52
		// reuse values obtained previously
		int smallerZNeighborValue = currentValue;
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
		}
		return changed;
	}

	private static boolean toppleRangeOfType9(int coord, int[][][][] wSlices, int[][][][] newWSlices, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[] relevantAsymmetricNeighborShareMultipliers) {
		int coordMinusOne = coord - 1, coordPlusOne = coord + 1;
		boolean changed = false;
		int[][][] smallerWSlice = wSlices[0], currentWSlice = wSlices[1], greaterWSlice = wSlices[2];
	//  w |  x |  y | 00 | 35
		int currentValue = currentWSlice[coord][coord][0];
		int greaterWNeighborValue = greaterWSlice[coord][coord][0];
		int smallerWNeighborValue = smallerWSlice[coord][coord][0];
		int greaterXNeighborValue = currentWSlice[coordPlusOne][coord][0];
		int smallerYNeighborValue = currentWSlice[coord][coordMinusOne][0];
		int greaterZNeighborValue = currentWSlice[coord][coord][1];
		if (topplePositionOfType21(coord, currentValue, greaterWNeighborValue, smallerWNeighborValue, greaterXNeighborValue, smallerYNeighborValue, 
				greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords,
				relevantAsymmetricNeighborSymmetryCounts, newWSlices)) {
			changed = true;
		}
	//  w |  x |  y | 01 | 56
		// reuse values obtained previously
		int smallerZNeighborValue = currentValue;
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
		}
		int z = 2;
		int zPlusOne = 3;
		for (; z != coordMinusOne; z = zPlusOne, zPlusOne++) {
	//  w |  x |  y |  z | 72
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
			}
		}
	//  w |  x |  y |  z | 57
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
		}
	//  w |  x |  y |++z | 37
		z = zPlusOne;
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
		}
		return changed;
	}
	
	private static boolean topplePositionOfType1(int currentValue, int gWValue, int[][][] newCurrentWSlice, int[][][] newGreaterWSlice) {
		boolean toppled = false;
		if (gWValue < currentValue) {
			int toShare = currentValue - gWValue;
			int share = toShare/9;
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

	private static boolean topplePositionOfType2(int currentValue, int gWValue, int sWValue, int gXValue, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType3(int currentValue, int gWValue, int sXValue, int gYValue, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType4(int currentValue, int gWValue, int sYValue, int gZValue, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType5(int currentValue, int gWValue, int sZValue, int[][][] newCurrentWSlice, int[][][] newGreaterWSlice) {
		boolean toppled = false;
		if (sZValue < currentValue) {
			if (gWValue < currentValue) {
				if (sZValue == gWValue) {
					// gw = sz < current
					int toShare = currentValue - gWValue; 
					int share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice[1][1][0] += share + share;// one more for the symmetric position at the other side
					newCurrentWSlice[1][1][1] += currentValue - toShare + share + toShare%9;
					newGreaterWSlice[1][1][1] += share;
				} else if (sZValue < gWValue) {
					// sz < gw < current
					int toShare = currentValue - gWValue; 
					int share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice[1][1][0] += share + share;
					newGreaterWSlice[1][1][1] += share;
					int currentRemainingValue = currentValue - 8*share;
					toShare = currentRemainingValue - sZValue; 
					share = toShare/5;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice[1][1][0] += share + share;
					newCurrentWSlice[1][1][1] += currentRemainingValue - toShare + share + toShare%5;
				} else {
					// gw < sz < current
					int toShare = currentValue - sZValue; 
					int share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice[1][1][0] += share + share;
					newGreaterWSlice[1][1][1] += share;
					int currentRemainingValue = currentValue - 8*share;
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
				int toShare = currentValue - sZValue; 
				int share = toShare/5;
				if (share != 0) {
					toppled = true;
				}
				newCurrentWSlice[1][1][0] += share + share;
				newCurrentWSlice[1][1][1] += currentValue - toShare + share + toShare%5;
			}
		} else if (gWValue < currentValue) {
			// gw < current <= sz
			int toShare = currentValue - gWValue; 
			int share = toShare/5;
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

	private static boolean topplePositionOfType6(int currentValue, int gWValue, int sWValue, int gXValue, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType7(int x, int currentValue, int gWValue, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType8(int coord, int currentValue, int gWValue, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType9(int coord, int currentValue, int gWValue, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType10(int x, int currentValue, int gWValue, int sXValue, int gYValue, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType11(int x, int y, int currentValue, int gWValue, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType12(int x, int coord, int currentValue, int gWValue, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType13(int coord, int currentValue, int gWValue, int sYValue, int gZValue, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType14(int coord, int z, int currentValue, int gWValue, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType15(int coord, int currentValue, int gWValue, int sZValue, int[][][] newCurrentWSlice, int[][][] newGreaterWSlice) {
		 // 4*GW, 4*SZ | 15 | 15
		boolean toppled = false;
		if (sZValue < currentValue) {
			if (gWValue < currentValue) {
				if (sZValue == gWValue) {
					// gw = sz < current
					int toShare = currentValue - gWValue; 
					int share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice[coord][coord][coord - 1] += share;
					newCurrentWSlice[coord][coord][coord] += currentValue - toShare + share + toShare%9;
					newGreaterWSlice[coord][coord][coord] += share;
				} else if (sZValue < gWValue) {
					// sz < gw < current
					int coordMinusOne = coord - 1;
					int toShare = currentValue - gWValue; 
					int share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice[coord][coord][coordMinusOne] += share;
					newGreaterWSlice[coord][coord][coord] += share;
					int currentRemainingValue = currentValue - 8*share;
					toShare = currentRemainingValue - sZValue; 
					share = toShare/5;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice[coord][coord][coordMinusOne] += share;
					newCurrentWSlice[coord][coord][coord] += currentRemainingValue - toShare + share + toShare%5;
				} else {
					// gw < sz < current
					int toShare = currentValue - sZValue; 
					int share = toShare/9;
					if (share != 0) {
						toppled = true;
					}
					newCurrentWSlice[coord][coord][coord - 1] += share;
					newGreaterWSlice[coord][coord][coord] += share;
					int currentRemainingValue = currentValue - 8*share;
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
				int toShare = currentValue - sZValue; 
				int share = toShare/5;
				if (share != 0) {
					toppled = true;
				}
				newCurrentWSlice[coord][coord][coord - 1] += share;
				newCurrentWSlice[coord][coord][coord] += currentValue - toShare + share + toShare%5;
			}
		} else if (gWValue < currentValue) {
			// gw < current <= sz
			int toShare = currentValue - gWValue; 
			int share = toShare/5;
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

	private static boolean topplePositionOfType16(int x, int y, int currentValue, int gWValue, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType17(int x, int coord, int currentValue, int gWValue, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType18(int coord, int z, int currentValue, int gWValue, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType19(int x, int y, int z, int currentValue, int gWValue, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType20(int x, int currentValue, int gWValue, int sWValue, int gXValue, int sXValue, int gYValue, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType21(int coord, int currentValue, int gWValue, int sWValue, int gXValue, int sYValue, int gZValue, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType22(int coord, int currentValue, int gWValue, int sWValue, int gXValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType23(int x, int y, int z, int currentValue, int gWValue, int sWValue, int sWShareMultiplier, int gXValue, int gXShareMultiplier, int sXValue, int sXShareMultiplier, int gYValue, int gYShareMultiplier, int sYValue, int sYShareMultiplier, int gZValue, int gZShareMultiplier, int sZValue, int sZShareMultiplier, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType24(int x, int y, int currentValue, int gWValue, int sXValue, int gYValue, int sYValue, int gZValue, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType25(int x, int coord, int currentValue, int gWValue, int sXValue, int gYValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType26(int coord, int z, int currentValue, int gWValue, int sYValue, int gZValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType27(int x, int y, int currentValue, int gWValue, int sWValue, int gXValue, int sXValue, int gYValue, int sYValue, int gZValue, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType28(int x, int coord, int currentValue, int gWValue, int sWValue, int gXValue, int sXValue, int gYValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType29(int coord, int z, int currentValue, int gWValue, int sWValue, int gXValue, int sYValue, int gZValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType30(int x, int y, int z, int currentValue, int gWValue, int sXValue, int gYValue, int sYValue, int gZValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, int[][][][] newWSlices) {
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

	private static boolean topplePositionOfType31(int x, int y, int z, int currentValue, int gWValue, int sWValue, int gXValue, int sXValue, int gYValue, int sYValue, int gZValue, int sZValue, int[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[][][][] newWSlices) {
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
	
	private static boolean topplePosition(int[][][][] newWSlices, int value, int x, int y, int z, int[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int neighborCount) {
		boolean toppled = false;
		switch (neighborCount) {
			case 3:
				Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, neighborValues, sortedNeighborsIndexes, 
						neighborCoords, 3);
				break;
			case 2:
				int n0Val = neighborValues[0], n1Val = neighborValues[1];
				int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
				if (n0Val == n1Val) {
					// n0Val = n1Val < value
					int toShare = value - n0Val; 
					int share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share;
					newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share;
					newWSlices[1][x][y][z] += value - toShare + share + toShare%3;
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					int toShare = value - n1Val; 
					int share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share;
					newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share;
					int currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/2;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share;
					newWSlices[1][x][y][z] += currentRemainingValue - toShare + share + toShare%2;
				} else {
					// n1Val < n0Val < value
					int toShare = value - n0Val; 
					int share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share;
					newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share;
					int currentRemainingValue = value - neighborCount*share;
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
				int toShare = value - neighborValues[0];
				int share = toShare/2;
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
	
	private static boolean topplePositionSortedNeighbors(int[][][][] newWSlices, int value, int x, int y, int z, 
			int[] neighborValues, int[] sortedNeighborsIndexes, int[][] neighborCoords, int neighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		int neighborValue = neighborValues[0];
		int toShare = value - neighborValue;
		int share = toShare/shareCount;
		if (share != 0) {
			toppled = true;
			value = value - toShare + toShare%shareCount + share;
			for (int j = 0; j < neighborCount; j++) {
				int[] nc = neighborCoords[sortedNeighborsIndexes[j]];
				newWSlices[nc[0]][nc[1]][nc[2]][nc[3]] += share;
			}
		}
		int previousNeighborValue = neighborValue;
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
	
	private static boolean topplePosition(int[][][][] newWSlices, int value, int x, int y, int z, int[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
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
				int n0Val = asymmetricNeighborValues[0], n1Val = asymmetricNeighborValues[1];
				int[] n0Coords = asymmetricNeighborCoords[0], n1Coords = asymmetricNeighborCoords[1];
				int n0Mult = asymmetricNeighborShareMultipliers[0], n1Mult = asymmetricNeighborShareMultipliers[1];
				int shareCount = neighborCount + 1;
				if (n0Val == n1Val) {
					// n0Val = n1Val < value
					int toShare = value - n0Val; 
					int share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share*n0Mult;
					newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share*n1Mult;
					newWSlices[1][x][y][z] += value - toShare + share + toShare%shareCount;
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					int toShare = value - n1Val; 
					int share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share*n0Mult;
					newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share*n1Mult;
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					int currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share*n0Mult;
					newWSlices[1][x][y][z] += currentRemainingValue - toShare + share + toShare%shareCount;
				} else {
					// n1Val < n0Val < value
					int toShare = value - n0Val; 
					int share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share*n0Mult;
					newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share*n1Mult;
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					int currentRemainingValue = value - neighborCount*share;
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
				int toShare = value - asymmetricNeighborValues[0];
				int share = toShare/shareCount;
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
	
	private static boolean topplePositionSortedNeighbors(int[][][][] newWSlices, int value, int x, int y, int z, int[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		int neighborValue = asymmetricNeighborValues[0];
		int toShare = value - neighborValue;
		int share = toShare/shareCount;
		if (share != 0) {
			toppled = true;
			value = value - toShare + toShare%shareCount + share;
			for (int j = 0; j < asymmetricNeighborCount; j++) {
				int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];
				newWSlices[nc[0]][nc[1]][nc[2]][nc[3]] += share * asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]];
			}
		}
		int previousNeighborValue = neighborValue;
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
	
	private static boolean topplePosition(int[][][][] newWSlices, int value, int x, int y, int z, int[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		boolean toppled = false;
		switch (neighborCount) {
			case 3:
				Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newWSlices, value, x, y, z, neighborValues, sortedNeighborsIndexes, 
						neighborCoords, neighborShareMultipliers, 3);
				break;
			case 2:
				int n0Val = neighborValues[0], n1Val = neighborValues[1];
				int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
				int n0Mult = neighborShareMultipliers[0], n1Mult = neighborShareMultipliers[1];
				if (n0Val == n1Val) {
					// n0Val = n1Val < value
					int toShare = value - n0Val; 
					int share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share*n0Mult;
					newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share*n1Mult;
					newWSlices[1][x][y][z] += value - toShare + share + toShare%3;
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					int toShare = value - n1Val; 
					int share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share*n0Mult;
					newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share*n1Mult;
					int currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/2;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share*n0Mult;
					newWSlices[1][x][y][z] += currentRemainingValue - toShare + share + toShare%2;
				} else {
					// n1Val < n0Val < value
					int toShare = value - n0Val; 
					int share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share*n0Mult;
					newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share*n1Mult;
					int currentRemainingValue = value - neighborCount*share;
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
				int toShare = value - neighborValues[0];
				int share = toShare/2;
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
	
	private static boolean topplePositionSortedNeighbors(int[][][][] newWSlices, int value, int x, int y, int z, int[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		int neighborValue = neighborValues[0];
		int toShare = value - neighborValue;
		int share = toShare/shareCount;
		if (share != 0) {
			toppled = true;
			value = value - toShare + toShare%shareCount + share;
			for (int j = 0; j < neighborCount; j++) {
				int[] nc = neighborCoords[sortedNeighborsIndexes[j]];
				newWSlices[nc[0]][nc[1]][nc[2]][nc[3]] += share * neighborShareMultipliers[sortedNeighborsIndexes[j]];
			}
		}
		int previousNeighborValue = neighborValue;
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
	
	private static boolean topplePosition(int[][][][] newWSlices, int value, int x, int y, int z, int[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
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
				int n0Val = asymmetricNeighborValues[0], n1Val = asymmetricNeighborValues[1];
				int[] n0Coords = asymmetricNeighborCoords[0], n1Coords = asymmetricNeighborCoords[1];
				int shareCount = neighborCount + 1;
				if (n0Val == n1Val) {
					// n0Val = n1Val < value
					int toShare = value - n0Val; 
					int share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share;
					newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share;
					newWSlices[1][x][y][z] += value - toShare + share + toShare%shareCount;
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					int toShare = value - n1Val; 
					int share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share;
					newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share;
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					int currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share;
					newWSlices[1][x][y][z] += currentRemainingValue - toShare + share + toShare%shareCount;
				} else {
					// n1Val < n0Val < value
					int toShare = value - n0Val; 
					int share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newWSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]][n0Coords[3]] += share;
					newWSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]][n1Coords[3]] += share;
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					int currentRemainingValue = value - neighborCount*share;
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
				int toShare = value - asymmetricNeighborValues[0];
				int share = toShare/shareCount;
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
	
	private static boolean topplePositionSortedNeighbors(int[][][][] newWSlices, int value, int x, int y, int z, int[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		int neighborValue = asymmetricNeighborValues[0];
		int toShare = value - neighborValue;
		int share = toShare/shareCount;
		if (share != 0) {
			toppled = true;
			value = value - toShare + toShare%shareCount + share;
			for (int j = 0; j < asymmetricNeighborCount; j++) {
				int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];
				newWSlices[nc[0]][nc[1]][nc[2]][nc[3]] += share;
			}
		}
		int previousNeighborValue = neighborValue;
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
	public int getIntialValue() {
		return initialValue;
	}
	
	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		SerializableModelData data = new SerializableModelData();
		data.put(SerializableModelData.MODEL, SerializableModelData.Models.AETHER);
		data.put(SerializableModelData.INITIAL_CONFIGURATION, initialValue);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_TYPE, SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE, SerializableModelData.InitialConfigurationImplementationTypes.INTEGER);
		data.put(SerializableModelData.GRID, grid);
		data.put(SerializableModelData.GRID_TYPE, SerializableModelData.GridTypes.INFINITE_REGULAR);
		data.put(SerializableModelData.GRID_DIMENSION, 4);
		data.put(SerializableModelData.GRID_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_INT_ARRAY_1);
		data.put(SerializableModelData.COORDINATE_BOUNDS, maxW);
		data.put(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE, SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER);
		data.put(SerializableModelData.STEP, step);
		data.put(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP, changed);
		Utils.serializeToFile(data, backupPath, backupName);
	}

	@Override
	public String getName() {
		return "Aether";
	}
	
	@Override
	public String getSubfolderPath() {
		return getName() + "/4D/" + initialValue;
	}
	
}