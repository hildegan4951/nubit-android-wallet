/*
 * Copyright 2014 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.woollysammoth.nubit_android_wallet.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import com.woollysammoth.nubit_android_wallet.util.Crypto;

/**
 * @author Andreas Schildbach
 */
public class CryptoTest
{
	private static final String PLAIN_TEXT = "plain text";
	private static final byte[] PLAIN_BYTES = PLAIN_TEXT.getBytes();
	private static final char[] PASSWORD = "password".toCharArray();

	@Test
	public void roundtripText() throws Exception
	{
		final String plainText = Crypto.decrypt(Crypto.encrypt(PLAIN_TEXT, PASSWORD), PASSWORD);
		assertEquals(PLAIN_TEXT, plainText);
	}

	@Test
	public void roundtripDifferentTextSizes() throws Exception
	{
		final StringBuilder builder = new StringBuilder();
		while (builder.length() < 4096)
		{
			final String plainText = builder.toString();
			final String roundtrippedPlainText = Crypto.decrypt(Crypto.encrypt(plainText, PASSWORD), PASSWORD);
			assertEquals(plainText, roundtrippedPlainText);

			builder.append('x');
		}
	}

	@Test
	public void roundtripBytes() throws Exception
	{
		final byte[] plainBytes = Crypto.decryptBytes(Crypto.encrypt(PLAIN_BYTES, PASSWORD), PASSWORD);
		assertArrayEquals(PLAIN_BYTES, plainBytes);
	}

	@Test
	public void roundtripDifferentByteSizes() throws Exception
	{
		final ByteArrayOutputStream stream = new ByteArrayOutputStream(4096);
		while (stream.toByteArray().length < 4096)
		{
			final byte[] plainBytes = stream.toByteArray();
			final byte[] roundtrippedPlainBytes = Crypto.decryptBytes(Crypto.encrypt(plainBytes, PASSWORD), PASSWORD);
			assertArrayEquals(plainBytes, roundtrippedPlainBytes);

			stream.write(42);
		}
	}

	@Test
	public void roundtripDifferentPasswordSizes() throws Exception
	{
		final StringBuilder builder = new StringBuilder();
		while (builder.length() < 4096)
		{
			final char[] password = builder.toString().toCharArray();
			final String plainText = Crypto.decrypt(Crypto.encrypt(PLAIN_TEXT, password), password);
			assertEquals(PLAIN_TEXT, plainText);

			builder.append('x');
		}
	}
}
