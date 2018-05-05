/* SpreadIntValue2DImgMaker2 -- console app to generate images from the SpreadIntegerValue2D algorithm
    Copyright (C) 2016 Jaume Ribas

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

public class LongCyclingColorMap implements LongColorMap {
	
	private static final int MAX_COLOR_PROP_VAL = 255;
	private int minBrightness;
	private int maxBrightness = MAX_COLOR_PROP_VAL;
	private int brightnessRange;
	private float saturation = (float)0.5;
	private long range;
	
	public void setRange(long range) {
		this.range = range;
	}

	public LongCyclingColorMap(int minBrightness, long range) {
		this.minBrightness = minBrightness;
		this.brightnessRange = maxBrightness - minBrightness;
		this.range = range;
	}
	
	@Override
	public Color getColor(long value) {		
		long loopedValue = loopValue(value, range);
		double fraction = (double)loopedValue/range;
		float brightness = (float)((fraction*brightnessRange + minBrightness)/MAX_COLOR_PROP_VAL);		
		//hue
		long loopCount = value/range;
		if (value == range) loopCount--;
		long hueVal = (long)(Math.PI * loopCount * (MAX_COLOR_PROP_VAL/5));
		long loopedHueVal = loopValue(hueVal, MAX_COLOR_PROP_VAL);
		float hue = (float)((double)loopedHueVal/MAX_COLOR_PROP_VAL);
		
		Color color = new Color(java.awt.Color.HSBtoRGB(hue, saturation, brightness));
		return color;
	}

	private static long loopValue(long value, long max) {
		return value%(max + 1);
	}
	
}
