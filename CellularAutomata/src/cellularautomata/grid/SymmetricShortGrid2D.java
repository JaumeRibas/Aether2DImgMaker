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

public abstract class SymmetricShortGrid2D extends ShortGrid2D implements SymmetricGrid2D {
	
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
	 */
	public abstract short getNonSymmetricValue(int x, int y);
	
	public short[] getMinAndMaxValue() {
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX(), 
				maxY = getNonSymmetricMaxY(), minY = getNonSymmetricMinY();
		short maxValue = getNonSymmetricValue(minX, minY), minValue = maxValue;
		for (int y = minY; y <= maxY; y++) {
			for (int x = minX; x <= maxX; x++) {
				short value = getNonSymmetricValue(x, y);
				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;
			}
		}
		return new short[]{ minValue, maxValue };
	}
	
	public short[] getMinAndMaxValue(short backgroundValue) {
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX(), 
				maxY = getNonSymmetricMaxY(), minY = getNonSymmetricMinY();
		short maxValue = getNonSymmetricValue(minX, minY), minValue = maxValue;
		for (int y = minY; y <= maxY; y++) {
			for (int x = minX; x <= maxX; x++) {
				short value = getNonSymmetricValue(x, y);
				if (value != backgroundValue) {
					if (value > maxValue)
						maxValue = value;
					if (value < minValue)
						minValue = value;	
				}
			}
		}
		return new short[]{ minValue, maxValue };
	}
	
	@Override
	public SymmetricShortGrid2D absoluteGrid() {
		return new AbsSymmetricShortGrid2D(this);
	}
}
