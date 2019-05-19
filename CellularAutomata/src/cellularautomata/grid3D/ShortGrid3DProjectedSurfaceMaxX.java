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
package cellularautomata.grid3D;

import cellularautomata.grid2D.ShortGrid2D;

public class ShortGrid3DProjectedSurfaceMaxX implements ShortGrid2D {

	private ShortGrid3D source;
	private short backgroundValue;
	
	public ShortGrid3DProjectedSurfaceMaxX(ShortGrid3D source, short backgroundValue) {
		this.source = source;
		this.backgroundValue = backgroundValue;
	}

	@Override
	public int getMinX() {
		return source.getMinZ();
	}

	@Override
	public int getMaxX() {
		return source.getMaxZ();
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
	public short getValueAtPosition(int x, int y) {
		int sourceX, sourceY, sourceZ, minX;
		sourceY = y;
		sourceZ = x;
		sourceX = source.getMaxX();
		minX = source.getMinX();
		short value = source.getValueAtPosition(sourceX, sourceY, sourceZ);
		while (value == backgroundValue && sourceX > minX) {
			sourceX--;
			value = source.getValueAtPosition(sourceX, sourceY, sourceZ);
		}
		return value;
	}
}
