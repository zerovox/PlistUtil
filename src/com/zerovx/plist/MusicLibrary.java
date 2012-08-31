package com.zerovx.plist;

import java.util.Collection;

public class MusicLibrary {

	public static class Library {
		public Tracks tracks;
		public Playlists playlists;
		public Videos videos;
		public Podcasts podcasts;
	}

	public static class Playlists {
		public Collection<Playlist> playlists;
	}

	public static class Playlist {
		public String name;
		public Collection<Track> tracks;
	}

	public static class Videos {
		public Collection<TVShow> tvshows;
		public Collection<Movie> movies;
	}

	public abstract static class Video extends Track {
		public Boolean hd;
		public Long height;
		public Long width;
	}

	public static class TVShow extends Video {
		public String series;
		public Long season;
		public String episode;
	}

	public static class Movie extends Video {

	}

	public static class Podcast extends Track {

	}

	public static class Podcasts {
		public Collection<Podcast> podcasts;
	}

	public static class Tracks {
		public Collection<Track> tracks;
	}

	public static class Track {
		public String name;
		public String artist;
		public String album;
		public Long year;
		public String genre;
		public Long length;
		public Long bitRate;
		public Long playCount;
		public Long rating;
		public Long albumRating;
		public Long lastPlayDate;
		public String releaseDate;
	}

}
