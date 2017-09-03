/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017 Jaume Ribas

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
package cellularautomata.grid;


public abstract class IntGrid2D implements Grid2D, IntGrid {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @return the value at (x,y)
	 */
	public abstract int getValueAt(int x, int y);
	
	public int[] getMinAndMaxValue() {
		int maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxY(), minY = getMinY();
		int maxValue = getValueAt(minX, minY), minValue = maxValue;
		for (int y = minY; y <= maxY; y++) {
			for (int x = minX; x <= maxX; x++) {
				int value = getValueAt(x, y);
				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;
			}
		}
		return new int[]{ minValue, maxValue };
	}
	
	/**
	 * Get min and max values excluding a backgroundValue
	 * 
	 * @param backgroundValue
	 * @return
	 */
	public int[] getMinAndMaxValue(int backgroundValue) {
		int maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxY(), minY = getMinY();
		int maxValue = getValueAt(minX, minY), minValue = maxValue;
		for (int y = minY; y <= maxY; y++) {
			for (int x = minX; x <= maxX; x++) {
				int value = getValueAt(x, y);
				if (value != backgroundValue) {
					if (value > maxValue)
						maxValue = value;
					if (value < minValue)
						minValue = value;
				}
			}
		}
		return new int[]{ minValue, maxValue };
	}
	
	public int getTotalValue() {
		int total = 0;
		int maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxY(), minY = getMinY();
		for (int y = minY; y <= maxY; y++) {
			for (int x = minX; x <= maxX; x++) {
				total += getValueAt(x, y);
			}	
		}
		return total;
	}
	
	public int getMaxAbsoluteValue() {
		int maxAbsoluteValue;
		int[] minAndMax = getMinAndMaxValue();
		if (minAndMax[0] < 0) {
			minAndMax[0] = Math.abs(minAndMax[0]);
			maxAbsoluteValue = Math.max(minAndMax[0], minAndMax[1]);
		} else {
			maxAbsoluteValue = minAndMax[1];
		}
		return maxAbsoluteValue;
	}
	
	public IntGrid2D absoluteGrid() {
		return new AbsIntGrid2D(this);
	}
	
	public IntGrid2D subGrid(int minX, int maxX, int minY, int maxY) {
		return subGrid(minX, maxX, minY, maxY, false);
	}
	
	public IntGrid2D subGrid(int minX, int maxX, int minY, int maxY, boolean allowOverflow) {
		if (!allowOverflow && (minX < getMinX() || minX > getMaxX() 
				|| maxX < getMinX() || maxX > getMaxX()
				|| minY < getMinY() || minY > getMaxY() 
				|| maxY < getMinY() || maxY > getMaxY()))
			throw new IllegalArgumentException("Sub-grid bounds outside of grid bounds.");
		if (minX > maxX || minY > maxY)
			throw new IllegalArgumentException("Transposed bounds. Check argument order.");
		return new IntSubGrid2D(this, minX, maxX, minY, maxY);
	}
}
