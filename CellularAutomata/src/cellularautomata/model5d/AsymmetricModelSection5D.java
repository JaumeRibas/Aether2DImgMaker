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
package cellularautomata.model5d;

import cellularautomata.model.AsymmetricModelSection;

public class AsymmetricModelSection5D<G extends SymmetricModel5D> extends AsymmetricModelSection<G> implements Model5D {
	
	public AsymmetricModelSection5D(G source) {
		super(source);
	}

	@Override
	public int getMinV() { return source.getAsymmetricMinV(); }

	@Override
	public int getMaxV() { return source.getAsymmetricMaxV(); }

	@Override
	public int getMinVAtW(int w) { return source.getAsymmetricMinVAtW(w); }

	@Override
	public int getMaxVAtW(int w) { return source.getAsymmetricMaxVAtW(w); }

	@Override
	public int getMinVAtX(int x) { return source.getAsymmetricMinVAtX(x); }

	@Override
	public int getMaxVAtX(int x) { return source.getAsymmetricMaxVAtX(x); }

	@Override
	public int getMinVAtY(int y) { return source.getAsymmetricMinVAtY(y); }

	@Override
	public int getMaxVAtY(int y) { return source.getAsymmetricMaxVAtY(y); }

	@Override
	public int getMinVAtZ(int z) { return source.getAsymmetricMinVAtZ(z); }

	@Override
	public int getMaxVAtZ(int z) { return source.getAsymmetricMaxVAtZ(z); }

	@Override
	public int getMinVAtWX(int w, int x) { return source.getAsymmetricMinVAtWX(w, x); }

	@Override
	public int getMaxVAtWX(int w, int x) { return source.getAsymmetricMaxVAtWX(w, x); }

	@Override
	public int getMinVAtWY(int w, int y) { return source.getAsymmetricMinVAtWY(w, y); }

	@Override
	public int getMaxVAtWY(int w, int y) { return source.getAsymmetricMaxVAtWY(w, y); }

	@Override
	public int getMinVAtWZ(int w, int z) { return source.getAsymmetricMinVAtWZ(w, z); }

	@Override
	public int getMaxVAtWZ(int w, int z) { return source.getAsymmetricMaxVAtWZ(w, z); }

	@Override
	public int getMinVAtXY(int x, int y) { return source.getAsymmetricMinVAtXY(x, y); }

	@Override
	public int getMaxVAtXY(int x, int y) { return source.getAsymmetricMaxVAtXY(x, y); }

	@Override
	public int getMinVAtXZ(int x, int z) { return source.getAsymmetricMinVAtXZ(x, z); }

	@Override
	public int getMaxVAtXZ(int x, int z) { return source.getAsymmetricMaxVAtXZ(x, z); }

	@Override
	public int getMinVAtYZ(int y, int z) { return source.getAsymmetricMinVAtYZ(y, z); }

	@Override
	public int getMaxVAtYZ(int y, int z) { return source.getAsymmetricMaxVAtYZ(y, z); }

	@Override
	public int getMinVAtWXY(int w, int x, int y) { return source.getAsymmetricMinVAtWXY(w, x, y); }

	@Override
	public int getMaxVAtWXY(int w, int x, int y) { return source.getAsymmetricMaxVAtWXY(w, x, y); }

	@Override
	public int getMinVAtWXZ(int w, int x, int z) { return source.getAsymmetricMinVAtWXZ(w, x, z); }

	@Override
	public int getMaxVAtWXZ(int w, int x, int z) { return source.getAsymmetricMaxVAtWXZ(w, x, z); }

	@Override
	public int getMinVAtWYZ(int w, int y, int z) { return source.getAsymmetricMinVAtWYZ(w, y, z); }

	@Override
	public int getMaxVAtWYZ(int w, int y, int z) { return source.getAsymmetricMaxVAtWYZ(w, y, z); }

	@Override
	public int getMinVAtXYZ(int x, int y, int z) { return source.getAsymmetricMinVAtXYZ(x, y, z); }

