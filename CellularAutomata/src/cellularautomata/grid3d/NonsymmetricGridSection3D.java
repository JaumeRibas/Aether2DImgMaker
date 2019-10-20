package cellularautomata.grid3d;

public abstract class NonsymmetricGridSection3D implements Grid3D {

	protected SymmetricGrid3D grid;
	
	public NonsymmetricGridSection3D(SymmetricGrid3D grid) {
		super();
		this.grid = grid;
	}

	@Override
	public int getMinX() {
		return grid.getNonsymmetricMinX();
	}

	@Override
	public int getMaxX() {
		return grid.getNonsymmetricMaxX();
	}
	
	@Override
	public int getMinXAtY(int y) {
		return grid.getNonsymmetricMinXAtY(y);
	}

	@Override
	public int getMaxXAtY(int y) {
		return grid.getNonsymmetricMaxXAtY(y);
	}
	
	@Override
	public int getMinXAtZ(int z) {
		return grid.getNonsymmetricMinXAtZ(z);
	}

	@Override
	public int getMaxXAtZ(int z) {
		return grid.getNonsymmetricMaxXAtZ(z);
	}
	
	@Override
	public int getMinX(int y, int z) {
		return grid.getNonsymmetricMinX(y, z);
	}

	@Override
	public int getMaxX(int y, int z) {
		return grid.getNonsymmetricMaxX(y, z);
	}

	@Override
	public int getMinY() {
		return grid.getNonsymmetricMinY();
	}

	@Override
	public int getMaxY() {
		return grid.getNonsymmetricMaxY();
	}
	
	@Override
	public int getMinYAtX(int x) {
		return grid.getNonsymmetricMinYAtX(x);
	}

	@Override
	public int getMaxYAtX(int x) {
		return grid.getNonsymmetricMaxYAtX(x);
	}
	
	@Override
	public int getMinYAtZ(int z) {
		return grid.getNonsymmetricMinYAtZ(z);
	}

	@Override
	public int getMaxYAtZ(int z) {
		return grid.getNonsymmetricMaxYAtZ(z);
	}
	
	@Override
	public int getMinY(int x, int z) {
		return grid.getNonsymmetricMinY(x, z);
	}

	@Override
	public int getMaxY(int x, int z) {
		return grid.getNonsymmetricMaxY(x, z);
	}
	
	@Override
	public int getMinZ() {
		return grid.getNonsymmetricMinZ();
	}

	@Override
	public int getMaxZ() {
		return grid.getNonsymmetricMaxZ();
	}
	
	@Override
	public int getMinZAtY(int y) {
		return grid.getNonsymmetricMinZAtY(y);
	}

	@Override
	public int getMaxZAtY(int y) {
		return grid.getNonsymmetricMaxZAtY(y);
	}
	
	@Override
	public int getMinZAtX(int x) {
		return grid.getNonsymmetricMinZAtX(x);
	}

	@Override
	public int getMaxZAtX(int x) {
		return grid.getNonsymmetricMaxZAtX(x);
	}
	
	@Override
	public int getMinZ(int x, int y) {
		return grid.getNonsymmetricMinZ(x, y);
	}

	@Override
	public int getMaxZ(int x, int y) {
		return grid.getNonsymmetricMaxZ(x, y);
	}

}
