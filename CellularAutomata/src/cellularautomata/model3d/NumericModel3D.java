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
package cellularautomata.model3d;

import org.apache.commons.math3.FieldElement;

import cellularautomata.model.MinAndMax;
import cellularautomata.model.NumericModel;
import cellularautomata.model2d.NumericModel2D;

public interface NumericModel3D<T extends FieldElement<T> & Comparable<T>> extends ObjectModel3D<T>, NumericModel<T> {

	@Override
	default MinAndMax<T> getMinAndMax() throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY, maxZ, minZ;
		minY = getMinYAtX(minX);
		T maxValue = getFromPosition(minX, minY, getMinZ(minX, minY));
		T minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinYAtX(x);
			maxY = getMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getMinZ(x, y);
				maxZ = getMaxZ(x, y);
				for (int z = minZ; z <= maxZ; z++) {
					T value = getFromPosition(x, y, z);
					if (value.compareTo(maxValue) > 0)
						maxValue = value;
					if (value.compareTo(minValue) < 0)
						minValue = value;
				}
			}
		}
		return new MinAndMax<T>(minValue, maxValue);
	}
	
	@Override
	default MinAndMax<T> getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		int maxX = getMaxX(), minX = getMinX();
		T maxValue = null;
		T minValue = null;
		int x = minX;
		for (; x <= maxX && maxValue == null; x++) {
			int minY = getMinYAtX(x);
			int maxY = getMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				int minZ = getMinZ(x, y);
				int maxZ = getMaxZ(x, y);
				boolean isPositionEven = (minZ+x+y)%2 == 0;
				if (isPositionEven != isEven) {
					minZ++;
				}
				if (minZ <= maxZ) {
					T value = getFromPosition(x, y, minZ);
					maxValue = value;
					minValue = value;
					for (int z = minZ + 2; z <= maxZ; z+=2) {
						value = getFromPosition(x, y, z);
						if (value.compareTo(maxValue) > 0)
							maxValue = value;
						if (value.compareTo(minValue) < 0)
							minValue = value;
					}
				}
			}
		}
		for (; x <= maxX; x++) {
			int minY = getMinYAtX(x);
			int maxY = getMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				int minZ = getMinZ(x, y);
				int maxZ = getMaxZ(x, y);
				boolean isPositionEven = (minZ+x+y)%2 == 0;
				if (isPositionEven != isEven) {
					minZ++;
				}
				for (int z = minZ; z <= maxZ; z+=2) {
					T value = getFromPosition(x, y, z);
					if (value.compareTo(maxValue) > 0)
						maxValue = value;
					if (value.compareTo(minValue) < 0)
						minValue = value;
				}
			}
		}
		return minValue == null? null : new MinAndMax<T>(minValue, maxValue);
	}
	
	@Override
	default T getTotal() throws Exception {
		int maxX = getMaxX(), minX = getMinX();
		int minY = getMinYAtX(minX);
		int minZ = getMinZ(minX, minY);		
		T total = getFromPosition(minX, minY, minZ);
		int maxZ = getMaxZ(minX, minY);
		for (int z = minZ + 1; z <= maxZ; z++) {
			total = total.add(getFromPosition(minX, minY, z));
		}
		int maxY = getMaxYAtX(minX);
		for (int y = minY + 1; y <= maxY; y++) {
			minZ = getMinZ(minX, y);
			maxZ = getMaxZ(minX, y);
			for (int z = minZ; z <= maxZ; z++) {
				total = total.add(getFromPosition(minX, y, z));
			}
		}
		for (int x = minX + 1; x <= maxX; x++) {
			minY = getMinYAtX(x);
			maxY = getMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getMinZ(x, y);
				maxZ = getMaxZ(x, y);
				for (int z = minZ; z <= maxZ; z++) {
					total = total.add(getFromPosition(x, y, z));
				}
			}
		}
		return total;
	}
	
	@Override
	default NumericModel3D<T> subsection(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new NumericSubModel3D<T, NumericModel3D<T>>(this, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default NumericModel2D<T> crossSectionAtX(int x) {
		return new NumericModel3DXCrossSection<T, NumericModel3D<T>>(this, x);
	}
	
	@Override
	default NumericModel2D<T> crossSectionAtY(int y) {
		return new NumericModel3DYCrossSection<T, NumericModel3D<T>>(this, y);
	}
	
	@Override
	default NumericModel2D<T> crossSectionAtZ(int z) {
		return new NumericModel3DZCrossSection<T, NumericModel3D<T>>(this, z);
	}
	
	@Override
	default NumericModel2D<T> diagonalCrossSectionOnXY(int yOffsetFromX) {
		return new NumericModel3DXYDiagonalCrossSection<T, NumericModel3D<T>>(this, yOffsetFromX);
	}
	
	@Override
	default NumericModel2D<T> diagonalCrossSectionOnXZ(int zOffsetFromX) {
		return new NumericModel3DXZDiagonalCrossSection<T, NumericModel3D<T>>(this, zOffsetFromX);
	}
	
	@Override
	default NumericModel2D<T> diagonalCrossSectionOnYZ(int zOffsetFromY) {
		return new NumericModel3DYZDiagonalCrossSection<T, NumericModel3D<T>>(this, zOffsetFromY);
	}

}
