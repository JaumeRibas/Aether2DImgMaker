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


public class Model5DXCrossSection<Source_Type extends Model5D> implements Model4D {

	protected final Source_Type source;
	protected final int x;
	
	public Model5DXCrossSection(Source_Type source, int x) {
		if (x > source.getMaxX() || x < source.getMinX()) {
			throw new IllegalArgumentException("The cross section is out of bounds.");
		}
		this.source = source;
		this.x = x;
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
	    return source.getYLabel();
	}

	@Override
	public String getZLabel() {
	    return source.getZLabel();
	}

	@Override
	public int getMinW() {
	    return source.getMinVAtX(this.x);
	}

	@Override
	public int getMaxW() {
	    return source.getMaxVAtX(this.x);
	}

	@Override
	public int getMinWAtX(int x) {
	    return source.getMinVAtWX(x, this.x);
	}

	@Override
	public int getMaxWAtX(int x) {
	    return source.getMaxVAtWX(x, this.x);
	}

	@Override
	public int getMinWAtY(int y) {
	    return source.getMinVAtXY(this.x, y);
	}

	@Override
	public int getMaxWAtY(int y) {
	    return source.getMaxVAtXY(this.x, y);
	}

	@Override
	public int getMinWAtZ(int z) {
	    return source.getMinVAtXZ(this.x, z);
	}

	@Override
	public int getMaxWAtZ(int z) {
	    return source.getMaxVAtXZ(this.x, z);
	}

	@Override
	public int getMinWAtXY(int x, int y) {
	    return source.getMinVAtWXY(x, this.x, y);
	}

	@Override
	public int getMaxWAtXY(int x, int y) {
	    return source.getMaxVAtWXY(x, this.x, y);
	}

	@Override
	public int getMinWAtXZ(int x, int z) {
	    return source.getMinVAtWXZ(x, this.x, z);
	}

	@Override
	public int getMaxWAtXZ(int x, int z) {
	    return source.getMaxVAtWXZ(x, this.x, z);
	}

	@Override
	public int getMinWAtYZ(int y, int z) {
	    return source.getMinVAtXYZ(this.x, y, z);
	}

	@Override
	public int getMaxWAtYZ(int y, int z) {
	    return source.getMaxVAtXYZ(this.x, y, z);
	}

	@Override
	public int getMinW(int x, int y, int z) {
	    return source.getMinV(x, this.x, y, z);
	}

	@Override
	public int getMaxW(int x, int y, int z) {
	    return source.getMaxV(x, this.x, y, z);
	}

	@Override
	public int getMinX() {
	    return source.getMinWAtX(this.x);
	}

	@Override
	public int getMaxX() {
	    return source.getMaxWAtX(this.x);
	}

	@Override
	public int getMinXAtW(int w) {
	    return source.getMinWAtVX(w, this.x);
	}

	@Override
	public int getMaxXAtW(int w) {
	    return source.getMaxWAtVX(w, this.x);
	}

	@Override
	public int getMinXAtY(int y) {
	    return source.getMinWAtXY(this.x, y);
	}

	@Override
	public int getMaxXAtY(int y) {
	    return source.getMaxWAtXY(this.x, y);
	}

	@Override
	public int getMinXAtZ(int z) {
	    return source.getMinWAtXZ(this.x, z);
	}

	@Override
	public int getMaxXAtZ(int z) {
	    return source.getMaxWAtXZ(this.x, z);
	}

	@Override
	public int getMinXAtWY(int w, int y) {
	    return source.getMinWAtVXY(w, this.x, y);
	}

	@Override
	public int getMaxXAtWY(int w, int y) {
	    return source.getMaxWAtVXY(w, this.x, y);
	}

	@Override
	public int getMinXAtWZ(int w, int z) {
	    return source.getMinWAtVXZ(w, this.x, z);
	}

	@Override
	public int getMaxXAtWZ(int w, int z) {
	    return source.getMaxWAtVXZ(w, this.x, z);
	}

	@Override
	public int getMinXAtYZ(int y, int z) {
	    return source.getMinWAtXYZ(this.x, y, z);
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
	    return source.getMaxWAtXYZ(this.x, y, z);
	}

	@Override
	public int getMinX(int w, int y, int z) {
	    return source.getMinW(w, this.x, y, z);
	}

