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
package org.opensearch.notifications.spi.client

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.AmazonSNSClientBuilder
import com.amazonaws.services.sns.model.PublishResult
import com.nhaarman.mockitokotlin2.whenever
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.opensearch.notifications.spi.model.MessageContent
import org.opensearch.notifications.spi.model.destination.SNSDestination
import kotlin.test.assertEquals

internal class DestinationSNSClientTests {

    @Test
    fun `SNS client should use default credential to publish message`() {
        val region = "us-east-1"
        val title = "Test"
        val body = "This is a test"
        val topicArn = "arn:aws:sns:${region}:012345678989:test-notification"
        val messageId = "message-1"

        val amazonSNS = mock(AmazonSNS::class.java)
        val builder = mock(AmazonSNSClientBuilder::class.java)
        mockkStatic(AmazonSNSClientBuilder::standard)
        every { AmazonSNSClientBuilder.standard() } returns builder
        whenever(builder.withRegion(region)).thenReturn(builder)
        whenever(builder.withCredentials(any(DefaultAWSCredentialsProviderChain::class.java)))
            .thenReturn(builder)
        whenever(builder.build()).thenReturn(amazonSNS)
        whenever(amazonSNS.publish(topicArn, body, title))
            .thenReturn(PublishResult().withMessageId(messageId))

        val message = MessageContent(title, body)
        val destination = SNSDestination(topicArn, null, null, null)
        val snsClient = DestinationSNSClient(destination)
        val result = snsClient.execute(topicArn, message)
        assertEquals(messageId, result)
    }

    @Test
    fun `SNS client should use IAM credential to publish message`() {

    }
}