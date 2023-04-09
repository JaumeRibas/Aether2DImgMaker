/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2023 Jaume Ribas

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
package cellularautomata.model5d;

import java.util.Iterator;

import cellularautomata.Coordinates;
import cellularautomata.PartialCoordinates;
import cellularautomata.model.LongModel;
import cellularautomata.model4d.LongModel4D;

public interface LongModel5D extends Model5D, LongModel {
	
	/**
	 * <p>Returns the value at a given position.</p>
	 * <p>It is not defined to call this method passing coordinates outside the bounds of the region.</p>
	 * 
	 * @param v the position on the v-axis 
	 * @param w the position on the w-axis 
	 * @param x the position on the x-axis
	 * @param y the position on the y-axis
	 * @param z the position on the z-axis
	 * @return the value at (v,w,x,y,z)
	 * @throws Exception 
	 */
	long getFromPosition(int v, int w, int x, int y, int z) throws Exception;
	
	@Override
	default long getFromPosition(Coordinates coordinates) throws Exception {
		return getFromPosition(coordinates.get(0), coordinates.get(1), coordinates.get(2), coordinates.get(3), coordinates.get(4));
	}

	@Override
	default long[] getMinAndMax() throws Exception {
		int maxV = getMaxV(), minV = getMinV(), maxW, minW, maxX, minX, maxY, minY, maxZ, minZ;
		long minValue = Long.MAX_VALUE, maxValue = Long.MIN_VALUE;
		for (int v = minV; v <= maxV; v++) {
			minW = getMinWAtV(v);
			maxW = getMaxWAtV(v);
			for (int w = minW; w <= maxW; w++) {
				minX = getMinXAtVW(v, w);
				maxX = getMaxXAtVW(v, w);
				for (int x = minX; x <= maxX; x++) {
					minY = getMinYAtVWX(v, w, x);
					maxY = getMaxYAtVWX(v, w, x);
					for (int y = minY; y <= maxY; y++) {
						minZ = getMinZ(v, w, x, y);
						maxZ = getMaxZ(v, w, x, y);
						for (int z = minZ; z <= maxZ; z++) {
							long value = getFromPosition(v, w, x, y, z);
							if (value > maxValue)
								maxValue = value;
							if (value < minValue)
								minValue = value;
						}
					}
				}
			}
		}
		return new long[]{minValue, maxValue};
	}
	
