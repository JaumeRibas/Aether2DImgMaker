/* CellularSharing2DImgMaker -- console app to generate images from the Cellular Sharing 2D cellular automaton
    Copyright (C) 2017 Jaume Ribas

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

import java.math.BigInteger;

public abstract class SymmetricBigIntegerGrid3D extends BigIntegerGrid3D implements SymmetricGrid3D {
	
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
	 * @param z the position on the z-coordinate
	 * @return the {@link BigInteger} value at (x,y,z)
	 */
	public abstract BigInteger getNonSymmetricValueAt(int x, int y, int z);
	
	public BigInteger[] getMinAndMaxValue() {
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX(), 
				maxY = getNonSymmetricMaxY(), minY = getNonSymmetricMinY(),
						maxZ = getNonSymmetricMaxZ(), minZ = getNonSymmetricMinZ();
		BigInteger maxValue = getNonSymmetricValueAt(minX, minY, minZ), minValue = maxValue;
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					BigInteger value = getNonSymmetricValueAt(x, y, z);
					if (value.compareTo(maxValue) > 0)
						maxValue = value;
					if (value.compareTo(minValue) < 0)
						minValue = value;
				}
			}
		}
		return new BigInteger[]{ minValue, maxValue };
	}
	
	public BigInteger[] getMinAndMaxValue(BigInteger backgroundValue) {
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX(), 
				maxY = getNonSymmetricMaxY(), minY = getNonSymmetricMinY(),
						maxZ = getNonSymmetricMaxZ(), minZ = getNonSymmetricMinZ();
		BigInteger maxValue = getNonSymmetricValueAt(minX, minY, minZ), minValue = maxValue;
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					BigInteger value = getNonSymmetricValueAt(x, y, z);
					if (!value.equals(backgroundValue)) {
						if (value.compareTo(maxValue) > 0)
							maxValue = value;
						if (value.compareTo(minValue) < 0)
							minValue = value;
					}
				}
			}
		}
		return new BigInteger[]{ minValue, maxValue };
	}
	
	public SymmetricBigIntegerGrid3D absoluteGrid() {
		return new AbsSymmetricBigIntegerGrid3D(this);
	}
}
