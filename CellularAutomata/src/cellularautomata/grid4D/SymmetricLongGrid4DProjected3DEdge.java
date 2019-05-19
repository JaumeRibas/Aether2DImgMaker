/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2018 Jaume Ribas

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

import cellularautomata.grid3D.LongGrid3D;

public class SymmetricLongGrid4DProjected3DEdge implements LongGrid3D {

	private SymmetricLongGrid4D source;
	private long backgroundValue;
	
	public SymmetricLongGrid4DProjected3DEdge(SymmetricLongGrid4D source, long backgroundValue) {
		this.source = source;
		this.backgroundValue = backgroundValue;
	}

	@Override
	public int getMinX() {
		return source.getNonSymmetricMinX();
	}

	@Override
	public int getMaxX() {
		return source.getNonSymmetricMaxX();
	}

	@Override
	public int getMinY() {
		return source.getNonSymmetricMinY();
	}

	@Override
	public int getMaxY() {
		return source.getNonSymmetricMaxY();
	}
	
	@Override
	public int getMinZ() {
		return source.getNonSymmetricMinZ();
	}

	@Override
	public int getMaxZ() {
		return source.getNonSymmetricMaxZ();
	}
	
	@Override
	public long getValueAtPosition(int x, int y, int z) {
		int w = source.getNonSymmetricMaxW(), 
				minW = source.getNonSymmetricMinW();
		long value = source.getValueAtPosition(w, x, y, z);
		while (value == backgroundValue && w > minW) {
			w--;
			value = source.getValueAtNonSymmetricPosition(w, x, y, z);
		}
		return value;
	}
}
