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
package cellularautomata.model;

import java.util.function.Consumer;

import cellularautomata.Coordinates;
import cellularautomata.PartialCoordinates;

public class AsymmetricModelSection<Source_Type extends SymmetricModel> implements Model {
	
	protected Source_Type source;
	
	public AsymmetricModelSection(Source_Type source) {
		this.source = source;
	}

	@Override
	public int getGridDimension() {
		return source.getGridDimension();
	}

	@Override
	public String getAxisLabel(int axis) {
		return source.getAxisLabel(axis);
	}

	@Override
	public int getMinCoordinate(int axis) {
		return source.getAsymmetricMinCoordinate(axis);
	}

	@Override
	public int getMinCoordinate(int axis, PartialCoordinates coordinates) {
		return source.getAsymmetricMinCoordinate(axis, coordinates);
	}

	@Override
	public int getMaxCoordinate(int axis) {
		return source.getAsymmetricMaxCoordinate(axis);
	}

	@Override
	public int getMaxCoordinate(int axis, PartialCoordinates coordinates) {
		return source.getAsymmetricMaxCoordinate(axis, coordinates);
	}

	@Override
	public void forEachPosition(Consumer<? super Coordinates> consumer) {
		source.forEachPositionInAsymmetricSection(consumer);
	}
	
	@Override
	public void forEachEvenPosition(Consumer<? super Coordinates> consumer) {
		source.forEachEvenPositionInAsymmetricSection(consumer);
	}
	
	@Override
	public void forEachOddPosition(Consumer<? super Coordinates> consumer) {
		source.forEachOddPositionInAsymmetricSection(consumer);
	}

	@Override
	public String getSubfolderPath() {
		return source.getSubfolderPath() + "/asymmetric_section";
	}

	@Override
	public Boolean nextStep() throws Exception {
		return source.nextStep();
	}

	@Override
	public Boolean isChanged() {
		return source.isChanged();
	}

	@Override
	public long getStep() {
		return source.getStep();
	}

	@Override
	public String getName() {
		return source.getName();
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
		source.backUp(backupPath, backupName);
	}
}
