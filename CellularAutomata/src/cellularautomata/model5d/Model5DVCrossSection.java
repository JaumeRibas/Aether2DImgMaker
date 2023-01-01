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
package cellularautomata.model5d;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.model4d.Model4D;


public class Model5DVCrossSection<Source_Type extends Model5D> implements Model4D {

	protected Source_Type source;
	protected int v;
	
	public Model5DVCrossSection(Source_Type source, int v) {
		if (v > source.getMaxV() || v < source.getMinV()) {
			throw new IllegalArgumentException("The cross section is out of bounds.");
		}
		this.source = source;
		this.v = v;
	}
	
	@Override
	public String getWLabel() {
	    return source.getWLabel();
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
	    return source.getMinWAtV(this.v);
	}

	@Override
	public int getMaxW() {
	    return source.getMaxWAtV(this.v);
	}

	@Override
	public int getMinWAtX(int x) {
	    return source.getMinWAtVX(this.v, x);
	}

	@Override
	public int getMaxWAtX(int x) {
	    return source.getMaxWAtVX(this.v, x);
	}

	@Override
	public int getMinWAtY(int y) {
	    return source.getMinWAtVY(this.v, y);
	}

	@Override
	public int getMaxWAtY(int y) {
	    return source.getMaxWAtVY(this.v, y);
	}

	@Override
	public int getMinWAtZ(int z) {
	    return source.getMinWAtVZ(this.v, z);
	}

	@Override
	public int getMaxWAtZ(int z) {
	    return source.getMaxWAtVZ(this.v, z);
	}

	@Override
	public int getMinWAtXY(int x, int y) {
	    return source.getMinWAtVXY(this.v, x, y);
	}

	@Override
	public int getMaxWAtXY(int x, int y) {
	    return source.getMaxWAtVXY(this.v, x, y);
	}

	@Override
	public int getMinWAtXZ(int x, int z) {
	    return source.getMinWAtVXZ(this.v, x, z);
	}

	@Override
	public int getMaxWAtXZ(int x, int z) {
	    return source.getMaxWAtVXZ(this.v, x, z);
	}

	@Override
	public int getMinWAtYZ(int y, int z) {
	    return source.getMinWAtVYZ(this.v, y, z);
	}

	@Override
	public int getMaxWAtYZ(int y, int z) {
	    return source.getMaxWAtVYZ(this.v, y, z);
	}

	@Override
	public int getMinW(int x, int y, int z) {
	    return source.getMinW(this.v, x, y, z);
	}

	@Override
	public int getMaxW(int x, int y, int z) {
	    return source.getMaxW(this.v, x, y, z);
	}

	@Override
	public int getMinX() {
	    return source.getMinXAtV(this.v);
	}

	@Override
	public int getMaxX() {
	    return source.getMaxXAtV(this.v);
	}

	@Override
	public int getMinXAtW(int w) {
	    return source.getMinXAtVW(this.v, w);
	}

	@Override
	public int getMaxXAtW(int w) {
	    return source.getMaxXAtVW(this.v, w);
	}

	@Override
	public int getMinXAtY(int y) {
	    return source.getMinXAtVY(this.v, y);
	}

	@Override
	public int getMaxXAtY(int y) {
	    return source.getMaxXAtVY(this.v, y);
	}

	@Override
	public int getMinXAtZ(int z) {
	    return source.getMinXAtVZ(this.v, z);
	}

	@Override
	public int getMaxXAtZ(int z) {
	    return source.getMaxXAtVZ(this.v, z);
	}

	@Override
	public int getMinXAtWY(int w, int y) {
	    return source.getMinXAtVWY(this.v, w, y);
	}

	@Override
	public int getMaxXAtWY(int w, int y) {
	    return source.getMaxXAtVWY(this.v, w, y);
	}

	@Override
	public int getMinXAtWZ(int w, int z) {
	    return source.getMinXAtVWZ(this.v, w, z);
	}

	@Override
	public int getMaxXAtWZ(int w, int z) {
	    return source.getMaxXAtVWZ(this.v, w, z);
	}

	@Override
	public int getMinXAtYZ(int y, int z) {
	    return source.getMinXAtVYZ(this.v, y, z);
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
	    return source.getMaxXAtVYZ(this.v, y, z);
	}

