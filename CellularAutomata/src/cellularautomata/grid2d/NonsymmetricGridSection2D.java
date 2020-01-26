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
package cellularautomata.grid2d;

public abstract class NonsymmetricGridSection2D implements Grid2D {

	protected SymmetricGrid2D grid;
	
	public NonsymmetricGridSection2D(SymmetricGrid2D grid) {
		super();
		this.grid = grid;
	}

	@Override
	public int getMinX() {
		return grid.getNonsymmetricMinX();
	}

	@Override
	public int getMaxX() {
		return grid.getNonsymmetricMaxX();
	}
	
	@Override
	public int getMinX(int y) {
		return grid.getNonsymmetricMinX(y);
	}

	@Override
	public int getMaxX(int y) {
		return grid.getNonsymmetricMaxX(y);
	}

	@Override
	public int getMinY() {
		return grid.getNonsymmetricMinY();
	}

	@Override
	public int getMaxY() {
		return grid.getNonsymmetricMaxY();
	}
	
	@Override
	public int getMinY(int x) {
		return grid.getNonsymmetricMinY(x);
	}

	@Override
	public int getMaxY(int x) {
		return grid.getNonsymmetricMaxY(x);
	}

}
