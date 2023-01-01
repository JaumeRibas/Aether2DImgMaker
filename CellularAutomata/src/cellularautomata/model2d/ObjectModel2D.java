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
package cellularautomata.model2d;

import java.util.Iterator;

import cellularautomata.Coordinates;
import cellularautomata.PartialCoordinates;
import cellularautomata.model.ObjectModel;

public interface ObjectModel2D<Object_Type> extends Model2D, ObjectModel<Object_Type> {

	/**
	 * Returns the object at a given position
	 * 
	 * @param x the position on the x-axis
	 * @param y the position on the y-axis
	 * @return the object at (x,y)
	 * @throws Exception 
	 */
	Object_Type getFromPosition(int x, int y) throws Exception;
	
	@Override
	default Object_Type getFromPosition(Coordinates coordinates) throws Exception {
		return getFromPosition(coordinates.get(0), coordinates.get(1));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	default ObjectModel2D<Object_Type> subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return (ObjectModel2D<Object_Type>) Model2D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	default ObjectModel2D<Object_Type> subsection(Integer minX, Integer maxX, Integer minY, Integer maxY) {
		return new ObjectSubModel2D<ObjectModel2D<Object_Type>, Object_Type>(this, minX, maxX, minY, maxY);
	}

	@Override
	default Iterator<Object_Type> iterator() {
		return new ObjectModel2DIterator<Object_Type>(this);
	}
}
