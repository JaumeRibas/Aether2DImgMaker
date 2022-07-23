/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2022 Jaume Ribas

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
import java.sql.Timestamp;

import cellularautomata.Constants;
import cellularautomata.Utils;
import cellularautomata.model3d.IsotropicCubicModelA;
import cellularautomata.model3d.SymmetricNumericModel3D;
import cellularautomata.numbers.BigInt;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 3D with a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class BigIntAether3D implements SymmetricNumericModel3D<BigInt>, IsotropicCubicModelA, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2082212040097521896L;
	
	private static final BigInt TWO = BigInt.valueOf(2);
	private static final BigInt THREE = BigInt.valueOf(3);
	private static final BigInt FOUR = BigInt.valueOf(4);
	private static final BigInt SIX = BigInt.valueOf(6);
	private static final BigInt SEVEN = BigInt.valueOf(7);

	/** A 3D array representing the grid */
	private BigInt[][][] grid;
	
	private BigInt initialValue;
	private long step;
	
	private int maxX;
	/**
	 * Used in {@link #getSubfolderPath()} in case the initial value is too big.
	 */
	private String creationTimestamp;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public BigIntAether3D(BigInt initialValue) {
		this.initialValue = initialValue;
		grid = Utils.buildAnisotropic3DBigIntArray(7);
		grid[0][0][0] = this.initialValue;
		maxX = 4;
		step = 0;
		creationTimestamp = new Timestamp(System.currentTimeMillis()).toString().replace(":", "");
	}
	
	/**
	 * Creates an instance restoring a backup
	 * 
	 * @param backupPath the path to the backup file to restore.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 */
	public BigIntAether3D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		BigIntAether3D data = (BigIntAether3D) Utils.deserializeFromFile(backupPath);
		initialValue = data.initialValue;
		grid = data.grid;
		maxX = data.maxX;
		step = data.step;
		creationTimestamp = data.creationTimestamp;
	}
	
	@Override
	public boolean nextStep() {
		BigInt[][][] newGrid = new BigInt[maxX + 3][][];
		boolean changed = false;
		BigInt[][] smallerXSlice = null, currentXSlice = grid[0], greaterXSlice = grid[1];
		BigInt[][] newSmallerXSlice = null, 
				newCurrentXSlice = Utils.buildAnisotropic2DBigIntArray(1), 
				newGreaterXSlice = Utils.buildAnisotropic2DBigIntArray(2);// build new grid progressively to save memory
		newGrid[0] = newCurrentXSlice;
		newGrid[1] = newGreaterXSlice;
		// x = 0, y = 0, z = 0
		BigInt currentValue = currentXSlice[0][0];
		BigInt greaterXNeighborValue = greaterXSlice[0][0];
		if (topplePositionType1(currentValue, greaterXNeighborValue, newCurrentXSlice, newGreaterXSlice)) {
			changed = true;
		}
		// x = 1, y = 0, z = 0
		// smallerXSlice = currentXSlice; // not needed here
		currentXSlice = greaterXSlice;
		greaterXSlice = grid[2];
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = Utils.buildAnisotropic2DBigIntArray(3);
		newGrid[2] = newGreaterXSlice;
		BigInt[][][] newXSlices = new BigInt[][][] { newSmallerXSlice, newCurrentXSlice, newGreaterXSlice};
		BigInt[] relevantAsymmetricNeighborValues = new BigInt[6];
		int[] sortedNeighborsIndexes = new int[6];
		int[][] relevantAsymmetricNeighborCoords = new int[6][3];
		int[] relevantAsymmetricNeighborShareMultipliers = new int[6];// to compensate for omitted symmetric positions
		int[] relevantAsymmetricNeighborSymmetryCounts = new int[6];// to compensate for omitted symmetric positions
		// reuse values obtained previously
		BigInt smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterXNeighborValue = greaterXSlice[0][0];
		BigInt greaterYNeighborValue = currentXSlice[1][0];
		if (topplePositionType2(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 1, y = 1, z = 0
		// reuse values obtained previously
		BigInt smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterXNeighborValue = greaterXSlice[1][0];
		BigInt greaterZNeighborValue = currentXSlice[1][1];
		if (topplePositionType3(currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 1, y = 1, z = 1
		// reuse values obtained previously
		BigInt smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterXNeighborValue = greaterXSlice[1][1];
		if (topplePositionType4(currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, newGreaterXSlice)) {
			changed = true;
		}
		grid[0] = null;// free old grid progressively to save memory
		// x = 2, y = 0, z = 0
		smallerXSlice = currentXSlice;
		currentXSlice = greaterXSlice;
		greaterXSlice = grid[3];
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = Utils.buildAnisotropic2DBigIntArray(4);
		newGrid[3] = newGreaterXSlice;
		newXSlices[0] = newSmallerXSlice;
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		currentValue = currentXSlice[0][0];
		greaterXNeighborValue = greaterXSlice[0][0];
		smallerXNeighborValue = smallerXSlice[0][0];
		greaterYNeighborValue = currentXSlice[1][0];
		if (topplePositionType5(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 2, y = 1, z = 0
		greaterXNeighborValue = greaterXSlice[1][0];
		smallerXNeighborValue = smallerXSlice[1][0];
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice[2][0];
		greaterZNeighborValue = currentXSlice[1][1];
		if (topplePositionType6(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 2, y = 1, z = 1
		greaterXNeighborValue = greaterXSlice[1][1];
		smallerXNeighborValue = smallerXSlice[1][1];
		greaterYNeighborValue = currentXSlice[2][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		if (topplePositionType7(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 2, y = 2, z = 0
		currentValue = currentXSlice[2][0];
		greaterXNeighborValue = greaterXSlice[2][0];
		// reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		if (topplePositionType8(2, currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, 
				newXSlices)) {
			changed = true;
		}
		// x = 2, y = 2, z = 1
		greaterXNeighborValue = greaterXSlice[2][1];
		smallerYNeighborValue = currentXSlice[1][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[2][2];
		if (topplePositionType9(2, 1, currentValue, greaterXNeighborValue, smallerYNeighborValue, 2, 
				greaterZNeighborValue, 3, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 2, y = 2, z = 2
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterXNeighborValue = greaterXSlice[2][2];
		if (topplePositionType10(2, currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, 
				newGreaterXSlice)) {
			changed = true;
		}
		grid[1] = null;
		// x = 3, y = 0, z = 0
		smallerXSlice = currentXSlice;
		currentXSlice = greaterXSlice;
		greaterXSlice = grid[4];
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = Utils.buildAnisotropic2DBigIntArray(5);
		newGrid[4] = newGreaterXSlice;
		newXSlices[0] = newSmallerXSlice;
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		currentValue = currentXSlice[0][0];
		greaterXNeighborValue = greaterXSlice[0][0];
		smallerXNeighborValue = smallerXSlice[0][0];
		greaterYNeighborValue = currentXSlice[1][0];
		if (topplePositionType5(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 1, z = 0
		greaterXNeighborValue = greaterXSlice[1][0];
		smallerXNeighborValue = smallerXSlice[1][0];
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice[2][0];
		greaterZNeighborValue = currentXSlice[1][1];
		if (topplePositionType6(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 1, z = 1
		greaterXNeighborValue = greaterXSlice[1][1];
		smallerXNeighborValue = smallerXSlice[1][1];
		greaterYNeighborValue = currentXSlice[2][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		if (topplePositionType7(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 2, z = 0
		currentValue = currentXSlice[2][0];
		greaterXNeighborValue = greaterXSlice[2][0];
		smallerXNeighborValue = smallerXSlice[2][0];
		// reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice[3][0];
		if (topplePositionType6(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 2, z = 1
		greaterXNeighborValue = greaterXSlice[2][1];
		smallerXNeighborValue = smallerXSlice[2][1];
		greaterYNeighborValue = currentXSlice[3][1];
		smallerYNeighborValue = currentXSlice[1][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[2][2];
		if (topplePositionType11(2, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 2, z = 2
		greaterXNeighborValue = greaterXSlice[2][2];
		smallerXNeighborValue = smallerXSlice[2][2];
		greaterYNeighborValue = currentXSlice[3][2];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		if (topplePositionType7(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 3, z = 0
		currentValue = currentXSlice[3][0];
		greaterXNeighborValue = greaterXSlice[3][0];
		smallerYNeighborValue = currentXSlice[2][0];
		greaterZNeighborValue = currentXSlice[3][1];
		if (topplePositionType8(3, currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 3, z = 1
		greaterXNeighborValue = greaterXSlice[3][1];
		smallerYNeighborValue = currentXSlice[2][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[3][2];
		if (topplePositionType9(3, 1, currentValue, greaterXNeighborValue, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 3, z = 2
		greaterXNeighborValue = greaterXSlice[3][2];
		smallerYNeighborValue = currentXSlice[2][2];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[3][3];
		if (topplePositionType9(3, 2, currentValue, greaterXNeighborValue, smallerYNeighborValue, 2, 
				greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 3, z = 3
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterXNeighborValue = greaterXSlice[3][3];
		if (topplePositionType10(3, currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, 
				newGreaterXSlice)) {
			changed = true;
		}
		grid[2] = null;
		// 4 <= x < edge - 2
		int edge = grid.length - 1;
		int edgeMinusTwo = edge - 2;
		BigInt[][][] xSlices = new BigInt[][][] {null, currentXSlice, greaterXSlice};
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		if (toppleRangeBeyondX3(xSlices, newXSlices, newGrid, 4, edgeMinusTwo, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts)) { // is it faster to reuse these arrays?
			changed = true;
		}
		//edge - 2 <= x < edge
		if (toppleRangeBeyondX3(xSlices, newXSlices, newGrid, edgeMinusTwo, edge, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts)) {
			changed = true;
			maxX++;
		}
		if (newGrid.length > grid.length) {
			newGrid[grid.length] = Utils.buildAnisotropic2DBigIntArray(newGrid.length);
		}
		grid = newGrid;
		step++;
		return changed;
	}
	
	private boolean toppleRangeBeyondX3(BigInt[][][] xSlices, BigInt[][][] newXSlices, BigInt[][][] newGrid, int minX, int maxX, 
			BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts) {
		boolean anyToppled = false;
		int x = minX, xMinusOne = x - 1, xPlusOne = x + 1, xPlusTwo = xPlusOne + 1;
		BigInt[][] smallerXSlice = null, currentXSlice = xSlices[1], greaterXSlice = xSlices[2];
		BigInt[][] newSmallerXSlice = null, newCurrentXSlice = newXSlices[1], newGreaterXSlice = newXSlices[2];
		for (; x < maxX; xMinusOne = x, x = xPlusOne, xPlusOne = xPlusTwo, xPlusTwo++) {
			// y = 0, z = 0
			smallerXSlice = currentXSlice;
			currentXSlice = greaterXSlice;
			greaterXSlice = grid[xPlusOne];
			newSmallerXSlice = newCurrentXSlice;
			newCurrentXSlice = newGreaterXSlice;
			newGreaterXSlice = Utils.buildAnisotropic2DBigIntArray(xPlusTwo);
			newGrid[xPlusOne] = newGreaterXSlice;
			newXSlices[0] = newSmallerXSlice;
			newXSlices[1] = newCurrentXSlice;
			newXSlices[2] = newGreaterXSlice;
			BigInt currentValue = currentXSlice[0][0];
			BigInt greaterXNeighborValue = greaterXSlice[0][0];
			BigInt smallerXNeighborValue = smallerXSlice[0][0];
			BigInt greaterYNeighborValue = currentXSlice[1][0];
			if (topplePositionType5(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
			// y = 1, z = 0
			greaterXNeighborValue = greaterXSlice[1][0];
			smallerXNeighborValue = smallerXSlice[1][0];
			// reuse values obtained previously
			BigInt smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterYNeighborValue = currentXSlice[2][0];
			BigInt greaterZNeighborValue = currentXSlice[1][1];
			if (topplePositionType6(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
			// y = 1, z = 1
			greaterXNeighborValue = greaterXSlice[1][1];
			smallerXNeighborValue = smallerXSlice[1][1];
			greaterYNeighborValue = currentXSlice[2][1];
			// reuse values obtained previously
			BigInt smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			if (topplePositionType7(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
			// y = 2, z = 0
			currentValue = currentXSlice[2][0];
			greaterXNeighborValue = greaterXSlice[2][0];
			smallerXNeighborValue = smallerXSlice[2][0];
			// reuse values obtained previously
			smallerYNeighborValue = smallerZNeighborValue;
			greaterZNeighborValue = greaterYNeighborValue;
			greaterYNeighborValue = currentXSlice[3][0];
			if (topplePositionType12(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
			// y = 2, z = 1
			greaterXNeighborValue = greaterXSlice[2][1];
			smallerXNeighborValue = smallerXSlice[2][1];
			greaterYNeighborValue = currentXSlice[3][1];
			smallerYNeighborValue = currentXSlice[1][1];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[2][2];
			if (topplePositionType11(2, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
				anyToppled = true;
			}
			// y = 2, z = 2
			greaterXNeighborValue = greaterXSlice[2][2];
			smallerXNeighborValue = smallerXSlice[2][2];
			greaterYNeighborValue = currentXSlice[3][2];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			if (topplePositionType13(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
			int y = 3, yMinusOne = 2, yPlusOne = 4;
			for (int lastY = x - 2; y <= lastY;) {
				// z = 0
				currentValue = currentXSlice[y][0];
				greaterXNeighborValue = greaterXSlice[y][0];
				smallerXNeighborValue = smallerXSlice[y][0];
				greaterYNeighborValue = currentXSlice[yPlusOne][0];
				smallerYNeighborValue = currentXSlice[yMinusOne][0];
				greaterZNeighborValue = currentXSlice[y][1];
				if (topplePositionType12(y, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
						smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
					anyToppled = true;
				}
				// z = 1
				greaterXNeighborValue = greaterXSlice[y][1];
				smallerXNeighborValue = smallerXSlice[y][1];
				greaterYNeighborValue = currentXSlice[yPlusOne][1];
				smallerYNeighborValue = currentXSlice[yMinusOne][1];
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterZNeighborValue = currentXSlice[y][2];
				if (topplePositionType11(y, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, 
						relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
					anyToppled = true;
				}
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
					if (topplePositionType15(y, z, currentValue, greaterXNeighborValue, smallerXNeighborValue, 
							greaterYNeighborValue, smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue, 
							relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, newXSlices)) {
						anyToppled = true;
					}
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
				if (topplePositionType11(y, z, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, 
						relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
					anyToppled = true;
				}
				// z = y
				z = zPlusOne;
				greaterXNeighborValue = greaterXSlice[y][z];
				smallerXNeighborValue = smallerXSlice[y][z];
				greaterYNeighborValue = currentXSlice[yPlusOne][z];
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				if (topplePositionType13(y, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
						smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
					anyToppled = true;
				}				 
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
			if (topplePositionType6(y, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
			// y = x - 1, z = 1
			greaterXNeighborValue = greaterXSlice[y][1];
			smallerXNeighborValue = smallerXSlice[y][1];
			greaterYNeighborValue = currentXSlice[yPlusOne][1];
			smallerYNeighborValue = currentXSlice[yMinusOne][1];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[y][2];
			if (topplePositionType11(y, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
				anyToppled = true;
			}
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
				if (topplePositionType11(y, z, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
						greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, 
						relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
					anyToppled = true;
				}
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
			if (topplePositionType11(y, z, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
				anyToppled = true;
			}
			z = zPlusOne;
			zPlusOne++;
			// y = x - 1, z = y
			greaterXNeighborValue = greaterXSlice[y][z];
			smallerXNeighborValue = smallerXSlice[y][z];
			greaterYNeighborValue = currentXSlice[yPlusOne][z];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			if (topplePositionType7(y, currentValue, greaterXNeighborValue, smallerXNeighborValue, 3, 
					greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
			yMinusOne = y;
			y = yPlusOne;
			// y = x, z = 0
			currentValue = currentXSlice[y][0];
			greaterXNeighborValue = greaterXSlice[y][0];
			smallerYNeighborValue = currentXSlice[yMinusOne][0];
			greaterZNeighborValue = currentXSlice[y][1];
			if (topplePositionType8(y, currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
			// y = x, z = 1
			greaterXNeighborValue = greaterXSlice[y][1];
			smallerYNeighborValue = currentXSlice[yMinusOne][1];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[y][2];
			if (topplePositionType9(y, 1, currentValue, greaterXNeighborValue, smallerYNeighborValue, 1, 
					greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
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
				if (topplePositionType14(y, z, currentValue, greaterXNeighborValue, smallerYNeighborValue, 
						greaterZNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
					anyToppled = true;
				}
			}			
			// y = x, z = y - 1
			greaterXNeighborValue = greaterXSlice[y][z];
			smallerYNeighborValue = currentXSlice[yMinusOne][z];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[y][zPlusOne];
			if (topplePositionType9(y, z, currentValue, greaterXNeighborValue, smallerYNeighborValue, 2, 
					greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
			z = zPlusOne;
			// y = x, z = y
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterXNeighborValue = greaterXSlice[y][z];
			if (topplePositionType10(y, currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, 
					newGreaterXSlice)) {
				anyToppled = true;
			}
			grid[xMinusOne] = null;
		}
		xSlices[1] = currentXSlice;
		xSlices[2] = greaterXSlice;
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		return anyToppled;
	}

	private static boolean topplePositionType1(BigInt currentValue, BigInt greaterXNeighborValue, BigInt[][] newCurrentXSlice, 
			BigInt[][] newGreaterXSlice) {
		boolean toppled = false;
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			BigInt toShare = currentValue.subtract(greaterXNeighborValue);
			BigInt[] shareAndRemainder = toShare.divideAndRemainder(SEVEN);
			BigInt share = shareAndRemainder[0];
			if (!share.equals(BigInt.ZERO)) {
				toppled = true;
				newCurrentXSlice[0][0] = newCurrentXSlice[0][0].add(currentValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
				newGreaterXSlice[0][0] = newGreaterXSlice[0][0].add(share);
			} else {
				newCurrentXSlice[0][0] = newCurrentXSlice[0][0].add(currentValue);
			}			
		} else {
			newCurrentXSlice[0][0] = newCurrentXSlice[0][0].add(currentValue);
		}
		return toppled;
	}

	private static boolean topplePositionType2(BigInt currentValue, BigInt greaterXNeighborValue, BigInt smallerXNeighborValue, 
			BigInt greaterYNeighborValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][] newXSlices) {
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
		return topplePosition(newXSlices, currentValue, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType3(BigInt currentValue, BigInt greaterXNeighborValue, BigInt smallerYNeighborValue, 
			BigInt greaterZNeighborValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][] newXSlices) {
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
		return topplePosition(newXSlices, currentValue, 1, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType4(BigInt currentValue, BigInt greaterXNeighborValue, BigInt smallerZNeighborValue, 
			BigInt[][] newCurrentXSlice, BigInt[][] newGreaterXSlice) {
		boolean toppled = false;
		if (smallerZNeighborValue.compareTo(currentValue) < 0) {
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				if (smallerZNeighborValue.equals(greaterXNeighborValue)) {
					// gx = sz < current
					BigInt toShare = currentValue.subtract(greaterXNeighborValue);
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(SEVEN);
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newCurrentXSlice[1][0] = newCurrentXSlice[1][0].add(share).add(share);// one more for the symmetric position at the other side
					newCurrentXSlice[1][1] = newCurrentXSlice[1][1].add(currentValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
					newGreaterXSlice[1][1] = newGreaterXSlice[1][1].add(share);
				} else if (smallerZNeighborValue.compareTo(greaterXNeighborValue) < 0) {
					// sz < gx < current
					BigInt toShare = currentValue.subtract(greaterXNeighborValue); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(SEVEN);
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newCurrentXSlice[1][0] = newCurrentXSlice[1][0].add(share).add(share);
					newGreaterXSlice[1][1] = newGreaterXSlice[1][1].add(share);
					BigInt currentRemainingValue = currentValue.subtract(share.multiply(SIX));
					toShare = currentRemainingValue.subtract(smallerZNeighborValue); 
					shareAndRemainder = toShare.divideAndRemainder(FOUR);
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newCurrentXSlice[1][0] = newCurrentXSlice[1][0].add(share).add(share);
					newCurrentXSlice[1][1] = newCurrentXSlice[1][1].add(currentRemainingValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
				} else {
					// gx < sz < current
					BigInt toShare = currentValue.subtract(smallerZNeighborValue); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(SEVEN);
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newCurrentXSlice[1][0] = newCurrentXSlice[1][0].add(share).add(share);
					newGreaterXSlice[1][1] = newGreaterXSlice[1][1].add(share);
					BigInt currentRemainingValue = currentValue.subtract(share.multiply(SIX));
					toShare = currentRemainingValue.subtract(greaterXNeighborValue); 
					shareAndRemainder = toShare.divideAndRemainder(FOUR);
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newCurrentXSlice[1][1] = newCurrentXSlice[1][1].add(currentRemainingValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
					newGreaterXSlice[1][1] = newGreaterXSlice[1][1].add(share);
				}
			} else {
				// sz < current <= gx
				BigInt toShare = currentValue.subtract(smallerZNeighborValue); 
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(FOUR);
				BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newCurrentXSlice[1][0] = newCurrentXSlice[1][0].add(share).add(share);
				newCurrentXSlice[1][1] = newCurrentXSlice[1][1].add(currentValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
			}
		} else {
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				// gx < current <= sz
				BigInt toShare = currentValue.subtract(greaterXNeighborValue); 
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(FOUR);
				BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newCurrentXSlice[1][1] = newCurrentXSlice[1][1].add(currentValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
				newGreaterXSlice[1][1] = newGreaterXSlice[1][1].add(share);
			} else {
				newCurrentXSlice[1][1] = newCurrentXSlice[1][1].add(currentValue);
			}
		}
		return toppled;
	}

	private static boolean topplePositionType5(BigInt currentValue, BigInt greaterXNeighborValue, 
			BigInt smallerXNeighborValue, BigInt greaterYNeighborValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts,
			BigInt[][][] newXSlices) {
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
		return topplePosition(newXSlices, currentValue, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType6(int y, BigInt currentValue, BigInt gXValue, BigInt sXValue, 
			int sXShareMultiplier, BigInt gYValue, int gYShareMultiplier, BigInt sYValue, int sYShareMultiplier, 
			BigInt gZValue, int gZShareMultiplier, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][] newXSlices) {
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
		return topplePosition(newXSlices, currentValue, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType7(int coord, BigInt currentValue, BigInt gXValue, BigInt sXValue, 
			int sXShareMultiplier, BigInt gYValue, int gYShareMultiplier, BigInt sZValue, 
			int sZShareMultiplier, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][] newXSlices) {
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
		return topplePosition(newXSlices, currentValue, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType8(int y, BigInt currentValue, BigInt greaterXNeighborValue, BigInt smallerYNeighborValue, 
			BigInt greaterZNeighborValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][] newXSlices ) {
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
		return topplePosition(newXSlices, currentValue, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType9(int y, int z, BigInt currentValue, BigInt gXValue, BigInt sYValue, 
			int sYShareMultiplier, BigInt gZValue, int gZShareMultiplier, BigInt sZValue, int sZShareMultiplier, 
			BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, 
			BigInt[][][] newXSlices) {
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
		return topplePosition(newXSlices, currentValue, y, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType10(int coord, BigInt currentValue, BigInt greaterXNeighborValue, 
			BigInt smallerZNeighborValue, BigInt[][] newCurrentXSlice, BigInt[][] newGreaterXSlice) {
		boolean toppled = false;
		if (smallerZNeighborValue.compareTo(currentValue) < 0) {
			int coordMinusOne = coord - 1;
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				if (smallerZNeighborValue.equals(greaterXNeighborValue)) {
					// gx = sz < current
					BigInt toShare = currentValue.subtract(greaterXNeighborValue); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(SEVEN);
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newCurrentXSlice[coord][coordMinusOne] = newCurrentXSlice[coord][coordMinusOne].add(share);
					newCurrentXSlice[coord][coord] = newCurrentXSlice[coord][coord].add(currentValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
					newGreaterXSlice[coord][coord] = newGreaterXSlice[coord][coord].add(share);
				} else if (smallerZNeighborValue.compareTo(greaterXNeighborValue) < 0) {
					// sz < gx < current
					BigInt toShare = currentValue.subtract(greaterXNeighborValue); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(SEVEN);
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newCurrentXSlice[coord][coordMinusOne] = newCurrentXSlice[coord][coordMinusOne].add(share);
					newGreaterXSlice[coord][coord] = newGreaterXSlice[coord][coord].add(share);
					BigInt currentRemainingValue = currentValue.subtract(share.multiply(SIX));
					toShare = currentRemainingValue.subtract(smallerZNeighborValue); 
					shareAndRemainder = toShare.divideAndRemainder(FOUR);
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newCurrentXSlice[coord][coordMinusOne] = newCurrentXSlice[coord][coordMinusOne].add(share);
					newCurrentXSlice[coord][coord] = newCurrentXSlice[coord][coord].add(currentRemainingValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
				} else {
					// gx < sz < current
					BigInt toShare = currentValue.subtract(smallerZNeighborValue); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(SEVEN);
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newCurrentXSlice[coord][coordMinusOne] = newCurrentXSlice[coord][coordMinusOne].add(share);
					newGreaterXSlice[coord][coord] = newGreaterXSlice[coord][coord].add(share);
					BigInt currentRemainingValue = currentValue.subtract(share.multiply(SIX));
					toShare = currentRemainingValue.subtract(greaterXNeighborValue); 
					shareAndRemainder = toShare.divideAndRemainder(FOUR);
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newCurrentXSlice[coord][coord] = newCurrentXSlice[coord][coord].add(currentRemainingValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
					newGreaterXSlice[coord][coord] = newGreaterXSlice[coord][coord].add(share);
				}
			} else {
				// sz < current <= gx
				BigInt toShare = currentValue.subtract(smallerZNeighborValue); 
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(FOUR);
				BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newCurrentXSlice[coord][coordMinusOne] = newCurrentXSlice[coord][coordMinusOne].add(share);
				newCurrentXSlice[coord][coord] = newCurrentXSlice[coord][coord].add(currentValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
			}
		} else {
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				// gx < current <= sz
				BigInt toShare = currentValue.subtract(greaterXNeighborValue); 
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(FOUR);
				BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
				}
				newCurrentXSlice[coord][coord] = newCurrentXSlice[coord][coord].add(currentValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
				newGreaterXSlice[coord][coord] = newGreaterXSlice[coord][coord].add(share);
			} else {
				newCurrentXSlice[coord][coord] = newCurrentXSlice[coord][coord].add(currentValue);
			}
		}
		return toppled;
	}

	private static boolean topplePositionType11(int y, int z, BigInt currentValue, BigInt gXValue, BigInt sXValue, 
			int sXShareMultiplier, BigInt gYValue, int gYShareMultiplier, BigInt sYValue, int sYShareMultiplier, 
			BigInt gZValue, int gZShareMultiplier, BigInt sZValue, int sZShareMultiplier, BigInt[] relevantNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantNeighborCoords, 
			int[] relevantNeighborShareMultipliers, 
			BigInt[][][] newXSlices) {
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
		return topplePosition(newXSlices, currentValue, y, z, relevantNeighborValues, sortedNeighborsIndexes, relevantNeighborCoords, 
				relevantNeighborShareMultipliers, relevantNeighborCount);
	}

	private static boolean topplePositionType12(int y, BigInt currentValue, BigInt greaterXNeighborValue, 
			BigInt smallerXNeighborValue, BigInt greaterYNeighborValue, BigInt smallerYNeighborValue, 
			BigInt greaterZNeighborValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, 
			BigInt[][][] newXSlices) {
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
		return topplePosition(newXSlices, currentValue, y, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, 
				relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType13(int coord, BigInt currentValue, BigInt greaterXNeighborValue, 
			BigInt smallerXNeighborValue, BigInt greaterYNeighborValue, BigInt smallerZNeighborValue, 
			BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][] newXSlices) {
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
		return topplePosition(newXSlices, currentValue, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType14(int y, int z, BigInt currentValue, BigInt greaterXNeighborValue, 
			BigInt smallerYNeighborValue,	BigInt greaterZNeighborValue, BigInt smallerZNeighborValue, 
			BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][] newXSlices) {
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
		return topplePosition(newXSlices, currentValue, y, z, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionType15(int y, int z, BigInt currentValue, BigInt greaterXNeighborValue, 
			BigInt smallerXNeighborValue,	BigInt greaterYNeighborValue, BigInt smallerYNeighborValue, 
			BigInt greaterZNeighborValue, BigInt smallerZNeighborValue, BigInt[] relevantNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantNeighborCoords, BigInt[][][] newXSlices) {
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
		return topplePosition(newXSlices, currentValue, y, z, relevantNeighborValues, sortedNeighborsIndexes, relevantNeighborCoords, 
				relevantNeighborCount);
	}
	
	private static boolean topplePosition(BigInt[][][] newXSlices, BigInt value, int y, int z, BigInt[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int neighborCount) {
		boolean toppled = false;
		switch (neighborCount) {
			case 3:
				Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, neighborValues, sortedNeighborsIndexes, 
						neighborCoords, 3);
				break;
			case 2:
				BigInt n0Val = neighborValues[0], n1Val = neighborValues[1];
				int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
				if (n0Val.equals(n1Val)) {
					// n0Val = n1Val < value
					BigInt toShare = value.subtract(n0Val); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(THREE);
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share);
					newXSlices[1][y][z] = newXSlices[1][y][z].add(value).subtract(toShare).add(share).add(shareAndRemainder[1]);
				} else if (n0Val.compareTo(n1Val) < 0) {
					// n0Val < n1Val < value
					BigInt toShare = value.subtract(n1Val); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(THREE);
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share);
					BigInt currentRemainingValue = value.subtract(share.multiply(BigInt.valueOf(neighborCount)));
					toShare = currentRemainingValue.subtract(n0Val);
					shareAndRemainder = toShare.divideAndRemainder(TWO);
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share);
					newXSlices[1][y][z] = newXSlices[1][y][z].add(currentRemainingValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
				} else {
					// n1Val < n0Val < value
					BigInt toShare = value.subtract(n0Val); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(THREE);
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share);
					BigInt currentRemainingValue = value.subtract(share.multiply(BigInt.valueOf(neighborCount)));
					toShare = currentRemainingValue.subtract(n1Val);
					shareAndRemainder = toShare.divideAndRemainder(TWO);
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share);
					newXSlices[1][y][z] = newXSlices[1][y][z].add(currentRemainingValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
				}				
				break;
			case 1:
				BigInt toShare = value.subtract(neighborValues[0]);
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(TWO);
				BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
					value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
					int[] nc = neighborCoords[0];
					newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
				}
				// no break
			case 0:
				newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
				break;
			default: // 6, 5, 4
				Utils.sortDescending(neighborCount, neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, neighborValues, sortedNeighborsIndexes, neighborCoords, 
						neighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(BigInt[][][] newXSlices, BigInt value, int y, int z, 
			BigInt[] neighborValues, int[] sortedNeighborsIndexes, int[][] neighborCoords, int neighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		BigInt neighborValue = neighborValues[0];
		BigInt toShare = value.subtract(neighborValue);
		BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
		BigInt share = shareAndRemainder[0];
		if (!share.equals(BigInt.ZERO)) {
			toppled = true;
			value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
			for (int j = 0; j < neighborCount; j++) {
				int[] nc = neighborCoords[sortedNeighborsIndexes[j]];
				newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
			}
		}
		BigInt previousNeighborValue = neighborValue;
		shareCount--;
		for (int i = 1; i < neighborCount; i++) {
			neighborValue = neighborValues[i];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
				share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
					value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
					for (int j = i; j < neighborCount; j++) {
						int[] nc = neighborCoords[sortedNeighborsIndexes[j]];
						newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
		return toppled;
	}
	
	private static boolean topplePosition(BigInt[][][] newXSlices, BigInt value, int y, int z, BigInt[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
			case 3:
				Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, 
						asymmetricNeighborCoords, asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
				break;
			case 2:
				BigInt n0Val = asymmetricNeighborValues[0], n1Val = asymmetricNeighborValues[1];
				int[] n0Coords = asymmetricNeighborCoords[0], n1Coords = asymmetricNeighborCoords[1];
				int n0Mult = asymmetricNeighborShareMultipliers[0], n1Mult = asymmetricNeighborShareMultipliers[1];
				int shareCount = neighborCount + 1;
				if (n0Val.equals(n1Val)) {
					// n0Val = n1Val < value
					BigInt toShare = value.subtract(n0Val); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(BigInt.valueOf(n0Mult)));
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(BigInt.valueOf(n1Mult)));
					newXSlices[1][y][z] = newXSlices[1][y][z].add(value).subtract(toShare).add(share).add(shareAndRemainder[1]);
				} else if (n0Val.compareTo(n1Val) < 0) {
					// n0Val < n1Val < value
					BigInt toShare = value.subtract(n1Val); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(BigInt.valueOf(n0Mult)));
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(BigInt.valueOf(n1Mult)));
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					BigInt currentRemainingValue = value.subtract(share.multiply(BigInt.valueOf(neighborCount)));
					toShare = currentRemainingValue.subtract(n0Val);
					shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(BigInt.valueOf(n0Mult)));
					newXSlices[1][y][z] = newXSlices[1][y][z].add(currentRemainingValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
				} else {
					// n1Val < n0Val < value
					BigInt toShare = value.subtract(n0Val); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(BigInt.valueOf(n0Mult)));
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(BigInt.valueOf(n1Mult)));
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					BigInt currentRemainingValue = value.subtract(share.multiply(BigInt.valueOf(neighborCount)));
					toShare = currentRemainingValue.subtract(n1Val);
					shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(BigInt.valueOf(n1Mult)));
					newXSlices[1][y][z] = newXSlices[1][y][z].add(currentRemainingValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
				}				
				break;
			case 1:
				shareCount = neighborCount + 1;
				BigInt toShare = value.subtract(asymmetricNeighborValues[0]);
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
				BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
					value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
					int[] nc = asymmetricNeighborCoords[0];
					newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(BigInt.valueOf(asymmetricNeighborShareMultipliers[0])));
				}
				// no break
			case 0:
				newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
				break;
			default: // 6, 5, 4
				Utils.sortDescending(asymmetricNeighborCount, asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, asymmetricNeighborCoords, 
						asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(BigInt[][][] newXSlices, BigInt value, int y, int z, BigInt[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		BigInt neighborValue = asymmetricNeighborValues[0];
		BigInt toShare = value.subtract(neighborValue);
		BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
		BigInt share = shareAndRemainder[0];
		if (!share.equals(BigInt.ZERO)) {
			toppled = true;
			value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
			for (int j = 0; j < asymmetricNeighborCount; j++) {
				int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];
				newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(BigInt.valueOf(asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]])));
			}
		}
		BigInt previousNeighborValue = neighborValue;
		shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[0]];
		for (int i = 1; i < asymmetricNeighborCount; i++) {
			neighborValue = asymmetricNeighborValues[i];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
				share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
					value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
					for (int j = i; j < asymmetricNeighborCount; j++) {
						int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];
						newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(BigInt.valueOf(asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]])));
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
		return toppled;
	}
	
	private static boolean topplePosition(BigInt[][][] newXSlices, BigInt value, int y, int z, BigInt[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		boolean toppled = false;
		switch (neighborCount) {
			case 3:
				Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, neighborValues, sortedNeighborsIndexes, 
						neighborCoords, neighborShareMultipliers, 3);
				break;
			case 2:
				BigInt n0Val = neighborValues[0], n1Val = neighborValues[1];
				int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
				int n0Mult = neighborShareMultipliers[0], n1Mult = neighborShareMultipliers[1];
				if (n0Val.equals(n1Val)) {
					// n0Val = n1Val < value
					BigInt toShare = value.subtract(n0Val); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(THREE);
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(BigInt.valueOf(n0Mult)));
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(BigInt.valueOf(n1Mult)));
					newXSlices[1][y][z] = newXSlices[1][y][z].add(value).subtract(toShare).add(share).add(shareAndRemainder[1]);
				} else if (n0Val.compareTo(n1Val) < 0) {
					// n0Val < n1Val < value
					BigInt toShare = value.subtract(n1Val); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(THREE);
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(BigInt.valueOf(n0Mult)));
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(BigInt.valueOf(n1Mult)));
					BigInt currentRemainingValue = value.subtract(share.multiply(BigInt.valueOf(neighborCount)));
					toShare = currentRemainingValue.subtract(n0Val);
					shareAndRemainder = toShare.divideAndRemainder(TWO);
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(BigInt.valueOf(n0Mult)));
					newXSlices[1][y][z] = newXSlices[1][y][z].add(currentRemainingValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
				} else {
					// n1Val < n0Val < value
					BigInt toShare = value.subtract(n0Val); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(THREE);
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(BigInt.valueOf(n0Mult)));
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(BigInt.valueOf(n1Mult)));
					BigInt currentRemainingValue = value.subtract(share.multiply(BigInt.valueOf(neighborCount)));
					toShare = currentRemainingValue.subtract(n1Val);
					shareAndRemainder = toShare.divideAndRemainder(TWO);
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(BigInt.valueOf(n1Mult)));
					newXSlices[1][y][z] = newXSlices[1][y][z].add(currentRemainingValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
				}				
				break;
			case 1:
				BigInt toShare = value.subtract(neighborValues[0]);
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(TWO);
				BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
					value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
					int[] nc = neighborCoords[0];
					newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(BigInt.valueOf(neighborShareMultipliers[0])));
				}
				// no break
			case 0:
				newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
				break;
			default: // 6, 5, 4
				Utils.sortDescending(neighborCount, neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, neighborValues, sortedNeighborsIndexes, neighborCoords, 
						neighborShareMultipliers, neighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(BigInt[][][] newXSlices, BigInt value, int y, int z, BigInt[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		BigInt neighborValue = neighborValues[0];
		BigInt toShare = value.subtract(neighborValue);
		BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
		BigInt share = shareAndRemainder[0];
		if (!share.equals(BigInt.ZERO)) {
			toppled = true;
			value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
			for (int j = 0; j < neighborCount; j++) {
				int[] nc = neighborCoords[sortedNeighborsIndexes[j]];
				newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(BigInt.valueOf(neighborShareMultipliers[sortedNeighborsIndexes[j]])));
			}
		}
		BigInt previousNeighborValue = neighborValue;
		shareCount--;
		for (int i = 1; i < neighborCount; i++) {
			neighborValue = neighborValues[i];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
				share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
					value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
					for (int j = i; j < neighborCount; j++) {
						int[] nc = neighborCoords[sortedNeighborsIndexes[j]];
						newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(BigInt.valueOf(neighborShareMultipliers[sortedNeighborsIndexes[j]])));
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
		return toppled;
	}
	
	private static boolean topplePosition(BigInt[][][] newXSlices, BigInt value, int y, int z, BigInt[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
			case 3:
				Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, 
						asymmetricNeighborCoords, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
				break;
			case 2:
				BigInt n0Val = asymmetricNeighborValues[0], n1Val = asymmetricNeighborValues[1];
				int[] n0Coords = asymmetricNeighborCoords[0], n1Coords = asymmetricNeighborCoords[1];
				int shareCount = neighborCount + 1;
				if (n0Val.equals(n1Val)) {
					// n0Val = n1Val < value
					BigInt toShare = value.subtract(n0Val); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share);
					newXSlices[1][y][z] = newXSlices[1][y][z].add(value).subtract(toShare).add(share).add(shareAndRemainder[1]);
				} else if (n0Val.compareTo(n1Val) < 0) {
					// n0Val < n1Val < value
					BigInt toShare = value.subtract(n1Val); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share);
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					BigInt currentRemainingValue = value.subtract(share.multiply(BigInt.valueOf(neighborCount)));
					toShare = currentRemainingValue.subtract(n0Val);
					shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share);
					newXSlices[1][y][z] = newXSlices[1][y][z].add(currentRemainingValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
				} else {
					// n1Val < n0Val < value
					BigInt toShare = value.subtract(n0Val); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share);
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					BigInt currentRemainingValue = value.subtract(share.multiply(BigInt.valueOf(neighborCount)));
					toShare = currentRemainingValue.subtract(n1Val);
					shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share);
					newXSlices[1][y][z] = newXSlices[1][y][z].add(currentRemainingValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
				}				
				break;
			case 1:
				shareCount = neighborCount + 1;
				BigInt toShare = value.subtract(asymmetricNeighborValues[0]);
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
				BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
					value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
					int[] nc = asymmetricNeighborCoords[0];
					newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
				}
				// no break
			case 0:
				newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
				break;
			default: // 6, 5, 4
				Utils.sortDescending(asymmetricNeighborCount, asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, asymmetricNeighborValues, sortedNeighborsIndexes, asymmetricNeighborCoords, 
						asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(BigInt[][][] newXSlices, BigInt value, int y, int z, BigInt[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		int shareCount = neighborCount + 1;
		BigInt neighborValue = asymmetricNeighborValues[0];
		BigInt toShare = value.subtract(neighborValue);
		BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
		BigInt share = shareAndRemainder[0];
		if (!share.equals(BigInt.ZERO)) {
			toppled = true;
			value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
			for (int j = 0; j < asymmetricNeighborCount; j++) {
				int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];
				newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
			}
		}
		BigInt previousNeighborValue = neighborValue;
		shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[0]];
		for (int i = 1; i < asymmetricNeighborCount; i++) {
			neighborValue = asymmetricNeighborValues[i];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
				share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
					value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
					for (int j = i; j < asymmetricNeighborCount; j++) {
						int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[j]];
						newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
		return toppled;
	}
	
	@Override
	public BigInt getFromPosition(int x, int y, int z) {	
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		if (z < 0) z = -z;
		if (x >= y) {
			if (y >= z) {
				//x >= y >= z
				if (x < grid.length) {
					return grid[x][y][z];
				}
			} else if (x >= z) { 
				//x >= z > y
				if (x < grid.length) {
					return grid[x][z][y];
				}
			} else {
				//z > x >= y
				if (z < grid.length) {
					return grid[z][x][y];
				}
			}
		} else if (y >= z) {
			if (y < grid.length) {
				if (x >= z) {
					//y > x >= z
					return grid[y][x][z];
				} else {
					//y >= z > x
					return grid[y][z][x];
				}
			}
		} else {
			// z > y > x
			if (z < grid.length) {
				return grid[z][y][x];
			}
		}
		return BigInt.ZERO;
	}
	
	@Override
	public BigInt getFromAsymmetricPosition(int x, int y, int z) {	
		return grid[x][y][z];
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
	public BigInt getInitialValue() {
		return initialValue;
	}	

	@Override
	public String getName() {
		return "Aether";
	}
	
	@Override
	public String getSubfolderPath() {
		String strInitialValue = initialValue.toString();
		if (strInitialValue.length() > Constants.MAX_INITIAL_VALUE_LENGTH_IN_PATH)
			strInitialValue = creationTimestamp;
		return getName() + "/3D/" + strInitialValue;
	}
	
	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		Utils.serializeToFile(this, backupPath, backupName);
	}
	
}