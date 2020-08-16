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

public class LongSubGrid2D<G extends LongGrid2D> extends SubGrid2D<G> implements LongGrid2D {
	
	public LongSubGrid2D(G source, int minX, int maxX, int minY, int maxY) {
		super(source, minX, maxX, minY, maxY);
	}

	@Override
	public long getValueAtPosition(int x, int y) throws Exception {
		return source.getValueAtPosition(x, y);
	}

}
