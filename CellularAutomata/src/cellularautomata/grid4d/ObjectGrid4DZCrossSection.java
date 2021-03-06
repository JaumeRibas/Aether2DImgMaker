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

import cellularautomata.grid3d.ObjectGrid3D;

public class ObjectGrid4DZCrossSection<T, G extends ObjectGrid4D<T>> extends Grid4DZCrossSection<G> implements ObjectGrid3D<T> {

	public ObjectGrid4DZCrossSection(G source, int z) {
		super(source, z);
	}

	@Override
	public T getFromPosition(int x, int y, int z) throws Exception {
		return source.getFromPosition(x, y, z, this.z);
	}
}
