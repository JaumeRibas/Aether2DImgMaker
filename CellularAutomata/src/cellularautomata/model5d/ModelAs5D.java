/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2022 Jaume Ribas

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
package cellularautomata.model5d;

import cellularautomata.model.Model;
import cellularautomata.model.ModelDecorator;
import cellularautomata.PartialCoordinates;
import cellularautomata.model4d.Model4D;

public class ModelAs5D<Source_Type extends Model> extends ModelDecorator<Source_Type> implements Model5D {

	public ModelAs5D(Source_Type source) {
		super(source);
		int dimension = source.getGridDimension();
		if (dimension != 5) {
			throw new IllegalArgumentException("Model's grid dimension (" + dimension + ") must be 5.");
		}
	}
	
	@Override
	public String getVLabel() {
		return source.getAxisLabel(0);
	}
	
	@Override
	public String getWLabel() {
		return source.getAxisLabel(1);
	}
	
	@Override
	public String getXLabel() {
		return source.getAxisLabel(2);
	}
	
	@Override
	public String getYLabel() {
		return source.getAxisLabel(3);
	}
	
	@Override
	public String getZLabel() {
		return source.getAxisLabel(4);
	}
	
	@Override
	public int getMinV() {
	    return source.getMinCoordinate(0);
	}

	@Override
	public int getMaxV() {
	    return source.getMaxCoordinate(0);
	}

	@Override
	public int getMinVAtW(int w) {
	    return source.getMinCoordinate(0, new PartialCoordinates(null, w, null, null, null));
	}

	@Override
	public int getMaxVAtW(int w) {
	    return source.getMaxCoordinate(0, new PartialCoordinates(null, w, null, null, null));
	}

	@Override
	public int getMinVAtX(int x) {
	    return source.getMinCoordinate(0, new PartialCoordinates(null, null, x, null, null));
	}

	@Override
	public int getMaxVAtX(int x) {
	    return source.getMaxCoordinate(0, new PartialCoordinates(null, null, x, null, null));
	}

	@Override
	public int getMinVAtY(int y) {
	    return source.getMinCoordinate(0, new PartialCoordinates(null, null, null, y, null));
	}

	@Override
	public int getMaxVAtY(int y) {
	    return source.getMaxCoordinate(0, new PartialCoordinates(null, null, null, y, null));
	}

	@Override
	public int getMinVAtZ(int z) {
	    return source.getMinCoordinate(0, new PartialCoordinates(null, null, null, null, z));
	}

	@Override
	public int getMaxVAtZ(int z) {
	    return source.getMaxCoordinate(0, new PartialCoordinates(null, null, null, null, z));
	}

	@Override
	public int getMinVAtWX(int w, int x) {
	    return source.getMinCoordinate(0, new PartialCoordinates(null, w, x, null, null));
	}

	@Override
	public int getMaxVAtWX(int w, int x) {
	    return source.getMaxCoordinate(0, new PartialCoordinates(null, w, x, null, null));
	}

	@Override
	public int getMinVAtWY(int w, int y) {
	    return source.getMinCoordinate(0, new PartialCoordinates(null, w, null, y, null));
	}

	@Override
	public int getMaxVAtWY(int w, int y) {
	    return source.getMaxCoordinate(0, new PartialCoordinates(null, w, null, y, null));
	}

	@Override
	public int getMinVAtWZ(int w, int z) {
	    return source.getMinCoordinate(0, new PartialCoordinates(null, w, null, null, z));
	}

	@Override
	public int getMaxVAtWZ(int w, int z) {
	    return source.getMaxCoordinate(0, new PartialCoordinates(null, w, null, null, z));
	}

	@Override
	public int getMinVAtXY(int x, int y) {
	    return source.getMinCoordinate(0, new PartialCoordinates(null, null, x, y, null));
	}

	@Override
	public int getMaxVAtXY(int x, int y) {
	    return source.getMaxCoordinate(0, new PartialCoordinates(null, null, x, y, null));
	}

