/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2025 Jaume Ribas

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
package cellularautomata.model2d;

import java.util.Iterator;

import cellularautomata.Coordinates;
import cellularautomata.PartialCoordinates;
import cellularautomata.model.LongModel;
import cellularautomata.numbers.BigInt;

public interface LongModel2D extends Model2D, LongModel {
	
	/**
	 * <p>Returns the value at a given position.</p>
	 * <p>It is not defined to call this method passing coordinates outside the bounds of the region.</p>
	 * 
	 * @param x the position on the x-axis
	 * @param y the position on the y-axis
	 * @return the value at (x,y)
	 * @throws Exception 
	 */
	long getFromPosition(int x, int y) throws Exception;
	
	@Override
	default long getFromPosition(Coordinates coordinates) throws Exception {
		return getFromPosition(coordinates.get(0), coordinates.get(1));
	}
	
	@Override
	default long[] getMinAndMax() throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY;
		long maxValue = Long.MIN_VALUE, minValue = Long.MAX_VALUE;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinY(x);
			maxY = getMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				long value = getFromPosition(x, y);
				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;
			}
		}
		return new long[]{ minValue, maxValue };
	}
	
	@Override
	default long[] getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		boolean anyPositionMatches = false;
		int maxX = getMaxX(), minX = getMinX();
		long maxValue = Long.MIN_VALUE, minValue = Long.MAX_VALUE;
		for (int x = minX; x <= maxX; x++) {
			int minY = getMinY(x);
			int maxY = getMaxY(x);
			boolean isPositionEven = (minY+x)%2 == 0;
			if (isPositionEven != isEven) {
				minY++;
			}
			for (int y = minY; y <= maxY; y += 2) {
				anyPositionMatches = true;
				long value = getFromPosition(x, y);
				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;
			}
		}
		return anyPositionMatches ? new long[]{ minValue, maxValue } : null;
	}
	
	default long[] getMinAndMaxAtEvenOddX(boolean isEven) throws Exception {
		boolean anyPositionMatches = false;
		int maxX = getMaxX(), minX = getMinX();
		long maxValue = Long.MIN_VALUE, minValue = Long.MAX_VALUE;
		int x = minX;
		boolean isXEven = x%2 == 0;
		if (isXEven != isEven) {
			x++;
		}
		for (; x <= maxX; x += 2) {
			int minY = getMinY(x);
			int maxY = getMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				anyPositionMatches = true;
				long value = getFromPosition(x, y);
				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;
			}
		}
		return anyPositionMatches ? new long[]{ minValue, maxValue } : null;
	}
	
	default long[] getMinAndMaxAtEvenOddY(boolean isEven) throws Exception {
		boolean anyPositionMatches = false;
		int maxX = getMaxX(), minX = getMinX();
		long maxValue = Long.MIN_VALUE, minValue = Long.MAX_VALUE;
		for (int x = minX; x <= maxX; x++) {
			int minY = getMinY(x);
			int maxY = getMaxY(x);
			boolean isYEven = minY%2 == 0;
			if (isYEven != isEven) {
				minY++;
			}
			for (int y = minY; y <= maxY; y += 2) {
				anyPositionMatches = true;
				long value = getFromPosition(x, y);
				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;
			}
		}
		return anyPositionMatches ? new long[]{ minValue, maxValue } : null;
	}
	
	@Override
	default BigInt getTotal() throws Exception {
		BigInt total = BigInt.ZERO;
		int maxX = getMaxX(), minX = getMinX(), maxY, minY;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinY(x);
			maxY = getMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				total = total.add(BigInt.valueOf(getFromPosition(x, y)));
			}	
		}
		return total;
	}
	
	@Override
	default LongModel2D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return (LongModel2D) Model2D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	default LongModel2D subsection(Integer minX, Integer maxX, Integer minY, Integer maxY) {
		return new LongSubModel2D(this, minX, maxX, minY, maxY);
	}

	@Override
	default Iterator<Long> iterator() {
		return new LongModel2DIterator(this);
	}

}
