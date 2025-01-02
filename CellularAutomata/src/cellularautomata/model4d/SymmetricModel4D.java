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
package cellularautomata.model4d;

import cellularautomata.PartialCoordinates;
import cellularautomata.model.SymmetricModel;

public interface SymmetricModel4D extends Model4D, SymmetricModel {
	
	/**
	 * Returns the smallest w-coordinate of the asymmetric section of the grid
	 * 
	 * @return the smallest w
	 */
	int getAsymmetricMinW();
	
	/**
	 * Returns the largest w-coordinate of the asymmetric section of the grid
	 * 
	 * @return the largest w
	 */
	int getAsymmetricMaxW();
	
	/**
	 * Returns the smallest w-coordinate of the asymmetric section of the grid at (x,y,z).<br/>
	 * It's not defined to call this method on a 'x', 'y' and 'z' coordinates outside the bounds of [{@link #getAsymmetricMinX()}, 
	 * {@link #getAsymmetricMaxX()}], [{@link #getAsymmetricMinY()}, {@link #getAsymmetricMaxY()}] and [{@link #getAsymmetricMinZ()}, {@link #getAsymmetricMaxZ()}] 
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @return the smallest w
	 */
	int getAsymmetricMinW(int x, int y, int z);
	
	/**
	 * Returns the largest w-coordinate of the asymmetric section of the grid at (x,y,z).<br/>
	 * It's not defined to call this method on a 'x', 'y' and 'z' coordinates outside the bounds of [{@link #getAsymmetricMinX()}, 
	 * {@link #getAsymmetricMaxX()}], [{@link #getAsymmetricMinY()}, {@link #getAsymmetricMaxY()}] and [{@link #getAsymmetricMinZ()}, {@link #getAsymmetricMaxZ()}] 
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @return the largest w
	 */
	int getAsymmetricMaxW(int x, int y, int z);
	
	/**
	 * Returns the smallest x-coordinate of the asymmetric section of the grid
	 * 
	 * @return the smallest x
	 */
	int getAsymmetricMinX();
	
	/**
	 * Returns the largest x-coordinate of the asymmetric section of the grid
	 * 
	 * @return the largest x
	 */
	int getAsymmetricMaxX();
	
	/**
	 * Returns the smallest y-coordinate of the asymmetric section of the grid
	 * 
	 * @return the smallest y
	 */
	int getAsymmetricMinY();
	
	/**
	 * Returns the largest y-coordinate of the asymmetric section of the grid
	 * 
	 * @return the largest y
	 */
	int getAsymmetricMaxY();
	
	/**
	 * Returns the smallest z-coordinate of the asymmetric section of the grid
	 * 
	 * @return the smallest z
	 */
	int getAsymmetricMinZ();
	
	/**
	 * Returns the largest z-coordinate of the asymmetric section of the grid
	 * 
	 * @return the largest z
	 */
	int getAsymmetricMaxZ();

	int getAsymmetricMinWAtZ(int z);

	int getAsymmetricMinWAtXZ(int x, int z);

	int getAsymmetricMinWAtYZ(int y, int z);

	int getAsymmetricMaxWAtZ(int z);

	int getAsymmetricMaxWAtXZ(int x, int z);

	int getAsymmetricMaxWAtYZ(int y, int z);

	int getAsymmetricMinXAtZ(int z);

	int getAsymmetricMinXAtWZ(int w, int z);

	int getAsymmetricMinXAtYZ(int y, int z);

	int getAsymmetricMinX(int w, int y, int z);

	int getAsymmetricMaxXAtZ(int z);

	int getAsymmetricMaxXAtWZ(int w, int z);

	int getAsymmetricMaxXAtYZ(int y, int z);

	int getAsymmetricMaxX(int w, int y, int z);

	int getAsymmetricMinYAtZ(int z);

	int getAsymmetricMaxYAtWZ(int w, int z);

	int getAsymmetricMinYAtXZ(int x, int z);

	int getAsymmetricMinY(int w, int x, int z);

	int getAsymmetricMaxYAtZ(int z);

	int getAsymmetricMinYAtWZ(int w, int z);

	int getAsymmetricMaxYAtXZ(int x, int z);

	int getAsymmetricMaxY(int w, int x, int z);
	
	int getAsymmetricMinXAtW(int w);

	int getAsymmetricMaxXAtW(int w);

	int getAsymmetricMinYAtWX(int w, int x);

	int getAsymmetricMaxYAtWX(int w, int x);

	int getAsymmetricMinZ(int w, int x, int y);

	int getAsymmetricMaxZ(int w, int x, int y);
	
	int getAsymmetricMinYAtW(int w);
	