	@Override
	public int getMaxX(int w, int y, int z) {
	    return source.getMaxW(w, this.x, y, z);
	}

	@Override
	public int getMinY() {
	    return source.getMinYAtX(this.x);
	}

	@Override
	public int getMaxY() {
	    return source.getMaxYAtX(this.x);
	}

	@Override
	public int getMinYAtW(int w) {
	    return source.getMinYAtVX(w, this.x);
	}

	@Override
	public int getMaxYAtW(int w) {
	    return source.getMaxYAtVX(w, this.x);
	}

	@Override
	public int getMinYAtX(int x) {
	    return source.getMinYAtWX(x, this.x);
	}

	@Override
	public int getMaxYAtX(int x) {
	    return source.getMaxYAtWX(x, this.x);
	}

	@Override
	public int getMinYAtZ(int z) {
	    return source.getMinYAtXZ(this.x, z);
	}

	@Override
	public int getMaxYAtZ(int z) {
	    return source.getMaxYAtXZ(this.x, z);
	}

	@Override
	public int getMinYAtWX(int w, int x) {
	    return source.getMinYAtVWX(w, x, this.x);
	}

	@Override
	public int getMaxYAtWX(int w, int x) {
	    return source.getMaxYAtVWX(w, x, this.x);
	}

	@Override
	public int getMinYAtWZ(int w, int z) {
	    return source.getMinYAtVXZ(w, this.x, z);
	}

	@Override
	public int getMaxYAtWZ(int w, int z) {
	    return source.getMaxYAtVXZ(w, this.x, z);
	}

	@Override
	public int getMinYAtXZ(int x, int z) {
	    return source.getMinYAtWXZ(x, this.x, z);
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
	    return source.getMaxYAtWXZ(x, this.x, z);
	}

	@Override
	public int getMinY(int w, int x, int z) {
	    return source.getMinY(w, x, this.x, z);
	}

	@Override
	public int getMaxY(int w, int x, int z) {
	    return source.getMaxY(w, x, this.x, z);
	}

	@Override
	public int getMinZ() {
	    return source.getMinZAtX(this.x);
	}

	@Override
	public int getMaxZ() {
	    return source.getMaxZAtX(this.x);
	}

	@Override
	public int getMinZAtW(int w) {
	    return source.getMinZAtVX(w, this.x);
	}

	@Override
	public int getMaxZAtW(int w) {
	    return source.getMaxZAtVX(w, this.x);
	}

	@Override
	public int getMinZAtX(int x) {
	    return source.getMinZAtWX(x, this.x);
	}

	@Override
	public int getMaxZAtX(int x) {
	    return source.getMaxZAtWX(x, this.x);
	}

	@Override
	public int getMinZAtY(int y) {
	    return source.getMinZAtXY(this.x, y);
	}

	@Override
	public int getMaxZAtY(int y) {
	    return source.getMaxZAtXY(this.x, y);
	}

	@Override
	public int getMinZAtWX(int w, int x) {
	    return source.getMinZAtVWX(w, x, this.x);
	}

	@Override
	public int getMaxZAtWX(int w, int x) {
	    return source.getMaxZAtVWX(w, x, this.x);
	}

	@Override
	public int getMinZAtWY(int w, int y) {
	    return source.getMinZAtVXY(w, this.x, y);
	}

	@Override
	public int getMaxZAtWY(int w, int y) {
	    return source.getMaxZAtVXY(w, this.x, y);
	}

	@Override
	public int getMinZAtXY(int x, int y) {
	    return source.getMinZAtWXY(x, this.x, y);
	}

	@Override
	public int getMaxZAtXY(int x, int y) {
	    return source.getMaxZAtWXY(x, this.x, y);
	}

	@Override
	public int getMinZ(int w, int x, int y) {
	    return source.getMinZ(w, x, this.x, y);
	}

	@Override
	public int getMaxZ(int w, int x, int y) {
	    return source.getMaxZ(w, x, this.x, y);
	}

	@Override
	public Boolean nextStep() throws Exception {
		Boolean changed = source.nextStep();
		if (x > source.getMaxX() || x < source.getMinX()) {
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
		return source.getSubfolderPath() + "/" + source.getXLabel() + "=" + x;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
		source.backUp(backupPath, backupName);
	}
}
