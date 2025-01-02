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
package cellularautomata.model3d;

import cellularautomata.PartialCoordinates;
import cellularautomata.model.SymmetricModel;

public interface SymmetricModel3D extends Model3D, SymmetricModel {
	
	/**
	 * Returns the smallest x-coordinate of the asymmetric section of the grid
	 * 
	 * @return the smallest x
	 */
	int getAsymmetricMinX();
	
	/**
	 * Returns the smallest x-coordinate of the asymmetric section of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getAsymmetricMinY()} 
	 * or greater than {@link #getAsymmetricMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the smallest x
	 */
	int getAsymmetricMinXAtY(int y);
	
	/**
	 * Returns the smallest x-coordinate of the asymmetric section of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getAsymmetricMinZ()} 
	 * or greater than {@link #getAsymmetricMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the smallest x
	 */
	int getAsymmetricMinXAtZ(int z);
	
	/**
	 * Returns the smallest x-coordinate of the asymmetric section of the grid at (y,z).<br/>
	 * It's not defined to call this method on a 'y' and 'z' coordinates outside the bounds of [{@link #getAsymmetricMinY()}, 
	 * {@link #getAsymmetricMaxY()}] and [{@link #getAsymmetricMinZ()}, {@link #getAsymmetricMaxZ()}] 
	 * 
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @return the smallest x
	 */
	int getAsymmetricMinX(int y, int z);
	
	/**
	 * Returns the largest x-coordinate of the asymmetric section of the grid
	 * 
	 * @return the largest x
	 */
	int getAsymmetricMaxX();
	
	/**
	 * Returns the largest x-coordinate of the asymmetric section of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getAsymmetricMinY()} 
	 * or greater than {@link #getAsymmetricMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the largest x
	 */
	int getAsymmetricMaxXAtY(int y);
	
	/**
	 * Returns the largest x-coordinate of the asymmetric section of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getAsymmetricMinZ()} 
	 * or greater than {@link #getAsymmetricMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the largest x
	 */
	int getAsymmetricMaxXAtZ(int z);
	
	/**
	 * Returns the largest x-coordinate of the asymmetric section of the grid at (y,z).<br/>
	 * It's not defined to call this method on a 'y' and 'z' coordinates outside the bounds of [{@link #getAsymmetricMinY()}, 
	 * {@link #getAsymmetricMaxY()}] and [{@link #getAsymmetricMinZ()}, {@link #getAsymmetricMaxZ()}] 
	 * 
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @return the largest x
	 */
	int getAsymmetricMaxX(int y, int z);
	
	/**
	 * Returns the smallest y-coordinate of the asymmetric section of the grid
	 * 
	 * @return the smallest y
	 */
	int getAsymmetricMinY();
	
	/**
	 * Returns the smallest y-coordinate of the asymmetric section of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getAsymmetricMinX()} 
	 * or greater than {@link #getAsymmetricMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the smallest y
	 */
	int getAsymmetricMinYAtX(int x);
	
	/**
	 * Returns the smallest y-coordinate of the asymmetric section of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getAsymmetricMinZ()} 
	 * or greater than {@link #getAsymmetricMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the smallest y
	 */
	int getAsymmetricMinYAtZ(int z);
	
	/**
	 * Returns the smallest y-coordinate of the asymmetric section of the grid at (x,z).<br/>
	 * It's not defined to call this method on a 'x' and 'z' coordinates outside the bounds of [{@link #getAsymmetricMinX()}, 
	 * {@link #getAsymmetricMaxX()}] and [{@link #getAsymmetricMinZ()}, {@link #getAsymmetricMaxZ()}] 
	 * 
	 * @param x the x-coordinate
	 * @param z the z-coordinate
	 * @return the smallest y
	 */
	int getAsymmetricMinY(int x, int z);

	/**
	 * Returns the largest y-coordinate of the asymmetric section of the grid
	 * 
	 * @return the largest y
	 */
	int getAsymmetricMaxY();
	
	/**
	 * Returns the largest y-coordinate of the asymmetric section of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getAsymmetricMinX()} 
	 * or greater than {@link #getAsymmetricMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the largest y
	 */
	int getAsymmetricMaxYAtX(int x);
	
	/**
	 * Returns the largest y-coordinate of the asymmetric section of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getAsymmetricMinZ()} 
	 * or greater than {@link #getAsymmetricMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the largest y
	 */
	int getAsymmetricMaxYAtZ(int z);
	
	/**
	 * Returns the largest y-coordinate of the asymmetric section of the grid at (x,z).<br/>
	 * It's not defined to call this method on a 'x' and 'z' coordinates outside the bounds of [{@link #getAsymmetricMinX()}, 
	 * {@link #getAsymmetricMaxX()}] and [{@link #getAsymmetricMinZ()}, {@link #getAsymmetricMaxZ()}] 
	 * 
	 * @param x the x-coordinate
	 * @param z the z-coordinate
	 * @return the largest y
	 */
	int getAsymmetricMaxY(int x, int z);
	
	/**
	 * Returns the smallest z-coordinate of the asymmetric section of the grid
	 * 
	 * @return the smallest z
	 */
	int getAsymmetricMinZ();
	
	/**
	 * Returns the smallest z-coordinate of the asymmetric section of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getAsymmetricMinX()} 
	 * or greater than {@link #getAsymmetricMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the smallest z
	 */
	int getAsymmetricMinZAtX(int x);
	
