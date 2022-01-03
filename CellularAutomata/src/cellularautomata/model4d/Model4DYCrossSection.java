/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2021 Jaume Ribas

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
package cellularautomata.model4d;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.model3d.Model3D;


public class Model4DYCrossSection<G extends Model4D> implements Model3D {

	protected G source;
	protected int y;
	
	public Model4DYCrossSection(G source, int y) {
		if (y > source.getMaxY() || y < source.getMinY()) {
			throw new IllegalArgumentException("Y coordinate is out of bounds.");
		}
		this.source = source;
		this.y = y;
	}

	/*
	 * x -> w
	 * y -> x
	 * z -> z
	 */
	
	@Override
	public int getMinX() {
		return source.getMinWAtY(y);
	}
	
	@Override
	public int getMinXAtY(int y) {
		return source.getMinWAtXY(y, this.y);
	}
	
	@Override
	public int getMinXAtZ(int z) {
		return source.getMinWAtYZ(this.y, z);
	}
	
	@Override
	public int getMinX(int y, int z) {
		return source.getMinW(y, this.y, z);
	}
	
	@Override
	public int getMaxX() {
		return source.getMaxWAtY(y);
	}
	
	@Override
	public int getMaxXAtY(int y) {
		return source.getMaxWAtXY(y, this.y);
	}
	
	@Override
	public int getMaxXAtZ(int z) {
		return source.getMaxWAtYZ(this.y, z);
	}
	
	@Override
	public int getMaxX(int y, int z) {
		return source.getMaxW(y, this.y, z);
	}

	@Override
	public int getMinY() {
		return source.getMinXAtY(this.y);
	}
	
	@Override
	public int getMinYAtX(int x) {
		return source.getMinXAtWY(x, this.y);
	}
	
	@Override
	public int getMinYAtZ(int z) {
		return source.getMinXAtYZ(this.y, z);
	}
	
	@Override
	public int getMinY(int x, int z) {
		return source.getMinX(x, this.y, z);
	}
	
	@Override
	public int getMaxY() {
		return source.getMaxXAtY(this.y);
	}
	
	@Override
	public int getMaxYAtX(int x) {
		return source.getMaxXAtWY(x, this.y);
	}
	
	@Override
	public int getMaxYAtZ(int z) {
		return source.getMaxXAtYZ(this.y, z);
	}
	
	@Override
	public int getMaxY(int x, int z) {
		return source.getMaxX(x, this.y, z);
	}
	
	@Override
	public int getMinZ() {
		return source.getMinZAtY(this.y);
	}
	
	@Override
	public int getMinZAtX(int x) {
		return source.getMinZAtWY(x, this.y);
	}
	
	@Override
	public int getMinZAtY(int y) {
		return source.getMinZAtXY(y, this.y);
	}
	
	@Override
	public int getMinZ(int x, int y) {
		return source.getMinZ(x, y, this.y);
	}
	
	@Override
	public int getMaxZ() {
		return source.getMaxZAtY(this.y);
	}
	
	@Override
	public int getMaxZAtX(int x) {
		return source.getMaxZAtWY(x, this.y);
	}
	
	@Override
	public int getMaxZAtY(int y) {
		return source.getMaxZAtXY(y, this.y);
	}
	
	@Override
	public int getMaxZ(int x, int y) {
		return source.getMaxZ(x, y, this.y);
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
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (y > source.getMaxY() || y < source.getMinY()) {
			throw new UnsupportedOperationException("Y coordinate is out of bounds.");
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
		return source.getSubfolderPath() + "/" + source.getYLabel() + "=" + y;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}
}
