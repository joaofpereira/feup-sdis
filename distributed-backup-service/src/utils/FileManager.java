package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import peer.Peer;
import chunk.ChunkID;

public class FileManager {

	public static final String FILES = "FILES/";

	private static final String CHUNKS = "CHUNKS/";

	private static final String RESTORES = "RESTORES/";

	public static final byte[] loadFile(File file) throws FileNotFoundException {
		FileInputStream inputStream = new FileInputStream(file);

		byte[] data = new byte[(int) file.length()];

		try {
			inputStream.read(data);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return data;
	}

	public static final void saveChunk(ChunkID chunkID, byte[] data)
			throws IOException {
		// write chunk
		FileOutputStream out = new FileOutputStream(CHUNKS + chunkID.toString());
		out.write(data);
		out.close();

		// update database
		Peer.getChunkDB().addChunk(chunkID);
		Peer.saveChunkDB();
	}

	public static final byte[] loadChunk(ChunkID chunkID)
			throws FileNotFoundException {
		File file = new File(CHUNKS + chunkID);
		FileInputStream inputStream = new FileInputStream(file);

		byte[] data = new byte[(int) file.length()];

		try {
			inputStream.read(data);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return data;
	}

	public static final void saveRestore(String fileName, byte[] data)
			throws IOException {
		FileOutputStream out = new FileOutputStream(RESTORES + fileName);
		out.write(data);
		out.close();
	}

}