	int getAsymmetricMaxYAtW(int w);
	
	int getAsymmetricMinZAtW(int w);
	
	int getAsymmetricMaxZAtW(int w);

	int getAsymmetricMinWAtX(int x);

	int getAsymmetricMaxWAtX(int x);

	int getAsymmetricMinWAtY(int y);

	int getAsymmetricMaxWAtY(int y);

	int getAsymmetricMinWAtXY(int x, int y);

	int getAsymmetricMaxWAtXY(int x, int y);

	int getAsymmetricMinXAtY(int y);

	int getAsymmetricMaxXAtY(int y);

	int getAsymmetricMinXAtWY(int w, int y);

	int getAsymmetricMaxXAtWY(int w, int y);

	int getAsymmetricMinYAtX(int x);

	int getAsymmetricMaxYAtX(int x);

	int getAsymmetricMinZAtX(int x);

	int getAsymmetricMaxZAtX(int x);

	int getAsymmetricMinZAtY(int y);

	int getAsymmetricMaxZAtY(int y);

	int getAsymmetricMinZAtWX(int w, int x);

	int getAsymmetricMaxZAtWX(int w, int x);

	int getAsymmetricMinZAtWY(int w, int y);

	int getAsymmetricMaxZAtWY(int w, int y);

	int getAsymmetricMinZAtXY(int x, int y);

	int getAsymmetricMaxZAtXY(int x, int y);
	
	@Override
	default int getAsymmetricMaxCoordinate(int axis) {
		switch (axis) {
		case 0: 
			return getAsymmetricMaxW();
		case 1: 
			return getAsymmetricMaxX();
		case 2: 
			return getAsymmetricMaxY();
		case 3: 
			return getAsymmetricMaxZ();
		default:
			throw new IllegalArgumentException("The axis must be 0, 1, 2 or 3. Got " + axis + ".");
		}
	}

	@Override
	default int getAsymmetricMaxCoordinate(int axis, PartialCoordinates coordinates) {
		Integer w, x, y, z;
		switch (axis) {
		case 0:
			x = coordinates.get(1);	y = coordinates.get(2); z = coordinates.get(3);
			if (x == null) {
				if (y == null) {
					if (z == null) {
						return getAsymmetricMaxW();
					} else {
						return getAsymmetricMaxWAtZ(z);
					}
				} else if (z == null) {
					return getAsymmetricMaxWAtY(y);
				} else {
					return getAsymmetricMaxWAtYZ(y, z);
				}
			} else if (y == null) {
				if (z == null) {
					return getAsymmetricMaxWAtX(x);
				} else {
					return getAsymmetricMaxWAtXZ(x, z);
				}
			} else if (z == null) {
				return getAsymmetricMaxWAtXY(x, y);
			} else {
				return getAsymmetricMaxW(x, y, z);
			}
		case 1:
			w = coordinates.get(0);	y = coordinates.get(2); z = coordinates.get(3);
			if (w == null) {
				if (y == null) {
					if (z == null) {
						return getAsymmetricMaxX();
					} else {
						return getAsymmetricMaxXAtZ(z);
					}
				} else if (z == null) {
					return getAsymmetricMaxXAtY(y);
				} else {
					return getAsymmetricMaxXAtYZ(y, z);
				}
			} else if (y == null) {
				if (z == null) {
					return getAsymmetricMaxXAtW(w);
				} else {
					return getAsymmetricMaxXAtWZ(w, z);
				}
			} else if (z == null) {
				return getAsymmetricMaxXAtWY(w, y);
			} else {
				return getAsymmetricMaxX(w, y, z);
			}
		case 2:
			w = coordinates.get(0);	x = coordinates.get(1); z = coordinates.get(3);
			if (w == null) {
				if (x == null) {
					if (z == null) {
						return getAsymmetricMaxY();
					} else {
						return getAsymmetricMaxYAtZ(z);
					}
				} else if (z == null) {
					return getAsymmetricMaxYAtX(x);
				} else {
					return getAsymmetricMaxYAtXZ(x, z);
				}
			} else if (x == null) {
				if (z == null) {
					return getAsymmetricMaxYAtW(w);
				} else {
					return getAsymmetricMaxYAtWZ(w, z);
				}
			} else if (z == null) {
				return getAsymmetricMaxYAtWX(w, x);
			} else {
				return getAsymmetricMaxY(w, x, z);
			}
		case 3:
			w = coordinates.get(0);	x = coordinates.get(1); y = coordinates.get(2);
			if (w == null) {
				if (x == null) {
					if (y == null) {
						return getAsymmetricMaxZ();
					} else {
						return getAsymmetricMaxZAtY(y);
					}
				} else if (y == null) {
					return getAsymmetricMaxZAtX(x);
				} else {
					return getAsymmetricMaxZAtXY(x, y);
				}
			} else if (x == null) {
				if (y == null) {
					return getAsymmetricMaxZAtW(w);
				} else {
					return getAsymmetricMaxZAtWY(w, y);
				}
			} else if (y == null) {
				return getAsymmetricMaxZAtWX(w, x);
			} else {
				return getAsymmetricMaxZ(w, x, y);
			}
		default:
			throw new IllegalArgumentException("The axis must be 0, 1, 2 or 3. Got " + axis + ".");
		}
	}
	
