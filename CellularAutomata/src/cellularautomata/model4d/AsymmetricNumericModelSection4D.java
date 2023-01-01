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
package cellularautomata.model4d;

import org.apache.commons.math3.FieldElement;

import cellularautomata.PartialCoordinates;
import cellularautomata.model3d.NumericModel3D;

public class AsymmetricNumericModelSection4D<Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> 
	extends AsymmetricObjectModelSection4D<SymmetricNumericModel4D<Number_Type>, Number_Type> implements NumericModel4D<Number_Type> {
	
	public AsymmetricNumericModelSection4D(SymmetricNumericModel4D<Number_Type> grid) {
		super(grid);
	}
	
	@Override
	public NumericModel4D<Number_Type> subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return NumericModel4D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	public NumericModel3D<Number_Type> crossSection(int axis, int coordinate) {
		return NumericModel4D.super.crossSection(axis, coordinate);
	}
	
	@Override
	public NumericModel3D<Number_Type> diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return NumericModel4D.super.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}

}
