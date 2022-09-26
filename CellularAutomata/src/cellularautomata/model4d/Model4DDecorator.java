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
import cellularautomata.model.ModelDecorator;
import cellularautomata.model3d.Model3D;

public class Model4DDecorator<Source_Type extends Model4D> extends ModelDecorator<Source_Type> implements Model4D {

	public Model4DDecorator(Source_Type source) {
		super(source);
	}
	
	@Override
	public String getWLabel() {
		return source.getWLabel();
	}
	
	@Override
	public String getXLabel() {
		return source.getXLabel();
	}
	
	@Override
	public String getYLabel() {
		return source.getYLabel();
	}
	
	@Override
	public String getZLabel() {
		return source.getZLabel();
	}

	@Override
	public int getMinW() { return source.getMinW(); }

	@Override
	public int getMaxW() { return source.getMaxW(); }

	@Override
	public int getMinWAtX(int x) { return source.getMinWAtX(x); }

	@Override
	public int getMaxWAtX(int x) { return source.getMaxWAtX(x); }

	@Override
	public int getMinWAtY(int y) { return source.getMinWAtY(y); }

	@Override
	public int getMaxWAtY(int y) { return source.getMaxWAtY(y); }

	@Override
	public int getMinWAtZ(int z) { return source.getMinWAtZ(z); }

	@Override
	public int getMaxWAtZ(int z) { return source.getMaxWAtZ(z); }

	@Override
	public int getMinWAtXY(int x, int y) { return source.getMinWAtXY(x, y); }

	@Override
	public int getMaxWAtXY(int x, int y) { return source.getMaxWAtXY(x, y); }

	@Override
	public int getMinWAtXZ(int x, int z) { return source.getMinWAtXZ(x, z); }

	@Override
	public int getMaxWAtXZ(int x, int z) { return source.getMaxWAtXZ(x, z); }

	@Override
	public int getMinWAtYZ(int y, int z) { return source.getMinWAtYZ(y, z); }

	@Override
	public int getMaxWAtYZ(int y, int z) { return source.getMaxWAtYZ(y, z); }

	@Override
	public int getMinW(int x, int y, int z) { return source.getMinW(x, y, z); }

	@Override
	public int getMaxW(int x, int y, int z) { return source.getMaxW(x, y, z); }

	@Override
	public int getMinX() { return source.getMinX(); }

	@Override
	public int getMaxX() { return source.getMaxX(); }

	@Override
	public int getMinXAtW(int w) { return source.getMinXAtW(w); }

	@Override
	public int getMaxXAtW(int w) { return source.getMaxXAtW(w); }

	@Override
	public int getMinXAtY(int y) { return source.getMinXAtY(y); }

	@Override
	public int getMaxXAtY(int y) { return source.getMaxXAtY(y); }

	@Override
	public int getMinXAtZ(int z) { return source.getMinXAtZ(z); }

	@Override
	public int getMaxXAtZ(int z) { return source.getMaxXAtZ(z); }

	@Override
	public int getMinXAtWY(int w, int y) { return source.getMinXAtWY(w, y); }

	@Override
	public int getMaxXAtWY(int w, int y) { return source.getMaxXAtWY(w, y); }

	@Override
	public int getMinXAtWZ(int w, int z) { return source.getMinXAtWZ(w, z); }

	@Override
	public int getMaxXAtWZ(int w, int z) { return source.getMaxXAtWZ(w, z); }

	@Override
	public int getMinXAtYZ(int y, int z) { return source.getMinXAtYZ(y, z); }

	@Override
	public int getMaxXAtYZ(int y, int z) { return source.getMaxXAtYZ(y, z); }

	@Override
	public int getMinX(int w, int y, int z) { return source.getMinX(w, y, z); }

	@Override
	public int getMaxX(int w, int y, int z) { return source.getMaxX(w, y, z); }

	@Override
	public int getMinY() { return source.getMinY(); }

	@Override
	public int getMaxY() { return source.getMaxY(); }

	@Override
	public int getMinYAtW(int w) { return source.getMinYAtW(w); }

	@Override
	public int getMaxYAtW(int w) { return source.getMaxYAtW(w); }

