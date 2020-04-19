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
package cellularautomata.grid4d;

import cellularautomata.grid.IntGrid;
import cellularautomata.grid2d.IntGrid2D;
import cellularautomata.grid3d.IntGrid3D;

public interface IntGrid4D extends Grid4D, IntGrid {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param w the position on the w-coordinate 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @param z the position on the z-coordinate
	 * @return the value at (w,x,y,z)
	 * @throws Exception 
	 */
	int getValueAtPosition(int w, int x, int y, int z) throws Exception;

	@Override
	default int[] getMinAndMaxValue() throws Exception {
		int maxW = getMaxW(), minW = getMinW(),
				maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxY(), minY = getMinY(),
				maxZ = getMaxZ(), minZ = getMinZ();
		int maxValue = Integer.MIN_VALUE, minValue = Integer.MAX_VALUE;
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					for (int w = minW; w <= maxW; w++) {
						int value = getValueAtPosition(w, x, y, z);
						if (value > maxValue)
							maxValue = value;
						if (value < minValue)
							minValue = value;
					}
				}
			}
		}
		return new int[]{ minValue, maxValue };
	}
	
	@Override
	default int[] getEvenOddPositionsMinAndMaxValue(boolean isEven) throws Exception {
		int maxW = getMaxW(), minW = getMinW(),
				maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxY(), minY = getMinY(),
				maxZ = getMaxZ(), minZ = getMinZ();
		int minValue = Integer.MAX_VALUE, maxValue = Integer.MIN_VALUE;
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					boolean isPositionEven = (minW+x+y+z)%2 == 0;
					if (isEven) { 
						if (!isPositionEven) {
							minW++;
						}
					} else {
						if (isPositionEven) {
							minW++;
						}
					}
					for (int w = minW; w <= maxW; w+=2) {
						int value = getValueAtPosition(w, x, y, z);
						if (value > maxValue)
							maxValue = value;
						if (value < minValue)
							minValue = value;
					}
				}
			}
		}
		return new int[]{minValue, maxValue};
	}
	
	@Override
	default int getTotalValue() throws Exception {
		int total = 0;
		int maxW = getMaxW(), minW = getMinW(),
				maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxY(), minY = getMinY(),
				maxZ = getMaxZ(), minZ = getMinZ();
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					for (int w = minW; w <= maxW; w++) {
						total += getValueAtPosition(w, x, y, z);
					}
				}	
			}	
		}
		return total;
	}
	
	@Override
	default IntGrid2D crossSectionAtYZ(int y, int z) {
		return new IntGrid4DYZCrossSection(this, y, z);
	}
	
	@Override
	default IntGrid4D subGrid(int minW, int maxW, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		if (minW < getMinW() || minW > getMaxW() 
				|| maxW < getMinW() || maxW > getMaxW()
				|| minX < getMinX() || minX > getMaxX() 
				|| maxX < getMinX() || maxX > getMaxX()
				|| minY < getMinY() || minY > getMaxY() 
				|| maxY < getMinY() || maxY > getMaxY()
				|| minZ < getMinZ() || minZ > getMaxZ() 
				|| maxZ < getMinZ() || maxZ > getMaxZ())
			throw new IllegalArgumentException("Sub-grid bounds outside of grid bounds.");
		if (minX > maxX || minY > maxY || minZ > maxZ)
			throw new IllegalArgumentException("Transposed bounds. Check argument order.");
		return new IntSubGrid4D(this, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default IntGrid3D crossSectionAtZ(int z) {
		return new IntGrid4DZCrossSection<IntGrid4D>(this, z);
	}
}
