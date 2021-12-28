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
package cellularautomata.grid5d;

import cellularautomata.grid.PartialCoordinates;
import cellularautomata.grid.SymmetricGrid;

public interface SymmetricGrid5D extends Grid5D, SymmetricGrid {
	
	int getAsymmetricMinV();

	int getAsymmetricMaxV();

	int getAsymmetricMinVAtW(int w);

	int getAsymmetricMaxVAtW(int w);

	int getAsymmetricMinVAtX(int x);

	int getAsymmetricMaxVAtX(int x);

	int getAsymmetricMinVAtY(int y);

	int getAsymmetricMaxVAtY(int y);

	int getAsymmetricMinVAtZ(int z);

	int getAsymmetricMaxVAtZ(int z);

	int getAsymmetricMinVAtWX(int w, int x);

	int getAsymmetricMaxVAtWX(int w, int x);

	int getAsymmetricMinVAtWY(int w, int y);

	int getAsymmetricMaxVAtWY(int w, int y);

	int getAsymmetricMinVAtWZ(int w, int z);

	int getAsymmetricMaxVAtWZ(int w, int z);

	int getAsymmetricMinVAtXY(int x, int y);

	int getAsymmetricMaxVAtXY(int x, int y);

	int getAsymmetricMinVAtXZ(int x, int z);

	int getAsymmetricMaxVAtXZ(int x, int z);

	int getAsymmetricMinVAtYZ(int y, int z);

	int getAsymmetricMaxVAtYZ(int y, int z);

	int getAsymmetricMinVAtWXY(int w, int x, int y);

	int getAsymmetricMaxVAtWXY(int w, int x, int y);

	int getAsymmetricMinVAtWXZ(int w, int x, int z);

	int getAsymmetricMaxVAtWXZ(int w, int x, int z);

	int getAsymmetricMinVAtWYZ(int w, int y, int z);

	int getAsymmetricMaxVAtWYZ(int w, int y, int z);

	int getAsymmetricMinVAtXYZ(int x, int y, int z);

	int getAsymmetricMaxVAtXYZ(int x, int y, int z);

	int getAsymmetricMinV(int w, int x, int y, int z);

	int getAsymmetricMaxV(int w, int x, int y, int z);

	int getAsymmetricMinW();

	int getAsymmetricMaxW();

	int getAsymmetricMinWAtV(int v);

	int getAsymmetricMaxWAtV(int v);

	int getAsymmetricMinWAtX(int x);

	int getAsymmetricMaxWAtX(int x);

	int getAsymmetricMinWAtY(int y);

	int getAsymmetricMaxWAtY(int y);

	int getAsymmetricMinWAtZ(int z);

	int getAsymmetricMaxWAtZ(int z);

	int getAsymmetricMinWAtVX(int v, int x);

	int getAsymmetricMaxWAtVX(int v, int x);

	int getAsymmetricMinWAtVY(int v, int y);

	int getAsymmetricMaxWAtVY(int v, int y);

	int getAsymmetricMinWAtVZ(int v, int z);

	int getAsymmetricMaxWAtVZ(int v, int z);

	int getAsymmetricMinWAtXY(int x, int y);

	int getAsymmetricMaxWAtXY(int x, int y);

	int getAsymmetricMinWAtXZ(int x, int z);

	int getAsymmetricMaxWAtXZ(int x, int z);

	int getAsymmetricMinWAtYZ(int y, int z);

	int getAsymmetricMaxWAtYZ(int y, int z);

	int getAsymmetricMinWAtVXY(int v, int x, int y);

	int getAsymmetricMaxWAtVXY(int v, int x, int y);

	int getAsymmetricMinWAtVXZ(int v, int x, int z);

	int getAsymmetricMaxWAtVXZ(int v, int x, int z);

	int getAsymmetricMinWAtVYZ(int v, int y, int z);

	int getAsymmetricMaxWAtVYZ(int v, int y, int z);

	int getAsymmetricMinWAtXYZ(int x, int y, int z);

	int getAsymmetricMaxWAtXYZ(int x, int y, int z);

	int getAsymmetricMinW(int v, int x, int y, int z);

	int getAsymmetricMaxW(int v, int x, int y, int z);

	int getAsymmetricMinX();

	int getAsymmetricMaxX();

	int getAsymmetricMinXAtV(int v);

	int getAsymmetricMaxXAtV(int v);

	int getAsymmetricMinXAtW(int w);

	int getAsymmetricMaxXAtW(int w);

	int getAsymmetricMinXAtY(int y);

	int getAsymmetricMaxXAtY(int y);