	@Override
	public int getMinVAtXZ(int x, int z) {
	    return source.getMinCoordinate(0, new PartialCoordinates(null, null, x, null, z));
	}

	@Override
	public int getMaxVAtXZ(int x, int z) {
	    return source.getMaxCoordinate(0, new PartialCoordinates(null, null, x, null, z));
	}

	@Override
	public int getMinVAtYZ(int y, int z) {
	    return source.getMinCoordinate(0, new PartialCoordinates(null, null, null, y, z));
	}

	@Override
	public int getMaxVAtYZ(int y, int z) {
	    return source.getMaxCoordinate(0, new PartialCoordinates(null, null, null, y, z));
	}

	@Override
	public int getMinVAtWXY(int w, int x, int y) {
	    return source.getMinCoordinate(0, new PartialCoordinates(null, w, x, y, null));
	}

	@Override
	public int getMaxVAtWXY(int w, int x, int y) {
	    return source.getMaxCoordinate(0, new PartialCoordinates(null, w, x, y, null));
	}

	@Override
	public int getMinVAtWXZ(int w, int x, int z) {
	    return source.getMinCoordinate(0, new PartialCoordinates(null, w, x, null, z));
	}

	@Override
	public int getMaxVAtWXZ(int w, int x, int z) {
	    return source.getMaxCoordinate(0, new PartialCoordinates(null, w, x, null, z));
	}

	@Override
	public int getMinVAtWYZ(int w, int y, int z) {
	    return source.getMinCoordinate(0, new PartialCoordinates(null, w, null, y, z));
	}

	@Override
	public int getMaxVAtWYZ(int w, int y, int z) {
	    return source.getMaxCoordinate(0, new PartialCoordinates(null, w, null, y, z));
	}

	@Override
	public int getMinVAtXYZ(int x, int y, int z) {
	    return source.getMinCoordinate(0, new PartialCoordinates(null, null, x, y, z));
	}

	@Override
	public int getMaxVAtXYZ(int x, int y, int z) {
	    return source.getMaxCoordinate(0, new PartialCoordinates(null, null, x, y, z));
	}

	@Override
	public int getMinV(int w, int x, int y, int z) {
	    return source.getMinCoordinate(0, new PartialCoordinates(null, w, x, y, z));
	}

	@Override
	public int getMaxV(int w, int x, int y, int z) {
	    return source.getMaxCoordinate(0, new PartialCoordinates(null, w, x, y, z));
	}

	@Override
	public int getMinW() {
	    return source.getMinCoordinate(1);
	}

	@Override
	public int getMaxW() {
	    return source.getMaxCoordinate(1);
	}

	@Override
	public int getMinWAtV(int v) {
	    return source.getMinCoordinate(1, new PartialCoordinates(v, null, null, null, null));
	}

	@Override
	public int getMaxWAtV(int v) {
	    return source.getMaxCoordinate(1, new PartialCoordinates(v, null, null, null, null));
	}

	@Override
	public int getMinWAtX(int x) {
	    return source.getMinCoordinate(1, new PartialCoordinates(null, null, x, null, null));
	}

	@Override
	public int getMaxWAtX(int x) {
	    return source.getMaxCoordinate(1, new PartialCoordinates(null, null, x, null, null));
	}

	@Override
	public int getMinWAtY(int y) {
	    return source.getMinCoordinate(1, new PartialCoordinates(null, null, null, y, null));
	}

	@Override
	public int getMaxWAtY(int y) {
	    return source.getMaxCoordinate(1, new PartialCoordinates(null, null, null, y, null));
	}

	@Override
	public int getMinWAtZ(int z) {
	    return source.getMinCoordinate(1, new PartialCoordinates(null, null, null, null, z));
	}

	@Override
	public int getMaxWAtZ(int z) {
	    return source.getMaxCoordinate(1, new PartialCoordinates(null, null, null, null, z));
	}

