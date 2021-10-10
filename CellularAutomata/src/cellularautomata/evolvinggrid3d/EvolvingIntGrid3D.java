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
package cellularautomata.evolvinggrid3d;

import cellularautomata.evolvinggrid.EvolvingIntGrid;
import cellularautomata.evolvinggrid2d.EvolvingIntGrid2D;
import cellularautomata.grid3d.IntGrid3D;

public interface EvolvingIntGrid3D extends IntGrid3D, EvolvingIntGrid {
	@Override
	default EvolvingIntGrid2D crossSectionAtZ(int z) {
		return new EvolvingIntGrid3DZCrossSection(this, z);
	}
	
	@Override
	default EvolvingIntGrid2D crossSectionAtX(int x) {
		return new EvolvingIntGrid3DXCrossSection(this, x);
	}
	
	@Override
	default EvolvingIntGrid3D subGrid(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new EvolvingIntSubGrid3D(this, minX, maxX, minY, maxY, minZ, maxZ);
	}
}