package cellularautomata.grid;

public class SymmetricLongGrid3DXSection extends SymmetricLongGrid2D {

	private SymmetricLongGrid3D source;
	private int z;
	
	public SymmetricLongGrid3DXSection(SymmetricLongGrid3D source, int z) {
		this.source = source;
		this.z = z;
	}

	@Override
	public int getMinX() {
		return source.getMinX();
	}

	@Override
	public int getMaxX() {
		return source.getMaxX();
	}

	@Override
	public int getMinY() {
		return source.getMinY();
	}

	@Override
	public int getMaxY() {
		return source.getMaxY();
	}

	@Override
	public long getValueAt(int x, int y) {
		return source.getValueAt(x, y, z);
	}

	@Override
	public int getNonSymmetricMinX() {
		return source.getNonSymmetricMinX();
	}

	@Override
	public int getNonSymmetricMaxX() {
		return source.getNonSymmetricMaxX();
	}

	@Override
	public int getNonSymmetricMinY() {
		return source.getNonSymmetricMinY();
	}

	@Override
	public int getNonSymmetricMaxY() {
		return source.getNonSymmetricMaxY();
	}

	@Override
	public long getNonSymmetricValueAt(int x, int y) {
		return source.getNonSymmetricValueAt(x, y, z);
	}

}
