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
package cellularautomata.grid4d;

import cellularautomata.grid2d.LongGrid2D;

public class LongGrid4DYZCrossSection implements LongGrid2D {

	private LongGrid4D source;
	private int y;
	private int z;
	
	public LongGrid4DYZCrossSection(LongGrid4D source, int y, int z) {
		this.source = source;
		this.y = y;
		this.z = z;
	}

	@Override
	public int getMinX() {
		return source.getMinW();
	}

	@Override
	public int getMaxX() {
		return source.getMaxW();
	}

	@Override
	public int getMinY() {
		return source.getMinX();
	}

	@Override
	public int getMaxY() {
		return source.getMaxX();
	}

	@Override
	public long getValueAtPosition(int x, int y) throws Exception {
		return source.getValueAtPosition(x, y, this.y, z);
	}

}
