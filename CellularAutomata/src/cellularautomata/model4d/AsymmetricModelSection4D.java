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
package cellularautomata.model4d;

import cellularautomata.PartialCoordinates;
import cellularautomata.model.AsymmetricModelSection;
import cellularautomata.model3d.Model3D;

public class AsymmetricModelSection4D<Source_Type extends SymmetricModel4D> extends AsymmetricModelSection<Source_Type> implements Model4D {
	
	public AsymmetricModelSection4D(Source_Type source) {
		super(source);
	}

	@Override
	public int getMinW() { return source.getAsymmetricMinW(); }

	@Override
	public int getMaxW() { return source.getAsymmetricMaxW(); }

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
	public int getMinW(int x, int y, int z) { return source.getAsymmetricMinW(x, y, z); }

	@Override
	public int getMaxW(int x, int y, int z) { return source.getAsymmetricMaxW(x, y, z); }

	@Override
	public int getMinX() { return source.getAsymmetricMinX(); }

	@Override
	public int getMaxX() { return source.getAsymmetricMaxX(); }

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
	public int getMinX(int w, int y, int z) { return source.getAsymmetricMinX(w, y, z); }

	@Override
	public int getMaxX(int w, int y, int z) { return source.getAsymmetricMaxX(w, y, z); }

	@Override
	public int getMinY() { return source.getAsymmetricMinY(); }

	@Override
	public int getMaxY() { return source.getAsymmetricMaxY(); }

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
	public int getMinY(int w, int x, int z) { return source.getAsymmetricMinY(w, x, z); }

	@Override
	public int getMaxY(int w, int x, int z) { return source.getAsymmetricMaxY(w, x, z); }

	@Override
	public int getMinZ() { return source.getAsymmetricMinZ(); }

	@Override
	public int getMaxZ() { return source.getAsymmetricMaxZ(); }

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
	public int getMinZ(int w, int x, int y) { return source.getAsymmetricMinZ(w, x, y); }

	@Override
	public int getMaxZ(int w, int x, int y) { return source.getAsymmetricMaxZ(w, x, y); }
	
	@Override
	public Model4D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return Model4D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	public Model3D crossSection(int axis, int coordinate) {
		return Model4D.super.crossSection(axis, coordinate);
	}

}
