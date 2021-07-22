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

import com.amazonaws.services.sns.AmazonSNSClientBuilder
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Test
import org.mockito.Mockito.mock
import org.opensearch.notifications.spi.model.MessageContent
import org.opensearch.notifications.spi.model.destination.SNSDestination

internal class DestinationSNSClientTests {

    @Test
    fun `SNS client should use default credential to publish message`() {
        val builder = mock(AmazonSNSClientBuilder::class.java)

        mockkStatic(AmazonSNSClientBuilder::standard)
        every { AmazonSNSClientBuilder.standard() } returns builder

        val topicArn = "arn arn:aws:sns:us-west-2:012345678989:test-notification"
        val destination = SNSDestination(topicArn, null, null, null)
        val message = MessageContent("title", "description")
        val snsClient = DestinationSNSClient(destination)
        val result = snsClient.execute(topicArn, message)
    }

    @Test
    fun `SNS client should use IAM credential to publish message`() {

    }
}