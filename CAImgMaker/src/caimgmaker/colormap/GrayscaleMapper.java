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

import java.awt.Color;
import cellularautomata.grid2d.NumberGrid2D;
import cellularautomata.grid2d.ObjectGrid2D;
import cellularautomata.grid2d.IntGrid2D;
import cellularautomata.grid2d.LongGrid2D;
import cellularautomata.grid2d.ShortGrid2D;
import cellularautomata.numbers.BigInt;

public class GrayscaleMapper implements ColorMapper {

	private int minBrightness;
	private Color outOfLowerBoundColor;
	private Color outOfUpperBoundColor;	
	
	public GrayscaleMapper(int minBrightness) {
		this.minBrightness = minBrightness;
	}
	
	public GrayscaleMapper(int minBrightness, Color outOfBoundsColor) {
		this.minBrightness = minBrightness;
		this.outOfLowerBoundColor = outOfBoundsColor;
		this.outOfUpperBoundColor = outOfBoundsColor;
	}
	
	public GrayscaleMapper(int minBrightness, Color outOfLowerBoundColor, Color outOfUpperBoundColor) {
		this.minBrightness = minBrightness;
		this.outOfLowerBoundColor = outOfLowerBoundColor;
		this.outOfUpperBoundColor = outOfUpperBoundColor;
	}

	@Override
	public ObjectGrid2D<Color> getMappedGrid(NumberGrid2D<BigInt> grid, BigInt minValue, BigInt maxValue) {
		ColorMap<BigInt> colorMap = null;
		if (minValue.equals(maxValue)) {
			colorMap = new SolidColorMap<BigInt>(new Color(0, 0, minBrightness/255));
		} else {
			colorMap = new BigIntGrayscaleMap(minValue, maxValue, minBrightness);
		}
		if (outOfLowerBoundColor != null) {
			colorMap = new BigIntUnboundedColorMap(colorMap, minValue, maxValue, outOfLowerBoundColor, outOfUpperBoundColor);
		}
		return new ColorMappedGrid2D<BigInt>(grid, colorMap);
	}

	@Override
	public ObjectGrid2D<Color> getMappedGrid(LongGrid2D grid, long minValue, long maxValue) {
		LongColorMap colorMap = null;
		if (minValue == maxValue) {
			colorMap = new SolidColorMap<Object>(new Color(0, 0, minBrightness/255));
		} else {
			colorMap = new LongGrayscaleMap(minValue, maxValue, minBrightness);
		}
		if (outOfLowerBoundColor != null) {
			colorMap = new LongUnboundedColorMap(colorMap, minValue, maxValue, outOfLowerBoundColor, outOfUpperBoundColor);
		}
		return new ColorMappedLongGrid2D(grid, colorMap);
	}

	@Override
	public ObjectGrid2D<Color> getMappedGrid(IntGrid2D grid, int minValue, int maxValue) {
		IntColorMap colorMap = null;
		if (minValue == maxValue) {
			colorMap = new SolidColorMap<Object>(new Color(0, 0, minBrightness/255));
		} else {
			colorMap = new IntGrayscaleMap(minValue, maxValue, minBrightness);
		}
		if (outOfLowerBoundColor != null) {
			colorMap = new IntUnboundedColorMap(colorMap, minValue, maxValue, outOfLowerBoundColor, outOfUpperBoundColor);
		}
		return new ColorMappedIntGrid2D(grid, colorMap);
	}
	
	@Override
	public ObjectGrid2D<Color> getMappedGrid(ShortGrid2D grid, short minValue, short maxValue) {
		IntColorMap colorMap = null;
		if (minValue == maxValue) {
			colorMap = new SolidColorMap<Object>(new Color(0, 0, minBrightness/255));
		} else {
			colorMap = new IntGrayscaleMap(minValue, maxValue, minBrightness);
		}
		if (outOfLowerBoundColor != null) {
			colorMap = new IntUnboundedColorMap(colorMap, minValue, maxValue, outOfLowerBoundColor, outOfUpperBoundColor);
		}
		return new ColorMappedShortGrid2D(grid, colorMap);
	}

}
