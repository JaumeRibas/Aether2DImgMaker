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
package cellularautomata.model1d;

import java.util.Iterator;

import cellularautomata.model.ObjectModel;

public interface ObjectModel1D<T> extends Model1D, ObjectModel<T> {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-axis
	 * @return the value at (x)
	 * @throws Exception 
	 */
	T getFromPosition(int x) throws Exception;
	
	@Override
	default ObjectModel1D<T> subsection(int minX, int maxX) {
		return new ObjectSubModel1D<T, ObjectModel1D<T>>(this, minX, maxX);
	}

	@Override
	default Iterator<T> iterator() {
		return new ObjectModel1DIterator<T>(this);
	}
}
