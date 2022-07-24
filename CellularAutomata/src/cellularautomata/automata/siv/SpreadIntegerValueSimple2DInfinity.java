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
package cellularautomata.automata.siv;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.math3.fraction.BigFraction;

import cellularautomata.model2d.IsotropicSquareModelA;
import cellularautomata.model2d.SymmetricNumericModel2D;

/**
 * Implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/SIV-Cellular-Automaton-Definition">Spread Integer Value</a> cellular automaton in 2D with a single source initial configuration of infinity
 * 
 * @author Jaume
 *
 */
public class SpreadIntegerValueSimple2DInfinity implements SymmetricNumericModel2D<BigFraction>, IsotropicSquareModelA {	
	
	/** 2D array representing the grid **/
	private BigFraction[][] grid;
	
	private long step;
	
	/** The indexes of the origin within the array */
	private int originIndex;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	
	public SpreadIntegerValueSimple2DInfinity() {
		//initial side of the array, will be increased as needed
		int side = 5;
		grid = new BigFraction[side][side];
		for (int i = 0; i < grid.length; i++) {
			Arrays.fill(grid[i], BigFraction.ZERO);
		}
		originIndex = (side - 1)/2;
		grid[originIndex][originIndex] = BigFraction.ONE;
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
			newGrid = new BigFraction[grid.length + 2][grid[0].length + 2];
			//The offset between the indexes of the new and old array
			indexOffset = 1;
		} else {
			newGrid = new BigFraction[grid.length][grid[0].length];
		}
		for (int i = 0; i < newGrid.length; i++) {
			Arrays.fill(newGrid[i], BigFraction.ZERO);
		}
		//For every position
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[0].length; y++) {
				BigFraction value = grid[x][y];
				if (!value.equals(BigFraction.ZERO)) {
					//Divide its value by 5
					BigFraction share = value.divide(5);
					//Add the share to the corresponding position in the new array
					newGrid[x + indexOffset][y + indexOffset] = newGrid[x + indexOffset][y + indexOffset].add(share);
					//Add the share to the neighboring positions
					newGrid[x + indexOffset + 1][y + indexOffset] = newGrid[x + indexOffset + 1][y + indexOffset].add(share);
					newGrid[x + indexOffset - 1][y + indexOffset] = newGrid[x + indexOffset - 1][y + indexOffset].add(share);
					newGrid[x + indexOffset][y + indexOffset + 1] = newGrid[x + indexOffset][y + indexOffset + 1].add(share);
					newGrid[x + indexOffset][y + indexOffset - 1] = newGrid[x + indexOffset][y + indexOffset - 1].add(share);
					//Check whether or not we reached the edge of the array
					if (x == 1 || x == this.grid.length - 2 || 
						y == 1 || y == this.grid[0].length - 2) {
						boundsReached = true;
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
		//Return whether or not the state of the grid changed (always changes)
		return true;
	}
	
	@Override
	public BigFraction getFromPosition(int x, int y) {	
		int arrayX = originIndex + x;
		int arrayY = originIndex + y;
		if (arrayX < 0 || arrayX > grid.length - 1 
				|| arrayY < 0 || arrayY > grid[0].length - 1) {
			//If the entered position is outside the array the value will be zero
			return BigFraction.ZERO;
		} else {
			//Note that the positions whose value hasn't been defined have value zero by default
			return grid[arrayX][arrayY];
		}
	}
	
	@Override
	public BigFraction getFromAsymmetricPosition(int x, int y) {
		return getFromPosition(x, y);
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
	public long getStep() {
		return step;
	}
	
	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		return "SpreadIntegerValue";
	}
	
	@Override
	public String getSubfolderPath() {
		return getName() + "/2D/infinity";
	}
}