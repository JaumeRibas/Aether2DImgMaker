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
package cellularautomata.model4d;

import java.util.Iterator;

import cellularautomata.Coordinates;
import cellularautomata.PartialCoordinates;
import cellularautomata.model.LongModel;
import cellularautomata.model3d.LongModel3D;

public interface LongModel4D extends Model4D, LongModel {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param w the position on the w-axis 
	 * @param x the position on the x-axis
	 * @param y the position on the y-axis
	 * @param z the position on the z-axis
	 * @return the value at (w,x,y,z)
	 * @throws Exception 
	 */
	long getFromPosition(int w, int x, int y, int z) throws Exception;
	
	@Override
	default long getFromPosition(Coordinates coordinates) throws Exception {
		return getFromPosition(coordinates.get(0), coordinates.get(1), coordinates.get(2), coordinates.get(3));
	}

	@Override
	default long[] getMinAndMax() throws Exception {
		int maxW = getMaxW(), minW = getMinW(), maxX, minX, maxY, minY, maxZ, minZ;
		long minValue = Long.MAX_VALUE, maxValue = Long.MIN_VALUE;
		for (int w = minW; w <= maxW; w++) {
			minX = getMinXAtW(w);
			maxX = getMaxXAtW(w);
			for (int x = minX; x <= maxX; x++) {
				minY = getMinYAtWX(w, x);
				maxY = getMaxYAtWX(w, x);
				for (int y = minY; y <= maxY; y++) {
					minZ = getMinZ(w, x, y);
					maxZ = getMaxZ(w, x, y);
					for (int z = minZ; z <= maxZ; z++) {
						long value = getFromPosition(w, x, y, z);
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
	default long[] getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		boolean anyPositionMatches = false;
		int maxW = getMaxW(), minW = getMinW(), maxX, minX, maxY, minY, maxZ, minZ;
		long minValue = Long.MAX_VALUE, maxValue = Long.MIN_VALUE;
		for (int w = minW; w <= maxW; w++) {
			minX = getMinXAtW(w);
			maxX = getMaxXAtW(w);
			for (int x = minX; x <= maxX; x++) {
				minY = getMinYAtWX(w, x);
				maxY = getMaxYAtWX(w, x);
				for (int y = minY; y <= maxY; y++) {
					minZ = getMinZ(w, x, y);
					maxZ = getMaxZ(w, x, y);
					boolean isPositionEven = (minZ+w+x+y)%2 == 0;
					if (isPositionEven != isEven) {
						minZ++;
					}
					for (int z = minZ; z <= maxZ; z+=2) {
						anyPositionMatches = true;
						long value = getFromPosition(w, x, y, z);
						if (value > maxValue)
							maxValue = value;
						if (value < minValue)
							minValue = value;
					}
				}
			}
		}
		return anyPositionMatches ? new long[]{minValue, maxValue} : null;
	}
	
	@Override
	default long getTotal() throws Exception {
		long total = 0;
		int maxW = getMaxW(), minW = getMinW(), maxX, minX, maxY, minY, maxZ, minZ;
		for (int w = minW; w <= maxW; w++) {
			minX = getMinXAtW(w);
			maxX = getMaxXAtW(w);
			for (int x = minX; x <= maxX; x++) {
				minY = getMinYAtWX(w, x);
				maxY = getMaxYAtWX(w, x);
				for (int y = minY; y <= maxY; y++) {
					minZ = getMinZ(w, x, y);
					maxZ = getMaxZ(w, x, y);
					for (int z = minZ; z <= maxZ; z++) {
						total += getFromPosition(w, x, y, z);
					}
				}
			}
		}
		return total;
	}
	
	@Override
	default LongModel4D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return (LongModel4D) Model4D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	default LongModel4D subsection(Integer minW, Integer maxW, Integer minX, Integer maxX, Integer minY, Integer maxY, Integer minZ, Integer maxZ) {
		return new LongSubModel4D(this, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default LongModel3D crossSection(int axis, int coordinate) {
		return (LongModel3D) Model4D.super.crossSection(axis, coordinate);
	}
	
	@Override
	default LongModel3D crossSectionAtW(int w) {
		return new LongModel4DWCrossSection(this, w);
	}
	
	@Override
	default LongModel3D crossSectionAtX(int x) {
		return new LongModel4DXCrossSection(this, x);
	}
	
	@Override
	default LongModel3D crossSectionAtY(int y) {
		return new LongModel4DYCrossSection(this, y);
	}
	
	@Override
	default LongModel3D crossSectionAtZ(int z) {
		return new LongModel4DZCrossSection(this, z);
	}
	
	@Override
	default LongModel3D diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return (LongModel3D) Model4D.super.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}
	
	@Override
	default LongModel3D diagonalCrossSectionOnWX(boolean positiveSlope, int xOffsetFromW) {
		return new LongModel4DWXDiagonalCrossSection(this, positiveSlope, xOffsetFromW);
	}
	
	@Override
	default LongModel3D diagonalCrossSectionOnWY(boolean positiveSlope, int yOffsetFromW) {
		return new LongModel4DWYDiagonalCrossSection(this, positiveSlope, yOffsetFromW);
	}
	
	@Override
	default LongModel3D diagonalCrossSectionOnWZ(boolean positiveSlope, int zOffsetFromW) {
		return new LongModel4DWZDiagonalCrossSection(this, positiveSlope, zOffsetFromW);
	}
	
	@Override
	default LongModel3D diagonalCrossSectionOnXY(boolean positiveSlope, int yOffsetFromX) {
		return new LongModel4DXYDiagonalCrossSection(this, positiveSlope, yOffsetFromX);
	}
	
	@Override
	default LongModel3D diagonalCrossSectionOnXZ(boolean positiveSlope, int zOffsetFromX) {
		return new LongModel4DXZDiagonalCrossSection(this, positiveSlope, zOffsetFromX);
	}

	@Override
	default LongModel3D diagonalCrossSectionOnYZ(boolean positiveSlope, int zOffsetFromY) {
		return new LongModel4DYZDiagonalCrossSection(this, positiveSlope, zOffsetFromY);
	}

	@Override
	default Iterator<Long> iterator() {
		return new LongModel4DIterator(this);
	}
}
