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
package cellularautomata.model2d;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class Model2DIterator<Source_Type extends Model2D, Element_Type> implements Iterator<Element_Type> {
	
	protected Source_Type source;
	private int x;
	private int y;
	private int maxX;
	private int localMaxY;
	private boolean hasNext;
	
	public Model2DIterator(Source_Type grid) {
		this.source = grid;
		x = grid.getMinX();
		maxX = grid.getMaxX();
		y = grid.getMinY(x);
		localMaxY = grid.getMaxY(x);
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
			next = getFromModelPosition(x, y);
		} catch (Exception e) {
			throw new NoSuchElementException(e.toString());
		}
		if (y == localMaxY) {
			if (x == maxX) {
				hasNext = false;
			} else {
				x++;
				y = source.getMinY(x);
				localMaxY = source.getMaxY(x);
			}
		} else {
			y++;
		}
		return next;
	}
	
	protected abstract Element_Type getFromModelPosition(int x, int y) throws Exception;

}
