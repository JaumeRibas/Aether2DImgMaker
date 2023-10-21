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
import cellularautomata.Constants;
import cellularautomata.Utils;
import cellularautomata.model3d.SymmetricNumericModel3D;
import cellularautomata.numbers.BigInt;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 3D with a bounded cube-shaped grid of uneven side and a single source initial configuration at its center
 * 
 * @author Jaume
 *
 */
public class BigIntAether3DCubicGrid implements SymmetricNumericModel3D<BigInt>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -369090693127386206L;
	private static final BigInt TWO = BigInt.valueOf(2);
	private static final BigInt THREE = BigInt.valueOf(3);
	private static final BigInt FOUR = BigInt.valueOf(4);
	private static final BigInt SIX = BigInt.valueOf(6);
	private static final BigInt SEVEN = BigInt.valueOf(7);

	private final int side;
	private final int singleSourceCoord;
	private final BigInt initialValue;
	private long step;
	/** A 3D array representing the grid */
	private BigInt[][][] grid;
	private Boolean changed = null;
	
	/**
	 * Used in {@link #getSubfolderPath()}.
	 */
	private final String folderName;
	
	public BigIntAether3DCubicGrid(int side, BigInt initialValue) {
		if (side%2 == 0)
			throw new IllegalArgumentException("Only uneven grid sides are supported.");
		if (side < 13) {
			throw new IllegalArgumentException("Grid side cannot be smaller than thirteen.");
		}
		this.initialValue = initialValue;
		this.side = side;
		this.singleSourceCoord = side/2;
		grid = Utils.buildAnisotropic3DBigIntArray(singleSourceCoord + 1);
		grid[0][0][0] = this.initialValue;
		step = 0;
		String strInitialValue = Utils.numberToPlainTextMaxLength(initialValue, Constants.MAX_INITIAL_VALUE_LENGTH_IN_PATH);
		if (strInitialValue == null) {
			folderName = Utils.getFileNameSafeTimeStamp();
		} else {
			folderName = strInitialValue;
		}
	}
	
	/**
	 * Creates an instance restoring a backup
	 * 
	 * @param backupPath the path to the backup file to restore.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 */
	public BigIntAether3DCubicGrid(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		BigIntAether3DCubicGrid data = (BigIntAether3DCubicGrid) Utils.deserializeFromFile(backupPath);
		initialValue = data.initialValue;
		grid = data.grid;
		side = data.side;
		singleSourceCoord = data.singleSourceCoord;
		step = data.step;
		folderName = data.folderName;
		changed = data.changed;
	}

	@Override
	public Boolean nextStep() {
		BigInt[][][] newGrid = new BigInt[grid.length][][];
		boolean changed = false;
		BigInt[][] smallerXSlice = null, currentXSlice = grid[0], greaterXSlice = grid[1];
		BigInt[][] newSmallerXSlice = null, 
				newCurrentXSlice = Utils.buildAnisotropic2DBigIntArray(1), 
				newGreaterXSlice = Utils.buildAnisotropic2DBigIntArray(2);// build new grid progressively to save memory
		newGrid[0] = newCurrentXSlice;
		newGrid[1] = newGreaterXSlice;
		// i = 0, j = 0, k = 0
		BigInt currentValue = currentXSlice[0][0];
		BigInt greaterXNeighborValue = greaterXSlice[0][0];
		if (topplePositionOfType1(currentValue, greaterXNeighborValue, newCurrentXSlice, newGreaterXSlice)) {
			changed = true;
		}
		// i = 1, j = 0, k = 0
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
		if (topplePositionOfType2(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// i = 1, j = 1, k = 0
		// reuse values obtained previously
		BigInt smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterXNeighborValue = greaterXSlice[1][0];
		BigInt greaterZNeighborValue = currentXSlice[1][1];
		if (topplePositionOfType3(currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// i = 1, j = 1, k = 1
		// reuse values obtained previously
		BigInt smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterXNeighborValue = greaterXSlice[1][1];
		if (topplePositionOfType4(currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, newGreaterXSlice)) {
			changed = true;
		}
		grid[0] = null;// free old grid progressively to save memory
		// i = 2, j = 0, k = 0
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
		if (topplePositionOfType5(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// i = 2, j = 1, k = 0
		greaterXNeighborValue = greaterXSlice[1][0];
		smallerXNeighborValue = smallerXSlice[1][0];
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice[2][0];
		greaterZNeighborValue = currentXSlice[1][1];
		if (topplePositionOfType6(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// i = 2, j = 1, k = 1
		greaterXNeighborValue = greaterXSlice[1][1];
		smallerXNeighborValue = smallerXSlice[1][1];
		greaterYNeighborValue = currentXSlice[2][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		if (topplePositionOfType7(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// i = 2, j = 2, k = 0
		currentValue = currentXSlice[2][0];
		greaterXNeighborValue = greaterXSlice[2][0];
		// reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		if (topplePositionOfType8(2, currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, 
				newXSlices)) {
			changed = true;
		}
		// i = 2, j = 2, k = 1
		greaterXNeighborValue = greaterXSlice[2][1];
		smallerYNeighborValue = currentXSlice[1][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[2][2];
		if (topplePositionOfType9(2, 1, currentValue, greaterXNeighborValue, smallerYNeighborValue, 2, 
				greaterZNeighborValue, 3, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// i = 2, j = 2, k = 2
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterXNeighborValue = greaterXSlice[2][2];
		if (topplePositionOfType10(2, currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, 
				newGreaterXSlice)) {
			changed = true;
		}
		grid[1] = null;
		// i = 3, j = 0, k = 0
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
		if (topplePositionOfType5(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// i = 3, j = 1, k = 0
		greaterXNeighborValue = greaterXSlice[1][0];
		smallerXNeighborValue = smallerXSlice[1][0];
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice[2][0];
		greaterZNeighborValue = currentXSlice[1][1];
		if (topplePositionOfType6(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// i = 3, j = 1, k = 1
		greaterXNeighborValue = greaterXSlice[1][1];
		smallerXNeighborValue = smallerXSlice[1][1];
		greaterYNeighborValue = currentXSlice[2][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		if (topplePositionOfType7(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// i = 3, j = 2, k = 0
		currentValue = currentXSlice[2][0];
		greaterXNeighborValue = greaterXSlice[2][0];
		smallerXNeighborValue = smallerXSlice[2][0];
		// reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice[3][0];
		if (topplePositionOfType6(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// i = 3, j = 2, k = 1
		greaterXNeighborValue = greaterXSlice[2][1];
		smallerXNeighborValue = smallerXSlice[2][1];
		greaterYNeighborValue = currentXSlice[3][1];
		smallerYNeighborValue = currentXSlice[1][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[2][2];
		if (topplePositionOfType11(2, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
			changed = true;
		}
		// i = 3, j = 2, k = 2
		greaterXNeighborValue = greaterXSlice[2][2];
		smallerXNeighborValue = smallerXSlice[2][2];
		greaterYNeighborValue = currentXSlice[3][2];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		if (topplePositionOfType7(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// i = 3, j = 3, k = 0
		currentValue = currentXSlice[3][0];
		greaterXNeighborValue = greaterXSlice[3][0];
		smallerYNeighborValue = currentXSlice[2][0];
		greaterZNeighborValue = currentXSlice[3][1];
		if (topplePositionOfType8(3, currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// i = 3, j = 3, k = 1
		greaterXNeighborValue = greaterXSlice[3][1];
		smallerYNeighborValue = currentXSlice[2][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[3][2];
		if (topplePositionOfType9(3, 1, currentValue, greaterXNeighborValue, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// i = 3, j = 3, k = 2
		greaterXNeighborValue = greaterXSlice[3][2];
		smallerYNeighborValue = currentXSlice[2][2];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[3][3];
		if (topplePositionOfType9(3, 2, currentValue, greaterXNeighborValue, smallerYNeighborValue, 2, 
				greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// i = 3, j = 3, k = 3
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterXNeighborValue = greaterXSlice[3][3];
		if (topplePositionOfType10(3, currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, 
				newGreaterXSlice)) {
			changed = true;
		}
		grid[2] = null;
		// 4 <= i < edge - 1
		int edge = grid.length - 1;
		BigInt[][][] xSlices = new BigInt[][][] {null, currentXSlice, greaterXSlice};
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		if (toppleRangeBeyondI3(xSlices, newXSlices, newGrid, 4, edge, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts)) { // is it faster to reuse these arrays?
			changed = true;
		}
		//edge
		if (toppleEdge(xSlices, newXSlices, newGrid, edge, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts)) {
			changed = true;
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
	
	private boolean toppleRangeBeyondI3(BigInt[][][] xSlices, BigInt[][][] newXSlices, BigInt[][][] newGrid, int minI, int maxX, 
			BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts) {
		boolean anyToppled = false;
		int i = minI, iMinusOne = i - 1, iPlusOne = i + 1, iPlusTwo = iPlusOne + 1;
		BigInt[][] smallerXSlice = null, currentXSlice = xSlices[1], greaterXSlice = xSlices[2];
		BigInt[][] newSmallerXSlice = null, newCurrentXSlice = newXSlices[1], newGreaterXSlice = newXSlices[2];
		for (; i < maxX; iMinusOne = i, i = iPlusOne, iPlusOne = iPlusTwo, iPlusTwo++) {
			// j = 0, k = 0
			smallerXSlice = currentXSlice;
			currentXSlice = greaterXSlice;
			greaterXSlice = grid[iPlusOne];
			newSmallerXSlice = newCurrentXSlice;
			newCurrentXSlice = newGreaterXSlice;
			newGreaterXSlice = Utils.buildAnisotropic2DBigIntArray(iPlusTwo);
			newGrid[iPlusOne] = newGreaterXSlice;
			newXSlices[0] = newSmallerXSlice;
			newXSlices[1] = newCurrentXSlice;
			newXSlices[2] = newGreaterXSlice;
			BigInt currentValue = currentXSlice[0][0];
			BigInt greaterXNeighborValue = greaterXSlice[0][0];
			BigInt smallerXNeighborValue = smallerXSlice[0][0];
			BigInt greaterYNeighborValue = currentXSlice[1][0];
			if (topplePositionOfType5(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
			// j = 1, k = 0
			greaterXNeighborValue = greaterXSlice[1][0];
			smallerXNeighborValue = smallerXSlice[1][0];
			// reuse values obtained previously
			BigInt smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterYNeighborValue = currentXSlice[2][0];
			BigInt greaterZNeighborValue = currentXSlice[1][1];
			if (topplePositionOfType6(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
			// j = 1, k = 1
			greaterXNeighborValue = greaterXSlice[1][1];
			smallerXNeighborValue = smallerXSlice[1][1];
			greaterYNeighborValue = currentXSlice[2][1];
			// reuse values obtained previously
			BigInt smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			if (topplePositionOfType7(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
			// j = 2, k = 0
			currentValue = currentXSlice[2][0];
			greaterXNeighborValue = greaterXSlice[2][0];
			smallerXNeighborValue = smallerXSlice[2][0];
			// reuse values obtained previously
			smallerYNeighborValue = smallerZNeighborValue;
			greaterZNeighborValue = greaterYNeighborValue;
			greaterYNeighborValue = currentXSlice[3][0];
			if (topplePositionOfType12(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
			// j = 2, k = 1
			greaterXNeighborValue = greaterXSlice[2][1];
			smallerXNeighborValue = smallerXSlice[2][1];
			greaterYNeighborValue = currentXSlice[3][1];
			smallerYNeighborValue = currentXSlice[1][1];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[2][2];
			if (topplePositionOfType11(2, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
				anyToppled = true;
			}
			// j = 2, k = 2
			greaterXNeighborValue = greaterXSlice[2][2];
			smallerXNeighborValue = smallerXSlice[2][2];
			greaterYNeighborValue = currentXSlice[3][2];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			if (topplePositionOfType13(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
			int j = 3, jMinusOne = 2, jPlusOne = 4;
			for (int lastJ = i - 2; j <= lastJ;) {
				// k = 0
				currentValue = currentXSlice[j][0];
				greaterXNeighborValue = greaterXSlice[j][0];
				smallerXNeighborValue = smallerXSlice[j][0];
				greaterYNeighborValue = currentXSlice[jPlusOne][0];
				smallerYNeighborValue = currentXSlice[jMinusOne][0];
				greaterZNeighborValue = currentXSlice[j][1];
				if (topplePositionOfType12(j, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
						smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
					anyToppled = true;
				}
				// k = 1
				greaterXNeighborValue = greaterXSlice[j][1];
				smallerXNeighborValue = smallerXSlice[j][1];
				greaterYNeighborValue = currentXSlice[jPlusOne][1];
				smallerYNeighborValue = currentXSlice[jMinusOne][1];
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterZNeighborValue = currentXSlice[j][2];
				if (topplePositionOfType11(j, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, 
						relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
					anyToppled = true;
				}
				int k = 2, kPlusOne = 3;
				for (int lastK = j - 2; k <= lastK;) {
					greaterXNeighborValue = greaterXSlice[j][k];
					smallerXNeighborValue = smallerXSlice[j][k];
					greaterYNeighborValue = currentXSlice[jPlusOne][k];
					smallerYNeighborValue = currentXSlice[jMinusOne][k];
					// reuse values obtained previously
					smallerZNeighborValue = currentValue;
					currentValue = greaterZNeighborValue;
					greaterZNeighborValue = currentXSlice[j][kPlusOne];
					if (topplePositionOfType15(j, k, currentValue, greaterXNeighborValue, smallerXNeighborValue, 
							greaterYNeighborValue, smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue, 
							relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, newXSlices)) {
						anyToppled = true;
					}
					k = kPlusOne;
					kPlusOne++;
				}
				// k = j - 1
				greaterXNeighborValue = greaterXSlice[j][k];
				smallerXNeighborValue = smallerXSlice[j][k];
				greaterYNeighborValue = currentXSlice[jPlusOne][k];
				smallerYNeighborValue = currentXSlice[jMinusOne][k];
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterZNeighborValue = currentXSlice[j][kPlusOne];
				if (topplePositionOfType11(j, k, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, 
						relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
					anyToppled = true;
				}
				// k = j
				k = kPlusOne;
				greaterXNeighborValue = greaterXSlice[j][k];
				smallerXNeighborValue = smallerXSlice[j][k];
				greaterYNeighborValue = currentXSlice[jPlusOne][k];
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				if (topplePositionOfType13(j, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
						smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
					anyToppled = true;
				}				 
				jMinusOne = j;
				j = jPlusOne;
				jPlusOne++;
			}
			// j = i - 1, k = 0
			currentValue = currentXSlice[j][0];
			greaterXNeighborValue = greaterXSlice[j][0];
			smallerXNeighborValue = smallerXSlice[j][0];
			greaterYNeighborValue = currentXSlice[jPlusOne][0];
			smallerYNeighborValue = currentXSlice[jMinusOne][0];
			greaterZNeighborValue = currentXSlice[j][1];
			if (topplePositionOfType6(j, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
			// j = i - 1, k = 1
			greaterXNeighborValue = greaterXSlice[j][1];
			smallerXNeighborValue = smallerXSlice[j][1];
			greaterYNeighborValue = currentXSlice[jPlusOne][1];
			smallerYNeighborValue = currentXSlice[jMinusOne][1];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[j][2];
			if (topplePositionOfType11(j, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
				anyToppled = true;
			}
			int k = 2, kPlusOne = 3, lastK = j - 2;
			for(; k <= lastK;) {
				greaterXNeighborValue = greaterXSlice[j][k];
				smallerXNeighborValue = smallerXSlice[j][k];
				greaterYNeighborValue = currentXSlice[jPlusOne][k];
				smallerYNeighborValue = currentXSlice[jMinusOne][k];
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterZNeighborValue = currentXSlice[j][kPlusOne];
				if (topplePositionOfType11(j, k, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
						greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, 
						relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
					anyToppled = true;
				}
				k = kPlusOne;
				kPlusOne++;
			}
			// j = i - 1, k = j - 1
			greaterXNeighborValue = greaterXSlice[j][k];
			smallerXNeighborValue = smallerXSlice[j][k];
			greaterYNeighborValue = currentXSlice[jPlusOne][k];
			smallerYNeighborValue = currentXSlice[jMinusOne][k];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[j][kPlusOne];
			if (topplePositionOfType11(j, k, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
				anyToppled = true;
			}
			k = kPlusOne;
			kPlusOne++;
			// j = i - 1, k = j
			greaterXNeighborValue = greaterXSlice[j][k];
			smallerXNeighborValue = smallerXSlice[j][k];
			greaterYNeighborValue = currentXSlice[jPlusOne][k];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			if (topplePositionOfType7(j, currentValue, greaterXNeighborValue, smallerXNeighborValue, 3, 
					greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
			jMinusOne = j;
			j = jPlusOne;
			// j = i, k = 0
			currentValue = currentXSlice[j][0];
			greaterXNeighborValue = greaterXSlice[j][0];
			smallerYNeighborValue = currentXSlice[jMinusOne][0];
			greaterZNeighborValue = currentXSlice[j][1];
			if (topplePositionOfType8(j, currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
			// j = i, k = 1
			greaterXNeighborValue = greaterXSlice[j][1];
			smallerYNeighborValue = currentXSlice[jMinusOne][1];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[j][2];
			if (topplePositionOfType9(j, 1, currentValue, greaterXNeighborValue, smallerYNeighborValue, 1, 
					greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
			k = 2;
			kPlusOne = 3;
			lastK++;
			for(; k <= lastK; k = kPlusOne, kPlusOne++) {
				greaterXNeighborValue = greaterXSlice[j][k];
				smallerYNeighborValue = currentXSlice[jMinusOne][k];
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterZNeighborValue = currentXSlice[j][kPlusOne];
				if (topplePositionOfType14(j, k, currentValue, greaterXNeighborValue, smallerYNeighborValue, 
						greaterZNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
					anyToppled = true;
				}
			}			
			// j = i, k = j - 1
			greaterXNeighborValue = greaterXSlice[j][k];
			smallerYNeighborValue = currentXSlice[jMinusOne][k];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[j][kPlusOne];
			if (topplePositionOfType9(j, k, currentValue, greaterXNeighborValue, smallerYNeighborValue, 2, 
					greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
			k = kPlusOne;
			// j = i, k = j
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterXNeighborValue = greaterXSlice[j][k];
			if (topplePositionOfType10(j, currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, 
					newGreaterXSlice)) {
				anyToppled = true;
			}
			grid[iMinusOne] = null;
		}
		xSlices[1] = currentXSlice;
		xSlices[2] = greaterXSlice;
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		return anyToppled;
	}

	private boolean toppleEdge(BigInt[][][] xSlices, BigInt[][][] newXSlices, BigInt[][][] newGrid, int i, 
			BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts) {
		boolean anyToppled = false;
		BigInt[][] smallerXSlice = null, currentXSlice = null;
		BigInt[][] newSmallerXSlice = null, newCurrentXSlice = null;
		// j = 0, k = 0
		smallerXSlice = xSlices[1];
		currentXSlice = xSlices[2];
		newSmallerXSlice = newXSlices[1];
		newCurrentXSlice = newXSlices[2];
		newXSlices[0] = newSmallerXSlice;
		newXSlices[1] = newCurrentXSlice;
		BigInt currentValue = currentXSlice[0][0];
		BigInt smallerXNeighborValue = smallerXSlice[0][0];
		BigInt greaterYNeighborValue = currentXSlice[1][0];
		if (topplePositionOfType5Edge(currentValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			anyToppled = true;
		}
		// j = 1, k = 0
		smallerXNeighborValue = smallerXSlice[1][0];
		// reuse values obtained previously
		BigInt smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice[2][0];
		BigInt greaterZNeighborValue = currentXSlice[1][1];
		if (topplePositionOfType6Edge(1, currentValue, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			anyToppled = true;
		}
		// j = 1, k = 1
		smallerXNeighborValue = smallerXSlice[1][1];
		greaterYNeighborValue = currentXSlice[2][1];
		// reuse values obtained previously
		BigInt smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		if (topplePositionOfType7Edge(1, currentValue, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			anyToppled = true;
		}
		// j = 2, k = 0
		currentValue = currentXSlice[2][0];
		smallerXNeighborValue = smallerXSlice[2][0];
		// reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice[3][0];
		if (topplePositionOfType12Edge(2, currentValue, smallerXNeighborValue, greaterYNeighborValue, 
				smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			anyToppled = true;
		}
		// j = 2, k = 1
		smallerXNeighborValue = smallerXSlice[2][1];
		greaterYNeighborValue = currentXSlice[3][1];
		smallerYNeighborValue = currentXSlice[1][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[2][2];
		if (topplePositionOfType11Edge(2, 1, currentValue, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
			anyToppled = true;
		}
		// j = 2, k = 2
		smallerXNeighborValue = smallerXSlice[2][2];
		greaterYNeighborValue = currentXSlice[3][2];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		if (topplePositionOfType13Edge(2, currentValue, smallerXNeighborValue, greaterYNeighborValue, 
				smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			anyToppled = true;
		}
		int j = 3, jMinusOne = 2, jPlusOne = 4;
		for (int lastJ = i - 2; j <= lastJ;) {
			// k = 0
			currentValue = currentXSlice[j][0];
			smallerXNeighborValue = smallerXSlice[j][0];
			greaterYNeighborValue = currentXSlice[jPlusOne][0];
			smallerYNeighborValue = currentXSlice[jMinusOne][0];
			greaterZNeighborValue = currentXSlice[j][1];
			if (topplePositionOfType12Edge(j, currentValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
			// k = 1
			smallerXNeighborValue = smallerXSlice[j][1];
			greaterYNeighborValue = currentXSlice[jPlusOne][1];
			smallerYNeighborValue = currentXSlice[jMinusOne][1];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[j][2];
			if (topplePositionOfType11Edge(j, 1, currentValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
				anyToppled = true;
			}
			int k = 2, kPlusOne = 3;
			for (int lastK = j - 2; k <= lastK;) {
				smallerXNeighborValue = smallerXSlice[j][k];
				greaterYNeighborValue = currentXSlice[jPlusOne][k];
				smallerYNeighborValue = currentXSlice[jMinusOne][k];
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterZNeighborValue = currentXSlice[j][kPlusOne];
				if (topplePositionOfType15Edge(j, k, currentValue, smallerXNeighborValue, 
						greaterYNeighborValue, smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue, 
						relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, newXSlices)) {
					anyToppled = true;
				}
				k = kPlusOne;
				kPlusOne++;
			}
			// k = j - 1
			smallerXNeighborValue = smallerXSlice[j][k];
			greaterYNeighborValue = currentXSlice[jPlusOne][k];
			smallerYNeighborValue = currentXSlice[jMinusOne][k];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[j][kPlusOne];
			if (topplePositionOfType11Edge(j, k, currentValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
				anyToppled = true;
			}
			// k = j
			k = kPlusOne;
			smallerXNeighborValue = smallerXSlice[j][k];
			greaterYNeighborValue = currentXSlice[jPlusOne][k];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			if (topplePositionOfType13Edge(j, currentValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}				 
			jMinusOne = j;
			j = jPlusOne;
			jPlusOne++;
		}
		// j = i - 1, k = 0
		currentValue = currentXSlice[j][0];
		smallerXNeighborValue = smallerXSlice[j][0];
		greaterYNeighborValue = currentXSlice[jPlusOne][0];
		smallerYNeighborValue = currentXSlice[jMinusOne][0];
		greaterZNeighborValue = currentXSlice[j][1];
		if (topplePositionOfType6Edge(j, currentValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			anyToppled = true;
		}
		// j = i - 1, k = 1
		smallerXNeighborValue = smallerXSlice[j][1];
		greaterYNeighborValue = currentXSlice[jPlusOne][1];
		smallerYNeighborValue = currentXSlice[jMinusOne][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[j][2];
		if (topplePositionOfType11Edge(j, 1, currentValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
			anyToppled = true;
		}
		int k = 2, kPlusOne = 3, lastK = j - 2;
		for(; k <= lastK;) {
			smallerXNeighborValue = smallerXSlice[j][k];
			greaterYNeighborValue = currentXSlice[jPlusOne][k];
			smallerYNeighborValue = currentXSlice[jMinusOne][k];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[j][kPlusOne];
			if (topplePositionOfType11Edge(j, k, currentValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
				anyToppled = true;
			}
			k = kPlusOne;
			kPlusOne++;
		}
		// j = i - 1, k = j - 1
		smallerXNeighborValue = smallerXSlice[j][k];
		greaterYNeighborValue = currentXSlice[jPlusOne][k];
		smallerYNeighborValue = currentXSlice[jMinusOne][k];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[j][kPlusOne];
		if (topplePositionOfType11Edge(j, k, currentValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, newXSlices)) {
			anyToppled = true;
		}
		k = kPlusOne;
		kPlusOne++;
		// j = i - 1, k = j
		smallerXNeighborValue = smallerXSlice[j][k];
		greaterYNeighborValue = currentXSlice[jPlusOne][k];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		if (topplePositionOfType7Edge(j, currentValue, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			anyToppled = true;
		}
		jMinusOne = j;
		j = jPlusOne;
		// j = i, k = 0
		currentValue = currentXSlice[j][0];
		smallerYNeighborValue = currentXSlice[jMinusOne][0];
		greaterZNeighborValue = currentXSlice[j][1];
		if (topplePositionOfType8Edge(j, currentValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			anyToppled = true;
		}
		// j = i, k = 1
		smallerYNeighborValue = currentXSlice[jMinusOne][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[j][2];
		if (topplePositionOfType9Edge(j, 1, currentValue, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			anyToppled = true;
		}
		k = 2;
		kPlusOne = 3;
		lastK++;
		for(; k <= lastK; k = kPlusOne, kPlusOne++) {
			smallerYNeighborValue = currentXSlice[jMinusOne][k];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[j][kPlusOne];
			if (topplePositionOfType14Edge(j, k, currentValue, smallerYNeighborValue, 
					greaterZNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
				anyToppled = true;
			}
		}			
		// j = i, k = j - 1
		smallerYNeighborValue = currentXSlice[jMinusOne][k];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[j][kPlusOne];
		if (topplePositionOfType9Edge(j, k, currentValue, smallerYNeighborValue, 2, 
				greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			anyToppled = true;
		}
		k = kPlusOne;
		// j = i, k = j
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		if (topplePositionOfType10Edge(j, currentValue, smallerZNeighborValue, newCurrentXSlice)) {
			anyToppled = true;
		}
		return anyToppled;
	}
	
	private static boolean topplePositionOfType1(BigInt currentValue, BigInt greaterXNeighborValue, BigInt[][] newCurrentXSlice, 
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

	private static boolean topplePositionOfType2(BigInt currentValue, BigInt greaterXNeighborValue, BigInt smallerXNeighborValue, 
			BigInt greaterYNeighborValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;// this is the index of the new slice: 0->newSmallerXSlice, 1->newCurrentXSlice, 2->newGreaterXSlice
			nc[1] = 0;// this is the actual j index
			nc[2] = 0;// this is the actual k index
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

	private static boolean topplePositionOfType3(BigInt currentValue, BigInt greaterXNeighborValue, BigInt smallerYNeighborValue, 
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

	private static boolean topplePositionOfType4(BigInt currentValue, BigInt greaterXNeighborValue, BigInt smallerZNeighborValue, 
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
		} else if (greaterXNeighborValue.compareTo(currentValue) < 0) {
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
			// gx >= current <= sz
			newCurrentXSlice[1][1] = newCurrentXSlice[1][1].add(currentValue);
		}
		return toppled;
	}

	private static boolean topplePositionOfType5(BigInt currentValue, BigInt greaterXNeighborValue, 
			BigInt smallerXNeighborValue, BigInt greaterYNeighborValue, BigInt[] relevantAsymmetricNeighborValues, 
			int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts,
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
	
	private static boolean topplePositionOfType5Edge(BigInt currentValue, BigInt smallerXNeighborValue, BigInt greaterYNeighborValue, 
			BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
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

	private static boolean topplePositionOfType6(int j, BigInt currentValue, BigInt gXValue, BigInt sXValue, 
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
			nc[1] = j;
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
			nc[1] = j;
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
			nc[1] = j + 1;
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
			nc[1] = j - 1;
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
			nc[1] = j;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newXSlices, currentValue, j, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}
	
	private static boolean topplePositionOfType6Edge(int j, BigInt currentValue, BigInt sXValue, 
			int sXShareMultiplier, BigInt gYValue, int gYShareMultiplier, BigInt sYValue, int sYShareMultiplier, 
			BigInt gZValue, int gZShareMultiplier, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (sXValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = j;
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
			nc[1] = j + 1;
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
			nc[1] = j - 1;
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
			nc[1] = j;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newXSlices, currentValue, j, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType7(int coord, BigInt currentValue, BigInt gXValue, BigInt sXValue, 
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
	
	private static boolean topplePositionOfType7Edge(int coord, BigInt currentValue, BigInt sXValue, 
			int sXShareMultiplier, BigInt gYValue, int gYShareMultiplier, BigInt sZValue, 
			int sZShareMultiplier, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
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

	private static boolean topplePositionOfType8(int j, BigInt currentValue, BigInt greaterXNeighborValue, BigInt smallerYNeighborValue, 
			BigInt greaterZNeighborValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][] newXSlices ) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = j;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = j - 1;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = j;
			nc[2] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newXSlices, currentValue, j, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}
	
	private static boolean topplePositionOfType8Edge(int j, BigInt currentValue, BigInt smallerYNeighborValue, 
			BigInt greaterZNeighborValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][] newXSlices ) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (smallerYNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = j - 1;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = j;
			nc[2] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newXSlices, currentValue, j, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType9(int j, int k, BigInt currentValue, BigInt gXValue, BigInt sYValue, 
			int sYShareMultiplier, BigInt gZValue, int gZShareMultiplier, BigInt sZValue, int sZShareMultiplier, 
			BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (gXValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gXValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = j;
			nc[2] = k;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (sYValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = j - 1;
			nc[2] = k;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = j;
			nc[2] = k + 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = j;
			nc[2] = k - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newXSlices, currentValue, j, k, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}
	
	private static boolean topplePositionOfType9Edge(int j, int k, BigInt currentValue, BigInt sYValue, 
			int sYShareMultiplier, BigInt gZValue, int gZShareMultiplier, BigInt sZValue, int sZShareMultiplier, 
			BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, 
			BigInt[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (sYValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sYValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = j - 1;
			nc[2] = k;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sYShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (gZValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = gZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = j;
			nc[2] = k + 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = gZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (sZValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = sZValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = j;
			nc[2] = k - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = sZShareMultiplier;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newXSlices, currentValue, j, k, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType10(int coord, BigInt currentValue, BigInt greaterXNeighborValue, 
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
		} else if (greaterXNeighborValue.compareTo(currentValue) < 0) {
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
			// gx >= current <= sz
			newCurrentXSlice[coord][coord] = newCurrentXSlice[coord][coord].add(currentValue);
		}
		return toppled;
	}
	
	private static boolean topplePositionOfType10Edge(int coord, BigInt currentValue, 
			BigInt smallerZNeighborValue, BigInt[][] newCurrentXSlice) {
		boolean toppled = false;
		if (smallerZNeighborValue.compareTo(currentValue) < 0) {
			int coordMinusOne = coord - 1;
			// sz < current
			BigInt toShare = currentValue.subtract(smallerZNeighborValue); 
			BigInt[] shareAndRemainder = toShare.divideAndRemainder(FOUR);
			BigInt share = shareAndRemainder[0];
			if (!share.equals(BigInt.ZERO)) {
				toppled = true;
			}
			newCurrentXSlice[coord][coordMinusOne] = newCurrentXSlice[coord][coordMinusOne].add(share);
			newCurrentXSlice[coord][coord] = newCurrentXSlice[coord][coord].add(currentValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
		} else {
			// sz >= current
			newCurrentXSlice[coord][coord] = newCurrentXSlice[coord][coord].add(currentValue);
		}
		return toppled;
	}

	private static boolean topplePositionOfType11(int j, int k, BigInt currentValue, BigInt gXValue, BigInt sXValue, 
			int sXShareMultiplier, BigInt gYValue, int gYShareMultiplier, BigInt sYValue, int sYShareMultiplier, 
			BigInt gZValue, int gZShareMultiplier, BigInt sZValue, int sZShareMultiplier, BigInt[] relevantNeighborValues, 
			int[] sortedNeighborsIndexes, int[][] relevantNeighborCoords, int[] relevantNeighborShareMultipliers, BigInt[][][] newXSlices) {
		int relevantNeighborCount = 0;
		if (gXValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount ] = gXValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 2;
			nc[1] = j;
			nc[2] = k;
			relevantNeighborShareMultipliers[relevantNeighborCount] = 1;
			relevantNeighborCount++;
		}
		if (sXValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = sXValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 0;
			nc[1] = j;
			nc[2] = k;
			relevantNeighborShareMultipliers[relevantNeighborCount] = sXShareMultiplier;
			relevantNeighborCount++;
		}
		if (gYValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = gYValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = j + 1;
			nc[2] = k;
			relevantNeighborShareMultipliers[relevantNeighborCount] = gYShareMultiplier;
			relevantNeighborCount++;
		}
		if (sYValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = sYValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = j - 1;
			nc[2] = k;
			relevantNeighborShareMultipliers[relevantNeighborCount] = sYShareMultiplier;
			relevantNeighborCount++;
		}
		if (gZValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = gZValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = j;
			nc[2] = k + 1;
			relevantNeighborShareMultipliers[relevantNeighborCount] = gZShareMultiplier;
			relevantNeighborCount++;
		}
		if (sZValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = sZValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = j;
			nc[2] = k - 1;
			relevantNeighborShareMultipliers[relevantNeighborCount] = sZShareMultiplier;
			relevantNeighborCount++;
		}
		return topplePosition(newXSlices, currentValue, j, k, relevantNeighborValues, sortedNeighborsIndexes, relevantNeighborCoords, 
				relevantNeighborShareMultipliers, relevantNeighborCount);
	}
	
	private static boolean topplePositionOfType11Edge(int j, int k, BigInt currentValue, BigInt sXValue, 
			int sXShareMultiplier, BigInt gYValue, int gYShareMultiplier, BigInt sYValue, int sYShareMultiplier, 
			BigInt gZValue, int gZShareMultiplier, BigInt sZValue, int sZShareMultiplier, BigInt[] relevantNeighborValues, 
			int[] sortedNeighborsIndexes, int[][] relevantNeighborCoords, int[] relevantNeighborShareMultipliers, 
			BigInt[][][] newXSlices) {
		int relevantNeighborCount = 0;
		if (sXValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = sXValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 0;
			nc[1] = j;
			nc[2] = k;
			relevantNeighborShareMultipliers[relevantNeighborCount] = sXShareMultiplier;
			relevantNeighborCount++;
		}
		if (gYValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = gYValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = j + 1;
			nc[2] = k;
			relevantNeighborShareMultipliers[relevantNeighborCount] = gYShareMultiplier;
			relevantNeighborCount++;
		}
		if (sYValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = sYValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = j - 1;
			nc[2] = k;
			relevantNeighborShareMultipliers[relevantNeighborCount] = sYShareMultiplier;
			relevantNeighborCount++;
		}
		if (gZValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = gZValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = j;
			nc[2] = k + 1;
			relevantNeighborShareMultipliers[relevantNeighborCount] = gZShareMultiplier;
			relevantNeighborCount++;
		}
		if (sZValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = sZValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = j;
			nc[2] = k - 1;
			relevantNeighborShareMultipliers[relevantNeighborCount] = sZShareMultiplier;
			relevantNeighborCount++;
		}
		return topplePosition(newXSlices, currentValue, j, k, relevantNeighborValues, sortedNeighborsIndexes, relevantNeighborCoords, 
				relevantNeighborShareMultipliers, relevantNeighborCount);
	}

	private static boolean topplePositionOfType12(int j, BigInt currentValue, BigInt greaterXNeighborValue, 
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
			nc[1] = j;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerXNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = j;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = j + 1;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = j - 1;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = j;
			nc[2] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newXSlices, currentValue, j, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, 
				relevantAsymmetricNeighborCount);
	}
	
	private static boolean topplePositionOfType12Edge(int j, BigInt currentValue, 
			BigInt smallerXNeighborValue, BigInt greaterYNeighborValue, BigInt smallerYNeighborValue, 
			BigInt greaterZNeighborValue, BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, 
			BigInt[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (smallerXNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = j;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = j + 1;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = j - 1;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = j;
			nc[2] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newXSlices, currentValue, j, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, 
				relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType13(int coord, BigInt currentValue, BigInt greaterXNeighborValue, 
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
	
	private static boolean topplePositionOfType13Edge(int coord, BigInt currentValue, 
			BigInt smallerXNeighborValue, BigInt greaterYNeighborValue, BigInt smallerZNeighborValue, 
			BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
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

	private static boolean topplePositionOfType14(int j, int k, BigInt currentValue, BigInt greaterXNeighborValue, 
			BigInt smallerYNeighborValue,	BigInt greaterZNeighborValue, BigInt smallerZNeighborValue, 
			BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = j;
			nc[2] = k;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = j - 1;
			nc[2] = k;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = j;
			nc[2] = k + 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerZNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = j;
			nc[2] = k - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newXSlices, currentValue, j, k, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}
	
	private static boolean topplePositionOfType14Edge(int j, int k, BigInt currentValue, 
			BigInt smallerYNeighborValue,	BigInt greaterZNeighborValue, BigInt smallerZNeighborValue, 
			BigInt[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigInt[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (smallerYNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = j - 1;
			nc[2] = k;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = j;
			nc[2] = k + 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerZNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = j;
			nc[2] = k - 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		return topplePosition(newXSlices, currentValue, j, k, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static boolean topplePositionOfType15(int j, int k, BigInt currentValue, BigInt greaterXNeighborValue, 
			BigInt smallerXNeighborValue,	BigInt greaterYNeighborValue, BigInt smallerYNeighborValue, 
			BigInt greaterZNeighborValue, BigInt smallerZNeighborValue, BigInt[] relevantNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantNeighborCoords, BigInt[][][] newXSlices) {
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 2;
			nc[1] = j;
			nc[2] = k;
			relevantNeighborCount++;
		}
		if (smallerXNeighborValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 0;
			nc[1] = j;
			nc[2] = k;
			relevantNeighborCount++;
		}
		if (greaterYNeighborValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = j + 1;
			nc[2] = k;
			relevantNeighborCount++;
		}
		if (smallerYNeighborValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = j - 1;
			nc[2] = k;
			relevantNeighborCount++;
		}
		if (greaterZNeighborValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = j;
			nc[2] = k + 1;
			relevantNeighborCount++;
		}
		if (smallerZNeighborValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = smallerZNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = j;
			nc[2] = k - 1;
			relevantNeighborCount++;
		}
		return topplePosition(newXSlices, currentValue, j, k, relevantNeighborValues, sortedNeighborsIndexes, relevantNeighborCoords, 
				relevantNeighborCount);
	}
	
	private static boolean topplePositionOfType15Edge(int j, int k, BigInt currentValue, 
			BigInt smallerXNeighborValue,	BigInt greaterYNeighborValue, BigInt smallerYNeighborValue, 
			BigInt greaterZNeighborValue, BigInt smallerZNeighborValue, BigInt[] relevantNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantNeighborCoords, BigInt[][][] newXSlices) {
		int relevantNeighborCount = 0;
		if (smallerXNeighborValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 0;
			nc[1] = j;
			nc[2] = k;
			relevantNeighborCount++;
		}
		if (greaterYNeighborValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = j + 1;
			nc[2] = k;
			relevantNeighborCount++;
		}
		if (smallerYNeighborValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = j - 1;
			nc[2] = k;
			relevantNeighborCount++;
		}
		if (greaterZNeighborValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = j;
			nc[2] = k + 1;
			relevantNeighborCount++;
		}
		if (smallerZNeighborValue.compareTo(currentValue) < 0) {
			relevantNeighborValues[relevantNeighborCount] = smallerZNeighborValue;
			int[] nc = relevantNeighborCoords[relevantNeighborCount];
			nc[0] = 1;
			nc[1] = j;
			nc[2] = k - 1;
			relevantNeighborCount++;
		}
		return topplePosition(newXSlices, currentValue, j, k, relevantNeighborValues, sortedNeighborsIndexes, relevantNeighborCoords, 
				relevantNeighborCount);
	}
	
	private static boolean topplePosition(BigInt[][][] newXSlices, BigInt value, int j, int k, BigInt[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int neighborCount) {
		boolean toppled = false;
		switch (neighborCount) {
			case 3:
				Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, j, k, neighborValues, sortedNeighborsIndexes, 
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(value).subtract(toShare).add(share).add(shareAndRemainder[1]);
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(currentRemainingValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(currentRemainingValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
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
				newXSlices[1][j][k] = newXSlices[1][j][k].add(value);
				break;
			default: // 6, 5, 4
				Utils.sortDescending(neighborCount, neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, j, k, neighborValues, sortedNeighborsIndexes, neighborCoords, 
						neighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(BigInt[][][] newXSlices, BigInt value, int j, int k, 
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
			for (int neighborIndex = 0; neighborIndex < neighborCount; neighborIndex++) {
				int[] nc = neighborCoords[sortedNeighborsIndexes[neighborIndex]];
				newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
			}
		}
		BigInt previousNeighborValue = neighborValue;
		shareCount--;
		for (int neighborIndex = 1; neighborIndex < neighborCount; neighborIndex++) {
			neighborValue = neighborValues[neighborIndex];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
				share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
					value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
					for (int remainingNeighborIndex = neighborIndex; remainingNeighborIndex < neighborCount; remainingNeighborIndex++) {
						int[] nc = neighborCoords[sortedNeighborsIndexes[remainingNeighborIndex]];
						newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newXSlices[1][j][k] = newXSlices[1][j][k].add(value);
		return toppled;
	}
	
	private static boolean topplePosition(BigInt[][][] newXSlices, BigInt value, int j, int k, BigInt[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
			case 3:
				Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, j, k, asymmetricNeighborValues, sortedNeighborsIndexes, 
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(value).subtract(toShare).add(share).add(shareAndRemainder[1]);
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(currentRemainingValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(currentRemainingValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
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
				newXSlices[1][j][k] = newXSlices[1][j][k].add(value);
				break;
			default: // 6, 5, 4
				Utils.sortDescending(asymmetricNeighborCount, asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, j, k, asymmetricNeighborValues, sortedNeighborsIndexes, asymmetricNeighborCoords, 
						asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(BigInt[][][] newXSlices, BigInt value, int j, int k, BigInt[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
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
			for (int neighborIndex = 0; neighborIndex < asymmetricNeighborCount; neighborIndex++) {
				int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[neighborIndex]];
				newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(BigInt.valueOf(asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[neighborIndex]])));
			}
		}
		BigInt previousNeighborValue = neighborValue;
		shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[0]];
		for (int neighborIndex = 1; neighborIndex < asymmetricNeighborCount; neighborIndex++) {
			neighborValue = asymmetricNeighborValues[neighborIndex];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
				share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
					value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
					for (int remainingNeighborIndex = neighborIndex; remainingNeighborIndex < asymmetricNeighborCount; remainingNeighborIndex++) {
						int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[remainingNeighborIndex]];
						newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(BigInt.valueOf(asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[remainingNeighborIndex]])));
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[neighborIndex]];
		}
		newXSlices[1][j][k] = newXSlices[1][j][k].add(value);
		return toppled;
	}
	
	private static boolean topplePosition(BigInt[][][] newXSlices, BigInt value, int j, int k, BigInt[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		boolean toppled = false;
		switch (neighborCount) {
			case 3:
				Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, j, k, neighborValues, sortedNeighborsIndexes, 
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(value).subtract(toShare).add(share).add(shareAndRemainder[1]);
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(currentRemainingValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(currentRemainingValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
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
				newXSlices[1][j][k] = newXSlices[1][j][k].add(value);
				break;
			default: // 6, 5, 4
				Utils.sortDescending(neighborCount, neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, j, k, neighborValues, sortedNeighborsIndexes, neighborCoords, 
						neighborShareMultipliers, neighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(BigInt[][][] newXSlices, BigInt value, int j, int k, BigInt[] neighborValues, int[] sortedNeighborsIndexes,
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
			for (int neighborIndex = 0; neighborIndex < neighborCount; neighborIndex++) {
				int[] nc = neighborCoords[sortedNeighborsIndexes[neighborIndex]];
				newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(BigInt.valueOf(neighborShareMultipliers[sortedNeighborsIndexes[neighborIndex]])));
			}
		}
		BigInt previousNeighborValue = neighborValue;
		shareCount--;
		for (int neighborIndex = 1; neighborIndex < neighborCount; neighborIndex++) {
			neighborValue = neighborValues[neighborIndex];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
				share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
					value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
					for (int remainingNeighborIndex = neighborIndex; remainingNeighborIndex < neighborCount; remainingNeighborIndex++) {
						int[] nc = neighborCoords[sortedNeighborsIndexes[remainingNeighborIndex]];
						newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(BigInt.valueOf(neighborShareMultipliers[sortedNeighborsIndexes[remainingNeighborIndex]])));
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newXSlices[1][j][k] = newXSlices[1][j][k].add(value);
		return toppled;
	}
	
	private static boolean topplePosition(BigInt[][][] newXSlices, BigInt value, int j, int k, BigInt[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
			case 3:
				Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, j, k, asymmetricNeighborValues, sortedNeighborsIndexes, 
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(value).subtract(toShare).add(share).add(shareAndRemainder[1]);
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(currentRemainingValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(currentRemainingValue).subtract(toShare).add(share).add(shareAndRemainder[1]);
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
				newXSlices[1][j][k] = newXSlices[1][j][k].add(value);
				break;
			default: // 6, 5, 4
				Utils.sortDescending(asymmetricNeighborCount, asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, j, k, asymmetricNeighborValues, sortedNeighborsIndexes, asymmetricNeighborCoords, 
						asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(BigInt[][][] newXSlices, BigInt value, int j, int k, BigInt[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
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
			for (int neighborIndex = 0; neighborIndex < asymmetricNeighborCount; neighborIndex++) {
				int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[neighborIndex]];
				newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
			}
		}
		BigInt previousNeighborValue = neighborValue;
		shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[0]];
		for (int neighborIndex = 1; neighborIndex < asymmetricNeighborCount; neighborIndex++) {
			neighborValue = asymmetricNeighborValues[neighborIndex];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
				share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
					value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
					for (int remainingNeighborIndex = neighborIndex; remainingNeighborIndex < asymmetricNeighborCount; remainingNeighborIndex++) {
						int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[remainingNeighborIndex]];
						newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[neighborIndex]];
		}
		newXSlices[1][j][k] = newXSlices[1][j][k].add(value);
		return toppled;
	}
	
	@Override
	public BigInt getFromPosition(int x, int y, int z) {	
		int i = x - singleSourceCoord;
		int j = y - singleSourceCoord;
		int k = z - singleSourceCoord;
		if (i < 0) i = -i;
		if (j < 0) j = -j;
		if (k < 0) k = -k;
		if (i >= j) {
			if (j >= k) {
				//i >= j >= k
				return grid[i][j][k];
			} else if (i >= k) { 
				//i >= k > j
				return grid[i][k][j];
			} else {
				//k > i >= j
				return grid[k][i][j];
			}
		} else if (j >= k) {
			if (i >= k) {
				//j > i >= k
				return grid[j][i][k];
			} else {
				//j >= k > i
				return grid[j][k][i];
			}
		} else {
			// k > j > i
			return grid[k][j][i];
		}
	}
	
	@Override
	public BigInt getFromAsymmetricPosition(int x, int y, int z) {	
		int i = x - singleSourceCoord;
		int j = y - singleSourceCoord;
		int k = z - singleSourceCoord;
		return grid[i][j][k];
	}
	
	@Override
	public int getMinX() { return 0; }

	@Override
	public int getMaxX() { return getAsymmetricMaxX(); }

	@Override
	public int getAsymmetricMinX() { return singleSourceCoord; }

	@Override
	public int getAsymmetricMaxX() { return side - 1; }

	@Override
	public int getAsymmetricMinXAtY(int y) { return y; }

	@Override
	public int getAsymmetricMaxXAtY(int y) { return getAsymmetricMaxX(); }

	@Override
	public int getAsymmetricMinXAtZ(int z) { return z; }

	@Override
	public int getAsymmetricMaxXAtZ(int z) { return getAsymmetricMaxX(); }

	@Override
	public int getAsymmetricMinX(int y, int z) { return y; }

	@Override
	public int getAsymmetricMaxX(int y, int z) { return getAsymmetricMaxX(); }

	@Override
	public int getMinY() { return 0; }

	@Override
	public int getMaxY() { return getAsymmetricMaxX(); }

	@Override
	public int getAsymmetricMinY() { return singleSourceCoord; }

	@Override
	public int getAsymmetricMaxY() { return getAsymmetricMaxX(); }

	@Override
	public int getAsymmetricMinYAtX(int x) { return singleSourceCoord; }

	@Override
	public int getAsymmetricMaxYAtX(int x) { return x; }

	@Override
	public int getAsymmetricMinYAtZ(int z) { return z; }

	@Override
	public int getAsymmetricMaxYAtZ(int z) { return getAsymmetricMaxX(); }

	@Override
	public int getAsymmetricMinY(int x, int z) { return z; }

	@Override
	public int getAsymmetricMaxY(int x, int z) { return x; }

	@Override
	public int getMinZ() { return 0; }

	@Override
	public int getMaxZ() { return getAsymmetricMaxX(); }

	@Override
	public int getAsymmetricMinZ() { return singleSourceCoord; }

	@Override
	public int getAsymmetricMaxZ() { return getAsymmetricMaxX(); }

	@Override
	public int getAsymmetricMinZAtX(int x) { return singleSourceCoord; }

	@Override
	public int getAsymmetricMaxZAtX(int x) { return x; }

	@Override
	public int getAsymmetricMinZAtY(int y) { return singleSourceCoord; }

	@Override
	public int getAsymmetricMaxZAtY(int y) { return y; }

	@Override
	public int getAsymmetricMinZ(int x, int y) { return singleSourceCoord; }

	@Override
	public int getAsymmetricMaxZ(int x, int y) { return y; }
	
	@Override
	public long getStep() {
		return step;
	}
	
	/**
	 * Returns the initial value
	 * 
	 * @return the single source value
	 */
	public BigInt getInitialValue() {
		return initialValue;
	}	
	
	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		Utils.serializeToFile(this, backupPath, backupName);
	}

	@Override
	public String getName() {
		return "Aether";
	}

	@Override
	public String getSubfolderPath() {
		return getName() + "/3D/bounded_grid/" + side + "x" + side + "x" + side + "/" + folderName;
	}
	
	public int getSide() {
		return side;
	}
	
}