	@Override
	public int getMaxVAtXYZ(int x, int y, int z) { return source.getAsymmetricMaxVAtXYZ(x, y, z); }

	@Override
	public int getMinV(int w, int x, int y, int z) { return source.getAsymmetricMinV(w, x, y, z); }

	@Override
	public int getMaxV(int w, int x, int y, int z) { return source.getAsymmetricMaxV(w, x, y, z); }

	@Override
	public int getMinW() { return source.getAsymmetricMinW(); }

	@Override
	public int getMaxW() { return source.getAsymmetricMaxW(); }

	@Override
	public int getMinWAtV(int v) { return source.getAsymmetricMinWAtV(v); }

	@Override
	public int getMaxWAtV(int v) { return source.getAsymmetricMaxWAtV(v); }

	@Override
	public int getMinWAtX(int x) { return source.getAsymmetricMinWAtX(x); }

	@Override
	public int getMaxWAtX(int x) { return source.getAsymmetricMaxWAtX(x); }

	@Override
	public int getMinWAtY(int y) { return source.getAsymmetricMinWAtY(y); }

	@Override
	public int getMaxWAtY(int y) { return source.getAsymmetricMaxWAtY(y); }

	@Override
	public int getMinWAtZ(int z) { return source.getAsymmetricMinWAtZ(z); }

	@Override
	public int getMaxWAtZ(int z) { return source.getAsymmetricMaxWAtZ(z); }

	@Override
	public int getMinWAtVX(int v, int x) { return source.getAsymmetricMinWAtVX(v, x); }

	@Override
	public int getMaxWAtVX(int v, int x) { return source.getAsymmetricMaxWAtVX(v, x); }

	@Override
	public int getMinWAtVY(int v, int y) { return source.getAsymmetricMinWAtVY(v, y); }

	@Override
	public int getMaxWAtVY(int v, int y) { return source.getAsymmetricMaxWAtVY(v, y); }

	@Override
	public int getMinWAtVZ(int v, int z) { return source.getAsymmetricMinWAtVZ(v, z); }

	@Override
	public int getMaxWAtVZ(int v, int z) { return source.getAsymmetricMaxWAtVZ(v, z); }

	@Override
	public int getMinWAtXY(int x, int y) { return source.getAsymmetricMinWAtXY(x, y); }

	@Override
	public int getMaxWAtXY(int x, int y) { return source.getAsymmetricMaxWAtXY(x, y); }

	@Override
	public int getMinWAtXZ(int x, int z) { return source.getAsymmetricMinWAtXZ(x, z); }

	@Override
	public int getMaxWAtXZ(int x, int z) { return source.getAsymmetricMaxWAtXZ(x, z); }

	@Override
	public int getMinWAtYZ(int y, int z) { return source.getAsymmetricMinWAtYZ(y, z); }

	@Override
	public int getMaxWAtYZ(int y, int z) { return source.getAsymmetricMaxWAtYZ(y, z); }

	@Override
	public int getMinWAtVXY(int v, int x, int y) { return source.getAsymmetricMinWAtVXY(v, x, y); }

	@Override
	public int getMaxWAtVXY(int v, int x, int y) { return source.getAsymmetricMaxWAtVXY(v, x, y); }

	@Override
	public int getMinWAtVXZ(int v, int x, int z) { return source.getAsymmetricMinWAtVXZ(v, x, z); }

	@Override
	public int getMaxWAtVXZ(int v, int x, int z) { return source.getAsymmetricMaxWAtVXZ(v, x, z); }

	@Override
	public int getMinWAtVYZ(int v, int y, int z) { return source.getAsymmetricMinWAtVYZ(v, y, z); }

	@Override
	public int getMaxWAtVYZ(int v, int y, int z) { return source.getAsymmetricMaxWAtVYZ(v, y, z); }

	@Override
	public int getMinWAtXYZ(int x, int y, int z) { return source.getAsymmetricMinWAtXYZ(x, y, z); }

	@Override
	public int getMaxWAtXYZ(int x, int y, int z) { return source.getAsymmetricMaxWAtXYZ(x, y, z); }

