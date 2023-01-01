/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2023 Jaume Ribas

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

import cellularautomata.model.Model;
import cellularautomata.PartialCoordinates;
import cellularautomata.model4d.Model4D;

public interface Model5D extends Model {
	
	@Override
	default int getGridDimension() {
		return 5;
	}
	
	default String getVLabel() {
		return "v";
	}
	
	default String getWLabel() {
		return "w";
	}
	
	default String getXLabel() {
		return "x";
	}
	
	default String getYLabel() {
		return "y";
	}
	
	default String getZLabel() {
		return "z";
	}
	
	@Override
	default String getAxisLabel(int axis) {
		switch (axis) {
		case 0: 
			return getVLabel();
		case 1: 
			return getWLabel();
		case 2: 
			return getXLabel();
		case 3: 
			return getYLabel();
		case 4: 
			return getZLabel();
		default:
			throw new IllegalArgumentException("The axis must be 0, 1, 2, 3 or 4. Got " + axis + ".");
		}
	}
	
	int getMinV();

	int getMaxV();

	default int getMinVAtW(int w) { return getMinV(); }

	default int getMaxVAtW(int w) { return getMaxV(); }

	default int getMinVAtX(int x) { return getMinV(); }

	default int getMaxVAtX(int x) { return getMaxV(); }

	default int getMinVAtY(int y) { return getMinV(); }

	default int getMaxVAtY(int y) { return getMaxV(); }

	default int getMinVAtZ(int z) { return getMinV(); }

	default int getMaxVAtZ(int z) { return getMaxV(); }

	default int getMinVAtWX(int w, int x) { return getMinV(); }

	default int getMaxVAtWX(int w, int x) { return getMaxV(); }

	default int getMinVAtWY(int w, int y) { return getMinV(); }

	default int getMaxVAtWY(int w, int y) { return getMaxV(); }

	default int getMinVAtWZ(int w, int z) { return getMinV(); }

	default int getMaxVAtWZ(int w, int z) { return getMaxV(); }

	default int getMinVAtXY(int x, int y) { return getMinV(); }

	default int getMaxVAtXY(int x, int y) { return getMaxV(); }

	default int getMinVAtXZ(int x, int z) { return getMinV(); }

	default int getMaxVAtXZ(int x, int z) { return getMaxV(); }

	default int getMinVAtYZ(int y, int z) { return getMinV(); }

	default int getMaxVAtYZ(int y, int z) { return getMaxV(); }

	default int getMinVAtWXY(int w, int x, int y) { return getMinV(); }

	default int getMaxVAtWXY(int w, int x, int y) { return getMaxV(); }

	default int getMinVAtWXZ(int w, int x, int z) { return getMinV(); }

	default int getMaxVAtWXZ(int w, int x, int z) { return getMaxV(); }

	default int getMinVAtWYZ(int w, int y, int z) { return getMinV(); }

	default int getMaxVAtWYZ(int w, int y, int z) { return getMaxV(); }

	default int getMinVAtXYZ(int x, int y, int z) { return getMinV(); }

	default int getMaxVAtXYZ(int x, int y, int z) { return getMaxV(); }

	default int getMinV(int w, int x, int y, int z) { return getMinV(); }

	default int getMaxV(int w, int x, int y, int z) { return getMaxV(); }

	int getMinW();

	int getMaxW();

	default int getMinWAtV(int v) { return getMinW(); }

	default int getMaxWAtV(int v) { return getMaxW(); }

	default int getMinWAtX(int x) { return getMinW(); }

	default int getMaxWAtX(int x) { return getMaxW(); }

	default int getMinWAtY(int y) { return getMinW(); }

	default int getMaxWAtY(int y) { return getMaxW(); }

	default int getMinWAtZ(int z) { return getMinW(); }

	default int getMaxWAtZ(int z) { return getMaxW(); }

	default int getMinWAtVX(int v, int x) { return getMinW(); }

	default int getMaxWAtVX(int v, int x) { return getMaxW(); }

	default int getMinWAtVY(int v, int y) { return getMinW(); }

	default int getMaxWAtVY(int v, int y) { return getMaxW(); }

	default int getMinWAtVZ(int v, int z) { return getMinW(); }

	default int getMaxWAtVZ(int v, int z) { return getMaxW(); }

	default int getMinWAtXY(int x, int y) { return getMinW(); }

