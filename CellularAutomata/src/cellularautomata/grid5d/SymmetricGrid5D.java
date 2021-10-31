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
	default Grid5D asymmetricSection() {
		return new AsymmetricGridSection5D<SymmetricGrid5D>(this);
	}

}
