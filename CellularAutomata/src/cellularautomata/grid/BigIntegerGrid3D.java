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

public abstract class BigIntegerGrid3D implements Grid3D, BigIntegerGrid {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @param z the position on the z-coordinate
	 * @return the value at (x,y,z)
	 */
	public abstract BigInteger getValueAt(int x, int y, int z);
	
	public BigInteger[] getMinAndMaxValue() {
		int maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxY(), minY = getMinY(),
				maxZ = getMaxZ(), minZ = getMinZ();
		BigInteger maxValue = getValueAt(minX, minY, minZ), minValue = maxValue;
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					BigInteger value = getValueAt(x, y, z);
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
		int maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxY(), minY = getMinY(),
				maxZ = getMaxZ(), minZ = getMinZ();
		BigInteger maxValue = getValueAt(minX, minY, minZ), minValue = maxValue;
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					BigInteger value = getValueAt(x, y, z);
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
	
	public BigInteger getTotalValue() {
		BigInteger total = BigInteger.ZERO;
		int maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxY(), minY = getMinY(),
				maxZ = getMaxZ(), minZ = getMinZ();
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					BigInteger value = getValueAt(x, y, z);
					total = total.add(value);
				}
			}
		}
		return total;
	}
	
	public BigInteger getMaxAbsoluteValue() {
		BigInteger maxAbsoluteValue;
		BigInteger[] minAndMax = getMinAndMaxValue();
		if (minAndMax[0].compareTo(BigInteger.ZERO) < 0) {
			minAndMax[0] = minAndMax[0].abs();
			maxAbsoluteValue = minAndMax[0].max(minAndMax[1]);
		} else {
			maxAbsoluteValue = minAndMax[1];
		}
		return maxAbsoluteValue;
	}
	
	public BigIntegerGrid3D absoluteGrid() {
		return new AbsBigIntegerGrid3D(this);
	}
	
	public BigIntegerGrid3D subGrid(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		if (minX < getMinX() || minX > getMaxX() 
				|| maxX < getMinX() || maxX > getMaxX()
				|| minY < getMinY() || minY > getMaxY() 
				|| maxY < getMinY() || maxY > getMaxY()
				|| minZ < getMinZ() || minZ > getMaxZ() 
				|| maxZ < getMinZ() || maxZ > getMaxZ())
			throw new IllegalArgumentException("Sub-grid bounds outside of grid bounds.");
		if (minX > maxX || minY > maxY || minZ > maxZ)
			throw new IllegalArgumentException("Transposed bounds. Check argument order.");
		return new BigIntegerSubGrid3D(this, minX, maxX, minY, maxY, minZ, maxZ);
	}
}