	@Override
	public int getMinX(int w, int y, int z) {
	    return source.getMinX(this.v, w, y, z);
	}

	@Override
	public int getMaxX(int w, int y, int z) {
	    return source.getMaxX(this.v, w, y, z);
	}

	@Override
	public int getMinY() {
	    return source.getMinYAtV(this.v);
	}

	@Override
	public int getMaxY() {
	    return source.getMaxYAtV(this.v);
	}

	@Override
	public int getMinYAtW(int w) {
	    return source.getMinYAtVW(this.v, w);
	}

	@Override
	public int getMaxYAtW(int w) {
	    return source.getMaxYAtVW(this.v, w);
	}

	@Override
	public int getMinYAtX(int x) {
	    return source.getMinYAtVX(this.v, x);
	}

	@Override
	public int getMaxYAtX(int x) {
	    return source.getMaxYAtVX(this.v, x);
	}

	@Override
	public int getMinYAtZ(int z) {
	    return source.getMinYAtVZ(this.v, z);
	}

	@Override
	public int getMaxYAtZ(int z) {
	    return source.getMaxYAtVZ(this.v, z);
	}

	@Override
	public int getMinYAtWX(int w, int x) {
	    return source.getMinYAtVWX(this.v, w, x);
	}

	@Override
	public int getMaxYAtWX(int w, int x) {
	    return source.getMaxYAtVWX(this.v, w, x);
	}

	@Override
	public int getMinYAtWZ(int w, int z) {
	    return source.getMinYAtVWZ(this.v, w, z);
	}

	@Override
	public int getMaxYAtWZ(int w, int z) {
	    return source.getMaxYAtVWZ(this.v, w, z);
	}

	@Override
	public int getMinYAtXZ(int x, int z) {
	    return source.getMinYAtVXZ(this.v, x, z);
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
	    return source.getMaxYAtVXZ(this.v, x, z);
	}

	@Override
	public int getMinY(int w, int x, int z) {
	    return source.getMinY(this.v, w, x, z);
	}

	@Override
	public int getMaxY(int w, int x, int z) {
	    return source.getMaxY(this.v, w, x, z);
	}

	@Override
	public int getMinZ() {
	    return source.getMinZAtV(this.v);
	}

	@Override
	public int getMaxZ() {
	    return source.getMaxZAtV(this.v);
	}

	@Override
	public int getMinZAtW(int w) {
	    return source.getMinZAtVW(this.v, w);
	}

	@Override
	public int getMaxZAtW(int w) {
	    return source.getMaxZAtVW(this.v, w);
	}

	@Override
	public int getMinZAtX(int x) {
	    return source.getMinZAtVX(this.v, x);
	}

	@Override
	public int getMaxZAtX(int x) {
	    return source.getMaxZAtVX(this.v, x);
	}

	@Override
	public int getMinZAtY(int y) {
	    return source.getMinZAtVY(this.v, y);
	}

	@Override
	public int getMaxZAtY(int y) {
	    return source.getMaxZAtVY(this.v, y);
	}

	@Override
	public int getMinZAtWX(int w, int x) {
	    return source.getMinZAtVWX(this.v, w, x);
	}

	@Override
	public int getMaxZAtWX(int w, int x) {
	    return source.getMaxZAtVWX(this.v, w, x);
	}

	@Override
	public int getMinZAtWY(int w, int y) {
	    return source.getMinZAtVWY(this.v, w, y);
	}

	@Override
	public int getMaxZAtWY(int w, int y) {
	    return source.getMaxZAtVWY(this.v, w, y);
	}

	@Override
	public int getMinZAtXY(int x, int y) {
	    return source.getMinZAtVXY(this.v, x, y);
	}

	@Override
	public int getMaxZAtXY(int x, int y) {
	    return source.getMaxZAtVXY(this.v, x, y);
	}

	@Override
	public int getMinZ(int w, int x, int y) {
	    return source.getMinZ(this.v, w, x, y);
	}

	@Override
	public int getMaxZ(int w, int x, int y) {
	    return source.getMaxZ(this.v, w, x, y);
	}

	@Override
	public Boolean nextStep() throws Exception {
		Boolean changed = source.nextStep();
		if (v > source.getMaxV() || v < source.getMinV()) {
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
		return source.getSubfolderPath() + "/" + source.getVLabel() + "=" + v;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}
}
