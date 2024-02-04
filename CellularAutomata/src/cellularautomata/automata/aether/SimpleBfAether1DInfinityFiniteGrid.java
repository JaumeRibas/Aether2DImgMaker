/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2024 Jaume Ribas

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
import cellularautomata.model1d.NumericModel1D;

/**
 * Simplified implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 1D, with a finite grid and a single source initial configuration of infinity, for review and testing purposes
 * 
 * @author Jaume
 *
 */
public class SimpleBfAether1DInfinityFiniteGrid implements NumericModel1D<BigFraction> {
	
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;
	
	/** A 1D array representing the grid */
	private BigFraction[] grid;
	
	private long step;
	private final boolean isPositive;
	private final int side;
	/** Whether or not the state of the model changed between the current and the previous step **/
	private Boolean changed = null;
	
	public SimpleBfAether1DInfinityFiniteGrid(boolean isPositive, int side) {
		if (side < 1) {
			throw new IllegalArgumentException("Grid side cannot be smaller than one.");
		}
		this.isPositive = isPositive;
		this.side = side;
		grid = new BigFraction[side];
		Arrays.fill(grid, BigFraction.ZERO);
		grid[0] = isPositive? BigFraction.ONE : BigFraction.MINUS_ONE;
		step = 0;
	}
	
	@Override
	public Boolean nextStep() {
		//Use new array to store the values of the next step
		BigFraction[] newGrid = null;
		//If at the previous step the values reached the edge, make the new array bigger
		newGrid = new BigFraction[grid.length];
		Arrays.fill(newGrid, BigFraction.ZERO);
		boolean changed = false;
		//For every cell
		for (int index = 0; index < grid.length; index++) {
			//Distribute the cell's value among its neighbors (von Neumann) using the algorithm
			
			//Get the cell's value
			BigFraction value = grid[index];
			//Get a list of the neighbors whose value is smaller than the one at the current cell
			List<Neighbor<BigFraction>> neighbors = new ArrayList<Neighbor<BigFraction>>(2);						
			BigFraction neighborValue;
			if (index < grid.length - 1) {
				neighborValue = grid[index + 1];
			} else {
				neighborValue = grid[0];
			}
			if (neighborValue.compareTo(value) < 0)
				neighbors.add(new Neighbor<BigFraction>(RIGHT, neighborValue));
			if (index > 0) {
				neighborValue = grid[index - 1];
			} else {
				neighborValue = grid[grid.length - 1];
			}
			if (neighborValue.compareTo(value) < 0)
				neighbors.add(new Neighbor<BigFraction>(LEFT, neighborValue));

			//If there are any
			if (neighbors.size() > 0) {
				changed = true;
				if (neighbors.size() > 1) {
					//Sort them by value in ascending order
					Neighbor<BigFraction> next = neighbors.get(1);
					if (neighbors.get(0).getValue().compareTo(next.getValue()) > 0) {
						neighbors.remove(1);
						neighbors.add(0, next);
					}
				}
				boolean isFirst = true;
				BigFraction previousNeighborValue = null;
				//Apply the algorithm
				for (int i = neighbors.size() - 1; i >= 0; i--,isFirst = false) {
					neighborValue = neighbors.get(i).getValue();
					if (!neighborValue.equals(previousNeighborValue) || isFirst) {
						//Add one for the current cell
						int shareCount = neighbors.size() + 1;
						BigFraction toShare = value.subtract(neighborValue);
						BigFraction share = toShare.divide(shareCount);
						//The current cell keeps one share
						value = value.subtract(toShare).add(share);
						for (Neighbor<BigFraction> n : neighbors) {
							int nc = getNeighborIndex(index, n.getDirection());
							newGrid[nc] = newGrid[nc].add(share);
						}
						previousNeighborValue = neighborValue;
					}
					neighbors.remove(i);
				}	
			}					
			newGrid[index] = newGrid[index].add(value);
		}
		//Replace the old array with the new one
		this.grid = newGrid;
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
	
	private int getNeighborIndex(int index, byte direction) {
		if (direction == RIGHT) {
			if (index == side - 1) {
				index = 0;
			} else {
				index++;
			}
		} else {
			if (index == 0) {
				index = side - 1;
			} else {
				index--;
			}
		}
		return index;
	}
	
	@Override
	public BigFraction getFromPosition(int x) {
		//Transform passed coordinates to grid coordinates
		x = x < 0 ? side - 1 + x%side : x%side;
		return grid[x];
	}
	
	@Override
	public int getMaxX() {
		return side - 1;
	}
	
	@Override
	public int getMinX() {
		return 0;
	}
	
	@Override
	public long getStep() {
		return step;
	}

	@Override
	public String getName() {
		return "Aether";
	}

	@Override
	public String getSubfolderPath() {
		String path = getName() + "/1D/finite_grid/" + side + "/";
		if (!isPositive) path += "-";
		path += "infinity";
		return path;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}
}