	@Override
	public int getMinWAtVX(int v, int x) {
	    return source.getMinCoordinate(1, new PartialCoordinates(v, null, x, null, null));
	}

	@Override
	public int getMaxWAtVX(int v, int x) {
	    return source.getMaxCoordinate(1, new PartialCoordinates(v, null, x, null, null));
	}

	@Override
	public int getMinWAtVY(int v, int y) {
	    return source.getMinCoordinate(1, new PartialCoordinates(v, null, null, y, null));
	}

	@Override
	public int getMaxWAtVY(int v, int y) {
	    return source.getMaxCoordinate(1, new PartialCoordinates(v, null, null, y, null));
	}

	@Override
	public int getMinWAtVZ(int v, int z) {
	    return source.getMinCoordinate(1, new PartialCoordinates(v, null, null, null, z));
	}

	@Override
	public int getMaxWAtVZ(int v, int z) {
	    return source.getMaxCoordinate(1, new PartialCoordinates(v, null, null, null, z));
	}

	@Override
	public int getMinWAtXY(int x, int y) {
	    return source.getMinCoordinate(1, new PartialCoordinates(null, null, x, y, null));
	}

	@Override
	public int getMaxWAtXY(int x, int y) {
	    return source.getMaxCoordinate(1, new PartialCoordinates(null, null, x, y, null));
	}

	@Override
	public int getMinWAtXZ(int x, int z) {
	    return source.getMinCoordinate(1, new PartialCoordinates(null, null, x, null, z));
	}

	@Override
	public int getMaxWAtXZ(int x, int z) {
	    return source.getMaxCoordinate(1, new PartialCoordinates(null, null, x, null, z));
	}

	@Override
	public int getMinWAtYZ(int y, int z) {
	    return source.getMinCoordinate(1, new PartialCoordinates(null, null, null, y, z));
	}

	@Override
	public int getMaxWAtYZ(int y, int z) {
	    return source.getMaxCoordinate(1, new PartialCoordinates(null, null, null, y, z));
	}

	@Override
	public int getMinWAtVXY(int v, int x, int y) {
	    return source.getMinCoordinate(1, new PartialCoordinates(v, null, x, y, null));
	}

	@Override
	public int getMaxWAtVXY(int v, int x, int y) {
	    return source.getMaxCoordinate(1, new PartialCoordinates(v, null, x, y, null));
	}

	@Override
	public int getMinWAtVXZ(int v, int x, int z) {
	    return source.getMinCoordinate(1, new PartialCoordinates(v, null, x, null, z));
	}

	@Override
	public int getMaxWAtVXZ(int v, int x, int z) {
	    return source.getMaxCoordinate(1, new PartialCoordinates(v, null, x, null, z));
	}

	@Override
	public int getMinWAtVYZ(int v, int y, int z) {
	    return source.getMinCoordinate(1, new PartialCoordinates(v, null, null, y, z));
	}

	@Override
	public int getMaxWAtVYZ(int v, int y, int z) {
	    return source.getMaxCoordinate(1, new PartialCoordinates(v, null, null, y, z));
	}

	@Override
	public int getMinWAtXYZ(int x, int y, int z) {
	    return source.getMinCoordinate(1, new PartialCoordinates(null, null, x, y, z));
	}

	@Override
	public int getMaxWAtXYZ(int x, int y, int z) {
	    return source.getMaxCoordinate(1, new PartialCoordinates(null, null, x, y, z));
	}

	@Override
	public int getMinW(int v, int x, int y, int z) {
	    return source.getMinCoordinate(1, new PartialCoordinates(v, null, x, y, z));
	}

	@Override
	public int getMaxW(int v, int x, int y, int z) {
	    return source.getMaxCoordinate(1, new PartialCoordinates(v, null, x, y, z));
	}

	@Override
	public int getMinX() {
	    return source.getMinCoordinate(2);
	}

