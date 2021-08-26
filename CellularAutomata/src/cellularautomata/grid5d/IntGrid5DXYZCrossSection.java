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
package cellularautomata.grid5d;

import cellularautomata.grid2d.IntGrid2D;

public class IntGrid5DXYZCrossSection<G extends IntGrid5D> extends Grid5DXYZCrossSection<G> implements IntGrid2D {

	public IntGrid5DXYZCrossSection(G source, int x, int y, int z) {
		super(source, x, y, z);
	}

	@Override
	public int getFromPosition(int x, int y) throws Exception {
		return source.getFromPosition(x, y, this.x, this.y, z);
	}

}
