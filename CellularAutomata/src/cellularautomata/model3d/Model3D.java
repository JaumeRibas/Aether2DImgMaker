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
package cellularautomata.model3d;

import cellularautomata.grid3d.Grid3D;
import cellularautomata.model.Model;
import cellularautomata.model2d.Model2D;

public interface Model3D extends Grid3D, Model {
	
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
	default Model3D subsection(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new SubModel3D<Model3D>(this, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default Model2D crossSectionAtX(int x) {
		return new Model3DXCrossSection<Model3D>(this, x);
	}
	
	@Override
	default Model2D crossSectionAtY(int y) {
		return new Model3DYCrossSection<Model3D>(this, y);
	}
	
	@Override
	default Model2D crossSectionAtZ(int z) {
		return new Model3DZCrossSection<Model3D>(this, z);
	}
	
	@Override
	default Model2D diagonalCrossSectionOnXY(int yOffsetFromX) {
		return new Model3DXYDiagonalCrossSection<Model3D>(this, yOffsetFromX);
	}
	
	@Override
	default Model2D diagonalCrossSectionOnXZ(int zOffsetFromX) {
		return new Model3DXZDiagonalCrossSection<Model3D>(this, zOffsetFromX);
	}
	
	@Override
	default Model2D diagonalCrossSectionOnYZ(int zOffsetFromY) {
		return new Model3DYZDiagonalCrossSection<Model3D>(this, zOffsetFromY);
	}
}
