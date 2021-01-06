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
package cellularautomata.grid2d;

public interface SymmetricShortGrid2D extends ShortGrid2D, SymmetricGrid2D {
	
	/**
	 * <p>
	 * Returns the value at a given position within the asymmetric section of the grid.
	 * That is, where the x is larger or equal to {@link #getAsymmetricMinX()} 
	 * and smaller or equal to {@link #getAsymmetricMaxX()}; 
	 * and the y is is larger or equal to {@link #getAsymmetricMinY()} 
	 * and smaller or equal to {@link #getAsymmetricMaxY()}.
	 * </p>
	 * <p>
	 * The result of getting the value of a position outside this bounds is undefined.
	 * <p>
	 * 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @return the {@link long} value at (x,y)
	 */
	short getValueAtAsymmetricPosition(int x, int y);
	
	@Override
	default ShortGrid2D asymmetricSection() {
		return new AsymmetricShortGridSection2D<SymmetricShortGrid2D>(this);
	}
}
