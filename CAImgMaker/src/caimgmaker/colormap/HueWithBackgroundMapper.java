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
package caimgmaker.colormap;

import java.awt.Color;

import cellularautomata.grid2d.IntGrid2D;
import cellularautomata.grid2d.LongGrid2D;
import cellularautomata.grid2d.ShortGrid2D;

public class HueWithBackgroundMapper extends ColorMapper {

	private long backgroundValue;
	private Color backgroundColor;
	
	public HueWithBackgroundMapper(long backgroundValue, Color backgroundColor) {
		this.backgroundValue = backgroundValue;
		this.backgroundColor = backgroundColor;
	}

	@Override
	public ColorMappedLongGrid2D getMappedLongGrid(LongGrid2D grid, long minValue, long maxValue) {
		LongHueMap colorMap = new LongHueMap(minValue, maxValue);
		return new ColorMappedLongGrid2DWithBackground(grid, colorMap, backgroundValue, backgroundColor);
	}

	@Override
	public ColorMappedIntGrid2D getMappedIntGrid(IntGrid2D grid, int minValue, int maxValue) {
		LongHueMap colorMap = new LongHueMap(minValue, maxValue);
		return new ColorMappedIntGrid2DWithBackground(grid, colorMap, (int) backgroundValue, backgroundColor);
	}
	
	@Override
	public ColorMappedShortGrid2D getMappedShortGrid(ShortGrid2D grid, short minValue, short maxValue) {
		LongHueMap colorMap = new LongHueMap(minValue, maxValue);
		return new ColorMappedShortGrid2DWithBackground(grid, colorMap, (short) backgroundValue, backgroundColor);
	}

}
