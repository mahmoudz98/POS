package com.casecode.pos.core.testing.di.data

import com.casecode.pos.core.data.di.ServiceModule
import com.casecode.pos.core.data.service.AccountService
import com.casecode.pos.core.data.service.AuthService
import com.casecode.pos.core.testing.service.TestAccountService
import com.casecode.pos.core.testing.service.TestAuthService
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
    internal abstract fun bindAuthServiceTest(authService: TestAuthService): AuthService

    @Binds
    internal abstract fun bindAccountService(accountService: TestAccountService): AccountService
}