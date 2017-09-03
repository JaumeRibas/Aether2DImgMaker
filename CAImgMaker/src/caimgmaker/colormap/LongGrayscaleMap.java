/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017 Jaume Ribas

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

public class LongGrayscaleMap implements LongColorMap {
	private int minBrightness;
	private double brightnessIncreasePerUnit;
	private long minValue;
	private long maxValue;
	private Color[] colors;
	private static final int MAX_COLOR_COUNT = 100;
	
	public LongGrayscaleMap(long min, long max, int minBrightness) {
		this.minBrightness = minBrightness;
		this.minValue = min;
		this.maxValue = max;
		computeColors();
	}
	
	public LongGrayscaleMap(int minBrightness) {
		this.minBrightness = minBrightness;
	}
	
	public void setRange(long min, long max) {
		if (this.minValue != min || this.maxValue != max) {
			this.minValue = min;
			this.maxValue = max;
			computeColors();
		}
	}
	
	private void computeColors() {
		long lRange = maxValue - minValue + 1;
		if (lRange > 1) {
			this.brightnessIncreasePerUnit = (double)(255 - minBrightness)/(lRange - 1);
		} else {
			this.brightnessIncreasePerUnit = 0;
		}
		if (lRange > MAX_COLOR_COUNT) {
			colors = null;
			return;
		}
		int range = (int)lRange;
		this.colors = new Color[range];
		int i = 0;
		if (range == 1) {
			colors[0] = computeColor(minValue);
		} else {
			for (long value = minValue; value <= maxValue; value++, i++) {
				colors[i] = computeColor(value);
			}
		}
	}
	
	private Color computeColor(long value) {
		float brightness = (float) (((value - minValue)*brightnessIncreasePerUnit + minBrightness)/255);
		Color color = new Color(Color.HSBtoRGB(0, 0, brightness));
		return color;
	}
	
	public Color getColor(long value) throws IllegalArgumentException {
		if (colors != null) {
			return colors[(int)(value - minValue)];
		} else {
			if (value < minValue || value > maxValue ) 
				throw new IllegalArgumentException("Value " + value + " outside range (" + minValue + "-" + maxValue + ")");
			return computeColor(value);
		}
	}
	
	public long getMaxValue() {
		return maxValue;
	}

	public long getMinValue() {
		return minValue;
	}
	
	public void setMin(long min) throws Exception {
		if (this.minValue != min) {
			this.minValue = min;
			computeColors();
		}
	}
	
	public void setMax(long max) throws Exception {
		if (this.maxValue != max) {
			this.maxValue = max;
			computeColors();
		}
	}

}
