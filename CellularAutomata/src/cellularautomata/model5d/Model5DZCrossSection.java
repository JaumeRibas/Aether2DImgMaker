/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2025 Jaume Ribas

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

import cellularautomata.model4d.Model4D;


public class Model5DZCrossSection<Source_Type extends Model5D> implements Model4D {

	protected Source_Type source;
	protected int z;
	
	public Model5DZCrossSection(Source_Type source, int z) {
		if (z > source.getMaxZ() || z < source.getMinZ()) {
			throw new IllegalArgumentException("The cross section is out of bounds.");
		}
		this.source = source;
		this.z = z;
	}
	
	@Override
	public String getWLabel() {
	    return source.getVLabel();
	}

	@Override
	public String getXLabel() {
	    return source.getWLabel();
	}

	@Override
	public String getYLabel() {
	    return source.getXLabel();
	}

	@Override
	public String getZLabel() {
	    return source.getYLabel();
	}

	@Override
	public int getMinW() {
	    return source.getMinVAtZ(this.z);
	}

	@Override
	public int getMaxW() {
	    return source.getMaxVAtZ(this.z);
	}

	@Override
	public int getMinWAtX(int x) {
	    return source.getMinVAtWZ(x, this.z);
	}

	@Override
	public int getMaxWAtX(int x) {
	    return source.getMaxVAtWZ(x, this.z);
	}

	@Override
	public int getMinWAtY(int y) {
	    return source.getMinVAtXZ(y, this.z);
	}

	@Override
	public int getMaxWAtY(int y) {
	    return source.getMaxVAtXZ(y, this.z);
	}

	@Override
	public int getMinWAtZ(int z) {
	    return source.getMinVAtYZ(z, this.z);
	}

	@Override
	public int getMaxWAtZ(int z) {
	    return source.getMaxVAtYZ(z, this.z);
	}

	@Override
	public int getMinWAtXY(int x, int y) {
	    return source.getMinVAtWXZ(x, y, this.z);
	}

	@Override
	public int getMaxWAtXY(int x, int y) {
	    return source.getMaxVAtWXZ(x, y, this.z);
	}

	@Override
	public int getMinWAtXZ(int x, int z) {
	    return source.getMinVAtWYZ(x, z, this.z);
	}

	@Override
	public int getMaxWAtXZ(int x, int z) {
	    return source.getMaxVAtWYZ(x, z, this.z);
	}

	@Override
	public int getMinWAtYZ(int y, int z) {
	    return source.getMinVAtXYZ(y, z, this.z);
	}

	@Override
	public int getMaxWAtYZ(int y, int z) {
	    return source.getMaxVAtXYZ(y, z, this.z);
	}

	@Override
	public int getMinW(int x, int y, int z) {
	    return source.getMinV(x, y, z, this.z);
	}

	@Override
	public int getMaxW(int x, int y, int z) {
	    return source.getMaxV(x, y, z, this.z);
	}

	@Override
	public int getMinX() {
	    return source.getMinWAtZ(this.z);
	}

	@Override
	public int getMaxX() {
	    return source.getMaxWAtZ(this.z);
	}

	@Override
	public int getMinXAtW(int w) {
	    return source.getMinWAtVZ(w, this.z);
	}

	@Override
	public int getMaxXAtW(int w) {
	    return source.getMaxWAtVZ(w, this.z);
	}

	@Override
	public int getMinXAtY(int y) {
	    return source.getMinWAtXZ(y, this.z);
	}

	@Override
	public int getMaxXAtY(int y) {
	    return source.getMaxWAtXZ(y, this.z);
	}

	@Override
	public int getMinXAtZ(int z) {
	    return source.getMinWAtYZ(z, this.z);
	}

	@Override
	public int getMaxXAtZ(int z) {
	    return source.getMaxWAtYZ(z, this.z);
	}

	@Override
	public int getMinXAtWY(int w, int y) {
	    return source.getMinWAtVXZ(w, y, this.z);
	}

	@Override
	public int getMaxXAtWY(int w, int y) {
	    return source.getMaxWAtVXZ(w, y, this.z);
	}

	@Override
	public int getMinXAtWZ(int w, int z) {
	    return source.getMinWAtVYZ(w, z, this.z);
	}

	@Override
	public int getMaxXAtWZ(int w, int z) {
	    return source.getMaxWAtVYZ(w, z, this.z);
	}

	@Override
	public int getMinXAtYZ(int y, int z) {
	    return source.getMinWAtXYZ(y, z, this.z);
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
	    return source.getMaxWAtXYZ(y, z, this.z);
	}