	int getAsymmetricMinXAtZ(int z);

	int getAsymmetricMaxXAtZ(int z);

	int getAsymmetricMinXAtVW(int v, int w);

	int getAsymmetricMaxXAtVW(int v, int w);

	int getAsymmetricMinXAtVY(int v, int y);

	int getAsymmetricMaxXAtVY(int v, int y);

	int getAsymmetricMinXAtVZ(int v, int z);

	int getAsymmetricMaxXAtVZ(int v, int z);

	int getAsymmetricMinXAtWY(int w, int y);

	int getAsymmetricMaxXAtWY(int w, int y);

	int getAsymmetricMinXAtWZ(int w, int z);

	int getAsymmetricMaxXAtWZ(int w, int z);

	int getAsymmetricMinXAtYZ(int y, int z);

	int getAsymmetricMaxXAtYZ(int y, int z);

	int getAsymmetricMinXAtVWY(int v, int w, int y);

	int getAsymmetricMaxXAtVWY(int v, int w, int y);

	int getAsymmetricMinXAtVWZ(int v, int w, int z);

	int getAsymmetricMaxXAtVWZ(int v, int w, int z);

	int getAsymmetricMinXAtVYZ(int v, int y, int z);

	int getAsymmetricMaxXAtVYZ(int v, int y, int z);

	int getAsymmetricMinXAtWYZ(int w, int y, int z);

	int getAsymmetricMaxXAtWYZ(int w, int y, int z);

	int getAsymmetricMinX(int v, int w, int y, int z);

	int getAsymmetricMaxX(int v, int w, int y, int z);

	int getAsymmetricMinY();

	int getAsymmetricMaxY();

	int getAsymmetricMinYAtV(int v);

	int getAsymmetricMaxYAtV(int v);

	int getAsymmetricMinYAtW(int w);

	int getAsymmetricMaxYAtW(int w);

	int getAsymmetricMinYAtX(int x);

	int getAsymmetricMaxYAtX(int x);

	int getAsymmetricMinYAtZ(int z);

	int getAsymmetricMaxYAtZ(int z);

	int getAsymmetricMinYAtVW(int v, int w);

	int getAsymmetricMaxYAtVW(int v, int w);

	int getAsymmetricMinYAtVX(int v, int x);

	int getAsymmetricMaxYAtVX(int v, int x);

	int getAsymmetricMinYAtVZ(int v, int z);

	int getAsymmetricMaxYAtVZ(int v, int z);

	int getAsymmetricMinYAtWX(int w, int x);

	int getAsymmetricMaxYAtWX(int w, int x);

	int getAsymmetricMinYAtWZ(int w, int z);

	int getAsymmetricMaxYAtWZ(int w, int z);

	int getAsymmetricMinYAtXZ(int x, int z);

	int getAsymmetricMaxYAtXZ(int x, int z);

	int getAsymmetricMinYAtVWX(int v, int w, int x);

	int getAsymmetricMaxYAtVWX(int v, int w, int x);

	int getAsymmetricMinYAtVWZ(int v, int w, int z);

	int getAsymmetricMaxYAtVWZ(int v, int w, int z);

	int getAsymmetricMinYAtVXZ(int v, int x, int z);

	int getAsymmetricMaxYAtVXZ(int v, int x, int z);

	int getAsymmetricMinYAtWXZ(int w, int x, int z);

	int getAsymmetricMaxYAtWXZ(int w, int x, int z);

	int getAsymmetricMinY(int v, int w, int x, int z);

	int getAsymmetricMaxY(int v, int w, int x, int z);

	int getAsymmetricMinZ();

	int getAsymmetricMaxZ();

	int getAsymmetricMinZAtV(int v);

	int getAsymmetricMaxZAtV(int v);

	int getAsymmetricMinZAtW(int w);

	int getAsymmetricMaxZAtW(int w);

	int getAsymmetricMinZAtX(int x);

	int getAsymmetricMaxZAtX(int x);

	int getAsymmetricMinZAtY(int y);

	int getAsymmetricMaxZAtY(int y);

	int getAsymmetricMinZAtVW(int v, int w);

	int getAsymmetricMaxZAtVW(int v, int w);

	int getAsymmetricMinZAtVX(int v, int x);

	int getAsymmetricMaxZAtVX(int v, int x);

	int getAsymmetricMinZAtVY(int v, int y);

	int getAsymmetricMaxZAtVY(int v, int y);

	int getAsymmetricMinZAtWX(int w, int x);

	int getAsymmetricMaxZAtWX(int w, int x);

