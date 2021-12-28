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
package cellularautomata.grid1d;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class Grid1DIterator<G extends Grid1D, T> implements Iterator<T> {
	
	protected G grid;
	private int x;
	private int maxX;
	private boolean hasNext;
	
	public Grid1DIterator(G grid) {
		this.grid = grid;
		this.x = grid.getMinX();
		maxX = grid.getMaxX();
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
		T next = getFromGridPosition(x);
		if (x == maxX) {
			hasNext = false;
		} else {
			x++;
		}
		return next;
	}
	
	protected abstract T getFromGridPosition(int x);

}
