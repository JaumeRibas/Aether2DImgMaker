/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2020 Jaume Ribas

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
package cellularautomata.grid1d;

import cellularautomata.grid.LongGrid;

public interface LongGrid1D extends Grid1D, LongGrid {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-coordinate
	 * @return the value at (x)
	 */
	long getValueAtPosition(int x);
	
	@Override
	default long[] getMinAndMaxValue() {
		int maxX = getMaxX(), minX = getMinX();
		long maxValue = getValueAtPosition(minX), minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			long value = getValueAtPosition(x);
			if (value > maxValue)
				maxValue = value;
			if (value < minValue)
				minValue = value;
		}
		return new long[]{ minValue, maxValue };
	}
	
	@Override
	default long[] getEvenOddPositionsMinAndMaxValue(boolean isEven) throws Exception {
		int maxX = getMaxX(), minX = getMinX();
		long maxValue = Long.MIN_VALUE, minValue = Long.MAX_VALUE;
		boolean isPositionEven = minX%2 == 0;
		if (isEven) { 
			if (!isPositionEven) {
				minX++;
			}
		} else {
			if (isPositionEven) {
				minX++;
			}
		}
		for (int x = minX; x <= maxX; x+=2) {
			long value = getValueAtPosition(x);
			if (value > maxValue)
				maxValue = value;
			if (value < minValue)
				minValue = value;
		}
		return new long[]{ minValue, maxValue };
	}
	
	@Override
	default long getTotalValue() {
		long total = 0;
		int maxX = getMaxX(), minX = getMinX();
		for (int x = minX; x <= maxX; x++) {
			total += getValueAtPosition(x);
		}
		return total;
	}
	
	@Override
	default LongGrid1D subGrid(int minX, int maxX) {
		return new LongSubGrid1D<LongGrid1D>(this, minX, maxX);
	}
}
