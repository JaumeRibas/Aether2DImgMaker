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

import org.apache.commons.math3.FieldElement;

import cellularautomata.MinAndMax;
import cellularautomata.PartialCoordinates;
import cellularautomata.model.NumericModel;

public interface NumericModel2D<Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> extends ObjectModel2D<Number_Type>, NumericModel<Number_Type> {
	
	@Override
	default MinAndMax<Number_Type> getMinAndMax() throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY;
		Number_Type maxValue = getFromPosition(minX, getMinY(minX));
		Number_Type minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinY(x);
			maxY = getMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				Number_Type value = getFromPosition(x, y);
				if (value.compareTo(minValue) < 0) {
					minValue = value;
				} else if (value.compareTo(maxValue) > 0) {
					maxValue = value;
				}
			}
		}
		return new MinAndMax<Number_Type>(minValue, maxValue);
	}
	
	@Override
	default MinAndMax<Number_Type> getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		int maxX = getMaxX(), minX = getMinX();
		Number_Type maxValue = null;
		Number_Type minValue = null;
		int x = minX;
		for (; x <= maxX && maxValue == null; x++) {
			int minY = getMinY(x);
			int maxY = getMaxY(x);
			boolean isPositionEven = (minY+x)%2 == 0;
			if (isPositionEven != isEven) {
				minY++;
			}
			if (minY <= maxY) {
				Number_Type value = getFromPosition(x, minY);
				maxValue = value;
				minValue = value;
				for (int y = minY + 2; y <= maxY; y += 2) {
					value = getFromPosition(x, y);
					if (value.compareTo(minValue) < 0) {
						minValue = value;
					} else if (value.compareTo(maxValue) > 0) {
						maxValue = value;
					}
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
			for (int y = minY; y <= maxY; y += 2) {
				Number_Type value = getFromPosition(x, y);
				if (value.compareTo(minValue) < 0) {
					minValue = value;
				} else if (value.compareTo(maxValue) > 0) {
					maxValue = value;
				}
			}
		}
		return minValue == null? null : new MinAndMax<Number_Type>(minValue, maxValue);
	}
	
	default MinAndMax<Number_Type> getMinAndMaxAtEvenOddX(boolean isEven) throws Exception {
		int maxX = getMaxX(), minX = getMinX();
		Number_Type maxValue = null;
		Number_Type minValue = null;
		int x = minX;
		boolean isXEven = x%2 == 0;
		if (isXEven != isEven) {
			x++;
		}
		for (; x <= maxX && maxValue == null; x += 2) {
			int minY = getMinY(x);
			int maxY = getMaxY(x);
			if (minY <= maxY) {
				Number_Type value = getFromPosition(x, minY);
				maxValue = value;
				minValue = value;
				for (int y = minY + 1; y <= maxY; y++) {
					value = getFromPosition(x, y);
					if (value.compareTo(minValue) < 0) {
						minValue = value;
					} else if (value.compareTo(maxValue) > 0) {
						maxValue = value;
					}
				}
			}
		}
		for (; x <= maxX; x += 2) {
			int minY = getMinY(x);
			int maxY = getMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				Number_Type value = getFromPosition(x, y);
				if (value.compareTo(minValue) < 0) {
					minValue = value;
				} else if (value.compareTo(maxValue) > 0) {
					maxValue = value;
				}
			}
		}
		return minValue == null? null : new MinAndMax<Number_Type>(minValue, maxValue);
	}
	
	default MinAndMax<Number_Type> getMinAndMaxAtEvenOddY(boolean isEven) throws Exception {
		int maxX = getMaxX(), minX = getMinX();
		Number_Type maxValue = null;
		Number_Type minValue = null;
		int x = minX;
		for (; x <= maxX && maxValue == null; x++) {
			int minY = getMinY(x);
			int maxY = getMaxY(x);
			boolean isYEven = minY%2 == 0;
			if (isYEven != isEven) {
				minY++;
			}
			if (minY <= maxY) {
				Number_Type value = getFromPosition(x, minY);
				maxValue = value;
				minValue = value;
				for (int y = minY + 2; y <= maxY; y += 2) {
					value = getFromPosition(x, y);
					if (value.compareTo(minValue) < 0) {
						minValue = value;
					} else if (value.compareTo(maxValue) > 0) {
						maxValue = value;
					}
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
			for (int y = minY; y <= maxY; y += 2) {
				Number_Type value = getFromPosition(x, y);
				if (value.compareTo(minValue) < 0) {
					minValue = value;
				} else if (value.compareTo(maxValue) > 0) {
					maxValue = value;
				}
			}
		}
		return minValue == null? null : new MinAndMax<Number_Type>(minValue, maxValue);
	}
	
	@Override
	default Number_Type getTotal() throws Exception {
		int maxX = getMaxX(), minX = getMinX();
		int minY = getMinY(minX);
		Number_Type total = getFromPosition(minX, minY);
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
	default NumericModel2D<Number_Type> subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return (NumericModel2D<Number_Type>) ObjectModel2D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	default NumericModel2D<Number_Type> subsection(Integer minX, Integer maxX, Integer minY, Integer maxY) {
		return new NumericSubModel2D<Number_Type>(this, minX, maxX, minY, maxY);
	}

}
