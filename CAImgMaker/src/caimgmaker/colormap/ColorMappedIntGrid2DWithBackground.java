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
package caimgmaker.colormap;

import java.awt.Color;
import cellularautomata.grid2D.IntGrid2D;

public class ColorMappedIntGrid2DWithBackground extends ColorMappedIntGrid2D {

	protected int backgroundValue;
	protected Color backgroundColor;
	
	public ColorMappedIntGrid2DWithBackground(IntGrid2D grid, LongColorMap colorMap, 
			int backgroundValue, Color backgroundColor) {
		super(grid, colorMap);
		this.backgroundColor = backgroundColor;
		this.backgroundValue = backgroundValue;
	}

	@Override
	public Color getColorAtPosition(int x, int y) throws Exception {
		int value = source.getValueAtPosition(x, y);
		if (value == backgroundValue) {
			return backgroundColor;
		}
		return colorMap.getColor(value);
	}
}
