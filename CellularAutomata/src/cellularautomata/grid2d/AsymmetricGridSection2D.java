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

public abstract class AsymmetricGridSection2D implements Grid2D {

	protected SymmetricGrid2D source;
	
	public AsymmetricGridSection2D(SymmetricGrid2D grid) {
		super();
		this.source = grid;
	}

	@Override
	public int getMinX() {
		return source.getAsymmetricMinX();
	}

	@Override
	public int getMaxX() {
		return source.getAsymmetricMaxX();
	}
	
	@Override
	public int getMinX(int y) {
		return source.getAsymmetricMinX(y);
	}

	@Override
	public int getMaxX(int y) {
		return source.getAsymmetricMaxX(y);
	}

	@Override
	public int getMinY() {
		return source.getAsymmetricMinY();
	}

	@Override
	public int getMaxY() {
		return source.getAsymmetricMaxY();
	}
	
	@Override
	public int getMinY(int x) {
		return source.getAsymmetricMinY(x);
	}

	@Override
	public int getMaxY(int x) {
		return source.getAsymmetricMaxY(x);
	}

}