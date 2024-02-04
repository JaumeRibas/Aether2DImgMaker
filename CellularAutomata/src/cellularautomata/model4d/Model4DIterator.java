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
package cellularautomata.model4d;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class Model4DIterator<Source_Type extends Model4D, Element_Type> implements Iterator<Element_Type> {
	
	protected Source_Type source;
	private int w;
	private int x;
	private int y;
	private int z;
	private int maxW;
	private int localMaxX;
	private int localMaxY;
	private int localMaxZ;
	private boolean hasNext;
	
	public Model4DIterator(Source_Type source) {
		this.source = source;
		w = source.getMinW();
		maxW = source.getMaxW();
		x = source.getMinXAtW(w);
		localMaxX = source.getMaxXAtW(w);
		y = source.getMinYAtWX(w, x);
		localMaxY = source.getMaxYAtWX(w, x);
		z = source.getMinZ(w, x, y);
		localMaxZ = source.getMaxZ(w, x, y);
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
			next = getFromModelPosition(w, x, y, z);
		} catch (Exception e) {
			throw new NoSuchElementException(e.toString());
		}
		if (z == localMaxZ) {
			if (y == localMaxY) {
				if (x == localMaxX) {
					if (w == maxW) {
						hasNext = false;
					} else {
						w++;
						x = source.getMinXAtW(w);
						localMaxX = source.getMaxXAtW(w);
						y = source.getMinYAtWX(w, x);
						localMaxY = source.getMaxYAtWX(w, x);
						z = source.getMinZ(w, x, y);
						localMaxZ = source.getMaxZ(w, x, y);
					}
				} else {
					x++;
					y = source.getMinYAtWX(w, x);
					localMaxY = source.getMaxYAtWX(w, x);
					z = source.getMinZ(w, x, y);
					localMaxZ = source.getMaxZ(w, x, y);
				}
			} else {
				y++;
				z = source.getMinZ(w, x, y);
				localMaxZ = source.getMaxZ(w, x, y);
			}
		} else {
			z++;
		}
		return next;
	}
	
	protected abstract Element_Type getFromModelPosition(int w, int x, int y, int z) throws Exception;

}
