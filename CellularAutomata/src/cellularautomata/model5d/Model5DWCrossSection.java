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


public class Model5DWCrossSection<Source_Type extends Model5D> implements Model4D {

	protected Source_Type source;
	protected int w;
	
	public Model5DWCrossSection(Source_Type source, int w) {
		if (w > source.getMaxW() || w < source.getMinW()) {
			throw new IllegalArgumentException("The cross section is out of bounds.");
		}
		this.source = source;
		this.w = w;
	}
	
	@Override
	public String getWLabel() {
	    return source.getVLabel();
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
	    return source.getZLabel();
	}

	@Override
	public int getMinW() {
	    return source.getMinVAtW(this.w);
	}

	@Override
	public int getMaxW() {
	    return source.getMaxVAtW(this.w);
	}

	@Override
	public int getMinWAtX(int x) {
	    return source.getMinVAtWX(this.w, x);
	}

	@Override
	public int getMaxWAtX(int x) {
	    return source.getMaxVAtWX(this.w, x);
	}

	@Override
	public int getMinWAtY(int y) {
	    return source.getMinVAtWY(this.w, y);
	}

	@Override
	public int getMaxWAtY(int y) {
	    return source.getMaxVAtWY(this.w, y);
	}

	@Override
	public int getMinWAtZ(int z) {
	    return source.getMinVAtWZ(this.w, z);
	}

	@Override
	public int getMaxWAtZ(int z) {
	    return source.getMaxVAtWZ(this.w, z);
	}

	@Override
	public int getMinWAtXY(int x, int y) {
	    return source.getMinVAtWXY(this.w, x, y);
	}

	@Override
	public int getMaxWAtXY(int x, int y) {
	    return source.getMaxVAtWXY(this.w, x, y);
	}

	@Override
	public int getMinWAtXZ(int x, int z) {
	    return source.getMinVAtWXZ(this.w, x, z);
	}

	@Override
	public int getMaxWAtXZ(int x, int z) {
	    return source.getMaxVAtWXZ(this.w, x, z);
	}

	@Override
	public int getMinWAtYZ(int y, int z) {
	    return source.getMinVAtWYZ(this.w, y, z);
	}

	@Override
	public int getMaxWAtYZ(int y, int z) {
	    return source.getMaxVAtWYZ(this.w, y, z);
	}

	@Override
	public int getMinW(int x, int y, int z) {
	    return source.getMinV(this.w, x, y, z);
	}

	@Override
	public int getMaxW(int x, int y, int z) {
	    return source.getMaxV(this.w, x, y, z);
	}

	@Override
	public int getMinX() {
	    return source.getMinXAtW(this.w);
	}

	@Override
	public int getMaxX() {
	    return source.getMaxXAtW(this.w);
	}

	@Override
	public int getMinXAtW(int w) {
	    return source.getMinXAtVW(w, this.w);
	}

	@Override
	public int getMaxXAtW(int w) {
	    return source.getMaxXAtVW(w, this.w);
	}

	@Override
	public int getMinXAtY(int y) {
	    return source.getMinXAtWY(this.w, y);
	}

	@Override
	public int getMaxXAtY(int y) {
	    return source.getMaxXAtWY(this.w, y);
	}

	@Override
	public int getMinXAtZ(int z) {
	    return source.getMinXAtWZ(this.w, z);
	}

	@Override
	public int getMaxXAtZ(int z) {
	    return source.getMaxXAtWZ(this.w, z);
	}

	@Override
	public int getMinXAtWY(int w, int y) {
	    return source.getMinXAtVWY(w, this.w, y);
	}

	@Override
	public int getMaxXAtWY(int w, int y) {
	    return source.getMaxXAtVWY(w, this.w, y);
	}

	@Override
	public int getMinXAtWZ(int w, int z) {
	    return source.getMinXAtVWZ(w, this.w, z);
	}

	@Override
	public int getMaxXAtWZ(int w, int z) {
	    return source.getMaxXAtVWZ(w, this.w, z);
	}

	@Override
	public int getMinXAtYZ(int y, int z) {
	    return source.getMinXAtWYZ(this.w, y, z);
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
	    return source.getMaxXAtWYZ(this.w, y, z);
	}

	@Override
	public int getMinX(int w, int y, int z) {
	    return source.getMinX(w, this.w, y, z);
	}