	int getAsymmetricMinZAtWY(int w, int y);

	int getAsymmetricMaxZAtWY(int w, int y);

	int getAsymmetricMinZAtXY(int x, int y);

	int getAsymmetricMaxZAtXY(int x, int y);

	int getAsymmetricMinZAtVWX(int v, int w, int x);

	int getAsymmetricMaxZAtVWX(int v, int w, int x);

	int getAsymmetricMinZAtVWY(int v, int w, int y);

	int getAsymmetricMaxZAtVWY(int v, int w, int y);

	int getAsymmetricMinZAtVXY(int v, int x, int y);

	int getAsymmetricMaxZAtVXY(int v, int x, int y);

	int getAsymmetricMinZAtWXY(int w, int x, int y);

	int getAsymmetricMaxZAtWXY(int w, int x, int y);

	int getAsymmetricMinZ(int v, int w, int x, int y);

	int getAsymmetricMaxZ(int v, int w, int x, int y);
	
	@Override
	default int getAsymmetricMaxCoordinate(int axis) {
		switch (axis) {
		case 0: 
			return getAsymmetricMaxV();
		case 1: 
			return getAsymmetricMaxW();
		case 2: 
			return getAsymmetricMaxX();
		case 3: 
			return getAsymmetricMaxY();
		case 4: 
			return getAsymmetricMaxZ();
		default: throw new IllegalArgumentException("Axis must be 0, 1, 2, 3 or 4. Got " + axis + ".");
		}
	}

