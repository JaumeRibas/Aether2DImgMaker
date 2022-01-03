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
package cellularautomata.model2d;

import cellularautomata.model.Coordinates;
import cellularautomata.model.SymmetricIntModel;

public interface SymmetricIntModel2D extends IntModel2D, SymmetricModel2D, SymmetricIntModel {
	
	/**
	 * <p>
	 * Returns the value at a given position within the asymmetric section of the grid.
	 * That is, where the x-coordinate is inside the [{@link #getAsymmetricMinX()}, {@link #getAsymmetricMaxX()}] bounds 
	 * and the y-coordinate is inside the [{@link #getAsymmetricMinY(int x)}, {@link #getAsymmetricMaxY(int x)}] bounds.
	 * </p>
	 * <p>
	 * Or where the y-coordinate is inside the [{@link #getAsymmetricMinY()}, {@link #getAsymmetricMaxY()}] bounds 
	 * and the x-coordinate is inside the [{@link #getAsymmetricMinX(int y)}, {@link #getAsymmetricMaxX(int y)}] bounds.
	 * </p>
	 * <p>
	 * The result of getting the value of a position outside this bounds is undefined.
	 * <p>
	 * 
	 * @param x the position on the x-axis
	 * @param y the position on the y-axis
	 * @return the {@link int} value at (x,y)
	 * @throws Exception 
	 */
	int getFromAsymmetricPosition(int x, int y) throws Exception;
	
	default int getFromAsymmetricPosition(Coordinates coordinates) throws Exception {
		return getFromAsymmetricPosition(coordinates.get(0), coordinates.get(1));
	}
	
	@Override
	default IntModel2D asymmetricSection() {
		return new AsymmetricIntModelSection2D<SymmetricIntModel2D>(this);
	}
}
