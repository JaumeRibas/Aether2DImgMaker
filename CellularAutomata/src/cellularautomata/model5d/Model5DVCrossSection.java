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

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.model4d.Model4D;


public class Model5DVCrossSection<G extends Model5D> implements Model4D {

	protected G source;
	protected int v;
	
	public Model5DVCrossSection(G source, int v) {
		if (v > source.getMaxV() || v < source.getMinV()) {
			throw new IllegalArgumentException("V coordinate is out of bounds.");
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
	public int getMinW() { return source.getMinWAtV(v); }

	@Override
	public int getMaxW() { return source.getMaxWAtV(v); }

	@Override
	public int getMinWAtX(int x) { return source.getMinWAtVX(v, x); }

	@Override
	public int getMaxWAtX(int x) { return source.getMaxWAtVX(v, x); }

	@Override
	public int getMinWAtY(int y) { return source.getMinWAtVY(v, y); }

	@Override
	public int getMaxWAtY(int y) { return source.getMaxWAtVY(v, y); }

	@Override
	public int getMinWAtZ(int z) { return source.getMinWAtVZ(v, z); }

	@Override
	public int getMaxWAtZ(int z) { return source.getMaxWAtVZ(v, z); }

	@Override
	public int getMinWAtXY(int x, int y) { return source.getMinWAtVXY(v, x, y); }

	@Override
	public int getMaxWAtXY(int x, int y) { return source.getMaxWAtVXY(v, x, y); }

	@Override
	public int getMinWAtXZ(int x, int z) { return source.getMinWAtVXZ(v, x, z); }

	@Override
	public int getMaxWAtXZ(int x, int z) { return source.getMaxWAtVXZ(v, x, z); }

	@Override
	public int getMinWAtYZ(int y, int z) { return source.getMinWAtVYZ(v, y, z); }

	@Override
	public int getMaxWAtYZ(int y, int z) { return source.getMaxWAtVYZ(v, y, z); }

	@Override
	public int getMinW(int x, int y, int z) { return source.getMinW(v, x, y, z); }

	@Override
	public int getMaxW(int x, int y, int z) { return source.getMaxW(v, x, y, z); }

	@Override
	public int getMinX() { return source.getMinXAtV(v); }

	@Override
	public int getMaxX() { return source.getMaxXAtV(v); }

	@Override
	public int getMinXAtW(int w) { return source.getMinXAtVW(v, w); }

	@Override
	public int getMaxXAtW(int w) { return source.getMaxXAtVW(v, w); }

	@Override
	public int getMinXAtY(int y) { return source.getMinXAtVY(v, y); }

	@Override
	public int getMaxXAtY(int y) { return source.getMaxXAtVY(v, y); }

	@Override
	public int getMinXAtZ(int z) { return source.getMinXAtVZ(v, z); }

	@Override
	public int getMaxXAtZ(int z) { return source.getMaxXAtVZ(v, z); }

	@Override
	public int getMinXAtWY(int w, int y) { return source.getMinXAtVWY(v, w, y); }

	@Override
	public int getMaxXAtWY(int w, int y) { return source.getMaxXAtVWY(v, w, y); }

	@Override
	public int getMinXAtWZ(int w, int z) { return source.getMinXAtVWZ(v, w, z); }

	@Override
	public int getMaxXAtWZ(int w, int z) { return source.getMaxXAtVWZ(v, w, z); }

	@Override
	public int getMinXAtYZ(int y, int z) { return source.getMinXAtVYZ(v, y, z); }

	@Override
	public int getMaxXAtYZ(int y, int z) { return source.getMaxXAtVYZ(v, y, z); }

	@Override
	public int getMinX(int w, int y, int z) { return source.getMinX(v, w, y, z); }

	@Override
	public int getMaxX(int w, int y, int z) { return source.getMaxX(v, w, y, z); }

	@Override
	public int getMinY() { return source.getMinYAtV(v); }

	@Override
	public int getMaxY() { return source.getMaxYAtV(v); }

	@Override
	public int getMinYAtW(int w) { return source.getMinYAtVW(v, w); }

	@Override
	public int getMaxYAtW(int w) { return source.getMaxYAtVW(v, w); }

	@Override
	public int getMinYAtX(int x) { return source.getMinYAtVX(v, x); }

	@Override
	public int getMaxYAtX(int x) { return source.getMaxYAtVX(v, x); }

	@Override
	public int getMinYAtZ(int z) { return source.getMinYAtVZ(v, z); }

	@Override
	public int getMaxYAtZ(int z) { return source.getMaxYAtVZ(v, z); }

	@Override
	public int getMinYAtWX(int w, int x) { return source.getMinYAtVWX(v, w, x); }

	@Override
	public int getMaxYAtWX(int w, int x) { return source.getMaxYAtVWX(v, w, x); }

	@Override
	public int getMinYAtWZ(int w, int z) { return source.getMinYAtVWZ(v, w, z); }

	@Override
	public int getMaxYAtWZ(int w, int z) { return source.getMaxYAtVWZ(v, w, z); }

	@Override
	public int getMinYAtXZ(int x, int z) { return source.getMinYAtVXZ(v, x, z); }

	@Override
	public int getMaxYAtXZ(int x, int z) { return source.getMaxYAtVXZ(v, x, z); }

	@Override
	public int getMinY(int w, int x, int z) { return source.getMinY(v, w, x, z); }

	@Override
	public int getMaxY(int w, int x, int z) { return source.getMaxY(v, w, x, z); }

	@Override
	public int getMinZ() { return source.getMinZAtV(v); }

	@Override
	public int getMaxZ() { return source.getMaxZAtV(v); }

	@Override
	public int getMinZAtW(int w) { return source.getMinZAtVW(v, w); }

	@Override
	public int getMaxZAtW(int w) { return source.getMaxZAtVW(v, w); }

	@Override
	public int getMinZAtX(int x) { return source.getMinZAtVX(v, x); }

	@Override
	public int getMaxZAtX(int x) { return source.getMaxZAtVX(v, x); }

	@Override
	public int getMinZAtY(int y) { return source.getMinZAtVY(v, y); }

	@Override
	public int getMaxZAtY(int y) { return source.getMaxZAtVY(v, y); }

	@Override
	public int getMinZAtWX(int w, int x) { return source.getMinZAtVWX(v, w, x); }

	@Override
	public int getMaxZAtWX(int w, int x) { return source.getMaxZAtVWX(v, w, x); }

	@Override
	public int getMinZAtWY(int w, int y) { return source.getMinZAtVWY(v, w, y); }

	@Override
	public int getMaxZAtWY(int w, int y) { return source.getMaxZAtVWY(v, w, y); }

	@Override
	public int getMinZAtXY(int x, int y) { return source.getMinZAtVXY(v, x, y); }

	@Override
	public int getMaxZAtXY(int x, int y) { return source.getMaxZAtVXY(v, x, y); }

	@Override
	public int getMinZ(int w, int x, int y) { return source.getMinZ(v, w, x, y); }

	@Override
	public int getMaxZ(int w, int x, int y) { return source.getMaxZ(v, w, x, y); }

	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (v > source.getMaxV() || v < source.getMinV()) {
			throw new UnsupportedOperationException("V coordinate is out of bounds.");
		}
		return changed;
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