	@Override
	public int getMinW(int v, int x, int y, int z) { return source.getAsymmetricMinW(v, x, y, z); }

	@Override
	public int getMaxW(int v, int x, int y, int z) { return source.getAsymmetricMaxW(v, x, y, z); }

	@Override
	public int getMinX() { return source.getAsymmetricMinX(); }

	@Override
	public int getMaxX() { return source.getAsymmetricMaxX(); }

	@Override
	public int getMinXAtV(int v) { return source.getAsymmetricMinXAtV(v); }

	@Override
	public int getMaxXAtV(int v) { return source.getAsymmetricMaxXAtV(v); }

	@Override
	public int getMinXAtW(int w) { return source.getAsymmetricMinXAtW(w); }

	@Override
	public int getMaxXAtW(int w) { return source.getAsymmetricMaxXAtW(w); }

	@Override
	public int getMinXAtY(int y) { return source.getAsymmetricMinXAtY(y); }

	@Override
	public int getMaxXAtY(int y) { return source.getAsymmetricMaxXAtY(y); }

	@Override
	public int getMinXAtZ(int z) { return source.getAsymmetricMinXAtZ(z); }

	@Override
	public int getMaxXAtZ(int z) { return source.getAsymmetricMaxXAtZ(z); }

	@Override
	public int getMinXAtVW(int v, int w) { return source.getAsymmetricMinXAtVW(v, w); }

	@Override
	public int getMaxXAtVW(int v, int w) { return source.getAsymmetricMaxXAtVW(v, w); }

	@Override
	public int getMinXAtVY(int v, int y) { return source.getAsymmetricMinXAtVY(v, y); }

	@Override
	public int getMaxXAtVY(int v, int y) { return source.getAsymmetricMaxXAtVY(v, y); }

	@Override
	public int getMinXAtVZ(int v, int z) { return source.getAsymmetricMinXAtVZ(v, z); }

	@Override
	public int getMaxXAtVZ(int v, int z) { return source.getAsymmetricMaxXAtVZ(v, z); }

	@Override
	public int getMinXAtWY(int w, int y) { return source.getAsymmetricMinXAtWY(w, y); }

	@Override
	public int getMaxXAtWY(int w, int y) { return source.getAsymmetricMaxXAtWY(w, y); }

	@Override
	public int getMinXAtWZ(int w, int z) { return source.getAsymmetricMinXAtWZ(w, z); }

	@Override
	public int getMaxXAtWZ(int w, int z) { return source.getAsymmetricMaxXAtWZ(w, z); }

	@Override
	public int getMinXAtYZ(int y, int z) { return source.getAsymmetricMinXAtYZ(y, z); }

	@Override
	public int getMaxXAtYZ(int y, int z) { return source.getAsymmetricMaxXAtYZ(y, z); }

	@Override
	public int getMinXAtVWY(int v, int w, int y) { return source.getAsymmetricMinXAtVWY(v, w, y); }

	@Override
	public int getMaxXAtVWY(int v, int w, int y) { return source.getAsymmetricMaxXAtVWY(v, w, y); }

	@Override
	public int getMinXAtVWZ(int v, int w, int z) { return source.getAsymmetricMinXAtVWZ(v, w, z); }

	@Override
	public int getMaxXAtVWZ(int v, int w, int z) { return source.getAsymmetricMaxXAtVWZ(v, w, z); }

	@Override
	public int getMinXAtVYZ(int v, int y, int z) { return source.getAsymmetricMinXAtVYZ(v, y, z); }

	@Override
	public int getMaxXAtVYZ(int v, int y, int z) { return source.getAsymmetricMaxXAtVYZ(v, y, z); }

	@Override
	public int getMinXAtWYZ(int w, int y, int z) { return source.getAsymmetricMinXAtWYZ(w, y, z); }

	@Override
	public int getMaxXAtWYZ(int w, int y, int z) { return source.getAsymmetricMaxXAtWYZ(w, y, z); }

