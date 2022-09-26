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
package cellularautomata.model3d;

import cellularautomata.PartialCoordinates;
import cellularautomata.model2d.ObjectModel2D;

public class AsymmetricObjectModelSection3D<Source_Type extends SymmetricObjectModel3D<Object_Type>, Object_Type> extends AsymmetricModelSection3D<Source_Type> implements ObjectModel3D<Object_Type> {
	
	public AsymmetricObjectModelSection3D(Source_Type grid) {
		super(grid);
	}

	@Override
	public Object_Type getFromPosition(int x, int y, int z) throws Exception {
		return source.getFromAsymmetricPosition(x, y, z);
	}
	
	@Override
	public ObjectModel3D<Object_Type> subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return ObjectModel3D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	public ObjectModel2D<Object_Type> crossSection(int axis, int coordinate) {
		return ObjectModel3D.super.crossSection(axis, coordinate);
	}
	
	@Override
	public ObjectModel2D<Object_Type> diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return ObjectModel3D.super.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}

}
