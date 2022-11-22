package tech.zemn.mobile.data

import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class UserPreferencesSerializer @Inject constructor() : Serializer<UserPreferences> {
    override val defaultValue: UserPreferences
        get() = UserPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserPreferences =
        try {
            UserPreferences.parseFrom(input)
        } catch (exception: Exception) {
            defaultValue
        }


    override suspend fun writeTo(t: UserPreferences, output: OutputStream) = t.writeTo(output)
}