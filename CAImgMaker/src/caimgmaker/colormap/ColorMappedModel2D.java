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

import cellularautomata.PartialCoordinates;
import cellularautomata.model.ObjectModel;
import cellularautomata.model2d.Model2DDecorator;
import cellularautomata.model2d.ObjectModel2D;

public class ColorMappedModel2D<Object_Type> extends Model2DDecorator<ObjectModel2D<Object_Type>> implements ObjectModel2D<Color> {

	protected ColorMap<Object_Type> colorMap;
	
	public ColorMappedModel2D(ObjectModel2D<Object_Type> source, ColorMap<Object_Type> colorMap) {
		super(source);
		this.colorMap = colorMap;
	}

	@Override
	public Color getFromPosition(int x, int y) throws Exception {
		return colorMap.getColor(source.getFromPosition(x, y));
	}
	
	@Override
	public ObjectModel2D<Color> subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return ObjectModel2D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	public ObjectModel2D<Color> subsection(Integer minX, Integer maxX, Integer minY, Integer maxY) {
		return ObjectModel2D.super.subsection(minX, maxX, minY, maxY);
	}
	
	@Override
	public ObjectModel/*1D*/<Color> crossSection(int axis, int coordinate) {
		return ObjectModel2D.super.crossSection(axis, coordinate);
	}
	
	@Override
	public ObjectModel/*1D*/<Color> diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return ObjectModel2D.super.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}
}
