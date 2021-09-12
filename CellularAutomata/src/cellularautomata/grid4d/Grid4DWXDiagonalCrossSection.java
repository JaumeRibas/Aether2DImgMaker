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
package cellularautomata.grid4d;

import cellularautomata.grid3d.Grid3D;

public class Grid4DWXDiagonalCrossSection<G extends Grid4D> implements Grid3D {

	protected G source;
	protected int xOffsetFromW;
	protected int crossSectionMinW;
	protected int crossSectionMaxW;
	protected int crossSectionMinY;
	protected int crossSectionMaxY;
	protected int crossSectionMinZ;
	protected int crossSectionMaxZ;
	
	/*
	 * w -> x
	 * y -> y
	 * z -> z
	 * */
	
	public Grid4DWXDiagonalCrossSection(G source, int xOffsetFromW) {		
		this.source = source;
		this.xOffsetFromW = xOffsetFromW;
		if (!getBounds()) {
			throw new IllegalArgumentException("Cross section outside of grid bounds.");
		}
	}
	
	protected boolean getBounds() {
		boolean isCrossing = false;
		int w = source.getMinW();
		int maxW = source.getMaxW();
		for (; w <= maxW && !isCrossing; w++) {
			int crossSectionX = w + xOffsetFromW;
			int localMaxX = source.getMaxXAtW(w), localMinX = source.getMinXAtW(w);
			if (crossSectionX >= localMinX && crossSectionX <= localMaxX) {
				isCrossing = true;
				crossSectionMinW = w;
				crossSectionMaxW = w;
				crossSectionMaxY = source.getMaxYAtWX(w, crossSectionX);
				crossSectionMinY = source.getMinYAtWX(w, crossSectionX);
				crossSectionMaxZ = source.getMaxZAtWX(w, crossSectionX);
				crossSectionMinZ = source.getMinZAtWX(w, crossSectionX);
			}
		}
		for (; w <= maxW; w++) {
			int crossSectionX = w + xOffsetFromW;
			int localMaxX = source.getMaxXAtW(w), localMinX = source.getMinXAtW(w);
			if (crossSectionX >= localMinX && crossSectionX <= localMaxX) {
				crossSectionMaxW = w;
				int localMaxY = source.getMaxYAtWX(w, crossSectionX), localMinY = source.getMinYAtWX(w, crossSectionX);
				if (localMaxY > crossSectionMaxY) {
					crossSectionMaxY = localMaxY;
				}
				if (localMinY < crossSectionMinY) {
					crossSectionMinY = localMinY;
				}
				int localMaxZ = source.getMaxZAtWX(w, crossSectionX), localMinZ = source.getMinZAtWX(w, crossSectionX);
				if (localMaxZ > crossSectionMaxZ) {
					crossSectionMaxZ = localMaxZ;
				}
				if (localMinZ < crossSectionMinZ) {
					crossSectionMinZ = localMinZ;
				}
			}
		}
		return isCrossing;
	}
	
	@Override
	public int getMinX() { 
		return crossSectionMinW; 
	}

	@Override
	public int getMaxX() { 
		return crossSectionMaxW; 
	}

	@Override
	public int getMinXAtY(int y) { 
		for (int crossSectionW = crossSectionMinW, crossSectionX = crossSectionW + xOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionX++) {
			int localMaxY = source.getMaxYAtWX(crossSectionW, crossSectionX), localMinY = source.getMinYAtWX(crossSectionW, crossSectionX);
			if (y >= localMinY && y <= localMaxY) {
				return crossSectionW;
			}
		}
		throw new IllegalArgumentException("Y coordinate outside of bounds.");
	}

	@Override
	public int getMaxXAtY(int y) { 
		for (int crossSectionW = crossSectionMaxW, crossSectionX = crossSectionW + xOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionX--) {
			int localMaxY = source.getMaxYAtWX(crossSectionW, crossSectionX), localMinY = source.getMinYAtWX(crossSectionW, crossSectionX);
			if (y >= localMinY && y <= localMaxY) {
				return crossSectionW;
			}
		}
		throw new IllegalArgumentException("Y coordinate outside of bounds.");
	}

