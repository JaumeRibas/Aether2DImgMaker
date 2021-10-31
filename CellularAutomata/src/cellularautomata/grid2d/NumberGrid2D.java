/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2021 Jaume Ribas

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

import org.apache.commons.math3.FieldElement;

import cellularautomata.grid.MinAndMax;
import cellularautomata.grid.NumberGrid;

public interface NumberGrid2D<T extends FieldElement<T> & Comparable<T>> extends ObjectGrid2D<T>, NumberGrid<T> {
	
	@Override
	default MinAndMax<T> getMinAndMax() throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY;
		T maxValue = getFromPosition(minX, getMinY(minX));
		T minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinY(x);
			maxY = getMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				T value = getFromPosition(x, y);
				if (value.compareTo(maxValue) > 0)
					maxValue = value;
				if (value.compareTo(minValue) < 0)
					minValue = value;
			}
		}
		return new MinAndMax<T>(minValue, maxValue);
	}
	
	@Override
	default MinAndMax<T> getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		int maxX = getMaxX(), minX = getMinX();
		T maxValue = null;
		T minValue = null;
		int x = minX;
		for (; x <= maxX && maxValue == null; x++) {
			int minY = getMinY(x);
			int maxY = getMaxY(x);
			boolean isPositionEven = (minY+x)%2 == 0;
			if (isPositionEven != isEven) {
				minY++;
			}
			if (minY <= maxY) {
				T value = getFromPosition(x, minY);
				maxValue = value;
				minValue = value;
				for (int y = minY + 2; y <= maxY; y+=2) {
					value = getFromPosition(x, y);
					if (value.compareTo(maxValue) > 0)
						maxValue = value;
					if (value.compareTo(minValue) < 0)
						minValue = value;
				}
			}
		}
		for (; x <= maxX; x++) {
			int minY = getMinY(x);
			int maxY = getMaxY(x);
			boolean isPositionEven = (minY+x)%2 == 0;
			if (isPositionEven != isEven) {
				minY++;
			}
			for (int y = minY; y <= maxY; y+=2) {
				T value = getFromPosition(x, y);
				if (value.compareTo(maxValue) > 0)
					maxValue = value;
				if (value.compareTo(minValue) < 0)
					minValue = value;
			}
		}
		return minValue == null? null : new MinAndMax<T>(minValue, maxValue);
	}
	
	default MinAndMax<T> getMinAndMaxAtEvenOddY(boolean isEven) throws Exception {
		int maxX = getMaxX(), minX = getMinX();
		T maxValue = null;
		T minValue = null;
		int x = minX;
		for (; x <= maxX && maxValue == null; x++) {
			int minY = getMinY(x);
			int maxY = getMaxY(x);
			boolean isYEven = minY%2 == 0;
			if (isYEven != isEven) {
				minY++;
			}
			if (minY <= maxY) {
				T value = getFromPosition(x, minY);
				maxValue = value;
				minValue = value;
				for (int y = minY + 2; y <= maxY; y+=2) {
					value = getFromPosition(x, y);
					if (value.compareTo(maxValue) > 0)
						maxValue = value;
					if (value.compareTo(minValue) < 0)
						minValue = value;
				}
			}
		}
		for (; x <= maxX; x++) {
			int minY = getMinY(x);
			int maxY = getMaxY(x);
			boolean isYEven = minY%2 == 0;
			if (isYEven != isEven) {
				minY++;
			}
			for (int y = minY; y <= maxY; y+=2) {
				T value = getFromPosition(x, y);
				if (value.compareTo(maxValue) > 0)
					maxValue = value;
				if (value.compareTo(minValue) < 0)
					minValue = value;
			}
		}
		return minValue == null? null : new MinAndMax<T>(minValue, maxValue);
	}
	
	@Override
	default T getTotal() throws Exception {
		int maxX = getMaxX(), minX = getMinX();
		int minY = getMinY(minX);
		T total = getFromPosition(minX, minY);
		int maxY = getMaxY(minX);
		for (int y = minY + 1; y <= maxY; y++) {
			total = total.add(getFromPosition(minX, y));
		}
		for (int x = minX + 1; x <= maxX; x++) {
			minY = getMinY(x);
			maxY = getMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				total = total.add(getFromPosition(x, y));
			}
		}
		return total;
	}
	
	@Override
	default NumberGrid2D<T> subsection(int minX, int maxX, int minY, int maxY) {
		return new NumberSubGrid2D<T, NumberGrid2D<T>>(this, minX, maxX, minY, maxY);
	}

}
