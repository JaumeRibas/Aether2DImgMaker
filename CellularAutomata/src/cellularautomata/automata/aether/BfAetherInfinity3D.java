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
import org.apache.commons.math3.fraction.BigFraction;

import cellularautomata.Utils;
import cellularautomata.model.SerializableModelData;
import cellularautomata.model3d.IsotropicCubicNumericArrayModelA;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 3D with a single source initial configuration of infinity. 
 * 
 * @author Jaume
 *
 */
public class BfAetherInfinity3D extends IsotropicCubicNumericArrayModelA<BigFraction> {
	
	private long step;
	private final boolean isPositive;
	private int maxX;
	
	public BfAetherInfinity3D(boolean isPositive) {
		this.isPositive = isPositive;
		grid = Utils.buildAnisotropic3DBigFractionArray(7);
		grid[0][0][0] = isPositive? BigFraction.ONE : BigFraction.MINUS_ONE;
		maxX = 4;
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
	public BfAetherInfinity3D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		SerializableModelData data = (SerializableModelData) Utils.deserializeFromFile(backupPath);
		data = SerializableModelData.updateDataFormat(data);
		if (!SerializableModelData.Models.AETHER.equals(data.get(SerializableModelData.MODEL))) {
			throw new IllegalArgumentException("The backup file contains a different model.");
		}
		if (!SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_TYPE))
				|| !SerializableModelData.InitialConfigurationImplementationTypes.BOOLEAN.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.GridTypes.INFINITE_REGULAR.equals(data.get(SerializableModelData.GRID_TYPE))
				|| !Integer.valueOf(3).equals(data.get(SerializableModelData.GRID_DIMENSION))
				|| !SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BIG_FRACTION_ARRAY_1.equals(data.get(SerializableModelData.GRID_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER.equals(data.get(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE))) {
			throw new IllegalArgumentException("The backup file's configuration is not compatible with this class.");
		}
		isPositive = (boolean) data.get(SerializableModelData.INITIAL_CONFIGURATION);
		grid = (BigFraction[][][]) data.get(SerializableModelData.GRID);
		maxX = (int) data.get(SerializableModelData.COORDINATE_BOUNDS);
		step = (long) data.get(SerializableModelData.STEP);
	}
	
