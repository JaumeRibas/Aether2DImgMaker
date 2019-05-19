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
package cellularautomata.grid4D;

import cellularautomata.grid.IntGrid;

public interface IntGrid4D extends Grid4D, IntGrid {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param w the position on the w-coordinate 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @param z the position on the z-coordinate
	 * @return the value at (w,x,y,z)
	 */
	int getValueAtPosition(int w, int x, int y, int z);

	@Override
	default int[] getMinAndMaxValue() {
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
	default int[] getMinAndMaxValueAtEvenPositions() throws Exception {
		int maxW = getMaxW(), minW = getMinW(),
				maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxY(), minY = getMinY(),
				maxZ = getMaxZ(), minZ = getMinZ();
		int evenMinValue = Integer.MAX_VALUE, evenMaxValue = Integer.MIN_VALUE;
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					if ((minW+x+y+z)%2 != 0) {
						minW++;
					}
					for (int w = minW; w <= maxW; w+=2) {
						int value = getValueAtPosition(w, x, y, z);
						if (value > evenMaxValue)
							evenMaxValue = value;
						if (value < evenMinValue)
							evenMinValue = value;
					}
				}
			}
		}
		return new int[]{evenMinValue, evenMaxValue};
	}
	
	@Override
	default int[] getMinAndMaxValueAtOddPositions() throws Exception {
		int maxW = getMaxW(), minW = getMinW(),
				maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxY(), minY = getMinY(),
				maxZ = getMaxZ(), minZ = getMinZ();
		int oddMinValue = Integer.MAX_VALUE, oddMaxValue = Integer.MIN_VALUE;
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					if ((minW+x+y+z)%2 == 0) {
						minW++;
					}
					for (int w = minW; w <= maxW; w+=2) {
						int value = getValueAtPosition(w, x, y, z);
						if (value > oddMaxValue)
							oddMaxValue = value;
						if (value < oddMinValue)
							oddMinValue = value;
					}
				}
			}
		}
		return new int[]{oddMinValue, oddMaxValue};
	}
	
	@Override
	default int[] getMinAndMaxValueExcluding(int backgroundValue) {
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
						if (value != backgroundValue) {
							if (value > maxValue)
								maxValue = value;
							if (value < minValue)
								minValue = value;
						}
					}
				}
			}
		}
		return new int[]{ minValue, maxValue };
	}
	
	@Override
	default int getTotalValue() {
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
	default int getMaxAbsoluteValue() {
		int maxAbsoluteValue;
		int[] minAndMax = getMinAndMaxValue();
		if (minAndMax[0] < 0) {
			minAndMax[0] = Math.abs(minAndMax[0]);
			maxAbsoluteValue = Math.max(minAndMax[0], minAndMax[1]);
		} else {
			maxAbsoluteValue = minAndMax[1];
		}
		return maxAbsoluteValue;
	}
	
	default IntGrid4D absoluteGrid() {
		return new AbsIntGrid4D(this);
	}
	
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
}
