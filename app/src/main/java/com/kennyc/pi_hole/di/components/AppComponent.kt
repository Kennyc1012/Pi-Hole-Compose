package com.kennyc.pi_hole.di.components


import android.content.Context
import com.kennyc.pi_hole.MainActivity
import com.kennyc.pi_hole.di.modules.DataModule
import com.kennyc.pi_hole.di.modules.ViewModelFactoryModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DataModule::class, ViewModelFactoryModule::class])
interface AppComponent {

    fun inject(activity: MainActivity)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun appContext(context: Context): Builder

        fun build(): AppComponent
    }
}