/*
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  The OpenSearch Contributors require contributions made to
 *  this file be licensed under the Apache-2.0 license or a
 *  compatible open source license.
 *
 *  Modifications Copyright OpenSearch Contributors. See
 *  GitHub history for details.
 */
package org.opensearch.notifications.spi.factory

import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.opensearch.notifications.spi.client.DestinationClientPool
import org.opensearch.notifications.spi.client.DestinationSNSClient
import org.opensearch.notifications.spi.model.MessageContent
import org.opensearch.notifications.spi.model.destination.SNSDestination
import org.opensearch.rest.RestStatus

internal class SNSDestinationFactoryTests {

    private val snsDestinationFactory = SNSDestinationFactory()

    @Test
    fun `Send message should call SNS client to send message`() {
        val topicArn = "topic-1"
        val message = MessageContent("title", "description")
        val snsClient = mock(DestinationSNSClient::class.java)
        whenever(snsClient.execute(topicArn, message)).thenReturn("success")

        mockkObject(DestinationClientPool)
        every { DestinationClientPool.getSNSClient(any()) } returns snsClient

        val destination = SNSDestination(topicArn)
        val response = snsDestinationFactory.sendMessage(destination, message)
        verify(snsClient, times(1)).execute(topicArn, message)
        assertEquals(RestStatus.OK.status, response.statusCode)
        assertEquals("success", response.statusText)
    }
}