/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2021 Jaume Ribas

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
package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.math3.fraction.BigFraction;

import cellularautomata.grid.MinAndMax;
import cellularautomata.grid3d.BigIntGrid3DPattern;
import cellularautomata.numbers.BigInt;

public class EvolvingBigIntGrid3DPattern extends BigIntGrid3DPattern<EvolvingNumberGrid3D<BigInt>> implements EvolvingNumberGrid3D<BigFraction> {
	
	public EvolvingBigIntGrid3DPattern(EvolvingNumberGrid3D<BigInt> grid) throws Exception {
		super(grid);
	}

	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		MinAndMax<BigInt> minMax = source.getMinAndMax();
		min = minMax.getMin();
		range = minMax.getMax().subtract(min);
		return changed;
	}

	@Override
	public long getStep() {
		return source.getStep();
	}

	@Override
	public String getName() {
		return source.getName() + "_pattern";
	}

	@Override
	public String getSubFolderPath() {
		return source.getSubFolderPath() + "/pattern";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}

}