	@Override
	public int getMinX(int w, int y, int z) {
	    return source.getMinW(w, y, z, this.z);
	}

	@Override
	public int getMaxX(int w, int y, int z) {
	    return source.getMaxW(w, y, z, this.z);
	}

	@Override
	public int getMinY() {
	    return source.getMinXAtZ(this.z);
	}

	@Override
	public int getMaxY() {
	    return source.getMaxXAtZ(this.z);
	}

	@Override
	public int getMinYAtW(int w) {
	    return source.getMinXAtVZ(w, this.z);
	}

	@Override
	public int getMaxYAtW(int w) {
	    return source.getMaxXAtVZ(w, this.z);
	}

	@Override
	public int getMinYAtX(int x) {
	    return source.getMinXAtWZ(x, this.z);
	}

	@Override
	public int getMaxYAtX(int x) {
	    return source.getMaxXAtWZ(x, this.z);
	}

	@Override
	public int getMinYAtZ(int z) {
	    return source.getMinXAtYZ(z, this.z);
	}

	@Override
	public int getMaxYAtZ(int z) {
	    return source.getMaxXAtYZ(z, this.z);
	}

	@Override
	public int getMinYAtWX(int w, int x) {
	    return source.getMinXAtVWZ(w, x, this.z);
	}

	@Override
	public int getMaxYAtWX(int w, int x) {
	    return source.getMaxXAtVWZ(w, x, this.z);
	}

	@Override
	public int getMinYAtWZ(int w, int z) {
	    return source.getMinXAtVYZ(w, z, this.z);
	}

	@Override
	public int getMaxYAtWZ(int w, int z) {
	    return source.getMaxXAtVYZ(w, z, this.z);
	}

	@Override
	public int getMinYAtXZ(int x, int z) {
	    return source.getMinXAtWYZ(x, z, this.z);
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
	    return source.getMaxXAtWYZ(x, z, this.z);
	}

	@Override
	public int getMinY(int w, int x, int z) {
	    return source.getMinX(w, x, z, this.z);
	}

	@Override
	public int getMaxY(int w, int x, int z) {
	    return source.getMaxX(w, x, z, this.z);
	}

	@Override
	public int getMinZ() {
	    return source.getMinYAtZ(this.z);
	}

	@Override
	public int getMaxZ() {
	    return source.getMaxYAtZ(this.z);
	}

	@Override
	public int getMinZAtW(int w) {
	    return source.getMinYAtVZ(w, this.z);
	}

	@Override
	public int getMaxZAtW(int w) {
	    return source.getMaxYAtVZ(w, this.z);
	}

	@Override
	public int getMinZAtX(int x) {
	    return source.getMinYAtWZ(x, this.z);
	}

	@Override
	public int getMaxZAtX(int x) {
	    return source.getMaxYAtWZ(x, this.z);
	}

	@Override
	public int getMinZAtY(int y) {
	    return source.getMinYAtXZ(y, this.z);
	}

	@Override
	public int getMaxZAtY(int y) {
	    return source.getMaxYAtXZ(y, this.z);
	}

	@Override
	public int getMinZAtWX(int w, int x) {
	    return source.getMinYAtVWZ(w, x, this.z);
	}

	@Override
	public int getMaxZAtWX(int w, int x) {
	    return source.getMaxYAtVWZ(w, x, this.z);
	}

	@Override
	public int getMinZAtWY(int w, int y) {
	    return source.getMinYAtVXZ(w, y, this.z);
	}

	@Override
	public int getMaxZAtWY(int w, int y) {
	    return source.getMaxYAtVXZ(w, y, this.z);
	}

	@Override
	public int getMinZAtXY(int x, int y) {
	    return source.getMinYAtWXZ(x, y, this.z);
	}

	@Override
	public int getMaxZAtXY(int x, int y) {
	    return source.getMaxYAtWXZ(x, y, this.z);
	}

	@Override
	public int getMinZ(int w, int x, int y) {
	    return source.getMinY(w, x, y, this.z);
	}

	@Override
	public int getMaxZ(int w, int x, int y) {
	    return source.getMaxY(w, x, y, this.z);
	}

	@Override
	public Boolean nextStep() throws Exception {
		Boolean changed = source.nextStep();
		if (z > source.getMaxZ() || z < source.getMinZ()) {
			throw new UnsupportedOperationException("The cross section is out of bounds.");
		}
		return changed;
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
	public String getSubfolderPath() {
		return source.getSubfolderPath() + "/" + source.getZLabel() + "=" + z;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
		source.backUp(backupPath, backupName);
	}
}
