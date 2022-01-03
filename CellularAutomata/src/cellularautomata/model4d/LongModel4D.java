/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2021 Jaume Ribas

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

import cellularautomata.model.Coordinates;
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
	default LongModel4D subsection(int minW, int maxW, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new LongSubModel4D(this, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default LongModel3D crossSectionAtW(int w) {
		return new LongModel4DWCrossSection<LongModel4D>(this, w);
	}
	
	@Override
	default LongModel3D crossSectionAtX(int x) {
		return new LongModel4DXCrossSection<LongModel4D>(this, x);
	}
	
	@Override
	default LongModel3D crossSectionAtY(int y) {
		return new LongModel4DYCrossSection<LongModel4D>(this, y);
	}
	
	@Override
	default LongModel3D crossSectionAtZ(int z) {
		return new LongModel4DZCrossSection<LongModel4D>(this, z);
	}
	
	@Override
	default LongModel3D diagonalCrossSectionOnWX(int xOffsetFromW) {
		return new LongModel4DWXDiagonalCrossSection<LongModel4D>(this, xOffsetFromW);
	}

	@Override
	default LongModel3D diagonalCrossSectionOnYZ(int zOffsetFromY) {
		return new LongModel4DYZDiagonalCrossSection<LongModel4D>(this, zOffsetFromY);
	}

	@Override
	default Iterator<Long> iterator() {
		return new LongModel4DIterator(this);
	}
}
