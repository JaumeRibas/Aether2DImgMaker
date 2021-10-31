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
package cellularautomata.grid3d;

import cellularautomata.grid2d.Grid2D;

public class Grid3DXCrossSection<G extends Grid3D> implements Grid2D {

	protected G source;
	protected int x;
	
	public Grid3DXCrossSection(G source, int x) {
		if (x > source.getMaxX() || x < source.getMinX()) {
			throw new IllegalArgumentException("X coordinate is out of bounds.");
		}
		this.source = source;
		this.x = x;
	}

	@Override
	public int getMinX() {
		return source.getMinZAtX(x);
	}
	
	@Override
	public int getMinX(int y) {
		return source.getMinZ(x, y);
	}
	
	@Override
	public int getMaxX() {
		return source.getMaxZAtX(x);
	}
	
	@Override
	public int getMaxX(int y) {
		return source.getMaxZ(x, y);
	}
	
	@Override
	public int getMinY() {
		return source.getMinYAtX(x);
	}
	
	@Override
	public int getMinY(int x) {
		return source.getMinY(this.x, x);
	}
	
	@Override
	public int getMaxY() {
		return source.getMaxYAtX(x);
	}
	
	@Override
	public int getMaxY(int x) {
		return source.getMaxY(this.x, x);
	}

}
