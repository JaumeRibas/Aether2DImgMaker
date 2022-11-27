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

import org.apache.commons.math3.FieldElement;

import cellularautomata.MinAndMax;
import cellularautomata.PartialCoordinates;
import cellularautomata.model.NumericModel;
import cellularautomata.model3d.NumericModel3D;

public interface NumericModel4D<Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> extends ObjectModel4D<Number_Type>, NumericModel<Number_Type> {

	@Override
	default MinAndMax<Number_Type> getMinAndMax() throws Exception {
		int maxW = getMaxW(), minW = getMinW(), maxX, maxY, maxZ, minZ;
		int minX = getMinXAtW(minW);
		int minY = getMinYAtWX(minW, minX);
		Number_Type minValue = getFromPosition(minW, minX, minY, getMinZ(minW, minX, minY));
		Number_Type maxValue = minValue;
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
						Number_Type value = getFromPosition(w, x, y, z);
						if (value.compareTo(maxValue) > 0)
							maxValue = value;
						if (value.compareTo(minValue) < 0)
							minValue = value;
					}
				}
			}
		}
		return new MinAndMax<Number_Type>(minValue, maxValue);
	}
	
	@Override
	default MinAndMax<Number_Type> getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		int maxW = getMaxW(), minW = getMinW(), maxX, minX, maxY, minY, maxZ, minZ;
		Number_Type minValue = null, maxValue = null;
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
						Number_Type value = getFromPosition(w, x, y, minZ);
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
						Number_Type value = getFromPosition(w, x, y, z);
						if (value.compareTo(maxValue) > 0)
							maxValue = value;
						if (value.compareTo(minValue) < 0)
							minValue = value;
					}
				}
			}
		}
		return minValue == null? null : new MinAndMax<Number_Type>(minValue, maxValue);
	}
	
	@Override
	default Number_Type getTotal() throws Exception {
		int maxW = getMaxW(), minW = getMinW();
		int minX = getMinXAtW(minW);
		int minY = getMinYAtWX(minW, minX);
		int minZ = getMinZ(minW, minX, minY);
		Number_Type total = getFromPosition(minW, minX, minY, minZ);
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
	default NumericModel4D<Number_Type> subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return (NumericModel4D<Number_Type>) ObjectModel4D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	default NumericModel4D<Number_Type> subsection(Integer minW, Integer maxW, Integer minX, Integer maxX, Integer minY, Integer maxY, Integer minZ, Integer maxZ) {
		return new NumericSubModel4D<Number_Type>(this, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default NumericModel3D<Number_Type> crossSection(int axis, int coordinate) {
		return (NumericModel3D<Number_Type>) ObjectModel4D.super.crossSection(axis, coordinate);
	}
	
	@Override
	default NumericModel3D<Number_Type> crossSectionAtW(int w) {
		return new NumericModel4DWCrossSection<Number_Type>(this, w);
	}
	
	@Override
	default NumericModel3D<Number_Type> crossSectionAtX(int x) {
		return new NumericModel4DXCrossSection<Number_Type>(this, x);
	}
	
	@Override
	default NumericModel3D<Number_Type> crossSectionAtY(int y) {
		return new NumericModel4DYCrossSection<Number_Type>(this, y);
	}
	
	@Override
	default NumericModel3D<Number_Type> crossSectionAtZ(int z) {
		return new NumericModel4DZCrossSection<Number_Type>(this, z);
	}

	@Override
	default NumericModel3D<Number_Type> diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return (NumericModel3D<Number_Type>) ObjectModel4D.super.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}
	
	@Override
	default NumericModel3D<Number_Type> diagonalCrossSectionOnWX(boolean positiveSlope, int xOffsetFromW) {
		return new NumericModel4DWXDiagonalCrossSection<Number_Type>(this, positiveSlope, xOffsetFromW);
	}
	
	@Override
	default NumericModel3D<Number_Type> diagonalCrossSectionOnWY(boolean positiveSlope, int yOffsetFromW) {
		return new NumericModel4DWYDiagonalCrossSection<Number_Type>(this, positiveSlope, yOffsetFromW);
	}
	
	@Override
	default NumericModel3D<Number_Type> diagonalCrossSectionOnWZ(boolean positiveSlope, int zOffsetFromW) {
		return new NumericModel4DWZDiagonalCrossSection<Number_Type>(this, positiveSlope, zOffsetFromW);
	}
	
	@Override
	default NumericModel3D<Number_Type> diagonalCrossSectionOnXY(boolean positiveSlope, int yOffsetFromX) {
		return new NumericModel4DXYDiagonalCrossSection<Number_Type>(this, positiveSlope, yOffsetFromX);
	}
	
	@Override
	default NumericModel3D<Number_Type> diagonalCrossSectionOnXZ(boolean positiveSlope, int zOffsetFromX) {
		return new NumericModel4DXZDiagonalCrossSection<Number_Type>(this, positiveSlope, zOffsetFromX);
	}
	
	@Override
	default NumericModel3D<Number_Type> diagonalCrossSectionOnYZ(boolean positiveSlope, int zOffsetFromY) {
		return new NumericModel4DYZDiagonalCrossSection<Number_Type>(this, positiveSlope, zOffsetFromY);
	}
}
