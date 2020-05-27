package org.wso2.carbon.identity.mobile.wso2verify

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import org.wso2.carbon.identity.mobile.wso2verify.Model.BiometricAuthProfile
import org.wso2.carbon.identity.mobile.wso2verify.Model.DiscoveryDataDTO

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.DATABASE_VERSION) {

    override fun onCreate(database: SQLiteDatabase?) {
        database!!.execSQL(DatabaseConstants.AUTH_PROFILE_CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(database: SQLiteDatabase?, p1: Int, p2: Int) {
        database!!.execSQL(DatabaseConstants.AUTH_PROFILE_DROP_TABLE_QUERY)
    }

    fun getProfilesFromQrData(discoveryData: DiscoveryDataDTO): ArrayList<BiometricAuthProfile>{

        val profiles: ArrayList<BiometricAuthProfile> = ArrayList()
        val AUTH_PROFILE_SELECT_QUERY = "SELECT * FROM BIOMETRIC_AUTH_PROFILE WHERE " +
                "${DatabaseConstants.USERNAME} = '${discoveryData.username}' " +
                "AND ${DatabaseConstants.TENANT_DOMAIN} = '${discoveryData.tennantDomain}' " +
                "AND ${DatabaseConstants.USER_STORE} = '${discoveryData.userStoreDomain}'"
        var database: SQLiteDatabase = this.readableDatabase
        var cursor: Cursor = database.rawQuery(AUTH_PROFILE_SELECT_QUERY,null)
        var profile:BiometricAuthProfile
        try {
            if (cursor.moveToFirst()) {
                do {
                    profile = BiometricAuthProfile()
                    profile.deviceId = cursor.getString(cursor.getColumnIndex(DatabaseConstants.DEVICE_ID))
                    profile.username = cursor.getString(cursor.getColumnIndex(DatabaseConstants.USERNAME))
                    profile.tenantDomain = cursor.getString(cursor.getColumnIndex(DatabaseConstants.TENANT_DOMAIN))
                    profile.userStore = cursor.getString(cursor.getColumnIndex(DatabaseConstants.USER_STORE))
                    profile.authUrl = cursor.getString(cursor.getColumnIndex(DatabaseConstants.AUTH_URL))
                    profile.privateKey = cursor.getString(cursor.getColumnIndex(DatabaseConstants.PRIVATE_KEY))
                    profiles.add(profile)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error while trying to get posts from database")
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }

        return profiles
    }

    fun addBiometricProfile(profile: BiometricAuthProfile) {
        val database: SQLiteDatabase = this.writableDatabase
        val values = ContentValues()
        values.put(DatabaseConstants.DEVICE_ID, profile.deviceId)
        values.put(DatabaseConstants.USERNAME, profile.username)
        values.put(DatabaseConstants.TENANT_DOMAIN, profile.tenantDomain)
        values.put(DatabaseConstants.USER_STORE, profile.userStore)
        values.put(DatabaseConstants.AUTH_URL, profile.authUrl)
        values.put(DatabaseConstants.PRIVATE_KEY, profile.privateKey)
        database.insert(DatabaseConstants.AUTH_PROFILE_TABLE_NAME, null, values)
        database.close()

    }

    fun getPrivateKey(deviceId: String): String{
        var privateKey: String = ""
        val GET_PRIVSTE_KEY_QUERY = "SELECT PRIVATE_KEY FROM BIOMETRIC_AUTH_PROFILE WHERE " +
                "${DatabaseConstants.DEVICE_ID} = '${deviceId}' "
        var database: SQLiteDatabase = this.readableDatabase
        var cursor: Cursor = database.rawQuery(GET_PRIVSTE_KEY_QUERY,null)
        try {
            if (cursor.moveToFirst()) {
                    privateKey = cursor.getString(cursor.getColumnIndex(DatabaseConstants.PRIVATE_KEY))
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error while trying to get posts from database")
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }

        return privateKey;
    }

    fun listAllProfiles(): ArrayList<BiometricAuthProfile>{

        val profiles: ArrayList<BiometricAuthProfile> = ArrayList()
        var database: SQLiteDatabase = this.readableDatabase
        var cursor: Cursor = database.rawQuery(DatabaseConstants.LIST_ALL_PROFILES_QUERY,null)
        var profile:BiometricAuthProfile
        try {
            if (cursor.moveToFirst()) {
                do {
                    profile = BiometricAuthProfile()
                    profile.deviceId = cursor.getString(cursor.getColumnIndex(DatabaseConstants.DEVICE_ID))
                    profile.username = cursor.getString(cursor.getColumnIndex(DatabaseConstants.USERNAME))
                    profile.tenantDomain = cursor.getString(cursor.getColumnIndex(DatabaseConstants.TENANT_DOMAIN))
                    profile.userStore = cursor.getString(cursor.getColumnIndex(DatabaseConstants.USER_STORE))
                    profiles.add(profile)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error while trying to get posts from database")
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }

        return profiles
    }
}