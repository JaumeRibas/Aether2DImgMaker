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

import org.apache.commons.math3.FieldElement;

import cellularautomata.MinAndMax;
import cellularautomata.PartialCoordinates;
import cellularautomata.model.NumericModel;
import cellularautomata.model2d.NumericModel2D;

public interface NumericModel3D<Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> extends ObjectModel3D<Number_Type>, NumericModel<Number_Type> {

	@Override
	default MinAndMax<Number_Type> getMinAndMax() throws Exception {
		int maxX = getMaxX(), minX = getMinX(), maxY, minY, maxZ, minZ;
		minY = getMinYAtX(minX);
		Number_Type maxValue = getFromPosition(minX, minY, getMinZ(minX, minY));
		Number_Type minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinYAtX(x);
			maxY = getMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getMinZ(x, y);
				maxZ = getMaxZ(x, y);
				for (int z = minZ; z <= maxZ; z++) {
					Number_Type value = getFromPosition(x, y, z);
					if (value.compareTo(maxValue) > 0)
						maxValue = value;
					if (value.compareTo(minValue) < 0)
						minValue = value;
				}
			}
		}
		return new MinAndMax<Number_Type>(minValue, maxValue);
	}
	
	@Override
	default MinAndMax<Number_Type> getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		int maxX = getMaxX(), minX = getMinX();
		Number_Type maxValue = null;
		Number_Type minValue = null;
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
					Number_Type value = getFromPosition(x, y, minZ);
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
					Number_Type value = getFromPosition(x, y, z);
					if (value.compareTo(maxValue) > 0)
						maxValue = value;
					if (value.compareTo(minValue) < 0)
						minValue = value;
				}
			}
		}
		return minValue == null? null : new MinAndMax<Number_Type>(minValue, maxValue);
	}
	
	@Override
	default Number_Type getTotal() throws Exception {
		int maxX = getMaxX(), minX = getMinX();
		int minY = getMinYAtX(minX);
		int minZ = getMinZ(minX, minY);		
		Number_Type total = getFromPosition(minX, minY, minZ);
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
	default NumericModel3D<Number_Type> subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return (NumericModel3D<Number_Type>) ObjectModel3D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	default NumericModel3D<Number_Type> subsection(Integer minX, Integer maxX, Integer minY, Integer maxY, Integer minZ, Integer maxZ) {
		return new NumericSubModel3D<Number_Type>(this, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default NumericModel2D<Number_Type> crossSection(int axis, int coordinate) {
		return (NumericModel2D<Number_Type>) ObjectModel3D.super.crossSection(axis, coordinate);
	}
	
	@Override
	default NumericModel2D<Number_Type> crossSectionAtX(int x) {
		return new NumericModel3DXCrossSection<Number_Type>(this, x);
	}
	
	@Override
	default NumericModel2D<Number_Type> crossSectionAtY(int y) {
		return new NumericModel3DYCrossSection<Number_Type>(this, y);
	}
	
	@Override
	default NumericModel2D<Number_Type> crossSectionAtZ(int z) {
		return new NumericModel3DZCrossSection<Number_Type>(this, z);
	}

	@Override
	default NumericModel2D<Number_Type> diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return (NumericModel2D<Number_Type>) ObjectModel3D.super.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}
	
	@Override
	default NumericModel2D<Number_Type> diagonalCrossSectionOnXY(boolean positiveSlope, int yOffsetFromX) {
		return new NumericModel3DXYDiagonalCrossSection<Number_Type>(this, positiveSlope, yOffsetFromX);
	}
	
	@Override
	default NumericModel2D<Number_Type> diagonalCrossSectionOnXZ(boolean positiveSlope, int zOffsetFromX) {
		return new NumericModel3DXZDiagonalCrossSection<Number_Type>(this, positiveSlope, zOffsetFromX);
	}
	
	@Override
	default NumericModel2D<Number_Type> diagonalCrossSectionOnYZ(boolean positiveSlope, int zOffsetFromY) {
		return new NumericModel3DYZDiagonalCrossSection<Number_Type>(this, positiveSlope, zOffsetFromY);
	}

}