	@Override
	public int getMinX(int v, int w, int y, int z) { return source.getAsymmetricMinX(v, w, y, z); }

	@Override
	public int getMaxX(int v, int w, int y, int z) { return source.getAsymmetricMaxX(v, w, y, z); }

	@Override
	public int getMinY() { return source.getAsymmetricMinY(); }

	@Override
	public int getMaxY() { return source.getAsymmetricMaxY(); }

	@Override
	public int getMinYAtV(int v) { return source.getAsymmetricMinYAtV(v); }

	@Override
	public int getMaxYAtV(int v) { return source.getAsymmetricMaxYAtV(v); }

	@Override
	public int getMinYAtW(int w) { return source.getAsymmetricMinYAtW(w); }

	@Override
	public int getMaxYAtW(int w) { return source.getAsymmetricMaxYAtW(w); }

	@Override
	public int getMinYAtX(int x) { return source.getAsymmetricMinYAtX(x); }

	@Override
	public int getMaxYAtX(int x) { return source.getAsymmetricMaxYAtX(x); }

	@Override
	public int getMinYAtZ(int z) { return source.getAsymmetricMinYAtZ(z); }

	@Override
	public int getMaxYAtZ(int z) { return source.getAsymmetricMaxYAtZ(z); }

	@Override
	public int getMinYAtVW(int v, int w) { return source.getAsymmetricMinYAtVW(v, w); }

	@Override
	public int getMaxYAtVW(int v, int w) { return source.getAsymmetricMaxYAtVW(v, w); }

	@Override
	public int getMinYAtVX(int v, int x) { return source.getAsymmetricMinYAtVX(v, x); }

	@Override
	public int getMaxYAtVX(int v, int x) { return source.getAsymmetricMaxYAtVX(v, x); }

	@Override
	public int getMinYAtVZ(int v, int z) { return source.getAsymmetricMinYAtVZ(v, z); }

	@Override
	public int getMaxYAtVZ(int v, int z) { return source.getAsymmetricMaxYAtVZ(v, z); }

	@Override
	public int getMinYAtWX(int w, int x) { return source.getAsymmetricMinYAtWX(w, x); }

	@Override
	public int getMaxYAtWX(int w, int x) { return source.getAsymmetricMaxYAtWX(w, x); }

	@Override
	public int getMinYAtWZ(int w, int z) { return source.getAsymmetricMinYAtWZ(w, z); }

	@Override
	public int getMaxYAtWZ(int w, int z) { return source.getAsymmetricMaxYAtWZ(w, z); }

	@Override
	public int getMinYAtXZ(int x, int z) { return source.getAsymmetricMinYAtXZ(x, z); }

	@Override
	public int getMaxYAtXZ(int x, int z) { return source.getAsymmetricMaxYAtXZ(x, z); }

	@Override
	public int getMinYAtVWX(int v, int w, int x) { return source.getAsymmetricMinYAtVWX(v, w, x); }

	@Override
	public int getMaxYAtVWX(int v, int w, int x) { return source.getAsymmetricMaxYAtVWX(v, w, x); }

	@Override
	public int getMinYAtVWZ(int v, int w, int z) { return source.getAsymmetricMinYAtVWZ(v, w, z); }

	@Override
	public int getMaxYAtVWZ(int v, int w, int z) { return source.getAsymmetricMaxYAtVWZ(v, w, z); }

	@Override
	public int getMinYAtVXZ(int v, int x, int z) { return source.getAsymmetricMinYAtVXZ(v, x, z); }

	@Override
	public int getMaxYAtVXZ(int v, int x, int z) { return source.getAsymmetricMaxYAtVXZ(v, x, z); }

	@Override
	public int getMinYAtWXZ(int w, int x, int z) { return source.getAsymmetricMinYAtWXZ(w, x, z); }

	@Override
	public int getMaxYAtWXZ(int w, int x, int z) { return source.getAsymmetricMaxYAtWXZ(w, x, z); }

	@Override
	public int getMinY(int v, int w, int x, int z) { return source.getAsymmetricMinY(v, w, x, z); }