	@Override
	public int getMinYAtX(int x) { return source.getMinYAtX(x); }

	@Override
	public int getMaxYAtX(int x) { return source.getMaxYAtX(x); }

	@Override
	public int getMinYAtZ(int z) { return source.getMinYAtZ(z); }

	@Override
	public int getMaxYAtZ(int z) { return source.getMaxYAtZ(z); }

	@Override
	public int getMinYAtWX(int w, int x) { return source.getMinYAtWX(w, x); }

	@Override
	public int getMaxYAtWX(int w, int x) { return source.getMaxYAtWX(w, x); }

	@Override
	public int getMinYAtWZ(int w, int z) { return source.getMinYAtWZ(w, z); }

	@Override
	public int getMaxYAtWZ(int w, int z) { return source.getMaxYAtWZ(w, z); }

	@Override
	public int getMinYAtXZ(int x, int z) { return source.getMinYAtXZ(x, z); }

	@Override
	public int getMaxYAtXZ(int x, int z) { return source.getMaxYAtXZ(x, z); }

	@Override
	public int getMinY(int w, int x, int z) { return source.getMinY(w, x, z); }

	@Override
	public int getMaxY(int w, int x, int z) { return source.getMaxY(w, x, z); }

	@Override
	public int getMinZ() { return source.getMinZ(); }

	@Override
	public int getMaxZ() { return source.getMaxZ(); }

	@Override
	public int getMinZAtW(int w) { return source.getMinZAtW(w); }

	@Override
	public int getMaxZAtW(int w) { return source.getMaxZAtW(w); }

	@Override
	public int getMinZAtX(int x) { return source.getMinZAtX(x); }

	@Override
	public int getMaxZAtX(int x) { return source.getMaxZAtX(x); }

	@Override
	public int getMinZAtY(int y) { return source.getMinZAtY(y); }

	@Override
	public int getMaxZAtY(int y) { return source.getMaxZAtY(y); }

	@Override
	public int getMinZAtWX(int w, int x) { return source.getMinZAtWX(w, x); }

	@Override
	public int getMaxZAtWX(int w, int x) { return source.getMaxZAtWX(w, x); }

	@Override
	public int getMinZAtWY(int w, int y) { return source.getMinZAtWY(w, y); }

	@Override
	public int getMaxZAtWY(int w, int y) { return source.getMaxZAtWY(w, y); }

	@Override
	public int getMinZAtXY(int x, int y) { return source.getMinZAtXY(x, y); }

	@Override
	public int getMaxZAtXY(int x, int y) { return source.getMaxZAtXY(x, y); }

	@Override
	public int getMinZ(int w, int x, int y) { return source.getMinZ(w, x, y); }

	@Override
	public int getMaxZ(int w, int x, int y) { return source.getMaxZ(w, x, y); }
	
	@Override
	public Model4D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return source.subsection(minCoordinates, maxCoordinates);
	}

	@Override
	public Model4D subsection(Integer minW, Integer maxW, Integer minX, Integer maxX, Integer minY, Integer maxY, Integer minZ, Integer maxZ) {
		return source.subsection(minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	public Model3D crossSection(int axis, int coordinate) {
		return source.crossSection(axis, coordinate);
	}
	
	@Override
	public Model3D crossSectionAtW(int w) {
		return source.crossSectionAtW(w);
	}
	
	@Override
	public Model3D crossSectionAtX(int x) {
		return source.crossSectionAtX(x);
	}
	
	@Override
	public Model3D crossSectionAtY(int y) {
		return source.crossSectionAtY(y);
	}
	
	@Override
	public Model3D crossSectionAtZ(int z) {
		return source.crossSectionAtZ(z);
	}
	
	@Override
	public Model3D diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return source.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}
	
	@Override
	public Model3D diagonalCrossSectionOnWX(boolean positiveSlope, int xOffsetFromW) {
		return source.diagonalCrossSectionOnWX(positiveSlope, xOffsetFromW);
	}
	
	@Override
	public Model3D diagonalCrossSectionOnYZ(boolean positiveSlope, int zOffsetFromY) {
		return source.diagonalCrossSectionOnYZ(positiveSlope, zOffsetFromY);
	}

}
