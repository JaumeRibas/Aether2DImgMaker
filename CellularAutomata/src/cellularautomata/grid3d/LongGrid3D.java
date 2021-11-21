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

import java.util.Iterator;

import cellularautomata.grid.LongGrid;
import cellularautomata.grid2d.LongGrid2D;

public interface LongGrid3D extends Grid3D, LongGrid {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-axis
	 * @param y the position on the y-axis
	 * @param z the position on the z-axis
	 * @return the value at (x,y,z)
	 * @throws Exception 
	 */
	long getFromPosition(int x, int y, int z) throws Exception;

	@Override
	default long[] getMinAndMax() throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY, maxZ, minZ;
		long maxValue = Long.MIN_VALUE, minValue = Long.MAX_VALUE;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinYAtX(x);
			maxY = getMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getMinZ(x, y);
				maxZ = getMaxZ(x, y);
				for (int z = minZ; z <= maxZ; z++) {
					long value = getFromPosition(x, y, z);
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
	default long[] getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		boolean anyPositionMatches = false;
		int maxX = getMaxX(), minX = getMinX(), maxY, minY, maxZ, minZ;
		long minValue = Long.MAX_VALUE, maxValue = Long.MIN_VALUE;
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
					anyPositionMatches = true;
					long value = getFromPosition(x, y, z);
					if (value > maxValue)
						maxValue = value;
					if (value < minValue)
						minValue = value;
				}
			}
		}
		return anyPositionMatches ? new long[]{minValue, maxValue} : null;
	}
	
	@Override
	default long getTotal() throws Exception {
		long total = 0;
		int maxX = getMaxX(), minX = getMinX(), maxY, minY, maxZ, minZ;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinYAtX(x);
			maxY = getMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getMinZ(x, y);
				maxZ = getMaxZ(x, y);
				for (int z = minZ; z <= maxZ; z++) {
					total += getFromPosition(x, y, z);
				}
			}
		}
		return total;
	}
	
	@Override
	default LongGrid3D subsection(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new LongSubGrid3D<LongGrid3D>(this, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default LongGrid2D crossSectionAtX(int x) {
		return new LongGrid3DXCrossSection<LongGrid3D>(this, x);
	}
	
	@Override
	default LongGrid2D crossSectionAtY(int y) {
		return new LongGrid3DYCrossSection<LongGrid3D>(this, y);
	}
	
	@Override
	default LongGrid2D crossSectionAtZ(int z) {
		return new LongGrid3DZCrossSection<LongGrid3D>(this, z);
	}
	
	@Override
	default LongGrid2D diagonalCrossSectionOnXY(int yOffsetFromX) {
		return new LongGrid3DXYDiagonalCrossSection<LongGrid3D>(this, yOffsetFromX);
	}
	
	@Override
	default LongGrid2D diagonalCrossSectionOnXZ(int zOffsetFromX) {
		return new LongGrid3DXZDiagonalCrossSection<LongGrid3D>(this, zOffsetFromX);
	}
	
	@Override
	default LongGrid2D diagonalCrossSectionOnYZ(int zOffsetFromY) {
		return new LongGrid3DYZDiagonalCrossSection<LongGrid3D>(this, zOffsetFromY);
	}

	@Override
	default Iterator<Long> iterator() {
		return new LongGrid3DIterator(this);
	}

}
