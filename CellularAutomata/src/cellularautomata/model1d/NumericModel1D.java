/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2022 Jaume Ribas

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
package cellularautomata.model1d;

import org.apache.commons.math3.FieldElement;

import cellularautomata.model.MinAndMax;
import cellularautomata.model.NumericModel;

public interface NumericModel1D<T extends FieldElement<T> & Comparable<T>> extends ObjectModel1D<T>, NumericModel<T> {
	
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
		T minValue = null;
		T maxValue = null;
		int maxX = getMaxX(), minX = getMinX();
		boolean isPositionEven = minX%2 == 0;
		if (isPositionEven != isEven) {
			minX++;
		}
		if (minX <= maxX) {
			T value = getFromPosition(minX);
			minValue = value;
			maxValue = value;
			for (int x = minX + 2; x <= maxX; x+=2) {
				value = getFromPosition(x);
				if (value.compareTo(minValue) < 0)
					minValue = value;
				if (value.compareTo(maxValue) > 0)
					maxValue = value;
			}
		}
		return minValue == null? null : new MinAndMax<T>(minValue, maxValue);
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
	default NumericModel1D<T> subsection(int minX, int maxX) {
		return new NumericSubModel1D<T, NumericModel1D<T>>(this, minX, maxX);
	}
}
