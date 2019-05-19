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
package cellularautomata.grid1D;

public class AbsSymmetricLongGrid1D implements SymmetricLongGrid1D {
	
	private SymmetricLongGrid1D source;
	
	public AbsSymmetricLongGrid1D(SymmetricLongGrid1D source) {
		this.source = source;
	}

	@Override
	public long getValueAtPosition(int x) {
		return Math.abs(source.getValueAtPosition(x));
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
	public int getNonSymmetricMinX() {
		return source.getNonSymmetricMinX();
	}

	@Override
	public int getNonSymmetricMaxX() {
		return source.getNonSymmetricMaxX();
	}

	@Override
	public long getValueAtNonSymmetricPosition(int x) {
		return Math.abs(source.getValueAtNonSymmetricPosition(x));
	}

}