	@Override
	public int getMaxX() {
	    return source.getMaxCoordinate(2);
	}

	@Override
	public int getMinXAtV(int v) {
	    return source.getMinCoordinate(2, new PartialCoordinates(v, null, null, null, null));
	}

	@Override
	public int getMaxXAtV(int v) {
	    return source.getMaxCoordinate(2, new PartialCoordinates(v, null, null, null, null));
	}

	@Override
	public int getMinXAtW(int w) {
	    return source.getMinCoordinate(2, new PartialCoordinates(null, w, null, null, null));
	}

	@Override
	public int getMaxXAtW(int w) {
	    return source.getMaxCoordinate(2, new PartialCoordinates(null, w, null, null, null));
	}

	@Override
	public int getMinXAtY(int y) {
	    return source.getMinCoordinate(2, new PartialCoordinates(null, null, null, y, null));
	}

	@Override
	public int getMaxXAtY(int y) {
	    return source.getMaxCoordinate(2, new PartialCoordinates(null, null, null, y, null));
	}

	@Override
	public int getMinXAtZ(int z) {
	    return source.getMinCoordinate(2, new PartialCoordinates(null, null, null, null, z));
	}

	@Override
	public int getMaxXAtZ(int z) {
	    return source.getMaxCoordinate(2, new PartialCoordinates(null, null, null, null, z));
	}

	@Override
	public int getMinXAtVW(int v, int w) {
	    return source.getMinCoordinate(2, new PartialCoordinates(v, w, null, null, null));
	}

	@Override
	public int getMaxXAtVW(int v, int w) {
	    return source.getMaxCoordinate(2, new PartialCoordinates(v, w, null, null, null));
	}

	@Override
	public int getMinXAtVY(int v, int y) {
	    return source.getMinCoordinate(2, new PartialCoordinates(v, null, null, y, null));
	}

	@Override
	public int getMaxXAtVY(int v, int y) {
	    return source.getMaxCoordinate(2, new PartialCoordinates(v, null, null, y, null));
	}

	@Override
	public int getMinXAtVZ(int v, int z) {
	    return source.getMinCoordinate(2, new PartialCoordinates(v, null, null, null, z));
	}

	@Override
	public int getMaxXAtVZ(int v, int z) {
	    return source.getMaxCoordinate(2, new PartialCoordinates(v, null, null, null, z));
	}

	@Override
	public int getMinXAtWY(int w, int y) {
	    return source.getMinCoordinate(2, new PartialCoordinates(null, w, null, y, null));
	}

	@Override
	public int getMaxXAtWY(int w, int y) {
	    return source.getMaxCoordinate(2, new PartialCoordinates(null, w, null, y, null));
	}

	@Override
	public int getMinXAtWZ(int w, int z) {
	    return source.getMinCoordinate(2, new PartialCoordinates(null, w, null, null, z));
	}

	@Override
	public int getMaxXAtWZ(int w, int z) {
	    return source.getMaxCoordinate(2, new PartialCoordinates(null, w, null, null, z));
	}

	@Override
	public int getMinXAtYZ(int y, int z) {
	    return source.getMinCoordinate(2, new PartialCoordinates(null, null, null, y, z));
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
	    return source.getMaxCoordinate(2, new PartialCoordinates(null, null, null, y, z));
	}

	@Override
	public int getMinXAtVWY(int v, int w, int y) {
	    return source.getMinCoordinate(2, new PartialCoordinates(v, w, null, y, null));
	}

	@Override
	public int getMaxXAtVWY(int v, int w, int y) {
	    return source.getMaxCoordinate(2, new PartialCoordinates(v, w, null, y, null));
	}

	@Override
	public int getMinXAtVWZ(int v, int w, int z) {
	    return source.getMinCoordinate(2, new PartialCoordinates(v, w, null, null, z));
	}

