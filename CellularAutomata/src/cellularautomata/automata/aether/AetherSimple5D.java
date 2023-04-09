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

import cellularautomata.automata.Neighbor;
import cellularautomata.model5d.IsotropicHypercubicModel5DA;
import cellularautomata.model5d.SymmetricLongModel5D;

/**
 * Simplified implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 5D, with a single source initial configuration, for review and testing purposes
 * 
 * @author Jaume
 *
 */
public class AetherSimple5D implements SymmetricLongModel5D, IsotropicHypercubicModel5DA {	

	public static final long MAX_INITIAL_VALUE = Long.MAX_VALUE;
	public static final long MIN_INITIAL_VALUE = -2049638230412172401L;
	
	private static final byte V_POSITIVE = 8;
	private static final byte V_NEGATIVE = 9;
	private static final byte W_POSITIVE = 0;
	private static final byte W_NEGATIVE = 1;
	private static final byte X_POSITIVE = 2;
	private static final byte X_NEGATIVE = 3;
	private static final byte Y_POSITIVE = 4;
	private static final byte Y_NEGATIVE = 5;
	private static final byte Z_POSITIVE = 6;
	private static final byte Z_NEGATIVE = 7;
	
	/** 5D array representing the grid **/
	private long[][][][][] grid;
	
	private final long initialValue;
	private long step;
	
	/** The indexes of the origin within the array */
	private int originIndex;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;	

