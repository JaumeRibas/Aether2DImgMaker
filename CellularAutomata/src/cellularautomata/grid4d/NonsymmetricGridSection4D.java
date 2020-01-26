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

public abstract class NonsymmetricGridSection4D implements Grid4D {

	protected SymmetricGrid4D grid;
	
	public NonsymmetricGridSection4D(SymmetricGrid4D grid) {
		super();
		this.grid = grid;
	}
	
	@Override
	public int getMinW() {
		return grid.getNonsymmetricMinW();
	}

	@Override
	public int getMaxW() {
		return grid.getNonsymmetricMaxW();
	}
	
	@Override
	public int getMinW(int x, int y, int z) {
		return grid.getNonsymmetricMinW(x, y, z);
	}
	
	@Override
	public int getMaxW(int x, int y, int z) {
		return grid.getNonsymmetricMaxW(x, y, z);
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
	public int getMinY() {
		return grid.getNonsymmetricMinY();
	}

	@Override
	public int getMaxY() {
		return grid.getNonsymmetricMaxY();
	}
	
	@Override
	public int getMinZ() {
		return grid.getNonsymmetricMinZ();
	}

	@Override
	public int getMaxZ() {
		return grid.getNonsymmetricMaxZ();
	}

}
