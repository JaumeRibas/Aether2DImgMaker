/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2025 Jaume Ribas

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

import cellularautomata.Coordinates;
import cellularautomata.PartialCoordinates;
import cellularautomata.model.ObjectModel;

public interface ObjectModel1D<Object_Type> extends Model1D, ObjectModel<Object_Type> {
	
	/**
	 * <p>Returns the value at a given position.</p>
	 * <p>It is not defined to call this method passing coordinates outside the bounds of the region. 
	 * To get these bounds use the {@link #getMaxX()} and {@link #getMinX()} methods.</p>
	 * 
	 * @param x the position on the x-axis
	 * @return the value at (x)
	 * @throws Exception 
	 */
	Object_Type getFromPosition(int x) throws Exception;
	
	@Override
	default Object_Type getFromPosition(Coordinates coordinates) throws Exception {
		return getFromPosition(coordinates.get(0));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	default ObjectModel1D<Object_Type> subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return (ObjectModel1D<Object_Type>) Model1D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	default ObjectModel1D<Object_Type> subsection(Integer minX, Integer maxX) {
		return new ObjectSubModel1D<ObjectModel1D<Object_Type>, Object_Type>(this, minX, maxX);
	}

	@Override
	default Iterator<Object_Type> iterator() {
		return new ObjectModel1DIterator<Object_Type>(this);
	}
}
