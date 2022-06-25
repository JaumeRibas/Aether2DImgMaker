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

import cellularautomata.model.Model;
import cellularautomata.PartialCoordinates;
import cellularautomata.model2d.Model2D;

public interface Model3D extends Model {
	
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
	default int getGridDimension() {
		return 3;
	}
	
	/**
	 * Returns the smallest x-coordinate of the grid
	 * 
	 * @return the smallest x
	 */
	int getMinX();
	
	/**
	 * Returns the smallest x-coordinate of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getMinY()} 
	 * or bigger than {@link #getMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the smallest x
	 */
	default int getMinXAtY(int y) {
		return getMinX();
	}
	
	/**
	 * Returns the smallest x-coordinate of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getMinZ()} 
	 * or bigger than {@link #getMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the smallest x
	 */
	default int getMinXAtZ(int z) {
		return getMinX();
	}
	
	/**
	 * Returns the smallest x-coordinate of the grid at (y,z).<br/>
	 * It's not defined to call this method on a 'y' and 'z' coordinates outside the bounds of [{@link #getMinY()}, 
	 * {@link #getMaxY()}] and [{@link #getMinZ()}, {@link #getMaxZ()}] 
	 * 
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @return the smallest x
	 */
	default int getMinX(int y, int z) {
		return getMinX();
	}
	
	/**
	 * Returns the largest x-coordinate of the grid
	 * 
	 * @return the largest x
	 */
	int getMaxX();
	
	/**
	 * Returns the largest x-coordinate of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getMinY()} 
	 * or bigger than {@link #getMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the largest x
	 */
	default int getMaxXAtY(int y) {
		return getMaxX();
	}
	
	/**
	 * Returns the largest x-coordinate of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getMinZ()} 
	 * or bigger than {@link #getMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the largest x
	 */
	default int getMaxXAtZ(int z) {
		return getMaxX();
	}
	
	/**
	 * Returns the largest x-coordinate of the grid at (y,z).<br/>
	 * It's not defined to call this method on a 'y' and 'z' coordinates outside the bounds of [{@link #getMinY()}, 
	 * {@link #getMaxY()}] and [{@link #getMinZ()}, {@link #getMaxZ()}] 
	 * 
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @return the largest x
	 */
	default int getMaxX(int y, int z) {
		return getMaxX();
	}
	
	/**
	 * Returns the smallest y-coordinate of the grid
	 * 
	 * @return the smallest y
	 */
	int getMinY();
	
	/**
	 * Returns the smallest y-coordinate of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getMinX()} 
	 * or bigger than {@link #getMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the smallest y
	 */
	default int getMinYAtX(int x) {
		return getMinY();
	}
	
	/**
	 * Returns the smallest y-coordinate of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getMinZ()} 
	 * or bigger than {@link #getMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the smallest y
	 */
	default int getMinYAtZ(int z) {
		return getMinY();
	}
	
	/**
	 * Returns the smallest y-coordinate of the grid at (x,z).<br/>
	 * It's not defined to call this method on a 'x' and 'z' coordinates outside the bounds of [{@link #getMinX()}, 
	 * {@link #getMaxX()}] and [{@link #getMinZ()}, {@link #getMaxZ()}] 
	 * 
	 * @param x the x-coordinate
	 * @param z the z-coordinate
	 * @return the smallest y
	 */
	default int getMinY(int x, int z) {
		return getMinY();
	}

	/**
	 * Returns the largest y-coordinate of the grid
	 * 
	 * @return the largest y
	 */
	int getMaxY();
	
	/**
	 * Returns the largest y-coordinate of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getMinX()} 
	 * or bigger than {@link #getMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the largest y
	 */
	default int getMaxYAtX(int x) {
		return getMaxY();
	}
	
	/**
	 * Returns the largest y-coordinate of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getMinZ()} 
	 * or bigger than {@link #getMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the largest y
	 */
	default int getMaxYAtZ(int z) {
		return getMaxY();
	}
	
