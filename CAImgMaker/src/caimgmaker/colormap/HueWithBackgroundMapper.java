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
import cellularautomata.grid2D.LongGrid2D;
import cellularautomata.grid2D.ShortGrid2D;
import cellularautomata.grid2D.SymmetricIntGrid2D;
import cellularautomata.grid2D.SymmetricLongGrid2D;
import cellularautomata.grid2D.SymmetricShortGrid2D;

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
	public ColorMappedSymmetricLongGrid2D getMappedLongGrid(SymmetricLongGrid2D grid, long minValue, long maxValue) {
		LongHueMap colorMap = new LongHueMap(minValue, maxValue);
		return new ColorMappedSymmetricLongGrid2DWithBackground(grid, colorMap, backgroundValue, backgroundColor);
	}

	@Override
	public ColorMappedIntGrid2D getMappedIntGrid(IntGrid2D grid, int minValue, int maxValue) {
		LongHueMap colorMap = new LongHueMap(minValue, maxValue);
		return new ColorMappedIntGrid2DWithBackground(grid, colorMap, (int) backgroundValue, backgroundColor);
	}

	@Override
	public ColorMappedSymmetricIntGrid2D getMappedIntGrid(SymmetricIntGrid2D grid, int minValue, int maxValue) {
		LongHueMap colorMap = new LongHueMap(minValue, maxValue);
		return new ColorMappedSymmetricIntGrid2DWithBackground(grid, colorMap, (int) backgroundValue, backgroundColor);
	}
	
	@Override
	public ColorMappedShortGrid2D getMappedShortGrid(ShortGrid2D grid, short minValue, short maxValue) {
		LongHueMap colorMap = new LongHueMap(minValue, maxValue);
		return new ColorMappedShortGrid2DWithBackground(grid, colorMap, (short) backgroundValue, backgroundColor);
	}

	@Override
	public ColorMappedSymmetricShortGrid2D getMappedShortGrid(SymmetricShortGrid2D grid, short minValue, short maxValue) {
		LongHueMap colorMap = new LongHueMap(minValue, maxValue);
		return new ColorMappedSymmetricShortGrid2DWithBackground(grid, colorMap, (short) backgroundValue, backgroundColor);
	}

}
