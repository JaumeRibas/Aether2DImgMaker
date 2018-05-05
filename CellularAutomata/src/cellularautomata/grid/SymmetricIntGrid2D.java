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

public abstract class SymmetricIntGrid2D extends IntGrid2D implements SymmetricGrid2D {
	
	/**
	 * <p>
	 * Returns the value at a given position within the non symmetric section of the grid.
	 * That is, where the x-coordinate is inside the [{@link #getNonSymmetricMinX()}, {@link #getNonSymmetricMaxX()}] bounds 
	 * and the y-coordinate is inside the [{@link #getNonSymmetricMinY(int x)}, {@link #getNonSymmetricMaxY(int x)}] bounds.
	 * </p>
	 * <p>
	 * Or where the y-coordinate is inside the [{@link #getNonSymmetricMinY()}, {@link #getNonSymmetricMaxY()}] bounds 
	 * and the x-coordinate is inside the [{@link #getNonSymmetricMinX(int y)}, {@link #getNonSymmetricMaxX(int y)}] bounds.
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
	public abstract int getNonSymmetricValue(int x, int y) throws Exception;
	
	public int[] getMinAndMaxValue() throws Exception {
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX(), 
				maxY = getNonSymmetricMaxY(minX), minY = getNonSymmetricMinY(minX);
		int maxValue = getNonSymmetricValue(minX, minY), minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			minY = getNonSymmetricMinY(x);
			maxY = getNonSymmetricMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				int value = getNonSymmetricValue(x, y);
				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;
			}
		}
		return new int[]{ minValue, maxValue };
	}
	
	public int[] getMinAndMaxValue(int backgroundValue) throws Exception {
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX(), 
				maxY = getNonSymmetricMaxY(minX), minY = getNonSymmetricMinY(minX);
		int maxValue = getNonSymmetricValue(minX, minY), minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			minY = getNonSymmetricMinY(x);
			maxY = getNonSymmetricMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				int value = getNonSymmetricValue(x, y);
				if (value != backgroundValue) {
					if (value > maxValue)
						maxValue = value;
					if (value < minValue)
						minValue = value;	
				}
			}
		}
		return new int[]{ minValue, maxValue };
	}
	
	@Override
	public SymmetricIntGrid2D absoluteGrid() {
		return new AbsSymmetricIntGrid2D(this);
	}
}
