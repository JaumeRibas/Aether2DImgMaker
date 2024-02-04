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
package cellularautomata.model5d;

import cellularautomata.model4d.Model4D;


public class Model5DYCrossSection<Source_Type extends Model5D> implements Model4D {

	protected Source_Type source;
	protected int y;
	
	public Model5DYCrossSection(Source_Type source, int y) {
		if (y > source.getMaxY() || y < source.getMinY()) {
			throw new IllegalArgumentException("The cross section is out of bounds.");
		}
		this.source = source;
		this.y = y;
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
	    return source.getZLabel();
	}

	@Override
	public int getMinW() {
	    return source.getMinVAtY(this.y);
	}

	@Override
	public int getMaxW() {
	    return source.getMaxVAtY(this.y);
	}

	@Override
	public int getMinWAtX(int x) {
	    return source.getMinVAtWY(x, this.y);
	}

	@Override
	public int getMaxWAtX(int x) {
	    return source.getMaxVAtWY(x, this.y);
	}

	@Override
	public int getMinWAtY(int y) {
	    return source.getMinVAtXY(y, this.y);
	}

	@Override
	public int getMaxWAtY(int y) {
	    return source.getMaxVAtXY(y, this.y);
	}

	@Override
	public int getMinWAtZ(int z) {
	    return source.getMinVAtYZ(this.y, z);
	}

	@Override
	public int getMaxWAtZ(int z) {
	    return source.getMaxVAtYZ(this.y, z);
	}

	@Override
	public int getMinWAtXY(int x, int y) {
	    return source.getMinVAtWXY(x, y, this.y);
	}

	@Override
	public int getMaxWAtXY(int x, int y) {
	    return source.getMaxVAtWXY(x, y, this.y);
	}

	@Override
	public int getMinWAtXZ(int x, int z) {
	    return source.getMinVAtWYZ(x, this.y, z);
	}

	@Override
	public int getMaxWAtXZ(int x, int z) {
	    return source.getMaxVAtWYZ(x, this.y, z);
	}

	@Override
	public int getMinWAtYZ(int y, int z) {
	    return source.getMinVAtXYZ(y, this.y, z);
	}

	@Override
	public int getMaxWAtYZ(int y, int z) {
	    return source.getMaxVAtXYZ(y, this.y, z);
	}

	@Override
	public int getMinW(int x, int y, int z) {
	    return source.getMinV(x, y, this.y, z);
	}

	@Override
	public int getMaxW(int x, int y, int z) {
	    return source.getMaxV(x, y, this.y, z);
	}

	@Override
	public int getMinX() {
	    return source.getMinWAtY(this.y);
	}

	@Override
	public int getMaxX() {
	    return source.getMaxWAtY(this.y);
	}

	@Override
	public int getMinXAtW(int w) {
	    return source.getMinWAtVY(w, this.y);
	}

	@Override
	public int getMaxXAtW(int w) {
	    return source.getMaxWAtVY(w, this.y);
	}

	@Override
	public int getMinXAtY(int y) {
	    return source.getMinWAtXY(y, this.y);
	}

	@Override
	public int getMaxXAtY(int y) {
	    return source.getMaxWAtXY(y, this.y);
	}

	@Override
	public int getMinXAtZ(int z) {
	    return source.getMinWAtYZ(this.y, z);
	}

	@Override
	public int getMaxXAtZ(int z) {
	    return source.getMaxWAtYZ(this.y, z);
	}

	@Override
	public int getMinXAtWY(int w, int y) {
	    return source.getMinWAtVXY(w, y, this.y);
	}

	@Override
	public int getMaxXAtWY(int w, int y) {
	    return source.getMaxWAtVXY(w, y, this.y);
	}

	@Override
	public int getMinXAtWZ(int w, int z) {
	    return source.getMinWAtVYZ(w, this.y, z);
	}

	@Override
	public int getMaxXAtWZ(int w, int z) {
	    return source.getMaxWAtVYZ(w, this.y, z);
	}

	@Override
	public int getMinXAtYZ(int y, int z) {
	    return source.getMinWAtXYZ(y, this.y, z);
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
	    return source.getMaxWAtXYZ(y, this.y, z);
	}

	@Override
	public int getMinX(int w, int y, int z) {
	    return source.getMinW(w, y, this.y, z);
	}