	@Override
	public int getMaxXAtVWZ(int v, int w, int z) {
	    return source.getMaxCoordinate(2, new PartialCoordinates(v, w, null, null, z));
	}

	@Override
	public int getMinXAtVYZ(int v, int y, int z) {
	    return source.getMinCoordinate(2, new PartialCoordinates(v, null, null, y, z));
	}

	@Override
	public int getMaxXAtVYZ(int v, int y, int z) {
	    return source.getMaxCoordinate(2, new PartialCoordinates(v, null, null, y, z));
	}

	@Override
	public int getMinXAtWYZ(int w, int y, int z) {
	    return source.getMinCoordinate(2, new PartialCoordinates(null, w, null, y, z));
	}

	@Override
	public int getMaxXAtWYZ(int w, int y, int z) {
	    return source.getMaxCoordinate(2, new PartialCoordinates(null, w, null, y, z));
	}

	@Override
	public int getMinX(int v, int w, int y, int z) {
	    return source.getMinCoordinate(2, new PartialCoordinates(v, w, null, y, z));
	}

	@Override
	public int getMaxX(int v, int w, int y, int z) {
	    return source.getMaxCoordinate(2, new PartialCoordinates(v, w, null, y, z));
	}

	@Override
	public int getMinY() {
	    return source.getMinCoordinate(3);
	}

	@Override
	public int getMaxY() {
	    return source.getMaxCoordinate(3);
	}

	@Override
	public int getMinYAtV(int v) {
	    return source.getMinCoordinate(3, new PartialCoordinates(v, null, null, null, null));
	}

	@Override
	public int getMaxYAtV(int v) {
	    return source.getMaxCoordinate(3, new PartialCoordinates(v, null, null, null, null));
	}

	@Override
	public int getMinYAtW(int w) {
	    return source.getMinCoordinate(3, new PartialCoordinates(null, w, null, null, null));
	}

	@Override
	public int getMaxYAtW(int w) {
	    return source.getMaxCoordinate(3, new PartialCoordinates(null, w, null, null, null));
	}

	@Override
	public int getMinYAtX(int x) {
	    return source.getMinCoordinate(3, new PartialCoordinates(null, null, x, null, null));
	}

	@Override
	public int getMaxYAtX(int x) {
	    return source.getMaxCoordinate(3, new PartialCoordinates(null, null, x, null, null));
	}

	@Override
	public int getMinYAtZ(int z) {
	    return source.getMinCoordinate(3, new PartialCoordinates(null, null, null, null, z));
	}

	@Override
	public int getMaxYAtZ(int z) {
	    return source.getMaxCoordinate(3, new PartialCoordinates(null, null, null, null, z));
	}

	@Override
	public int getMinYAtVW(int v, int w) {
	    return source.getMinCoordinate(3, new PartialCoordinates(v, w, null, null, null));
	}

	@Override
	public int getMaxYAtVW(int v, int w) {
	    return source.getMaxCoordinate(3, new PartialCoordinates(v, w, null, null, null));
	}

	@Override
	public int getMinYAtVX(int v, int x) {
	    return source.getMinCoordinate(3, new PartialCoordinates(v, null, x, null, null));
	}

	@Override
	public int getMaxYAtVX(int v, int x) {
	    return source.getMaxCoordinate(3, new PartialCoordinates(v, null, x, null, null));
	}

	@Override
	public int getMinYAtVZ(int v, int z) {
	    return source.getMinCoordinate(3, new PartialCoordinates(v, null, null, null, z));
	}

	@Override
	public int getMaxYAtVZ(int v, int z) {
	    return source.getMaxCoordinate(3, new PartialCoordinates(v, null, null, null, z));
	}

	@Override
	public int getMinYAtWX(int w, int x) {
	    return source.getMinCoordinate(3, new PartialCoordinates(null, w, x, null, null));
	}

	@Override
	public int getMaxYAtWX(int w, int x) {
	    return source.getMaxCoordinate(3, new PartialCoordinates(null, w, x, null, null));
	}

