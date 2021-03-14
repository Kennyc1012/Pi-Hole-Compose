package com.kennyc.api.pi_hole.di

import com.kennyc.api.pi_hole.PiholeApi
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = [PiholeApiModule::class])
interface PiholeApiComponent {

    fun api(): PiholeApi

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun auth(@Named("auth") auth: String): Builder

        fun build(): PiholeApiComponent
    }

    companion object {
        fun builder(): Builder = DaggerPiholeApiComponent.builder()
    }
}