	@Override
	public int getMaxX(int w, int y, int z) {
	    return source.getMaxX(w, this.w, y, z);
	}

	@Override
	public int getMinY() {
	    return source.getMinYAtW(this.w);
	}

	@Override
	public int getMaxY() {
	    return source.getMaxYAtW(this.w);
	}

	@Override
	public int getMinYAtW(int w) {
	    return source.getMinYAtVW(w, this.w);
	}

	@Override
	public int getMaxYAtW(int w) {
	    return source.getMaxYAtVW(w, this.w);
	}

	@Override
	public int getMinYAtX(int x) {
	    return source.getMinYAtWX(this.w, x);
	}

	@Override
	public int getMaxYAtX(int x) {
	    return source.getMaxYAtWX(this.w, x);
	}

	@Override
	public int getMinYAtZ(int z) {
	    return source.getMinYAtWZ(this.w, z);
	}

	@Override
	public int getMaxYAtZ(int z) {
	    return source.getMaxYAtWZ(this.w, z);
	}

	@Override
	public int getMinYAtWX(int w, int x) {
	    return source.getMinYAtVWX(w, this.w, x);
	}

	@Override
	public int getMaxYAtWX(int w, int x) {
	    return source.getMaxYAtVWX(w, this.w, x);
	}

	@Override
	public int getMinYAtWZ(int w, int z) {
	    return source.getMinYAtVWZ(w, this.w, z);
	}

	@Override
	public int getMaxYAtWZ(int w, int z) {
	    return source.getMaxYAtVWZ(w, this.w, z);
	}

	@Override
	public int getMinYAtXZ(int x, int z) {
	    return source.getMinYAtWXZ(this.w, x, z);
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
	    return source.getMaxYAtWXZ(this.w, x, z);
	}

	@Override
	public int getMinY(int w, int x, int z) {
	    return source.getMinY(w, this.w, x, z);
	}

	@Override
	public int getMaxY(int w, int x, int z) {
	    return source.getMaxY(w, this.w, x, z);
	}

	@Override
	public int getMinZ() {
	    return source.getMinZAtW(this.w);
	}

	@Override
	public int getMaxZ() {
	    return source.getMaxZAtW(this.w);
	}

	@Override
	public int getMinZAtW(int w) {
	    return source.getMinZAtVW(w, this.w);
	}

	@Override
	public int getMaxZAtW(int w) {
	    return source.getMaxZAtVW(w, this.w);
	}

	@Override
	public int getMinZAtX(int x) {
	    return source.getMinZAtWX(this.w, x);
	}

	@Override
	public int getMaxZAtX(int x) {
	    return source.getMaxZAtWX(this.w, x);
	}

	@Override
	public int getMinZAtY(int y) {
	    return source.getMinZAtWY(this.w, y);
	}

	@Override
	public int getMaxZAtY(int y) {
	    return source.getMaxZAtWY(this.w, y);
	}

	@Override
	public int getMinZAtWX(int w, int x) {
	    return source.getMinZAtVWX(w, this.w, x);
	}

	@Override
	public int getMaxZAtWX(int w, int x) {
	    return source.getMaxZAtVWX(w, this.w, x);
	}

	@Override
	public int getMinZAtWY(int w, int y) {
	    return source.getMinZAtVWY(w, this.w, y);
	}

	@Override
	public int getMaxZAtWY(int w, int y) {
	    return source.getMaxZAtVWY(w, this.w, y);
	}

	@Override
	public int getMinZAtXY(int x, int y) {
	    return source.getMinZAtWXY(this.w, x, y);
	}

	@Override
	public int getMaxZAtXY(int x, int y) {
	    return source.getMaxZAtWXY(this.w, x, y);
	}

	@Override
	public int getMinZ(int w, int x, int y) {
	    return source.getMinZ(w, this.w, x, y);
	}

	@Override
	public int getMaxZ(int w, int x, int y) {
	    return source.getMaxZ(w, this.w, x, y);
	}

	@Override
	public Boolean nextStep() throws Exception {
		Boolean changed = source.nextStep();
		if (w > source.getMaxW() || w < source.getMinW()) {
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
		return source.getSubfolderPath() + "/" + source.getWLabel() + "=" + w;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
		source.backUp(backupPath, backupName);
	}
}
