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
package cellularautomata.grid5d;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class Grid5DIterator<G extends Grid5D, T> implements Iterator<T> {
	
	protected G grid;
	protected int v;
	protected int w;
	protected int x;
	protected int y;
	protected int z;
	
	public Grid5DIterator(G grid) {
		this.grid = grid;
		this.v = grid.getMinV();
		this.w = grid.getMinWAtV(v);
		this.x = grid.getMinXAtVW(v, w);
		this.y = grid.getMinYAtVWX(v, w, x);
		this.z = grid.getMinZ(v, w, x, y);
	}

	@Override
	public boolean hasNext() {
		return v <= grid.getMaxV();
	}

	@Override
	public T next() {
		if (v > grid.getMaxV())
			throw new NoSuchElementException();
		T next = null;
		try {
			next = getFromGridPosition(v, w, x, y, z);
		} catch (Exception e) {
			throw new NoSuchElementException(e.getMessage());//what should be done here?
		}
		if (z < grid.getMaxZ(v, w, x, y)) {
			z++;
		} else {
			if (y < grid.getMaxYAtVWX(v, w, x)) {
				y++;
				z = grid.getMinZ(v, w, x, y);				
			} else {
				if (x < grid.getMaxXAtVW(v, w)) {
					x++;
					y = grid.getMinYAtVWX(v, w, x);
					z = grid.getMinZ(v, w, x, y);
				} else {
					if (w < grid.getMaxWAtV(v)) {
						w++;
						x = grid.getMinXAtVW(v, w);
						y = grid.getMinYAtVWX(v, w, x);
						z = grid.getMinZ(v, w, x, y);
					} else {
						v++;
						if (v <= grid.getMaxV()) {
							w = grid.getMinWAtV(v);
							x = grid.getMinXAtVW(v, w);
							y = grid.getMinYAtVWX(v, w, x);
							z = grid.getMinZ(v, w, x, y);
						}
					}
				}
			}
		}
		return next;
	}
	
	protected abstract T getFromGridPosition(int v, int w, int x, int y, int z) throws Exception;

}
