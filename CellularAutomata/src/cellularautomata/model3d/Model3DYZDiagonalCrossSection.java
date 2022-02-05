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

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.model2d.Model2D;

public class Model3DYZDiagonalCrossSection<G extends Model3D> implements Model2D {

	protected G source;
	protected int zOffsetFromY;
	protected int crossSectionMinY;
	protected int crossSectionMaxY;
	protected int crossSectionMinX;
	protected int crossSectionMaxX;
	
	public Model3DYZDiagonalCrossSection(G source, int zOffsetFromY) {		
		this.source = source;
		this.zOffsetFromY = zOffsetFromY;
		if (!getBounds()) {
			throw new IllegalArgumentException("Cross section is out of bounds.");
		}
	}
	
	@Override
	public String getXLabel() {
		return source.getXLabel();
	}
	
	@Override
	public String getYLabel() {
		return source.getYLabel();
	}
	
	protected boolean getBounds() {
		int y = source.getMinY();
		int maxY = source.getMaxY();
		int crossSectionZ = y + zOffsetFromY;
		while (y <= maxY && (crossSectionZ < source.getMinZAtY(y) || crossSectionZ > source.getMaxZAtY(y))) {
			y++;
			crossSectionZ++;
		}
		if (y <= maxY) {
			crossSectionMinY = y;
			crossSectionMaxY = y;
			crossSectionMaxX = source.getMaxX(y, crossSectionZ);
			crossSectionMinX = source.getMinX(y, crossSectionZ);
			y++;
			crossSectionZ++;
			while (y <= maxY && crossSectionZ >= source.getMinZAtY(y) && crossSectionZ <= source.getMaxZAtY(y)) {
				crossSectionMaxY = y;
				int localMaxX = source.getMaxX(y, crossSectionZ), localMinX = source.getMinX(y, crossSectionZ);
				if (localMaxX > crossSectionMaxX) {
					crossSectionMaxX = localMaxX;
				}
				if (localMinX < crossSectionMinX) {
					crossSectionMinX = localMinX;
				}
				y++;
				crossSectionZ++;
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int getMinX() {
		return crossSectionMinX;
	}
	
	@Override
	public int getMinX(int y) {
		return source.getMinX(y, y + zOffsetFromY);
	}
	
	@Override
	public int getMaxX() {
		return crossSectionMaxX;
	}
	
	@Override
	public int getMaxX(int y) {
		return source.getMaxX(y, y + zOffsetFromY);
	}
	
	@Override
	public int getMinY() {
		return crossSectionMinY;
	}
	
	@Override
	public int getMinY(int x) {
		for (int crossSectionY = crossSectionMinY, crossSectionZ = crossSectionY + zOffsetFromY; crossSectionY <= crossSectionMaxY; crossSectionY++, crossSectionZ++) {
			int localMaxX = source.getMaxX(crossSectionY, crossSectionZ), localMinX = source.getMinX(crossSectionY, crossSectionZ);
			if (x >= localMinX && x <= localMaxX) {
				return crossSectionY;
			}
		}
		throw new IllegalArgumentException("X coordinate out of bounds.");
	}
	
	@Override
	public int getMaxY() {
		return crossSectionMaxY;
	}
	
	@Override
	public int getMaxY(int x) {
		for (int crossSectionY = crossSectionMaxY, crossSectionZ = crossSectionY + zOffsetFromY; crossSectionY >= crossSectionMinY; crossSectionY--, crossSectionZ--) {
			int localMaxX = source.getMaxX(crossSectionY, crossSectionZ), localMinX = source.getMinX(crossSectionY, crossSectionZ);
			if (x >= localMinX && x <= localMaxX) {
				return crossSectionY;
			}
		}
		throw new IllegalArgumentException("X coordinate out of bounds.");
	}

	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (!getBounds()) {
			throw new UnsupportedOperationException("Cross section is out of bounds.");
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
		String path = source.getSubfolderPath() + "/" + source.getZLabel() + "=" + source.getYLabel();
		if (zOffsetFromY < 0) {
			path += zOffsetFromY;
		} else if (zOffsetFromY > 0) {
			path += "+" + zOffsetFromY;
		}
		return path;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}

}
