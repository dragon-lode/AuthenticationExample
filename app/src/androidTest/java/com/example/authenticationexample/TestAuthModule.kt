package com.example.authenticationexample

import com.example.authenticationexample.data.AuthRepo
import com.example.authenticationexample.data.ticket.TicketRepo
import com.example.authenticationexample.data.user.UserRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.mockito.Mockito
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAuthModule {
    @Provides
    @Singleton
    fun provideAuthRepo(): AuthRepo = Mockito.mock(AuthRepo::class.java)
    @Provides
    @Singleton
    fun provideTicketRepo(): TicketRepo = Mockito.mock(TicketRepo::class.java)
    @Provides
    @Singleton
    fun provideUserRepo(): UserRepo = Mockito.mock(UserRepo::class.java)
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Mockito.mock(FirebaseAuth::class.java)
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Mockito.mock(FirebaseFirestore::class.java)
}