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
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import java.lang.reflect.Field

class ServerManagementTest {
    private val serverAddr: String = "localhost"
    private val serverPort: String = "8080"

    @Before
    fun init() {
        ServerManagement.initServerCommunication(serverAddr, serverPort)
    }

    @Test
    fun isServerInitiated() {
        assertThat(ServerManagement.initServerCommunication(serverAddr, serverPort)).isEqualTo(true)
    }

    @Test
    fun isGettingRootTokenWorks() {
        assertThat(ServerManagement.getRootToken()).isNotEqualTo("")
    }

    @Test
    fun isGetInsideFilesWorks() {
        assertThat(ServerManagement.getInsideFiles(ServerManagement.getRootToken())).isNotEqualTo(null)
    }
}