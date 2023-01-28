package com.hocel.mosiko.di

import android.content.Context
import com.hocel.mosiko.MosikoApplication
import com.hocel.mosiko.common.AppDatastore
import com.hocel.mosiko.data.MusyRepository
import com.hocel.mosiko.ui.MusicControllerViewModel
import com.hocel.mosiko.ui.album.AlbumViewModel
import com.hocel.mosiko.ui.artist.ArtistViewModel
import com.hocel.mosiko.ui.home.HomeViewModel
import com.hocel.mosiko.ui.playlist.PlaylistViewModel
import com.hocel.mosiko.common.ScanMusicViewModel
import com.hocel.mosiko.ui.search.SearchViewModel
import com.hocel.mosiko.utils.DatabaseUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): MosikoApplication {
        return app as MosikoApplication
    }

    @Singleton
    @Provides
    fun provideDatabaseUtil(application: MosikoApplication): DatabaseUtil = DatabaseUtil.getInstance(application)

    @Singleton
    @Provides
    fun provideAppDatastore(application: MosikoApplication): AppDatastore = AppDatastore.getInstance(application)

    @Singleton
    @Provides
    fun provideHomeViewModel(application: MosikoApplication): HomeViewModel = HomeViewModel(
        application,
        MusyRepository(provideDatabaseUtil(application))
    )

    @Singleton
    @Provides
    fun provideScanMusicViewModel(application: MosikoApplication): ScanMusicViewModel = ScanMusicViewModel(
        MusyRepository(provideDatabaseUtil(application))
    )

    @Singleton
    @Provides
    fun provideSearchViewModel(application: MosikoApplication): SearchViewModel = SearchViewModel(
        MusyRepository(provideDatabaseUtil(application))
    )

    @Singleton
    @Provides
    fun provideArtistViewModel(application: MosikoApplication): ArtistViewModel = ArtistViewModel(
        MusyRepository(provideDatabaseUtil(application))
    )

    @Singleton
    @Provides
    fun provideAlbumViewModel(application: MosikoApplication): AlbumViewModel = AlbumViewModel(
        MusyRepository(provideDatabaseUtil(application))
    )

    @Singleton
    @Provides
    fun providePlaylistViewModel(application: MosikoApplication): PlaylistViewModel = PlaylistViewModel(
        MusyRepository(provideDatabaseUtil(application))
    )

    @Singleton
    @Provides
    fun provideMusicControllerViewModel(application: MosikoApplication): MusicControllerViewModel = MusicControllerViewModel(
        application,
        MusyRepository(provideDatabaseUtil(application)),
        provideAppDatastore(application)
    )

}