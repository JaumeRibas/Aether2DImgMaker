/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2024 Jaume Ribas

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
import cellularautomata.model1d.IsotropicModel1DA;
import cellularautomata.model1d.SymmetricBooleanModel1D;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 1D with a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class LongAether1DTopplingAlternationCompliance implements SymmetricBooleanModel1D, IsotropicModel1DA {
	
	public static final long MAX_INITIAL_VALUE = Long.MAX_VALUE;
	public static final long MIN_INITIAL_VALUE = -9223372036854775807L;

	/** A 1D array representing the grid */
	private long[] grid;
	
	private boolean[] topplingAlternationCompliance;
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
	public LongAether1DTopplingAlternationCompliance(long initialValue) {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of long type
			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
	    }
		this.initialValue = initialValue;
		itsEvenPositionsTurnToTopple = initialValue >= 0;
		grid = new long[5];
		grid[0] = initialValue;
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
	public LongAether1DTopplingAlternationCompliance(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		SerializableModelData data = (SerializableModelData) Utils.deserializeFromFile(backupPath);
		if (!SerializableModelData.Models.AETHER.equals(data.get(SerializableModelData.MODEL))) {
			throw new IllegalArgumentException("The backup file contains a different model.");
		}
		if (!SerializableModelData.InitialConfigurationTypes.SINGLE_SOURCE_AT_ORIGIN.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_TYPE))
				|| !SerializableModelData.InitialConfigurationImplementationTypes.LONG.equals(data.get(SerializableModelData.INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.GridTypes.INFINITE_1D.equals(data.get(SerializableModelData.GRID_TYPE))
				|| !SerializableModelData.GridImplementationTypes.ANYSOTROPIC_LONG_PRIMITIVE_ARRAY_1.equals(data.get(SerializableModelData.GRID_IMPLEMENTATION_TYPE))
				|| !SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER.equals(data.get(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE))
				|| !data.contains(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP)) {
			throw new IllegalArgumentException("The backup file's configuration is not compatible with the " + LongAether1DTopplingAlternationCompliance.class + " class.");
		}
		initialValue = (long) data.get(SerializableModelData.INITIAL_CONFIGURATION);
		grid = (long[]) data.get(SerializableModelData.GRID);
		maxX = (int) data.get(SerializableModelData.COORDINATE_BOUNDS);
		step = (long) data.get(SerializableModelData.STEP);
		changed = (Boolean) data.get(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP);
		itsEvenPositionsTurnToTopple = initialValue >= 0 == (step%2 == 0);
		if (SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BOOLEAN_PRIMITIVE_ARRAY_1.equals(data.get(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE_IMPLEMENTATION_TYPE))) {
			topplingAlternationCompliance = (boolean[]) data.get(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE);
		} else {
			nextStep();
		}
	}
	
	@Override
	public Boolean nextStep() {
		final int newSide = maxX + 3;
		long[] newGrid = new long[newSide];
		topplingAlternationCompliance = null;
		topplingAlternationCompliance = new boolean[newSide];
		boolean changed = false;
		long currentValue, greaterXNeighborValue, smallerXNeighborValue;
		//x = 0
		boolean itsCurrentPositionsTurnToTopple = itsEvenPositionsTurnToTopple;
		currentValue = grid[0];
		greaterXNeighborValue = grid[1];
		boolean toppled = false;
		if (greaterXNeighborValue < currentValue) {
			long toShare = currentValue - greaterXNeighborValue;
			long share = toShare/3;
			if (share != 0) {
				changed = true;
				toppled = true;
				newGrid[0] += currentValue - toShare + share + toShare%3;
				newGrid[1] += share;
			} else {
				newGrid[0] += currentValue;
			}			
		} else {
			newGrid[0] += currentValue;
		}
		topplingAlternationCompliance[0] = toppled == itsCurrentPositionsTurnToTopple;
		//x = 1
		itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple;
		//reuse values obtained previously
		smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterXNeighborValue = grid[2];
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
					newGrid[0] += share + share;//one more for the symmetric position at the other side
					newGrid[1] += currentValue - toShare + share + toShare%3;
					newGrid[2] += share;
				} else if (smallerXNeighborValue < greaterXNeighborValue) {
					// sn < gn < current
					long toShare = currentValue - greaterXNeighborValue; 
					long share = toShare/3;
					if (share != 0) {
						changed = true;
						toppled = true;
					}
					newGrid[0] += share + share;//one more for the symmetric position at the other side
					newGrid[2] += share;
					long currentRemainingValue = currentValue - share - share;
					toShare = currentRemainingValue - smallerXNeighborValue; 
					share = toShare/2;
					if (share != 0) {
						changed = true;
						toppled = true;
					}
					newGrid[0] += share + share;//one more for the symmetric position at the other side
					newGrid[1] += currentRemainingValue - toShare + share + toShare%2;
				} else {
					// gn < sn < current
					long toShare = currentValue - smallerXNeighborValue; 
					long share = toShare/3;
					if (share != 0) {
						changed = true;
						toppled = true;
					}
					newGrid[0] += share + share;//one more for the symmetric position at the other side
					newGrid[2] += share;
					long currentRemainingValue = currentValue - share - share;
					toShare = currentRemainingValue - greaterXNeighborValue; 
					share = toShare/2;
					if (share != 0) {
						changed = true;
						toppled = true;
					}
					newGrid[1] += currentRemainingValue - toShare + share + toShare%2;
					newGrid[2] += share;
				}
			} else {
				// sn < current <= gn
				long toShare = currentValue - smallerXNeighborValue; 
				long share = toShare/2;
				if (share != 0) {
					changed = true;
					toppled = true;
				}
				newGrid[0] += share + share;//one more for the symmetric position at the other side
				newGrid[1] += currentValue - toShare + share + toShare%2;
			}
		} else if (greaterXNeighborValue < currentValue) {
			// gn < current <= sn
			long toShare = currentValue - greaterXNeighborValue; 
			long share = toShare/2;
			if (share != 0) {
				changed = true;
				toppled = true;
			}
			newGrid[1] += currentValue - toShare + share + toShare%2;
			newGrid[2] += share;
		} else {
			// gn >= current <= sn
			newGrid[1] += currentValue;
		}
		topplingAlternationCompliance[1] = toppled == itsCurrentPositionsTurnToTopple;
		//2 <= x < edge - 2
		int edge = grid.length - 1;
		int edgeMinusTwo = edge - 2;
		if (toppleRangeBeyondX1(newGrid, 2, edgeMinusTwo)) {
			changed = true;
		}
		//edge - 2 <= x < edge
		if (toppleRangeBeyondX1(newGrid, edgeMinusTwo, edge)) {
			changed = true;
			maxX++;
		}
		topplingAlternationCompliance[edge] = edge%2 == 0 != itsEvenPositionsTurnToTopple;
		grid = newGrid;
		step++;
		itsEvenPositionsTurnToTopple = !itsEvenPositionsTurnToTopple;
		this.changed = changed;
		return changed;
	}

	@Override
	public Boolean isChanged() {
		return changed;
	}
	
	private boolean toppleRangeBeyondX1(long[] newGrid, int minX, int maxX) {
		boolean anyToppled = false;
		int x = minX, xMinusOne = x - 1, xPlusOne = x + 1;
		long smallerXNeighborValue, currentValue = grid[xMinusOne], greaterXNeighborValue = grid[x];
		boolean itsCurrentPositionsTurnToTopple = x%2 == 0 == itsEvenPositionsTurnToTopple;
		for (; x < maxX; xMinusOne = x, x = xPlusOne, xPlusOne++, itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple) {
			//reuse values obtained previously
			smallerXNeighborValue = currentValue;
			currentValue = greaterXNeighborValue;
			greaterXNeighborValue = grid[xPlusOne];
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
						newGrid[xMinusOne] += share;
						newGrid[x] += currentValue - toShare + share + toShare%3;
						newGrid[xPlusOne] += share;
					} else if (smallerXNeighborValue < greaterXNeighborValue) {
						// sn < gn < current
						long toShare = currentValue - greaterXNeighborValue; 
						long share = toShare/3;
						if (share != 0) {
							anyToppled = true;
							toppled = true;
						}
						newGrid[xMinusOne] += share;
						newGrid[xPlusOne] += share;
						long currentRemainingValue = currentValue - share - share;
						toShare = currentRemainingValue - smallerXNeighborValue; 
						share = toShare/2;
						if (share != 0) {
							anyToppled = true;
							toppled = true;
						}
						newGrid[xMinusOne] += share;
						newGrid[x] += currentRemainingValue - toShare + share + toShare%2;
					} else {
						// gn < sn < current
						long toShare = currentValue - smallerXNeighborValue; 
						long share = toShare/3;
						if (share != 0) {
							anyToppled = true;
							toppled = true;
						}
						newGrid[xMinusOne] += share;
						newGrid[xPlusOne] += share;
						long currentRemainingValue = currentValue - share - share;
						toShare = currentRemainingValue - greaterXNeighborValue; 
						share = toShare/2;
						if (share != 0) {
							anyToppled = true;
							toppled = true;
						}
						newGrid[x] += currentRemainingValue - toShare + share + toShare%2;
						newGrid[xPlusOne] += share;
					}
				} else {
					// sn < current <= gn
					long toShare = currentValue - smallerXNeighborValue; 
					long share = toShare/2;
					if (share != 0) {
						anyToppled = true;
						toppled = true;
					}
					newGrid[xMinusOne] += share;
					newGrid[x] += currentValue - toShare + share + toShare%2;
				}
			} else if (greaterXNeighborValue < currentValue) {
				// gn < current <= sn
				long toShare = currentValue - greaterXNeighborValue; 
				long share = toShare/2;
				if (share != 0) {
					anyToppled = true;
					toppled = true;
				}
				newGrid[x] += currentValue - toShare + share + toShare%2;
				newGrid[xPlusOne] += share;
			} else {
				// gn >= current <= sn
				newGrid[x] += currentValue;
			}
			topplingAlternationCompliance[x] = toppled == itsCurrentPositionsTurnToTopple;
		}
		return anyToppled;
	}
	
	@Override
	public boolean getFromPosition(int x) {	
		if (x < 0) x = -x;
		return topplingAlternationCompliance[x];
	}

	@Override
	public boolean getFromAsymmetricPosition(int x) {
		return topplingAlternationCompliance[x];
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
		data.put(SerializableModelData.GRID, grid);
		data.put(SerializableModelData.GRID_TYPE, SerializableModelData.GridTypes.INFINITE_1D);
		data.put(SerializableModelData.GRID_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_LONG_PRIMITIVE_ARRAY_1);
		data.put(SerializableModelData.COORDINATE_BOUNDS, maxX);
		data.put(SerializableModelData.COORDINATE_BOUNDS_IMPLEMENTATION_TYPE, SerializableModelData.CoordinateBoundsImplementationTypes.MAX_COORDINATE_INTEGER);
		data.put(SerializableModelData.STEP, step);
		data.put(SerializableModelData.CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP, changed);
		data.put(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE, topplingAlternationCompliance);
		data.put(SerializableModelData.TOPPLING_ALTERNATION_COMPLIANCE_IMPLEMENTATION_TYPE, SerializableModelData.GridImplementationTypes.ANYSOTROPIC_BOOLEAN_PRIMITIVE_ARRAY_1);
		Utils.serializeToFile(data, backupPath, backupName);
	}
}
