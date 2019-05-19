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
package cellularautomata.grid3D;

import cellularautomata.grid.IntGrid;
import cellularautomata.grid2D.IntGrid2D;

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
	default int[] getMinAndMaxValueAtEvenPositions() throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY, maxZ, minZ;
		//even
		int evenMinValue = Integer.MAX_VALUE, evenMaxValue = Integer.MIN_VALUE;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinYAtX(x);
			maxY = getMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getMinZ(x, y);
				maxZ = getMaxZ(x, y);
				if ((minZ+x+y)%2 != 0) {
					minZ++;
				}
				for (int z = minZ; z <= maxZ; z+=2) {
					int value = getValueAtPosition(x, y, z);
					if (value > evenMaxValue)
						evenMaxValue = value;
					if (value < evenMinValue)
						evenMinValue = value;
				}
			}
		}
		return new int[]{evenMinValue, evenMaxValue};
	}
	
	@Override
	default int[] getMinAndMaxValueAtOddPositions() throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY, maxZ, minZ;
		//odd
		int oddMinValue = Integer.MAX_VALUE, oddMaxValue = Integer.MIN_VALUE;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinYAtX(x);
			maxY = getMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getMinZ(x, y);
				maxZ = getMaxZ(x, y);
				if ((minZ+x+y)%2 == 0) {
					minZ++;
				}
				for (int z = minZ; z <= maxZ; z+=2) {
					int value = getValueAtPosition(x, y, z);
					if (value > oddMaxValue)
						oddMaxValue = value;
					if (value < oddMinValue)
						oddMinValue = value;
				}
			}
		}
		return new int[]{oddMinValue, oddMaxValue};
	}
	
	@Override
	default int[] getMinAndMaxValueExcluding(int backgroundValue) throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY, maxZ, minZ;
		int maxValue, minValue;
		switch (backgroundValue) {
			case Integer.MIN_VALUE:
				maxValue = Integer.MIN_VALUE + 1;
				minValue = Integer.MAX_VALUE;
				break;
			case Integer.MAX_VALUE:
				maxValue = Integer.MIN_VALUE;
				minValue = Integer.MAX_VALUE - 1;
				break;
			default:
				maxValue = Integer.MIN_VALUE;
				minValue = Integer.MAX_VALUE;
		}
		for (int x = minX; x <= maxX; x++) {
			minY = getMinYAtX(x);
			maxY = getMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getMinZ(x, y);
				maxZ = getMaxZ(x, y);
				for (int z = minZ; z <= maxZ; z++) {
					int value = getValueAtPosition(x, y, z);
					if (value != backgroundValue) {
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
	default int getMaxAbsoluteValue() throws Exception {
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
	
	default IntGrid3D absoluteGrid() {
		return new AbsIntGrid3D(this);
	}
	
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
	
	default IntGrid2D crossSection(int z) {
		return new IntGrid3DCrossSection(this, z);
	}
	
	default IntGrid2D projectedSurfaceMaxX(int backgroundValue) {
		return new IntGrid3DProjectedSurfaceMaxX(this, backgroundValue);
	}
	
}
