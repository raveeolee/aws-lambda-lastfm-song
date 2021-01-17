package dev.may_i.domain;

import dev.may_i.ArtistResponseJson;

import java.util.List;
import java.util.stream.Collectors;

public class SpotifyTrack {
    private Item item;

    public SpotifyTrack(Item item) {
        this.item = item;
    }

    public SpotifyTrack() {
    }

    public Item getItem() {
        return item;
    }

    public static class Item {
        private String name;
        private List<Artist> artists;

        public Item(String name, List<Artist> artists) {
            this.name = name;
            this.artists = artists;
        }

        public Item() {
        }

        public String getName() {
            return name;
        }

        public List<Artist> getArtists() {
            return artists;
        }
    }

    public static class Artist {
        private String name;

        public Artist(String name) {
            this.name = name;
        }

        public Artist() {
        }

        public String getName() {
            return name;
        }
    }

    public ArtistResponseJson toJson() {
        String artists = getItem().getArtists().stream()
                .map(SpotifyTrack.Artist::getName)
                .collect(Collectors.joining(","));
        return new ArtistResponseJson(artists, getItem().getName());
    }

}
