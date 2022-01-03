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
package cellularautomata.model4d;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class Model4DIterator<G extends Model4D, T> implements Iterator<T> {
	
	protected G grid;
	private int w;
	private int x;
	private int y;
	private int z;
	private int maxW;
	private int localMaxX;
	private int localMaxY;
	private int localMaxZ;
	private boolean hasNext;
	
	public Model4DIterator(G grid) {
		this.grid = grid;
		w = grid.getMinW();
		maxW = grid.getMaxW();
		x = grid.getMinXAtW(w);
		localMaxX = grid.getMaxXAtW(w);
		y = grid.getMinYAtWX(w, x);
		localMaxY = grid.getMaxYAtWX(w, x);
		z = grid.getMinZ(w, x, y);
		localMaxZ = grid.getMaxZ(w, x, y);
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
			next = getFromModelPosition(w, x, y, z);
		} catch (Exception e) {
			throw new NoSuchElementException(e.getMessage());
		}
		if (z == localMaxZ) {
			if (y == localMaxY) {
				if (x == localMaxX) {
					if (w == maxW) {
						hasNext = false;
					} else {
						w++;
						x = grid.getMinXAtW(w);
						localMaxX = grid.getMaxXAtW(w);
						y = grid.getMinYAtWX(w, x);
						localMaxY = grid.getMaxYAtWX(w, x);
						z = grid.getMinZ(w, x, y);
						localMaxZ = grid.getMaxZ(w, x, y);
					}
				} else {
					x++;
					y = grid.getMinYAtWX(w, x);
					localMaxY = grid.getMaxYAtWX(w, x);
					z = grid.getMinZ(w, x, y);
					localMaxZ = grid.getMaxZ(w, x, y);
				}
			} else {
				y++;
				z = grid.getMinZ(w, x, y);
				localMaxZ = grid.getMaxZ(w, x, y);
			}
		} else {
			z++;
		}
		return next;
	}
	
	protected abstract T getFromModelPosition(int w, int x, int y, int z) throws Exception;

}
