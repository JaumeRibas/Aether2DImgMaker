/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2018 Jaume Ribas

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
package cellularautomata.grid;

public class AbsSymmetricLongGrid2D extends SymmetricLongGrid2D {
	
	private SymmetricLongGrid2D source;
	
	public AbsSymmetricLongGrid2D(SymmetricLongGrid2D source) {
		this.source = source;
	}

	@Override
	public long getValue(int x, int y) throws Exception {
		return Math.abs(source.getValue(x, y));
	}

	@Override
	public int getMinX() {
		return source.getMinX();
	}

	@Override
	public int getMaxX() {
		return source.getMaxX();
	}

	@Override
	public int getMinY() {
		return source.getMinY();
	}

	@Override
	public int getMaxY() {
		return source.getMaxY();
	}

	@Override
	public int getNonSymmetricMinX() {
		return source.getNonSymmetricMinX();
	}

	@Override
	public int getNonSymmetricMaxX() {
		return source.getNonSymmetricMaxX();
	}

	@Override
	public int getNonSymmetricMinY() {
		return source.getNonSymmetricMinY();
	}

	@Override
	public int getNonSymmetricMaxY() {
		return source.getNonSymmetricMaxY();
	}

	@Override
	public long getNonSymmetricValue(int x, int y) throws Exception {
		return Math.abs(source.getNonSymmetricValue(x, y));
	}

	@Override
	public int getNonSymmetricMinX(int y) {
		return source.getNonSymmetricMinX(y);
	}

	@Override
	public int getNonSymmetricMaxX(int y) {
		return source.getNonSymmetricMaxX(y);
	}

	@Override
	public int getNonSymmetricMinY(int x) {
		return source.getNonSymmetricMinY(x);
	}

	@Override
	public int getNonSymmetricMaxY(int x) {
		return source.getNonSymmetricMaxY(x);
	}

}
