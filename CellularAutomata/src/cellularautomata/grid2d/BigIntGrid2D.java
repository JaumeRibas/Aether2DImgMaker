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
package cellularautomata.grid2d;

import java.math.BigInteger;

import cellularautomata.grid.BigIntGrid;

public interface BigIntGrid2D extends Grid2D, BigIntGrid {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @return the value at (x,y)
	 * @throws Exception 
	 */
	public abstract BigInteger getValueAtPosition(int x, int y) throws Exception;
	
	@Override
	default BigInteger[] getMinAndMaxValue() throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY;
		BigInteger maxValue = getValueAtPosition(minX, getMinY(minX));
		BigInteger minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinY(x);
			maxY = getMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				BigInteger value = getValueAtPosition(x, y);
				if (value.compareTo(maxValue) > 0)
					maxValue = value;
				if (value.compareTo(minValue) < 0)
					minValue = value;
			}
		}
		return new BigInteger[]{ minValue, maxValue };
	}
	
	@Override
	default BigInteger[] getEvenOddPositionsMinAndMaxValue(boolean isEven) throws Exception {
		int maxX = getMaxX(), minX = getMinX();
		BigInteger maxValue = getValueAtPosition(minX, getMinY(minX));
		BigInteger minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			int minY = getMinY(x);
			int maxY = getMaxY(x);
			boolean isPositionEven = (minY+x)%2 == 0;
			if (isPositionEven != isEven) {
				minY++;
			}
			for (int y = minY; y <= maxY; y+=2) {
				BigInteger value = getValueAtPosition(x, y);
				if (value.compareTo(maxValue) > 0)
					maxValue = value;
				if (value.compareTo(minValue) < 0)
					minValue = value;
			}
		}
		return new BigInteger[]{ minValue, maxValue };
	}
	
	@Override
	default BigInteger getTotalValue() throws Exception {
		BigInteger total = BigInteger.ZERO;
		int maxX = getMaxX(), minX = getMinX(), maxY, minY;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinY(x);
			maxY = getMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				total = total.add(getValueAtPosition(x, y));
			}	
		}
		return total;
	}
	
	@Override
	default BigIntGrid2D subGrid(int minX, int maxX, int minY, int maxY) {
		return new BigIntSubGrid2D<BigIntGrid2D>(this, minX, maxX, minY, maxY);
	}

}
