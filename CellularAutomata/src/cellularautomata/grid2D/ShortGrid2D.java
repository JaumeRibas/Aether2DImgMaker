/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2018 Jaume Ribas

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
package cellularautomata.grid2D;

import cellularautomata.grid.ShortGrid;

public interface ShortGrid2D extends Grid2D, ShortGrid {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @return the value at (x,y)
	 */
	short getValueAtPosition(int x, int y);
	
	default short[] getMinAndMaxValue() throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY;
		short maxValue = Short.MIN_VALUE, minValue = Short.MAX_VALUE;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinY(x);
			maxY = getMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				short value = getValueAtPosition(x, y);
				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;
			}
		}
		return new short[]{ minValue, maxValue };
	}
	
	/**
	 * Get min and max values excluding a backgroundValue
	 * 
	 * @param backgroundValue
	 * @return
	 * @throws Exception 
	 */
	default short[] getMinAndMaxValueExcluding(short backgroundValue) throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY;
		short maxValue, minValue;
		switch (backgroundValue) {
			case Short.MIN_VALUE:
				maxValue = Short.MIN_VALUE + 1;
				minValue = Short.MAX_VALUE;
				break;
			case Short.MAX_VALUE:
				maxValue = Short.MIN_VALUE;
				minValue = Short.MAX_VALUE - 1;
				break;
			default:
				maxValue = Short.MIN_VALUE;
				minValue = Short.MAX_VALUE;
		}
		for (int x = minX; x <= maxX; x++) {
			minY = getMinY(x);
			maxY = getMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				short value = getValueAtPosition(x, y);
				if (value != backgroundValue) {
					if (value > maxValue)
						maxValue = value;
					if (value < minValue)
						minValue = value;
				}
			}
		}
		return new short[]{ minValue, maxValue };
	}
	
	default short getTotalValue() throws Exception {
		short total = 0;
		int maxX = getMaxX(), minX = getMinX(), maxY, minY;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinY(x);
			maxY = getMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				total += getValueAtPosition(x, y);
			}	
		}
		return total;
	}
	
	default short getMaxAbsoluteValue() throws Exception {
		short maxAbsoluteValue;
		short[] minAndMax = getMinAndMaxValue();
		if (minAndMax[0] < 0) {
			minAndMax[0] = (short) Math.abs(minAndMax[0]);
			maxAbsoluteValue = (short) Math.max(minAndMax[0], minAndMax[1]);
		} else {
			maxAbsoluteValue = minAndMax[1];
		}
		return maxAbsoluteValue;
	}
	
	default ShortGrid2D absoluteGrid() {
		return new AbsShortGrid2D(this);
	}
	
	default ShortGrid2D subGrid(int minX, int maxX, int minY, int maxY) {
		return subGrid(minX, maxX, minY, maxY, false);
	}
	
	default ShortGrid2D subGrid(int minX, int maxX, int minY, int maxY, boolean allowOverflow) {
		if (!allowOverflow && (minX < getMinX() || minX > getMaxX() 
				|| maxX < getMinX() || maxX > getMaxX()
				|| minY < getMinY() || minY > getMaxY() 
				|| maxY < getMinY() || maxY > getMaxY()))
			throw new IllegalArgumentException("Sub-grid bounds outside of grid bounds.");
		if (minX > maxX || minY > maxY)
			throw new IllegalArgumentException("Transposed bounds. Check argument order.");
		return new ShortSubGrid2D(this, minX, maxX, minY, maxY);
	}
}
