/*
 * Copyright 2013-2014 the original author or authors.
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

package com.woollysammoth.nubit_android_wallet.ui;

import java.math.BigInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import android.os.Handler;
import android.os.Looper;

import com.woollysammoth.nubitj.core.InsufficientMoneyException;
import com.woollysammoth.nubitj.core.Transaction;
import com.woollysammoth.nubitj.core.Wallet;
import com.woollysammoth.nubitj.core.Wallet.SendRequest;

/**
 * @author Andreas Schildbach
 */
public abstract class SendCoinsOfflineTask
{
	private final Wallet wallet;
	private final Handler backgroundHandler;
	private final Handler callbackHandler;

	public SendCoinsOfflineTask(@Nonnull final Wallet wallet, @Nonnull final Handler backgroundHandler)
	{
		this.wallet = wallet;
		this.backgroundHandler = backgroundHandler;
		this.callbackHandler = new Handler(Looper.myLooper());
	}

	public final void sendCoinsOffline(@Nonnull final SendRequest sendRequest)
	{
		backgroundHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					final Transaction transaction = wallet.sendCoinsOffline(sendRequest); // can take long

					callbackHandler.post(new Runnable()
					{
						@Override
						public void run()
						{
							onSuccess(transaction);
						}
					});
				}
				catch (final InsufficientMoneyException x)
				{
					callbackHandler.post(new Runnable()
					{
						@Override
						public void run()
						{
							onInsufficientMoney(x.missing);
						}
					});
				}
				catch (final IllegalArgumentException x)
				{
					callbackHandler.post(new Runnable()
					{
						@Override
						public void run()
						{
							onFailure();
						}
					});
				}
			}
		});
	}

	protected abstract void onSuccess(@Nonnull Transaction transaction);

	protected abstract void onInsufficientMoney(@Nullable BigInteger missing);

	protected abstract void onFailure();
}
