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
package cellularautomata.automata;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cellularautomata.evolvinggrid.SymmetricEvolvingLongGrid4D;

/**
 * Simplified implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 4D, with a single source initial configuration, for review and testing purposes
 * 
 * @author Jaume
 *
 */
public class AetherSimple4D implements SymmetricEvolvingLongGrid4D {	
	
	public static final long MAX_INITIAL_VALUE = Long.MAX_VALUE;
	public static final long MIN_INITIAL_VALUE = -2635249153387078803L;
	
	private static final byte W_POSITIVE = 0;
	private static final byte W_NEGATIVE = 1;
	private static final byte X_POSITIVE = 2;
	private static final byte X_NEGATIVE = 3;
	private static final byte Y_POSITIVE = 4;
	private static final byte Y_NEGATIVE = 5;
	private static final byte Z_POSITIVE = 6;
	private static final byte Z_NEGATIVE = 7;
	
	/** 4D array representing the grid **/
	private long[][][][] grid;
	
	private long initialValue;
	private long currentStep;
	
	/** The indexes of the origin within the array */
	private int originIndex;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public AetherSimple4D(long initialValue) {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of long type
			throw new IllegalArgumentException("Initial value cannot be smaller than -2,635,249,153,387,078,803. Use a greater initial value or a different implementation.");
		}
		this.initialValue = initialValue;
		int side = 5;
		grid = new long[side][side][side][side];
		originIndex = (side - 1)/2;
		grid[originIndex][originIndex][originIndex][originIndex] = this.initialValue;
		boundsReached = false;
		//Set the current step to zero
		currentStep = 0;
	}
	
	@Override
	public boolean nextStep(){
		//Use new array to store the values of the next step
		long[][][][] newGrid = null;
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new long[grid.length + 2][grid[0].length + 2][grid[0][0].length + 2][grid[0][0][0].length + 2];
			//The offset between the indexes of the new and old array
			indexOffset = 1;
		} else {
			newGrid = new long[grid.length][grid[0].length][grid[0][0].length][grid[0][0][0].length];
		}
		boolean changed = false;
		//For every position
		for (int w = 0; w < grid.length; w++) {
			for (int x = 0; x < grid[0].length; x++) {
				for (int y = 0; y < grid[0][0].length; y++) {
					for (int z = 0; z < grid[0][0][0].length; z++) {
						long value = grid[w][x][y][z];
						List<Neighbor<Long>> neighbors = new ArrayList<Neighbor<Long>>(8);						
						long neighborValue;
						if (w < grid.length - 1)
							neighborValue = grid[w + 1][x][y][z];
						else
							neighborValue = 0;
						if (neighborValue < value)
							neighbors.add(new Neighbor<Long>(W_POSITIVE, neighborValue));
						if (w > 0)
							neighborValue = grid[w - 1][x][y][z];
						else
							neighborValue = 0;
						if (neighborValue < value)
							neighbors.add(new Neighbor<Long>(W_NEGATIVE, neighborValue));
						if (x < grid[w].length - 1)
							neighborValue = grid[w][x + 1][y][z];
						else
							neighborValue = 0;
						if (neighborValue < value)
							neighbors.add(new Neighbor<Long>(X_POSITIVE, neighborValue));
						if (x > 0)
							neighborValue = grid[w][x - 1][y][z];
						else
							neighborValue = 0;
						if (neighborValue < value)
							neighbors.add(new Neighbor<Long>(X_NEGATIVE, neighborValue));
						if (y < grid[w][x].length - 1)
							neighborValue = grid[w][x][y + 1][z];
						else
							neighborValue = 0;
						if (neighborValue < value)
							neighbors.add(new Neighbor<Long>(Y_POSITIVE, neighborValue));
						if (y > 0)
							neighborValue = grid[w][x][y - 1][z];
						else
							neighborValue = 0;
						if (neighborValue < value)
							neighbors.add(new Neighbor<Long>(Y_NEGATIVE, neighborValue));
						if (z < grid[w][x][y].length - 1)
							neighborValue = grid[w][x][y][z + 1];
						else
							neighborValue = 0;
						if (neighborValue < value)
							neighbors.add(new Neighbor<Long>(Z_POSITIVE, neighborValue));
						if (z > 0)
							neighborValue = grid[w][x][y][z - 1];
						else
							neighborValue = 0;
						if (neighborValue < value)
							neighbors.add(new Neighbor<Long>(Z_NEGATIVE, neighborValue));
						
						if (neighbors.size() > 0) {
							//sort
							boolean sorted = false;
							while (!sorted) {
								sorted = true;
								for (int i = neighbors.size() - 2; i >= 0; i--) {
									Neighbor<Long> next = neighbors.get(i+1);
									if (neighbors.get(i).getValue() > next.getValue()) {
										sorted = false;
										neighbors.remove(i+1);
										neighbors.add(i, next);
									}
								}
							}
							//divide
							boolean isFirst = true;
							long previousNeighborValue = 0;
							for (int i = neighbors.size() - 1; i >= 0; i--,isFirst = false) {
								neighborValue = neighbors.get(i).getValue();
								if (neighborValue != previousNeighborValue || isFirst) {
									int shareCount = neighbors.size() + 1;
									long toShare = value - neighborValue;
									long share = toShare/shareCount;
									if (share != 0) {
										checkBoundsReached(w + indexOffset, x + indexOffset, y + indexOffset, z + indexOffset, newGrid.length);
										changed = true;
										value = value - toShare + toShare%shareCount + share;
										for (Neighbor<Long> neighbor : neighbors) {
											int[] nc = getNeighborCoordinates(w, x, y, z, neighbor.getDirection());
											newGrid[nc[0] + indexOffset][nc[1] + indexOffset][nc[2] + indexOffset][nc[3] + indexOffset] += share;
										}
									}
									previousNeighborValue = neighborValue;
								}
								neighbors.remove(i);
							}	
						}					
						newGrid[w + indexOffset][x + indexOffset][y + indexOffset][z + indexOffset] += value;
					}
				}
			}
		}
		//Replace the old array with the new one
		grid = newGrid;
		//Update the index of the origin
		originIndex += indexOffset;
		//Increase the current step by one
		currentStep++;
		//Return whether or not the state of the grid changed
		return changed;
	}
	
	private void checkBoundsReached(int w, int x, int y, int z, int length) {
		if (w == 1 || w == length - 2 || 
			x == 1 || x == length - 2 || 
			y == 1 || y == length - 2 || 
			z == 1 || z == length - 2) {
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
	public long getFromPosition(int w, int x, int y, int z){
		int arrayW = originIndex + w;
		int arrayX = originIndex + x;
		int arrayY = originIndex + y;
		int arrayZ = originIndex + z;
		if (arrayW < 0 || arrayW > grid.length - 1 
				|| arrayX < 0 || arrayX > grid.length - 1 
				|| arrayY < 0 || arrayY > grid[0].length - 1
				|| arrayZ < 0 || arrayZ > grid[0][0].length - 1) {
			//If the entered position is outside the array the value will be the background value
			return 0;
		} else {
			//Note that the positions whose value hasn't been defined have value zero by default
			return grid[arrayW][arrayX][arrayY][arrayZ];
		}
	}
	
	@Override
	public int getMinW() {
		int arrayMinW = - originIndex;
		int valuesMinW;
		if (boundsReached) {
			valuesMinW = arrayMinW;
		} else {
			valuesMinW = arrayMinW + 1;
		}
		return valuesMinW;
	}

	@Override
	public int getMaxW() {
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
	public int getAsymmetricMinW(int x, int y, int z) {
		return Math.max(Math.max(x, y), z);
	}

	@Override
	public int getAsymmetricMaxW(int x, int y, int z) {
		return getAsymmetricMaxW();
	}

	@Override
	public int getAsymmetricMinW() {
		return 0;
	}

	@Override
	public int getAsymmetricMaxW() {
		return getMaxW();
	}

	@Override
	public int getAsymmetricMinX() {
		return 0;
	}

	@Override
	public int getAsymmetricMaxX() {
		return getMaxW();
	}

	@Override
	public int getAsymmetricMinY() {
		return 0;
	}

	@Override
	public int getAsymmetricMaxY() {
		return getMaxW();
	}

	@Override
	public int getAsymmetricMinZ() {
		return 0;
	}
	
	@Override
	public int getAsymmetricMaxZ() {
		return getMaxW();
	}

	@Override
	public int getAsymmetricMinWAtZ(int z) {
		return z;
	}

	@Override
	public int getAsymmetricMinWAtXZ(int x, int z) {
		return x;
	}

	@Override
	public int getAsymmetricMinWAtYZ(int y, int z) {
		return y;
	}

	@Override
	public int getAsymmetricMaxWAtZ(int z) {
		return getAsymmetricMaxW(); //TODO: check actual value? store all max values?
	}

	@Override
	public int getAsymmetricMaxWAtXZ(int x, int z) {
		return getAsymmetricMaxW();
	}

	@Override
	public int getAsymmetricMaxWAtYZ(int y, int z) {
		return getAsymmetricMaxW();
	}

	@Override
	public int getAsymmetricMinXAtZ(int z) {
		return z;
	}

	@Override
	public int getAsymmetricMinXAtWZ(int w, int z) {
		return z;
	}

	@Override
	public int getAsymmetricMinXAtYZ(int y, int z) {
		return y;
	}

	@Override
	public int getAsymmetricMinX(int w, int y, int z) {
		return y;
	}

	@Override
	public int getAsymmetricMaxXAtZ(int z) {
		return getAsymmetricMaxX();
	}

	@Override
	public int getAsymmetricMaxXAtWZ(int w, int z) {
		return Math.min(getAsymmetricMaxX(), w);
	}

	@Override
	public int getAsymmetricMaxXAtYZ(int y, int z) {
		return getAsymmetricMaxX();
	}

	@Override
	public int getAsymmetricMaxX(int w, int y, int z) {
		return Math.min(getAsymmetricMaxX(), w);
	}

	@Override
	public int getAsymmetricMinYAtZ(int z) {
		return z;
	}

	@Override
	public int getAsymmetricMaxYAtWZ(int w, int z) {
		return Math.min(getAsymmetricMaxY(), w);
	}

	@Override
	public int getAsymmetricMinYAtXZ(int x, int z) {
		return z;
	}

	@Override
	public int getAsymmetricMinY(int w, int x, int z) {
		return z;
	}

	@Override
	public int getAsymmetricMaxYAtZ(int z) {
		return getAsymmetricMaxY();
	}

	@Override
	public int getAsymmetricMinYAtWZ(int w, int z) {
		return z;
	}

	@Override
	public int getAsymmetricMaxYAtXZ(int x, int z) {
		return Math.min(getAsymmetricMaxY(), x);
	}

	@Override
	public int getAsymmetricMaxY(int w, int x, int z) {
		return Math.min(getAsymmetricMaxY(), x);
	}
	
	@Override
	public int getAsymmetricMinXAtW(int w) {
		return 0;
	}

	@Override
	public int getAsymmetricMaxXAtW(int w) {
		return Math.min(getAsymmetricMaxX(), w);
	}

	@Override
	public int getAsymmetricMinYAtWX(int w, int x) {
		return 0;
	}

	@Override
	public int getAsymmetricMaxYAtWX(int w, int x) {
		return Math.min(getAsymmetricMaxY(), x);//check that x <= w?
	}

	@Override
	public int getAsymmetricMinZ(int w, int x, int y) {
		return 0;
	}

	@Override
	public int getAsymmetricMaxZ(int w, int x, int y) {
		return Math.min(getAsymmetricMaxZ(), y);
	}
	
	@Override
	public int getAsymmetricMinYAtW(int w) {
		return 0;
	}

	@Override
	public int getAsymmetricMaxYAtW(int w) {
		return Math.min(getAsymmetricMaxY(), w);
	}

	@Override
	public int getAsymmetricMinZAtW(int w) {
		return 0;
	}

	@Override
	public int getAsymmetricMaxZAtW(int w) {
		return Math.min(getAsymmetricMaxZ(), w);
	}

	@Override
	public long getFromAsymmetricPosition(int w, int x, int y, int z) {
		return getFromPosition(w, x, y, z);
	}

	@Override
	public long getStep() {
		return currentStep;
	}
	
	/**
	 * Returns the initial value
	 * 
	 * @return the value at the origin at step 0
	 */
	public long getIntialValue() {
		return initialValue;
	}

	@Override
	public String getName() {
		return "Aether4D";
	}

	@Override
	public String getSubFolderPath() {
		return getName() + "/" + initialValue;
	}
	
	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}
}