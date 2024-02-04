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
import java.util.List;

import cellularautomata.automata.Neighbor;
import cellularautomata.model1d.IsotropicModel1DA;
import cellularautomata.model1d.SymmetricBooleanModel1D;

/**
 * Simplified implementation of the <a href="https://github.com/JaumeRibas/Aether2DImgMaker/wiki/Aether-Cellular-Automaton-Definition">Aether</a> cellular automaton in 1D, with a single source initial configuration, for review and testing purposes
 * 
 * @author Jaume
 *
 */
public class SimpleLongAether1DTopplingAlternationCompliance implements SymmetricBooleanModel1D, IsotropicModel1DA {	
	
	public static final long MAX_INITIAL_VALUE = Long.MAX_VALUE;
	public static final long MIN_INITIAL_VALUE = -9223372036854775807L;
	
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;
	
	/** A 1D array representing the grid */
	private long[] grid;
	
	private boolean[] topplingAlternationCompliance;
	private boolean itsEvenPositionsTurnToTopple;
	
	private final long initialValue;
	private long step;
	
	/** The index of the origin within the array */
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
	public SimpleLongAether1DTopplingAlternationCompliance(long initialValue) {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of long type
			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
	    }
		this.initialValue = initialValue;
		itsEvenPositionsTurnToTopple = initialValue >= 0;
		int side = 5;
		grid = new long[side];
		//The origin will be at the center of the array
		originIndex = (side - 1)/2;
		grid[originIndex] = initialValue;
		boundsReached = false;
		//Set the current step to zero
		step = 0;
		nextStep();
	}
	
	@Override
	public Boolean nextStep() {
		//Use new array to store the values of the next step
		long[] newGrid = null;
		//The offset between the indexes of the new and old array
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			int newSide = grid.length + 2;
			newGrid = new long[newSide];
			topplingAlternationCompliance = new boolean[newSide];
			registerTopplingAlternationComplianceInGridEdges();
			indexOffset = 1;
		} else {
			int newSide = grid.length;
			newGrid = new long[newSide];
			topplingAlternationCompliance = new boolean[newSide];
		}
		boolean itsCurrentPositionsTurnToTopple = itsEvenPositionsTurnToTopple == (originIndex%2 == 0); //when the dimension is odd the corner coordinates are not always even
		boolean changed = false;
		//For every cell
		for (int index = 0; index < grid.length; index++, itsCurrentPositionsTurnToTopple = !itsCurrentPositionsTurnToTopple) {
			//Distribute the cell's value among its neighbors (von Neumann) using the algorithm
			
			//Get the cell's value
			long value = grid[index];
			//Get a list of the neighbors whose value is smaller than the one at the current cell
			List<Neighbor<Long>> neighbors = new ArrayList<Neighbor<Long>>(2);						
			long neighborValue;
			if (index < grid.length - 1)
				neighborValue = grid[index + 1];
			else
				neighborValue = 0;
			if (neighborValue < value)
				neighbors.add(new Neighbor<Long>(RIGHT, neighborValue));
			if (index > 0)
				neighborValue = grid[index - 1];
			else
				neighborValue = 0;
			if (neighborValue < value)
				neighbors.add(new Neighbor<Long>(LEFT, neighborValue));

			boolean toppled = false;
			//If there are any
			if (neighbors.size() > 0) {
				if (neighbors.size() > 1) {
					//Sort them by value in ascending order
					Neighbor<Long> next = neighbors.get(1);
					if (neighbors.get(0).getValue() > next.getValue()) {
						neighbors.remove(1);
						neighbors.add(0, next);
					}
				}
				boolean isFirst = true;
				long previousNeighborValue = 0;
				//Apply the algorithm
				for (int i = neighbors.size() - 1; i >= 0; i--,isFirst = false) {
					neighborValue = neighbors.get(i).getValue();
					if (neighborValue != previousNeighborValue || isFirst) {
						//Add one for the current cell
						int shareCount = neighbors.size() + 1;
						long toShare = value - neighborValue;
						long share = toShare/shareCount;
						if (share != 0) {
							checkBoundsReached(index + indexOffset, newGrid.length);
							toppled = true;
							//The current cell keeps the remainder and one share
							value = value - toShare + toShare%shareCount + share;
							for (Neighbor<Long> n : neighbors) {
								int nc = getNeighborCoordinates(index, n.getDirection());
								newGrid[nc + indexOffset] += share;
							}
						}
						previousNeighborValue = neighborValue;
					}
					neighbors.remove(i);
				}
				changed = changed || toppled;
			}					
			newGrid[index + indexOffset] += value;
			topplingAlternationCompliance[index + indexOffset] = toppled == itsCurrentPositionsTurnToTopple;
		}
		//Replace the old array with the new one
		this.grid = newGrid;
		//Update the index of the origin
		originIndex += indexOffset;
		//Increase the current step by one
		step++;
		itsEvenPositionsTurnToTopple = !itsEvenPositionsTurnToTopple;
		this.changed = changed;
		//Return whether or not the state of the grid changed
		return changed;
	}
	
	private void registerTopplingAlternationComplianceInGridEdges() {
		boolean itsNotEvenIndexesTurnToTopple = itsEvenPositionsTurnToTopple == (originIndex%2 == 0);
		topplingAlternationCompliance[0] = itsNotEvenIndexesTurnToTopple;
		topplingAlternationCompliance[topplingAlternationCompliance.length - 1] = itsNotEvenIndexesTurnToTopple;
	}

	@Override
	public Boolean isChanged() {
		return changed;
	}
	
	private void checkBoundsReached(int index, int length) {
		if (index == 1 || index == length - 2) {
			boundsReached = true;
		}
	}
	
	private static int getNeighborCoordinates(int x, byte direction) {
		if (direction == RIGHT) {
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
	
	/**
	 * Returns the initial value
	 * 
	 * @return the value at the origin at step 0
	 */
	public long getInitialValue() {
		return initialValue;
	}

	@Override
	public String getName() {
		return "Aether";
	}
	
	@Override
	public String getSubfolderPath() {
		return getName() + "/1D/" + initialValue + "/toppling_alternation_compliance";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}
}
