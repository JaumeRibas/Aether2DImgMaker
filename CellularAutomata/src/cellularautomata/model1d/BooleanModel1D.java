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
package cellularautomata.model1d;

import java.util.Iterator;

import cellularautomata.Coordinates;
import cellularautomata.PartialCoordinates;
import cellularautomata.model.BooleanModel;

public interface BooleanModel1D extends Model1D, BooleanModel {
	
	/**
	 * <p>Returns the value at a given position.</p>
	 * <p>It is not defined to call this method passing coordinates outside the bounds of the region. 
	 * To get these bounds use the {@link #getMaxX()} and {@link #getMinX()} methods.</p>
	 * 
	 * @param x the position on the x-axis
	 * @return the value at (x)
	 * @throws Exception 
	 */
	boolean getFromPosition(int x) throws Exception;
	
	@Override
	default boolean getFromPosition(Coordinates coordinates) throws Exception {
		return getFromPosition(coordinates.get(0));
	}
	
	@Override
	default BooleanModel1D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return (BooleanModel1D) Model1D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	default BooleanModel1D subsection(Integer minX, Integer maxX) {
		return new BooleanSubModel1D(this, minX, maxX);
	}

	@Override
	default Iterator<Boolean> iterator() {
		return new BooleanModel1DIterator(this);
	}
}
