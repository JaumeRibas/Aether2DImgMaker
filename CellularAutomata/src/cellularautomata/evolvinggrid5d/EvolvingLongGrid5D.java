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
package cellularautomata.evolvinggrid5d;

import cellularautomata.evolvinggrid.EvolvingLongGrid;
import cellularautomata.evolvinggrid2d.EvolvingLongGrid2D;
import cellularautomata.evolvinggrid3d.EvolvingLongGrid3D;
import cellularautomata.grid5d.LongGrid5D;

public interface EvolvingLongGrid5D extends LongGrid5D, EvolvingLongGrid {
	
	@Override
	default EvolvingLongGrid2D crossSectionAtXYZ(int x, int y, int z) {
		return new EvolvingLongGrid5DXYZCrossSection(this, x, y, z);
	}
	
	@Override
	default EvolvingLongGrid3D crossSectionAtYZ(int y, int z) {
		return new EvolvingLongGrid5DYZCrossSection(this, y, z);
	}
}