	/**
	 * Returns the smallest z-coordinate of the asymmetric section of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getAsymmetricMinY()} 
	 * or greater than {@link #getAsymmetricMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the smallest z
	 */
	int getAsymmetricMinZAtY(int y);
	
	/**
	 * Returns the smallest z-coordinate of the asymmetric section of the grid at (x,y).<br/>
	 * It's not defined to call this method on a 'x' and 'y' coordinates outside the bounds of [{@link #getAsymmetricMinX()}, 
	 * {@link #getAsymmetricMaxX()}] and [{@link #getAsymmetricMinY()}, {@link #getAsymmetricMaxY()}] 
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the smallest z
	 */
	int getAsymmetricMinZ(int x, int y);
	
	/**
	 * Returns the largest z-coordinate of the asymmetric section of the grid
	 * 
	 * @return the largest z
	 */
	int getAsymmetricMaxZ();
	
	/**
	 * Returns the largest z-coordinate of the asymmetric section of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getAsymmetricMinX()} 
	 * or greater than {@link #getAsymmetricMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the largest z
	 */
	int getAsymmetricMaxZAtX(int x);
	
	/**
	 * Returns the largest z-coordinate of the asymmetric section of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getAsymmetricMinY()} 
	 * or greater than {@link #getAsymmetricMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the largest z
	 */
	int getAsymmetricMaxZAtY(int y);
	
	/**
	 * Returns the largest z-coordinate of the asymmetric section of the grid at (x,y).<br/>
	 * It's not defined to call this method on a 'x' and 'y' coordinates outside the bounds of [{@link #getAsymmetricMinX()}, 
	 * {@link #getAsymmetricMaxX()}] and [{@link #getAsymmetricMinY()}, {@link #getAsymmetricMaxY()}] 
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the largest z
	 */
	int getAsymmetricMaxZ(int x, int y);
	
	@Override
	default int getAsymmetricMaxCoordinate(int axis) {
		switch (axis) {
		case 0: 
			return getAsymmetricMaxX();
		case 1: 
			return getAsymmetricMaxY();
		case 2: 
			return getAsymmetricMaxZ();
		default:
			throw new IllegalArgumentException("The axis must be 0, 1 or 2. Got " + axis + ".");
		}
	}

	@Override
	default int getAsymmetricMaxCoordinate(int axis, PartialCoordinates coordinates) {
		Integer x, y, z;
		switch (axis) {
		case 0:
			y = coordinates.get(1);
			z = coordinates.get(2);
			if (y == null) {
				if (z == null) {
					return getAsymmetricMaxX();
				} else {
					return getAsymmetricMaxXAtZ(z);
				}
			} else if (z == null) {
				return getAsymmetricMaxXAtY(y);
			} else {
				return getAsymmetricMaxX(y, z);
			}
		case 1:
			x = coordinates.get(0);
			z = coordinates.get(2);
			if (x == null) {
				if (z == null) {
					return getAsymmetricMaxY();
				} else {
					return getAsymmetricMaxYAtZ(z);
				}
			} else if (z == null) {
				return getAsymmetricMaxYAtX(x);
			} else {
				return getAsymmetricMaxY(x, z);
			}
		case 2:
			x = coordinates.get(0);
			y = coordinates.get(1);
			if (x == null) {
				if (y == null) {
					return getAsymmetricMaxZ();
				} else {
					return getAsymmetricMaxZAtY(y);
				}
			} else if (y == null) {
				return getAsymmetricMaxZAtX(x);
			} else {
				return getAsymmetricMaxZ(x, y);
			}
		default:
			throw new IllegalArgumentException("The axis must be 0, 1 or 2. Got " + axis + ".");
		}
	}
	
	@Override
	default int getAsymmetricMinCoordinate(int axis) {
		switch (axis) {
		case 0: 
			return getAsymmetricMinX();
		case 1: 
			return getAsymmetricMinY();
		case 2: 
			return getAsymmetricMinZ();
		default:
			throw new IllegalArgumentException("The axis must be 0, 1 or 2. Got " + axis + ".");
		}
	}

	@Override
	default int getAsymmetricMinCoordinate(int axis, PartialCoordinates coordinates) {
		Integer x, y, z;
		switch (axis) {
		case 0:
			y = coordinates.get(1);
			z = coordinates.get(2);
			if (y == null) {
				if (z == null) {
					return getAsymmetricMinX();
				} else {
					return getAsymmetricMinXAtZ(z);
				}
			} else if (z == null) {
				return getAsymmetricMinXAtY(y);
			} else {
				return getAsymmetricMinX(y, z);
			}
		case 1:
			x = coordinates.get(0);
			z = coordinates.get(2);
			if (x == null) {
				if (z == null) {
					return getAsymmetricMinY();
				} else {
					return getAsymmetricMinYAtZ(z);
				}
			} else if (z == null) {
				return getAsymmetricMinYAtX(x);
			} else {
				return getAsymmetricMinY(x, z);
			}
		case 2:
			x = coordinates.get(0);
			y = coordinates.get(1);
			if (x == null) {
				if (y == null) {
					return getAsymmetricMinZ();
				} else {
					return getAsymmetricMinZAtY(y);
				}
			} else if (y == null) {
				return getAsymmetricMinZAtX(x);
			} else {
				return getAsymmetricMinZ(x, y);
			}
		default:
			throw new IllegalArgumentException("The axis must be 0, 1 or 2. Got " + axis + ".");
		}
	}
	
	@Override
	default Model3D asymmetricSection() {
		return new AsymmetricModelSection3D<SymmetricModel3D>(this);
	}

}
