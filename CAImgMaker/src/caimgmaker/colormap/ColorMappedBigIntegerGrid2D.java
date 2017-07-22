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
package caimgmaker.colormap;

import java.awt.Color;

import cellularautomata.grid.BigIntegerGrid2D;

public class ColorMappedBigIntegerGrid2D extends ColorGrid2D {

	protected BigIntegerGrid2D grid;
	protected BigIntegerColorMap colorMap;
	
	public ColorMappedBigIntegerGrid2D(BigIntegerGrid2D grid, BigIntegerColorMap colorMap) {
		this.grid = grid;
		this.colorMap = colorMap;
	}
		
	@Override
	public int getMinX() {
		return grid.getMinX();
	}

	@Override
	public int getMaxX() {
		return grid.getMaxX();
	}

	@Override
	public int getMinY() {
		return grid.getMinY();
	}

	@Override
	public int getMaxY() {
		return grid.getMaxY();
	}

	@Override
	public Color getColorAt(int x, int y) {
		return colorMap.getColor(grid.getValueAt(x, y));
	}
}