	@Override
	public Boolean nextStep() {
		BigFraction[][][] newGrid = new BigFraction[maxX + 3][][];
		BigFraction[][] smallerXSlice = null, currentXSlice = grid[0], greaterXSlice = grid[1];
		BigFraction[][] newSmallerXSlice = null, 
				newCurrentXSlice = Utils.buildAnisotropic2DBigFractionArray(1), 
				newGreaterXSlice = Utils.buildAnisotropic2DBigFractionArray(2);// build new grid progressively to save memory
		newGrid[0] = newCurrentXSlice;
		newGrid[1] = newGreaterXSlice;
		// x = 0, y = 0, z = 0
		BigFraction currentValue = currentXSlice[0][0];
		BigFraction greaterXNeighborValue = greaterXSlice[0][0];
		topplePositionOfType1(currentValue, greaterXNeighborValue, newCurrentXSlice, newGreaterXSlice);
		// x = 1, y = 0, z = 0
		// smallerXSlice = currentXSlice; // not needed here
		currentXSlice = greaterXSlice;
		greaterXSlice = grid[2];
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = Utils.buildAnisotropic2DBigFractionArray(3);
		newGrid[2] = newGreaterXSlice;
		BigFraction[][][] newXSlices = new BigFraction[][][] { newSmallerXSlice, newCurrentXSlice, newGreaterXSlice};
		BigFraction[] relevantAsymmetricNeighborValues = new BigFraction[6];
		int[] sortedNeighborsIndexes = new int[6];
		int[][] relevantAsymmetricNeighborCoords = new int[6][3];
		int[] relevantAsymmetricNeighborShareMultipliers = new int[6];// to compensate for omitted symmetric positions
		int[] relevantAsymmetricNeighborSymmetryCounts = new int[6];// to compensate for omitted symmetric positions
		// reuse values obtained previously
		BigFraction smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterXNeighborValue = greaterXSlice[0][0];
		BigFraction greaterYNeighborValue = currentXSlice[1][0];
		topplePositionOfType2(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 1, y = 1, z = 0
		// reuse values obtained previously
		BigFraction smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterXNeighborValue = greaterXSlice[1][0];
		BigFraction greaterZNeighborValue = currentXSlice[1][1];
		topplePositionOfType3(currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 1, y = 1, z = 1
		// reuse values obtained previously
		BigFraction smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterXNeighborValue = greaterXSlice[1][1];
		topplePositionOfType4(currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, newGreaterXSlice);
		grid[0] = null;// free old grid progressively to save memory
		// x = 2, y = 0, z = 0
		smallerXSlice = currentXSlice;
		currentXSlice = greaterXSlice;
		greaterXSlice = grid[3];
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = Utils.buildAnisotropic2DBigFractionArray(4);
		newGrid[3] = newGreaterXSlice;
		newXSlices[0] = newSmallerXSlice;
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		currentValue = currentXSlice[0][0];
		greaterXNeighborValue = greaterXSlice[0][0];
		smallerXNeighborValue = smallerXSlice[0][0];
		greaterYNeighborValue = currentXSlice[1][0];
		topplePositionOfType5(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 2, y = 1, z = 0
		greaterXNeighborValue = greaterXSlice[1][0];
		smallerXNeighborValue = smallerXSlice[1][0];
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice[2][0];
		greaterZNeighborValue = currentXSlice[1][1];
		topplePositionOfType6(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 2, y = 1, z = 1
		greaterXNeighborValue = greaterXSlice[1][1];
		smallerXNeighborValue = smallerXSlice[1][1];
		greaterYNeighborValue = currentXSlice[2][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		topplePositionOfType7(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 2, y = 2, z = 0
		currentValue = currentXSlice[2][0];
		greaterXNeighborValue = greaterXSlice[2][0];
		// reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		topplePositionOfType8(2, currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, 
				newXSlices);
		// x = 2, y = 2, z = 1
		greaterXNeighborValue = greaterXSlice[2][1];
		smallerYNeighborValue = currentXSlice[1][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[2][2];
		topplePositionOfType9(2, 1, currentValue, greaterXNeighborValue, smallerYNeighborValue, 2, 
				greaterZNeighborValue, 3, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 2, y = 2, z = 2
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterXNeighborValue = greaterXSlice[2][2];
		topplePositionOfType10(2, currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, 
				newGreaterXSlice);
		grid[1] = null;
		// x = 3, y = 0, z = 0
		smallerXSlice = currentXSlice;
		currentXSlice = greaterXSlice;
		greaterXSlice = grid[4];
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = Utils.buildAnisotropic2DBigFractionArray(5);
		newGrid[4] = newGreaterXSlice;
		newXSlices[0] = newSmallerXSlice;
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		currentValue = currentXSlice[0][0];
		greaterXNeighborValue = greaterXSlice[0][0];
		smallerXNeighborValue = smallerXSlice[0][0];
		greaterYNeighborValue = currentXSlice[1][0];
		topplePositionOfType5(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 3, y = 1, z = 0
		greaterXNeighborValue = greaterXSlice[1][0];
		smallerXNeighborValue = smallerXSlice[1][0];
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice[2][0];
		greaterZNeighborValue = currentXSlice[1][1];
		topplePositionOfType6(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 3, y = 1, z = 1
		greaterXNeighborValue = greaterXSlice[1][1];
		smallerXNeighborValue = smallerXSlice[1][1];
		greaterYNeighborValue = currentXSlice[2][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		topplePositionOfType7(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 3, y = 2, z = 0
		currentValue = currentXSlice[2][0];
		greaterXNeighborValue = greaterXSlice[2][0];
		smallerXNeighborValue = smallerXSlice[2][0];
		// reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice[3][0];
		topplePositionOfType6(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 3, y = 2, z = 1
		greaterXNeighborValue = greaterXSlice[2][1];
		smallerXNeighborValue = smallerXSlice[2][1];
		greaterYNeighborValue = currentXSlice[3][1];
		smallerYNeighborValue = currentXSlice[1][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[2][2];
		topplePositionOfType11(2, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, newXSlices);
		// x = 3, y = 2, z = 2
		greaterXNeighborValue = greaterXSlice[2][2];
		smallerXNeighborValue = smallerXSlice[2][2];
		greaterYNeighborValue = currentXSlice[3][2];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		topplePositionOfType7(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 3, y = 3, z = 0
		currentValue = currentXSlice[3][0];
		greaterXNeighborValue = greaterXSlice[3][0];
		smallerYNeighborValue = currentXSlice[2][0];
		greaterZNeighborValue = currentXSlice[3][1];
		topplePositionOfType8(3, currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 3, y = 3, z = 1
		greaterXNeighborValue = greaterXSlice[3][1];
		smallerYNeighborValue = currentXSlice[2][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[3][2];
		topplePositionOfType9(3, 1, currentValue, greaterXNeighborValue, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 3, y = 3, z = 2
		greaterXNeighborValue = greaterXSlice[3][2];
		smallerYNeighborValue = currentXSlice[2][2];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[3][3];
		topplePositionOfType9(3, 2, currentValue, greaterXNeighborValue, smallerYNeighborValue, 2, 
				greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 3, y = 3, z = 3
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterXNeighborValue = greaterXSlice[3][3];
		topplePositionOfType10(3, currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, 
				newGreaterXSlice);
		grid[2] = null;
		// 4 <= x < edge
		int edge = grid.length - 1;
		BigFraction[][][] xSlices = new BigFraction[][][] {null, currentXSlice, greaterXSlice};
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		toppleRangeBeyondX3(xSlices, newXSlices, newGrid, 4, edge, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts); // is it faster to reuse these arrays?
		if (newGrid.length > grid.length) {
			newGrid[grid.length] = Utils.buildAnisotropic2DBigFractionArray(newGrid.length);
		}
		grid = newGrid;
		maxX++;
		step++;
		return true;
	}

	@Override
	public Boolean isChanged() {
		return step == 0 ? null : true;
	}
	
	private boolean toppleRangeBeyondX3(BigFraction[][][] xSlices, BigFraction[][][] newXSlices, BigFraction[][][] newGrid, int minX, int maxX, 
			BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts) {
		boolean anyToppled = false;
		int x = minX, xMinusOne = x - 1, xPlusOne = x + 1, xPlusTwo = xPlusOne + 1;
		BigFraction[][] smallerXSlice = null, currentXSlice = xSlices[1], greaterXSlice = xSlices[2];
		BigFraction[][] newSmallerXSlice = null, newCurrentXSlice = newXSlices[1], newGreaterXSlice = newXSlices[2];
		for (; x < maxX; xMinusOne = x, x = xPlusOne, xPlusOne = xPlusTwo, xPlusTwo++) {
			// y = 0, z = 0
			smallerXSlice = currentXSlice;
			currentXSlice = greaterXSlice;
			greaterXSlice = grid[xPlusOne];
			newSmallerXSlice = newCurrentXSlice;
			newCurrentXSlice = newGreaterXSlice;
			newGreaterXSlice = Utils.buildAnisotropic2DBigFractionArray(xPlusTwo);
			newGrid[xPlusOne] = newGreaterXSlice;
			newXSlices[0] = newSmallerXSlice;
			newXSlices[1] = newCurrentXSlice;
			newXSlices[2] = newGreaterXSlice;
			BigFraction currentValue = currentXSlice[0][0];
			BigFraction greaterXNeighborValue = greaterXSlice[0][0];
			BigFraction smallerXNeighborValue = smallerXSlice[0][0];
			BigFraction greaterYNeighborValue = currentXSlice[1][0];
			topplePositionOfType5(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			// y = 1, z = 0
			greaterXNeighborValue = greaterXSlice[1][0];
			smallerXNeighborValue = smallerXSlice[1][0];
			// reuse values obtained previously
			BigFraction smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterYNeighborValue = currentXSlice[2][0];
			BigFraction greaterZNeighborValue = currentXSlice[1][1];
			topplePositionOfType6(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			// y = 1, z = 1
			greaterXNeighborValue = greaterXSlice[1][1];
			smallerXNeighborValue = smallerXSlice[1][1];
			greaterYNeighborValue = currentXSlice[2][1];
			// reuse values obtained previously
			BigFraction smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			topplePositionOfType7(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			// y = 2, z = 0
			currentValue = currentXSlice[2][0];
			greaterXNeighborValue = greaterXSlice[2][0];
			smallerXNeighborValue = smallerXSlice[2][0];
			// reuse values obtained previously
			smallerYNeighborValue = smallerZNeighborValue;
			greaterZNeighborValue = greaterYNeighborValue;
			greaterYNeighborValue = currentXSlice[3][0];
			topplePositionOfType12(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			// y = 2, z = 1
			greaterXNeighborValue = greaterXSlice[2][1];
			smallerXNeighborValue = smallerXSlice[2][1];
			greaterYNeighborValue = currentXSlice[3][1];
			smallerYNeighborValue = currentXSlice[1][1];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[2][2];
			topplePositionOfType11(2, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newXSlices);
			// y = 2, z = 2
			greaterXNeighborValue = greaterXSlice[2][2];
			smallerXNeighborValue = smallerXSlice[2][2];
			greaterYNeighborValue = currentXSlice[3][2];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			topplePositionOfType13(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			int y = 3, yMinusOne = 2, yPlusOne = 4;
			for (int lastY = x - 2; y <= lastY;) {
				// z = 0
				currentValue = currentXSlice[y][0];
				greaterXNeighborValue = greaterXSlice[y][0];
				smallerXNeighborValue = smallerXSlice[y][0];
				greaterYNeighborValue = currentXSlice[yPlusOne][0];
				smallerYNeighborValue = currentXSlice[yMinusOne][0];
				greaterZNeighborValue = currentXSlice[y][1];
				topplePositionOfType12(y, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
						smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
				// z = 1
				greaterXNeighborValue = greaterXSlice[y][1];
				smallerXNeighborValue = smallerXSlice[y][1];
				greaterYNeighborValue = currentXSlice[yPlusOne][1];
				smallerYNeighborValue = currentXSlice[yMinusOne][1];
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterZNeighborValue = currentXSlice[y][2];
				topplePositionOfType11(y, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, 
						relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newXSlices);
				int z = 2, zPlusOne = 3;
				for (int lastZ = y - 2; z <= lastZ;) {
					greaterXNeighborValue = greaterXSlice[y][z];
					smallerXNeighborValue = smallerXSlice[y][z];
					greaterYNeighborValue = currentXSlice[yPlusOne][z];
					smallerYNeighborValue = currentXSlice[yMinusOne][z];
					// reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterZNeighborValue = currentXSlice[y][zPlusOne];
					topplePositionOfType15(y, z, currentValue, greaterXNeighborValue, smallerXNeighborValue, 
							greaterYNeighborValue, smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue, 
							relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, newXSlices);
					z = zPlusOne;
					zPlusOne++;
				}
				// z = y - 1
				greaterXNeighborValue = greaterXSlice[y][z];
				smallerXNeighborValue = smallerXSlice[y][z];
				greaterYNeighborValue = currentXSlice[yPlusOne][z];
				smallerYNeighborValue = currentXSlice[yMinusOne][z];
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterZNeighborValue = currentXSlice[y][zPlusOne];
				topplePositionOfType11(y, z, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, 
						relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newXSlices);
				// z = y
				z = zPlusOne;
				greaterXNeighborValue = greaterXSlice[y][z];
				smallerXNeighborValue = smallerXSlice[y][z];
				greaterYNeighborValue = currentXSlice[yPlusOne][z];
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				topplePositionOfType13(y, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
						smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborSymmetryCounts, newXSlices);
				yMinusOne = y;
				y = yPlusOne;
				yPlusOne++;
			}
			// y = x - 1, z = 0
			currentValue = currentXSlice[y][0];
			greaterXNeighborValue = greaterXSlice[y][0];
			smallerXNeighborValue = smallerXSlice[y][0];
			greaterYNeighborValue = currentXSlice[yPlusOne][0];
			smallerYNeighborValue = currentXSlice[yMinusOne][0];
			greaterZNeighborValue = currentXSlice[y][1];
			topplePositionOfType6(y, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			// y = x - 1, z = 1
			greaterXNeighborValue = greaterXSlice[y][1];
			smallerXNeighborValue = smallerXSlice[y][1];
			greaterYNeighborValue = currentXSlice[yPlusOne][1];
			smallerYNeighborValue = currentXSlice[yMinusOne][1];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[y][2];
			topplePositionOfType11(y, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newXSlices);
			int z = 2, zPlusOne = 3, lastZ = y - 2;
			for(; z <= lastZ;) {
				greaterXNeighborValue = greaterXSlice[y][z];
				smallerXNeighborValue = smallerXSlice[y][z];
				greaterYNeighborValue = currentXSlice[yPlusOne][z];
				smallerYNeighborValue = currentXSlice[yMinusOne][z];
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterZNeighborValue = currentXSlice[y][zPlusOne];
				topplePositionOfType11(y, z, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
						greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, 
						relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newXSlices);
				z = zPlusOne;
				zPlusOne++;
			}
			// y = x - 1, z = y - 1
			greaterXNeighborValue = greaterXSlice[y][z];
			smallerXNeighborValue = smallerXSlice[y][z];
			greaterYNeighborValue = currentXSlice[yPlusOne][z];
			smallerYNeighborValue = currentXSlice[yMinusOne][z];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[y][zPlusOne];
			topplePositionOfType11(y, z, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newXSlices);
			z = zPlusOne;
			zPlusOne++;
			// y = x - 1, z = y
			greaterXNeighborValue = greaterXSlice[y][z];
			smallerXNeighborValue = smallerXSlice[y][z];
			greaterYNeighborValue = currentXSlice[yPlusOne][z];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			topplePositionOfType7(y, currentValue, greaterXNeighborValue, smallerXNeighborValue, 3, 
					greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			yMinusOne = y;
			y = yPlusOne;
			// y = x, z = 0
			currentValue = currentXSlice[y][0];
			greaterXNeighborValue = greaterXSlice[y][0];
			smallerYNeighborValue = currentXSlice[yMinusOne][0];
			greaterZNeighborValue = currentXSlice[y][1];
			topplePositionOfType8(y, currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			// y = x, z = 1
			greaterXNeighborValue = greaterXSlice[y][1];
			smallerYNeighborValue = currentXSlice[yMinusOne][1];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[y][2];
			topplePositionOfType9(y, 1, currentValue, greaterXNeighborValue, smallerYNeighborValue, 1, 
					greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			z = 2;
			zPlusOne = 3;
			lastZ++;
			for(; z <= lastZ; z = zPlusOne, zPlusOne++) {
				greaterXNeighborValue = greaterXSlice[y][z];
				smallerYNeighborValue = currentXSlice[yMinusOne][z];
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterZNeighborValue = currentXSlice[y][zPlusOne];
				topplePositionOfType14(y, z, currentValue, greaterXNeighborValue, smallerYNeighborValue, 
						greaterZNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			}			
			// y = x, z = y - 1
			greaterXNeighborValue = greaterXSlice[y][z];
			smallerYNeighborValue = currentXSlice[yMinusOne][z];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[y][zPlusOne];
			topplePositionOfType9(y, z, currentValue, greaterXNeighborValue, smallerYNeighborValue, 2, 
					greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			z = zPlusOne;
			// y = x, z = y
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterXNeighborValue = greaterXSlice[y][z];
			topplePositionOfType10(y, currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, 
					newGreaterXSlice);
			grid[xMinusOne] = null;
		}
		xSlices[1] = currentXSlice;
		xSlices[2] = greaterXSlice;
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		return anyToppled;
	}

	private static void topplePositionOfType1(BigFraction currentValue, BigFraction greaterXNeighborValue, BigFraction[][] newCurrentXSlice, 
			BigFraction[][] newGreaterXSlice) {
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			BigFraction toShare = currentValue.subtract(greaterXNeighborValue);
			BigFraction share = toShare.divide(7);
			newCurrentXSlice[0][0] = newCurrentXSlice[0][0].add(currentValue).subtract(toShare).add(share);
			newGreaterXSlice[0][0] = newGreaterXSlice[0][0].add(share);		
		} else {
			newCurrentXSlice[0][0] = newCurrentXSlice[0][0].add(currentValue);
		}
	}

	private static void topplePositionOfType2(BigFraction currentValue, BigFraction greaterXNeighborValue, BigFraction smallerXNeighborValue, 
			BigFraction greaterYNeighborValue, BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, BigFraction[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;// this is the index of the new slice: 0->newSmallerXSlice, 1->newCurrentXSlice, 2->newGreaterXSlice
			nc[1] = 0;// this is the actual y coordinate
			nc[2] = 0;// this is the actual z coordinate
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerXNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = 0;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 6;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		topplePosition(newXSlices, currentValue, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, 
				relevantAsymmetricNeighborCount);
	}

	private static void topplePositionOfType3(BigFraction currentValue, BigFraction greaterXNeighborValue, BigFraction smallerYNeighborValue, 
			BigFraction greaterZNeighborValue, BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, BigFraction[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 1;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 0;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 4;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 3;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		topplePosition(newXSlices, currentValue, 1, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, 
				relevantAsymmetricNeighborCount);
	}

	private static void topplePositionOfType4(BigFraction currentValue, BigFraction greaterXNeighborValue, BigFraction smallerZNeighborValue, 
			BigFraction[][] newCurrentXSlice, BigFraction[][] newGreaterXSlice) {
		if (smallerZNeighborValue.compareTo(currentValue) < 0) {
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				if (smallerZNeighborValue.equals(greaterXNeighborValue)) {
					// gx = sz < current
					BigFraction toShare = currentValue.subtract(greaterXNeighborValue);
					BigFraction share = toShare.divide(7);
					newCurrentXSlice[1][0] = newCurrentXSlice[1][0].add(share).add(share);// one more for the symmetric position at the other side
					newCurrentXSlice[1][1] = newCurrentXSlice[1][1].add(currentValue).subtract(toShare).add(share);
					newGreaterXSlice[1][1] = newGreaterXSlice[1][1].add(share);
				} else if (smallerZNeighborValue.compareTo(greaterXNeighborValue) < 0) {
					// sz < gx < current
					BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
					BigFraction share = toShare.divide(7);
					newCurrentXSlice[1][0] = newCurrentXSlice[1][0].add(share).add(share);
					newGreaterXSlice[1][1] = newGreaterXSlice[1][1].add(share);
					BigFraction currentRemainingValue = currentValue.subtract(share.multiply(6));
					toShare = currentRemainingValue.subtract(smallerZNeighborValue); 
					share = toShare.divide(4);
					newCurrentXSlice[1][0] = newCurrentXSlice[1][0].add(share).add(share);
					newCurrentXSlice[1][1] = newCurrentXSlice[1][1].add(currentRemainingValue).subtract(toShare).add(share);
				} else {
					// gx < sz < current
					BigFraction toShare = currentValue.subtract(smallerZNeighborValue); 
					BigFraction share = toShare.divide(7);
					newCurrentXSlice[1][0] = newCurrentXSlice[1][0].add(share).add(share);
					newGreaterXSlice[1][1] = newGreaterXSlice[1][1].add(share);
					BigFraction currentRemainingValue = currentValue.subtract(share.multiply(6));
					toShare = currentRemainingValue.subtract(greaterXNeighborValue); 
					share = toShare.divide(4);
					newCurrentXSlice[1][1] = newCurrentXSlice[1][1].add(currentRemainingValue).subtract(toShare).add(share);
					newGreaterXSlice[1][1] = newGreaterXSlice[1][1].add(share);
				}
			} else {
				// sz < current <= gx
				BigFraction toShare = currentValue.subtract(smallerZNeighborValue); 
				BigFraction share = toShare.divide(4);
				newCurrentXSlice[1][0] = newCurrentXSlice[1][0].add(share).add(share);
				newCurrentXSlice[1][1] = newCurrentXSlice[1][1].add(currentValue).subtract(toShare).add(share);
			}
		} else if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			// gx < current <= sz
			BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
			BigFraction share = toShare.divide(4);
			newCurrentXSlice[1][1] = newCurrentXSlice[1][1].add(currentValue).subtract(toShare).add(share);
			newGreaterXSlice[1][1] = newGreaterXSlice[1][1].add(share);
		} else {
			// gx >= current <= sz
			newCurrentXSlice[1][1] = newCurrentXSlice[1][1].add(currentValue);
		}
	}

	private static void topplePositionOfType5(BigFraction currentValue, BigFraction greaterXNeighborValue, 
			BigFraction smallerXNeighborValue, BigFraction greaterYNeighborValue, BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts,
			BigFraction[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 0;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerXNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = 0;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		topplePosition(newXSlices, currentValue, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static void topplePositionOfType6(int y, BigFraction currentValue, BigFraction gXValue, BigFraction sXValue, 
			int sXShareMultiplier, BigFraction gYValue, int gYShareMultiplier, BigFraction sYValue, int sYShareMultiplier, 
			BigFraction gZValue, int gZShareMultiplier, BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigFraction[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gXValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = y;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = y;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y + 1;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y - 1;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		topplePosition(newXSlices, currentValue, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, 
				relevantAsymmetricNeighborCount);
	}

	private static void topplePositionOfType7(int coord, BigFraction currentValue, BigFraction gXValue, BigFraction sXValue, 
			int sXShareMultiplier, BigFraction gYValue, int gYShareMultiplier, BigFraction sZValue, 
			int sZShareMultiplier, BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigFraction[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gXValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (sXValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord;
			nc[2] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sXShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (gYValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord + 1;
			nc[2] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		topplePosition(newXSlices, currentValue, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, 
				relevantAsymmetricNeighborCount);
	}

	private static void topplePositionOfType8(int y, BigFraction currentValue, BigFraction greaterXNeighborValue, BigFraction smallerYNeighborValue, 
			BigFraction greaterZNeighborValue, BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigFraction[][][] newXSlices ) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = y;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y - 1;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		topplePosition(newXSlices, currentValue, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static void topplePositionOfType9(int y, int z, BigFraction currentValue, BigFraction gXValue, BigFraction sYValue, 
			int sYShareMultiplier, BigFraction gZValue, int gZShareMultiplier, BigFraction sZValue, int sZShareMultiplier, 
			BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, 
			BigFraction[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gXValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = y;
			nc[2] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y - 1;
			nc[2] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = z + 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		topplePosition(newXSlices, currentValue, y, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, 
				relevantAsymmetricNeighborCount);
	}

	private static void topplePositionOfType10(int coord, BigFraction currentValue, BigFraction greaterXNeighborValue, 
			BigFraction smallerZNeighborValue, BigFraction[][] newCurrentXSlice, BigFraction[][] newGreaterXSlice) {
		if (smallerZNeighborValue.compareTo(currentValue) < 0) {
			int coordMinusOne = coord - 1;
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				if (smallerZNeighborValue.equals(greaterXNeighborValue)) {
					// gx = sz < current
					BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
					BigFraction share = toShare.divide(7);
					newCurrentXSlice[coord][coordMinusOne] = newCurrentXSlice[coord][coordMinusOne].add(share);
					newCurrentXSlice[coord][coord] = newCurrentXSlice[coord][coord].add(currentValue).subtract(toShare).add(share);
					newGreaterXSlice[coord][coord] = newGreaterXSlice[coord][coord].add(share);
				} else if (smallerZNeighborValue.compareTo(greaterXNeighborValue) < 0) {
					// sz < gx < current
					BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
					BigFraction share = toShare.divide(7);
					newCurrentXSlice[coord][coordMinusOne] = newCurrentXSlice[coord][coordMinusOne].add(share);
					newGreaterXSlice[coord][coord] = newGreaterXSlice[coord][coord].add(share);
					BigFraction currentRemainingValue = currentValue.subtract(share.multiply(6));
					toShare = currentRemainingValue.subtract(smallerZNeighborValue); 
					share = toShare.divide(4);
					newCurrentXSlice[coord][coordMinusOne] = newCurrentXSlice[coord][coordMinusOne].add(share);
					newCurrentXSlice[coord][coord] = newCurrentXSlice[coord][coord].add(currentRemainingValue).subtract(toShare).add(share);
				} else {
					// gx < sz < current
					BigFraction toShare = currentValue.subtract(smallerZNeighborValue); 
					BigFraction share = toShare.divide(7);
					newCurrentXSlice[coord][coordMinusOne] = newCurrentXSlice[coord][coordMinusOne].add(share);
					newGreaterXSlice[coord][coord] = newGreaterXSlice[coord][coord].add(share);
					BigFraction currentRemainingValue = currentValue.subtract(share.multiply(6));
					toShare = currentRemainingValue.subtract(greaterXNeighborValue); 
					share = toShare.divide(4);
					newCurrentXSlice[coord][coord] = newCurrentXSlice[coord][coord].add(currentRemainingValue).subtract(toShare).add(share);
					newGreaterXSlice[coord][coord] = newGreaterXSlice[coord][coord].add(share);
				}
			} else {
				// sz < current <= gx
				BigFraction toShare = currentValue.subtract(smallerZNeighborValue); 
				BigFraction share = toShare.divide(4);
				newCurrentXSlice[coord][coordMinusOne] = newCurrentXSlice[coord][coordMinusOne].add(share);
				newCurrentXSlice[coord][coord] = newCurrentXSlice[coord][coord].add(currentValue).subtract(toShare).add(share);
			}
		} else if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			// gx < current <= sz
			BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
			BigFraction share = toShare.divide(4);
			newCurrentXSlice[coord][coord] = newCurrentXSlice[coord][coord].add(currentValue).subtract(toShare).add(share);
			newGreaterXSlice[coord][coord] = newGreaterXSlice[coord][coord].add(share);
		} else {
			// gx >= current <= sz
			newCurrentXSlice[coord][coord] = newCurrentXSlice[coord][coord].add(currentValue);
		}
	}

	private static void topplePositionOfType11(int y, int z, BigFraction currentValue, BigFraction gXValue, BigFraction sXValue, 
			int sXShareMultiplier, BigFraction gYValue, int gYShareMultiplier, BigFraction sYValue, int sYShareMultiplier, 
			BigFraction gZValue, int gZShareMultiplier, BigFraction sZValue, int sZShareMultiplier, BigFraction[] relevantNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantNeighborCoords, 
			int[] relevantNeighborShareMultipliers, 
			BigFraction[][][] newXSlices) {
		int relevantNeighborCount = 0;
		if (gXValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount ] = gXValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 2;
			nc[1] = y;
			nc[2] = z;
			relevantNeighborShareMultipliers[relevantNeighborCount] = 1;
			relevantNeighborCount++;
		}
		if (sXValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = sXValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 0;
			nc[1] = y;
			nc[2] = z;
			relevantNeighborShareMultipliers[relevantNeighborCount] = sXShareMultiplier;
			relevantNeighborCount++;
		}
		if (gYValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = gYValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = y + 1;
			nc[2] = z;
			relevantNeighborShareMultipliers[relevantNeighborCount] = gYShareMultiplier;
			relevantNeighborCount++;
		}
		if (sYValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = sYValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = y - 1;
			nc[2] = z;
			relevantNeighborShareMultipliers[relevantNeighborCount] = sYShareMultiplier;
			relevantNeighborCount++;
		}
		if (gZValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = gZValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = z + 1;
			relevantNeighborShareMultipliers[relevantNeighborCount] = gZShareMultiplier;
			relevantNeighborCount++;
		}
		if (sZValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = sZValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = z - 1;
			relevantNeighborShareMultipliers[relevantNeighborCount] = sZShareMultiplier;
			relevantNeighborCount++;
		}
		topplePosition(newXSlices, currentValue, y, z, relevantNeighborValues, sortedNeighborsIndexes, relevantNeighborCoords, 
				relevantNeighborShareMultipliers, relevantNeighborCount);
	}

	private static void topplePositionOfType12(int y, BigFraction currentValue, BigFraction greaterXNeighborValue, 
			BigFraction smallerXNeighborValue, BigFraction greaterYNeighborValue, BigFraction smallerYNeighborValue, 
			BigFraction greaterZNeighborValue, BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, 
			BigFraction[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount ] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = y;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerXNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = y;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y + 1;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y - 1;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		topplePosition(newXSlices, currentValue, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, 
				relevantAsymmetricNeighborCount);
	}

	private static void topplePositionOfType13(int coord, BigFraction currentValue, BigFraction greaterXNeighborValue, 
			BigFraction smallerXNeighborValue, BigFraction greaterYNeighborValue, BigFraction smallerZNeighborValue, 
			BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigFraction[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerXNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord;
			nc[2] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord + 1;
			nc[2] = coord;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerZNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		topplePosition(newXSlices, currentValue, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static void topplePositionOfType14(int y, int z, BigFraction currentValue, BigFraction greaterXNeighborValue, 
			BigFraction smallerYNeighborValue,	BigFraction greaterZNeighborValue, BigFraction smallerZNeighborValue, 
			BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigFraction[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = y;
			nc[2] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y - 1;
			nc[2] = z;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = z + 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerZNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = z - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		topplePosition(newXSlices, currentValue, y, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static void topplePositionOfType15(int y, int z, BigFraction currentValue, BigFraction greaterXNeighborValue, 
			BigFraction smallerXNeighborValue,	BigFraction greaterYNeighborValue, BigFraction smallerYNeighborValue, 
			BigFraction greaterZNeighborValue, BigFraction smallerZNeighborValue, BigFraction[] relevantNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantNeighborCoords, BigFraction[][][] newXSlices) {
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 2;
			nc[1] = y;
			nc[2] = z;
			relevantNeighborCount++;
		}
		if (smallerXNeighborValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 0;
			nc[1] = y;
			nc[2] = z;
			relevantNeighborCount++;
		}
		if (greaterYNeighborValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = y + 1;
			nc[2] = z;
			relevantNeighborCount++;
		}
		if (smallerYNeighborValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = y - 1;
			nc[2] = z;
			relevantNeighborCount++;
		}
		if (greaterZNeighborValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = z + 1;
			relevantNeighborCount++;
		}
		if (smallerZNeighborValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = smallerZNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = z - 1;
			relevantNeighborCount++;
		}
		topplePosition(newXSlices, currentValue, y, z, relevantNeighborValues, sortedNeighborsIndexes, relevantNeighborCoords, 
				relevantNeighborCount);
	}
	
	private static void topplePosition(BigFraction[][][] newXSlices, BigFraction value, int y, int z, BigFraction[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int neighborCount) {
		switch (neighborCount) {
			case 3:
				Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, y, z, neighborValues, sortedNeighborsIndexes, 
						neighborCoords, 3);
				break;
			case 2:
				BigFraction n0Val = neighborValues[0], n1Val = neighborValues[1];
				int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
				if (n0Val.equals(n1Val)) {
					// n0Val = n1Val < value
					BigFraction toShare = value.subtract(n0Val); 
					BigFraction share = toShare.divide(3);
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share);
					newXSlices[1][y][z] = newXSlices[1][y][z].add(value).subtract(toShare).add(share);
				} else if (n0Val.compareTo(n1Val) < 0) {
					// n0Val < n1Val < value
					BigFraction toShare = value.subtract(n1Val); 
					BigFraction share = toShare.divide(3);
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share);
					BigFraction currentRemainingValue = value.subtract(share.multiply(neighborCount));
					toShare = currentRemainingValue.subtract(n0Val);
					share = toShare.divide(2);
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share);
					newXSlices[1][y][z] = newXSlices[1][y][z].add(currentRemainingValue).subtract(toShare).add(share);
				} else {
					// n1Val < n0Val < value
					BigFraction toShare = value.subtract(n0Val); 
					BigFraction share = toShare.divide(3);
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share);
					BigFraction currentRemainingValue = value.subtract(share.multiply(neighborCount));
					toShare = currentRemainingValue.subtract(n1Val);
					share = toShare.divide(2);
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share);
					newXSlices[1][y][z] = newXSlices[1][y][z].add(currentRemainingValue).subtract(toShare).add(share);
				}				
				break;
			case 1:
				BigFraction toShare = value.subtract(neighborValues[0]);
				BigFraction share = toShare.divide(2);
				value = value.subtract(toShare).add(share);
				int[] nc = neighborCoords[0];
				newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
				// no break
			case 0:
				newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
				break;
			default: // 6, 5, 4
				Utils.sortDescending(neighborCount, neighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, y, z, neighborValues, sortedNeighborsIndexes, neighborCoords, 
						neighborCount);
		}
	}
	
	private static void topplePositionSortedNeighbors(BigFraction[][][] newXSlices, BigFraction value, int y, int z, 
			BigFraction[] neighborValues, int[] sortedNeighborsIndexes, int[][] neighborCoords, int neighborCount) {
		int shareCount = neighborCount + 1;
		BigFraction neighborValue = neighborValues[0];
		BigFraction toShare = value.subtract(neighborValue);
		BigFraction share = toShare.divide(shareCount);
		value = value.subtract(toShare).add(share);
		for (int j = 0; j < neighborCount; j++) {
			int[] nc = neighborCoords[sortedNeighborsIndexes[j]];
			newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
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
					newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
	}
	
	private static void topplePosition(BigFraction[][][] newXSlices, BigFraction value, int y, int z, BigFraction[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		switch (asymmetricNeighborCount) {
			case 3:
				Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, 
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
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(n0Mult));
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(n1Mult));
					newXSlices[1][y][z] = newXSlices[1][y][z].add(value).subtract(toShare).add(share);
				} else if (n0Val.compareTo(n1Val) < 0) {
					// n0Val < n1Val < value
					BigFraction toShare = value.subtract(n1Val); 
					BigFraction share = toShare.divide(shareCount);
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(n0Mult));
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(n1Mult));
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					BigFraction currentRemainingValue = value.subtract(share.multiply(neighborCount));
					toShare = currentRemainingValue.subtract(n0Val);
					share = toShare.divide(shareCount);
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(n0Mult));
					newXSlices[1][y][z] = newXSlices[1][y][z].add(currentRemainingValue).subtract(toShare).add(share);
				} else {
					// n1Val < n0Val < value
					BigFraction toShare = value.subtract(n0Val); 
					BigFraction share = toShare.divide(shareCount);
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(n0Mult));
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(n1Mult));
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					BigFraction currentRemainingValue = value.subtract(share.multiply(neighborCount));
					toShare = currentRemainingValue.subtract(n1Val);
					share = toShare.divide(shareCount);
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(n1Mult));
					newXSlices[1][y][z] = newXSlices[1][y][z].add(currentRemainingValue).subtract(toShare).add(share);
				}				
				break;
			case 1:
				shareCount = neighborCount + 1;
				BigFraction toShare = value.subtract(asymmetricNeighborValues[0]);
				BigFraction share = toShare.divide(shareCount);
				value = value.subtract(toShare).add(share);
				int[] nc = asymmetricNeighborCoords[0];
				newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(asymmetricNeighborShareMultipliers[0]));
				// no break
			case 0:
				newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
				break;
			default: // 6, 5, 4
				Utils.sortDescending(asymmetricNeighborCount, asymmetricNeighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, asymmetricNeighborCoords, 
						asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
	}
	
	private static void topplePositionSortedNeighbors(BigFraction[][][] newXSlices, BigFraction value, int y, int z, BigFraction[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		int shareCount = neighborCount + 1;
		BigFraction neighborValue = asymmetricNeighborValues[0];
		BigFraction toShare = value.subtract(neighborValue);
		BigFraction share = toShare.divide(shareCount);
		value = value.subtract(toShare).add(share);
		for (int j = 0; j < asymmetricNeighborCount; j++) {
			int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];
			newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]]));
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
					newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]]));
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
	}
	
	private static void topplePosition(BigFraction[][][] newXSlices, BigFraction value, int y, int z, BigFraction[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		switch (neighborCount) {
			case 3:
				Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, y, z, neighborValues, sortedNeighborsIndexes, 
						neighborCoords, neighborShareMultipliers, 3);
				break;
			case 2:
				BigFraction n0Val = neighborValues[0], n1Val = neighborValues[1];
				int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
				int n0Mult = neighborShareMultipliers[0], n1Mult = neighborShareMultipliers[1];
				if (n0Val.equals(n1Val)) {
					// n0Val = n1Val < value
					BigFraction toShare = value.subtract(n0Val); 
					BigFraction share = toShare.divide(3);
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(n0Mult));
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(n1Mult));
					newXSlices[1][y][z] = newXSlices[1][y][z].add(value).subtract(toShare).add(share);
				} else if (n0Val.compareTo(n1Val) < 0) {
					// n0Val < n1Val < value
					BigFraction toShare = value.subtract(n1Val); 
					BigFraction share = toShare.divide(3);
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(n0Mult));
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(n1Mult));
					BigFraction currentRemainingValue = value.subtract(share.multiply(neighborCount));
					toShare = currentRemainingValue.subtract(n0Val);
					share = toShare.divide(2);
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(n0Mult));
					newXSlices[1][y][z] = newXSlices[1][y][z].add(currentRemainingValue).subtract(toShare).add(share);
				} else {
					// n1Val < n0Val < value
					BigFraction toShare = value.subtract(n0Val); 
					BigFraction share = toShare.divide(3);
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(n0Mult));
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(n1Mult));
					BigFraction currentRemainingValue = value.subtract(share.multiply(neighborCount));
					toShare = currentRemainingValue.subtract(n1Val);
					share = toShare.divide(2);
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(n1Mult));
					newXSlices[1][y][z] = newXSlices[1][y][z].add(currentRemainingValue).subtract(toShare).add(share);
				}				
				break;
			case 1:
				BigFraction toShare = value.subtract(neighborValues[0]);
				BigFraction share = toShare.divide(2);
				value = value.subtract(toShare).add(share);
				int[] nc = neighborCoords[0];
				newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(neighborShareMultipliers[0]));
				// no break
			case 0:
				newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
				break;
			default: // 6, 5, 4
				Utils.sortDescending(neighborCount, neighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, y, z, neighborValues, sortedNeighborsIndexes, neighborCoords, 
						neighborShareMultipliers, neighborCount);
		}
	}
	
	private static void topplePositionSortedNeighbors(BigFraction[][][] newXSlices, BigFraction value, int y, int z, BigFraction[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		int shareCount = neighborCount + 1;
		BigFraction neighborValue = neighborValues[0];
		BigFraction toShare = value.subtract(neighborValue);
		BigFraction share = toShare.divide(shareCount);
		value = value.subtract(toShare).add(share);
		for (int j = 0; j < neighborCount; j++) {
			int[] nc = neighborCoords[sortedNeighborsIndexes[j]];
			newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(neighborShareMultipliers[sortedNeighborsIndexes[j]]));
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
					newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(neighborShareMultipliers[sortedNeighborsIndexes[j]]));
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
	}
	
	private static void topplePosition(BigFraction[][][] newXSlices, BigFraction value, int y, int z, BigFraction[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		switch (asymmetricNeighborCount) {
			case 3:
				Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, 
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
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share);
					newXSlices[1][y][z] = newXSlices[1][y][z].add(value).subtract(toShare).add(share);
				} else if (n0Val.compareTo(n1Val) < 0) {
					// n0Val < n1Val < value
					BigFraction toShare = value.subtract(n1Val); 
					BigFraction share = toShare.divide(shareCount);
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share);
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					BigFraction currentRemainingValue = value.subtract(share.multiply(neighborCount));
					toShare = currentRemainingValue.subtract(n0Val);
					share = toShare.divide(shareCount);
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share);
					newXSlices[1][y][z] = newXSlices[1][y][z].add(currentRemainingValue).subtract(toShare).add(share);
				} else {
					// n1Val < n0Val < value
					BigFraction toShare = value.subtract(n0Val); 
					BigFraction share = toShare.divide(shareCount);
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share);
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					BigFraction currentRemainingValue = value.subtract(share.multiply(neighborCount));
					toShare = currentRemainingValue.subtract(n1Val);
					share = toShare.divide(shareCount);
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share);
					newXSlices[1][y][z] = newXSlices[1][y][z].add(currentRemainingValue).subtract(toShare).add(share);
				}				
				break;
			case 1:
				shareCount = neighborCount + 1;
				BigFraction toShare = value.subtract(asymmetricNeighborValues[0]);
				BigFraction share = toShare.divide(shareCount);
				value = value.subtract(toShare).add(share);
				int[] nc = asymmetricNeighborCoords[0];
				newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
				// no break
			case 0:
				newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
				break;
			default: // 6, 5, 4
				Utils.sortDescending(asymmetricNeighborCount, asymmetricNeighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, asymmetricNeighborCoords, 
						asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
	}
	
	private static void topplePositionSortedNeighbors(BigFraction[][][] newXSlices, BigFraction value, int y, int z, BigFraction[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		int shareCount = neighborCount + 1;
		BigFraction neighborValue = asymmetricNeighborValues[0];
		BigFraction toShare = value.subtract(neighborValue);
		BigFraction share = toShare.divide(shareCount);
		value = value.subtract(toShare).add(share);
		for (int j = 0; j < asymmetricNeighborCount; j++) {
			int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];
			newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
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
					newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
	}

	@Override
	public int getAsymmetricMaxX() {
		return (int) step;
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
		String path = getName() + "/3D/";
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
		data.put(SerializableModelData.GRID_DIMENSION, 3);
		data.put(SerializableModelData.GRID_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BIG_FRACTION_ARRAY_1);
		data.put(SerializableModelData.COORDINATE_BOUNDS, maxX);
		data.put(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE, SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER);
		data.put(SerializableModelData.STEP, step);
		Utils.serializeToFile(data, backupPath, backupName);
	}
	
}