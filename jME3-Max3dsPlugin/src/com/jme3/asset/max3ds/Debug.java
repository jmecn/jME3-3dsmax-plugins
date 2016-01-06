package com.jme3.asset.max3ds;

import com.jme3.asset.max3ds.chunks.Chunk;

public class Debug {

	public Debug() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * prints some handy information... the chunk hierarchy.
	 */
	protected static void debug(Chunk parentChunk, int level, Integer chunkID,
			long chunkLength, int position, long limit) {
		try {
			for (int i = 0; i < level; i++) {
				System.out.print("  ");
			}
			Object child = parentChunk.getSubChunk(chunkID);
			int id = ((short) chunkID.intValue()) & 0xFFFF;
			System.out.println(parentChunk + " is "
					+ (child == null ? "skipping" : "LOADING") + ": [id="
					+ Integer.toHexString(id) + ", object= <"
					+ parentChunk.getSubChunk(chunkID) + ">, chunkLength="
					+ chunkLength + ", position=" + position + " limit="
					+ limit + "]");
		} catch (Exception e) {
			// We're debugging.. its ok
			e.printStackTrace();
		}
	}
	
	/**
	 * Convert the integer to an unsigned number.
	 * 
	 * @param i
	 *            the integer to convert.
	 */
	private static String byteString(int i) {
		final char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'a', 'b', 'c', 'd', 'e', 'f' };

		char[] buf = new char[2];
		buf[1] = digits[i & 0xF];
		i >>>= 4;
		buf[0] = digits[i & 0xF];

		return "0x" + new String(buf).toUpperCase();
	}
}
