package com.kennyc.pi_hole.di.modules

import com.kennyc.api.pi_hole.di.PiholeApiComponent
import com.kennyc.data.pi_hole.PiholeRepository
import com.kennyc.pi_hole.BuildConfig
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DataModule {

    @Provides
    @Singleton
    @JvmStatic
    fun providesPiholeComponent(): PiholeApiComponent = PiholeApiComponent.builder()
        .auth(BuildConfig.PIHOLE_PASSWORD)
        .build()

    @Provides
    @Singleton
    @JvmStatic
    fun providesRepository(component: PiholeApiComponent): PiholeRepository =
        PiholeRepository(component.api())
}