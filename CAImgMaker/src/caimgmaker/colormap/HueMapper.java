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

import cellularautomata.grid.IntGrid2D;
import cellularautomata.grid.LongGrid2D;
import cellularautomata.grid.ShortGrid2D;
import cellularautomata.grid.SymmetricIntGrid2D;
import cellularautomata.grid.SymmetricLongGrid2D;
import cellularautomata.grid.SymmetricShortGrid2D;

public class HueMapper extends ColorMapper {

	@Override
	public ColorMappedLongGrid2D getMappedLongGrid(LongGrid2D grid, long minValue, long maxValue) {
		LongHueMap colorMap = new LongHueMap(minValue, maxValue);
		return new ColorMappedLongGrid2D(grid, colorMap);
	}

	@Override
	public ColorMappedSymmetricLongGrid2D getMappedLongGrid(SymmetricLongGrid2D grid, long minValue, long maxValue) {
		LongHueMap colorMap = new LongHueMap(minValue, maxValue);
		return new ColorMappedSymmetricLongGrid2D(grid, colorMap);
	}

	@Override
	public ColorMappedIntGrid2D getMappedIntGrid(IntGrid2D grid, int minValue, int maxValue) {
		LongHueMap colorMap = new LongHueMap(minValue, maxValue);
		return new ColorMappedIntGrid2D(grid, colorMap);
	}

	@Override
	public ColorMappedSymmetricIntGrid2D getMappedIntGrid(SymmetricIntGrid2D grid, int minValue, int maxValue) {
		LongHueMap colorMap = new LongHueMap(minValue, maxValue);
		return new ColorMappedSymmetricIntGrid2D(grid, colorMap);
	}

	@Override
	public ColorMappedShortGrid2D getMappedShortGrid(ShortGrid2D grid, short minValue, short maxValue) {
		LongHueMap colorMap = new LongHueMap(minValue, maxValue);
		return new ColorMappedShortGrid2D(grid, colorMap);
	}

	@Override
	public ColorMappedSymmetricShortGrid2D getMappedShortGrid(SymmetricShortGrid2D grid, short minValue, short maxValue) {
		LongHueMap colorMap = new LongHueMap(minValue, maxValue);
		return new ColorMappedSymmetricShortGrid2D(grid, colorMap);
	}

}
