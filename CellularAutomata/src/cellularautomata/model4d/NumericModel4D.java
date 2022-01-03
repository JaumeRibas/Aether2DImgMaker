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

import org.apache.commons.math3.FieldElement;

import cellularautomata.model.MinAndMax;
import cellularautomata.model.NumericModel;
import cellularautomata.model3d.NumericModel3D;

public interface NumericModel4D<T extends FieldElement<T> & Comparable<T>> extends ObjectModel4D<T>, NumericModel<T> {

	@Override
	default MinAndMax<T> getMinAndMax() throws Exception {
		int maxW = getMaxW(), minW = getMinW(), maxX, maxY, maxZ, minZ;
		int minX = getMinXAtW(minW);
		int minY = getMinYAtWX(minW, minX);
		T minValue = getFromPosition(minW, minX, minY, getMinZ(minW, minX, minY));
		T maxValue = minValue;
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
						T value = getFromPosition(w, x, y, z);
						if (value.compareTo(maxValue) > 0)
							maxValue = value;
						if (value.compareTo(minValue) < 0)
							minValue = value;
					}
				}
			}
		}
		return new MinAndMax<T>(minValue, maxValue);
	}
	
	@Override
	default MinAndMax<T> getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		int maxW = getMaxW(), minW = getMinW(), maxX, minX, maxY, minY, maxZ, minZ;
		T minValue = null, maxValue = null;
		int w = minW;
		for (; w <= maxW && maxValue == null; w++) {
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
					if (minZ <= maxZ) {
						T value = getFromPosition(w, x, y, minZ);
						maxValue = value;
						minValue = value;
						for (int z = minZ + 2; z <= maxZ; z+=2) {
							value = getFromPosition(w, x, y, z);
							if (value.compareTo(maxValue) > 0)
								maxValue = value;
							if (value.compareTo(minValue) < 0)
								minValue = value;
						}
					}
				}
			}
		}
		for (; w <= maxW; w++) {
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
						T value = getFromPosition(w, x, y, z);
						if (value.compareTo(maxValue) > 0)
							maxValue = value;
						if (value.compareTo(minValue) < 0)
							minValue = value;
					}
				}
			}
		}
		return minValue == null? null : new MinAndMax<T>(minValue, maxValue);
	}
	
	@Override
	default T getTotal() throws Exception {
		int maxW = getMaxW(), minW = getMinW();
		int minX = getMinXAtW(minW);
		int minY = getMinYAtWX(minW, minX);
		int minZ = getMinZ(minW, minX, minY);
		T total = getFromPosition(minW, minX, minY, minZ);
		int maxZ = getMaxZ(minW, minX, minY);
		for (int z = minZ + 1; z <= maxZ; z++) {
			total = total.add(getFromPosition(minW, minX, minY, z));
		}
		int maxY = getMaxYAtWX(minW, minX);
		for (int y = minY + 1; y <= maxY; y++) {
			minZ = getMinZ(minW, minX, y);
			maxZ = getMaxZ(minW, minX, y);
			for (int z = minZ; z <= maxZ; z++) {
				total = total.add(getFromPosition(minW, minX, y, z));
			}
		}
		int maxX = getMaxXAtW(minW);
		for (int x = minX + 1; x <= maxX; x++) {
			minY = getMinYAtWX(minW, x);
			maxY = getMaxYAtWX(minW, x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getMinZ(minW, x, y);
				maxZ = getMaxZ(minW, x, y);
				for (int z = minZ; z <= maxZ; z++) {
					total = total.add(getFromPosition(minW, x, y, z));
				}
			}
		}
		for (int w = minW + 1; w <= maxW; w++) {
			minX = getMinXAtW(w);
			maxX = getMaxXAtW(w);
			for (int x = minX; x <= maxX; x++) {
				minY = getMinYAtWX(w, x);
				maxY = getMaxYAtWX(w, x);
				for (int y = minY; y <= maxY; y++) {
					minZ = getMinZ(w, x, y);
					maxZ = getMaxZ(w, x, y);
					for (int z = minZ; z <= maxZ; z++) {
						total = total.add(getFromPosition(w, x, y, z));
					}
				}
			}
		}
		return total;
	}
	
	@Override
	default NumericModel4D<T> subsection(int minW, int maxW, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new NumericSubModel4D<T, NumericModel4D<T>>(this, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default NumericModel3D<T> crossSectionAtW(int w) {
		return new NumericModel4DWCrossSection<T, NumericModel4D<T>>(this, w);
	}
	
	@Override
	default NumericModel3D<T> crossSectionAtX(int x) {
		return new NumericModel4DXCrossSection<T, NumericModel4D<T>>(this, x);
	}
	
	@Override
	default NumericModel3D<T> crossSectionAtY(int y) {
		return new NumericModel4DYCrossSection<T, NumericModel4D<T>>(this, y);
	}
	
	@Override
	default NumericModel3D<T> crossSectionAtZ(int z) {
		return new NumericModel4DZCrossSection<T, NumericModel4D<T>>(this, z);
	}
}
