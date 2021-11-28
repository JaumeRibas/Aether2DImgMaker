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
package cellularautomata.grid3d;

import cellularautomata.grid2d.Grid2D;

public class Grid3DXYDiagonalCrossSection<G extends Grid3D> implements Grid2D {

	protected G source;
	protected int yOffsetFromX;
	protected int crossSectionMinX;
	protected int crossSectionMaxX;
	protected int crossSectionMinZ;
	protected int crossSectionMaxZ;
	
	public Grid3DXYDiagonalCrossSection(G source, int yOffsetFromX) {		
		this.source = source;
		this.yOffsetFromX = yOffsetFromX;
		if (!getBounds()) {
			throw new IllegalArgumentException("Cross section is out of bounds.");
		}
	}
	
	protected boolean getBounds() {
		int x = source.getMinX();
		int maxX = source.getMaxX();
		int crossSectionY = x + yOffsetFromX;
		while (x <= maxX && (crossSectionY < source.getMinYAtX(x) || crossSectionY > source.getMaxYAtX(x))) {
			x++;
			crossSectionY++;
		}
		if (x <= maxX) {
			crossSectionMinX = x;
			crossSectionMaxX = x;
			crossSectionMaxZ = source.getMaxZ(x, crossSectionY);
			crossSectionMinZ = source.getMinZ(x, crossSectionY);
			x++;
			crossSectionY++;
			while (x <= maxX && crossSectionY >= source.getMinYAtX(x) && crossSectionY <= source.getMaxYAtX(x)) {
				crossSectionMaxX = x;
				int localMaxZ = source.getMaxZ(x, crossSectionY), localMinZ = source.getMinZ(x, crossSectionY);
				if (localMaxZ > crossSectionMaxZ) {
					crossSectionMaxZ = localMaxZ;
				}
				if (localMinZ < crossSectionMinZ) {
					crossSectionMinZ = localMinZ;
				}
				x++;
				crossSectionY++;
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int getMinX() {
		return crossSectionMinZ;
	}
	
	@Override
	public int getMinX(int y) {
		return source.getMinZ(y, y + yOffsetFromX);
	}
	
	@Override
	public int getMaxX() {
		return crossSectionMaxZ;
	}
	
	@Override
	public int getMaxX(int y) {
		return source.getMaxZ(y, y + yOffsetFromX);
	}
	
	@Override
	public int getMinY() {
		return crossSectionMinX;
	}
	
	@Override
	public int getMinY(int x) {
		for (int crossSectionX = crossSectionMinX, crossSectionY = crossSectionX + yOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionY++) {
			int localMaxZ = source.getMaxZ(crossSectionX, crossSectionY), localMinZ = source.getMinZ(crossSectionX, crossSectionY);
			if (x >= localMinZ && x <= localMaxZ) {
				return crossSectionX;
			}
		}
		throw new IllegalArgumentException("X coordinate out of bounds.");
	}
	
	@Override
	public int getMaxY() {
		return crossSectionMaxX;
	}
	
	@Override
	public int getMaxY(int x) {
		for (int crossSectionX = crossSectionMaxX, crossSectionY = crossSectionX + yOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionY--) {
			int localMaxZ = source.getMaxZ(crossSectionX, crossSectionY), localMinZ = source.getMinZ(crossSectionX, crossSectionY);
			if (x >= localMinZ && x <= localMaxZ) {
				return crossSectionX;
			}
		}
		throw new IllegalArgumentException("X coordinate out of bounds.");
	}

}
