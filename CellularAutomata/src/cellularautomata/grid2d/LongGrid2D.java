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
package cellularautomata.grid2d;

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
	
	@Override
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
	default long[] getEvenOddPositionsMinAndMaxValue(boolean isEven) throws Exception {
		int maxX = getMaxX(), minX = getMinX();
		long maxValue = Long.MIN_VALUE, minValue = Long.MAX_VALUE;
		for (int x = minX; x <= maxX; x++) {
			int minY = getMinY(x);
			int maxY = getMaxY(x);
			boolean isPositionEven = (minY+x)%2 == 0;
			if (isEven) { 
				if (!isPositionEven) {
					minY++;
				}
			} else {
				if (isPositionEven) {
					minY++;
				}
			}
			for (int y = minY; y <= maxY; y+=2) {
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
	
	@Override
	default LongGrid2D absoluteGrid() {
		return new AbsLongGrid2D(this);
	}
	
	@Override
	default LongGrid2D subGrid(int minX, int maxX, int minY, int maxY) {
		return new LongSubGrid2D(this, minX, maxX, minY, maxY);
	}

}
