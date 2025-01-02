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
import cellularautomata.model1d.IsotropicBooleanArrayModel1DA;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 1D with a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class LongAetherTopplingAlternationCompliance1D extends IsotropicBooleanArrayModel1DA {
	
	public static final long MAX_INITIAL_VALUE = Long.MAX_VALUE;
	public static final long MIN_INITIAL_VALUE = -9223372036854775807L;

	/** A 1D array representing the grid */
	private long[] sourceGrid;
	
	private boolean itsEvenPositionsTurnToTopple;
	
	private final long initialValue;
	private long step;
	private int maxX;
	private Boolean changed = null;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public LongAetherTopplingAlternationCompliance1D(long initialValue) {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of long type
			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
	    }
		this.initialValue = initialValue;
		itsEvenPositionsTurnToTopple = initialValue >= 0;
		sourceGrid = new long[5];
		sourceGrid[0] = initialValue;
		maxX = 2;
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
	public LongAetherTopplingAlternationCompliance1D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		SerializableModelData data = (SerializableModelData) Utils.deserializeFromFile(backupPath);
		data = SerializableModelData.updateDataFormat(data);
		if (!SerializableModelData.Models.AETHER.equals(data.get(SerializableModelData.MODEL))) {
			throw new IllegalArgumentException("The backup file contains a different model.");
		}
		if (!SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_TYPE))
				|| !SerializableModelData.InitialConfigurationImplementationTypes.LONG.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.GridTypes.INFINITE_REGULAR.equals(data.get(SerializableModelData.GRID_TYPE))
				|| !Integer.valueOf(1).equals(data.get(SerializableModelData.GRID_DIMENSION))
				|| !SerializableModelData.GridImplementationTypes.ANYSOTROPIC_LONG_PRIMITIVE_ARRAY_1.equals(data.get(SerializableModelData.GRID_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER.equals(data.get(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE))
				|| !data.contains(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP)) {
			throw new IllegalArgumentException("The backup file's configuration is not compatible with this class.");
		}
		initialValue = (long) data.get(SerializableModelData.INITIAL_CONFIGURATION);
		sourceGrid = (long[]) data.get(SerializableModelData.GRID);
		maxX = (int) data.get(SerializableModelData.COORDINATE_BOUNDS);
		step = (long) data.get(SerializableModelData.STEP);
		changed = (Boolean) data.get(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP);
		itsEvenPositionsTurnToTopple = initialValue >= 0 == (step%2 == 0);
		if (SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BOOLEAN_PRIMITIVE_ARRAY_1.equals(data.get(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE_IMPLEMENTATION_TYPE))) {
			grid = (boolean[]) data.get(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE);
		} else {
			nextStep();
		}
	}
	
	@Override
	public Boolean nextStep() {
		final int newSide = maxX + 3;
		long[] newSourceGrid = new long[newSide];
		grid = null;
		grid = new boolean[newSide];
		boolean changed = false;
		long currentValue, greaterXNeighborValue, smallerXNeighborValue;
		//x = 0
		boolean itsCurrentPositionsTurnToTopple = itsEvenPositionsTurnToTopple;
		currentValue = sourceGrid[0];
		greaterXNeighborValue = sourceGrid[1];
		boolean toppled = false;
		if (greaterXNeighborValue < currentValue) {
			long toShare = currentValue - greaterXNeighborValue;
			long share = toShare/3;
			if (share != 0) {
				changed = true;
				toppled = true;
				newSourceGrid[0] += currentValue - toShare + share + toShare%3;
				newSourceGrid[1] += share;
			} else {
				newSourceGrid[0] += currentValue;
			}			
		} else {
			newSourceGrid[0] += currentValue;
		}
		grid[0] = toppled == itsCurrentPositionsTurnToTopple;
		//x = 1
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		//reuse values obtained previously
		smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterXNeighborValue = sourceGrid[2];
		toppled = false;
		if (smallerXNeighborValue < currentValue) {
			if (greaterXNeighborValue < currentValue) {
				if (smallerXNeighborValue == greaterXNeighborValue) {
					// gn == sn < current
					long toShare = currentValue - greaterXNeighborValue; 
					long share = toShare/3;
					if (share != 0) {
						changed = true;
						toppled = true;
					}
					newSourceGrid[0] += share + share;//one more for the symmetric position at the other side
					newSourceGrid[1] += currentValue - toShare + share + toShare%3;
					newSourceGrid[2] += share;
				} else if (smallerXNeighborValue < greaterXNeighborValue) {
					// sn < gn < current
					long toShare = currentValue - greaterXNeighborValue; 
					long share = toShare/3;
					if (share != 0) {
						changed = true;
						toppled = true;
					}
					newSourceGrid[0] += share + share;//one more for the symmetric position at the other side
					newSourceGrid[2] += share;
					long currentRemainingValue = currentValue - share - share;
					toShare = currentRemainingValue - smallerXNeighborValue; 
					share = toShare/2;
					if (share != 0) {
						changed = true;
						toppled = true;
					}
					newSourceGrid[0] += share + share;//one more for the symmetric position at the other side
					newSourceGrid[1] += currentRemainingValue - toShare + share + toShare%2;
				} else {
					// gn < sn < current
					long toShare = currentValue - smallerXNeighborValue; 
					long share = toShare/3;
					if (share != 0) {
						changed = true;
						toppled = true;
					}
					newSourceGrid[0] += share + share;//one more for the symmetric position at the other side
					newSourceGrid[2] += share;
					long currentRemainingValue = currentValue - share - share;
					toShare = currentRemainingValue - greaterXNeighborValue; 
					share = toShare/2;
					if (share != 0) {
						changed = true;
						toppled = true;
					}
					newSourceGrid[1] += currentRemainingValue - toShare + share + toShare%2;
					newSourceGrid[2] += share;
				}
			} else {
				// sn < current <= gn
				long toShare = currentValue - smallerXNeighborValue; 
				long share = toShare/2;
				if (share != 0) {
					changed = true;
					toppled = true;
				}
				newSourceGrid[0] += share + share;//one more for the symmetric position at the other side
				newSourceGrid[1] += currentValue - toShare + share + toShare%2;
			}
		} else if (greaterXNeighborValue < currentValue) {
			// gn < current <= sn
			long toShare = currentValue - greaterXNeighborValue; 
			long share = toShare/2;
			if (share != 0) {
				changed = true;
				toppled = true;
			}
			newSourceGrid[1] += currentValue - toShare + share + toShare%2;
			newSourceGrid[2] += share;
		} else {
			// gn >= current <= sn
			newSourceGrid[1] += currentValue;
		}
		grid[1] = toppled == itsCurrentPositionsTurnToTopple;
		//2 <= x < edge - 2
		int edge = sourceGrid.length - 1;
		int edgeMinusTwo = edge - 2;
		if (toppleRangeBeyondX1(newSourceGrid, 2, edgeMinusTwo)) {
			changed = true;
		}
		//edge - 2 <= x < edge
		if (toppleRangeBeyondX1(newSourceGrid, edgeMinusTwo, edge)) {
			changed = true;
			maxX++;
		}
		grid[edge] = edge%2 == 0 != itsEvenPositionsTurnToTopple;
		sourceGrid = newSourceGrid;
		step++;
		itsEvenPositionsTurnToTopple = !itsEvenPositionsTurnToTopple;
		this.changed = changed;
		return changed;
	}

	@Override
	public Boolean isChanged() {
		return changed;
	}
	
	private boolean toppleRangeBeyondX1(long[] newSourceGrid, int minX, int maxX) {
		boolean anyToppled = false;
		int x = minX, xMinusOne = x - 1, xPlusOne = x + 1;
		long smallerXNeighborValue, currentValue = sourceGrid[xMinusOne], greaterXNeighborValue = sourceGrid[x];
		boolean itsCurrentPositionsTurnToTopple = x%2 == 0 == itsEvenPositionsTurnToTopple;
		for (; x < maxX; xMinusOne = x, x = xPlusOne, xPlusOne++, itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple) {
			//reuse values obtained previously
			smallerXNeighborValue = currentValue;
			currentValue = greaterXNeighborValue;
			greaterXNeighborValue = sourceGrid[xPlusOne];
			boolean toppled = false;
			if (smallerXNeighborValue < currentValue) {
				if (greaterXNeighborValue < currentValue) {
					if (smallerXNeighborValue == greaterXNeighborValue) {
						// gn == sn < current
						long toShare = currentValue - greaterXNeighborValue; 
						long share = toShare/3;
						if (share != 0) {
							anyToppled = true;
							toppled = true;
						}
						newSourceGrid[xMinusOne] += share;
						newSourceGrid[x] += currentValue - toShare + share + toShare%3;
						newSourceGrid[xPlusOne] += share;
					} else if (smallerXNeighborValue < greaterXNeighborValue) {
						// sn < gn < current
						long toShare = currentValue - greaterXNeighborValue; 
						long share = toShare/3;
						if (share != 0) {
							anyToppled = true;
							toppled = true;
						}
						newSourceGrid[xMinusOne] += share;
						newSourceGrid[xPlusOne] += share;
						long currentRemainingValue = currentValue - share - share;
						toShare = currentRemainingValue - smallerXNeighborValue; 
						share = toShare/2;
						if (share != 0) {
							anyToppled = true;
							toppled = true;
						}
						newSourceGrid[xMinusOne] += share;
						newSourceGrid[x] += currentRemainingValue - toShare + share + toShare%2;
					} else {
						// gn < sn < current
						long toShare = currentValue - smallerXNeighborValue; 
						long share = toShare/3;
						if (share != 0) {
							anyToppled = true;
							toppled = true;
						}
						newSourceGrid[xMinusOne] += share;
						newSourceGrid[xPlusOne] += share;
						long currentRemainingValue = currentValue - share - share;
						toShare = currentRemainingValue - greaterXNeighborValue; 
						share = toShare/2;
						if (share != 0) {
							anyToppled = true;
							toppled = true;
						}
						newSourceGrid[x] += currentRemainingValue - toShare + share + toShare%2;
						newSourceGrid[xPlusOne] += share;
					}
				} else {
					// sn < current <= gn
					long toShare = currentValue - smallerXNeighborValue; 
					long share = toShare/2;
					if (share != 0) {
						anyToppled = true;
						toppled = true;
					}
					newSourceGrid[xMinusOne] += share;
					newSourceGrid[x] += currentValue - toShare + share + toShare%2;
				}
			} else if (greaterXNeighborValue < currentValue) {
				// gn < current <= sn
				long toShare = currentValue - greaterXNeighborValue; 
				long share = toShare/2;
				if (share != 0) {
					anyToppled = true;
					toppled = true;
				}
				newSourceGrid[x] += currentValue - toShare + share + toShare%2;
				newSourceGrid[xPlusOne] += share;
			} else {
				// gn >= current <= sn
				newSourceGrid[x] += currentValue;
			}
			grid[x] = toppled == itsCurrentPositionsTurnToTopple;
		}
		return anyToppled;
	}

	@Override
	public int getAsymmetricMaxX() {
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
	public String getSubfolderPath() {
		return getName() + "/1D/" + initialValue + "/toppling_alternation_compliance";
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
		data.put(SerializableModelData.GRID_DIMENSION, 1);
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
