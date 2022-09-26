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
package cellularautomata.model;

import org.apache.commons.math3.FieldElement;

import cellularautomata.PartialCoordinates;

public class NumericModelCrossSection<Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>>
	extends ObjectModelCrossSection<NumericModel<Number_Type>, Number_Type> implements NumericModel<Number_Type> {
	
	public NumericModelCrossSection(NumericModel<Number_Type> grid, int axis, int coordinate) {
		super(grid, axis, coordinate);
	}
	
	@Override
	public NumericModel<Number_Type> subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return NumericModel.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	public NumericModel<Number_Type> crossSection(int axis, int coordinate) {
		return NumericModel.super.crossSection(axis, coordinate);
	}
	
	@Override
	public NumericModel<Number_Type> diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return NumericModel.super.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}

}