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
	private int x;
	private int y;
	private int z;
	private int maxX;
	private int localMaxY;
	private int localMaxZ;
	private boolean hasNext;
	
	public Grid3DIterator(G grid) {
		this.grid = grid;
		x = grid.getMinX();
		maxX = grid.getMaxX();
		y = grid.getMinYAtX(x);
		localMaxY = grid.getMaxYAtX(x);
		z = grid.getMinZ(x, y);
		localMaxZ = grid.getMaxZ(x, y);
		hasNext = true;
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public T next() {
		if (!hasNext)
			throw new NoSuchElementException();
		T next = null;
		try {
			next = getFromGridPosition(x, y, z);
		} catch (Exception e) {
			throw new NoSuchElementException(e.getMessage());
		}
		if (z == localMaxZ) {
			if (y == localMaxY) {
				if (x == maxX) {
					hasNext = false;
				} else {
					x++;
					y = grid.getMinYAtX(x);
					localMaxY = grid.getMaxYAtX(x);
					z = grid.getMinZ(x, y);
					localMaxZ = grid.getMaxZ(x, y);					
				}
			} else {
				y++;
				z = grid.getMinZ(x, y);
				localMaxZ = grid.getMaxZ(x, y);
			}
		} else {
			z++;
		}
		return next;
	}
	
	protected abstract T getFromGridPosition(int x, int y, int z) throws Exception;

}
