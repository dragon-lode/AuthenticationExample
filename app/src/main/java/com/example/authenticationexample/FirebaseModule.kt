package com.example.authenticationexample

import com.example.authenticationexample.data.AuthRepo
import com.example.authenticationexample.data.AuthRepository
import com.example.authenticationexample.data.user.UserRepo
import com.example.authenticationexample.data.user.UserRepository
import com.example.authenticationexample.data.ticket.TicketRepo
import com.example.authenticationexample.data.ticket.TicketRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepository: AuthRepository
    ): AuthRepo

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepository: UserRepository
    ): UserRepo

    @Binds
    @Singleton
    abstract fun bindTicketRepository(
        ticketRepository: TicketRepository
    ): TicketRepo

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth {
            val auth = FirebaseAuth.getInstance()
            if (BuildConfig.DEBUG) {
                try {
                    auth.useEmulator("10.0.2.2", 9099)
                } catch (e: Exception) {
                    // Handle or log potential emulator connection issues
                }
            }
            return auth
        }

        @Provides
        @Singleton
        fun provideFirestore(): FirebaseFirestore {
            val firestore = Firebase.firestore
            if (BuildConfig.DEBUG) {
                try {
                    firestore.useEmulator("10.0.2.2", 8080)
                } catch (e: Exception) {
                    // Handle or log potential emulator connection issues
                }
            }
            return firestore
        }
    }
}