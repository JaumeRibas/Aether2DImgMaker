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

public class Grid3DXZDiagonalCrossSection<G extends Grid3D> implements Grid2D {

	protected G source;
	protected int zOffsetFromX;
	protected int crossSectionMinX;
	protected int crossSectionMaxX;
	protected int crossSectionMinY;
	protected int crossSectionMaxY;
	
	public Grid3DXZDiagonalCrossSection(G source, int zOffsetFromX) {		
		this.source = source;
		this.zOffsetFromX = zOffsetFromX;
		if (!getBounds()) {
			throw new IllegalArgumentException("Cross section is out of bounds.");
		}
	}
	
	protected boolean getBounds() {
		boolean isCrossing = false;
		int x = source.getMinX();
		int maxX = source.getMaxX();
		for (; x <= maxX && !isCrossing; x++) {
			int crossSectionZ = x + zOffsetFromX;
			int localMaxZ = source.getMaxZAtX(x), localMinZ = source.getMinZAtX(x);
			if (crossSectionZ >= localMinZ && crossSectionZ <= localMaxZ) {
				isCrossing = true;
				crossSectionMinX = x;
				crossSectionMaxX = x;
				crossSectionMaxY = source.getMaxY(x, crossSectionZ);
				crossSectionMinY = source.getMinY(x, crossSectionZ);
			}
		}
		for (; x <= maxX; x++) {
			int crossSectionZ = x + zOffsetFromX;
			int localMaxZ = source.getMaxZAtX(x), localMinZ = source.getMinZAtX(x);
			if (crossSectionZ >= localMinZ && crossSectionZ <= localMaxZ) {
				crossSectionMaxX = x;
				int localMaxY = source.getMaxY(x, crossSectionZ), localMinY = source.getMinY(x, crossSectionZ);
				if (localMaxY > crossSectionMaxY) {
					crossSectionMaxY = localMaxY;
				}
				if (localMinY < crossSectionMinY) {
					crossSectionMinY = localMinY;
				}
			}
		}
		return isCrossing;
	}
	
	@Override
	public int getMinX() {
		return crossSectionMinX;
	}
	
	@Override
	public int getMinX(int y) {
		for (int crossSectionX = crossSectionMinX, crossSectionZ = crossSectionX + zOffsetFromX; crossSectionX <= crossSectionMaxX; crossSectionX++, crossSectionZ++) {
			int localMaxY = source.getMaxY(crossSectionX, crossSectionZ), localMinY = source.getMinY(crossSectionX, crossSectionZ);
			if (y >= localMinY && y <= localMaxY) {
				return crossSectionX;
			}
		}
		throw new IllegalArgumentException("Y coordinate out of bounds.");
	}
	
	@Override
	public int getMaxX() {
		return crossSectionMaxX;
	}
	
	@Override
	public int getMaxX(int y) {
		for (int crossSectionX = crossSectionMaxX, crossSectionZ = crossSectionX + zOffsetFromX; crossSectionX >= crossSectionMinX; crossSectionX--, crossSectionZ--) {
			int localMaxY = source.getMaxY(crossSectionX, crossSectionZ), localMinY = source.getMinY(crossSectionX, crossSectionZ);
			if (y >= localMinY && y <= localMaxY) {
				return crossSectionX;
			}
		}
		throw new IllegalArgumentException("Y coordinate out of bounds.");
	}
	
	@Override
	public int getMinY() {
		return crossSectionMinY;
	}
	
	@Override
	public int getMinY(int x) {
		return source.getMinY(x, x + zOffsetFromX);
	}
	
	@Override
	public int getMaxY() {
		return crossSectionMaxY;
	}
	
	@Override
	public int getMaxY(int x) {
		return source.getMaxY(x, x + zOffsetFromX);
	}

}
