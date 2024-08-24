package com.casecode.pos.core.data.di

import com.casecode.pos.core.data.service.AccountService
import com.casecode.pos.core.data.service.AccountServiceImpl
import com.casecode.pos.core.data.service.AuthService
import com.casecode.pos.core.data.service.AuthServiceImpl
import com.casecode.pos.core.data.service.LogService
import com.casecode.pos.core.data.service.LogServiceImpl
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
    internal abstract fun bindLogService(logServiceImpl: LogServiceImpl): LogService

    @Binds
    internal abstract fun bindAccountService(accountServiceImpl: AccountServiceImpl): AccountService
}