	@Override
	default int getAsymmetricMaxCoordinate(int axis, PartialCoordinates coordinates) {
		Integer v, w, x, y, z;
		switch (axis) {
		case 0:
			w = coordinates.get(1);	x = coordinates.get(2);	y = coordinates.get(3); z = coordinates.get(4);
			if (w == null) {
				if (x == null) {
					if (y == null) {
						if (z == null) {
							return getAsymmetricMaxV();
						} else {
							return getAsymmetricMaxVAtZ(z);
						}
					} else if (z == null) {
						return getAsymmetricMaxVAtY(y);
					} else {
						return getAsymmetricMaxVAtYZ(y, z);
					}
				} else if (y == null) {
					if (z == null) {
						return getAsymmetricMaxVAtX(x);
					} else {
						return getAsymmetricMaxVAtXZ(x, z);
					}
				} else if (z == null) {
					return getAsymmetricMaxVAtXY(x, y);
				} else {
					return getAsymmetricMaxVAtXYZ(x, y, z);
				}
			} else if (x == null) {
				if (y == null) {
					if (z == null) {
						return getAsymmetricMaxVAtW(w);
					} else {
						return getAsymmetricMaxVAtWZ(w, z);
					}
				} else if (z == null) {
					return getAsymmetricMaxVAtWY(w, y);
				} else {
					return getAsymmetricMaxVAtWYZ(w, y, z);
				}
			} else if (y == null) {
				if (z == null) {
					return getAsymmetricMaxVAtWX(w, x);
				} else {
					return getAsymmetricMaxVAtWXZ(w, x, z);
				}
			} else if (z == null) {
				return getAsymmetricMaxVAtWXY(w, x, y);
			} else {
				return getAsymmetricMaxV(w, x, y, z);
			}
		case 1:
			v = coordinates.get(0);	x = coordinates.get(2);	y = coordinates.get(3); z = coordinates.get(4);
			if (v == null) {
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
					return getAsymmetricMaxWAtXYZ(x, y, z);
				}
			} else if (x == null) {
				if (y == null) {
					if (z == null) {
						return getAsymmetricMaxWAtV(v);
					} else {
						return getAsymmetricMaxWAtVZ(v, z);
					}
				} else if (z == null) {
					return getAsymmetricMaxWAtVY(v, y);
				} else {
					return getAsymmetricMaxWAtVYZ(v, y, z);
				}
			} else if (y == null) {
				if (z == null) {
					return getAsymmetricMaxWAtVX(v, x);
				} else {
					return getAsymmetricMaxWAtVXZ(v, x, z);
				}
			} else if (z == null) {
				return getAsymmetricMaxWAtVXY(v, x, y);
			} else {
				return getAsymmetricMaxW(v, x, y, z);
			}
		case 2:
			v = coordinates.get(0);	w = coordinates.get(1);	y = coordinates.get(3); z = coordinates.get(4);
			if (v == null) {
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
					return getAsymmetricMaxXAtWYZ(w, y, z);
				}
			} else if (w == null) {
				if (y == null) {
					if (z == null) {
						return getAsymmetricMaxXAtV(v);
					} else {
						return getAsymmetricMaxXAtVZ(v, z);
					}
				} else if (z == null) {
					return getAsymmetricMaxXAtVY(v, y);
				} else {
					return getAsymmetricMaxXAtVYZ(v, y, z);
				}
			} else if (y == null) {
				if (z == null) {
					return getAsymmetricMaxXAtVW(v, w);
				} else {
					return getAsymmetricMaxXAtVWZ(v, w, z);
				}
			} else if (z == null) {
				return getAsymmetricMaxXAtVWY(v, w, y);
			} else {
				return getAsymmetricMaxX(v, w, y, z);
			}
		case 3:
			v = coordinates.get(0);	w = coordinates.get(1);	x = coordinates.get(2); z = coordinates.get(4);
			if (v == null) {
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
					return getAsymmetricMaxYAtWXZ(w, x, z);
				}
			} else if (w == null) {
				if (x == null) {
					if (z == null) {
						return getAsymmetricMaxYAtV(v);
					} else {
						return getAsymmetricMaxYAtVZ(v, z);
					}
				} else if (z == null) {
					return getAsymmetricMaxYAtVX(v, x);
				} else {
					return getAsymmetricMaxYAtVXZ(v, x, z);
				}
			} else if (x == null) {
				if (z == null) {
					return getAsymmetricMaxYAtVW(v, w);
				} else {
					return getAsymmetricMaxYAtVWZ(v, w, z);
				}
			} else if (z == null) {
				return getAsymmetricMaxYAtVWX(v, w, x);
			} else {
				return getAsymmetricMaxY(v, w, x, z);
			}
		case 4:
			v = coordinates.get(0);	w = coordinates.get(1);	x = coordinates.get(2); y = coordinates.get(3);
			if (v == null) {
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
					return getAsymmetricMaxZAtWXY(w, x, y);
				}
			} else if (w == null) {
				if (x == null) {
					if (y == null) {
						return getAsymmetricMaxZAtV(v);
					} else {
						return getAsymmetricMaxZAtVY(v, y);
					}
				} else if (y == null) {
					return getAsymmetricMaxZAtVX(v, x);
				} else {
					return getAsymmetricMaxZAtVXY(v, x, y);
				}
			} else if (x == null) {
				if (y == null) {
					return getAsymmetricMaxZAtVW(v, w);
				} else {
					return getAsymmetricMaxZAtVWY(v, w, y);
				}
			} else if (y == null) {
				return getAsymmetricMaxZAtVWX(v, w, x);
			} else {
				return getAsymmetricMaxZ(v, w, x, y);
			}
		default: throw new IllegalArgumentException("Axis must be 0, 1, 2, 3 or 4. Got " + axis + ".");
		}
	}
	
	@Override
	default int getAsymmetricMinCoordinate(int axis) {
		switch (axis) {
		case 0: 
			return getAsymmetricMinV();
		case 1: 
			return getAsymmetricMinW();
		case 2: 
			return getAsymmetricMinX();
		case 3: 
			return getAsymmetricMinY();
		case 4: 
			return getAsymmetricMinZ();
		default: throw new IllegalArgumentException("Axis must be 0, 1, 2, 3 or 4. Got " + axis + ".");
		}
	}

	@Override
	default int getAsymmetricMinCoordinate(int axis, PartialCoordinates coordinates) {
		Integer v, w, x, y, z;
		switch (axis) {
		case 0:
			w = coordinates.get(1);	x = coordinates.get(2);	y = coordinates.get(3); z = coordinates.get(4);
			if (w == null) {
				if (x == null) {
					if (y == null) {
						if (z == null) {
							return getAsymmetricMinV();
						} else {
							return getAsymmetricMinVAtZ(z);
						}
					} else if (z == null) {
						return getAsymmetricMinVAtY(y);
					} else {
						return getAsymmetricMinVAtYZ(y, z);
					}
				} else if (y == null) {
					if (z == null) {
						return getAsymmetricMinVAtX(x);
					} else {
						return getAsymmetricMinVAtXZ(x, z);
					}
				} else if (z == null) {
					return getAsymmetricMinVAtXY(x, y);
				} else {
					return getAsymmetricMinVAtXYZ(x, y, z);
				}
			} else if (x == null) {
				if (y == null) {
					if (z == null) {
						return getAsymmetricMinVAtW(w);
					} else {
						return getAsymmetricMinVAtWZ(w, z);
					}
				} else if (z == null) {
					return getAsymmetricMinVAtWY(w, y);
				} else {
					return getAsymmetricMinVAtWYZ(w, y, z);
				}
			} else if (y == null) {
				if (z == null) {
					return getAsymmetricMinVAtWX(w, x);
				} else {
					return getAsymmetricMinVAtWXZ(w, x, z);
				}
			} else if (z == null) {
				return getAsymmetricMinVAtWXY(w, x, y);
			} else {
				return getAsymmetricMinV(w, x, y, z);
			}
		case 1:
			v = coordinates.get(0);	x = coordinates.get(2);	y = coordinates.get(3); z = coordinates.get(4);
			if (v == null) {
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
					return getAsymmetricMinWAtXYZ(x, y, z);
				}
			} else if (x == null) {
				if (y == null) {
					if (z == null) {
						return getAsymmetricMinWAtV(v);
					} else {
						return getAsymmetricMinWAtVZ(v, z);
					}
				} else if (z == null) {
					return getAsymmetricMinWAtVY(v, y);
				} else {
					return getAsymmetricMinWAtVYZ(v, y, z);
				}
			} else if (y == null) {
				if (z == null) {
					return getAsymmetricMinWAtVX(v, x);
				} else {
					return getAsymmetricMinWAtVXZ(v, x, z);
				}
			} else if (z == null) {
				return getAsymmetricMinWAtVXY(v, x, y);
			} else {
				return getAsymmetricMinW(v, x, y, z);
			}
		case 2:
			v = coordinates.get(0);	w = coordinates.get(1);	y = coordinates.get(3); z = coordinates.get(4);
			if (v == null) {
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
					return getAsymmetricMinXAtWYZ(w, y, z);
				}
			} else if (w == null) {
				if (y == null) {
					if (z == null) {
						return getAsymmetricMinXAtV(v);
					} else {
						return getAsymmetricMinXAtVZ(v, z);
					}
				} else if (z == null) {
					return getAsymmetricMinXAtVY(v, y);
				} else {
					return getAsymmetricMinXAtVYZ(v, y, z);
				}
			} else if (y == null) {
				if (z == null) {
					return getAsymmetricMinXAtVW(v, w);
				} else {
					return getAsymmetricMinXAtVWZ(v, w, z);
				}
			} else if (z == null) {
				return getAsymmetricMinXAtVWY(v, w, y);
			} else {
				return getAsymmetricMinX(v, w, y, z);
			}
		case 3:
			v = coordinates.get(0);	w = coordinates.get(1);	x = coordinates.get(2); z = coordinates.get(4);
			if (v == null) {
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
					return getAsymmetricMinYAtWXZ(w, x, z);
				}
			} else if (w == null) {
				if (x == null) {
					if (z == null) {
						return getAsymmetricMinYAtV(v);
					} else {
						return getAsymmetricMinYAtVZ(v, z);
					}
				} else if (z == null) {
					return getAsymmetricMinYAtVX(v, x);
				} else {
					return getAsymmetricMinYAtVXZ(v, x, z);
				}
			} else if (x == null) {
				if (z == null) {
					return getAsymmetricMinYAtVW(v, w);
				} else {
					return getAsymmetricMinYAtVWZ(v, w, z);
				}
			} else if (z == null) {
				return getAsymmetricMinYAtVWX(v, w, x);
			} else {
				return getAsymmetricMinY(v, w, x, z);
			}
		case 4:
			v = coordinates.get(0);	w = coordinates.get(1);	x = coordinates.get(2); y = coordinates.get(3);
			if (v == null) {
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
					return getAsymmetricMinZAtWXY(w, x, y);
				}
			} else if (w == null) {
				if (x == null) {
					if (y == null) {
						return getAsymmetricMinZAtV(v);
					} else {
						return getAsymmetricMinZAtVY(v, y);
					}
				} else if (y == null) {
					return getAsymmetricMinZAtVX(v, x);
				} else {
					return getAsymmetricMinZAtVXY(v, x, y);
				}
			} else if (x == null) {
				if (y == null) {
					return getAsymmetricMinZAtVW(v, w);
				} else {
					return getAsymmetricMinZAtVWY(v, w, y);
				}
			} else if (y == null) {
				return getAsymmetricMinZAtVWX(v, w, x);
			} else {
				return getAsymmetricMinZ(v, w, x, y);
			}
		default: throw new IllegalArgumentException("Axis must be 0, 1, 2, 3 or 4. Got " + axis + ".");
		}
	}
	
	@Override
	default Grid5D asymmetricSection() {
		return new AsymmetricGridSection5D<SymmetricGrid5D>(this);
	}

}
