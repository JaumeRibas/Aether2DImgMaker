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

import cellularautomata.grid4d.IntGrid4D;
import cellularautomata.model.IntModel;
import cellularautomata.model3d.IntModel3D;

public interface IntModel4D extends IntGrid4D, IntModel, Model4D {
	
	@Override
	default IntModel4D subsection(int minW, int maxW, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new IntSubModel4D(this, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default IntModel3D crossSectionAtW(int w) {
		return new IntModel4DWCrossSection(this, w);
	}
	
	@Override
	default IntModel3D crossSectionAtX(int x) {
		return new IntModel4DXCrossSection(this, x);
	}
	
	@Override
	default IntModel3D crossSectionAtY(int y) {
		return new IntModel4DYCrossSection(this, y);
	}
	
	@Override
	default IntModel3D crossSectionAtZ(int z) {
		return new IntModel4DZCrossSection(this, z);
	}
	
	@Override
	default IntModel3D diagonalCrossSectionOnWX(int xOffsetFromW) {
		return new IntModel4DWXDiagonalCrossSection(this, xOffsetFromW);
	}
}
