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

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.AmazonSNSClientBuilder
import com.amazonaws.services.sns.model.PublishResult
import com.nhaarman.mockitokotlin2.whenever
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.junit.MockitoJUnitRunner
import org.opensearch.common.settings.SecureString
import org.opensearch.notifications.spi.model.MessageContent
import org.opensearch.notifications.spi.model.destination.SNSDestination
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
internal class DestinationSNSClientTests {

    @Mock
    private lateinit var amazonSNS: AmazonSNS

    @Mock
    private lateinit var builder: AmazonSNSClientBuilder

    @Test
    fun `SNS client should use default credential to publish message if no IAM credential`() {
        val region = "us-east-1"
        val title = "Test"
        val body = "This is a test"
        val topicArn = "arn:aws:sns:${region}:012345678989:test-notification"
        val messageId = "message-1"

        mockBuilderChain(region, DefaultAWSCredentialsProviderChain::class.java)
        whenever(amazonSNS.publish(topicArn, body, title))
            .thenReturn(PublishResult().withMessageId(messageId))

        val message = MessageContent(title, body)
        val destination = SNSDestination(topicArn, null, null, null)
        val snsClient = DestinationSNSClient(destination)
        val result = snsClient.execute(topicArn, message)
        assertEquals(messageId, result)
    }

    @Test
    fun `SNS client should use IAM credential to publish message if configured`() {
        val region = "us-west-2"
        val title = "Hello"
        val body = "World"
        val topicArn = "arn:aws:sns:${region}:012345678989:test-notification"
        val accessKey = SecureString("access-key".toCharArray())
        val secretKey = SecureString("secret-key".toCharArray())
        val messageId = "message-2"

        mockBuilderChain(region, AWSStaticCredentialsProvider::class.java)
        whenever(amazonSNS.publish(topicArn, body, title))
            .thenReturn(PublishResult().withMessageId(messageId))

        val message = MessageContent(title, body)
        val destination = SNSDestination(topicArn, null, accessKey, secretKey)
        val snsClient = DestinationSNSClient(destination)
        val result = snsClient.execute(topicArn, message)
        assertEquals(messageId, result)
    }

    private fun <T: AWSCredentialsProvider> mockBuilderChain(region: String, providerType: Class<T>) {
        mockkStatic(AmazonSNSClientBuilder::standard)
        every { AmazonSNSClientBuilder.standard() } returns builder
        whenever(builder.withRegion(region)).thenReturn(builder)
        whenever(builder.withCredentials(any(providerType))).thenReturn(builder)
        whenever(builder.build()).thenReturn(amazonSNS)
    }
}