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
import cellularautomata.model2d.IsotropicSquareBooleanArrayModelAsymmetricSection;

public class LongAetherTopplingAlternationCompliance2D extends IsotropicSquareBooleanArrayModelAsymmetricSection {
	
	public static final long MAX_INITIAL_VALUE = Long.MAX_VALUE;
	public static final long MIN_INITIAL_VALUE = -6148914691236517205L;

	/** A 2D array representing the grid */
	private long[][] sourceGrid;
	
	private boolean itsEvenPositionsTurnToTopple;
	
	private final long initialValue;
	private long step;
	private int maxX;
	private Boolean changed = null;

	/**
	 * Creates an instance with the given initial value.
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public LongAetherTopplingAlternationCompliance2D(long initialValue) {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of long type
			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
	    }
		this.initialValue = initialValue;
		itsEvenPositionsTurnToTopple = initialValue >= 0;
		final int side = 6;
		sourceGrid = Utils.buildAnisotropic2DLongArray(side);
		sourceGrid[0][0] = initialValue;
		maxX = 3;
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
	public LongAetherTopplingAlternationCompliance2D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		SerializableModelData data = (SerializableModelData) Utils.deserializeFromFile(backupPath);
		data = SerializableModelData.updateDataFormat(data);
		if (!SerializableModelData.Models.AETHER.equals(data.get(SerializableModelData.MODEL))) {
			throw new IllegalArgumentException("The backup file contains a different model.");
		}
		if (!SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_TYPE))
				|| !SerializableModelData.InitialConfigurationImplementationTypes.LONG.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.GridTypes.INFINITE_REGULAR.equals(data.get(SerializableModelData.GRID_TYPE))
				|| !Integer.valueOf(2).equals(data.get(SerializableModelData.GRID_DIMENSION))
				|| !SerializableModelData.GridImplementationTypes.ANYSOTROPIC_LONG_PRIMITIVE_ARRAY_1.equals(data.get(SerializableModelData.GRID_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER.equals(data.get(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE))
				|| !data.contains(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP)) {
			throw new IllegalArgumentException("The backup file's configuration is not compatible with this class.");
		}
		initialValue = (long) data.get(SerializableModelData.INITIAL_CONFIGURATION);
		sourceGrid = (long[][]) data.get(SerializableModelData.GRID);
		maxX = (int) data.get(SerializableModelData.COORDINATE_BOUNDS);
		step = (long) data.get(SerializableModelData.STEP);
		changed = (Boolean) data.get(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP);
		itsEvenPositionsTurnToTopple = initialValue >= 0 == (step%2 == 0);
		if (SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BOOLEAN_PRIMITIVE_ARRAY_1.equals(data.get(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE_IMPLEMENTATION_TYPE))) {
			grid = (boolean[][]) data.get(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE);
		} else {
			nextStep();
		}
	}
	
	@Override
	public Boolean nextStep() {
		final int newSide = maxX + 3;
		long[][] newSourceGrid = new long[newSide][];
		grid = null;
		grid = Utils.buildAnisotropic2DBooleanArray(newSide);
		boolean changed = false;
		long currentValue, greaterXNeighborValue;
		long[] smallerXSlice = null, currentXSlice = sourceGrid[0], greaterXSlice = sourceGrid[1];
		long[] newSmallerXSlice = null, newCurrentXSlice = new long[1], newGreaterXSlice = new long[2];// build new grid progressively to save memory
		boolean[] newCurrentXSliceCompliance = grid[0]; 
		newSourceGrid[0] = newCurrentXSlice;
		newSourceGrid[1] = newGreaterXSlice;
		// x = 0, y = 0
		boolean itsCurrentPositionsTurnToTopple = itsEvenPositionsTurnToTopple;
		currentValue = currentXSlice[0];
		greaterXNeighborValue = greaterXSlice[0];
		boolean toppled = false;
		if (greaterXNeighborValue < currentValue) {
			long toShare = currentValue - greaterXNeighborValue;
			long share = toShare/5;
			if (share != 0) {
				changed = true;
				toppled = true;
				newCurrentXSlice[0] += currentValue - toShare + share + toShare%5;
				newGreaterXSlice[0] += share;
			} else {
				newCurrentXSlice[0] += currentValue;
			}			
		} else {
			newCurrentXSlice[0] += currentValue;
		}	
		newCurrentXSliceCompliance[0] = toppled == itsCurrentPositionsTurnToTopple;
		// x = 1, y = 0
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// smallerXSlice = currentXSlice; // not needed here
		currentXSlice = greaterXSlice;
		greaterXSlice = sourceGrid[2];
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = new long[3];
		newCurrentXSliceCompliance = grid[1];
		newSourceGrid[2] = newGreaterXSlice;
		long[][] newXSlices = new long[][] { newSmallerXSlice, newCurrentXSlice, newGreaterXSlice};
		int relevantAsymmetricNeighborCount = 0;
		int relevantNeighborCount = 0;
		long[] relevantAsymmetricNeighborValues = new long[4];
		int[] sortedNeighborsIndexes = new int[4];
		int[][] relevantAsymmetricNeighborCoords = new int[4][2];
		int[] relevantAsymmetricNeighborShareMultipliers = new int[4];// to compensate for omitted symmetric positions
		int[] relevantAsymmetricNeighborSymmetryCounts = new int[4];// to compensate for omitted symmetric positions
		// reuse values obtained previously
		long smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterXNeighborValue = greaterXSlice[0];
		long greaterYNeighborValue = currentXSlice[1];
		if (smallerXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;// this is the index of the new slice: 0->newSmallerXSlice, 1->newCurrentXSlice, 2->newGreaterXSlice
			nc[1] = 0;// this is the actual y coordinate
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 4;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, 0, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, 
				relevantAsymmetricNeighborCount, sortedNeighborsIndexes)) {
			changed = true;
			newCurrentXSliceCompliance[0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentXSliceCompliance[0] = !itsCurrentPositionsTurnToTopple;
		}
		// x = 1, y = 1
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		long smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterXNeighborValue = greaterXSlice[1];
		toppled = false;
		if (smallerYNeighborValue < currentValue) {
			if (greaterXNeighborValue < currentValue) {
				if (smallerYNeighborValue == greaterXNeighborValue) {
					// gx = sy < current
					long toShare = currentValue - greaterXNeighborValue; 
					long share = toShare/5;
					if (share != 0) {
						changed = true;
						toppled = true;
					}
					newCurrentXSlice[0] += share + share;// one more for the symmetric position at the other side
					newCurrentXSlice[1] += currentValue - toShare + share + toShare%5;
					newGreaterXSlice[1] += share;
				} else if (smallerYNeighborValue < greaterXNeighborValue) {
					// sy < gx < current
					long toShare = currentValue - greaterXNeighborValue; 
					long share = toShare/5;
					if (share != 0) {
						changed = true;
						toppled = true;
					}
					newCurrentXSlice[0] += share + share;
					newGreaterXSlice[1] += share;
					long currentRemainingValue = currentValue - 4*share;
					toShare = currentRemainingValue - smallerYNeighborValue; 
					share = toShare/3;
					if (share != 0) {
						changed = true;
						toppled = true;
					}
					newCurrentXSlice[0] += share + share;
					newCurrentXSlice[1] += currentRemainingValue - toShare + share + toShare%3;
				} else {
					// gx < sy < current
					long toShare = currentValue - smallerYNeighborValue; 
					long share = toShare/5;
					if (share != 0) {
						changed = true;
						toppled = true;
					}
					newCurrentXSlice[0] += share + share;
					newGreaterXSlice[1] += share;
					long currentRemainingValue = currentValue - 4*share;
					toShare = currentRemainingValue - greaterXNeighborValue; 
					share = toShare/3;
					if (share != 0) {
						changed = true;
						toppled = true;
					}
					newCurrentXSlice[1] += currentRemainingValue - toShare + share + toShare%3;
					newGreaterXSlice[1] += share;
				}
			} else {
				// sy < current <= gx
				long toShare = currentValue - smallerYNeighborValue; 
				long share = toShare/3;
				if (share != 0) {
					changed = true;
					toppled = true;
				}
				newCurrentXSlice[0] += share + share;
				newCurrentXSlice[1] += currentValue - toShare + share + toShare%3;
			}
		} else if (greaterXNeighborValue < currentValue) {
			// gx < current <= sy
			long toShare = currentValue - greaterXNeighborValue; 
			long share = toShare/3;
			if (share != 0) {
				changed = true;
				toppled = true;
			}
			newCurrentXSlice[1] += currentValue - toShare + share + toShare%3;
			newGreaterXSlice[1] += share;
		} else {
			// gx >= current <= sy
			newCurrentXSlice[1] += currentValue;
		}
		newCurrentXSliceCompliance[1] = toppled == itsCurrentPositionsTurnToTopple;
		sourceGrid[0] = null;// free old grid progressively to save memory
		// x = 2, y = 0
		smallerXSlice = currentXSlice;
		currentXSlice = greaterXSlice;
		greaterXSlice = sourceGrid[3];
		newSmallerXSlice = newCurrentXSlice;
		newCurrentXSlice = newGreaterXSlice;
		newGreaterXSlice = new long[4];
		newCurrentXSliceCompliance = grid[2];
		newSourceGrid[3] = newGreaterXSlice;
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
		if (smallerXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 0;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
			relevantNeighborCount++;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 1;
			relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
			relevantNeighborCount += 2;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, 0, relevantAsymmetricNeighborValues, 
				relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, 
				relevantNeighborCount, relevantAsymmetricNeighborCount, sortedNeighborsIndexes)) {
			changed = true;
			newCurrentXSliceCompliance[0] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentXSliceCompliance[0] = !itsCurrentPositionsTurnToTopple;
		}
		// x = 2, y = 1
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		relevantAsymmetricNeighborCount = 0;
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterYNeighborValue = currentXSlice[2];
		smallerXNeighborValue = smallerXSlice[1];
		greaterXNeighborValue = greaterXSlice[1];
		if (smallerXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 0;
			nc[1] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterXNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 2;
			nc[1] = 1;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
			relevantAsymmetricNeighborCount++;
		}
		if (smallerYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 0;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborCount++;
		}
		if (greaterYNeighborValue < currentValue) {
			relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
			int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
			nc[0] = 1;
			nc[1] = 2;
			relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
			relevantAsymmetricNeighborCount++;
		}
		if (topplePosition(newXSlices, currentValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborCount, sortedNeighborsIndexes)) {
			changed = true;
			newCurrentXSliceCompliance[1] = itsCurrentPositionsTurnToTopple;
		} else {
			newCurrentXSliceCompliance[1] = !itsCurrentPositionsTurnToTopple;
		}
		// x = 2, y = 2
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		// reuse values obtained previously
		smallerYNeighborValue = currentValue;
		currentValue = greaterYNeighborValue;
		greaterXNeighborValue = greaterXSlice[2];
		toppled = false;
		if (smallerYNeighborValue < currentValue) {
			if (greaterXNeighborValue < currentValue) {
				if (smallerYNeighborValue == greaterXNeighborValue) {
					// gx = sy < current
					long toShare = currentValue - greaterXNeighborValue; 
					long share = toShare/5;
					if (share != 0) {
						changed = true;
						toppled = true;
					}
					newCurrentXSlice[1] += share;
					newCurrentXSlice[2] += currentValue - toShare + share + toShare%5;
					newGreaterXSlice[2] += share;
				} else if (smallerYNeighborValue < greaterXNeighborValue) {
					// sy < gx < current
					long toShare = currentValue - greaterXNeighborValue; 
					long share = toShare/5;
					if (share != 0) {
						changed = true;
						toppled = true;
					}
					newCurrentXSlice[1] += share;
					newGreaterXSlice[2] += share;
					long currentRemainingValue = currentValue - 4*share;
					toShare = currentRemainingValue - smallerYNeighborValue; 
					share = toShare/3;
					if (share != 0) {
						changed = true;
						toppled = true;
					}
					newCurrentXSlice[1] += share;
					newCurrentXSlice[2] += currentRemainingValue - toShare + share + toShare%3;
				} else {
					// gx < sy < current
					long toShare = currentValue - smallerYNeighborValue; 
					long share = toShare/5;
					if (share != 0) {
						changed = true;
						toppled = true;
					}
					newCurrentXSlice[1] += share;
					newGreaterXSlice[2] += share;
					long currentRemainingValue = currentValue - 4*share;
					toShare = currentRemainingValue - greaterXNeighborValue; 
					share = toShare/3;
					if (share != 0) {
						changed = true;
						toppled = true;
					}
					newCurrentXSlice[2] += currentRemainingValue - toShare + share + toShare%3;
					newGreaterXSlice[2] += share;
				}
			} else {
				// sy < current <= gx
				long toShare = currentValue - smallerYNeighborValue; 
				long share = toShare/3;
				if (share != 0) {
					changed = true;
					toppled = true;
				}
				newCurrentXSlice[1] += share;
				newCurrentXSlice[2] += currentValue - toShare + share + toShare%3;
			}
		} else if (greaterXNeighborValue < currentValue) {
			// gx < current <= sy
			long toShare = currentValue - greaterXNeighborValue; 
			long share = toShare/3;
			if (share != 0) {
				changed = true;
				toppled = true;
			}
			newCurrentXSlice[2] += currentValue - toShare + share + toShare%3;
			newGreaterXSlice[2] += share;
		} else {
			// gx >= current <= sy
			newCurrentXSlice[2] += currentValue;
		}
		newCurrentXSliceCompliance[2] = toppled == itsCurrentPositionsTurnToTopple;
		sourceGrid[1] = null;
		// 3 <= x < edge - 2
		int edge = sourceGrid.length - 1;
		int edgeMinusTwo = edge - 2;
		long[][] xSlices = new long[][] {null, currentXSlice, greaterXSlice};
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		if (toppleRangeBeyondX2(xSlices, newXSlices, newSourceGrid, 3, edgeMinusTwo, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, sortedNeighborsIndexes)) { // is it faster to reuse these arrays?
			changed = true;
		}
		//edge - 2 <= x < edge
		if (toppleRangeBeyondX2(xSlices, newXSlices, newSourceGrid, edgeMinusTwo, edge, 
				relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
				relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborSymmetryCounts, sortedNeighborsIndexes)) {
			changed = true;
			maxX++;
		}
		registerStaticGridSliceCompliance(edge);
		if (newSourceGrid.length > sourceGrid.length) {
			newSourceGrid[sourceGrid.length] = new long[newSourceGrid.length];
		}
		sourceGrid = newSourceGrid;
		step++;
		itsEvenPositionsTurnToTopple = !itsEvenPositionsTurnToTopple;
		this.changed = changed;
		return changed;
	}
	
	private void registerStaticGridSliceCompliance(int x) {
		if (x%2 == 0 == itsEvenPositionsTurnToTopple) {
			Utils.fillOddIndexes(grid[x], true);
		} else {
			Utils.fillEvenIndexes(grid[x], true);
		}
	}

	@Override
	public Boolean isChanged() {
		return changed;
	}
	
	private boolean toppleRangeBeyondX2(long[][] xSlices, long[][] newXSlices, long[][] newSourceGrid, int minX, int maxX, 
			long[] relevantAsymmetricNeighborValues, int[][] relevantAsymmetricNeighborCoords, int[] relevantAsymmetricNeighborShareMultipliers, 
			int[] relevantAsymmetricNeighborSymmetryCounts, int[] sortedNeighborsIndexes) {
		boolean anyToppled = false;
		int x = minX, xMinusOne = x - 1, xPlusOne = x + 1, xPlusTwo = xPlusOne + 1;
		long[] smallerXSlice = null, currentXSlice = xSlices[1], greaterXSlice = xSlices[2];
		long[] newSmallerXSlice = null, newCurrentXSlice = newXSlices[1], newGreaterXSlice = newXSlices[2];
		boolean itsY0PositionsTurnToTopple = x%2 == 0 == itsEvenPositionsTurnToTopple;
		for (; x < maxX; xMinusOne = x, x = xPlusOne, xPlusOne = xPlusTwo, xPlusTwo++, itsY0PositionsTurnToTopple = !itsY0PositionsTurnToTopple) {
			// y = 0;
			boolean itsCurrentPositionsTurnToTopple = itsY0PositionsTurnToTopple;
			smallerXSlice = currentXSlice;
			currentXSlice = greaterXSlice;
			greaterXSlice = sourceGrid[xPlusOne];
			newSmallerXSlice = newCurrentXSlice;
			newCurrentXSlice = newGreaterXSlice;
			newGreaterXSlice = new long[xPlusTwo];
			boolean[] newCurrentXSliceCompliance = grid[x];
			newSourceGrid[xPlusOne] = newGreaterXSlice;
			newXSlices[0] = newSmallerXSlice;
			newXSlices[1] = newCurrentXSlice;
			newXSlices[2] = newGreaterXSlice;
			int relevantAsymmetricNeighborCount = 0;
			int relevantNeighborCount = 0;
			long currentValue = currentXSlice[0];
			long greaterYNeighborValue = currentXSlice[1];
			long smallerXNeighborValue = smallerXSlice[0];
			long greaterXNeighborValue = greaterXSlice[0];
			if (smallerXNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 0;
				nc[1] = 0;
				relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
				relevantNeighborCount++;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterXNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 2;
				nc[1] = 0;
				relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 1;
				relevantNeighborCount++;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterYNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 1;
				nc[1] = 1;
				relevantAsymmetricNeighborSymmetryCounts[relevantAsymmetricNeighborCount] = 2;
				relevantNeighborCount += 2;
				relevantAsymmetricNeighborCount++;
			}
			if (topplePosition(newXSlices, currentValue, 0, relevantAsymmetricNeighborValues, 
					relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborSymmetryCounts, relevantNeighborCount, relevantAsymmetricNeighborCount, sortedNeighborsIndexes)) {
				anyToppled = true;
				newCurrentXSliceCompliance[0] = itsCurrentPositionsTurnToTopple;
			} else {
				newCurrentXSliceCompliance[0] = !itsCurrentPositionsTurnToTopple;
			}
			// y = 1
			itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
			relevantAsymmetricNeighborCount = 0;
			// reuse values obtained previously
			long smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterYNeighborValue = currentXSlice[2];
			smallerXNeighborValue = smallerXSlice[1];
			greaterXNeighborValue = greaterXSlice[1];
			if (smallerXNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 0;
				nc[1] = 1;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterXNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 2;
				nc[1] = 1;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
				relevantAsymmetricNeighborCount++;
			}
			if (smallerYNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 1;
				nc[1] = 0;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterYNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 1;
				nc[1] = 2;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
				relevantAsymmetricNeighborCount++;
			}
			if (topplePosition(newXSlices, currentValue, 1, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborCount, sortedNeighborsIndexes)) {
				anyToppled = true;
				newCurrentXSliceCompliance[1] = itsCurrentPositionsTurnToTopple;
			} else {
				newCurrentXSliceCompliance[1] = !itsCurrentPositionsTurnToTopple;
			}
			// 2 >= y < x - 1
			int y = 2, yMinusOne = 1, yPlusOne = 3;
			for (; y < xMinusOne; yMinusOne = y, y = yPlusOne, yPlusOne++) {
				itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
				relevantAsymmetricNeighborCount = 0;
				// reuse values obtained previously
				smallerYNeighborValue = currentValue;
				currentValue = greaterYNeighborValue;
				greaterYNeighborValue = currentXSlice[yPlusOne];
				smallerXNeighborValue = smallerXSlice[y];
				greaterXNeighborValue = greaterXSlice[y];
				if (smallerXNeighborValue < currentValue) {
					relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
					int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
					nc[0] = 0;
					nc[1] = y;
					relevantAsymmetricNeighborCount++;
				}
				if (greaterXNeighborValue < currentValue) {
					relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
					int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
					nc[0] = 2;
					nc[1] = y;
					relevantAsymmetricNeighborCount++;
				}
				if (smallerYNeighborValue < currentValue) {
					relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
					int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
					nc[0] = 1;
					nc[1] = yMinusOne;
					relevantAsymmetricNeighborCount++;
				}
				if (greaterYNeighborValue < currentValue) {
					relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
					int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
					nc[0] = 1;
					nc[1] = yPlusOne;
					relevantAsymmetricNeighborCount++;
				}
				if (topplePosition(newXSlices, currentValue, y, relevantAsymmetricNeighborValues, 
						relevantAsymmetricNeighborCoords, relevantAsymmetricNeighborCount, sortedNeighborsIndexes)) {
					anyToppled = true;
					newCurrentXSliceCompliance[y] = itsCurrentPositionsTurnToTopple;
				} else {
					newCurrentXSliceCompliance[y] = !itsCurrentPositionsTurnToTopple;
				}
			}
			// y = x - 1
			itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
			relevantAsymmetricNeighborCount = 0;
			// reuse values obtained previously
			smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterYNeighborValue = currentXSlice[yPlusOne];
			smallerXNeighborValue = smallerXSlice[y];
			greaterXNeighborValue = greaterXSlice[y];
			if (smallerXNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 0;
				nc[1] = y;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterXNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterXNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 2;
				nc[1] = y;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
				relevantAsymmetricNeighborCount++;
			}
			if (smallerYNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = smallerYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 1;
				nc[1] = yMinusOne;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 1;
				relevantAsymmetricNeighborCount++;
			}
			if (greaterYNeighborValue < currentValue) {
				relevantAsymmetricNeighborValues[relevantAsymmetricNeighborCount] = greaterYNeighborValue;
				int[] nc = relevantAsymmetricNeighborCoords[relevantAsymmetricNeighborCount];
				nc[0] = 1;
				nc[1] = yPlusOne;
				relevantAsymmetricNeighborShareMultipliers[relevantAsymmetricNeighborCount] = 2;
				relevantAsymmetricNeighborCount++;
			}
			if (topplePosition(newXSlices, currentValue, y, relevantAsymmetricNeighborValues, relevantAsymmetricNeighborCoords, 
					relevantAsymmetricNeighborShareMultipliers, relevantAsymmetricNeighborCount, sortedNeighborsIndexes)) {
				anyToppled = true;
				newCurrentXSliceCompliance[y] = itsCurrentPositionsTurnToTopple;
			} else {
				newCurrentXSliceCompliance[y] = !itsCurrentPositionsTurnToTopple;
			}
			// y = x
			yMinusOne = y;
			y = x;
			itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
			// reuse values obtained previously
			smallerYNeighborValue = currentValue;
			currentValue = greaterYNeighborValue;
			greaterXNeighborValue = greaterXSlice[y];
			boolean toppled = false;
			if (smallerYNeighborValue < currentValue) {
				if (greaterXNeighborValue < currentValue) {
					if (smallerYNeighborValue == greaterXNeighborValue) {
						// gx = sy < current
						long toShare = currentValue - greaterXNeighborValue; 
						long share = toShare/5;
						if (share != 0) {
							anyToppled = true;
							toppled = true;
						}
						newCurrentXSlice[yMinusOne] += share;
						newCurrentXSlice[y] += currentValue - toShare + share + toShare%5;
						newGreaterXSlice[y] += share;
					} else if (smallerYNeighborValue < greaterXNeighborValue) {
						// sy < gx < current
						long toShare = currentValue - greaterXNeighborValue; 
						long share = toShare/5;
						if (share != 0) {
							anyToppled = true;
							toppled = true;
						}
						newCurrentXSlice[yMinusOne] += share;
						newGreaterXSlice[y] += share;
						long currentRemainingValue = currentValue - 4*share;
						toShare = currentRemainingValue - smallerYNeighborValue; 
						share = toShare/3;
						if (share != 0) {
							anyToppled = true;
							toppled = true;
						}
						newCurrentXSlice[yMinusOne] += share;
						newCurrentXSlice[y] += currentRemainingValue - toShare + share + toShare%3;
					} else {
						// gx < sy < current
						long toShare = currentValue - smallerYNeighborValue; 
						long share = toShare/5;
						if (share != 0) {
							anyToppled = true;
							toppled = true;
						}
						newCurrentXSlice[yMinusOne] += share;
						newGreaterXSlice[y] += share;
						long currentRemainingValue = currentValue - 4*share;
						toShare = currentRemainingValue - greaterXNeighborValue; 
						share = toShare/3;
						if (share != 0) {
							anyToppled = true;
							toppled = true;
						}
						newCurrentXSlice[y] += currentRemainingValue - toShare + share + toShare%3;
						newGreaterXSlice[y] += share;
					}
				} else {
					// sy < current <= gx
					long toShare = currentValue - smallerYNeighborValue; 
					long share = toShare/3;
					if (share != 0) {
						anyToppled = true;
						toppled = true;
					}
					newCurrentXSlice[yMinusOne] += share;
					newCurrentXSlice[y] += currentValue - toShare + share + toShare%3;
				}
			} else if (greaterXNeighborValue < currentValue) {
				// gx < current <= sy
				long toShare = currentValue - greaterXNeighborValue; 
				long share = toShare/3;
				if (share != 0) {
					anyToppled = true;
					toppled = true;
				}
				newCurrentXSlice[y] += currentValue - toShare + share + toShare%3;
				newGreaterXSlice[y] += share;
			} else {
				// gx >= current <= sy
				newCurrentXSlice[y] += currentValue;
			}
			newCurrentXSliceCompliance[y] = toppled == itsCurrentPositionsTurnToTopple;
			sourceGrid[xMinusOne] = null;
		}
		xSlices[1] = currentXSlice;
		xSlices[2] = greaterXSlice;
		newXSlices[1] = newCurrentXSlice;
		newXSlices[2] = newGreaterXSlice;
		return anyToppled;
	}
	
	private static boolean topplePosition(long[][] newXSlices, long value, int y, long[] neighborValues,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount, int[] sortedNeighborsIndexes) {
		boolean toppled = false;
		switch (neighborCount) {
			case 4:
				Utils.sortDescendingLength4(neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, neighborValues, 
						neighborCoords, neighborShareMultipliers, neighborCount, sortedNeighborsIndexes);
				break;
			case 3:
				Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, neighborValues, 
						neighborCoords, neighborShareMultipliers, neighborCount, sortedNeighborsIndexes);
				break;
			case 2:
				long n0Val = neighborValues[0], n1Val = neighborValues[1];
				int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
				int n0Mult = neighborShareMultipliers[0], n1Mult = neighborShareMultipliers[1];
				int shareCount = 3;
				if (n0Val == n1Val) {
					// n0Val = n1Val < value
					long toShare = value - n0Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] += share*n0Mult;
					newXSlices[n1Coords[0]][n1Coords[1]] += share*n1Mult;
					newXSlices[1][y] += value - toShare + share + toShare%shareCount;
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					long toShare = value - n1Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] += share*n0Mult;
					newXSlices[n1Coords[0]][n1Coords[1]] += share*n1Mult;
					shareCount = 2;
					long currentRemainingValue = value - 2*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] += share*n0Mult;
					newXSlices[1][y] += currentRemainingValue - toShare + share + toShare%shareCount;
				} else {
					// n1Val < n0Val < value
					long toShare = value - n0Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] += share*n0Mult;
					newXSlices[n1Coords[0]][n1Coords[1]] += share*n1Mult;
					shareCount = 2;
					long currentRemainingValue = value - 2*share;
					toShare = currentRemainingValue - n1Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n1Coords[0]][n1Coords[1]] += share*n1Mult;
					newXSlices[1][y] += currentRemainingValue - toShare + share + toShare%shareCount;
				}
				break;
			case 1:
				long toShare = value - neighborValues[0];
				long share = toShare/2;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%2 + share;
					int[] nc = neighborCoords[0];
					newXSlices[nc[0]][nc[1]] += share * neighborShareMultipliers[0];
				}
				// no break
			default: // 0
				newXSlices[1][y] += value;
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(long[][] newXSlices, long value, int y, long[] neighborValues,
			int[][] neighborCoords, int[] neighborShareMultipliers, int neighborCount, int[] sortedNeighborsIndexes) {
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
				newXSlices[nc[0]][nc[1]] += share * neighborShareMultipliers[sortedNeighborsIndexes[j]];
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
						newXSlices[nc[0]][nc[1]] += share * neighborShareMultipliers[sortedNeighborsIndexes[j]];
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newXSlices[1][y] += value;
		return toppled;
	}
	
	private static boolean topplePosition(long[][] newXSlices, long value, int y, long[] neighborValues,
			int[][] neighborCoords, int neighborCount, int[] sortedNeighborsIndexes) {
		boolean toppled = false;
		switch (neighborCount) {
			case 4:
				Utils.sortDescendingLength4(neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, neighborValues, 
						neighborCoords, neighborCount, sortedNeighborsIndexes);
				break;
			case 3:
				Utils.sortDescendingLength3(neighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, neighborValues, 
						neighborCoords, neighborCount, sortedNeighborsIndexes);
				break;
			case 2:
				long n0Val = neighborValues[0], n1Val = neighborValues[1];
				int[] n0Coords = neighborCoords[0], n1Coords = neighborCoords[1];
				int shareCount = 3;
				if (n0Val == n1Val) {
					// n0Val = n1Val < value
					long toShare = value - n0Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] += share;
					newXSlices[n1Coords[0]][n1Coords[1]] += share;
					newXSlices[1][y] += value - toShare + share + toShare%shareCount;
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					long toShare = value - n1Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] += share;
					newXSlices[n1Coords[0]][n1Coords[1]] += share;
					shareCount = 2;
					long currentRemainingValue = value - 2*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] += share;
					newXSlices[1][y] += currentRemainingValue - toShare + share + toShare%shareCount;
				} else {
					// n1Val < n0Val < value
					long toShare = value - n0Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] += share;
					newXSlices[n1Coords[0]][n1Coords[1]] += share;
					shareCount = 2;
					long currentRemainingValue = value - 2*share;
					toShare = currentRemainingValue - n1Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n1Coords[0]][n1Coords[1]] += share;
					newXSlices[1][y] += currentRemainingValue - toShare + share + toShare%shareCount;
				}
				break;
			case 1:
				long toShare = value - neighborValues[0];
				long share = toShare/2;
				if (share != 0) {
					toppled = true;
					value = value - toShare + toShare%2 + share;
					int[] nc = neighborCoords[0];
					newXSlices[nc[0]][nc[1]] += share;
				}
				// no break
			default: // 0
				newXSlices[1][y] += value;
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(long[][] newXSlices, long value, int y, long[] neighborValues,
			int[][] neighborCoords, int neighborCount, int[] sortedNeighborsIndexes) {
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
				newXSlices[nc[0]][nc[1]] += share;
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
						newXSlices[nc[0]][nc[1]] += share;
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount--;
		}
		newXSlices[1][y] += value;
		return toppled;
	}
	
	private static boolean topplePosition(long[][] newXSlices, long value, int y, long[] asymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, int neighborCount, int asymmetricNeighborCount, int[] sortedNeighborsIndexes) {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
			case 4:
				Utils.sortDescendingLength4(asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, asymmetricNeighborValues, 
						asymmetricNeighborCoords, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount, sortedNeighborsIndexes);
				break;
			case 3:
				Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, asymmetricNeighborValues, 
						asymmetricNeighborCoords, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount, sortedNeighborsIndexes);
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
					newXSlices[n0Coords[0]][n0Coords[1]] += share;
					newXSlices[n1Coords[0]][n1Coords[1]] += share;
					newXSlices[1][y] += value - toShare + share + toShare%shareCount;
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					long toShare = value - n1Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] += share;
					newXSlices[n1Coords[0]][n1Coords[1]] += share;
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] += share;
					newXSlices[1][y] += currentRemainingValue - toShare + share + toShare%shareCount;
				} else {
					// n1Val < n0Val < value
					long toShare = value - n0Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] += share;
					newXSlices[n1Coords[0]][n1Coords[1]] += share;
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n1Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n1Coords[0]][n1Coords[1]] += share;
					newXSlices[1][y] += currentRemainingValue - toShare + share + toShare%shareCount;
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
					newXSlices[nc[0]][nc[1]] += share;
				}
				// no break
			default: // 0
				newXSlices[1][y] += value;
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(long[][] newXSlices, long value, int y, long[] asymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborSymmetryCounts, int neighborCount, int asymmetricNeighborCount, int[] sortedNeighborsIndexes) {
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
				newXSlices[nc[0]][nc[1]] += share;
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
						newXSlices[nc[0]][nc[1]] += share;
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		newXSlices[1][y] += value;
		return toppled;
	}
	
	private static boolean topplePosition(long[][] newXSlices, long value, int y, long[] asymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount, int[] sortedNeighborsIndexes) {
		boolean toppled = false;
		switch (asymmetricNeighborCount) {
			case 4:
				Utils.sortDescendingLength4(asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, asymmetricNeighborValues, 
						asymmetricNeighborCoords, asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount, sortedNeighborsIndexes);
				break;
			case 3:
				Utils.sortDescendingLength3(asymmetricNeighborValues, sortedNeighborsIndexes);
				toppled = topplePositionSortedNeighbors(newXSlices, value, y, asymmetricNeighborValues, 
						asymmetricNeighborCoords, asymmetricNeighborShareMultipliers, asymmetricNeighborSymmetryCounts, neighborCount, asymmetricNeighborCount, sortedNeighborsIndexes);
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
					newXSlices[n0Coords[0]][n0Coords[1]] += share*n0Mult;
					newXSlices[n1Coords[0]][n1Coords[1]] += share*n1Mult;
					newXSlices[1][y] += value - toShare + share + toShare%shareCount;
				} else if (n0Val < n1Val) {
					// n0Val < n1Val < value
					long toShare = value - n1Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] += share*n0Mult;
					newXSlices[n1Coords[0]][n1Coords[1]] += share*n1Mult;
					shareCount -= asymmetricNeighborSymmetryCounts[1];
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n0Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] += share*n0Mult;
					newXSlices[1][y] += currentRemainingValue - toShare + share + toShare%shareCount;
				} else {
					// n1Val < n0Val < value
					long toShare = value - n0Val; 
					long share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n0Coords[0]][n0Coords[1]] += share*n0Mult;
					newXSlices[n1Coords[0]][n1Coords[1]] += share*n1Mult;
					shareCount -= asymmetricNeighborSymmetryCounts[0];
					long currentRemainingValue = value - neighborCount*share;
					toShare = currentRemainingValue - n1Val;
					share = toShare/shareCount;
					if (share != 0) {
						toppled = true;
					}
					newXSlices[n1Coords[0]][n1Coords[1]] += share*n1Mult;
					newXSlices[1][y] += currentRemainingValue - toShare + share + toShare%shareCount;
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
					newXSlices[nc[0]][nc[1]] += share * asymmetricNeighborShareMultipliers[0];
				}
				// no break
			default: // 0
				newXSlices[1][y] += value;
		}
		return toppled;
	}
	
	private static boolean topplePositionSortedNeighbors(long[][] newXSlices, long value, int y, long[] asymmetricNeighborValues,
			int[][] asymmetricNeighborCoords, int[] asymmetricNeighborShareMultipliers, int[] asymmetricNeighborSymmetryCounts, 
			int neighborCount, int asymmetricNeighborCount, int[] sortedNeighborsIndexes) {
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
				newXSlices[nc[0]][nc[1]] += share * asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]];
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
						newXSlices[nc[0]][nc[1]] += share * asymmetricNeighborShareMultipliers[sortedNeighborsIndexes[j]];
					}
				}
				previousNeighborValue = neighborValue;
			}
			shareCount -= asymmetricNeighborSymmetryCounts[sortedNeighborsIndexes[i]];
		}
		newXSlices[1][y] += value;
		return toppled;
	}

	@Override
	public int getSize() {
		return maxX;
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
	public long getStep() {
		return step;
	}

	@Override
	public String getName() {
		return "Aether";
	}

	@Override
	public String getWholeGridSubfolderPath() {
		return getName() + "/2D/" + initialValue + "/toppling_alternation_compliance";
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
		data.put(SerializableModelData.GRID_DIMENSION, 2);
		data.put(SerializableModelData.GRID_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_LONG_PRIMITIVE_ARRAY_1);
		data.put(SerializableModelData.COORDINATE_BOUNDS, maxX);
		data.put(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE, SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER);
		data.put(SerializableModelData.STEP, step);
		data.put(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP, changed);
		data.put(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE, grid);
		data.put(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BOOLEAN_PRIMITIVE_ARRAY_1);
		Utils.serializeToFile(data, backupPath, backupName);
	}
}
