/* CellularSharing2DImgMaker -- console app to generate images from the Cellular Sharing 2D cellular automaton
    Copyright (C) 2017 Jaume Ribas

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

public class AbsSymmetricLongGrid3D extends SymmetricLongGrid3D {
	
	private SymmetricLongGrid3D source;
	
	public AbsSymmetricLongGrid3D(SymmetricLongGrid3D source) {
		this.source = source;
	}

	@Override
	public long getValueAt(int x, int y, int z) {
		return Math.abs(source.getValueAt(x, y, z));
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
	public long getNonSymmetricValueAt(int x, int y, int z) {
		return Math.abs(source.getNonSymmetricValueAt(x, y, z));
	}

}
