/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2019 Jaume Ribas

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

public interface SymmetricIntGrid2D extends IntGrid2D, SymmetricGrid2D {
	
	/**
	 * <p>
	 * Returns the value at a given position within the nonsymmetric section of the grid.
	 * That is, where the x-coordinate is inside the [{@link #getNonsymmetricMinX()}, {@link #getNonsymmetricMaxX()}] bounds 
	 * and the y-coordinate is inside the [{@link #getNonsymmetricMinY(int x)}, {@link #getNonsymmetricMaxY(int x)}] bounds.
	 * </p>
	 * <p>
	 * Or where the y-coordinate is inside the [{@link #getNonsymmetricMinY()}, {@link #getNonsymmetricMaxY()}] bounds 
	 * and the x-coordinate is inside the [{@link #getNonsymmetricMinX(int y)}, {@link #getNonsymmetricMaxX(int y)}] bounds.
	 * </p>
	 * <p>
	 * The result of getting the value of a position outside this bounds is undefined.
	 * <p>
	 * 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @return the {@link int} value at (x,y)
	 * @throws Exception 
	 */
	int getValueAtNonsymmetricPosition(int x, int y) throws Exception;
	
	@Override
	default IntGrid2D nonsymmetricSection() {
		return new NonsymmetricIntGridSection2D(this);
	}
}
