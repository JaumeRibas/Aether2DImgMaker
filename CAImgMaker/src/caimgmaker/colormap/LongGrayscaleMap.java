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

public class LongGrayscaleMap implements LongBoundedColorMap {
	private long minValue;
	private long maxValue;
	private int minBrightness;
	private double brightnessIncreasePerUnit;
	
	public LongGrayscaleMap(long minValue, long maxValue, int minBrightness) {
		this.minBrightness = minBrightness;
		setValueRange(minValue, maxValue);
	}
	
	public LongGrayscaleMap(int minBrightness) {
		this.minBrightness = minBrightness;
	}
	
	@Override
	public void setValueRange(long min, long max) {
		if (minValue != min || maxValue != max) {
			minValue = min;
			maxValue = max;
			long range = maxValue - minValue + 1;
			if (range > 1) {
				brightnessIncreasePerUnit = (double)(255 - minBrightness)/(range - 1);
			} else {
				brightnessIncreasePerUnit = 0;
			}
		}
	}
	
	@Override
	public Color getColor(long value) throws IllegalArgumentException {
		if (value < minValue || value > maxValue ) 
			throw new IllegalArgumentException("Value " + value + " outside range [" + minValue + ", " + maxValue + "]");
		float brightness = (float) (((value - minValue)*brightnessIncreasePerUnit + minBrightness)/255);
		Color color = new Color(Color.HSBtoRGB(0, 0, brightness));
		return color;
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

}
