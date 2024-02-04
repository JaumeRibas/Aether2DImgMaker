/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2024 Jaume Ribas

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

import cellularautomata.model3d.BooleanModel3D;

public class BooleanModel4DZCrossSection extends Model4DZCrossSection<BooleanModel4D> implements BooleanModel3D {

	public BooleanModel4DZCrossSection(BooleanModel4D source, int z) {
		super(source, z);
	}

	@Override
	public boolean getFromPosition(int x, int y, int z) throws Exception {
		return source.getFromPosition(x, y, z, this.z);
	}
}
