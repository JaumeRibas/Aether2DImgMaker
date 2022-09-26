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

import java.util.Iterator;

import cellularautomata.Coordinates;
import cellularautomata.PartialCoordinates;
import cellularautomata.model.LongModel;

public interface LongModel1D extends Model1D, LongModel {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-axis
	 * @return the value at (x)
	 * @throws Exception 
	 */
	long getFromPosition(int x) throws Exception;
	
	@Override
	default long getFromPosition(Coordinates coordinates) throws Exception {
		return getFromPosition(coordinates.get(0));
	}
	
	@Override
	default long[] getMinAndMax() throws Exception {
		int maxX = getMaxX(), minX = getMinX();
		long maxValue = getFromPosition(minX), minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			long value = getFromPosition(x);
			if (value > maxValue)
				maxValue = value;
			if (value < minValue)
				minValue = value;
		}
		return new long[]{ minValue, maxValue };
	}
	
	@Override
	default long[] getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		boolean anyPositionMatches = false;
		int maxX = getMaxX(), minX = getMinX();
		long maxValue = Long.MIN_VALUE, minValue = Long.MAX_VALUE;
		boolean isPositionEven = minX%2 == 0;
		if (isPositionEven != isEven) {
			minX++;
		}
		for (int x = minX; x <= maxX; x+=2) {
			anyPositionMatches = true;
			long value = getFromPosition(x);
			if (value > maxValue)
				maxValue = value;
			if (value < minValue)
				minValue = value;
		}
		return anyPositionMatches? new long[]{ minValue, maxValue } : null;
	}
	
	@Override
	default long getTotal() throws Exception {
		long total = 0;
		int maxX = getMaxX(), minX = getMinX();
		for (int x = minX; x <= maxX; x++) {
			total += getFromPosition(x);
		}
		return total;
	}
	
	@Override
	default LongModel1D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return (LongModel1D) Model1D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	default LongModel1D subsection(Integer minX, Integer maxX) {
		return new LongSubModel1D<LongModel1D>(this, minX, maxX);
	}

	@Override
	default Iterator<Long> iterator() {
		return new LongModel1DIterator(this);
	}
}
