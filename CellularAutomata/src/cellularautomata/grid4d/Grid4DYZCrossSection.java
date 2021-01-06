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
package cellularautomata.grid4d;

import cellularautomata.grid2d.Grid2D;

public class Grid4DYZCrossSection<G extends Grid4D> implements Grid2D {

	protected G source;
	protected int y;
	protected int z;
	
	public Grid4DYZCrossSection(G source, int y, int z) {
		this.source = source;
		this.y = y;
		this.z = z;
	}

	@Override
	public int getMinX() {
		return source.getMinWAtYZ(y, z);
	}

	@Override
	public int getMaxX() {
		return source.getMaxWAtYZ(y, z);
	}

	@Override
	public int getMinY() {
		return source.getMinXAtYZ(y, z);
	}

	@Override
	public int getMaxY() {
		return source.getMaxXAtYZ(y, z);
	}

}
