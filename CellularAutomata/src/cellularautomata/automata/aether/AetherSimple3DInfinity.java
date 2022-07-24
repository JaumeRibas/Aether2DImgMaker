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
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.fraction.BigFraction;

import cellularautomata.automata.Neighbor;
import cellularautomata.model3d.IsotropicCubicModelA;
import cellularautomata.model3d.SymmetricNumericModel3D;

/**
 * Simplified implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 3D, with a single source initial configuration of infinity, for review and testing purposes
 * 
 * @author Jaume
 *
 */
public class AetherSimple3DInfinity implements SymmetricNumericModel3D<BigFraction>, IsotropicCubicModelA {	
	
	private static final byte UP = 0;
	private static final byte DOWN = 1;
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;
	private static final byte FRONT = 4;
	private static final byte BACK = 5;
	
	/** 3D array representing the grid **/
	private BigFraction[][][] grid;
	
	private long step;
	private boolean isPositive;
	
	/** The indexes of the origin within the array */
	private int originIndex;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	
	/**
	 * 
	 * @param isPositive
	 */
	public AetherSimple3DInfinity(boolean isPositive) {
		this.isPositive = isPositive;
		//initial side of the array, will be increased as needed
		int side = 5;
		grid = new BigFraction[side][side][side];
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				Arrays.fill(grid[i][j], BigFraction.ZERO);
			}
		}
		originIndex = (side - 1)/2;
		grid[originIndex][originIndex][originIndex] = isPositive? BigFraction.ONE : BigFraction.MINUS_ONE;
		boundsReached = false;
		//Set the current step to zero
		step = 0;
	}
	
	@Override
	public boolean nextStep() {
		//Use new array to store the values of the next step
		BigFraction[][][] newGrid = null;
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new BigFraction[grid.length + 2][grid[0].length + 2][grid[0][0].length + 2];
			//The offset between the indexes of the new and old array
			indexOffset = 1;
		} else {
			newGrid = new BigFraction[grid.length][grid[0].length][grid[0][0].length];
		}
		for (int i = 0; i < newGrid.length; i++) {
			for (int j = 0; j < newGrid[i].length; j++) {
				Arrays.fill(newGrid[i][j], BigFraction.ZERO);
			}
		}
		boolean changed = false;
		//For every position
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[0].length; y++) {
				for (int z = 0; z < grid[0][0].length; z++) {
					BigFraction value = grid[x][y][z];
					//make list of von Neumann neighbors with value smaller than current position's value
					List<Neighbor<BigFraction>> neighbors = new ArrayList<Neighbor<BigFraction>>(6);						
					BigFraction neighborValue;
					if (x < grid.length - 1)
						neighborValue = grid[x + 1][y][z];
					else
						neighborValue = BigFraction.ZERO;
					if (neighborValue.compareTo(value) < 0)
						neighbors.add(new Neighbor<BigFraction>(RIGHT, neighborValue));
					if (x > 0)
						neighborValue = grid[x - 1][y][z];
					else
						neighborValue = BigFraction.ZERO;
					if (neighborValue.compareTo(value) < 0)
						neighbors.add(new Neighbor<BigFraction>(LEFT, neighborValue));
					if (y < grid[x].length - 1)
						neighborValue = grid[x][y + 1][z];
					else
						neighborValue = BigFraction.ZERO;
					if (neighborValue.compareTo(value) < 0)
						neighbors.add(new Neighbor<BigFraction>(UP, neighborValue));
					if (y > 0)
						neighborValue = grid[x][y - 1][z];
					else
						neighborValue = BigFraction.ZERO;
					if (neighborValue.compareTo(value) < 0)
						neighbors.add(new Neighbor<BigFraction>(DOWN, neighborValue));
					if (z < grid[x][y].length - 1)
						neighborValue = grid[x][y][z + 1];
					else
						neighborValue = BigFraction.ZERO;
					if (neighborValue.compareTo(value) < 0)
						neighbors.add(new Neighbor<BigFraction>(FRONT, neighborValue));
					if (z > 0)
						neighborValue = grid[x][y][z - 1];
					else
						neighborValue = BigFraction.ZERO;
					if (neighborValue.compareTo(value) < 0)
						neighbors.add(new Neighbor<BigFraction>(BACK, neighborValue));
					
					if (neighbors.size() > 0) {
						//sort neighbors by value
						boolean sorted = false;
						while (!sorted) {
							sorted = true;
							for (int i = neighbors.size() - 2; i >= 0; i--) {
								Neighbor<BigFraction> next = neighbors.get(i+1);
								if (neighbors.get(i).getValue().compareTo(next.getValue()) > 0) {
									sorted = false;
									neighbors.remove(i+1);
									neighbors.add(i, next);
								}
							}
						}
						//apply algorithm rules to redistribute value
						boolean isFirst = true;
						BigFraction previousNeighborValue = null;
						for (int i = neighbors.size() - 1; i >= 0; i--,isFirst = false) {
							neighborValue = neighbors.get(i).getValue();
							if (isFirst || !neighborValue.equals(previousNeighborValue)) {
								int shareCount = neighbors.size() + 1;
								BigFraction toShare = value.subtract(neighborValue);
								BigFraction share = toShare.divide(shareCount);
								checkBoundsReached(x + indexOffset, y + indexOffset, z + indexOffset, newGrid.length);
								changed = true;
								value = value.subtract(toShare).add(share);
								for (Neighbor<BigFraction> neighbor : neighbors) {
									int[] nc = getNeighborCoordinates(x, y, z, neighbor.getDirection());
									newGrid[nc[0] + indexOffset][nc[1] + indexOffset][nc[2] + indexOffset] = 
											newGrid[nc[0] + indexOffset][nc[1] + indexOffset][nc[2] + indexOffset].add(share);
								}
								previousNeighborValue = neighborValue;
							}
							neighbors.remove(i);
						}	
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
	public BigFraction getFromPosition(int x, int y, int z) {	
		int arrayX = originIndex + x;
		int arrayY = originIndex + y;
		int arrayZ = originIndex + z;
		if (arrayX < 0 || arrayX > grid.length - 1 
				|| arrayY < 0 || arrayY > grid[0].length - 1
				|| arrayZ < 0 || arrayZ > grid[0][0].length - 1) {
			//If the entered position is outside the array the value will be zero
			return BigFraction.ZERO;
		} else {
			//Note that the positions whose value hasn't been defined have value zero by default
			return grid[arrayX][arrayY][arrayZ];
		}
	}

	@Override
	public BigFraction getFromAsymmetricPosition(int x, int y, int z) {
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
		String path = getName() + "/3D/";
		if (!isPositive) path += "-";
		path += "infinity";
		return path;
	}
	
}