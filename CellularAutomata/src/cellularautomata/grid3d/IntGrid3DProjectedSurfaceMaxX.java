/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2019 Jaume Ribas

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

import cellularautomata.grid2d.IntGrid2D;

public class IntGrid3DProjectedSurfaceMaxX implements IntGrid2D {

	private IntGrid3D source;
	
	public IntGrid3DProjectedSurfaceMaxX(IntGrid3D source) {
		this.source = source;
	}

	@Override
	public int getMinX() {
		return source.getMinZ();
	}

	@Override
	public int getMaxX() {
		return source.getMaxZ();
	}
	
	@Override
	public int getMinX(int y) {
		return source.getMinZAtY(y);
	}

	@Override
	public int getMaxX(int y) {
		return source.getMaxZAtY(y);
	}

	@Override
	public int getMinY() {
		return source.getMinY();
	}

	@Override
	public int getMaxY() {
		return source.getMaxY();
	}
	
	@Override
	public int getMinY(int x) {
		return source.getMinYAtZ(x);
	}

	@Override
	public int getMaxY(int x) {
		return source.getMaxYAtZ(x);
	}

	@Override
	public int getValueAtPosition(int x, int y) throws Exception {
		int sourceX, sourceY, sourceZ;
		sourceY = y;
		sourceZ = x;
		sourceX = source.getMaxX(sourceY, sourceZ);
		return source.getValueAtPosition(sourceX, sourceY, sourceZ);
		/*//TODO move to overridden getMaxX(int y, int z) at CA implementation
		while (getValueAtPosition(maxX, y, z) == backgroundValue && maxX > minX) {
			maxX--;
		}*/
	}
}
