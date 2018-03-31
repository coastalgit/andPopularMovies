package com.bf.popularmovies;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.junit.Assert.*;

import com.bf.popularmovies.db.MovieDbTransactionManager;
import com.bf.popularmovies.db.MoviesContentProvider;
import com.bf.popularmovies.db.MoviesContract;
import com.bf.popularmovies.db.MoviesDbHelper;
import com.bf.popularmovies.model.TMDBMovie;

import java.util.concurrent.CountDownLatch;

@SuppressWarnings({"FieldCanBeLocal", "SameParameterValue"})
@RunWith(AndroidJUnit4.class)
public class TestMovieDatabase {

    private final Context mContext = InstrumentationRegistry.getTargetContext();
    private final String mPackageName = "com.bf.popularmovies";
    private TMDBMovie mTestMovie1, mTestMovie2;

    @Before
    public void setUp() {
        /* Access to a writable database */
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(MoviesContract.MovieEntry.TABLE_NAME, null, null);
        mTestMovie1 = createTestMovie(100000);
        //mTestMovie2 = createTestMovie(200000);
    }

    private TMDBMovie createTestMovie(int uniqueId){
        TMDBMovie movie = new TMDBMovie();
        movie.setId(uniqueId);
        movie.setVoteAverage(9.9);
        movie.setTitle("I am the Movie Title of "+String.valueOf(uniqueId));
        movie.setPosterPath("/I_am_the_poster_path_of_"+String.valueOf(uniqueId));
        movie.setBackdropPath("/I_am_the_backdrop_path_of_"+String.valueOf(uniqueId));
        movie.setOverview("I am the Overview of "+String.valueOf(uniqueId));
        movie.setReleaseDate("2018-01-01");
        movie.setOriginalLanguage("en");
        return movie;
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        assertEquals(mPackageName, mContext.getPackageName());
    }

    /**
     * This test checks to make sure that the content provider is registered correctly in the
     * AndroidManifest file.
     */
    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    public void testProviderRegistry() {

        String packageName = mContext.getPackageName();
        String taskProviderClassName = MoviesContentProvider.class.getName();
        ComponentName componentName = new ComponentName(packageName, taskProviderClassName);

        try {
            PackageManager pm = mContext.getPackageManager();

            /* The ProviderInfo will contain the authority, which is what we want to test */
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            String actualAuthority = providerInfo.authority;
            String expectedAuthority = packageName;

            /* Make sure that the registered authority matches the authority from the Contract */
            String incorrectAuthority =
                    "Error: The ContentProvider registered with authority: " + actualAuthority +
                            " instead of expected authority: " + expectedAuthority;
            assertEquals(incorrectAuthority,
                    actualAuthority,
                    expectedAuthority);

        } catch (PackageManager.NameNotFoundException e) {
            String providerNotRegisteredAtAll =
                    "Error: The ContentProvider not registered at " + mContext.getPackageName();
            fail(providerNotRegisteredAtAll);
        }
    }

    // *** Test UriMatcher ***

    private static final Uri TEST_MOVIE = MoviesContract.MovieEntry.CONTENT_URI;
    private static final Uri MOVIE_TASK_WITH_TESTID = TEST_MOVIE.buildUpon().appendPath("123456").build();

    @Test
    public void testUriMatcher() {

        UriMatcher testMatcher = MoviesContentProvider.buildUriMatcher();

        /* Test that the code returned from our matcher matches the expected TASKS int */
        String tasksUriDoesNotMatch = "Error: The TASKS URI was matched incorrectly.";
        int actualMoviesMatchCode = testMatcher.match(TEST_MOVIE);
        int expectedMoviesMatchCode = MoviesContentProvider.CODE_MOVIES;
        assertEquals(tasksUriDoesNotMatch,
                actualMoviesMatchCode,
                actualMoviesMatchCode);

        /* Test that the code returned from our matcher matches the expected TASK_WITH_ID */
        String taskWithIdDoesNotMatch = "ERROR: The MOVIE_TASK_WITH_TESTID URI was matched incorrectly.";
        int actualTaskWithIdCode = testMatcher.match(MOVIE_TASK_WITH_TESTID);
        int expectedTaskWithIdCode = MoviesContentProvider.CODE_MOVIE_BY_ID;
        assertEquals(taskWithIdDoesNotMatch,
                actualTaskWithIdCode,
                expectedTaskWithIdCode);
    }

    @Test
    public void testSingleRecord_insert() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.MovieEntry.COLUMN_ID, mTestMovie1.getId());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, mTestMovie1.getVoteAverage());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, mTestMovie1.getTitle());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, mTestMovie1.getPosterPath());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH, mTestMovie1.getBackdropPath());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, mTestMovie1.getOverview());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, mTestMovie1.getReleaseDate());
        contentValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_LANG, mTestMovie1.getOriginalLanguage());


        MovieDbTransactionManager mDbTransactionManager = new MovieDbTransactionManager(mContext, new MovieDbTransactionManager.onDbTransactionHandler() {
            @Override
            public void onFaveMovieAdded_Ok() {
                latch.countDown();
                assertTrue(true);
            }

            @Override
            public void onFaveMovieAdded_Fail(String errorMsg) {
                latch.countDown();
                Assert.fail("onFaveMovieAdded_Fail");
            }

            @Override
            public void onFaveMovieRemoved() {
                latch.countDown();
                Assert.fail("onFaveMovieRemoved");
            }
        });

        mDbTransactionManager.doAddMovieToFavorites(mTestMovie1);
        latch.await();
    }

}
