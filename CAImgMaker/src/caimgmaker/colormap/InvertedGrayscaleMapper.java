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

public class InvertedGrayscaleMapper extends ColorMapper {

	private int minBrightness;
	private Color outOfLowerBoundColor;
	private Color outOfUpperBoundColor;	
	
	public InvertedGrayscaleMapper(int minBrightness) {
		this.minBrightness = minBrightness;
	}
	
	public InvertedGrayscaleMapper(int minBrightness, Color outOfBoundsColor) {
		this.minBrightness = minBrightness;
		this.outOfLowerBoundColor = outOfBoundsColor;
		this.outOfUpperBoundColor = outOfBoundsColor;
	}
	
	public InvertedGrayscaleMapper(int minBrightness, Color outOfLowerBoundColor, Color outOfUpperBoundColor) {
		this.minBrightness = minBrightness;
		this.outOfLowerBoundColor = outOfLowerBoundColor;
		this.outOfUpperBoundColor = outOfUpperBoundColor;
	}

	@Override
	public ColorMappedLongGrid2D getMappedLongGrid(LongGrid2D grid, long minValue, long maxValue) {
		LongBoundedColorMap colorMap = new LongInvertedGrayscaleMap(minValue, maxValue, minBrightness);
		if (outOfLowerBoundColor != null) {
			colorMap = new LongUnboundedColorMap(colorMap, outOfLowerBoundColor, outOfUpperBoundColor);
		}
		return new ColorMappedLongGrid2D(grid, colorMap);
	}

	@Override
	public ColorMappedIntGrid2D getMappedIntGrid(IntGrid2D grid, int minValue, int maxValue) {
		LongBoundedColorMap colorMap = new LongInvertedGrayscaleMap(minValue, maxValue, minBrightness);
		if (outOfLowerBoundColor != null) {
			colorMap = new LongUnboundedColorMap(colorMap, outOfLowerBoundColor, outOfUpperBoundColor);
		}
		return new ColorMappedIntGrid2D(grid, colorMap);
	}
	
	@Override
	public ColorMappedShortGrid2D getMappedShortGrid(ShortGrid2D grid, short minValue, short maxValue) {
		LongBoundedColorMap colorMap = new LongInvertedGrayscaleMap(minValue, maxValue, minBrightness);
		if (outOfLowerBoundColor != null) {
			colorMap = new LongUnboundedColorMap(colorMap, outOfLowerBoundColor, outOfUpperBoundColor);
		}
		return new ColorMappedShortGrid2D(grid, colorMap);
	}

}
