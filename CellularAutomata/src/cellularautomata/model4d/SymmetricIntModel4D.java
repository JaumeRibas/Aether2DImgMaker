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
package cellularautomata.model4d;

import cellularautomata.Coordinates;
import cellularautomata.model.SymmetricIntModel;

public interface SymmetricIntModel4D extends IntModel4D, SymmetricModel4D, SymmetricIntModel {

	/**
	 * <p>
	 * Returns the value at a given position within the asymmetric section of the grid.
	 * </p>
	 * <p>
	 * The result of getting the value of a position outside this section is undefined.
	 * <p>
	 * 
	 * @param w the position on the w-axis
	 * @param x the position on the x-axis
	 * @param y the position on the y-axis
	 * @param z the position on the z-axis
	 * @return the value at (w,x,y,z)
	 * @throws Exception 
	 */
	int getFromAsymmetricPosition(int w, int x, int y, int z);
	
	@Override
	default int getFromAsymmetricPosition(Coordinates coordinates) throws Exception {
		return getFromAsymmetricPosition(coordinates.get(0), coordinates.get(1), coordinates.get(2), coordinates.get(3));
	}

	@Override
	default IntModel4D asymmetricSection() {
		return new AsymmetricIntModelSection4D(this);
	}

}
