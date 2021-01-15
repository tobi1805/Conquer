package org.jel.game.data;

import java.util.ArrayList;
import java.util.List;

import org.jel.game.messages.Message;
import org.jel.game.plugins.MessageListener;

public final class EventList extends ArrayList<Message> {
	private static final long serialVersionUID = -3059648150677032552L;

	private final List<MessageListener> listeners = new ArrayList<>(50);

	@Override
	public void add(final int index, final Message element) {
		if (element == null) {
			throw new IllegalArgumentException("message==null");
		}
		this.listeners.forEach(a -> a.added(element));
		super.add(index, element);
	}

	@Override
	public boolean add(final Message message) {
		if (message == null) {
			throw new IllegalArgumentException("message==null");
		}
		this.listeners.forEach(a -> a.added(message));
		return super.add(message);
	}

	public void addListener(final MessageListener ml) {
		this.listeners.add(ml);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		} else {
			return super.equals(o);
		}
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ this.listeners.hashCode();
	}

	@Override
	public boolean remove(final Object o) {
		if (!(o instanceof Message)) {
			throw new IllegalArgumentException("o has to be an instanceof Message");
		}
		this.listeners.forEach(a -> a.removed((Message) o));
		return super.remove(o);
	}
}
