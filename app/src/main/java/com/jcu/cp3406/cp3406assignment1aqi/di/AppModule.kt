package com.jcu.cp3406.cp3406assignment1aqi.di

import com.jcu.cp3406.cp3406assignment1aqi.data.repository.AqiRepository
import com.jcu.cp3406.cp3406assignment1aqi.presentation.viewmodel.AqiViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AqiRepository() }
    viewModel { AqiViewModel(get()) }
}