	@Override
	public int getMinXAtZ(int z) { 
		for (int crossSectionW = crossSectionMinW, crossSectionX = crossSectionW + xOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionX++) {
			int localMaxZ = source.getMaxZAtWX(crossSectionW, crossSectionX), localMinZ = source.getMinZAtWX(crossSectionW, crossSectionX);
			if (z >= localMinZ && z <= localMaxZ) {
				return crossSectionW;
			}
		}
		throw new IllegalArgumentException("Z coordinate outside of bounds.");
	}

	@Override
	public int getMaxXAtZ(int z) {
		for (int crossSectionW = crossSectionMaxW, crossSectionX = crossSectionW + xOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionX--) {
			int localMaxZ = source.getMaxZAtWX(crossSectionW, crossSectionX), localMinZ = source.getMinZAtWX(crossSectionW, crossSectionX);
			if (z >= localMinZ && z <= localMaxZ) {
				return crossSectionW;
			}
		}
		throw new IllegalArgumentException("Z coordinate outside of bounds.");
	}

	@Override
	public int getMinX(int y, int z) { 
		for (int crossSectionW = crossSectionMinW, crossSectionX = crossSectionW + xOffsetFromW; crossSectionW <= crossSectionMaxW; crossSectionW++, crossSectionX++) {
			int localMaxY = source.getMaxYAtWX(crossSectionW, crossSectionX), localMinY = source.getMinYAtWX(crossSectionW, crossSectionX);
			if (y >= localMinY && y <= localMaxY) {
				int localMaxZ = source.getMaxZ(crossSectionW, crossSectionX, y), localMinZ = source.getMinZ(crossSectionW, crossSectionX, y);
				if (z >= localMinZ && z <= localMaxZ) {
					return crossSectionW;
				}
			}
		}
		throw new IllegalArgumentException("Coordinates outside of bounds.");
	}

	@Override
	public int getMaxX(int y, int z) { 
		for (int crossSectionW = crossSectionMaxW, crossSectionX = crossSectionW + xOffsetFromW; crossSectionW >= crossSectionMinW; crossSectionW--, crossSectionX--) {
			int localMaxY = source.getMaxYAtWX(crossSectionW, crossSectionX), localMinY = source.getMinYAtWX(crossSectionW, crossSectionX);
			if (y >= localMinY && y <= localMaxY) {
				int localMaxZ = source.getMaxZ(crossSectionW, crossSectionX, y), localMinZ = source.getMinZ(crossSectionW, crossSectionX, y);
				if (z >= localMinZ && z <= localMaxZ) {
					return crossSectionW;
				}
			}
		}
		throw new IllegalArgumentException("Coordinates outside of bounds.");
	}

	@Override
	public int getMinY() { 
		return crossSectionMinY;
	}

	@Override
	public int getMaxY() { 
		return crossSectionMaxY;
	}

	@Override
	public int getMinYAtX(int x) { 
		return source.getMinYAtWX(x, x + xOffsetFromW);
	}

	@Override
	public int getMaxYAtX(int x) { 
		return source.getMaxYAtWX(x, x + xOffsetFromW);
	}

	@Override
	public int getMinYAtZ(int z) { 
		return source.getMinYAtZ(z);
	}

	@Override
	public int getMaxYAtZ(int z) { 
		return source.getMaxYAtZ(z);
	}

	@Override
	public int getMinY(int x, int z) { 
		return source.getMinY(x, x + xOffsetFromW, z);
	}

	@Override
	public int getMaxY(int x, int z) {
		return source.getMaxY(x, x + xOffsetFromW, z);
	}

	@Override
	public int getMinZ() {
		return crossSectionMinZ;
	}

	@Override
	public int getMaxZ() { 
		return crossSectionMaxZ;
	}

	@Override
	public int getMinZAtX(int x) {
		return source.getMinZAtWX(x, x + xOffsetFromW);
	}

	@Override
	public int getMaxZAtX(int x) {
		return source.getMaxZAtWX(x, x + xOffsetFromW);
	}

	@Override
	public int getMinZAtY(int y) { 
		return source.getMinZAtY(y); 
	}

	@Override
	public int getMaxZAtY(int y) {
		return source.getMaxZAtY(y); 
	}

	@Override
	public int getMinZ(int x, int y) {
		return source.getMinZ(x, x + xOffsetFromW, y);
	}

	@Override
	public int getMaxZ(int x, int y) {
		return source.getMaxZ(x, x + xOffsetFromW, y);
	}

}
