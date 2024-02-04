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

import cellularautomata.Utils;
import cellularautomata.automata.Neighbor;
import cellularautomata.model5d.IsotropicHypercubicModel5DA;
import cellularautomata.model5d.SymmetricBooleanModel5D;

public class SimpleLongAether5DTopplingAlternationCompliance2 implements SymmetricBooleanModel5D, IsotropicHypercubicModel5DA {	

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
	
	private boolean[][][][][] topplingAlternationCompliance;
	private boolean itsEvenPositionsTurnToTopple;
	
	private final long initialValue;
	private long step;
	
	/** Whether or not the values reached the bounds of the array */
	private boolean boundsReached;	

	/** Whether or not the state of the model changed between the current and the previous step **/
	private Boolean changed = null;
	
	/**
	 * Creates an instance with the given initial value
	 * 
	 * @param initialValue the value at the origin at step 0
	 */
	public SimpleLongAether5DTopplingAlternationCompliance2(long initialValue) {
		if (initialValue < MIN_INITIAL_VALUE) {//to prevent overflow of long type
			throw new IllegalArgumentException(String.format("Initial value cannot be smaller than %,d. Use a greater initial value or a different implementation.", MIN_INITIAL_VALUE));
		}
		this.initialValue = initialValue;
		itsEvenPositionsTurnToTopple = initialValue >= 0;
		grid = Utils.buildAnisotropic5DLongArray(4);
		grid[0][0][0][0][0] = this.initialValue;
		boundsReached = false;
		//Set the current step to zero
		step = 0;
		nextStep();
	}
	
	@Override
	public Boolean nextStep() {
		//Use new array to store the values of the next step
		long[][][][][] newGrid = null;
		topplingAlternationCompliance = null;
		//If at the previous step the values reached the edge, make the new array bigger
		if (boundsReached) {
			boundsReached = false;
			int newSide = grid.length + 1;
			newGrid = Utils.buildAnisotropic5DLongArray(newSide);
			topplingAlternationCompliance = Utils.buildAnisotropic5DBooleanArray(newSide);
			registerStaticGridSliceCompliance(newSide - 1);
		} else {
			newGrid = Utils.buildAnisotropic5DLongArray(grid.length);
			topplingAlternationCompliance = Utils.buildAnisotropic5DBooleanArray(grid.length);
		}
		boolean changed = false;
		int lenghtMinusOne = grid.length - 1;
		//For every cell inside the asymmetric section and those just outside its edge
		for (int v = -1, vPlusOne = v + 1; v <= lenghtMinusOne; v = vPlusOne, vPlusOne++) {
			for (int w = -1, wPlusOne = w + 1; w <= vPlusOne; w = wPlusOne, wPlusOne++) {
				for (int x = -1, xPlusOne = x + 1; x <= wPlusOne; x = xPlusOne, xPlusOne++) {
					for (int y = -1, yPlusOne = y + 1; y <= xPlusOne; y = yPlusOne, yPlusOne++) {
						for (int z = -1; z <= yPlusOne; z++) {
							long value = getValueAt(v, w, x, y, z);
							List<Neighbor<Long>> neighbors = new ArrayList<Neighbor<Long>>(10);						
							long neighborValue;
							neighborValue = getValueAt(v + 1, w, x, y, z);
							if (neighborValue < value)
								neighbors.add(new Neighbor<Long>(V_POSITIVE, neighborValue));
							neighborValue = getValueAt(v - 1, w, x, y, z);
							if (neighborValue < value)
								neighbors.add(new Neighbor<Long>(V_NEGATIVE, neighborValue));
							neighborValue = getValueAt(v, w + 1, x, y, z);
							if (neighborValue < value)
								neighbors.add(new Neighbor<Long>(W_POSITIVE, neighborValue));
							neighborValue = getValueAt(v, w - 1, x, y, z);
							if (neighborValue < value)
								neighbors.add(new Neighbor<Long>(W_NEGATIVE, neighborValue));
							neighborValue = getValueAt(v, w, x + 1, y, z);
							if (neighborValue < value)
								neighbors.add(new Neighbor<Long>(X_POSITIVE, neighborValue));
							neighborValue = getValueAt(v, w, x - 1, y, z);
							if (neighborValue < value)
								neighbors.add(new Neighbor<Long>(X_NEGATIVE, neighborValue));
							neighborValue = getValueAt(v, w, x, y + 1, z);
							if (neighborValue < value)
								neighbors.add(new Neighbor<Long>(Y_POSITIVE, neighborValue));
							neighborValue = getValueAt(v, w, x, y - 1, z);
							if (neighborValue < value)
								neighbors.add(new Neighbor<Long>(Y_NEGATIVE, neighborValue));
							neighborValue = getValueAt(v, w, x, y, z + 1);
							if (neighborValue < value)
								neighbors.add(new Neighbor<Long>(Z_POSITIVE, neighborValue));
							neighborValue = getValueAt(v, w, x, y, z - 1);
							if (neighborValue < value)
								neighbors.add(new Neighbor<Long>(Z_NEGATIVE, neighborValue));
							
							boolean toppled = false;
							if (neighbors.size() > 0) {
								//sort
								boolean sorted = false;
								while (!sorted) {
									sorted = true;
									for (int i = neighbors.size() - 2; i >= 0; i--) {
										Neighbor<Long> next = neighbors.get(i+1);
										if (neighbors.get(i).getValue() > next.getValue()) {
											sorted = false;
											neighbors.remove(i+1);
											neighbors.add(i, next);
										}
									}
								}
								//divide
								boolean isFirst = true;
								long previousNeighborValue = 0;
								for (int i = neighbors.size() - 1; i >= 0; i--,isFirst = false) {
									neighborValue = neighbors.get(i).getValue();
									if (neighborValue != previousNeighborValue || isFirst) {
										int shareCount = neighbors.size() + 1;
										long toShare = value - neighborValue;
										long share = toShare/shareCount;
										if (share != 0) {
											checkBoundsReached(v, newGrid.length);
											toppled = true;
											value = value - toShare + toShare%shareCount + share;
											for (Neighbor<Long> neighbor : neighbors) {
												int[] nc = getNeighborCoordinates(v, w, x, y, z, neighbor.getDirection());
												addToPosition(newGrid, nc[0], nc[1], nc[2], nc[3], nc[4], share);
											}
										}
										previousNeighborValue = neighborValue;
									}
									neighbors.remove(i);
								}	
								changed = changed || toppled;
							}
							addToPosition(newGrid, v, w, x, y, z, value);
							setComplianceAtPosition(v, w, x, y, z, toppled == itsEvenPositionsTurnToTopple == Utils.isEvenPosition(new int[] {v, w, x, y, z}));
						}
					}
				}
			}
		}
		//Replace the old array with the new one
		grid = newGrid;
		//Increase the current step by one
		step++;
		itsEvenPositionsTurnToTopple = !itsEvenPositionsTurnToTopple;
		this.changed = changed;
		//Return whether or not the state of the grid changed
		return changed;
	}
	
