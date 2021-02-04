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

import kotlinx.coroutines.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ServerManagementTest {
    companion object {
        // Server INIT related
        private val coroutineScope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
        private var job: Job? = null
        private var process: Process? = null
        private val serverURL: String = "https://github.com/Navi-Cloud/Navi-Server/releases/download/v0.4.0-ALPHA/NavIServer-1.0-SNAPSHOT.jar"
        private val targetServerDirectory: File = File(System.getProperty("java.io.tmpdir"), "testServer.jar")
        private val targetServerRoot: File = File(System.getProperty("java.io.tmpdir"), "naviTesting")
        private val targetServerSettings: File = File(System.getProperty("java.io.tmpdir"), "application-test.properties")

        @BeforeClass
        @JvmStatic
        fun initServer() {
            // Create Server Directory
            targetServerRoot.mkdir()

            // Download server to targetServerDirectory
            println("Downloading files...")
            URL(serverURL).openStream().use { `in` ->
                val serverPath: Path = Paths.get(targetServerDirectory.absolutePath)
                Files.copy(`in`, serverPath)
            }
            println("Download Finished!")

            // Set Property file
            val propertyString: String = """
            spring.jpa.show-sql=true
            spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect
            spring.h2.console.enabled=true
            navi.server-root=${targetServerRoot}
        """.trimIndent()
            val writer: BufferedWriter = BufferedWriter(FileWriter(targetServerSettings))
            writer.write(propertyString)
            writer.close()

            job = coroutineScope.launch {
                runInterruptible {
                    val command: Array<String> = arrayOf("java", "-jar", targetServerDirectory.absolutePath, "--spring.config.location=${targetServerSettings.absolutePath}")
                    process = Runtime.getRuntime().exec(command)
                }
            }

            runBlocking {
                delay(30 * 1000)
            }
        }

        @AfterClass
        @JvmStatic
        fun destroyServer() {
            println("Destroying..")
            // Kill Java process
            runBlocking {
                process?.destroy()
                job?.cancelAndJoin()
            }
            if (targetServerRoot.exists()) targetServerRoot.deleteRecursively()
            if (targetServerDirectory.exists()) targetServerDirectory.delete()
            if (targetServerSettings.exists()) targetServerSettings.delete()
        }
    }

    @Test
    fun isServerCommunicationInitiated() {
        assertThat(ServerManagement.initServerCommunication("localhost", "8080"))
            .isEqualTo(true)
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