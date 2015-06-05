package peer;

import java.io.IOException;
import java.util.ArrayList;

import screens.CanvasScreen;
import utils.Curve;
import utils.Utils;
import commands.Command;
import commands.CommandType;

public class Forwarder {

	private final CanvasScreen canvasScreen;

	public Forwarder(CanvasScreen canvasScreen) {
		this.canvasScreen = canvasScreen;
	}

	public void sendJOIN(String ip) throws IOException {
		Peer host = new Peer(ip);
		host.createNetworkData();

		host.oos.writeObject(new Command(CommandType.JOIN));
		Utils.log("Sent JOIN");

		canvasScreen.game.peers.add(host);
		Utils.log("Added host to peers array");
		Utils.log("Current peers array: " + canvasScreen.game.peers);

		Utils.log("Creating PeerListener for the peer we just sent the JOIN to.");
		new Thread(new PeerListener(canvasScreen, host)).start();
	}

	public void sendGET_PEERS() throws IOException {
		Peer host = canvasScreen.game.peers.get(0);

		host.oos.writeObject(new Command(CommandType.GET_PEERS));
		Utils.log("Sent GET_PEERS");
	}

	public void sendPULL_DRAWING() throws IOException {
		int randomPeerIndex = Utils.random.nextInt(canvasScreen.game.peers
				.size());

		Peer randomPeer = canvasScreen.game.peers.get(randomPeerIndex);

		randomPeer.oos.writeObject(new Command(CommandType.PULL_DRAWING));
		Utils.log("Sent PULL_DRAWING");
	}

	public void sendDRAWING(ArrayList<Curve> drawing, Peer peer)
			throws IOException {
		peer.oos.writeObject(new Command(canvasScreen.drawing, 1));
	}

	public void sendCURVE(Curve currentCurve, Peer peer) {
		try {
			// send curve
			peer.oos.writeObject(new Command(currentCurve));
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("CanvasScreen.sendCURVE");
		}
	}

}