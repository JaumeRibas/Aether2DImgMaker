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

import cellularautomata.grid2d.Grid2D;

public class Grid5DXYZCrossSection<G extends Grid5D> implements Grid2D {

	protected G source;
	protected int x;
	protected int y;
	protected int z;
	
	public Grid5DXYZCrossSection(G source, int x, int y, int z) {
		if (z > source.getMaxZ() || z < source.getMinZ()) {
			throw new IllegalArgumentException("Z coordinate outside of grid bounds.");
		} else if (y > source.getMaxYAtZ(z) || y < source.getMinYAtZ(z)) {
			throw new IllegalArgumentException("Y coordinate outside of grid bounds.");
		} else if (x > source.getMaxXAtYZ(y, z) || x < source.getMinXAtYZ(y, z)) {
			throw new IllegalArgumentException("X coordinate outside of grid bounds.");
		}
		this.source = source;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	//x -> v
	//y -> w

	@Override
	public int getMinX() {
		return source.getMinVAtXYZ(x, y, z);
	}

	@Override
	public int getMaxX() {
		return source.getMaxVAtXYZ(x, y, z);
	}

	@Override
	public int getMinX(int y) {
		return source.getMinV(y, x, this.y, z);
	}

	@Override
	public int getMaxX(int y) {
		return source.getMaxV(y, x, this.y, z);
	}

	@Override
	public int getMinY() {
		return source.getMinWAtXYZ(x, y, z);
	}

	@Override
	public int getMaxY() {
		return source.getMaxWAtXYZ(x, y, z);
	}

	@Override
	public int getMinY(int x) {
		return source.getMinW(x, this.x, y, z);
	}

	@Override
	public int getMaxY(int x) {
		return source.getMaxW(x, this.x, y, z);
	}

}
