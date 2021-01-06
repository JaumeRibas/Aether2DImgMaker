/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2021 Jaume Ribas

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
package cellularautomata.grid3d;

import java.math.BigInteger;

import cellularautomata.grid.BigIntGrid;
import cellularautomata.grid2d.BigIntGrid2D;

public interface BigIntGrid3D extends Grid3D, BigIntGrid {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @param z the position on the z-coordinate
	 * @return the value at (x,y,z)
	 * @throws Exception 
	 */
	BigInteger getValueAtPosition(int x, int y, int z) throws Exception;

	@Override
	default BigInteger[] getMinAndMaxValue() throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY, maxZ, minZ;
		minY = getMinYAtX(minX);
		BigInteger maxValue = getValueAtPosition(minX, minY, getMinZ(minX, minY));
		BigInteger minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinYAtX(x);
			maxY = getMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getMinZ(x, y);
				maxZ = getMaxZ(x, y);
				for (int z = minZ; z <= maxZ; z++) {
					BigInteger value = getValueAtPosition(x, y, z);
					if (value.compareTo(maxValue) > 0)
						maxValue = value;
					if (value.compareTo(minValue) < 0)
						minValue = value;
				}
			}
		}
		return new BigInteger[]{ minValue, maxValue };
	}
	
	@Override
	default BigInteger[] getEvenOddPositionsMinAndMaxValue(boolean isEven) throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY, maxZ, minZ;
		minY = getMinYAtX(minX);
		BigInteger maxValue = getValueAtPosition(minX, minY, getMinZ(minX, minY));
		BigInteger minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinYAtX(x);
			maxY = getMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getMinZ(x, y);
				maxZ = getMaxZ(x, y);
				boolean isPositionEven = (minZ+x+y)%2 == 0;
				if (isPositionEven != isEven) {
					minZ++;
				}
				for (int z = minZ; z <= maxZ; z+=2) {
					BigInteger value = getValueAtPosition(x, y, z);
					if (value.compareTo(maxValue) > 0)
						maxValue = value;
					if (value.compareTo(minValue) < 0)
						minValue = value;
				}
			}
		}
		return new BigInteger[]{minValue, maxValue};
	}
	
	@Override
	default BigInteger getTotalValue() throws Exception {
		BigInteger total = BigInteger.ZERO;
		int maxX = getMaxX(), minX = getMinX(), maxY, minY, maxZ, minZ;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinYAtX(x);
			maxY = getMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getMinZ(x, y);
				maxZ = getMaxZ(x, y);
				for (int z = minZ; z <= maxZ; z++) {
					total = total.add(getValueAtPosition(x, y, z));
				}
			}
		}
		return total;
	}
	
	@Override
	default BigIntGrid3D subGrid(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new BigIntSubGrid3D<BigIntGrid3D>(this, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default BigIntGrid2D crossSectionAtZ(int z) {
		return new BigIntGrid3DZCrossSection<BigIntGrid3D>(this, z);
	}
	
	@Override
	default BigIntGrid2D crossSectionAtX(int x) {
		return new BigIntGrid3DXCrossSection<BigIntGrid3D>(this, x);
	}

}
