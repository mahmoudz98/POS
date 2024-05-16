package com.casecode.testing.di.data

import com.casecode.data.service.AccountServiceImpl
import com.casecode.data.service.AuthServiceImpl
import com.casecode.di.data.ServiceModule
import com.casecode.service.AccountService
import com.casecode.service.AuthService
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ServiceModule::class],
)
abstract class TestServiceModule {
    @Binds
    internal abstract fun bindAuthServiceTest(authServiceImpl: AuthServiceImpl): AuthService
    @Binds
    internal abstract fun bindAccountService(accountServiceImpl: AccountServiceImpl): AccountService
}