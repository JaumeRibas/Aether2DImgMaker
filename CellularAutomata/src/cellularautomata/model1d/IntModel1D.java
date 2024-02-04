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
package cellularautomata.model1d;

import java.util.Iterator;

import cellularautomata.Coordinates;
import cellularautomata.PartialCoordinates;
import cellularautomata.model.IntModel;

public interface IntModel1D extends Model1D, IntModel {
	
	/**
	 * <p>Returns the value at a given position.</p>
	 * <p>It is not defined to call this method passing coordinates outside the bounds of the region. 
	 * To get these bounds use the {@link #getMaxX()} and {@link #getMinX()} methods.</p>
	 * 
	 * @param x the position on the x-axis
	 * @return the value at (x)
	 * @throws Exception 
	 */
	int getFromPosition(int x) throws Exception;
	
	@Override
	default int getFromPosition(Coordinates coordinates) throws Exception {
		return getFromPosition(coordinates.get(0));
	}
	
	@Override
	default int[] getMinAndMax() throws Exception {
		int maxX = getMaxX(), minX = getMinX();
		int maxValue = getFromPosition(minX), minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			int value = getFromPosition(x);
			if (value > maxValue)
				maxValue = value;
			if (value < minValue)
				minValue = value;
		}
		return new int[]{ minValue, maxValue };
	}
	
	@Override
	default int[] getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		boolean anyPositionMatches = false;
		int maxX = getMaxX(), minX = getMinX();
		int maxValue = Integer.MIN_VALUE, minValue = Integer.MAX_VALUE;
		boolean isPositionEven = minX%2 == 0;
		if (isPositionEven != isEven) {
			minX++;
		}
		for (int x = minX; x <= maxX; x+=2) {
			anyPositionMatches = true;
			int value = getFromPosition(x);
			if (value > maxValue)
				maxValue = value;
			if (value < minValue)
				minValue = value;
		}
		return anyPositionMatches? new int[]{ minValue, maxValue } : null;
	}
	
	@Override
	default int getTotal() throws Exception {
		int total = 0;
		int maxX = getMaxX(), minX = getMinX();
		for (int x = minX; x <= maxX; x++) {
			total += getFromPosition(x);
		}
		return total;
	}
	
	@Override
	default IntModel1D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return (IntModel1D) Model1D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	default IntModel1D subsection(Integer minX, Integer maxX) {
		return new IntSubModel1D(this, minX, maxX);
	}

	@Override
	default Iterator<Integer> iterator() {
		return new IntModel1DIterator(this);
	}
}
