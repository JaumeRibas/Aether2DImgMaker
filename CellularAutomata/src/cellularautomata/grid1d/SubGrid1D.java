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
package cellularautomata.grid1d;

public class SubGrid1D<G extends Grid1D> implements Grid1D {
	
	protected G source;
	protected int minX;
	protected int maxX;
	
	public SubGrid1D(G source, int minX, int maxX) {
		if (minX > maxX) {
			throw new IllegalArgumentException("Min x cannot be bigger than max x.");
		}
		this.source = source;
		if (!getActualBounds(minX, maxX)) {
			throw new IllegalArgumentException("Sub-grid bounds outside of grid bounds.");
		}
	}
	
	protected boolean getActualBounds(int minX, int maxX) {
		int sourceMinX = source.getMinX();
		int sourceMaxX = source.getMaxX();
		if (minX > sourceMaxX || maxX < sourceMinX) {
			return false;
		} else {
			this.minX = Math.max(minX, sourceMinX);
			this.maxX = Math.min(maxX, sourceMaxX);
			return true;
		}
	}

	@Override
	public int getMinX() {
		return minX;
	}

	@Override
	public int getMaxX() {
		return maxX;
	}

}
