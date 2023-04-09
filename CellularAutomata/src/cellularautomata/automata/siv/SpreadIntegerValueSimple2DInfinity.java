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
package cellularautomata.automata.siv;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.math3.fraction.BigFraction;

import cellularautomata.Utils;
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
		Utils.fillArray(grid, BigFraction.ZERO);
		originIndex = (side - 1)/2;
		grid[originIndex][originIndex] = BigFraction.ONE;
		boundsReached = false;
		//Set the current step to zero
		step = 0;
	}
	
	@Override
	public Boolean nextStep() {
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
				if (!value.equals(BigFraction.ZERO)) {
					//Divide its value by 5
					BigFraction share = value.divide(5);
					//Add the share to the corresponding position in the new array
					newGrid[i + indexOffset][j + indexOffset] = newGrid[i + indexOffset][j + indexOffset].add(share);
					//Add the share to the neighboring cells
					newGrid[i + indexOffset + 1][j + indexOffset] = newGrid[i + indexOffset + 1][j + indexOffset].add(share);
					newGrid[i + indexOffset - 1][j + indexOffset] = newGrid[i + indexOffset - 1][j + indexOffset].add(share);
					newGrid[i + indexOffset][j + indexOffset + 1] = newGrid[i + indexOffset][j + indexOffset + 1].add(share);
					newGrid[i + indexOffset][j + indexOffset - 1] = newGrid[i + indexOffset][j + indexOffset - 1].add(share);
					//Check whether or not we reached the edge of the array
					if (i == 1 || i == this.grid.length - 2 || 
						j == 1 || j == this.grid[0].length - 2) {
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
	public Boolean isChanged() {
		return step == 0 ? null : true;
	}
	
	@Override
	public BigFraction getFromPosition(int x, int y) {	
		int i = originIndex + x;
		int j = originIndex + y;
		return grid[i][j];
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
		return "SpreadIntegerValue";
	}
	
	@Override
	public String getSubfolderPath() {
		return getName() + "/2D/infinity";
	}
}