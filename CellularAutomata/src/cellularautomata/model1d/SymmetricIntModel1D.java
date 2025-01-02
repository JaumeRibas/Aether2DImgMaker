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

import cellularautomata.Coordinates;
import cellularautomata.model.SymmetricIntModel;

public interface SymmetricIntModel1D extends IntModel1D, SymmetricModel1D, SymmetricIntModel {
	
	/**
	 * <p>
	 * Returns the value at a given position within the asymmetric section of the grid.
	 * That is, where the x-coordinate is inside the [{@link #getAsymmetricMinX()}, {@link #getAsymmetricMaxX()}] bounds.
	 * </p>
	 * <p>
	 * The result of getting the value of a position outside these bounds is undefined.
	 * <p>
	 * 
	 * @param x the position on the x-axis
	 * @return the {@code int} value at (x)
	 * @throws Exception 
	 */
	int getFromAsymmetricPosition(int x) throws Exception;
	
	@Override
	default int getFromAsymmetricPosition(Coordinates coordinates) throws Exception {
		return getFromAsymmetricPosition(coordinates.get(0));
	}
	
	@Override
	default IntModel1D asymmetricSection() {
		return new AsymmetricIntModelSection1D(this);
	}

}
