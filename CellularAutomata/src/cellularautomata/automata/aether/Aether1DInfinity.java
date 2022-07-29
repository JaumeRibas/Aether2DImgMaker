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
import java.util.Arrays;

import org.apache.commons.math3.fraction.BigFraction;

import cellularautomata.Utils;
import cellularautomata.model1d.IsotropicModel1DA;
import cellularautomata.model1d.SymmetricNumericModel1D;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 1D with a single source initial configuration of infinity
 * 
 * @author Jaume
 *
 */
public class Aether1DInfinity implements SymmetricNumericModel1D<BigFraction>, IsotropicModel1DA, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3150748881076657654L;

	/** A 1D array representing the grid */
	private BigFraction[] grid;

	private final boolean isPositive;
	private long step;
	private int maxX;
	
	public Aether1DInfinity(boolean isPositive) {
		this.isPositive = isPositive;
		grid = new BigFraction[5];
		Arrays.fill(grid, BigFraction.ZERO);
		grid[0] = isPositive? BigFraction.ONE : BigFraction.MINUS_ONE;
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
	public Aether1DInfinity(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		Aether1DInfinity data = (Aether1DInfinity) Utils.deserializeFromFile(backupPath);
		isPositive = data.isPositive;
		grid = data.grid;
		maxX = data.maxX;
		step = data.step;
	}
	
	@Override
	public boolean nextStep() {
		BigFraction[] newGrid = new BigFraction[maxX + 3];
		Arrays.fill(newGrid, BigFraction.ZERO);
		BigFraction currentValue, greaterXNeighborValue, smallerXNeighborValue;
		//x = 0
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
		//x = 1
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
		} else {
			if (greaterXNeighborValue.compareTo(currentValue) < 0) {
				// gn < current <= sn
				BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
				BigFraction share = toShare.divide(2);
				newGrid[1] = newGrid[1].add(currentValue.subtract(toShare).add(share));
				newGrid[2] = newGrid[2].add(share);
			} else {
				newGrid[1] = newGrid[1].add(currentValue);
			}
		}
		//2 <= x < edge - 2
		int edge = grid.length - 1;
		int edgeMinusTwo = edge - 2;
		toppleRangeBeyondX1(newGrid, 2, edgeMinusTwo);
		//edge - 2 <= x < edge
		toppleRangeBeyondX1(newGrid, edgeMinusTwo, edge);
		grid = newGrid;
		maxX++;
		step++;
		return true;
	}
	
	private void toppleRangeBeyondX1(BigFraction[] newGrid, int minX, int maxX) {
		int x = minX, xMinusOne = x - 1, xPlusOne = x + 1;
		BigFraction smallerXNeighborValue, currentValue = grid[xMinusOne], greaterXNeighborValue = grid[x];
		for (; x < maxX; xMinusOne = x, x = xPlusOne, xPlusOne++) {
			//reuse values obtained previously
			smallerXNeighborValue = currentValue;
			currentValue = greaterXNeighborValue;
			greaterXNeighborValue = grid[xPlusOne];
			if (smallerXNeighborValue.compareTo(currentValue) < 0) {
				if (greaterXNeighborValue.compareTo(currentValue) < 0) {
					if (smallerXNeighborValue.equals(greaterXNeighborValue)) {
						// gn == sn < current
						BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
						BigFraction share = toShare.divide(3);
						newGrid[xMinusOne] = newGrid[xMinusOne].add(share);
						newGrid[x] = newGrid[x].add(currentValue.subtract(toShare).add(share));
						newGrid[xPlusOne] = newGrid[xPlusOne].add(share);
					} else if (smallerXNeighborValue.compareTo(greaterXNeighborValue) < 0) {
						// sn < gn < current
						BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
						BigFraction share = toShare.divide(3);
						newGrid[xMinusOne] = newGrid[xMinusOne].add(share);
						newGrid[xPlusOne] = newGrid[xPlusOne].add(share);
						BigFraction currentRemainingValue = currentValue.subtract(share).subtract(share);
						toShare = currentRemainingValue.subtract(smallerXNeighborValue); 
						share = toShare.divide(2);
						newGrid[xMinusOne] = newGrid[xMinusOne].add(share);
						newGrid[x] = newGrid[x].add(currentRemainingValue.subtract(toShare).add(share));
					} else {
						// gn < sn < current
						BigFraction toShare = currentValue.subtract(smallerXNeighborValue); 
						BigFraction share = toShare.divide(3);
						newGrid[xMinusOne] = newGrid[xMinusOne].add(share);
						newGrid[xPlusOne] = newGrid[xPlusOne].add(share);
						BigFraction currentRemainingValue = currentValue.subtract(share).subtract(share);
						toShare = currentRemainingValue.subtract(greaterXNeighborValue); 
						share = toShare.divide(2);
						newGrid[x] = newGrid[x].add(currentRemainingValue.subtract(toShare).add(share));
						newGrid[xPlusOne] = newGrid[xPlusOne].add(share);
					}
				} else {
					// sn < current <= gn
					BigFraction toShare = currentValue.subtract(smallerXNeighborValue); 
					BigFraction share = toShare.divide(2);
					newGrid[xMinusOne] = newGrid[xMinusOne].add(share);
					newGrid[x] = newGrid[x].add(currentValue.subtract(toShare).add(share));
				}
			} else {
				if (greaterXNeighborValue.compareTo(currentValue) < 0) {
					// gn < current <= sn
					BigFraction toShare = currentValue.subtract(greaterXNeighborValue); 
					BigFraction share = toShare.divide(2);
					newGrid[x] = newGrid[x].add(currentValue.subtract(toShare).add(share));
					newGrid[xPlusOne] = newGrid[xPlusOne].add(share);
				} else {
					newGrid[x] = newGrid[x].add(currentValue);
				}
			}
		}
	}
	
	@Override
	public BigFraction getFromPosition(int x) {	
		if (x < 0) x = -x;
		if (x < grid.length) {
			return grid[x];
		} else {
			return BigFraction.ZERO;
		}
	}

	@Override
	public BigFraction getFromAsymmetricPosition(int x) {
		return grid[x];
	}

	@Override
	public int getAsymmetricMaxX() {
		return maxX;
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
		String path = getName() + "/1D/";
		if (!isPositive) path += "-";
		path += "infinity";
		return path;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		Utils.serializeToFile(this, backupPath, backupName);
	}
}
