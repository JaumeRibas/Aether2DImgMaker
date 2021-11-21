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

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class Grid3DIterator<G extends Grid3D, T> implements Iterator<T> {
	
	protected G grid;
	protected int x;
	protected int y;
	protected int z;
	
	public Grid3DIterator(G grid) {
		this.grid = grid;
		this.x = grid.getMinX();
		this.y = grid.getMinYAtX(x);
		this.z = grid.getMinZ(x, y);
	}

	@Override
	public boolean hasNext() {
		return x <= grid.getMaxX();
	}

	@Override
	public T next() {
		if (x > grid.getMaxX())
			throw new NoSuchElementException();
		T next = null;
		try {
			next = getFromGridPosition(x, y, z);
		} catch (Exception e) {
			throw new NoSuchElementException(e.getMessage());//what should be done here?
		}
		if (z < grid.getMaxZ(x, y)) {
			z++;
		} else {
			if (y < grid.getMaxYAtX(x)) {
				y++;
				z = grid.getMinZ(x, y);
			} else {
				x++;
				if (x <= grid.getMaxX()) {
					y = grid.getMinYAtX(x);
					z = grid.getMinZ(x, y);
				}
			}
		}
		return next;
	}
	
	protected abstract T getFromGridPosition(int x, int y, int z) throws Exception;

}
