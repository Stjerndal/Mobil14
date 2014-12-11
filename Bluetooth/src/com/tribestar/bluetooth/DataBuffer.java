package com.tribestar.bluetooth;

import java.util.ArrayList;

public final class DataBuffer {

	private static ArrayList<Byte> buffer = new ArrayList<Byte>();
	private static int threadsOnBuffer = 0;
	private static int maxThreadsOnBuffer = 1;

	private DataBuffer() {
	}

	private static synchronized void postToBuffer(byte b) {
		while (threadsOnBuffer >= maxThreadsOnBuffer) {
			try {
				DataBuffer.class.wait();
			} catch (InterruptedException e) {

			}
		}
		threadsOnBuffer++;
		buffer.add(b);
		threadsOnBuffer--;
		DataBuffer.class.notifyAll();
	}

	private synchronized void readFromBuffer(GraphicsThread thread) {
		while (threadsOnBuffer >= maxThreadsOnBuffer) {
			try {
				DataBuffer.class.wait();
			} catch (InterruptedException e) {

			}
		}
		threadsOnBuffer++;
		byte[] newBuff = new byte[buffer.size()];
		for (int i = 0; i < newBuff.length; i++) {
			newBuff[i] = buffer.get(i);
		}
		thread.postByteBuffer(newBuff);
		threadsOnBuffer--;
		DataBuffer.class.notifyAll();

	}

}
