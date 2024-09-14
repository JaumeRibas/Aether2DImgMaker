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
package cellularautomata.model1d;

import cellularautomata.numbers.BigInt;

public class IsotropicLongArrayModel1D2StepsDelta implements SymmetricNumericModel1D<BigInt>, IsotropicModel1DA {	

	private long[] previousStepGrid;
	private long[] stepBeforePreviousGrid;
	private IsotropicLongArrayModel1DA model;
	private int sizeDifference;

	public IsotropicLongArrayModel1D2StepsDelta(IsotropicLongArrayModel1DA model) throws Exception {
		this.model = model;
		stepBeforePreviousGrid = model.grid;
		model.nextStep();
		previousStepGrid = model.grid;
		model.nextStep();
		sizeDifference = model.grid.length - stepBeforePreviousGrid.length;
	}
	
	@Override
	public Boolean nextStep() throws Exception {
		stepBeforePreviousGrid = previousStepGrid;
		previousStepGrid = model.grid;
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
	public BigInt getFromPosition(int x) {	
		if (x < 0) x = -x;
		return getFromAsymmetricPosition(x);
	}

	@Override
	public BigInt getFromAsymmetricPosition(int x) {
		return BigInt.valueOf(model.grid[x]).subtract(BigInt.valueOf(stepBeforePreviousGrid[x]));
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
