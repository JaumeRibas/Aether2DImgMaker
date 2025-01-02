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
import java.util.Arrays;

import cellularautomata.Constants;
import cellularautomata.Utils;
import cellularautomata.model.SerializableModelData;
import cellularautomata.model2d.IsotropicSquareNumericArrayModelA;
import cellularautomata.numbers.BigInt;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 2D with a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class BigIntAether2D extends IsotropicSquareNumericArrayModelA<BigInt> {
	
	private static final BigInt two = BigInt.valueOf(2);
	private static final BigInt three = BigInt.valueOf(3);
	private static final BigInt four = BigInt.valueOf(4);
	private static final BigInt five = BigInt.valueOf(5);
	
	private final BigInt initialValue;
	private long step;	
	private int maxX;
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
	public BigIntAether2D(BigInt initialValue) {
		this.initialValue = initialValue;
		grid = Utils.buildAnisotropic2DBigIntArray(6);
		grid[0][0] = this.initialValue;
		maxX = 3;
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
	public BigIntAether2D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		SerializableModelData data = (SerializableModelData) Utils.deserializeFromFile(backupPath);
		data = SerializableModelData.updateDataFormat(data);
		if (!SerializableModelData.Models.AETHER.equals(data.get(SerializableModelData.MODEL))) {
			throw new IllegalArgumentException("The backup file contains a different model.");
		}
		if (!SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_TYPE))
				|| !SerializableModelData.InitialConfigurationImplementationTypes.BIG_INT.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.GridTypes.INFINITE_REGULAR.equals(data.get(SerializableModelData.GRID_TYPE))
				|| !Integer.valueOf(2).equals(data.get(SerializableModelData.GRID_DIMENSION))
				|| !SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BIG_INT_ARRAY_1.equals(data.get(SerializableModelData.GRID_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER.equals(data.get(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE))
				|| !data.contains(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP)
				|| !data.contains(SerializableModelData.INITIAL_CONFIGURATION_FOLDER_NAME)) {
			throw new IllegalArgumentException("The backup file's configuration is not compatible with this class.");
		}
		initialValue = (BigInt) data.get(SerializableModelData.INITIAL_CONFIGURATION);
		grid = (BigInt[][]) data.get(SerializableModelData.GRID);
		maxX = (int) data.get(SerializableModelData.COORDINATE_BOUNDS);
		step = (long) data.get(SerializableModelData.STEP);
		changed = (Boolean) data.get(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP);
		folderName = (String) data.get(SerializableModelData.INITIAL_CONFIGURATION_FOLDER_NAME);
	}
	
	@Override
	public Boolean nextStep() {
		BigInt[][] newGrid = new BigInt[maxX + 3][];
		boolean changed = false;
		BigInt currentValue, greaterXNeighborValue;
		BigInt[] smallerXSlice = null, currentXSlice = grid[0], greaterXSlice = grid[1];
		BigInt[] newSmallerXSlice = null, newCurrentXSlice = new BigInt[1], newGreaterXSlice = new BigInt[2];// build new grid progressively to save memory
		Arrays.fill(newCurrentXSlice, BigInt.ZERO);
		Arrays.fill(newGreaterXSlice, BigInt.ZERO);
		newGrid[0] = newCurrentXSlice;
		newGrid[1] = newGreaterXSlice;
		// x = 0, y = 0
		currentValue = currentXSlice[0];
		greaterXNeighborValue = greaterXSlice[0];
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			BigInt toShare = currentValue.subtract(greaterXNeighborValue);
			BigInt[] shareAndRemainder = toShare.divideAndRemainder(five);
			BigInt share = shareAndRemainder[0];
			if (!share.equals(BigInt.ZERO)) {
				changed = true;
				newCurrentXSlice[0] = newCurrentXSlice[0].add(currentValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
				newGreaterXSlice[0] = newGreaterXSlice[0].add(share);
			} else {
				newCurrentXSlice[0] = newCurrentXSlice[0].add(currentValue);
			}			
		} else {
			newCurrentXSlice[0] = newCurrentXSlice[0].add(currentValue);
		}		
		// x = 1, y = 0
		// smallerXSlice = currentXSlice; // not needed here
		currentXSlice = greaterXSlice;
		greaterXSlice = grid[2];
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = new BigInt[3];
		Arrays.fill(newGreaterXSlice, BigInt.ZERO);
		newGrid[2] = newGreaterXSlice;
		BigInt[][] newXSlices = new BigInt[][] { newSmallerXSlice, newCurrentXSlice, newGreaterXSlice};
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		BigInt[] relevantAsymmetricNeighborValues = new BigInt[4];
		int[] sortedNeighborsIndexes = new int[4];
		int[][] relevantAsymmetricNeighborCoords = new int[4][2];
		int[] relevantAsymmetricNeighborShareMultipliers = new int[4];// to compensate for omitted symmetric positions
		int[] relevantAsymmetricNeighborSymmetryCounts = new int[4];// to compensate for omitted symmetric positions
		// reuse values obtained previously
		BigInt smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterXNeighborValue = greaterXSlice[0];
		BigInt greaterYNeighborValue = currentXSlice[1];
		if (smallerXNeighborValue.compareTo(currentValue) < 0) {
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
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, 
				relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			changed = true;
		}
		// x = 1, y = 1
		// reuse values obtained previously
		BigInt smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterXNeighborValue = greaterXSlice[1];
		if (smallerYNeighborValue.compareTo(currentValue) < 0) {
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				if (smallerYNeighborValue.equals(greaterXNeighborValue)) {
					// gx = sy < current
					BigInt toShare = currentValue.subtract(greaterXNeighborValue); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(five);
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						changed = true;
					}
					newCurrentXSlice[0] = newCurrentXSlice[0].add(share.add(share));// one more for the symmetric position at the other side
					newCurrentXSlice[1] = newCurrentXSlice[1].add(currentValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
					newGreaterXSlice[1] = newGreaterXSlice[1].add(share);
				} else if (smallerYNeighborValue.compareTo(greaterXNeighborValue) < 0) {
					// sy < gx < current
					BigInt toShare = currentValue.subtract(greaterXNeighborValue); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(five);
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						changed = true;
					}
					newCurrentXSlice[0] = newCurrentXSlice[0].add(share.add(share));
					newGreaterXSlice[1] = newGreaterXSlice[1].add(share);
					BigInt currentRemainingValue = currentValue.subtract(share.multiply(four));
					toShare = currentRemainingValue.subtract(smallerYNeighborValue); 
					shareAndRemainder = toShare.divideAndRemainder(three);
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						changed = true;
					}
					newCurrentXSlice[0] = newCurrentXSlice[0].add(share.add(share));
					newCurrentXSlice[1] = newCurrentXSlice[1].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
				} else {
					// gx < sy < current
					BigInt toShare = currentValue.subtract(smallerYNeighborValue); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(five);
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						changed = true;
					}
					newCurrentXSlice[0] = newCurrentXSlice[0].add(share.add(share));
					newGreaterXSlice[1] = newGreaterXSlice[1].add(share);
					BigInt currentRemainingValue = currentValue.subtract(share.multiply(four));
					toShare = currentRemainingValue.subtract(greaterXNeighborValue); 
					shareAndRemainder = toShare.divideAndRemainder(three);
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						changed = true;
					}
					newCurrentXSlice[1] = newCurrentXSlice[1].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
					newGreaterXSlice[1] = newGreaterXSlice[1].add(share);
				}
			} else {
				// sy < current <= gx
				BigInt toShare = currentValue.subtract(smallerYNeighborValue); 
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(three);
				BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					changed = true;
				}
				newCurrentXSlice[0] = newCurrentXSlice[0].add(share.add(share));
				newCurrentXSlice[1] = newCurrentXSlice[1].add(currentValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
			}
		} else if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			// gx < current <= sy
			BigInt toShare = currentValue.subtract(greaterXNeighborValue); 
			BigInt[] shareAndRemainder = toShare.divideAndRemainder(three);
			BigInt share = shareAndRemainder[0];
			if (!share.equals(BigInt.ZERO)) {
				changed = true;
			}
			newCurrentXSlice[1] = newCurrentXSlice[1].add(currentValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
			newGreaterXSlice[1] = newGreaterXSlice[1].add(share);
		} else {
			// gx >= current <= sy
			newCurrentXSlice[1] = newCurrentXSlice[1].add(currentValue);
		}
		grid[0] = null;// free old grid progressively to save memory
		// x = 2, y = 0
		smallerXSlice = currentXSlice;
		currentXSlice = greaterXSlice;
		greaterXSlice = grid[3];
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = new BigInt[4];	
		Arrays.fill(newGreaterXSlice, BigInt.ZERO);
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
		if (smallerXNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, 
				relevantNeighborCount, relevantAsymmetricNeighborCount)) {
			changed = true;
		}
		// x = 2, y = 1
		relevantAsymmetricNeighborCount = 0;
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice[2];
		smallerXNeighborValue = smallerXSlice[1];
		greaterXNeighborValue = greaterXSlice[1];
		if (smallerXNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue.compareTo(currentValue) < 0) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborCount)) {
			changed = true;
		}
		// x = 2, y = 2
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterXNeighborValue = greaterXSlice[2];
		if (smallerYNeighborValue.compareTo(currentValue) < 0) {
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				if (smallerYNeighborValue.equals(greaterXNeighborValue)) {
					// gx = sy < current
					BigInt toShare = currentValue.subtract(greaterXNeighborValue); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(five);
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						changed = true;
					}
					newCurrentXSlice[1] = newCurrentXSlice[1].add(share);
					newCurrentXSlice[2] = newCurrentXSlice[2].add(currentValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
					newGreaterXSlice[2] = newGreaterXSlice[2].add(share);
				} else if (smallerYNeighborValue.compareTo(greaterXNeighborValue) < 0) {
					// sy < gx < current
					BigInt toShare = currentValue.subtract(greaterXNeighborValue); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(five);
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						changed = true;
					}
					newCurrentXSlice[1] = newCurrentXSlice[1].add(share);
					newGreaterXSlice[2] = newGreaterXSlice[2].add(share);
					BigInt currentRemainingValue = currentValue.subtract(share.multiply(four));
					toShare = currentRemainingValue.subtract(smallerYNeighborValue); 
					shareAndRemainder = toShare.divideAndRemainder(three);
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						changed = true;
					}
					newCurrentXSlice[1] = newCurrentXSlice[1].add(share);
					newCurrentXSlice[2] = newCurrentXSlice[2].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
				} else {
					// gx < sy < current
					BigInt toShare = currentValue.subtract(smallerYNeighborValue); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(five);
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						changed = true;
					}
					newCurrentXSlice[1] = newCurrentXSlice[1].add(share);
					newGreaterXSlice[2] = newGreaterXSlice[2].add(share);
					BigInt currentRemainingValue = currentValue.subtract(share.multiply(four));
					toShare = currentRemainingValue.subtract(greaterXNeighborValue); 
					shareAndRemainder = toShare.divideAndRemainder(three);
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						changed = true;
					}
					newCurrentXSlice[2] = newCurrentXSlice[2].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
					newGreaterXSlice[2] = newGreaterXSlice[2].add(share);
				}
			} else {
				// sy < current <= gx
				BigInt toShare = currentValue.subtract(smallerYNeighborValue); 
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(three);
				BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					changed = true;
				}
				newCurrentXSlice[1] = newCurrentXSlice[1].add(share);
				newCurrentXSlice[2] = newCurrentXSlice[2].add(currentValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
			}
		} else if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			// gx < current <= sy
			BigInt toShare = currentValue.subtract(greaterXNeighborValue); 
			BigInt[] shareAndRemainder = toShare.divideAndRemainder(three);
			BigInt share = shareAndRemainder[0];
			if (!share.equals(BigInt.ZERO)) {
				changed = true;
			}
			newCurrentXSlice[2] = newCurrentXSlice[2].add(currentValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
			newGreaterXSlice[2] = newGreaterXSlice[2].add(share);
		} else {
			// gx >= current <= sy
			newCurrentXSlice[2] = newCurrentXSlice[2].add(currentValue);
		}
		grid[1] = null;
		// 3 <= x < edge - 2
		int edge = grid.length - 1;
		int edgeMinusTwo = edge - 2;
		BigInt[][] xSlices = new BigInt[][] {null, currentXSlice, greaterXSlice};
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		if (toppleRangeBeyondX2(xSlices, newXSlices, newGrid, 3, edgeMinusTwo, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, sortedNeighborsIndexes)) { // is it faster to reuse these arrays?
			changed = true;
		}
		//edge - 2 <= x < edge
		if (toppleRangeBeyondX2(xSlices, newXSlices, newGrid, edgeMinusTwo, edge, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, sortedNeighborsIndexes)) {
			changed = true;
			maxX++;
		}
		if (newGrid.length > grid.length) {
			newGreaterXSlice = new BigInt[newGrid.length];
			Arrays.fill(newGreaterXSlice, BigInt.ZERO);
			newGrid[grid.length] = newGreaterXSlice;
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
	
	private boolean toppleRangeBeyondX2(BigInt[][] xSlices, BigInt[][] newXSlices, BigInt[][] newGrid, int minX, int maxX, 
			BigInt[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts, int[] sortedNeighborsIndexes) {
		boolean anyToppled = false;
		int x = minX, xMinusOne = x - 1, xPlusOne = x + 1, xPlusTwo = xPlusOne + 1;
		BigInt[] smallerXSlice = null, currentXSlice = xSlices[1], greaterXSlice = xSlices[2];
		BigInt[] newSmallerXSlice = null, newCurrentXSlice = newXSlices[1], newGreaterXSlice = newXSlices[2];
		for (; x < maxX; xMinusOne = x, x = xPlusOne, xPlusOne = xPlusTwo, xPlusTwo++) {
			// y = 0;
			smallerXSlice = currentXSlice;
			currentXSlice = greaterXSlice;
			greaterXSlice = grid[xPlusOne];
			newSmallerXSlice = newCurrentXSlice;
			newCurrentXSlice = newGreaterXSlice;
			newGreaterXSlice = new BigInt[xPlusTwo];
			Arrays.fill(newGreaterXSlice, BigInt.ZERO);
			newGrid[xPlusOne] = newGreaterXSlice;
			newXSlices[0] = newSmallerXSlice;
			newXSlices[1] = newCurrentXSlice;
			newXSlices[2] = newGreaterXSlice;
			int relevantAsymmetricNeighborCount = 0;
			int relevantNeighborCount = 0;
			BigInt currentValue = currentXSlice[0];
			BigInt greaterYNeighborValue = currentXSlice[1];
			BigInt smallerXNeighborValue = smallerXSlice[0];
			BigInt greaterXNeighborValue = greaterXSlice[0];
			if (smallerXNeighborValue.compareTo(currentValue) < 0) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 0;
				nc[1] = 0;
				relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
				relevantNeighborCount++;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 2;
				nc[1] = 0;
				relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
				relevantNeighborCount++;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterYNeighborValue.compareTo(currentValue) < 0) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 1;
				nc[1] = 1;
				relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
				relevantNeighborCount += 2;
				relevantAsymmetricNeighborCount++;
			}
			if (topplePosition(newXSlices, currentValue, 0, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount)) {
				anyToppled = true;
			}
			// y = 1
			relevantAsymmetricNeighborCount = 0;
			// reuse values obtained previously
			BigInt smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterYNeighborValue = currentXSlice[2];
			smallerXNeighborValue = smallerXSlice[1];
			greaterXNeighborValue = greaterXSlice[1];
			if (smallerXNeighborValue.compareTo(currentValue) < 0) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 0;
				nc[1] = 1;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 2;
				nc[1] = 1;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
				relevantAsymmetricNeighborCount++;
			}
			if (smallerYNeighborValue.compareTo(currentValue) < 0) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 1;
				nc[1] = 0;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterYNeighborValue.compareTo(currentValue) < 0) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 1;
				nc[1] = 2;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
				relevantAsymmetricNeighborCount++;
			}
			if (topplePosition(newXSlices, currentValue, 1, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborCount)) {
				anyToppled = true;
			}
			// 2 >= y < x - 1
			int y = 2, yMinusOne = 1, yPlusOne = 3;
			for (; y < xMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
				relevantAsymmetricNeighborCount = 0;
				// reuse values obtained previously
				smallerYNeighborValue = currentValue;
				currentValue = greaterYNeighborValue;
				greaterYNeighborValue = currentXSlice[yPlusOne];
				smallerXNeighborValue = smallerXSlice[y];
				greaterXNeighborValue = greaterXSlice[y];
				if (smallerXNeighborValue.compareTo(currentValue) < 0) {
					relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
					int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
					nc[0] = 0;
					nc[1] = y;
					relevantAsymmetricNeighborCount++;
				}
				if (greaterXNeighborValue.compareTo(currentValue) < 0) {
					relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
					int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
					nc[0] = 2;
					nc[1] = y;
					relevantAsymmetricNeighborCount++;
				}
				if (smallerYNeighborValue.compareTo(currentValue) < 0) {
					relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
					int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
					nc[0] = 1;
					nc[1] = yMinusOne;
					relevantAsymmetricNeighborCount++;
				}
				if (greaterYNeighborValue.compareTo(currentValue) < 0) {
					relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
					int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
					nc[0] = 1;
					nc[1] = yPlusOne;
					relevantAsymmetricNeighborCount++;
				}
				if (topplePosition(newXSlices, currentValue, y, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborCount)) {
					anyToppled = true;
				}
			}
			// y = x - 1
			relevantAsymmetricNeighborCount = 0;
			// reuse values obtained previously
			smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterYNeighborValue = currentXSlice[yPlusOne];
			smallerXNeighborValue = smallerXSlice[y];
			greaterXNeighborValue = greaterXSlice[y];
			if (smallerXNeighborValue.compareTo(currentValue) < 0) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 0;
				nc[1] = y;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 2;
				nc[1] = y;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
				relevantAsymmetricNeighborCount++;
			}
			if (smallerYNeighborValue.compareTo(currentValue) < 0) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 1;
				nc[1] = yMinusOne;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterYNeighborValue.compareTo(currentValue) < 0) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 1;
				nc[1] = yPlusOne;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
				relevantAsymmetricNeighborCount++;
			}
			if (topplePosition(newXSlices, currentValue, y, relevantAsymmetricNeighborValues, sortedNeighborsIndexes, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborCount)) {
				anyToppled = true;
			}
			// y = x
			yMinusOne = y;
			y = x;
			// reuse values obtained previously
			smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterXNeighborValue = greaterXSlice[y];
			if (smallerYNeighborValue.compareTo(currentValue) < 0) {
				if (greaterXNeighborValue.compareTo(currentValue) < 0) {
					if (smallerYNeighborValue.equals(greaterXNeighborValue)) {
						// gx = sy < current
						BigInt toShare = currentValue.subtract(greaterXNeighborValue); 
						BigInt[] shareAndRemainder = toShare.divideAndRemainder(five);
						BigInt share = shareAndRemainder[0];
						if (!share.equals(BigInt.ZERO)) {
							anyToppled = true;
						}
						newCurrentXSlice[yMinusOne] = newCurrentXSlice[yMinusOne].add(share);
						newCurrentXSlice[y] = newCurrentXSlice[y].add(currentValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
						newGreaterXSlice[y] = newGreaterXSlice[y].add(share);
					} else if (smallerYNeighborValue.compareTo(greaterXNeighborValue) < 0) {
						// sy < gx < current
						BigInt toShare = currentValue.subtract(greaterXNeighborValue); 
						BigInt[] shareAndRemainder = toShare.divideAndRemainder(five);
						BigInt share = shareAndRemainder[0];
						if (!share.equals(BigInt.ZERO)) {
							anyToppled = true;
						}
						newCurrentXSlice[yMinusOne] = newCurrentXSlice[yMinusOne].add(share);
						newGreaterXSlice[y] = newGreaterXSlice[y].add(share);
						BigInt currentRemainingValue = currentValue.subtract(share.multiply(four));
						toShare = currentRemainingValue.subtract(smallerYNeighborValue); 
						shareAndRemainder = toShare.divideAndRemainder(three);
						share = shareAndRemainder[0];
						if (!share.equals(BigInt.ZERO)) {
							anyToppled = true;
						}
						newCurrentXSlice[yMinusOne] = newCurrentXSlice[yMinusOne].add(share);
						newCurrentXSlice[y] = newCurrentXSlice[y].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
					} else {
						// gx < sy < current
						BigInt toShare = currentValue.subtract(smallerYNeighborValue); 
						BigInt[] shareAndRemainder = toShare.divideAndRemainder(five);
						BigInt share = shareAndRemainder[0];
						if (!share.equals(BigInt.ZERO)) {
							anyToppled = true;
						}
						newCurrentXSlice[yMinusOne] = newCurrentXSlice[yMinusOne].add(share);
						newGreaterXSlice[y] = newGreaterXSlice[y].add(share);
						BigInt currentRemainingValue = currentValue.subtract(share.multiply(four));
						toShare = currentRemainingValue.subtract(greaterXNeighborValue); 
						shareAndRemainder = toShare.divideAndRemainder(three);
						share = shareAndRemainder[0];
						if (!share.equals(BigInt.ZERO)) {
							anyToppled = true;
						}
						newCurrentXSlice[y] = newCurrentXSlice[y].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
						newGreaterXSlice[y] = newGreaterXSlice[y].add(share);
					}
				} else {
					// sy < current <= gx
					BigInt toShare = currentValue.subtract(smallerYNeighborValue); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(three);
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						anyToppled = true;
					}
					newCurrentXSlice[yMinusOne] = newCurrentXSlice[yMinusOne].add(share);
					newCurrentXSlice[y] = newCurrentXSlice[y].add(currentValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
				}
			} else if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				// gx < current <= sy
				BigInt toShare = currentValue.subtract(greaterXNeighborValue); 
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(three);
				BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					anyToppled = true;
				}
				newCurrentXSlice[y] = newCurrentXSlice[y].add(currentValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
				newGreaterXSlice[y] = newGreaterXSlice[y].add(share);
			} else {
				// gx >= current <= sy
				newCurrentXSlice[y] = newCurrentXSlice[y].add(currentValue);
			}
			grid[xMinusOne] = null;
		}
		xSlices[1] = currentXSlice;
		xSlices[2] = greaterXSlice;
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		return anyToppled;
	}

	private static boolean topplePosition(BigInt[][] newXSlices, BigInt value, int y, BigInt[] neighborValues, 
			int[] sortedNeighborsIndexes, int[][] neighborCoords, int neighborCount) {
		boolean toppled = false;
		switch (neighborCount) {
			case 4:
				Utils.sortDescendingLength4(neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, neighborValues, 
						sortedNeighborsIndexes, neighborCoords, neighborCount);
				break;
			case 3:
				Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, neighborValues, 
						sortedNeighborsIndexes, neighborCoords, neighborCount);
				break;
			case 2:
				BigInt n0Val = neighborValues[0], n1Val = neighborValues[1];
				int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
				int shareCount = 3;
				if (n0Val.equals(n1Val)) {
					// n0Val = n1Val < value
					BigInt toShare = value.subtract(n0Val); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share);
					newXSlices[1][y] = newXSlices[1][y].add(value.subtract(toShare).add(share).add(shareAndRemainder[1]));
				} else if (n0Val.compareTo(n1Val) < 0) {
					// n0Val < n1Val < value
					BigInt toShare = value.subtract(n1Val); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share);
					shareCount = 2;
					BigInt currentRemainingValue = value.subtract(share.multiply(two));
					toShare = currentRemainingValue.subtract(n0Val);
					shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share);
					newXSlices[1][y] = newXSlices[1][y].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
				} else {
					// n1Val < n0Val < value
					BigInt toShare = value.subtract(n0Val); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share);
					shareCount = 2;
					BigInt currentRemainingValue = value.subtract(share.multiply(two));
					toShare = currentRemainingValue.subtract(n1Val);
					shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share);
					newXSlices[1][y] = newXSlices[1][y].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
				}
				break;
			case 1:
				BigInt toShare = value.subtract(neighborValues[0]);
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(two);
				BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
					value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
					int[] nc = neighborCoords[0];
					newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share);
				}
				// no break
			default: // 0
				newXSlices[1][y] = newXSlices[1][y].add(value);
		}
		return toppled;
	}
	
	
	private static boolean topplePositionSortedNeighbors(BigInt[][] newXSlices, BigInt value, int y, 
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
				newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share);
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
						newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newXSlices[1][y] = newXSlices[1][y].add(value);
		return toppled;
	}
	
	private static boolean topplePosition(BigInt[][] newXSlices, BigInt value, int y, BigInt[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
			case 4:
				Utils.sortDescendingLength4(asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, asymmetricNeighborValues, sortedNeighborsIndexes, 
						asymmetricNeighborCoords, asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
				break;
			case 3:
				Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, asymmetricNeighborValues, sortedNeighborsIndexes, 
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
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share.multiply(n0Mult));
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share.multiply(n1Mult));
					newXSlices[1][y] = newXSlices[1][y].add(value.subtract(toShare).add(share).add(shareAndRemainder[1]));
				} else if (n0Val.compareTo(n1Val) < 0) {
					// n0Val < n1Val < value
					BigInt toShare = value.subtract(n1Val); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share.multiply(n0Mult));
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share.multiply(n1Mult));
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					BigInt currentRemainingValue = value.subtract(share.multiply(BigInt.valueOf(neighborCount)));
					toShare = currentRemainingValue.subtract(n0Val);
					shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share.multiply(n0Mult));
					newXSlices[1][y] = newXSlices[1][y].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
				} else {
					// n1Val < n0Val < value
					BigInt toShare = value.subtract(n0Val); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share.multiply(n0Mult));
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share.multiply(n1Mult));
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					BigInt currentRemainingValue = value.subtract(share.multiply(BigInt.valueOf(neighborCount)));
					toShare = currentRemainingValue.subtract(n1Val);
					shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share.multiply(n1Mult));
					newXSlices[1][y] = newXSlices[1][y].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
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
					newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share.multiply(asymmetricNeighborShareMultipliers[0]));
				}
				// no break
			default: // 0
				newXSlices[1][y] = newXSlices[1][y].add(value);
		}
		return toppled;
	}
	
	
	private static boolean topplePositionSortedNeighbors(BigInt[][] newXSlices, BigInt value, int y, BigInt[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
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
				newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share.multiply(BigInt.valueOf(asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]])));
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
						newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share.multiply(BigInt.valueOf(asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]])));
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		newXSlices[1][y] = newXSlices[1][y].add(value);
		return toppled;
	}
	
	private static boolean topplePosition(BigInt[][] newXSlices, BigInt value, int y, BigInt[] neighborValues, int[] sortedNeighborsIndexes,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount) {
		boolean toppled = false;
		switch (neighborCount) {
			case 4:
				Utils.sortDescendingLength4(neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, neighborValues, sortedNeighborsIndexes, 
						neighborCoords, neighborShareMultipliers, neighborCount);
				break;
			case 3:
				Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, neighborValues, sortedNeighborsIndexes, 
						neighborCoords, neighborShareMultipliers, neighborCount);
				break;
			case 2:
				BigInt n0Val = neighborValues[0], n1Val = neighborValues[1];
				int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
				int n0Mult = neighborShareMultipliers[0], n1Mult = neighborShareMultipliers[1];
				int shareCount = 3;
				if (n0Val.equals(n1Val)) {
					// n0Val = n1Val < value
					BigInt toShare = value.subtract(n0Val); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share.multiply(n0Mult));
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share.multiply(n1Mult));
					newXSlices[1][y] = newXSlices[1][y].add(value.subtract(toShare).add(share).add(shareAndRemainder[1]));
				} else if (n0Val.compareTo(n1Val) < 0) {
					// n0Val < n1Val < value
					BigInt toShare = value.subtract(n1Val); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share.multiply(n0Mult));
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share.multiply(n1Mult));
					shareCount = 2;
					BigInt currentRemainingValue = value.subtract(share.multiply(two));
					toShare = currentRemainingValue.subtract(n0Val);
					shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share.multiply(n0Mult));
					newXSlices[1][y] = newXSlices[1][y].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
				} else {
					// n1Val < n0Val < value
					BigInt toShare = value.subtract(n0Val); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share.multiply(n0Mult));
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share.multiply(n1Mult));
					shareCount = 2;
					BigInt currentRemainingValue = value.subtract(share.multiply(two));
					toShare = currentRemainingValue.subtract(n1Val);
					shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share.multiply(n1Mult));
					newXSlices[1][y] = newXSlices[1][y].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
				}
				break;
			case 1:
				BigInt toShare = value.subtract(neighborValues[0]);
				BigInt[] shareAndRemainder = toShare.divideAndRemainder(two);
				BigInt share = shareAndRemainder[0];
				if (!share.equals(BigInt.ZERO)) {
					toppled = true;
					value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
					int[] nc = neighborCoords[0];
					newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share.multiply(neighborShareMultipliers[0]));
				}
				// no break
			default: // 0
				newXSlices[1][y] = newXSlices[1][y].add(value);
		}
		return toppled;
	}
	
	
	private static boolean topplePositionSortedNeighbors(BigInt[][] newXSlices, BigInt value, int y, BigInt[] neighborValues, int[] sortedNeighborsIndexes,
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
				newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share.multiply(BigInt.valueOf(neighborShareMultipliers[sortedNeighborsIndexes[j]])));
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
						newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share.multiply(BigInt.valueOf(neighborShareMultipliers[sortedNeighborsIndexes[j]])));
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newXSlices[1][y] = newXSlices[1][y].add(value);
		return toppled;
	}
	
	private static boolean topplePosition(BigInt[][] newXSlices, BigInt value, int y, BigInt[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, int neighborCount, int asymmetricNeighborCount) {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
			case 4:
				Utils.sortDescendingLength4(asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, asymmetricNeighborValues, sortedNeighborsIndexes, 
						asymmetricNeighborCoords, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount);
				break;
			case 3:
				Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, asymmetricNeighborValues, sortedNeighborsIndexes, 
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
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share);
					newXSlices[1][y] = newXSlices[1][y].add(value.subtract(toShare).add(share).add(shareAndRemainder[1]));
				} else if (n0Val.compareTo(n1Val) < 0) {
					// n0Val < n1Val < value
					BigInt toShare = value.subtract(n1Val); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share);
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					BigInt currentRemainingValue = value.subtract(share.multiply(BigInt.valueOf(neighborCount)));
					toShare = currentRemainingValue.subtract(n0Val);
					shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share);
					newXSlices[1][y] = newXSlices[1][y].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
				} else {
					// n1Val < n0Val < value
					BigInt toShare = value.subtract(n0Val); 
					BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					BigInt share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] = newXSlices[n0Coords[0]][n0Coords[1]].add(share);
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share);
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					BigInt currentRemainingValue = value.subtract(share.multiply(BigInt.valueOf(neighborCount)));
					toShare = currentRemainingValue.subtract(n1Val);
					shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
					share = shareAndRemainder[0];
					if (!share.equals(BigInt.ZERO)) {
						toppled = true;
					}
					newXSlices[n1Coords[0]][n1Coords[1]] = newXSlices[n1Coords[0]][n1Coords[1]].add(share);
					newXSlices[1][y] = newXSlices[1][y].add(currentRemainingValue.subtract(toShare).add(share).add(shareAndRemainder[1]));
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
					newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share);
				}
				// no break
			default: // 0
				newXSlices[1][y] = newXSlices[1][y].add(value);
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(BigInt[][] newXSlices, BigInt value, int y, BigInt[] asymmetricNeighborValues, int[] sortedNeighborsIndexes,
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
				newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share);
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
						newXSlices[nc[0]][nc[1]] = newXSlices[nc[0]][nc[1]].add(share);
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		newXSlices[1][y] = newXSlices[1][y].add(value);
		return toppled;
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
		return getName() + "/2D/" + folderName;
	}
	
	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		SerializableModelData data = new SerializableModelData();
		data.put(SerializableModelData.MODEL, SerializableModelData.Models.AETHER);
		data.put(SerializableModelData.INITIAL_CONFIGURATION, initialValue);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_TYPE, SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE, SerializableModelData.InitialConfigurationImplementationTypes.BIG_INT);
		data.put(SerializableModelData.GRID, grid);
		data.put(SerializableModelData.GRID_TYPE, SerializableModelData.GridTypes.INFINITE_REGULAR);
		data.put(SerializableModelData.GRID_DIMENSION, 2);
		data.put(SerializableModelData.GRID_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BIG_INT_ARRAY_1);
		data.put(SerializableModelData.COORDINATE_BOUNDS, maxX);
		data.put(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE, SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER);
		data.put(SerializableModelData.STEP, step);
		data.put(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP, changed);
		data.put(SerializableModelData.INITIAL_CONFIGURATION_FOLDER_NAME, folderName);
		Utils.serializeToFile(data, backupPath, backupName);
	}
	
}