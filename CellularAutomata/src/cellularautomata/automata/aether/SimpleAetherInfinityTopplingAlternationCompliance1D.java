/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2025 Jaume Ribas

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

import cellularautomata.Direction;
import cellularautomata.automata.Neighbor;
import cellularautomata.model1d.IsotropicModel1DA;
import cellularautomata.model1d.SymmetricBooleanModel1D;

public class SimpleAetherInfinityTopplingAlternationCompliance1D implements SymmetricBooleanModel1D, IsotropicModel1DA {
	
	/** A 1D array representing the grid */
	private BigFraction[] grid;
	
	private boolean[] topplingAlternationCompliance;
	private boolean itsEvenPositionsTurnToTopple;
	
	private long step;
	private final boolean isPositive;
	
	/** The index of the origin within the array */
	private int originIndex;

	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;
	
	public SimpleAetherInfinityTopplingAlternationCompliance1D(boolean isPositive) {
		this.isPositive = isPositive;
		int side = 5;
		grid = new BigFraction[side];
		//The origin will be at the center of the array
		originIndex = (side - 1)/2;
		Arrays.fill(grid, BigFraction.ZERO);
		grid[originIndex] = isPositive? BigFraction.ONE : BigFraction.MINUS_ONE;
		itsEvenPositionsTurnToTopple = isPositive;
		boundsReached = false;
		step = 0;
		nextStep();
	}
	
	@Override
	public Boolean nextStep() {
		//Use new array to store the values of the next step
		BigFraction[] newGrid = null;
		//The offset between the indexes of the new and old array
		int indexOffset = 0;
		int newSide;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			newSide = grid.length + 2;
			indexOffset = 1;
			topplingAlternationCompliance = new boolean[newSide];
			registerTopplingAlternationComplianceInGridEdges();
		} else {
			newSide = grid.length;
			topplingAlternationCompliance = new boolean[newSide];
		}
		newGrid = new BigFraction[newSide];
		Arrays.fill(newGrid, BigFraction.ZERO);
		boolean itsCurrentPositionsTurnToTopple = itsEvenPositionsTurnToTopple == (originIndex%2 == 0); //when the dimension is odd the corner coordinates are not always even
		//For every cell
		for (int index = 0, newIndex = indexOffset; index < grid.length; index++, newIndex++, itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple) {
			//Distribute the cell's value among its neighbors (von Neumann) using the algorithm
			
			//Get the cell's value
			BigFraction value = grid[index];
			//Get a list of the neighbors whose value is smaller than the one at the current cell
			List<Neighbor<BigFraction>> neighbors = new ArrayList<Neighbor<BigFraction>>(2);						
			BigFraction neighborValue;
			if (index < grid.length - 1)
				neighborValue = grid[index + 1];
			else
				neighborValue = BigFraction.ZERO;
			if (neighborValue.compareTo(value) < 0)
				neighbors.add(new Neighbor<BigFraction>(Direction.RIGHT, neighborValue));
			if (index > 0)
				neighborValue = grid[index - 1];
			else
				neighborValue = BigFraction.ZERO;
			if (neighborValue.compareTo(value) < 0)
				neighbors.add(new Neighbor<BigFraction>(Direction.LEFT, neighborValue));

			boolean toppled = false;
			//If there are any
			if (neighbors.size() > 0) {
				toppled = true;
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
						checkBoundsReached(index + indexOffset, newGrid.length);
						//The current cell keeps one share
						value = value.subtract(toShare).add(share);
						for (Neighbor<BigFraction> n : neighbors) {
							int nc = getNeighborCoordinates(index, n.getDirection()) + indexOffset;
							newGrid[nc] = newGrid[nc].add(share);
						}
						previousNeighborValue = neighborValue;
					}
					neighbors.remove(i);
				}	
			}					
			newGrid[newIndex] = newGrid[newIndex].add(value);
			topplingAlternationCompliance[newIndex] = toppled == itsCurrentPositionsTurnToTopple;
		}
		//Replace the old array with the new one
		this.grid = newGrid;
		//Update the index of the origin
		originIndex += indexOffset;
		//Increase the current step by one
		step++;
		itsEvenPositionsTurnToTopple = !itsEvenPositionsTurnToTopple;
		//Return whether or not the state of the grid changed
		return true;
	}
	
	private void registerTopplingAlternationComplianceInGridEdges() {
		int side = topplingAlternationCompliance.length - 1;
		boolean itsNotEvenIndexesTurnToTopple = itsEvenPositionsTurnToTopple == (originIndex%2 == 0);
		if (itsNotEvenIndexesTurnToTopple) {
			topplingAlternationCompliance[0] = true;
			topplingAlternationCompliance[side] = true;
		}
	}

	@Override
	public Boolean isChanged() {
		return step == 0 ? null : true;
	}
	
	private void checkBoundsReached(int index, int length) {
		if (index == 1 || index == length - 2) {
			boundsReached = true;
		}
	}
	
	private static int getNeighborCoordinates(int x, Direction direction) {
		if (direction == Direction.RIGHT) {
			x++;
		} else {
			x--;
		}
		return x;
	}
	
	@Override
	public boolean getFromPosition(int x) {	
		int index = originIndex + x;
		return topplingAlternationCompliance[index];
	}
	
	@Override
	public boolean getFromAsymmetricPosition(int x) {
		return getFromPosition(x);
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
	public String getName() {
		return "Aether";
	}
	
	@Override
	public String getSubfolderPath() {
		String path = getName() + "/1D/";
		if (!isPositive) path += "-";
		path += "infinity";
		return path + "/toppling_alternation_compliance";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}
}
