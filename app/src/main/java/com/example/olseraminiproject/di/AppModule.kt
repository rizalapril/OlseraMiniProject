package com.example.olseraminiproject.di

import com.example.olseraminiproject.data.repository.CompanyRepository
import com.example.olseraminiproject.data.repository.CompanyRepositoryImpl
import com.example.olseraminiproject.viewmodel.DetailsViewModel
import com.example.olseraminiproject.viewmodel.MainViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module


val appModule = module {
    viewModel {
        MainViewModel(get(),get())
    }
    viewModel {
        DetailsViewModel(get(),get())
    }
}

val dataModule = module(createOnStart = true) {
    //repository
    single<CompanyRepository> {CompanyRepositoryImpl(get())}

}

val dataSource = listOf(appModule, dataModule)