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

import cellularautomata.grid4d.LongGrid4D;
import cellularautomata.model.LongModel;
import cellularautomata.model3d.LongModel3D;

public interface LongModel4D extends LongGrid4D, LongModel, Model4D {
	
	@Override
	default LongModel4D subsection(int minW, int maxW, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new LongSubModel4D(this, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default LongModel3D crossSectionAtW(int w) {
		return new LongModel4DWCrossSection(this, w);
	}
	
	@Override
	default LongModel3D crossSectionAtX(int x) {
		return new LongModel4DXCrossSection(this, x);
	}
	
	@Override
	default LongModel3D crossSectionAtY(int y) {
		return new LongModel4DYCrossSection(this, y);
	}
	
	@Override
	default LongModel3D crossSectionAtZ(int z) {
		return new LongModel4DZCrossSection(this, z);
	}
	
	@Override
	default LongModel3D diagonalCrossSectionOnWX(int xOffsetFromW) {
		return new LongModel4DWXDiagonalCrossSection(this, xOffsetFromW);
	}
	
}
