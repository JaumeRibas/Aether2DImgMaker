/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2023 Jaume Ribas

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

import cellularautomata.PartialCoordinates;
import cellularautomata.model.ModelDecorator;
import cellularautomata.model4d.Model4D;

public class Model5DDecorator<Source_Type extends Model5D> extends ModelDecorator<Source_Type> implements Model5D {

	public Model5DDecorator(Source_Type source) {
		super(source);
	}
	
	@Override
	public String getVLabel() {
		return source.getVLabel();
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
	public int getMinV() { return source.getMinV(); }

	@Override
	public int getMaxV() { return source.getMaxV(); }

	@Override
	public int getMinVAtW(int w) { return source.getMinVAtW(w); }

	@Override
	public int getMaxVAtW(int w) { return source.getMaxVAtW(w); }

	@Override
	public int getMinVAtX(int x) { return source.getMinVAtX(x); }

	@Override
	public int getMaxVAtX(int x) { return source.getMaxVAtX(x); }

	@Override
	public int getMinVAtY(int y) { return source.getMinVAtY(y); }

	@Override
	public int getMaxVAtY(int y) { return source.getMaxVAtY(y); }

	@Override
	public int getMinVAtZ(int z) { return source.getMinVAtZ(z); }

	@Override
	public int getMaxVAtZ(int z) { return source.getMaxVAtZ(z); }

	@Override
	public int getMinVAtWX(int w, int x) { return source.getMinVAtWX(w, x); }

	@Override
	public int getMaxVAtWX(int w, int x) { return source.getMaxVAtWX(w, x); }

	@Override
	public int getMinVAtWY(int w, int y) { return source.getMinVAtWY(w, y); }

	@Override
	public int getMaxVAtWY(int w, int y) { return source.getMaxVAtWY(w, y); }

	@Override
	public int getMinVAtWZ(int w, int z) { return source.getMinVAtWZ(w, z); }

	@Override
	public int getMaxVAtWZ(int w, int z) { return source.getMaxVAtWZ(w, z); }

	@Override
	public int getMinVAtXY(int x, int y) { return source.getMinVAtXY(x, y); }

	@Override
	public int getMaxVAtXY(int x, int y) { return source.getMaxVAtXY(x, y); }

	@Override
	public int getMinVAtXZ(int x, int z) { return source.getMinVAtXZ(x, z); }

	@Override
	public int getMaxVAtXZ(int x, int z) { return source.getMaxVAtXZ(x, z); }

	@Override
	public int getMinVAtYZ(int y, int z) { return source.getMinVAtYZ(y, z); }

	@Override
	public int getMaxVAtYZ(int y, int z) { return source.getMaxVAtYZ(y, z); }

	@Override
	public int getMinVAtWXY(int w, int x, int y) { return source.getMinVAtWXY(w, x, y); }

	@Override
	public int getMaxVAtWXY(int w, int x, int y) { return source.getMaxVAtWXY(w, x, y); }

	@Override
	public int getMinVAtWXZ(int w, int x, int z) { return source.getMinVAtWXZ(w, x, z); }

	@Override
	public int getMaxVAtWXZ(int w, int x, int z) { return source.getMaxVAtWXZ(w, x, z); }

	@Override
	public int getMinVAtWYZ(int w, int y, int z) { return source.getMinVAtWYZ(w, y, z); }

	@Override
	public int getMaxVAtWYZ(int w, int y, int z) { return source.getMaxVAtWYZ(w, y, z); }

	@Override
	public int getMinVAtXYZ(int x, int y, int z) { return source.getMinVAtXYZ(x, y, z); }

	@Override
	public int getMaxVAtXYZ(int x, int y, int z) { return source.getMaxVAtXYZ(x, y, z); }

	@Override
	public int getMinV(int w, int x, int y, int z) { return source.getMinV(w, x, y, z); }

	@Override
	public int getMaxV(int w, int x, int y, int z) { return source.getMaxV(w, x, y, z); }

	@Override
	public int getMinW() { return source.getMinW(); }

	@Override
	public int getMaxW() { return source.getMaxW(); }

	@Override
	public int getMinWAtV(int v) { return source.getMinWAtV(v); }

	@Override
	public int getMaxWAtV(int v) { return source.getMaxWAtV(v); }

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
	public int getMinWAtVX(int v, int x) { return source.getMinWAtVX(v, x); }

	@Override
	public int getMaxWAtVX(int v, int x) { return source.getMaxWAtVX(v, x); }

	@Override
	public int getMinWAtVY(int v, int y) { return source.getMinWAtVY(v, y); }

	@Override
	public int getMaxWAtVY(int v, int y) { return source.getMaxWAtVY(v, y); }

	@Override
	public int getMinWAtVZ(int v, int z) { return source.getMinWAtVZ(v, z); }

	@Override
	public int getMaxWAtVZ(int v, int z) { return source.getMaxWAtVZ(v, z); }

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
	public int getMinWAtVXY(int v, int x, int y) { return source.getMinWAtVXY(v, x, y); }

	@Override
	public int getMaxWAtVXY(int v, int x, int y) { return source.getMaxWAtVXY(v, x, y); }

	@Override
	public int getMinWAtVXZ(int v, int x, int z) { return source.getMinWAtVXZ(v, x, z); }

	@Override
	public int getMaxWAtVXZ(int v, int x, int z) { return source.getMaxWAtVXZ(v, x, z); }

	@Override
	public int getMinWAtVYZ(int v, int y, int z) { return source.getMinWAtVYZ(v, y, z); }

	@Override
	public int getMaxWAtVYZ(int v, int y, int z) { return source.getMaxWAtVYZ(v, y, z); }

	@Override
	public int getMinWAtXYZ(int x, int y, int z) { return source.getMinWAtXYZ(x, y, z); }

	@Override
	public int getMaxWAtXYZ(int x, int y, int z) { return source.getMaxWAtXYZ(x, y, z); }

	@Override
	public int getMinW(int v, int x, int y, int z) { return source.getMinW(v, x, y, z); }

	@Override
	public int getMaxW(int v, int x, int y, int z) { return source.getMaxW(v, x, y, z); }

	@Override
	public int getMinX() { return source.getMinX(); }

	@Override
	public int getMaxX() { return source.getMaxX(); }

	@Override
	public int getMinXAtV(int v) { return source.getMinXAtV(v); }

	@Override
	public int getMaxXAtV(int v) { return source.getMaxXAtV(v); }

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
	public int getMinXAtVW(int v, int w) { return source.getMinXAtVW(v, w); }

	@Override
	public int getMaxXAtVW(int v, int w) { return source.getMaxXAtVW(v, w); }

	@Override
	public int getMinXAtVY(int v, int y) { return source.getMinXAtVY(v, y); }

	@Override
	public int getMaxXAtVY(int v, int y) { return source.getMaxXAtVY(v, y); }

	@Override
	public int getMinXAtVZ(int v, int z) { return source.getMinXAtVZ(v, z); }

	@Override
	public int getMaxXAtVZ(int v, int z) { return source.getMaxXAtVZ(v, z); }

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
	public int getMinXAtVWY(int v, int w, int y) { return source.getMinXAtVWY(v, w, y); }

	@Override
	public int getMaxXAtVWY(int v, int w, int y) { return source.getMaxXAtVWY(v, w, y); }

	@Override
	public int getMinXAtVWZ(int v, int w, int z) { return source.getMinXAtVWZ(v, w, z); }

	@Override
	public int getMaxXAtVWZ(int v, int w, int z) { return source.getMaxXAtVWZ(v, w, z); }

	@Override
	public int getMinXAtVYZ(int v, int y, int z) { return source.getMinXAtVYZ(v, y, z); }

	@Override
	public int getMaxXAtVYZ(int v, int y, int z) { return source.getMaxXAtVYZ(v, y, z); }

	@Override
	public int getMinXAtWYZ(int w, int y, int z) { return source.getMinXAtWYZ(w, y, z); }

	@Override
	public int getMaxXAtWYZ(int w, int y, int z) { return source.getMaxXAtWYZ(w, y, z); }

	@Override
	public int getMinX(int v, int w, int y, int z) { return source.getMinX(v, w, y, z); }

	@Override
	public int getMaxX(int v, int w, int y, int z) { return source.getMaxX(v, w, y, z); }

	@Override
	public int getMinY() { return source.getMinY(); }

	@Override
	public int getMaxY() { return source.getMaxY(); }

	@Override
	public int getMinYAtV(int v) { return source.getMinYAtV(v); }

	@Override
	public int getMaxYAtV(int v) { return source.getMaxYAtV(v); }

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
	public int getMinYAtVW(int v, int w) { return source.getMinYAtVW(v, w); }

	@Override
	public int getMaxYAtVW(int v, int w) { return source.getMaxYAtVW(v, w); }

	@Override
	public int getMinYAtVX(int v, int x) { return source.getMinYAtVX(v, x); }

	@Override
	public int getMaxYAtVX(int v, int x) { return source.getMaxYAtVX(v, x); }

	@Override
	public int getMinYAtVZ(int v, int z) { return source.getMinYAtVZ(v, z); }

	@Override
	public int getMaxYAtVZ(int v, int z) { return source.getMaxYAtVZ(v, z); }

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
	public int getMinYAtVWX(int v, int w, int x) { return source.getMinYAtVWX(v, w, x); }

	@Override
	public int getMaxYAtVWX(int v, int w, int x) { return source.getMaxYAtVWX(v, w, x); }

	@Override
	public int getMinYAtVWZ(int v, int w, int z) { return source.getMinYAtVWZ(v, w, z); }

	@Override
	public int getMaxYAtVWZ(int v, int w, int z) { return source.getMaxYAtVWZ(v, w, z); }

	@Override
	public int getMinYAtVXZ(int v, int x, int z) { return source.getMinYAtVXZ(v, x, z); }

	@Override
	public int getMaxYAtVXZ(int v, int x, int z) { return source.getMaxYAtVXZ(v, x, z); }

	@Override
	public int getMinYAtWXZ(int w, int x, int z) { return source.getMinYAtWXZ(w, x, z); }

	@Override
	public int getMaxYAtWXZ(int w, int x, int z) { return source.getMaxYAtWXZ(w, x, z); }

	@Override
	public int getMinY(int v, int w, int x, int z) { return source.getMinY(v, w, x, z); }

	@Override
	public int getMaxY(int v, int w, int x, int z) { return source.getMaxY(v, w, x, z); }

	@Override
	public int getMinZ() { return source.getMinZ(); }

	@Override
	public int getMaxZ() { return source.getMaxZ(); }

	@Override
	public int getMinZAtV(int v) { return source.getMinZAtV(v); }

	@Override
	public int getMaxZAtV(int v) { return source.getMaxZAtV(v); }

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
	public int getMinZAtVW(int v, int w) { return source.getMinZAtVW(v, w); }

	@Override
	public int getMaxZAtVW(int v, int w) { return source.getMaxZAtVW(v, w); }

	@Override
	public int getMinZAtVX(int v, int x) { return source.getMinZAtVX(v, x); }

	@Override
	public int getMaxZAtVX(int v, int x) { return source.getMaxZAtVX(v, x); }

	@Override
	public int getMinZAtVY(int v, int y) { return source.getMinZAtVY(v, y); }

	@Override
	public int getMaxZAtVY(int v, int y) { return source.getMaxZAtVY(v, y); }

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
	public int getMinZAtVWX(int v, int w, int x) { return source.getMinZAtVWX(v, w, x); }

	@Override
	public int getMaxZAtVWX(int v, int w, int x) { return source.getMaxZAtVWX(v, w, x); }

	@Override
	public int getMinZAtVWY(int v, int w, int y) { return source.getMinZAtVWY(v, w, y); }

	@Override
	public int getMaxZAtVWY(int v, int w, int y) { return source.getMaxZAtVWY(v, w, y); }

	@Override
	public int getMinZAtVXY(int v, int x, int y) { return source.getMinZAtVXY(v, x, y); }

	@Override
	public int getMaxZAtVXY(int v, int x, int y) { return source.getMaxZAtVXY(v, x, y); }

	@Override
	public int getMinZAtWXY(int w, int x, int y) { return source.getMinZAtWXY(w, x, y); }

	@Override
	public int getMaxZAtWXY(int w, int x, int y) { return source.getMaxZAtWXY(w, x, y); }

	@Override
	public int getMinZ(int v, int w, int x, int y) { return source.getMinZ(v, w, x, y); }

	@Override
	public int getMaxZ(int v, int w, int x, int y) { return source.getMaxZ(v, w, x, y); }
	
	@Override
	public Model5D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return source.subsection(minCoordinates, maxCoordinates);
	}

	@Override
	public Model5D subsection(Integer minV, Integer maxV, Integer minW, Integer maxW, Integer minX, Integer maxX, Integer minY, Integer maxY, Integer minZ, Integer maxZ) {
		return source.subsection(minV, maxV, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	public Model4D crossSection(int axis, int coordinate) {
		return source.crossSection(axis, coordinate);
	}

	@Override
	public Model4D crossSectionAtV(int v) {
		return source.crossSectionAtV(v);
	}
//	
//	@Override
//	public Model4D crossSectionAtW(int w) {
//		return source.crossSectionAtW(w);
//	}
//	
//	@Override
//	public Model4D crossSectionAtX(int x) {
//		return source.crossSectionAtX(x);
//	}
//	
//	@Override
//	public Model4D crossSectionAtY(int y) {
//		return source.crossSectionAtY(y);
//	}
//	
//	@Override
//	public Model4D crossSectionAtZ(int z) {
//		return source.crossSectionAtZ(z);
//	}
	
	@Override
	public Model4D diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return source.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}
	
	@Override
	public Model4D diagonalCrossSectionOnVW(boolean positiveSlope, int wOffsetFromV) {
		return source.diagonalCrossSectionOnVW(positiveSlope, wOffsetFromV);
	}

}
