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
import java.util.concurrent.RejectedExecutionException;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.woollysammoth.nubitj.core.Wallet;
import com.woollysammoth.nubitj.core.Wallet.BalanceType;
import com.woollysammoth.nubitj.utils.Threading;

import com.woollysammoth.nubit_android_wallet.util.ThrottlingWalletChangeListener;

/**
 * @author Andreas Schildbach
 */
public final class WalletBalanceLoader extends AsyncTaskLoader<BigInteger>
{
	private final Wallet wallet;

	private static final Logger log = LoggerFactory.getLogger(WalletBalanceLoader.class);

	public WalletBalanceLoader(final Context context, @Nonnull final Wallet wallet)
	{
		super(context);

		this.wallet = wallet;
	}

	@Override
	protected void onStartLoading()
	{
		super.onStartLoading();

		wallet.addEventListener(walletChangeListener, Threading.SAME_THREAD);

		forceLoad();
	}

	@Override
	protected void onStopLoading()
	{
		wallet.removeEventListener(walletChangeListener);
		walletChangeListener.removeCallbacks();

		super.onStopLoading();
	}

	@Override
	public BigInteger loadInBackground()
	{
		return wallet.getBalance(BalanceType.ESTIMATED);
	}

	private final ThrottlingWalletChangeListener walletChangeListener = new ThrottlingWalletChangeListener()
	{
		@Override
		public void onThrottledWalletChanged()
		{
			try
			{
				forceLoad();
			}
			catch (final RejectedExecutionException x)
			{
				log.info("rejected execution: " + WalletBalanceLoader.this.toString());
			}
		}
	};
}