	@Override
	public int getMaxY(int v, int w, int x, int z) { return source.getAsymmetricMaxY(v, w, x, z); }

	@Override
	public int getMinZ() { return source.getAsymmetricMinZ(); }

	@Override
	public int getMaxZ() { return source.getAsymmetricMaxZ(); }

	@Override
	public int getMinZAtV(int v) { return source.getAsymmetricMinZAtV(v); }

	@Override
	public int getMaxZAtV(int v) { return source.getAsymmetricMaxZAtV(v); }

	@Override
	public int getMinZAtW(int w) { return source.getAsymmetricMinZAtW(w); }

	@Override
	public int getMaxZAtW(int w) { return source.getAsymmetricMaxZAtW(w); }

	@Override
	public int getMinZAtX(int x) { return source.getAsymmetricMinZAtX(x); }

	@Override
	public int getMaxZAtX(int x) { return source.getAsymmetricMaxZAtX(x); }

	@Override
	public int getMinZAtY(int y) { return source.getAsymmetricMinZAtY(y); }

	@Override
	public int getMaxZAtY(int y) { return source.getAsymmetricMaxZAtY(y); }

	@Override
	public int getMinZAtVW(int v, int w) { return source.getAsymmetricMinZAtVW(v, w); }

	@Override
	public int getMaxZAtVW(int v, int w) { return source.getAsymmetricMaxZAtVW(v, w); }

	@Override
	public int getMinZAtVX(int v, int x) { return source.getAsymmetricMinZAtVX(v, x); }

	@Override
	public int getMaxZAtVX(int v, int x) { return source.getAsymmetricMaxZAtVX(v, x); }

	@Override
	public int getMinZAtVY(int v, int y) { return source.getAsymmetricMinZAtVY(v, y); }

	@Override
	public int getMaxZAtVY(int v, int y) { return source.getAsymmetricMaxZAtVY(v, y); }

	@Override
	public int getMinZAtWX(int w, int x) { return source.getAsymmetricMinZAtWX(w, x); }

	@Override
	public int getMaxZAtWX(int w, int x) { return source.getAsymmetricMaxZAtWX(w, x); }

	@Override
	public int getMinZAtWY(int w, int y) { return source.getAsymmetricMinZAtWY(w, y); }

	@Override
	public int getMaxZAtWY(int w, int y) { return source.getAsymmetricMaxZAtWY(w, y); }

	@Override
	public int getMinZAtXY(int x, int y) { return source.getAsymmetricMinZAtXY(x, y); }

	@Override
	public int getMaxZAtXY(int x, int y) { return source.getAsymmetricMaxZAtXY(x, y); }

	@Override
	public int getMinZAtVWX(int v, int w, int x) { return source.getAsymmetricMinZAtVWX(v, w, x); }

	@Override
	public int getMaxZAtVWX(int v, int w, int x) { return source.getAsymmetricMaxZAtVWX(v, w, x); }

	@Override
	public int getMinZAtVWY(int v, int w, int y) { return source.getAsymmetricMinZAtVWY(v, w, y); }

	@Override
	public int getMaxZAtVWY(int v, int w, int y) { return source.getAsymmetricMaxZAtVWY(v, w, y); }

	@Override
	public int getMinZAtVXY(int v, int x, int y) { return source.getAsymmetricMinZAtVXY(v, x, y); }

	@Override
	public int getMaxZAtVXY(int v, int x, int y) { return source.getAsymmetricMaxZAtVXY(v, x, y); }

	@Override
	public int getMinZAtWXY(int w, int x, int y) { return source.getAsymmetricMinZAtWXY(w, x, y); }

	@Override
	public int getMaxZAtWXY(int w, int x, int y) { return source.getAsymmetricMaxZAtWXY(w, x, y); }

	@Override
	public int getMinZ(int v, int w, int x, int y) { return source.getAsymmetricMinZ(v, w, x, y); }

	@Override
	public int getMaxZ(int v, int w, int x, int y) { return source.getAsymmetricMaxZ(v, w, x, y); }

}
