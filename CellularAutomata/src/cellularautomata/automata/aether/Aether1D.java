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
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package cellularautomata.automata.aether;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import cellularautomata.Utils;
import cellularautomata.grid1d.IsotropicGrid1DA;
import cellularautomata.model1d.SymmetricLongModel1D;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 1D with a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class Aether1D implements SymmetricLongModel1D, IsotropicGrid1DA, Serializable {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5112179402220722606L;
	
	public static final long MAX_INITIAL_VALUE = Long.MAX_VALUE;
	public static final long MIN_INITIAL_VALUE = -9223372036854775807L;

	/** A 1D array representing the grid */
	private long[] grid;
	
	private long initialValue;
	private long step;
	private int maxX;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public Aether1D(long initialValue) {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of long type
			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
	    }
		this.initialValue = initialValue;
		grid = new long[5];
		grid[0] = initialValue;
		maxX = 2;
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
	public Aether1D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		Aether1D data = (Aether1D) Utils.deserializeFromFile(backupPath);
		initialValue = data.initialValue;
		grid = data.grid;
		maxX = data.maxX;
		step = data.step;
	}
	
	@Override
	public boolean nextStep(){
		long[] newGrid = new long[maxX + 3];
		boolean changed = false;
		long currentValue, greaterXNeighborValue, smallerXNeighborValue;
		//x = 0
		currentValue = grid[0];
		greaterXNeighborValue = grid[1];
		if (greaterXNeighborValue < currentValue) {
			long toShare = currentValue - greaterXNeighborValue;
			long share = toShare/3;
			if (share != 0) {
				changed = true;
				newGrid[0] += currentValue - toShare + share + toShare%3;
				newGrid[1] += share;
			} else {
				newGrid[0] += currentValue;
			}			
		} else {
			newGrid[0] += currentValue;
		}
		//x = 1
		//reuse values obtained previously
		smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterXNeighborValue = grid[2];
		if (smallerXNeighborValue < currentValue) {
			if (greaterXNeighborValue < currentValue) {
				if (smallerXNeighborValue == greaterXNeighborValue) {
					// gn == sn < current
					long toShare = currentValue - greaterXNeighborValue; 
					long share = toShare/3;
					if (share != 0) {
						changed = true;
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
					}
					newGrid[0] += share + share;//one more for the symmetric position at the other side
					newGrid[2] += share;
					long currentRemainingValue = currentValue - share - share;
					toShare = currentRemainingValue - smallerXNeighborValue; 
					share = toShare/2;
					if (share != 0) {
						changed = true;
					}
					newGrid[0] += share + share;//one more for the symmetric position at the other side
					newGrid[1] += currentRemainingValue - toShare + share + toShare%2;
				} else {
					// gn < sn < current
					long toShare = currentValue - smallerXNeighborValue; 
					long share = toShare/3;
					if (share != 0) {
						changed = true;
					}
					newGrid[0] += share + share;//one more for the symmetric position at the other side
					newGrid[2] += share;
					long currentRemainingValue = currentValue - share - share;
					toShare = currentRemainingValue - greaterXNeighborValue; 
					share = toShare/2;
					if (share != 0) {
						changed = true;
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
				}
				newGrid[0] += share + share;//one more for the symmetric position at the other side
				newGrid[1] += currentValue - toShare + share + toShare%2;
			}
		} else {
			if (greaterXNeighborValue < currentValue) {
				// gn < current <= sn
				long toShare = currentValue - greaterXNeighborValue; 
				long share = toShare/2;
				if (share != 0) {
					changed = true;
				}
				newGrid[1] += currentValue - toShare + share + toShare%2;
				newGrid[2] += share;
			} else {
				newGrid[1] += currentValue;
			}
		}
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
		grid = newGrid;
		step++;
		return changed;
	}
	
	private boolean toppleRangeBeyondX1(long[] newGrid, int minX, int maxX) {
		boolean anyToppled = false;
		int x = minX, xMinusOne = x - 1, xPlusOne = x + 1;
		long smallerXNeighborValue, currentValue = grid[xMinusOne], greaterXNeighborValue = grid[x];
		for (; x < maxX; xMinusOne = x, x = xPlusOne, xPlusOne++) {
			//reuse values obtained previously
			smallerXNeighborValue = currentValue;
			currentValue = greaterXNeighborValue;
			greaterXNeighborValue = grid[xPlusOne];
			if (smallerXNeighborValue < currentValue) {
				if (greaterXNeighborValue < currentValue) {
					if (smallerXNeighborValue == greaterXNeighborValue) {
						// gn == sn < current
						long toShare = currentValue - greaterXNeighborValue; 
						long share = toShare/3;
						if (share != 0) {
							anyToppled = true;
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
						}
						newGrid[xMinusOne] += share;
						newGrid[xPlusOne] += share;
						long currentRemainingValue = currentValue - share - share;
						toShare = currentRemainingValue - smallerXNeighborValue; 
						share = toShare/2;
						if (share != 0) {
							anyToppled = true;
						}
						newGrid[xMinusOne] += share;
						newGrid[x] += currentRemainingValue - toShare + share + toShare%2;
					} else {
						// gn < sn < current
						long toShare = currentValue - smallerXNeighborValue; 
						long share = toShare/3;
						if (share != 0) {
							anyToppled = true;
						}
						newGrid[xMinusOne] += share;
						newGrid[xPlusOne] += share;
						long currentRemainingValue = currentValue - share - share;
						toShare = currentRemainingValue - greaterXNeighborValue; 
						share = toShare/2;
						if (share != 0) {
							anyToppled = true;
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
					}
					newGrid[xMinusOne] += share;
					newGrid[x] += currentValue - toShare + share + toShare%2;
				}
			} else {
				if (greaterXNeighborValue < currentValue) {
					// gn < current <= sn
					long toShare = currentValue - greaterXNeighborValue; 
					long share = toShare/2;
					if (share != 0) {
						anyToppled = true;
					}
					newGrid[x] += currentValue - toShare + share + toShare%2;
					newGrid[xPlusOne] += share;
				} else {
					newGrid[x] += currentValue;
				}
			}
		}
		return anyToppled;
	}
	
	@Override
	public long getFromPosition(int x){	
		if (x < 0) x = -x;
		if (x < grid.length) {
			return grid[x];
		} else {
			return 0;
		}
	}

	@Override
	public long getFromAsymmetricPosition(int x) {
		return grid[x];
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
		return getName() + "/1D/" + initialValue;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		Utils.serializeToFile(this, backupPath, backupName);
	}
}
