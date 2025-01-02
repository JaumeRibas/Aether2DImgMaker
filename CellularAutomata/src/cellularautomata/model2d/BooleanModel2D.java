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
package cellularautomata.model2d;

import java.util.Iterator;

import cellularautomata.Coordinates;
import cellularautomata.PartialCoordinates;
import cellularautomata.model.BooleanModel;

public interface BooleanModel2D extends Model2D, BooleanModel {
	
	/**
	 * <p>Returns the value at a given position.</p>
	 * <p>It is not defined to call this method passing coordinates outside the bounds of the region.</p>
	 * 
	 * @param x the position on the x-axis
	 * @param y the position on the y-axis
	 * @return the value at (x,y)
	 * @throws Exception 
	 */
	boolean getFromPosition(int x, int y) throws Exception;
	
	@Override
	default boolean getFromPosition(Coordinates coordinates) throws Exception {
		return getFromPosition(coordinates.get(0), coordinates.get(1));
	}
	
	@Override
	default BooleanModel2D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return (BooleanModel2D) Model2D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	default BooleanModel2D subsection(Integer minX, Integer maxX, Integer minY, Integer maxY) {
		return new BooleanSubModel2D(this, minX, maxX, minY, maxY);
	}

	@Override
	default Iterator<Boolean> iterator() {
		return new BooleanModel2DIterator(this);
	}

}
