/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2025 Jaume Ribas

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

import org.apache.commons.math3.FieldElement;

import cellularautomata.MinAndMax;
import cellularautomata.PartialCoordinates;
import cellularautomata.model.NumericModel;
import cellularautomata.model4d.NumericModel4D;

public interface NumericModel5D<Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> extends ObjectModel5D<Number_Type>, NumericModel<Number_Type> {

	@Override
	default MinAndMax<Number_Type> getMinAndMax() throws Exception {
		int maxV = getMaxV(), minV = getMinV(), maxW, minW, maxX, minX, maxY, minY, maxZ, minZ;
		minW = getMinWAtV(minV);
		minX = getMinXAtVW(minV, minW);
		minY = getMinYAtVWX(minV, minW, minX);
		Number_Type minValue = getFromPosition(minV, minW, minX, minY, getMinZ(minV, minW, minX, minY));
		Number_Type maxValue = minValue;
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
							Number_Type value = getFromPosition(v, w, x, y, z);
							if (value.compareTo(minValue) < 0) {
								minValue = value;
							} else if (value.compareTo(maxValue) > 0) {
								maxValue = value;
							}
						}
					}
				}
			}
		}
		return new MinAndMax<Number_Type>(minValue, maxValue);
	}
	
	@Override
	default MinAndMax<Number_Type> getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		int maxV = getMaxV(), minV = getMinV(), maxW, minW, maxX, minX, maxY, minY, maxZ, minZ;
		Number_Type minValue = null, maxValue = null;
		int v = minV;
		for (; v <= maxV && maxValue == null; v++) {
			minW = getMinWAtV(v);
			maxW = getMaxWAtV(v);
			int w = minW;
			for (; w <= maxW && maxValue == null; w++) {
				minX = getMinXAtVW(v, w);
				maxX = getMaxXAtVW(v, w);
				int x = minX;
				for (; x <= maxX && maxValue == null; x++) {
					minY = getMinYAtVWX(v, w, x);
					maxY = getMaxYAtVWX(v, w, x);
					int y = minY;
					for (; y <= maxY && maxValue == null; y++) {
						minZ = getMinZ(v, w, x, y);
						maxZ = getMaxZ(v, w, x, y);
						boolean isPositionEven = (minZ+v+w+x+y)%2 == 0;
						if (isPositionEven != isEven) {
							minZ++;
						}
						if (minZ <= maxZ) {
							Number_Type value = getFromPosition(v, w, x, y, minZ);
							maxValue = value;
							minValue = value;
							for (int z = minZ + 2; z <= maxZ; z += 2) {
								value = getFromPosition(v, w, x, y, z);
								if (value.compareTo(minValue) < 0) {
									minValue = value;
								} else if (value.compareTo(maxValue) > 0) {
									maxValue = value;
								}
							}
						}
					}
					for (; y <= maxY; y++) {
						minZ = getMinZ(v, w, x, y);
						maxZ = getMaxZ(v, w, x, y);
						boolean isPositionEven = (minZ+v+w+x+y)%2 == 0;
						if (isPositionEven != isEven) {
							minZ++;
						}
						for (int z = minZ; z <= maxZ; z += 2) {
							Number_Type value = getFromPosition(v, w, x, y, z);
							if (value.compareTo(minValue) < 0) {
								minValue = value;
							} else if (value.compareTo(maxValue) > 0) {
								maxValue = value;
							}
						}
					}
				}
				for (; x <= maxX; x++) {
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
							Number_Type value = getFromPosition(v, w, x, y, z);
							if (value.compareTo(minValue) < 0) {
								minValue = value;
							} else if (value.compareTo(maxValue) > 0) {
								maxValue = value;
							}
						}
					}
				}
			}
			for (; w <= maxW; w++) {
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
							Number_Type value = getFromPosition(v, w, x, y, z);
							if (value.compareTo(minValue) < 0) {
								minValue = value;
							} else if (value.compareTo(maxValue) > 0) {
								maxValue = value;
							}
						}
					}
				}
			}
		}
		for (; v <= maxV; v++) {
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
							Number_Type value = getFromPosition(v, w, x, y, z);
							if (value.compareTo(minValue) < 0) {
								minValue = value;
							} else if (value.compareTo(maxValue) > 0) {
								maxValue = value;
							}
						}
					}
				}
			}
		}
		return minValue == null? null : new MinAndMax<Number_Type>(minValue, maxValue);
	}
	
	@Override
	default Number_Type getTotal() throws Exception {
		int maxV = getMaxV(), minV = getMinV();
		int minW = getMinWAtV(minV);
		int minX = getMinXAtVW(minV, minW);
		int minY = getMinYAtVWX(minV, minW, minX);
		int minZ = getMinZ(minV, minW, minX, minY);
		Number_Type total = getFromPosition(minV, minW, minX, minY, minZ);
		int maxZ = getMaxZ(minV, minW, minX, minY);
		for (int z = minZ + 1; z <= maxZ; z++) {
			total = total.add(getFromPosition(minV, minW, minX, minY, z));
		}
		int maxY = getMaxYAtVWX(minV, minW, minX);
		for (int y = minY + 1; y <= maxY; y++) {
			minZ = getMinZ(minV, minW, minX, y);
			maxZ = getMaxZ(minV, minW, minX, y);
			for (int z = minZ; z <= maxZ; z++) {
				total = total.add(getFromPosition(minV, minW, minX, y, z));
			}
		}
		int maxX = getMaxXAtVW(minV, minW);
		for (int x = minX + 1; x <= maxX; x++) {
			minY = getMinYAtVWX(minV, minW, x);
			maxY = getMaxYAtVWX(minV, minW, x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getMinZ(minV, minW, x, y);
				maxZ = getMaxZ(minV, minW, x, y);
				for (int z = minZ; z <= maxZ; z++) {
					total = total.add(getFromPosition(minV, minW, x, y, z));
				}
			}
		}
		int maxW = getMaxWAtV(minV);
		for (int w = minW + 1; w <= maxW; w++) {
			minX = getMinXAtVW(minV, w);
			maxX = getMaxXAtVW(minV, w);
			for (int x = minX; x <= maxX; x++) {
				minY = getMinYAtVWX(minV, w, x);
				maxY = getMaxYAtVWX(minV, w, x);
				for (int y = minY; y <= maxY; y++) {
					minZ = getMinZ(minV, w, x, y);
					maxZ = getMaxZ(minV, w, x, y);
					for (int z = minZ; z <= maxZ; z++) {
						total = total.add(getFromPosition(minV, w, x, y, z));
					}
				}
			}
		}
		for (int v = minV + 1; v <= maxV; v++) {
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
							total = total.add(getFromPosition(v, w, x, y, z));
						}
					}
				}
			}
		}
		return total;
	}
	
	@Override
	default NumericModel5D<Number_Type> subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return (NumericModel5D<Number_Type>) ObjectModel5D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	default NumericModel5D<Number_Type> subsection(Integer minV, Integer maxV, Integer minW, Integer maxW, Integer minX, Integer maxX, Integer minY, Integer maxY, Integer minZ, Integer maxZ) {
		return new NumericSubModel5D<Number_Type>(this, minV, maxV, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default NumericModel4D<Number_Type> crossSection(int axis, int coordinate) {
		return (NumericModel4D<Number_Type>) ObjectModel5D.super.crossSection(axis, coordinate);
	}

	@Override
	default NumericModel4D<Number_Type> crossSectionAtV(int v) {
		return new NumericModel5DVCrossSection<Number_Type>(this, v);
	}

	@Override
	default NumericModel4D<Number_Type> crossSectionAtW(int w) {
		return new NumericModel5DWCrossSection<Number_Type>(this, w);
	}

	@Override
	default NumericModel4D<Number_Type> crossSectionAtX(int x) {
		return new NumericModel5DXCrossSection<Number_Type>(this, x);
	}

	@Override
	default NumericModel4D<Number_Type> crossSectionAtY(int y) {
		return new NumericModel5DYCrossSection<Number_Type>(this, y);
	}

	@Override
	default NumericModel4D<Number_Type> crossSectionAtZ(int z) {
		return new NumericModel5DZCrossSection<Number_Type>(this, z);
	}
	
	@Override
	default NumericModel4D<Number_Type> diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return (NumericModel4D<Number_Type>) ObjectModel5D.super.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}
	
	@Override
	default NumericModel4D<Number_Type> diagonalCrossSectionOnVW(boolean positiveSlope, int wOffsetFromV) {
		return new NumericModel5DVWDiagonalCrossSection<Number_Type>(this, positiveSlope, wOffsetFromV);
	}
	
	@Override
	default NumericModel4D<Number_Type> diagonalCrossSectionOnVX(boolean positiveSlope, int xOffsetFromV) {
		return new NumericModel5DVXDiagonalCrossSection<Number_Type>(this, positiveSlope, xOffsetFromV);
	}
	
	@Override
	default NumericModel4D<Number_Type> diagonalCrossSectionOnVY(boolean positiveSlope, int yOffsetFromV) {
		return new NumericModel5DVYDiagonalCrossSection<Number_Type>(this, positiveSlope, yOffsetFromV);
	}
	
	@Override
	default NumericModel4D<Number_Type> diagonalCrossSectionOnVZ(boolean positiveSlope, int zOffsetFromV) {
		return new NumericModel5DVZDiagonalCrossSection<Number_Type>(this, positiveSlope, zOffsetFromV);
	}
	
	@Override
	default NumericModel4D<Number_Type> diagonalCrossSectionOnWX(boolean positiveSlope, int xOffsetFromW) {
		return new NumericModel5DWXDiagonalCrossSection<Number_Type>(this, positiveSlope, xOffsetFromW);
	}
	
	@Override
	default NumericModel4D<Number_Type> diagonalCrossSectionOnWY(boolean positiveSlope, int yOffsetFromW) {
		return new NumericModel5DWYDiagonalCrossSection<Number_Type>(this, positiveSlope, yOffsetFromW);
	}
	
	@Override
	default NumericModel4D<Number_Type> diagonalCrossSectionOnWZ(boolean positiveSlope, int zOffsetFromW) {
		return new NumericModel5DWZDiagonalCrossSection<Number_Type>(this, positiveSlope, zOffsetFromW);
	}
	
	@Override
	default NumericModel4D<Number_Type> diagonalCrossSectionOnXY(boolean positiveSlope, int yOffsetFromX) {
		return new NumericModel5DXYDiagonalCrossSection<Number_Type>(this, positiveSlope, yOffsetFromX);
	}
	
	@Override
	default NumericModel4D<Number_Type> diagonalCrossSectionOnXZ(boolean positiveSlope, int zOffsetFromX) {
		return new NumericModel5DXZDiagonalCrossSection<Number_Type>(this, positiveSlope, zOffsetFromX);
	}
	
	@Override
	default NumericModel4D<Number_Type> diagonalCrossSectionOnYZ(boolean positiveSlope, int zOffsetFromY) {
		return new NumericModel5DYZDiagonalCrossSection<Number_Type>(this, positiveSlope, zOffsetFromY);
	}

}
