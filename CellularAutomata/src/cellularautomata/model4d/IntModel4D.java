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
import cellularautomata.model.IntModel;
import cellularautomata.model3d.IntModel3D;

public interface IntModel4D extends Model4D, IntModel {
	
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
	int getFromPosition(int w, int x, int y, int z) throws Exception;
	
	@Override
	default int getFromPosition(Coordinates coordinates) throws Exception {
		return getFromPosition(coordinates.get(0), coordinates.get(1), coordinates.get(2), coordinates.get(3));
	}

	@Override
	default int[] getMinAndMax() throws Exception {
		int maxW = getMaxW(), minW = getMinW(), maxX, minX, maxY, minY, maxZ, minZ;
		int minValue = Integer.MAX_VALUE, maxValue = Integer.MIN_VALUE;
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
						int value = getFromPosition(w, x, y, z);
						if (value > maxValue)
							maxValue = value;
						if (value < minValue)
							minValue = value;
					}
				}
			}
		}
		return new int[]{minValue, maxValue};
	}
	
	@Override
	default int[] getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		boolean anyPositionMatches = false;
		int maxW = getMaxW(), minW = getMinW(), maxX, minX, maxY, minY, maxZ, minZ;
		int minValue = Integer.MAX_VALUE, maxValue = Integer.MIN_VALUE;
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
						int value = getFromPosition(w, x, y, z);
						if (value > maxValue)
							maxValue = value;
						if (value < minValue)
							minValue = value;
					}
				}
			}
		}
		return anyPositionMatches ? new int[]{minValue, maxValue} : null;
	}

	@Override
	default int getTotal() throws Exception {
		int total = 0;
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
	default IntModel4D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return (IntModel4D) Model4D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	default IntModel4D subsection(int minW, int maxW, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new IntSubModel4D(this, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default IntModel3D crossSection(int axis, int coordinate) {
		return (IntModel3D) Model4D.super.crossSection(axis, coordinate);
	}
	
	@Override
	default IntModel3D crossSectionAtW(int w) {
		return new IntModel4DWCrossSection(this, w);
	}
	
	@Override
	default IntModel3D crossSectionAtX(int x) {
		return new IntModel4DXCrossSection(this, x);
	}
	
	@Override
	default IntModel3D crossSectionAtY(int y) {
		return new IntModel4DYCrossSection(this, y);
	}
	
	@Override
	default IntModel3D crossSectionAtZ(int z) {
		return new IntModel4DZCrossSection(this, z);
	}
	
	@Override
	default IntModel3D diagonalCrossSectionOnWX(boolean positiveSlope, int xOffsetFromW) {
		return new IntModel4DWXDiagonalCrossSection(this, positiveSlope, xOffsetFromW);
	}

	@Override
	default IntModel3D diagonalCrossSectionOnYZ(boolean positiveSlope, int zOffsetFromY) {
		return new IntModel4DYZDiagonalCrossSection(this, positiveSlope, zOffsetFromY);
	}

	@Override
	default Iterator<Integer> iterator() {
		return new IntModel4DIterator(this);
	}
	
}
