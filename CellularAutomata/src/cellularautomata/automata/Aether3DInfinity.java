/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2021 Jaume Ribas

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    aBigFraction with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package cellularautomata.automata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.math3.fraction.BigFraction;

import cellularautomata.evolvinggrid.SymmetricEvolvingNumberGrid3D;

/**
 * An implementation that produces a pattern equivalent to the one produced by Aether when the initial value tends to infinity. 
 * 
 * @author Jaume
 *
 */
public class Aether3DInfinity implements SymmetricEvolvingNumberGrid3D<BigFraction>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2530829980428894317L;

	/** A 3D array representing the grid */
	private BigFraction[][][] grid;
	
	private int step;
	private final boolean isPositive;
	private boolean isEvenStep = true;
	
	private int maxX;
	
	public Aether3DInfinity(boolean isPositive) {
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
	public Aether3DInfinity(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		Aether3DInfinity data = (Aether3DInfinity) Utils.deserializeFromFile(backupPath);
		isPositive = data.isPositive;
		grid = data.grid;
		maxX = data.maxX;
		step = data.step;
	}
	
	@Override
	public boolean nextStep(){
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
		topplePositionType1(currentValue, greaterXNeighborValue, newCurrentXSlice, newGreaterXSlice);
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
		int[][] relevantAsymmetricNeighborCoords = new int[6][3];
		int[] relevantAsymmetricNeighborShareMultipliers = new int[6];// to compensate for omitted symmetric positions
		int[] relevantAsymmetricNeighborSymmetryCounts = new int[6];// to compensate for omitted symmetric positions
		// reuse values obtained previously
		BigFraction smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterXNeighborValue = greaterXSlice[0][0];
		BigFraction greaterYNeighborValue = currentXSlice[1][0];
		topplePositionType2(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 1, y = 1, z = 0
		// reuse values obtained previously
		BigFraction smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterXNeighborValue = greaterXSlice[1][0];
		BigFraction greaterZNeighborValue = currentXSlice[1][1];
		topplePositionType3(currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 1, y = 1, z = 1
		// reuse values obtained previously
		BigFraction smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterXNeighborValue = greaterXSlice[1][1];
		topplePositionType4(currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, newGreaterXSlice);
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
		topplePositionType5(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 2, y = 1, z = 0
		greaterXNeighborValue = greaterXSlice[1][0];
		smallerXNeighborValue = smallerXSlice[1][0];
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice[2][0];
		greaterZNeighborValue = currentXSlice[1][1];
		topplePositionType6(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 2, y = 1, z = 1
		greaterXNeighborValue = greaterXSlice[1][1];
		smallerXNeighborValue = smallerXSlice[1][1];
		greaterYNeighborValue = currentXSlice[2][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		topplePositionType7(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 2, y = 2, z = 0
		currentValue = currentXSlice[2][0];
		greaterXNeighborValue = greaterXSlice[2][0];
		// reuse values obtained previously
		smallerYNeighborValue = smallerZNeighborValue;
		greaterZNeighborValue = greaterYNeighborValue;
		topplePositionType8(2, currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, 
				newXSlices);
		// x = 2, y = 2, z = 1
		greaterXNeighborValue = greaterXSlice[2][1];
		smallerYNeighborValue = currentXSlice[1][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[2][2];
		topplePositionType9(2, 1, currentValue, greaterXNeighborValue, smallerYNeighborValue, 2, 
				greaterZNeighborValue, 3, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 2, y = 2, z = 2
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterXNeighborValue = greaterXSlice[2][2];
		topplePositionType10(2, currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, 
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
		topplePositionType5(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 3, y = 1, z = 0
		greaterXNeighborValue = greaterXSlice[1][0];
		smallerXNeighborValue = smallerXSlice[1][0];
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice[2][0];
		greaterZNeighborValue = currentXSlice[1][1];
		topplePositionType6(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 3, y = 1, z = 1
		greaterXNeighborValue = greaterXSlice[1][1];
		smallerXNeighborValue = smallerXSlice[1][1];
		greaterYNeighborValue = currentXSlice[2][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		topplePositionType7(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
				greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, 
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
		topplePositionType6(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
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
		topplePositionType11(2, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
				greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, newXSlices);
		// x = 3, y = 2, z = 2
		greaterXNeighborValue = greaterXSlice[2][2];
		smallerXNeighborValue = smallerXSlice[2][2];
		greaterYNeighborValue = currentXSlice[3][2];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		topplePositionType7(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, 3, 
				greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 3, y = 3, z = 0
		currentValue = currentXSlice[3][0];
		greaterXNeighborValue = greaterXSlice[3][0];
		smallerYNeighborValue = currentXSlice[2][0];
		greaterZNeighborValue = currentXSlice[3][1];
		topplePositionType8(3, currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 3, y = 3, z = 1
		greaterXNeighborValue = greaterXSlice[3][1];
		smallerYNeighborValue = currentXSlice[2][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[3][2];
		topplePositionType9(3, 1, currentValue, greaterXNeighborValue, smallerYNeighborValue, 1, 
				greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 3, y = 3, z = 2
		greaterXNeighborValue = greaterXSlice[3][2];
		smallerYNeighborValue = currentXSlice[2][2];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[3][3];
		topplePositionType9(3, 2, currentValue, greaterXNeighborValue, smallerYNeighborValue, 2, 
				greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices);
		// x = 3, y = 3, z = 3
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterXNeighborValue = greaterXSlice[3][3];
		topplePositionType10(3, currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, 
				newGreaterXSlice);
		grid[2] = null;
		// 4 >= x < edge - 2
		int edge = grid.length - 1;
		int edgeMinusTwo = edge - 2;
		BigFraction[][][] xSlices = new BigFraction[][][] {null, currentXSlice, greaterXSlice};
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		toppleRangeBeyondX3(xSlices, newXSlices, newGrid, 4, edgeMinusTwo, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts); // is it faster to reuse this arrays?
		//edge - 2 >= x < edge
		toppleRangeBeyondX3(xSlices, newXSlices, newGrid, edgeMinusTwo, edge, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
				relevantAsymmetricNeighborSymmetryCounts);
		if (newGrid.length > grid.length) {
			newGrid[grid.length] = Utils.buildAnisotropic2DBigFractionArray(newGrid.length);
		}
		grid = newGrid;
		maxX++;
		step++;
		isEvenStep = !isEvenStep;
		return true;
	}
	
	private boolean toppleRangeBeyondX3(BigFraction[][][] xSlices, BigFraction[][][] newXSlices, BigFraction[][][] newGrid, int minX, int maxX, 
			BigFraction[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
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
			topplePositionType5(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			// y = 1, z = 0
			greaterXNeighborValue = greaterXSlice[1][0];
			smallerXNeighborValue = smallerXSlice[1][0];
			// reuse values obtained previously
			BigFraction smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterYNeighborValue = currentXSlice[2][0];
			BigFraction greaterZNeighborValue = currentXSlice[1][1];
			topplePositionType6(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 4, greaterZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			// y = 1, z = 1
			greaterXNeighborValue = greaterXSlice[1][1];
			smallerXNeighborValue = smallerXSlice[1][1];
			greaterYNeighborValue = currentXSlice[2][1];
			// reuse values obtained previously
			BigFraction smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			topplePositionType7(1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, 
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
			topplePositionType12(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, 
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
			topplePositionType11(2, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
					greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, newXSlices);
			// y = 2, z = 2
			greaterXNeighborValue = greaterXSlice[2][2];
			smallerXNeighborValue = smallerXSlice[2][2];
			greaterYNeighborValue = currentXSlice[3][2];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			topplePositionType13(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
					smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
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
				topplePositionType12(y, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
						smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, 
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
				topplePositionType11(y, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, 
						relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
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
					topplePositionType15(y, z, currentValue, greaterXNeighborValue, smallerXNeighborValue, 
							greaterYNeighborValue, smallerYNeighborValue, greaterZNeighborValue, smallerZNeighborValue, 
							relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, newXSlices);
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
				topplePositionType11(y, z, currentValue, greaterXNeighborValue, smallerXNeighborValue, 1, 
						greaterYNeighborValue, 1, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, 
						relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
						relevantAsymmetricNeighborShareMultipliers, newXSlices);
				// z = y
				z = zPlusOne;
				greaterXNeighborValue = greaterXSlice[y][z];
				smallerXNeighborValue = smallerXSlice[y][z];
				greaterYNeighborValue = currentXSlice[yPlusOne][z];
				// reuse values obtained previously
				smallerZNeighborValue = currentValue;
				currentValue = greaterZNeighborValue;
				topplePositionType13(y, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
						smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
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
			topplePositionType6(y, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, 
					relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
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
			topplePositionType11(y, 1, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 2, 
					relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
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
				topplePositionType11(y, z, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
						greaterYNeighborValue, 2, smallerYNeighborValue, 1, greaterZNeighborValue, 1, smallerZNeighborValue, 1, 
						relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
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
			topplePositionType11(y, z, currentValue, greaterXNeighborValue, smallerXNeighborValue, 2, 
					greaterYNeighborValue, 2, smallerYNeighborValue, 2, greaterZNeighborValue, 2, smallerZNeighborValue, 1, 
					relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
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
			topplePositionType7(y, currentValue, greaterXNeighborValue, smallerXNeighborValue, 3, 
					greaterYNeighborValue, 2, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			yMinusOne = y;
			y = yPlusOne;
			// y = x, z = 0
			currentValue = currentXSlice[y][0];
			greaterXNeighborValue = greaterXSlice[y][0];
			smallerYNeighborValue = currentXSlice[yMinusOne][0];
			greaterZNeighborValue = currentXSlice[y][1];
			topplePositionType8(y, currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
					relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			// y = x, z = 1
			greaterXNeighborValue = greaterXSlice[y][1];
			smallerYNeighborValue = currentXSlice[yMinusOne][1];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[y][2];
			topplePositionType9(y, 1, currentValue, greaterXNeighborValue, smallerYNeighborValue, 1, 
					greaterZNeighborValue, 1, smallerZNeighborValue, 2, relevantAsymmetricNeighborValues, 
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
				topplePositionType14(y, z, currentValue, greaterXNeighborValue, smallerYNeighborValue, 
						greaterZNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, 
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			}			
			// y = x, z = y - 1
			greaterXNeighborValue = greaterXSlice[y][z];
			smallerYNeighborValue = currentXSlice[yMinusOne][z];
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterZNeighborValue = currentXSlice[y][zPlusOne];
			topplePositionType9(y, z, currentValue, greaterXNeighborValue, smallerYNeighborValue, 2, 
					greaterZNeighborValue, 3, smallerZNeighborValue, 1, relevantAsymmetricNeighborValues, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, 
					relevantAsymmetricNeighborSymmetryCounts, newXSlices);
			z = zPlusOne;
			// y = x, z = y
			// reuse values obtained previously
			smallerZNeighborValue = currentValue;
			currentValue = greaterZNeighborValue;
			greaterXNeighborValue = greaterXSlice[y][z];
			topplePositionType10(y, currentValue, greaterXNeighborValue, smallerZNeighborValue, newCurrentXSlice, 
					newGreaterXSlice);
			grid[xMinusOne] = null;
		}
		xSlices[1] = currentXSlice;
		xSlices[2] = greaterXSlice;
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		return anyToppled;
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
			BigFraction greaterYNeighborValue, BigFraction[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, 
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
		topplePosition(newXSlices, currentValue, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, 
				relevantAsymmetricNeighborCount);
	}

	private static void topplePositionType3(BigFraction currentValue, BigFraction greaterXNeighborValue, BigFraction smallerYNeighborValue, 
			BigFraction greaterZNeighborValue, BigFraction[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, 
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
		topplePosition(newXSlices, currentValue, 1, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, 
				relevantAsymmetricNeighborCount);
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
		} else {
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				// gx < current <= sz
				BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
				BigFraction share = toShare.divide(4);
				newCurrentXSlice[1][1] = newCurrentXSlice[1][1].add(currentValue).subtract(toShare).add(share);
				newGreaterXSlice[1][1] = newGreaterXSlice[1][1].add(share);
			} else {
				newCurrentXSlice[1][1] = newCurrentXSlice[1][1].add(currentValue);
			}
		}
	}

	private static void topplePositionType5(BigFraction currentValue, BigFraction greaterXNeighborValue, 
			BigFraction smallerXNeighborValue, BigFraction greaterYNeighborValue, BigFraction[] relevantAsymmetricNeighborValues, 
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
		topplePosition(newXSlices, currentValue, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	};

	private static void topplePositionType6(int y, BigFraction currentValue, BigFraction gXValue, BigFraction sXValue, 
			int sXShareMultiplier, BigFraction gYValue, int gYShareMultiplier, BigFraction sYValue, int sYShareMultiplier, 
			BigFraction gZValue, int gZShareMultiplier, BigFraction[] relevantAsymmetricNeighborValues, 
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
		topplePosition(newXSlices, currentValue, y, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, 
				relevantAsymmetricNeighborCount);
	}

	private static void topplePositionType7(int coord, BigFraction currentValue, BigFraction gXValue, BigFraction sXValue, 
			int sXShareMultiplier, BigFraction gYValue, int gYShareMultiplier, BigFraction sZValue, 
			int sZShareMultiplier, BigFraction[] relevantAsymmetricNeighborValues, 
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
		topplePosition(newXSlices, currentValue, coord, coord, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, 
				relevantAsymmetricNeighborCount);
	}

	private static void topplePositionType8(int y, BigFraction currentValue, BigFraction greaterXNeighborValue, BigFraction smallerYNeighborValue, 
			BigFraction greaterZNeighborValue, BigFraction[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, 
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
		topplePosition(newXSlices, currentValue, y, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static void topplePositionType9(int y, int z, BigFraction currentValue, BigFraction gXValue, BigFraction sYValue, 
			int sYShareMultiplier, BigFraction gZValue, int gZShareMultiplier, BigFraction sZValue, int sZShareMultiplier, 
			BigFraction[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, 
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
		topplePosition(newXSlices, currentValue, y, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, 
				relevantAsymmetricNeighborCount);
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
		} else {
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				// gx < current <= sz
				BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
				BigFraction share = toShare.divide(4);
				newCurrentXSlice[coord][coord] = newCurrentXSlice[coord][coord].add(currentValue).subtract(toShare).add(share);
				newGreaterXSlice[coord][coord] = newGreaterXSlice[coord][coord].add(share);
			} else {
				newCurrentXSlice[coord][coord] = newCurrentXSlice[coord][coord].add(currentValue);
			}
		}
	}

	private static void topplePositionType11(int y, int z, BigFraction currentValue, BigFraction gXValue, BigFraction sXValue, 
			int sXShareMultiplier, BigFraction gYValue, int gYShareMultiplier, BigFraction sYValue, int sYShareMultiplier, 
			BigFraction gZValue, int gZShareMultiplier, BigFraction sZValue, int sZShareMultiplier, BigFraction[] relevantNeighborValues, int[][] relevantNeighborCoords, 
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
		topplePosition(newXSlices, currentValue, y, z, relevantNeighborValues, relevantNeighborCoords, 
				relevantNeighborShareMultipliers, relevantNeighborCount);
	}

	private static void topplePositionType12(int y, BigFraction currentValue, BigFraction greaterXNeighborValue, 
			BigFraction smallerXNeighborValue, BigFraction greaterYNeighborValue, BigFraction smallerYNeighborValue, 
			BigFraction greaterZNeighborValue, BigFraction[] relevantAsymmetricNeighborValues, 
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
		topplePosition(newXSlices, currentValue, y, 0, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, 
				relevantAsymmetricNeighborCount);
	}

	private static void topplePositionType13(int coord, BigFraction currentValue, BigFraction greaterXNeighborValue, 
			BigFraction smallerXNeighborValue, BigFraction greaterYNeighborValue, BigFraction smallerZNeighborValue, 
			BigFraction[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, 
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
		topplePosition(newXSlices, currentValue, coord, coord, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static void topplePositionType14(int y, int z, BigFraction currentValue, BigFraction greaterXNeighborValue, 
			BigFraction smallerYNeighborValue,	BigFraction greaterZNeighborValue, BigFraction smallerZNeighborValue, 
			BigFraction[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, 
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
		topplePosition(newXSlices, currentValue, y, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount);
	}

	private static void topplePositionType15(int y, int z, BigFraction currentValue, BigFraction greaterXNeighborValue, 
			BigFraction smallerXNeighborValue,	BigFraction greaterYNeighborValue, BigFraction smallerYNeighborValue, 
			BigFraction greaterZNeighborValue, BigFraction smallerZNeighborValue, BigFraction[] relevantNeighborValues, 
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
		topplePosition(newXSlices, currentValue, y, z, relevantNeighborValues, relevantNeighborCoords, 
				relevantNeighborCount);
	}
	
	private static void topplePosition(BigFraction[][][] newXSlices, BigFraction value, int y, int z, BigFraction[] neighborValues,
			int[][] neighborCoords, int neighborCount) {
		switch (neighborCount) {
			case 3:
				Utils.sort3NeighborsByValueDesc(neighborValues, neighborCoords);
				topplePositionSortedNeighbors(newXSlices, value, y, z, neighborValues, 
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
					BigFraction currentRemainingValue = value.subtract(share.multiply(new BigFraction(neighborCount)));
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
					BigFraction currentRemainingValue = value.subtract(share.multiply(new BigFraction(neighborCount)));
					toShare = currentRemainingValue.subtract(n1Val);
					share = toShare.divide(2);
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share);
					newXSlices[1][y][z] = newXSlices[1][y][z].add(currentRemainingValue).subtract(toShare).add(share);
				}				
				break;
			case 1:
				BigFraction toShare = value.subtract(neighborValues[0]);
				BigFraction share = toShare.divide(2);
				if (!share.equals(BigFraction.ZERO)) {
					value = value.subtract(toShare).add(share);
					int[] nc = neighborCoords[0];
					newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
				}
				// no break
			case 0:
				newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
				break;
			default: // 6, 5, 4
				Utils.sortNeighborsByValueDesc(neighborCount, neighborValues, neighborCoords);
				topplePositionSortedNeighbors(newXSlices, value, y, z, neighborValues, neighborCoords, 
						neighborCount);
		}
	}
	
	private static void topplePositionSortedNeighbors(BigFraction[][][] newXSlices, BigFraction value, int y, int z, 
			BigFraction[] neighborValues, int[][] neighborCoords, int neighborCount) {
		int shareCount = neighborCount + 1;
		BigFraction neighborValue = neighborValues[0];
		BigFraction toShare = value.subtract(neighborValue);
		BigFraction share = toShare.divide(new BigFraction(shareCount));
		if (!share.equals(BigFraction.ZERO)) {
			value = value.subtract(toShare).add(share);
			for (int j = 0; j < neighborCount; j++) {
				int[] nc = neighborCoords[j];
				newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
			}
		}
		BigFraction previousNeighborValue = neighborValue;
		shareCount--;
		for (int i = 1; i < neighborCount; i++) {
			neighborValue = neighborValues[i];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				share = toShare.divide(new BigFraction(shareCount));
				if (!share.equals(BigFraction.ZERO)) {
					value = value.subtract(toShare).add(share);
					for (int j = i; j < neighborCount; j++) {
						int[] nc = neighborCoords[j];
						newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
	}
	
	private static void topplePosition(BigFraction[][][] newXSlices, BigFraction value, int y, int z, BigFraction[] asymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		switch (asymmetricNeighborCount) {
			case 3:
				Utils.sort3NeighborsByValueDesc(asymmetricNeighborValues, asymmetricNeighborCoords, asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts);
				topplePositionSortedNeighbors(newXSlices, value, y, z, asymmetricNeighborValues, 
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
					BigFraction share = toShare.divide(new BigFraction(shareCount));
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(new BigFraction(n0Mult)));
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(new BigFraction(n1Mult)));
					newXSlices[1][y][z] = newXSlices[1][y][z].add(value).subtract(toShare).add(share);
				} else if (n0Val.compareTo(n1Val) < 0) {
					// n0Val < n1Val < value
					BigFraction toShare = value.subtract(n1Val); 
					BigFraction share = toShare.divide(new BigFraction(shareCount));
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(new BigFraction(n0Mult)));
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(new BigFraction(n1Mult)));
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					BigFraction currentRemainingValue = value.subtract(share.multiply(new BigFraction(neighborCount)));
					toShare = currentRemainingValue.subtract(n0Val);
					share = toShare.divide(new BigFraction(shareCount));
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(new BigFraction(n0Mult)));
					newXSlices[1][y][z] = newXSlices[1][y][z].add(currentRemainingValue).subtract(toShare).add(share);
				} else {
					// n1Val < n0Val < value
					BigFraction toShare = value.subtract(n0Val); 
					BigFraction share = toShare.divide(new BigFraction(shareCount));
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(new BigFraction(n0Mult)));
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(new BigFraction(n1Mult)));
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					BigFraction currentRemainingValue = value.subtract(share.multiply(new BigFraction(neighborCount)));
					toShare = currentRemainingValue.subtract(n1Val);
					share = toShare.divide(new BigFraction(shareCount));
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(new BigFraction(n1Mult)));
					newXSlices[1][y][z] = newXSlices[1][y][z].add(currentRemainingValue).subtract(toShare).add(share);
				}				
				break;
			case 1:
				shareCount = neighborCount + 1;
				BigFraction toShare = value.subtract(asymmetricNeighborValues[0]);
				BigFraction share = toShare.divide(new BigFraction(shareCount));
				if (!share.equals(BigFraction.ZERO)) {
					value = value.subtract(toShare).add(share);
					int[] nc = asymmetricNeighborCoords[0];
					newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(new BigFraction(asymmetricNeighborShareMultipliers[0])));
				}
				// no break
			case 0:
				newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
				break;
			default: // 6, 5, 4
				Utils.sortNeighborsByValueDesc(asymmetricNeighborCount, asymmetricNeighborValues, asymmetricNeighborCoords, 
						asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts);
				topplePositionSortedNeighbors(newXSlices, value, y, z, asymmetricNeighborValues, asymmetricNeighborCoords, 
						asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
	}
	
	private static void topplePositionSortedNeighbors(BigFraction[][][] newXSlices, BigFraction value, int y, int z, BigFraction[] asymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		int shareCount = neighborCount + 1;
		BigFraction neighborValue = asymmetricNeighborValues[0];
		BigFraction toShare = value.subtract(neighborValue);
		BigFraction share = toShare.divide(new BigFraction(shareCount));
		if (!share.equals(BigFraction.ZERO)) {
			value = value.subtract(toShare).add(share);
			for (int j = 0; j < asymmetricNeighborCount; j++) {
				int[] nc = asymmetricNeighborCoords[j];
				newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(new BigFraction(asymmetricNeighborShareMultipliers[j])));
			}
		}
		BigFraction previousNeighborValue = neighborValue;
		shareCount -= asymmetricNeighborSymmetryCounts[0];
		for (int i = 1; i < asymmetricNeighborCount; i++) {
			neighborValue = asymmetricNeighborValues[i];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				share = toShare.divide(new BigFraction(shareCount));
				if (!share.equals(BigFraction.ZERO)) {
					value = value.subtract(toShare).add(share);
					for (int j = i; j < asymmetricNeighborCount; j++) {
						int[] nc = asymmetricNeighborCoords[j];
						newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(new BigFraction(asymmetricNeighborShareMultipliers[j])));
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[i];
		}
		newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
	}
	
	private static void topplePosition(BigFraction[][][] newXSlices, BigFraction value, int y, int z, BigFraction[] neighborValues,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		switch (neighborCount) {
			case 3:
				Utils.sort3NeighborsByValueDesc(neighborValues, neighborCoords, neighborShareMultipliers);
				topplePositionSortedNeighbors(newXSlices, value, y, z, neighborValues, 
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
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(new BigFraction(n0Mult)));
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(new BigFraction(n1Mult)));
					newXSlices[1][y][z] = newXSlices[1][y][z].add(value).subtract(toShare).add(share);
				} else if (n0Val.compareTo(n1Val) < 0) {
					// n0Val < n1Val < value
					BigFraction toShare = value.subtract(n1Val); 
					BigFraction share = toShare.divide(3);
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(new BigFraction(n0Mult)));
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(new BigFraction(n1Mult)));
					BigFraction currentRemainingValue = value.subtract(share.multiply(new BigFraction(neighborCount)));
					toShare = currentRemainingValue.subtract(n0Val);
					share = toShare.divide(2);
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(new BigFraction(n0Mult)));
					newXSlices[1][y][z] = newXSlices[1][y][z].add(currentRemainingValue).subtract(toShare).add(share);
				} else {
					// n1Val < n0Val < value
					BigFraction toShare = value.subtract(n0Val); 
					BigFraction share = toShare.divide(3);
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share.multiply(new BigFraction(n0Mult)));
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(new BigFraction(n1Mult)));
					BigFraction currentRemainingValue = value.subtract(share.multiply(new BigFraction(neighborCount)));
					toShare = currentRemainingValue.subtract(n1Val);
					share = toShare.divide(2);
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share.multiply(new BigFraction(n1Mult)));
					newXSlices[1][y][z] = newXSlices[1][y][z].add(currentRemainingValue).subtract(toShare).add(share);
				}				
				break;
			case 1:
				BigFraction toShare = value.subtract(neighborValues[0]);
				BigFraction share = toShare.divide(2);
				if (!share.equals(BigFraction.ZERO)) {
					value = value.subtract(toShare).add(share);
					int[] nc = neighborCoords[0];
					newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(new BigFraction(neighborShareMultipliers[0])));
				}
				// no break
			case 0:
				newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
				break;
			default: // 6, 5, 4
				Utils.sortNeighborsByValueDesc(neighborCount, neighborValues, neighborCoords, 
						neighborShareMultipliers);
				topplePositionSortedNeighbors(newXSlices, value, y, z, neighborValues, neighborCoords, 
						neighborShareMultipliers, neighborCount);
		}
	}
	
	private static void topplePositionSortedNeighbors(BigFraction[][][] newXSlices, BigFraction value, int y, int z, BigFraction[] neighborValues,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		int shareCount = neighborCount + 1;
		BigFraction neighborValue = neighborValues[0];
		BigFraction toShare = value.subtract(neighborValue);
		BigFraction share = toShare.divide(new BigFraction(shareCount));
		if (!share.equals(BigFraction.ZERO)) {
			value = value.subtract(toShare).add(share);
			for (int j = 0; j < neighborCount; j++) {
				int[] nc = neighborCoords[j];
				newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(new BigFraction(neighborShareMultipliers[j])));
			}
		}
		BigFraction previousNeighborValue = neighborValue;
		shareCount--;
		for (int i = 1; i < neighborCount; i++) {
			neighborValue = neighborValues[i];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				share = toShare.divide(new BigFraction(shareCount));
				if (!share.equals(BigFraction.ZERO)) {
					value = value.subtract(toShare).add(share);
					for (int j = i; j < neighborCount; j++) {
						int[] nc = neighborCoords[j];
						newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share.multiply(new BigFraction(neighborShareMultipliers[j])));
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
	}
	
	private static void topplePosition(BigFraction[][][] newXSlices, BigFraction value, int y, int z, BigFraction[] asymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		switch (asymmetricNeighborCount) {
			case 3:
				Utils.sort3NeighborsByValueDesc(asymmetricNeighborValues, asymmetricNeighborCoords, asymmetricNeighborSymmetryCounts);
				topplePositionSortedNeighbors(newXSlices, value, y, z, asymmetricNeighborValues, 
						asymmetricNeighborCoords, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
				break;
			case 2:
				BigFraction n0Val = asymmetricNeighborValues[0], n1Val = asymmetricNeighborValues[1];
				int[] n0Coords = asymmetricNeighborCoords[0], n1Coords = asymmetricNeighborCoords[1];
				int shareCount = neighborCount + 1;
				if (n0Val.equals(n1Val)) {
					// n0Val = n1Val < value
					BigFraction toShare = value.subtract(n0Val); 
					BigFraction share = toShare.divide(new BigFraction(shareCount));
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share);
					newXSlices[1][y][z] = newXSlices[1][y][z].add(value).subtract(toShare).add(share);
				} else if (n0Val.compareTo(n1Val) < 0) {
					// n0Val < n1Val < value
					BigFraction toShare = value.subtract(n1Val); 
					BigFraction share = toShare.divide(new BigFraction(shareCount));
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share);
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					BigFraction currentRemainingValue = value.subtract(share.multiply(new BigFraction(neighborCount)));
					toShare = currentRemainingValue.subtract(n0Val);
					share = toShare.divide(new BigFraction(shareCount));
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share);
					newXSlices[1][y][z] = newXSlices[1][y][z].add(currentRemainingValue).subtract(toShare).add(share);
				} else {
					// n1Val < n0Val < value
					BigFraction toShare = value.subtract(n0Val); 
					BigFraction share = toShare.divide(new BigFraction(shareCount));
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] = newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share);
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					BigFraction currentRemainingValue = value.subtract(share.multiply(new BigFraction(neighborCount)));
					toShare = currentRemainingValue.subtract(n1Val);
					share = toShare.divide(new BigFraction(shareCount));
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] = newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]].add(share);
					newXSlices[1][y][z] = newXSlices[1][y][z].add(currentRemainingValue).subtract(toShare).add(share);
				}				
				break;
			case 1:
				shareCount = neighborCount + 1;
				BigFraction toShare = value.subtract(asymmetricNeighborValues[0]);
				BigFraction share = toShare.divide(new BigFraction(shareCount));
				if (!share.equals(BigFraction.ZERO)) {
					value = value.subtract(toShare).add(share);
					int[] nc = asymmetricNeighborCoords[0];
					newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
				}
				// no break
			case 0:
				newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
				break;
			default: // 6, 5, 4
				Utils.sortNeighborsByValueDesc(asymmetricNeighborCount, asymmetricNeighborValues, asymmetricNeighborCoords, 
						asymmetricNeighborSymmetryCounts);
				topplePositionSortedNeighbors(newXSlices, value, y, z, asymmetricNeighborValues, asymmetricNeighborCoords, 
						asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
	}
	
	private static void topplePositionSortedNeighbors(BigFraction[][][] newXSlices, BigFraction value, int y, int z, BigFraction[] asymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		int shareCount = neighborCount + 1;
		BigFraction neighborValue = asymmetricNeighborValues[0];
		BigFraction toShare = value.subtract(neighborValue);
		BigFraction share = toShare.divide(new BigFraction(shareCount));
		if (!share.equals(BigFraction.ZERO)) {
			value = value.subtract(toShare).add(share);
			for (int j = 0; j < asymmetricNeighborCount; j++) {
				int[] nc = asymmetricNeighborCoords[j];
				newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
			}
		}
		BigFraction previousNeighborValue = neighborValue;
		shareCount -= asymmetricNeighborSymmetryCounts[0];
		for (int i = 1; i < asymmetricNeighborCount; i++) {
			neighborValue = asymmetricNeighborValues[i];
			if (!neighborValue.equals(previousNeighborValue)) {
				toShare = value.subtract(neighborValue);
				share = toShare.divide(new BigFraction(shareCount));				
				if (!share.equals(BigFraction.ZERO)) {
					value = value.subtract(toShare).add(share);
					for (int j = i; j < asymmetricNeighborCount; j++) {
						int[] nc = asymmetricNeighborCoords[j];
						newXSlices[nc[0]][nc[1]][nc[2]] = newXSlices[nc[0]][nc[1]][nc[2]].add(share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[i];
		}
		newXSlices[1][y][z] = newXSlices[1][y][z].add(value);
	}
	
	@Override
	public BigFraction getFromPosition(int x, int y, int z){	
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
		return BigFraction.ZERO;
	}
	
	@Override
	public BigFraction getFromAsymmetricPosition(int x, int y, int z){	
		return grid[x][y][z];
	}
	
	@Override
	public int getAsymmetricMinX() {
		return 0;
	}

	@Override
	public int getAsymmetricMaxX() {
		return step;
	}
	
	@Override
	public int getAsymmetricMinY() {
		return 0;
	}
	
	@Override
	public int getAsymmetricMaxY() {
		return (step + 2)/2 - 1;
	}
	
	@Override
	public int getAsymmetricMinZ() {
		return 0;
	}
	
	@Override
	public int getAsymmetricMaxZ() {
		return getAsymmetricMaxY();
	}
	@Override
	public int getAsymmetricMinXAtY(int y) {
		return y;
	}

	@Override
	public int getAsymmetricMinXAtZ(int z) {
		return z;
	}

	@Override
	public int getAsymmetricMinX(int y, int z) {
		return Math.max(y, z);
	}

	@Override
	public int getAsymmetricMaxXAtY(int y) {
		return getAsymmetricMaxX();
	}

	@Override
	public int getAsymmetricMaxXAtZ(int z) {
		return getAsymmetricMaxX();
	}

	@Override
	public int getAsymmetricMaxX(int y, int z) {
		return getAsymmetricMaxX();
	}

	@Override
	public int getAsymmetricMinYAtX(int x) {
		return 0;
	}

	@Override
	public int getAsymmetricMinYAtZ(int z) {
		return z;
	}

	@Override
	public int getAsymmetricMinY(int x, int z) {
		return z;
	}

	@Override
	public int getAsymmetricMaxYAtX(int x) {
		return Math.min(getAsymmetricMaxY(), x);
	}

	@Override
	public int getAsymmetricMaxYAtZ(int z) {
		return getAsymmetricMaxY();
	}

	@Override
	public int getAsymmetricMaxY(int x, int z) {
		return Math.min(getAsymmetricMaxY(), x);
	}

	@Override
	public int getAsymmetricMinZAtX(int x) {
		return 0;
	}

	@Override
	public int getAsymmetricMinZAtY(int y) {
		return 0;
	}

	@Override
	public int getAsymmetricMinZ(int x, int y) {
		return 0;
	}

	@Override
	public int getAsymmetricMaxZAtX(int x) {
		return Math.min(getAsymmetricMaxZ(), x);
	}

	@Override
	public int getAsymmetricMaxZAtY(int y) {
		return Math.min(getAsymmetricMaxZ(), y);
	}

	@Override
	public int getAsymmetricMaxZ(int x, int y) {
		return Math.min(getAsymmetricMaxZ(), y);
	}
	
	@Override
	public int getMinX() {
		return -getAsymmetricMaxX();
	}

	@Override
	public int getMaxX() {
		return getAsymmetricMaxX();
	}

	@Override
	public int getMinY() {
		return -getAsymmetricMaxX();
	}

	@Override
	public int getMaxY() {
		return getAsymmetricMaxX();
	}
	
	@Override
	public int getMinZ() {
		return -getAsymmetricMaxX();
	}

	@Override
	public int getMaxZ() {
		return getAsymmetricMaxX();
	}	
	
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
		return "Aether3D";
	}
	
	@Override
	public String getSubFolderPath() {
		String path = getName() + File.separator;
		if (!isPositive) path += "-";
		path += "∞";
		return path;
	}
	
}