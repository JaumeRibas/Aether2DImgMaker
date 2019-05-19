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
package cellularautomata.grid3D;

import cellularautomata.grid2D.LongGrid2D;

public class LongGrid3DCrossSection implements LongGrid2D {

	private LongGrid3D source;
	private int z;
	
	public LongGrid3DCrossSection(LongGrid3D source, int z) {
		this.source = source;
		this.z = z;
	}

	@Override
	public int getMinX() {
		return source.getMinXAtZ(z);
	}
	
	@Override
	public int getMinX(int y) {
		return source.getMinX(y, z);
	}
	
	@Override
	public int getMaxX() {
		return source.getMaxXAtZ(z);
	}
	
	@Override
	public int getMaxX(int y) {
		return source.getMaxX(y, z);
	}
	
	@Override
	public int getMinY() {
		return source.getMinYAtZ(z);
	}
	
	@Override
	public int getMinY(int x) {
		return source.getMinY(x, z);
	}
	
	@Override
	public int getMaxY() {
		return source.getMaxYAtZ(z);
	}
	
	@Override
	public int getMaxY(int x) {
		return source.getMaxY(x, z);
	}

	@Override
	public long getValueAtPosition(int x, int y) throws Exception {
		return source.getValueAtPosition(x, y, z);
	}

}
