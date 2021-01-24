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
package cellularautomata.grid1d;

import org.apache.commons.math3.FieldElement;

import cellularautomata.grid.MinAndMax;
import cellularautomata.grid.NumberGrid;

public interface NumberGrid1D<T extends FieldElement<T> & Comparable<T>> extends Grid1D, NumberGrid<T> {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-coordinate
	 * @return the value at (x)
	 */
	T getFromPosition(int x);
	
	@Override
	default MinAndMax<T> getMinAndMax() {
		int maxX = getMaxX(), minX = getMinX();
		T minValue = getFromPosition(minX);
		T maxValue = minValue;
		for (int x = minX; x <= maxX; x++) {
			T value = getFromPosition(x);
			if (value.compareTo(minValue) < 0)
				minValue = value;
			if (value.compareTo(maxValue) > 0)
				maxValue = value;
		}
		return new MinAndMax<T>(minValue, maxValue);
	}
	
	@Override
	default MinAndMax<T> getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		int maxX = getMaxX(), minX = getMinX();
		boolean isPositionEven = minX%2 == 0;
		if (isPositionEven != isEven) {
			minX++;
		}
		T minValue = getFromPosition(minX);
		T maxValue = minValue;
		for (int x = minX + 2; x <= maxX; x+=2) {
			T value = getFromPosition(x);
			if (value.compareTo(minValue) < 0)
				minValue = value;
			if (value.compareTo(maxValue) > 0)
				maxValue = value;
		}
		return new MinAndMax<T>(minValue, maxValue);
	}
	
	@Override
	default T getTotal() {
		int maxX = getMaxX(), minX = getMinX();
		T total = getFromPosition(minX);
		for (int x = minX + 1; x <= maxX; x++) {
			total = total.add(getFromPosition(x));
		}
		return total;
	}
	
	@Override
	default NumberGrid1D<T> subGrid(int minX, int maxX) {
		return new NumberSubGrid1D<T, NumberGrid1D<T>>(this, minX, maxX);
	}
}
