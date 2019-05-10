/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2018 Jaume Ribas

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
package cellularautomata.grid;

public abstract class SymmetricLongGrid2D extends LongGrid2D implements SymmetricGrid2D {
	
	/**
	 * <p>
	 * Returns the value at a given position within the non symmetric section of the grid.
	 * That is, where the x is larger or equal to {@link #getNonSymmetricMinX()} 
	 * and smaller or equal to {@link #getNonSymmetricMaxX()}; 
	 * and the y is is larger or equal to {@link #getNonSymmetricMinY()} 
	 * and smaller or equal to {@link #getNonSymmetricMaxY()}.
	 * </p>
	 * <p>
	 * The result of getting the value of a position outside this bounds is undefined.
	 * <p>
	 * 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @return the {@link long} value at (x,y)
	 * @throws Exception 
	 */
	public abstract long getNonSymmetricValue(int x, int y) throws Exception;
	
	public long[] getMinAndMaxValue() throws Exception {
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX();
		long maxValue = Long.MIN_VALUE, minValue = Long.MAX_VALUE;
		for (int x = minX; x <= maxX; x++) {
			int minY = getNonSymmetricMinY(x);
			int maxY = getNonSymmetricMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				long value = getNonSymmetricValue(x, y);
				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;
			}
		}
		return new long[]{ minValue, maxValue };
	}
	
	public long[] getEvenPositionsMinAndMaxValue() throws Exception {
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX();
		long maxValue = Long.MIN_VALUE, minValue = Long.MAX_VALUE;
		for (int x = minX; x <= maxX; x++) {
			int minY = getNonSymmetricMinY(x);
			int maxY = getNonSymmetricMaxY(x);
			if ((minY+x)%2 != 0) {
				minY++;
			}
			for (int y = minY; y <= maxY; y+=2) {
				long value = getNonSymmetricValue(x, y);
				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;
			}
		}
		return new long[]{ minValue, maxValue };
	}
	
	public long[] getEvenOddPositionsMinAndMaxValue(boolean isEven) throws Exception {
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX();
		long maxValue = Long.MIN_VALUE, minValue = Long.MAX_VALUE;
		for (int x = minX; x <= maxX; x++) {
			int minY = getNonSymmetricMinY(x);
			int maxY = getNonSymmetricMaxY(x);
			boolean isPositionEven = (minY+x)%2 == 0;
			if (isEven) { 
				if (!isPositionEven) {
					minY++;
				}
			} else {
				if (isPositionEven) {
					minY++;
				}
			}
			for (int y = minY; y <= maxY; y+=2) {
				long value = getNonSymmetricValue(x, y);
				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;
			}
		}
		return new long[]{ minValue, maxValue };
	}
	
	public long[] getMinAndMaxValue(long backgroundValue) throws Exception {		
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX();
		long maxValue = Long.MIN_VALUE, minValue = Long.MAX_VALUE;
		for (int x = minX; x <= maxX; x++) {
			int minY = getNonSymmetricMinY(x);
			int maxY = getNonSymmetricMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				long value = getNonSymmetricValue(x, y);
				if (value != backgroundValue) {
					if (value > maxValue)
						maxValue = value;
					if (value < minValue)
						minValue = value;	
				}
			}
		}
		return new long[]{ minValue, maxValue };
	}
	
	@Override
	public SymmetricLongGrid2D absoluteGrid() {
		return new AbsSymmetricLongGrid2D(this);
	}
}
