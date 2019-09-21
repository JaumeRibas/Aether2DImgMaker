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
package cellularautomata.grid4d;

import cellularautomata.grid.LongGrid;
import cellularautomata.grid2d.LongGrid2D;

public interface LongGrid4D extends Grid4D, LongGrid {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param w the position on the w-coordinate 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @param z the position on the z-coordinate
	 * @return the value at (w,x,y,z)
	 */
	long getValueAtPosition(int w, int x, int y, int z);

	@Override
	default long[] getMinAndMaxValue() {
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
	default long[] getMinAndMaxValueAtEvenPositions() throws Exception {
		int maxW = getMaxW(), minW = getMinW(),
				maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxY(), minY = getMinY(),
				maxZ = getMaxZ(), minZ = getMinZ();
		long evenMinValue = Integer.MAX_VALUE, evenMaxValue = Integer.MIN_VALUE;
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					if ((minW+x+y+z)%2 != 0) {
						minW++;
					}
					for (int w = minW; w <= maxW; w+=2) {
						long value = getValueAtPosition(w, x, y, z);
						if (value > evenMaxValue)
							evenMaxValue = value;
						if (value < evenMinValue)
							evenMinValue = value;
					}
				}
			}
		}
		return new long[]{evenMinValue, evenMaxValue};
	}
	
	@Override
	default long[] getMinAndMaxValueAtOddPositions() throws Exception {
		int maxW = getMaxW(), minW = getMinW(),
				maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxY(), minY = getMinY(),
				maxZ = getMaxZ(), minZ = getMinZ();
		long oddMinValue = Integer.MAX_VALUE, oddMaxValue = Integer.MIN_VALUE;
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					if ((minW+x+y+z)%2 == 0) {
						minW++;
					}
					for (int w = minW; w <= maxW; w+=2) {
						long value = getValueAtPosition(w, x, y, z);
						if (value > oddMaxValue)
							oddMaxValue = value;
						if (value < oddMinValue)
							oddMinValue = value;
					}
				}
			}
		}
		return new long[]{oddMinValue, oddMaxValue};
	}
	
	@Override
	default long[] getMinAndMaxValueExcluding(long excludedValue) {
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
						if (value != excludedValue) {
							if (value > maxValue)
								maxValue = value;
							if (value < minValue)
								minValue = value;
						}
					}
				}
			}
		}
		return new long[]{ minValue, maxValue };
	}
	
	@Override
	default long getTotalValue() {
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
	default long getMaxAbsoluteValue() {
		long maxAbsoluteValue;
		long[] minAndMax = getMinAndMaxValue();
		if (minAndMax[0] < 0) {
			minAndMax[0] = Math.abs(minAndMax[0]);
			maxAbsoluteValue = Math.max(minAndMax[0], minAndMax[1]);
		} else {
			maxAbsoluteValue = minAndMax[1];
		}
		return maxAbsoluteValue;
	}
	
	default LongGrid4D absoluteGrid() {
		return new AbsLongGrid4D(this);
	}
	
	default LongGrid2D crossSectionAtYZ(int y, int z) {
		return new LongGrid4DYZCrossSection(this, y, z);
	}
	
	default LongGrid4D subGrid(int minW, int maxW, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
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
		return new LongSubGrid4D(this, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
}
