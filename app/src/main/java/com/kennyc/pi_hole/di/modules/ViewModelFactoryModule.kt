package com.kennyc.pi_hole.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kennyc.pi_hole.MainViewModel
import com.kennyc.pi_hole.di.DaggerViewModelFactory
import com.kennyc.pi_hole.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: DaggerViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindCreatePinViewModel(viewModel: MainViewModel): ViewModel

}