	/** Whether or not the state of the model changed between the current and the previous step **/
	private Boolean changed = null;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public AetherSimple5D(long initialValue) {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of long type
			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
		}
		this.initialValue = initialValue;
		int side = 5;
		grid = new long[side][side][side][side][side];
		originIndex = (side - 1)/2;
		grid[originIndex][originIndex][originIndex][originIndex][originIndex] = this.initialValue;
		boundsReached = false;
		//Set the current step to zero
		step = 0;
	}
	
	@Override
	public Boolean nextStep() {
		//Use new array to store the values of the next step
		long[][][][][] newGrid = null;
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new long[grid.length + 2][grid.length + 2][grid.length + 2][grid.length + 2][grid.length + 2];
			//The offset between the indexes of the new and old array
			indexOffset = 1;
		} else {
			newGrid = new long[grid.length][grid.length][grid.length][grid.length][grid.length];
		}
		boolean changed = false;
		//For every cell
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				for (int k = 0; k < grid.length; k++) {
					for (int l = 0; l < grid.length; l++) {
						for (int m = 0; m < grid.length; m++) {
							long value = grid[i][j][k][l][m];
							List<Neighbor<Long>> neighbors = new ArrayList<Neighbor<Long>>(10);						
							long neighborValue;
							if (i < grid.length - 1)
								neighborValue = grid[i + 1][j][k][l][m];
							else
								neighborValue = 0;
							if (neighborValue < value)
								neighbors.add(new Neighbor<Long>(V_POSITIVE, neighborValue));
							if (i > 0)
								neighborValue = grid[i - 1][j][k][l][m];
							else
								neighborValue = 0;
							if (neighborValue < value)
								neighbors.add(new Neighbor<Long>(V_NEGATIVE, neighborValue));
							if (j < grid.length - 1)
								neighborValue = grid[i][j + 1][k][l][m];
							else
								neighborValue = 0;
							if (neighborValue < value)
								neighbors.add(new Neighbor<Long>(W_POSITIVE, neighborValue));
							if (j > 0)
								neighborValue = grid[i][j - 1][k][l][m];
							else
								neighborValue = 0;
							if (neighborValue < value)
								neighbors.add(new Neighbor<Long>(W_NEGATIVE, neighborValue));
							if (k < grid.length - 1)
								neighborValue = grid[i][j][k + 1][l][m];
							else
								neighborValue = 0;
							if (neighborValue < value)
								neighbors.add(new Neighbor<Long>(X_POSITIVE, neighborValue));
							if (k > 0)
								neighborValue = grid[i][j][k - 1][l][m];
							else
								neighborValue = 0;
							if (neighborValue < value)
								neighbors.add(new Neighbor<Long>(X_NEGATIVE, neighborValue));
							if (l < grid.length - 1)
								neighborValue = grid[i][j][k][l + 1][m];
							else
								neighborValue = 0;
							if (neighborValue < value)
								neighbors.add(new Neighbor<Long>(Y_POSITIVE, neighborValue));
							if (l > 0)
								neighborValue = grid[i][j][k][l - 1][m];
							else
								neighborValue = 0;
							if (neighborValue < value)
								neighbors.add(new Neighbor<Long>(Y_NEGATIVE, neighborValue));
							if (m < grid.length - 1)
								neighborValue = grid[i][j][k][l][m + 1];
							else
								neighborValue = 0;
							if (neighborValue < value)
								neighbors.add(new Neighbor<Long>(Z_POSITIVE, neighborValue));
							if (m > 0)
								neighborValue = grid[i][j][k][l][m - 1];
							else
								neighborValue = 0;
							if (neighborValue < value)
								neighbors.add(new Neighbor<Long>(Z_NEGATIVE, neighborValue));
							
							if (neighbors.size() > 0) {
								//sort
								boolean sorted = false;
								while (!sorted) {
									sorted = true;
									for (int neighborIndex = neighbors.size() - 2; neighborIndex >= 0; neighborIndex--) {
										Neighbor<Long> next = neighbors.get(neighborIndex+1);
										if (neighbors.get(neighborIndex).getValue() > next.getValue()) {
											sorted = false;
											neighbors.remove(neighborIndex+1);
											neighbors.add(neighborIndex, next);
										}
									}
								}
								//divide
								boolean isFirst = true;
								long previousNeighborValue = 0;
								for (int neighborIndex = neighbors.size() - 1; neighborIndex >= 0; neighborIndex--,isFirst = false) {
									neighborValue = neighbors.get(neighborIndex).getValue();
									if (neighborValue != previousNeighborValue || isFirst) {
										int shareCount = neighbors.size() + 1;
										long toShare = value - neighborValue;
										long share = toShare/shareCount;
										if (share != 0) {
											checkBoundsReached(i + indexOffset, j + indexOffset, k + indexOffset, l + indexOffset, m + indexOffset, newGrid.length);
											changed = true;
											value = value - toShare + toShare%shareCount + share;
											for (Neighbor<Long> neighbor : neighbors) {
												int[] nc = getNeighborCoordinates(i, j, k, l, m, neighbor.getDirection());
												newGrid[nc[0] + indexOffset][nc[1] + indexOffset][nc[2] + indexOffset][nc[3] + indexOffset][nc[4] + indexOffset] += share;
											}
										}
										previousNeighborValue = neighborValue;
									}
									neighbors.remove(neighborIndex);
								}	
							}					
							newGrid[i + indexOffset][j + indexOffset][k + indexOffset][l + indexOffset][m + indexOffset] += value;
						}
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
	
	private void checkBoundsReached(int i, int j, int k, int l, int m, int length) {
		if (i == 1 || i == length - 2 ||
			j == 1 || j == length - 2 || 
			k == 1 || k == length - 2 || 
			l == 1 || l == length - 2 || 
			m == 1 || m == length - 2) {
			boundsReached = true;
		}
	}
	
	private static int[] getNeighborCoordinates(int v, int w, int x, int y, int z, byte direction) {
		switch(direction) {
		case V_POSITIVE:
			v++;
			break;
		case V_NEGATIVE:
			v--;
			break;
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
			v, w, x, y, z
		};
	}
	
	@Override
	public long getFromPosition(int v, int w, int x, int y, int z) {
		int i = originIndex + v;
		int j = originIndex + w;
		int k = originIndex + x;
		int l = originIndex + y;
		int m = originIndex + z;
		//Note that the indexes whose value hasn't been defined have value zero by default
		return grid[i][j][k][l][m];
	}
	
	@Override
	public long getFromAsymmetricPosition(int v, int w, int x, int y, int z) {
		return getFromPosition(v, w, x, y, z);
	}

	@Override
	public int getAsymmetricMaxV() {
		int arrayMaxV = grid.length - 1 - originIndex;
		int valuesMaxV;
		if (boundsReached) {
			valuesMaxV = arrayMaxV;
		} else {
			valuesMaxV = arrayMaxV - 1;
		}
		return valuesMaxV;
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
	public long getIntialValue() {
		return initialValue;
	}

	@Override
	public String getName() {
		return "Aether";
	}

	@Override
	public String getSubfolderPath() {
		return getName() + "/5D/" + initialValue;
	}
	
	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}
}