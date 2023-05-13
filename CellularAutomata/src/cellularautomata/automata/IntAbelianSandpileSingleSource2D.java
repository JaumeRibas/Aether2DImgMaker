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
package cellularautomata.automata;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import cellularautomata.Utils;
import cellularautomata.model2d.IsotropicSquareModelA;
import cellularautomata.model2d.SymmetricIntModel2D;

/**
 * Implementation of the <a href="https://en.wikipedia.org/wiki/Abelian_sandpile_model">Abelian sandpile</a> cellular automaton in 2D with synchronous toppling and a single source initial configuration
 * 
 * @author Jaume
 *
 */
public class IntAbelianSandpileSingleSource2D implements SymmetricIntModel2D, IsotropicSquareModelA, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2801220671142073702L;

	private int[][] grid;
	
	private final int initialValue;
	private long step;
	private Boolean changed = null;

	/** Whether or not the values reached the bounds of the array */
	private boolean xBoundReached;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public IntAbelianSandpileSingleSource2D(int initialValue) {
		if (initialValue < 0) {
			throw new IllegalArgumentException("Initial value cannot be less than zero.");
		}
		this.initialValue = initialValue;
		grid = Utils.buildAnisotropic2DIntArray(3);
		grid[0][0] = this.initialValue;
		xBoundReached = false;
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
	public IntAbelianSandpileSingleSource2D(String backupPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		IntAbelianSandpileSingleSource2D data = (IntAbelianSandpileSingleSource2D) Utils.deserializeFromFile(backupPath);
		initialValue = data.initialValue;
		grid = data.grid;
		xBoundReached = data.xBoundReached;
		step = data.step;
		changed = data.changed;
	}
	
	@Override
	public Boolean nextStep() {
		int[][] newGrid = null;
		if (xBoundReached) {
			xBoundReached = false;
			newGrid = new int[grid.length + 1][];
		} else {
			newGrid = new int[grid.length][];
		}
		int maxXMinusOne = newGrid.length - 2;
		boolean changed = false;
		newGrid[0] = new int[1];
		boolean isFirst = true;
		for (int x = 0, nextX = 1; x < grid.length; x = nextX, nextX++, isFirst = false) {
			if (nextX < newGrid.length) {
				newGrid[nextX] = new int[nextX + 1];
			}
			for (int y = 0; y <= x; y++) {
				int value = grid[x][y];
				if (value >= 4) {
					//If any position topples the state changes
					changed = true;
					//Add one to the neighboring positions
					//x+
					newGrid[x+1][y] += 1;
					//x-
					if (x > y) {
						int valueToAdd = 1;
						if (x == y + 1) {
							valueToAdd += 1;
							if (x == 1) {
								valueToAdd += 2;							
							}
						}
						newGrid[x-1][y] += valueToAdd;
					}
					//y+
					if (y < x) {
						int valueToAdd = 1;
						if (y == x - 1) {
							valueToAdd += 1;
						}
						int yy = y+1;
						newGrid[x][yy] += valueToAdd;
					}
					//y-
					if (y > 0) {
						int valueToAdd = 1;
						if (y == 1) {
							valueToAdd += 1;
						}
						newGrid[x][y-1] += valueToAdd;
					}
					
					if (x >= maxXMinusOne) {
						xBoundReached = true;
					}
					newGrid[x][y] += value - 4;
				} else {
					newGrid[x][y] += value;
				}
			}
			if (!isFirst) {
				grid[x-1] = null;
			}
		}
		grid = newGrid;
		step++;
		this.changed = changed;
		return changed;
	}

	@Override
	public Boolean isChanged() {
		return changed;
	}
	
	@Override
	public int getFromPosition(int x, int y) {	
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		if (y > x) {
			int swp = y;
			y = x;
			x = swp;
		}
		if (x < grid.length 
				&& y < grid[x].length) {
			return grid[x][y];
		} else {
			return 0;
		}
	}
	
	@Override
	public int getFromAsymmetricPosition(int x, int y) {	
		return grid[x][y];
	}

	@Override
	public int getAsymmetricMaxX() {
		return grid.length - 1;
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
	public int getInitialValue() {
		return initialValue;
	}

	@Override
	public String getName() {
		return "AbelianSandpile";
	}
	
	@Override
	public String getSubfolderPath() {
		return getName() + "/2D/" + initialValue;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		Utils.serializeToFile(this, backupPath, backupName);
	}
	
}
