/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2022 Jaume Ribas

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
package cellularautomata.model3d;

public class ObjectSubModel3D<T, G extends ObjectModel3D<T>> extends SubModel3D<G> implements ObjectModel3D<T> {

	public ObjectSubModel3D(G source, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		super(source, minX, maxX, minY, maxY, minZ, maxZ);
	}

	@Override
	public T getFromPosition(int x, int y, int z) throws Exception {
		return source.getFromPosition(x, y, z);
	}

}
