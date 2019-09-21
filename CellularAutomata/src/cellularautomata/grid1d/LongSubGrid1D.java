/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2019 Jaume Ribas

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

public class LongSubGrid1D implements LongGrid1D {
	
	private LongGrid1D source;
	private int minX;
	private int maxX;
	
	public LongSubGrid1D(LongGrid1D source, int minX, int maxX) {
		this.source = source;
		this.minX = minX;
		this.maxX = maxX;
	}

	@Override
	public long getValueAtPosition(int x) {
		return source.getValueAtPosition(x);
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
