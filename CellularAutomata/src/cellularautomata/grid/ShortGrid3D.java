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

public abstract class ShortGrid3D implements Grid3D, ShortGrid {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @param z the position on the z-coordinate
	 * @return the value at (x,y,z)
	 */
	public abstract short getValue(int x, int y, int z);

	public short[] getMinAndMaxValue() {
		int maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxY(), minY = getMinY(),
				maxZ = getMaxZ(), minZ = getMinZ();
		short maxValue = getValue(minX, minY, minZ), minValue = maxValue;
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					short value = getValue(x, y, z);
					if (value > maxValue)
						maxValue = value;
					if (value < minValue)
						minValue = value;
				}
			}
		}
		return new short[]{ minValue, maxValue };
	}
	
	public short[] getMinAndMaxValue(short backgroundValue) {
		int maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxY(), minY = getMinY(),
				maxZ = getMaxZ(), minZ = getMinZ();
		short maxValue = getValue(minX, minY, minZ), minValue = maxValue;
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					short value = getValue(x, y, z);
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
	
	public short getTotalValue() {
		short total = 0;
		int maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxY(), minY = getMinY(), 
				maxZ = getMaxZ(), minZ = getMinZ();
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					total += getValue(x, y, z);
				}	
			}	
		}
		return total;
	}
	
	public short getMaxAbsoluteValue() {
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
	
	public ShortGrid3D absoluteGrid() {
		return new AbsShortGrid3D(this);
	}
	
	public ShortGrid3D subGrid(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
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
	
	public ShortGrid2D crossSection(int z) {
		return new ShortGrid3DCrossSection(this, z);
	}
	
	public ShortGrid2D projectedSurfaceMaxX(short backgroundValue) {
		return new ShortGrid3DProjectedSurfaceMaxX(this, backgroundValue);
	}
}
