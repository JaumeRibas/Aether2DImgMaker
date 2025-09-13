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

public class IsotropicCubicBooleanModel extends IsotropicCubicModel<IsotropicCubicBooleanModelAsymmetricSection> implements BooleanModel3D {
	
	public IsotropicCubicBooleanModel(IsotropicCubicBooleanModelAsymmetricSection asymmetricSection) {
		super(asymmetricSection);
	}

	@Override
	public boolean getFromPosition(int x, int y, int z) throws Exception {	
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		if (z < 0) z = -z;
		if (x >= y) {
			if (y >= z) {
				//x >= y >= z
				return asymmetricSection.getFromPosition(x, y, z);
			} else if (x >= z) { 
				//x >= z > y
				return asymmetricSection.getFromPosition(x, z, y);
			} else {
				//z > x >= y
				return asymmetricSection.getFromPosition(z, x, y);
			}
		} else if (y >= z) {
			if (x >= z) {
				//y > x >= z
				return asymmetricSection.getFromPosition(y, x, z);
			} else {
				//y >= z > x
				return asymmetricSection.getFromPosition(y, z, x);
			}
		} else {
			// z > y > x
			return asymmetricSection.getFromPosition(z, y, x);
		}
	}

}
