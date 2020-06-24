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
import java.math.BigInteger;

import cellularautomata.evolvinggrid.SymmetricEvolvingLongGrid1D;

public class Aether1D implements SymmetricEvolvingLongGrid1D {	

	/** A 1D array representing the grid */
	private long[] grid;
	
	private long initialValue;
	private long currentStep;
	private int maxX;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public Aether1D(long initialValue) {
		if (initialValue < 0) {
			BigInteger maxNeighboringValuesDifference = Utils.getAetherMaxNeighboringValuesDifferenceFromSingleSource(1, BigInteger.valueOf(initialValue));
			if (maxNeighboringValuesDifference.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
				throw new IllegalArgumentException("Resulting max value difference between neighboring positions (" + maxNeighboringValuesDifference 
						+ ") exceeds implementation's limit (" + Long.MAX_VALUE + "). Use a greater initial value or a different implementation.");
			}
		}
		this.initialValue = initialValue;
		grid = new long[5];
		grid[0] = initialValue;
		maxX = 1;//at the smallest size it won't be exact
		currentStep = 0;
	}
	
	@Override
	public boolean nextStep(){
		long[] newGrid = new long[maxX + 4];
		boolean changed = false;
		int edge = grid.length - 1;
		long currentValue, greaterXNeighborValue, smallerXNeighborValue;
		//x = 0
		currentValue = grid[0];
		greaterXNeighborValue = grid[1];
		if (greaterXNeighborValue < currentValue) {
			long toShare = currentValue - greaterXNeighborValue;
			long share = toShare/3;
			if (share > 0) {
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
					if (share > 0) {
						changed = true;
					}
					newGrid[0] += share + share;//one more for the symmetric position at the other side
					newGrid[1] += currentValue - toShare + share + toShare%3;
					newGrid[2] += share;
				} else if (smallerXNeighborValue < greaterXNeighborValue) {
					// sn < gn < current
					long toShare = currentValue - greaterXNeighborValue; 
					long share = toShare/3;
					if (share > 0) {
						changed = true;
					}
					newGrid[0] += share + share;//one more for the symmetric position at the other side
					newGrid[2] += share;
					long currentRemainingValue = currentValue - share - share;
					toShare = currentRemainingValue - smallerXNeighborValue; 
					share = toShare/2;
					if (share > 0) {
						changed = true;
					}
					newGrid[0] += share + share;//one more for the symmetric position at the other side
					newGrid[1] += currentRemainingValue - toShare + share + toShare%2;
				} else {
					// gn < sn < current
					long toShare = currentValue - smallerXNeighborValue; 
					long share = toShare/3;
					if (share > 0) {
						changed = true;
					}
					newGrid[0] += share + share;//one more for the symmetric position at the other side
					newGrid[2] += share;
					long currentRemainingValue = currentValue - share - share;
					toShare = currentRemainingValue - greaterXNeighborValue; 
					share = toShare/2;
					if (share > 0) {
						changed = true;
					}
					newGrid[1] += currentRemainingValue - toShare + share + toShare%2;
					newGrid[2] += share;
				}
			} else {
				// sn < current <= gn
				long toShare = currentValue - smallerXNeighborValue; 
				long share = toShare/2;
				if (share > 0) {
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
				if (share > 0) {
					changed = true;
				}
				newGrid[1] += currentValue - toShare + share + toShare%2;
				newGrid[2] += share;
			} else {
				newGrid[1] += currentValue;
			}
		}
		//2 >= x < edge - 2
		int edgeMinusTwo = edge - 2;
		if (toppleRangeBeyondX1(newGrid, 2, edgeMinusTwo)) {
			changed = true;
		}
		//edge - 2 >= x < edge
		if (toppleRangeBeyondX1(newGrid, edgeMinusTwo, edge)) {
			changed = true;
			maxX++;
		}
		grid = newGrid;
		currentStep++;
		return changed;
	}
	
	private boolean toppleRangeBeyondX1(long[] newGrid, int minX, int maxX) {
		boolean anyToppled = false;
		int x = minX, xMinusOne = x - 1, xPlusOne = x + 1;
		long smallerXNeighborValue, currentValue = grid[xMinusOne], greaterXNeighborValue = grid[x];
		for (; x < maxX; x++, xMinusOne++, xPlusOne++) {
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
						if (share > 0) {
							anyToppled = true;
						}
						newGrid[xMinusOne] += share;
						newGrid[x] += currentValue - toShare + share + toShare%3;
						newGrid[xPlusOne] += share;
					} else if (smallerXNeighborValue < greaterXNeighborValue) {
						// sn < gn < current
						long toShare = currentValue - greaterXNeighborValue; 
						long share = toShare/3;
						if (share > 0) {
							anyToppled = true;
						}
						newGrid[xMinusOne] += share;
						newGrid[xPlusOne] += share;
						long currentRemainingValue = currentValue - share - share;
						toShare = currentRemainingValue - smallerXNeighborValue; 
						share = toShare/2;
						if (share > 0) {
							anyToppled = true;
						}
						newGrid[xMinusOne] += share;
						newGrid[x] += currentRemainingValue - toShare + share + toShare%2;
					} else {
						// gn < sn < current
						long toShare = currentValue - smallerXNeighborValue; 
						long share = toShare/3;
						if (share > 0) {
							anyToppled = true;
						}
						newGrid[xMinusOne] += share;
						newGrid[xPlusOne] += share;
						long currentRemainingValue = currentValue - share - share;
						toShare = currentRemainingValue - greaterXNeighborValue; 
						share = toShare/2;
						if (share > 0) {
							anyToppled = true;
						}
						newGrid[x] += currentRemainingValue - toShare + share + toShare%2;
						newGrid[xPlusOne] += share;
					}
				} else {
					// sn < current <= gn
					long toShare = currentValue - smallerXNeighborValue; 
					long share = toShare/2;
					if (share > 0) {
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
					if (share > 0) {
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
	public long getValueAtPosition(int x){	
		if (x < 0) x = -x;
		if (x < grid.length) {
			return grid[x];
		} else {
			return 0;
		}
	}
	
	@Override
	public int getAsymmetricMinX() {
		return 0;
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
	public int getMinX() {
		return -getAsymmetricMaxX();
	}

	@Override
	public int getMaxX() {
		return getAsymmetricMaxX();
	}

	@Override
	public long getStep() {
		return currentStep;
	}

	@Override
	public String getName() {
		return "Aether1D";
	}
	
	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialValue;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getValueAtAsymmetricPosition(int x) {
		return grid[x];
	}
}
