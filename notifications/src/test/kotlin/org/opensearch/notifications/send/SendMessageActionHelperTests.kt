/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 *
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */
package org.opensearch.notifications.send

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.RETURNS_DEEP_STUBS
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness.LENIENT
import org.opensearch.commons.notifications.action.SendNotificationRequest
import org.opensearch.commons.notifications.model.ChannelMessage
import org.opensearch.commons.notifications.model.ConfigType
import org.opensearch.commons.notifications.model.EventSource
import org.opensearch.commons.notifications.model.Feature
import org.opensearch.commons.notifications.model.SeverityType
import org.opensearch.notifications.index.ConfigOperations
import org.opensearch.notifications.index.EventOperations
import org.opensearch.notifications.model.DocInfo
import org.opensearch.notifications.model.NotificationConfigDoc
import org.opensearch.notifications.model.NotificationConfigDocInfo
import org.opensearch.notifications.security.UserAccess

@ExtendWith(MockitoExtension::class)
//@MockitoSettings(strictness = LENIENT)
internal class SendMessageActionHelperTests {

    @Mock
    private lateinit var configOperations: ConfigOperations

    @Mock
    private lateinit var eventOperations: EventOperations

    @Mock
    private lateinit var userAccess: UserAccess

    @BeforeEach
    fun setUp() {
        SendMessageActionHelper.initialize(configOperations, eventOperations, userAccess)
    }

    @Test
    fun `Action helper should send SNS message`() {
        val notificationInfo = EventSource(
            "title",
            "reference_id",
            Feature.REPORTS,
            SeverityType.HIGH,
            listOf("tag1", "tag2")
        )
        val channelMessage = ChannelMessage(
            "text_description",
            "<b>htmlDescription</b>",
            null
        )
        val request = SendNotificationRequest(
            notificationInfo,
            channelMessage,
            listOf("channelId1"),
            "sample-thread-context"
        )

        val docInfo = DocInfo("doc-1", 1, 1, 1)
        val configDoc = mock(NotificationConfigDoc::class.java, RETURNS_DEEP_STUBS)
        val configDocInfo = NotificationConfigDocInfo(docInfo, configDoc)
        whenever(configDoc.config.name).thenReturn("config")
        whenever(configDoc.config.configType).thenReturn(ConfigType.SNS)
        whenever(configOperations.getNotificationConfig("channelId1"))
            .thenReturn(configDocInfo)
        whenever(eventOperations.createNotificationEvent(any(), any())).thenReturn("doc-2")
        whenever(userAccess.getUserTenant(any())).thenReturn("tenant-1")
        whenever(userAccess.getAllAccessInfo(any())).thenReturn(listOf())

        var response = SendMessageActionHelper.executeRequest(request)
        // TODO: ...
    }

}