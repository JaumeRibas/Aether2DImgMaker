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
import org.apache.commons.math3.fraction.BigFraction;

import cellularautomata.Utils;
import cellularautomata.model3d.SymmetricNumericModel3D;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 3D with a bounded cube-shaped grid of uneven side and a single source initial configuration of infinity at its center
 * 
 * @author Jaume
 *
 */
public class Aether3DInfinityCubicGrid implements SymmetricNumericModel3D<BigFraction>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1928438678102746882L;

	/** A 3D array representing the grid */
	private BigFraction[][][] grid;

	private final boolean isPositive;
	private long step;	
	private final int side;
	private final int singleSourceCoord;
	
	public Aether3DInfinityCubicGrid(boolean isPositive, int side) {
		if (side%2 == 0)
			throw new IllegalArgumentException("Only uneven grid sides are supported.");
		if (side < 13) {
			throw new IllegalArgumentException("Grid side cannot be smaller than thirteen.");
		}
		this.isPositive = isPositive;
		this.side = side;
		this.singleSourceCoord = side/2;
		grid = Utils.buildAnisotropic3DBigFractionArray(side/2 + 1);
		grid[0][0][0] = isPositive? BigFraction.ONE : BigFraction.MINUS_ONE;
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
	public Aether3DInfinityCubicGrid(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		Aether3DInfinityCubicGrid data = (Aether3DInfinityCubicGrid) Utils.deserializeFromFile(backupPath);
		isPositive = data.isPositive;
		grid = data.grid;
		side = data.side;
		singleSourceCoord = data.singleSourceCoord;
		step = data.step;
	}

	@Override
	public Boolean nextStep() {
		BigFraction[][][] newGrid = new BigFraction[grid.length][][];
		BigFraction[][] smallerXSlice = null, currentXSlice = grid[0], greaterXSlice = grid[1];
		BigFraction[][] newSmallerXSlice = null, 
				newCurrentXSlice = Utils.buildAnisotropic2DBigFractionArray(1), 
				newGreaterXSlice = Utils.buildAnisotropic2DBigFractionArray(2);// build new grid progressively to save memory
		newGrid[0] = newCurrentXSlice;
		newGrid[1] = newGreaterXSlice;
		// i = 0, j = 0, k = 0
		BigFraction currentValue = currentXSlice[0][0];
		BigFraction greaterXNeighborValue = greaterXSlice[0][0];
		topplePositionType1(currentValue, greaterXNeighborValue, newCurrentXSlice, newGreaterXSlice);
		// i = 1, j = 0, k = 0
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
		topplePositionType2(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// i = 1, j = 1, k = 0
		// reuse values obtained previously
		BigFraction smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterXNeighborValue = greaterXSlice[1][0];
		BigFraction greaterZNeighborValue = currentXSlice[1][1];
		topplePositionType3(currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// i = 1, j = 1, k = 1
		// reuse values obtained previously
		BigFraction smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterXNeighborValue = greaterXSlice[1][1];
		topplePositionType4(currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, newGreaterXSlice);
		grid[0] = null;// free old grid progressively to save memory
		// i = 2, j = 0, k = 0
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
		topplePositionType5(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// i = 2, j = 1, k = 0
		greaterXNeighborValue = greaterXSlice[1][0];
		smallerXNeighborValue = smallerXSlice[1][0];
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice[2][0];
		greaterZNeighborValue = currentXSlice[1][1];
		topplePositionType6(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// i = 2, j = 1, k = 1
		greaterXNeighborValue = greaterXSlice[1][1];
		smallerXNeighborValue = smallerXSlice[1][1];
		greaterYNeighborValue = currentXSlice[2][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		topplePositionType7(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// i = 2, j = 2, k = 0
		currentValue = currentXSlice[2][0];
		greaterXNeighborValue = greaterXSlice[2][0];
		// reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		topplePositionType8(2, currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, 
				newXSlices);
		// i = 2, j = 2, k = 1
		greaterXNeighborValue = greaterXSlice[2][1];
		smallerYNeighborValue = currentXSlice[1][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[2][2];
		topplePositionType9(2, 1, currentValue, greaterXNeighborValue, smallerYNeighborValue, 2, 
				greaterZNeighborValue, 3, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// i = 2, j = 2, k = 2
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterXNeighborValue = greaterXSlice[2][2];
		topplePositionType10(2, currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, 
				newGreaterXSlice);
		grid[1] = null;
		// i = 3, j = 0, k = 0
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
		topplePositionType5(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// i = 3, j = 1, k = 0
		greaterXNeighborValue = greaterXSlice[1][0];
		smallerXNeighborValue = smallerXSlice[1][0];
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice[2][0];
		greaterZNeighborValue = currentXSlice[1][1];
		topplePositionType6(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// i = 3, j = 1, k = 1
		greaterXNeighborValue = greaterXSlice[1][1];
		smallerXNeighborValue = smallerXSlice[1][1];
		greaterYNeighborValue = currentXSlice[2][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		topplePositionType7(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// i = 3, j = 2, k = 0
		currentValue = currentXSlice[2][0];
		greaterXNeighborValue = greaterXSlice[2][0];
		smallerXNeighborValue = smallerXSlice[2][0];
		// reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice[3][0];
		topplePositionType6(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// i = 3, j = 2, k = 1
		greaterXNeighborValue = greaterXSlice[2][1];
		smallerXNeighborValue = smallerXSlice[2][1];
		greaterYNeighborValue = currentXSlice[3][1];
		smallerYNeighborValue = currentXSlice[1][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[2][2];
		topplePositionType11(2, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, newXSlices);
		// i = 3, j = 2, k = 2
		greaterXNeighborValue = greaterXSlice[2][2];
		smallerXNeighborValue = smallerXSlice[2][2];
		greaterYNeighborValue = currentXSlice[3][2];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		topplePositionType7(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// i = 3, j = 3, k = 0
		currentValue = currentXSlice[3][0];
		greaterXNeighborValue = greaterXSlice[3][0];
		smallerYNeighborValue = currentXSlice[2][0];
		greaterZNeighborValue = currentXSlice[3][1];
		topplePositionType8(3, currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// i = 3, j = 3, k = 1
		greaterXNeighborValue = greaterXSlice[3][1];
		smallerYNeighborValue = currentXSlice[2][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[3][2];
		topplePositionType9(3, 1, currentValue, greaterXNeighborValue, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// i = 3, j = 3, k = 2
		greaterXNeighborValue = greaterXSlice[3][2];
		smallerYNeighborValue = currentXSlice[2][2];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[3][3];
		topplePositionType9(3, 2, currentValue, greaterXNeighborValue, smallerYNeighborValue, 2, 
				greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// i = 3, j = 3, k = 3
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterXNeighborValue = greaterXSlice[3][3];
		topplePositionType10(3, currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, 
				newGreaterXSlice);
		grid[2] = null;
		// 4 <= i < edge - 1
		int edge = grid.length - 1;
		BigFraction[][][] xSlices = new BigFraction[][][] {null, currentXSlice, greaterXSlice};
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		toppleRangeBeyondI3(xSlices, newXSlices, newGrid, 4, edge, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts); // is it faster to reuse these arrays?
		//edge
		toppleEdge(xSlices, newXSlices, newGrid, edge, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts);
		grid = newGrid;
		step++;
		return true;
	}

	@Override
	public Boolean isChanged() {
		return step == 0 ? null : true;
	}
	
	private void toppleRangeBeyondI3(BigFraction[][][] xSlices, BigFraction[][][] newXSlices, BigFraction[][][] newGrid, int minI, int maxI, 
			BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts) {
		int i = minI, iMinusOne = i - 1, iPlusOne = i + 1, iPlusTwo = iPlusOne + 1;
		BigFraction[][] smallerXSlice = null, currentXSlice = xSlices[1], greaterXSlice = xSlices[2];
		BigFraction[][] newSmallerXSlice = null, newCurrentXSlice = newXSlices[1], newGreaterXSlice = newXSlices[2];
		for (; i < maxI; iMinusOne = i, i = iPlusOne, iPlusOne = iPlusTwo, iPlusTwo++) {
			// j = 0, k = 0
			smallerXSlice = currentXSlice;
			currentXSlice = greaterXSlice;
			greaterXSlice = grid[iPlusOne];
			newSmallerXSlice = newCurrentXSlice;
			newCurrentXSlice = newGreaterXSlice;
			newGreaterXSlice = Utils.buildAnisotropic2DBigFractionArray(iPlusTwo);
			newGrid[iPlusOne] = newGreaterXSlice;
			newXSlices[0] = newSmallerXSlice;
			newXSlices[1] = newCurrentXSlice;
			newXSlices[2] = newGreaterXSlice;
			BigFraction currentValue = currentXSlice[0][0];
			BigFraction greaterXNeighborValue = greaterXSlice[0][0];
			BigFraction smallerXNeighborValue = smallerXSlice[0][0];
			BigFraction greaterYNeighborValue = currentXSlice[1][0];
			topplePositionType5(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			// j = 1, k = 0
			greaterXNeighborValue = greaterXSlice[1][0];
			smallerXNeighborValue = smallerXSlice[1][0];
			// reuse values obtained previously
			BigFraction smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterYNeighborValue = currentXSlice[2][0];
			BigFraction greaterZNeighborValue = currentXSlice[1][1];
			topplePositionType6(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			// j = 1, k = 1
			greaterXNeighborValue = greaterXSlice[1][1];
			smallerXNeighborValue = smallerXSlice[1][1];
			greaterYNeighborValue = currentXSlice[2][1];
			// reuse values obtained previously
			BigFraction smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			topplePositionType7(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			// j = 2, k = 0
			currentValue = currentXSlice[2][0];
			greaterXNeighborValue = greaterXSlice[2][0];
			smallerXNeighborValue = smallerXSlice[2][0];
			// reuse values obtained previously
			smallerYNeighborValue = smallerZNeighborValue;
			greaterZNeighborValue = greaterYNeighborValue;
			greaterYNeighborValue = currentXSlice[3][0];
			topplePositionType12(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			// j = 2, k = 1
			greaterXNeighborValue = greaterXSlice[2][1];
			smallerXNeighborValue = smallerXSlice[2][1];
			greaterYNeighborValue = currentXSlice[3][1];
			smallerYNeighborValue = currentXSlice[1][1];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[2][2];
			topplePositionType11(2, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newXSlices);
			// j = 2, k = 2
			greaterXNeighborValue = greaterXSlice[2][2];
			smallerXNeighborValue = smallerXSlice[2][2];
			greaterYNeighborValue = currentXSlice[3][2];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			topplePositionType13(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			int j = 3, jMinusOne = 2, jPlusOne = 4;
			for (int lastJ = i - 2; j <= lastJ;) {
				// k = 0
				currentValue = currentXSlice[j][0];
				greaterXNeighborValue = greaterXSlice[j][0];
				smallerXNeighborValue = smallerXSlice[j][0];
				greaterYNeighborValue = currentXSlice[jPlusOne][0];
				smallerYNeighborValue = currentXSlice[jMinusOne][0];
				greaterZNeighborValue = currentXSlice[j][1];
				topplePositionType12(j, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
						smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
				// k = 1
				greaterXNeighborValue = greaterXSlice[j][1];
				smallerXNeighborValue = smallerXSlice[j][1];
				greaterYNeighborValue = currentXSlice[jPlusOne][1];
				smallerYNeighborValue = currentXSlice[jMinusOne][1];
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterZNeighborValue = currentXSlice[j][2];
				topplePositionType11(j, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, 
						relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newXSlices);
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
					topplePositionType15(j, k, currentValue, greaterXNeighborValue, smallerXNeighborValue, 
							greaterYNeighborValue, smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue, 
							relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, newXSlices);
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
				topplePositionType11(j, k, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, 
						relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newXSlices);
				// k = j
				k = kPlusOne;
				greaterXNeighborValue = greaterXSlice[j][k];
				smallerXNeighborValue = smallerXSlice[j][k];
				greaterYNeighborValue = currentXSlice[jPlusOne][k];
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				topplePositionType13(j, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
						smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborSymmetryCounts, newXSlices);				 
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
			topplePositionType6(j, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			// j = i - 1, k = 1
			greaterXNeighborValue = greaterXSlice[j][1];
			smallerXNeighborValue = smallerXSlice[j][1];
			greaterYNeighborValue = currentXSlice[jPlusOne][1];
			smallerYNeighborValue = currentXSlice[jMinusOne][1];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[j][2];
			topplePositionType11(j, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newXSlices);
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
				topplePositionType11(j, k, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
						greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, 
						relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newXSlices);
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
			topplePositionType11(j, k, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newXSlices);
			k = kPlusOne;
			kPlusOne++;
			// j = i - 1, k = j
			greaterXNeighborValue = greaterXSlice[j][k];
			smallerXNeighborValue = smallerXSlice[j][k];
			greaterYNeighborValue = currentXSlice[jPlusOne][k];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			topplePositionType7(j, currentValue, greaterXNeighborValue, smallerXNeighborValue, 3, 
					greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			jMinusOne = j;
			j = jPlusOne;
			// j = i, k = 0
			currentValue = currentXSlice[j][0];
			greaterXNeighborValue = greaterXSlice[j][0];
			smallerYNeighborValue = currentXSlice[jMinusOne][0];
			greaterZNeighborValue = currentXSlice[j][1];
			topplePositionType8(j, currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			// j = i, k = 1
			greaterXNeighborValue = greaterXSlice[j][1];
			smallerYNeighborValue = currentXSlice[jMinusOne][1];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[j][2];
			topplePositionType9(j, 1, currentValue, greaterXNeighborValue, smallerYNeighborValue, 1, 
					greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices);
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
				topplePositionType14(j, k, currentValue, greaterXNeighborValue, smallerYNeighborValue, 
						greaterZNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			}			
			// j = i, k = j - 1
			greaterXNeighborValue = greaterXSlice[j][k];
			smallerYNeighborValue = currentXSlice[jMinusOne][k];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[j][kPlusOne];
			topplePositionType9(j, k, currentValue, greaterXNeighborValue, smallerYNeighborValue, 2, 
					greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			k = kPlusOne;
			// j = i, k = j
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterXNeighborValue = greaterXSlice[j][k];
			topplePositionType10(j, currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, 
					newGreaterXSlice);
			grid[iMinusOne] = null;
		}
		xSlices[1] = currentXSlice;
		xSlices[2] = greaterXSlice;
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
	}

	private void toppleEdge(BigFraction[][][] xSlices, BigFraction[][][] newXSlices, BigFraction[][][] newGrid, int i, 
			BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts) {
		BigFraction[][] smallerXSlice = null, currentXSlice = null;
		BigFraction[][] newSmallerXSlice = null, newCurrentXSlice = null;
		// j = 0, k = 0
		smallerXSlice = xSlices[1];
		currentXSlice = xSlices[2];
		newSmallerXSlice = newXSlices[1];
		newCurrentXSlice = newXSlices[2];
		newXSlices[0] = newSmallerXSlice;
		newXSlices[1] = newCurrentXSlice;
		BigFraction currentValue = currentXSlice[0][0];
		BigFraction smallerXNeighborValue = smallerXSlice[0][0];
		BigFraction greaterYNeighborValue = currentXSlice[1][0];
		topplePositionType5Edge(currentValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// j = 1, k = 0
		smallerXNeighborValue = smallerXSlice[1][0];
		// reuse values obtained previously
		BigFraction smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice[2][0];
		BigFraction greaterZNeighborValue = currentXSlice[1][1];
		topplePositionType6Edge(1, currentValue, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// j = 1, k = 1
		smallerXNeighborValue = smallerXSlice[1][1];
		greaterYNeighborValue = currentXSlice[2][1];
		// reuse values obtained previously
		BigFraction smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		topplePositionType7Edge(1, currentValue, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// j = 2, k = 0
		currentValue = currentXSlice[2][0];
		smallerXNeighborValue = smallerXSlice[2][0];
		// reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice[3][0];
		topplePositionType12Edge(2, currentValue, smallerXNeighborValue, greaterYNeighborValue, 
				smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// j = 2, k = 1
		smallerXNeighborValue = smallerXSlice[2][1];
		greaterYNeighborValue = currentXSlice[3][1];
		smallerYNeighborValue = currentXSlice[1][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[2][2];
		topplePositionType11Edge(2, 1, currentValue, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, newXSlices);
		// j = 2, k = 2
		smallerXNeighborValue = smallerXSlice[2][2];
		greaterYNeighborValue = currentXSlice[3][2];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		topplePositionType13Edge(2, currentValue, smallerXNeighborValue, greaterYNeighborValue, 
				smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		int j = 3, jMinusOne = 2, jPlusOne = 4;
		for (int lastJ = i - 2; j <= lastJ;) {
			// k = 0
			currentValue = currentXSlice[j][0];
			smallerXNeighborValue = smallerXSlice[j][0];
			greaterYNeighborValue = currentXSlice[jPlusOne][0];
			smallerYNeighborValue = currentXSlice[jMinusOne][0];
			greaterZNeighborValue = currentXSlice[j][1];
			topplePositionType12Edge(j, currentValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			// k = 1
			smallerXNeighborValue = smallerXSlice[j][1];
			greaterYNeighborValue = currentXSlice[jPlusOne][1];
			smallerYNeighborValue = currentXSlice[jMinusOne][1];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[j][2];
			topplePositionType11Edge(j, 1, currentValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newXSlices);
			int k = 2, kPlusOne = 3;
			for (int lastK = j - 2; k <= lastK;) {
				smallerXNeighborValue = smallerXSlice[j][k];
				greaterYNeighborValue = currentXSlice[jPlusOne][k];
				smallerYNeighborValue = currentXSlice[jMinusOne][k];
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				greaterZNeighborValue = currentXSlice[j][kPlusOne];
				topplePositionType15Edge(j, k, currentValue, smallerXNeighborValue, 
						greaterYNeighborValue, smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue, 
						relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, newXSlices);
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
			topplePositionType11Edge(j, k, currentValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newXSlices);
			// k = j
			k = kPlusOne;
			smallerXNeighborValue = smallerXSlice[j][k];
			greaterYNeighborValue = currentXSlice[jPlusOne][k];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			topplePositionType13Edge(j, currentValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices);				 
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
		topplePositionType6Edge(j, currentValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// j = i - 1, k = 1
		smallerXNeighborValue = smallerXSlice[j][1];
		greaterYNeighborValue = currentXSlice[jPlusOne][1];
		smallerYNeighborValue = currentXSlice[jMinusOne][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[j][2];
		topplePositionType11Edge(j, 1, currentValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, newXSlices);
		int k = 2, kPlusOne = 3, lastK = j - 2;
		for(; k <= lastK;) {
			smallerXNeighborValue = smallerXSlice[j][k];
			greaterYNeighborValue = currentXSlice[jPlusOne][k];
			smallerYNeighborValue = currentXSlice[jMinusOne][k];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[j][kPlusOne];
			topplePositionType11Edge(j, k, currentValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, 
					relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newXSlices);
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
		topplePositionType11Edge(j, k, currentValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, newXSlices);
		k = kPlusOne;
		kPlusOne++;
		// j = i - 1, k = j
		smallerXNeighborValue = smallerXSlice[j][k];
		greaterYNeighborValue = currentXSlice[jPlusOne][k];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		topplePositionType7Edge(j, currentValue, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		jMinusOne = j;
		j = jPlusOne;
		// j = i, k = 0
		currentValue = currentXSlice[j][0];
		smallerYNeighborValue = currentXSlice[jMinusOne][0];
		greaterZNeighborValue = currentXSlice[j][1];
		topplePositionType8Edge(j, currentValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// j = i, k = 1
		smallerYNeighborValue = currentXSlice[jMinusOne][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[j][2];
		topplePositionType9Edge(j, 1, currentValue, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		k = 2;
		kPlusOne = 3;
		lastK++;
		for(; k <= lastK; k = kPlusOne, kPlusOne++) {
			smallerYNeighborValue = currentXSlice[jMinusOne][k];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[j][kPlusOne];
			topplePositionType14Edge(j, k, currentValue, smallerYNeighborValue, 
					greaterZNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		}			
		// j = i, k = j - 1
		smallerYNeighborValue = currentXSlice[jMinusOne][k];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[j][kPlusOne];
		topplePositionType9Edge(j, k, currentValue, smallerYNeighborValue, 2, 
				greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		k = kPlusOne;
		// j = i, k = j
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		topplePositionType10Edge(j, currentValue, smallerZNeighborValue, newCurrentXSlice);
	}
	
	private static void topplePositionType1(BigFraction currentValue, BigFraction greaterXNeighborValue, BigFraction[][] newCurrentXSlice, 
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

	private static void topplePositionType2(BigFraction currentValue, BigFraction greaterXNeighborValue, BigFraction smallerXNeighborValue, 
			BigFraction greaterYNeighborValue, BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, BigFraction[][][] newXSlices) {
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
		topplePosition(newXSlices, currentValue, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static void topplePositionType3(BigFraction currentValue, BigFraction greaterXNeighborValue, BigFraction smallerYNeighborValue, 
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
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static void topplePositionType4(BigFraction currentValue, BigFraction greaterXNeighborValue, BigFraction smallerZNeighborValue, 
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

	private static void topplePositionType5(BigFraction currentValue, BigFraction greaterXNeighborValue, 
			BigFraction smallerXNeighborValue, BigFraction greaterYNeighborValue, BigFraction[] relevantAsymmetricNeighborValues, 
			int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts,
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
	
	private static void topplePositionType5Edge(BigFraction currentValue, BigFraction smallerXNeighborValue, BigFraction greaterYNeighborValue, 
			BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigFraction[][][] newXSlices) {
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
		topplePosition(newXSlices, currentValue, 0, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static void topplePositionType6(int j, BigFraction currentValue, BigFraction gXValue, BigFraction sXValue, 
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
		topplePosition(newXSlices, currentValue, j, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}
	
	private static void topplePositionType6Edge(int j, BigFraction currentValue, BigFraction sXValue, 
			int sXShareMultiplier, BigFraction gYValue, int gYShareMultiplier, BigFraction sYValue, int sYShareMultiplier, 
			BigFraction gZValue, int gZShareMultiplier, BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigFraction[][][] newXSlices) {
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
		topplePosition(newXSlices, currentValue, j, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static void topplePositionType7(int coord, BigFraction currentValue, BigFraction gXValue, BigFraction sXValue, 
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
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}
	
	private static void topplePositionType7Edge(int coord, BigFraction currentValue, BigFraction sXValue, 
			int sXShareMultiplier, BigFraction gYValue, int gYShareMultiplier, BigFraction sZValue, 
			int sZShareMultiplier, BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigFraction[][][] newXSlices) {
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
		topplePosition(newXSlices, currentValue, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static void topplePositionType8(int j, BigFraction currentValue, BigFraction greaterXNeighborValue, BigFraction smallerYNeighborValue, 
			BigFraction greaterZNeighborValue, BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigFraction[][][] newXSlices ) {
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
		topplePosition(newXSlices, currentValue, j, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}
	
	private static void topplePositionType8Edge(int j, BigFraction currentValue, BigFraction smallerYNeighborValue, 
			BigFraction greaterZNeighborValue, BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, BigFraction[][][] newXSlices ) {
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
		topplePosition(newXSlices, currentValue, j, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static void topplePositionType9(int j, int k, BigFraction currentValue, BigFraction gXValue, BigFraction sYValue, 
			int sYShareMultiplier, BigFraction gZValue, int gZShareMultiplier, BigFraction sZValue, int sZShareMultiplier, 
			BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, BigFraction[][][] newXSlices) {
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
		topplePosition(newXSlices, currentValue, j, k, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}
	
	private static void topplePositionType9Edge(int j, int k, BigFraction currentValue, BigFraction sYValue, 
			int sYShareMultiplier, BigFraction gZValue, int gZShareMultiplier, BigFraction sZValue, int sZShareMultiplier, 
			BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, 
			BigFraction[][][] newXSlices) {
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
		topplePosition(newXSlices, currentValue, j, k, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static void topplePositionType10(int coord, BigFraction currentValue, BigFraction greaterXNeighborValue, 
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
	
	private static void topplePositionType10Edge(int coord, BigFraction currentValue, 
			BigFraction smallerZNeighborValue, BigFraction[][] newCurrentXSlice) {
		if (smallerZNeighborValue.compareTo(currentValue) < 0) {
			int coordMinusOne = coord - 1;
			// sz < current
			BigFraction toShare = currentValue.subtract(smallerZNeighborValue); 
			BigFraction share = toShare.divide(4);
			newCurrentXSlice[coord][coordMinusOne] = newCurrentXSlice[coord][coordMinusOne].add(share);
			newCurrentXSlice[coord][coord] = newCurrentXSlice[coord][coord].add(currentValue).subtract(toShare).add(share);
		} else {
			// sz >= current
			newCurrentXSlice[coord][coord] = newCurrentXSlice[coord][coord].add(currentValue);
		}
	}

	private static void topplePositionType11(int j, int k, BigFraction currentValue, BigFraction gXValue, BigFraction sXValue, 
			int sXShareMultiplier, BigFraction gYValue, int gYShareMultiplier, BigFraction sYValue, int sYShareMultiplier, 
			BigFraction gZValue, int gZShareMultiplier, BigFraction sZValue, int sZShareMultiplier, BigFraction[] relevantNeighborValues, 
			int[] sortedNeighborsIndexes, int[][] relevantNeighborCoords, int[] relevantNeighborShareMultipliers, BigFraction[][][] newXSlices) {
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
		topplePosition(newXSlices, currentValue, j, k, relevantNeighborValues, sortedNeighborsIndexes, relevantNeighborCoords, 
				relevantNeighborShareMultipliers, relevantNeighborCount);
	}
	
	private static void topplePositionType11Edge(int j, int k, BigFraction currentValue, BigFraction sXValue, 
			int sXShareMultiplier, BigFraction gYValue, int gYShareMultiplier, BigFraction sYValue, int sYShareMultiplier, 
			BigFraction gZValue, int gZShareMultiplier, BigFraction sZValue, int sZShareMultiplier, BigFraction[] relevantNeighborValues, 
			int[] sortedNeighborsIndexes, int[][] relevantNeighborCoords, int[] relevantNeighborShareMultipliers, 
			BigFraction[][][] newXSlices) {
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
		topplePosition(newXSlices, currentValue, j, k, relevantNeighborValues, sortedNeighborsIndexes, relevantNeighborCoords, 
				relevantNeighborShareMultipliers, relevantNeighborCount);
	}

	private static void topplePositionType12(int j, BigFraction currentValue, BigFraction greaterXNeighborValue, 
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
		topplePosition(newXSlices, currentValue, j, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, 
				relevantAsymmetricNeighborCount);
	}
	
	private static void topplePositionType12Edge(int j, BigFraction currentValue, 
			BigFraction smallerXNeighborValue, BigFraction greaterYNeighborValue, BigFraction smallerYNeighborValue, 
			BigFraction greaterZNeighborValue, BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts, 
			BigFraction[][][] newXSlices) {
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
		topplePosition(newXSlices, currentValue, j, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, 
				relevantAsymmetricNeighborCount);
	}

	private static void topplePositionType13(int coord, BigFraction currentValue, BigFraction greaterXNeighborValue, 
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
	
	private static void topplePositionType13Edge(int coord, BigFraction currentValue, 
			BigFraction smallerXNeighborValue, BigFraction greaterYNeighborValue, BigFraction smallerZNeighborValue, 
			BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigFraction[][][] newXSlices) {
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
		topplePosition(newXSlices, currentValue, coord, coord, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static void topplePositionType14(int j, int k, BigFraction currentValue, BigFraction greaterXNeighborValue, 
			BigFraction smallerYNeighborValue,	BigFraction greaterZNeighborValue, BigFraction smallerZNeighborValue, 
			BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigFraction[][][] newXSlices) {
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
		topplePosition(newXSlices, currentValue, j, k, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}
	
	private static void topplePositionType14Edge(int j, int k, BigFraction currentValue, 
			BigFraction smallerYNeighborValue,	BigFraction greaterZNeighborValue, BigFraction smallerZNeighborValue, 
			BigFraction[] relevantAsymmetricNeighborValues, int[] sortedNeighborsIndexes, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, BigFraction[][][] newXSlices) {
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
		topplePosition(newXSlices, currentValue, j, k, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static void topplePositionType15(int j, int k, BigFraction currentValue, BigFraction greaterXNeighborValue, 
			BigFraction smallerXNeighborValue,	BigFraction greaterYNeighborValue, BigFraction smallerYNeighborValue, 
			BigFraction greaterZNeighborValue, BigFraction smallerZNeighborValue, BigFraction[] relevantNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantNeighborCoords, BigFraction[][][] newXSlices) {
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
		topplePosition(newXSlices, currentValue, j, k, relevantNeighborValues, sortedNeighborsIndexes, relevantNeighborCoords, 
				relevantNeighborCount);
	}
	
	private static void topplePositionType15Edge(int j, int k, BigFraction currentValue, 
			BigFraction smallerXNeighborValue,	BigFraction greaterYNeighborValue, BigFraction smallerYNeighborValue, 
			BigFraction greaterZNeighborValue, BigFraction smallerZNeighborValue, BigFraction[] relevantNeighborValues, int[] sortedNeighborsIndexes, 
			int[][] relevantNeighborCoords, BigFraction[][][] newXSlices) {
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
		topplePosition(newXSlices, currentValue, j, k, relevantNeighborValues, sortedNeighborsIndexes, relevantNeighborCoords, 
				relevantNeighborCount);
	}
	
	private static void topplePosition(BigFraction[][][] newXSlices, BigFraction value, int j, int k, BigFraction[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int neighborCount) {
		switch (neighborCount) {
			case 3:
				Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, j, k, neighborValues, sortedNeighborsIndexes, 
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(value).subtract(toShare).add(share);
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(currentRemainingValue).subtract(toShare).add(share);
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(currentRemainingValue).subtract(toShare).add(share);
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
				newXSlices[1][j][k] = newXSlices[1][j][k].add(value);
				break;
			default: // 6, 5, 4
				Utils.sortDescending(neighborCount, neighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, j, k, neighborValues, sortedNeighborsIndexes, neighborCoords, 
						neighborCount);
		}
	}
	
	private static void topplePositionSortedNeighbors(BigFraction[][][] newXSlices, BigFraction value, int j, int k, 
			BigFraction[] neighborValues, int[] sortedNeighborsIndexes, int[][] neighborCoords, int neighborCount) {
		int shareCount = neighborCount + 1;
		BigFraction neighborValue = neighborValues[0];
		BigFraction toShare = value.subtract(neighborValue);
		BigFraction share = toShare.divide(shareCount);
		value = value.subtract(toShare).add(share);
		for (int neighborIndex = 0; neighborIndex < neighborCount; neighborIndex++) {
			int[] nc = neighborCoords[sortedNeighborsIndexes[neighborIndex]];
			newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
		}
		BigFraction previousNeighborValue = neighborValue;
		shareCount--;
		for (int neighborIndex = 1; neighborIndex < neighborCount; neighborIndex++) {
			neighborValue = neighborValues[neighborIndex];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				share = toShare.divide(shareCount);
				value = value.subtract(toShare).add(share);
				for (int remainingNeighborIndex = neighborIndex; remainingNeighborIndex < neighborCount; remainingNeighborIndex++) {
					int[] nc = neighborCoords[sortedNeighborsIndexes[remainingNeighborIndex]];
					newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newXSlices[1][j][k] = newXSlices[1][j][k].add(value);
	}
	
	private static void topplePosition(BigFraction[][][] newXSlices, BigFraction value, int j, int k, BigFraction[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		switch (asymmetricNeighborCount) {
			case 3:
				Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, j, k, asymmetricNeighborValues, sortedNeighborsIndexes, 
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(value).subtract(toShare).add(share);
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(currentRemainingValue).subtract(toShare).add(share);
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(currentRemainingValue).subtract(toShare).add(share);
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
				newXSlices[1][j][k] = newXSlices[1][j][k].add(value);
				break;
			default: // 6, 5, 4
				Utils.sortDescending(asymmetricNeighborCount, asymmetricNeighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, j, k, asymmetricNeighborValues, sortedNeighborsIndexes, asymmetricNeighborCoords, 
						asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
	}
	
	private static void topplePositionSortedNeighbors(BigFraction[][][] newXSlices, BigFraction value, int j, int k, BigFraction[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		int shareCount = neighborCount + 1;
		BigFraction neighborValue = asymmetricNeighborValues[0];
		BigFraction toShare = value.subtract(neighborValue);
		BigFraction share = toShare.divide(shareCount);
		value = value.subtract(toShare).add(share);
		for (int neighborIndex = 0; neighborIndex < asymmetricNeighborCount; neighborIndex++) {
			int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[neighborIndex]];
			newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[neighborIndex]]));
		}
		BigFraction previousNeighborValue = neighborValue;
		shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[0]];
		for (int neighborIndex = 1; neighborIndex < asymmetricNeighborCount; neighborIndex++) {
			neighborValue = asymmetricNeighborValues[neighborIndex];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				share = toShare.divide(shareCount);
				value = value.subtract(toShare).add(share);
				for (int remainingNeighborIndex = neighborIndex; remainingNeighborIndex < asymmetricNeighborCount; remainingNeighborIndex++) {
					int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[remainingNeighborIndex]];
					newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[remainingNeighborIndex]]));
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[neighborIndex]];
		}
		newXSlices[1][j][k] = newXSlices[1][j][k].add(value);
	}
	
	private static void topplePosition(BigFraction[][][] newXSlices, BigFraction value, int j, int k, BigFraction[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		switch (neighborCount) {
			case 3:
				Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, j, k, neighborValues, sortedNeighborsIndexes, 
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(value).subtract(toShare).add(share);
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(currentRemainingValue).subtract(toShare).add(share);
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(currentRemainingValue).subtract(toShare).add(share);
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
				newXSlices[1][j][k] = newXSlices[1][j][k].add(value);
				break;
			default: // 6, 5, 4
				Utils.sortDescending(neighborCount, neighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, j, k, neighborValues, sortedNeighborsIndexes, neighborCoords, 
						neighborShareMultipliers, neighborCount);
		}
	}
	
	private static void topplePositionSortedNeighbors(BigFraction[][][] newXSlices, BigFraction value, int j, int k, BigFraction[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		int shareCount = neighborCount + 1;
		BigFraction neighborValue = neighborValues[0];
		BigFraction toShare = value.subtract(neighborValue);
		BigFraction share = toShare.divide(shareCount);
		value = value.subtract(toShare).add(share);
		for (int neighborIndex = 0; neighborIndex < neighborCount; neighborIndex++) {
			int[] nc = neighborCoords[sortedNeighborsIndexes[neighborIndex]];
			newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(neighborShareMultipliers[sortedNeighborsIndexes[neighborIndex]]));
		}
		BigFraction previousNeighborValue = neighborValue;
		shareCount--;
		for (int neighborIndex = 1; neighborIndex < neighborCount; neighborIndex++) {
			neighborValue = neighborValues[neighborIndex];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				share = toShare.divide(shareCount);
				value = value.subtract(toShare).add(share);
				for (int remainingNeighborIndex = neighborIndex; remainingNeighborIndex < neighborCount; remainingNeighborIndex++) {
					int[] nc = neighborCoords[sortedNeighborsIndexes[remainingNeighborIndex]];
					newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(neighborShareMultipliers[sortedNeighborsIndexes[remainingNeighborIndex]]));
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newXSlices[1][j][k] = newXSlices[1][j][k].add(value);
	}
	
	private static void topplePosition(BigFraction[][][] newXSlices, BigFraction value, int j, int k, BigFraction[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		switch (asymmetricNeighborCount) {
			case 3:
				Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, j, k, asymmetricNeighborValues, sortedNeighborsIndexes, 
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(value).subtract(toShare).add(share);
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(currentRemainingValue).subtract(toShare).add(share);
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
					newXSlices[1][j][k] = newXSlices[1][j][k].add(currentRemainingValue).subtract(toShare).add(share);
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
				newXSlices[1][j][k] = newXSlices[1][j][k].add(value);
				break;
			default: // 6, 5, 4
				Utils.sortDescending(asymmetricNeighborCount, asymmetricNeighborValues, sortedNeighborsIndexes);
				topplePositionSortedNeighbors(newXSlices, value, j, k, asymmetricNeighborValues, sortedNeighborsIndexes, asymmetricNeighborCoords, 
						asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
	}
	
	private static void topplePositionSortedNeighbors(BigFraction[][][] newXSlices, BigFraction value, int j, int k, BigFraction[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		int shareCount = neighborCount + 1;
		BigFraction neighborValue = asymmetricNeighborValues[0];
		BigFraction toShare = value.subtract(neighborValue);
		BigFraction share = toShare.divide(shareCount);
		value = value.subtract(toShare).add(share);
		for (int neighborIndex = 0; neighborIndex < asymmetricNeighborCount; neighborIndex++) {
			int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[neighborIndex]];
			newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
		}
		BigFraction previousNeighborValue = neighborValue;
		shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[0]];
		for (int neighborIndex = 1; neighborIndex < asymmetricNeighborCount; neighborIndex++) {
			neighborValue = asymmetricNeighborValues[neighborIndex];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				share = toShare.divide(shareCount);
				value = value.subtract(toShare).add(share);
				for (int remainingNeighborIndex = neighborIndex; remainingNeighborIndex < asymmetricNeighborCount; remainingNeighborIndex++) {
					int[] nc = asymmetricNeighborCoords[sortedNeighborsIndexes[remainingNeighborIndex]];
					newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[neighborIndex]];
		}
		newXSlices[1][j][k] = newXSlices[1][j][k].add(value);
	}
	
	@Override
	public BigFraction getFromPosition(int x, int y, int z) {	
		int i = x - singleSourceCoord;
		int j = y - singleSourceCoord;
		int k = z - singleSourceCoord;
		if (i < 0) i = -i;
		if (j < 0) j = -j;
		if (k < 0) k = -k;
		if (i >= j) {
			if (j >= k) {
				//i >= j >= k
				if (i < grid.length) {
					return grid[i][j][k];
				}
			} else if (i >= k) { 
				//i >= k > j
				if (i < grid.length) {
					return grid[i][k][j];
				}
			} else {
				//k > i >= j
				if (k < grid.length) {
					return grid[k][i][j];
				}
			}
		} else if (j >= k) {
			if (j < grid.length) {
				if (i >= k) {
					//j > i >= k
					return grid[j][i][k];
				} else {
					//j >= k > i
					return grid[j][k][i];
				}
			}
		} else {
			// k > j > i
			if (k < grid.length) {
				return grid[k][j][i];
			}
		}
		throw new IllegalArgumentException("Coordinates out of bounds.");
	}
	
	@Override
	public BigFraction getFromAsymmetricPosition(int x, int y, int z) {	
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
		String path = getName() + "/3D/bounded_grid/" + side + "x" + side + "x" + side 
				+ "/(" + singleSourceCoord + "," + singleSourceCoord + "," + singleSourceCoord + ")=";
		if (!isPositive) path += "-";
		path += "infinity";
		return path;
	}
	
	public int getSide() {
		return side;
	}
	
}