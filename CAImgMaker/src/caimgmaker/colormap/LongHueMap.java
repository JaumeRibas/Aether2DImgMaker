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

public class LongHueMap implements LongBoundedColorMap {
	private long minValue;
	private long maxValue;
	private double hueIncreasePerUnit;
	private static final int HUE_RANGE = 220;
	private static final int HUE_MARGIN = 255 - HUE_RANGE;
	
	public LongHueMap(long minValue, long maxValue) {
		setValueRange(minValue, maxValue);
	}
	
	@Override
	public void setValueRange(long min, long max) {
		if (minValue != min || maxValue != max) {
			minValue = min;
			maxValue = max;
			long range = maxValue - minValue + 1;
			if (range > 1) {
				this.hueIncreasePerUnit = (double)HUE_RANGE/(range - 1);
			} else {
				this.hueIncreasePerUnit = 0;
			} 
		}
	}

	@Override
	public void setMinValue(long value) {
		setValueRange(value, maxValue);
	}
	
	@Override
	public void setMaxValue(long value) {
		setValueRange(minValue, value);
	}

	@Override
	public long getMaxValue() {
		return maxValue;
	}

	@Override
	public long getMinValue() {
		return minValue;
	}

	
	@Override
	public Color getColor(long value) throws IllegalArgumentException {
		if (value < minValue || value > maxValue ) 
			throw new IllegalArgumentException("Value " + value + " outside range (" + minValue + "-" + maxValue + ")");
		float hue = (float) (((value - minValue)*hueIncreasePerUnit + HUE_MARGIN)/255);
		hue = (hue + (float)1/6)%1;
		hue = 1 - hue;
		Color color = new Color(Color.HSBtoRGB(hue, 1, 1));
		return color;
	}
}
