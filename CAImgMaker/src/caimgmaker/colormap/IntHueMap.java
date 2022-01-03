/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2022 Jaume Ribas

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

public class IntHueMap implements IntBoundedColorMap {
	
	private int minValue;
	private int maxValue;
	private double hueIncreasePerUnit;
	private static final int HUE_RANGE = 220;
	public static final int HUE_MARGIN = 255 - HUE_RANGE;
	
	public IntHueMap(int minValue, int maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		long range = maxValue - minValue;
		if (range > 0) {
			this.hueIncreasePerUnit = (double)HUE_RANGE/range;
		} else {
			this.hueIncreasePerUnit = 0;
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

	
	@Override
	public Color getColor(int value) throws IllegalArgumentException {
		if (value < minValue || value > maxValue ) 
			throw new IllegalArgumentException("Value " + value + " outside range [" + minValue + ", " + maxValue + "]");
		float hue = (float) (((value - minValue)*hueIncreasePerUnit + HUE_MARGIN)/255);
		hue = (hue + (float)1/6)%1;
		hue = 1 - hue;
		Color color = new Color(Color.HSBtoRGB(hue, 1, 1));
		return color;
	}
}
