package peer;

import java.io.IOException;
import java.net.DatagramPacket;

import listeners.MCListener;
import listeners.MDBListener;
import listeners.MDRListener;
import service.Chunk;
import service.MessageType;
import service.Protocol;
import service.Utils;

public class SynchedHandler implements Protocol {

	public MCListener mcListener;
	public MDBListener mdbListener;
	public MDRListener mdrListener;

	public SynchedHandler(MCListener mcListener, MDBListener mdbListener,
			MDRListener mdrListener) {
		this.mcListener = mcListener;
		this.mdbListener = mdbListener;
		this.mdrListener = mdrListener;
	}

	@Override
	public void putChunk(Chunk chunk) {
		String header = MessageType.PUTCHUNK + " " + Protocol.VERSION;
		header += " " + chunk.getFileID();
		header += " " + chunk.getChunkNo();
		header += " " + chunk.getReplicationDegree();
		header += " " + Protocol.CRLF;
		header += Protocol.CRLF;

		sendPacketToMDB(Utils.concatBytes(header.getBytes(), chunk.getData()));
	}

	@Override
	public void storeChunk(Chunk chunk) {
		String header = MessageType.STORED + " " + Protocol.VERSION;
		header += " " + chunk.getFileID();
		header += " " + chunk.getChunkNo();
		header += " " + Protocol.CRLF;
		header += Protocol.CRLF;

		sendPacketToMC(header.getBytes());
	}

	@Override
	public void getChunk() {
		// TODO Auto-generated method stub
	}

	@Override
	public void sendChunk() {
		// TODO Auto-generated method stub
	}

	@Override
	public void deleteChunk() {
		// TODO Auto-generated method stub
	}

	@Override
	public void removeChunk() {
		// TODO Auto-generated method stub
	}

	private synchronized void sendPacketToMC(byte[] buf) {
		DatagramPacket packet = new DatagramPacket(buf, buf.length,
				mcListener.address, mcListener.port);

		try {
			Peer.getSocket().send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void sendPacketToMDB(byte[] buf) {
		DatagramPacket packet = new DatagramPacket(buf, buf.length,
				mdbListener.address, mdbListener.port);

		try {
			Peer.getSocket().send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void sendPacketToMDR(byte[] buf) {
		DatagramPacket packet = new DatagramPacket(buf, buf.length,
				mdrListener.address, mdrListener.port);

		try {
			Peer.getSocket().send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