	/**
	 * Returns the largest y-coordinate of the grid at (x,z).<br/>
	 * It's not defined to call this method on a 'x' and 'z' coordinates outside the bounds of [{@link #getMinX()}, 
	 * {@link #getMaxX()}] and [{@link #getMinZ()}, {@link #getMaxZ()}] 
	 * 
	 * @param x the x-coordinate
	 * @param z the z-coordinate
	 * @return the largest y
	 */
	default int getMaxY(int x, int z) {
		return getMaxY();
	}
	
	/**
	 * Returns the smallest z-coordinate of the grid
	 * 
	 * @return the smallest z
	 */
	int getMinZ();
	
	/**
	 * Returns the smallest z-coordinate of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getMinX()} 
	 * or bigger than {@link #getMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the smallest z
	 */
	default int getMinZAtX(int x) {
		return getMinZ();
	}
	
	/**
	 * Returns the smallest z-coordinate of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getMinY()} 
	 * or bigger than {@link #getMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the smallest z
	 */
	default int getMinZAtY(int y) {
		return getMinZ();
	}
	
	/**
	 * Returns the smallest z-coordinate of the grid at (x,y).<br/>
	 * It's not defined to call this method on a 'x' and 'y' coordinates outside the bounds of [{@link #getMinX()}, 
	 * {@link #getMaxX()}] and [{@link #getMinY()}, {@link #getMaxY()}] 
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the smallest z
	 */
	default int getMinZ(int x, int y) {
		return getMinZ();
	}
	
	/**
	 * Returns the largest z-coordinate of the grid
	 * 
	 * @return the largest z
	 */
	int getMaxZ();
	
	/**
	 * Returns the largest z-coordinate of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getMinX()} 
	 * or bigger than {@link #getMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the largest z
	 */
	default int getMaxZAtX(int x) {
		return getMaxZ();
	}
	
	/**
	 * Returns the largest z-coordinate of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getMinY()} 
	 * or bigger than {@link #getMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the largest z
	 */
	default int getMaxZAtY(int y) {
		return getMaxZ();
	}
	
	/**
	 * Returns the largest z-coordinate of the grid at (x,y).<br/>
	 * It's not defined to call this method on a 'x' and 'y' coordinates outside the bounds of [{@link #getMinX()}, 
	 * {@link #getMaxX()}] and [{@link #getMinY()}, {@link #getMaxY()}] 
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the largest z
	 */
	default int getMaxZ(int x, int y) {
		return getMaxZ();
	}
	
