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
package cellularautomata.model2d;

import cellularautomata.PartialCoordinates;
import cellularautomata.model.IntModel;

public class AsymmetricIntModelSection2D extends AsymmetricModelSection2D<SymmetricIntModel2D> implements IntModel2D {
	
	public AsymmetricIntModelSection2D(SymmetricIntModel2D source) {
		super(source);
	}

	@Override
	public int getFromPosition(int x, int y) throws Exception {
		return source.getFromAsymmetricPosition(x, y);
	}
	
	@Override
	public IntModel2D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return IntModel2D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	public IntModel/*1D*/ crossSection(int axis, int coordinate) {
		return IntModel2D.super.crossSection(axis, coordinate);
	}
	
	@Override
	public IntModel/*1D*/ diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return IntModel2D.super.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}

}
