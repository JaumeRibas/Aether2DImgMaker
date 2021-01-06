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
package caimgmaker.colormap;

import cellularautomata.grid2d.IntGrid2D;
import cellularautomata.grid2d.LongGrid2D;
import cellularautomata.grid2d.ShortGrid2D;

public class CyclingColorMapper extends ColorMapper {

	private int minBrightness;
	private int range;
	
	public CyclingColorMapper(int minBrightness, int range) {
		this.minBrightness = minBrightness;
		this.range = range;
	}
	
	@Override
	public ColorMappedLongGrid2D getMappedLongGrid(LongGrid2D grid, long minValue, long maxValue) {
		LongCyclingColorMap colorMap = new LongCyclingColorMap(minBrightness, range);
		return new ColorMappedLongGrid2D(grid, colorMap);
	}

	@Override
	public ColorMappedIntGrid2D getMappedIntGrid(IntGrid2D grid, int minValue, int maxValue) {
		LongCyclingColorMap colorMap = new LongCyclingColorMap(minBrightness, range);
		return new ColorMappedIntGrid2D(grid, colorMap);
	}
	
	@Override
	public ColorMappedShortGrid2D getMappedShortGrid(ShortGrid2D grid, short minValue, short maxValue) {
		LongCyclingColorMap colorMap = new LongCyclingColorMap(minBrightness, range);
		return new ColorMappedShortGrid2D(grid, colorMap);
	}
}
