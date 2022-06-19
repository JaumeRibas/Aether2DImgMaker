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
package cellularautomata.automata.nearaether;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cellularautomata.Constants;
import cellularautomata.Utils;
import cellularautomata.automata.Neighbor;
import cellularautomata.model3d.IsotropicCubicModelA;
import cellularautomata.model3d.SymmetricNumericModel3D;
import cellularautomata.numbers.BigInt;

/**
 * Implementation of a cellular automaton very similar to <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> to showcase its uniqueness.
 * 
 * @author Jaume
 *
 */
public class BigIntNearAether3Simple3D implements SymmetricNumericModel3D<BigInt>, IsotropicCubicModelA, Serializable {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4799014576150501853L;
	
	private static final byte UP = 0;
	private static final byte DOWN = 1;
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;
	private static final byte FRONT = 4;
	private static final byte BACK = 5;
	
	/** 3D array representing the grid **/
	private BigInt[][][] grid;
	
	private long step;
	private BigInt initialValue;
	
	/** The indexes of the origin within the array */
	private int originIndex;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	/**
	 * Used in {@link #getSubfolderPath()} in case the initial value is too big.
	 */
	private String creationTimestamp;
	
	public BigIntNearAether3Simple3D(BigInt initialValue) {
		this.initialValue = initialValue;
		//initial side of the array, will be increased as needed
		int side = 5;
		grid = new BigInt[side][side][side];
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				Arrays.fill(grid[i][j], BigInt.ZERO);
			}
		}
		originIndex = (side - 1)/2;
		grid[originIndex][originIndex][originIndex] = initialValue;
		boundsReached = false;
		//Set the current step to zero
		step = 0;
		creationTimestamp = new Timestamp(System.currentTimeMillis()).toString().replace(":", "");
	}
	
	/**
	 * Creates an instance restoring a backup
	 * 
	 * @param backupPath the path to the backup file to restore.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 */
	public BigIntNearAether3Simple3D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		BigIntNearAether3Simple3D data = (BigIntNearAether3Simple3D) Utils.deserializeFromFile(backupPath);
		grid = data.grid;
		step = data.step;
		initialValue = data.initialValue;
		originIndex = data.originIndex;
		boundsReached = data.boundsReached;
		creationTimestamp = data.creationTimestamp;
	}
	
	@Override
	public boolean nextStep() {
		//Use new array to store the values of the next step
		BigInt[][][] newGrid = null;
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new BigInt[grid.length + 2][grid[0].length + 2][grid[0][0].length + 2];
			//The offset between the indexes of the new and old array
			indexOffset = 1;
		} else {
			newGrid = new BigInt[grid.length][grid[0].length][grid[0][0].length];
		}
		for (int i = 0; i < newGrid.length; i++) {
			for (int j = 0; j < newGrid[i].length; j++) {
				Arrays.fill(newGrid[i][j], BigInt.ZERO);
			}
		}
		boolean changed = false;
		//For every position
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[0].length; y++) {
				for (int z = 0; z < grid[0][0].length; z++) {
					BigInt value = grid[x][y][z];
					//make list of von Neumann neighbors. Get all neighbors as opposed to Aether
					List<Neighbor<BigInt>> neighbors = new ArrayList<Neighbor<BigInt>>(6);						
					BigInt neighborValue;
					if (x < grid.length - 1)
						neighborValue = grid[x + 1][y][z];
					else
						neighborValue = BigInt.ZERO;
					neighbors.add(new Neighbor<BigInt>(RIGHT, neighborValue));
					if (x > 0)
						neighborValue = grid[x - 1][y][z];
					else
						neighborValue = BigInt.ZERO;
					neighbors.add(new Neighbor<BigInt>(LEFT, neighborValue));
					if (y < grid[x].length - 1)
						neighborValue = grid[x][y + 1][z];
					else
						neighborValue = BigInt.ZERO;
					neighbors.add(new Neighbor<BigInt>(UP, neighborValue));
					if (y > 0)
						neighborValue = grid[x][y - 1][z];
					else
						neighborValue = BigInt.ZERO;
					neighbors.add(new Neighbor<BigInt>(DOWN, neighborValue));
					if (z < grid[x][y].length - 1)
						neighborValue = grid[x][y][z + 1];
					else
						neighborValue = BigInt.ZERO;
					neighbors.add(new Neighbor<BigInt>(FRONT, neighborValue));
					if (z > 0)
						neighborValue = grid[x][y][z - 1];
					else
						neighborValue = BigInt.ZERO;
					neighbors.add(new Neighbor<BigInt>(BACK, neighborValue));
					
					//sort neighbors by value
					boolean sorted = false;
					while (!sorted) {
						sorted = true;
						for (int i = neighbors.size() - 2; i >= 0; i--) {
							Neighbor<BigInt> next = neighbors.get(i+1);
							if (neighbors.get(i).getValue().compareTo(next.getValue()) > 0) {
								sorted = false;
								neighbors.remove(i+1);
								neighbors.add(i, next);
							}
						}
					}
					//apply algorithm rules to redistribute value
					boolean isFirst = true;
					BigInt previousNeighborValue = null;
					for (int i = neighbors.size() - 1; i >= 0; i--,isFirst = false) {
						neighborValue = neighbors.get(i).getValue();
						if (isFirst || !neighborValue.equals(previousNeighborValue)) {
							int shareCount = neighbors.size() + 1;
							BigInt toShare = value.subtract(neighborValue);
							BigInt[] shareAndRemainder = toShare.divideAndRemainder(BigInt.valueOf(shareCount));
							BigInt share = shareAndRemainder[0];
							if (!share.equals(BigInt.ZERO)) {
								checkBoundsReached(x + indexOffset, y + indexOffset, z + indexOffset, newGrid.length);
								changed = true;
								value = value.subtract(toShare).add(share).add(shareAndRemainder[1]);
								for (Neighbor<BigInt> neighbor : neighbors) {
									int[] nc = getNeighborCoordinates(x, y, z, neighbor.getDirection());
									newGrid[nc[0] + indexOffset][nc[1] + indexOffset][nc[2] + indexOffset] = 
											newGrid[nc[0] + indexOffset][nc[1] + indexOffset][nc[2] + indexOffset].add(share);
								}
							}
							previousNeighborValue = neighborValue;
						}
						neighbors.remove(i);
					}					
					newGrid[x + indexOffset][y + indexOffset][z + indexOffset] = 
							newGrid[x + indexOffset][y + indexOffset][z + indexOffset].add(value);
				}
			}
		}
		//Replace the old array with the new one
		grid = newGrid;
		//Update the index of the origin
		originIndex += indexOffset;
		//Increase the current step by one
		step++;
		//Return whether or not the state of the grid changed
		return changed;
	}
	
	private void checkBoundsReached(int x, int y, int z, int length) {
		if (x == 1 || x == length - 2 || 
			y == 1 || y == length - 2 || 
			z == 1 || z == length - 2) {
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
		int arrayX = originIndex + x;
		int arrayY = originIndex + y;
		int arrayZ = originIndex + z;
		if (arrayX < 0 || arrayX > grid.length - 1 
				|| arrayY < 0 || arrayY > grid[0].length - 1
				|| arrayZ < 0 || arrayZ > grid[0][0].length - 1) {
			//If the entered position is outside the array the value will be zero
			return BigInt.ZERO;
		} else {
			//Note that the positions whose value hasn't been defined have value zero by default
			return grid[arrayX][arrayY][arrayZ];
		}
	}
	
	@Override
	public BigInt getFromAsymmetricPosition(int x, int y, int z) {
		return getFromPosition(x, y, z);
	}
	
	@Override
	public int getMinX() {
		int arrayMinX = - originIndex;
		int valuesMinX;
		if (boundsReached) {
			valuesMinX = arrayMinX;
		} else {
			valuesMinX = arrayMinX + 1;
		}
		return valuesMinX;
	}
	
	@Override
	public int getMaxX() {
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
	public int getAsymmetricMaxX() {
		return getMaxX();
	}
	
	@Override
	public int getMinY() {
		int arrayMinY = - originIndex;
		int valuesMinY;
		if (boundsReached) {
			valuesMinY = arrayMinY;
		} else {
			valuesMinY = arrayMinY + 1;
		}
		return valuesMinY;
	}
	
	@Override
	public int getMaxY() {
		int arrayMaxY = grid[0].length - 1 - originIndex;
		int valuesMaxY;
		if (boundsReached) {
			valuesMaxY = arrayMaxY;
		} else {
			valuesMaxY = arrayMaxY - 1;
		}
		return valuesMaxY;
	}
	
	@Override
	public int getMinZ() {
		int arrayMinZ = - originIndex;
		int valuesMinZ;
		if (boundsReached) {
			valuesMinZ = arrayMinZ;
		} else {
			valuesMinZ = arrayMinZ + 1;
		}
		return valuesMinZ;
	}
	
	@Override
	public int getMaxZ() {
		int arrayMaxZ = grid[0][0].length - 1 - originIndex;
		int valuesMaxZ;
		if (boundsReached) {
			valuesMaxZ = arrayMaxZ;
		} else {
			valuesMaxZ = arrayMaxZ - 1;
		}
		return valuesMaxZ;
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
		return "NearAether3";
	}
	
	@Override
	public String getSubfolderPath() {
		String strInitialValue = initialValue.toString();
		if (strInitialValue.length() > Constants.MAX_INITIAL_VALUE_LENGTH_IN_PATH)
			strInitialValue = creationTimestamp;
		return getName() + "/3D/" + strInitialValue;
	}
	
	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		Utils.serializeToFile(this, backupPath, backupName);
	}

}