package com.example.authenticationexample.data.user

import com.example.authenticationexample.data.Repository
import com.example.authenticationexample.data.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UserRepo : Repository<User> {
    suspend fun createUserProfile(newUserDetails: User): Response
    suspend fun getUserRole(uid: String): UserRole
}

class UserRepository @Inject constructor(
    private val dao: UserDao
) : UserRepo {

    override suspend fun createUserProfile(newUserDetails: User) = dao.create(newUserDetails)

    override suspend fun insert(item: User) = dao.add(item)


    override suspend fun delete(id: String) = dao.delete(id)


    override suspend fun update(item: User) = dao.update(item)

    override fun findAll(): Flow<List<User>> = dao.getAll()

    override suspend fun findById(id: String): User? = dao.getById(id)

    override suspend fun getUserRole(uid: String): UserRole {
        val user = dao.getById(uid)

        return user?.role ?: UserRole.UNKNOWN
    }
}