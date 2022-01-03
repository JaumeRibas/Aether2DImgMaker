/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2021 Jaume Ribas

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
package cellularautomata.model5d;

public class LongSubModel5D extends SubModel5D<LongModel5D> implements LongModel5D {
	
	public LongSubModel5D(LongModel5D source, int minV, int maxV, int minW, int maxW, int minX, int maxX, int minY, int maxY, int minZ,
			int maxZ) {
		super(source, minV, maxV, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}

	@Override
	public long getFromPosition(int v, int w, int x, int y, int z) throws Exception {
		return source.getFromPosition(v, w, x, y, z);
	}

}