	default int getMaxWAtXY(int x, int y) { return getMaxW(); }

	default int getMinWAtXZ(int x, int z) { return getMinW(); }

	default int getMaxWAtXZ(int x, int z) { return getMaxW(); }

	default int getMinWAtYZ(int y, int z) { return getMinW(); }

	default int getMaxWAtYZ(int y, int z) { return getMaxW(); }

	default int getMinWAtVXY(int v, int x, int y) { return getMinW(); }

	default int getMaxWAtVXY(int v, int x, int y) { return getMaxW(); }

	default int getMinWAtVXZ(int v, int x, int z) { return getMinW(); }

	default int getMaxWAtVXZ(int v, int x, int z) { return getMaxW(); }

	default int getMinWAtVYZ(int v, int y, int z) { return getMinW(); }

	default int getMaxWAtVYZ(int v, int y, int z) { return getMaxW(); }

	default int getMinWAtXYZ(int x, int y, int z) { return getMinW(); }

	default int getMaxWAtXYZ(int x, int y, int z) { return getMaxW(); }

	default int getMinW(int v, int x, int y, int z) { return getMinW(); }

	default int getMaxW(int v, int x, int y, int z) { return getMaxW(); }

	int getMinX();

	int getMaxX();

	default int getMinXAtV(int v) { return getMinX(); }

	default int getMaxXAtV(int v) { return getMaxX(); }

	default int getMinXAtW(int w) { return getMinX(); }

	default int getMaxXAtW(int w) { return getMaxX(); }

	default int getMinXAtY(int y) { return getMinX(); }

	default int getMaxXAtY(int y) { return getMaxX(); }

	default int getMinXAtZ(int z) { return getMinX(); }

	default int getMaxXAtZ(int z) { return getMaxX(); }

	default int getMinXAtVW(int v, int w) { return getMinX(); }

	default int getMaxXAtVW(int v, int w) { return getMaxX(); }

	default int getMinXAtVY(int v, int y) { return getMinX(); }

	default int getMaxXAtVY(int v, int y) { return getMaxX(); }

	default int getMinXAtVZ(int v, int z) { return getMinX(); }

	default int getMaxXAtVZ(int v, int z) { return getMaxX(); }

	default int getMinXAtWY(int w, int y) { return getMinX(); }

	default int getMaxXAtWY(int w, int y) { return getMaxX(); }

	default int getMinXAtWZ(int w, int z) { return getMinX(); }

	default int getMaxXAtWZ(int w, int z) { return getMaxX(); }

	default int getMinXAtYZ(int y, int z) { return getMinX(); }

	default int getMaxXAtYZ(int y, int z) { return getMaxX(); }

	default int getMinXAtVWY(int v, int w, int y) { return getMinX(); }

	default int getMaxXAtVWY(int v, int w, int y) { return getMaxX(); }

	default int getMinXAtVWZ(int v, int w, int z) { return getMinX(); }

	default int getMaxXAtVWZ(int v, int w, int z) { return getMaxX(); }

	default int getMinXAtVYZ(int v, int y, int z) { return getMinX(); }

	default int getMaxXAtVYZ(int v, int y, int z) { return getMaxX(); }

	default int getMinXAtWYZ(int w, int y, int z) { return getMinX(); }

	default int getMaxXAtWYZ(int w, int y, int z) { return getMaxX(); }

	default int getMinX(int v, int w, int y, int z) { return getMinX(); }

	default int getMaxX(int v, int w, int y, int z) { return getMaxX(); }

	int getMinY();

	int getMaxY();

	default int getMinYAtV(int v) { return getMinY(); }

	default int getMaxYAtV(int v) { return getMaxY(); }

	default int getMinYAtW(int w) { return getMinY(); }

	default int getMaxYAtW(int w) { return getMaxY(); }

	default int getMinYAtX(int x) { return getMinY(); }

	default int getMaxYAtX(int x) { return getMaxY(); }

	default int getMinYAtZ(int z) { return getMinY(); }

	default int getMaxYAtZ(int z) { return getMaxY(); }

	default int getMinYAtVW(int v, int w) { return getMinY(); }

	default int getMaxYAtVW(int v, int w) { return getMaxY(); }

	default int getMinYAtVX(int v, int x) { return getMinY(); }

	default int getMaxYAtVX(int v, int x) { return getMaxY(); }

	default int getMinYAtVZ(int v, int z) { return getMinY(); }

