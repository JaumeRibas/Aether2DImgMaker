/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2020 Jaume Ribas

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
package cellularautomata.automata;

import java.io.FileNotFoundException;
import java.io.IOException;
import cellularautomata.evolvinggrid.SymmetricEvolvingLongGrid3D;

public class Aether3DB implements SymmetricEvolvingLongGrid3D {

	/** A 3D array representing the grid */
	private long[][][] grid;
	
	private long initialValue;
	private long step;
	
	private int maxX;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public Aether3DB(long initialValue) {
		if (initialValue < Long.valueOf("-3689348814741910323")) {//to prevent overflow of long type
			throw new IllegalArgumentException("Initial value cannot be smaller than -3,689,348,814,741,910,323. Use a greater initial value or a different implementation.");
		}
		this.initialValue = initialValue;
		grid = Utils.buildAnisotropic3DLongArray(3);//TODO change
		grid[0][0][0] = this.initialValue;
		maxX = 0;//TODO change
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
	public Aether3DB(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		Aether3DB data = (Aether3DB) Utils.deserializeFromFile(backupPath);
		initialValue = data.initialValue;
		grid = data.grid;
		maxX = data.maxX;
		step = data.step;
	}
	
	@Override
	public boolean nextStep(){
		long[][][] newGrid = new long[maxX + 1][][];//TODO change
		boolean changed = false;
		long[][] smallerXSlice = null, currentXSlice = grid[0], greaterXSlice = grid[1];
		long[][] newSmallerXSlice = null, 
				newCurrentXSlice = Utils.buildAnisotropic2DLongArray(1), 
				newGreaterXSlice = Utils.buildAnisotropic2DLongArray(2);// build new grid progressively to save memory
		newGrid[0] = newCurrentXSlice;
		newGrid[1] = newGreaterXSlice;
		// x = 0, y = 0, z = 0
		long currentValue = currentXSlice[0][0];
		long greaterXNeighborValue = greaterXSlice[0][0];
		if (greaterXNeighborValue < currentValue) {
			long toShare = currentValue - greaterXNeighborValue;
			long share = toShare/7;
			if (share != 0) {
				changed = true;
				newCurrentXSlice[0][0] += currentValue - toShare + share + toShare%7;
				newGreaterXSlice[0][0] += share;
			} else {
				newCurrentXSlice[0][0] += currentValue;
			}			
		} else {
			newCurrentXSlice[0][0] += currentValue;
		}
		// x = 1, y = 0, z = 0
		// smallerXSlice = currentXSlice; // not needed here
		currentXSlice = greaterXSlice;
		greaterXSlice = grid[2];
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = Utils.buildAnisotropic2DLongArray(3);
		newGrid[2] = newGreaterXSlice;
		long[][][] newXSlices = new long[][][] { newSmallerXSlice, newCurrentXSlice, newGreaterXSlice};
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		long[] relevantAsymmetricNeighborValues = new long[6];
		int[][] relevantAsymmetricNeighborCoords = new int[6][3];
		int[] relevantAsymmetricNeighborShareMultipliers = new int[6];// to compensate for omitted symmetric positions
		int[] relevantAsymmetricNeighborSymmetryCounts = new int[6];// to compensate for omitted symmetric positions
		// reuse values obtained previously
		long smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterXNeighborValue = greaterXSlice[0][0];
		long greaterYNeighborValue = currentXSlice[1][0];
		if (greaterXNeighborValue < currentValue) {
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
		if (smallerXNeighborValue < currentValue) {
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
		if (greaterYNeighborValue < currentValue) {
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
		if (topplePosition(newXSlices, currentValue, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			changed = true;
		}
		// x = 1, y = 1, z = 0
		relevantAsymmetricNeighborCount = 0;
		relevantNeighborCount = 0;
		// reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterXNeighborValue = greaterXSlice[1][0];
		long greaterZNeighborValue = currentXSlice[1][1];
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 0;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue < currentValue) {
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
		if (greaterZNeighborValue < currentValue) {
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
		if (topplePosition(newXSlices, currentValue, 1, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			changed = true;
		}
		// x = 1, y = 1, z = 1
		// reuse values obtained previously
		long smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterXNeighborValue = greaterXSlice[1][1];
		if (smallerZNeighborValue < currentValue) {
			if (greaterXNeighborValue < currentValue) {
				if (smallerZNeighborValue == greaterXNeighborValue) {
					// gx = sz < current
					long toShare = currentValue - greaterXNeighborValue; 
					long share = toShare/7;
					if (share != 0) {
						changed = true;
					}
					newCurrentXSlice[1][0] += share + share;// one more for the symmetric position at the other side
					newCurrentXSlice[1][1] += currentValue - toShare + share + toShare%7;
					newGreaterXSlice[1][1] += share;
				} else if (smallerZNeighborValue < greaterXNeighborValue) {
					// sz < gx < current
					long toShare = currentValue - greaterXNeighborValue; 
					long share = toShare/7;
					if (share != 0) {
						changed = true;
					}
					newCurrentXSlice[1][0] += share + share;
					newGreaterXSlice[1][1] += share;
					long currentRemainingValue = currentValue - 6*share;
					toShare = currentRemainingValue - smallerZNeighborValue; 
					share = toShare/4;
					if (share != 0) {
						changed = true;
					}
					newCurrentXSlice[1][0] += share + share;
					newCurrentXSlice[1][1] += currentRemainingValue - toShare + share + toShare%4;
				} else {
					// gx < sz < current
					long toShare = currentValue - smallerZNeighborValue; 
					long share = toShare/7;
					if (share != 0) {
						changed = true;
					}
					newCurrentXSlice[1][0] += share + share;
					newGreaterXSlice[1][1] += share;
					long currentRemainingValue = currentValue - 6*share;
					toShare = currentRemainingValue - greaterXNeighborValue; 
					share = toShare/4;
					if (share != 0) {
						changed = true;
					}
					newCurrentXSlice[1][1] += currentRemainingValue - toShare + share + toShare%4;
					newGreaterXSlice[1][1] += share;
				}
			} else {
				// sz < current <= gx
				long toShare = currentValue - smallerZNeighborValue; 
				long share = toShare/4;
				if (share != 0) {
					changed = true;
				}
				newCurrentXSlice[1][0] += share + share;
				newCurrentXSlice[1][1] += currentValue - toShare + share + toShare%4;
			}
		} else {
			if (greaterXNeighborValue < currentValue) {
				// gx < current <= sz
				long toShare = currentValue - greaterXNeighborValue; 
				long share = toShare/4;
				if (share != 0) {
					changed = true;
				}
				newCurrentXSlice[1][1] += currentValue - toShare + share + toShare%4;
				newGreaterXSlice[1][1] += share;
			} else {
				newCurrentXSlice[1][1] += currentValue;
			}
		}
		grid[0] = null;// free old grid progressively to save memory
		// x = 2, y = 0, z = 0
		smallerXSlice = currentXSlice;
		currentXSlice = greaterXSlice;
		greaterXSlice = grid[3];
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = Utils.buildAnisotropic2DLongArray(4);
		newGrid[3] = newGreaterXSlice;
		newXSlices[0] = newSmallerXSlice;
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		currentValue = currentXSlice[0][0];
		greaterXNeighborValue = greaterXSlice[0][0];
		smallerXNeighborValue = smallerXSlice[0][0];
		greaterYNeighborValue = currentXSlice[1][0];
		if (topplePositionType1(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, newXSlices, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts)) {
			changed = true;
		}
		// x = 2, y = 1, z = 0
		relevantAsymmetricNeighborCount = 0;
		relevantNeighborCount = 0;
		greaterXNeighborValue = greaterXSlice[1][0];
		smallerXNeighborValue = smallerXSlice[1][0];
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice[2][0];
		greaterZNeighborValue = currentXSlice[1][1];
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 1;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = 1;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 2;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 0;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 4;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, 1, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			changed = true;
		}
		// x = 2, y = 1, z = 1
		relevantAsymmetricNeighborCount = 0;
		relevantNeighborCount = 0;
		greaterXNeighborValue = greaterXSlice[1][1];
		smallerXNeighborValue = smallerXSlice[1][1];
		greaterYNeighborValue = currentXSlice[2][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 1;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = 1;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 3;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 2;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, 1, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			changed = true;
		}
		// x = 2, y = 2, z = 0
		if (topplePositionType2(2, currentXSlice, greaterXSlice, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 2, y = 2, z = 1
		relevantAsymmetricNeighborCount = 0;
		relevantNeighborCount = 0;
		greaterXNeighborValue = greaterXSlice[2][1];
		smallerYNeighborValue = currentXSlice[1][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[2][2];
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 2;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 2;
			nc[2] = 2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 3;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 2;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, 2, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			changed = true;
		}
		// x = 2, y = 2, z = 2
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		if (topplePositionType3(2, greaterXSlice, smallerZNeighborValue, currentValue, newCurrentXSlice, newGreaterXSlice)) {
			changed = true;
		}
		grid[1] = null;
		// x = 3, y = 0, z = 0
		smallerXSlice = currentXSlice;
		currentXSlice = greaterXSlice;
		greaterXSlice = grid[4];
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = Utils.buildAnisotropic2DLongArray(5);
		newGrid[4] = newGreaterXSlice;
		newXSlices[0] = newSmallerXSlice;
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		currentValue = currentXSlice[0][0];
		greaterXNeighborValue = greaterXSlice[0][0];
		smallerXNeighborValue = smallerXSlice[0][0];
		greaterYNeighborValue = currentXSlice[1][0];
		if (topplePositionType1(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, newXSlices, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts)) {
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
		if (topplePositionType4(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				smallerYNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
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
		if (topplePositionType5(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 2, z = 0
		currentValue = currentXSlice[2][0];
		greaterXNeighborValue = greaterXSlice[2][0];
		smallerXNeighborValue = smallerXSlice[2][0];
		greaterYNeighborValue = currentXSlice[3][0];
		smallerYNeighborValue = currentXSlice[1][0];
		greaterZNeighborValue = currentXSlice[2][1];
		if (topplePositionType6(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				smallerYNeighborValue, greaterZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 2, z = 1
		relevantAsymmetricNeighborCount = 0;
		relevantNeighborCount = 0;
		greaterXNeighborValue = greaterXSlice[2][1];
		smallerXNeighborValue = smallerXSlice[2][1];
		greaterYNeighborValue = currentXSlice[3][1];
		smallerYNeighborValue = currentXSlice[1][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[2][2];
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 2;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
		}
		if (smallerXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = 2;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount++;
		}
		if (greaterYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 3;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount++;
		}
		if (smallerYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount++;
		}
		if (greaterZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 2;
			nc[2] = 2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount++;
		}
		if (smallerZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 2;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, 2, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantNeighborCount)) {
			changed = true;
		}
		// x = 3, y = 2, z = 2
		greaterXNeighborValue = greaterXSlice[1][1];
		smallerXNeighborValue = smallerXSlice[1][1];
		greaterYNeighborValue = currentXSlice[2][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		if (topplePositionType7(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 3, z = 0
		if (topplePositionType2(3, currentXSlice, greaterXSlice, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 3, z = 1
		greaterXNeighborValue = greaterXSlice[3][1];
		smallerYNeighborValue = currentXSlice[2][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[3][2];
		if (topplePositionType8(3, currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 3, z = 2
		greaterXNeighborValue = greaterXSlice[3][2];
		smallerYNeighborValue = currentXSlice[2][2];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		greaterZNeighborValue = currentXSlice[3][3];
		if (topplePositionType9(3, currentValue, greaterXNeighborValue, smallerYNeighborValue, greaterZNeighborValue, 
				smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 3, y = 3, z = 3
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		if (topplePositionType3(3, greaterXSlice, smallerZNeighborValue, currentValue, newCurrentXSlice, newGreaterXSlice)) {
			changed = true;
		}
		grid[2] = null;
		// x = 4, y = 0, z = 0
		smallerXSlice = currentXSlice;
		currentXSlice = greaterXSlice;
		greaterXSlice = grid[5];
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = Utils.buildAnisotropic2DLongArray(6);
		newGrid[5] = newGreaterXSlice;
		newXSlices[0] = newSmallerXSlice;
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		currentValue = currentXSlice[0][0];
		greaterXNeighborValue = greaterXSlice[0][0];
		smallerXNeighborValue = smallerXSlice[0][0];
		greaterYNeighborValue = currentXSlice[1][0];
		if (topplePositionType1(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, newXSlices, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts)) {
			changed = true;
		}
		// x = 4, y = 1, z = 0
		greaterXNeighborValue = greaterXSlice[1][0];
		smallerXNeighborValue = smallerXSlice[1][0];
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice[2][0];
		greaterZNeighborValue = currentXSlice[1][1];
		if (topplePositionType4(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				smallerYNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 4, y = 1, z = 1
		greaterXNeighborValue = greaterXSlice[1][1];
		smallerXNeighborValue = smallerXSlice[1][1];
		greaterYNeighborValue = currentXSlice[2][1];
		// reuse values obtained previously
		smallerZNeighborValue = currentValue;
		currentValue = greaterZNeighborValue;
		if (topplePositionType5(currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		// x = 4, y = 2, z = 0 wip
		//TODO reuse values
		currentValue = currentXSlice[2][0];
		greaterXNeighborValue = greaterXSlice[2][0];
		smallerXNeighborValue = smallerXSlice[2][0];
		greaterYNeighborValue = currentXSlice[3][0];
		smallerYNeighborValue = currentXSlice[1][0];
		greaterZNeighborValue = currentXSlice[2][1];
		if (topplePositionType10(2, currentValue, greaterXNeighborValue, smallerXNeighborValue, greaterYNeighborValue, 
				smallerYNeighborValue, smallerZNeighborValue, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, newXSlices)) {
			changed = true;
		}
		
		grid = newGrid;
		step++;
		return changed;
	}

	private static boolean topplePositionType10(int y, long currentValue, long greaterXNeighborValue, long smallerXNeighborValue, 
			long greaterYNeighborValue, long smallerYNeighborValue, long greaterZNeighborValue, 
			long[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, 
			long[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount ] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = y;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount ++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = y;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y + 1;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y - 1;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, y, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean topplePositionType9(int y, long currentValue, long greaterXNeighborValue, long smallerYNeighborValue, 
			long greaterZNeighborValue, long smallerZNeighborValue, long[] relevantAsymmetricNeighborValues, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts, long[][][] newXSlices) {
		int z = y - 1;
		int yMinusOne = z;
		int zPlusOne = y;
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = y;
			nc[2] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = yMinusOne;
			nc[2] = z;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = zPlusOne;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 3;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = z - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, y, z, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean topplePositionType8(int y, long currentValue, long greaterXNeighborValue, long smallerYNeighborValue, 
			long greaterZNeighborValue, long smallerZNeighborValue, long[] relevantAsymmetricNeighborValues, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts, long[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = y;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y - 1;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = 2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, y, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean topplePositionType7(int coord, long currentValue, long greaterXNeighborValue, 
			long smallerXNeighborValue,	long greaterYNeighborValue, long smallerZNeighborValue, 
			long[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, 
			long[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = coord;
			nc[2] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = coord;
			nc[2] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 3;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord + 1;
			nc[2] = coord;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = coord;
			nc[2] = coord - 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, coord, coord, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean topplePositionType6(int y, long currentValue, long greaterXNeighborValue, long smallerXNeighborValue, 
			long greaterYNeighborValue, long smallerYNeighborValue, long greaterZNeighborValue, 
			long[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, long[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = y;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = y;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y + 1;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y - 1;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, y, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean topplePositionType5(long currentValue, long greaterXNeighborValue, long smallerXNeighborValue, 
			long greaterYNeighborValue, long smallerZNeighborValue, long[] relevantAsymmetricNeighborValues, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts, long[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount ] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 1;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount ++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = 1;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 2;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, 1, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean topplePositionType4(long currentValue, long greaterXNeighborValue, long smallerXNeighborValue, 
			long greaterYNeighborValue, long smallerYNeighborValue, long greaterZNeighborValue, 
			long[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborShareMultipliers, int[] relevantAsymmetricNeighborSymmetryCounts, 
			long[][][] newXSlices) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount ] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 1;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount ++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = 1;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 2;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 0;
			nc[2] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 4;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, 1, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean topplePositionType3(int coord, long[][] greaterXSlice, long smallerZNeighborValue, long currentValue, 
			long[][] newCurrentXSlice, long[][] newGreaterXSlice) {
		boolean changed = false;
		int coordMinusOne = coord - 1;
		long greaterXNeighborValue = greaterXSlice[coord][coord];
		if (smallerZNeighborValue < currentValue) {
			if (greaterXNeighborValue < currentValue) {
				if (smallerZNeighborValue == greaterXNeighborValue) {
					// gx = sz < current
					long toShare = currentValue - greaterXNeighborValue; 
					long share = toShare/7;
					if (share != 0) {
						changed = true;
					}
					newCurrentXSlice[coord][coordMinusOne] += share;
					newCurrentXSlice[coord][coord] += currentValue - toShare + share + toShare%7;
					newGreaterXSlice[coord][coord] += share;
				} else if (smallerZNeighborValue < greaterXNeighborValue) {
					// sz < gx < current
					long toShare = currentValue - greaterXNeighborValue; 
					long share = toShare/7;
					if (share != 0) {
						changed = true;
					}
					newCurrentXSlice[coord][coordMinusOne] += share;
					newGreaterXSlice[coord][coord] += share;
					long currentRemainingValue = currentValue - 6*share;
					toShare = currentRemainingValue - smallerZNeighborValue; 
					share = toShare/4;
					if (share != 0) {
						changed = true;
					}
					newCurrentXSlice[coord][coordMinusOne] += share;
					newCurrentXSlice[coord][coord] += currentRemainingValue - toShare + share + toShare%4;
				} else {
					// gx < sz < current
					long toShare = currentValue - smallerZNeighborValue; 
					long share = toShare/7;
					if (share != 0) {
						changed = true;
					}
					newCurrentXSlice[coord][coordMinusOne] += share;
					newGreaterXSlice[coord][coord] += share;
					long currentRemainingValue = currentValue - 6*share;
					toShare = currentRemainingValue - greaterXNeighborValue; 
					share = toShare/4;
					if (share != 0) {
						changed = true;
					}
					newCurrentXSlice[coord][coord] += currentRemainingValue - toShare + share + toShare%4;
					newGreaterXSlice[coord][coord] += share;
				}
			} else {
				// sz < current <= gx
				long toShare = currentValue - smallerZNeighborValue; 
				long share = toShare/4;
				if (share != 0) {
					changed = true;
				}
				newCurrentXSlice[coord][coordMinusOne] += share;
				newCurrentXSlice[coord][coord] += currentValue - toShare + share + toShare%4;
			}
		} else {
			if (greaterXNeighborValue < currentValue) {
				// gx < current <= sz
				long toShare = currentValue - greaterXNeighborValue; 
				long share = toShare/4;
				if (share != 0) {
					changed = true;
				}
				newCurrentXSlice[coord][coord] += currentValue - toShare + share + toShare%4;
				newGreaterXSlice[coord][coord] += share;
			} else {
				newCurrentXSlice[coord][coord] += currentValue;
			}
		}
		return changed;
	}

	private static boolean topplePositionType2(int y, long[][] currentXSlice, long[][] greaterXSlice, 
			long[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, 
			int[] relevantAsymmetricNeighborSymmetryCounts, long[][][] newXSlices ) {
		int yMinusOne = y - 1;
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		long currentValue = currentXSlice[y][0];
		long greaterXNeighborValue = greaterXSlice[y][0];
		long smallerYNeighborValue = currentXSlice[yMinusOne][0];
		long greaterZNeighborValue = currentXSlice[y][1];
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = y;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = yMinusOne;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterZNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterZNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = y;
			nc[2] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, 2, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean topplePositionType1(long currentValue, long greaterXNeighborValue, long smallerXNeighborValue, 
			long greaterYNeighborValue, long[][][] newXSlices, long[] relevantAsymmetricNeighborValues, 
			int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborSymmetryCounts) {
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 0;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = 0;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			nc[2] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 4;
			relevantNeighborCount += 4;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, 0, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			return true;
		} else {
			return false;
		}
	};
	
	private static boolean topplePosition(long[][][] newXSlices, long value, int y, int z, long[] aymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
			case 3:
				Utils.sort3NeighborsByValueDesc(aymmetricNeighborValues, asymmetricNeighborCoords, asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, aymmetricNeighborValues, 
						asymmetricNeighborCoords, asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
				break;
			case 2:
				long n0Val = aymmetricNeighborValues[0], n1Val = aymmetricNeighborValues[1];
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
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] += share*n0Mult;
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] += share*n1Mult;
					newXSlices[1][y][z] += value - toShare + share + toShare%shareCount;
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					long toShare = value - n1Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] += share*n0Mult;
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] += share*n1Mult;
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] += share*n0Mult;
					newXSlices[1][y][z] += currentRemainingValue - toShare + share + toShare%shareCount;
				} else {
					// n1Val < n0Val < value
					long toShare = value - n0Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] += share*n0Mult;
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] += share*n1Mult;
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n1Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] += share*n1Mult;
					newXSlices[1][y][z] += currentRemainingValue - toShare + share + toShare%shareCount;
				}				
				break;
			case 1:
				shareCount = neighborCount + 1;
				long toShare = value - aymmetricNeighborValues[0];
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%shareCount + share;
					int[] nc = asymmetricNeighborCoords[0];
					newXSlices[nc[0]][nc[1]][nc[2]] += share * asymmetricNeighborShareMultipliers[0];
				}
				// no break
			case 0: 
				newXSlices[1][y][z] += value;
				break;
			default: // 6, 5, 4
				Utils.sortNeighborsByValueDesc(asymmetricNeighborCount, aymmetricNeighborValues, asymmetricNeighborCoords, 
						asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, aymmetricNeighborValues, asymmetricNeighborCoords, 
						asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(long[][][] newXSlices, long value, int y, int z, long[] asymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		boolean isFirstNeighbor = true;
		long previousNeighborValue = 0;
		int shareCount = neighborCount + 1;
		for (int i = 0; i < asymmetricNeighborCount; i++, isFirstNeighbor = false) {
			long neighborValue = asymmetricNeighborValues[i];
			if (neighborValue != previousNeighborValue || isFirstNeighbor) {
				long toShare = value - neighborValue;
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%shareCount + share;
					for (int j = i; j < asymmetricNeighborCount; j++) {
						int[] nc = asymmetricNeighborCoords[j];
						newXSlices[nc[0]][nc[1]][nc[2]] += share * asymmetricNeighborShareMultipliers[j];
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[i];
		}
		newXSlices[1][y][z] += value;
		return toppled;
	}
	
	private static boolean topplePosition(long[][][] newXSlices, long value, int y, int z, long[] neighborValues,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		boolean toppled = false;
		switch (neighborCount) {
			case 3:
				Utils.sort3NeighborsByValueDesc(neighborValues, neighborCoords, neighborShareMultipliers);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, neighborValues, 
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
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] += share*n0Mult;
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] += share*n1Mult;
					newXSlices[1][y][z] += value - toShare + share + toShare%3;
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					long toShare = value - n1Val; 
					long share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] += share*n0Mult;
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] += share*n1Mult;
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/2;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] += share*n0Mult;
					newXSlices[1][y][z] += currentRemainingValue - toShare + share + toShare%2;
				} else {
					// n1Val < n0Val < value
					long toShare = value - n0Val; 
					long share = toShare/3;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] += share*n0Mult;
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] += share*n1Mult;
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n1Val;
					share = toShare/2;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] += share*n1Mult;
					newXSlices[1][y][z] += currentRemainingValue - toShare + share + toShare%2;
				}				
				break;
			case 1:
				long toShare = value - neighborValues[0];
				long share = toShare/2;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%2 + share;
					int[] nc = neighborCoords[0];
					newXSlices[nc[0]][nc[1]][nc[2]] += share * neighborShareMultipliers[0];
				}
				// no break
			case 0: 
				newXSlices[1][y][z] += value;
				break;
			default: // 6, 5, 4
				Utils.sortNeighborsByValueDesc(neighborCount, neighborValues, neighborCoords, 
						neighborShareMultipliers);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, neighborValues, neighborCoords, 
						neighborShareMultipliers, neighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(long[][][] newXSlices, long value, int y, int z, long[] neighborValues,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		boolean toppled = false;
		boolean isFirstNeighbor = true;
		long previousNeighborValue = 0;
		int shareCount = neighborCount + 1;
		for (int i = 0; i < neighborCount; i++, isFirstNeighbor = false) {
			long neighborValue = neighborValues[i];
			if (neighborValue != previousNeighborValue || isFirstNeighbor) {
				long toShare = value - neighborValue;
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%shareCount + share;
					for (int j = i; j < neighborCount; j++) {
						int[] nc = neighborCoords[j];
						newXSlices[nc[0]][nc[1]][nc[2]] += share * neighborShareMultipliers[j];
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newXSlices[1][y][z] += value;
		return toppled;
	}
	
	private static boolean topplePosition(long[][][] newXSlices, long value, int y, int z, long[] aymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
			case 3:
				Utils.sort3NeighborsByValueDesc(aymmetricNeighborValues, asymmetricNeighborCoords, asymmetricNeighborSymmetryCounts);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, aymmetricNeighborValues, 
						asymmetricNeighborCoords, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
				break;
			case 2:
				long n0Val = aymmetricNeighborValues[0], n1Val = aymmetricNeighborValues[1];
				int[] n0Coords = asymmetricNeighborCoords[0], n1Coords = asymmetricNeighborCoords[1];
				int shareCount = neighborCount + 1;
				if (n0Val == n1Val) {
					// n0Val = n1Val < value
					long toShare = value - n0Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] += share;
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] += share;
					newXSlices[1][y][z] += value - toShare + share + toShare%shareCount;
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					long toShare = value - n1Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] += share;
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] += share;
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] += share;
					newXSlices[1][y][z] += currentRemainingValue - toShare + share + toShare%shareCount;
				} else {
					// n1Val < n0Val < value
					long toShare = value - n0Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]][n0Coords[2]] += share;
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] += share;
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n1Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n1Coords[0]][n1Coords[1]][n1Coords[2]] += share;
					newXSlices[1][y][z] += currentRemainingValue - toShare + share + toShare%shareCount;
				}				
				break;
			case 1:
				shareCount = neighborCount + 1;
				long toShare = value - aymmetricNeighborValues[0];
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%shareCount + share;
					int[] nc = asymmetricNeighborCoords[0];
					newXSlices[nc[0]][nc[1]][nc[2]] += share;
				}
				// no break
			case 0: 
				newXSlices[1][y][z] += value;
				break;
			default: // 6, 5, 4
				Utils.sortNeighborsByValueDesc(asymmetricNeighborCount, aymmetricNeighborValues, asymmetricNeighborCoords, 
						asymmetricNeighborSymmetryCounts);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, z, aymmetricNeighborValues, asymmetricNeighborCoords, 
						asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(long[][][] newXSlices, long value, int y, int z, long[] asymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		boolean isFirstNeighbor = true;
		long previousNeighborValue = 0;
		int shareCount = neighborCount + 1;
		for (int i = 0; i < asymmetricNeighborCount; i++, isFirstNeighbor = false) {
			long neighborValue = asymmetricNeighborValues[i];
			if (neighborValue != previousNeighborValue || isFirstNeighbor) {
				long toShare = value - neighborValue;
				long share = toShare/shareCount;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%shareCount + share;
					for (int j = i; j < asymmetricNeighborCount; j++) {
						int[] nc = asymmetricNeighborCoords[j];
						newXSlices[nc[0]][nc[1]][nc[2]] += share;
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[i];
		}
		newXSlices[1][y][z] += value;
		return toppled;
	}
	
	@Override
	public long getValueAtPosition(int x, int y, int z){	
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
		return 0;
	}
	
	@Override
	public long getValueAtAsymmetricPosition(int x, int y, int z){	
		return grid[x][y][z];
	}
	
	@Override
	public int getAsymmetricMinX() {
		return 0;
	}

	@Override
	public int getAsymmetricMaxX() {
		return maxX;
	}
	
	@Override
	public int getAsymmetricMinY() {
		return 0;
	}
	
	@Override
	public int getAsymmetricMaxY() {
		return maxX;
	}
	
	@Override
	public int getAsymmetricMinZ() {
		return 0;
	}
	
	@Override
	public int getAsymmetricMaxZ() {
		return maxX;
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
	
	/**
	 * Returns the initial value
	 * 
	 * @return the value at the origin at step 0
	 */
	public long getInitialValue() {
		return initialValue;
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
		return getName() + "/" + initialValue;
	}
	
}