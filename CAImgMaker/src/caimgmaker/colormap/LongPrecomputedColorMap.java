/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2018 Jaume Ribas

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

public class LongPrecomputedColorMap implements LongBoundedColorMap {
	private LongBoundedColorMap colorMap;
	private Color[] colors;
	private static final int MAX_COLOR_COUNT = 100;
	
	public LongPrecomputedColorMap(LongBoundedColorMap colorMap) throws Exception {
		this.colorMap = colorMap;
		computeColors();
	}
	
	public void setValueRange(long min, long max) throws Exception {
		if (colorMap.getMinValue() != min || colorMap.getMaxValue() != max) {
			colorMap.setValueRange(min, max);
			computeColors();
		}
	}
	
	private void computeColors() throws Exception {
		long minValue = colorMap.getMinValue(), maxValue = colorMap.getMaxValue();
		long lRange = maxValue - minValue + 1;
		if (lRange > MAX_COLOR_COUNT) {
			colors = null;
			return;
		}
		int range = (int)lRange;
		this.colors = new Color[range];
		int i = 0;
		if (range == 1) {
			colors[0] = colorMap.getColor(minValue);
		} else {
			for (long value = minValue; value <= maxValue; value++, i++) {
				colors[i] = colorMap.getColor(value);
			}
		}
	}
	
	public Color getColor(long value) throws Exception {
		if (colors != null) {
			return colors[(int)(value - colorMap.getMinValue())];
		} else {
			return colorMap.getColor(value);
		}
	}
	
	public void setMinValue(long value) throws Exception {
		if (colorMap.getMinValue() != value) {
			colorMap.setMinValue(value);
			computeColors();
		}
	}
	
	public void setMaxValue(long value) throws Exception {
		if (colorMap.getMaxValue() != value) {
			colorMap.setMaxValue(value);
			computeColors();
		}
	}

	@Override
	public long getMaxValue() {
		return colorMap.getMaxValue();
	}

	@Override
	public long getMinValue() {
		return colorMap.getMinValue();
	}

}
