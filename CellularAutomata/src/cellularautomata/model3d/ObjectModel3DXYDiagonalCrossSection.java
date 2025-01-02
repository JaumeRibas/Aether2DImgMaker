/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2025 Jaume Ribas

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

import cellularautomata.model2d.ObjectModel2D;

public class ObjectModel3DXYDiagonalCrossSection<Source_Type extends ObjectModel3D<Object_Type>, Object_Type> extends Model3DXYDiagonalCrossSection<Source_Type> implements ObjectModel2D<Object_Type> {

	public ObjectModel3DXYDiagonalCrossSection(Source_Type source, boolean positiveSlope, int yOffsetFromX) {
		super(source, positiveSlope, yOffsetFromX);
	}

	@Override
	public Object_Type getFromPosition(int x, int y) throws Exception {
		return source.getFromPosition(x, slope*x + yOffsetFromX, y);
	}

}
