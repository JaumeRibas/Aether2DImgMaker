/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2020 Jaume Ribas

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

public class ShortGrid4DZCrossSection<G extends ShortGrid4D> extends Grid4DZCrossSection<G> implements ShortGrid3D {

	public ShortGrid4DZCrossSection(G source, int z) {
		super(source, z);
	}

	@Override
	public short getValueAtPosition(int x, int y, int z) throws Exception {
		return source.getValueAtPosition(x, y, z, this.z);
	}
}
