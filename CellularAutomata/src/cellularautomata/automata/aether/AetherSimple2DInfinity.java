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

import org.apache.commons.math3.fraction.BigFraction;

import cellularautomata.Utils;
import cellularautomata.automata.Neighbor;
import cellularautomata.model2d.IsotropicSquareModelA;
import cellularautomata.model2d.SymmetricNumericModel2D;

/**
 * Simplified implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 2D, with a single source initial configuration of infinity, for review and testing purposes
 * 
 * @author Jaume
 *
 */
public class AetherSimple2DInfinity implements SymmetricNumericModel2D<BigFraction>, IsotropicSquareModelA {	
	
	private static final byte UP = 0;
	private static final byte DOWN = 1;
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;
	
	/** 2D array representing the grid **/
	private BigFraction[][] grid;
	
	private long step;
	private final boolean isPositive;
	
	/** The indexes of the origin within the array */
	private int originIndex;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	
	public AetherSimple2DInfinity(boolean isPositive) {
		this.isPositive = isPositive;
		//initial side of the array, will be increased as needed
		int side = 5;
		grid = new BigFraction[side][side];
		Utils.fillArray(grid, BigFraction.ZERO);
		originIndex = (side - 1)/2;
		grid[originIndex][originIndex] = isPositive? BigFraction.ONE : BigFraction.MINUS_ONE;
		boundsReached = false;
		//Set the current step to zero
		step = 0;
	}
	
	@Override
	public boolean nextStep() {
		//Use new array to store the values of the next step
		BigFraction[][] newGrid = null;
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newGrid = new BigFraction[grid.length + 2][grid.length + 2];
			//The offset between the indexes of the new and old array
			indexOffset = 1;
		} else {
			newGrid = new BigFraction[grid.length][grid.length];
		}
		Utils.fillArray(newGrid, BigFraction.ZERO);
		//For every cell
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				BigFraction value = grid[i][j];
				//make list of von Neumann neighbors with value smaller than current cell's value
				List<Neighbor<BigFraction>> neighbors = new ArrayList<Neighbor<BigFraction>>(4);						
				BigFraction neighborValue;
				if (i < grid.length - 1)
					neighborValue = grid[i + 1][j];
				else
					neighborValue = BigFraction.ZERO;
				if (neighborValue.compareTo(value) < 0)
					neighbors.add(new Neighbor<BigFraction>(RIGHT, neighborValue));
				if (i > 0)
					neighborValue = grid[i - 1][j];
				else
					neighborValue = BigFraction.ZERO;
				if (neighborValue.compareTo(value) < 0)
					neighbors.add(new Neighbor<BigFraction>(LEFT, neighborValue));
				if (j < grid[i].length - 1)
					neighborValue = grid[i][j + 1];
				else
					neighborValue = BigFraction.ZERO;
				if (neighborValue.compareTo(value) < 0)
					neighbors.add(new Neighbor<BigFraction>(UP, neighborValue));
				if (j > 0)
					neighborValue = grid[i][j - 1];
				else
					neighborValue = BigFraction.ZERO;
				if (neighborValue.compareTo(value) < 0)
					neighbors.add(new Neighbor<BigFraction>(DOWN, neighborValue));

				if (neighbors.size() > 0) {
					//sort neighbors by value
					boolean sorted = false;
					while (!sorted) {
						sorted = true;
						for (int neighborIndex = neighbors.size() - 2; neighborIndex >= 0; neighborIndex--) {
							Neighbor<BigFraction> next = neighbors.get(neighborIndex+1);
							if (neighbors.get(neighborIndex).getValue().compareTo(next.getValue()) > 0) {
								sorted = false;
								neighbors.remove(neighborIndex+1);
								neighbors.add(neighborIndex, next);
							}
						}
					}
					//apply algorithm rules to redistribute value
					boolean isFirst = true;
					BigFraction previousNeighborValue = null;
					for (int neighborIndex = neighbors.size() - 1; neighborIndex >= 0; neighborIndex--,isFirst = false) {
						neighborValue = neighbors.get(neighborIndex).getValue();
						if (isFirst || !neighborValue.equals(previousNeighborValue)) {
							//Add one for the current cell
							int shareCount = neighbors.size() + 1;
							BigFraction toShare = value.subtract(neighborValue);
							BigFraction share = toShare.divide(shareCount);
							checkBoundsReached(i + indexOffset, j + indexOffset, newGrid.length);
							//The current cell keeps one share
							value = value.subtract(toShare).add(share);
							for (Neighbor<BigFraction> neighbor : neighbors) {
								int[] nc = getNeighborCoordinates(i, j, neighbor.getDirection());
								newGrid[nc[0] + indexOffset][nc[1] + indexOffset] = 
										newGrid[nc[0] + indexOffset][nc[1] + indexOffset].add(share);
							}
							previousNeighborValue = neighborValue;
						}
						neighbors.remove(neighborIndex);
					}	
				}					
				newGrid[i + indexOffset][j + indexOffset] = 
						newGrid[i + indexOffset][j + indexOffset].add(value);
			}
		}
		//Replace the old array with the new one
		grid = newGrid;
		//Update the index of the origin
		originIndex += indexOffset;
		//Increase the current step by one
		step++;
		//Return whether or not the state of the grid changed
		return true;
	}
	
	private void checkBoundsReached(int i, int j, int length) {
		if (i == 1 || i == length - 2 || 
			j == 1 || j == length - 2) {
			boundsReached = true;
		}
	}
	
	private static int[] getNeighborCoordinates(int x, int y, byte direction) {
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
		}
		return new int[]{
			x, y
		};
	}
	
	@Override
	public BigFraction getFromPosition(int x, int y) {	
		int i = originIndex + x;
		int j = originIndex + y;
		if (i < 0 || i > grid.length - 1 
				|| j < 0 || j > grid.length - 1) {
			//If the passed coordinates are outside the array, the value will be zero
			return BigFraction.ZERO;
		} else {
			return grid[i][j];
		}
	}
	
	@Override
	public BigFraction getFromAsymmetricPosition(int x, int y) {
		return getFromPosition(x, y);
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
		String path = getName() + "/2D/";
		if (!isPositive) path += "-";
		path += "infinity";
		return path;
	}
}