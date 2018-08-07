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

public class SymmetricLongGrid4DCrossSection extends SymmetricLongGrid2D {

	private SymmetricLongGrid4D source;
	private int y;
	private int z;
	
	public SymmetricLongGrid4DCrossSection(SymmetricLongGrid4D source, int y, int z) {
		this.source = source;
		this.y = y;
		this.z = z;
	}

	@Override
	public int getMinX() {
		return source.getMinW();
	}

	@Override
	public int getMaxX() {
		return source.getMaxW();
	}

	@Override
	public int getMinY() {
		return source.getMinX();
	}

	@Override
	public int getMaxY() {
		return source.getMaxX();
	}

	@Override
	public long getValueAtPosition(int x, int y) {
		return source.getValueAtPosition(x, y, this.y, z);
	}

	@Override
	public int getNonSymmetricMinX() {
		return source.getNonSymmetricMinW();
	}

	@Override
	public int getNonSymmetricMaxX() {
		return source.getNonSymmetricMaxW();
	}

	@Override
	public int getNonSymmetricMinY() {
		return source.getNonSymmetricMinX();
	}

	@Override
	public int getNonSymmetricMaxY() {
		return source.getNonSymmetricMaxX();
	}

	@Override
	public long getNonSymmetricValue(int x, int y) {
		return source.getNonSymmetricValue(x, y, this.y, z);
	}
	
	@Override
	public int getNonSymmetricMinX(int y) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public int getNonSymmetricMaxX(int y) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public int getNonSymmetricMinY(int x) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public int getNonSymmetricMaxY(int x) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

}
