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

public class IntUnboundedColorMap implements IntBoundedColorMap {
	
	private int minValue;
	private int maxValue;
	private IntColorMap colorMap;
	private Color outOfLowerBoundColor;
	private Color outOfUpperBoundColor;
	
	public IntUnboundedColorMap(IntColorMap colorMap, int minValue, int maxValue, 
			Color outOfLowerBoundColor, Color outOfUpperBoundColor) {
		this.colorMap = colorMap;
		this.outOfLowerBoundColor = outOfLowerBoundColor;
		this.outOfUpperBoundColor = outOfUpperBoundColor;
		if (minValue > maxValue) {
			int swap = minValue;
			minValue = maxValue;
			maxValue = swap;
		}
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	@Override
	public Color getColor(int value) throws Exception {
		if (value < minValue) {
			return outOfLowerBoundColor;
		} else if (value > maxValue) {
			return outOfUpperBoundColor;
		} else {
			return colorMap.getColor(value);
		}
	}

	@Override
	public int getMaxValue() {
		return maxValue;
	}

	@Override
	public int getMinValue() {
		return minValue;
	}

}
