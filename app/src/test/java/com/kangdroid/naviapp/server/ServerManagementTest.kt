/**
 * ServerManagementTest: Server-Related basic test
 * Author: KangDroid[Jason.HW.Kang]
 * Warning: This will fail if server is NOT running on host machine for now.
 * Any External CI/CD - Workflow will fail, unless we are integrating server
 * itself when testing.
 *
 * TODO: Integrate RC Jar-Based Server under @Before annotation via Github Release Channel.
 */

package com.kangdroid.naviapp.server

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import retrofit2.Retrofit
import java.lang.reflect.Field

class ServerManagementTest {
    @Test
    fun isRetroFitInitiated() {
        val field: Field = ServerManagement.javaClass.getDeclaredField("retroFit").apply {
            isAccessible = true
        }
        val retroFit = field.get(ServerManagement) as Retrofit?
        assertThat(retroFit).isNotEqualTo(null)
    }

    @Test
    fun isApiInitiated() {
        val field: Field = ServerManagement.javaClass.getDeclaredField("api").apply {
            isAccessible = true
        }
        val apiInterface = field.get(ServerManagement) as APIInterface?

        assertThat(apiInterface).isNotEqualTo(null)
    }

    @Test
    fun isGettingRootTokenWorks() {
        assertThat(ServerManagement.getRootToken()).isNotEqualTo("")
    }

    @Test
    fun isGetInsideFilesWorks() {
        assertThat(ServerManagement.getInsideFiles(ServerManagement.getRootToken())).isNotEqualTo(
            null
        )
    }
}