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
package cellularautomata.grid;


public abstract class LongGrid1D implements Grid1D, LongGrid {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-coordinate
	 * @return the value at (x)
	 */
	public abstract long getValue(int x);
	
	public long[] getMinAndMaxValue() {
		int maxX = getMaxX(), minX = getMinX();
		long maxValue = getValue(minX), minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			long value = getValue(x);
			if (value > maxValue)
				maxValue = value;
			if (value < minValue)
				minValue = value;
		}
		return new long[]{ minValue, maxValue };
	}
	
	/**
	 * Get min and max values excluding a backgroundValue
	 * 
	 * @param backgroundValue
	 * @return
	 */
	public long[] getMinAndMaxValue(long backgroundValue) {
		int maxX = getMaxX(), minX = getMinX();
		long maxValue = getValue(minX), minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			long value = getValue(x);
			if (value != backgroundValue) {
				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;
			}
		}
		return new long[]{ minValue, maxValue };
	}
	
	public long getTotalValue() {
		long total = 0;
		int maxX = getMaxX(), minX = getMinX();
		for (int x = minX; x <= maxX; x++) {
			total += getValue(x);
		}
		return total;
	}
	
	public long getMaxAbsoluteValue() {
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
	
	public LongGrid1D absoluteGrid() {
		return new AbsLongGrid1D(this);
	}
	
	public LongGrid1D subGrid(int minX, int maxX) {
		return subGrid(minX, maxX, false);
	}
	
	public LongGrid1D subGrid(int minX, int maxX, boolean allowOverflow) {
		if (!allowOverflow && (minX < getMinX() || minX > getMaxX() 
				|| maxX < getMinX() || maxX > getMaxX()))
			throw new IllegalArgumentException("Sub-grid bounds outside of grid bounds.");
		if (minX > maxX)
			throw new IllegalArgumentException("Transposed bounds. Check argument order.");
		return new LongSubGrid1D(this, minX, maxX);
	}
}
