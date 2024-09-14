/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2024 Jaume Ribas

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
import cellularautomata.model.IntModel;
import cellularautomata.model4d.IntModel4D;
import cellularautomata.numbers.BigInt;

public interface IntModel5D extends Model5D, IntModel {
	
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
	int getFromPosition(int v, int w, int x, int y, int z) throws Exception;
	
	@Override
	default int getFromPosition(Coordinates coordinates) throws Exception {
		return getFromPosition(coordinates.get(0), coordinates.get(1), coordinates.get(2), coordinates.get(3), coordinates.get(4));
	}

	@Override
	default int[] getMinAndMax() throws Exception {
		int maxV = getMaxV(), minV = getMinV(), maxW, minW, maxX, minX, maxY, minY, maxZ, minZ;
		int minValue = Integer.MAX_VALUE, maxValue = Integer.MIN_VALUE;
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
							int value = getFromPosition(v, w, x, y, z);
							if (value > maxValue)
								maxValue = value;
							if (value < minValue)
								minValue = value;
						}
					}
				}
			}
		}
		return new int[]{minValue, maxValue};
	}
	
	@Override
	default int[] getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		boolean anyPositionMatches = false;		
		int maxV = getMaxV(), minV = getMinV(), maxW, minW, maxX, minX, maxY, minY, maxZ, minZ;
		int minValue = Integer.MAX_VALUE, maxValue = Integer.MIN_VALUE;
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
						boolean isPositionEven = (minZ+v+w+x+y)%2 == 0;
						if (isPositionEven != isEven) {
							minZ++;
						}
						for (int z = minZ; z <= maxZ; z += 2) {
							anyPositionMatches = true;
							int value = getFromPosition(v, w, x, y, z);
							if (value > maxValue)
								maxValue = value;
							if (value < minValue)
								minValue = value;
						}
					}
				}
			}
		}
		return anyPositionMatches ? new int[]{minValue, maxValue} : null;
	}
	
	@Override
	default BigInt getTotal() throws Exception {
		BigInt total = BigInt.ZERO;
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
							total = total.add(BigInt.valueOf(getFromPosition(v, w, x, y, z)));
						}
					}
				}
			}
		}
		return total;
	}
	
	@Override
	default IntModel5D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return (IntModel5D) Model5D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	default IntModel5D subsection(Integer minV, Integer maxV, Integer minW, Integer maxW, Integer minX, Integer maxX, Integer minY, Integer maxY, Integer minZ, Integer maxZ) {
		return new IntSubModel5D(this, minV, maxV, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default IntModel4D crossSection(int axis, int coordinate) {
		return (IntModel4D) Model5D.super.crossSection(axis, coordinate);
	}

	@Override
	default IntModel4D crossSectionAtV(int v) {
		return new IntModel5DVCrossSection(this, v);
	}

	@Override
	default IntModel4D crossSectionAtW(int w) {
		return new IntModel5DWCrossSection(this, w);
	}

	@Override
	default IntModel4D crossSectionAtX(int x) {
		return new IntModel5DXCrossSection(this, x);
	}

	@Override
	default IntModel4D crossSectionAtY(int y) {
		return new IntModel5DYCrossSection(this, y);
	}

	@Override
	default IntModel4D crossSectionAtZ(int z) {
		return new IntModel5DZCrossSection(this, z);
	}
	
	@Override
	default IntModel4D diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return (IntModel4D) Model5D.super.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}
	
	@Override
	default IntModel4D diagonalCrossSectionOnVW(boolean positiveSlope, int wOffsetFromV) {
		return new IntModel5DVWDiagonalCrossSection(this, positiveSlope, wOffsetFromV);
	}
	
	@Override
	default IntModel4D diagonalCrossSectionOnVX(boolean positiveSlope, int xOffsetFromV) {
		return new IntModel5DVXDiagonalCrossSection(this, positiveSlope, xOffsetFromV);
	}
	
	@Override
	default IntModel4D diagonalCrossSectionOnVY(boolean positiveSlope, int yOffsetFromV) {
		return new IntModel5DVYDiagonalCrossSection(this, positiveSlope, yOffsetFromV);
	}
	
	@Override
	default IntModel4D diagonalCrossSectionOnVZ(boolean positiveSlope, int zOffsetFromV) {
		return new IntModel5DVZDiagonalCrossSection(this, positiveSlope, zOffsetFromV);
	}
	
	@Override
	default IntModel4D diagonalCrossSectionOnWX(boolean positiveSlope, int xOffsetFromW) {
		return new IntModel5DWXDiagonalCrossSection(this, positiveSlope, xOffsetFromW);
	}
	
	@Override
	default IntModel4D diagonalCrossSectionOnWY(boolean positiveSlope, int yOffsetFromW) {
		return new IntModel5DWYDiagonalCrossSection(this, positiveSlope, yOffsetFromW);
	}
	
	@Override
	default IntModel4D diagonalCrossSectionOnWZ(boolean positiveSlope, int zOffsetFromW) {
		return new IntModel5DWZDiagonalCrossSection(this, positiveSlope, zOffsetFromW);
	}
	
	@Override
	default IntModel4D diagonalCrossSectionOnXY(boolean positiveSlope, int yOffsetFromX) {
		return new IntModel5DXYDiagonalCrossSection(this, positiveSlope, yOffsetFromX);
	}
	
	@Override
	default IntModel4D diagonalCrossSectionOnXZ(boolean positiveSlope, int zOffsetFromX) {
		return new IntModel5DXZDiagonalCrossSection(this, positiveSlope, zOffsetFromX);
	}
	
	@Override
	default IntModel4D diagonalCrossSectionOnYZ(boolean positiveSlope, int zOffsetFromY) {
		return new IntModel5DYZDiagonalCrossSection(this, positiveSlope, zOffsetFromY);
	}

	@Override
	default Iterator<Integer> iterator() {
		return new IntModel5DIterator(this);
	}
	
}