	@Override
	default int getAsymmetricMinCoordinate(int axis) {
		switch (axis) {
		case 0: 
			return getAsymmetricMinW();
		case 1: 
			return getAsymmetricMinX();
		case 2: 
			return getAsymmetricMinY();
		case 3: 
			return getAsymmetricMinZ();
		default:
			throw new IllegalArgumentException("The axis must be 0, 1, 2 or 3. Got " + axis + ".");
		}
	}

	@Override
	default int getAsymmetricMinCoordinate(int axis, PartialCoordinates coordinates) {
		Integer w, x, y, z;
		switch (axis) {
		case 0:
			x = coordinates.get(1);	y = coordinates.get(2); z = coordinates.get(3);
			if (x == null) {
				if (y == null) {
					if (z == null) {
						return getAsymmetricMinW();
					} else {
						return getAsymmetricMinWAtZ(z);
					}
				} else if (z == null) {
					return getAsymmetricMinWAtY(y);
				} else {
					return getAsymmetricMinWAtYZ(y, z);
				}
			} else if (y == null) {
				if (z == null) {
					return getAsymmetricMinWAtX(x);
				} else {
					return getAsymmetricMinWAtXZ(x, z);
				}
			} else if (z == null) {
				return getAsymmetricMinWAtXY(x, y);
			} else {
				return getAsymmetricMinW(x, y, z);
			}
		case 1:
			w = coordinates.get(0);	y = coordinates.get(2); z = coordinates.get(3);
			if (w == null) {
				if (y == null) {
					if (z == null) {
						return getAsymmetricMinX();
					} else {
						return getAsymmetricMinXAtZ(z);
					}
				} else if (z == null) {
					return getAsymmetricMinXAtY(y);
				} else {
					return getAsymmetricMinXAtYZ(y, z);
				}
			} else if (y == null) {
				if (z == null) {
					return getAsymmetricMinXAtW(w);
				} else {
					return getAsymmetricMinXAtWZ(w, z);
				}
			} else if (z == null) {
				return getAsymmetricMinXAtWY(w, y);
			} else {
				return getAsymmetricMinX(w, y, z);
			}
		case 2:
			w = coordinates.get(0);	x = coordinates.get(1); z = coordinates.get(3);
			if (w == null) {
				if (x == null) {
					if (z == null) {
						return getAsymmetricMinY();
					} else {
						return getAsymmetricMinYAtZ(z);
					}
				} else if (z == null) {
					return getAsymmetricMinYAtX(x);
				} else {
					return getAsymmetricMinYAtXZ(x, z);
				}
			} else if (x == null) {
				if (z == null) {
					return getAsymmetricMinYAtW(w);
				} else {
					return getAsymmetricMinYAtWZ(w, z);
				}
			} else if (z == null) {
				return getAsymmetricMinYAtWX(w, x);
			} else {
				return getAsymmetricMinY(w, x, z);
			}
		case 3:
			w = coordinates.get(0);	x = coordinates.get(1); y = coordinates.get(2);
			if (w == null) {
				if (x == null) {
					if (y == null) {
						return getAsymmetricMinZ();
					} else {
						return getAsymmetricMinZAtY(y);
					}
				} else if (y == null) {
					return getAsymmetricMinZAtX(x);
				} else {
					return getAsymmetricMinZAtXY(x, y);
				}
			} else if (x == null) {
				if (y == null) {
					return getAsymmetricMinZAtW(w);
				} else {
					return getAsymmetricMinZAtWY(w, y);
				}
			} else if (y == null) {
				return getAsymmetricMinZAtWX(w, x);
			} else {
				return getAsymmetricMinZ(w, x, y);
			}
		default:
			throw new IllegalArgumentException("The axis must be 0, 1, 2 or 3. Got " + axis + ".");
		}
	}
	
	@Override
	default Model4D asymmetricSection() {
		return new AsymmetricModelSection4D<SymmetricModel4D>(this);
	}

}