	@Override
	default Model3D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return subsection(
				minCoordinates.get(0), maxCoordinates.get(0),
				minCoordinates.get(1), maxCoordinates.get(1),
				minCoordinates.get(2), maxCoordinates.get(2));
	}
	
	/**
	 * Returns a decorated {@link Model2D} with the passed bounds.
	 * 
	 * @param minX
	 * @param maxX
	 * @param minY
	 * @param maxY
	 * @param minZ
	 * @param maxZ
	 * @return a {@link Model2D} decorating the current grid 
	 */
	default Model3D subsection(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new SubModel3D<Model3D>(this, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default Model2D crossSection(int axis, int coordinate) {
		switch (axis) {
		case 0: 
			return crossSectionAtX(coordinate);
		case 1: 
			return crossSectionAtY(coordinate);
		case 2: 
			return crossSectionAtZ(coordinate);
		default: throw new IllegalArgumentException("Axis must be 0, 1 or 2. Got " + axis + ".");
		}
	}
	
	default Model2D crossSectionAtX(int x) {
		return new Model3DXCrossSection<Model3D>(this, x);
	}
	
	default Model2D crossSectionAtY(int y) {
		return new Model3DYCrossSection<Model3D>(this, y);
	}
	
	default Model2D crossSectionAtZ(int z) {
		return new Model3DZCrossSection<Model3D>(this, z);
	}
	
	default Model2D diagonalCrossSectionOnXY(boolean positiveSlope, int yOffsetFromX) {
		return new Model3DXYDiagonalCrossSection<Model3D>(this, positiveSlope, yOffsetFromX);
	}
	
	default Model2D diagonalCrossSectionOnXZ(boolean positiveSlope, int zOffsetFromX) {
		return new Model3DXZDiagonalCrossSection<Model3D>(this, positiveSlope, zOffsetFromX);
	}
	
	default Model2D diagonalCrossSectionOnYZ(boolean positiveSlope, int zOffsetFromY) {
		return new Model3DYZDiagonalCrossSection<Model3D>(this, positiveSlope, zOffsetFromY);
	}
	
	@Override
	default int getMaxCoordinate(int axis) {
		switch (axis) {
		case 0: 
			return getMaxX();
		case 1: 
			return getMaxY();
		case 2: 
			return getMaxZ();
		default: throw new IllegalArgumentException("Axis must be 0, 1 or 2. Got " + axis + ".");
		}
	}

	@Override
	default int getMaxCoordinate(int axis, PartialCoordinates coordinates) {
		Integer x, y, z;
		switch (axis) {
		case 0:
			y = coordinates.get(1);
			z = coordinates.get(2);
			if (y == null) {
				if (z == null) {
					return getMaxX();
				} else {
					return getMaxXAtZ(z);
				}
			} else if (z == null) {
				return getMaxXAtY(y);
			} else {
				return getMaxX(y, z);
			}
		case 1:
			x = coordinates.get(0);
			z = coordinates.get(2);
			if (x == null) {
				if (z == null) {
					return getMaxY();
				} else {
					return getMaxYAtZ(z);
				}
			} else if (z == null) {
				return getMaxYAtX(x);
			} else {
				return getMaxY(x, z);
			}
		case 2:
			x = coordinates.get(0);
			y = coordinates.get(1);
			if (x == null) {
				if (y == null) {
					return getMaxZ();
				} else {
					return getMaxZAtY(y);
				}
			} else if (y == null) {
				return getMaxZAtX(x);
			} else {
				return getMaxZ(x, y);
			}
		default: throw new IllegalArgumentException("Axis must be 0, 1 or 2. Got " + axis + ".");
		}
	}
	
	@Override
	default int getMinCoordinate(int axis) {
		switch (axis) {
		case 0: 
			return getMinX();
		case 1: 
			return getMinY();
		case 2: 
			return getMinZ();
		default: throw new IllegalArgumentException("Axis must be 0, 1 or 2. Got " + axis + ".");
		}
	}

	@Override
	default int getMinCoordinate(int axis, PartialCoordinates coordinates) {
		Integer x, y, z;
		switch (axis) {
		case 0:
			y = coordinates.get(1);
			z = coordinates.get(2);
			if (y == null) {
				if (z == null) {
					return getMinX();
				} else {
					return getMinXAtZ(z);
				}
			} else if (z == null) {
				return getMinXAtY(y);
			} else {
				return getMinX(y, z);
			}
		case 1:
			x = coordinates.get(0);
			z = coordinates.get(2);
			if (x == null) {
				if (z == null) {
					return getMinY();
				} else {
					return getMinYAtZ(z);
				}
			} else if (z == null) {
				return getMinYAtX(x);
			} else {
				return getMinY(x, z);
			}
		case 2:
			x = coordinates.get(0);
			y = coordinates.get(1);
			if (x == null) {
				if (y == null) {
					return getMinZ();
				} else {
					return getMinZAtY(y);
				}
			} else if (y == null) {
				return getMinZAtX(x);
			} else {
				return getMinZ(x, y);
			}
		default: throw new IllegalArgumentException("Axis must be 0, 1 or 2. Got " + axis + ".");
		}
	}

}