	@Override
	default long[] getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		boolean anyPositionMatches = false;		
		int maxV = getMaxV(), minV = getMinV(), maxW, minW, maxX, minX, maxY, minY, maxZ, minZ;
		long minValue = Long.MAX_VALUE, maxValue = Long.MIN_VALUE;
		for (int v = minV; v <= maxV; v++) {
			minW = getMinWAtV(v);
			maxW = getMaxWAtV(v);
			for (int w = minW; w <= maxW; w++) {
				minX = getMinXAtVW(v, w);
				maxX = getMaxXAtVW(v, w);
				for (int x = minX; x <= maxX; x++) {
					minY = getMinYAtVWX(v, w, x);
					maxY = getMaxYAtVWX(v, w, x);
					for (int y = minY; y <= maxY; y++) {
						minZ = getMinZ(v, w, x, y);
						maxZ = getMaxZ(v, w, x, y);
						boolean isPositionEven = (minZ+w+x+y)%2 == 0;
						if (isPositionEven != isEven) {
							minZ++;
						}
						for (int z = minZ; z <= maxZ; z+=2) {
							long value = getFromPosition(v, w, x, y, z);
							if (value > maxValue)
								maxValue = value;
							if (value < minValue)
								minValue = value;
						}
					}
				}
			}
		}
		return anyPositionMatches ? new long[]{minValue, maxValue} : null;
	}
	
	@Override
	default long getTotal() throws Exception {
		long total = 0;
		int maxV = getMaxV(), minV = getMinV(), maxW, minW, maxX, minX, maxY, minY, maxZ, minZ;
		for (int v = minV; v <= maxV; v++) {
			minW = getMinWAtV(v);
			maxW = getMaxWAtV(v);
			for (int w = minW; w <= maxW; w++) {
				minX = getMinXAtVW(v, w);
				maxX = getMaxXAtVW(v, w);
				for (int x = minX; x <= maxX; x++) {
					minY = getMinYAtVWX(v, w, x);
					maxY = getMaxYAtVWX(v, w, x);
					for (int y = minY; y <= maxY; y++) {
						minZ = getMinZ(v, w, x, y);
						maxZ = getMaxZ(v, w, x, y);
						for (int z = minZ; z <= maxZ; z++) {
							total += getFromPosition(v, w, x, y, z);
						}
					}
				}
			}
		}
		return total;
	}
	
	@Override
	default LongModel5D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return (LongModel5D) Model5D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	default LongModel5D subsection(Integer minV, Integer maxV, Integer minW, Integer maxW, Integer minX, Integer maxX, Integer minY, Integer maxY, Integer minZ, Integer maxZ) {
		return new LongSubModel5D(this, minV, maxV, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default LongModel4D crossSection(int axis, int coordinate) {
		return (LongModel4D) Model5D.super.crossSection(axis, coordinate);
	}

	@Override
	default LongModel4D crossSectionAtV(int v) {
		return new LongModel5DVCrossSection(this, v);
	}

	@Override
	default LongModel4D crossSectionAtW(int w) {
		return new LongModel5DWCrossSection(this, w);
	}

	@Override
	default LongModel4D crossSectionAtX(int x) {
		return new LongModel5DXCrossSection(this, x);
	}

	@Override
	default LongModel4D crossSectionAtY(int y) {
		return new LongModel5DYCrossSection(this, y);
	}

	@Override
	default LongModel4D crossSectionAtZ(int z) {
		return new LongModel5DZCrossSection(this, z);
	}
	
	@Override
	default LongModel4D diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return (LongModel4D) Model5D.super.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}
	
	@Override
	default LongModel4D diagonalCrossSectionOnVW(boolean positiveSlope, int wOffsetFromV) {
		return new LongModel5DVWDiagonalCrossSection(this, positiveSlope, wOffsetFromV);
	}
	
	@Override
	default LongModel4D diagonalCrossSectionOnVX(boolean positiveSlope, int xOffsetFromV) {
		return new LongModel5DVXDiagonalCrossSection(this, positiveSlope, xOffsetFromV);
	}
	
	@Override
	default LongModel4D diagonalCrossSectionOnVY(boolean positiveSlope, int yOffsetFromV) {
		return new LongModel5DVYDiagonalCrossSection(this, positiveSlope, yOffsetFromV);
	}
	
	@Override
	default LongModel4D diagonalCrossSectionOnVZ(boolean positiveSlope, int zOffsetFromV) {
		return new LongModel5DVZDiagonalCrossSection(this, positiveSlope, zOffsetFromV);
	}
	
	@Override
	default LongModel4D diagonalCrossSectionOnWX(boolean positiveSlope, int xOffsetFromW) {
		return new LongModel5DWXDiagonalCrossSection(this, positiveSlope, xOffsetFromW);
	}
	
	@Override
	default LongModel4D diagonalCrossSectionOnWY(boolean positiveSlope, int yOffsetFromW) {
		return new LongModel5DWYDiagonalCrossSection(this, positiveSlope, yOffsetFromW);
	}
	
	@Override
	default LongModel4D diagonalCrossSectionOnWZ(boolean positiveSlope, int zOffsetFromW) {
		return new LongModel5DWZDiagonalCrossSection(this, positiveSlope, zOffsetFromW);
	}
	
	@Override
	default LongModel4D diagonalCrossSectionOnXY(boolean positiveSlope, int yOffsetFromX) {
		return new LongModel5DXYDiagonalCrossSection(this, positiveSlope, yOffsetFromX);
	}
	
	@Override
	default LongModel4D diagonalCrossSectionOnXZ(boolean positiveSlope, int zOffsetFromX) {
		return new LongModel5DXZDiagonalCrossSection(this, positiveSlope, zOffsetFromX);
	}
	
	@Override
	default LongModel4D diagonalCrossSectionOnYZ(boolean positiveSlope, int zOffsetFromY) {
		return new LongModel5DYZDiagonalCrossSection(this, positiveSlope, zOffsetFromY);
	}

	@Override
	default Iterator<Long> iterator() {
		return new LongModel5DIterator(this);
	}

}
