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

import cellularautomata.grid.Grid;

public interface Grid5D extends Grid {
	
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

//	default Grid2D crossSectionAtV(int v) {
//		return new Grid5DVCrossSection<Grid5D>(this, v);
//	}
//	
//	default Grid2D crossSectionAtW(int v) {
//		return new Grid5DWCrossSection<Grid5D>(this, w);
//	}
//	
//	default Grid2D crossSectionAtX(int v) {
//		return new Grid5DXCrossSection<Grid5D>(this, x);
//	}
//	
//	default Grid2D crossSectionAtY(int v) {
//		return new Grid5DYCrossSection<Grid5D>(this, y);
//	}
//	
//	default Grid2D crossSectionAtZ(int v) {
//		return new Grid5DZCrossSection<Grid5D>(this, z);
//	}

//	default Grid5D subsection(int minV, int maxV, int minW, int maxW, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
//		return new SubGrid5D<Grid5D>(this, minV, maxV, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
//	}
	
}
