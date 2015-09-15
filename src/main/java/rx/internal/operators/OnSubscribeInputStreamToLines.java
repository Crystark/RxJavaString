package rx.internal.operators;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import rx.Subscriber;
import rx.observables.AbstractOnSubscribe;

public final class OnSubscribeInputStreamToLines extends AbstractOnSubscribe<String, BufferedReader> {
	private final InputStream	is;
	private final Charset		ch;

	public OnSubscribeInputStreamToLines(InputStream is) {
		this(is, Charset.defaultCharset());
	}

	public OnSubscribeInputStreamToLines(InputStream is, Charset ch) {
		this.is = is;
		this.ch = ch;
	}

	@Override
	protected BufferedReader onSubscribe(Subscriber<? super String> subscriber) {
		return new BufferedReader(new InputStreamReader(is, ch));
	}

	@Override
	protected void onTerminated(BufferedReader state) {
		try {
			state.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void next(SubscriptionState<String, BufferedReader> state) {
		BufferedReader reader = state.state();
		try {
			String line = reader.readLine();
			if (line == null) {
				state.onCompleted();
			}
			else {
				state.onNext(line);
			}
		} catch (IOException e) {
			state.onError(e);
		}
	}
}
