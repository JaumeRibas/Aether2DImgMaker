/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2020 Jaume Ribas

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
package cellularautomata.grid4d;

public interface SymmetricLongGrid4D extends LongGrid4D, SymmetricGrid4D {

	/**
	 * Returns the value at a given position
	 * 
	 * @param w the position on the w-coordinate
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @param z the position on the z-coordinate
	 * @return the value at (x,y,z)
	 */
	long getValueAtNonsymmetricPosition(int w, int x, int y, int z);

	@Override
	default LongGrid4D nonsymmetricSection() {
		return new NonsymmetricLongGridSection4D(this);
	}
}
