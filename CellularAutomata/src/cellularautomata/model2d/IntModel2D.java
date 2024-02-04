/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2024 Jaume Ribas

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
import cellularautomata.model.IntModel;

public interface IntModel2D extends Model2D, IntModel {
	
	/**
	 * <p>Returns the value at a given position.</p>
	 * <p>It is not defined to call this method passing coordinates outside the bounds of the region.</p>
	 * 
	 * @param x the position on the x-axis
	 * @param y the position on the y-axis
	 * @return the value at (x,y)
	 * @throws Exception 
	 */
	int getFromPosition(int x, int y) throws Exception;
	
	@Override
	default int getFromPosition(Coordinates coordinates) throws Exception {
		return getFromPosition(coordinates.get(0), coordinates.get(1));
	}
	
	@Override
	default int[] getMinAndMax() throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY;
		int maxValue = Integer.MIN_VALUE, minValue = Integer.MAX_VALUE;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinY(x);
			maxY = getMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				int value = getFromPosition(x, y);
				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;
			}
		}
		return new int[]{ minValue, maxValue };
	}
	
	@Override
	default int[] getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		boolean anyPositionMatches = false;
		int maxX = getMaxX(), minX = getMinX();
		int maxValue = Integer.MIN_VALUE, minValue = Integer.MAX_VALUE;
		for (int x = minX; x <= maxX; x++) {
			int minY = getMinY(x);
			int maxY = getMaxY(x);
			boolean isPositionEven = (minY+x)%2 == 0;
			if (isPositionEven != isEven) {
				minY++;
			}
			for (int y = minY; y <= maxY; y+=2) {
				anyPositionMatches = true;
				int value = getFromPosition(x, y);
				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;
			}
		}
		return anyPositionMatches ? new int[]{ minValue, maxValue } : null;
	}
	
	default int[] getMinAndMaxAtEvenOddX(boolean isEven) throws Exception {
		boolean anyPositionMatches = false;
		int maxX = getMaxX(), minX = getMinX();
		int maxValue = Integer.MIN_VALUE, minValue = Integer.MAX_VALUE;
		int x = minX;
		boolean isXEven = x%2 == 0;
		if (isXEven != isEven) {
			x++;
		}
		for (; x <= maxX; x+=2) {
			int minY = getMinY(x);
			int maxY = getMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				anyPositionMatches = true;
				int value = getFromPosition(x, y);
				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;
			}
		}
		return anyPositionMatches ? new int[]{ minValue, maxValue } : null;
	}
	
	default int[] getMinAndMaxAtEvenOddY(boolean isEven) throws Exception {
		boolean anyPositionMatches = false;
		int maxX = getMaxX(), minX = getMinX();
		int maxValue = Integer.MIN_VALUE, minValue = Integer.MAX_VALUE;
		for (int x = minX; x <= maxX; x++) {
			int minY = getMinY(x);
			int maxY = getMaxY(x);
			boolean isYEven = minY%2 == 0;
			if (isYEven != isEven) {
				minY++;
			}
			for (int y = minY; y <= maxY; y+=2) {
				anyPositionMatches = true;
				int value = getFromPosition(x, y);
				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;
			}
		}
		return anyPositionMatches ? new int[]{ minValue, maxValue } : null;
	}
	
	@Override
	default int getTotal() throws Exception {
		int total = 0;
		int maxX = getMaxX(), minX = getMinX(), maxY, minY;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinY(x);
			maxY = getMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				total += getFromPosition(x, y);
			}	
		}
		return total;
	}
	
	@Override
	default IntModel2D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return (IntModel2D) Model2D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	default IntModel2D subsection(Integer minX, Integer maxX, Integer minY, Integer maxY) {
		return new IntSubModel2D(this, minX, maxX, minY, maxY);
	}

	@Override
	default Iterator<Integer> iterator() {
		return new IntModel2DIterator(this);
	}

}
