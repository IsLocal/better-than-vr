package tfc.btvr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IO {
	public static byte[] readAllBytes(InputStream is) throws IOException {
		int count = is.available();
		byte[] data = new byte[Math.min(count, 512)];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while (true) {
			count = is.available();
			if (count > data.length) {
				data = new byte[count];
			}
			int len = is.read(data);
			baos.write(data, 0, count);
			if (len == -1) break;
		}
		return baos.toByteArray();
	}
}
