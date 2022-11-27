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

import cellularautomata.PartialCoordinates;
import cellularautomata.model.Model;
import cellularautomata.model3d.Model3D;

public interface Model4D extends Model {
	
	@Override
	default int getGridDimension() {
		return 4;
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
			return getWLabel();
		case 1: 
			return getXLabel();
		case 2: 
			return getYLabel();
		case 3: 
			return getZLabel();
		default:
			throw new IllegalArgumentException("The axis must be 0, 1, 2 or 3. Got " + axis + ".");
		}
	}
	
	/**
	 * Returns the smallest w-coordinate
	 * 
	 * @return the smallest w
	 */
	int getMinW();

	default int getMinWAtZ(int z) {
		return getMinW();
	}

	default int getMinWAtXZ(int x, int z) {
		return getMinW();
	}

	default int getMinWAtYZ(int y, int z) {
		return getMinW();
	}
	
	/**
	 * Returns the smallest w-coordinate of the grid at (x,y,z).<br/>
	 * It's not defined to call this method on a 'x', 'y' and 'z' coordinates outside the bounds of [{@link #getMinX()}, 
	 * {@link #getMaxX()}], [{@link #getMinY()}, {@link #getMaxY()}] and [{@link #getMinZ()}, {@link #getMaxZ()}] 
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @return the smallest w
	 */
	default int getMinW(int x, int y, int z) {
		return getMinW();
	}
	
	/**
	 * Returns the largest w-coordinate
	 * 
	 * @return the largest w
	 */
	int getMaxW();

	default int getMaxWAtZ(int z) {
		return getMaxW();
	}

	default int getMaxWAtXZ(int x, int z) {
		return getMaxW();
	}

	default int getMaxWAtYZ(int y, int z) {
		return getMaxW();
	}
	
	/**
	 * Returns the largest w-coordinate of the grid at (x,y,z).<br/>
	 * It's not defined to call this method on a 'x', 'y' and 'z' coordinates outside the bounds of [{@link #getMinX()}, 
	 * {@link #getMaxX()}], [{@link #getMinY()}, {@link #getMaxY()}] and [{@link #getMinZ()}, {@link #getMaxZ()}] 
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @return the largest w
	 */
	default int getMaxW(int x, int y, int z) {
		return getMaxW();
	}
	
	/**
	 * Returns the smallest x-coordinate
	 * 
	 * @return the smallest x
	 */
	int getMinX();
	
	default int getMinXAtW(int w) {
		return getMinX();
	}

	default int getMinXAtZ(int z) {
		return getMinX();
	}

	default int getMinXAtWZ(int w, int z) {
		return getMinX();
	}

	default int getMinXAtYZ(int y, int z) {
		return getMinX();
	}

	default int getMinX(int w, int y, int z) {
		return getMinX();
	}
	
	/**
	 * Returns the largest x-coordinate
	 * 
	 * @return the largest x
	 */
	int getMaxX();
	
	default int getMaxXAtW(int w) {
		return getMaxX();
	}

	default int getMaxXAtZ(int z) {
		return getMaxX();
	}

	default int getMaxXAtWZ(int w, int z) {
		return getMaxX();
	}

	default int getMaxXAtYZ(int y, int z) {
		return getMaxX();
	}

	default int getMaxX(int w, int y, int z) {
		return getMaxX();
	}
	
	/**
	 * Returns the smallest y-coordinate
	 * 
	 * @return the smallest y
	 */
	int getMinY();
	
	default int getMinYAtW(int w) {
		return getMinY();
	}

	default int getMinYAtZ(int z) {
		return getMinY();
	}
	
	default int getMinYAtWX(int w, int x) {
		return getMinY();
	}

	default int getMinYAtWZ(int w, int z) {
		return getMinY();
	}

	default int getMinYAtXZ(int x, int z) {
		return getMinY();
	}

	default int getMinY(int w, int x, int z) {
		return getMinY();
	}
	
	/**
	 * Returns the largest y-coordinate
	 * 
	 * @return the largest y
	 */
	int getMaxY();
	
	default int getMaxYAtW(int w) {
		return getMaxY();
	}
	
	default int getMaxYAtWX(int w, int x) {
		return getMaxY();
	}

	default int getMaxYAtWZ(int w, int z) {
		return getMaxY();
	}

	default int getMaxYAtZ(int z) {
		return getMaxY();
	}

	default int getMaxYAtXZ(int x, int z) {
		return getMaxY();
	}

	default int getMaxY(int w, int x, int z) {
		return getMaxY();
	}
	
	/**
	 * Returns the smallest z-coordinate
	 * 
	 * @return the smallest z
	 */
	int getMinZ();
	
	default int getMinZAtW(int w) {
		return getMinZ();
	}

	default int getMinZ(int w, int x, int y) {
		return getMinZ();
	}
	
	/**
	 * Returns the largest z-coordinate
	 * 
	 * @return the largest z
	 */
	int getMaxZ();
	
	default int getMaxZAtW(int w) {
		return getMaxZ();
	}

	default int getMaxZ(int w, int x, int y) {
		return getMaxZ();
	}

	default int getMinWAtX(int x) { return getMinW(); }

	default int getMaxWAtX(int x) { return getMaxW(); }

	default int getMinWAtY(int y) { return getMinW(); }

	default int getMaxWAtY(int y) { return getMaxW(); }

	default int getMinWAtXY(int x, int y) { return getMinW(); }

	default int getMaxWAtXY(int x, int y) { return getMaxW(); }

	default int getMinXAtY(int y) { return getMinX(); }

	default int getMaxXAtY(int y) { return getMaxX(); }

	default int getMinXAtWY(int w, int y) { return getMinX(); }

	default int getMaxXAtWY(int w, int y) { return getMaxX(); }

	default int getMinYAtX(int x) { return getMinY(); }

	default int getMaxYAtX(int x) { return getMaxY(); }

	default int getMinZAtX(int x) { return getMinZ(); }

	default int getMaxZAtX(int x) { return getMaxZ(); }

	default int getMinZAtY(int y) { return getMinZ(); }

	default int getMaxZAtY(int y) { return getMaxZ(); }

	default int getMinZAtWX(int w, int x) { return getMinZ(); }

	default int getMaxZAtWX(int w, int x) { return getMaxZ(); }

	default int getMinZAtWY(int w, int y) { return getMinZ(); }

	default int getMaxZAtWY(int w, int y) { return getMaxZ(); }

	default int getMinZAtXY(int x, int y) { return getMinZ(); }

	default int getMaxZAtXY(int x, int y) { return getMaxZ(); }
	
	@Override
	default Model4D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return subsection(
				minCoordinates.get(0), maxCoordinates.get(0),
				minCoordinates.get(1), maxCoordinates.get(1),
				minCoordinates.get(2), maxCoordinates.get(2),
				minCoordinates.get(3), maxCoordinates.get(3));
	}

	default Model4D subsection(Integer minW, Integer maxW, Integer minX, Integer maxX, Integer minY, Integer maxY, Integer minZ, Integer maxZ) {
		return new SubModel4D<Model4D>(this, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default Model3D crossSection(int axis, int coordinate) {
		switch (axis) {
		case 0:
			return crossSectionAtW(coordinate);
		case 1:
			return crossSectionAtX(coordinate);
		case 2:
			return crossSectionAtY(coordinate);
		case 3:
			return crossSectionAtZ(coordinate);
		default:
			throw new IllegalArgumentException("The axis must be 0, 1, 2 or 3. Got " + axis + ".");
		}
	}
	
	default Model3D crossSectionAtW(int w) {
		return new Model4DWCrossSection<Model4D>(this, w);
	}
	
	default Model3D crossSectionAtX(int x) {
		return new Model4DXCrossSection<Model4D>(this, x);
	}
	
	default Model3D crossSectionAtY(int y) {
		return new Model4DYCrossSection<Model4D>(this, y);
	}
	
	/**
	 * <p>Returns a 3D grid decorating the current one to show only the positions whose z coordinate is equal to the passed value.</p>
	 * <p>It is important to note that due to the use of the names '<strong>w</strong>', '<strong>x</strong>', '<strong>y</strong>' and '<strong>z</strong>' 
	 * for the 4D coordinates and '<strong>x</strong>', '<strong>y</strong>' and '<strong>z</strong>' for the 3D coordinates, 
	 * there is a mismatch between the names of the coordinates in the 3D cross section and those of the same coordinates in the source 4D grid. 
	 * The source's '<strong>w</strong>' coordinate becomes the cross section's '<strong>x</strong>' coordinate, the '<strong>x</strong>' becomes the 
	 * '<strong>y</strong>' and the '<strong>y</strong>' becomes the '<strong>z</strong>'.</p>
	 * 
	 * @param z
	 * @return
	 */
	default Model3D crossSectionAtZ(int z) {
		return new Model4DZCrossSection<Model4D>(this, z);
	}
	
	@Override
	default Model3D diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		switch (firstAxis) {
		case 0: 
			switch (secondAxis) {
			case 0:
				throw new IllegalArgumentException("The axes cannot be equal.");				
			case 1: 
				return diagonalCrossSectionOnWX(positiveSlope, offset);
			case 2: 
				return diagonalCrossSectionOnWY(positiveSlope, offset);
			case 3: 
				return diagonalCrossSectionOnWZ(positiveSlope, offset);
			default:
				throw new IllegalArgumentException("The axes must be greater than -1 and smaller than 4. Got " + secondAxis + ".");
			}
		case 1: 
			switch (secondAxis) {
			case 0: 
				return diagonalCrossSectionOnWX(positiveSlope, positiveSlope ? -offset : offset);				
			case 1:
				throw new IllegalArgumentException("The axes cannot be equal.");
			case 2: 
				return diagonalCrossSectionOnXY(positiveSlope, offset);
			case 3: 
				return diagonalCrossSectionOnXZ(positiveSlope, offset);
			default:
				throw new IllegalArgumentException("The axes must be greater than -1 and smaller than 4. Got " + secondAxis + ".");
			}			
		case 2: 
			switch (secondAxis) {
			case 0: 
				return diagonalCrossSectionOnWY(positiveSlope, positiveSlope ? -offset : offset);				
			case 1: 
				return diagonalCrossSectionOnXY(positiveSlope, positiveSlope ? -offset : offset);
			case 2:
				throw new IllegalArgumentException("The axes cannot be equal.");
			case 3: 
				return diagonalCrossSectionOnYZ(positiveSlope, offset);
			default:
				throw new IllegalArgumentException("The axes must be greater than -1 and smaller than 4. Got " + secondAxis + ".");
			}			
		case 3: 
			switch (secondAxis) {
			case 0: 
				return diagonalCrossSectionOnWZ(positiveSlope, positiveSlope ? -offset : offset);				
			case 1: 
				return diagonalCrossSectionOnXZ(positiveSlope, positiveSlope ? -offset : offset);
			case 2:
				return diagonalCrossSectionOnYZ(positiveSlope, positiveSlope ? -offset : offset);
			case 3: 
				throw new IllegalArgumentException("The axes cannot be equal.");
			default:
				throw new IllegalArgumentException("The axes must be greater than -1 and smaller than 4. Got " + secondAxis + ".");
			}
		default:
			throw new IllegalArgumentException("The axes must be greater than -1 and smaller than 4. Got " + firstAxis + ".");
		}
	}
	
	default Model3D diagonalCrossSectionOnWX(boolean positiveSlope, int xOffsetFromW) {
		return new Model4DWXDiagonalCrossSection<Model4D>(this, positiveSlope, xOffsetFromW);
	}
	
	default Model3D diagonalCrossSectionOnWY(boolean positiveSlope, int yOffsetFromW) {
		return new Model4DWYDiagonalCrossSection<Model4D>(this, positiveSlope, yOffsetFromW);
	}
	
	default Model3D diagonalCrossSectionOnWZ(boolean positiveSlope, int zOffsetFromW) {
		return new Model4DWZDiagonalCrossSection<Model4D>(this, positiveSlope, zOffsetFromW);
	}
	
	default Model3D diagonalCrossSectionOnXY(boolean positiveSlope, int yOffsetFromX) {
		return new Model4DXYDiagonalCrossSection<Model4D>(this, positiveSlope, yOffsetFromX);
	}
	
	default Model3D diagonalCrossSectionOnXZ(boolean positiveSlope, int zOffsetFromX) {
		return new Model4DXZDiagonalCrossSection<Model4D>(this, positiveSlope, zOffsetFromX);
	}
	
	default Model3D diagonalCrossSectionOnYZ(boolean positiveSlope, int zOffsetFromY) {
		return new Model4DYZDiagonalCrossSection<Model4D>(this, positiveSlope, zOffsetFromY);
	}
	
	@Override
	default int getMaxCoordinate(int axis) {
		switch (axis) {
		case 0: 
			return getMaxW();
		case 1: 
			return getMaxX();
		case 2: 
			return getMaxY();
		case 3: 
			return getMaxZ();
		default:
			throw new IllegalArgumentException("The axis must be 0, 1, 2 or 3. Got " + axis + ".");
		}
	}

	@Override
	default int getMaxCoordinate(int axis, PartialCoordinates coordinates) {
		Integer w, x, y, z;
		switch (axis) {
		case 0:
			x = coordinates.get(1);	y = coordinates.get(2); z = coordinates.get(3);
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
				return getMaxW(x, y, z);
			}
		case 1:
			w = coordinates.get(0);	y = coordinates.get(2); z = coordinates.get(3);
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
				return getMaxX(w, y, z);
			}
		case 2:
			w = coordinates.get(0);	x = coordinates.get(1); z = coordinates.get(3);
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
				return getMaxY(w, x, z);
			}
		case 3:
			w = coordinates.get(0);	x = coordinates.get(1); y = coordinates.get(2);
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
				return getMaxZ(w, x, y);
			}
		default:
			throw new IllegalArgumentException("The axis must be 0, 1, 2 or 3. Got " + axis + ".");
		}
	}
	
	@Override
	default int getMinCoordinate(int axis) {
		switch (axis) {
		case 0: 
			return getMinW();
		case 1: 
			return getMinX();
		case 2: 
			return getMinY();
		case 3: 
			return getMinZ();
		default:
			throw new IllegalArgumentException("The axis must be 0, 1, 2 or 3. Got " + axis + ".");
		}
	}

	@Override
	default int getMinCoordinate(int axis, PartialCoordinates coordinates) {
		Integer w, x, y, z;
		switch (axis) {
		case 0:
			x = coordinates.get(1);	y = coordinates.get(2); z = coordinates.get(3);
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
				return getMinW(x, y, z);
			}
		case 1:
			w = coordinates.get(0);	y = coordinates.get(2); z = coordinates.get(3);
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
				return getMinX(w, y, z);
			}
		case 2:
			w = coordinates.get(0);	x = coordinates.get(1); z = coordinates.get(3);
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
				return getMinY(w, x, z);
			}
		case 3:
			w = coordinates.get(0);	x = coordinates.get(1); y = coordinates.get(2);
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
				return getMinZ(w, x, y);
			}
		default:
			throw new IllegalArgumentException("The axis must be 0, 1, 2 or 3. Got " + axis + ".");
		}
	}
}
