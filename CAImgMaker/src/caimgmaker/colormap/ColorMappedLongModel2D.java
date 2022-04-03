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

import cellularautomata.model2d.LongModel2D;
import cellularautomata.model2d.Model2DDecorator;
import cellularautomata.model2d.ObjectModel2D;

public class ColorMappedLongModel2D extends Model2DDecorator<LongModel2D> implements ObjectModel2D<Color> {

	protected LongColorMap colorMap;
	
	public ColorMappedLongModel2D(LongModel2D source, LongColorMap colorMap) {
		super(source);
		this.colorMap = colorMap;
	}

	@Override
	public Color getFromPosition(int x, int y) throws Exception {
		return colorMap.getColor(source.getFromPosition(x, y));
	}
	
	@Override
	public ObjectModel2D<Color> subsection(int minX, int maxX, int minY, int maxY) {
		return ObjectModel2D.super.subsection(minX, maxX, minY, maxY);
	}
}
