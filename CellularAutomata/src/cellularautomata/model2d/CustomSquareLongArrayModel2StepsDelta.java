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
package cellularautomata.model2d;

import cellularautomata.numbers.BigInt;

public class CustomSquareLongArrayModel2StepsDelta implements SymmetricNumericModel2D<BigInt>, IsotropicSquareModelA {	

	private long[][] previousStepGrid;
	private long[][] stepBeforePreviousGrid;
	private IsotropicSquareLongArrayModelA model;
	private int sizeDifference;

	public CustomSquareLongArrayModel2StepsDelta(IsotropicSquareLongArrayModelA model) throws Exception {
		this.model = model;
		stepBeforePreviousGrid = model.grid.clone();
		model.nextStep();
		previousStepGrid = model.grid.clone();
		model.nextStep();
		sizeDifference = model.grid.length - stepBeforePreviousGrid.length;
	}
	
	@Override
	public Boolean nextStep() throws Exception {
		stepBeforePreviousGrid = null;
		stepBeforePreviousGrid = previousStepGrid;
		previousStepGrid = model.grid.clone();//Clone it because it gets progressively cleared
		Boolean result = model.nextStep();
		sizeDifference = model.grid.length - stepBeforePreviousGrid.length;
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
	public BigInt getFromPosition(int x, int y) {	
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		BigInt value;
		if (y > x) {
			value = getFromAsymmetricPosition(y, x);
		} else {
			value = getFromAsymmetricPosition(x, y);
		}
		return value;
	}

	@Override
	public BigInt getFromAsymmetricPosition(int x, int y) {
		return BigInt.valueOf(model.grid[x][y]).subtract(BigInt.valueOf(stepBeforePreviousGrid[x][y]));
	}

	@Override
	public String getName() {
		return model.getName();
	}
	
	@Override
	public String getSubfolderPath() {
		return model.getSubfolderPath() + "/two_steps_delta";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
		model.backUp(backupPath, backupName);
	}
	
}
