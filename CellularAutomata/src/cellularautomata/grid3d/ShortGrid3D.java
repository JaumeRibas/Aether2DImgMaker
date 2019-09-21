/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2019 Jaume Ribas

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

import cellularautomata.grid.ShortGrid;
import cellularautomata.grid2d.ShortGrid2D;

public interface ShortGrid3D extends Grid3D, ShortGrid {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @param z the position on the z-coordinate
	 * @return the value at (x,y,z)
	 */
	short getValueAtPosition(int x, int y, int z);

	default short[] getMinAndMaxValue() {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY, maxZ, minZ;
		short maxValue = Short.MIN_VALUE, minValue = Short.MAX_VALUE;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinYAtX(x);
			maxY = getMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getMinZ(x, y);
				maxZ = getMaxZ(x, y);
				for (int z = minZ; z <= maxZ; z++) {
					short value = getValueAtPosition(x, y, z);
					if (value > maxValue)
						maxValue = value;
					if (value < minValue)
						minValue = value;
				}
			}
		}
		return new short[]{ minValue, maxValue };
	}
	
	default short[] getMinAndMaxValueExcluding(short backgroundValue) {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY, maxZ, minZ;
		short maxValue, minValue;
		switch (backgroundValue) {
			case Short.MIN_VALUE:
				maxValue = Short.MIN_VALUE + 1;
				minValue = Short.MAX_VALUE;
				break;
			case Short.MAX_VALUE:
				maxValue = Short.MIN_VALUE;
				minValue = Short.MAX_VALUE - 1;
				break;
			default:
				maxValue = Short.MIN_VALUE;
				minValue = Short.MAX_VALUE;
		}
		for (int x = minX; x <= maxX; x++) {
			minY = getMinYAtX(x);
			maxY = getMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getMinZ(x, y);
				maxZ = getMaxZ(x, y);
				for (int z = minZ; z <= maxZ; z++) {
					short value = getValueAtPosition(x, y, z);
					if (value != backgroundValue) {
						if (value > maxValue)
							maxValue = value;
						if (value < minValue)
							minValue = value;
					}
				}
			}
		}
		return new short[]{ minValue, maxValue };
	}
	
	default short getTotalValue() {
		short total = 0;
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
	
	default short getMaxAbsoluteValue() {
		short maxAbsoluteValue;
		short[] minAndMax = getMinAndMaxValue();
		if (minAndMax[0] < 0) {
			minAndMax[0] = (short) Math.abs(minAndMax[0]);
			maxAbsoluteValue = (short) Math.max(minAndMax[0], minAndMax[1]);
		} else {
			maxAbsoluteValue = minAndMax[1];
		}
		return maxAbsoluteValue;
	}
	
	default ShortGrid3D absoluteGrid() {
		return new AbsShortGrid3D(this);
	}
	
	default ShortGrid3D subGrid(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		if (minX < getMinX() || minX > getMaxX() 
				|| maxX < getMinX() || maxX > getMaxX()
				|| minY < getMinY() || minY > getMaxY() 
				|| maxY < getMinY() || maxY > getMaxY()
				|| minZ < getMinZ() || minZ > getMaxZ() 
				|| maxZ < getMinZ() || maxZ > getMaxZ())
			throw new IllegalArgumentException("Sub-grid bounds outside of grid bounds.");
		if (minX > maxX || minY > maxY || minZ > maxZ)
			throw new IllegalArgumentException("Transposed bounds. Check argument order.");
		return new ShortSubGrid3D(this, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	default ShortGrid2D crossSectionAtZ(int z) {
		return new ShortGrid3DZCrossSection(this, z);
	}
	
	default ShortGrid2D projectedSurfaceMaxX(short backgroundValue) {
		return new ShortGrid3DProjectedSurfaceMaxX(this, backgroundValue);
	}
}
