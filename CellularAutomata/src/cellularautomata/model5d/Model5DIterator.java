/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2024 Jaume Ribas

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
package cellularautomata.model5d;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class Model5DIterator<Source_Type extends Model5D, Element_Type> implements Iterator<Element_Type> {
	
	protected Source_Type source;
	private int v;
	private int w;
	private int x;
	private int y;
	private int z;
	private int maxV;
	private int localMaxW;
	private int localMaxX;
	private int localMaxY;
	private int localMaxZ;
	private boolean hasNext;
	
	public Model5DIterator(Source_Type grid) {
		this.source = grid;
		v = grid.getMinV();
		maxV = grid.getMaxV();
		w = grid.getMinWAtV(v);
		localMaxW = grid.getMaxWAtV(v);
		x = grid.getMinXAtVW(v, w);
		localMaxX = grid.getMaxXAtVW(v, w);
		y = grid.getMinYAtVWX(v, w, x);
		localMaxY = grid.getMaxYAtVWX(v, w, x);
		z = grid.getMinZ(v, w, x, y);
		localMaxZ = grid.getMaxZ(v, w, x, y);
		hasNext = true;
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public Element_Type next() {
		if (!hasNext)
			throw new NoSuchElementException();
		Element_Type next = null;
		try {
			next = getFromModelPosition(v, w, x, y, z);
		} catch (Exception e) {
			throw new NoSuchElementException(e.toString());
		}
		if (z == localMaxZ) {
			if (y == localMaxY) {
				if (x == localMaxX) {
					if (w == localMaxW) {
						if (v == maxV) {
							hasNext = false;
						} else {
							v++;
							w = source.getMinWAtV(v);
							localMaxW = source.getMaxWAtV(v);
							x = source.getMinXAtVW(v, w);
							localMaxX = source.getMaxXAtVW(v, w);
							y = source.getMinYAtVWX(v, w, x);
							localMaxY = source.getMaxYAtVWX(v, w, x);
							z = source.getMinZ(v, w, x, y);
							localMaxZ = source.getMaxZ(v, w, x, y);
						}
					} else {
						w++;
						x = source.getMinXAtVW(v, w);
						localMaxX = source.getMaxXAtVW(v, w);
						y = source.getMinYAtVWX(v, w, x);
						localMaxY = source.getMaxYAtVWX(v, w, x);
						z = source.getMinZ(v, w, x, y);
						localMaxZ = source.getMaxZ(v, w, x, y);
					}
				} else {
					x++;
					y = source.getMinYAtVWX(v, w, x);
					localMaxY = source.getMaxYAtVWX(v, w, x);
					z = source.getMinZ(v, w, x, y);
					localMaxZ = source.getMaxZ(v, w, x, y);
				}
			} else {
				y++;
				z = source.getMinZ(v, w, x, y);
				localMaxZ = source.getMaxZ(v, w, x, y);
			}
		} else {
			z++;
		}
		return next;
	}
	
	protected abstract Element_Type getFromModelPosition(int v, int w, int x, int y, int z) throws Exception;

}
