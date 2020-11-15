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

import cellularautomata.grid.LongGrid;
import cellularautomata.grid2d.LongGrid2D;
import cellularautomata.grid3d.LongGrid3D;

public interface LongGrid4D extends Grid4D, LongGrid {
	
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
	long getValueAtPosition(int w, int x, int y, int z) throws Exception;

	@Override
	default long[] getMinAndMaxValue() throws Exception {
		int maxW = getMaxW(), minW = getMinW(),
				maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxY(), minY = getMinY(),
				maxZ = getMaxZ(), minZ = getMinZ();
		long maxValue = getValueAtPosition(minW, minX, minY, minZ), minValue = maxValue;
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					for (int w = minW; w <= maxW; w++) {
						long value = getValueAtPosition(w, x, y, z);
						if (value > maxValue)
							maxValue = value;
						if (value < minValue)
							minValue = value;
					}
				}
			}
		}
		return new long[]{ minValue, maxValue };
	}
	
	@Override
	default long[] getEvenOddPositionsMinAndMaxValue(boolean isEven) throws Exception {
		int maxW = getMaxW(), minW = getMinW(),
				maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxY(), minY = getMinY(),
				maxZ = getMaxZ(), minZ = getMinZ();
		long minValue = Long.MAX_VALUE, maxValue = Long.MIN_VALUE;
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					boolean isPositionEven = (minW+x+y+z)%2 == 0;
					if (isPositionEven != isEven) {
						minW++;
					}
					for (int w = minW; w <= maxW; w+=2) {
						long value = getValueAtPosition(w, x, y, z);
						if (value > maxValue)
							maxValue = value;
						if (value < minValue)
							minValue = value;
					}
				}
			}
		}
		return new long[]{minValue, maxValue};
	}
	
	@Override
	default long getTotalValue() throws Exception {
		long total = 0;
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
	default LongGrid2D crossSectionAtYZ(int y, int z) {
		return new LongGrid4DYZCrossSection(this, y, z);
	}
	
	@Override
	default LongGrid4D subGrid(int minW, int maxW, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new LongSubGrid4D(this, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default LongGrid3D crossSectionAtZ(int z) {
		return new LongGrid4DZCrossSection<LongGrid4D>(this, z);
	}
}
