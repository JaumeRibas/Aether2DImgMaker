/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2020 Jaume Ribas

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
package cellularautomata.grid4d;

public abstract class AsymmetricGridSection4D implements Grid4D {

	protected SymmetricGrid4D source;
	
	public AsymmetricGridSection4D(SymmetricGrid4D grid) {
		super();
		this.source = grid;
	}
	
	@Override
	public int getMinW() {
		return source.getAsymmetricMinW();
	}

	@Override
	public int getMaxW() {
		return source.getAsymmetricMaxW();
	}
	
	@Override
	public int getMinW(int x, int y, int z) {
		return source.getAsymmetricMinW(x, y, z);
	}
	
	@Override
	public int getMaxW(int x, int y, int z) {
		return source.getAsymmetricMaxW(x, y, z);
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
	public int getMinY() {
		return source.getAsymmetricMinY();
	}

	@Override
	public int getMaxY() {
		return source.getAsymmetricMaxY();
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
	public int getMinWAtZ(int z) {
		return source.getAsymmetricMinWAtZ(z);
	}

	@Override
	public int getMinWAtXZ(int x, int z) {
		return source.getAsymmetricMinWAtXZ(x, z);
	}

	@Override
	public int getMinWAtYZ(int y, int z) {
		return source.getAsymmetricMinWAtYZ(y, z);
	}

	@Override
	public int getMaxWAtZ(int z) {
		return source.getAsymmetricMaxWAtZ(z);
	}

	@Override
	public int getMaxWAtXZ(int x, int z) {
		return source.getAsymmetricMaxWAtXZ(x, z);
	}

	@Override
	public int getMaxWAtYZ(int y, int z) {
		return source.getAsymmetricMaxWAtYZ(y, z);
	}

	@Override
	public int getMinXAtZ(int z) {
		return source.getAsymmetricMinXAtZ(z);
	}

	@Override
	public int getMinXAtWZ(int w, int z) {
		return source.getAsymmetricMinXAtWZ(w, z);
	}

	@Override
	public int getMinXAtYZ(int y, int z) {
		return source.getAsymmetricMinXAtYZ(y, z);
	}

	@Override
	public int getMinX(int w, int y, int z) {
		return source.getAsymmetricMinX(w, y, z);
	}

	@Override
	public int getMaxXAtZ(int z) {
		return source.getAsymmetricMaxXAtZ(z);
	}

	@Override
	public int getMaxXAtWZ(int w, int z) {
		return source.getAsymmetricMaxXAtWZ(w, z);
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
		return source.getAsymmetricMaxXAtYZ(y, z);
	}

	@Override
	public int getMaxX(int w, int y, int z) {
		return source.getAsymmetricMaxX(w, y, z);
	}

	@Override
	public int getMinYAtZ(int z) {
		return source.getAsymmetricMinYAtZ(z);
	}

	@Override
	public int getMaxYAtWZ(int w, int z) {
		return source.getAsymmetricMaxYAtWZ(w, z);
	}

	@Override
	public int getMinYAtXZ(int x, int z) {
		return source.getAsymmetricMinYAtXZ(x, z);
	}

	@Override
	public int getMinY(int w, int x, int z) {
		return source.getAsymmetricMinY(w, x, z);
	}

	@Override
	public int getMaxYAtZ(int z) {
		return source.getAsymmetricMaxYAtZ(z);
	}

	@Override
	public int getMinYAtWZ(int w, int z) {
		return source.getAsymmetricMinYAtWZ(w, z);
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
		return source.getAsymmetricMaxYAtXZ(x, z);
	}

	@Override
	public int getMaxY(int w, int x, int z) {
		return source.getAsymmetricMaxY(w, x, z);
	}

}
