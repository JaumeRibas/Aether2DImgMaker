/* CellularSharing2DImgMaker -- console app to generate images from the Cellular Sharing 2D cellular automaton
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

public class LongHueMap implements LongColorMap {
	private double hueIncreasePerUnit;
	private long minValue;
	private long maxValue;
	private Color[] colors;
	private static final int MAX_COLOR_COUNT = 100;
	private static final int HUE_RANGE = 220;
	private static final int HUE_MARGIN = 255 - HUE_RANGE;
	
	public LongHueMap(long min, long max) {
		this.minValue = min;
		this.maxValue = max;
		computeColors();
	}
	
	private void computeColors() {
		long lRange = maxValue - minValue + 1;
		this.hueIncreasePerUnit = (double)(HUE_RANGE /*- minBrightness*/)/lRange;
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
		float hue = (float) (((value - minValue + 1)*hueIncreasePerUnit + HUE_MARGIN)/255);
		hue = (hue + (float)1/6)%1;
		hue = 1 - hue;
		Color color = new Color(Color.HSBtoRGB(hue, 1, 1));
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

}
