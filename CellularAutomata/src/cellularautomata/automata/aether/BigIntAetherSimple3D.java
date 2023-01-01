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
import java.util.ArrayList;
import java.util.List;

import cellularautomata.Constants;
import cellularautomata.Utils;
import cellularautomata.automata.Neighbor;
import cellularautomata.model3d.IsotropicCubicModelA;
import cellularautomata.model3d.SymmetricNumericModel3D;
import cellularautomata.numbers.BigInt;

/**
 * Simplified implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 3D, with a single source initial configuration, for review and testing purposes
 * 
 * @author Jaume
 *
 */
public class BigIntAetherSimple3D implements SymmetricNumericModel3D<BigInt>, IsotropicCubicModelA {	
	
	private static final byte UP = 0;
	private static final byte DOWN = 1;
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;
	private static final byte FRONT = 4;
	private static final byte BACK = 5;
	
	/** 3D array representing the grid **/
	private BigInt[][][] grid;
	
	private long step;
	private final BigInt initialValue;
	
	/** The indexes of the origin within the array */
	private int originIndex;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	/**
	 * Used in {@link #getSubfolderPath()}.
	 */
	private final String folderName;
	
	/** Whether or not the state of the model changed between the current and the previous step **/
	private Boolean changed = null;
	
	public BigIntAetherSimple3D(BigInt initialValue) {
		this.initialValue = initialValue;
		//initial side of the array, will be increased as needed
		int side = 5;
		grid = new BigInt[side][side][side];
		Utils.fillArray(grid, BigInt.ZERO);
		originIndex = (side - 1)/2;
		grid[originIndex][originIndex][originIndex] = initialValue;
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
		BigInt[][][] newGrid = null;
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new BigInt[grid.length + 2][grid.length + 2][grid.length + 2];
			//The offset between the indexes of the new and old array
			indexOffset = 1;
		} else {
			newGrid = new BigInt[grid.length][grid.length][grid.length];
		}
		Utils.fillArray(newGrid, BigInt.ZERO);
		boolean changed = false;
		//For every cell
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				for (int k = 0; k < grid.length; k++) {
					BigInt value = grid[i][j][k];
					//make list of von Neumann neighbors with value smaller than current cell's value
					List<Neighbor<BigInt>> neighbors = new ArrayList<Neighbor<BigInt>>(6);						
					BigInt neighborValue;
					if (i < grid.length - 1)
						neighborValue = grid[i + 1][j][k];
					else
						neighborValue = BigInt.ZERO;
					if (neighborValue.compareTo(value) < 0)
						neighbors.add(new Neighbor<BigInt>(RIGHT, neighborValue));
					if (i > 0)
						neighborValue = grid[i - 1][j][k];
					else
						neighborValue = BigInt.ZERO;
					if (neighborValue.compareTo(value) < 0)
						neighbors.add(new Neighbor<BigInt>(LEFT, neighborValue));
					if (j < grid.length - 1)
						neighborValue = grid[i][j + 1][k];
					else
						neighborValue = BigInt.ZERO;
					if (neighborValue.compareTo(value) < 0)
						neighbors.add(new Neighbor<BigInt>(UP, neighborValue));
					if (j > 0)
						neighborValue = grid[i][j - 1][k];
					else
						neighborValue = BigInt.ZERO;
					if (neighborValue.compareTo(value) < 0)
						neighbors.add(new Neighbor<BigInt>(DOWN, neighborValue));
					if (k < grid.length - 1)
						neighborValue = grid[i][j][k + 1];
					else
						neighborValue = BigInt.ZERO;
					if (neighborValue.compareTo(value) < 0)
						neighbors.add(new Neighbor<BigInt>(FRONT, neighborValue));
					if (k > 0)
						neighborValue = grid[i][j][k - 1];
					else
						neighborValue = BigInt.ZERO;
					if (neighborValue.compareTo(value) < 0)
						neighbors.add(new Neighbor<BigInt>(BACK, neighborValue));
					
					if (neighbors.size() > 0) {
						//sort neighbors by value
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
						//apply algorithm rules to redistribute value
						boolean isFirst = true;
						BigInt previousNeighborValue = null;
						for (int neighborIndex = neighbors.size() - 1; neighborIndex >= 0; neighborIndex--,isFirst = false) {
							neighborValue = neighbors.get(neighborIndex).getValue();
							if (isFirst || !neighborValue.equals(previousNeighborValue)) {
								//Add one for the current cell
								int shareCount = neighbors.size() + 1;
								BigInt toShare = value.subtract(neighborValue);
								BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
								BigInt share = shareAndRemainder[0];
								if (!share.equals(BigInt.ZERO)) {
									checkBoundsReached(i + indexOffset, j + indexOffset, k + indexOffset, newGrid.length);
									changed = true;
									//The current cell keeps the remainder and one share
									value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
									for (Neighbor<BigInt> neighbor : neighbors) {
										int[] nc = getNeighborCoordinates(i, j, k, neighbor.getDirection());
										newGrid[nc[0] + indexOffset][nc[1] + indexOffset][nc[2] + indexOffset] = 
												newGrid[nc[0] + indexOffset][nc[1] + indexOffset][nc[2] + indexOffset].add(share);
									}
								}
								previousNeighborValue = neighborValue;
							}
							neighbors.remove(neighborIndex);
						}	
					}					
					newGrid[i + indexOffset][j + indexOffset][k + indexOffset] = 
							newGrid[i + indexOffset][j + indexOffset][k + indexOffset].add(value);
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
	
	private void checkBoundsReached(int i, int j, int k, int length) {
		if (i == 1 || i == length - 2 || 
			j == 1 || j == length - 2 || 
			k == 1 || k == length - 2) {
			boundsReached = true;
		}
	}
	
	private static int[] getNeighborCoordinates(int x, int y, int z, byte direction) {
		switch(direction) {
		case UP:
			y++;
			break;
		case DOWN:
			y--;
			break;
		case RIGHT:
			x++;
			break;
		case LEFT:
			x--;
			break;
		case FRONT:
			z++;
			break;
		case BACK:
			z--;
			break;
		}
		return new int[]{
			x, y, z
		};
	}
	
	@Override
	public BigInt getFromPosition(int x, int y, int z) {	
		int i = originIndex + x;
		int j = originIndex + y;
		int k = originIndex + z;
		if (i < 0 || i > grid.length - 1 
				|| j < 0 || j > grid.length - 1
				|| k < 0 || k > grid.length - 1) {
			//If the passed coordinates are outside the array, the value will be zero
			return BigInt.ZERO;
		} else {
			return grid[i][j][k];
		}
	}
	
	@Override
	public BigInt getFromAsymmetricPosition(int x, int y, int z) {
		return getFromPosition(x, y, z);
	}
	
	@Override
	public int getAsymmetricMaxX() {
		int arrayMaxX = grid.length - 1 - originIndex;
		int valuesMaxX;
		if (boundsReached) {
			valuesMaxX = arrayMaxX;
		} else {
			valuesMaxX = arrayMaxX - 1;
		}
		return valuesMaxX;
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
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		return "Aether";
	}
	
	@Override
	public String getSubfolderPath() {
		return getName() + "/3D/" + folderName;
	}
}