	default int getMaxYAtVZ(int v, int z) { return getMaxY(); }

	default int getMinYAtWX(int w, int x) { return getMinY(); }

	default int getMaxYAtWX(int w, int x) { return getMaxY(); }

	default int getMinYAtWZ(int w, int z) { return getMinY(); }

	default int getMaxYAtWZ(int w, int z) { return getMaxY(); }

	default int getMinYAtXZ(int x, int z) { return getMinY(); }

	default int getMaxYAtXZ(int x, int z) { return getMaxY(); }

	default int getMinYAtVWX(int v, int w, int x) { return getMinY(); }

	default int getMaxYAtVWX(int v, int w, int x) { return getMaxY(); }

	default int getMinYAtVWZ(int v, int w, int z) { return getMinY(); }

	default int getMaxYAtVWZ(int v, int w, int z) { return getMaxY(); }

	default int getMinYAtVXZ(int v, int x, int z) { return getMinY(); }

	default int getMaxYAtVXZ(int v, int x, int z) { return getMaxY(); }

	default int getMinYAtWXZ(int w, int x, int z) { return getMinY(); }

	default int getMaxYAtWXZ(int w, int x, int z) { return getMaxY(); }

	default int getMinY(int v, int w, int x, int z) { return getMinY(); }

	default int getMaxY(int v, int w, int x, int z) { return getMaxY(); }

	int getMinZ();

	int getMaxZ();

	default int getMinZAtV(int v) { return getMinZ(); }

	default int getMaxZAtV(int v) { return getMaxZ(); }

	default int getMinZAtW(int w) { return getMinZ(); }

	default int getMaxZAtW(int w) { return getMaxZ(); }

	default int getMinZAtX(int x) { return getMinZ(); }

	default int getMaxZAtX(int x) { return getMaxZ(); }

	default int getMinZAtY(int y) { return getMinZ(); }

	default int getMaxZAtY(int y) { return getMaxZ(); }

	default int getMinZAtVW(int v, int w) { return getMinZ(); }

	default int getMaxZAtVW(int v, int w) { return getMaxZ(); }

	default int getMinZAtVX(int v, int x) { return getMinZ(); }

	default int getMaxZAtVX(int v, int x) { return getMaxZ(); }

	default int getMinZAtVY(int v, int y) { return getMinZ(); }

	default int getMaxZAtVY(int v, int y) { return getMaxZ(); }

	default int getMinZAtWX(int w, int x) { return getMinZ(); }

	default int getMaxZAtWX(int w, int x) { return getMaxZ(); }

	default int getMinZAtWY(int w, int y) { return getMinZ(); }

	default int getMaxZAtWY(int w, int y) { return getMaxZ(); }

	default int getMinZAtXY(int x, int y) { return getMinZ(); }

	default int getMaxZAtXY(int x, int y) { return getMaxZ(); }

	default int getMinZAtVWX(int v, int w, int x) { return getMinZ(); }

	default int getMaxZAtVWX(int v, int w, int x) { return getMaxZ(); }

	default int getMinZAtVWY(int v, int w, int y) { return getMinZ(); }

	default int getMaxZAtVWY(int v, int w, int y) { return getMaxZ(); }

	default int getMinZAtVXY(int v, int x, int y) { return getMinZ(); }

	default int getMaxZAtVXY(int v, int x, int y) { return getMaxZ(); }

	default int getMinZAtWXY(int w, int x, int y) { return getMinZ(); }

	default int getMaxZAtWXY(int w, int x, int y) { return getMaxZ(); }

	default int getMinZ(int v, int w, int x, int y) { return getMinZ(); }

	default int getMaxZ(int v, int w, int x, int y) { return getMaxZ(); }
	
