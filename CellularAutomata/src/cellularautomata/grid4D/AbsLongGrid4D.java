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
package cellularautomata.grid4D;

public class AbsLongGrid4D implements LongGrid4D {
	
	private LongGrid4D source;
	
	public AbsLongGrid4D(LongGrid4D source) {
		this.source = source;
	}

	@Override
	public long getValueAtPosition(int w, int x, int y, int z) {
		return Math.abs(source.getValueAtPosition(w, x, y, z));
	}
	
	@Override
	public int getMinW() {
		return source.getMinW();
	}

	@Override
	public int getMaxW() {
		return source.getMaxW();
	}

	@Override
	public int getMinX() {
		return source.getMinX();
	}

	@Override
	public int getMaxX() {
		return source.getMaxX();
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
	public int getMinZ() {
		return source.getMinZ();
	}

	@Override
	public int getMaxZ() {
		return source.getMaxZ();
	}

}
