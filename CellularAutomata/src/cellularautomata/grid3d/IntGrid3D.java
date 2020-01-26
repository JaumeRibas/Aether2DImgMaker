/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2020 Jaume Ribas

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

import cellularautomata.grid.IntGrid;
import cellularautomata.grid2d.IntGrid2D;

public interface IntGrid3D extends Grid3D, IntGrid {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @param z the position on the z-coordinate
	 * @return the value at (x,y,z)
	 * @throws Exception 
	 */
	int getValueAtPosition(int x, int y, int z) throws Exception;

	@Override
	default int[] getMinAndMaxValue() throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY, maxZ, minZ;
		int maxValue = Integer.MIN_VALUE, minValue = Integer.MAX_VALUE;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinYAtX(x);
			maxY = getMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getMinZ(x, y);
				maxZ = getMaxZ(x, y);
				for (int z = minZ; z <= maxZ; z++) {
					int value = getValueAtPosition(x, y, z);
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
	default int[] getEvenOddPositionsMinAndMaxValue(boolean isEven) throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY, maxZ, minZ;
		int minValue = Integer.MAX_VALUE, maxValue = Integer.MIN_VALUE;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinYAtX(x);
			maxY = getMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getMinZ(x, y);
				maxZ = getMaxZ(x, y);
				boolean isPositionEven = (minZ+x+y)%2 == 0;
				if (isEven) { 
					if (!isPositionEven) {
						minZ++;
					}
				} else {
					if (isPositionEven) {
						minZ++;
					}
				}
				for (int z = minZ; z <= maxZ; z+=2) {
					int value = getValueAtPosition(x, y, z);
					if (value > maxValue)
						maxValue = value;
					if (value < minValue)
						minValue = value;
				}
			}
		}
		return new int[]{minValue, maxValue};
	}
	
	@Override
	default int getTotalValue() throws Exception {
		int total = 0;
		int maxX = getMaxX(), minX = getMinX(), maxY, minY, maxZ, minZ;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinYAtX(x);
			maxY = getMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getMinZ(x, y);
				maxZ = getMaxZ(x, y);
				for (int z = minZ; z <= maxZ; z++) {
					total += getValueAtPosition(x, y, z);
				}
			}
		}
		return total;
	}
	
	@Override
	default IntGrid3D absoluteGrid() {
		return new AbsIntGrid3D(this);
	}
	
	@Override
	default IntGrid3D subGrid(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		if (minX < getMinX() || minX > getMaxX() 
				|| maxX < getMinX() || maxX > getMaxX()
				|| minY < getMinY() || minY > getMaxY() 
				|| maxY < getMinY() || maxY > getMaxY()
				|| minZ < getMinZ() || minZ > getMaxZ() 
				|| maxZ < getMinZ() || maxZ > getMaxZ())
			throw new IllegalArgumentException("Sub-grid bounds outside of grid bounds.");
		if (minX > maxX || minY > maxY || minZ > maxZ)
			throw new IllegalArgumentException("Transposed bounds. Check argument order.");
		return new IntSubGrid3D(this, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default IntGrid2D crossSectionAtZ(int z) {
		return new IntGrid3DZCrossSection(this, z);
	}
	
	@Override
	default IntGrid2D crossSectionAtX(int x) {
		return new IntGrid3DXCrossSection(this, x);
	}
	
	@Override
	default IntGrid2D projectedSurfaceMaxX() {
		return new IntGrid3DProjectedSurfaceMaxX(this);
	}
	
}
