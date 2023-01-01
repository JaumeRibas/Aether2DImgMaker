/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2023 Jaume Ribas

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

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class Model3DIterator<Source_Type extends Model3D, Element_Type> implements Iterator<Element_Type> {
	
	protected Source_Type source;
	private int x;
	private int y;
	private int z;
	private int maxX;
	private int localMaxY;
	private int localMaxZ;
	private boolean hasNext;
	
	public Model3DIterator(Source_Type source) {
		this.source = source;
		x = source.getMinX();
		maxX = source.getMaxX();
		y = source.getMinYAtX(x);
		localMaxY = source.getMaxYAtX(x);
		z = source.getMinZ(x, y);
		localMaxZ = source.getMaxZ(x, y);
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
			next = getFromModelPosition(x, y, z);
		} catch (Exception e) {
			throw new NoSuchElementException(e.toString());
		}
		if (z == localMaxZ) {
			if (y == localMaxY) {
				if (x == maxX) {
					hasNext = false;
				} else {
					x++;
					y = source.getMinYAtX(x);
					localMaxY = source.getMaxYAtX(x);
					z = source.getMinZ(x, y);
					localMaxZ = source.getMaxZ(x, y);					
				}
			} else {
				y++;
				z = source.getMinZ(x, y);
				localMaxZ = source.getMaxZ(x, y);
			}
		} else {
			z++;
		}
		return next;
	}
	
	protected abstract Element_Type getFromModelPosition(int x, int y, int z) throws Exception;

}
