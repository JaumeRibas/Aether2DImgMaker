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
package cellularautomata.model5d;

import java.util.Iterator;

import cellularautomata.Coordinates;
import cellularautomata.model.LongModel;
import cellularautomata.model4d.LongModel4D;

public interface LongModel5D extends Model5D, LongModel {
	
	/**
	 * Returns the value at a given position
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
	default LongModel5D subsection(int minV, int maxV, int minW, int maxW, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new LongSubModel5D(this, minV, maxV, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}

	@Override
	default LongModel4D crossSectionAtV(int v) {
		return new LongModel5DVCrossSection(this, v);
	}
	
	@Override
	default LongModel4D diagonalCrossSectionOnVW(boolean positiveSlope, int wOffsetFromV) {
		return new LongModel5DVWDiagonalCrossSection(this, positiveSlope, wOffsetFromV);
	}

	@Override
	default Iterator<Long> iterator() {
		return new LongModel5DIterator(this);
	}

}
