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

import cellularautomata.grid.IntGrid;
import cellularautomata.grid2d.IntGrid2D;

public interface IntGrid3D extends Grid3D, IntGrid {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-axis
	 * @param y the position on the y-axis
	 * @param z the position on the z-axis
	 * @return the value at (x,y,z)
	 * @throws Exception 
	 */
	int getFromPosition(int x, int y, int z) throws Exception;

	@Override
	default int[] getMinAndMax() throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY, maxZ, minZ;
		int maxValue = Integer.MIN_VALUE, minValue = Integer.MAX_VALUE;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinYAtX(x);
			maxY = getMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getMinZ(x, y);
				maxZ = getMaxZ(x, y);
				for (int z = minZ; z <= maxZ; z++) {
					int value = getFromPosition(x, y, z);
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
	default int[] getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		boolean anyPositionMatches = false;
		int maxX = getMaxX(), minX = getMinX(), maxY, minY, maxZ, minZ;
		int minValue = Integer.MAX_VALUE, maxValue = Integer.MIN_VALUE;
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
					int value = getFromPosition(x, y, z);
					if (value > maxValue)
						maxValue = value;
					if (value < minValue)
						minValue = value;
				}
			}
		}
		return anyPositionMatches ? new int[]{minValue, maxValue} : null;
	}
	
	@Override
	default int getTotal() throws Exception {
		int total = 0;
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
	default IntGrid3D subsection(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new IntSubGrid3D<IntGrid3D>(this, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default IntGrid2D crossSectionAtX(int x) {
		return new IntGrid3DXCrossSection<IntGrid3D>(this, x);
	}
	
	@Override
	default IntGrid2D crossSectionAtY(int y) {
		return new IntGrid3DYCrossSection<IntGrid3D>(this, y);
	}
	
	@Override
	default IntGrid2D crossSectionAtZ(int z) {
		return new IntGrid3DZCrossSection<IntGrid3D>(this, z);
	}
	
	@Override
	default IntGrid2D diagonalCrossSectionOnXY(int yOffsetFromX) {
		return new IntGrid3DXYDiagonalCrossSection<IntGrid3D>(this, yOffsetFromX);
	}
	
	@Override
	default IntGrid2D diagonalCrossSectionOnXZ(int zOffsetFromX) {
		return new IntGrid3DXZDiagonalCrossSection<IntGrid3D>(this, zOffsetFromX);
	}
	
	@Override
	default IntGrid2D diagonalCrossSectionOnYZ(int zOffsetFromY) {
		return new IntGrid3DYZDiagonalCrossSection<IntGrid3D>(this, zOffsetFromY);
	}

	@Override
	default Iterator<Integer> iterator() {
		return new IntGrid3DIterator(this);
	}
	
}