	private void registerStaticGridSliceCompliance(int v) {
		if (v%2 == 0 == itsEvenPositionsTurnToTopple) {
			Utils.fillOddIndexes(topplingAlternationCompliance[v], true);
		} else {
			Utils.fillEvenIndexes(topplingAlternationCompliance[v], true);
		}
	}

	@Override
	public Boolean isChanged() {
		return changed;
	}
	
	private void checkBoundsReached(int v, int length) {
		if (v == length - 2) {
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

	private void addToPosition(long[][][][][] newGrid, int v, int w, int x, int y, int z, long value) {	
		if (!(x < 0 || y < 0 || z < 0 || w < 0 || v < 0 
				|| z > y || y > x || x > w || w > v)) {
			newGrid[v][w][x][y][z] += value;
		}
	}

	private void setComplianceAtPosition(int v, int w, int x, int y, int z, boolean value) {	
		if (!(x < 0 || y < 0 || z < 0 || w < 0 || v < 0 
				|| z > y || y > x || x > w || w > v)) {
			topplingAlternationCompliance[v][w][x][y][z] = value;
		}
	}
	
	public long getValueAt(int v, int w, int x, int y, int z) {	
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		if (z < 0) z = -z;
		if (w < 0) w = -w;
		if (v < 0) v = -v;
		//sort coordinates
		boolean sorted;
		do {
			sorted = true;
			if (z > y) {
				sorted = false;
				int swp = z;
				z = y;
				y = swp;
			}
			if (y > x) {
				sorted = false;
				int swp = y;
				y = x;
				x = swp;
			}
			if (x > w) {
				sorted = false;
				int swp = x;
				x = w;
				w = swp;
			}
			if (w > v) {
				sorted = false;
				int swp = w;
				w = v;
				v = swp;
			}
		} while (!sorted);
		if (v < grid.length)
			return grid[v][w][x][y][z];
		else
			return 0;
	}

	@Override
	public boolean getFromPosition(int v, int w, int x, int y, int z) {	
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		if (z < 0) z = -z;
		if (w < 0) w = -w;
		if (v < 0) v = -v;
		//sort coordinates
		boolean sorted;
		do {
			sorted = true;
			if (z > y) {
				sorted = false;
				int swp = z;
				z = y;
				y = swp;
			}
			if (y > x) {
				sorted = false;
				int swp = y;
				y = x;
				x = swp;
			}
			if (x > w) {
				sorted = false;
				int swp = x;
				x = w;
				w = swp;
			}
			if (w > v) {
				sorted = false;
				int swp = w;
				w = v;
				v = swp;
			}
		} while (!sorted);
		return topplingAlternationCompliance[v][w][x][y][z];
	}

	@Override
	public boolean getFromAsymmetricPosition(int v, int w, int x, int y, int z) {
		return topplingAlternationCompliance[v][w][x][y][z];
	}

	@Override
	public int getAsymmetricMaxV() {
		int arrayMaxV = grid.length - 1;
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
		return getName() + "/5D/" + initialValue + "/toppling_alternation_compliance";
	}
	
	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}
}