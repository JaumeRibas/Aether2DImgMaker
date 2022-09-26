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
import cellularautomata.model.LongModel;

public class AsymmetricLongModelSection2D extends AsymmetricModelSection2D<SymmetricLongModel2D> implements LongModel2D {
	
	public AsymmetricLongModelSection2D(SymmetricLongModel2D source) {
		super(source);
	}

	@Override
	public long getFromPosition(int x, int y) throws Exception {
		return source.getFromAsymmetricPosition(x, y);
	}
	
	@Override
	public LongModel2D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return LongModel2D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	public LongModel/*1D*/ crossSection(int axis, int coordinate) {
		return LongModel2D.super.crossSection(axis, coordinate);
	}
	
	@Override
	public LongModel/*1D*/ diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return LongModel2D.super.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}

}
