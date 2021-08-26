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
package cellularautomata.grid5d;

public interface SymmetricIntGrid5D extends IntGrid5D, SymmetricGrid5D {

	/**
	 * <p>
	 * Returns the value at a given position within the asymmetric section of the grid.
	 * </p>
	 * <p>
	 * The result of getting the value of a position outside this section is undefined.
	 * <p>
	 * 
	 * @param v the position on the v-axis
	 * @param w the position on the w-axis
	 * @param x the position on the x-axis
	 * @param y the position on the y-axis
	 * @param z the position on the z-axis
	 * @return the value at (v,w,x,y,z)
	 * @throws Exception 
	 */
	int getFromAsymmetricPosition(int v, int w, int x, int y, int z) throws Exception;

	@Override
	default IntGrid5D asymmetricSection() {
		return new AsymmetricIntGridSection5D<SymmetricIntGrid5D>(this);
	}
}
