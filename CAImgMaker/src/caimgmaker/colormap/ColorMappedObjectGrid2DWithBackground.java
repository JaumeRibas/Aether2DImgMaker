/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2023 Jaume Ribas

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

import cellularautomata.model2d.ObjectModel2D;

public class ColorMappedObjectGrid2DWithBackground<Object_Type> extends ColorMappedObjectGrid2D<Object_Type> {

	protected Object_Type backgroundValue;
	protected Color backgroundColor;
	
	public ColorMappedObjectGrid2DWithBackground(ObjectModel2D<Object_Type> grid, ColorMap<Object_Type> colorMap, 
			Object_Type backgroundValue, Color backgroundColor) {
		super(grid, colorMap);
		this.backgroundColor = backgroundColor;
		this.backgroundValue = backgroundValue;
	}

	@Override
	public Color getFromPosition(int x, int y) throws Exception {
		Object_Type value = source.getFromPosition(x, y);
		if (value.equals(backgroundValue)) {
			return backgroundColor;
		}
		return colorMap.getColor(value);
	}
}