	@Override
	public int getMinYAtWZ(int w, int z) {
	    return source.getMinCoordinate(3, new PartialCoordinates(null, w, null, null, z));
	}

	@Override
	public int getMaxYAtWZ(int w, int z) {
	    return source.getMaxCoordinate(3, new PartialCoordinates(null, w, null, null, z));
	}

	@Override
	public int getMinYAtXZ(int x, int z) {
	    return source.getMinCoordinate(3, new PartialCoordinates(null, null, x, null, z));
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
	    return source.getMaxCoordinate(3, new PartialCoordinates(null, null, x, null, z));
	}

	@Override
	public int getMinYAtVWX(int v, int w, int x) {
	    return source.getMinCoordinate(3, new PartialCoordinates(v, w, x, null, null));
	}

	@Override
	public int getMaxYAtVWX(int v, int w, int x) {
	    return source.getMaxCoordinate(3, new PartialCoordinates(v, w, x, null, null));
	}

	@Override
	public int getMinYAtVWZ(int v, int w, int z) {
	    return source.getMinCoordinate(3, new PartialCoordinates(v, w, null, null, z));
	}

	@Override
	public int getMaxYAtVWZ(int v, int w, int z) {
	    return source.getMaxCoordinate(3, new PartialCoordinates(v, w, null, null, z));
	}

	@Override
	public int getMinYAtVXZ(int v, int x, int z) {
	    return source.getMinCoordinate(3, new PartialCoordinates(v, null, x, null, z));
	}

	@Override
	public int getMaxYAtVXZ(int v, int x, int z) {
	    return source.getMaxCoordinate(3, new PartialCoordinates(v, null, x, null, z));
	}

	@Override
	public int getMinYAtWXZ(int w, int x, int z) {
	    return source.getMinCoordinate(3, new PartialCoordinates(null, w, x, null, z));
	}

	@Override
	public int getMaxYAtWXZ(int w, int x, int z) {
	    return source.getMaxCoordinate(3, new PartialCoordinates(null, w, x, null, z));
	}

	@Override
	public int getMinY(int v, int w, int x, int z) {
	    return source.getMinCoordinate(3, new PartialCoordinates(v, w, x, null, z));
	}

	@Override
	public int getMaxY(int v, int w, int x, int z) {
	    return source.getMaxCoordinate(3, new PartialCoordinates(v, w, x, null, z));
	}

	@Override
	public int getMinZ() {
	    return source.getMinCoordinate(4);
	}

	@Override
	public int getMaxZ() {
	    return source.getMaxCoordinate(4);
	}

	@Override
	public int getMinZAtV(int v) {
	    return source.getMinCoordinate(4, new PartialCoordinates(v, null, null, null, null));
	}

	@Override
	public int getMaxZAtV(int v) {
	    return source.getMaxCoordinate(4, new PartialCoordinates(v, null, null, null, null));
	}

	@Override
	public int getMinZAtW(int w) {
	    return source.getMinCoordinate(4, new PartialCoordinates(null, w, null, null, null));
	}

	@Override
	public int getMaxZAtW(int w) {
	    return source.getMaxCoordinate(4, new PartialCoordinates(null, w, null, null, null));
	}

	@Override
	public int getMinZAtX(int x) {
	    return source.getMinCoordinate(4, new PartialCoordinates(null, null, x, null, null));
	}

	@Override
	public int getMaxZAtX(int x) {
	    return source.getMaxCoordinate(4, new PartialCoordinates(null, null, x, null, null));
	}

	@Override
	public int getMinZAtY(int y) {
	    return source.getMinCoordinate(4, new PartialCoordinates(null, null, null, y, null));
	}

	@Override
	public int getMaxZAtY(int y) {
	    return source.getMaxCoordinate(4, new PartialCoordinates(null, null, null, y, null));
	}

