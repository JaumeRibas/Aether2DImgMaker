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
package cellularautomata.grid;

import java.util.function.Consumer;

public class AsymmetricGridSection<G extends SymmetricGrid> implements GridRegion {

	protected G source;
	
	public AsymmetricGridSection(G source) {
		this.source = source;
	}

	@Override
	public int getGridDimension() {
		return source.getGridDimension();
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
	public void forEachPosition(Consumer<Coordinates> consumer) {
		source.forEachPositionInAsymmetricSection(consumer);
	}
	
	@Override
	public void forEachEvenPosition(Consumer<Coordinates> consumer) {
		source.forEachEvenPositionInAsymmetricSection(consumer);
	}
	
	@Override
	public void forEachOddPosition(Consumer<Coordinates> consumer) {
		source.forEachOddPositionInAsymmetricSection(consumer);
	}
}