	@Override
	public int getMaxX(int w, int y, int z) {
	    return source.getMaxW(w, y, this.y, z);
	}

	@Override
	public int getMinY() {
	    return source.getMinXAtY(this.y);
	}

	@Override
	public int getMaxY() {
	    return source.getMaxXAtY(this.y);
	}

	@Override
	public int getMinYAtW(int w) {
	    return source.getMinXAtVY(w, this.y);
	}

	@Override
	public int getMaxYAtW(int w) {
	    return source.getMaxXAtVY(w, this.y);
	}

	@Override
	public int getMinYAtX(int x) {
	    return source.getMinXAtWY(x, this.y);
	}

	@Override
	public int getMaxYAtX(int x) {
	    return source.getMaxXAtWY(x, this.y);
	}

	@Override
	public int getMinYAtZ(int z) {
	    return source.getMinXAtYZ(this.y, z);
	}

	@Override
	public int getMaxYAtZ(int z) {
	    return source.getMaxXAtYZ(this.y, z);
	}

	@Override
	public int getMinYAtWX(int w, int x) {
	    return source.getMinXAtVWY(w, x, this.y);
	}

	@Override
	public int getMaxYAtWX(int w, int x) {
	    return source.getMaxXAtVWY(w, x, this.y);
	}

	@Override
	public int getMinYAtWZ(int w, int z) {
	    return source.getMinXAtVYZ(w, this.y, z);
	}

	@Override
	public int getMaxYAtWZ(int w, int z) {
	    return source.getMaxXAtVYZ(w, this.y, z);
	}

	@Override
	public int getMinYAtXZ(int x, int z) {
	    return source.getMinXAtWYZ(x, this.y, z);
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
	    return source.getMaxXAtWYZ(x, this.y, z);
	}

	@Override
	public int getMinY(int w, int x, int z) {
	    return source.getMinX(w, x, this.y, z);
	}

	@Override
	public int getMaxY(int w, int x, int z) {
	    return source.getMaxX(w, x, this.y, z);
	}

	@Override
	public int getMinZ() {
	    return source.getMinZAtY(this.y);
	}

	@Override
	public int getMaxZ() {
	    return source.getMaxZAtY(this.y);
	}

	@Override
	public int getMinZAtW(int w) {
	    return source.getMinZAtVY(w, this.y);
	}

	@Override
	public int getMaxZAtW(int w) {
	    return source.getMaxZAtVY(w, this.y);
	}

	@Override
	public int getMinZAtX(int x) {
	    return source.getMinZAtWY(x, this.y);
	}

	@Override
	public int getMaxZAtX(int x) {
	    return source.getMaxZAtWY(x, this.y);
	}

	@Override
	public int getMinZAtY(int y) {
	    return source.getMinZAtXY(y, this.y);
	}

	@Override
	public int getMaxZAtY(int y) {
	    return source.getMaxZAtXY(y, this.y);
	}

	@Override
	public int getMinZAtWX(int w, int x) {
	    return source.getMinZAtVWY(w, x, this.y);
	}

	@Override
	public int getMaxZAtWX(int w, int x) {
	    return source.getMaxZAtVWY(w, x, this.y);
	}

	@Override
	public int getMinZAtWY(int w, int y) {
	    return source.getMinZAtVXY(w, y, this.y);
	}

	@Override
	public int getMaxZAtWY(int w, int y) {
	    return source.getMaxZAtVXY(w, y, this.y);
	}

	@Override
	public int getMinZAtXY(int x, int y) {
	    return source.getMinZAtWXY(x, y, this.y);
	}

	@Override
	public int getMaxZAtXY(int x, int y) {
	    return source.getMaxZAtWXY(x, y, this.y);
	}

	@Override
	public int getMinZ(int w, int x, int y) {
	    return source.getMinZ(w, x, y, this.y);
	}

	@Override
	public int getMaxZ(int w, int x, int y) {
	    return source.getMaxZ(w, x, y, this.y);
	}

	@Override
	public Boolean nextStep() throws Exception {
		Boolean changed = source.nextStep();
		if (y > source.getMaxY() || y < source.getMinY()) {
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
		return source.getSubfolderPath() + "/" + source.getYLabel() + "=" + y;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
		source.backUp(backupPath, backupName);
	}
}
