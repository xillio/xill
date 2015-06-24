package nl.xillio.xill.util;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Cookie store implementation to replace the default java cookie handler
 */
public class PersistentCookieStore implements CookieStore {
	private final Map<URI, List<HttpCookie>> mapCookies = new ConcurrentHashMap<>();

	@Override
	public synchronized void add(final URI uri, final HttpCookie cookie) {
		List<HttpCookie> cookies = mapCookies.get(uri);
		if (cookies == null) {
			cookies = new CopyOnWriteArrayList<>();
			mapCookies.put(uri, cookies);
		}
		cookies.add(cookie);
	}

	@Override
	public synchronized List<HttpCookie> get(final URI uri) {
		List<HttpCookie> lstCookies = mapCookies.get(uri);
		if (lstCookies == null) {
			mapCookies.put(uri, new CopyOnWriteArrayList<HttpCookie>());
		}
		return mapCookies.get(uri);
	}

	@Override
	public boolean removeAll() {
		mapCookies.clear();
		return true;
	}

	@Override
	public List<HttpCookie> getCookies() {
		List<HttpCookie> result = new CopyOnWriteArrayList<>();
		mapCookies.values().forEach(result::addAll);
		return result;
	}

	@Override
	public List<URI> getURIs() {
		return new CopyOnWriteArrayList<>(mapCookies.keySet());
	}

	@Override
	public boolean remove(final URI uri, final HttpCookie cookie) {
		List<HttpCookie> lstCookies = mapCookies.get(uri);
		return lstCookies == null ? false : lstCookies.remove(cookie);
	}
}
