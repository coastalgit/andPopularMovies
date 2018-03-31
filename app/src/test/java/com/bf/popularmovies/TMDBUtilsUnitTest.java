package com.bf.popularmovies;

import com.bf.popularmovies.model.TMDBGenre;
import com.bf.popularmovies.model.TMDBGenres;
import com.bf.popularmovies.utility.TMDBUtils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

//@RunWith(AndroidJUnit4.class)
public class TMDBUtilsUnitTest {

    //private static String API_KEY = "ThisIsADummyApiKeyString";

    @Test
    public void genreList_isCorrect() {

        TMDBGenres tmdbGenres = mockTMDBGenres();

        //int[] genreIds = {28,10752};
        List<Integer> genreIds = Arrays.asList(28,10752);

        ArrayList<String> genreNames = TMDBUtils.buildGenreStringListById(tmdbGenres, genreIds);
        int expected = 2;

        assertEquals("Returned genre matchcount", expected, genreNames.size());
    }

    @Test
    public void genreList_isMismatch() {

        TMDBGenres tmdbGenres = mockTMDBGenres();

        //int[] genreIds = {28,10752,80,999};
        List<Integer> genreIds = Arrays.asList(28,10752,80,999);

        ArrayList<String> genreNames = TMDBUtils.buildGenreStringListById(tmdbGenres, genreIds);
        int expected = 2;

        assertTrue("Returned genre mismatch count", genreNames.size() > expected);
    }

    private TMDBGenres mockTMDBGenres(){
        TMDBGenres tmdbGenres = new TMDBGenres();
        ArrayList<TMDBGenre> genreObjects = new ArrayList<>();

        TMDBGenre genre1 = new TMDBGenre();
        genre1.setId(28);
        genre1.setName("Action");
        genreObjects.add(genre1);

        TMDBGenre genre2 = new TMDBGenre();
        genre2.setId(80);
        genre2.setName("Crime");
        genreObjects.add(genre2);

        TMDBGenre genre3 = new TMDBGenre();
        genre3.setId(9468);
        genre3.setName("Mystery");
        genreObjects.add(genre3);

        TMDBGenre genre4 = new TMDBGenre();
        genre4.setId(10752);
        genre4.setName("War");
        genreObjects.add(genre4);

        tmdbGenres.setGenres(genreObjects);
        return tmdbGenres;
    }
}