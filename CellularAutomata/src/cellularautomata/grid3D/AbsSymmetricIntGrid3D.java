/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2018 Jaume Ribas

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
package cellularautomata.grid3D;

public class AbsSymmetricIntGrid3D implements SymmetricIntGrid3D {
	
	private SymmetricIntGrid3D source;
	
	public AbsSymmetricIntGrid3D(SymmetricIntGrid3D source) {
		this.source = source;
	}

	@Override
	public int getValueAtPosition(int x, int y, int z) throws Exception {
		return Math.abs(source.getValueAtPosition(x, y, z));
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
	public int getMinY() {
		return source.getMinY();
	}

	@Override
	public int getMaxY() {
		return source.getMaxY();
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
	public int getNonSymmetricMinX() {
		return source.getNonSymmetricMinX();
	}

	@Override
	public int getNonSymmetricMaxX() {
		return source.getNonSymmetricMaxX();
	}

	@Override
	public int getNonSymmetricMinY() {
		return source.getNonSymmetricMinY();
	}

	@Override
	public int getNonSymmetricMaxY() {
		return source.getNonSymmetricMaxY();
	}
	
	@Override
	public int getNonSymmetricMinZ() {
		return source.getNonSymmetricMinZ();
	}

	@Override
	public int getNonSymmetricMaxZ() {
		return source.getNonSymmetricMaxZ();
	}

	@Override
	public int getValueAtNonSymmetricPosition(int x, int y, int z) throws Exception {
		return Math.abs(source.getValueAtNonSymmetricPosition(x, y, z));
	}

	@Override
	public int getNonSymmetricMinXAtY(int y) {
		return source.getNonSymmetricMinXAtY(y);
	}

	@Override
	public int getNonSymmetricMinXAtZ(int z) {
		return source.getNonSymmetricMinXAtZ(z);
	}

	@Override
	public int getNonSymmetricMinX(int y, int z) {
		return source.getNonSymmetricMinX(y, z);
	}

	@Override
	public int getNonSymmetricMaxXAtY(int y) {
		return source.getNonSymmetricMaxXAtY(y);
	}

	@Override
	public int getNonSymmetricMaxXAtZ(int z) {
		return source.getNonSymmetricMaxXAtZ(z);
	}

	@Override
	public int getNonSymmetricMaxX(int y, int z) {
		return source.getNonSymmetricMaxX(y, z);
	}

	@Override
	public int getNonSymmetricMinYAtX(int x) {
		return source.getNonSymmetricMinYAtX(x);
	}

	@Override
	public int getNonSymmetricMinYAtZ(int z) {
		return source.getNonSymmetricMinYAtZ(z);
	}

	@Override
	public int getNonSymmetricMinY(int x, int z) {
		return source.getNonSymmetricMinY(x, z);
	}

	@Override
	public int getNonSymmetricMaxYAtX(int x) {
		return source.getNonSymmetricMaxYAtX(x);
	}

	@Override
	public int getNonSymmetricMaxYAtZ(int z) {
		return source.getNonSymmetricMaxYAtZ(z);
	}

	@Override
	public int getNonSymmetricMaxY(int x, int z) {
		return source.getNonSymmetricMaxY(x, z);
	}

	@Override
	public int getNonSymmetricMinZAtX(int x) {
		return source.getNonSymmetricMinZAtX(x);
	}

	@Override
	public int getNonSymmetricMinZAtY(int y) {
		return source.getNonSymmetricMinZAtY(y);
	}

	@Override
	public int getNonSymmetricMinZ(int x, int y) {
		return source.getNonSymmetricMinZ(x, y);
	}

	@Override
	public int getNonSymmetricMaxZAtX(int x) {
		return source.getNonSymmetricMaxZAtX(x);
	}

	@Override
	public int getNonSymmetricMaxZAtY(int y) {
		return source.getNonSymmetricMaxZAtY(y);
	}

	@Override
	public int getNonSymmetricMaxZ(int x, int y) {
		return source.getNonSymmetricMaxZ(x, y);
	}

}
