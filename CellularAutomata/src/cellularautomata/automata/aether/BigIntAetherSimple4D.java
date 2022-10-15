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
import java.util.ArrayList;
import java.util.List;

import cellularautomata.Constants;
import cellularautomata.Utils;
import cellularautomata.automata.Neighbor;
import cellularautomata.model4d.IsotropicHypercubicModel4DA;
import cellularautomata.model4d.SymmetricNumericModel4D;
import cellularautomata.numbers.BigInt;

/**
 * Simplified implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 4D, with a single source initial configuration, for review and testing purposes
 * 
 * @author Jaume
 *
 */
public class BigIntAetherSimple4D implements SymmetricNumericModel4D<BigInt>, IsotropicHypercubicModel4DA {	
	
	private static final byte W_POSITIVE = 0;
	private static final byte W_NEGATIVE = 1;
	private static final byte X_POSITIVE = 2;
	private static final byte X_NEGATIVE = 3;
	private static final byte Y_POSITIVE = 4;
	private static final byte Y_NEGATIVE = 5;
	private static final byte Z_POSITIVE = 6;
	private static final byte Z_NEGATIVE = 7;
	
	/** 4D array representing the grid **/
	private BigInt[][][][] grid;
	
	private final BigInt initialValue;
	private long step;
	
	/** The indexes of the origin within the array */
	private int originIndex;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	/**
	 * Used in {@link #getSubfolderPath()}.
	 */
	private final String folderName;

	private Boolean changed = null;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public BigIntAetherSimple4D(BigInt initialValue) {
		this.initialValue = initialValue;
		int side = 5;
		grid = new BigInt[side][side][side][side];
		Utils.fillArray(grid, BigInt.ZERO);
		originIndex = (side - 1)/2;
		grid[originIndex][originIndex][originIndex][originIndex] = this.initialValue;
		boundsReached = false;
		//Set the current step to zero
		step = 0;
		String strInitialValue = Utils.numberToPlainTextMaxLength(initialValue, Constants.MAX_INITIAL_VALUE_LENGTH_IN_PATH);
		if (strInitialValue == null) {
			folderName = Utils.getTimeStampFolderName();
		} else {
			folderName = strInitialValue;
		}
	}
	
