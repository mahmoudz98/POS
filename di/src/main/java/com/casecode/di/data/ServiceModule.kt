package com.casecode.di.data

import com.casecode.data.service.AccountServiceImpl
import com.casecode.data.service.AuthServiceImpl
import com.casecode.service.AccountService
import com.casecode.service.AuthService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    internal abstract fun bindAuthService(authServiceImpl: AuthServiceImpl): AuthService

    @Binds
    internal abstract fun bindAccountService(accountServiceImpl: AccountServiceImpl): AccountService

}