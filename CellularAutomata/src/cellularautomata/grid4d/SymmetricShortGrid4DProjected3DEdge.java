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

import cellularautomata.grid3d.ShortGrid3D;

public class SymmetricShortGrid4DProjected3DEdge implements ShortGrid3D {

	private SymmetricShortGrid4D source;
	private short backgroundValue;
	
	public SymmetricShortGrid4DProjected3DEdge(SymmetricShortGrid4D source, short backgroundValue) {
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
	public short getValueAtPosition(int x, int y, int z) {
		int w = source.getNonSymmetricMaxW(), 
				minW = source.getNonSymmetricMinW();
		short value = source.getValueAtPosition(w, x, y, z);
		while (value == backgroundValue && w > minW) {
			w--;
			value = source.getValueAtNonSymmetricPosition(w, x, y, z);
		}
		return value;
	}
}
