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

public class LongUnboundedColorMap implements LongBoundedColorMap {
	private LongBoundedColorMap colorMap;
	private Color outOfBoundsColor;
	
	public LongUnboundedColorMap(LongBoundedColorMap colorMap, Color outOfBoundsColor) {
		this.colorMap = colorMap;
		this.outOfBoundsColor = outOfBoundsColor;
	}
	
	public void setValueRange(long min, long max) throws Exception {
		colorMap.setValueRange(min, max);
	}
	
	public Color getColorAtPosition(long value) throws Exception {
		if (value < colorMap.getMinValue() || value > colorMap.getMaxValue()) {
			return outOfBoundsColor;
		} else {
			return colorMap.getColorAtPosition(value);
		}
	}
	
	public void setMinValue(long value) throws Exception {
		colorMap.setMinValue(value);
	}
	
	public void setMaxValue(long value) throws Exception {
		colorMap.setMaxValue(value);
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
