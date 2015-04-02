package peer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import listeners.MCListener;
import listeners.MDBListener;
import listeners.MDRListener;
import service.Chunk;
import service.RMIService;
import service.Utils;

public class Peer implements RMIService {

	private static final String remoteObjectName = "rmi-peer";

	private static InetAddress IP;
	public static MulticastSocket socket;

	private static MCListener mcListener;
	private static MDBListener mdbListener;
	private static MDRListener mdrListener;

	public static SynchedHandler synchedHandler;

	public static void main(String[] args) throws IOException {
		if (!validArgs(args))
			return;

		startRMI();

		IP = Utils.getIPv4();
		socket = new MulticastSocket();
		// System.out.println(socket.getInetAddress() + " :: " +
		// socket.getPort());

		new Thread(mcListener).start();
		new Thread(mdbListener).start();
		new Thread(mdrListener).start();

		synchedHandler = new SynchedHandler(mcListener, mdbListener,
				mdrListener);

		System.out.println("- SERVER READY -");
	}

	private static void startRMI() {
		Peer peer = new Peer();

		try {
			RMIService rmiService = (RMIService) UnicastRemoteObject
					.exportObject(peer, 0);

			LocateRegistry.getRegistry().rebind(remoteObjectName, rmiService);
		} catch (RemoteException e) {
			Utils.printError("Could not bind to rmiregistry");
		}
	}

	@Override
	public void backup(File file, int replicationDegree) {
		try {
			Chunk chunk = new Chunk(Utils.getFileID(file), 0,
					replicationDegree, Utils.getFileData(file));

			// TODO improve this method to split files

			synchedHandler.putChunk(chunk);
		} catch (FileNotFoundException e) {
			Utils.printError("file not found");
		}
	}

	@Override
	public void delete(File file) throws RemoteException {
		System.out.println("deleting " + file.getName());
	}

	@Override
	public void free(int kbyte) throws RemoteException {
		System.out.println("freeing " + kbyte + "kbyte");
	}

	@Override
	public void restore(File file) throws RemoteException {
		System.out.println("restoring " + file.getName());
	}

	private static boolean validArgs(String[] args) throws UnknownHostException {
		InetAddress mcAddress, mdbAddress, mdrAddress;
		int mcPort, mdbPort, mdrPort;

		if (args.length != 0 && args.length != 6) {
			System.out.println("Usage:");
			System.out.println("\tjava Server");
			System.out
					.println("\tjava Server <mcAddress> <mcPort> <mdbAddress> <mdbPort> <mdrAddress> <mdrPort>");

			return false;
		} else if (args.length == 0) {
			mcAddress = InetAddress.getByName("224.0.0.0");
			mcPort = 8000;

			mdbAddress = InetAddress.getByName("224.0.0.0");
			mdbPort = 8001;

			mdrAddress = InetAddress.getByName("224.0.0.0");
			mdrPort = 8002;
		} else {
			mcAddress = InetAddress.getByName(args[0]);
			mcPort = Integer.parseInt(args[1]);

			mdbAddress = InetAddress.getByName(args[2]);
			mdbPort = Integer.parseInt(args[3]);

			mdrAddress = InetAddress.getByName(args[4]);
			mdrPort = Integer.parseInt(args[5]);
		}

		mcListener = new MCListener(mcAddress, mcPort);
		mdbListener = new MDBListener(mdbAddress, mdbPort);
		mdrListener = new MDRListener(mdrAddress, mdrPort);

		return true;
	}

	public static InetAddress getIP() {
		return IP;
	}

}
