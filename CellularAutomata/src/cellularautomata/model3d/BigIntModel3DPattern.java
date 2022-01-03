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
package cellularautomata.model3d;

import org.apache.commons.math3.fraction.BigFraction;

import cellularautomata.model.MinAndMax;
import cellularautomata.numbers.BigInt;

public class BigIntModel3DPattern<T extends NumericModel3D<BigInt>> implements NumericModel3D<BigFraction> {

	protected T source;
	protected BigInt min;
	protected BigInt range;
	
	public BigIntModel3DPattern(T grid) throws Exception {
		this.source = grid;
		MinAndMax<BigInt> minMax = grid.getMinAndMax();
		min = minMax.getMin();
		range = minMax.getMax().subtract(min);
	}

	@Override
	public int getMinX() {
		return source.getMinX();
	}

	@Override
	public int getMaxX() {
		return source.getMaxX();
	}
	
	@Override
	public int getMinXAtY(int y) {
		return source.getMinXAtY(y);
	}

	@Override
	public int getMaxXAtY(int y) {
		return source.getMaxXAtY(y);
	}
	
	@Override
	public int getMinXAtZ(int z) {
		return source.getMinXAtZ(z);
	}

	@Override
	public int getMaxXAtZ(int z) {
		return source.getMaxXAtZ(z);
	}
	
	@Override
	public int getMinX(int y, int z) {
		return source.getMinX(y, z);
	}

	@Override
	public int getMaxX(int y, int z) {
		return source.getMaxX(y, z);
	}

	@Override
	public int getMinY() {
		return source.getMinY();
	}

	@Override
	public int getMaxY() {
		return source.getMaxY();
	}
	
	@Override
	public int getMinYAtX(int x) {
		return source.getMinYAtX(x);
	}

	@Override
	public int getMaxYAtX(int x) {
		return source.getMaxYAtX(x);
	}
	
	@Override
	public int getMinYAtZ(int z) {
		return source.getMinYAtZ(z);
	}

	@Override
	public int getMaxYAtZ(int z) {
		return source.getMaxYAtZ(z);
	}
	
	@Override
	public int getMinY(int x, int z) {
		return source.getMinY(x, z);
	}

	@Override
	public int getMaxY(int x, int z) {
		return source.getMaxY(x, z);
	}
	
	@Override
	public int getMinZ() {
		return source.getMinZ();
	}

	@Override
	public int getMaxZ() {
		return source.getMaxZ();
	}
	
	@Override
	public int getMinZAtY(int y) {
		return source.getMinZAtY(y);
	}

	@Override
	public int getMaxZAtY(int y) {
		return source.getMaxZAtY(y);
	}
	
	@Override
	public int getMinZAtX(int x) {
		return source.getMinZAtX(x);
	}

	@Override
	public int getMaxZAtX(int x) {
		return source.getMaxZAtX(x);
	}
	
	@Override
	public int getMinZ(int x, int y) {
		return source.getMinZ(x, y);
	}

	@Override
	public int getMaxZ(int x, int y) {
		return source.getMaxZ(x, y);
	}

	@Override
	public BigFraction getFromPosition(int x, int y, int z) throws Exception {
		BigInt value = source.getFromPosition(x, y, z);
		return new BigFraction(value.subtract(min).bigIntegerValue(), range.bigIntegerValue());
	}

}
