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
import java.util.Arrays;

import org.apache.commons.math3.fraction.BigFraction;

import cellularautomata.Utils;
import cellularautomata.model1d.SymmetricNumericModel1D;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 1D with a bounded grid of uneven side and a single source initial configuration of infinity at its center
 * 
 * @author Jaume
 *
 */
public class BfAether1DInfinityBoundedGridCenterSource implements SymmetricNumericModel1D<BigFraction>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2149483493000195860L;

	/** A 1D array representing the grid */
	private BigFraction[] grid;

	private final boolean isPositive;
	private long step;
	private final int size;
	private final int singleSourceCoord;
	
	public BfAether1DInfinityBoundedGridCenterSource(int size, boolean isPositive) {
		if (size%2 == 0)
			throw new IllegalArgumentException("Only uneven grid sizes are supported.");
		if (size < 5) {
			throw new IllegalArgumentException("Grid size cannot be smaller than five.");
		}
		this.isPositive = isPositive;
		this.size = size;
		this.singleSourceCoord = size/2;
		grid = new BigFraction[singleSourceCoord + 1];
		Arrays.fill(grid, BigFraction.ZERO);
		grid[0] = isPositive? BigFraction.ONE : BigFraction.MINUS_ONE;
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
	public BfAether1DInfinityBoundedGridCenterSource(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		BfAether1DInfinityBoundedGridCenterSource data = (BfAether1DInfinityBoundedGridCenterSource) Utils.deserializeFromFile(backupPath);
		isPositive = data.isPositive;
		grid = data.grid;
		size = data.size;
		singleSourceCoord = data.singleSourceCoord;
		step = data.step;
	}
	
	@Override
	public Boolean nextStep() {
		BigFraction[] newGrid = new BigFraction[grid.length];
		Arrays.fill(newGrid, BigFraction.ZERO);
		BigFraction currentValue, greaterXNeighborValue, smallerXNeighborValue;
		//i = 0
		currentValue = grid[0];
		greaterXNeighborValue = grid[1];
		if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			BigFraction toShare = currentValue.subtract(greaterXNeighborValue);
			BigFraction share = toShare.divide(3);
			newGrid[0] = newGrid[0].add(currentValue.subtract(toShare).add(share));
			newGrid[1] = newGrid[1].add(share);	
		} else {
			newGrid[0] = newGrid[0].add(currentValue);
		}
		//i = 1
		//reuse values obtained previously
		smallerXNeighborValue = currentValue;
		currentValue = greaterXNeighborValue;
		greaterXNeighborValue = grid[2];
		if (smallerXNeighborValue.compareTo(currentValue) < 0) {
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				if (smallerXNeighborValue.equals(greaterXNeighborValue)) {
					// gn == sn < current
					BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
					BigFraction share = toShare.divide(3);
					newGrid[0] = newGrid[0].add(share.add(share));//one more for the symmetric position at the other side
					newGrid[1] = newGrid[1].add(currentValue.subtract(toShare).add(share));
					newGrid[2] = newGrid[2].add(share);
				} else if (smallerXNeighborValue.compareTo(greaterXNeighborValue) < 0) {
					// sn < gn < current
					BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
					BigFraction share = toShare.divide(3);
					newGrid[0] = newGrid[0].add(share.add(share));//one more for the symmetric position at the other side
					newGrid[2] = newGrid[2].add(share);
					BigFraction currentRemainingValue = currentValue.subtract(share).subtract(share);
					toShare = currentRemainingValue.subtract(smallerXNeighborValue); 
					share = toShare.divide(2);
					newGrid[0] = newGrid[0].add(share.add(share));//one more for the symmetric position at the other side
					newGrid[1] = newGrid[1].add(currentRemainingValue.subtract(toShare).add(share));
				} else {
					// gn < sn < current
					BigFraction toShare = currentValue.subtract(smallerXNeighborValue); 
					BigFraction share = toShare.divide(3);
					newGrid[0] = newGrid[0].add(share.add(share));//one more for the symmetric position at the other side
					newGrid[2] = newGrid[2].add(share);
					BigFraction currentRemainingValue = currentValue.subtract(share).subtract(share);
					toShare = currentRemainingValue.subtract(greaterXNeighborValue); 
					share = toShare.divide(2);
					newGrid[1] = newGrid[1].add(currentRemainingValue.subtract(toShare).add(share));
					newGrid[2] = newGrid[2].add(share);
				}
			} else {
				// sn < current <= gn
				BigFraction toShare = currentValue.subtract(smallerXNeighborValue); 
				BigFraction share = toShare.divide(2);
				newGrid[0] = newGrid[0].add(share.add(share));//one more for the symmetric position at the other side
				newGrid[1] = newGrid[1].add(currentValue.subtract(toShare).add(share));
			}
		} else if (greaterXNeighborValue.compareTo(currentValue) < 0) {
			// gn < current <= sn
			BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
			BigFraction share = toShare.divide(2);
			newGrid[1] = newGrid[1].add(currentValue.subtract(toShare).add(share));
			newGrid[2] = newGrid[2].add(share);
		} else {
			// gn >= current <= sn
			newGrid[1] = newGrid[1].add(currentValue);
		}
		//2 <= i < edge - 1
		int edge = grid.length - 1;
		toppleRangeBeyondI1(newGrid, 2, edge);
		//i = edge
		int edgeMinusOne = edge - 1;
		currentValue = grid[edge];
		smallerXNeighborValue = grid[edgeMinusOne];
		if (smallerXNeighborValue.compareTo(currentValue) < 0) {
			BigFraction toShare = currentValue.subtract(smallerXNeighborValue);
			BigFraction share = toShare.divide(2);
			newGrid[edge] = newGrid[edge].add(currentValue.subtract(toShare).add(share));
			newGrid[edgeMinusOne] = newGrid[edgeMinusOne].add(share);	
		} else {
			newGrid[edge] = newGrid[edge].add(currentValue);
		}
		grid = newGrid;
		step++;
		return true;
	}

	@Override
	public Boolean isChanged() {
		return step == 0 ? null : true;
	}
	
	private void toppleRangeBeyondI1(BigFraction[] newGrid, int minI, int maxI) {
		int i = minI, iMinusOne = i - 1, iPlusOne = i + 1;
		BigFraction smallerXNeighborValue, currentValue = grid[iMinusOne], greaterXNeighborValue = grid[i];
		for (; i < maxI; iMinusOne = i, i = iPlusOne, iPlusOne++) {
			//reuse values obtained previously
			smallerXNeighborValue = currentValue;
			currentValue = greaterXNeighborValue;
			greaterXNeighborValue = grid[iPlusOne];
			if (smallerXNeighborValue.compareTo(currentValue) < 0) {
				if (greaterXNeighborValue.compareTo(currentValue) < 0) {
					if (smallerXNeighborValue.equals(greaterXNeighborValue)) {
						// gn == sn < current
						BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
						BigFraction share = toShare.divide(3);
						newGrid[iMinusOne] = newGrid[iMinusOne].add(share);
						newGrid[i] = newGrid[i].add(currentValue.subtract(toShare).add(share));
						newGrid[iPlusOne] = newGrid[iPlusOne].add(share);
					} else if (smallerXNeighborValue.compareTo(greaterXNeighborValue) < 0) {
						// sn < gn < current
						BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
						BigFraction share = toShare.divide(3);
						newGrid[iMinusOne] = newGrid[iMinusOne].add(share);
						newGrid[iPlusOne] = newGrid[iPlusOne].add(share);
						BigFraction currentRemainingValue = currentValue.subtract(share).subtract(share);
						toShare = currentRemainingValue.subtract(smallerXNeighborValue); 
						share = toShare.divide(2);
						newGrid[iMinusOne] = newGrid[iMinusOne].add(share);
						newGrid[i] = newGrid[i].add(currentRemainingValue.subtract(toShare).add(share));
					} else {
						// gn < sn < current
						BigFraction toShare = currentValue.subtract(smallerXNeighborValue); 
						BigFraction share = toShare.divide(3);
						newGrid[iMinusOne] = newGrid[iMinusOne].add(share);
						newGrid[iPlusOne] = newGrid[iPlusOne].add(share);
						BigFraction currentRemainingValue = currentValue.subtract(share).subtract(share);
						toShare = currentRemainingValue.subtract(greaterXNeighborValue); 
						share = toShare.divide(2);
						newGrid[i] = newGrid[i].add(currentRemainingValue.subtract(toShare).add(share));
						newGrid[iPlusOne] = newGrid[iPlusOne].add(share);
					}
				} else {
					// sn < current <= gn
					BigFraction toShare = currentValue.subtract(smallerXNeighborValue); 
					BigFraction share = toShare.divide(2);
					newGrid[iMinusOne] = newGrid[iMinusOne].add(share);
					newGrid[i] = newGrid[i].add(currentValue.subtract(toShare).add(share));
				}
			} else if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				// gn < current <= sn
				BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
				BigFraction share = toShare.divide(2);
				newGrid[i] = newGrid[i].add(currentValue.subtract(toShare).add(share));
				newGrid[iPlusOne] = newGrid[iPlusOne].add(share);
			} else {
				// gn >= current <= sn
				newGrid[i] = newGrid[i].add(currentValue);
			}
		}
	}
	
	@Override
	public BigFraction getFromPosition(int x) {	
		int i = x - singleSourceCoord;
		if (i < 0) i = -i;
		return grid[i];
	}

	@Override
	public BigFraction getFromAsymmetricPosition(int x) {
		return grid[x - singleSourceCoord];
	}

	@Override
	public int getAsymmetricMinX() { return singleSourceCoord; }

	@Override
	public int getAsymmetricMaxX() {
		return grid.length - 1;
	}

	@Override
	public int getMinX() {
		return 0;
	}

	@Override
	public int getMaxX() {
		return getAsymmetricMaxX();
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
		String path = getName() + "/1D/bounded_grid/" + size + "/(" + singleSourceCoord + ")=";
		if (!isPositive) path += "-";
		path += "infinity";
		return path;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		Utils.serializeToFile(this, backupPath, backupName);
	}
	
	public int getSize() {
		return size;
	}
}
