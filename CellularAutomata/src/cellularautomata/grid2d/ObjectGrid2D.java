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
package cellularautomata.grid2d;

import java.util.Iterator;

import cellularautomata.grid.ObjectGrid;

public interface ObjectGrid2D<T> extends Grid2D, ObjectGrid<T> {

	/**
	 * Returns the object at a given position
	 * 
	 * @param x the position on the x-axis
	 * @param y the position on the y-axis
	 * @return the object at (x,y)
	 * @throws Exception 
	 */
	T getFromPosition(int x, int y) throws Exception;
	
	@Override
	default ObjectGrid2D<T> subsection(int minX, int maxX, int minY, int maxY) {
		return new ObjectSubGrid2D<T, ObjectGrid2D<T>>(this, minX, maxX, minY, maxY);
	}

	@Override
	default Iterator<T> iterator() {
		return new ObjectGrid2DIterator<T>(this);
	}
}
