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
import java.math.BigInteger;

import cellularautomata.grid2d.BigIntGrid2D;
import cellularautomata.grid2d.IntGrid2D;
import cellularautomata.grid2d.LongGrid2D;
import cellularautomata.grid2d.ShortGrid2D;

public class HueWithBackgroundMapper implements ColorMapper {

	private BigInteger backgroundValue;
	private Color backgroundColor;
	
	public HueWithBackgroundMapper(long backgroundValue, Color backgroundColor) {
		this.backgroundValue = BigInteger.valueOf(backgroundValue);
		this.backgroundColor = backgroundColor;
	}
	
	public HueWithBackgroundMapper(BigInteger backgroundValue, Color backgroundColor) {
		this.backgroundValue = backgroundValue;
		this.backgroundColor = backgroundColor;
	}

	@Override
	public ColorGrid2D getMappedGrid(BigIntGrid2D grid, BigInteger minValue, BigInteger maxValue) {
		BigIntColorMap colorMap = null;
		if (minValue.equals(maxValue)) {
			colorMap = new SolidColorMap(getEmptyColor());
		} else {
			colorMap = new BigIntHueMap(minValue, maxValue);
		}
		return new ColorMappedBigIntGrid2DWithBackground(grid, colorMap, backgroundValue, backgroundColor);
	}

	@Override
	public ColorGrid2D getMappedGrid(LongGrid2D grid, long minValue, long maxValue) {
		LongColorMap colorMap = null;
		if (minValue == maxValue) {
			colorMap = new SolidColorMap(getEmptyColor());
		} else {
			colorMap = new LongHueMap(minValue, maxValue);
		}
		return new ColorMappedLongGrid2DWithBackground(grid, colorMap, backgroundValue.longValue(), backgroundColor);
	}

	@Override
	public ColorGrid2D getMappedGrid(IntGrid2D grid, int minValue, int maxValue) {
		IntColorMap colorMap = null;
		if (minValue == maxValue) {
			colorMap = new SolidColorMap(getEmptyColor());
		} else {
			colorMap = new IntHueMap(minValue, maxValue);
		}
		return new ColorMappedIntGrid2DWithBackground(grid, colorMap, backgroundValue.intValue(), backgroundColor);
	}
	
	@Override
	public ColorGrid2D getMappedGrid(ShortGrid2D grid, short minValue, short maxValue) {
		IntColorMap colorMap = null;
		if (minValue == maxValue) {
			colorMap = new SolidColorMap(getEmptyColor());
		} else {
			colorMap = new IntHueMap(minValue, maxValue);
		}
		return new ColorMappedShortGrid2DWithBackground(grid, colorMap, backgroundValue.shortValue(), backgroundColor);
	}
	
	private Color getEmptyColor() {
		float hue = (float)IntHueMap.HUE_MARGIN/255;
		hue = (hue + (float)1/6)%1;
		hue = 1 - hue;
		return new Color(Color.HSBtoRGB(hue, 1, 1));
	}

}