	@Override
	default Model5D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return subsection(
				minCoordinates.get(0), maxCoordinates.get(0),
				minCoordinates.get(1), maxCoordinates.get(1),
				minCoordinates.get(2), maxCoordinates.get(2),
				minCoordinates.get(3), maxCoordinates.get(3),
				minCoordinates.get(4), maxCoordinates.get(4));
	}

	default Model5D subsection(Integer minV, Integer maxV, Integer minW, Integer maxW, Integer minX, Integer maxX, Integer minY, Integer maxY, Integer minZ, Integer maxZ) {
		return new SubModel5D<Model5D>(this, minV, maxV, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default Model4D crossSection(int axis, int coordinate) {
		switch (axis) {
		case 0:
			return crossSectionAtV(coordinate);
		case 1:
			return crossSectionAtW(coordinate);
		case 2:
			return crossSectionAtX(coordinate);
		case 3:
			return crossSectionAtY(coordinate);
		case 4:
			return crossSectionAtZ(coordinate);
		default:
			throw new IllegalArgumentException("The axis must be 0, 1, 2, 3 or 4. Got " + axis + ".");
		}
	}

	default Model4D crossSectionAtV(int v) {
		return new Model5DVCrossSection<Model5D>(this, v);
	}
	
	default Model4D crossSectionAtW(int w) {
		return new Model5DWCrossSection<Model5D>(this, w);
	}
	
	default Model4D crossSectionAtX(int x) {
		return new Model5DXCrossSection<Model5D>(this, x);
	}
	
	default Model4D crossSectionAtY(int y) {
		return new Model5DYCrossSection<Model5D>(this, y);
	}
	
	default Model4D crossSectionAtZ(int z) {
		return new Model5DZCrossSection<Model5D>(this, z);
	}
	
	@Override
	default Model4D diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		switch (firstAxis) {
		case 0: 
			switch (secondAxis) {
			case 0:
				throw new IllegalArgumentException("The axes cannot be equal.");		
			case 1: 
				return diagonalCrossSectionOnVW(positiveSlope, offset);				
			case 2: 
				return diagonalCrossSectionOnVX(positiveSlope, offset);
			case 3: 
				return diagonalCrossSectionOnVY(positiveSlope, offset);
			case 4: 
				return diagonalCrossSectionOnVZ(positiveSlope, offset);
			default:
				throw new IllegalArgumentException("The axes must be greater than -1 and smaller than 5. Got " + secondAxis + ".");
			}
		case 1: 
			switch (secondAxis) {			
			case 0: 
				return diagonalCrossSectionOnVW(positiveSlope, positiveSlope ? -offset : offset);
			case 1:
				throw new IllegalArgumentException("The axes cannot be equal.");				
			case 2: 
				return diagonalCrossSectionOnWX(positiveSlope, offset);
			case 3: 
				return diagonalCrossSectionOnWY(positiveSlope, offset);
			case 4: 
				return diagonalCrossSectionOnWZ(positiveSlope, offset);
			default:
				throw new IllegalArgumentException("The axes must be greater than -1 and smaller than 5. Got " + secondAxis + ".");
			}
		case 2: 
			switch (secondAxis) {
			case 0: 
				return diagonalCrossSectionOnVX(positiveSlope, positiveSlope ? -offset : offset);
			case 1: 
				return diagonalCrossSectionOnWX(positiveSlope, positiveSlope ? -offset : offset);				
			case 2:
				throw new IllegalArgumentException("The axes cannot be equal.");
			case 3: 
				return diagonalCrossSectionOnXY(positiveSlope, offset);
			case 4: 
				return diagonalCrossSectionOnXZ(positiveSlope, offset);
			default:
				throw new IllegalArgumentException("The axes must be greater than -1 and smaller than 5. Got " + secondAxis + ".");
			}			
		case 3: 
			switch (secondAxis) {
			case 0: 
				return diagonalCrossSectionOnVY(positiveSlope, positiveSlope ? -offset : offset);		
			case 1: 
				return diagonalCrossSectionOnWY(positiveSlope, positiveSlope ? -offset : offset);				
			case 2: 
				return diagonalCrossSectionOnXY(positiveSlope, positiveSlope ? -offset : offset);
			case 3:
				throw new IllegalArgumentException("The axes cannot be equal.");
			case 4: 
				return diagonalCrossSectionOnYZ(positiveSlope, offset);
			default:
				throw new IllegalArgumentException("The axes must be greater than -1 and smaller than 5. Got " + secondAxis + ".");
			}			
		case 4: 
			switch (secondAxis) {
			case 0: 
				return diagonalCrossSectionOnVZ(positiveSlope, positiveSlope ? -offset : offset);	
			case 1: 
				return diagonalCrossSectionOnWZ(positiveSlope, positiveSlope ? -offset : offset);				
			case 2: 
				return diagonalCrossSectionOnXZ(positiveSlope, positiveSlope ? -offset : offset);
			case 3:
				return diagonalCrossSectionOnYZ(positiveSlope, positiveSlope ? -offset : offset);
			case 4: 
				throw new IllegalArgumentException("The axes cannot be equal.");
			default:
				throw new IllegalArgumentException("The axes must be greater than -1 and smaller than 5. Got " + secondAxis + ".");
			}
		default:
			throw new IllegalArgumentException("The axes must be greater than -1 and smaller than 5. Got " + firstAxis + ".");
		}
	}
	
	default Model4D diagonalCrossSectionOnVW(boolean positiveSlope, int wOffsetFromV) {
		return new Model5DVWDiagonalCrossSection<Model5D>(this, positiveSlope, wOffsetFromV);
	}
	
	default Model4D diagonalCrossSectionOnVX(boolean positiveSlope, int xOffsetFromV) {
		return new Model5DVXDiagonalCrossSection<Model5D>(this, positiveSlope, xOffsetFromV);
	}
	
	default Model4D diagonalCrossSectionOnVY(boolean positiveSlope, int yOffsetFromV) {
		return new Model5DVYDiagonalCrossSection<Model5D>(this, positiveSlope, yOffsetFromV);
	}
	
	default Model4D diagonalCrossSectionOnVZ(boolean positiveSlope, int zOffsetFromV) {
		return new Model5DVZDiagonalCrossSection<Model5D>(this, positiveSlope, zOffsetFromV);
	}
	
	default Model4D diagonalCrossSectionOnWX(boolean positiveSlope, int xOffsetFromW) {
		return new Model5DWXDiagonalCrossSection<Model5D>(this, positiveSlope, xOffsetFromW);
	}
	
	default Model4D diagonalCrossSectionOnWY(boolean positiveSlope, int yOffsetFromW) {
		return new Model5DWYDiagonalCrossSection<Model5D>(this, positiveSlope, yOffsetFromW);
	}
	
	default Model4D diagonalCrossSectionOnWZ(boolean positiveSlope, int zOffsetFromW) {
		return new Model5DWZDiagonalCrossSection<Model5D>(this, positiveSlope, zOffsetFromW);
	}
	
	default Model4D diagonalCrossSectionOnXY(boolean positiveSlope, int yOffsetFromX) {
		return new Model5DXYDiagonalCrossSection<Model5D>(this, positiveSlope, yOffsetFromX);
	}
	
	default Model4D diagonalCrossSectionOnXZ(boolean positiveSlope, int zOffsetFromX) {
		return new Model5DXZDiagonalCrossSection<Model5D>(this, positiveSlope, zOffsetFromX);
	}
	
	default Model4D diagonalCrossSectionOnYZ(boolean positiveSlope, int zOffsetFromY) {
		return new Model5DYZDiagonalCrossSection<Model5D>(this, positiveSlope, zOffsetFromY);
	}
	
	@Override
	default int getMaxCoordinate(int axis) {
		switch (axis) {
		case 0: 
			return getMaxV();
		case 1: 
			return getMaxW();
		case 2: 
			return getMaxX();
		case 3: 
			return getMaxY();
		case 4: 
			return getMaxZ();
		default:
			throw new IllegalArgumentException("The axis must be 0, 1, 2, 3 or 4. Got " + axis + ".");
		}
	}

	@Override
	default int getMaxCoordinate(int axis, PartialCoordinates coordinates) {
		Integer v, w, x, y, z;
		switch (axis) {
		case 0:
			w = coordinates.get(1);	x = coordinates.get(2);	y = coordinates.get(3); z = coordinates.get(4);
			if (w == null) {
				if (x == null) {
					if (y == null) {
						if (z == null) {
							return getMaxV();
						} else {
							return getMaxVAtZ(z);
						}
					} else if (z == null) {
						return getMaxVAtY(y);
					} else {
						return getMaxVAtYZ(y, z);
					}
				} else if (y == null) {
					if (z == null) {
						return getMaxVAtX(x);
					} else {
						return getMaxVAtXZ(x, z);
					}
				} else if (z == null) {
					return getMaxVAtXY(x, y);
				} else {
					return getMaxVAtXYZ(x, y, z);
				}
			} else if (x == null) {
				if (y == null) {
					if (z == null) {
						return getMaxVAtW(w);
					} else {
						return getMaxVAtWZ(w, z);
					}
				} else if (z == null) {
					return getMaxVAtWY(w, y);
				} else {
					return getMaxVAtWYZ(w, y, z);
				}
			} else if (y == null) {
				if (z == null) {
					return getMaxVAtWX(w, x);
				} else {
					return getMaxVAtWXZ(w, x, z);
				}
			} else if (z == null) {
				return getMaxVAtWXY(w, x, y);
			} else {
				return getMaxV(w, x, y, z);
			}
		case 1:
			v = coordinates.get(0);	x = coordinates.get(2);	y = coordinates.get(3); z = coordinates.get(4);
			if (v == null) {
				if (x == null) {
					if (y == null) {
						if (z == null) {
							return getMaxW();
						} else {
							return getMaxWAtZ(z);
						}
					} else if (z == null) {
						return getMaxWAtY(y);
					} else {
						return getMaxWAtYZ(y, z);
					}
				} else if (y == null) {
					if (z == null) {
						return getMaxWAtX(x);
					} else {
						return getMaxWAtXZ(x, z);
					}
				} else if (z == null) {
					return getMaxWAtXY(x, y);
				} else {
					return getMaxWAtXYZ(x, y, z);
				}
			} else if (x == null) {
				if (y == null) {
					if (z == null) {
						return getMaxWAtV(v);
					} else {
						return getMaxWAtVZ(v, z);
					}
				} else if (z == null) {
					return getMaxWAtVY(v, y);
				} else {
					return getMaxWAtVYZ(v, y, z);
				}
			} else if (y == null) {
				if (z == null) {
					return getMaxWAtVX(v, x);
				} else {
					return getMaxWAtVXZ(v, x, z);
				}
			} else if (z == null) {
				return getMaxWAtVXY(v, x, y);
			} else {
				return getMaxW(v, x, y, z);
			}
		case 2:
			v = coordinates.get(0);	w = coordinates.get(1);	y = coordinates.get(3); z = coordinates.get(4);
			if (v == null) {
				if (w == null) {
					if (y == null) {
						if (z == null) {
							return getMaxX();
						} else {
							return getMaxXAtZ(z);
						}
					} else if (z == null) {
						return getMaxXAtY(y);
					} else {
						return getMaxXAtYZ(y, z);
					}
				} else if (y == null) {
					if (z == null) {
						return getMaxXAtW(w);
					} else {
						return getMaxXAtWZ(w, z);
					}
				} else if (z == null) {
					return getMaxXAtWY(w, y);
				} else {
					return getMaxXAtWYZ(w, y, z);
				}
			} else if (w == null) {
				if (y == null) {
					if (z == null) {
						return getMaxXAtV(v);
					} else {
						return getMaxXAtVZ(v, z);
					}
				} else if (z == null) {
					return getMaxXAtVY(v, y);
				} else {
					return getMaxXAtVYZ(v, y, z);
				}
			} else if (y == null) {
				if (z == null) {
					return getMaxXAtVW(v, w);
				} else {
					return getMaxXAtVWZ(v, w, z);
				}
			} else if (z == null) {
				return getMaxXAtVWY(v, w, y);
			} else {
				return getMaxX(v, w, y, z);
			}
		case 3:
			v = coordinates.get(0);	w = coordinates.get(1);	x = coordinates.get(2); z = coordinates.get(4);
			if (v == null) {
				if (w == null) {
					if (x == null) {
						if (z == null) {
							return getMaxY();
						} else {
							return getMaxYAtZ(z);
						}
					} else if (z == null) {
						return getMaxYAtX(x);
					} else {
						return getMaxYAtXZ(x, z);
					}
				} else if (x == null) {
					if (z == null) {
						return getMaxYAtW(w);
					} else {
						return getMaxYAtWZ(w, z);
					}
				} else if (z == null) {
					return getMaxYAtWX(w, x);
				} else {
					return getMaxYAtWXZ(w, x, z);
				}
			} else if (w == null) {
				if (x == null) {
					if (z == null) {
						return getMaxYAtV(v);
					} else {
						return getMaxYAtVZ(v, z);
					}
				} else if (z == null) {
					return getMaxYAtVX(v, x);
				} else {
					return getMaxYAtVXZ(v, x, z);
				}
			} else if (x == null) {
				if (z == null) {
					return getMaxYAtVW(v, w);
				} else {
					return getMaxYAtVWZ(v, w, z);
				}
			} else if (z == null) {
				return getMaxYAtVWX(v, w, x);
			} else {
				return getMaxY(v, w, x, z);
			}
		case 4:
			v = coordinates.get(0);	w = coordinates.get(1);	x = coordinates.get(2); y = coordinates.get(3);
			if (v == null) {
				if (w == null) {
					if (x == null) {
						if (y == null) {
							return getMaxZ();
						} else {
							return getMaxZAtY(y);
						}
					} else if (y == null) {
						return getMaxZAtX(x);
					} else {
						return getMaxZAtXY(x, y);
					}
				} else if (x == null) {
					if (y == null) {
						return getMaxZAtW(w);
					} else {
						return getMaxZAtWY(w, y);
					}
				} else if (y == null) {
					return getMaxZAtWX(w, x);
				} else {
					return getMaxZAtWXY(w, x, y);
				}
			} else if (w == null) {
				if (x == null) {
					if (y == null) {
						return getMaxZAtV(v);
					} else {
						return getMaxZAtVY(v, y);
					}
				} else if (y == null) {
					return getMaxZAtVX(v, x);
				} else {
					return getMaxZAtVXY(v, x, y);
				}
			} else if (x == null) {
				if (y == null) {
					return getMaxZAtVW(v, w);
				} else {
					return getMaxZAtVWY(v, w, y);
				}
			} else if (y == null) {
				return getMaxZAtVWX(v, w, x);
			} else {
				return getMaxZ(v, w, x, y);
			}
		default:
			throw new IllegalArgumentException("The axis must be 0, 1, 2, 3 or 4. Got " + axis + ".");
		}
	}
	
	@Override
	default int getMinCoordinate(int axis) {
		switch (axis) {
		case 0: 
			return getMinV();
		case 1: 
			return getMinW();
		case 2: 
			return getMinX();
		case 3: 
			return getMinY();
		case 4: 
			return getMinZ();
		default:
			throw new IllegalArgumentException("The axis must be 0, 1, 2, 3 or 4. Got " + axis + ".");
		}
	}

	@Override
	default int getMinCoordinate(int axis, PartialCoordinates coordinates) {
		Integer v, w, x, y, z;
		switch (axis) {
		case 0:
			w = coordinates.get(1);	x = coordinates.get(2);	y = coordinates.get(3); z = coordinates.get(4);
			if (w == null) {
				if (x == null) {
					if (y == null) {
						if (z == null) {
							return getMinV();
						} else {
							return getMinVAtZ(z);
						}
					} else if (z == null) {
						return getMinVAtY(y);
					} else {
						return getMinVAtYZ(y, z);
					}
				} else if (y == null) {
					if (z == null) {
						return getMinVAtX(x);
					} else {
						return getMinVAtXZ(x, z);
					}
				} else if (z == null) {
					return getMinVAtXY(x, y);
				} else {
					return getMinVAtXYZ(x, y, z);
				}
			} else if (x == null) {
				if (y == null) {
					if (z == null) {
						return getMinVAtW(w);
					} else {
						return getMinVAtWZ(w, z);
					}
				} else if (z == null) {
					return getMinVAtWY(w, y);
				} else {
					return getMinVAtWYZ(w, y, z);
				}
			} else if (y == null) {
				if (z == null) {
					return getMinVAtWX(w, x);
				} else {
					return getMinVAtWXZ(w, x, z);
				}
			} else if (z == null) {
				return getMinVAtWXY(w, x, y);
			} else {
				return getMinV(w, x, y, z);
			}
		case 1:
			v = coordinates.get(0);	x = coordinates.get(2);	y = coordinates.get(3); z = coordinates.get(4);
			if (v == null) {
				if (x == null) {
					if (y == null) {
						if (z == null) {
							return getMinW();
						} else {
							return getMinWAtZ(z);
						}
					} else if (z == null) {
						return getMinWAtY(y);
					} else {
						return getMinWAtYZ(y, z);
					}
				} else if (y == null) {
					if (z == null) {
						return getMinWAtX(x);
					} else {
						return getMinWAtXZ(x, z);
					}
				} else if (z == null) {
					return getMinWAtXY(x, y);
				} else {
					return getMinWAtXYZ(x, y, z);
				}
			} else if (x == null) {
				if (y == null) {
					if (z == null) {
						return getMinWAtV(v);
					} else {
						return getMinWAtVZ(v, z);
					}
				} else if (z == null) {
					return getMinWAtVY(v, y);
				} else {
					return getMinWAtVYZ(v, y, z);
				}
			} else if (y == null) {
				if (z == null) {
					return getMinWAtVX(v, x);
				} else {
					return getMinWAtVXZ(v, x, z);
				}
			} else if (z == null) {
				return getMinWAtVXY(v, x, y);
			} else {
				return getMinW(v, x, y, z);
			}
		case 2:
			v = coordinates.get(0);	w = coordinates.get(1);	y = coordinates.get(3); z = coordinates.get(4);
			if (v == null) {
				if (w == null) {
					if (y == null) {
						if (z == null) {
							return getMinX();
						} else {
							return getMinXAtZ(z);
						}
					} else if (z == null) {
						return getMinXAtY(y);
					} else {
						return getMinXAtYZ(y, z);
					}
				} else if (y == null) {
					if (z == null) {
						return getMinXAtW(w);
					} else {
						return getMinXAtWZ(w, z);
					}
				} else if (z == null) {
					return getMinXAtWY(w, y);
				} else {
					return getMinXAtWYZ(w, y, z);
				}
			} else if (w == null) {
				if (y == null) {
					if (z == null) {
						return getMinXAtV(v);
					} else {
						return getMinXAtVZ(v, z);
					}
				} else if (z == null) {
					return getMinXAtVY(v, y);
				} else {
					return getMinXAtVYZ(v, y, z);
				}
			} else if (y == null) {
				if (z == null) {
					return getMinXAtVW(v, w);
				} else {
					return getMinXAtVWZ(v, w, z);
				}
			} else if (z == null) {
				return getMinXAtVWY(v, w, y);
			} else {
				return getMinX(v, w, y, z);
			}
		case 3:
			v = coordinates.get(0);	w = coordinates.get(1);	x = coordinates.get(2); z = coordinates.get(4);
			if (v == null) {
				if (w == null) {
					if (x == null) {
						if (z == null) {
							return getMinY();
						} else {
							return getMinYAtZ(z);
						}
					} else if (z == null) {
						return getMinYAtX(x);
					} else {
						return getMinYAtXZ(x, z);
					}
				} else if (x == null) {
					if (z == null) {
						return getMinYAtW(w);
					} else {
						return getMinYAtWZ(w, z);
					}
				} else if (z == null) {
					return getMinYAtWX(w, x);
				} else {
					return getMinYAtWXZ(w, x, z);
				}
			} else if (w == null) {
				if (x == null) {
					if (z == null) {
						return getMinYAtV(v);
					} else {
						return getMinYAtVZ(v, z);
					}
				} else if (z == null) {
					return getMinYAtVX(v, x);
				} else {
					return getMinYAtVXZ(v, x, z);
				}
			} else if (x == null) {
				if (z == null) {
					return getMinYAtVW(v, w);
				} else {
					return getMinYAtVWZ(v, w, z);
				}
			} else if (z == null) {
				return getMinYAtVWX(v, w, x);
			} else {
				return getMinY(v, w, x, z);
			}
		case 4:
			v = coordinates.get(0);	w = coordinates.get(1);	x = coordinates.get(2); y = coordinates.get(3);
			if (v == null) {
				if (w == null) {
					if (x == null) {
						if (y == null) {
							return getMinZ();
						} else {
							return getMinZAtY(y);
						}
					} else if (y == null) {
						return getMinZAtX(x);
					} else {
						return getMinZAtXY(x, y);
					}
				} else if (x == null) {
					if (y == null) {
						return getMinZAtW(w);
					} else {
						return getMinZAtWY(w, y);
					}
				} else if (y == null) {
					return getMinZAtWX(w, x);
				} else {
					return getMinZAtWXY(w, x, y);
				}
			} else if (w == null) {
				if (x == null) {
					if (y == null) {
						return getMinZAtV(v);
					} else {
						return getMinZAtVY(v, y);
					}
				} else if (y == null) {
					return getMinZAtVX(v, x);
				} else {
					return getMinZAtVXY(v, x, y);
				}
			} else if (x == null) {
				if (y == null) {
					return getMinZAtVW(v, w);
				} else {
					return getMinZAtVWY(v, w, y);
				}
			} else if (y == null) {
				return getMinZAtVWX(v, w, x);
			} else {
				return getMinZ(v, w, x, y);
			}
		default:
			throw new IllegalArgumentException("The axis must be 0, 1, 2, 3 or 4. Got " + axis + ".");
		}
	}
	
}
