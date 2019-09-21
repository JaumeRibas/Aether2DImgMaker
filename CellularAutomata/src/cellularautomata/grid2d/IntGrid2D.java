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

import cellularautomata.grid.IntGrid;

public interface IntGrid2D extends Grid2D, IntGrid {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @return the value at (x,y)
	 * @throws Exception 
	 */
	int getValueAtPosition(int x, int y) throws Exception;
	
	default int[] getMinAndMaxValue() throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY;
		int maxValue = Integer.MIN_VALUE, minValue = Integer.MAX_VALUE;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinY(x);
			maxY = getMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				int value = getValueAtPosition(x, y);
				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;
			}
		}
		return new int[]{ minValue, maxValue };
	}
	
	@Override
	default int[] getMinAndMaxValueAtEvenPositions() throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY;
		int evenMinValue = Integer.MAX_VALUE, evenMaxValue = Integer.MIN_VALUE;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinY(x);
			maxY = getMaxY(x);
			if ((minY+x)%2 != 0) {
				minY++;
			}
			for (int y = minY; y <= maxY; y+=2) {
				int value = getValueAtPosition(x, y);
				if (value > evenMaxValue)
					evenMaxValue = value;
				if (value < evenMinValue)
					evenMinValue = value;
			}
		}
		return new int[]{evenMinValue, evenMaxValue};
	}
	
	@Override
	default int[] getMinAndMaxValueAtOddPositions() throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY;
		int oddMinValue = Integer.MAX_VALUE, oddMaxValue = Integer.MIN_VALUE;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinY(x);
			maxY = getMaxY(x);
			if ((minY+x)%2 == 0) {
				minY++;
			}
			for (int y = minY; y <= maxY; y+=2) {
				int value = getValueAtPosition(x, y);
				if (value > oddMaxValue)
					oddMaxValue = value;
				if (value < oddMinValue)
					oddMinValue = value;
			}
		}
		return new int[] {oddMinValue, oddMaxValue};
	}
	
	default int[] getMinAndMaxValueExcluding(int backgroundValue) throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY;
		int maxValue, minValue;
		switch (backgroundValue) {
			case Integer.MIN_VALUE:
				maxValue = Integer.MIN_VALUE + 1;
				minValue = Integer.MAX_VALUE;
				break;
			case Integer.MAX_VALUE:
				maxValue = Integer.MIN_VALUE;
				minValue = Integer.MAX_VALUE - 1;
				break;
			default:
				maxValue = Integer.MIN_VALUE;
				minValue = Integer.MAX_VALUE;
		}
		for (int x = minX; x <= maxX; x++) {
			minY = getMinY(x);
			maxY = getMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				int value = getValueAtPosition(x, y);
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
	
	default int getTotalValue() throws Exception {
		int total = 0;
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
	
	default int getMaxAbsoluteValue() throws Exception {
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
	
	default IntGrid2D absoluteGrid() {
		return new AbsIntGrid2D(this);
	}
	
	@Override
	default IntGrid2D subGrid(int minX, int maxX, int minY, int maxY) {
		return new IntSubGrid2D(this, minX, maxX, minY, maxY);
	}

}
