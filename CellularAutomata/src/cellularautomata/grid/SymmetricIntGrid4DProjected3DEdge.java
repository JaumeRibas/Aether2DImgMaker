package cellularautomata.grid;

public class SymmetricIntGrid4DProjected3DEdge extends IntGrid3D {

	private SymmetricIntGrid4D source;
	private int backgroundValue;
	
	public SymmetricIntGrid4DProjected3DEdge(SymmetricIntGrid4D source, int backgroundValue) {
		this.source = source;
		this.backgroundValue = backgroundValue;
	}

	@Override
	public int getMinX() {
		return source.getNonSymmetricMinX();
	}

	@Override
	public int getMaxX() {
		return source.getNonSymmetricMaxX();
	}

	@Override
	public int getMinY() {
		return source.getNonSymmetricMinY();
	}

	@Override
	public int getMaxY() {
		return source.getNonSymmetricMaxY();
	}
	
	@Override
	public int getMinZ() {
		return source.getNonSymmetricMinZ();
	}

	@Override
	public int getMaxZ() {
		return source.getNonSymmetricMaxZ();
	}
	
	@Override
	public int getValueAt(int x, int y, int z) {
		int w = source.getNonSymmetricMaxW(), 
				minW = source.getNonSymmetricMinW();
		int value = source.getValueAt(w, x, y, z);
		while (value == backgroundValue && w > minW) {
			w--;
			value = source.getNonSymmetricValueAt(w, x, y, z);
		}
		return value;
	}
}
