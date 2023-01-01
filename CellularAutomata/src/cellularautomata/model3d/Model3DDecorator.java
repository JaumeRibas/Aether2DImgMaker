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
import cellularautomata.model.ModelDecorator;
import cellularautomata.model2d.Model2D;

public class Model3DDecorator<Source_Type extends Model3D> extends ModelDecorator<Source_Type> implements Model3D {

	public Model3DDecorator(Source_Type source) {
		super(source);
	}

	@Override
	public String getXLabel() {
		return source.getXLabel();
	}
	
	@Override
	public String getYLabel() {
		return source.getYLabel();
	}
	
	@Override
	public String getZLabel() {
		return source.getYLabel();
	}

	@Override
	public int getMinX() { return source.getMinX(); }

	@Override
	public int getMaxX() { return source.getMaxX(); }

	@Override
	public int getMinXAtY(int y) { return source.getMinXAtY(y); }

	@Override
	public int getMaxXAtY(int y) { return source.getMaxXAtY(y); }

	@Override
	public int getMinXAtZ(int z) { return source.getMinXAtZ(z); }

	@Override
	public int getMaxXAtZ(int z) { return source.getMaxXAtZ(z); }

	@Override
	public int getMinX(int y, int z) { return source.getMinX(y, z); }

	@Override
	public int getMaxX(int y, int z) { return source.getMaxX(y, z); }

	@Override
	public int getMinY() { return source.getMinY(); }

	@Override
	public int getMaxY() { return source.getMaxY(); }

	@Override
	public int getMinYAtX(int x) { return source.getMinYAtX(x); }

	@Override
	public int getMaxYAtX(int x) { return source.getMaxYAtX(x); }

	@Override
	public int getMinYAtZ(int z) { return source.getMinYAtZ(z); }

	@Override
	public int getMaxYAtZ(int z) { return source.getMaxYAtZ(z); }

	@Override
	public int getMinY(int x, int z) { return source.getMinY(x, z); }

	@Override
	public int getMaxY(int x, int z) { return source.getMaxY(x, z); }

	@Override
	public int getMinZ() { return source.getMinZ(); }

	@Override
	public int getMaxZ() { return source.getMaxZ(); }

	@Override
	public int getMinZAtX(int x) { return source.getMinZAtX(x); }

	@Override
	public int getMaxZAtX(int x) { return source.getMaxZAtX(x); }

	@Override
	public int getMinZAtY(int y) { return source.getMinZAtY(y); }

	@Override
	public int getMaxZAtY(int y) { return source.getMaxZAtY(y); }

	@Override
	public int getMinZ(int x, int y) { return source.getMinZ(x, y); }

	@Override
	public int getMaxZ(int x, int y) { return source.getMaxZ(x, y); }
	
	@Override
	public Model3D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return source.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	public Model3D subsection(Integer minX, Integer maxX, Integer minY, Integer maxY, Integer minZ, Integer maxZ) {
		return source.subsection(minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	public Model2D crossSection(int axis, int coordinate) {
		return source.crossSection(axis, coordinate);
	}

	@Override
	public Model2D crossSectionAtX(int x) {
		return source.crossSectionAtX(x);
	}

	@Override
	public Model2D crossSectionAtY(int y) {
		return source.crossSectionAtY(y);
	}

	@Override
	public Model2D crossSectionAtZ(int z) {
		return source.crossSectionAtZ(z);
	}
	
	@Override
	public Model2D diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return source.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}

	@Override
	public Model2D diagonalCrossSectionOnXY(boolean positiveSlope, int yOffsetFromX) {
		return source.diagonalCrossSectionOnXY(positiveSlope, yOffsetFromX);
	}

	@Override
	public Model2D diagonalCrossSectionOnXZ(boolean positiveSlope, int zOffsetFromX) {
		return source.diagonalCrossSectionOnXZ(positiveSlope, zOffsetFromX);
	}

	public Model2D diagonalCrossSectionOnYZ(boolean positiveSlope, int zOffsetFromY) {
		return source.diagonalCrossSectionOnYZ(positiveSlope, zOffsetFromY);
	}

}