	@Override
	public Boolean nextStep() {
		//Use new array to store the values of the next step
		BigInt[][][][] newGrid = null;
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new BigInt[grid.length + 2][grid.length + 2][grid.length + 2][grid.length + 2];
			//The offset between the indexes of the new and old array
			indexOffset = 1;
		} else {
			newGrid = new BigInt[grid.length][grid.length][grid.length][grid.length];
		}
		Utils.fillArray(newGrid, BigInt.ZERO);
		boolean changed = false;
		//For every cell
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				for (int k = 0; k < grid.length; k++) {
					for (int l = 0; l < grid.length; l++) {
						BigInt value = grid[i][j][k][l];
						List<Neighbor<BigInt>> neighbors = new ArrayList<Neighbor<BigInt>>(8);						
						BigInt neighborValue;
						if (i < grid.length - 1)
							neighborValue = grid[i + 1][j][k][l];
						else
							neighborValue = BigInt.ZERO;
						if (neighborValue.compareTo(value) < 0)
							neighbors.add(new Neighbor<BigInt>(W_POSITIVE, neighborValue));
						if (i > 0)
							neighborValue = grid[i - 1][j][k][l];
						else
							neighborValue = BigInt.ZERO;
						if (neighborValue.compareTo(value) < 0)
							neighbors.add(new Neighbor<BigInt>(W_NEGATIVE, neighborValue));
						if (j < grid.length - 1)
							neighborValue = grid[i][j + 1][k][l];
						else
							neighborValue = BigInt.ZERO;
						if (neighborValue.compareTo(value) < 0)
							neighbors.add(new Neighbor<BigInt>(X_POSITIVE, neighborValue));
						if (j > 0)
							neighborValue = grid[i][j - 1][k][l];
						else
							neighborValue = BigInt.ZERO;
						if (neighborValue.compareTo(value) < 0)
							neighbors.add(new Neighbor<BigInt>(X_NEGATIVE, neighborValue));
						if (k < grid.length - 1)
							neighborValue = grid[i][j][k + 1][l];
						else
							neighborValue = BigInt.ZERO;
						if (neighborValue.compareTo(value) < 0)
							neighbors.add(new Neighbor<BigInt>(Y_POSITIVE, neighborValue));
						if (k > 0)
							neighborValue = grid[i][j][k - 1][l];
						else
							neighborValue = BigInt.ZERO;
						if (neighborValue.compareTo(value) < 0)
							neighbors.add(new Neighbor<BigInt>(Y_NEGATIVE, neighborValue));
						if (l < grid.length - 1)
							neighborValue = grid[i][j][k][l + 1];
						else
							neighborValue = BigInt.ZERO;
						if (neighborValue.compareTo(value) < 0)
							neighbors.add(new Neighbor<BigInt>(Z_POSITIVE, neighborValue));
						if (l > 0)
							neighborValue = grid[i][j][k][l - 1];
						else
							neighborValue = BigInt.ZERO;
						if (neighborValue.compareTo(value) < 0)
							neighbors.add(new Neighbor<BigInt>(Z_NEGATIVE, neighborValue));
						
						if (neighbors.size() > 0) {
							//sort
							boolean sorted = false;
							while (!sorted) {
								sorted = true;
								for (int neighborIndex = neighbors.size() - 2; neighborIndex >= 0; neighborIndex--) {
									Neighbor<BigInt> next = neighbors.get(neighborIndex+1);
									if (neighbors.get(neighborIndex).getValue().compareTo(next.getValue()) > 0) {
										sorted = false;
										neighbors.remove(neighborIndex+1);
										neighbors.add(neighborIndex, next);
									}
								}
							}
							//divide
							boolean isFirst = true;
							BigInt previousNeighborValue = null;
							for (int neighborIndex = neighbors.size() - 1; neighborIndex >= 0; neighborIndex--,isFirst = false) {
								neighborValue = neighbors.get(neighborIndex).getValue();
								if (!neighborValue.equals(previousNeighborValue) || isFirst) {
									//Add one for the current cell
									int shareCount = neighbors.size() + 1;
									BigInt toShare = value.subtract(neighborValue);
									BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
									BigInt share = shareAndRemainder[0];
									if (!share.equals(BigInt.ZERO)) {
										checkBoundsReached(i + indexOffset, j + indexOffset, k + indexOffset, l + indexOffset, newGrid.length);
										changed = true;
										//The current cell keeps the remainder and one share
										value = value.subtract(toShare).add(shareAndRemainder[1]).add(share);
										for (Neighbor<BigInt> neighbor : neighbors) {
											int[] nc = getNeighborCoordinates(i, j, k, l, neighbor.getDirection());
											nc[0] += indexOffset;
											nc[1] += indexOffset;
											nc[2] += indexOffset;
											nc[3] += indexOffset;
											newGrid[nc[0]][nc[1]][nc[2]][nc[3]] = newGrid[nc[0]][nc[1]][nc[2]][nc[3]].add(share);
										}
									}
									previousNeighborValue = neighborValue;
								}
								neighbors.remove(neighborIndex);
							}	
						}					
						newGrid[i + indexOffset][j + indexOffset][k + indexOffset][l + indexOffset] = 
								newGrid[i + indexOffset][j + indexOffset][k + indexOffset][l + indexOffset].add(value);
					}
				}
			}
		}
		//Replace the old array with the new one
		grid = newGrid;
		//Update the index of the origin
		originIndex += indexOffset;
		//Increase the current step by one
		step++;
		this.changed = changed;
		//Return whether or not the state of the grid changed
		return changed;
	}

	@Override
	public Boolean isChanged() {
		return changed;
	}
	
	private void checkBoundsReached(int i, int j, int k, int l, int length) {
		if (i == 1 || i == length - 2 || 
			j == 1 || j == length - 2 || 
			k == 1 || k == length - 2 || 
			l == 1 || l == length - 2) {
			boundsReached = true;
		}
	}
	
	private static int[] getNeighborCoordinates(int w, int x, int y, int z, byte direction) {
		switch(direction) {
		case W_POSITIVE:
			w++;
			break;
		case W_NEGATIVE:
			w--;
			break;
		case X_POSITIVE:
			x++;
			break;
		case X_NEGATIVE:
			x--;
			break;
		case Y_POSITIVE:
			y++;
			break;
		case Y_NEGATIVE:
			y--;
			break;
		case Z_POSITIVE:
			z++;
			break;
		case Z_NEGATIVE:
			z--;
			break;
		}
		return new int[]{
			w, x, y, z
		};
	}
	
	@Override
	public BigInt getFromPosition(int w, int x, int y, int z) {
		int i = originIndex + w;
		int j = originIndex + x;
		int k = originIndex + y;
		int l = originIndex + z;
		if (i < 0 || i > grid.length - 1 
				|| j < 0 || j > grid.length - 1 
				|| k < 0 || k > grid.length - 1
				|| l < 0 || l > grid.length - 1) {
			//If the passed coordinates are outside the array, the value will be zero
			return BigInt.ZERO;
		} else {
			return grid[i][j][k][l];
		}
	}
	
	@Override
	public BigInt getFromAsymmetricPosition(int w, int x, int y, int z) {
		return getFromPosition(w, x, y, z);
	}

	@Override
	public int getAsymmetricMaxW() {
		int arrayMaxW = grid.length - 1 - originIndex;
		int valuesMaxW;
		if (boundsReached) {
			valuesMaxW = arrayMaxW;
		} else {
			valuesMaxW = arrayMaxW - 1;
		}
		return valuesMaxW;
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
	public BigInt getIntialValue() {
		return initialValue;
	}

	@Override
	public String getName() {
		return "Aether";
	}
	
	@Override
	public String getSubfolderPath() {
		return getName() + "/4D/" + folderName;
	}
	
	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}
}