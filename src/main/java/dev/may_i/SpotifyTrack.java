package dev.may_i;

import java.util.List;

public class SpotifyTrack {
    private Item item;

    public Item getItem() {
        return item;
    }

    public static class Item {
        private String name;
        private List<Artist> artists;

        public String getName() {
            return name;
        }

        public List<Artist> getArtists() {
            return artists;
        }
    }

    public static class Artist {
        private String name;

        public String getName() {
            return name;
        }
    }
}
