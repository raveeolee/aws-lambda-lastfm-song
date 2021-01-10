package dev.may_i;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class SpotifyTrackTest {

    private Gson gson = new Gson();

    @Test
    void should_serialise_response_for_current_track() {
        String response = "{\n" +
                "  \"timestamp\": 1610300193755,\n" +
                "  \"context\": {\n" +
                "    \"external_urls\": {\n" +
                "      \"spotify\": \"https://open.spotify.com/artist/6d24kC5fxHFOSEAmjQPPhc\"\n" +
                "    },\n" +
                "    \"href\": \"https://api.spotify.com/v1/artists/6d24kC5fxHFOSEAmjQPPhc\",\n" +
                "    \"type\": \"artist\",\n" +
                "    \"uri\": \"spotify:artist:6d24kC5fxHFOSEAmjQPPhc\"\n" +
                "  },\n" +
                "  \"progress_ms\": 297802,\n" +
                "  \"item\": {\n" +
                "    \"album\": {\n" +
                "      \"album_type\": \"album\",\n" +
                "      \"artists\": [\n" +
                "        {\n" +
                "          \"external_urls\": {\n" +
                "            \"spotify\": \"https://open.spotify.com/artist/6d24kC5fxHFOSEAmjQPPhc\"\n" +
                "          },\n" +
                "          \"href\": \"https://api.spotify.com/v1/artists/6d24kC5fxHFOSEAmjQPPhc\",\n" +
                "          \"id\": \"6d24kC5fxHFOSEAmjQPPhc\",\n" +
                "          \"name\": \"Periphery\",\n" +
                "          \"type\": \"artist\",\n" +
                "          \"uri\": \"spotify:artist:6d24kC5fxHFOSEAmjQPPhc\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"external_urls\": {\n" +
                "        \"spotify\": \"https://open.spotify.com/album/1seeMmdvQUplCh1cTRbWJx\"\n" +
                "      },\n" +
                "      \"href\": \"https://api.spotify.com/v1/albums/1seeMmdvQUplCh1cTRbWJx\",\n" +
                "      \"id\": \"1seeMmdvQUplCh1cTRbWJx\",\n" +
                "      \"images\": [\n" +
                "        {\n" +
                "          \"height\": 640,\n" +
                "          \"url\": \"https://i.scdn.co/image/ab67616d0000b273ff0e4ca93eec94b21340f8e8\",\n" +
                "          \"width\": 640\n" +
                "        },\n" +
                "        {\n" +
                "          \"height\": 300,\n" +
                "          \"url\": \"https://i.scdn.co/image/ab67616d00001e02ff0e4ca93eec94b21340f8e8\",\n" +
                "          \"width\": 300\n" +
                "        },\n" +
                "        {\n" +
                "          \"height\": 64,\n" +
                "          \"url\": \"https://i.scdn.co/image/ab67616d00004851ff0e4ca93eec94b21340f8e8\",\n" +
                "          \"width\": 64\n" +
                "        }\n" +
                "      ],\n" +
                "      \"name\": \"Periphery IV: HAIL STAN\",\n" +
                "      \"release_date\": \"2019-04-05\",\n" +
                "      \"release_date_precision\": \"day\",\n" +
                "      \"total_tracks\": 9,\n" +
                "      \"type\": \"album\",\n" +
                "      \"uri\": \"spotify:album:1seeMmdvQUplCh1cTRbWJx\"\n" +
                "    },\n" +
                "    \"artists\": [\n" +
                "      {\n" +
                "        \"external_urls\": {\n" +
                "          \"spotify\": \"https://open.spotify.com/artist/6d24kC5fxHFOSEAmjQPPhc\"\n" +
                "        },\n" +
                "        \"href\": \"https://api.spotify.com/v1/artists/6d24kC5fxHFOSEAmjQPPhc\",\n" +
                "        \"id\": \"6d24kC5fxHFOSEAmjQPPhc\",\n" +
                "        \"name\": \"Periphery\",\n" +
                "        \"type\": \"artist\",\n" +
                "        \"uri\": \"spotify:artist:6d24kC5fxHFOSEAmjQPPhc\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"disc_number\": 1,\n" +
                "    \"duration_ms\": 358120,\n" +
                "    \"explicit\": false,\n" +
                "    \"external_ids\": {\n" +
                "      \"isrc\": \"USKO11900020\"\n" +
                "    },\n" +
                "    \"external_urls\": {\n" +
                "      \"spotify\": \"https://open.spotify.com/track/09TYM8GCSYtQmpaXe8kYuK\"\n" +
                "    },\n" +
                "    \"href\": \"https://api.spotify.com/v1/tracks/09TYM8GCSYtQmpaXe8kYuK\",\n" +
                "    \"id\": \"09TYM8GCSYtQmpaXe8kYuK\",\n" +
                "    \"is_local\": false,\n" +
                "    \"is_playable\": true,\n" +
                "    \"name\": \"Blood Eagle\",\n" +
                "    \"popularity\": 45,\n" +
                "    \"preview_url\": \"https://p.scdn.co/mp3-preview/33a2a6800d53a07fbfedcefba026f3f8bcce6734?cid=60b90e34b56949eaaf945441ffbc6eff\",\n" +
                "    \"track_number\": 2,\n" +
                "    \"type\": \"track\",\n" +
                "    \"uri\": \"spotify:track:09TYM8GCSYtQmpaXe8kYuK\"\n" +
                "  },\n" +
                "  \"currently_playing_type\": \"track\",\n" +
                "  \"actions\": {\n" +
                "    \"disallows\": {\n" +
                "      \"resuming\": true\n" +
                "    }\n" +
                "  },\n" +
                "  \"is_playing\": true\n" +
                "}";

        SpotifyTrack spotifyTrack = gson.fromJson(response, SpotifyTrack.class);
        assertEquals("Blood Eagle", spotifyTrack.getItem().getName());
        assertEquals("Periphery", spotifyTrack.getItem().getArtists().get(0).getName());
    }
}