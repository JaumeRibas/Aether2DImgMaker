/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2022 Jaume Ribas

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
package cellularautomata.model3d;

import java.util.Iterator;

import cellularautomata.Coordinates;
import cellularautomata.model.LongModel;
import cellularautomata.model2d.LongModel2D;

public interface LongModel3D extends Model3D, LongModel {
	
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
	default long getFromPosition(Coordinates coordinates) throws Exception {
		return getFromPosition(coordinates.get(0), coordinates.get(1), coordinates.get(2));
	}

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
	default LongModel3D subsection(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new LongSubModel3D(this, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default LongModel2D crossSectionAtX(int x) {
		return new LongModel3DXCrossSection(this, x);
	}
	
	@Override
	default LongModel2D crossSectionAtY(int y) {
		return new LongModel3DYCrossSection(this, y);
	}
	
	@Override
	default LongModel2D crossSectionAtZ(int z) {
		return new LongModel3DZCrossSection(this, z);
	}
	
	@Override
	default LongModel2D diagonalCrossSectionOnXY(boolean positiveSlope, int yOffsetFromX) {
		return new LongModel3DXYDiagonalCrossSection(this, positiveSlope, yOffsetFromX);
	}
	
	@Override
	default LongModel2D diagonalCrossSectionOnXZ(int zOffsetFromX) {
		return new LongModel3DXZDiagonalCrossSection(this, zOffsetFromX);
	}
	
	@Override
	default LongModel2D diagonalCrossSectionOnYZ(int zOffsetFromY) {
		return new LongModel3DYZDiagonalCrossSection(this, zOffsetFromY);
	}

	@Override
	default Iterator<Long> iterator() {
		return new LongModel3DIterator(this);
	}

}
