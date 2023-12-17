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
package caimgmaker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ResourceBundle;

public class InputReaderTask implements Runnable {
	
	private ResourceBundle messages;
	private volatile boolean stop = false;
	public volatile boolean backupRequested = false;
	
	public InputReaderTask(ResourceBundle messages) {
		this.messages = messages;
	}
	
	@Override
	public void run() {
		InputStreamReader inputStreamReader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		try {
			while (!stop) {
				while (!bufferedReader.ready() && !stop) {
		          Thread.sleep(200);
		        }
				if (stop) {
					bufferedReader.close();
				} else {
					String line = bufferedReader.readLine().trim().toLowerCase();
					if (line.equals("backup") || line.equals("save")) {
						backupRequested = true;
						System.out.println(messages.getString("backup-requested"));
					} else {
						System.out.printf(messages.getString("unknown-command-format"), line);
					}
				}
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		stop = true;
	}
	
}
