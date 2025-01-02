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

import org.apache.commons.math3.FieldElement;

public class CustomCubicNumericArrayModelDelta<Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> implements SymmetricNumericModel3D<Number_Type>, IsotropicCubicModelA {	

	private Number_Type[][][] oldGrid;
	private IsotropicCubicNumericArrayModelA<Number_Type> model;
	private int sizeDifference;

	public CustomCubicNumericArrayModelDelta(IsotropicCubicNumericArrayModelA<Number_Type> model) throws Exception {
		this.model = model;
		nextStep();
	}
	
	@Override
	public Boolean nextStep() throws Exception {
		oldGrid = null;
		oldGrid = model.grid.clone();//Clone it because it gets progressively cleared
		Boolean result = model.nextStep();
		sizeDifference = model.grid.length - oldGrid.length;
		return result;
	}

	@Override
	public Boolean isChanged() {
		return model.isChanged();
	}

	@Override
	public long getStep() {
		return model.getStep();
	}

	@Override
	public int getAsymmetricMaxX() {
		return model.getAsymmetricMaxX() - sizeDifference;
	}
	
	@Override
	public Number_Type getFromPosition(int x, int y, int z) {	
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		if (z < 0) z = -z;
		if (x >= y) {
			if (y >= z) {
				//x >= y >= z
				return getFromAsymmetricPosition(x, y, z);
			} else if (x >= z) { 
				//x >= z > y
				return getFromAsymmetricPosition(x, z, y);
			} else {
				//z > x >= y
				return getFromAsymmetricPosition(z, x, y);
			}
		} else if (y >= z) {
			if (x >= z) {
				//y > x >= z
				return getFromAsymmetricPosition(y, x, z);
			} else {
				//y >= z > x
				return getFromAsymmetricPosition(y, z, x);
			}
		} else {
			// z > y > x
			return getFromAsymmetricPosition(z, y, x);
		}
	}

	@Override
	public Number_Type getFromAsymmetricPosition(int x, int y, int z) {
		return model.grid[x][y][z].subtract(oldGrid[x][y][z]);
	}

	@Override
	public String getName() {
		return model.getName();
	}
	
	@Override
	public String getSubfolderPath() {
		return model.getSubfolderPath() + "/delta";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
		model.backUp(backupPath, backupName);
	}
	
}
