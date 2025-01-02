/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2025 Jaume Ribas

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
package cellularautomata.model;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import cellularautomata.Utils;


public abstract class FileBackedModel implements Closeable, Model {
	
	public static final String GRID_FOLDER_NAME = "grid";	
	public static final String PROPERTIES_BACKUP_FILE_NAME = "properties.ser";
	public static final String FILE_NAME_FORMAT = "step=%d.data";

	protected RandomAccessFile grid;
	private String gridFolderPath;
	protected File currentFile;
	protected boolean readingBackup = false;
	
	public FileBackedModel() {
		Runtime.getRuntime().addShutdownHook(new Thread() {

		      @Override
		      public void run() {
		        try {
		        	close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		      }
		 });
	}
	
	protected String getGridFolderPath() {
		return gridFolderPath;
	}

	/**
	 * Creates an instance restoring a backup
	 * 
	 * @param backupPath the path to the backup file to restore.
	 * @param folderPath
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws FileNotFoundException 
	 */
	public FileBackedModel(String backupPath, String folderPath) throws FileNotFoundException, ClassNotFoundException, IOException {
		this();
		readingBackup = true;
		File backupGridFolder = new File(backupPath + File.separator + GRID_FOLDER_NAME);
		if (!backupGridFolder.exists()) {
			throw new FileNotFoundException("Missing grid folder at \"" + backupGridFolder.getAbsolutePath() + '"');
		}
		@SuppressWarnings("unchecked")
		HashMap<String, Object> properties = 
				(HashMap<String, Object>) Utils.deserializeFromFile(backupPath + File.separator + PROPERTIES_BACKUP_FILE_NAME);
		setPropertiesFromMap(properties);
		currentFile = new File(backupGridFolder.getPath() + File.separator + String.format(FILE_NAME_FORMAT, getStep()));
		grid = new RandomAccessFile(currentFile, "r");
		createGridFolder(folderPath);		
	}
	
	protected abstract void setPropertiesFromMap(HashMap<String, Object> properties);

	protected void createGridFolder(String path) throws IOException {
		if (gridFolderPath == null) {
			Path tmp = Paths.get(path + File.separator + getSubfolderPath());
			Files.createDirectories(tmp);
			File gridFolder = Files.createTempDirectory(tmp, GRID_FOLDER_NAME).toFile();
			gridFolderPath = gridFolder.getPath();
		}
	}	

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		String backupFolderPath = backupPath + File.separator + backupName;
		File backupFolder = new File(backupFolderPath);
		if (backupFolder.exists()) {
			FileUtils.cleanDirectory(backupFolder);
		} else {
			backupFolder.mkdirs();
		}
		File gridBackupFile = new File(backupFolderPath + File.separator + GRID_FOLDER_NAME + File.separator + currentFile.getName());
	    FileUtils.copyFile(currentFile, gridBackupFile);
		HashMap<String, Object> properties = getPropertiesMap();
		Utils.serializeToFile(properties, backupFolderPath, PROPERTIES_BACKUP_FILE_NAME);
	}
	
	protected abstract HashMap<String, Object> getPropertiesMap();

	@Override
	public void close() throws IOException {
		if (grid != null) {
			grid.close();	
		}
		FileUtils.deleteDirectory(new File(gridFolderPath));
	}

}
