package com.joy.rxjava.observable;

import com.joy.rxjava.functions.Function;
import com.joy.rxjava.observer.Observer;
import com.joy.rxjava.utils.RLog;

import java.util.Iterator;

/**
 * Created by joybar on 2018/6/11.
 */

public class ObservableFlapMapIterable<T, U> extends Observable<U> {

	final Function<? super T, Iterable<U>> function;
	final ObservableSource<T> source;

	public ObservableFlapMapIterable(ObservableSource<T> source, Function<? super T, Iterable<U>> function) {
		this.source = source;
		this.function = function;
	}

	public final ObservableSource<T> source() {
		return source;
	}

	@Override
	public void subscribeActual(Observer<? super U> observer) {
		MergeObserver mergeObserver = new MergeObserver<T, U>(observer, function);
		source.subscribe(mergeObserver);
	}

	static final class MergeObserver<T, U> implements Observer<T> {
		protected final Observer<? super U> actual;
		final Function<? super T, Iterable<U>> mapper;

		MergeObserver(Observer<? super U> actual, Function<? super T, Iterable<U>> mapper) {
			this.actual = actual;
			this.mapper = mapper;
		}

		@Override
		public void onSubscribe() {
			actual.onSubscribe();
		}

		@Override
		public void onNext(T t) {
			RLog.printInfo("ObservableFlapMapSimple: onNext");
			Iterable<? extends U> p = null;
			try {
				p = mapper.apply(t);
			} catch (Exception e) {
				onError(e);
				e.printStackTrace();
			}
			Iterator<? extends U> it = p.iterator();
			while (it.hasNext()) {
				U v = it.next();
				actual.onNext(v);
			}
		}

		@Override
		public void onError(Throwable error) {
			actual.onError(error);
		}

		@Override
		public void onComplete() {
			actual.onComplete();
		}
	}

}
