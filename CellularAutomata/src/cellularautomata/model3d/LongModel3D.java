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

import cellularautomata.grid3d.LongGrid3D;
import cellularautomata.model.LongModel;
import cellularautomata.model2d.LongModel2D;

public interface LongModel3D extends LongGrid3D, LongModel, Model3D {
	
	@Override
	default LongModel2D crossSectionAtX(int x) {
		return new LongModel3DXCrossSection(this, x);
	}
	
	@Override
	default LongModel2D crossSectionAtY(int y) {
		return new LongModel3DYCrossSection(this, y);
	}
	
	@Override
	default LongModel2D crossSectionAtZ(int z) {
		return new LongModel3DZCrossSection(this, z);
	}
	
	@Override
	default LongModel2D diagonalCrossSectionOnXY(int yOffsetFromX) {
		return new LongModel3DXYDiagonalCrossSection(this, yOffsetFromX);
	}
	
	@Override
	default LongModel2D diagonalCrossSectionOnXZ(int zOffsetFromX) {
		return new LongModel3DXZDiagonalCrossSection(this, zOffsetFromX);
	}
	
	@Override
	default LongModel2D diagonalCrossSectionOnYZ(int zOffsetFromY) {
		return new LongModel3DYZDiagonalCrossSection(this, zOffsetFromY);
	}
	
	@Override
	default LongModel3D subsection(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new LongSubModel3D(this, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
}
