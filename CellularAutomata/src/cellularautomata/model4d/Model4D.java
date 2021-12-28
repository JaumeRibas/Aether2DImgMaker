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
package cellularautomata.model4d;

import cellularautomata.grid4d.Grid4D;
import cellularautomata.model.Model;
import cellularautomata.model3d.Model3D;

public interface Model4D extends Grid4D, Model {
	
	default String getWLabel() {
		return "w";
	}
	
	default String getXLabel() {
		return "x";
	}
	
	default String getYLabel() {
		return "y";
	}
	
	default String getZLabel() {
		return "z";
	}
	
	@Override
	default Model4D subsection(int minW, int maxW, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new SubModel4D<Model4D>(this, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default Model3D crossSectionAtW(int w) {
		return new Model4DWCrossSection<Model4D>(this, w);
	}
	
	@Override
	default Model3D crossSectionAtX(int x) {
		return new Model4DXCrossSection<Model4D>(this, x);
	}
	
	@Override
	default Model3D crossSectionAtY(int y) {
		return new Model4DYCrossSection<Model4D>(this, y);
	}
	
	@Override
	default Model3D crossSectionAtZ(int z) {
		return new Model4DZCrossSection<Model4D>(this, z);
	}
	
	@Override
	default Model3D diagonalCrossSectionOnWX(int xOffsetFromW) {
		return new Model4DWXDiagonalCrossSection<Model4D>(this, xOffsetFromW);
	}
	
	@Override
	default Model3D diagonalCrossSectionOnYZ(int zOffsetFromY) {
		return new Model4DYZDiagonalCrossSection<Model4D>(this, zOffsetFromY);
	}
	
}
