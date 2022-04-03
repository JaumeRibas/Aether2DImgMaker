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
package cellularautomata.model2d;

import cellularautomata.Coordinates;
import cellularautomata.model.SymmetricObjectModel;

public interface SymmetricObjectModel2D<Object_Type> extends ObjectModel2D<Object_Type>, SymmetricModel2D, SymmetricObjectModel<Object_Type> {

	/**
	 * <p>
	 * Returns the object at a given position within the asymmetric section of the grid.
	 * </p>
	 * <p>
	 * The result of getting an object at a position outside this section is undefined.
	 * <p>
	 * 
	 * @param x the position on the x-axis
	 * @param y the position on the y-axis
	 * @return the value at (x,y)
	 * @throws Exception 
	 */
	Object_Type getFromAsymmetricPosition(int x, int y) throws Exception;
	
	@Override
	default Object_Type getFromAsymmetricPosition(Coordinates coordinates) throws Exception {
		return getFromAsymmetricPosition(coordinates.get(0), coordinates.get(1));
	}

	@Override
	default ObjectModel2D<Object_Type> asymmetricSection() {
		return new AsymmetricObjectModelSection2D<SymmetricObjectModel2D<Object_Type>, Object_Type>(this);
	}
}
