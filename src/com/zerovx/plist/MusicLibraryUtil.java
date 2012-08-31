package com.zerovx.plist;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zerovx.plist.MusicLibrary.Library;
import com.zerovx.plist.MusicLibrary.Movie;
import com.zerovx.plist.MusicLibrary.Playlist;
import com.zerovx.plist.MusicLibrary.Playlists;
import com.zerovx.plist.MusicLibrary.Podcast;
import com.zerovx.plist.MusicLibrary.Podcasts;
import com.zerovx.plist.MusicLibrary.TVShow;
import com.zerovx.plist.MusicLibrary.Track;
import com.zerovx.plist.MusicLibrary.Tracks;
import com.zerovx.plist.MusicLibrary.Video;
import com.zerovx.plist.MusicLibrary.Videos;

public class MusicLibraryUtil {

	public static Library plisToMusicLibrary(String file) {
		Map<String, Object> musicMap = PlistUtil.plistToMap(file);
		return mapToMusicLibrary(musicMap);
	}

	public static Library plisToMusicLibrary(File file) throws IOException {
		Map<String, Object> musicMap = PlistUtil.plistToMap(file);
		return mapToMusicLibrary(musicMap);
	}

	@SuppressWarnings("unchecked")
	private static MusicLibrary.Library mapToMusicLibrary(Map<String, Object> plistMap) {

		if (!plistMap.containsKey("plist"))
			throw new RuntimeException();
		Map<String, Object> lib = (Map<String, Object>) plistMap.get("plist");
		Quad<Map<Long, Track>, Map<Long, TVShow>, Map<Long, Movie>, Map<Long, Podcast>> tracks = mapToTracksMap((Map<String, Map<String, Object>>) lib.get("Tracks"));
		Map<Long, Playlist> playlists = mapToPlaylistsMap(tracks, (List<Map<String, Object>>) lib.get("Playlists"));

		Library library = new Library();
		library.playlists = new Playlists();
		library.playlists.playlists = playlists.values();
		library.podcasts = new Podcasts();
		library.podcasts.podcasts = tracks.d.values();
		library.tracks = new Tracks();
		library.tracks.tracks = tracks.a.values();
		library.videos = new Videos();
		library.videos.movies = tracks.c.values();
		library.videos.tvshows = tracks.b.values();
		return library;
	}

	@SuppressWarnings("unchecked")
	private static Map<Long, Playlist> mapToPlaylistsMap(Quad<Map<Long, Track>, Map<Long, TVShow>, Map<Long, Movie>, Map<Long, Podcast>> tracks,
			List<Map<String, Object>> list) {
		Map<Long, Playlist> playlists = new HashMap<Long, MusicLibrary.Playlist>();
		for (Map<String, Object> m : list) {
			Playlist p = new Playlist();
			p.tracks = new LinkedList<MusicLibrary.Track>();
			if (m.containsKey("Name"))
				p.name = (String) m.get("Name");
			List<Map<String, Long>> listOfTracks = (List<Map<String, Long>>) m.get("Playlist Items");
			if (listOfTracks != null) {
				for (Map<String, Long> maps : listOfTracks) {
					Long key = maps.get("Track ID");
					if (tracks.a.containsKey(key)) {
						p.tracks.add(tracks.a.get(key));
						continue;
					}
					if (tracks.b.containsKey(key)) {
						p.tracks.add(tracks.b.get(key));
						continue;
					}
					if (tracks.c.containsKey(key)) {
						p.tracks.add(tracks.c.get(key));
						continue;
					}
					if (tracks.d.containsKey(key)) {
						p.tracks.add(tracks.d.get(key));
						continue;
					}
					throw new RuntimeException("Track Not Found");
				}
			}
			playlists.put((Long) m.get("Playlist ID"), p);

		}
		return playlists;
	}

	private static Quad<Map<Long, Track>, Map<Long, TVShow>, Map<Long, Movie>, Map<Long, Podcast>> mapToTracksMap(Map<String, Map<String, Object>> map) {
		Map<Long, Track> tracks = new HashMap<Long, MusicLibrary.Track>();
		Map<Long, TVShow> tvshows = new HashMap<Long, MusicLibrary.TVShow>();
		Map<Long, Movie> movies = new HashMap<Long, MusicLibrary.Movie>();
		Map<Long, Podcast> podcasts = new HashMap<Long, MusicLibrary.Podcast>();
		for (Map<String, Object> m : map.values()) {
			if (m.containsKey("Movie")) {
				Movie t = new Movie();
				videoToTrack(m, t);
				movies.put((Long) m.get("Track ID"), t);
			} else if (m.containsKey("TV Show")) {
				TVShow t = new TVShow();
				videoToTrack(m, t);
				if (m.containsKey("Series"))
					t.series = (String) m.get("Series");
				if (m.containsKey("Season"))
					t.season = (Long) m.get("Season");
				if (m.containsKey("Episode"))
					t.episode = (String) m.get("Episode");

				tvshows.put((Long) m.get("Track ID"), t);
			} else if (m.containsKey("Podcast")) {
				Podcast t = new Podcast();
				mapToTrack(m, t);

				podcasts.put((Long) m.get("Track ID"), t);
			} else {
				Track t = new Track();
				mapToTrack(m, t);
				tracks.put((Long) m.get("Track ID"), t);
			}
		}
		return new Quad<Map<Long, Track>, Map<Long, TVShow>, Map<Long, Movie>, Map<Long, Podcast>>(tracks, tvshows, movies, podcasts);
	}

	private static void videoToTrack(Map<String, Object> m, Video t) {
		mapToTrack(m, t);
		if (m.containsKey("HD"))
			t.hd = (Boolean) m.get("HD");
		if (m.containsKey("Width"))
			t.width = (Long) m.get("Width");
		if (m.containsKey("Height"))
			t.height = (Long) m.get("Height");
	}

	private static void mapToTrack(Map<String, Object> m, Track t) {
		if (m.containsKey("Name"))
			t.name = (String) m.get("Name");
		if (m.containsKey("Artist"))
			t.artist = (String) m.get("Artist");
		if (m.containsKey("Album"))
			t.album = (String) m.get("Album");
		if (m.containsKey("Genre"))
			t.genre = (String) m.get("Genre");
		if (m.containsKey("Release Date"))
			t.releaseDate = (String) m.get("Release Date");
		if (m.containsKey("Year"))
			t.year = (Long) m.get("Year");
		if (m.containsKey("Length"))
			t.length = (Long) m.get("Length");
		if (m.containsKey("Bit Rate"))
			t.bitRate = (Long) m.get("Bit Rate");
		if (m.containsKey("Play Count"))
			t.playCount = (Long) m.get("Play Count");
		if (m.containsKey("Play Date"))
			t.lastPlayDate = (Long) m.get("Play Date");
		if (m.containsKey("Rating"))
			t.rating = (Long) m.get("Rating");
		if (m.containsKey("Album Rating"))
			t.albumRating = (Long) m.get("Album Rating");

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Library ret = null;
		if (args.length == 0)
			ret = plisToMusicLibrary("C:\\itml.xml");
		else
			ret = plisToMusicLibrary(args[1]);

	}

	public static final class Pair<A, B> {
		public A a;
		public B b;
	}

	public static final class Quad<A, B, C, D> {
		public A a;
		public B b;
		public C c;
		public D d;

		public Quad(A _a, B _b, C _c, D _d) {
			a = _a;
			b = _b;
			c = _c;
			d = _d;
		}
	}

}
