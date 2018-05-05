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

public class AbsSymmetricLongGrid4D extends SymmetricLongGrid4D {
	
	private SymmetricLongGrid4D source;
	
	public AbsSymmetricLongGrid4D(SymmetricLongGrid4D source) {
		this.source = source;
	}

	@Override
	public long getValue(int w, int x, int y, int z) {
		return Math.abs(source.getValue(w, x, y, z));
	}
	
	@Override
	public int getMinW() {
		return source.getMinW();
	}

	@Override
	public int getMaxW() {
		return source.getMaxW();
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
	public int getMinZ() {
		return source.getMinZ();
	}

	@Override
	public int getMaxZ() {
		return source.getMaxZ();
	}

	@Override
	public int getNonSymmetricMinW() {
		return source.getNonSymmetricMinW();
	}

	@Override
	public int getNonSymmetricMaxW() {
		return source.getNonSymmetricMaxW();
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
	public int getNonSymmetricMinZ() {
		return source.getNonSymmetricMinZ();
	}

	@Override
	public int getNonSymmetricMaxZ() {
		return source.getNonSymmetricMaxZ();
	}

	@Override
	public long getNonSymmetricValue(int w, int x, int y, int z) {
		return Math.abs(source.getNonSymmetricValue(w, x, y, z));
	}

}
