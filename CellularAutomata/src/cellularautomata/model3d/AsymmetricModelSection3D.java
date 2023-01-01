/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2023 Jaume Ribas

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
import cellularautomata.model.AsymmetricModelSection;
import cellularautomata.model2d.Model2D;

public class AsymmetricModelSection3D<Source_Type extends SymmetricModel3D> extends AsymmetricModelSection<Source_Type> implements Model3D {

	public AsymmetricModelSection3D(Source_Type source) {
		super(source);
	}

	@Override
	public int getMinX() {
		return source.getAsymmetricMinX();
	}

	@Override
	public int getMaxX() {
		return source.getAsymmetricMaxX();
	}
	
	@Override
	public int getMinXAtY(int y) {
		return source.getAsymmetricMinXAtY(y);
	}

	@Override
	public int getMaxXAtY(int y) {
		return source.getAsymmetricMaxXAtY(y);
	}
	
	@Override
	public int getMinXAtZ(int z) {
		return source.getAsymmetricMinXAtZ(z);
	}

	@Override
	public int getMaxXAtZ(int z) {
		return source.getAsymmetricMaxXAtZ(z);
	}
	
	@Override
	public int getMinX(int y, int z) {
		return source.getAsymmetricMinX(y, z);
	}

	@Override
	public int getMaxX(int y, int z) {
		return source.getAsymmetricMaxX(y, z);
	}

	@Override
	public int getMinY() {
		return source.getAsymmetricMinY();
	}

	@Override
	public int getMaxY() {
		return source.getAsymmetricMaxY();
	}
	
	@Override
	public int getMinYAtX(int x) {
		return source.getAsymmetricMinYAtX(x);
	}

	@Override
	public int getMaxYAtX(int x) {
		return source.getAsymmetricMaxYAtX(x);
	}
	
	@Override
	public int getMinYAtZ(int z) {
		return source.getAsymmetricMinYAtZ(z);
	}

	@Override
	public int getMaxYAtZ(int z) {
		return source.getAsymmetricMaxYAtZ(z);
	}
	
	@Override
	public int getMinY(int x, int z) {
		return source.getAsymmetricMinY(x, z);
	}

	@Override
	public int getMaxY(int x, int z) {
		return source.getAsymmetricMaxY(x, z);
	}
	
	@Override
	public int getMinZ() {
		return source.getAsymmetricMinZ();
	}

	@Override
	public int getMaxZ() {
		return source.getAsymmetricMaxZ();
	}
	
	@Override
	public int getMinZAtY(int y) {
		return source.getAsymmetricMinZAtY(y);
	}

	@Override
	public int getMaxZAtY(int y) {
		return source.getAsymmetricMaxZAtY(y);
	}
	
	@Override
	public int getMinZAtX(int x) {
		return source.getAsymmetricMinZAtX(x);
	}

	@Override
	public int getMaxZAtX(int x) {
		return source.getAsymmetricMaxZAtX(x);
	}
	
	@Override
	public int getMinZ(int x, int y) {
		return source.getAsymmetricMinZ(x, y);
	}

	@Override
	public int getMaxZ(int x, int y) {
		return source.getAsymmetricMaxZ(x, y);
	}
	
	@Override
	public Model3D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return Model3D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	public Model2D crossSection(int axis, int coordinate) {
		return Model3D.super.crossSection(axis, coordinate);
	}
	
	@Override
	public Model2D diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return Model3D.super.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}

}
