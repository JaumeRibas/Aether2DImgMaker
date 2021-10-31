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

import cellularautomata.grid3d.IntGrid3D;
import cellularautomata.model.IntModel;
import cellularautomata.model2d.IntModel2D;

public interface IntModel3D extends IntGrid3D, IntModel, Model3D {
	
	@Override
	default IntModel3D subsection(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new IntSubModel3D(this, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default IntModel2D crossSectionAtX(int x) {
		return new IntModel3DXCrossSection(this, x);
	}
	
	@Override
	default IntModel2D crossSectionAtY(int y) {
		return new IntModel3DYCrossSection(this, y);
	}
	
	@Override
	default IntModel2D crossSectionAtZ(int z) {
		return new IntModel3DZCrossSection(this, z);
	}
	
	@Override
	default IntModel2D diagonalCrossSectionOnXY(int yOffsetFromX) {
		return new IntModel3DXYDiagonalCrossSection(this, yOffsetFromX);
	}
	
	@Override
	default IntModel2D diagonalCrossSectionOnXZ(int zOffsetFromX) {
		return new IntModel3DXZDiagonalCrossSection(this, zOffsetFromX);
	}
	
	@Override
	default IntModel2D diagonalCrossSectionOnYZ(int zOffsetFromY) {
		return new IntModel3DYZDiagonalCrossSection(this, zOffsetFromY);
	}
	
}
