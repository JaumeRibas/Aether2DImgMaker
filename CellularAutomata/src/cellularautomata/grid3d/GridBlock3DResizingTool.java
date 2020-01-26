/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2020 Jaume Ribas

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
package cellularautomata.grid3d;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GridBlock3DResizingTool {
	
	private static SizeLimitedNonsymmetricIntGrid3D sourceBlock;
	private static File sourceFolder;

	public static void main(String[] args) throws Exception {
//		if (args.length != 3) {
//			System.out.println("Please pass the following arguments:" 
//					+ System.lineSeparator() + "1. The path to the folder containing the grid blocks to shrink."
//					+ System.lineSeparator() + "2. The path to the folder where the new grid blocks will be saved."
//					+ System.lineSeparator() + "3. The new size of the blocks in bytes.");
//		}
//		gridFolder =  new File(args[0]);
//		File targetFolder = new File(args[1]);
//		long newSize = Long.parseLong(args[2]);
		
		sourceFolder =  new File("D:/data/Aether3D/-1073741823/backups/IntAether3DSwap_6513/grid");
		String targetFolderPath = "D:/data/Aether3D/-1073741823/backupResized/IntAether3DSwap_6513/grid/";
		long newSize = Long.parseLong("8589934592")/2;
		
//		sourceFolder =  new File("D:/data/test/Aether3D/-1000/backup/Aether3D_100/grid");
//		String targetFolderPath = "D:/data/test/Aether3D/-1000/backupResized/Aether3D_100/grid";
//		long newSize = (1024 * 70)/2;
		
		File targetFolder = new File(targetFolderPath);
		if (targetFolder.exists()) {
			System.out.println("Target path already exists. Chose a different one or move current folder if possible.");
			return;
		}
		targetFolder.mkdirs();
		
		int targetXIndex = 0;
		
		//read block
		System.out.println("Reading source block " + 0 + " from '" + sourceFolder + "'");
		sourceBlock = loadGridBlock(sourceFolder, 0);

		
		//create new block with new size
		System.out.println("Creating target block " + targetXIndex);
		SizeLimitedNonsymmetricIntGrid3D targetBlock = new SizeLimitedNonsymmetricIntGrid3D(targetXIndex, newSize);
		System.out.println("minX=" + targetBlock.minX + ", maxX=" + targetBlock.maxX);

		//fill with slices of source block
		System.out.println("Filling target block with slices of source block from " + targetXIndex + " to " + targetBlock.maxX);
		NonsymmetricIntGrid3DSlice slice = getSourceSlice(targetXIndex);
		for (; slice != null && targetXIndex <= targetBlock.maxX; targetXIndex++) {
			targetBlock.setSlice(targetXIndex, slice);
			slice = getSourceSlice(targetXIndex + 1);
		}

		while (slice != null) {
			//serialize to target folder and free
			System.out.println("Saving target block " + targetBlock.minX + " to '" + targetFolderPath + "'");
			saveGridBlock(targetBlock, targetFolderPath);
			targetBlock = null;
			
			//create block
			System.out.println("Creating target block " + targetXIndex);
			targetBlock = new SizeLimitedNonsymmetricIntGrid3D(targetXIndex, newSize);
			System.out.println("minX=" + targetBlock.minX + ", maxX=" + targetBlock.maxX);
			
			//fill with slices of source block
			System.out.println("Filling target block with slices of source block from " + targetXIndex + " to " + targetBlock.maxX);
			for (; slice != null && targetXIndex <= targetBlock.maxX; targetXIndex++) {
				targetBlock.setSlice(targetXIndex, slice);
				slice = getSourceSlice(targetXIndex + 1);
			}
		}		
		
		//serialize to target folder and free
		System.out.println("Saving target block " + targetBlock.minX + " to '" + targetFolderPath + "'");
		saveGridBlock(targetBlock, targetFolderPath);
	}
	
	
	private static NonsymmetricIntGrid3DSlice getSourceSlice(int x) throws ClassNotFoundException, IOException {
		if (x > sourceBlock.maxX) {
			int nextMinX = sourceBlock.maxX + 1;
			sourceBlock = null;
			//read block
			System.out.println("Reading source block " + nextMinX + " from '" + sourceFolder + "'");
			try {
				sourceBlock = loadGridBlock(sourceFolder, nextMinX);
			} catch (FileNotFoundException e) {
				System.out.println("Source block " + nextMinX + " not found");
				return null;
			}
		}
		return sourceBlock.getSlice(x);
	}
	
	
	private static void saveGridBlock(SizeLimitedNonsymmetricIntGrid3D gridBlock, String path) throws FileNotFoundException, IOException {
		String name = "minX=" + gridBlock.minX + "_maxX=" + gridBlock.maxX + ".ser";
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path + "/" + name));
		out.writeObject(gridBlock);
		out.flush();
		out.close();
	}
	
	private static SizeLimitedNonsymmetricIntGrid3D loadGridBlock(File folder, int minX) throws IOException, ClassNotFoundException {
		SizeLimitedNonsymmetricIntGrid3D gridBlock = loadGridBlockSafe(folder, minX);
		if (gridBlock == null) {
			throw new FileNotFoundException("No grid block with minX=" + minX + " could be found at folder path \"" + folder.getAbsolutePath() + "\".");
		} else {
			return gridBlock;
		}
	}
	
	private static SizeLimitedNonsymmetricIntGrid3D loadGridBlockSafe(File folder, int minX) throws IOException, ClassNotFoundException {
		File[] files = folder.listFiles();
		boolean found = false;
		SizeLimitedNonsymmetricIntGrid3D gridBlock = null;
		File gridBlockFile = null;
		for (int i = 0; i < files.length && !found; i++) {
			File currentFile = files[i];
			String fileName = currentFile.getName();
			int fileMinX;
			try {
				//"minX=".length() == 5
				fileMinX = Integer.parseInt(fileName.substring(5, fileName.indexOf("_")));
				if (fileMinX == minX) {
					found = true;
					gridBlockFile = currentFile;
				}
			} catch (NumberFormatException ex) {
				
			}
		}
		if (found) {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(gridBlockFile));
			gridBlock = (SizeLimitedNonsymmetricIntGrid3D) in.readObject();
			in.close();
		}
		return gridBlock;
	}
	
}

