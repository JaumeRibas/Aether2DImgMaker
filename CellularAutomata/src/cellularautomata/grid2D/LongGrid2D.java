/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2019 Jaume Ribas

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

import cellularautomata.grid.LongGrid;

public interface LongGrid2D extends Grid2D, LongGrid {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @return the value at (x,y)
	 * @throws Exception 
	 */
	public abstract long getValueAtPosition(int x, int y) throws Exception;
	
	default long[] getMinAndMaxValue() throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY;
		long maxValue = Long.MIN_VALUE, minValue = Long.MAX_VALUE;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinY(x);
			maxY = getMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				long value = getValueAtPosition(x, y);
				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;
			}
		}
		return new long[]{ minValue, maxValue };
	}
	
	@Override
	default long[] getMinAndMaxValueAtEvenPositions() throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY;
		long evenMinValue = Long.MAX_VALUE, evenMaxValue = Long.MIN_VALUE;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinY(x);
			maxY = getMaxY(x);
			if ((minY+x)%2 != 0) {
				minY++;
			}
			for (int y = minY; y <= maxY; y+=2) {
				long value = getValueAtPosition(x, y);
				if (value > evenMaxValue)
					evenMaxValue = value;
				if (value < evenMinValue)
					evenMinValue = value;
			}
		}
		return new long[]{evenMinValue, evenMaxValue};
	}
	
	default long[] getMinAndMaxValueAtOddPositions() throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY;
		long oddMinValue = Long.MAX_VALUE, oddMaxValue = Long.MIN_VALUE;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinY(x);
			maxY = getMaxY(x);
			if ((minY+x)%2 == 0) {
				minY++;
			}
			for (int y = minY; y <= maxY; y+=2) {
				long value = getValueAtPosition(x, y);
				if (value > oddMaxValue)
					oddMaxValue = value;
				if (value < oddMinValue)
					oddMinValue = value;
			}
		}
		return new long[]{oddMinValue, oddMaxValue};
	}

	default long[] getMinAndMaxValueExcluding(long excludedValue) throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY;
		long maxValue, minValue;
		if (excludedValue == Long.MIN_VALUE) {
			maxValue = Long.MIN_VALUE + 1;
			minValue = Long.MAX_VALUE;
		} else if (excludedValue == Long.MAX_VALUE) {
			maxValue = Long.MIN_VALUE;
			minValue = Long.MAX_VALUE - 1;
		} else {
			maxValue = Long.MIN_VALUE;
			minValue = Long.MAX_VALUE;
		}
		for (int x = minX; x <= maxX; x++) {
			minY = getMinY(x);
			maxY = getMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				long value = getValueAtPosition(x, y);
				if (value != excludedValue) {
					if (value > maxValue)
						maxValue = value;
					if (value < minValue)
						minValue = value;
				}
			}
		}
		return new long[]{ minValue, maxValue };
	}
	
	default long getTotalValue() throws Exception {
		long total = 0;
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
	
	default long getMaxAbsoluteValue() throws Exception {
		long maxAbsoluteValue;
		long[] minAndMax = getMinAndMaxValue();
		if (minAndMax[0] < 0) {
			minAndMax[0] = Math.abs(minAndMax[0]);
			maxAbsoluteValue = Math.max(minAndMax[0], minAndMax[1]);
		} else {
			maxAbsoluteValue = minAndMax[1];
		}
		return maxAbsoluteValue;
	}
	
	default LongGrid2D absoluteGrid() {
		return new AbsLongGrid2D(this);
	}
	
	default LongGrid2D subGrid(int minX, int maxX, int minY, int maxY) {
		return subGrid(minX, maxX, minY, maxY, false);
	}
	
	default LongGrid2D subGrid(int minX, int maxX, int minY, int maxY, boolean allowOverflow) {
		if (!allowOverflow && (minX < getMinX() || minX > getMaxX() 
				|| maxX < getMinX() || maxX > getMaxX()
				|| minY < getMinY() || minY > getMaxY() 
				|| maxY < getMinY() || maxY > getMaxY()))
			throw new IllegalArgumentException("Sub-grid bounds outside of grid bounds.");
		if (minX > maxX || minY > maxY)
			throw new IllegalArgumentException("Transposed bounds. Check argument order.");
		return new LongSubGrid2D(this, minX, maxX, minY, maxY);
	}
}
