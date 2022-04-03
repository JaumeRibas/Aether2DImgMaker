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

import cellularautomata.MinAndMax;
import cellularautomata.model.NumericModel;

public interface NumericModel1D<Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> extends ObjectModel1D<Number_Type>, NumericModel<Number_Type> {
	
	@Override
	default MinAndMax<Number_Type> getMinAndMax() throws Exception {
		int maxX = getMaxX(), minX = getMinX();
		Number_Type minValue = getFromPosition(minX);
		Number_Type maxValue = minValue;
		for (int x = minX; x <= maxX; x++) {
			Number_Type value = getFromPosition(x);
			if (value.compareTo(minValue) < 0)
				minValue = value;
			if (value.compareTo(maxValue) > 0)
				maxValue = value;
		}
		return new MinAndMax<Number_Type>(minValue, maxValue);
	}
	
	@Override
	default MinAndMax<Number_Type> getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		Number_Type minValue = null;
		Number_Type maxValue = null;
		int maxX = getMaxX(), minX = getMinX();
		boolean isPositionEven = minX%2 == 0;
		if (isPositionEven != isEven) {
			minX++;
		}
		if (minX <= maxX) {
			Number_Type value = getFromPosition(minX);
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
		return minValue == null? null : new MinAndMax<Number_Type>(minValue, maxValue);
	}
	
	@Override
	default Number_Type getTotal() throws Exception {
		int maxX = getMaxX(), minX = getMinX();
		Number_Type total = getFromPosition(minX);
		for (int x = minX + 1; x <= maxX; x++) {
			total = total.add(getFromPosition(x));
		}
		return total;
	}
	
	@Override
	default NumericModel1D<Number_Type> subsection(int minX, int maxX) {
		return new NumericSubModel1D<Number_Type>(this, minX, maxX);
	}
}
