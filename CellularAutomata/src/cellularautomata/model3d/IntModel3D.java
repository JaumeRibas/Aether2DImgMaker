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
import cellularautomata.model.IntModel;
import cellularautomata.model2d.IntModel2D;

public interface IntModel3D extends Model3D, IntModel {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-axis
	 * @param y the position on the y-axis
	 * @param z the position on the z-axis
	 * @return the value at (x,y,z)
	 * @throws Exception 
	 */
	int getFromPosition(int x, int y, int z) throws Exception;
	
	@Override
	default int getFromPosition(Coordinates coordinates) throws Exception {
		return getFromPosition(coordinates.get(0), coordinates.get(1), coordinates.get(2));
	}

	@Override
	default int[] getMinAndMax() throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY, maxZ, minZ;
		int maxValue = Integer.MIN_VALUE, minValue = Integer.MAX_VALUE;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinYAtX(x);
			maxY = getMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getMinZ(x, y);
				maxZ = getMaxZ(x, y);
				for (int z = minZ; z <= maxZ; z++) {
					int value = getFromPosition(x, y, z);
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
	default int[] getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		boolean anyPositionMatches = false;
		int maxX = getMaxX(), minX = getMinX(), maxY, minY, maxZ, minZ;
		int minValue = Integer.MAX_VALUE, maxValue = Integer.MIN_VALUE;
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
					int value = getFromPosition(x, y, z);
					if (value > maxValue)
						maxValue = value;
					if (value < minValue)
						minValue = value;
				}
			}
		}
		return anyPositionMatches ? new int[]{minValue, maxValue} : null;
	}
	
	@Override
	default int getTotal() throws Exception {
		int total = 0;
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
	default IntModel3D subsection(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new IntSubModel3D(this, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default IntModel2D crossSectionAtX(int x) {
		return new IntModel3DXCrossSection(this, x);
	}
	
	@Override
	default IntModel2D crossSectionAtY(int y) {
		return new IntModel3DYCrossSection(this, y);
	}
	
	@Override
	default IntModel2D crossSectionAtZ(int z) {
		return new IntModel3DZCrossSection(this, z);
	}
	
	@Override
	default IntModel2D diagonalCrossSectionOnXY(int yOffsetFromX) {
		return new IntModel3DXYDiagonalCrossSection(this, yOffsetFromX);
	}
	
	@Override
	default IntModel2D diagonalCrossSectionOnXZ(int zOffsetFromX) {
		return new IntModel3DXZDiagonalCrossSection(this, zOffsetFromX);
	}
	
	@Override
	default IntModel2D diagonalCrossSectionOnYZ(int zOffsetFromY) {
		return new IntModel3DYZDiagonalCrossSection(this, zOffsetFromY);
	}

	@Override
	default Iterator<Integer> iterator() {
		return new IntModel3DIterator(this);
	}
	
}
