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
import java.util.function.Consumer;

import cellularautomata.Coordinates;
import cellularautomata.arrays.HypercubicBooleanArray;
import cellularautomata.automata.Neighbor;
import cellularautomata.model3d.IsotropicCubicModelA;
import cellularautomata.model3d.SymmetricBooleanModel3D;

public class SimpleLongAether3DTopplingAlternationCompliance implements SymmetricBooleanModel3D, IsotropicCubicModelA {	
	
	public static final long MAX_INITIAL_VALUE = Long.MAX_VALUE;
	public static final long MIN_INITIAL_VALUE = -3689348814741910323L;
	
	private static final byte UP = 0;
	private static final byte DOWN = 1;
	private static final byte RIGHT = 2;
	private static final byte LEFT = 3;
	private static final byte FRONT = 4;
	private static final byte BACK = 5;
	
	/** 3D array representing the grid **/
	private long[][][] grid;
	
	private HypercubicBooleanArray topplingAlternationCompliance;
	private boolean isEvenPositionsTurnToTopple;
	
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
	public SimpleLongAether3DTopplingAlternationCompliance(long initialValue) {
		//safety check to prevent exceeding the data type's max value
		if (initialValue < MIN_INITIAL_VALUE) {
			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
		}
		this.initialValue = initialValue;
		isEvenPositionsTurnToTopple = initialValue >= 0;
		//initial side of the array, will be increased as needed
		int side = 5;
		grid = new long[side][side][side];
		originIndex = (side - 1)/2;
		grid[originIndex][originIndex][originIndex] = initialValue;
		boundsReached = false;
		//Set the current step to zero
		step = 0;
		nextStep();
	}
	
	@Override
	public Boolean nextStep() {
		//Use new array to store the values of the next step
		long[][][] newGrid = null;
		//The offset between the indexes of the new and old array
		int indexOffset = 0;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			int newSide = grid.length + 2;
			newGrid = new long[newSide][newSide][newSide];
			topplingAlternationCompliance = new HypercubicBooleanArray(3, newSide);
			registerTopplingAlternationComplianceInGridEdges();
			indexOffset = 1;
		} else {
			newGrid = new long[grid.length][grid.length][grid.length];
			topplingAlternationCompliance = new HypercubicBooleanArray(3, grid.length);
		}
		boolean isCurrentPositionsTurnToTopple = isEvenPositionsTurnToTopple == (originIndex%2 == 0); //when the dimension is odd the corner coordinates are not always even
		boolean changed = false;
		//For every cell
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				for (int k = 0; k < grid.length; k++, isCurrentPositionsTurnToTopple = !isCurrentPositionsTurnToTopple) {
					long value = grid[i][j][k];
					//make list of von Neumann neighbors with value smaller than current cell's value
					List<Neighbor<Long>> neighbors = new ArrayList<Neighbor<Long>>(6);						
					long neighborValue;
					if (i < grid.length - 1)
						neighborValue = grid[i + 1][j][k];
					else
						neighborValue = 0;
					if (neighborValue < value)
						neighbors.add(new Neighbor<Long>(RIGHT, neighborValue));
					if (i > 0)
						neighborValue = grid[i - 1][j][k];
					else
						neighborValue = 0;
					if (neighborValue < value)
						neighbors.add(new Neighbor<Long>(LEFT, neighborValue));
					if (j < grid.length - 1)
						neighborValue = grid[i][j + 1][k];
					else
						neighborValue = 0;
					if (neighborValue < value)
						neighbors.add(new Neighbor<Long>(UP, neighborValue));
					if (j > 0)
						neighborValue = grid[i][j - 1][k];
					else
						neighborValue = 0;
					if (neighborValue < value)
						neighbors.add(new Neighbor<Long>(DOWN, neighborValue));
					if (k < grid.length - 1)
						neighborValue = grid[i][j][k + 1];
					else
						neighborValue = 0;
					if (neighborValue < value)
						neighbors.add(new Neighbor<Long>(FRONT, neighborValue));
					if (k > 0)
						neighborValue = grid[i][j][k - 1];
					else
						neighborValue = 0;
					if (neighborValue < value)
						neighbors.add(new Neighbor<Long>(BACK, neighborValue));

					boolean toppled = false;
					if (neighbors.size() > 0) {
						//sort neighbors by value
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
						//apply algorithm rules to redistribute value
						boolean isFirst = true;
						long previousNeighborValue = 0;
						for (int neighborIndex = neighbors.size() - 1; neighborIndex >= 0; neighborIndex--,isFirst = false) {
							neighborValue = neighbors.get(neighborIndex).getValue();
							if (neighborValue != previousNeighborValue || isFirst) {
								int shareCount = neighbors.size() + 1;
								long toShare = value - neighborValue;
								long share = toShare/shareCount;
								if (share != 0) {
									checkBoundsReached(i + indexOffset, j + indexOffset, k + indexOffset, newGrid.length);
									toppled = true;
									value = value - toShare + toShare%shareCount + share;
									for (Neighbor<Long> neighbor : neighbors) {
										int[] nc = getNeighborCoordinates(i, j, k, neighbor.getDirection());
										newGrid[nc[0] + indexOffset][nc[1] + indexOffset][nc[2] + indexOffset] += share;
									}
								}
								previousNeighborValue = neighborValue;
							}
							neighbors.remove(neighborIndex);
						}
						changed = changed || toppled;
					}					
					newGrid[i + indexOffset][j + indexOffset][k + indexOffset] += value;
					topplingAlternationCompliance.set(new Coordinates(i + indexOffset, j + indexOffset, k + indexOffset), toppled == isCurrentPositionsTurnToTopple);
				}
			}
		}
		//Replace the old array with the new one
		grid = newGrid;
		//Update the index of the origin
		originIndex += indexOffset;
		//Increase the current step by one
		step++;
		isEvenPositionsTurnToTopple = !isEvenPositionsTurnToTopple;
		this.changed = changed;
		//Return whether or not the state of the grid changed
		return changed;
	}
	
	private void registerTopplingAlternationComplianceInGridEdges() {
		Consumer<Coordinates> setToTrueIndexConsumer = new Consumer<Coordinates>() {

			@Override
			public void accept(Coordinates indexes) {
				topplingAlternationCompliance.set(indexes, true);
			}
			
		};
		boolean notEvenIndexesTurnToTopple = isEvenPositionsTurnToTopple == (originIndex%2 == 0);
		if (notEvenIndexesTurnToTopple) {
			topplingAlternationCompliance.forEachEvenEdgeIndex(1, setToTrueIndexConsumer);
		} else {
			topplingAlternationCompliance.forEachOddEdgeIndex(1, setToTrueIndexConsumer);
		}		
	}

	@Override
	public Boolean isChanged() {
		return changed;
	}
	
	private void checkBoundsReached(int i, int j, int k, int length) {
		if (i == 1 || i == length - 2 || 
			j == 1 || j == length - 2 || 
			k == 1 || k == length - 2) {
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
	public boolean getFromPosition(int x, int y, int z) {	
		int i = originIndex + x;
		int j = originIndex + y;
		int k = originIndex + z;
		return topplingAlternationCompliance.get(new Coordinates(i, j, k));
	}
	
	@Override
	public boolean getFromAsymmetricPosition(int x, int y, int z) {
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
	
	/**
	 * Returns the initial value
	 * 
	 * @return the value at the origin at step 0
	 */
	public long getInitialValue() {
		return initialValue;
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
		return getName() + "/3D/" + initialValue + "/toppling_alternation_compliance";
	}
}