	@Override
	public int getMinZAtVW(int v, int w) {
	    return source.getMinCoordinate(4, new PartialCoordinates(v, w, null, null, null));
	}

	@Override
	public int getMaxZAtVW(int v, int w) {
	    return source.getMaxCoordinate(4, new PartialCoordinates(v, w, null, null, null));
	}

	@Override
	public int getMinZAtVX(int v, int x) {
	    return source.getMinCoordinate(4, new PartialCoordinates(v, null, x, null, null));
	}

	@Override
	public int getMaxZAtVX(int v, int x) {
	    return source.getMaxCoordinate(4, new PartialCoordinates(v, null, x, null, null));
	}

	@Override
	public int getMinZAtVY(int v, int y) {
	    return source.getMinCoordinate(4, new PartialCoordinates(v, null, null, y, null));
	}

	@Override
	public int getMaxZAtVY(int v, int y) {
	    return source.getMaxCoordinate(4, new PartialCoordinates(v, null, null, y, null));
	}

	@Override
	public int getMinZAtWX(int w, int x) {
	    return source.getMinCoordinate(4, new PartialCoordinates(null, w, x, null, null));
	}

	@Override
	public int getMaxZAtWX(int w, int x) {
	    return source.getMaxCoordinate(4, new PartialCoordinates(null, w, x, null, null));
	}

	@Override
	public int getMinZAtWY(int w, int y) {
	    return source.getMinCoordinate(4, new PartialCoordinates(null, w, null, y, null));
	}

	@Override
	public int getMaxZAtWY(int w, int y) {
	    return source.getMaxCoordinate(4, new PartialCoordinates(null, w, null, y, null));
	}

	@Override
	public int getMinZAtXY(int x, int y) {
	    return source.getMinCoordinate(4, new PartialCoordinates(null, null, x, y, null));
	}

	@Override
	public int getMaxZAtXY(int x, int y) {
	    return source.getMaxCoordinate(4, new PartialCoordinates(null, null, x, y, null));
	}

	@Override
	public int getMinZAtVWX(int v, int w, int x) {
	    return source.getMinCoordinate(4, new PartialCoordinates(v, w, x, null, null));
	}

	@Override
	public int getMaxZAtVWX(int v, int w, int x) {
	    return source.getMaxCoordinate(4, new PartialCoordinates(v, w, x, null, null));
	}

	@Override
	public int getMinZAtVWY(int v, int w, int y) {
	    return source.getMinCoordinate(4, new PartialCoordinates(v, w, null, y, null));
	}

	@Override
	public int getMaxZAtVWY(int v, int w, int y) {
	    return source.getMaxCoordinate(4, new PartialCoordinates(v, w, null, y, null));
	}

	@Override
	public int getMinZAtVXY(int v, int x, int y) {
	    return source.getMinCoordinate(4, new PartialCoordinates(v, null, x, y, null));
	}

	@Override
	public int getMaxZAtVXY(int v, int x, int y) {
	    return source.getMaxCoordinate(4, new PartialCoordinates(v, null, x, y, null));
	}

	@Override
	public int getMinZAtWXY(int w, int x, int y) {
	    return source.getMinCoordinate(4, new PartialCoordinates(null, w, x, y, null));
	}

	@Override
	public int getMaxZAtWXY(int w, int x, int y) {
	    return source.getMaxCoordinate(4, new PartialCoordinates(null, w, x, y, null));
	}

	@Override
	public int getMinZ(int v, int w, int x, int y) {
	    return source.getMinCoordinate(4, new PartialCoordinates(v, w, x, y, null));
	}

	@Override
	public int getMaxZ(int v, int w, int x, int y) {
	    return source.getMaxCoordinate(4, new PartialCoordinates(v, w, x, y, null));
	}
	
	@Override
	public Model5D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return Model5D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	public Model4D crossSection(int axis, int coordinate) {
		return Model5D.super.crossSection(axis, coordinate);
	}
	
}
